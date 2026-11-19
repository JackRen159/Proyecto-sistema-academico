package com.jackren.sistemaacademico.service;

import com.jackren.sistemaacademico.dto.*;
import com.jackren.sistemaacademico.entity.*;
import com.jackren.sistemaacademico.exception.ConflictoException;
import com.jackren.sistemaacademico.exception.RecursoNoEncontradoException;
import com.jackren.sistemaacademico.exception.ValidacionException;
import com.jackren.sistemaacademico.repository.*;
import com.jackren.sistemaacademico.security.TokenRevocationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Servicio de administración de cuentas de usuario, asignación de roles y órganos colegiados (UC-PROV-01, UC-PROV-02, UC-PROV-03 A2).
 */
@Service
@Transactional
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioRolRepository usuarioRolRepository;
    private final OrganoColegiadoMiembroRepository organoRepository;
    private final AnioLectivoRepository anioLectivoRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditoriaGlobalService auditoriaService;
    private final TokenRevocationService tokenRevocationService;
    private final AcudienteRepository acudienteRepository;
    private final EstudianteAcudienteRepository estudianteAcudienteRepository;
    private final ParametroInstitucionalRepository parametroRepository;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          UsuarioRolRepository usuarioRolRepository,
                          OrganoColegiadoMiembroRepository organoRepository,
                          AnioLectivoRepository anioLectivoRepository,
                          PasswordEncoder passwordEncoder,
                          AuditoriaGlobalService auditoriaService,
                          TokenRevocationService tokenRevocationService,
                          AcudienteRepository acudienteRepository,
                          EstudianteAcudienteRepository estudianteAcudienteRepository,
                          ParametroInstitucionalRepository parametroRepository) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioRolRepository = usuarioRolRepository;
        this.organoRepository = organoRepository;
        this.anioLectivoRepository = anioLectivoRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditoriaService = auditoriaService;
        this.tokenRevocationService = tokenRevocationService;
        this.acudienteRepository = acudienteRepository;
        this.estudianteAcudienteRepository = estudianteAcudienteRepository;
        this.parametroRepository = parametroRepository;
    }

    public UsuarioResponse crearUsuario(CrearUsuarioRequest request, String autorizador) {
        if (usuarioRepository.existsByUsername(request.getUsername())) {
            throw new ConflictoException("Ya existe una cuenta con el nombre de usuario: " + request.getUsername() + " (E1)");
        }

        // Validación de precondición: Vínculo previo para Acudiente (RN-SEG-02, E2)
        if (request.getRol() == Rol.ACUDIENTE) {
            if (request.getPersonaId() == null) {
                throw new ConflictoException("Para crear una cuenta de rol ACUDIENTE se requiere asociar el personaId del acudiente registrado (RN-SEG-02, E2).");
            }
            Acudiente acudiente = acudienteRepository.findById(request.getPersonaId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Acudiente no encontrado con ID: " + request.getPersonaId()));
            if (!estudianteAcudienteRepository.existsByAcudiente(acudiente)) {
                throw new ConflictoException("Prohibido aprovisionar cuenta de acudiente sin vínculo familiar o legal validado con al menos un estudiante (RN-SEG-02, E2).");
            }
        }

        // Validación de precondición: Habilitación de login para Estudiantes por política institucional (RN-SEG-08, E2)
        if (request.getRol() == Rol.ESTUDIANTE) {
            parametroRepository.findByClave("LOGIN_ESTUDIANTES_HABILITADO").ifPresent(p -> {
                if ("false".equalsIgnoreCase(p.getValor())) {
                    throw new ConflictoException("El acceso directo al sistema para estudiantes se encuentra deshabilitado por política institucional (RN-SEG-08, E2).");
                }
            });
        }

        // Generar contraseña temporal si no se suministra
        String passwordPlana = (request.getPassword() != null && !request.getPassword().isBlank())
                ? request.getPassword()
                : generarPasswordTemporal();

        String passwordHash = passwordEncoder.encode(passwordPlana);

        Usuario usuario = new Usuario();
        usuario.setUsername(request.getUsername());
        usuario.setPasswordHash(passwordHash);
        usuario.setRol(request.getRol());
        usuario.setEstado(EstadoUsuario.PENDIENTE_ACTIVACION);
        usuario.setDebeCambiarPassword(request.getDebeCambiarPassword() != null ? request.getDebeCambiarPassword() : true);
        usuario.setPersonaTipo(request.getPersonaTipo());
        usuario.setPersonaId(request.getPersonaId());
        usuario.setActivo(true);

        Usuario guardado = usuarioRepository.save(usuario);

        // Crear registro principal en usuario_roles
        UsuarioRol usuarioRol = new UsuarioRol(guardado, request.getRol(), true);
        usuarioRol.setMotivoAsignacion("Rol inicial al aprovisionar cuenta");
        usuarioRolRepository.save(usuarioRol);

        auditoriaService.registrarEvento(new RegistroAuditoriaEventoRequest(
                autorizador != null ? autorizador : "SISTEMA",
                "ADMINISTRADOR",
                "SEGURIDAD",
                "CREAR_USUARIO",
                "Usuario",
                String.valueOf(guardado.getId()),
                "Creación de cuenta en estado PENDIENTE_ACTIVACION",
                ResultadoAuditoria.EXITO,
                "USU-CREAR-" + guardado.getId()
        ));

        return toResponse(guardado);
    }

    public UsuarioResponse cambiarEstado(Long id, ActualizarEstadoUsuarioRequest request, String autorizador) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con ID: " + id));

        if (request.getMotivo() == null || request.getMotivo().isBlank()) {
            throw new ValidacionException("El motivo del cambio de estado es obligatorio (A2)");
        }

        EstadoUsuario estadoAnterior = usuario.getEstado();
        usuario.setEstado(request.getEstado());
        Usuario actualizado = usuarioRepository.save(usuario);

        // Invalidación forzosa inmediata de todas las sesiones activas en servidor al suspender o bloquear (RN-SEG-06)
        if (request.getEstado() == EstadoUsuario.BLOQUEADA || request.getEstado() == EstadoUsuario.INACTIVA) {
            tokenRevocationService.revokeUserTokens(usuario.getId(), Instant.now());
        }

        auditoriaService.registrarEvento(new RegistroAuditoriaEventoRequest(
                autorizador != null ? autorizador : "SISTEMA",
                "ADMINISTRADOR",
                "SEGURIDAD",
                "CAMBIAR_ESTADO_USUARIO",
                "Usuario",
                String.valueOf(usuario.getId()),
                "Cambio de estado de " + estadoAnterior + " a " + request.getEstado() + ". Motivo: " + request.getMotivo(),
                ResultadoAuditoria.EXITO,
                "USU-ESTADO-" + usuario.getId()
        ));

        return toResponse(actualizado);
    }

    public void eliminarUsuario(Long id, String autorizador) {
        // Regla no negociable E4 y RN-SEG-01: Prohibición absoluta de borrado físico de cuentas
        auditoriaService.registrarEvento(new RegistroAuditoriaEventoRequest(
                autorizador != null ? autorizador : "SISTEMA",
                "ADMINISTRADOR",
                "SEGURIDAD",
                "INTENTO_ELIMINACION_USUARIO",
                "Usuario",
                String.valueOf(id),
                "Intento de eliminación física denegado (E4)",
                ResultadoAuditoria.DENEGADO,
                "USU-DEL-DENIED-" + id
        ));

        throw new ConflictoException("Prohibido el borrado físico de cuentas de usuario con trazabilidad institucional (E4, RN-SEG-01). Debe inactivarse o pasarse a estado histórico.");
    }

    public UsuarioRolResponse asignarRol(Long usuarioId, AsignarRolUsuarioRequest request, String autorizador) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con ID: " + usuarioId));

        // Regla de segregación de funciones e incompatibilidad (E1)
        if (usuario.getRol() == Rol.ESTUDIANTE &&
                (request.getRol() == Rol.PROFESOR || request.getRol() == Rol.COORDINADOR ||
                 request.getRol() == Rol.COORDINADOR_ACADEMICO || request.getRol() == Rol.COORDINADOR_CONVIVENCIA ||
                 request.getRol() == Rol.RECTOR || request.getRol() == Rol.ADMINISTRADOR)) {
            throw new ConflictoException("Incompatibilidad de roles: Una cuenta de estudiante no puede acumular funciones docentes o directivas (E1, RN-SEG-04).");
        }

        AnioLectivo anio = null;
        if (request.getAnioLectivoId() != null) {
            anio = anioLectivoRepository.findById(request.getAnioLectivoId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Año lectivo no encontrado"));
        }

        Usuario autorizadorUsuario = autorizador != null ? usuarioRepository.findByUsername(autorizador).orElse(null) : null;

        UsuarioRol usuarioRol = new UsuarioRol();
        usuarioRol.setUsuario(usuario);
        usuarioRol.setRol(request.getRol());
        usuarioRol.setActivo(true);
        usuarioRol.setEsPrincipal(Boolean.TRUE.equals(request.getEsPrincipal()));
        usuarioRol.setFechaInicio(request.getFechaInicio() != null ? request.getFechaInicio() : LocalDate.now());
        usuarioRol.setFechaFin(request.getFechaFin());
        usuarioRol.setAnioLectivo(anio);
        usuarioRol.setMotivoAsignacion(request.getMotivoAsignacion());
        usuarioRol.setAsignadoPor(autorizadorUsuario);

        UsuarioRol guardado = usuarioRolRepository.save(usuarioRol);

        // Si se marca como rol principal, sincronizar la columna usuario.rol (Deuda Técnica D)
        if (Boolean.TRUE.equals(request.getEsPrincipal())) {
            usuario.setRol(request.getRol());
            usuarioRepository.save(usuario);
        }

        auditoriaService.registrarEvento(new RegistroAuditoriaEventoRequest(
                autorizador != null ? autorizador : "SISTEMA",
                "ADMINISTRADOR",
                "SEGURIDAD",
                "ASIGNAR_ROL",
                "UsuarioRol",
                String.valueOf(guardado.getId()),
                "Asignación de rol " + request.getRol() + " a usuario " + usuario.getUsername(),
                ResultadoAuditoria.EXITO,
                "ROL-ASIG-" + guardado.getId()
        ));

        return toRolResponse(guardado);
    }

    public void revocarRol(Long usuarioRolId, String autorizador) {
        UsuarioRol ur = usuarioRolRepository.findById(usuarioRolId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Asignación de rol no encontrada con ID: " + usuarioRolId));

        // Regla E3: Proteger que no se revoque el último administrador o rector activo
        if (ur.getRol() == Rol.ADMINISTRADOR || ur.getRol() == Rol.RECTOR) {
            long adminsActivos = usuarioRolRepository.countByRolAndActivoTrue(ur.getRol());
            if (adminsActivos <= 1) {
                throw new ConflictoException("No es posible revocar el último rol " + ur.getRol() + " activo de la institución (E3).");
            }
        }

        ur.setActivo(false);
        ur.setFechaFin(LocalDate.now());
        usuarioRolRepository.save(ur);

        // Revocar sesiones si el rol revocado era el rol activo en sesión (RN-SEG-06)
        tokenRevocationService.revokeUserTokens(ur.getUsuario().getId(), Instant.now());

        auditoriaService.registrarEvento(new RegistroAuditoriaEventoRequest(
                autorizador != null ? autorizador : "SISTEMA",
                "ADMINISTRADOR",
                "SEGURIDAD",
                "REVOCAR_ROL",
                "UsuarioRol",
                String.valueOf(ur.getId()),
                "Revocación de rol " + ur.getRol() + " para usuario ID " + ur.getUsuario().getId(),
                ResultadoAuditoria.EXITO,
                "ROL-REVOC-" + ur.getId()
        ));
    }

    public OrganoColegiadoMiembroResponse asignarMiembroOrganoColegiado(Long usuarioId, AsignarOrganoColegiadoRequest request, String autorizador) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));

        AnioLectivo anio = anioLectivoRepository.findById(request.getAnioLectivoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Año lectivo no encontrado"));

        Optional<OrganoColegiadoMiembro> existente = organoRepository.findByTipoOrganoAndUsuarioIdAndAnioLectivoId(
                request.getTipoOrgano(), usuarioId, request.getAnioLectivoId());

        if (existente.isPresent() && Boolean.TRUE.equals(existente.get().getActivo())) {
            throw new ConflictoException("El usuario ya es miembro activo de este órgano para el año lectivo (A2).");
        }

        Usuario autorizadorUsuario = autorizador != null ? usuarioRepository.findByUsername(autorizador).orElse(null) : null;

        OrganoColegiadoMiembro miembro = existente.orElseGet(OrganoColegiadoMiembro::new);
        miembro.setUsuario(usuario);
        miembro.setTipoOrgano(request.getTipoOrgano());
        miembro.setActaDesignacion(request.getActaDesignacion());
        miembro.setAnioLectivo(anio);
        miembro.setFechaInicio(request.getFechaInicio() != null ? request.getFechaInicio() : LocalDate.now());
        miembro.setFechaFin(request.getFechaFin());
        miembro.setActivo(true);
        miembro.setMotivo(request.getMotivo());
        miembro.setDesignadoPor(autorizadorUsuario);

        OrganoColegiadoMiembro guardado = organoRepository.save(miembro);

        auditoriaService.registrarEvento(new RegistroAuditoriaEventoRequest(
                autorizador != null ? autorizador : "SISTEMA",
                "RECTOR",
                "SEGURIDAD",
                "DESIGNAR_ORGANO_COLEGIADO",
                "OrganoColegiadoMiembro",
                String.valueOf(guardado.getId()),
                "Designación nominativa en " + request.getTipoOrgano() + " según acta " + request.getActaDesignacion(),
                ResultadoAuditoria.EXITO,
                "ORG-ASIG-" + guardado.getId()
        ));

        return toOrganoResponse(guardado);
    }

    public void removerMiembroOrganoColegiado(Long miembroId, String autorizador) {
        OrganoColegiadoMiembro m = organoRepository.findById(miembroId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Membresía no encontrada con ID: " + miembroId));

        m.setActivo(false);
        m.setFechaFin(LocalDate.now());
        organoRepository.save(m);

        auditoriaService.registrarEvento(new RegistroAuditoriaEventoRequest(
                autorizador != null ? autorizador : "SISTEMA",
                "RECTOR",
                "SEGURIDAD",
                "REMOVER_ORGANO_COLEGIADO",
                "OrganoColegiadoMiembro",
                String.valueOf(m.getId()),
                "Cese de membresía en órgano " + m.getTipoOrgano(),
                ResultadoAuditoria.EXITO,
                "ORG-REM-" + m.getId()
        ));
    }

    /**
     * Restablecimiento presencial asistido en ventanilla de Secretaría Académica (UC-PROV-03 A2).
     */
    public RestablecimientoPresencialResponse restablecerPresencial(Long usuarioId, String autorizador, String motivo) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con ID: " + usuarioId));

        if (motivo == null || motivo.isBlank()) {
            throw new ValidacionException("El motivo del restablecimiento presencial es obligatorio (UC-PROV-03 A2)");
        }

        String passwordTemporal = generarPasswordTemporal();
        usuario.setPasswordHash(passwordEncoder.encode(passwordTemporal));
        usuario.setDebeCambiarPassword(true);
        usuario.setEstado(EstadoUsuario.PENDIENTE_ACTIVACION);
        usuario.setIntentosFallidos(0);
        usuario.setBloqueadoHasta(null);
        usuario.setFechaUltimoCambioPassword(java.time.LocalDateTime.now());
        usuarioRepository.save(usuario);

        // Invalidar de inmediato todas las sesiones activas en servidor (RN-SEG-06)
        tokenRevocationService.revokeUserTokens(usuario.getId(), Instant.now());

        auditoriaService.registrarEvento(new RegistroAuditoriaEventoRequest(
                autorizador != null ? autorizador : "SECRETARIA",
                "SECRETARIA",
                "SEGURIDAD",
                "RESTABLECER_PRESENCIAL_PASSWORD",
                "Usuario",
                String.valueOf(usuario.getId()),
                "Restablecimiento asistido en ventanilla de Secretaría. Motivo: " + motivo,
                ResultadoAuditoria.EXITO,
                "REST-PRES-" + usuario.getId()
        ));

        return new RestablecimientoPresencialResponse(usuario.getId(), usuario.getUsername(), passwordTemporal);
    }

    @Transactional(readOnly = true)
    public Page<UsuarioResponse> listarUsuarios(Pageable pageable) {
        return usuarioRepository.findAll(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public UsuarioResponse obtenerPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con ID: " + id));
        return toResponse(usuario);
    }

    @Transactional(readOnly = true)
    public List<UsuarioRolResponse> listarRolesPorUsuario(Long usuarioId) {
        return usuarioRolRepository.findByUsuarioId(usuarioId).stream()
                .map(this::toRolResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<OrganoColegiadoMiembroResponse> listarOrganosPorUsuario(Long usuarioId) {
        return organoRepository.findByUsuarioId(usuarioId).stream()
                .map(this::toOrganoResponse)
                .toList();
    }

    private String generarPasswordTemporal() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789!@#$%";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(10);
        for (int i = 0; i < 10; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    public UsuarioResponse toResponse(Usuario u) {
        List<Rol> roles = usuarioRolRepository.findRolesVigentesPorUsuario(u.getId(), LocalDate.now());
        if (roles.isEmpty() && u.getRol() != null) {
            roles = List.of(u.getRol());
        }

        return new UsuarioResponse(
                u.getId(),
                u.getUsername(),
                u.getRol(),
                u.getActivo(),
                u.getEstado(),
                u.getDebeCambiarPassword(),
                roles
        );
    }

    public UsuarioRolResponse toRolResponse(UsuarioRol ur) {
        UsuarioRolResponse r = new UsuarioRolResponse();
        r.setId(ur.getId());
        r.setUsuarioId(ur.getUsuario().getId());
        r.setUsername(ur.getUsuario().getUsername());
        r.setRol(ur.getRol());
        r.setActivo(ur.getActivo());
        r.setEsPrincipal(ur.getEsPrincipal());
        r.setFechaInicio(ur.getFechaInicio());
        r.setFechaFin(ur.getFechaFin());
        r.setAnioLectivoId(ur.getAnioLectivo() != null ? ur.getAnioLectivo().getId() : null);
        r.setMotivoAsignacion(ur.getMotivoAsignacion());
        return r;
    }

    public OrganoColegiadoMiembroResponse toOrganoResponse(OrganoColegiadoMiembro m) {
        OrganoColegiadoMiembroResponse r = new OrganoColegiadoMiembroResponse();
        r.setId(m.getId());
        r.setUsuarioId(m.getUsuario().getId());
        r.setUsername(m.getUsuario().getUsername());
        r.setTipoOrgano(m.getTipoOrgano());
        r.setActaDesignacion(m.getActaDesignacion());
        r.setAnioLectivoId(m.getAnioLectivo().getId());
        r.setAnioLectivoNombre(m.getAnioLectivo().getNombre());
        r.setFechaInicio(m.getFechaInicio());
        r.setFechaFin(m.getFechaFin());
        r.setActivo(m.getActivo());
        r.setMotivo(m.getMotivo());
        return r;
    }
}
