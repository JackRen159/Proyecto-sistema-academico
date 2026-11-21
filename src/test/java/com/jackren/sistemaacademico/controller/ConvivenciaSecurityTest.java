package com.jackren.sistemaacademico.controller;

import com.jackren.sistemaacademico.config.JwtConfig;
import com.jackren.sistemaacademico.config.SecurityConfig;
import com.jackren.sistemaacademico.dto.*;
import com.jackren.sistemaacademico.entity.*;
import com.jackren.sistemaacademico.repository.UsuarioRepository;
import com.jackren.sistemaacademico.security.*;
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
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ConvivenciaController.class)
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
@DisplayName("Pruebas de Seguridad RBAC - Controlador de Convivencia Escolar V2")
class ConvivenciaSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ObservadorService observadorService;

    @MockitoBean
    private RutaAtencionService rutaAtencionService;

    @MockitoBean
    private DebidoProcesoService debidoProcesoService;

    @MockitoBean
    private MedidaSancionService medidaSancionService;

    @MockitoBean
    private ComiteConvivenciaService comiteConvivenciaService;

    @MockitoBean
    private ConvivenciaConsultaService convivenciaConsultaService;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @Test
    @DisplayName("Petición anónima a observador debe retornar 401 Unauthorized")
    void observador_SinAutenticacion_Retorna401() throws Exception {
        mockMvc.perform(get("/api/convivencia/observador/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Profesor autenticado puede registrar en observador")
    void registrarAnotacion_RolProfesor_Permitido() throws Exception {
        AnotacionObservadorResponse response = new AnotacionObservadorResponse(
                1L, 10L, "Juan Pérez", 1L, 5L, "docente1",
                TipoAnotacionObservador.SEGUIMIENTO_FORMATIVO,
                LocalDate.now(), LocalTime.now(), "Aula 101", "Descripción",
                "Acciones", false, false, null, null, null, false, null, true,
                LocalDateTime.now(), Collections.emptyList()
        );

        when(observadorService.registrarAnotacion(any(), any())).thenReturn(response);

        mockMvc.perform(post("/api/convivencia/observador")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_PROFESOR")).jwt(j -> j.claim("id", 5L)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "estudianteId": 10,
                            "tipoAnotacion": "SEGUIMIENTO_FORMATIVO",
                            "fechaHecho": "2026-05-10",
                            "lugarHecho": "Aula 101",
                            "descripcionFactica": "Descripción válida con más de diez caracteres"
                        }
                        """))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Coordinador de Convivencia puede abrir caso de convivencia (UC-23)")
    void abrirCaso_RolCoordinadorConvivencia_Permitido() throws Exception {
        CasoConvivenciaResponse response = new CasoConvivenciaResponse(
                1L, "EXP-CONV-2026-0001", 1L, 10L, "Juan Pérez",
                TipoSituacionConvivencia.TIPO_I, EstadoCasoConvivencia.EN_PROTOCOLO_ATENCION,
                LocalDate.now(), "Patio", "Conflicto", 2L, "coord1",
                false, false, null, false, null, null, false, null, null, null,
                LocalDateTime.now(), null, null
        );

        when(rutaAtencionService.abrirCaso(any(), any())).thenReturn(response);

        mockMvc.perform(post("/api/convivencia/casos")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_COORDINADOR_CONVIVENCIA")).jwt(j -> j.claim("id", 2L)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "anioLectivoId": 1,
                            "estudianteImplicadoId": 10,
                            "tipoSituacion": "TIPO_I",
                            "fechaHechos": "2026-05-10",
                            "lugarHechos": "Patio",
                            "descripcionSituacion": "Descripción de la situación"
                        }
                        """))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Profesor intenta abrir expediente disciplinario y es rechazado con 403 Forbidden")
    void abrirExpediente_RolProfesor_Retorna403() throws Exception {
        mockMvc.perform(post("/api/convivencia/debido-proceso/apertura")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_PROFESOR")).jwt(j -> j.claim("id", 5L)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "casoConvivenciaId": 1,
                            "estudianteId": 10,
                            "autoAperturaHechos": "Hechos",
                            "normasManualInfringidas": "Art 10",
                            "pruebasTrasladadasAnonimizadas": "Pruebas"
                        }
                        """))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Rector puede registrar acta de Comité Escolar de Convivencia (UC-PROV-26)")
    void registrarActaComite_RolRector_Permitido() throws Exception {
        ActaComiteConvivenciaResponse response = new ActaComiteConvivenciaResponse(
                1L, 1L, "ACT-COMITE-2026-001", LocalDate.now(),
                LocalTime.of(8, 0), LocalTime.of(10, 0), "Sala Juntas",
                TipoSesionComiteConvivencia.ORDINARIA, "[]", null, "[]", "[]",
                null, 2L, "secretario1", 1L, "rector1", LocalDateTime.now()
        );

        when(comiteConvivenciaService.registrarActa(any())).thenReturn(response);

        mockMvc.perform(post("/api/convivencia/comite/actas")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_RECTOR")).jwt(j -> j.claim("id", 1L)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "anioLectivoId": 1,
                            "numeroActa": "ACT-COMITE-2026-001",
                            "fechaSesion": "2026-05-12",
                            "horaInicio": "08:00:00",
                            "lugarSesion": "Sala Juntas",
                            "integrantesAsistentesJson": "[\\"Rector\\"]",
                            "casosAnalizadosJson": "[\\"EXP-1\\"]",
                            "acuerdosCompromisosJson": "[\\"Acuerdo 1\\"]",
                            "usuarioSecretarioId": 2,
                            "usuarioPresidenteId": 1
                        }
                        """))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Acudiente puede consultar seguimiento proyectado de su estudiante (UC-25)")
    void consultarSeguimiento_RolAcudiente_Permitido() throws Exception {
        SeguimientoConvivenciaEstudianteResponse response = new SeguimientoConvivenciaEstudianteResponse(
                10L, "Juan Pérez", Collections.emptyList(), Collections.emptyList(),
                Collections.emptyList(), Collections.emptyList(), Collections.emptyList()
        );

        when(convivenciaConsultaService.consultarSeguimientoEstudiante(eq(10L), any())).thenReturn(response);

        mockMvc.perform(get("/api/convivencia/seguimiento/estudiante/10")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ACUDIENTE")).jwt(j -> j.claim("id", 20L))))
                .andExpect(status().isOk());
    }
}
