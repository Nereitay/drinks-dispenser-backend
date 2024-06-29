package es.kiwi.drinksdispenser.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public GroupedOpenApi v1Api() {
        return GroupedOpenApi.builder()
                .group("V1")
                .pathsToMatch("/v1/**")
                .build();
    }

    @Bean
    public OpenAPI springOpenAPI() {
        return new OpenAPI().info(
                new Info()
                        .title("Drinks Dispenser API")
                        .description("drink dispenser back-end API")
                        .version("v0.0.1"));
    }
}