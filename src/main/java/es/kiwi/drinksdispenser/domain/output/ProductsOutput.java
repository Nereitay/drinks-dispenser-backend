package es.kiwi.drinksdispenser.domain.output;

import es.kiwi.drinksdispenser.domain.model.Products;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductsOutput {
    Mono<Products> findByName(String productName);
    Flux<Products> findAllByMachineId(Long machineId);

}
