package es.kiwi.drinksdispenser.infrastructure.persistence.repository;

import es.kiwi.drinksdispenser.infrastructure.persistence.dao.CoinsDAO;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface CoinsDAORepository extends R2dbcRepository<CoinsDAO, Long> {
}