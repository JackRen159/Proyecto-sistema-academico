package com.jackren.sistemaacademico.controller;

import com.jackren.sistemaacademico.config.JwtConfig;
import com.jackren.sistemaacademico.config.SecurityConfig;
import com.jackren.sistemaacademico.dto.AnioLectivoRequest;
import com.jackren.sistemaacademico.dto.AnioLectivoResponse;
import com.jackren.sistemaacademico.entity.EstadoAnioLectivo;
import com.jackren.sistemaacademico.repository.UsuarioRepository;
import com.jackren.sistemaacademico.security.CustomUserDetailsService;
import com.jackren.sistemaacademico.security.LoginRateLimiterService;
import com.jackren.sistemaacademico.security.RestAccessDeniedHandler;
import com.jackren.sistemaacademico.security.RestAuthenticationEntryPoint;
import com.jackren.sistemaacademico.security.TokenRevocationService;
import com.jackren.sistemaacademico.service.*;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {
        GradoController.class,
        AreaController.class,
        AsignaturaController.class,
        AnioLectivoController.class,
        PeriodoAcademicoController.class,
        GrupoController.class
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
@DisplayName("Pruebas de Seguridad y Control de Acceso (RBAC) - Estructura Académica")
class EstructuraAcademicaSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GradoService gradoService;

    @MockitoBean
    private GradoAsignaturaService gradoAsignaturaService;

    @MockitoBean
    private AreaService areaService;

    @MockitoBean
    private AsignaturaService asignaturaService;

    @MockitoBean
    private AnioLectivoService anioLectivoService;

    @MockitoBean
    private PeriodoAcademicoService periodoAcademicoService;

    @MockitoBean
    private DiaNoLectivoService diaNoLectivoService;

    @MockitoBean
    private GrupoService grupoService;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @Test
    @DisplayName("GET /api/grados sin autenticación debe retornar 401 Unauthorized")
    void getGrados_SinAutenticacion_DebeRetornar401() throws Exception {
        mockMvc.perform(get("/api/grados"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @DisplayName("GET /api/grados con rol PROFESOR debe permitir lectura (200 OK)")
    void getGrados_ConRolProfesor_DebeRetornar200() throws Exception {
        when(gradoService.listarActivos()).thenReturn(List.of());

        mockMvc.perform(get("/api/grados")
                        .with(jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_PROFESOR"))
                                .jwt(b -> b.subject("profesor1").claim("rol", "PROFESOR"))))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/anios-lectivos con rol PROFESOR debe retornar 403 Forbidden")
    void postAnioLectivo_ConRolProfesor_DebeRetornar403() throws Exception {
        mockMvc.perform(post("/api/anios-lectivos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"anio\":2026,\"nombre\":\"Año 2026\",\"fechaInicio\":\"2026-01-15\",\"fechaFin\":\"2026-11-30\"}")
                        .with(jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_PROFESOR"))
                                .jwt(b -> b.subject("profesor1").claim("rol", "PROFESOR"))))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /api/anios-lectivos con rol ADMINISTRADOR debe permitir creación (201 Created)")
    void postAnioLectivo_ConRolAdministrador_DebeRetornar201() throws Exception {
        AnioLectivoResponse response = new AnioLectivoResponse(
                1L, 2026, "Año 2026", LocalDate.of(2026, 1, 15), LocalDate.of(2026, 11, 30),
                EstadoAnioLectivo.PLANIFICACION, LocalDateTime.now(), LocalDateTime.now()
        );
        when(anioLectivoService.guardar(any(AnioLectivoRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/anios-lectivos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"anio\":2026,\"nombre\":\"Año 2026\",\"fechaInicio\":\"2026-01-15\",\"fechaFin\":\"2026-11-30\"}")
                        .with(jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_ADMINISTRADOR"))
                                .jwt(b -> b.subject("admin").claim("rol", "ADMINISTRADOR"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.anio").value(2026))
                .andExpect(jsonPath("$.estado").value("PLANIFICACION"));
    }

    @Test
    @DisplayName("PATCH /api/anios-lectivos/1/activar con rol RECTOR debe permitir activación (200 OK)")
    void activarAnioLectivo_ConRolRector_DebeRetornar200() throws Exception {
        AnioLectivoResponse response = new AnioLectivoResponse(
                1L, 2026, "Año 2026", LocalDate.of(2026, 1, 15), LocalDate.of(2026, 11, 30),
                EstadoAnioLectivo.ACTIVO, LocalDateTime.now(), LocalDateTime.now()
        );
        when(anioLectivoService.activar(1L)).thenReturn(response);

        mockMvc.perform(patch("/api/anios-lectivos/1/activar")
                        .with(jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_RECTOR"))
                                .jwt(b -> b.subject("rector").claim("rol", "RECTOR"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("ACTIVO"));
    }
}
