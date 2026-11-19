package com.jackren.sistemaacademico.controller;

import com.jackren.sistemaacademico.config.JwtConfig;
import com.jackren.sistemaacademico.config.SecurityConfig;
import com.jackren.sistemaacademico.repository.UsuarioRepository;
import com.jackren.sistemaacademico.security.CustomUserDetailsService;
import com.jackren.sistemaacademico.security.LoginRateLimiterService;
import com.jackren.sistemaacademico.security.RestAccessDeniedHandler;
import com.jackren.sistemaacademico.security.RestAuthenticationEntryPoint;
import com.jackren.sistemaacademico.security.TokenRevocationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@Import({
        SecurityConfig.class,
        JwtConfig.class,
        com.jackren.sistemaacademico.config.OpenApiConfig.class,
        RestAuthenticationEntryPoint.class,
        RestAccessDeniedHandler.class,
        CustomUserDetailsService.class,
        TokenRevocationService.class,
        LoginRateLimiterService.class
})
@TestPropertySource(properties = {
        "jwt.secret=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970",
        "jwt.expiration-hours=8"
})
class OpenApiSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private com.jackren.sistemaacademico.config.OpenApiConfig openApiConfig;

    @MockitoBean
    private com.jackren.sistemaacademico.service.AuthService authService;

    @MockitoBean
    private com.jackren.sistemaacademico.service.RecuperacionPasswordService recuperacionPasswordService;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @Test
    @DisplayName("El bean OpenAPI debe configurar la metadata del contrato y esquema BearerAuth")
    void openApiBean_DebeEstarConfiguradoCorrectamente() {
        io.swagger.v3.oas.models.OpenAPI openAPI = openApiConfig.customOpenAPI();
        org.junit.jupiter.api.Assertions.assertNotNull(openAPI);
        org.junit.jupiter.api.Assertions.assertEquals("Sistema de Gestión Escolar API", openAPI.getInfo().getTitle());
        org.junit.jupiter.api.Assertions.assertEquals("1.0.0", openAPI.getInfo().getVersion());
        org.junit.jupiter.api.Assertions.assertTrue(
                openAPI.getComponents().getSecuritySchemes().containsKey("BearerAuth")
        );
    }

    @Test
    @DisplayName("GET /v3/api-docs debe ser accesible sin autenticación y no retornar 401")
    void v3ApiDocs_SinAutenticacion_NoDebeRetornar401() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    if (status == 401) {
                        throw new AssertionError("Se esperaba que /v3/api-docs no retornara 401 Unauthorized, pero retornó 401");
                    }
                });
    }

    @Test
    @DisplayName("GET /swagger-ui/index.html debe ser accesible sin autenticación y no retornar 401")
    void swaggerUi_SinAutenticacion_NoDebeRetornar401() throws Exception {
        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    if (status == 401) {
                        throw new AssertionError("Se esperaba que /swagger-ui/index.html no retornara 401 Unauthorized, pero retornó 401");
                    }
                });
    }
}
