package es.kiwi.drinksdispenser.domain.model;

import lombok.Data;

@Data
public class Machines {

    private Long id;

    private String name;

    private MachineStatus status;

}
