package es.kiwi.drinksdispenser.domain.service;

import es.kiwi.drinksdispenser.domain.model.CoinType;
import es.kiwi.drinksdispenser.domain.model.Coins;
import es.kiwi.drinksdispenser.domain.output.CoinsOutput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CoinsValidationServiceTest {

    @Mock
    private CoinsOutput coinsOutput;

    private CoinsValidationService coinsValidationService;

    @BeforeEach
    void setUp() {
        coinsValidationService = new CoinsValidationService(coinsOutput);
    }

    @Test
    void testCalculateTotal() {
        List<Coins> coins = List.of(
                createCoins(CoinType.ONE_EURO, 2),
                createCoins(CoinType.FIFTY_CENTS, 5)
        );

        BigDecimal total = coinsValidationService.calculateTotal(coins);

        assertEquals(new BigDecimal("4.50"), total);
    }

    @Test
    void testIsSufficientAmount() {
        BigDecimal totalMoney = new BigDecimal("10.00");
        BigDecimal price = new BigDecimal("7.50");

        Mono<Boolean> result = coinsValidationService.isSufficientAmount(totalMoney, price);

        assertEquals(true, result.block());
    }

    @Test
    void  testHasEnoughFundsToChange() {
        Long machineId = 1L;
        BigDecimal totalMoney = new BigDecimal("10.00");
        BigDecimal change = new BigDecimal("5.00");

        when(coinsOutput.consultTotalMoneyInMachine(anyLong()))
                .thenReturn(Mono.just(totalMoney));

        Mono<Boolean> result = coinsValidationService.hasEnoughFundsToChange(machineId, change);

        assertEquals(true, result.block());
    }

    @Test
    void testReduceChange() {

        Long machineId = 1L;
        BigDecimal change = new BigDecimal("1.50");

        List<Coins> inputCoins = List.of(
                createCoins(CoinType.ONE_EURO, 2),
                createCoins(CoinType.FIFTY_CENTS, 2)
        );

        List<Coins> expectedChangeCoins = List.of(
                createCoins(CoinType.ONE_EURO, 1),
                createCoins(CoinType.FIFTY_CENTS, 1)
        );

        when(coinsOutput.findByMachineId(anyLong()))
                .thenReturn(Flux.fromIterable(inputCoins));

        Mono<List<Coins>> result = coinsValidationService.reduceChange(machineId, change);

        when(coinsOutput.saveAll(anyList()))
                .thenReturn(Mono.empty());

        List<Coins> actualChangeCoins = result.block();
        assertEquals(expectedChangeCoins.size(), actualChangeCoins.size());
        for (int i = 0; i < expectedChangeCoins.size(); i++) {
            assertEquals(expectedChangeCoins.get(i).getCoinType(), actualChangeCoins.get(i).getCoinType());
            assertEquals(expectedChangeCoins.get(i).getQuantity(), actualChangeCoins.get(i).getQuantity());
        }
    }




    @Test
    void testCanProvideChange() {
        Long machineId = 1L;
        BigDecimal change = new BigDecimal("3.50");

        List<Coins> coins = List.of(
                createCoins(CoinType.ONE_EURO, 2),
                createCoins(CoinType.FIFTY_CENTS, 5)
        );

        when(coinsOutput.findByMachineId(anyLong()))
                .thenReturn(Flux.fromIterable(coins));

        Mono<Boolean> result = coinsValidationService.canProvideChange(machineId, change);

        assertEquals(true, result.block());
    }

    private static Coins createCoins(CoinType coinType, int quantity) {
        Coins coins = new Coins();
        coins.setCoinType(coinType);
        coins.setQuantity(quantity);
        return coins;
    }
}