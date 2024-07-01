package es.kiwi.drinksdispenser.application.usecase;

import es.kiwi.drinksdispenser.domain.exception.MachineNotFoundException;
import es.kiwi.drinksdispenser.domain.model.MachineStatus;
import es.kiwi.drinksdispenser.domain.model.Machines;
import es.kiwi.drinksdispenser.domain.output.MachinesOutput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class ConsultMachineStatusUseCaseTest {
    @Mock
    private MachinesOutput machinesOutput;

    @InjectMocks
    private ConsultMachineStatusUseCase consultMachineStatusUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testConsultMachineStatus_Success() {
        Long machineId = 1L;
        Machines machine = new Machines();
        machine.setId(machineId);
        machine.setStatus(MachineStatus.AVAILABLE);

        when(machinesOutput.findById(anyLong())).thenReturn(Mono.just(machine));

        Mono<Machines> result = consultMachineStatusUseCase.consultMachineStatus(machineId);

        StepVerifier.create(result)
                .expectNextMatches(m -> m.getId().equals(machineId) && MachineStatus.AVAILABLE.equals(m.getStatus()))
                .verifyComplete();

        verify(machinesOutput, times(1)).findById(machineId);
    }

    @Test
    void testConsultMachineStatus_NotFound() {
        Long machineId = 1L;

        when(machinesOutput.findById(anyLong())).thenReturn(Mono.empty());

        Mono<Machines> result = consultMachineStatusUseCase.consultMachineStatus(machineId);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof MachineNotFoundException &&
                        throwable.getMessage().equals("Machine with ID " + machineId + " not found."))
                .verify();

        verify(machinesOutput, times(1)).findById(machineId);
    }
}