package es.kiwi.drinksdispenser.application.usecase;

import es.kiwi.drinksdispenser.application.service.ProductStockService;
import es.kiwi.drinksdispenser.application.vo.ProductStockVO;
import es.kiwi.drinksdispenser.domain.model.ProductsOption;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ConsultProductStockUseCase {

   private final ProductStockService productStockService;

    public Mono<ProductStockVO> consultProductStock(Long machineId, ProductsOption productsOption) {
        return productStockService.consultProductStock(machineId, productsOption.getName());
    }
}
