package es.kiwi.drinksdispenser.application.vo;

import es.kiwi.drinksdispenser.domain.model.Coins;
import es.kiwi.drinksdispenser.domain.model.ProductsOption;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DispenseDrinkVO {
    private String message;
    private List<Coins> coinsList;
    private Double totalCoinsValue;
    private ProductsOption productsOption;
}

