package es.kiwi.drinksdispenser.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MachineProducts {
    private Long id;

    private Machines machine;

    private Products product;

    private int stock;

    private LocalDate expirationDate;
}
