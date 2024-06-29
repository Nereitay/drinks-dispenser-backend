package es.kiwi.drinksdispenser.infrastructure.api.rest.v1;

import es.kiwi.drinksdispenser.application.usecase.ConsultMachineStatusUseCase;
import es.kiwi.drinksdispenser.domain.model.Machines;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1/machine")
@RequiredArgsConstructor
public class MachineRestAdapter {

    private final ConsultMachineStatusUseCase consultMachineStatusUseCase;

    @Tag(name = "/machine")
    @GetMapping("/{machineId}")
    @Operation(summary = "This method is used to consult the machine status.")
    public Mono<Machines> consultMachineStatus(@PathVariable Long machineId) {
        return consultMachineStatusUseCase.consultMachineStatus(machineId);
    }
}
