package es.kiwi.drinksdispenser.application.service;

import es.kiwi.drinksdispenser.application.vo.ProductStockVO;
import es.kiwi.drinksdispenser.domain.output.MachineProductsOutput;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@RequiredArgsConstructor
public class ProductStockService {

    private final MachineProductsOutput machineProductsOutput;

    public Mono<ProductStockVO> consultProductStock(Long machineId, String productName) {
        return machineProductsOutput.findByMachineIdAndProduct(machineId, productName)
                .filter(machineProducts -> machineProducts.getExpirationDate().isAfter(LocalDate.now()) && machineProducts.getStock() > 0)
                .reduce(0, (totalStock, machineProducts) -> totalStock + machineProducts.getStock())
                .flatMap(totalStock -> Mono.just(new ProductStockVO(machineId, productName, totalStock)));
    }
}
