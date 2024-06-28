package es.kiwi.drinksdispenser.infrastructure.persistence.repository;

import es.kiwi.drinksdispenser.infrastructure.persistence.dao.MachineProductsDAO;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

public interface MachineProductsDAORepository extends R2dbcRepository<MachineProductsDAO, Long> {

    Mono<MachineProductsDAO> findByMachineIdAndProductIdAndExpirationDate(Long machineId, Long productId,
                                                                           LocalDate expirationDate);
}
