package jackren.sistemaacademico.main.controller;

import jackren.sistemaacademico.main.dto.LoginRequest;
import jackren.sistemaacademico.main.dto.LoginResponse;
import jackren.sistemaacademico.main.dto.UsuarioResponse;
import jackren.sistemaacademico.main.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout() {
        return ResponseEntity.ok(Map.of("mensaje", "Sesion cerrada exitosamente. Descarte el token en memoria."));
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioResponse> me(@AuthenticationPrincipal Jwt jwt) {
        UsuarioResponse response = authService.obtenerUsuarioActual(jwt.getSubject());
        return ResponseEntity.ok(response);
    }
}

