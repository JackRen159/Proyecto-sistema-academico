package com.jackren.sistemaacademico.controller;

import com.jackren.sistemaacademico.dto.LoginRequest;
import com.jackren.sistemaacademico.dto.LoginResponse;
import com.jackren.sistemaacademico.dto.UsuarioResponse;
import com.jackren.sistemaacademico.exception.DemasiadasPeticionesException;
import com.jackren.sistemaacademico.security.LoginRateLimiterService;
import com.jackren.sistemaacademico.security.TokenRevocationService;
import com.jackren.sistemaacademico.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controlador REST para operaciones de autenticacion, cierre de sesion y consulta de perfil.
 *
 * <h3>Stateless Tradeoff y Ciclo de Vida JWT:</h3>
 * <p>
 * Los tokens emitidos son completamente stateless con una vigencia de 8 horas en la aplicacion
 * cliente SPA. El cierre de sesion ({@code /api/auth/logout}) registra el identificador unico
 * ({@code jti}) del token en {@link TokenRevocationService} para denegar peticiones subsecuentes
 * en el servidor, mientras que el cliente descarta las credenciales en memoria.
 * </p>
 * <p>
 * La proteccion contra fuerza bruta en {@code /api/auth/login} restringe a un maximo de 5 intentos
 * por minuto por direccion IP, respondiendo HTTP 429 Too Many Requests con la cabecera
 * {@code Retry-After: 60}.
 * </p>
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final LoginRateLimiterService loginRateLimiterService;
    private final TokenRevocationService tokenRevocationService;

    public AuthController(AuthService authService,
                          LoginRateLimiterService loginRateLimiterService,
                          TokenRevocationService tokenRevocationService) {
        this.authService = authService;
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
}
