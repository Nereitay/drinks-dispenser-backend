package es.kiwi.drinksdispenser.application.usecase;

import es.kiwi.drinksdispenser.application.dto.MachineProductsDTO;
import es.kiwi.drinksdispenser.application.mapper.MachineProductsDTOMapper;
import es.kiwi.drinksdispenser.domain.model.MachineProducts;
import es.kiwi.drinksdispenser.domain.output.MachineProductsOutput;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
public class AddProductsToMachineUseCase {
    private final MachineProductsOutput machineProductsOutput;
    private final MachineProductsDTOMapper machineProductsDTOMapper;

    public Mono<Void> addProduct(List<MachineProductsDTO> machineProductsDTOList) {
        List<MachineProducts> machineProductsList = machineProductsDTOMapper.toMachineProductsList(machineProductsDTOList);
        return machineProductsOutput.save(machineProductsList).then();
    }
}
