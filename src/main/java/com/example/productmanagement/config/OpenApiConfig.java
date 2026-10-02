package com.example.productmanagement.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Supplies the static part of the OpenAPI document that Swagger UI renders.
 *
 * <p>Springdoc derives paths, operations and schemas automatically by introspecting
 * the annotated controllers and DTOs at startup, so nothing here describes
 * endpoints. This bean only sets document-level metadata that cannot be inferred.
 *
 * <p>The paths Swagger UI is served from ({@code /swagger-ui.html}) and the JSON
 * document ({@code /v3/api-docs}) are configured under the {@code springdoc} key in
 * {@code application.yml}; springdoc merges the {@link OpenAPI} bean below with
 * those properties rather than replacing them.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI productManagementOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Product Management API")
                        .description("CRUD API for products, as a Spring Boot learning project")
                        .version("0.0.1"));
    }
}
