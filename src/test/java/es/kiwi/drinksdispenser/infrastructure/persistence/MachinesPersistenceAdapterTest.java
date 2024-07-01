package es.kiwi.drinksdispenser.infrastructure.persistence;

import es.kiwi.drinksdispenser.domain.model.MachineStatus;
import es.kiwi.drinksdispenser.domain.model.Machines;
import es.kiwi.drinksdispenser.infrastructure.persistence.dao.MachinesDAO;
import es.kiwi.drinksdispenser.infrastructure.persistence.mapper.MachineDAOMapper;
import es.kiwi.drinksdispenser.infrastructure.persistence.repository.MachinesDAORepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

class MachinesPersistenceAdapterTest {
    @Mock
    private MachinesDAORepository machinesDAORepository;

    @Mock
    private MachineDAOMapper machineDAOMapper;

    @InjectMocks
    private MachinesPersistenceAdapter machinesPersistenceAdapter;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFindById() {
        Long machineId = 1L;
        MachinesDAO machinesDAO = new MachinesDAO(1L, "Name", "L", 0, "L", LocalDateTime.now(), LocalDateTime.now());
        Machines machines = new Machines(1L, "Drink Dispenser 001", MachineStatus.AVAILABLE);

        when(machinesDAORepository.findById(anyLong())).thenReturn(Mono.just(machinesDAO));

        when(machineDAOMapper.machinesDAOToMachines(machinesDAO)).thenReturn(machines);

        StepVerifier.create(machinesPersistenceAdapter.findById(machineId))
                .expectNextMatches(result -> result.equals(machines))
                .verifyComplete();
    }

    @Test
    public void testFindById_NotFound() {
        Long machineId = 1L;

        when(machinesDAORepository.findById(anyLong())).thenReturn(Mono.empty());

        StepVerifier.create(machinesPersistenceAdapter.findById(machineId))
                .verifyComplete();
    }
}
