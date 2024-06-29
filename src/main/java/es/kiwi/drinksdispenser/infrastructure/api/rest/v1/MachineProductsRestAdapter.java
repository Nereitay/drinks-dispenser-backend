package es.kiwi.drinksdispenser.infrastructure.api.rest.v1;

import es.kiwi.drinksdispenser.application.dto.MachineProductsDTO;
import es.kiwi.drinksdispenser.application.usecase.AddProductsToMachineUseCase;
import es.kiwi.drinksdispenser.application.usecase.ConsultProductStockUseCase;
import es.kiwi.drinksdispenser.application.vo.ProductStockVO;
import es.kiwi.drinksdispenser.domain.model.ProductsOption;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/v1/machine-products")
@RequiredArgsConstructor
public class MachineProductsRestAdapter {

    private final AddProductsToMachineUseCase addProductsToMachineUseCase;
    private final ConsultProductStockUseCase consultProductStockUseCase;

    @PostMapping
    @Tag(name = "/machine-products")
    @Operation(summary = "This method is used to add more products to the dispenser.")
    public Mono<Void> addProductToMachine(@RequestBody List<MachineProductsDTO> machineProductsDTOList) {
        return addProductsToMachineUseCase.addProduct(machineProductsDTOList);
    }

    @GetMapping("/stock/{machineId}")
    public Mono<ProductStockVO> consultProductStock(@PathVariable Long machineId,
                                                    @RequestParam ProductsOption productsOption) {
        return consultProductStockUseCase.consultProductStock(machineId, productsOption);
    }

}
