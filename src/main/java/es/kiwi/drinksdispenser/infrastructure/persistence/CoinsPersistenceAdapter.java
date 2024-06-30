package es.kiwi.drinksdispenser.infrastructure.persistence;

import es.kiwi.drinksdispenser.domain.model.Coins;
import es.kiwi.drinksdispenser.domain.output.CoinsOutput;
import es.kiwi.drinksdispenser.infrastructure.persistence.dao.CoinsDAO;
import es.kiwi.drinksdispenser.infrastructure.persistence.mapper.CoinsDAOMapper;
import es.kiwi.drinksdispenser.infrastructure.persistence.repository.CoinsDAORepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class CoinsPersistenceAdapter implements CoinsOutput {
    private final CoinsDAORepository coinsDAORepository;
    private final CoinsDAOMapper coinsDAOMapper;
    @Override
    public Mono<Void> insertCoins(List<Coins> coins) {
        return Flux.fromIterable(coins)
                .map(coinsDAOMapper::toCoinsDAO)
                .flatMap(coinsDAO -> coinsDAORepository.findByMachineIdAndDenomination(coinsDAO.getMachineId(), coinsDAO.getDenomination())
                        .flatMap(existing -> updateQuantity(existing, coinsDAO)).switchIfEmpty(insertNew(coinsDAO))).then();
    }

    @Override
    public Flux<Coins> findByMachineId(Long machineId) {
        return coinsDAORepository.findByMachineId(machineId).map(coinsDAOMapper::toCoins);
    }

    @Override
    public Mono<Void> saveAll(List<Coins> updatedCoins) {
        return Flux.fromIterable(updatedCoins).map(coinsDAOMapper::toCoinsDAO).flatMap(coinsDAORepository::save).then();
    }

    @Override
    public Mono<BigDecimal> consultTotalMoneyInMachine(Long machineId) {
        return coinsDAORepository.findByMachineId(machineId).reduce(BigDecimal.ZERO,
                (aDouble, coinsDAO) -> aDouble.add(coinsDAO.getValue().multiply(BigDecimal.valueOf(coinsDAO.getQuantity()))));
    }

    @Override
    public Mono<Void> reduceCoins(List<Coins> coins) {
        return Flux.fromIterable(coins)
                .map(coinsDAOMapper::toCoinsDAO)
                .flatMap(coinsDAO -> coinsDAORepository.findByMachineIdAndDenomination(coinsDAO.getMachineId(),
                                coinsDAO.getDenomination())
                        .flatMap(existing -> reduceQuantity(existing, coinsDAO)).switchIfEmpty(insertNew(coinsDAO))).then();
    }

    private Mono<CoinsDAO> insertNew(CoinsDAO coinsDAO) {
        coinsDAO.setUpdatedAt(LocalDateTime.now());
        return coinsDAORepository.save(coinsDAO);
    }

    private Mono<CoinsDAO> updateQuantity(CoinsDAO existing, CoinsDAO coinsDAO) {
        existing.setQuantity(existing.getQuantity() + coinsDAO.getQuantity());
        existing.setUpdatedAt(LocalDateTime.now());
        return coinsDAORepository.save(existing);
    }

    private Mono<CoinsDAO> reduceQuantity(CoinsDAO existing, CoinsDAO coinsDAO) {
        existing.setQuantity(existing.getQuantity() - coinsDAO.getQuantity());
        existing.setUpdatedAt(LocalDateTime.now());
        return coinsDAORepository.save(existing);
    }
}
