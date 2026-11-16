package com.jackren.sistemaacademico.controller;

import com.jackren.sistemaacademico.config.JwtConfig;
import com.jackren.sistemaacademico.config.SecurityConfig;
import com.jackren.sistemaacademico.dto.CambioEstadoMatriculaRequest;
import com.jackren.sistemaacademico.dto.MatriculaRequest;
import com.jackren.sistemaacademico.dto.MatriculaResponse;
import com.jackren.sistemaacademico.dto.TrasladoGrupoRequest;
import com.jackren.sistemaacademico.entity.EstadoMatricula;
import com.jackren.sistemaacademico.entity.TipoMatricula;
import com.jackren.sistemaacademico.repository.UsuarioRepository;
import com.jackren.sistemaacademico.security.*;
import com.jackren.sistemaacademico.service.MatriculaService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = MatriculaController.class)
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
@DisplayName("Pruebas de Seguridad RBAC - Controlador de Matrículas")
class MatriculaSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MatriculaService matriculaService;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @Test
    @DisplayName("GET /api/matriculas sin autenticación debe retornar 401 Unauthorized")
    void getMatriculas_SinAutenticacion_DebeRetornar401() throws Exception {
        mockMvc.perform(get("/api/matriculas?grupoId=1"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @DisplayName("GET /api/matriculas con rol PROFESOR debe permitir consulta (200 OK)")
    void getMatriculas_ConRolProfesor_DebeRetornar200() throws Exception {
        when(matriculaService.listarPorGrupo(1L)).thenReturn(List.of());

        mockMvc.perform(get("/api/matriculas?grupoId=1")
                        .with(jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_PROFESOR"))
                                .jwt(b -> b.subject("profesor1").claim("rol", "PROFESOR"))))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/matriculas con rol ESTUDIANTE debe retornar 403 Forbidden")
    void postMatricula_ConRolEstudiante_DebeRetornar403() throws Exception {
        mockMvc.perform(post("/api/matriculas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estudianteId\":1,\"grupoId\":2,\"tipoMatricula\":\"REGULAR\"}")
                        .with(jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_ESTUDIANTE"))
                                .jwt(b -> b.subject("estudiante1").claim("rol", "ESTUDIANTE"))))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    @DisplayName("POST /api/matriculas con rol SECRETARIA debe permitir formalizar matrícula (201 Created)")
    void postMatricula_ConRolSecretaria_DebeRetornar201() throws Exception {
        MatriculaResponse response = new MatriculaResponse();
        response.setId(10L);
        response.setNumeroMatricula("MAT-2026-0001");
        response.setEstado(EstadoMatricula.MATRICULADA);
        when(matriculaService.matricular(any(MatriculaRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/matriculas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estudianteId\":1,\"grupoId\":2,\"tipoMatricula\":\"REGULAR\"}")
                        .with(jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_SECRETARIA"))
                                .jwt(b -> b.subject("secretaria1").claim("rol", "SECRETARIA"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.numeroMatricula").value("MAT-2026-0001"));
    }

    @Test
    @DisplayName("POST /api/matriculas/{id}/traslado con rol PROFESOR debe retornar 403 Forbidden")
    void postTraslado_ConRolProfesor_DebeRetornar403() throws Exception {
        mockMvc.perform(post("/api/matriculas/10/traslado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nuevoGrupoId\":3,\"motivo\":\"Cambio de jornada\"}")
                        .with(jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_PROFESOR"))
                                .jwt(b -> b.subject("profesor1").claim("rol", "PROFESOR"))))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /api/matriculas/{id}/traslado con rol COORDINADOR debe permitir traslado (200 OK)")
    void postTraslado_ConRolCoordinador_DebeRetornar200() throws Exception {
        MatriculaResponse response = new MatriculaResponse();
        response.setId(10L);
        response.setGrupoId(3L);
        when(matriculaService.trasladarDeGrupo(eq(10L), any(TrasladoGrupoRequest.class), any())).thenReturn(response);

        mockMvc.perform(post("/api/matriculas/10/traslado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nuevoGrupoId\":3,\"motivo\":\"Ajuste de cupo autorizado\"}")
                        .with(jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_COORDINADOR"))
                                .jwt(b -> b.subject("coordinador1").claim("rol", "COORDINADOR").claim("id", 5L))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10));
    }

    @Test
    @DisplayName("PATCH /api/matriculas/{id}/estado con rol ADMINISTRADOR debe permitir cambio de estado (200 OK)")
    void patchEstado_ConRolAdministrador_DebeRetornar200() throws Exception {
        MatriculaResponse response = new MatriculaResponse();
        response.setId(10L);
        response.setEstado(EstadoMatricula.RETIRADA);
        when(matriculaService.cambiarEstado(eq(10L), any(CambioEstadoMatriculaRequest.class))).thenReturn(response);

        mockMvc.perform(patch("/api/matriculas/10/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nuevoEstado\":\"RETIRADA\",\"motivo\":\"Retiro voluntario por traslado de ciudad\"}")
                        .with(jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_ADMINISTRADOR"))
                                .jwt(b -> b.subject("admin1").claim("rol", "ADMINISTRADOR"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("RETIRADA"));
    }

    @Test
    @DisplayName("GET /api/matriculas/verificar-activa con rol PROFESOR debe permitir consulta contextual (200 OK)")
    void getVerificarActiva_ConRolProfesor_DebeRetornar200() throws Exception {
        when(matriculaService.tieneMatriculaActivaEnGrupo(1L, 2L)).thenReturn(true);

        mockMvc.perform(get("/api/matriculas/verificar-activa?estudianteId=1&grupoId=2")
                        .with(jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_PROFESOR"))
                                .jwt(b -> b.subject("profesor1").claim("rol", "PROFESOR"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activa").value(true));
    }
}
