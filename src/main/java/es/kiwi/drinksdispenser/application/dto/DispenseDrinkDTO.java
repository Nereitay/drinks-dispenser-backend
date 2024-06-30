package es.kiwi.drinksdispenser.application.dto;

import es.kiwi.drinksdispenser.domain.model.CoinType;
import es.kiwi.drinksdispenser.domain.model.ProductsOption;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DispenseDrinkDTO {
    private Long machineId;
    private ProductsOption productsOption;
    private List<CoinType> coinTypeList;
    private boolean isConfirmed;
}