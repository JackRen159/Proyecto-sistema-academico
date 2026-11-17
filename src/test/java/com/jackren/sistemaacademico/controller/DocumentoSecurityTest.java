package com.jackren.sistemaacademico.controller;

import com.jackren.sistemaacademico.config.JwtConfig;
import com.jackren.sistemaacademico.config.SecurityConfig;
import com.jackren.sistemaacademico.dto.*;
import com.jackren.sistemaacademico.entity.TipoDocumentoAcademico;
import com.jackren.sistemaacademico.repository.UsuarioRepository;
import com.jackren.sistemaacademico.security.*;
import com.jackren.sistemaacademico.service.DocumentoAcademicoService;
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

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = DocumentoAcademicoController.class)
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
@DisplayName("Pruebas de Seguridad RBAC - Controlador de Documentos y Reportes Académicos")
class DocumentoSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DocumentoAcademicoService documentoService;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @Test
    @DisplayName("GET /api/documentos/consecutivos/anio-lectivo/1 sin autenticación debe retornar 401 Unauthorized")
    void getConsecutivos_SinAutenticacion_DebeRetornar401() throws Exception {
        mockMvc.perform(get("/api/documentos/consecutivos/anio-lectivo/1"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @DisplayName("POST /api/documentos/consecutivos con rol PROFESOR debe retornar 403 Forbidden")
    void postConsecutivo_ConRolProfesor_DebeRetornar403() throws Exception {
        mockMvc.perform(post("/api/documentos/consecutivos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"anioLectivoId\":1,\"tipoDocumento\":\"CERTIFICADO_ESTUDIO\",\"prefijo\":\"CERT\"}")
                        .with(jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_PROFESOR"))
                                .jwt(b -> b.subject("profesor1").claim("rol", "PROFESOR"))))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    @DisplayName("POST /api/documentos/consecutivos con rol RECTOR debe retornar 200 OK")
    void postConsecutivo_ConRolRector_DebeRetornar200() throws Exception {
        ConfiguracionConsecutivoResponse res = new ConfiguracionConsecutivoResponse();
        res.setId(10L);
        res.setPrefijo("CERT");

        when(documentoService.guardarOActualizarConsecutivo(any())).thenReturn(res);

        mockMvc.perform(post("/api/documentos/consecutivos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"anioLectivoId\":1,\"tipoDocumento\":\"CERTIFICADO_ESTUDIO\",\"prefijo\":\"CERT\"}")
                        .with(jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_RECTOR"))
                                .jwt(b -> b.subject("rector1").claim("rol", "RECTOR"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.prefijo").value("CERT"));
    }

    @Test
    @DisplayName("POST /api/documentos/boletines con rol ESTUDIANTE debe retornar 200 OK")
    void postBoletin_ConRolEstudiante_DebeRetornar200() throws Exception {
        BoletinPeriodoResponse res = new BoletinPeriodoResponse();
        res.setNumeroConsecutivo("BOL-2026-00001");
        res.setCodigoVerificacion("abc-123");

        when(documentoService.generarBoletinPeriodo(eq(1L), eq(2L), any(), any())).thenReturn(res);

        mockMvc.perform(post("/api/documentos/boletines/matricula/1/periodo/2")
                        .with(jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_ESTUDIANTE"))
                                .jwt(b -> b.subject("estudiante1").claim("rol", "ESTUDIANTE"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numeroConsecutivo").value("BOL-2026-00001"));
    }

    @Test
    @DisplayName("POST /api/documentos/constancias con rol ACUDIENTE debe retornar 200 OK")
    void postConstancia_ConRolAcudiente_DebeRetornar200() throws Exception {
        ConstanciaMatriculaResponse res = new ConstanciaMatriculaResponse();
        res.setNumeroConsecutivo("CONST-2026-00001");

        when(documentoService.generarConstanciaMatricula(eq(1L), any(), any())).thenReturn(res);

        mockMvc.perform(post("/api/documentos/constancias/matricula/1")
                        .with(jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_ACUDIENTE"))
                                .jwt(b -> b.subject("acudiente1").claim("rol", "ACUDIENTE"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numeroConsecutivo").value("CONST-2026-00001"));
    }

    @Test
    @DisplayName("POST /api/documentos/certificados con rol ESTUDIANTE debe retornar 403 Forbidden")
    void postCertificado_ConRolEstudiante_DebeRetornar403() throws Exception {
        mockMvc.perform(post("/api/documentos/certificados/matricula/1")
                        .with(jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_ESTUDIANTE"))
                                .jwt(b -> b.subject("estudiante1").claim("rol", "ESTUDIANTE"))))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    @DisplayName("GET /api/documentos/verificar sin autenticación debe retornar 200 OK (acceso público)")
    void getVerificar_SinAutenticacion_DebeRetornar200() throws Exception {
        RegistroEmisionResponse res = new RegistroEmisionResponse();
        res.setCodigoVerificacion("val-999");
        res.setNumeroConsecutivoCompleto("CERT-2026-00001");
        res.setTipoDocumento(TipoDocumentoAcademico.CERTIFICADO_ESTUDIO);

        when(documentoService.consultarEmisionPorCodigoVerificacion("val-999")).thenReturn(res);

        mockMvc.perform(get("/api/documentos/verificar/val-999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigoVerificacion").value("val-999"))
                .andExpect(jsonPath("$.numeroConsecutivoCompleto").value("CERT-2026-00001"));
    }

    @Test
    @DisplayName("PATCH /api/documentos/emisiones/1/anular con rol PROFESOR debe retornar 403 Forbidden")
    void patchAnular_ConRolProfesor_DebeRetornar403() throws Exception {
        mockMvc.perform(patch("/api/documentos/emisiones/1/anular")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"motivoAnulacion\":\"Error formal\"}")
                        .with(jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_PROFESOR"))
                                .jwt(b -> b.subject("profesor1").claim("rol", "PROFESOR"))))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    @DisplayName("PATCH /api/documentos/emisiones/1/anular con rol SECRETARIA debe retornar 200 OK")
    void patchAnular_ConRolSecretaria_DebeRetornar200() throws Exception {
        RegistroEmisionResponse res = new RegistroEmisionResponse();
        res.setId(1L);
        res.setAnulado(true);
        res.setMotivoAnulacion("Error formal");

        when(documentoService.anularEmision(eq(1L), any(), any())).thenReturn(res);

        mockMvc.perform(patch("/api/documentos/emisiones/1/anular")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"motivoAnulacion\":\"Error formal\"}")
                        .with(jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_SECRETARIA"))
                                .jwt(b -> b.subject("secretaria1").claim("rol", "SECRETARIA"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.anulado").value(true))
                .andExpect(jsonPath("$.motivoAnulacion").value("Error formal"));
    }
}
