package com.genovia.consultation.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;


@Configuration
public class OpenApiConfiguration {

    @Bean
    public OpenAPI consultationServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Genovia Consultation Service")
                        .version("1.0.0"))
                .servers(List.of(
                        new Server().url("http://localhost:8080")
                ));
    }
}
