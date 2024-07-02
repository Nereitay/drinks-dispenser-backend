package es.kiwi.drinksdispenser.infrastructure.config;

import io.asyncer.r2dbc.mysql.MySqlConnectionConfiguration;
import io.asyncer.r2dbc.mysql.MySqlConnectionFactory;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;

import java.time.Duration;
import java.time.ZoneId;

@Configuration
public class R2dbcConfiguration extends AbstractR2dbcConfiguration {

    @Bean
    @Override
    public ConnectionFactory connectionFactory() {
            return MySqlConnectionFactory.from(
                MySqlConnectionConfiguration.builder()
                        .host("mysql")
                        .port(3306)
                        .username("root")
                        .password("root")
                        .database("dispenser_db")
                        .serverZoneId(ZoneId.of("UTC"))
                        .connectTimeout(Duration.ofSeconds(3))
                        .useServerPrepareStatement()
                        .build()
        );
    }

}
