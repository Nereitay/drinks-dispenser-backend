package es.kiwi.drinksdispenser.infrastructure.api.rest.v1;

import es.kiwi.drinksdispenser.application.dto.MachineProductsDTO;
import es.kiwi.drinksdispenser.application.usecase.AddProductsToMachineUseCase;
import es.kiwi.drinksdispenser.application.usecase.ConsultProductStockUseCase;
import es.kiwi.drinksdispenser.application.usecase.ConsultProductsOptionUseCase;
import es.kiwi.drinksdispenser.application.vo.ProductStockVO;
import es.kiwi.drinksdispenser.application.vo.ProductsOptionVO;
import es.kiwi.drinksdispenser.domain.model.ProductsOption;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/v1/machine-products")
@RequiredArgsConstructor
@Validated
public class MachineProductsRestAdapter {

    private final AddProductsToMachineUseCase addProductsToMachineUseCase;
    private final ConsultProductStockUseCase consultProductStockUseCase;
    private final ConsultProductsOptionUseCase consultProductsOptionUseCase;

    @PostMapping
    @Tag(name = "/machine-products")
    @Operation(summary = "This method is used to add more products to the dispenser.")
    public Mono<Void> addProductToMachine(@Valid @RequestBody List<MachineProductsDTO> machineProductsDTOList) {
        return addProductsToMachineUseCase.addProduct(machineProductsDTOList);
    }

    @Tag(name = "/machine-products")
    @GetMapping("/stock/{machineId}")
    @Operation(summary = "This method is used to consult a product stock.")
    public Mono<ProductStockVO> consultProductStock(@PathVariable Long machineId,
                                                    @RequestParam ProductsOption productsOption) {
        return consultProductStockUseCase.consultProductStock(machineId, productsOption);
    }

    @Tag(name = "/machine-products")
    @GetMapping("/products-option/{machineId}")
    @Operation(summary = "This method is used to consult the products options in the machine.")
    public Flux<ProductsOptionVO> consultProductsOption(@PathVariable Long machineId) {
        return consultProductsOptionUseCase.consultProductsOptions(machineId);
    }

}
