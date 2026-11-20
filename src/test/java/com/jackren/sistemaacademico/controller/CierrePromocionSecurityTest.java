package com.jackren.sistemaacademico.controller;

import com.jackren.sistemaacademico.config.JwtConfig;
import com.jackren.sistemaacademico.config.SecurityConfig;
import com.jackren.sistemaacademico.dto.*;
import com.jackren.sistemaacademico.entity.*;
import com.jackren.sistemaacademico.repository.UsuarioRepository;
import com.jackren.sistemaacademico.security.*;
import com.jackren.sistemaacademico.service.CierrePeriodoService;
import com.jackren.sistemaacademico.service.ComisionPeriodicaService;
import com.jackren.sistemaacademico.service.GraduacionMediaService;
import com.jackren.sistemaacademico.service.PromocionAnualService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CierrePromocionController.class)
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
@DisplayName("Pruebas de Seguridad RBAC - Controlador de Cierre Académico y Promoción")
class CierrePromocionSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CierrePeriodoService cierrePeriodoService;

    @MockitoBean
    private ComisionPeriodicaService comisionPeriodicaService;

    @MockitoBean
    private PromocionAnualService promocionAnualService;

    @MockitoBean
    private GraduacionMediaService graduacionMediaService;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @Test
    @DisplayName("GET /api/cierre-promocion/periodos/1/diagnostico sin autenticación debe retornar 401 Unauthorized")
    void getDiagnostico_SinAutenticacion_DebeRetornar401() throws Exception {
        mockMvc.perform(get("/api/cierre-promocion/periodos/1/diagnostico"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @DisplayName("POST /api/cierre-promocion/periodos/cerrar con rol PROFESOR debe retornar 403 Forbidden")
    void postCierrePeriodo_ConRolProfesor_DebeRetornar403() throws Exception {
        mockMvc.perform(post("/api/cierre-promocion/periodos/cerrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"periodoId\":1,\"motivoCierre\":\"Cierre ordinario\"}")
                        .with(jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_PROFESOR"))
                                .jwt(b -> b.subject("profesor1").claim("rol", "PROFESOR"))))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    @DisplayName("POST /api/cierre-promocion/periodos/cerrar con rol COORDINADOR debe permitir ejecución (200 OK)")
    void postCierrePeriodo_ConRolCoordinador_DebeRetornar200() throws Exception {
        BoletinPublicadoResponse resp = new BoletinPublicadoResponse();
        resp.setId(10L);
        resp.setNumeroMatricula("MAT-2026-0001");
        when(cierrePeriodoService.cerrarPeriodo(any(), any())).thenReturn(List.of(resp));

        mockMvc.perform(post("/api/cierre-promocion/periodos/cerrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"periodoId\":1,\"motivoCierre\":\"Cierre ordinario\"}")
                        .with(jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_COORDINADOR"))
                                .jwt(b -> b.subject("coordinador1").claim("rol", "COORDINADOR").claim("id", 15L))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].numeroMatricula").value("MAT-2026-0001"));
    }

    @Test
    @DisplayName("POST /api/cierre-promocion/promocion/anticipada con rol RECTOR debe permitir ejecución (200 OK)")
    void postPromocionAnticipada_ConRolRector_DebeRetornar200() throws Exception {
        PromocionEstudianteResponse resp = new PromocionEstudianteResponse();
        resp.setId(1L);
        resp.setEstadoPromocion(EstadoPromocionEstudiante.PROMOCION_ANTICIPADA_APROBADA);
        when(promocionAnualService.tramitarPromocionAnticipada(any(), any())).thenReturn(resp);

        mockMvc.perform(post("/api/cierre-promocion/promocion/anticipada")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"matriculaId\":100,\"actaConsejoAcademico\":\"ACT-01\",\"actaConsejoDirectivo\":\"ACT-02\",\"consentimientoAcudienteUrl\":\"https://colegio.edu.co/cons.pdf\",\"gradoDestinoId\":6}")
                        .with(jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_RECTOR"))
                                .jwt(b -> b.subject("rector1").claim("rol", "RECTOR").claim("id", 1L))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estadoPromocion").value("PROMOCION_ANTICIPADA_APROBADA"));
    }
}
