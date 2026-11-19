package com.jackren.sistemaacademico.service;

import com.jackren.sistemaacademico.dto.*;
import com.jackren.sistemaacademico.entity.*;
import com.jackren.sistemaacademico.exception.ValidacionException;
import com.jackren.sistemaacademico.repository.ConsentimientoPoliticaDatosRepository;
import com.jackren.sistemaacademico.repository.UsuarioRepository;
import com.jackren.sistemaacademico.repository.UsuarioRolRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtEncoder jwtEncoder;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioRolRepository usuarioRolRepository;
    private final ConsentimientoPoliticaDatosRepository consentimientoRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditoriaGlobalService auditoriaService;

    @Value("${jwt.expiration-hours:8}")
    private long expirationHours;

    public AuthService(AuthenticationManager authenticationManager,
                       JwtEncoder jwtEncoder,
                       UsuarioRepository usuarioRepository,
                       UsuarioRolRepository usuarioRolRepository,
                       ConsentimientoPoliticaDatosRepository consentimientoRepository,
                       PasswordEncoder passwordEncoder,
                       AuditoriaGlobalService auditoriaService) {
        this.authenticationManager = authenticationManager;
        this.jwtEncoder = jwtEncoder;
        this.usuarioRepository = usuarioRepository;
        this.usuarioRolRepository = usuarioRolRepository;
        this.consentimientoRepository = consentimientoRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditoriaService = auditoriaService;
    }

    public LoginResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            Usuario usuario = usuarioRepository.findByUsername(authentication.getName())
                    .orElseThrow(() -> new BadCredentialsException("Credenciales invalidas"));

            if (usuario.getEstado() == EstadoUsuario.BLOQUEADA || usuario.getEstado() == EstadoUsuario.INACTIVA) {
                throw new BadCredentialsException("La cuenta se encuentra inactiva o bloqueada (E2)");
            }

            // Roles vigentes asignados a la persona (UC-01 A2, RN-SEG-04)
            List<Rol> rolesDisponibles = usuarioRolRepository.findByUsuarioIdAndActivoTrue(usuario.getId()).stream()
                    .map(UsuarioRol::getRol)
                    .distinct()
                    .toList();

            if (rolesDisponibles.isEmpty()) {
                rolesDisponibles = List.of(usuario.getRol());
            }

            Rol rolActivo = usuario.getRol();
            if (request.getRolSolicitado() != null) {
                if (!rolesDisponibles.contains(request.getRolSolicitado())) {
                    throw new BadCredentialsException("El rol solicitado no está vigente para el usuario (E3)");
                }
                rolActivo = request.getRolSolicitado();
            } else if (!rolesDisponibles.contains(rolActivo) && !rolesDisponibles.isEmpty()) {
                rolActivo = rolesDisponibles.get(0);
            }

            boolean politicaAceptada = consentimientoRepository.findByUsuarioIdOrderByFechaAceptacionDesc(usuario.getId()).size() > 0;

            Instant now = Instant.now();
            Instant expiresAt = now.plus(expirationHours, ChronoUnit.HOURS);

            JwtClaimsSet claims = JwtClaimsSet.builder()
                    .id(UUID.randomUUID().toString())
                    .issuer("sistema-academico")
                    .issuedAt(now)
                    .expiresAt(expiresAt)
                    .subject(usuario.getUsername())
                    .claim("id", usuario.getId())
                    .claim("rol", rolActivo.name())
                    .claim("estado", usuario.getEstado().name())
                    .claim("debeCambiarPassword", usuario.getDebeCambiarPassword())
                    .build();

            JwsHeader jwsHeader = JwsHeader.with(MacAlgorithm.HS256).build();
            String token = jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
            long expiresInSeconds = ChronoUnit.SECONDS.between(now, expiresAt);

            // Restablecer contador de intentos fallidos tras login exitoso
            if (usuario.getIntentosFallidos() > 0) {
                usuario.setIntentosFallidos(0);
                usuarioRepository.save(usuario);
            }

            auditoriaService.registrarEvento(new RegistroAuditoriaEventoRequest(
                    usuario.getUsername(),
                    rolActivo.name(),
                    "SEGURIDAD",
                    "LOGIN",
                    "Usuario",
                    String.valueOf(usuario.getId()),
                    "Inicio de sesión exitoso",
                    ResultadoAuditoria.EXITO,
                    "AUTH-LOGIN-" + usuario.getId()
            ));

            return new LoginResponse(
                    token,
                    "Bearer",
                    usuario.getUsername(),
                    rolActivo,
                    expiresInSeconds,
                    usuario.getDebeCambiarPassword(),
                    politicaAceptada,
                    rolesDisponibles
            );
        } catch (AuthenticationException ex) {
            // No revelar si el usuario existe o si la contrasena es incorrecta
            throw new BadCredentialsException("Credenciales invalidas");
        }
    }

    public void cambiarPassword(CambioPasswordRequest request, String username) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        if (!passwordEncoder.matches(request.getPasswordActual(), usuario.getPasswordHash())) {
            throw new ValidacionException("La contraseña actual no coincide (E1)");
        }

        if (!request.getPasswordNuevo().equals(request.getConfirmacionPassword())) {
            throw new ValidacionException("La nueva contraseña y su confirmación no coinciden");
        }

        if (passwordEncoder.matches(request.getPasswordNuevo(), usuario.getPasswordHash())) {
            throw new ValidacionException("La nueva contraseña debe ser diferente a la contraseña actual (E3)");
        }

        if (request.getPasswordNuevo().length() < 8) {
            throw new ValidacionException("La nueva contraseña debe tener una longitud mínima de 8 caracteres (E3)");
        }

        boolean tieneLetra = request.getPasswordNuevo().chars().anyMatch(Character::isLetter);
        boolean tieneNumero = request.getPasswordNuevo().chars().anyMatch(Character::isDigit);
        if (!tieneLetra || !tieneNumero) {
            throw new ValidacionException("La nueva contraseña debe contener al menos una letra y un número (E3)");
        }

        usuario.setPasswordHash(passwordEncoder.encode(request.getPasswordNuevo()));
        usuario.setDebeCambiarPassword(false);
        usuario.setFechaUltimoCambioPassword(LocalDateTime.now());
        if (usuario.getEstado() == EstadoUsuario.PENDIENTE_ACTIVACION) {
            usuario.setEstado(EstadoUsuario.ACTIVA);
        }
        usuarioRepository.save(usuario);

        auditoriaService.registrarEvento(new RegistroAuditoriaEventoRequest(
                usuario.getUsername(),
                usuario.getRol().name(),
                "SEGURIDAD",
                "CAMBIAR_PASSWORD",
                "Usuario",
                String.valueOf(usuario.getId()),
                "Cambio exitoso de contraseña",
                ResultadoAuditoria.EXITO,
                "AUTH-CHPASS-" + usuario.getId()
        ));
    }

    @Transactional(readOnly = true)
    public UsuarioResponse obtenerUsuarioActual(String username) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        List<Rol> roles = usuarioRolRepository.findByUsuarioIdAndActivoTrue(usuario.getId()).stream()
                .map(UsuarioRol::getRol)
                .distinct()
                .toList();

        return new UsuarioResponse(
                usuario.getId(),
                usuario.getUsername(),
                usuario.getRol(),
                usuario.getActivo(),
                usuario.getEstado(),
                usuario.getDebeCambiarPassword(),
                roles
        );
    }
}
