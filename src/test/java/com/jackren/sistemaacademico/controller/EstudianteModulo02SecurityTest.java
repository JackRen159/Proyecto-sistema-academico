package com.jackren.sistemaacademico.controller;

import com.jackren.sistemaacademico.config.JwtConfig;
import com.jackren.sistemaacademico.config.SecurityConfig;
import com.jackren.sistemaacademico.dto.*;
import com.jackren.sistemaacademico.entity.TipoDocumento;
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

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {EstudianteController.class, EstudianteAcudienteController.class, SolicitudRectificacionController.class})
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
@DisplayName("Pruebas de Seguridad RBAC - Módulo 02 V2 (Estudiantes, Acudientes, Expediente y Rectificaciones)")
class EstudianteModulo02SecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EstudianteService estudianteService;

    @MockitoBean
    private ExpedienteEstudianteService expedienteEstudianteService;

    @MockitoBean
    private EvolucionDocumentoService evolucionDocumentoService;

    @MockitoBean
    private SolicitudRectificacionService rectificacionService;

    @MockitoBean
    private EstudianteAcudienteService estudianteAcudienteService;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @Test
    @DisplayName("GET /api/vinculaciones/mis-estudiantes con rol ACUDIENTE debe retornar 200 OK (UC-06)")
    void misEstudiantes_RolAcudiente_DebeRetornar200() throws Exception {
        when(estudianteAcudienteService.listarMisEstudiantesAsociados("acudiente1"))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/vinculaciones/mis-estudiantes")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ACUDIENTE"))
                                .jwt(b -> b.subject("acudiente1").claim("rol", "ACUDIENTE"))))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/estudiantes/mi-ficha con rol ESTUDIANTE debe retornar 200 OK (UC-07)")
    void miFicha_RolEstudiante_DebeRetornar200() throws Exception {
        FichaEscolarEstudianteResponse resp = new FichaEscolarEstudianteResponse();
        resp.setDocumento("10203040");
        resp.setNombreCompleto("Juan Pérez");
        when(estudianteService.obtenerMiFichaEscolar("estudiante1")).thenReturn(resp);

        mockMvc.perform(get("/api/estudiantes/mi-ficha")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ESTUDIANTE"))
                                .jwt(b -> b.subject("estudiante1").claim("rol", "ESTUDIANTE"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.documento").value("10203040"));
    }

    @Test
    @DisplayName("POST /api/estudiantes/expediente-completo con rol SECRETARIA debe retornar 201 Created (UC-12)")
    void postExpedienteCompleto_RolSecretaria_DebeRetornar201() throws Exception {
        ExpedienteCompletoResponse resp = new ExpedienteCompletoResponse();
        resp.setEstudianteId(1L);
        resp.setEstudianteDocumento("10203040");
        when(expedienteEstudianteService.registrarExpedienteCompleto(any(), any(), any(), any())).thenReturn(resp);

        String json = """
                {
                    "nombres": "Carlos",
                    "apellidos": "Gómez",
                    "tipoDocumento": "TI",
                    "documento": "10203040",
                    "fechaNacimiento": "2012-05-10",
                    "direccion": "Calle 10",
                    "acudienteNombres": "Ana",
                    "acudienteApellidos": "Pérez",
                    "acudienteDocumento": "52000111",
                    "acudienteTelefono": "3001234567",
                    "parentesco": "MADRE"
                }
                """;

        mockMvc.perform(post("/api/estudiantes/expediente-completo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_SECRETARIA"))
                                .jwt(b -> b.subject("secretaria1").claim("rol", "SECRETARIA"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estudianteDocumento").value("10203040"));
    }

    @Test
    @DisplayName("PUT /api/estudiantes/{id}/evolucion-documento con rol SECRETARIA debe retornar 200 OK (UC-PROV-04 A1)")
    void putEvolucionDocumento_RolSecretaria_DebeRetornar200() throws Exception {
        HistorialDocumentoIdentidadResponse resp = new HistorialDocumentoIdentidadResponse();
        resp.setTipoDocumentoNuevo(TipoDocumento.TI);
        resp.setNumeroDocumentoNuevo("10203040");
        when(evolucionDocumentoService.registrarEvolucionDocumento(eq(1L), any(), any())).thenReturn(resp);

        String json = """
                {
                    "tipoDocumentoNuevo": "TI",
                    "numeroDocumentoNuevo": "10203040",
                    "motivoCambio": "Cumplimiento de 7 años"
                }
                """;

        mockMvc.perform(put("/api/estudiantes/1/evolucion-documento")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_SECRETARIA"))
                                .jwt(b -> b.subject("secretaria1").claim("rol", "SECRETARIA"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numeroDocumentoNuevo").value("10203040"));
    }

    @Test
    @DisplayName("POST /api/vinculaciones/{id}/restriccion-legal con rol RECTOR debe retornar 200 OK (UC-PROV-05 A3)")
    void postRestriccionLegal_RolRector_DebeRetornar200() throws Exception {
        String json = """
                {
                    "motivoBloqueoLegal": "Medida cautelar Comisaría de Familia Oficio 123-2026"
                }
                """;

        mockMvc.perform(post("/api/vinculaciones/10/restriccion-legal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_RECTOR"))
                                .jwt(b -> b.subject("rector1").claim("rol", "RECTOR"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").exists());
    }
}
