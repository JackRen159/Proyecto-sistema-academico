package com.jackren.sistemaacademico.controller;

import com.jackren.sistemaacademico.config.JwtConfig;
import com.jackren.sistemaacademico.config.SecurityConfig;
import com.jackren.sistemaacademico.dto.AsignacionAcademicaRequest;
import com.jackren.sistemaacademico.dto.AsignacionAcademicaResponse;
import com.jackren.sistemaacademico.dto.ProfesorRequest;
import com.jackren.sistemaacademico.dto.ProfesorResponse;
import com.jackren.sistemaacademico.entity.TipoDocumento;
import com.jackren.sistemaacademico.repository.UsuarioRepository;
import com.jackren.sistemaacademico.security.CustomUserDetailsService;
import com.jackren.sistemaacademico.security.LoginRateLimiterService;
import com.jackren.sistemaacademico.security.RestAccessDeniedHandler;
import com.jackren.sistemaacademico.security.RestAuthenticationEntryPoint;
import com.jackren.sistemaacademico.security.TokenRevocationService;
import com.jackren.sistemaacademico.service.AsignacionAcademicaService;
import com.jackren.sistemaacademico.service.ProfesorService;
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

@WebMvcTest(controllers = {
        ProfesorController.class,
        AsignacionAcademicaController.class
})
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
@DisplayName("Pruebas de Seguridad RBAC - Profesores y Asignaciones Académicas")
class ProfesorSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProfesorService profesorService;

    @MockitoBean
    private AsignacionAcademicaService asignacionService;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @Test
    @DisplayName("GET /api/profesores sin autenticación debe retornar 401 Unauthorized")
    void getProfesores_SinAutenticacion_DebeRetornar401() throws Exception {
        mockMvc.perform(get("/api/profesores"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @DisplayName("GET /api/profesores con rol PROFESOR debe permitir lectura (200 OK)")
    void getProfesores_ConRolProfesor_DebeRetornar200() throws Exception {
        when(profesorService.listarActivos()).thenReturn(List.of());

        mockMvc.perform(get("/api/profesores")
                        .with(jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_PROFESOR"))
                                .jwt(b -> b.subject("profesor1").claim("rol", "PROFESOR"))))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/profesores con rol ESTUDIANTE debe retornar 403 Forbidden")
    void postProfesor_ConRolEstudiante_DebeRetornar403() throws Exception {
        mockMvc.perform(post("/api/profesores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"documento\":\"112233\",\"nombres\":\"Pedro\",\"apellidos\":\"Ríos\",\"correo\":\"pedro@colegio.edu.co\"}")
                        .with(jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_ESTUDIANTE"))
                                .jwt(b -> b.subject("estudiante1").claim("rol", "ESTUDIANTE"))))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /api/profesores con rol ADMINISTRADOR debe permitir creación (201 Created)")
    void postProfesor_ConRolAdmin_DebeRetornar201() throws Exception {
        ProfesorResponse response = new ProfesorResponse();
        response.setId(1L);
        response.setDocumento("112233");
        response.setNombreCompleto("Pedro Ríos");
        response.setTipoDocumento(TipoDocumento.CC);
        when(profesorService.crear(any(ProfesorRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/profesores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"documento\":\"112233\",\"nombres\":\"Pedro\",\"apellidos\":\"Ríos\",\"correo\":\"pedro@colegio.edu.co\"}")
                        .with(jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_ADMINISTRADOR"))
                                .jwt(b -> b.subject("admin").claim("rol", "ADMINISTRADOR"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombreCompleto").value("Pedro Ríos"));
    }

    @Test
    @DisplayName("POST /api/asignaciones-academicas con rol COORDINADOR debe permitir creación (201 Created)")
    void postAsignacion_ConRolCoordinador_DebeRetornar201() throws Exception {
        AsignacionAcademicaResponse response = new AsignacionAcademicaResponse();
        response.setId(10L);
        response.setProfesorId(1L);
        response.setGrupoId(2L);
        response.setAsignaturaId(3L);
        response.setIntensidadHorariaSemanal(4);
        when(asignacionService.crear(any(AsignacionAcademicaRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/asignaciones-academicas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"profesorId\":1,\"grupoId\":2,\"asignaturaId\":3,\"intensidadHorariaSemanal\":4}")
                        .with(jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_COORDINADOR"))
                                .jwt(b -> b.subject("coord").claim("rol", "COORDINADOR"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.intensidadHorariaSemanal").value(4));
    }
}
