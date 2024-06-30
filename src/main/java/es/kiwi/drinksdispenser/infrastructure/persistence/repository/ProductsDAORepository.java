package es.kiwi.drinksdispenser.infrastructure.persistence.repository;

import es.kiwi.drinksdispenser.infrastructure.persistence.dao.ProductsDAO;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductsDAORepository extends R2dbcRepository<ProductsDAO, Long> {

    Mono<ProductsDAO> findByName(String name);

    @Query("select * from products where id in (select distinct machine_products.product_id from machine_products " +
            "where machine_id = :machineId)")
    Flux<ProductsDAO> findAllByMachineId(Long machineId);
}
