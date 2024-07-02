package es.kiwi.drinksdispenser.application.service;

import es.kiwi.drinksdispenser.application.dto.DispenseDrinkDTO;
import es.kiwi.drinksdispenser.application.mapper.CoinsVOMapper;
import es.kiwi.drinksdispenser.application.vo.CoinsVO;
import es.kiwi.drinksdispenser.domain.model.*;
import es.kiwi.drinksdispenser.domain.output.CoinsOutput;
import es.kiwi.drinksdispenser.domain.output.MachineProductsOutput;
import es.kiwi.drinksdispenser.domain.service.CoinsValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class DispenseDrinkServiceTest {

    @Mock
    private CoinsOutput coinsOutput;

    @Mock
    private CoinsValidationService coinsValidationService;

    @Mock
    private MachineProductsOutput machineProductsOutput;

    @Mock
    private CoinsVOMapper coinsVOMapper;

    @InjectMocks
    private DispenseDrinkService dispenseDrinkService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testDispenseDrink_Success() {
        DispenseDrinkDTO command = createDispenseDrinkDTO(true);
        List<Coins> coinsList = createCoinsList(command);

        MachineProducts machineProducts = new MachineProducts();
        machineProducts.setProduct(new Products(1L, ProductsOption.COKE, new BigDecimal("2.00")));

        when(coinsOutput.insertCoins(any())).thenReturn(Mono.empty());
        when(machineProductsOutput.findAvailableProduct(any(), any())).thenReturn(Mono.just(machineProducts));
        when(coinsValidationService.calculateTotal(any())).thenReturn(BigDecimal.valueOf(15));
        when(coinsValidationService.isSufficientAmount(any(), any())).thenReturn(Mono.just(true));
        when(coinsValidationService.hasEnoughFundsToChange(any(), any())).thenReturn(Mono.just(true));
        when(coinsValidationService.canProvideChange(any(), any())).thenReturn(Mono.just(true));
        when(machineProductsOutput.reduceProduct(any())).thenReturn(Mono.empty());
        when(coinsValidationService.reduceChange(any(), any())).thenReturn(Mono.just(coinsList));
        when(coinsVOMapper.toCoinsVOList(any())).thenReturn(coinsList.stream().map(coins -> new CoinsVO(coins.getCoinType(), coins.getQuantity())).collect(Collectors.toList()));

        StepVerifier.create(dispenseDrinkService.dispenseDrink(command))
                .expectNextMatches(vo -> vo.getMessage().contains("SUCCEED"))
                .verifyComplete();
    }

    @Test
    void testDispenseDrink_InsufficientAmount() {
        DispenseDrinkDTO command = createDispenseDrinkDTO(true);
        List<Coins> coinsList = createCoinsList(command);

        MachineProducts machineProducts = new MachineProducts();
        machineProducts.setProduct(new Products(1L, ProductsOption.COKE, BigDecimal.TEN));

        when(coinsOutput.insertCoins(any())).thenReturn(Mono.empty());
        when(machineProductsOutput.findAvailableProduct(any(), any())).thenReturn(Mono.just(machineProducts));
        when(coinsValidationService.calculateTotal(any())).thenReturn(BigDecimal.valueOf(5));
        when(coinsValidationService.isSufficientAmount(any(), any())).thenReturn(Mono.just(false));
        when(coinsOutput.reduceCoins(any())).thenReturn(Mono.empty());
        when(coinsVOMapper.toCoinsVOList(any())).thenReturn(coinsList.stream().map(coins -> new CoinsVO(coins.getCoinType(), coins.getQuantity())).collect(Collectors.toList()));

        StepVerifier.create(dispenseDrinkService.dispenseDrink(command))
                .expectNextMatches(vo -> vo.getMessage().contains("Insufficient coins Amount"))
                .verifyComplete();
    }

    private DispenseDrinkDTO createDispenseDrinkDTO(boolean isConfirmed) {
        return new DispenseDrinkDTO(
                1L,
                ProductsOption.COKE,
                Arrays.asList(CoinType.FIVE_CENTS, CoinType.TEN_CENTS),
                isConfirmed
        );
    }

    private List<Coins> createCoinsList(DispenseDrinkDTO command) {
        return command.getCoinTypeList().stream().collect(Collectors.groupingBy(coinType -> coinType,
                Collectors.counting())).entrySet().stream().map(entry -> {
            Coins coins = new Coins();
            coins.setCoinType(entry.getKey());
            coins.setMachineId(command.getMachineId());
            coins.setQuantity(Math.toIntExact(entry.getValue()));
            return coins;
        }).collect(Collectors.toList());
    }

}