package es.kiwi.drinksdispenser.application.usecase;

import es.kiwi.drinksdispenser.application.mapper.ProductsOptionVOMapper;
import es.kiwi.drinksdispenser.application.vo.ProductsOptionVO;
import es.kiwi.drinksdispenser.domain.exception.MachineIsNotAvailableException;
import es.kiwi.drinksdispenser.domain.exception.MachineNotFoundException;
import es.kiwi.drinksdispenser.domain.model.MachineStatus;
import es.kiwi.drinksdispenser.domain.model.Machines;
import es.kiwi.drinksdispenser.domain.model.Products;
import es.kiwi.drinksdispenser.domain.model.ProductsOption;
import es.kiwi.drinksdispenser.domain.output.MachinesOutput;
import es.kiwi.drinksdispenser.domain.output.ProductsOutput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class ConsultProductsOptionUseCaseTest {
    @Mock
    private ProductsOutput productsOutput;

    @Mock
    private MachinesOutput machinesOutput;

    @Mock
    private ProductsOptionVOMapper productsOptionVOMapper;

    @InjectMocks
    private ConsultProductsOptionUseCase consultProductsOptionUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testConsultProductsOptions_Success() {
        Long machineId = 1L;

        Machines machine = new Machines();
        machine.setId(machineId);
        machine.setStatus(MachineStatus.AVAILABLE);

        when(machinesOutput.findById(anyLong())).thenReturn(Mono.just(machine));
        when(productsOutput.findAllByMachineId(anyLong())).thenReturn(Flux.just(
                new Products(1L, ProductsOption.COKE, new BigDecimal("2.00")),
                new Products(2L, ProductsOption.WATER, new BigDecimal("0.50")
        )));
        when(productsOptionVOMapper.toProductsOptionVO(any())).thenAnswer(invocation -> {
            es.kiwi.drinksdispenser.domain.model.Products product = invocation.getArgument(0);
            return new ProductsOptionVO(product.getProductsOption(), product.getPrice());
        });

        Flux<ProductsOptionVO> result = consultProductsOptionUseCase.consultProductsOptions(machineId);

        StepVerifier.create(result)
                .expectNextMatches(vo -> vo.getProductsOption() == ProductsOption.COKE)
                .expectNextMatches(vo -> vo.getProductsOption() == ProductsOption.WATER)
                .verifyComplete();

        verify(machinesOutput, times(1)).findById(machineId);
        verify(productsOutput, times(1)).findAllByMachineId(machineId);
        verify(productsOptionVOMapper, times(2)).toProductsOptionVO(any());
    }

    @Test
    void testConsultProductsOptions_MachineNotFound() {
        Long machineId = 1L;

        when(machinesOutput.findById(anyLong())).thenReturn(Mono.empty());

        Flux<ProductsOptionVO> result = consultProductsOptionUseCase.consultProductsOptions(machineId);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof MachineNotFoundException &&
                        throwable.getMessage().equals("Machine with ID " + machineId + " is not found."))
                .verify();

        verify(machinesOutput, times(1)).findById(machineId);
        verify(productsOutput, never()).findAllByMachineId(anyLong());
        verify(productsOptionVOMapper, never()).toProductsOptionVO(any());
    }

    @Test
    void testConsultProductsOptions_MachineNotAvailable() {
        Long machineId = 1L;

        Machines machine = new Machines();
        machine.setId(machineId);
        machine.setStatus(MachineStatus.OUT_OF_ORDER);

        when(machinesOutput.findById(anyLong())).thenReturn(Mono.just(machine));

        Flux<ProductsOptionVO> result = consultProductsOptionUseCase.consultProductsOptions(machineId);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof MachineIsNotAvailableException &&
                        throwable.getMessage().equals("Machine with ID " + machineId + " is not available."))
                .verify();

        verify(machinesOutput, times(1)).findById(machineId);
        verify(productsOutput, never()).findAllByMachineId(anyLong());
        verify(productsOptionVOMapper, never()).toProductsOptionVO(any());
    }
}