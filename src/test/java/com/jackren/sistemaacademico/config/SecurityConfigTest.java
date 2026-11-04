package com.jackren.sistemaacademico.config;

import com.jackren.sistemaacademico.controller.AcudienteController;
import com.jackren.sistemaacademico.controller.AuthController;
import com.jackren.sistemaacademico.controller.EstudianteController;
import com.jackren.sistemaacademico.dto.EstudianteRequest;
import com.jackren.sistemaacademico.dto.EstudianteResponse;
import com.jackren.sistemaacademico.dto.LoginRequest;
import com.jackren.sistemaacademico.dto.LoginResponse;
import com.jackren.sistemaacademico.entity.Rol;
import com.jackren.sistemaacademico.repository.UsuarioRepository;
import com.jackren.sistemaacademico.security.CustomUserDetailsService;
import com.jackren.sistemaacademico.security.LoginRateLimiterService;
import com.jackren.sistemaacademico.security.RestAccessDeniedHandler;
import com.jackren.sistemaacademico.security.RestAuthenticationEntryPoint;
import com.jackren.sistemaacademico.security.TokenRevocationService;
import com.jackren.sistemaacademico.service.AcudienteService;
import com.jackren.sistemaacademico.service.AuthService;
import com.jackren.sistemaacademico.service.EstudianteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {EstudianteController.class, AcudienteController.class, AuthController.class})
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
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtEncoder jwtEncoder;

    @Autowired
    private TokenRevocationService tokenRevocationService;

    @Autowired
    private LoginRateLimiterService loginRateLimiterService;

    @MockitoBean
    private EstudianteService estudianteService;

    @MockitoBean
    private AcudienteService acudienteService;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @BeforeEach
    void setUp() {
        tokenRevocationService.clear();
        loginRateLimiterService.limpiarTodo();
    }

    // 1. Validacion de RBAC en lectura: ESTUDIANTE recibe 403, PROFESOR y SECRETARIA reciben 200
    @Test
    void accesoConTokenValidoRolEstudiante_DeberiaRetornar403Forbidden() throws Exception {
        mockMvc.perform(get("/api/estudiantes")
                        .with(jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_ESTUDIANTE"))
                                .jwt(builder -> builder.subject("estudiante").claim("rol", "ESTUDIANTE"))))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.error").value("Acceso denegado"));
    }

    @Test
    void accesoConTokenValidoRolProfesorOSecretaria_DeberiaPermitirLectura() throws Exception {
        when(estudianteService.listarPaginado(any())).thenReturn(Page.empty());

        // Rol PROFESOR -> 200 OK
        mockMvc.perform(get("/api/estudiantes")
                        .with(jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_PROFESOR"))
                                .jwt(builder -> builder.subject("profe").claim("rol", "PROFESOR"))))
                .andExpect(status().isOk());

        // Rol SECRETARIA -> 200 OK
        mockMvc.perform(get("/api/estudiantes")
                        .with(jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_SECRETARIA"))
                                .jwt(builder -> builder.subject("secretaria").claim("rol", "SECRETARIA"))))
                .andExpect(status().isOk());
    }

    // 2. Validacion de RBAC en mutaciones: ESTUDIANTE y PROFESOR reciben 403, SECRETARIA y RECTOR autorizados
    @Test
    void mutacionesEstudiantesYAcudientes_ConRolEstudianteOProfesor_DebeRetornar403() throws Exception {
        String estudianteJson = """
                {
                    "nombres": "Juan",
                    "apellidos": "Perez",
                    "documento": "12345678",
                    "correo": "juan@escuela.com",
                    "telefono": "3001234567",
                    "direccion": "Calle 10 # 5-20",
                    "fechaNacimiento": "2010-05-15"
                }
                """;

        // ESTUDIANTE intentando POST /api/estudiantes -> 403
        mockMvc.perform(post("/api/estudiantes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(estudianteJson)
                        .with(jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_ESTUDIANTE"))
                                .jwt(builder -> builder.subject("estudiante").claim("rol", "ESTUDIANTE"))))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.error").value("Acceso denegado"));

        // PROFESOR intentando POST /api/estudiantes -> 403
        mockMvc.perform(post("/api/estudiantes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(estudianteJson)
                        .with(jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_PROFESOR"))
                                .jwt(builder -> builder.subject("profe").claim("rol", "PROFESOR"))))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));

        // PROFESOR intentando PATCH /api/estudiantes/1/desactivar -> 403
        mockMvc.perform(patch("/api/estudiantes/1/desactivar")
                        .with(jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_PROFESOR"))
                                .jwt(builder -> builder.subject("profe").claim("rol", "PROFESOR"))))
                .andExpect(status().isForbidden());

        // ESTUDIANTE intentando POST /api/acudientes -> 403
        String acudienteJson = """
                {
                    "nombres": "Carlos",
                    "apellidos": "Perez",
                    "documento": "87654321",
                    "correo": "carlos@escuela.com",
                    "telefono": "3007654321",
                    "direccion": "Calle 10 # 5-20",
                    "ocupacion": "Ingeniero"
                }
                """;
        mockMvc.perform(post("/api/acudientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(acudienteJson)
                        .with(jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_ESTUDIANTE"))
                                .jwt(builder -> builder.subject("estudiante").claim("rol", "ESTUDIANTE"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void mutacionesEstudiantes_ConRolSecretariaORector_DebePermitirAutorizacion() throws Exception {
        String estudianteJson = """
                {
                    "nombres": "Juan",
                    "apellidos": "Perez",
                    "documento": "12345678",
                    "correo": "juan@escuela.com",
                    "telefono": "3001234567",
                    "direccion": "Calle 10 # 5-20",
                    "fechaNacimiento": "2010-05-15"
                }
                """;

        EstudianteResponse responseMock = new EstudianteResponse(
                1L, "Juan", "Perez", "12345678", LocalDate.of(2010, 5, 15),
                "juan@escuela.com", "3001234567", "Calle 10 # 5-20", true,
                LocalDateTime.now(), LocalDateTime.now()
        );
        when(estudianteService.guardar(any(EstudianteRequest.class))).thenReturn(responseMock);

        // SECRETARIA -> 201 Created
        mockMvc.perform(post("/api/estudiantes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(estudianteJson)
                        .with(jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_SECRETARIA"))
                                .jwt(builder -> builder.subject("secretaria").claim("rol", "SECRETARIA"))))
                .andExpect(status().isCreated());

        // RECTOR -> 201 Created
        mockMvc.perform(post("/api/estudiantes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(estudianteJson)
                        .with(jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_RECTOR"))
                                .jwt(builder -> builder.subject("rector").claim("rol", "RECTOR"))))
                .andExpect(status().isCreated());
    }

    // 3. Validacion de revocacion en logout: tras llamar a /logout con Bearer token, el token es rechazado con 401
    @Test
    void logout_ConBearerTokenValido_InvalidaTokenParaPeticionesSubsiguientes() throws Exception {
        Instant now = Instant.now();
        String jti = UUID.randomUUID().toString();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .id(jti)
                .issuer("sistema-academico")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(3600))
                .subject("admin")
                .claim("rol", "ADMINISTRADOR")
                .build();

        JwsHeader jwsHeader = JwsHeader.with(MacAlgorithm.HS256).build();
        String token = jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();

        when(estudianteService.listarPaginado(any())).thenReturn(Page.empty());

        // Peticion inicial con token valido -> 200 OK
        mockMvc.perform(get("/api/estudiantes")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        // Llamada a POST /api/auth/logout con ese mismo token -> 200 OK
        mockMvc.perform(post("/api/auth/logout")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").exists());

        // Peticion subsiguiente con el token revocado -> 401 Unauthorized
        mockMvc.perform(get("/api/estudiantes")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"));
    }

    // 4. Validacion de Rate Limiting en login: al realizar 6 peticiones consecutivas desde la misma IP, la 6ta devuelve 429
    @Test
    void login_AlSuperarCincoIntentosPorMinutoMismaIp_Retorna429TooManyRequests() throws Exception {
        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new BadCredentialsException("Credenciales invalidas"));

        String ipPrueba = "192.168.1.150";

        // Realizar 5 intentos que deben ser procesados (retornando 401 por credenciales invalidas)
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(post("/api/auth/login")
                            .header("X-Forwarded-For", ipPrueba)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"username\":\"admin\",\"password\":\"ClaveErronea123*\"}"))
                    .andExpect(status().isUnauthorized());
        }

        // El sexto intento consecutivo desde la misma IP debe ser bloqueado con 429 Too Many Requests
        mockMvc.perform(post("/api/auth/login")
                        .header("X-Forwarded-For", ipPrueba)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"ClaveErronea123*\"}"))
                .andExpect(status().isTooManyRequests())
                .andExpect(header().string("Retry-After", "60"))
                .andExpect(jsonPath("$.status").value(429))
                .andExpect(jsonPath("$.error").value("Demasiadas peticiones"))
                .andExpect(jsonPath("$.mensaje").value("Limite de intentos de inicio de sesion superado (maximo 5 intentos por minuto). Intente nuevamente en 60 segundos."));

        // Otra IP distinta NO debe estar bloqueada
        mockMvc.perform(post("/api/auth/login")
                        .header("X-Forwarded-For", "192.168.1.151")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"ClaveErronea123*\"}"))
                .andExpect(status().isUnauthorized());
    }
}
