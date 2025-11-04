package org.example.taskmanager03.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Task Manager API",
                version = "v1",
                description = "REST API for managing users and tasks with JWT authentication.",
                contact = @io.swagger.v3.oas.annotations.info.Contact(name = "Team", email = "support@example.org")
        ),
        servers = {
                @Server(url = "http://localhost:9090", description = "Local")
        }
)
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";
        return new OpenAPI()
                .components(new Components().addSecuritySchemes(securitySchemeName,
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                ))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .info(new io.swagger.v3.oas.models.info.Info()
                        .title("Task Manager API")
                        .version("v1")
                        .description("REST API for managing users and tasks with JWT authentication.")
                        .license(new License().name("MIT"))
                );
    }
}
