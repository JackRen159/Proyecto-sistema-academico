package com.jackren.sistemaacademico.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de OpenAPI 3.x para el Sistema de Gestión Escolar.
 *
 * Define la metadata del contrato REST, el esquema de seguridad basado en JWT Bearer
 * y la integración con Swagger UI.
 */
@Configuration
public class OpenApiConfig {

    public static final String SECURITY_SCHEME_NAME = "BearerAuth";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Sistema de Gestión Escolar API")
                        .version("1.0.0")
                        .description("API REST oficial para la gestión académica, estudiantes, acudientes y control de acceso.")
                        .contact(new Contact()
                                .name("Equipo de Desarrollo Sistema Escolar")
                                .email("soporte@sistemaescolar.local"))
                        .license(new License()
                                .name("Propiedad Institucional")
                                .url("https://sistemaescolar.local/licencia")))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .name(SECURITY_SCHEME_NAME)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Ingrese el token JWT obtenido del endpoint /api/auth/login")));
    }
}
