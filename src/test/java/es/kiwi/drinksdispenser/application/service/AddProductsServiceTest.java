package es.kiwi.drinksdispenser.application.service;

import es.kiwi.drinksdispenser.domain.model.MachineProducts;
import es.kiwi.drinksdispenser.domain.model.Machines;
import es.kiwi.drinksdispenser.domain.model.Products;
import es.kiwi.drinksdispenser.domain.model.ProductsOption;
import es.kiwi.drinksdispenser.domain.output.MachineProductsOutput;
import es.kiwi.drinksdispenser.domain.output.MachinesOutput;
import es.kiwi.drinksdispenser.domain.output.ProductsOutput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AddProductsServiceTest {
    @Mock
    private MachineProductsOutput machineProductsOutput;

    @Mock
    private MachinesOutput machinesOutput;

    @Mock
    private ProductsOutput productsOutput;

    @InjectMocks
    private AddProductsService addProductsService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddProducts_ValidProducts() {
        Machines machine = new Machines();
        machine.setId(1L);

        ProductsOption productsOption = ProductsOption.COKE;

        Products product = new Products();
        product.setId(1L);
        product.setProductsOption(productsOption);

        MachineProducts machineProducts = new MachineProducts();
        machineProducts.setId(1L);
        machineProducts.setMachine(machine);
        machineProducts.setProduct(product);

        List<MachineProducts> machineProductsList = Collections.singletonList(machineProducts);

        when(machinesOutput.findById(1L)).thenReturn(Mono.just(machine));
        when(productsOutput.findByName("Coke")).thenReturn(Mono.just(product));
        when(machineProductsOutput.save(any())).thenReturn(Mono.empty());

        StepVerifier.create(addProductsService.addProducts(machineProductsList))
                .verifyComplete();

        verify(machinesOutput, times(1)).findById(1L);
        verify(productsOutput, times(1)).findByName("Coke");
        verify(machineProductsOutput, times(1)).save(any());
    }

    @Test
    public void testAddProducts_MachineNotFound() {
        MachineProducts machineProducts = new MachineProducts();
        machineProducts.setId(1L);
        machineProducts.setMachine(new Machines());
        machineProducts.getMachine().setId(1L);
        machineProducts.setProduct(new Products());
        machineProducts.getProduct().setProductsOption(ProductsOption.COKE);

        List<MachineProducts> machineProductsList = Collections.singletonList(machineProducts);

        when(machinesOutput.findById(1L)).thenReturn(Mono.empty());

        StepVerifier.create(addProductsService.addProducts(machineProductsList))
                .expectErrorMatches(throwable -> throwable instanceof NullPointerException)
                .verify();

        verify(machinesOutput, times(1)).findById(1L);

    }

    @Test
    public void testAddProducts_ProductNotFound() {
        Machines machine = new Machines();
        machine.setId(1L);

        MachineProducts machineProducts = new MachineProducts();
        machineProducts.setId(1L);
        machineProducts.setMachine(machine);
        machineProducts.setProduct(new Products());
        machineProducts.getProduct().setProductsOption(null);


        List<MachineProducts> machineProductsList = Collections.singletonList(machineProducts);

        when(machinesOutput.findById(1L)).thenReturn(Mono.just(machine));
        when(productsOutput.findByName("Orange")).thenReturn(Mono.empty());

        StepVerifier.create(addProductsService.addProducts(machineProductsList))
                .expectErrorMatches(throwable -> throwable instanceof NullPointerException)
                .verify();

        verify(machinesOutput, times(1)).findById(1L);

    }
}