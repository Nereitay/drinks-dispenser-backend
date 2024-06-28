package es.kiwi.drinksdispenser.domain.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class MachineProducts {
    private Long id;

    private Machines machine;

    private Products product;

    private int stock;

    private LocalDate expirationDate;
}
