package es.kiwi.drinksdispenser.application.vo;

import es.kiwi.drinksdispenser.domain.model.CoinType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CoinsVO {
    private CoinType coinType;
    private int quantity;
}
