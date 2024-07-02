package es.kiwi.drinksdispenser.infrastructure.api.rest.v1;

import es.kiwi.drinksdispenser.application.dto.DispenseDrinkDTO;
import es.kiwi.drinksdispenser.application.usecase.DispenseDrinkUseCase;
import es.kiwi.drinksdispenser.application.vo.DispenseDrinkVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1/dispense-drink")
@RequiredArgsConstructor
public class DispenseDrinkRestAdapter {

    private final DispenseDrinkUseCase dispenseDrinkUseCase;

    @PostMapping
    @Tag(name = "/dispense-drink")
    @Operation(summary = "This method is used to dispense drink.")
    public Mono<DispenseDrinkVO> dispenseDrink(@RequestBody DispenseDrinkDTO dispenseDrinkDTO) {
        return dispenseDrinkUseCase.execute(dispenseDrinkDTO);
    }
}
