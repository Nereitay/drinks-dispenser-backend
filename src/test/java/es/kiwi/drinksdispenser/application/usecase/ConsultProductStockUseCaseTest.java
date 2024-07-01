package es.kiwi.drinksdispenser.application.usecase;

import es.kiwi.drinksdispenser.application.service.ProductStockService;
import es.kiwi.drinksdispenser.application.vo.ProductStockVO;
import es.kiwi.drinksdispenser.domain.model.ProductsOption;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class ConsultProductStockUseCaseTest {
    @Mock
    private ProductStockService productStockService;

    @InjectMocks
    private ConsultProductStockUseCase consultProductStockUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testConsultProductStock_Success() {
        Long machineId = 1L;
        String productName = "Coke";

        ProductStockVO expectedVO = new ProductStockVO(machineId, productName, 10);

        when(productStockService.consultProductStock(anyLong(), anyString())).thenReturn(Mono.just(expectedVO));

        Mono<ProductStockVO> result = consultProductStockUseCase.consultProductStock(machineId, ProductsOption.COKE);

        StepVerifier.create(result)
                .expectNextMatches(vo -> vo.getMachineId().equals(machineId) && vo.getProductName().equals(productName) && vo.getStock() == 10)
                .verifyComplete();

        verify(productStockService, times(1)).consultProductStock(machineId, productName);
    }

    @Test
    void testConsultProductStock_Error() {
        Long machineId = 1L;
        String productName = "Coke";

        when(productStockService.consultProductStock(anyLong(), anyString())).thenReturn(Mono.error(new RuntimeException("Product not found")));

        Mono<ProductStockVO> result = consultProductStockUseCase.consultProductStock(machineId, ProductsOption.COKE);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("Product not found"))
                .verify();

        verify(productStockService, times(1)).consultProductStock(machineId, productName);
    }

}