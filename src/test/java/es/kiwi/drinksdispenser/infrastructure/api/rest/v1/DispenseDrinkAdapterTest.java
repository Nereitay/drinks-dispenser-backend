package es.kiwi.drinksdispenser.infrastructure.api.rest.v1;

import es.kiwi.drinksdispenser.application.dto.DispenseDrinkDTO;
import es.kiwi.drinksdispenser.application.usecase.DispenseDrinkUseCase;
import es.kiwi.drinksdispenser.application.vo.CoinsVO;
import es.kiwi.drinksdispenser.application.vo.DispenseDrinkVO;
import es.kiwi.drinksdispenser.domain.model.CoinType;
import es.kiwi.drinksdispenser.domain.model.ProductsOption;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = DispenseDrinkAdapter.class)
class DispenseDrinkAdapterTest {
    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private DispenseDrinkUseCase dispenseDrinkUseCase;

    @Test
    public void dispenseDrink_Success() {
        DispenseDrinkDTO requestDto = new DispenseDrinkDTO(1L, ProductsOption.WATER, List.of(CoinType.ONE_EURO), true);
        DispenseDrinkVO expectedResponse = new DispenseDrinkVO("SUCCEED - Product dispensed with changes",
                List.of(new CoinsVO(CoinType.FIFTY_CENTS, 1)), new BigDecimal("0.5"), ProductsOption.WATER);
        when(dispenseDrinkUseCase.execute(any(DispenseDrinkDTO.class)))
                .thenReturn(Mono.just(expectedResponse));

        EntityExchangeResult<DispenseDrinkVO> result = webTestClient.post()
                .uri("/v1/dispense-drink")
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(DispenseDrinkVO.class)
                .returnResult();

        DispenseDrinkVO actualResponse = result.getResponseBody();
        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.getMessage()).isEqualTo(expectedResponse.getMessage());
        assertThat(actualResponse.getProductsDispensed()).isEqualTo(expectedResponse.getProductsDispensed());
        assertThat(actualResponse.getTotalDispensedCoinsValue()).isEqualTo(expectedResponse.getTotalDispensedCoinsValue());
        assertThat(actualResponse.getCoinDispensationList()).isEqualTo(expectedResponse.getCoinDispensationList());

    }


}