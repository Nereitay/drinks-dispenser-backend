package es.kiwi.drinksdispenser.application.usecase;

import es.kiwi.drinksdispenser.application.dto.DispenseDrinkDTO;
import es.kiwi.drinksdispenser.application.service.DispenseDrinkService;
import es.kiwi.drinksdispenser.application.vo.DispenseDrinkVO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class DispenseDrinkUseCase {
    private final DispenseDrinkService dispenseDrinkService;

    public Mono<DispenseDrinkVO> execute(DispenseDrinkDTO command) {
        return dispenseDrinkService.dispenseDrink(command);
    }
}
