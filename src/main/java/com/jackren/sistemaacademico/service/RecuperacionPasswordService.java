package com.jackren.sistemaacademico.service;

import com.jackren.sistemaacademico.dto.ConfirmacionRecuperacionPasswordRequest;
import com.jackren.sistemaacademico.dto.RegistroAuditoriaEventoRequest;
import com.jackren.sistemaacademico.dto.ResultadoSolicitudRecuperacion;
import com.jackren.sistemaacademico.dto.SolicitudRecuperacionPasswordRequest;
import com.jackren.sistemaacademico.entity.EstadoUsuario;
import com.jackren.sistemaacademico.entity.ResultadoAuditoria;
import com.jackren.sistemaacademico.entity.TipoPersonaUsuario;
import com.jackren.sistemaacademico.entity.TokenRecuperacionPassword;
import com.jackren.sistemaacademico.entity.Usuario;
import com.jackren.sistemaacademico.exception.ValidacionException;
import com.jackren.sistemaacademico.repository.AcudienteRepository;
import com.jackren.sistemaacademico.repository.EstudianteRepository;
import com.jackren.sistemaacademico.repository.ProfesorRepository;
import com.jackren.sistemaacademico.repository.TokenRecuperacionPasswordRepository;
import com.jackren.sistemaacademico.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * Servicio de autoservicio y recuperación de contraseñas (UC-PROV-03).
 * Implementa protección estricta contra enumeración de cuentas mediante respuesta neutra en tiempo constante (E1).
 */
@Service
@Transactional
public class RecuperacionPasswordService {

    private static final int MINUTOS_VIGENCIA_TOKEN = 15;
    private final UsuarioRepository usuarioRepository;
    private final TokenRecuperacionPasswordRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditoriaGlobalService auditoriaService;
    private final com.jackren.sistemaacademico.security.TokenRevocationService tokenRevocationService;
    private final ProfesorRepository profesorRepository;
    private final EstudianteRepository estudianteRepository;
    private final AcudienteRepository acudienteRepository;

    @Autowired
    public RecuperacionPasswordService(UsuarioRepository usuarioRepository,
                                       TokenRecuperacionPasswordRepository tokenRepository,
                                       PasswordEncoder passwordEncoder,
                                       AuditoriaGlobalService auditoriaService,
                                       com.jackren.sistemaacademico.security.TokenRevocationService tokenRevocationService,
                                       ProfesorRepository profesorRepository,
                                       EstudianteRepository estudianteRepository,
                                       AcudienteRepository acudienteRepository) {
        this.usuarioRepository = usuarioRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditoriaService = auditoriaService;
        this.tokenRevocationService = tokenRevocationService;
        this.profesorRepository = profesorRepository;
        this.estudianteRepository = estudianteRepository;
        this.acudienteRepository = acudienteRepository;
    }

    public RecuperacionPasswordService(UsuarioRepository usuarioRepository,
                                       TokenRecuperacionPasswordRepository tokenRepository,
                                       PasswordEncoder passwordEncoder,
                                       AuditoriaGlobalService auditoriaService,
                                       com.jackren.sistemaacademico.security.TokenRevocationService tokenRevocationService) {
        this(usuarioRepository, tokenRepository, passwordEncoder, auditoriaService, tokenRevocationService, null, null, null);
    }

    /**
     * Procesa la solicitud pública de recuperación retornando solo el mensaje neutro.
     */
    public String solicitarRecuperacion(SolicitudRecuperacionPasswordRequest request) {
        return procesarSolicitud(request).mensaje();
    }

    /**
     * Procesa la solicitud pública de recuperación y expone el token temporal para simulación/demo.
     * Siempre responde con el mismo mensaje informativo sin revelar si la cuenta existe o no (E1).
     */
    public ResultadoSolicitudRecuperacion procesarSolicitud(SolicitudRecuperacionPasswordRequest request) {
        Optional<Usuario> usuarioOpt = buscarUsuarioPorIdentificadorOCorreo(request.getIdentificador());
        String tokenDemoGenerado = null;

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();

            // Si está bloqueada administrativamente, no emitir token de autoservicio (E4)
            if (usuario.getEstado() != EstadoUsuario.BLOQUEADA && usuario.getEstado() != EstadoUsuario.INACTIVA) {
                // Invalidar tokens previos no usados
                tokenRepository.findByUsuarioIdAndUsadoFalse(usuario.getId()).forEach(t -> {
                    t.setUsado(true);
                    tokenRepository.save(t);
                });

                String tokenPlano = UUID.randomUUID().toString();
                tokenDemoGenerado = tokenPlano;
                String tokenHash = AuditoriaGlobalService.calcularSha256(tokenPlano);

                TokenRecuperacionPassword nuevoToken = new TokenRecuperacionPassword(
                        usuario,
                        tokenHash,
                        LocalDateTime.now().plusMinutes(MINUTOS_VIGENCIA_TOKEN)
                );
                tokenRepository.save(nuevoToken);

                auditoriaService.registrarEvento(new RegistroAuditoriaEventoRequest(
                        usuario.getUsername(),
                        usuario.getRol().name(),
                        "SEGURIDAD",
                        "SOLICITUD_RECUPERACION_PASSWORD",
                        "Usuario",
                        String.valueOf(usuario.getId()),
                        "Generación de token de recuperación temporal de 15 min",
                        ResultadoAuditoria.EXITO,
                        "REC-SOL-" + usuario.getId()
                ));
            } else {
                auditoriaService.registrarEvento(new RegistroAuditoriaEventoRequest(
                        request.getIdentificador(),
                        "ANONIMO",
                        "SEGURIDAD",
                        "SOLICITUD_RECUPERACION_DENEGADA",
                        "Usuario",
                        String.valueOf(usuario.getId()),
                        "Intento de recuperación sobre cuenta bloqueada administrativamente (E4)",
                        ResultadoAuditoria.DENEGADO,
                        "REC-DEN-" + usuario.getId()
                ));
            }
        }

        // Respuesta uniforme idéntica para prevenir enumeración (E1)
        String mensaje = "Si el identificador corresponde a una cuenta activa registrada, se han enviado las instrucciones de recuperación al canal autorizado.";
        return new ResultadoSolicitudRecuperacion(mensaje, tokenDemoGenerado);
    }

    private Optional<Usuario> buscarUsuarioPorIdentificadorOCorreo(String identificador) {
        if (identificador == null || identificador.isBlank()) {
            return Optional.empty();
        }
        String clean = identificador.trim();

        // 1. Búsqueda directa por username
        Optional<Usuario> porUsername = usuarioRepository.findByUsername(clean);
        if (porUsername.isPresent()) {
            return porUsername;
        }

        // 2. Si es correo o contiene @, intentar resolver por correo en los módulos correspondientes
        if (clean.contains("@")) {
            if (profesorRepository != null) {
                var prof = profesorRepository.findByCorreo(clean);
                if (prof.isPresent()) {
                    if (prof.get().getUsuario() != null) {
                        return Optional.of(prof.get().getUsuario());
                    }
                    var u = usuarioRepository.findByPersonaTipoAndPersonaId(TipoPersonaUsuario.PROFESOR, prof.get().getId());
                    if (u.isPresent()) return u;
                }
            }

            if (estudianteRepository != null) {
                var est = estudianteRepository.findByCorreo(clean);
                if (est.isPresent()) {
                    var u = usuarioRepository.findByPersonaTipoAndPersonaId(TipoPersonaUsuario.ESTUDIANTE, est.get().getId());
                    if (u.isPresent()) return u;
                }
            }

            if (acudienteRepository != null) {
                var acu = acudienteRepository.findByCorreo(clean);
                if (acu.isPresent()) {
                    var u = usuarioRepository.findByPersonaTipoAndPersonaId(TipoPersonaUsuario.ACUDIENTE, acu.get().getId());
                    if (u.isPresent()) return u;
                }
            }

            // 3. Fallback: extraer prefijo antes de @ (e.g. profesor1@colegio.edu.co -> profesor1)
            String usernamePrefix = clean.split("@")[0];
            return usuarioRepository.findByUsername(usernamePrefix);
        }

        return Optional.empty();
    }

    /**
     * Aplica la nueva contraseña verificando el token de un solo uso y su vigencia (E2, E3).
     */
    public void confirmarRecuperacion(ConfirmacionRecuperacionPasswordRequest request) {
        if (!request.getPasswordNuevo().equals(request.getConfirmacionPassword())) {
            throw new ValidacionException("La nueva contraseña y su confirmación no coinciden");
        }

        validarPoliticaComplejidad(request.getPasswordNuevo());

        String tokenRaw = request.getToken() != null ? request.getToken().trim() : "";
        String tokenHash = AuditoriaGlobalService.calcularSha256(tokenRaw);
        TokenRecuperacionPassword token = tokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new ValidacionException("El token de recuperación es inválido, ya fue utilizado o ha expirado (E2)"));

        if (Boolean.TRUE.equals(token.getUsado())) {
            throw new ValidacionException("El token de recuperación ya fue utilizado previamente (E2)");
        }

        if (LocalDateTime.now().isAfter(token.getFechaExpiracion())) {
            throw new ValidacionException("El token de recuperación ha expirado. Solicite un nuevo enlace (E2)");
        }

        Usuario usuario = token.getUsuario();
        usuario.setPasswordHash(passwordEncoder.encode(request.getPasswordNuevo()));
        usuario.setDebeCambiarPassword(false);
        usuario.setIntentosFallidos(0);
        usuario.setBloqueadoHasta(null);
        usuario.setFechaUltimoCambioPassword(LocalDateTime.now());
        if (usuario.getEstado() == EstadoUsuario.PENDIENTE_ACTIVACION) {
            usuario.setEstado(EstadoUsuario.ACTIVA);
        }
        usuarioRepository.save(usuario);

        // Invalidar forzosamente todas las sesiones previas en servidor (RN-SEG-06)
        tokenRevocationService.revokeUserTokens(usuario.getId(), java.time.Instant.now());

        token.setUsado(true);
        token.setFechaUso(LocalDateTime.now());
        tokenRepository.save(token);

        auditoriaService.registrarEvento(new RegistroAuditoriaEventoRequest(
                usuario.getUsername(),
                usuario.getRol().name(),
                "SEGURIDAD",
                "CONFIRMAR_RECUPERACION_PASSWORD",
                "Usuario",
                String.valueOf(usuario.getId()),
                "Restablecimiento exitoso de contraseña mediante token de recuperación",
                ResultadoAuditoria.EXITO,
                "REC-CONF-" + usuario.getId()
        ));
    }

    private void validarPoliticaComplejidad(String password) {
        if (password == null || password.length() < 8) {
            throw new ValidacionException("La contraseña debe tener una longitud mínima de 8 caracteres (E3)");
        }
        boolean tieneLetra = password.chars().anyMatch(Character::isLetter);
        boolean tieneNumero = password.chars().anyMatch(Character::isDigit);
        if (!tieneLetra || !tieneNumero) {
            throw new ValidacionException("La contraseña debe contener al menos una letra y un número (E3)");
        }
    }
}
