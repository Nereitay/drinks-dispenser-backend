package es.kiwi.drinksdispenser.application.service;

import es.kiwi.drinksdispenser.application.dto.DispenseDrinkDTO;
import es.kiwi.drinksdispenser.application.mapper.CoinsVOMapper;
import es.kiwi.drinksdispenser.application.vo.DispenseDrinkVO;
import es.kiwi.drinksdispenser.domain.model.Coins;
import es.kiwi.drinksdispenser.domain.model.MachineProducts;
import es.kiwi.drinksdispenser.domain.output.CoinsOutput;
import es.kiwi.drinksdispenser.domain.output.MachineProductsOutput;
import es.kiwi.drinksdispenser.domain.service.CoinsValidationService;
import es.kiwi.drinksdispenser.integration.event.ProductStockZeroEvent;
import es.kiwi.drinksdispenser.integration.event.ProductStockZeroEventHandler;
import es.kiwi.drinksdispenser.integration.lcd.LcdNotifier;
import io.netty.handler.timeout.TimeoutException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class DispenseDrinkService {

    private final CoinsOutput coinsOutput;
    private final LcdNotifier lcdNotifier;
    private final CoinsValidationService coinsValidationService;
    private final MachineProductsOutput machineProductsOutput;
    private final ProductStockZeroEventHandler productStockZeroEventHandler;
    private final CoinsVOMapper coinsVOMapper;

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
                .then(checkProductAvailability(command))
                .timeout(Duration.ofSeconds(5))
                .flatMap(product -> validateMoney(command, coinsList, product))
                .timeout(Duration.ofSeconds(5))
                .flatMap(product -> dispenseProduct(command, coinsList, product))
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(throwable -> handleReturnCoins(coinsList, throwable));
    }

    private Mono<Void> introduceCoins(List<Coins> coinsList) {
        return coinsOutput.insertCoins(coinsList);
    }

    private Mono<DispenseDrinkVO> isConfirmedByUser(DispenseDrinkDTO command, List<Coins> coinsList) {
        if (!command.isConfirmed()) {
            return handleReturnCoins(
                    coinsList, new RuntimeException("Operation rejected by user"));
        }

        return Mono.empty();
    }

    private Mono<DispenseDrinkVO> handleReturnCoins(List<Coins> coins, Throwable e) {
        if (e instanceof TimeoutException) {
            lcdNotifier.notify("Operation timed out");
        } else {
            lcdNotifier.notify("Operation failed: " + e.getMessage());
        }
        BigDecimal total = coinsValidationService.calculateTotal(coins);
        return coinsOutput.reduceCoins(coins)
                .then(Mono.just(new DispenseDrinkVO(e != null ?
                        e.getMessage().concat(", Operation Cancelled with return coins.") : "",
                        coinsVOMapper.toCoinsVOList(coins),
                        total, null)));
    }

    private Mono<MachineProducts> checkProductAvailability(DispenseDrinkDTO command) {
        lcdNotifier.notify("CHECK_PRODUCT_AVAILABILITY");
        return machineProductsOutput.findAvailableProduct(command.getMachineId(), command.getProductsOption().getName())
                .switchIfEmpty(Mono.error(new RuntimeException("Product not available or expired")));
    }

    private Mono<MachineProducts> validateMoney(DispenseDrinkDTO command, List<Coins> coins,
                                                MachineProducts machineProducts) {
        lcdNotifier.notify("VALIDATE_MONEY");
        return Mono.just(machineProducts)
                .flatMap(product -> {
                    BigDecimal totalMoney = coinsValidationService.calculateTotal(coins);
                    BigDecimal price = product.getProduct().getPrice();
                    BigDecimal change = totalMoney.subtract(price);
                    return coinsValidationService.isSufficientAmount(totalMoney, price)
                            .flatMap(isSufficient -> {
                                if (!isSufficient) {
                                    return Mono.error(new RuntimeException("Insufficient Amount"));
                                }
                                return coinsValidationService.hasEnoughFundsToChange(command.getMachineId(),
                                                change)
                                        .flatMap(hasEnoughFunds -> {
                                            if (!hasEnoughFunds) {
                                                return Mono.error(new RuntimeException("Insufficient Funds"));
                                            }

                                            return coinsValidationService.canProvideChange(command.getMachineId(), change)
                                                    .flatMap(canProvideChange -> {
                                                        if (!canProvideChange) {
                                                            return Mono.error(new RuntimeException("Can't provide " +
                                                                    "changes"));
                                                        }

                                                        return Mono.just(machineProducts);
                                                    });
                                        });
                            });
                });
    }

    private Mono<DispenseDrinkVO> dispenseProduct(DispenseDrinkDTO command, List<Coins> coins, MachineProducts machineProducts) {
        lcdNotifier.notify("DISPENSE_PRODUCT");
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
                            .then(Mono.just(new DispenseDrinkVO("SUCCEED - Product dispensed with changes",
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
