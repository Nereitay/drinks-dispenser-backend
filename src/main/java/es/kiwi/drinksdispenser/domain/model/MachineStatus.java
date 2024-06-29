package es.kiwi.drinksdispenser.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MachineStatus {

    AVAILABLE(0, "Available"), OUT_OF_ORDER(1, "Out of Order"), MAINTENANCE(2, "Maintenance");
    private final int status;
    private final String description;
}
