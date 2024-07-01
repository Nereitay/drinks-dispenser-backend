package es.kiwi.drinksdispenser.integration.consumer.manager;


import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
@Slf4j
public class ZeroStockConsumer {

    @Bean
    public Consumer<String> zeroStockNotifierChannel() {
        return msg -> log.info("General zero stock channel: {}", msg);
    }

    @Bean
    public Consumer<String> zeroStockNotifierChannelProduct4() {
        return msg -> log.info("Zero stock of product 4 (ORANGE_JUICE): {}", msg);
    }

    @Bean
    public Consumer<String> zeroStockNotifierChannelMachine1() {
        return msg -> log.info("Zero stock of machine 1: {}", msg);
    }
}
