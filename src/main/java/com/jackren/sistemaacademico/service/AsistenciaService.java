package com.jackren.sistemaacademico.service;

import com.jackren.sistemaacademico.dto.*;
import com.jackren.sistemaacademico.entity.*;
import com.jackren.sistemaacademico.exception.ConflictoNegocioException;
import com.jackren.sistemaacademico.exception.RecursoNoEncontradoException;
import com.jackren.sistemaacademico.exception.ValidacionException;
import com.jackren.sistemaacademico.mapper.AsistenciaMapper;
import com.jackren.sistemaacademico.repository.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

@Service
@Transactional
public class AsistenciaService {

    private final ConfiguracionAlertaAsistenciaRepository alertaRepository;
    private final SesionAsistenciaRepository sesionRepository;
    private final RegistroAsistenciaRepository registroRepository;
    private final AuditoriaAsistenciaRepository auditoriaRepository;
    private final JustificacionAsistenciaRepository justificacionRepository;
    private final SolicitudExcusaAsistenciaRepository solicitudExcusaRepository;
    private final AutorizacionSalidaAnticipadaRepository salidaRepository;
    private final AsignacionAcademicaRepository asignacionRepository;
    private final GrupoRepository grupoRepository;
    private final PeriodoAcademicoRepository periodoRepository;
    private final AnioLectivoRepository anioLectivoRepository;
    private final MatriculaRepository matriculaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProfesorRepository profesorRepository;
    private final EstudianteRepository estudianteRepository;
    private final EstudianteAcudienteRepository estudianteAcudienteRepository;
    private final DiaNoLectivoRepository diaNoLectivoRepository;
    private final ConfiguracionSieeVersionRepository sieeVersionRepository;
    private final AsistenciaMapper asistenciaMapper;

    public AsistenciaService(ConfiguracionAlertaAsistenciaRepository alertaRepository,
                             SesionAsistenciaRepository sesionRepository,
                             RegistroAsistenciaRepository registroRepository,
                             AuditoriaAsistenciaRepository auditoriaRepository,
                             JustificacionAsistenciaRepository justificacionRepository,
                             SolicitudExcusaAsistenciaRepository solicitudExcusaRepository,
                             AutorizacionSalidaAnticipadaRepository salidaRepository,
                             AsignacionAcademicaRepository asignacionRepository,
                             GrupoRepository grupoRepository,
                             PeriodoAcademicoRepository periodoRepository,
                             AnioLectivoRepository anioLectivoRepository,
                             MatriculaRepository matriculaRepository,
                             UsuarioRepository usuarioRepository,
                             ProfesorRepository profesorRepository,
                             EstudianteRepository estudianteRepository,
                             EstudianteAcudienteRepository estudianteAcudienteRepository,
                             DiaNoLectivoRepository diaNoLectivoRepository,
                             ConfiguracionSieeVersionRepository sieeVersionRepository,
                             AsistenciaMapper asistenciaMapper) {
        this.alertaRepository = alertaRepository;
        this.sesionRepository = sesionRepository;
        this.registroRepository = registroRepository;
        this.auditoriaRepository = auditoriaRepository;
        this.justificacionRepository = justificacionRepository;
        this.solicitudExcusaRepository = solicitudExcusaRepository;
        this.salidaRepository = salidaRepository;
        this.asignacionRepository = asignacionRepository;
        this.grupoRepository = grupoRepository;
        this.periodoRepository = periodoRepository;
        this.anioLectivoRepository = anioLectivoRepository;
        this.matriculaRepository = matriculaRepository;
        this.usuarioRepository = usuarioRepository;
        this.profesorRepository = profesorRepository;
        this.estudianteRepository = estudianteRepository;
        this.estudianteAcudienteRepository = estudianteAcudienteRepository;
        this.diaNoLectivoRepository = diaNoLectivoRepository;
        this.sieeVersionRepository = sieeVersionRepository;
        this.asistenciaMapper = asistenciaMapper;
    }

    // =========================================================================
    // 1. CONFIGURACIÓN DE ALERTAS DE AUSENTISMO
    // =========================================================================

    public ConfiguracionAlertaAsistenciaResponse guardarOActualizarConfiguracionAlerta(
            ConfiguracionAlertaAsistenciaRequest request) {
        if (request.getPorcentajeAlertaAmarilla().compareTo(request.getPorcentajeAlertaRoja()) >= 0) {
            throw new ConflictoNegocioException("El porcentaje de alerta amarilla (" +
                    request.getPorcentajeAlertaAmarilla() + "%) debe ser estrictamente menor al de alerta roja (" +
                    request.getPorcentajeAlertaRoja() + "%)");
        }

        AnioLectivo anioLectivo = anioLectivoRepository.findById(request.getAnioLectivoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Año lectivo no encontrado con ID " + request.getAnioLectivoId()));

        if (anioLectivo.getEstado() == EstadoAnioLectivo.CERRADO) {
            throw new ConflictoNegocioException("No se pueden configurar alertas en un año lectivo cerrado");
        }

        ConfiguracionAlertaAsistencia config = alertaRepository.findByAnioLectivoId(anioLectivo.getId())
                .orElseGet(ConfiguracionAlertaAsistencia::new);

        config.setAnioLectivo(anioLectivo);
        config.setPorcentajeAlertaAmarilla(request.getPorcentajeAlertaAmarilla());
        config.setPorcentajeAlertaRoja(request.getPorcentajeAlertaRoja());
        config.setDescripcion(request.getDescripcion());

        ConfiguracionAlertaAsistencia guardada = alertaRepository.save(config);
        return asistenciaMapper.toResponse(guardada);
    }

    @Transactional(readOnly = true)
    public ConfiguracionAlertaAsistenciaResponse obtenerConfiguracionAlerta(Long anioLectivoId) {
        ConfiguracionAlertaAsistencia config = obtenerConfiguracionAlertaVigente(anioLectivoId);
        return asistenciaMapper.toResponse(config);
    }

    public ConfiguracionAlertaAsistencia obtenerConfiguracionAlertaVigente(Long anioLectivoId) {
        Optional<ConfiguracionAlertaAsistencia> configOpt = alertaRepository.findByAnioLectivoId(anioLectivoId);
        if (configOpt.isPresent()) {
            return configOpt.get();
        }

        // Si no existe tabla legacy, buscar en versión aprobada del SIEE
        Optional<ConfiguracionSieeVersion> sieeOpt = sieeVersionRepository
                .findFirstByAnioLectivoVigenciaIdAndEstadoInOrderByNumeroVersionDesc(
                        anioLectivoId, List.of(EstadoVersionConfiguracion.VIGENTE, EstadoVersionConfiguracion.APROBADA_VIGENCIA_FUTURA)
                );

        if (sieeOpt.isEmpty()) {
            sieeOpt = sieeVersionRepository.findFirstByEstadoOrderByNumeroVersionDesc(EstadoVersionConfiguracion.VIGENTE);
        }

        if (sieeOpt.isPresent() && sieeOpt.get().getUmbralAsistenciaReprobacion() != null) {
            BigDecimal umbralRojo = sieeOpt.get().getUmbralAsistenciaReprobacion();
            BigDecimal umbralAmarillo = umbralRojo.multiply(new BigDecimal("0.60")).setScale(2, RoundingMode.HALF_UP);
            ConfiguracionAlertaAsistencia cfg = new ConfiguracionAlertaAsistencia();
            cfg.setPorcentajeAlertaAmarilla(umbralAmarillo);
            cfg.setPorcentajeAlertaRoja(umbralRojo);
            cfg.setDescripcion("Umbral derivado de la versión del SIEE vigente");
            return cfg;
        }

        // Por defecto para compatibilidad
        ConfiguracionAlertaAsistencia def = new ConfiguracionAlertaAsistencia();
        def.setPorcentajeAlertaAmarilla(new BigDecimal("15.00"));
        def.setPorcentajeAlertaRoja(new BigDecimal("25.00"));
        def.setDescripcion("Configuración estándar institucional");
        return def;
    }

    // =========================================================================
    // 2. GESTIÓN DE SESIONES DE ASISTENCIA (UC-19)
    // =========================================================================

    public SesionAsistenciaResponse crearSesion(SesionAsistenciaRequest request, Long usuarioId) {
        return crearSesion(request, usuarioId, null);
    }

    public SesionAsistenciaResponse crearSesion(SesionAsistenciaRequest request, Long usuarioId, String rol) {
        Grupo grupo = grupoRepository.findById(request.getGrupoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Grupo no encontrado con ID " + request.getGrupoId()));

        PeriodoAcademico periodo = periodoRepository.findById(request.getPeriodoAcademicoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Periodo académico no encontrado con ID " + request.getPeriodoAcademicoId()));

        if (periodo.getEstado() != EstadoPeriodo.ABIERTO) {
            throw new ConflictoNegocioException("Solo se pueden registrar sesiones de asistencia en periodos académicos con estado ABIERTO (UC-19 E4)");
        }

        if (grupo.getAnioLectivo() == null || !grupo.getAnioLectivo().getId().equals(periodo.getAnioLectivo().getId())) {
            throw new ConflictoNegocioException("El periodo académico no corresponde al año lectivo del grupo");
        }

        LocalDate fecha = request.getFecha() != null ? request.getFecha() : LocalDate.now();

        // UC-19 E1 / RN-ASI-01: Validar que no sea día no lectivo del calendario escolar
        if (diaNoLectivoRepository.esDiaNoLectivo(periodo.getAnioLectivo().getId(), fecha)) {
            throw new ConflictoNegocioException("La fecha " + fecha + " corresponde a un día festivo, receso o suspensión de clases no lectivo (RN-ASI-01, UC-19 E1)");
        }

        Integer bloque = request.getBloqueHorario() != null ? request.getBloqueHorario() : 1;
        TipoRegistroAsistencia tipo = request.getTipoRegistro() != null ? request.getTipoRegistro() : TipoRegistroAsistencia.SESION_ASIGNATURA;

        AsignacionAcademica asignacion = null;
        if (tipo == TipoRegistroAsistencia.SESION_ASIGNATURA) {
            if (request.getAsignacionAcademicaId() == null) {
                throw new ConflictoNegocioException("La asignación académica es obligatoria para el registro de asistencia por sesión de asignatura");
            }
            asignacion = asignacionRepository.findById(request.getAsignacionAcademicaId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Asignación académica no encontrada con ID " + request.getAsignacionAcademicaId()));

            if (!Boolean.TRUE.equals(asignacion.getActivo())) {
                throw new ConflictoNegocioException("La asignación académica seleccionada se encuentra inactiva");
            }

            validarDocenteAsignado(asignacion, usuarioId, rol);

            if (!asignacion.getGrupo().getId().equals(grupo.getId())) {
                throw new ConflictoNegocioException("La asignación académica pertenece al grupo '" +
                        asignacion.getGrupo().getNombre() + "', no al grupo '" + grupo.getNombre() + "'");
            }

            Optional<SesionAsistencia> sesionExistente = sesionRepository
                    .findByAsignacionAcademicaIdAndFechaAndBloqueHorario(asignacion.getId(), fecha, bloque);
            if (sesionExistente.isPresent()) {
                throw new ConflictoNegocioException("Ya existe una sesión de clase registrada para la fecha " + fecha +
                        " en el bloque horario " + bloque + " para esta asignatura y grupo");
            }
        } else {
            Optional<SesionAsistencia> sesionDiariaExistente = sesionRepository
                    .findByGrupoIdAndFechaAndTipoRegistro(grupo.getId(), fecha, TipoRegistroAsistencia.DIARIA_JORNADA);
            if (sesionDiariaExistente.isPresent()) {
                throw new ConflictoNegocioException("Ya existe un registro de jornada diaria para el grupo '" +
                        grupo.getNombre() + "' en la fecha " + fecha);
            }
        }

        Usuario usuarioCreador = usuarioId != null ? usuarioRepository.findById(usuarioId).orElse(null) : null;
        SesionAsistencia sesion = asistenciaMapper.toEntity(request, asignacion, grupo, periodo, usuarioCreador);
        SesionAsistencia guardada = sesionRepository.save(sesion);
        return asistenciaMapper.toResponse(guardada);
    }

    @Transactional(readOnly = true)
    public SesionAsistenciaResponse obtenerSesionPorId(Long sesionId) {
        SesionAsistencia sesion = sesionRepository.findById(sesionId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Sesión de asistencia no encontrada con ID " + sesionId));
        return asistenciaMapper.toResponse(sesion);
    }

    @Transactional(readOnly = true)
    public List<SesionAsistenciaResponse> listarSesionesPorGrupoYPeriodo(Long grupoId, Long periodoId) {
        return sesionRepository.findByGrupoIdAndPeriodoAcademicoIdOrderByFechaDescBloqueHorarioDesc(grupoId, periodoId).stream()
                .map(asistenciaMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SesionAsistenciaResponse> listarSesionesPorAsignacionYPeriodo(Long asignacionId, Long periodoId) {
        return sesionRepository.findByAsignacionAcademicaIdAndPeriodoAcademicoIdOrderByFechaDescBloqueHorarioDesc(asignacionId, periodoId).stream()
                .map(asistenciaMapper::toResponse)
                .toList();
    }

    // =========================================================================
    // 3. TOMA DE ASISTENCIA Y MODIFICACIÓN AUDITADA (UC-19)
    // =========================================================================

    public List<RegistroAsistenciaResponse> registrarPlanillaLote(PlanillaAsistenciaLoteRequest request) {
        return registrarPlanillaLote(request, null, null, null);
    }

    public List<RegistroAsistenciaResponse> registrarPlanillaLote(PlanillaAsistenciaLoteRequest request,
                                                                 Long usuarioId,
                                                                 String username,
                                                                 String rol) {
        SesionAsistencia sesion = sesionRepository.findById(request.getSesionId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Sesión de asistencia no encontrada con ID " + request.getSesionId()));

        if (sesion.getPeriodoAcademico().getEstado() != EstadoPeriodo.ABIERTO) {
            throw new ConflictoNegocioException("No se pueden registrar asistencias en un periodo académico cerrado (UC-19 E4)");
        }

        if (sesion.getAsignacionAcademica() != null) {
            validarDocenteAsignado(sesion.getAsignacionAcademica(), usuarioId, rol);
        }

        Grupo grupoSesion = sesion.getGrupo();
        LocalDate fechaSesion = sesion.getFecha();
        Usuario usuario = usuarioId != null ? usuarioRepository.findById(usuarioId).orElse(null) : null;
        String userStr = username != null ? username : (usuario != null ? usuario.getUsername() : "sistema");

        List<RegistroAsistenciaResponse> responses = new ArrayList<>();

        for (RegistroAsistenciaItemRequest item : request.getRegistros()) {
            Matricula matricula = matriculaRepository.findById(item.getMatriculaId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Matrícula no encontrada con ID " + item.getMatriculaId()));

            if (matricula.getEstado() != EstadoMatricula.MATRICULADA) {
                throw new ConflictoNegocioException("El estudiante con matrícula " + matricula.getNumeroMatricula() +
                        " no se encuentra en estado MATRICULADA");
            }

            if (!matricula.getGrupo().getId().equals(grupoSesion.getId())) {
                throw new ConflictoNegocioException("La matrícula " + matricula.getNumeroMatricula() +
                        " pertenece al grupo '" + matricula.getGrupo().getNombre() + "', no al grupo de la sesión ('" +
                        grupoSesion.getNombre() + "')");
            }

            // UC-19 E2: Coherencia temporal con fecha de inicio y retiro de matrícula
            if (matricula.getFechaEfectivaInicio() != null && fechaSesion.isBefore(matricula.getFechaEfectivaInicio())) {
                throw new ConflictoNegocioException("No se puede registrar asistencia en fecha anterior a la matrícula efectiva (RN-ASI-02)");
            }
            if (matricula.getFechaEfectivaRetiro() != null && fechaSesion.isAfter(matricula.getFechaEfectivaRetiro())) {
                throw new ConflictoNegocioException("No se puede registrar asistencia en fecha posterior al retiro del estudiante (RN-ASI-02)");
            }

            EstadoAsistencia estadoFinal = item.getEstado();

            // UC-19 A2: Detección automática de Evasión Interna de Clase (Ley 1098, RN-ASI-07)
            if (estadoFinal == EstadoAsistencia.AUSENTE_INJUSTIFICADA) {
                List<RegistroAsistencia> registrosDelDia = registroRepository
                        .findByMatriculaIdAndSesionAsistenciaFecha(matricula.getId(), fechaSesion);

                boolean estuvoPresentePreviamente = registrosDelDia.stream()
                        .anyMatch(r -> r.getEstado() == EstadoAsistencia.PRESENTE &&
                                !r.getSesionAsistencia().getId().equals(sesion.getId()));

                Optional<AutorizacionSalidaAnticipada> salidaOpt = salidaRepository
                        .findByMatriculaIdAndFecha(matricula.getId(), fechaSesion);

                if (estuvoPresentePreviamente && salidaOpt.isEmpty()) {
                    estadoFinal = EstadoAsistencia.EVASION_INTERNA_CLASE;
                }
            }

            // Pre-cargar si tiene permiso de salida anticipada autorizada
            Optional<AutorizacionSalidaAnticipada> salidaAutorizada = salidaRepository
                    .findByMatriculaIdAndFecha(matricula.getId(), fechaSesion);
            if (salidaAutorizada.isPresent() &&
                    salidaAutorizada.get().getEstado() == EstadoAutorizacionSalida.AUTORIZADA_VERIFICADA &&
                    estadoFinal == EstadoAsistencia.AUSENTE_INJUSTIFICADA) {
                estadoFinal = EstadoAsistencia.SALIDA_ANTICIPADA_AUTORIZADA;
            }

            // Pre-cargar si tiene excusa aprobada que cubre la fecha
            List<SolicitudExcusaAsistencia> excusasAprobadas = solicitudExcusaRepository
                    .findAprobadasCubriendoFecha(matricula.getId(), fechaSesion);
            if (!excusasAprobadas.isEmpty() && estadoFinal == EstadoAsistencia.AUSENTE_INJUSTIFICADA) {
                estadoFinal = EstadoAsistencia.AUSENTE_JUSTIFICADA;
            }

            Optional<RegistroAsistencia> existenteOpt = registroRepository
                    .findBySesionAsistenciaIdAndMatriculaId(sesion.getId(), matricula.getId());

            RegistroAsistencia registro;
            if (existenteOpt.isPresent()) {
                registro = existenteOpt.get();
                EstadoAsistencia estadoAnterior = registro.getEstado();

                // Error D / UC-19 E3 / RN-10: Justificación obligatoria en modificación con auditoría inmutable
                if (estadoAnterior != estadoFinal) {
                    if (item.getMotivoCorreccion() == null || item.getMotivoCorreccion().trim().isEmpty()) {
                        throw new ValidacionException("La modificación de la asistencia para la matrícula " +
                                matricula.getNumeroMatricula() + " exige justificación obligatoria (RN-10, UC-19 E3)");
                    }

                    AuditoriaAsistencia auditoria = new AuditoriaAsistencia(
                            registro,
                            matricula,
                            estadoAnterior,
                            estadoFinal,
                            item.getMotivoCorreccion(),
                            usuario,
                            userStr
                    );
                    auditoriaRepository.save(auditoria);
                }

                registro.setEstado(estadoFinal);
                registro.setMinutosRetardo(item.getMinutosRetardo() != null ? item.getMinutosRetardo() : 0);
                registro.setObservaciones(item.getObservaciones());
            } else {
                item.setEstado(estadoFinal);
                registro = asistenciaMapper.toEntity(item, sesion, matricula);
            }

            RegistroAsistencia guardado = registroRepository.save(registro);
            Optional<JustificacionAsistencia> justOpt = justificacionRepository.findByRegistroAsistenciaId(guardado.getId());
            responses.add(asistenciaMapper.toResponse(guardado, justOpt.orElse(null)));
        }

        return responses;
    }

    @Transactional(readOnly = true)
    public List<RegistroAsistenciaResponse> listarRegistrosPorSesion(Long sesionId) {
        return registroRepository.findBySesionAsistenciaId(sesionId).stream()
                .map(r -> {
                    Optional<JustificacionAsistencia> justOpt = justificacionRepository.findByRegistroAsistenciaId(r.getId());
                    return asistenciaMapper.toResponse(r, justOpt.orElse(null));
                })
                .toList();
    }

    // =========================================================================
    // 4. JUSTIFICACIÓN DE INASISTENCIA (LEGACY COMPATIBILITY)
    // =========================================================================

    public JustificacionAsistenciaResponse justificarInasistencia(Long registroId,
                                                                  JustificacionAsistenciaRequest request,
                                                                  Long usuarioId,
                                                                  String username) {
        RegistroAsistencia registro = registroRepository.findById(registroId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Registro de asistencia no encontrado con ID " + registroId));

        if (registro.getEstado() != EstadoAsistencia.AUSENTE_INJUSTIFICADA &&
                registro.getEstado() != EstadoAsistencia.RETARDO &&
                registro.getEstado() != EstadoAsistencia.EVASION_INTERNA_CLASE) {
            throw new ConflictoNegocioException("Solo se pueden radicar justificaciones para registros con falta injustificada, retardo o evasión (estado actual: " +
                    registro.getEstado() + ")");
        }

        if (justificacionRepository.findByRegistroAsistenciaId(registroId).isPresent()) {
            throw new ConflictoNegocioException("El registro de asistencia ya cuenta con una justificación radicada previamente");
        }

        Usuario usuario = usuarioId != null ? usuarioRepository.findById(usuarioId).orElse(null) : null;
        String userStr = username != null ? username : (usuario != null ? usuario.getUsername() : "coordinacion");

        JustificacionAsistencia justificacion = new JustificacionAsistencia(
                registro,
                request.getMotivo(),
                request.getNumeroRadicadoSoporte(),
                request.getFechaRadicacion() != null ? request.getFechaRadicacion() : LocalDate.now(),
                usuario,
                userStr,
                request.getObservaciones()
        );
        JustificacionAsistencia guardada = justificacionRepository.save(justificacion);

        if (registro.getEstado() == EstadoAsistencia.AUSENTE_INJUSTIFICADA ||
                registro.getEstado() == EstadoAsistencia.EVASION_INTERNA_CLASE) {
            registro.setEstado(EstadoAsistencia.AUSENTE_JUSTIFICADA);
            registroRepository.save(registro);
        }

        return asistenciaMapper.toResponse(guardada);
    }

    // =========================================================================
    // 5. RESÚMENES, PORCENTAJES Y CONSULTAS CONTEXTUALES (UC-03 y UC-09)
    // =========================================================================

    @Transactional(readOnly = true)
    public ResumenAsistenciaEstudianteResponse obtenerResumenEstudianteAsignatura(Long matriculaId,
                                                                                 Long periodoId,
                                                                                 Long asignacionId) {
        Matricula matricula = matriculaRepository.findById(matriculaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Matrícula no encontrada con ID " + matriculaId));

        PeriodoAcademico periodo = periodoRepository.findById(periodoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Periodo académico no encontrado con ID " + periodoId));

        AsignacionAcademica asignacion = asignacionRepository.findById(asignacionId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Asignación académica no encontrada con ID " + asignacionId));

        long totalSesiones = registroRepository.countTotalSesionesEstudianteAsignatura(matriculaId, periodoId, asignacionId);
        long totalPresente = registroRepository.countByMatriculaAndPeriodoAndAsignacionAndEstado(matriculaId, periodoId, asignacionId, EstadoAsistencia.PRESENTE);
        long totalAusInjust = registroRepository.countByMatriculaAndPeriodoAndAsignacionAndEstado(matriculaId, periodoId, asignacionId, EstadoAsistencia.AUSENTE_INJUSTIFICADA);
        long totalAusJust = registroRepository.countByMatriculaAndPeriodoAndAsignacionAndEstado(matriculaId, periodoId, asignacionId, EstadoAsistencia.AUSENTE_JUSTIFICADA);
        long totalRetardos = registroRepository.countByMatriculaAndPeriodoAndAsignacionAndEstado(matriculaId, periodoId, asignacionId, EstadoAsistencia.RETARDO);
        long totalRetiros = registroRepository.countByMatriculaAndPeriodoAndAsignacionAndEstado(matriculaId, periodoId, asignacionId, EstadoAsistencia.RETIRO_ANTICIPADO);

        BigDecimal pctInasistencia = BigDecimal.ZERO;
        BigDecimal pctAsistencia = new BigDecimal("100.00");

        if (totalSesiones > 0) {
            pctInasistencia = BigDecimal.valueOf(totalAusInjust)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(totalSesiones), 2, RoundingMode.HALF_UP);
            pctAsistencia = BigDecimal.valueOf(100).subtract(pctInasistencia);
        }

        ConfiguracionAlertaAsistencia config = obtenerConfiguracionAlertaVigente(periodo.getAnioLectivo().getId());

        String nivelAlerta = "NINGUNA";
        if (config.esAlertaRoja(pctInasistencia)) {
            nivelAlerta = "ROJA";
        } else if (config.esAlertaAmarilla(pctInasistencia)) {
            nivelAlerta = "AMARILLA";
        }

        ResumenAsistenciaEstudianteResponse res = new ResumenAsistenciaEstudianteResponse();
        res.setMatriculaId(matricula.getId());
        res.setNumeroMatricula(matricula.getNumeroMatricula());
        if (matricula.getEstudiante() != null) {
            res.setEstudianteId(matricula.getEstudiante().getId());
            res.setEstudianteNombreCompleto(matricula.getEstudiante().getNombres() + " " + matricula.getEstudiante().getApellidos());
            res.setEstudianteDocumento(matricula.getEstudiante().getDocumento());
        }
        res.setGrupoId(matricula.getGrupo().getId());
        res.setGrupoNombre(matricula.getGrupo().getNombre());
        res.setAsignaturaId(asignacion.getAsignatura().getId());
        res.setAsignaturaNombre(asignacion.getAsignatura().getNombre());
        res.setPeriodoAcademicoId(periodo.getId());
        res.setPeriodoNombre(periodo.getNombre());
        res.setTotalSesiones(totalSesiones);
        res.setTotalPresente(totalPresente);
        res.setTotalAusenciasInjustificadas(totalAusInjust);
        res.setTotalAusenciasJustificadas(totalAusJust);
        res.setTotalRetardos(totalRetardos);
        res.setTotalRetirosAnticipados(totalRetiros);
        res.setPorcentajeAsistencia(pctAsistencia);
        res.setPorcentajeInasistencia(pctInasistencia);
        res.setNivelAlerta(nivelAlerta);
        return res;
    }

    @Transactional(readOnly = true)
    public List<ResumenAsistenciaEstudianteResponse> listarAlertasPorGrupoYPeriodo(Long grupoId,
                                                                                  Long periodoId,
                                                                                  Long asignacionId) {
        List<Matricula> matriculados = matriculaRepository.findByGrupoIdAndEstado(grupoId, EstadoMatricula.MATRICULADA);
        List<ResumenAsistenciaEstudianteResponse> alertas = new ArrayList<>();

        for (Matricula m : matriculados) {
            ResumenAsistenciaEstudianteResponse resumen = obtenerResumenEstudianteAsignatura(m.getId(), periodoId, asignacionId);
            if (!"NINGUNA".equals(resumen.getNivelAlerta())) {
                alertas.add(resumen);
            }
        }

        return alertas;
    }

    // UC-03: Consulta contextual segura del estudiante
    @Transactional(readOnly = true)
    public ConsultaAsistenciaEstudianteResponse consultarMisAsistenciasEstudiante(Long usuarioId, Long periodoId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con ID " + usuarioId));

        if (usuario.getPersonaTipo() != TipoPersonaUsuario.ESTUDIANTE || usuario.getPersonaId() == null) {
            throw new AccessDeniedException("El usuario autenticado no posee perfil de Estudiante activo (UC-03 E1)");
        }

        Estudiante est = estudianteRepository.findById(usuario.getPersonaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Expediente de estudiante no encontrado"));

        return construirConsultaAsistencia(est, periodoId);
    }

    // UC-09: Consulta contextual segura del acudiente con validación de vínculo y restricciones
    @Transactional(readOnly = true)
    public ConsultaAsistenciaEstudianteResponse consultarAsistenciasAcudiente(Long usuarioId, Long estudianteId, Long periodoId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con ID " + usuarioId));

        if (usuario.getPersonaTipo() != TipoPersonaUsuario.ACUDIENTE || usuario.getPersonaId() == null) {
            throw new AccessDeniedException("El usuario autenticado no posee perfil de Acudiente activo (UC-09 E1)");
        }

        Estudiante est = estudianteRepository.findById(estudianteId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Estudiante no encontrado con ID " + estudianteId));

        List<EstudianteAcudiente> vinculos = estudianteAcudienteRepository.findByEstudiante(est);
        boolean vinculoValido = vinculos.stream().anyMatch(v ->
                v.getAcudiente().getId().equals(usuario.getPersonaId()) &&
                        v.getEstadoVinculo() == EstadoVinculo.VERIFICADO
        );

        if (!vinculoValido) {
            throw new AccessDeniedException("Acceso denegado: el acudiente no posee un vínculo verificado y activo con el menor o registra restricción legal (UC-09 E1)");
        }

        return construirConsultaAsistencia(est, periodoId);
    }

    private ConsultaAsistenciaEstudianteResponse construirConsultaAsistencia(Estudiante est, Long periodoId) {
        PeriodoAcademico periodo = periodoRepository.findById(periodoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Periodo no encontrado con ID " + periodoId));

        Matricula matricula = matriculaRepository
                .findByEstudianteIdAndAnioLectivoIdAndEstado(est.getId(), periodo.getAnioLectivo().getId(), EstadoMatricula.MATRICULADA)
                .orElseThrow(() -> new ConflictoNegocioException("El estudiante no registra matrícula activa para el periodo consultado (UC-03 E2)"));

        List<RegistroAsistencia> registros = registroRepository
                .findByMatriculaIdAndSesionAsistenciaPeriodoAcademicoId(matricula.getId(), periodo.getId());

        long totalSesiones = registros.size();
        long presentes = 0;
        long ausInjust = 0;
        long ausJust = 0;
        long retardos = 0;
        long evasiones = 0;

        List<ConsultaAsistenciaEstudianteResponse.SesionAsistenciaItem> sesionItems = new ArrayList<>();

        for (RegistroAsistencia r : registros) {
            switch (r.getEstado()) {
                case PRESENTE -> presentes++;
                case AUSENTE_INJUSTIFICADA -> ausInjust++;
                case AUSENTE_JUSTIFICADA, SALIDA_ANTICIPADA_AUTORIZADA -> ausJust++;
                case RETARDO -> retardos++;
                case EVASION_INTERNA_CLASE -> evasiones++;
                default -> {}
            }

            ConsultaAsistenciaEstudianteResponse.SesionAsistenciaItem item = new ConsultaAsistenciaEstudianteResponse.SesionAsistenciaItem();
            item.setSesionId(r.getSesionAsistencia().getId());
            item.setFecha(r.getSesionAsistencia().getFecha());
            item.setBloqueHorario(r.getSesionAsistencia().getBloqueHorario());
            if (r.getSesionAsistencia().getAsignacionAcademica() != null) {
                item.setAsignaturaNombre(r.getSesionAsistencia().getAsignacionAcademica().getAsignatura().getNombre());
            }
            item.setEstado(r.getEstado());
            item.setMinutosRetardo(r.getMinutosRetardo());
            item.setObservaciones(r.getObservaciones());
            sesionItems.add(item);
        }

        BigDecimal pctInasistencia = BigDecimal.ZERO;
        BigDecimal pctAsistencia = new BigDecimal("100.00");
        long fallasComputables = ausInjust + evasiones;

        if (totalSesiones > 0) {
            pctInasistencia = BigDecimal.valueOf(fallasComputables)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(totalSesiones), 2, RoundingMode.HALF_UP);
            pctAsistencia = BigDecimal.valueOf(100).subtract(pctInasistencia);
        }

        ConfiguracionAlertaAsistencia config = obtenerConfiguracionAlertaVigente(periodo.getAnioLectivo().getId());
        String nivelAlerta = "NINGUNA";
        if (config.esAlertaRoja(pctInasistencia)) {
            nivelAlerta = "ROJA";
        } else if (config.esAlertaAmarilla(pctInasistencia)) {
            nivelAlerta = "AMARILLA";
        }

        ConsultaAsistenciaEstudianteResponse resp = new ConsultaAsistenciaEstudianteResponse();
        resp.setMatriculaId(matricula.getId());
        resp.setNumeroMatricula(matricula.getNumeroMatricula());
        resp.setEstudianteId(est.getId());
        resp.setEstudianteNombreCompleto(est.getNombres() + " " + est.getApellidos());
        resp.setAnioLectivoId(periodo.getAnioLectivo().getId());
        resp.setAnioLectivo(periodo.getAnioLectivo().getAnio());
        resp.setPeriodoAcademicoId(periodo.getId());
        resp.setPeriodoNombre(periodo.getNombre());
        resp.setTotalSesiones(totalSesiones);
        resp.setTotalPresente(presentes);
        resp.setTotalAusenciasInjustificadas(ausInjust);
        resp.setTotalAusenciasJustificadas(ausJust);
        resp.setTotalRetardos(retardos);
        resp.setTotalEvasiones(evasiones);
        resp.setPorcentajeAsistencia(pctAsistencia);
        resp.setPorcentajeInasistencia(pctInasistencia);
        resp.setNivelAlerta(nivelAlerta);
        resp.setSesiones(sesionItems);

        // Excusas
        resp.setExcusas(solicitudExcusaRepository.findByMatriculaIdOrderByFechaRadicacionDesc(matricula.getId())
                .stream().map(asistenciaMapper::toResponse).toList());

        return resp;
    }

    // =========================================================================
    // 6. SÁBANA GRUPAL Y CONTROL DE TOMA DE ASISTENCIA (UC-PROV-19)
    // =========================================================================

    @Transactional(readOnly = true)
    public SabanaAsistenciaGrupalResponse consultarSabanaGrupal(Long grupoId, Long periodoId, Long usuarioId, String rol) {
        Grupo grupo = grupoRepository.findById(grupoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Grupo no encontrado con ID " + grupoId));

        PeriodoAcademico periodo = periodoRepository.findById(periodoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Periodo no encontrado con ID " + periodoId));

        // UC-PROV-19 E1: Director de grupo contextual
        if ("PROFESOR".equalsIgnoreCase(rol)) {
            Profesor docente = profesorRepository.findByUsuarioId(usuarioId)
                    .orElseThrow(() -> new AccessDeniedException("Perfil de profesor no encontrado"));

            Optional<AsignacionAcademica> dirOpt = asignacionRepository
                    .findByGrupoIdAndEsDirectorGrupoTrueAndActivoTrue(grupo.getId());

            boolean esDirector = dirOpt.isPresent() && dirOpt.get().getProfesor() != null &&
                    dirOpt.get().getProfesor().getId().equals(docente.getId());

            if (!esDirector) {
                throw new AccessDeniedException("Acceso denegado: solo el Director de Grupo asignado o directivos pueden consultar la sábana consolidada del curso (UC-PROV-19 E1)");
            }
        }

        List<Matricula> matriculados = matriculaRepository.findByGrupoIdAndEstado(grupoId, EstadoMatricula.MATRICULADA);
        ConfiguracionAlertaAsistencia config = obtenerConfiguracionAlertaVigente(periodo.getAnioLectivo().getId());

        SabanaAsistenciaGrupalResponse resp = new SabanaAsistenciaGrupalResponse();
        resp.setGrupoId(grupo.getId());
        resp.setGrupoNombre(grupo.getNombre());
        resp.setPeriodoId(periodo.getId());
        resp.setPeriodoNombre(periodo.getNombre());

        asignacionRepository.findByGrupoIdAndEsDirectorGrupoTrueAndActivoTrue(grupo.getId()).ifPresent(dir -> {
            if (dir.getProfesor() != null) {
                resp.setDirectorGrupoNombre(dir.getProfesor().getNombres() + " " + dir.getProfesor().getApellidos());
            }
        });

        int alertaRojaContador = 0;
        int alertaAmarillaContador = 0;

        for (Matricula m : matriculados) {
            List<RegistroAsistencia> regs = registroRepository
                    .findByMatriculaIdAndSesionAsistenciaPeriodoAcademicoId(m.getId(), periodo.getId());

            long total = regs.size();
            long pres = 0;
            long ausInj = 0;
            long ausJus = 0;
            long ret = 0;
            long eva = 0;

            for (RegistroAsistencia r : regs) {
                switch (r.getEstado()) {
                    case PRESENTE -> pres++;
                    case AUSENTE_INJUSTIFICADA -> ausInj++;
                    case AUSENTE_JUSTIFICADA, SALIDA_ANTICIPADA_AUTORIZADA -> ausJus++;
                    case RETARDO -> ret++;
                    case EVASION_INTERNA_CLASE -> eva++;
                    default -> {}
                }
            }

            BigDecimal pctInasistencia = BigDecimal.ZERO;
            if (total > 0) {
                pctInasistencia = BigDecimal.valueOf(ausInj + eva)
                        .multiply(BigDecimal.valueOf(100))
                        .divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP);
            }

            String nivel = "NINGUNA";
            if (config.esAlertaRoja(pctInasistencia)) {
                nivel = "ROJA";
                alertaRojaContador++;
            } else if (config.esAlertaAmarilla(pctInasistencia)) {
                nivel = "AMARILLA";
                alertaAmarillaContador++;
            }

            SabanaAsistenciaGrupalResponse.EstudianteAsistenciaSabanaItem item = new SabanaAsistenciaGrupalResponse.EstudianteAsistenciaSabanaItem();
            item.setMatriculaId(m.getId());
            item.setNumeroMatricula(m.getNumeroMatricula());
            item.setEstudianteId(m.getEstudiante().getId());
            item.setEstudianteNombreCompleto(m.getEstudiante().getNombres() + " " + m.getEstudiante().getApellidos());
            item.setTotalSesiones(total);
            item.setTotalPresente(pres);
            item.setTotalAusenciasInjustificadas(ausInj);
            item.setTotalAusenciasJustificadas(ausJus);
            item.setTotalRetardos(ret);
            item.setTotalEvasiones(eva);
            item.setPorcentajeInasistencia(pctInasistencia);
            item.setNivelAlerta(nivel);

            resp.getEstudiantes().add(item);
        }

        resp.setTotalEstudiantes(matriculados.size());
        resp.setTotalEstudiantesAlertaRoja(alertaRojaContador);
        resp.setTotalEstudiantesAlertaAmarilla(alertaAmarillaContador);
        return resp;
    }

    @Transactional(readOnly = true)
    public List<ControlSesionesDocenteResponse> consultarControlSesionesDocente(Long anioLectivoId, Long periodoId) {
        PeriodoAcademico periodo = periodoRepository.findById(periodoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Periodo no encontrado con ID " + periodoId));

        List<AsignacionAcademica> asignaciones = asignacionRepository.findAll().stream()
                .filter(a -> Boolean.TRUE.equals(a.getActivo()))
                .filter(a -> a.getGrupo() != null && a.getGrupo().getAnioLectivo() != null &&
                        a.getGrupo().getAnioLectivo().getId().equals(anioLectivoId))
                .toList();

        List<ControlSesionesDocenteResponse> lista = new ArrayList<>();
        long excusasPendientes = solicitudExcusaRepository.countByEstado(EstadoSolicitudExcusa.PENDIENTE_REVISION);

        for (AsignacionAcademica asig : asignaciones) {
            ControlSesionesDocenteResponse item = new ControlSesionesDocenteResponse();
            item.setAsignacionId(asig.getId());
            if (asig.getProfesor() != null) {
                item.setDocenteId(asig.getProfesor().getId());
                item.setDocenteNombreCompleto(asig.getProfesor().getNombres() + " " + asig.getProfesor().getApellidos());
            }
            if (asig.getGrupo() != null) {
                item.setGrupoId(asig.getGrupo().getId());
                item.setGrupoNombre(asig.getGrupo().getNombre());
            }
            if (asig.getAsignatura() != null) {
                item.setAsignaturaId(asig.getAsignatura().getId());
                item.setAsignaturaNombre(asig.getAsignatura().getNombre());
            }

            long totalSesiones = sesionRepository
                    .findByAsignacionAcademicaIdAndPeriodoAcademicoIdOrderByFechaDescBloqueHorarioDesc(asig.getId(), periodo.getId()).size();

            item.setSesionesRegistradas(totalSesiones);
            item.setSesionesPendientesEstimadas(Math.max(0, 40 - (int) totalSesiones)); // Estimado estándar de periodo
            item.setExcusasPendientesRevision(excusasPendientes);

            lista.add(item);
        }

        return lista;
    }

    // =========================================================================
    // VALIDACIÓN CONTEXTUAL DOCENTE
    // =========================================================================

    private void validarDocenteAsignado(AsignacionAcademica asignacion, Long usuarioId, String rol) {
        if (rol == null || !"PROFESOR".equalsIgnoreCase(rol)) {
            return;
        }

        if (usuarioId == null) {
            throw new AccessDeniedException("Acceso denegado: usuario no autenticado (UC-19 E1)");
        }

        Profesor docente = profesorRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new AccessDeniedException("Acceso denegado: no se encontró perfil de docente para el usuario en sesión (UC-19 E1)"));

        if (asignacion.getProfesor() == null || !asignacion.getProfesor().getId().equals(docente.getId())) {
            throw new AccessDeniedException("Acceso denegado: el docente no tiene asignación activa sobre este curso y asignatura (UC-19 E1)");
        }
    }
}
