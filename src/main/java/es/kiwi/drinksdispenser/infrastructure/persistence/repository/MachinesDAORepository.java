package es.kiwi.drinksdispenser.infrastructure.persistence.repository;

import es.kiwi.drinksdispenser.infrastructure.persistence.dao.MachinesDAO;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface MachinesDAORepository extends R2dbcRepository<MachinesDAO, Long> {
}
