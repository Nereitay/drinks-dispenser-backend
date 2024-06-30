package es.kiwi.drinksdispenser.application.vo;

import es.kiwi.drinksdispenser.domain.model.ProductsOption;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductsOptionVO {

    private ProductsOption productsOption;

    private Double price;

}
