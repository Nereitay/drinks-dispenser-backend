package es.kiwi.drinksdispenser.domain.model;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProductsOption {
    COKE("Coke", 2),
    REDBULL("Redbull", 2.25),
    WATER("Water", 0.50),
    ORANGE_JUICE("Orange Juice", 1.95);
    @JsonValue
    private final String name;
    private final double price;
}
