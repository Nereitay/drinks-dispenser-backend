package es.kiwi.drinksdispenser.infrastructure.api.rest;

import es.kiwi.drinksdispenser.application.dto.MachineProductsDTO;
import es.kiwi.drinksdispenser.application.usecase.AddProductsToMachineUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/machine-products")
@RequiredArgsConstructor
public class MachineProductsRestAdapter {

    private final AddProductsToMachineUseCase addProductsToMachineUseCase;

    @PostMapping
    @Tag(name = "/machine-products")
    @Operation(summary = "This method is used to add more products to the dispenser.")
    public Mono<Void> addProductToMachine(@RequestBody List<MachineProductsDTO> machineProductsDTOList) {
        return addProductsToMachineUseCase.addProduct(machineProductsDTOList);
    }
}
