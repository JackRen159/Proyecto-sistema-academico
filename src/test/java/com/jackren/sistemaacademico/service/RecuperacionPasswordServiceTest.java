package com.jackren.sistemaacademico.service;

import com.jackren.sistemaacademico.dto.ConfirmacionRecuperacionPasswordRequest;
import com.jackren.sistemaacademico.dto.SolicitudRecuperacionPasswordRequest;
import com.jackren.sistemaacademico.entity.EstadoUsuario;
import com.jackren.sistemaacademico.entity.Rol;
import com.jackren.sistemaacademico.entity.TokenRecuperacionPassword;
import com.jackren.sistemaacademico.entity.Usuario;
import com.jackren.sistemaacademico.exception.ValidacionException;
import com.jackren.sistemaacademico.repository.TokenRecuperacionPasswordRepository;
import com.jackren.sistemaacademico.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias - RecuperacionPasswordService (Autoservicio y Anti-enumeración E1, E2, E3)")
class RecuperacionPasswordServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private TokenRecuperacionPasswordRepository tokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuditoriaGlobalService auditoriaService;

    @Mock
    private com.jackren.sistemaacademico.security.TokenRevocationService tokenRevocationService;

    @InjectMocks
    private RecuperacionPasswordService recuperacionService;

    @Test
    @DisplayName("solicitarRecuperacion responde con mensaje neutro idéntico para usuario existente y no existente (E1)")
    void testSolicitarRecuperacionRespuestaNeutra() {
        // Caso 1: Usuario inexistente
        when(usuarioRepository.findByUsername("fantasma")).thenReturn(Optional.empty());
        String respInexistente = recuperacionService.solicitarRecuperacion(new SolicitudRecuperacionPasswordRequest("fantasma"));

        // Caso 2: Usuario existente
        Usuario u = new Usuario("real", "hash", Rol.ESTUDIANTE);
        u.setId(1L);
        when(usuarioRepository.findByUsername("real")).thenReturn(Optional.of(u));
        when(tokenRepository.findByUsuarioIdAndUsadoFalse(1L)).thenReturn(Collections.emptyList());
        String respExistente = recuperacionService.solicitarRecuperacion(new SolicitudRecuperacionPasswordRequest("real"));

        assertEquals(respInexistente, respExistente);
        assertTrue(respExistente.contains("Si el identificador corresponde"));
        verify(tokenRepository, times(1)).save(any(TokenRecuperacionPassword.class));
    }

    @Test
    @DisplayName("confirmarRecuperacion restablece contraseña exitosamente con token válido (A1)")
    void testConfirmarRecuperacionExitoso() {
        Usuario u = new Usuario("usuario1", "hashAnterior", Rol.ESTUDIANTE);
        u.setId(2L);
        u.setEstado(EstadoUsuario.PENDIENTE_ACTIVACION);

        String tokenPlano = "token-secreto-123";
        String tokenHash = AuditoriaGlobalService.calcularSha256(tokenPlano);

        TokenRecuperacionPassword token = new TokenRecuperacionPassword(
                u, tokenHash, LocalDateTime.now().plusMinutes(10));

        when(tokenRepository.findByTokenHash(tokenHash)).thenReturn(Optional.of(token));
        when(passwordEncoder.encode("NuevaPassword123")).thenReturn("nuevoHash");

        ConfirmacionRecuperacionPasswordRequest req = new ConfirmacionRecuperacionPasswordRequest(
                tokenPlano, "NuevaPassword123", "NuevaPassword123");

        recuperacionService.confirmarRecuperacion(req);

        assertEquals("nuevoHash", u.getPasswordHash());
        assertEquals(EstadoUsuario.ACTIVA, u.getEstado());
        assertFalse(u.getDebeCambiarPassword());
        assertTrue(token.getUsado());
        verify(usuarioRepository, times(1)).save(u);
        verify(tokenRepository, times(1)).save(token);
    }

    @Test
    @DisplayName("confirmarRecuperacion rechaza token expirado o utilizado con ValidacionException (E2)")
    void testConfirmarRecuperacionTokenExpirado() {
        Usuario u = new Usuario("usuario1", "hash", Rol.ESTUDIANTE);
        String tokenPlano = "token-expirado";
        String tokenHash = AuditoriaGlobalService.calcularSha256(tokenPlano);

        TokenRecuperacionPassword tokenExpirado = new TokenRecuperacionPassword(
                u, tokenHash, LocalDateTime.now().minusMinutes(5));

        when(tokenRepository.findByTokenHash(tokenHash)).thenReturn(Optional.of(tokenExpirado));

        ConfirmacionRecuperacionPasswordRequest req = new ConfirmacionRecuperacionPasswordRequest(
                tokenPlano, "NuevaPassword123", "NuevaPassword123");

        assertThrows(ValidacionException.class, () -> recuperacionService.confirmarRecuperacion(req));
    }

    @Test
    @DisplayName("confirmarRecuperacion rechaza contraseña débil con ValidacionException (E3)")
    void testConfirmarRecuperacionPasswordDebil() {
        ConfirmacionRecuperacionPasswordRequest reqCorta = new ConfirmacionRecuperacionPasswordRequest(
                "token", "123", "123");
        assertThrows(ValidacionException.class, () -> recuperacionService.confirmarRecuperacion(reqCorta));

        ConfirmacionRecuperacionPasswordRequest reqSinNumero = new ConfirmacionRecuperacionPasswordRequest(
                "token", "sololetras", "sololetras");
        assertThrows(ValidacionException.class, () -> recuperacionService.confirmarRecuperacion(reqSinNumero));
    }
}
