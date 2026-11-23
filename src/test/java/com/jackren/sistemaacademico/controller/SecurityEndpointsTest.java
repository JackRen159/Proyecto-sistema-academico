package com.jackren.sistemaacademico.controller;

import com.jackren.sistemaacademico.config.JwtConfig;
import com.jackren.sistemaacademico.config.SecurityConfig;
import com.jackren.sistemaacademico.dto.LoginRequest;
import com.jackren.sistemaacademico.dto.LoginResponse;
import com.jackren.sistemaacademico.entity.Rol;
import com.jackren.sistemaacademico.repository.UsuarioRepository;
import com.jackren.sistemaacademico.security.CustomUserDetailsService;
import com.jackren.sistemaacademico.security.LoginRateLimiterService;
import com.jackren.sistemaacademico.security.RestAccessDeniedHandler;
import com.jackren.sistemaacademico.security.RestAuthenticationEntryPoint;
import com.jackren.sistemaacademico.security.TokenRevocationService;
import com.jackren.sistemaacademico.service.AuthService;
import com.jackren.sistemaacademico.service.EstudianteService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {EstudianteController.class, AuthController.class})
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
class SecurityEndpointsTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EstudianteService estudianteService;

    @MockitoBean
    private com.jackren.sistemaacademico.service.ExpedienteEstudianteService expedienteEstudianteService;

    @MockitoBean
    private com.jackren.sistemaacademico.service.EvolucionDocumentoService evolucionDocumentoService;

    @MockitoBean
    private com.jackren.sistemaacademico.service.SolicitudRectificacionService rectificacionService;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private com.jackren.sistemaacademico.service.RecuperacionPasswordService recuperacionPasswordService;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @Test
    void getEstudiantes_SinToken_DebeRetornar401UnauthorizedJson() throws Exception {
        mockMvc.perform(get("/api/estudiantes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Autenticacion requerida para acceder al recurso solicitado"))
                .andExpect(jsonPath("$.path").value("/api/estudiantes"));
    }

    @Test
    void login_CuandoCredencialesSonInvalidas_DebeRetornar401Json() throws Exception {
        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new BadCredentialsException("Credenciales invalidas"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"clave_erronea\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("No autorizado"))
                .andExpect(jsonPath("$.mensaje").value("Credenciales invalidas"));
    }

    @Test
    void login_CuandoCredencialesSonValidas_DebeRetornar200ConToken() throws Exception {
        LoginResponse response = new LoginResponse("token.mock.jwt", "Bearer", "admin", Rol.ADMINISTRADOR, 28800L);
        when(authService.login(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"Admin123*\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token.mock.jwt"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.username").value("admin"))
                .andExpect(jsonPath("$.rol").value("ADMINISTRADOR"));
    }

    @Test
    void logout_SinAutenticacionPrevia_DebeRetornar200Ok() throws Exception {
        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").exists());
    }

    @Test
    void getEstudiantes_ConTokenJwtValido_DebePermitirAcceso() throws Exception {
        when(estudianteService.listarPaginado(any())).thenReturn(Page.empty());

        mockMvc.perform(get("/api/estudiantes")
                        .with(jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_ADMINISTRADOR"))
                                .jwt(builder -> builder.subject("admin").claim("rol", "ADMINISTRADOR"))))
                .andExpect(status().isOk());
    }

    @Test
    void eliminarEstudiante_ConRolNoAdministrador_DebeRetornar403Forbidden() throws Exception {
        mockMvc.perform(delete("/api/estudiantes/1")
                        .param("confirmar", "true")
                        .with(jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_PROFESOR"))
                                .jwt(builder -> builder.subject("profe").claim("rol", "PROFESOR"))))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.error").value("Acceso denegado"))
                .andExpect(jsonPath("$.mensaje").value("No posee los permisos requeridos para ejecutar esta operacion"));
    }

    @Test
    void eliminarEstudiante_ConRolAdministrador_DebePermitirOperacion() throws Exception {
        doNothing().when(estudianteService).eliminarPermanente(eq(1L), eq(true));

        mockMvc.perform(delete("/api/estudiantes/1")
                        .param("confirmar", "true")
                        .with(jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_ADMINISTRADOR"))
                                .jwt(builder -> builder.subject("admin").claim("rol", "ADMINISTRADOR"))))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Petición a /api/estudiantes con debeCambiarPassword=true debe ser bloqueada con 403 (UC-01 A1)")
    void getEstudiantes_ConTokenDebeCambiarPasswordTrue_DebeRetornar403Forbidden() throws Exception {
        mockMvc.perform(get("/api/estudiantes")
                        .with(jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_ADMINISTRADOR"))
                                .jwt(builder -> builder.subject("admin")
                                        .claim("rol", "ADMINISTRADOR")
                                        .claim("debeCambiarPassword", true))))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.codigo").value("PASSWORD_CAMBIO_REQUERIDO"))
                .andExpect(jsonPath("$.message").value("Debe cambiar su contraseña temporal antes de acceder a este recurso (UC-01 A1)"));
    }

    @Test
    @DisplayName("Petición a /api/auth/cambiar-password con debeCambiarPassword=true debe permitirse pasar el filtro")
    void postCambiarPassword_ConTokenDebeCambiarPasswordTrue_DebePermitirOperacion() throws Exception {
        mockMvc.perform(post("/api/auth/cambiar-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"passwordActual\":\"Temp123*\",\"passwordNuevo\":\"NuevoPass123*\",\"confirmacionPassword\":\"NuevoPass123*\"}")
                        .with(jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_DOCENTE"))
                                .jwt(builder -> builder.subject("docente1")
                                        .claim("rol", "PROFESOR")
                                        .claim("debeCambiarPassword", true))))
                .andExpect(status().isOk());
    }
}
