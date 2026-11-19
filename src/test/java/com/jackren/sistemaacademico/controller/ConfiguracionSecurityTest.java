package com.jackren.sistemaacademico.controller;

import com.jackren.sistemaacademico.config.JwtConfig;
import com.jackren.sistemaacademico.config.SecurityConfig;
import com.jackren.sistemaacademico.dto.ConfiguracionSieeResponse;
import com.jackren.sistemaacademico.dto.InstitucionDatosLegalesResponse;
import com.jackren.sistemaacademico.dto.PoliticaDatosResponse;
import com.jackren.sistemaacademico.repository.UsuarioRepository;
import com.jackren.sistemaacademico.security.*;
import com.jackren.sistemaacademico.service.ConfiguracionInstitucionalService;
import com.jackren.sistemaacademico.service.PoliticaDatosService;
import com.jackren.sistemaacademico.service.SieeConfiguracionService;
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

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ConfiguracionInstitucionalController.class)
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
@DisplayName("Pruebas de Seguridad RBAC - Controlador de Configuración Institucional y SIEE V2")
class ConfiguracionSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ConfiguracionInstitucionalService configuracionService;

    @MockitoBean
    private PoliticaDatosService politicaService;

    @MockitoBean
    private SieeConfiguracionService sieeService;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @Test
    @DisplayName("GET /api/configuracion/institucion es público y debe retornar 200 OK sin autenticación")
    void getInstitucion_Publico_DebeRetornar200() throws Exception {
        InstitucionDatosLegalesResponse resp = new InstitucionDatosLegalesResponse();
        resp.setNombreOficial("Colegio Departamental");
        resp.setCodigoDane("123456789012");
        when(configuracionService.obtenerDatosLegales()).thenReturn(resp);

        mockMvc.perform(get("/api/configuracion/institucion"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreOficial").value("Colegio Departamental"));
    }

    @Test
    @DisplayName("GET /api/configuracion/politica-datos/vigente es público y debe retornar 200 OK")
    void getPoliticaVigente_Publico_DebeRetornar200() throws Exception {
        PoliticaDatosResponse resp = new PoliticaDatosResponse();
        resp.setNumeroVersion(1);
        resp.setTitulo("Política de Datos V1");
        when(politicaService.obtenerPoliticaVigente()).thenReturn(resp);

        mockMvc.perform(get("/api/configuracion/politica-datos/vigente"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numeroVersion").value(1));
    }

    @Test
    @DisplayName("GET /api/configuracion/siee/vigente es público y debe retornar 200 OK")
    void getSieeVigente_Publico_DebeRetornar200() throws Exception {
        ConfiguracionSieeResponse resp = new ConfiguracionSieeResponse();
        resp.setNumeroVersion(1);
        resp.setNombreVersion("SIEE V1");
        when(sieeService.obtenerSieeVigente()).thenReturn(resp);

        mockMvc.perform(get("/api/configuracion/siee/vigente"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreVersion").value("SIEE V1"));
    }

    @Test
    @DisplayName("PUT /api/configuracion/institucion con rol ESTUDIANTE debe retornar 403 Forbidden")
    void putInstitucion_RolEstudiante_DebeRetornar403() throws Exception {
        String json = """
                {
                    "nombreOficial": "Intento no autorizado",
                    "codigoDane": "123456789012",
                    "nit": "900123456-7",
                    "resolucionAprobacion": "Res 123",
                    "caracter": "OFICIAL",
                    "nombreRectorFirmante": "Rector",
                    "nombreSecretarioFirmante": "Secretaria"
                }
                """;

        mockMvc.perform(put("/api/configuracion/institucion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ESTUDIANTE"))
                                .jwt(b -> b.subject("estudiante1").claim("rol", "ESTUDIANTE"))))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }
}
