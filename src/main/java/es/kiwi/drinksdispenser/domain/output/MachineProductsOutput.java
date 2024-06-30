package es.kiwi.drinksdispenser.domain.output;

import es.kiwi.drinksdispenser.domain.model.MachineProducts;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface MachineProductsOutput {

    Mono<Void> save(List<MachineProducts> machineProductsList);
    Flux<MachineProducts> findByMachineIdAndProduct(Long machineId, String productName);

    Mono<MachineProducts> findAvailableProduct(Long machineId, String productName);

    Mono<Void> reduceProduct(MachineProducts machineProducts);
}
