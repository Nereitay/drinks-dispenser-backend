package es.kiwi.drinksdispenser.integration.consumer.lcd;


import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
@Slf4j
public class LcdConsumer {

    @Bean
    public Consumer<String> lcdNotifierChannel() {
        return log::info;
    }
}
