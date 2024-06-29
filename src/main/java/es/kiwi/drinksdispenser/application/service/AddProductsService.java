package es.kiwi.drinksdispenser.application.service;

import es.kiwi.drinksdispenser.domain.exception.MachineNotFoundException;
import es.kiwi.drinksdispenser.domain.exception.ProductNotFoundException;
import es.kiwi.drinksdispenser.domain.model.MachineProducts;
import es.kiwi.drinksdispenser.domain.output.MachineProductsOutput;
import es.kiwi.drinksdispenser.domain.output.MachinesOutput;
import es.kiwi.drinksdispenser.domain.output.ProductsOutput;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
public class AddProductsService {
    private final MachineProductsOutput machineProductsOutput;
    private final MachinesOutput machinesOutput;
    private final ProductsOutput productsOutput;

    public Mono<Void> addProducts(List<MachineProducts> machineProductsList) {
        return Flux.fromIterable(machineProductsList)
                .flatMap(this::validateAndProcessMachineProduct)
                .then();
    }

    private Mono<Void> validateAndProcessMachineProduct(MachineProducts machineProducts) {
        return validateMachineExists(machineProducts.getMachine().getId())
                .then(validateProductExists(machineProducts.getProduct().getProductsOption().getName()))
                .thenReturn(machineProducts)
                .flatMap(product -> machineProductsOutput.save(List.of(product)));
    }
    private Mono<Void> validateMachineExists(Long machineId) {
        return machinesOutput.findById(machineId)
                .switchIfEmpty(Mono.error(new MachineNotFoundException("Machine with ID " + machineId + " not found.")))
                .then();
    }

    private Mono<Void> validateProductExists(String productName) {
        return productsOutput.findByName(productName)
                .switchIfEmpty(Mono.error(new ProductNotFoundException("Product with name '" + productName + "' not found.")))
                .then();
    }
}
