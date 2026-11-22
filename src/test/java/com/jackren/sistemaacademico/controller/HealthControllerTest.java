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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = HealthController.class)
@Import({
        SecurityConfig.class,
        JwtConfig.class,
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
class HealthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @Test
    @DisplayName("GET /api/health debe ser público y retornar 200 con status UP")
    void getHealthDebeSerPublicoYRetornarUp() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.service").value("sistema-academico-backend"))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}
