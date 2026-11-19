package com.jackren.sistemaacademico.controller;

import com.jackren.sistemaacademico.dto.*;
import com.jackren.sistemaacademico.exception.DemasiadasPeticionesException;
import com.jackren.sistemaacademico.security.LoginRateLimiterService;
import com.jackren.sistemaacademico.security.TokenRevocationService;
import com.jackren.sistemaacademico.service.AuthService;
import com.jackren.sistemaacademico.service.RecuperacionPasswordService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controlador REST para operaciones de autenticación, cierre de sesión, cambio y recuperación de contraseñas.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final RecuperacionPasswordService recuperacionService;
    private final LoginRateLimiterService loginRateLimiterService;
    private final TokenRevocationService tokenRevocationService;

    public AuthController(AuthService authService,
                          RecuperacionPasswordService recuperacionService,
                          LoginRateLimiterService loginRateLimiterService,
                          TokenRevocationService tokenRevocationService) {
        this.authService = authService;
        this.recuperacionService = recuperacionService;
        this.loginRateLimiterService = loginRateLimiterService;
        this.tokenRevocationService = tokenRevocationService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request,
                                               HttpServletRequest httpRequest) {
        String ip = loginRateLimiterService.extraerIpCliente(httpRequest);
        if (!loginRateLimiterService.intentar(ip)) {
            throw new DemasiadasPeticionesException(
                    "Limite de intentos de inicio de sesion superado (maximo 5 intentos por minuto). Intente nuevamente en 60 segundos."
            );
        }

        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(@AuthenticationPrincipal Jwt jwt) {
        if (jwt != null) {
            tokenRevocationService.revokeToken(jwt.getId(), jwt.getExpiresAt());
        }
        return ResponseEntity.ok(Map.of("mensaje", "Sesion cerrada exitosamente. Descarte el token en memoria."));
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioResponse> me(@AuthenticationPrincipal Jwt jwt) {
        UsuarioResponse response = authService.obtenerUsuarioActual(jwt.getSubject());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/cambiar-password")
    public ResponseEntity<Map<String, String>> cambiarPassword(
            @Valid @RequestBody CambioPasswordRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        authService.cambiarPassword(request, jwt.getSubject());
        return ResponseEntity.ok(Map.of("mensaje", "Contraseña actualizada exitosamente."));
    }

    @PostMapping("/conmutar-rol")
    public ResponseEntity<LoginResponse> conmutarRol(
            @Valid @RequestBody ConmutarRolRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        LoginResponse response = authService.conmutarRol(request, jwt.getSubject(), jwt != null ? jwt.getId() : null);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/recuperar-password/solicitar")
    public ResponseEntity<Map<String, String>> solicitarRecuperacion(
            @Valid @RequestBody SolicitudRecuperacionPasswordRequest request) {
        String mensaje = recuperacionService.solicitarRecuperacion(request);
        return ResponseEntity.ok(Map.of("mensaje", mensaje));
    }

    @PostMapping("/recuperar-password/confirmar")
    public ResponseEntity<Map<String, String>> confirmarRecuperacion(
            @Valid @RequestBody ConfirmacionRecuperacionPasswordRequest request) {
        recuperacionService.confirmarRecuperacion(request);
        return ResponseEntity.ok(Map.of("mensaje", "Contraseña restablecida exitosamente. Puede iniciar sesión con sus nuevas credenciales."));
    }
}
