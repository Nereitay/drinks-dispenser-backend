package es.kiwi.drinksdispenser.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
public enum CoinType {
    FIVE_CENTS("FIVE_CENTS",new BigDecimal("0.05")),
    TEN_CENTS("TEN_CENTS", new BigDecimal("0.10")),
    TWENTY_CENTS("TWENTY_CENTS", new BigDecimal("0.20")),
    FIFTY_CENTS("FIFTY_CENTS", new BigDecimal("0.50")),
    ONE_EURO("ONE_EURO", new BigDecimal("1.00")),
    TWO_EURO("TWO_EURO", new BigDecimal("2.00"));

    private final String denomination;
    private final BigDecimal value;

}
