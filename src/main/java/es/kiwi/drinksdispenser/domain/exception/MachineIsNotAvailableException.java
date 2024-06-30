package es.kiwi.drinksdispenser.domain.exception;

public class MachineIsNotAvailableException extends RuntimeException {
    public MachineIsNotAvailableException(String message) {
        super(message);
    }
}
