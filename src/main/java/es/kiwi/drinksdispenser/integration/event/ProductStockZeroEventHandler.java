package es.kiwi.drinksdispenser.integration.event;

import org.springframework.stereotype.Component;

@Component
public class ProductStockZeroEventHandler {

    public void handle(ProductStockZeroEvent event) {
        // Logic to handle product stock zero event
        System.out.println("Product stock is zero for product: " + event.toString());
    }
}