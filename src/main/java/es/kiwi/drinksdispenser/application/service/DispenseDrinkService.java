package es.kiwi.drinksdispenser.application.service;

import es.kiwi.drinksdispenser.application.dto.DispenseDrinkDTO;
import es.kiwi.drinksdispenser.application.exception.DispenseDrinkException;
import es.kiwi.drinksdispenser.application.mapper.CoinsVOMapper;
import es.kiwi.drinksdispenser.application.vo.DispenseDrinkVO;
import es.kiwi.drinksdispenser.domain.model.Coins;
import es.kiwi.drinksdispenser.domain.model.MachineProducts;
import es.kiwi.drinksdispenser.domain.output.CoinsOutput;
import es.kiwi.drinksdispenser.domain.output.MachineProductsOutput;
import es.kiwi.drinksdispenser.domain.service.CoinsValidationService;
import es.kiwi.drinksdispenser.integration.event.manager.ProductStockZeroEvent;
import es.kiwi.drinksdispenser.integration.event.manager.ProductStockZeroEventHandler;
import es.kiwi.drinksdispenser.integration.event.lcd.LcdNotifier;
import io.netty.handler.timeout.TimeoutException;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

import static es.kiwi.drinksdispenser.application.constants.DispenseDrinkMessageConstants.*;
import static es.kiwi.drinksdispenser.integration.constants.NotifierConstants.*;

@RequiredArgsConstructor
public class DispenseDrinkService {

    private final CoinsOutput coinsOutput;
    private final LcdNotifier lcdNotifier;
    private final CoinsValidationService coinsValidationService;
    private final MachineProductsOutput machineProductsOutput;
    private final ProductStockZeroEventHandler productStockZeroEventHandler;
    private final CoinsVOMapper coinsVOMapper;

    @Transactional
    public Mono<DispenseDrinkVO> dispenseDrink(DispenseDrinkDTO command) {

        List<Coins> coinsList = command.getCoinTypeList().stream().collect(Collectors.groupingBy(coinType -> coinType,
                Collectors.counting())).entrySet().stream().map(entry -> {
            Coins coins = new Coins();
            coins.setCoinType(entry.getKey());
            coins.setMachineId(command.getMachineId());
            coins.setQuantity(Math.toIntExact(entry.getValue()));
            return coins;
        }).collect(Collectors.toList());

        return introduceCoins(coinsList)
                .then(isConfirmedByUser(command, coinsList))
                .switchIfEmpty(checkProductAvailability(command)
                        .timeout(Duration.ofSeconds(5))
                        .flatMap(product -> validateMoney(command, coinsList, product))
                        .timeout(Duration.ofSeconds(5))
                        .flatMap(product -> dispenseProduct(command, coinsList, product))
                        .timeout(Duration.ofSeconds(5))
                        .onErrorResume(throwable -> handleReturnCoins(coinsList, throwable)));
    }

    private Mono<Void> introduceCoins(List<Coins> coinsList) {
        return coinsOutput.insertCoins(coinsList);
    }

    private Mono<DispenseDrinkVO> isConfirmedByUser(DispenseDrinkDTO command, List<Coins> coinsList) {
        if (Boolean.FALSE.equals(command.isConfirmed())) {
            return handleReturnCoins(
                    coinsList, new DispenseDrinkException(OPERATION_REJECTED_BY_USER));
        }

        return Mono.empty();
    }

    private Mono<DispenseDrinkVO> handleReturnCoins(List<Coins> coins, Throwable e) {
        if (e instanceof TimeoutException) {
            lcdNotifier.notify(OPERATION_TIMED_OUT);
        } else {
            lcdNotifier.notify(OPERATION_FAILED_ERROR + e.getMessage());
        }
        BigDecimal total = coinsValidationService.calculateTotal(coins);
        return coinsOutput.reduceCoins(coins)
                .then(Mono.just(new DispenseDrinkVO(e.getMessage().concat(OPERATION_CANCELLED),
                        coinsVOMapper.toCoinsVOList(coins),
                        total, null)));
    }

    private Mono<MachineProducts> checkProductAvailability(DispenseDrinkDTO command) {
        lcdNotifier.notify(CHECK_PRODUCT_AVAILABILITY);
        return machineProductsOutput.findAvailableProduct(command.getMachineId(), command.getProductsOption().getName())
                .switchIfEmpty(Mono.error(new DispenseDrinkException(PRODUCT_NO_STOCK_OR_EXPIRED)));
    }

    private Mono<MachineProducts> validateMoney(DispenseDrinkDTO command, List<Coins> coins,
                                                MachineProducts machineProducts) {
        lcdNotifier.notify(VALIDATE_MONEY);
        return Mono.just(machineProducts)
                .flatMap(product -> {
                    BigDecimal totalMoney = coinsValidationService.calculateTotal(coins);
                    BigDecimal price = product.getProduct().getPrice();
                    BigDecimal change = totalMoney.subtract(price);
                    return coinsValidationService.isSufficientAmount(totalMoney, price)
                            .flatMap(isSufficient -> {
                                if (Boolean.FALSE.equals(isSufficient)) {
                                    return Mono.error(new DispenseDrinkException(INSUFFICIENT_AMOUNT));
                                }
                                return coinsValidationService.hasEnoughFundsToChange(command.getMachineId(),
                                                change)
                                        .flatMap(hasEnoughFunds -> {
                                            if (Boolean.FALSE.equals(hasEnoughFunds)) {
                                                return Mono.error(new DispenseDrinkException(INSUFFICIENT_FUNDS));
                                            }

                                            return coinsValidationService.canProvideChange(command.getMachineId(), change)
                                                    .flatMap(canProvideChange -> {
                                                        if (Boolean.FALSE.equals(canProvideChange)) {
                                                            return Mono.error(new DispenseDrinkException(INSUFFICIENT_COINS_FOR_CHANGE));
                                                        }

                                                        return Mono.just(machineProducts);
                                                    });
                                        });
                            });
                });
    }

    private Mono<DispenseDrinkVO> dispenseProduct(DispenseDrinkDTO command, List<Coins> coins, MachineProducts machineProducts) {
        lcdNotifier.notify(DISPENSE_PRODUCT);
        BigDecimal totalCoinsValue = coinsValidationService.calculateTotal(coins);
        BigDecimal productPrice = machineProducts.getProduct().getPrice();
        BigDecimal changeAmount = totalCoinsValue.subtract(productPrice);

        return machineProductsOutput.reduceProduct(machineProducts)
                .then(Mono.defer(() -> coinsValidationService.reduceChange(command.getMachineId(), changeAmount)))
                .flatMap(changeCoins -> {
                    BigDecimal changeGiven = coinsValidationService.calculateTotal(changeCoins);
                    return machineProductsOutput.findAvailableProduct(command.getMachineId(),
                                    machineProducts.getProduct().getProductsOption().getName())
                            .switchIfEmpty(notifyOutOfStock(machineProducts))
                            .then(Mono.just(new DispenseDrinkVO(SUCCEED_DISPENSE_PRODUCT,
                                    coinsVOMapper.toCoinsVOList(changeCoins),
                                    changeGiven,
                                    machineProducts.getProduct().getProductsOption())));
                })
                .onErrorResume(throwable -> handleReturnCoins(coins, throwable));
    }

    private Mono<MachineProducts> notifyOutOfStock(MachineProducts machineProducts) {
        return Mono.fromRunnable(() -> productStockZeroEventHandler.handle(new ProductStockZeroEvent(machineProducts.getMachine(),
                machineProducts.getProduct())));
    }
}
