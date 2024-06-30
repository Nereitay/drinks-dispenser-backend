package es.kiwi.drinksdispenser.domain.output;

import es.kiwi.drinksdispenser.domain.model.Coins;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface CoinsOutput {
    Mono<Void> insertCoins(List<Coins> coins);

    Flux<Coins> findByMachineId(Long machineId);

    Mono<Void> saveAll(List<Coins> updatedCoins);

    Mono<Double> consultTotalMoneyInMachine(Long machineId);

    Mono<Void> reduceCoins(List<Coins> coins);
}
