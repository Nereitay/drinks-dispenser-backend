package es.kiwi.drinksdispenser.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Coins {
    private Long id;

    private Long machineId;

    private CoinType coinType;

    private Integer quantity;
}
