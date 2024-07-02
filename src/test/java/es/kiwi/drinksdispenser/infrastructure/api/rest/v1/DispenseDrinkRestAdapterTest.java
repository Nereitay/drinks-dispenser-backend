package es.kiwi.drinksdispenser.infrastructure.api.rest.v1;

import es.kiwi.drinksdispenser.application.dto.DispenseDrinkDTO;
import es.kiwi.drinksdispenser.application.vo.CoinsVO;
import es.kiwi.drinksdispenser.application.vo.DispenseDrinkVO;
import es.kiwi.drinksdispenser.domain.model.CoinType;
import es.kiwi.drinksdispenser.domain.model.ProductsOption;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

import static es.kiwi.drinksdispenser.application.constants.DispenseDrinkMessageConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@SpringBootTest
@AutoConfigureWebTestClient
class DispenseDrinkRestAdapterTest {

    @Autowired
    private WebTestClient webTestClient;

    @ParameterizedTest
    @MethodSource("provideDispenseDrinkTestArguments")
    @DisplayName("Test dispenseDrink method")
    void dispenseDrinkTest(Long machineId, ProductsOption productChose,
                           List<CoinType> coinReceivedList, boolean isConfirmed,
                           String expectedMessage, List<CoinsVO> changeCoinList,
                           BigDecimal totalChangeAmount, ProductsOption productDispensed) {
        DispenseDrinkDTO requestDto = new DispenseDrinkDTO(machineId, productChose, coinReceivedList, isConfirmed);
        DispenseDrinkVO expectedResponse = new DispenseDrinkVO(expectedMessage, changeCoinList, totalChangeAmount, productDispensed);

        webTestClient.post()
                .uri("/v1/dispense-drink")
                .contentType(APPLICATION_JSON)
                .body(Mono.just(requestDto), DispenseDrinkDTO.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(DispenseDrinkVO.class)
                .value(response -> {
                    assertEquals(expectedResponse.getMessage(), response.getMessage());
                    assertEquals(expectedResponse.getProductsDispensed(), response.getProductsDispensed());
                    assertEquals(expectedResponse.getTotalDispensedCoinsValue(), response.getTotalDispensedCoinsValue());
                    assertEquals(expectedResponse.getCoinDispensationList(), response.getCoinDispensationList());
                });

    }

    static Stream<Arguments> provideDispenseDrinkTestArguments() {
        return Stream.of(
                Arguments.of(1L, ProductsOption.WATER, List.of(CoinType.ONE_EURO), true,
                        SUCCEED_DISPENSE_PRODUCT, List.of(new CoinsVO(CoinType.FIFTY_CENTS, 1)),
                        new BigDecimal("0.50"), ProductsOption.WATER),
                Arguments.of(1L, ProductsOption.ORANGE_JUICE, List.of(CoinType.TWO_EURO), true,
                        SUCCEED_DISPENSE_PRODUCT, List.of(new CoinsVO(CoinType.FIVE_CENTS, 1)),
                        new BigDecimal("0.05"), ProductsOption.ORANGE_JUICE),
                Arguments.of(1L, ProductsOption.ORANGE_JUICE, List.of(CoinType.TWO_EURO), true,
                        PRODUCT_NO_STOCK_OR_EXPIRED.concat(OPERATION_CANCELLED),
                        List.of(new CoinsVO(CoinType.TWO_EURO, 1)),
                        new BigDecimal("2.00"), null),
                Arguments.of(1L, ProductsOption.WATER, List.of(CoinType.ONE_EURO), true,
                        SUCCEED_DISPENSE_PRODUCT, List.of(new CoinsVO(CoinType.FIFTY_CENTS, 1)),
                        new BigDecimal("0.50"), ProductsOption.WATER),
                Arguments.of(1L, ProductsOption.WATER, List.of(CoinType.ONE_EURO), true,
                        SUCCEED_DISPENSE_PRODUCT, List.of(new CoinsVO(CoinType.FIFTY_CENTS, 1)),
                        new BigDecimal("0.50"), ProductsOption.WATER),
                Arguments.of(1L, ProductsOption.REDBULL,
                        List.of(CoinType.ONE_EURO, CoinType.ONE_EURO, CoinType.ONE_EURO), true,
                        SUCCEED_DISPENSE_PRODUCT,
                        List.of(new CoinsVO(CoinType.FIFTY_CENTS, 1),
                                new CoinsVO(CoinType.TWENTY_CENTS, 1),
                                new CoinsVO(CoinType.FIVE_CENTS, 1)),
                        new BigDecimal("0.75"), ProductsOption.REDBULL),
                Arguments.of(1L, ProductsOption.REDBULL,
                        List.of(CoinType.ONE_EURO, CoinType.ONE_EURO, CoinType.ONE_EURO), true,
                        SUCCEED_DISPENSE_PRODUCT,
                        List.of(new CoinsVO(CoinType.FIFTY_CENTS, 1),
                                new CoinsVO(CoinType.TWENTY_CENTS, 1),
                                new CoinsVO(CoinType.FIVE_CENTS, 1)),
                        new BigDecimal("0.75"), ProductsOption.REDBULL),
                Arguments.of(1L, ProductsOption.REDBULL,
                        List.of(CoinType.TWO_EURO, CoinType.ONE_EURO), true,
                        SUCCEED_DISPENSE_PRODUCT,
                        List.of(new CoinsVO(CoinType.TWENTY_CENTS, 3),
                                new CoinsVO(CoinType.TEN_CENTS, 1),
                                new CoinsVO(CoinType.FIVE_CENTS, 1)),
                        new BigDecimal("0.75"), ProductsOption.REDBULL),
                Arguments.of(1L, ProductsOption.WATER, List.of(CoinType.ONE_EURO), true,
                        INSUFFICIENT_COINS_FOR_CHANGE.concat(OPERATION_CANCELLED), List.of(new CoinsVO(CoinType.ONE_EURO, 1)),
                        new BigDecimal("1.00"), null),
                Arguments.of(1L, ProductsOption.WATER, List.of(CoinType.ONE_EURO), false,
                        OPERATION_REJECTED_BY_USER.concat(OPERATION_CANCELLED), List.of(new CoinsVO(CoinType.ONE_EURO, 1)),
                        new BigDecimal("1.00"), null)
        );
    }

}