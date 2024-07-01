package es.kiwi.drinksdispenser.infrastructure.persistence;

import es.kiwi.drinksdispenser.domain.model.*;
import es.kiwi.drinksdispenser.infrastructure.persistence.dao.MachineProductsDAO;
import es.kiwi.drinksdispenser.infrastructure.persistence.dao.MachinesDAO;
import es.kiwi.drinksdispenser.infrastructure.persistence.dao.ProductsDAO;
import es.kiwi.drinksdispenser.infrastructure.persistence.mapper.MachineDAOMapper;
import es.kiwi.drinksdispenser.infrastructure.persistence.mapper.MachineProductsDAOMapper;
import es.kiwi.drinksdispenser.infrastructure.persistence.mapper.ProductsDAOMapper;
import es.kiwi.drinksdispenser.infrastructure.persistence.repository.MachineProductsDAORepository;
import es.kiwi.drinksdispenser.infrastructure.persistence.repository.MachinesDAORepository;
import es.kiwi.drinksdispenser.infrastructure.persistence.repository.ProductsDAORepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


class MachineProductsPersistenceAdapterTest {
    @Mock
    private MachineProductsDAORepository machineProductsDAORepository;

    @Mock
    private MachineProductsDAOMapper machineProductsDAOMapper;

    @Mock
    private ProductsDAORepository productsDAORepository;

    @Mock
    private MachinesDAORepository machinesDAORepository;

    @Mock
    private ProductsDAOMapper productsDAOMapper;

    @Mock
    private MachineDAOMapper machineDAOMapper;

    @InjectMocks
    private MachineProductsPersistenceAdapter machineProductsPersistenceAdapter;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSave() {
        MachineProducts machineProduct = new MachineProducts(1L,
                new Machines(1L, "Drink Dispenser 001", MachineStatus.AVAILABLE),
                new Products(1L, ProductsOption.COKE, new BigDecimal("2.00")), 10, LocalDate.parse("2024-12-31"));
        List<MachineProducts> machineProductsList = List.of(machineProduct);
        ProductsDAO productsDAO = new ProductsDAO(1L, "Coke", new BigDecimal("2.00"), "DRINK");
        when(productsDAORepository.findByName(anyString())).thenReturn(Mono.just(productsDAO));
        when(machineProductsDAORepository.findByMachineIdAndProductIdAndExpirationDate(anyLong(), anyLong(), any(LocalDate.class)))
                .thenReturn(Mono.empty());
        MachineProductsDAO machineProductsDAO = new MachineProductsDAO(1L, 1L, 1L, 10,
                LocalDate.parse("2024-12-31"), "KIWI", LocalDateTime.now());
        when(machineProductsDAOMapper.toMachineProductsDAO(any(MachineProducts.class))).thenReturn(machineProductsDAO);
        when(machineProductsDAORepository.save(any(MachineProductsDAO.class))).thenReturn(Mono.just(machineProductsDAO));

        StepVerifier.create(machineProductsPersistenceAdapter.save(machineProductsList))
                .verifyComplete();

        verify(machineProductsDAORepository, times(1)).save(any(MachineProductsDAO.class));
    }

    @Test
    public void testFindByMachineIdAndProduct() {
        Long machineId = 1L;
        String productName = "ProductA";
        MachinesDAO machineDAO = new MachinesDAO(1L, "Name", "L", 0, "L", LocalDateTime.now(), LocalDateTime.now());
        ProductsDAO productsDAO = new ProductsDAO(1L, "Coke", new BigDecimal("2.00"), "DRINK");
        MachineProductsDAO machineProductsDAO = new MachineProductsDAO(1L, 1L, 1L, 10,
                LocalDate.parse("2024-12-31"), "KIWI", LocalDateTime.now());
        MachineProducts machineProduct = new MachineProducts(1L,
                new Machines(1L, "Drink Dispenser 001", MachineStatus.AVAILABLE),
                new Products(1L, ProductsOption.COKE, new BigDecimal("2.00")), 10, LocalDate.parse("2024-12-31"));

        when(machinesDAORepository.findById(machineId)).thenReturn(Mono.just(machineDAO));
        when(productsDAORepository.findByName(productName)).thenReturn(Mono.just(productsDAO));
        when(machineProductsDAORepository.findByMachineIdAndProductId(machineId, productsDAO.getId())).thenReturn(Flux.just(machineProductsDAO));
        when(machineProductsDAOMapper.toMachineProducts(machineProductsDAO)).thenReturn(machineProduct);
        when(productsDAOMapper.productsDAOToProducts(productsDAO)).thenReturn(new Products());
        when(machineDAOMapper.machinesDAOToMachines(machineDAO)).thenReturn(new Machines());

        StepVerifier.create(machineProductsPersistenceAdapter.findByMachineIdAndProduct(machineId, productName))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void testReduceProduct() {
        MachineProducts machineProducts =  new MachineProducts(1L,
                new Machines(1L, "Drink Dispenser 001", MachineStatus.AVAILABLE),
                new Products(1L, ProductsOption.COKE, new BigDecimal("2.00")), 10, LocalDate.parse("2024-12-31"));
        machineProducts.setId(1L);

        when(machineProductsDAORepository.reduceStock(machineProducts.getId())).thenReturn(Mono.empty());

        StepVerifier.create(machineProductsPersistenceAdapter.reduceProduct(machineProducts))
                .verifyComplete();

        verify(machineProductsDAORepository, times(1)).reduceStock(machineProducts.getId());
    }



}