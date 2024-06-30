package es.kiwi.drinksdispenser.application.vo;

import es.kiwi.drinksdispenser.domain.model.CoinType;
import es.kiwi.drinksdispenser.domain.model.Coins;
import es.kiwi.drinksdispenser.domain.model.ProductsOption;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DispenseDrinkVO {
    private String message;
    private List<CoinsVO> coinDispensationList;
    private BigDecimal totalDispensedCoinsValue;
    private ProductsOption productsDispensed;
}

