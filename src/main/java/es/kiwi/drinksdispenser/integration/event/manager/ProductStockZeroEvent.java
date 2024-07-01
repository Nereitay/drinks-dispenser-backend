package es.kiwi.drinksdispenser.integration.event.manager;

import es.kiwi.drinksdispenser.domain.model.Machines;
import es.kiwi.drinksdispenser.domain.model.Products;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductStockZeroEvent {

    private Machines machines;
    private Products products;

}