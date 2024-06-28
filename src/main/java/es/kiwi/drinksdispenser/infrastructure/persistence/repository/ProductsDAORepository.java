package es.kiwi.drinksdispenser.infrastructure.persistence.repository;

import es.kiwi.drinksdispenser.infrastructure.persistence.dao.ProductsDAO;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface ProductsDAORepository extends R2dbcRepository<ProductsDAO, Long> {

    Mono<ProductsDAO> findByName(String name);
}
