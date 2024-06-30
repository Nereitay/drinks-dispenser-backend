package es.kiwi.drinksdispenser.domain.service;

import es.kiwi.drinksdispenser.domain.model.CoinType;
import es.kiwi.drinksdispenser.domain.model.Coins;
import es.kiwi.drinksdispenser.domain.output.CoinsOutput;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CoinsValidationService {

    private final CoinsOutput coinsOutput;

    public BigDecimal calculateTotal(List<Coins> coins) {
        return coins.stream()
                .map(coin -> coin.getCoinType().getValue().multiply(BigDecimal.valueOf(coin.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Mono<Boolean> isSufficientAmount(BigDecimal totalMoney, BigDecimal price) {
        return Mono.just(totalMoney.compareTo(price) >= 0);
    }

    public Mono<Boolean> hasEnoughFundsToChange(Long machineId, BigDecimal change) {
        return coinsOutput.consultTotalMoneyInMachine(machineId).map(totalMoney -> totalMoney.compareTo(change) >= 0);
    }

    public Mono<Void> addReceivedCoins(List<Coins> coins) {
        return Flux.fromIterable(coins)
                .flatMap(coin -> coinsOutput.insertCoins(coins))
                .then();
    }

    public Mono<List<Coins>> reduceChange(Long machineId, BigDecimal change) {
        return coinsOutput.findByMachineId(machineId)
                .collectList()
                .flatMap(coins -> {
                    BigDecimal remainingChange = change;
                    coins.sort(Comparator.comparing((Coins c) -> c.getCoinType().getValue()).reversed());
                    List<Coins> updatedCoins = new ArrayList<>();
                    List<Coins> changeCoins = new ArrayList<>();

                    for (Coins coin : coins) {
                        BigDecimal denomination = coin.getCoinType().getValue();
                        int availableCoins = coin.getQuantity();

                        int neededCoins = remainingChange.divide(denomination, 0, RoundingMode.DOWN).intValue();
                        BigDecimal neededAmount = denomination.multiply(BigDecimal.valueOf(neededCoins));
                        if (neededCoins <= availableCoins) {
                            coin.setQuantity(availableCoins - neededCoins);
                            remainingChange = remainingChange.subtract(neededAmount);

                        } else {
                            coin.setQuantity(0);
                            remainingChange = remainingChange.subtract(neededAmount);

                        }
                        if (neededCoins > 0 || coin.getQuantity() != availableCoins) {
                            changeCoins.add(createChangeCoin(coin.getCoinType(), neededCoins));
                            updatedCoins.add(coin);
                        }

                        if (remainingChange.compareTo(BigDecimal.ZERO) == 0) {
                            break;
                        }
                    }

                    return saveUpdatedCoins(updatedCoins).then(Mono.just(changeCoins));
                });
    }

    private Mono<Void> saveUpdatedCoins(List<Coins> updatedCoins) {
        if (updatedCoins.isEmpty()) {
            return Mono.empty();
        }
        return coinsOutput.saveAll(updatedCoins).then();
    }

    private Coins createChangeCoin(CoinType coinType, int quantity) {
        Coins changeCoin = new Coins();
        changeCoin.setCoinType(coinType);
        changeCoin.setQuantity(quantity);
        return changeCoin;
    }


    public Mono<Boolean> canProvideChange(Long machineId, BigDecimal change) {
        return coinsOutput.findByMachineId(machineId)
                .collectList()
                .map(coins -> {
                    BigDecimal remainingChange = change;
                    coins.sort(Comparator.comparing((Coins c) -> c.getCoinType().getValue()).reversed());
                    for (Coins coin : coins) {
                        BigDecimal denomination = coin.getCoinType().getValue();
                        int availableCoins = coin.getQuantity();
                        int neededCoins = remainingChange.divide(denomination, 0, RoundingMode.DOWN).intValue();
                        BigDecimal neededAmount = denomination.multiply(BigDecimal.valueOf(availableCoins));
                        if (neededCoins <= availableCoins) {
                            remainingChange = remainingChange.subtract(denomination.multiply(BigDecimal.valueOf(neededCoins)));
                        } else {
                            remainingChange = remainingChange.subtract(denomination.multiply(BigDecimal.valueOf(availableCoins)));
                        }

                        if (remainingChange.compareTo(BigDecimal.ZERO) == 0) {
                            return true;
                        }
                    }
                    return false;
                });
    }
}
