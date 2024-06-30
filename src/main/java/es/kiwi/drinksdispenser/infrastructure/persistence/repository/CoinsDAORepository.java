package es.kiwi.drinksdispenser.infrastructure.persistence.repository;

import es.kiwi.drinksdispenser.infrastructure.persistence.dao.CoinsDAO;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CoinsDAORepository extends R2dbcRepository<CoinsDAO, Long> {

    Mono<CoinsDAO> findByMachineIdAndDenomination(Long machineId, String denomination);
    Flux<CoinsDAO> findByMachineId(Long machineId);
}
