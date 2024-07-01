package es.kiwi.drinksdispenser.infrastructure.persistence;

import es.kiwi.drinksdispenser.domain.model.Products;
import es.kiwi.drinksdispenser.domain.model.ProductsOption;
import es.kiwi.drinksdispenser.infrastructure.persistence.dao.ProductsDAO;
import es.kiwi.drinksdispenser.infrastructure.persistence.mapper.ProductsDAOMapper;
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

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class ProductsPersistenceAdapterTest {
    @Mock
    private ProductsDAORepository productsDAORepository;

    @Mock
    private ProductsDAOMapper productsDAOMapper;

    @InjectMocks
    private ProductsPersistenceAdapter productsPersistenceAdapter;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFindByName() {
        String productName = "Coke";
        ProductsDAO productsDAO = new ProductsDAO(1L, "Coke", new BigDecimal("2.00"), "DRINK");
        Products products = new Products(1L, ProductsOption.COKE, new BigDecimal("2.00"));

        when(productsDAORepository.findByName(anyString())).thenReturn(Mono.just(productsDAO));

        when(productsDAOMapper.productsDAOToProducts(productsDAO)).thenReturn(products);

        StepVerifier.create(productsPersistenceAdapter.findByName(productName))
                .expectNextMatches(result -> result.equals(products))
                .verifyComplete();
    }

    @Test
    public void testFindByName_NotFound() {
        String productName = "Coke";

        when(productsDAORepository.findByName(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(productsPersistenceAdapter.findByName(productName))
                .verifyComplete();
    }

    @Test
    public void testFindAllByMachineId() {
        Long machineId = 1L;
        ProductsDAO productsDAO1 = new ProductsDAO(1L, "Coke", new BigDecimal("2.00"), "DRINK");
        ProductsDAO productsDAO2 = new ProductsDAO(2L, "Water", new BigDecimal("2.00"), "DRINK");
        Products products1 = new Products(1L, ProductsOption.COKE, new BigDecimal("2.00"));
        Products products2 = new Products(2L, ProductsOption.WATER, new BigDecimal("2.00"));

        when(productsDAORepository.findAllByMachineId(anyLong())).thenReturn(Flux.just(productsDAO1, productsDAO2));

        when(productsDAOMapper.productsDAOToProducts(productsDAO1)).thenReturn(products1);
        when(productsDAOMapper.productsDAOToProducts(productsDAO2)).thenReturn(products2);

        StepVerifier.create(productsPersistenceAdapter.findAllByMachineId(machineId))
                .expectNextMatches(result -> result.equals(products1))
                .expectNextMatches(result -> result.equals(products2))
                .verifyComplete();
    }
}