package es.kiwi.drinksdispenser.domain.model;

import lombok.Data;

@Data
public class Coins {
    private Long id;

    private Machines machine;

    private CoinType coinType;

    private Integer quantity;
}
