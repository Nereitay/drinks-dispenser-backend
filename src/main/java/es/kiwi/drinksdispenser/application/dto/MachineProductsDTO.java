package es.kiwi.drinksdispenser.application.dto;

import es.kiwi.drinksdispenser.domain.model.ProductsOption;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MachineProductsDTO {

    private Long machineId;
    private ProductsOption product;
    @Min(value = 1, message = "Quantity should be greater than 0")
    private int quantity;
    private LocalDate expirationDate;

}
