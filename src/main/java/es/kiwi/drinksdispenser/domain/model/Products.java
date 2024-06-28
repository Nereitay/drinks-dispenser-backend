package es.kiwi.drinksdispenser.domain.model;

import lombok.Data;

@Data
public class Products {
    private Long id;

    private ProductsOption productsOption;
}
