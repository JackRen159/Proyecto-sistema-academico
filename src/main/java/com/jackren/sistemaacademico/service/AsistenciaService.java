package com.jackren.sistemaacademico.service;

import com.jackren.sistemaacademico.dto.*;
import com.jackren.sistemaacademico.entity.*;
import com.jackren.sistemaacademico.exception.ConflictoNegocioException;
import com.jackren.sistemaacademico.exception.RecursoNoEncontradoException;
import com.jackren.sistemaacademico.mapper.AsistenciaMapper;
import com.jackren.sistemaacademico.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AsistenciaService {

    private final ConfiguracionAlertaAsistenciaRepository alertaRepository;
    private final SesionAsistenciaRepository sesionRepository;
    private final RegistroAsistenciaRepository registroRepository;
    private final JustificacionAsistenciaRepository justificacionRepository;
    private final AsignacionAcademicaRepository asignacionRepository;
    private final GrupoRepository grupoRepository;
    private final PeriodoAcademicoRepository periodoRepository;
    private final AnioLectivoRepository anioLectivoRepository;
    private final MatriculaRepository matriculaRepository;
    private final UsuarioRepository usuarioRepository;
    private final AsistenciaMapper asistenciaMapper;

    public AsistenciaService(ConfiguracionAlertaAsistenciaRepository alertaRepository,
                             SesionAsistenciaRepository sesionRepository,
                             RegistroAsistenciaRepository registroRepository,
                             JustificacionAsistenciaRepository justificacionRepository,
                             AsignacionAcademicaRepository asignacionRepository,
                             GrupoRepository grupoRepository,
                             PeriodoAcademicoRepository periodoRepository,
                             AnioLectivoRepository anioLectivoRepository,
                             MatriculaRepository matriculaRepository,
                             UsuarioRepository usuarioRepository,
                             AsistenciaMapper asistenciaMapper) {
        this.alertaRepository = alertaRepository;
        this.sesionRepository = sesionRepository;
        this.registroRepository = registroRepository;
        this.justificacionRepository = justificacionRepository;
        this.asignacionRepository = asignacionRepository;
        this.grupoRepository = grupoRepository;
        this.periodoRepository = periodoRepository;
        this.anioLectivoRepository = anioLectivoRepository;
        this.matriculaRepository = matriculaRepository;
        this.usuarioRepository = usuarioRepository;
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
                .orElseGet(() -> new ConfiguracionAlertaAsistencia());

        config.setAnioLectivo(anioLectivo);
        config.setPorcentajeAlertaAmarilla(request.getPorcentajeAlertaAmarilla());
        config.setPorcentajeAlertaRoja(request.getPorcentajeAlertaRoja());
        config.setDescripcion(request.getDescripcion());

        ConfiguracionAlertaAsistencia guardada = alertaRepository.save(config);
        return asistenciaMapper.toResponse(guardada);
    }

    @Transactional(readOnly = true)
    public ConfiguracionAlertaAsistenciaResponse obtenerConfiguracionAlerta(Long anioLectivoId) {
        ConfiguracionAlertaAsistencia config = alertaRepository.findByAnioLectivoId(anioLectivoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe configuración de alertas para el año lectivo ID " + anioLectivoId));
        return asistenciaMapper.toResponse(config);
    }

    public ConfiguracionAlertaAsistencia obtenerOCrearAlertaPorDefecto(AnioLectivo anioLectivo) {
        if (anioLectivo == null) {
            return new ConfiguracionAlertaAsistencia();
        }
        return alertaRepository.findByAnioLectivoId(anioLectivo.getId())
                .orElseGet(() -> {
                    ConfiguracionAlertaAsistencia def = new ConfiguracionAlertaAsistencia(
                            anioLectivo,
                            new BigDecimal("15.00"),
                            new BigDecimal("25.00"),
                            "Configuración de alertas estándar generada automáticamente"
                    );
                    return alertaRepository.save(def);
                });
    }

    // =========================================================================
    // 2. GESTIÓN DE SESIONES DE ASISTENCIA
    // =========================================================================

    public SesionAsistenciaResponse crearSesion(SesionAsistenciaRequest request, Long usuarioId) {
        Grupo grupo = grupoRepository.findById(request.getGrupoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Grupo no encontrado con ID " + request.getGrupoId()));

        PeriodoAcademico periodo = periodoRepository.findById(request.getPeriodoAcademicoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Periodo académico no encontrado con ID " + request.getPeriodoAcademicoId()));

        if (periodo.getEstado() != EstadoPeriodo.ABIERTO) {
            throw new ConflictoNegocioException("Solo se pueden registrar sesiones de asistencia en periodos académicos con estado ABIERTO");
        }

        if (grupo.getAnioLectivo() == null || !grupo.getAnioLectivo().getId().equals(periodo.getAnioLectivo().getId())) {
            throw new ConflictoNegocioException("El periodo académico no corresponde al año lectivo del grupo");
        }

        LocalDate fecha = request.getFecha() != null ? request.getFecha() : LocalDate.now();
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
    // 3. TOMA DE ASISTENCIA (PLANILLA EN LOTE)
    // =========================================================================

    public List<RegistroAsistenciaResponse> registrarPlanillaLote(PlanillaAsistenciaLoteRequest request) {
        SesionAsistencia sesion = sesionRepository.findById(request.getSesionId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Sesión de asistencia no encontrada con ID " + request.getSesionId()));

        if (sesion.getPeriodoAcademico().getEstado() != EstadoPeriodo.ABIERTO) {
            throw new ConflictoNegocioException("No se pueden registrar asistencias en un periodo académico cerrado");
        }

        Grupo grupoSesion = sesion.getGrupo();
        List<RegistroAsistenciaResponse> responses = new ArrayList<>();

        for (RegistroAsistenciaItemRequest item : request.getRegistros()) {
            Matricula matricula = matriculaRepository.findById(item.getMatriculaId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Matrícula no encontrada con ID " + item.getMatriculaId()));

            if (matricula.getEstado() != EstadoMatricula.MATRICULADA) {
                throw new ConflictoNegocioException("El estudiante con matrícula " + matricula.getNumeroMatricula() +
                        " no se encuentra en estado MATRICULADA (estado actual: " + matricula.getEstado() + ")");
            }

            if (!matricula.getGrupo().getId().equals(grupoSesion.getId())) {
                throw new ConflictoNegocioException("La matrícula " + matricula.getNumeroMatricula() +
                        " pertenece al grupo '" + matricula.getGrupo().getNombre() + "', no al grupo de la sesión ('" +
                        grupoSesion.getNombre() + "')");
            }

            Optional<RegistroAsistencia> existenteOpt = registroRepository
                    .findBySesionAsistenciaIdAndMatriculaId(sesion.getId(), matricula.getId());

            RegistroAsistencia registro;
            if (existenteOpt.isPresent()) {
                registro = existenteOpt.get();
                registro.setEstado(item.getEstado());
                registro.setMinutosRetardo(item.getMinutosRetardo() != null ? item.getMinutosRetardo() : 0);
                registro.setObservaciones(item.getObservaciones());
            } else {
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
    // 4. JUSTIFICACIÓN AUDITADA DE INASISTENCIA
    // =========================================================================

    public JustificacionAsistenciaResponse justificarInasistencia(Long registroId,
                                                                  JustificacionAsistenciaRequest request,
                                                                  Long usuarioId,
                                                                  String username) {
        RegistroAsistencia registro = registroRepository.findById(registroId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Registro de asistencia no encontrado con ID " + registroId));

        if (registro.getEstado() != EstadoAsistencia.AUSENTE_INJUSTIFICADA &&
                registro.getEstado() != EstadoAsistencia.RETARDO) {
            throw new ConflictoNegocioException("Solo se pueden radicar justificaciones para registros con estado AUSENTE_INJUSTIFICADA o RETARDO (estado actual: " +
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

        // Si la inasistencia era injustificada, se reclasifica formalmente como justificada
        if (registro.getEstado() == EstadoAsistencia.AUSENTE_INJUSTIFICADA) {
            registro.setEstado(EstadoAsistencia.AUSENTE_JUSTIFICADA);
            registroRepository.save(registro);
        }

        return asistenciaMapper.toResponse(guardada);
    }

    // =========================================================================
    // 5. CÁLCULO DE RESÚMENES, PORCENTAJES Y ALERTAS TEMPRANAS
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
            // Se calcula porcentaje de inasistencia sobre faltas injustificadas
            pctInasistencia = BigDecimal.valueOf(totalAusInjust)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(totalSesiones), 2, RoundingMode.HALF_UP);
            pctAsistencia = BigDecimal.valueOf(100).subtract(pctInasistencia);
        }

        ConfiguracionAlertaAsistencia config = obtenerOCrearAlertaPorDefecto(periodo.getAnioLectivo());

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
}
