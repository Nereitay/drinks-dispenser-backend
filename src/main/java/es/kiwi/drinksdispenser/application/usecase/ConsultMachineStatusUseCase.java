package es.kiwi.drinksdispenser.application.usecase;

import es.kiwi.drinksdispenser.domain.exception.MachineNotFoundException;
import es.kiwi.drinksdispenser.domain.model.Machines;
import es.kiwi.drinksdispenser.domain.output.MachinesOutput;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ConsultMachineStatusUseCase {
    private final MachinesOutput machinesOutput;

    public Mono<Machines> consultMachineStatus(Long machineId) {
        return machinesOutput.findById(machineId)
                .switchIfEmpty(Mono.error(new MachineNotFoundException("Machine with ID " + machineId + " not found.")));
    }
}
