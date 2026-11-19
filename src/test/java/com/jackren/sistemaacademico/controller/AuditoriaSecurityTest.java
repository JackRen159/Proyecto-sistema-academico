package com.jackren.sistemaacademico.controller;

import com.jackren.sistemaacademico.config.JwtConfig;
import com.jackren.sistemaacademico.config.SecurityConfig;
import com.jackren.sistemaacademico.dto.VerificacionIntegridadResponse;
import com.jackren.sistemaacademico.repository.UsuarioRepository;
import com.jackren.sistemaacademico.security.*;
import com.jackren.sistemaacademico.service.AuditoriaGlobalService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuditoriaGlobalController.class)
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
@DisplayName("Pruebas de Seguridad RBAC - Controlador de Auditoría Global (UC-27, UC-PROV-29)")
class AuditoriaSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuditoriaGlobalService auditoriaService;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @Test
    @DisplayName("GET /api/auditoria/verificar-integridad sin autenticación debe retornar 401 Unauthorized")
    void verificarIntegridad_SinAutenticacion_DebeRetornar401() throws Exception {
        mockMvc.perform(get("/api/auditoria/verificar-integridad"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @DisplayName("GET /api/auditoria/verificar-integridad con rol PROFESOR debe retornar 403 Forbidden (E1)")
    void verificarIntegridad_RolProfesor_DebeRetornar403() throws Exception {
        mockMvc.perform(get("/api/auditoria/verificar-integridad")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_PROFESOR"))
                                .jwt(b -> b.subject("profesor1").claim("rol", "PROFESOR"))))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    @DisplayName("GET /api/auditoria/verificar-integridad con rol ADMINISTRADOR debe retornar 200 OK")
    void verificarIntegridad_RolAdmin_DebeRetornar200() throws Exception {
        when(auditoriaService.verificarIntegridadCadena()).thenReturn(VerificacionIntegridadResponse.integra(50));

        mockMvc.perform(get("/api/auditoria/verificar-integridad")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMINISTRADOR"))
                                .jwt(b -> b.subject("admin").claim("rol", "ADMINISTRADOR"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("CADENA_INTEGRA_VERIFICADA"))
                .andExpect(jsonPath("$.totalEventosVerificados").value(50));
    }
}
