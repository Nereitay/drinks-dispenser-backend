package es.kiwi.drinksdispenser.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Products {
    private Long id;

    private ProductsOption productsOption;
}
