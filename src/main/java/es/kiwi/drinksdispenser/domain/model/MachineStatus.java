package es.kiwi.drinksdispenser.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MachineStatus {

    AVAILABLE(0), OUT_OF_ORDER(1), MAINTENANCE(2);
    private final int status;
}
