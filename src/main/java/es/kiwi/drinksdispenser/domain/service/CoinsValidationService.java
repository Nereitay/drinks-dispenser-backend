package es.kiwi.drinksdispenser.domain.service;

import es.kiwi.drinksdispenser.domain.model.CoinType;
import es.kiwi.drinksdispenser.domain.model.Coins;
import es.kiwi.drinksdispenser.domain.output.CoinsOutput;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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
                        int usedCoins = Math.min(neededCoins, availableCoins);

                        if (usedCoins > 0) {
                            coin.setQuantity(availableCoins - usedCoins);
                            remainingChange = remainingChange.subtract(denomination.multiply(BigDecimal.valueOf(usedCoins)));
                            changeCoins.add(createChangeCoin(coin.getCoinType(), usedCoins));
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
                        int usedCoins = Math.min(neededCoins, availableCoins);

                        if (usedCoins > 0) {
                            remainingChange = remainingChange.subtract(denomination.multiply(BigDecimal.valueOf(usedCoins)));
                        }

                        if (remainingChange.compareTo(BigDecimal.ZERO) == 0) {
                            return true;
                        }
                    }
                    return remainingChange.compareTo(BigDecimal.ZERO) == 0;
                });
    }
}
