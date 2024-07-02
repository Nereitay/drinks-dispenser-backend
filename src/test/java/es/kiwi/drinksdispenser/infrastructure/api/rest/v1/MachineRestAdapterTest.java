package es.kiwi.drinksdispenser.infrastructure.api.rest.v1;

import es.kiwi.drinksdispenser.domain.model.MachineStatus;
import es.kiwi.drinksdispenser.domain.model.Machines;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@AutoConfigureWebTestClient
class MachineRestAdapterTest {

    @Autowired
    private WebTestClient webTestClient;

    static Stream<Arguments> provideConsultMachineStatusTest() {
        return Stream.of(Arguments.of(1L, "Drink Dispenser 001", MachineStatus.AVAILABLE),
                Arguments.of(2L, "Drink Dispenser 002", MachineStatus.OUT_OF_ORDER));
    }

    @ParameterizedTest
    @MethodSource("provideConsultMachineStatusTest")
    @DisplayName("Test consult machine status method")
    void consultMachineStatusTest(Long machineId, String name, MachineStatus status) {
        Machines expectedResult = new Machines(machineId, name, status);
        webTestClient.get()
                .uri("/v1/machine/{machineId}", machineId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Machines.class)
                .value(result -> assertEquals(expectedResult, result));
    }

    @Test
    void consultMachineStatusTestNotFound() {
        webTestClient.get()
                .uri("/v1/machine/{machineId}", 0)
                .exchange()
                .expectStatus().isNotFound();
    }
}