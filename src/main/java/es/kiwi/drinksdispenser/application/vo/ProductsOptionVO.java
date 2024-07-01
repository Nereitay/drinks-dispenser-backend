package es.kiwi.drinksdispenser.application.vo;

import es.kiwi.drinksdispenser.domain.model.ProductsOption;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductsOptionVO {

    private ProductsOption productsOption;

    private BigDecimal price;

}
