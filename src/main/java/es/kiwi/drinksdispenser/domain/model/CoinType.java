package es.kiwi.drinksdispenser.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CoinType {
    FIVE_CENTS(0.05),
    TEN_CENTS(0.10),
    TWENTY_CENTS(0.20),
    FIFTY_CENTS(0.50),
    ONE_EURO(1.00),
    TWO_EURO(2.00);

    private final double value;

}
