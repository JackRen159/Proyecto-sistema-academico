package com.jackren.sistemaacademico.controller;

import com.jackren.sistemaacademico.config.JwtConfig;
import com.jackren.sistemaacademico.config.SecurityConfig;
import com.jackren.sistemaacademico.dto.*;
import com.jackren.sistemaacademico.repository.UsuarioRepository;
import com.jackren.sistemaacademico.security.*;
import com.jackren.sistemaacademico.service.AsistenciaService;
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

@WebMvcTest(controllers = AsistenciaController.class)
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
@DisplayName("Pruebas de Seguridad RBAC - Controlador de Asistencia Escolar")
class AsistenciaSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AsistenciaService asistenciaService;

    @MockitoBean
    private com.jackren.sistemaacademico.service.ExcusaAsistenciaService excusaService;

    @MockitoBean
    private com.jackren.sistemaacademico.service.SalidaAnticipadaService salidaService;

    @MockitoBean
    private com.jackren.sistemaacademico.service.SeguimientoAusentismoService seguimientoService;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @Test
    @DisplayName("GET /api/asistencia/sesiones sin autenticación debe retornar 401 Unauthorized")
    void getSesiones_SinAutenticacion_DebeRetornar401() throws Exception {
        mockMvc.perform(get("/api/asistencia/sesiones?grupoId=1&periodoId=1"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @DisplayName("POST /api/asistencia/alertas/configuracion con rol PROFESOR debe retornar 403 Forbidden")
    void postAlertas_ConRolProfesor_DebeRetornar403() throws Exception {
        mockMvc.perform(post("/api/asistencia/alertas/configuracion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"anioLectivoId\":1,\"porcentajeAlertaAmarilla\":15.00,\"porcentajeAlertaRoja\":25.00}")
                        .with(jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_PROFESOR"))
                                .jwt(b -> b.subject("profesor1").claim("rol", "PROFESOR"))))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    @DisplayName("POST /api/asistencia/alertas/configuracion con rol COORDINADOR debe permitir guardar (200 OK)")
    void postAlertas_ConRolCoordinador_DebeRetornar200() throws Exception {
        ConfiguracionAlertaAsistenciaResponse resp = new ConfiguracionAlertaAsistenciaResponse();
        resp.setId(1L);
        when(asistenciaService.guardarOActualizarConfiguracionAlerta(any())).thenReturn(resp);

        mockMvc.perform(post("/api/asistencia/alertas/configuracion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"anioLectivoId\":1,\"porcentajeAlertaAmarilla\":15.00,\"porcentajeAlertaRoja\":25.00}")
                        .with(jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_COORDINADOR"))
                                .jwt(b -> b.subject("coordinador1").claim("rol", "COORDINADOR"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("POST /api/asistencia/sesiones con rol ESTUDIANTE debe retornar 403 Forbidden")
    void postSesion_ConRolEstudiante_DebeRetornar403() throws Exception {
        mockMvc.perform(post("/api/asistencia/sesiones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"grupoId\":1,\"periodoAcademicoId\":1,\"tipoRegistro\":\"SESION_ASIGNATURA\"}")
                        .with(jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_ESTUDIANTE"))
                                .jwt(b -> b.subject("estudiante1").claim("rol", "ESTUDIANTE"))))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /api/asistencia/sesiones con rol PROFESOR debe permitir creación (201 Created)")
    void postSesion_ConRolProfesor_DebeRetornar201() throws Exception {
        SesionAsistenciaResponse resp = new SesionAsistenciaResponse();
        resp.setId(10L);
        resp.setTema("Álgebra lineal");
        when(asistenciaService.crearSesion(any(), any(), any())).thenReturn(resp);

        mockMvc.perform(post("/api/asistencia/sesiones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"asignacionAcademicaId\":1,\"grupoId\":1,\"periodoAcademicoId\":1,\"tema\":\"Álgebra lineal\"}")
                        .with(jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_PROFESOR"))
                                .jwt(b -> b.subject("profesor1").claim("rol", "PROFESOR").claim("id", 20L))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.tema").value("Álgebra lineal"));
    }

    @Test
    @DisplayName("POST /api/asistencia/sesiones/planilla con rol PROFESOR debe permitir registrar planilla (200 OK)")
    void postPlanilla_ConRolProfesor_DebeRetornar200() throws Exception {
        when(asistenciaService.registrarPlanillaLote(any(), any(), any(), any())).thenReturn(List.of());

        mockMvc.perform(post("/api/asistencia/sesiones/planilla")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sesionId\":10,\"registros\":[{\"matriculaId\":1,\"estado\":\"PRESENTE\"}]}")
                        .with(jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_PROFESOR"))
                                .jwt(b -> b.subject("profesor1").claim("rol", "PROFESOR"))))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/asistencia/registros/{id}/justificar con rol PROFESOR debe retornar 403 Forbidden")
    void postJustificar_ConRolProfesor_DebeRetornar403() throws Exception {
        mockMvc.perform(post("/api/asistencia/registros/100/justificar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"motivo\":\"Cita médica\"}")
                        .with(jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_PROFESOR"))
                                .jwt(b -> b.subject("profesor1").claim("rol", "PROFESOR"))))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /api/asistencia/registros/{id}/justificar con rol COORDINADOR debe permitir justificación (200 OK)")
    void postJustificar_ConRolCoordinador_DebeRetornar200() throws Exception {
        JustificacionAsistenciaResponse resp = new JustificacionAsistenciaResponse();
        resp.setId(5L);
        resp.setMotivo("Cita médica EPS");
        when(asistenciaService.justificarInasistencia(any(), any(), any(), any())).thenReturn(resp);

        mockMvc.perform(post("/api/asistencia/registros/100/justificar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"motivo\":\"Cita médica EPS\",\"numeroRadicadoSoporte\":\"RAD-001\"}")
                        .with(jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_COORDINADOR"))
                                .jwt(b -> b.subject("coordinador1").claim("rol", "COORDINADOR").claim("id", 3L))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.motivo").value("Cita médica EPS"));
    }

    @Test
    @DisplayName("GET /api/asistencia/estudiante/1/resumen con rol ESTUDIANTE debe permitir consulta (200 OK)")
    void getResumenEstudiante_ConRolEstudiante_DebeRetornar200() throws Exception {
        ResumenAsistenciaEstudianteResponse resp = new ResumenAsistenciaEstudianteResponse();
        resp.setMatriculaId(1L);
        resp.setNivelAlerta("NINGUNA");
        when(asistenciaService.obtenerResumenEstudianteAsignatura(1L, 1L, 1L)).thenReturn(resp);

        mockMvc.perform(get("/api/asistencia/estudiante/1/resumen?periodoId=1&asignacionId=1")
                        .with(jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_ESTUDIANTE"))
                                .jwt(b -> b.subject("estudiante1").claim("rol", "ESTUDIANTE"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nivelAlerta").value("NINGUNA"));
    }

    @Test
    @DisplayName("GET /api/asistencia/grupo/1/alertas con rol PROFESOR debe permitir consulta de alertas (200 OK)")
    void getAlertasGrupo_ConRolProfesor_DebeRetornar200() throws Exception {
        when(asistenciaService.listarAlertasPorGrupoYPeriodo(1L, 1L, 1L)).thenReturn(List.of());

        mockMvc.perform(get("/api/asistencia/grupo/1/alertas?periodoId=1&asignacionId=1")
                        .with(jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_PROFESOR"))
                                .jwt(b -> b.subject("profesor1").claim("rol", "PROFESOR"))))
                .andExpect(status().isOk());
    }
}
