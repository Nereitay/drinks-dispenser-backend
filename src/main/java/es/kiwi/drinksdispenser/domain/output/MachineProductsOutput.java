package es.kiwi.drinksdispenser.domain.output;

import es.kiwi.drinksdispenser.domain.model.MachineProducts;
import reactor.core.publisher.Mono;

import java.util.List;

public interface MachineProductsOutput {

    Mono<Void> save(List<MachineProducts> machineProductsList);
}
