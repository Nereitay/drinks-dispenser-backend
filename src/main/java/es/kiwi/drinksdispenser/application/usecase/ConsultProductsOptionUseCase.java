package es.kiwi.drinksdispenser.application.usecase;

import es.kiwi.drinksdispenser.application.mapper.ProductsOptionVOMapper;
import es.kiwi.drinksdispenser.application.vo.ProductsOptionVO;
import es.kiwi.drinksdispenser.domain.exception.MachineIsNotAvailableException;
import es.kiwi.drinksdispenser.domain.exception.MachineNotFoundException;
import es.kiwi.drinksdispenser.domain.output.MachinesOutput;
import es.kiwi.drinksdispenser.domain.output.ProductsOutput;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ConsultProductsOptionUseCase {

    private final ProductsOutput productsOutput;
    private final MachinesOutput machinesOutput;
    private final ProductsOptionVOMapper productsOptionVOMapper;

    public Flux<ProductsOptionVO> consultProductsOptions(Long machineId) {
        return machinesOutput.findById(machineId)
                .switchIfEmpty(Mono.error(new MachineNotFoundException("Machine with " +
                        "ID " + machineId + " is not found.")))
                .filter(machines -> machines.getStatus().getStatus() == 0)
                .switchIfEmpty(Mono.error(new MachineIsNotAvailableException("Machine with " +
                "ID " + machineId + " is not available.")))
                .flatMapMany(machines -> productsOutput.findAllByMachineId(machineId))
                .map(productsOptionVOMapper::toProductsOptionVO);
    }

}
