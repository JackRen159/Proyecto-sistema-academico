package com.jackren.sistemaacademico.service;

import com.jackren.sistemaacademico.dto.LoginRequest;
import com.jackren.sistemaacademico.dto.LoginResponse;
import com.jackren.sistemaacademico.dto.UsuarioResponse;
import com.jackren.sistemaacademico.entity.Usuario;
import com.jackren.sistemaacademico.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtEncoder jwtEncoder;
    private final UsuarioRepository usuarioRepository;

    @Value("${jwt.expiration-hours:8}")
    private long expirationHours;

    public AuthService(AuthenticationManager authenticationManager,
                       JwtEncoder jwtEncoder,
                       UsuarioRepository usuarioRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtEncoder = jwtEncoder;
        this.usuarioRepository = usuarioRepository;
    }

    public LoginResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            Usuario usuario = usuarioRepository.findByUsername(authentication.getName())
                    .orElseThrow(() -> new BadCredentialsException("Credenciales invalidas"));

            Instant now = Instant.now();
            Instant expiresAt = now.plus(expirationHours, ChronoUnit.HOURS);

            JwtClaimsSet claims = JwtClaimsSet.builder()
                    .id(UUID.randomUUID().toString())
                    .issuer("sistema-academico")
                    .issuedAt(now)
                    .expiresAt(expiresAt)
                    .subject(usuario.getUsername())
                    .claim("id", usuario.getId())
                    .claim("rol", usuario.getRol().name())
                    .build();

            JwsHeader jwsHeader = JwsHeader.with(MacAlgorithm.HS256).build();
            String token = jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
            long expiresInSeconds = ChronoUnit.SECONDS.between(now, expiresAt);

            return new LoginResponse(token, "Bearer", usuario.getUsername(), usuario.getRol(), expiresInSeconds);
        } catch (AuthenticationException ex) {
            // No revelar si el usuario existe o si la contrasena es incorrecta
            throw new BadCredentialsException("Credenciales invalidas");
        }
    }

    public UsuarioResponse obtenerUsuarioActual(String username) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        return new UsuarioResponse(usuario.getId(), usuario.getUsername(), usuario.getRol(), usuario.getActivo());
    }
}

