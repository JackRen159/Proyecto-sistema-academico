package com.jackren.sistemaacademico.controller;

import com.jackren.sistemaacademico.config.JwtConfig;
import com.jackren.sistemaacademico.config.SecurityConfig;
import com.jackren.sistemaacademico.dto.*;
import com.jackren.sistemaacademico.entity.*;
import com.jackren.sistemaacademico.repository.UsuarioRepository;
import com.jackren.sistemaacademico.security.*;
import com.jackren.sistemaacademico.service.HorarioService;
import com.jackren.sistemaacademico.service.NovedadHorarioService;
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

import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = HorarioController.class)
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
@DisplayName("Pruebas de Seguridad RBAC - Controlador de Horarios Escolares")
class HorarioSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HorarioService horarioService;

    @MockitoBean
    private NovedadHorarioService novedadHorarioService;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @Test
    @DisplayName("GET /api/horarios/aulas sin autenticación debe retornar 401 Unauthorized")
    void getAulas_SinAutenticacion_DebeRetornar401() throws Exception {
        mockMvc.perform(get("/api/horarios/aulas"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @DisplayName("POST /api/horarios/aulas con rol PROFESOR debe retornar 403 Forbidden")
    void postAula_ConRolProfesor_DebeRetornar403() throws Exception {
        mockMvc.perform(post("/api/horarios/aulas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sede\":\"SEDE_PRINCIPAL\",\"codigo\":\"201\",\"nombre\":\"Salón 201\",\"tipoEspacio\":\"AULA_ESTANDAR\",\"capacidadMaxima\":35}")
                        .with(jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_PROFESOR"))
                                .jwt(b -> b.subject("profesor1").claim("rol", "PROFESOR"))))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    @DisplayName("POST /api/horarios/aulas con rol COORDINADOR debe permitir creación (201 Created)")
    void postAula_ConRolCoordinador_DebeRetornar201() throws Exception {
        AulaResponse resp = new AulaResponse();
        resp.setId(1L);
        resp.setCodigo("201");
        resp.setNombre("Salón 201");
        resp.setCapacidadMaxima(35);
        when(horarioService.crearAula(any())).thenReturn(resp);

        mockMvc.perform(post("/api/horarios/aulas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sede\":\"SEDE_PRINCIPAL\",\"codigo\":\"201\",\"nombre\":\"Salón 201\",\"tipoEspacio\":\"AULA_ESTANDAR\",\"capacidadMaxima\":35}")
                        .with(jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_COORDINADOR"))
                                .jwt(b -> b.subject("coordinador1").claim("rol", "COORDINADOR"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.codigo").value("201"));
    }

    @Test
    @DisplayName("POST /api/horarios/franjas con rol RECTOR debe permitir creación (201 Created)")
    void postFranja_ConRolRector_DebeRetornar201() throws Exception {
        PlantillaFranjaHorariaResponse resp = new PlantillaFranjaHorariaResponse();
        resp.setId(10L);
        resp.setNumeroBloque(1);
        resp.setHoraInicio(LocalTime.of(7, 0));
        resp.setHoraFin(LocalTime.of(7, 55));
        when(horarioService.crearFranja(any())).thenReturn(resp);

        mockMvc.perform(post("/api/horarios/franjas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"anioLectivoId\":1,\"jornada\":\"MANANA\",\"numeroBloque\":1,\"horaInicio\":\"07:00:00\",\"horaFin\":\"07:55:00\"}")
                        .with(jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_RECTOR"))
                                .jwt(b -> b.subject("rector1").claim("rol", "RECTOR"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.numeroBloque").value(1));
    }

    @Test
    @DisplayName("GET /api/horarios/mis-horarios con rol ESTUDIANTE debe retornar 200 OK")
    void getMisHorarios_ConRolEstudiante_DebeRetornar200() throws Exception {
        ConsultaHorarioEstudianteResponse resp = new ConsultaHorarioEstudianteResponse();
        resp.setNumeroMatricula("MAT-2026-0001");
        resp.setGrupoNombre("10-A");
        when(horarioService.consultarHorarioEstudiante(any(), any())).thenReturn(resp);

        mockMvc.perform(get("/api/horarios/mis-horarios")
                        .with(jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_ESTUDIANTE"))
                                .jwt(b -> b.subject("estudiante1").claim("rol", "ESTUDIANTE").claim("id", 15L))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numeroMatricula").value("MAT-2026-0001"));
    }

    @Test
    @DisplayName("GET /api/horarios/docente/mi-agenda con rol PROFESOR debe retornar 200 OK")
    void getAgendaDocente_ConRolProfesor_DebeRetornar200() throws Exception {
        AgendaDocenteResponse resp = new AgendaDocenteResponse();
        resp.setProfesorNombreCompleto("Marie Curie");
        resp.setTotalBloquesSemanales(22L);
        when(horarioService.consultarAgendaDocente(any())).thenReturn(resp);

        mockMvc.perform(get("/api/horarios/docente/mi-agenda")
                        .with(jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_PROFESOR"))
                                .jwt(b -> b.subject("profesor1").claim("rol", "PROFESOR").claim("id", 20L))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.profesorNombreCompleto").value("Marie Curie"))
                .andExpect(jsonPath("$.totalBloquesSemanales").value(22));
    }
}
