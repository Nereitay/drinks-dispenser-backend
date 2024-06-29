package es.kiwi.drinksdispenser.application.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductStockVO {
    private Long machineId;
    private String productName;
    private int stock;
}
