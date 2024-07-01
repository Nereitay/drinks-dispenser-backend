package es.kiwi.drinksdispenser.integration.event.manager;

import es.kiwi.drinksdispenser.domain.model.MachineStatus;
import es.kiwi.drinksdispenser.domain.model.Machines;
import es.kiwi.drinksdispenser.domain.model.Products;
import es.kiwi.drinksdispenser.domain.model.ProductsOption;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.MessageChannel;


import javax.annotation.Resource;
import java.math.BigDecimal;


@SpringBootTest
@EnableBinding(Source.class)
class ProductStockZeroEventHandlerTest {
    @Resource(name = "zeroStockNotifierChannel-in-0")
    private MessageChannel zeroStockNotifierChannel;

    @Autowired
    private ProductStockZeroEventHandler eventHandler;

    @Test
    public void testHandle() {

        Machines machines = new Machines(1L, "Machine A", MachineStatus.OUT_OF_ORDER);
        Products products = new Products(4L, ProductsOption.ORANGE_JUICE, new BigDecimal("2.00"));
        ProductStockZeroEvent event = new ProductStockZeroEvent(machines, products);

        eventHandler.handle(event);

    }
}