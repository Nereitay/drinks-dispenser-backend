package es.kiwi.drinksdispenser.infrastructure.persistence.repository;

import es.kiwi.drinksdispenser.infrastructure.persistence.dao.MachinesDAO;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface MachinesDAORepository extends R2dbcRepository<MachinesDAO, Long> {

    @Modifying
    @Query("update machine_products set stock = stock - 1 where id = :id")
    Mono<Void> reduceStock(Long id);
}
