package com.jackren.sistemaacademico.service;

import com.jackren.sistemaacademico.dto.ConmutarRolRequest;
import com.jackren.sistemaacademico.dto.LoginRequest;
import com.jackren.sistemaacademico.dto.LoginResponse;
import com.jackren.sistemaacademico.dto.UsuarioResponse;
import com.jackren.sistemaacademico.entity.*;
import com.jackren.sistemaacademico.exception.ConflictoException;
import com.jackren.sistemaacademico.repository.ConsentimientoPoliticaDatosRepository;
import com.jackren.sistemaacademico.repository.OrganoColegiadoMiembroRepository;
import com.jackren.sistemaacademico.repository.UsuarioRepository;
import com.jackren.sistemaacademico.repository.UsuarioRolRepository;
import com.jackren.sistemaacademico.security.TokenRevocationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias - AuthService (Autenticación, Conmutación de Rol y Acceso Histórico)")
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtEncoder jwtEncoder;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private UsuarioRolRepository usuarioRolRepository;

    @Mock
    private OrganoColegiadoMiembroRepository organoRepository;

    @Mock
    private ConsentimientoPoliticaDatosRepository consentimientoRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuditoriaGlobalService auditoriaService;

    @Mock
    private TokenRevocationService tokenRevocationService;

    @InjectMocks
    private AuthService authService;

    private Usuario usuarioMock;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authService, "expirationHours", 8L);

        usuarioMock = new Usuario("admin", "hashed_password", Rol.ADMINISTRADOR);
        usuarioMock.setId(1L);
        usuarioMock.setEstado(EstadoUsuario.ACTIVA);
    }

    @Test
    @DisplayName("login cuando credenciales son válidas debe retornar token JWT")
    void login_CuandoCredencialesSonValidas_DebeRetornarTokenJWT() {
        LoginRequest request = new LoginRequest("admin", "Admin123*");
        Authentication authMock = mock(Authentication.class);
        when(authMock.getName()).thenReturn("admin");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authMock);
        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(usuarioMock));
        when(usuarioRolRepository.findRolesVigentesPorUsuario(eq(1L), any(LocalDate.class))).thenReturn(Collections.emptyList());
        when(consentimientoRepository.findByUsuarioIdOrderByFechaAceptacionDesc(1L)).thenReturn(Collections.emptyList());
        when(organoRepository.findByUsuarioId(1L)).thenReturn(Collections.emptyList());

        Jwt jwtMock = mock(Jwt.class);
        when(jwtMock.getTokenValue()).thenReturn("mock.jwt.token");
        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(jwtMock);

        LoginResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("mock.jwt.token", response.getToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals("admin", response.getUsername());
        assertEquals(Rol.ADMINISTRADOR, response.getRol());
        assertTrue(response.getExpiresIn() > 0);
    }

    @Test
    @DisplayName("login en cuenta INACTIVA_HISTORICA debe emitir token con rol HISTORICO_RESTRINGIDO (UC-01 A3)")
    void login_CuentaHistorica_DebeAsignarRolHistoricoRestringido() {
        usuarioMock.setEstado(EstadoUsuario.INACTIVA_HISTORICA);
        usuarioMock.setRol(Rol.ESTUDIANTE);

        LoginRequest request = new LoginRequest("admin", "Admin123*");
        Authentication authMock = mock(Authentication.class);
        when(authMock.getName()).thenReturn("admin");
        when(authenticationManager.authenticate(any())).thenReturn(authMock);
        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(usuarioMock));
        when(consentimientoRepository.findByUsuarioIdOrderByFechaAceptacionDesc(1L)).thenReturn(Collections.emptyList());
        when(organoRepository.findByUsuarioId(1L)).thenReturn(Collections.emptyList());

        Jwt jwtMock = mock(Jwt.class);
        when(jwtMock.getTokenValue()).thenReturn("mock.jwt.historico");
        when(jwtEncoder.encode(any())).thenReturn(jwtMock);

        LoginResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals(Rol.HISTORICO_RESTRINGIDO, response.getRol());
        assertEquals(List.of(Rol.HISTORICO_RESTRINGIDO), response.getRolesDisponibles());
    }

    @Test
    @DisplayName("conmutarRol debe validar vigencia, cambiar rol activo y sincronizar usuario.rol (UC-01 A2)")
    void conmutarRol_RolVigente_DebeEmitirNuevoTokenYSincronizar() {
        Usuario usuario = new Usuario("docente1", "hash", Rol.PROFESOR);
        usuario.setId(5L);
        usuario.setEstado(EstadoUsuario.ACTIVA);

        when(usuarioRepository.findByUsername("docente1")).thenReturn(Optional.of(usuario));
        when(usuarioRolRepository.findRolesVigentesPorUsuario(eq(5L), any(LocalDate.class)))
                .thenReturn(List.of(Rol.PROFESOR, Rol.COORDINADOR_ACADEMICO));
        when(organoRepository.findByUsuarioId(5L)).thenReturn(Collections.emptyList());
        when(consentimientoRepository.findByUsuarioIdOrderByFechaAceptacionDesc(5L)).thenReturn(Collections.emptyList());

        Jwt jwtMock = mock(Jwt.class);
        when(jwtMock.getTokenValue()).thenReturn("token.conmutado");
        when(jwtEncoder.encode(any())).thenReturn(jwtMock);

        ConmutarRolRequest req = new ConmutarRolRequest(Rol.COORDINADOR_ACADEMICO);
        LoginResponse resp = authService.conmutarRol(req, "docente1", "jti-anterior");

        assertNotNull(resp);
        assertEquals(Rol.COORDINADOR_ACADEMICO, resp.getRol());
        assertEquals("token.conmutado", resp.getToken());
        assertEquals(Rol.COORDINADOR_ACADEMICO, usuario.getRol()); // Sincronizado en BD (Deuda técnica D)
        verify(tokenRevocationService, times(1)).revokeToken(eq("jti-anterior"), any());
        verify(usuarioRepository, times(1)).save(usuario);
    }

    @Test
    @DisplayName("conmutarRol con rol no asignado debe lanzar ConflictoException (E3)")
    void conmutarRol_RolNoVigente_DebeLanzarConflicto() {
        Usuario usuario = new Usuario("docente1", "hash", Rol.PROFESOR);
        usuario.setId(5L);
        usuario.setEstado(EstadoUsuario.ACTIVA);

        when(usuarioRepository.findByUsername("docente1")).thenReturn(Optional.of(usuario));
        when(usuarioRolRepository.findRolesVigentesPorUsuario(eq(5L), any(LocalDate.class)))
                .thenReturn(List.of(Rol.PROFESOR));

        ConmutarRolRequest req = new ConmutarRolRequest(Rol.RECTOR);
        assertThrows(ConflictoException.class, () -> authService.conmutarRol(req, "docente1", "jti-1"));
    }

    @Test
    @DisplayName("login cuando credenciales son inválidas debe lanzar BadCredentialsException")
    void login_CuandoCredencialesSonInvalidas_DebeLanzarBadCredentialsException() {
        LoginRequest request = new LoginRequest("admin", "PasswordErroneo");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        BadCredentialsException exception = assertThrows(BadCredentialsException.class, () -> authService.login(request));

        assertEquals("Credenciales invalidas", exception.getMessage());
        verify(jwtEncoder, never()).encode(any());
    }

    @Test
    @DisplayName("obtenerUsuarioActual cuando existe debe retornar UsuarioResponse")
    void obtenerUsuarioActual_CuandoExiste_DebeRetornarUsuarioResponse() {
        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(usuarioMock));
        when(usuarioRolRepository.findRolesVigentesPorUsuario(eq(1L), any(LocalDate.class))).thenReturn(Collections.emptyList());

        UsuarioResponse response = authService.obtenerUsuarioActual("admin");

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("admin", response.getUsername());
        assertEquals(Rol.ADMINISTRADOR, response.getRol());
        assertTrue(response.getActivo());
    }

    @Test
    @DisplayName("obtenerUsuarioActual cuando no existe debe lanzar UsernameNotFoundException")
    void obtenerUsuarioActual_CuandoNoExiste_DebeLanzarException() {
        when(usuarioRepository.findByUsername("desconocido")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> authService.obtenerUsuarioActual("desconocido"));
    }
}
