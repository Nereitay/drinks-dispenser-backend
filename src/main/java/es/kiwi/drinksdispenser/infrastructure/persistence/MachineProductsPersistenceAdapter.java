package es.kiwi.drinksdispenser.infrastructure.persistence;

import es.kiwi.drinksdispenser.domain.exception.MachineNotFoundException;
import es.kiwi.drinksdispenser.domain.exception.ProductNotFoundException;
import es.kiwi.drinksdispenser.domain.model.MachineProducts;
import es.kiwi.drinksdispenser.domain.output.MachineProductsOutput;
import es.kiwi.drinksdispenser.infrastructure.persistence.dao.MachineProductsDAO;
import es.kiwi.drinksdispenser.infrastructure.persistence.mapper.MachineProductsDAOMapper;
import es.kiwi.drinksdispenser.infrastructure.persistence.repository.MachineProductsDAORepository;
import es.kiwi.drinksdispenser.infrastructure.persistence.repository.MachinesDAORepository;
import es.kiwi.drinksdispenser.infrastructure.persistence.repository.ProductsDAORepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor
public class MachineProductsPersistenceAdapter implements MachineProductsOutput {
    private final MachineProductsDAORepository machineProductsDAORepository;
    private final MachineProductsDAOMapper machineProductsDAOMapper;
    private final ProductsDAORepository productsDAORepository;
    private final MachinesDAORepository machinesDAORepository;


    @Override
    public Mono<Void> save(List<MachineProducts> machineProductsList) {
        return Flux.fromIterable(machineProductsList).flatMap(this::processMachineProduct).then();
    }

    @Override
    public Flux<MachineProducts> findByMachineIdAndProduct(Long machineId, String productName) {
        return machinesDAORepository.findById(machineId)
                .switchIfEmpty(Mono.error(new MachineNotFoundException("Machine with ID " + machineId + " not found.")))
                .flatMap(machineDAO ->
                        productsDAORepository.findByName(productName))
                .switchIfEmpty(Mono.error(new ProductNotFoundException("Product with name '" + productName + "' not " +
                        "found.")))
                .flatMapMany(productsDAO -> machineProductsDAORepository.findByMachineIdAndProductId(machineId,
                        productsDAO.getId()))
                .flatMap(machineProductsDAO -> Mono.just(machineProductsDAOMapper.toMachineProducts(machineProductsDAO)));
    }

    @Override
    public Mono<MachineProducts> findAvailableProduct(Long machineId, String productName) {
        return findByMachineIdAndProduct(machineId, productName)
                .filter(machineProducts -> machineProducts.getExpirationDate().isAfter(LocalDate.now()))
                .sort(Comparator.comparing(MachineProducts::getExpirationDate))
                .next();
    }

    @Override
    public Mono<Void> reduceProduct(MachineProducts machineProducts) {
        return machinesDAORepository.reduceStock(machineProducts.getId());
    }

    private Mono<MachineProductsDAO> processMachineProduct(MachineProducts machineProducts) {
        return productsDAORepository.findByName(machineProducts.getProduct().getProductsOption().getName())
                .flatMap(product -> machineProductsDAORepository.findByMachineIdAndProductIdAndExpirationDate(
                                machineProducts.getMachine().getId(), product.getId(), machineProducts.getExpirationDate())
                        .flatMap(existingProduct -> updateStock(existingProduct, machineProducts))
                        .switchIfEmpty(insertNewProduct(machineProducts, product.getId())));
    }

    private Mono<MachineProductsDAO> insertNewProduct(MachineProducts machineProducts, Long productId) {
        MachineProductsDAO machineProductsDAO = machineProductsDAOMapper.toMachineProductsDAO(machineProducts);
        machineProductsDAO.setProductId(productId);
        machineProductsDAO.setUpdatedAt(LocalDateTime.now());
        machineProductsDAO.setOperator("USER002");
        return machineProductsDAORepository.save(machineProductsDAO);
    }

    private Mono<MachineProductsDAO> updateStock(MachineProductsDAO existingProduct, MachineProducts machineProducts) {
        existingProduct.setStock(existingProduct.getStock() + machineProducts.getStock());
        existingProduct.setUpdatedAt(LocalDateTime.now());
        existingProduct.setOperator("USER002");
        return machineProductsDAORepository.save(existingProduct);
    }
}
