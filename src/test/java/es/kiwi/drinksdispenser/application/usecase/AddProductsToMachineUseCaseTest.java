package es.kiwi.drinksdispenser.application.usecase;

import es.kiwi.drinksdispenser.application.dto.MachineProductsDTO;
import es.kiwi.drinksdispenser.application.mapper.MachineProductsDTOMapper;
import es.kiwi.drinksdispenser.application.service.AddProductsService;
import es.kiwi.drinksdispenser.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AddProductsToMachineUseCaseTest {
    @Mock
    private MachineProductsDTOMapper machineProductsDTOMapper;
    @Mock
    private AddProductsService addProductsService;

    @InjectMocks
    private AddProductsToMachineUseCase addProductsToMachineUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddProduct_Success() {
        MachineProductsDTO dto1 = new MachineProductsDTO(1L, ProductsOption.COKE, 10, LocalDate.parse("2024-12-31"));
        MachineProductsDTO dto2 = new MachineProductsDTO(1L, ProductsOption.WATER, 20, LocalDate.parse("2024-12-31"));
        List<MachineProductsDTO> machineProductsDTOList = List.of(dto1, dto2);

        MachineProducts product1 = new MachineProducts(1L,
                new Machines(1L, "Drink Dispenser 001", MachineStatus.AVAILABLE),
                new Products(1L, ProductsOption.COKE, new BigDecimal("2.00")), 10, LocalDate.parse("2024-12-31"));
        MachineProducts product2 = new MachineProducts(1L,
                new Machines(1L, "Drink Dispenser 001", MachineStatus.AVAILABLE),
                new Products(1L, ProductsOption.WATER, new BigDecimal("0.50")), 20, LocalDate.parse("2024-12-31"));
        List<MachineProducts> machineProductsList = List.of(product1, product2);

        when(machineProductsDTOMapper.toMachineProductsList(any())).thenReturn(machineProductsList);
        when(addProductsService.addProducts(any())).thenReturn(Mono.empty());

        StepVerifier.create(addProductsToMachineUseCase.addProduct(machineProductsDTOList))
                .verifyComplete();

        verify(machineProductsDTOMapper, times(1)).toMachineProductsList(machineProductsDTOList);
        verify(addProductsService, times(1)).addProducts(machineProductsList);
    }

    @Test
    void testAddProduct_Error() {
        MachineProductsDTO dto1 = new MachineProductsDTO(1L, ProductsOption.COKE, 10, LocalDate.parse("2024-12-31"));
        List<MachineProductsDTO> machineProductsDTOList = List.of(dto1);

        MachineProducts product1 = new MachineProducts(1L,
                new Machines(1L, "Drink Dispenser 001", MachineStatus.AVAILABLE),
                new Products(1L, ProductsOption.COKE, new BigDecimal("2.00")), 10, LocalDate.parse("2024-12-31"));
        List<MachineProducts> machineProductsList = List.of(product1);

        when(machineProductsDTOMapper.toMachineProductsList(any())).thenReturn(machineProductsList);
        when(addProductsService.addProducts(any())).thenReturn(Mono.error(new RuntimeException("Add products failed")));

        StepVerifier.create(addProductsToMachineUseCase.addProduct(machineProductsDTOList))
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("Add products failed"))
                .verify();

        verify(machineProductsDTOMapper, times(1)).toMachineProductsList(machineProductsDTOList);
        verify(addProductsService, times(1)).addProducts(machineProductsList);
    }
}