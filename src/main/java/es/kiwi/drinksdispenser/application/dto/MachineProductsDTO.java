package es.kiwi.drinksdispenser.application.dto;

import es.kiwi.drinksdispenser.domain.model.ProductsOption;
import lombok.Data;

import java.time.LocalDate;

@Data
public class MachineProductsDTO {

    private Long machineId;
    private ProductsOption product;
    private int quantity;
    private LocalDate expirationDate;

}
