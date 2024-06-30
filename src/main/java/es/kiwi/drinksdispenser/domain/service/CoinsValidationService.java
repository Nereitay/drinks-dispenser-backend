package es.kiwi.drinksdispenser.domain.service;

import es.kiwi.drinksdispenser.domain.model.Coins;
import es.kiwi.drinksdispenser.domain.output.CoinsOutput;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CoinsValidationService {

    private final CoinsOutput coinsOutput;

    public double calculateTotal(List<Coins> coins) {
        // Convert total back to euros
        return coins.stream().mapToDouble(coin -> coin.getCoinType().getDenomination() * coin.getQuantity()).sum() / 100;
    }
    public Mono<Boolean> isSufficientAmount(double totalMoney, double price) {
        return Mono.just(totalMoney >= price);
    }

    public Mono<Boolean> hasEnoughFundsToChange(Long machineId, double change) {
        return coinsOutput.consultTotalMoneyInMachine(machineId).map(totalMoney -> totalMoney >= change);
    }

    public Mono<Void> addReceivedCoins(List<Coins> coins) {
        return Flux.fromIterable(coins)
                .flatMap(coin -> coinsOutput.insertCoins(coins))
                .then();
    }

    public Mono<List<Coins>> reduceChange(Long machineId, double change) {
        return coinsOutput.findByMachineId(machineId)
                .collectList()
                .flatMap(coins -> {
                    double remainingChange = change;
                    coins.sort(Comparator.comparingDouble((Coins c) -> c.getCoinType().getDenomination()).reversed());
                    List<Coins> updatedCoins = new ArrayList<>();
                    List<Coins> changeCoins = new ArrayList<>();

                    for (Coins coin : coins) {
                        double denomination = coin.getCoinType().getDenomination();
                        int availableCoins = coin.getQuantity();

                        int neededCoins = (int) (remainingChange / denomination);
                        if (neededCoins <= availableCoins) {
                            coin.setQuantity(availableCoins - neededCoins);
                            remainingChange -= neededCoins * denomination;
                            Coins changeCoin = new Coins();
                            changeCoin.setCoinType(coin.getCoinType());
                            changeCoin.setQuantity(neededCoins);
                            changeCoins.add(changeCoin);
                        } else {
                            coin.setQuantity(0);
                            remainingChange -= availableCoins * denomination;

                            Coins changeCoin = new Coins();
                            changeCoin.setCoinType(coin.getCoinType());
                            changeCoin.setQuantity(availableCoins);
                            changeCoins.add(changeCoin);
                        }
                        updatedCoins.add(coin);

                        if (remainingChange == 0.0) {
                            break;
                        }
                    }

                    return coinsOutput.saveAll(updatedCoins).then(Mono.just(changeCoins));
                });
    }


    public Mono<Boolean> canProvideChange(Long machineId, double change) {
        return coinsOutput.findByMachineId(machineId)
                .collectList()
                .map(coins -> {
                    double remainingChange = change;
                    coins.sort(Comparator.comparingDouble((Coins c) -> c.getCoinType().getDenomination()).reversed());
                    for (Coins coin : coins) {
                        double denomination = coin.getCoinType().getDenomination();
                        int availableCoins = coin.getQuantity();
                        int neededCoins = (int) (remainingChange / denomination);
                        if (neededCoins <= availableCoins) {
                            remainingChange -= neededCoins * denomination;
                        } else {
                            remainingChange -= availableCoins * denomination;
                        }
                        if (remainingChange == 0.0) {
                            return true;
                        }
                    }
                    return false;
                });
    }
}
