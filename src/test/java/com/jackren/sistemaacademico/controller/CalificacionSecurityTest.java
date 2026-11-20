package com.jackren.sistemaacademico.controller;

import com.jackren.sistemaacademico.config.JwtConfig;
import com.jackren.sistemaacademico.config.SecurityConfig;
import com.jackren.sistemaacademico.dto.*;
import com.jackren.sistemaacademico.repository.UsuarioRepository;
import com.jackren.sistemaacademico.security.*;
import com.jackren.sistemaacademico.service.CalificacionService;
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

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CalificacionController.class)
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
@DisplayName("Pruebas de Seguridad RBAC - Controlador de Calificaciones y SIEE")
class CalificacionSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CalificacionService calificacionService;

    @MockitoBean
    private com.jackren.sistemaacademico.service.NivelacionCalificacionService nivelacionService;

    @MockitoBean
    private com.jackren.sistemaacademico.service.ReclamacionCalificacionService reclamacionService;

    @MockitoBean
    private com.jackren.sistemaacademico.service.ConvalidacionCalificacionService convalidacionService;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @Test
    @DisplayName("GET /api/calificaciones/actividades sin autenticación debe retornar 401 Unauthorized")
    void getActividades_SinAutenticacion_DebeRetornar401() throws Exception {
        mockMvc.perform(get("/api/calificaciones/actividades?asignacionId=1&periodoId=1"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @DisplayName("POST /api/calificaciones/escala con rol PROFESOR debe retornar 403 Forbidden")
    void postEscala_ConRolProfesor_DebeRetornar403() throws Exception {
        mockMvc.perform(post("/api/calificaciones/escala")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"anioLectivoId\":1,\"notaMinima\":1.00,\"notaMaxima\":5.00,\"notaAprobatoria\":3.00,\"escalaBasicoMin\":3.00,\"escalaAltoMin\":4.00,\"escalaSuperiorMin\":4.60}")
                        .with(jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_PROFESOR"))
                                .jwt(b -> b.subject("profesor1").claim("rol", "PROFESOR"))))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    @DisplayName("POST /api/calificaciones/escala con rol COORDINADOR debe permitir guardar escala (200 OK)")
    void postEscala_ConRolCoordinador_DebeRetornar200() throws Exception {
        ConfiguracionEscalaSieeResponse resp = new ConfiguracionEscalaSieeResponse();
        resp.setId(1L);
        resp.setAnioLectivoId(1L);
        when(calificacionService.guardarOActualizarEscala(any(ConfiguracionEscalaSieeRequest.class))).thenReturn(resp);

        mockMvc.perform(post("/api/calificaciones/escala")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"anioLectivoId\":1,\"notaMinima\":1.00,\"notaMaxima\":5.00,\"notaAprobatoria\":3.00,\"escalaBasicoMin\":3.00,\"escalaAltoMin\":4.00,\"escalaSuperiorMin\":4.60}")
                        .with(jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_COORDINADOR"))
                                .jwt(b -> b.subject("coordinador1").claim("rol", "COORDINADOR"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("POST /api/calificaciones/actividades con rol ESTUDIANTE debe retornar 403 Forbidden")
    void postActividad_ConRolEstudiante_DebeRetornar403() throws Exception {
        mockMvc.perform(post("/api/calificaciones/actividades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"asignacionAcademicaId\":1,\"periodoAcademicoId\":1,\"nombre\":\"Taller\",\"porcentaje\":20.0}")
                        .with(jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_ESTUDIANTE"))
                                .jwt(b -> b.subject("estudiante1").claim("rol", "ESTUDIANTE"))))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /api/calificaciones/actividades con rol PROFESOR debe permitir creación (201 Created)")
    void postActividad_ConRolProfesor_DebeRetornar201() throws Exception {
        ActividadEvaluativaResponse resp = new ActividadEvaluativaResponse();
        resp.setId(10L);
        resp.setNombre("Taller de Gravitación");
        when(calificacionService.crearActividad(any(), any(), any())).thenReturn(resp);

        mockMvc.perform(post("/api/calificaciones/actividades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"asignacionAcademicaId\":1,\"periodoAcademicoId\":1,\"nombre\":\"Taller de Gravitación\",\"porcentaje\":25.0}")
                        .with(jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_PROFESOR"))
                                .jwt(b -> b.subject("profesor1").claim("rol", "PROFESOR"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.nombre").value("Taller de Gravitación"));
    }

    @Test
    @DisplayName("POST /api/calificaciones/actividades/notas/lote con rol PROFESOR debe permitir registro (200 OK)")
    void postNotasLote_ConRolProfesor_DebeRetornar200() throws Exception {
        CalificacionActividadResponse calResp = new CalificacionActividadResponse();
        calResp.setId(100L);
        calResp.setValor(new BigDecimal("4.50"));
        when(calificacionService.registrarCalificacionesLote(any(), any(), any(), any())).thenReturn(List.of(calResp));

        mockMvc.perform(post("/api/calificaciones/actividades/notas/lote")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"actividadId\":10,\"calificaciones\":[{\"matriculaId\":1,\"valor\":4.5}]}")
                        .with(jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_PROFESOR"))
                                .jwt(b -> b.subject("profesor1").claim("rol", "PROFESOR").claim("id", 20L))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(100));
    }

    @Test
    @DisplayName("GET /api/calificaciones/estudiante/1/periodo/1 con rol ESTUDIANTE debe permitir consulta (200 OK)")
    void getCalificacionesEstudiante_ConRolEstudiante_DebeRetornar200() throws Exception {
        when(calificacionService.listarCalificacionesEstudiantePeriodo(1L, 1L)).thenReturn(List.of());

        mockMvc.perform(get("/api/calificaciones/estudiante/1/periodo/1")
                        .with(jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_ESTUDIANTE"))
                                .jwt(b -> b.subject("estudiante1").claim("rol", "ESTUDIANTE"))))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/calificaciones/auditoria/matricula/1 con rol ESTUDIANTE debe retornar 403 Forbidden")
    void getAuditoria_ConRolEstudiante_DebeRetornar403() throws Exception {
        mockMvc.perform(get("/api/calificaciones/auditoria/matricula/1")
                        .with(jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_ESTUDIANTE"))
                                .jwt(b -> b.subject("estudiante1").claim("rol", "ESTUDIANTE"))))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/calificaciones/auditoria/matricula/1 con rol SECRETARIA debe permitir consulta (200 OK)")
    void getAuditoria_ConRolSecretaria_DebeRetornar200() throws Exception {
        when(calificacionService.listarHistorialAuditoriaMatricula(1L)).thenReturn(List.of());

        mockMvc.perform(get("/api/calificaciones/auditoria/matricula/1")
                        .with(jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_SECRETARIA"))
                                .jwt(b -> b.subject("secretaria1").claim("rol", "SECRETARIA"))))
                .andExpect(status().isOk());
    }
}
