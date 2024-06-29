package es.kiwi.drinksdispenser.infrastructure.config;

import es.kiwi.drinksdispenser.application.service.AddProductsService;
import es.kiwi.drinksdispenser.application.service.ProductStockService;
import es.kiwi.drinksdispenser.application.usecase.AddProductsToMachineUseCase;
import es.kiwi.drinksdispenser.infrastructure.persistence.MachineProductsPersistenceAdapter;
import es.kiwi.drinksdispenser.infrastructure.persistence.ProductsPersistenceAdapter;
import es.kiwi.drinksdispenser.infrastructure.persistence.mapper.MachineProductsDAOMapper;
import es.kiwi.drinksdispenser.infrastructure.persistence.repository.MachineProductsDAORepository;
import es.kiwi.drinksdispenser.infrastructure.persistence.repository.MachinesDAORepository;
import es.kiwi.drinksdispenser.infrastructure.persistence.MachinesPersistenceAdapter;
import es.kiwi.drinksdispenser.infrastructure.persistence.repository.ProductsDAORepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public AddProductsService addProductsService(MachineProductsPersistenceAdapter machineProductsPersistenceAdapter,
                                                 MachinesPersistenceAdapter machinesPersistenceAdapter,
                                                 ProductsPersistenceAdapter productsPersistenceAdapter) {
        return new AddProductsService(machineProductsPersistenceAdapter, machinesPersistenceAdapter, productsPersistenceAdapter);
    }

    @Bean
    public MachineProductsPersistenceAdapter machineProductsPersistenceAdapter(MachineProductsDAORepository machineProductsDAORepository,
                                                                               MachineProductsDAOMapper machineProductsDAOMapper,
                                                                               ProductsDAORepository productsDAORepository,
                                                                               MachinesDAORepository machinesDAORepository) {
        return new MachineProductsPersistenceAdapter(machineProductsDAORepository, machineProductsDAOMapper,
                productsDAORepository, machinesDAORepository);
    }

    @Bean
    public ProductStockService productStockService(MachineProductsPersistenceAdapter machineProductsPersistenceAdapter) {
        return new ProductStockService(machineProductsPersistenceAdapter);
    }
}
