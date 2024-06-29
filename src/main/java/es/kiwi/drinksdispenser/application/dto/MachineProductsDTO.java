package es.kiwi.drinksdispenser.application.dto;

import es.kiwi.drinksdispenser.domain.model.ProductsOption;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MachineProductsDTO {

    private Long machineId;
    private ProductsOption product;
    private int quantity;
    private LocalDate expirationDate;

}
