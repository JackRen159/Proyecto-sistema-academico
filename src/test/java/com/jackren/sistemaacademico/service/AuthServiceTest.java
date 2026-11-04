package com.jackren.sistemaacademico.service;

import com.jackren.sistemaacademico.dto.LoginRequest;
import com.jackren.sistemaacademico.dto.LoginResponse;
import com.jackren.sistemaacademico.dto.UsuarioResponse;
import com.jackren.sistemaacademico.entity.Rol;
import com.jackren.sistemaacademico.entity.Usuario;
import com.jackren.sistemaacademico.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtEncoder jwtEncoder;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private AuthService authService;

    private Usuario usuarioMock;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authService, "expirationHours", 8L);

        usuarioMock = new Usuario("admin", "hashed_password", Rol.ADMINISTRADOR);
        usuarioMock.setId(1L);
    }

    @Test
    void login_CuandoCredencialesSonValidas_DebeRetornarTokenJWT() {
        LoginRequest request = new LoginRequest("admin", "Admin123*");
        Authentication authMock = mock(Authentication.class);
        when(authMock.getName()).thenReturn("admin");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authMock);
        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(usuarioMock));

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
    void login_CuandoCredencialesSonInvalidas_DebeLanzarBadCredentialsException() {
        LoginRequest request = new LoginRequest("admin", "PasswordErroneo");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        BadCredentialsException exception = assertThrows(BadCredentialsException.class, () -> authService.login(request));

        assertEquals("Credenciales invalidas", exception.getMessage());
        verify(jwtEncoder, never()).encode(any());
    }

    @Test
    void obtenerUsuarioActual_CuandoExiste_DebeRetornarUsuarioResponse() {
        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(usuarioMock));

        UsuarioResponse response = authService.obtenerUsuarioActual("admin");

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("admin", response.getUsername());
        assertEquals(Rol.ADMINISTRADOR, response.getRol());
        assertTrue(response.getActivo());
    }

    @Test
    void obtenerUsuarioActual_CuandoNoExiste_DebeLanzarException() {
        when(usuarioRepository.findByUsername("desconocido")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> authService.obtenerUsuarioActual("desconocido"));
    }
}

