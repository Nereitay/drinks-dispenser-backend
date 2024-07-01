package es.kiwi.drinksdispenser.application.usecase;

import es.kiwi.drinksdispenser.application.dto.DispenseDrinkDTO;
import es.kiwi.drinksdispenser.application.service.DispenseDrinkService;
import es.kiwi.drinksdispenser.application.vo.DispenseDrinkVO;
import es.kiwi.drinksdispenser.domain.model.ProductsOption;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DispenseDrinkUseCaseTest {
    @Mock
    private DispenseDrinkService dispenseDrinkService;

    @InjectMocks
    private DispenseDrinkUseCase dispenseDrinkUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testExecute_Success() {
        DispenseDrinkDTO command = new DispenseDrinkDTO(1L, ProductsOption.COKE, Collections.emptyList(), true);
        DispenseDrinkVO expectedVO = new DispenseDrinkVO("Success message", Collections.emptyList(), null, ProductsOption.COKE);

        when(dispenseDrinkService.dispenseDrink(any(DispenseDrinkDTO.class))).thenReturn(Mono.just(expectedVO));

        Mono<DispenseDrinkVO> result = dispenseDrinkUseCase.execute(command);

        StepVerifier.create(result)
                .expectNextMatches(vo -> vo.getMessage().equals("Success message") && vo.getProductsDispensed() == ProductsOption.COKE)
                .verifyComplete();

        verify(dispenseDrinkService, times(1)).dispenseDrink(command);
    }

    @Test
    void testExecute_Error() {
        DispenseDrinkDTO command = new DispenseDrinkDTO(1L, ProductsOption.COKE, Collections.emptyList(), true);

        when(dispenseDrinkService.dispenseDrink(any(DispenseDrinkDTO.class))).thenReturn(Mono.error(new RuntimeException("Dispense failed")));

        Mono<DispenseDrinkVO> result = dispenseDrinkUseCase.execute(command);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("Dispense failed"))
                .verify();

        verify(dispenseDrinkService, times(1)).dispenseDrink(command);
    }
}