package es.kiwi.drinksdispenser.infrastructure.config;

import es.kiwi.drinksdispenser.application.service.AddProductsService;
import es.kiwi.drinksdispenser.application.service.DispenseDrinkService;
import es.kiwi.drinksdispenser.application.service.ProductStockService;
import es.kiwi.drinksdispenser.domain.output.MachineProductsOutput;
import es.kiwi.drinksdispenser.domain.service.CoinsValidationService;
import es.kiwi.drinksdispenser.infrastructure.persistence.CoinsPersistenceAdapter;
import es.kiwi.drinksdispenser.infrastructure.persistence.MachineProductsPersistenceAdapter;
import es.kiwi.drinksdispenser.infrastructure.persistence.ProductsPersistenceAdapter;
import es.kiwi.drinksdispenser.infrastructure.persistence.mapper.CoinsDAOMapper;
import es.kiwi.drinksdispenser.infrastructure.persistence.mapper.MachineDAOMapper;
import es.kiwi.drinksdispenser.infrastructure.persistence.mapper.MachineProductsDAOMapper;
import es.kiwi.drinksdispenser.infrastructure.persistence.mapper.ProductsDAOMapper;
import es.kiwi.drinksdispenser.infrastructure.persistence.repository.CoinsDAORepository;
import es.kiwi.drinksdispenser.infrastructure.persistence.repository.MachineProductsDAORepository;
import es.kiwi.drinksdispenser.infrastructure.persistence.repository.MachinesDAORepository;
import es.kiwi.drinksdispenser.infrastructure.persistence.MachinesPersistenceAdapter;
import es.kiwi.drinksdispenser.infrastructure.persistence.repository.ProductsDAORepository;
import es.kiwi.drinksdispenser.integration.event.ProductStockZeroEventHandler;
import es.kiwi.drinksdispenser.integration.lcd.LcdNotifier;
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

    @Bean
    public MachinesPersistenceAdapter machinesPersistenceAdapter(MachinesDAORepository machinesDAORepository,
                                                                 MachineDAOMapper machineDAOMapper) {
        return new MachinesPersistenceAdapter(machinesDAORepository, machineDAOMapper);
    }

    @Bean
    public ProductsPersistenceAdapter productsPersistenceAdapter(ProductsDAORepository productsDAORepository,
                                                                 ProductsDAOMapper productsDAOMapper) {
        return new ProductsPersistenceAdapter(productsDAORepository, productsDAOMapper);
    }

    @Bean
    public DispenseDrinkService dispenseDrinkService(CoinsPersistenceAdapter coinsPersistenceAdapter,
                                                     LcdNotifier lcdNotifier,
                                                     CoinsValidationService coinsValidationService,
                                                     MachineProductsOutput machineProductsOutput,
                                                     ProductStockZeroEventHandler productStockZeroEventHandler) {
        return new DispenseDrinkService(coinsPersistenceAdapter, lcdNotifier, coinsValidationService,
                machineProductsOutput, productStockZeroEventHandler);
    }

    @Bean
    public CoinsPersistenceAdapter coinsPersistenceAdapter(CoinsDAORepository coinsDAORepository,
                                                           CoinsDAOMapper coinsDAOMapper) {
        return new CoinsPersistenceAdapter(coinsDAORepository, coinsDAOMapper);
    }

}
