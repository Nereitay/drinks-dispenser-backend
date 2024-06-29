package es.kiwi.drinksdispenser.application.usecase;

import es.kiwi.drinksdispenser.application.dto.MachineProductsDTO;
import es.kiwi.drinksdispenser.application.mapper.MachineProductsDTOMapper;
import es.kiwi.drinksdispenser.application.service.AddProductsService;
import es.kiwi.drinksdispenser.domain.model.MachineProducts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddProductsToMachineUseCase {
    private final MachineProductsDTOMapper machineProductsDTOMapper;
    private final AddProductsService addProductsService;


    public Mono<Void> addProduct(List<MachineProductsDTO> machineProductsDTOList) {
        List<MachineProducts> machineProductsList = machineProductsDTOMapper.toMachineProductsList(machineProductsDTOList);
        return addProductsService.addProducts(machineProductsList);
    }
}
