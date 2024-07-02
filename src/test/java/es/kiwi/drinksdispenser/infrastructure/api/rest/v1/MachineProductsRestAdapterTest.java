package es.kiwi.drinksdispenser.infrastructure.api.rest.v1;

import es.kiwi.drinksdispenser.application.dto.MachineProductsDTO;
import es.kiwi.drinksdispenser.application.vo.ProductsOptionVO;
import es.kiwi.drinksdispenser.domain.model.ProductsOption;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@AutoConfigureWebTestClient
class MachineProductsRestAdapterTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    @DisplayName("Test consultProductsOption endpoint")
    void testConsultProductsOption() {
        List<ProductsOptionVO> expectedResult = List.of(
                new ProductsOptionVO(ProductsOption.COKE, new BigDecimal("2.0")),
                new ProductsOptionVO(ProductsOption.REDBULL, new BigDecimal("2.25")),
                new ProductsOptionVO(ProductsOption.WATER, new BigDecimal("0.5")),
                new ProductsOptionVO(ProductsOption.ORANGE_JUICE, new BigDecimal("1.95")));


        webTestClient.get().uri("/v1/machine-products/products-option/{machineId}", 1L)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ProductsOptionVO.class)
                .value(response -> assertEquals(expectedResult, response));
    }

    @Test
    @DisplayName("Test addProductToMachine endpoint")
    void testAddProductStock() {
        List<MachineProductsDTO> machineProductsDTOS = List.of(new MachineProductsDTO(1L, ProductsOption.COKE, 1, LocalDate.parse("2024-07-31")),
                new MachineProductsDTO(1L, ProductsOption.ORANGE_JUICE, 1, LocalDate.parse("2025-01-01")));


        webTestClient.post().uri("/v1/machine-products")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(machineProductsDTOS)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Void.class);

    }

}