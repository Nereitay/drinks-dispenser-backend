package es.kiwi.drinksdispenser.application.service;

import es.kiwi.drinksdispenser.application.vo.ProductStockVO;
import es.kiwi.drinksdispenser.domain.model.MachineProducts;
import es.kiwi.drinksdispenser.domain.output.MachineProductsOutput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class ProductStockServiceTest {
    @Mock
    private MachineProductsOutput machineProductsOutput;

    @InjectMocks
    private ProductStockService productStockService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testConsultProductStock_Success() {
        Long machineId = 1L;
        String productName = "Coke";
        MachineProducts product1 = new MachineProducts();
        product1.setExpirationDate(LocalDate.now().plusDays(10));
        product1.setStock(10);

        MachineProducts product2 = new MachineProducts();
        product2.setExpirationDate(LocalDate.now().plusDays(5));
        product2.setStock(15);

        when(machineProductsOutput.findByMachineIdAndProduct(anyLong(), anyString()))
                .thenReturn(Flux.just(product1, product2));

        Mono<ProductStockVO> result = productStockService.consultProductStock(machineId, productName);

        StepVerifier.create(result)
                .expectNextMatches(vo -> vo.getMachineId().equals(machineId) &&
                        vo.getProductName().equals(productName) &&
                        vo.getStock() == 25)
                .verifyComplete();
    }

    @Test
    void testConsultProductStock_NoStock() {
        Long machineId = 1L;
        String productName = "Coke";

        when(machineProductsOutput.findByMachineIdAndProduct(anyLong(), anyString()))
                .thenReturn(Flux.empty());

        Mono<ProductStockVO> result = productStockService.consultProductStock(machineId, productName);

        StepVerifier.create(result)
                .expectNextMatches(vo -> vo.getMachineId().equals(machineId) &&
                        vo.getProductName().equals(productName) &&
                        vo.getStock() == 0)
                .verifyComplete();
    }

    @Test
    void testConsultProductStock_ExpiredProduct() {
        Long machineId = 1L;
        String productName = "Coke";
        MachineProducts expiredProduct = new MachineProducts();
        expiredProduct.setExpirationDate(LocalDate.now().minusDays(1));
        expiredProduct.setStock(10);

        when(machineProductsOutput.findByMachineIdAndProduct(anyLong(), anyString()))
                .thenReturn(Flux.just(expiredProduct));

        Mono<ProductStockVO> result = productStockService.consultProductStock(machineId, productName);

        StepVerifier.create(result)
                .expectNextMatches(vo -> vo.getMachineId().equals(machineId) &&
                        vo.getProductName().equals(productName) &&
                        vo.getStock() == 0)
                .verifyComplete();
    }
}