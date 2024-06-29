package es.kiwi.drinksdispenser.domain.output;

import es.kiwi.drinksdispenser.domain.model.Machines;
import reactor.core.publisher.Mono;

public interface MachinesOutput {
    Mono<Machines> findById(Long machineId);
}
