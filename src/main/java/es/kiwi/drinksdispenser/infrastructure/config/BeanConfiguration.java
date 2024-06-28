package es.kiwi.drinksdispenser.infrastructure.config;

import es.kiwi.drinksdispenser.application.mapper.MachineProductsDTOMapper;
import es.kiwi.drinksdispenser.application.usecase.AddProductsToMachineUseCase;
import es.kiwi.drinksdispenser.infrastructure.persistence.MachineProductsPersistenceAdapter;
import es.kiwi.drinksdispenser.infrastructure.persistence.mapper.MachineProductsDAOMapper;
import es.kiwi.drinksdispenser.infrastructure.persistence.repository.MachineProductsDAORepository;
import es.kiwi.drinksdispenser.infrastructure.persistence.repository.ProductsDAORepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public AddProductsToMachineUseCase addProductsToMachineUseCase(MachineProductsPersistenceAdapter machineProductsPersistenceAdapter, MachineProductsDTOMapper machineProductsDTOMapper) {
        return new AddProductsToMachineUseCase(machineProductsPersistenceAdapter, machineProductsDTOMapper);
    }

    @Bean
    public MachineProductsPersistenceAdapter machineProductsPersistenceAdapter(MachineProductsDAORepository machineProductsDAORepository, MachineProductsDAOMapper machineProductsDAOMapper, ProductsDAORepository productsDAORepository) {
        return new MachineProductsPersistenceAdapter(machineProductsDAORepository, machineProductsDAOMapper, productsDAORepository);
    }
}
