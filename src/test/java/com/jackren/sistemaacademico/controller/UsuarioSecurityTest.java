package com.jackren.sistemaacademico.controller;

import com.jackren.sistemaacademico.config.JwtConfig;
import com.jackren.sistemaacademico.config.SecurityConfig;
import com.jackren.sistemaacademico.dto.ActualizarEstadoUsuarioRequest;
import com.jackren.sistemaacademico.dto.CrearUsuarioRequest;
import com.jackren.sistemaacademico.dto.UsuarioResponse;
import com.jackren.sistemaacademico.entity.EstadoUsuario;
import com.jackren.sistemaacademico.entity.Rol;
import com.jackren.sistemaacademico.repository.UsuarioRepository;
import com.jackren.sistemaacademico.security.*;
import com.jackren.sistemaacademico.service.UsuarioService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UsuarioController.class)
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
@DisplayName("Pruebas de Seguridad RBAC - Controlador de Usuarios y Roles V2")
class UsuarioSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioService usuarioService;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @Test
    @DisplayName("GET /api/usuarios sin autenticación debe retornar 401 Unauthorized")
    void getUsuarios_SinAutenticacion_DebeRetornar401() throws Exception {
        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @DisplayName("GET /api/usuarios con rol ESTUDIANTE debe retornar 403 Forbidden")
    void getUsuarios_RolEstudiante_DebeRetornar403() throws Exception {
        mockMvc.perform(get("/api/usuarios")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ESTUDIANTE"))
                                .jwt(b -> b.subject("estudiante1").claim("rol", "ESTUDIANTE"))))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    @DisplayName("POST /api/usuarios con rol ADMINISTRADOR debe permitir creación (201 Created)")
    void postUsuario_RolAdmin_DebeRetornar201() throws Exception {
        UsuarioResponse response = new UsuarioResponse(1L, "nuevo.docente", Rol.PROFESOR, true);
        when(usuarioService.crearUsuario(any(CrearUsuarioRequest.class), any())).thenReturn(response);

        String json = """
                {
                    "username": "nuevo.docente",
                    "rol": "PROFESOR"
                }
                """;

        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMINISTRADOR"))
                                .jwt(b -> b.subject("admin").claim("rol", "ADMINISTRADOR"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("nuevo.docente"));
    }

    @Test
    @DisplayName("POST /api/usuarios con rol SECRETARIA intentando crear rol RECTOR debe retornar 403 Forbidden (E3)")
    void postUsuario_RolSecretariaCreandoRector_DebeRetornar403() throws Exception {
        String json = """
                {
                    "username": "nuevo.rector",
                    "rol": "RECTOR"
                }
                """;

        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_SECRETARIA"))
                                .jwt(b -> b.subject("secretaria1").claim("rol", "SECRETARIA"))))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    @DisplayName("DELETE /api/usuarios/{id} debe responder 409 Conflicto prohibiendo borrado físico (E4)")
    void deleteUsuario_DebeRetornar409Prohibido() throws Exception {
        doThrow(new com.jackren.sistemaacademico.exception.ConflictoException("Prohibido el borrado físico (E4)"))
                .when(usuarioService).eliminarUsuario(eq(1L), any());

        mockMvc.perform(delete("/api/usuarios/1")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMINISTRADOR"))
                                .jwt(b -> b.subject("admin").claim("rol", "ADMINISTRADOR"))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }
}
