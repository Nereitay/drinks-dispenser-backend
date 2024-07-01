package es.kiwi.drinksdispenser.infrastructure.persistence.repository;

import es.kiwi.drinksdispenser.infrastructure.persistence.dao.MachineProductsDAO;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

public interface MachineProductsDAORepository extends R2dbcRepository<MachineProductsDAO, Long> {

    Mono<MachineProductsDAO> findByMachineIdAndProductIdAndExpirationDate(Long machineId, Long productId,
                                                                           LocalDate expirationDate);
    Flux<MachineProductsDAO> findByMachineIdAndProductId(Long machineId, Long productId);

    @Modifying
    @Query("update machine_products set stock = stock - 1, updated_at = now()  where id = :id")
    Mono<Void> reduceStock(Long id);
}
