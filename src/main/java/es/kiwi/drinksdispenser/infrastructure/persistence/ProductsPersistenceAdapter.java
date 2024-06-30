package es.kiwi.drinksdispenser.infrastructure.persistence;

import es.kiwi.drinksdispenser.domain.model.Products;
import es.kiwi.drinksdispenser.domain.output.ProductsOutput;
import es.kiwi.drinksdispenser.infrastructure.persistence.mapper.ProductsDAOMapper;
import es.kiwi.drinksdispenser.infrastructure.persistence.repository.ProductsDAORepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ProductsPersistenceAdapter implements ProductsOutput {

    private final ProductsDAORepository productsDAORepository;
    private final ProductsDAOMapper productsDAOMapper;

    @Override
    public Mono<Products> findByName(String productName) {
        return productsDAORepository.findByName(productName)
                .map(productsDAOMapper::productsDAOToProducts);
    }

    @Override
    public Flux<Products> findAllByMachineId(Long machineId) {
        return productsDAORepository.findAllByMachineId(machineId)
                .map(productsDAOMapper::productsDAOToProducts);
    }
}
