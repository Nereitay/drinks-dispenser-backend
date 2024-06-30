package es.kiwi.drinksdispenser.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProductsOption {
    COKE("Coke"),
    REDBULL("Redbull"),
    WATER("Water"),
    ORANGE_JUICE("Orange Juice");
    private final String name;
}
