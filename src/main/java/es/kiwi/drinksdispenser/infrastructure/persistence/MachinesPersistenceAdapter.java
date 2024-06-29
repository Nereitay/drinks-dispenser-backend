package es.kiwi.drinksdispenser.infrastructure.persistence;

import es.kiwi.drinksdispenser.domain.model.Machines;
import es.kiwi.drinksdispenser.domain.output.MachinesOutput;
import es.kiwi.drinksdispenser.infrastructure.persistence.mapper.MachineDAOMapper;
import es.kiwi.drinksdispenser.infrastructure.persistence.repository.MachinesDAORepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class MachinesPersistenceAdapter implements MachinesOutput {
    private final MachinesDAORepository machinesDAORepository;
    private final MachineDAOMapper machineDAOMapper;

    @Override
    public Mono<Machines> findById(Long machineId) {
        return machinesDAORepository.findById(machineId)
            .map(machineDAOMapper::machinesDAOToMachines);
    }
}