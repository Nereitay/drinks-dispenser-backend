package es.kiwi.drinksdispenser.infrastructure.persistence;

import es.kiwi.drinksdispenser.domain.model.CoinType;
import es.kiwi.drinksdispenser.domain.model.Coins;
import es.kiwi.drinksdispenser.infrastructure.persistence.dao.CoinsDAO;
import es.kiwi.drinksdispenser.infrastructure.persistence.mapper.CoinsDAOMapper;
import es.kiwi.drinksdispenser.infrastructure.persistence.repository.CoinsDAORepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class CoinsPersistenceAdapterTest {

    @Mock
    private CoinsDAORepository coinsDAORepository;

    @Mock
    private CoinsDAOMapper coinsDAOMapper;

    @InjectMocks
    private CoinsPersistenceAdapter coinsPersistenceAdapter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testInsertCoins_Success() {
        Coins coin = new Coins(1L, 1L, CoinType.FIFTY_CENTS, 2);
        CoinsDAO coinDAO = new CoinsDAO(1L, 1L, "FIFTY_CENTS", new BigDecimal("0.50"), 2, LocalDateTime.now());

        when(coinsDAOMapper.toCoinsDAO(any())).thenReturn(coinDAO);
        when(coinsDAORepository.findByMachineIdAndDenomination(anyLong(), anyString())).thenReturn(Mono.just(coinDAO));
        when(coinsDAORepository.save(any())).thenReturn(Mono.just(coinDAO));

        Mono<Void> result = coinsPersistenceAdapter.insertCoins(List.of(coin));

        StepVerifier.create(result)
                .verifyComplete();

        verify(coinsDAOMapper, times(1)).toCoinsDAO(coin);
        verify(coinsDAORepository, times(1)).findByMachineIdAndDenomination(coinDAO.getMachineId(), coinDAO.getDenomination());
    }

    @Test
    void testReduceCoins_Success() {
        Coins coin = new Coins(1L, 1L, CoinType.FIFTY_CENTS, 2);
        CoinsDAO coinDAO = new CoinsDAO(1L, 1L, "FIFTY_CENTS", new BigDecimal("0.50"), 2, LocalDateTime.now());

        when(coinsDAOMapper.toCoinsDAO(any())).thenReturn(coinDAO);
        when(coinsDAORepository.findByMachineIdAndDenomination(anyLong(), anyString())).thenReturn(Mono.just(coinDAO));
        when(coinsDAORepository.save(any())).thenReturn(Mono.just(coinDAO));

        Mono<Void> result = coinsPersistenceAdapter.reduceCoins(List.of(coin));

        StepVerifier.create(result)
                .verifyComplete();

        verify(coinsDAOMapper, times(1)).toCoinsDAO(coin);
        verify(coinsDAORepository, times(1)).findByMachineIdAndDenomination(coinDAO.getMachineId(), coinDAO.getDenomination());
    }

    @Test
    void testConsultTotalMoneyInMachine_Success() {
        Long machineId = 1L;
        CoinsDAO coinDAO1 = new CoinsDAO(1L, 1L, "FIFTY_CENTS", new BigDecimal("0.50"), 5, LocalDateTime.now());
        CoinsDAO coinDAO2 = new CoinsDAO(1L, 1L, "ONE_EURO", new BigDecimal("1.00"), 3, LocalDateTime.now());

        when(coinsDAORepository.findByMachineId(machineId)).thenReturn(Flux.just(coinDAO1, coinDAO2));

        BigDecimal expectedTotal = new BigDecimal("5.50");

        Mono<BigDecimal> result = coinsPersistenceAdapter.consultTotalMoneyInMachine(machineId);

        StepVerifier.create(result)
                .expectNext(expectedTotal)
                .verifyComplete();

        verify(coinsDAORepository, times(1)).findByMachineId(machineId);
    }
}