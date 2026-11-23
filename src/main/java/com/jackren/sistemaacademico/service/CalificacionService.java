package com.jackren.sistemaacademico.service;

import com.jackren.sistemaacademico.dto.*;
import com.jackren.sistemaacademico.entity.*;
import com.jackren.sistemaacademico.exception.ConflictoNegocioException;
import com.jackren.sistemaacademico.exception.RecursoNoEncontradoException;
import com.jackren.sistemaacademico.exception.ValidacionException;
import com.jackren.sistemaacademico.mapper.CalificacionMapper;
import com.jackren.sistemaacademico.repository.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class CalificacionService {

    private final ConfiguracionEscalaSieeRepository escalaRepository;
    private final ConfiguracionSieeVersionRepository sieeVersionRepository;
    private final ActividadEvaluativaRepository actividadRepository;
    private final CalificacionActividadRepository calificacionActividadRepository;
    private final CalificacionPeriodoRepository calificacionPeriodoRepository;
    private final CalificacionAreaPeriodoRepository calificacionAreaPeriodoRepository;
    private final AuditoriaCalificacionRepository auditoriaRepository;
    private final AsignacionAcademicaRepository asignacionRepository;
    private final PeriodoAcademicoRepository periodoRepository;
    private final AnioLectivoRepository anioLectivoRepository;
    private final MatriculaRepository matriculaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProfesorRepository profesorRepository;
    private final EstudianteRepository estudianteRepository;
    private final EstudianteAcudienteRepository estudianteAcudienteRepository;
    private final GradoAsignaturaRepository gradoAsignaturaRepository;
    private final ActaNivelacionRepository actaNivelacionRepository;
    private final ConvalidacionCalificacionRepository convalidacionRepository;
    private final CalificacionMapper calificacionMapper;

    public CalificacionService(ConfiguracionEscalaSieeRepository escalaRepository,
                               ConfiguracionSieeVersionRepository sieeVersionRepository,
                               ActividadEvaluativaRepository actividadRepository,
                               CalificacionActividadRepository calificacionActividadRepository,
                               CalificacionPeriodoRepository calificacionPeriodoRepository,
                               CalificacionAreaPeriodoRepository calificacionAreaPeriodoRepository,
                               AuditoriaCalificacionRepository auditoriaRepository,
                               AsignacionAcademicaRepository asignacionRepository,
                               PeriodoAcademicoRepository periodoRepository,
                               AnioLectivoRepository anioLectivoRepository,
                               MatriculaRepository matriculaRepository,
                               UsuarioRepository usuarioRepository,
                               ProfesorRepository profesorRepository,
                               EstudianteRepository estudianteRepository,
                               EstudianteAcudienteRepository estudianteAcudienteRepository,
                               GradoAsignaturaRepository gradoAsignaturaRepository,
                               ActaNivelacionRepository actaNivelacionRepository,
                               ConvalidacionCalificacionRepository convalidacionRepository,
                               CalificacionMapper calificacionMapper) {
        this.escalaRepository = escalaRepository;
        this.sieeVersionRepository = sieeVersionRepository;
        this.actividadRepository = actividadRepository;
        this.calificacionActividadRepository = calificacionActividadRepository;
        this.calificacionPeriodoRepository = calificacionPeriodoRepository;
        this.calificacionAreaPeriodoRepository = calificacionAreaPeriodoRepository;
        this.auditoriaRepository = auditoriaRepository;
        this.asignacionRepository = asignacionRepository;
        this.periodoRepository = periodoRepository;
        this.anioLectivoRepository = anioLectivoRepository;
        this.matriculaRepository = matriculaRepository;
        this.usuarioRepository = usuarioRepository;
        this.profesorRepository = profesorRepository;
        this.estudianteRepository = estudianteRepository;
        this.estudianteAcudienteRepository = estudianteAcudienteRepository;
        this.gradoAsignaturaRepository = gradoAsignaturaRepository;
        this.actaNivelacionRepository = actaNivelacionRepository;
        this.convalidacionRepository = convalidacionRepository;
        this.calificacionMapper = calificacionMapper;
    }

    // =========================================================================
    // 1. CONFIGURACIÓN DE ESCALA SIEE INSTITUCIONAL (DECRETO 1290)
    // =========================================================================

    public ConfiguracionEscalaSieeResponse guardarOActualizarEscala(ConfiguracionEscalaSieeRequest request) {
        AnioLectivo anioLectivo = anioLectivoRepository.findById(request.getAnioLectivoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Año lectivo no encontrado con ID " + request.getAnioLectivoId()));

        if (anioLectivo.getEstado() == EstadoAnioLectivo.CERRADO) {
            throw new ConflictoNegocioException("No se puede configurar la escala SIEE en un año lectivo cerrado");
        }

        validarCoherenciaEscala(request);

        ConfiguracionEscalaSiee escala = escalaRepository.findByAnioLectivoId(anioLectivo.getId())
                .orElseGet(ConfiguracionEscalaSiee::new);

        escala.setAnioLectivo(anioLectivo);
        escala.setNotaMinima(request.getNotaMinima());
        escala.setNotaMaxima(request.getNotaMaxima());
        escala.setNotaAprobatoria(request.getNotaAprobatoria());
        escala.setEscalaBasicoMin(request.getEscalaBasicoMin());
        escala.setEscalaAltoMin(request.getEscalaAltoMin());
        escala.setEscalaSuperiorMin(request.getEscalaSuperiorMin());
        escala.setDecimalesRedondeo(request.getDecimalesRedondeo() != null ? request.getDecimalesRedondeo() : 1);
        escala.setDescripcion(request.getDescripcion());

        ConfiguracionEscalaSiee guardada = escalaRepository.save(escala);
        return calificacionMapper.toResponse(guardada);
    }

    @Transactional(readOnly = true)
    public ConfiguracionEscalaSieeResponse obtenerEscalaPorAnio(Long anioLectivoId) {
        ConfiguracionEscalaSiee escala = obtenerEscalaVigentePorAnio(anioLectivoId);
        return calificacionMapper.toResponse(escala);
    }

    public ConfiguracionEscalaSiee obtenerEscalaVigentePorAnio(Long anioLectivoId) {
        if (anioLectivoId == null) {
            throw new ConflictoNegocioException("El ID del año lectivo es obligatorio para resolver la escala SIEE");
        }

        Optional<ConfiguracionEscalaSiee> escalaOpt = escalaRepository.findByAnioLectivoId(anioLectivoId);
        if (escalaOpt.isPresent()) {
            return escalaOpt.get();
        }

        // Si no está en tabla legacy, buscar versión aprobada del SIEE
        Optional<ConfiguracionSieeVersion> sieeOpt = sieeVersionRepository
                .findFirstByAnioLectivoVigenciaIdAndEstadoInOrderByNumeroVersionDesc(
                        anioLectivoId, List.of(EstadoVersionConfiguracion.VIGENTE, EstadoVersionConfiguracion.APROBADA_VIGENCIA_FUTURA)
                );

        if (sieeOpt.isEmpty()) {
            sieeOpt = sieeVersionRepository.findFirstByEstadoOrderByNumeroVersionDesc(EstadoVersionConfiguracion.VIGENTE);
        }

        if (sieeOpt.isPresent()) {
            ConfiguracionSieeVersion v = sieeOpt.get();
            ConfiguracionEscalaSiee escala = new ConfiguracionEscalaSiee();
            escala.setNotaMinima(v.getEscalaMinima());
            escala.setNotaMaxima(v.getEscalaMaxima());
            escala.setNotaAprobatoria(v.getEscalaAprobatoria());
            escala.setEscalaBasicoMin(v.getEscalaAprobatoria());
            escala.setEscalaAltoMin(v.getEscalaAprobatoria().add(new BigDecimal("1.00")).min(v.getEscalaMaxima()));
            escala.setEscalaSuperiorMin(v.getEscalaAprobatoria().add(new BigDecimal("1.60")).min(v.getEscalaMaxima()));
            escala.setDecimalesRedondeo(v.getDecimales() != null ? v.getDecimales() : 2);
            escala.setDescripcion(v.getNombreVersion());
            return escala;
        }

        // Error A: Regla RN-01 y UC-16 E3 - Bloquear con HTTP 409 sin inventar escalas por defecto
        throw new ConflictoNegocioException("No existe versión del SIEE aprobada y vigente para el año lectivo ID " + anioLectivoId +
                ". Debe configurarse institucionalmente antes de evaluar (RN-01)");
    }

    // =========================================================================
    // 2. GESTIÓN DE ACTIVIDADES EVALUATIVAS (UC-16)
    // =========================================================================

    public ActividadEvaluativaResponse crearActividad(ActividadEvaluativaRequest request) {
        return crearActividad(request, null, null);
    }

    public ActividadEvaluativaResponse crearActividad(ActividadEvaluativaRequest request, Long usuarioId, String rol) {
        AsignacionAcademica asignacion = asignacionRepository.findById(request.getAsignacionAcademicaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Asignación académica no encontrada con ID " + request.getAsignacionAcademicaId()));

        if (!Boolean.TRUE.equals(asignacion.getActivo())) {
            throw new ConflictoNegocioException("No se pueden crear actividades en una asignación académica inactiva");
        }

        validarDocenteAsignado(asignacion, usuarioId, rol);

        PeriodoAcademico periodo = periodoRepository.findById(request.getPeriodoAcademicoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Periodo académico no encontrado con ID " + request.getPeriodoAcademicoId()));

        if (periodo.getEstado() != EstadoPeriodo.ABIERTO) {
            throw new ConflictoNegocioException("Solo se pueden crear actividades evaluativas en periodos académicos con estado ABIERTO (UC-16 E4)");
        }

        if (asignacion.getGrupo().getAnioLectivo() == null ||
                !asignacion.getGrupo().getAnioLectivo().getId().equals(periodo.getAnioLectivo().getId())) {
            throw new ConflictoNegocioException("El periodo académico no corresponde al año lectivo de la asignación del grupo");
        }

        // UC-16 E2: Validación de fecha programada dentro de [fechaInicio, fechaFin]
        if (request.getFechaEvaluacion() != null) {
            if (request.getFechaEvaluacion().isBefore(periodo.getFechaInicio()) ||
                    request.getFechaEvaluacion().isAfter(periodo.getFechaFin())) {
                throw new ConflictoNegocioException("La fecha programada de la actividad (" + request.getFechaEvaluacion() +
                        ") está fuera del rango del periodo académico [" + periodo.getFechaInicio() + " a " + periodo.getFechaFin() + "] (UC-16 E2)");
            }
        }

        // UC-16 E1: Validación de porcentaje acumulado <= 100.00%
        BigDecimal sumaActual = actividadRepository.sumPorcentajeByAsignacionAndPeriodo(asignacion.getId(), periodo.getId());
        if (sumaActual == null) {
            sumaActual = BigDecimal.ZERO;
        }
        BigDecimal nuevaSuma = sumaActual.add(request.getPorcentaje());
        if (nuevaSuma.compareTo(new BigDecimal("100.00")) > 0) {
            throw new ConflictoNegocioException("La suma acumulada de ponderaciones para esta asignatura en el periodo superaría el 100.00% (actual: " +
                    sumaActual + "%, solicitada: " + request.getPorcentaje() + "%) (UC-16 E1, RN-CAL-01)");
        }

        // Obtener versión vigente del SIEE (UC-16 E3, RN-01)
        ConfiguracionSieeVersion sieeVersion = sieeVersionRepository
                .findFirstByAnioLectivoVigenciaIdAndEstadoInOrderByNumeroVersionDesc(
                        periodo.getAnioLectivo().getId(), List.of(EstadoVersionConfiguracion.VIGENTE)
                ).orElse(null);

        ActividadEvaluativa actividad = calificacionMapper.toEntity(request, asignacion, periodo, sieeVersion);
        ActividadEvaluativa guardada = actividadRepository.save(actividad);
        return calificacionMapper.toResponse(guardada);
    }

    public ActividadEvaluativaResponse actualizarActividad(Long actividadId,
                                                          ActividadEvaluativaRequest request,
                                                          String motivoJustificacion,
                                                          Long usuarioId,
                                                          String rol) {
        ActividadEvaluativa actividad = actividadRepository.findById(actividadId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Actividad evaluativa no encontrada con ID " + actividadId));

        validarDocenteAsignado(actividad.getAsignacionAcademica(), usuarioId, rol);

        if (actividad.getPeriodoAcademico().getEstado() != EstadoPeriodo.ABIERTO) {
            throw new ConflictoNegocioException("No se puede modificar una actividad en un periodo académico cerrado (UC-16 E4)");
        }

        boolean tieneNotas = calificacionActividadRepository.existsByActividadId(actividad.getId());
        if (tieneNotas) {
            // UC-16 A4: Modificación de actividad ya calificada exige justificación obligatoria
            if (motivoJustificacion == null || motivoJustificacion.trim().isEmpty()) {
                throw new ValidacionException("La modificación de una actividad que ya posee calificaciones registradas exige registrar justificación obligatoria (UC-16 A4)");
            }
        }

        if (request.getPorcentaje() != null && request.getPorcentaje().compareTo(actividad.getPorcentaje()) != 0) {
            BigDecimal sumaSinEsta = actividadRepository.sumPorcentajeByAsignacionAndPeriodo(
                    actividad.getAsignacionAcademica().getId(), actividad.getPeriodoAcademico().getId()
            ).subtract(actividad.getPorcentaje());

            BigDecimal nuevaSuma = sumaSinEsta.add(request.getPorcentaje());
            if (nuevaSuma.compareTo(new BigDecimal("100.00")) > 0) {
                throw new ConflictoNegocioException("La nueva ponderación supera el 100.00% del periodo (acumulado sería: " + nuevaSuma + "%)");
            }
            actividad.setPorcentaje(request.getPorcentaje());
        }

        if (request.getNombre() != null) {
            actividad.setNombre(request.getNombre());
        }
        if (request.getDescripcion() != null) {
            actividad.setDescripcion(request.getDescripcion());
        }
        if (request.getFechaEvaluacion() != null) {
            actividad.setFechaEvaluacion(request.getFechaEvaluacion());
        }
        if (request.getCategoriaEvaluativa() != null) {
            actividad.setCategoriaEvaluativa(request.getCategoriaEvaluativa());
        }
        if (request.getTipoComponente() != null) {
            actividad.setTipoComponente(request.getTipoComponente());
        }
        if (request.getModalidad() != null) {
            actividad.setModalidad(request.getModalidad());
        }
        if (request.getEstado() != null) {
            actividad.setEstado(request.getEstado());
        }

        ActividadEvaluativa actualizada = actividadRepository.save(actividad);
        return calificacionMapper.toResponse(actualizada);
    }

    public void eliminarActividad(Long actividadId, Long usuarioId, String rol) {
        ActividadEvaluativa actividad = actividadRepository.findById(actividadId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Actividad evaluativa no encontrada con ID " + actividadId));

        validarDocenteAsignado(actividad.getAsignacionAcademica(), usuarioId, rol);

        // UC-16 E4: Ninguna actividad que ya posea calificaciones registradas puede eliminarse físicamente
        if (calificacionActividadRepository.existsByActividadId(actividad.getId())) {
            throw new ConflictoNegocioException("No se puede eliminar físicamente una actividad que ya posee calificaciones registradas (UC-16 E4, inmutabilidad)");
        }

        actividad.setActivo(false);
        actividadRepository.save(actividad);
    }

    @Transactional(readOnly = true)
    public List<ActividadEvaluativaResponse> listarActividadesPorAsignacionYPeriodo(Long asignacionId, Long periodoId) {
        return actividadRepository.findByAsignacionAcademicaIdAndPeriodoAcademicoIdAndActivoTrue(asignacionId, periodoId).stream()
                .map(calificacionMapper::toResponse)
                .toList();
    }

    // =========================================================================
    // 3. REGISTRO Y CALIFICACIÓN DE ACTIVIDADES (UC-17)
    // =========================================================================

    public List<CalificacionActividadResponse> registrarCalificacionesLote(RegistroCalificacionesLoteRequest request,
                                                                         Long usuarioId,
                                                                         String username) {
        return registrarCalificacionesLote(request, usuarioId, username, null);
    }

    public List<CalificacionActividadResponse> registrarCalificacionesLote(RegistroCalificacionesLoteRequest request,
                                                                         Long usuarioId,
                                                                         String username,
                                                                         String rol) {
        ActividadEvaluativa actividad = actividadRepository.findById(request.getActividadId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Actividad evaluativa no encontrada con ID " + request.getActividadId()));

        if (!Boolean.TRUE.equals(actividad.getActivo())) {
            throw new ConflictoNegocioException("La actividad evaluativa está inactiva");
        }

        validarDocenteAsignado(actividad.getAsignacionAcademica(), usuarioId, rol);

        if (actividad.getPeriodoAcademico().getEstado() != EstadoPeriodo.ABIERTO) {
            throw new ConflictoNegocioException("No se pueden registrar calificaciones en un periodo académico cerrado (UC-17 E2)");
        }

        AnioLectivo anio = actividad.getPeriodoAcademico().getAnioLectivo();
        ConfiguracionEscalaSiee escala = obtenerEscalaVigentePorAnio(anio.getId());

        Grupo grupoAsignado = actividad.getAsignacionAcademica().getGrupo();
        boolean esPreescolar = grupoAsignado.getGrado().getNivel() == NivelEducativo.PREESCOLAR;

        Usuario usuario = usuarioId != null ? usuarioRepository.findById(usuarioId).orElse(null) : null;
        String userStr = username != null ? username : (usuario != null ? usuario.getUsername() : "sistema");

        List<CalificacionActividadResponse> responses = new ArrayList<>();

        for (RegistroCalificacionRequest califReq : request.getCalificaciones()) {
            // Validación de nota cuantitativa vs cualitativa (UC-17 E1, RN-CAL-02)
            if (califReq.getEstado() == EstadoCalificacionActividad.PENDIENTE_POR_EXCUSA_JUSTIFICADA) {
                // Estado diferido por inasistencia justificada (UC-17 A1, RN-CAL-04)
                califReq.setValor(null);
            } else if (!esPreescolar) {
                validarNotaEnRango(califReq.getValor(), escala);
            }

            Matricula matricula = matriculaRepository.findById(califReq.getMatriculaId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Matrícula no encontrada con ID " + califReq.getMatriculaId()));

            if (matricula.getEstado() != EstadoMatricula.MATRICULADA) {
                throw new ConflictoNegocioException("El estudiante con matrícula " + matricula.getNumeroMatricula() +
                        " no se encuentra en estado MATRICULADA");
            }

            if (!matricula.getGrupo().getId().equals(grupoAsignado.getId())) {
                throw new ConflictoNegocioException("La matrícula " + matricula.getNumeroMatricula() +
                        " pertenece al grupo '" + matricula.getGrupo().getNombre() + "', no al grupo de la actividad ('" +
                        grupoAsignado.getNombre() + "') (UC-17 E3)");
            }

            // UC-17 RN-CAL-06: Bloquear capturas posteriores a fecha de retiro efectivo
            if (matricula.getFechaEfectivaRetiro() != null && actividad.getFechaEvaluacion() != null) {
                if (actividad.getFechaEvaluacion().isAfter(matricula.getFechaEfectivaRetiro())) {
                    throw new ConflictoNegocioException("No se pueden registrar calificaciones en fecha posterior al retiro del estudiante (RN-CAL-06)");
                }
            }

            Optional<CalificacionActividad> existenteOpt = calificacionActividadRepository
                    .findByActividadIdAndMatriculaId(actividad.getId(), matricula.getId());

            CalificacionActividad calificacion;
            if (existenteOpt.isPresent()) {
                calificacion = existenteOpt.get();
                BigDecimal valorAnterior = calificacion.getValor();

                // Error D / UC-17 E5: Justificación obligatoria en corrección de nota existente
                if (valorAnterior != null && califReq.getValor() != null && valorAnterior.compareTo(califReq.getValor()) != 0) {
                    if (califReq.getMotivoCorreccion() == null || califReq.getMotivoCorreccion().trim().isEmpty()) {
                        throw new ValidacionException("La corrección de la nota de matrícula " + matricula.getNumeroMatricula() +
                                " exige registrar justificación obligatoria (RN-10, UC-17 E5)");
                    }

                    AuditoriaCalificacion auditoria = new AuditoriaCalificacion(
                            TipoEntidadAuditoriaCalificacion.ACTIVIDAD,
                            calificacion.getId(),
                            matricula,
                            valorAnterior,
                            califReq.getValor(),
                            califReq.getMotivoCorreccion(),
                            usuario,
                            userStr
                    );
                    auditoriaRepository.save(auditoria);
                }

                calificacion.setValor(califReq.getValor());
                if (califReq.getEstado() != null) {
                    calificacion.setEstado(califReq.getEstado());
                }
                calificacion.setFechaSupletoria(califReq.getFechaSupletoria());
                calificacion.setValorCualitativo(califReq.getValorCualitativo());
                calificacion.setObservacionDescriptiva(califReq.getObservacionDescriptiva());
                calificacion.setObservaciones(califReq.getObservaciones());
            } else {
                calificacion = calificacionMapper.toEntity(califReq, actividad, matricula);
            }

            CalificacionActividad guardada = calificacionActividadRepository.save(calificacion);
            responses.add(calificacionMapper.toResponse(guardada, escala));
        }

        // Recálculo reactivo y sincronizado de definitivas de periodo (UC-17, S2)
        if (actividad.getAsignacionAcademica() != null && actividad.getPeriodoAcademico() != null) {
            recalcularDefinitivasSeguro(actividad.getAsignacionAcademica().getId(), actividad.getPeriodoAcademico().getId());
        }

        return responses;
    }

    public CalificacionActividadResponse actualizarCalificacionActividad(Long calificacionId,
                                                                         ActualizarCalificacionRequest request,
                                                                         Long usuarioId,
                                                                         String username) {
        return actualizarCalificacionActividad(calificacionId, request, usuarioId, username, null);
    }

    public CalificacionActividadResponse actualizarCalificacionActividad(Long calificacionId,
                                                                         ActualizarCalificacionRequest request,
                                                                         Long usuarioId,
                                                                         String username,
                                                                         String rol) {
        CalificacionActividad calificacion = calificacionActividadRepository.findById(calificacionId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Calificación de actividad no encontrada con ID " + calificacionId));

        validarDocenteAsignado(calificacion.getActividad().getAsignacionAcademica(), usuarioId, rol);

        if (calificacion.getActividad().getPeriodoAcademico().getEstado() != EstadoPeriodo.ABIERTO) {
            throw new ConflictoNegocioException("No se puede modificar una calificación en un periodo académico cerrado (UC-17 E2)");
        }

        AnioLectivo anio = calificacion.getActividad().getPeriodoAcademico().getAnioLectivo();
        ConfiguracionEscalaSiee escala = obtenerEscalaVigentePorAnio(anio.getId());

        validarNotaEnRango(request.getValorNuevo(), escala);

        if (request.getMotivo() == null || request.getMotivo().trim().isEmpty()) {
            throw new ValidacionException("La corrección de calificación exige registrar motivo o justificación obligatoria (RN-10, UC-17 E5)");
        }

        BigDecimal valorAnterior = calificacion.getValor();
        Usuario usuario = usuarioId != null ? usuarioRepository.findById(usuarioId).orElse(null) : null;
        String userStr = username != null ? username : (usuario != null ? usuario.getUsername() : "sistema");

        AuditoriaCalificacion auditoria = new AuditoriaCalificacion(
                TipoEntidadAuditoriaCalificacion.ACTIVIDAD,
                calificacion.getId(),
                calificacion.getMatricula(),
                valorAnterior,
                request.getValorNuevo(),
                request.getMotivo(),
                usuario,
                userStr
        );
        auditoriaRepository.save(auditoria);

        calificacion.setValor(request.getValorNuevo());
        if (request.getObservaciones() != null) {
            calificacion.setObservaciones(request.getObservaciones());
        }

        CalificacionActividad actualizada = calificacionActividadRepository.save(calificacion);
        if (calificacion.getActividad() != null &&
                calificacion.getActividad().getAsignacionAcademica() != null &&
                calificacion.getActividad().getPeriodoAcademico() != null) {
            recalcularDefinitivasSeguro(
                    calificacion.getActividad().getAsignacionAcademica().getId(),
                    calificacion.getActividad().getPeriodoAcademico().getId()
            );
        }
        return calificacionMapper.toResponse(actualizada, escala);
    }

    @Transactional(readOnly = true)
    public List<CalificacionActividadResponse> listarCalificacionesPorActividad(Long actividadId) {
        ActividadEvaluativa actividad = actividadRepository.findById(actividadId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Actividad evaluativa no encontrada con ID " + actividadId));
        ConfiguracionEscalaSiee escala = obtenerEscalaVigentePorAnio(actividad.getPeriodoAcademico().getAnioLectivo().getId());
        return calificacionActividadRepository.findByActividadId(actividadId).stream()
                .map(c -> calificacionMapper.toResponse(c, escala))
                .toList();
    }

    // =========================================================================
    // 4. CÁLCULO Y CIERRE DE DEFINITIVAS DE ASIGNATURA Y ÁREA (UC-17)
    // =========================================================================

    public List<CalificacionPeriodoResponse> calcularDefinitivasPeriodo(Long asignacionId, Long periodoId) {
        AsignacionAcademica asignacion = asignacionRepository.findById(asignacionId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Asignación académica no encontrada con ID " + asignacionId));

        PeriodoAcademico periodo = periodoRepository.findById(periodoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Periodo académico no encontrado con ID " + periodoId));

        List<ActividadEvaluativa> actividades = actividadRepository
                .findByAsignacionAcademicaIdAndPeriodoAcademicoIdAndActivoTrue(asignacionId, periodoId);

        if (actividades.isEmpty()) {
            throw new ConflictoNegocioException("No se pueden calcular definitivas: no existen actividades evaluativas activas registradas");
        }

        BigDecimal sumaPorcentajes = actividades.stream()
                .map(ActividadEvaluativa::getPorcentaje)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (sumaPorcentajes.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ConflictoNegocioException("La suma de porcentajes de las actividades debe ser mayor a 0");
        }

        AnioLectivo anio = periodo.getAnioLectivo();
        ConfiguracionEscalaSiee escala = obtenerEscalaVigentePorAnio(anio.getId());

        List<Matricula> matriculas = matriculaRepository.findByGrupoIdAndEstado(
                asignacion.getGrupo().getId(), EstadoMatricula.MATRICULADA
        );

        List<CalificacionPeriodoResponse> responses = new ArrayList<>();

        for (Matricula matricula : matriculas) {
            BigDecimal sumaPonderada = BigDecimal.ZERO;
            BigDecimal pesoCalificado = BigDecimal.ZERO;

            for (ActividadEvaluativa act : actividades) {
                Optional<CalificacionActividad> notaOpt = calificacionActividadRepository
                        .findByActividadIdAndMatriculaId(act.getId(), matricula.getId());

                if (notaOpt.isPresent()) {
                    CalificacionActividad ca = notaOpt.get();
                    // UC-17 RN-CAL-04: Ausencia justificada no penaliza con cero
                    if (ca.getEstado() == EstadoCalificacionActividad.PENDIENTE_POR_EXCUSA_JUSTIFICADA ||
                            ca.getEstado() == EstadoCalificacionActividad.EXIMIDA_CONVALIDADA) {
                        continue;
                    }
                    if (ca.getValor() != null) {
                        sumaPonderada = sumaPonderada.add(ca.getValor().multiply(act.getPorcentaje()));
                        pesoCalificado = pesoCalificado.add(act.getPorcentaje());
                    }
                }
            }

            BigDecimal notaCalculada;
            if (pesoCalificado.compareTo(BigDecimal.ZERO) > 0) {
                notaCalculada = sumaPonderada.divide(pesoCalificado, 4, RoundingMode.HALF_UP);
            } else {
                notaCalculada = escala.getNotaMinima();
            }

            BigDecimal notaDefinitiva = escala.redondear(notaCalculada);
            DesempenoNacional desempeno = escala.homologarDesempeno(notaDefinitiva);
            boolean aprobada = escala.esAprobada(notaDefinitiva);

            CalificacionPeriodo califPeriodo = calificacionPeriodoRepository
                    .findByMatriculaIdAndAsignaturaIdAndPeriodoAcademicoId(
                            matricula.getId(), asignacion.getAsignatura().getId(), periodo.getId())
                    .orElseGet(() -> new CalificacionPeriodo(
                            matricula, asignacion.getAsignatura(), periodo, notaDefinitiva, desempeno, aprobada
                    ));

            califPeriodo.setNotaDefinitiva(notaDefinitiva);
            califPeriodo.setDesempeno(desempeno);
            califPeriodo.setAprobada(aprobada);
            if (califPeriodo.getNotaRecuperacion() != null) {
                califPeriodo.setNotaFinal(califPeriodo.getNotaRecuperacion().max(notaDefinitiva));
                califPeriodo.setDesempeno(escala.homologarDesempeno(califPeriodo.getNotaFinal()));
                califPeriodo.setAprobada(escala.esAprobada(califPeriodo.getNotaFinal()));
            } else {
                califPeriodo.setNotaFinal(notaDefinitiva);
            }

            CalificacionPeriodo guardada = calificacionPeriodoRepository.save(califPeriodo);
            responses.add(calificacionMapper.toResponse(guardada));

            // Error E / UC-17 Paso 4: Consolidar automáticamente el Área Curricular
            consolidarAreaCurricular(matricula, asignacion.getAsignatura().getArea(), periodo, escala);
        }

        return responses;
    }

    public void consolidarAreaCurricular(Matricula matricula, Area area, PeriodoAcademico periodo, ConfiguracionEscalaSiee escala) {
        if (area == null || matricula == null || periodo == null) {
            return;
        }

        Grado grado = matricula.getGrupo().getGrado();
        List<GradoAsignatura> asignaturasDelArea = gradoAsignaturaRepository
                .findByGradoIdAndAnioLectivoIdAndActivoTrue(grado.getId(), periodo.getAnioLectivo().getId())
                .stream()
                .filter(ga -> ga.getAsignatura().getArea() != null && ga.getAsignatura().getArea().getId().equals(area.getId()))
                .toList();

        if (asignaturasDelArea.isEmpty()) {
            asignaturasDelArea = gradoAsignaturaRepository.findByGradoIdAndActivoTrue(grado.getId())
                    .stream()
                    .filter(ga -> ga.getAsignatura().getArea() != null && ga.getAsignatura().getArea().getId().equals(area.getId()))
                    .toList();
        }

        if (asignaturasDelArea.isEmpty()) {
            return;
        }

        BigDecimal sumaPonderadaArea = BigDecimal.ZERO;
        BigDecimal sumaPesosArea = BigDecimal.ZERO;

        for (GradoAsignatura ga : asignaturasDelArea) {
            Optional<CalificacionPeriodo> cpOpt = calificacionPeriodoRepository
                    .findByMatriculaIdAndAsignaturaIdAndPeriodoAcademicoId(matricula.getId(), ga.getAsignatura().getId(), periodo.getId());

            BigDecimal peso = (ga.getPesoPorcentualEnArea() != null && ga.getPesoPorcentualEnArea().compareTo(BigDecimal.ZERO) > 0)
                    ? ga.getPesoPorcentualEnArea()
                    : BigDecimal.valueOf(ga.getIntensidadHoraria() != null ? ga.getIntensidadHoraria() : 1);

            if (cpOpt.isPresent()) {
                BigDecimal notaAsig = cpOpt.get().getNotaFinal() != null ? cpOpt.get().getNotaFinal() : cpOpt.get().getNotaDefinitiva();
                sumaPonderadaArea = sumaPonderadaArea.add(notaAsig.multiply(peso));
                sumaPesosArea = sumaPesosArea.add(peso);
            }
        }

        if (sumaPesosArea.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal notaAreaCalculada = sumaPonderadaArea.divide(sumaPesosArea, 4, RoundingMode.HALF_UP);
            BigDecimal notaAreaDefinitiva = escala.redondear(notaAreaCalculada);
            DesempenoNacional desempenoArea = escala.homologarDesempeno(notaAreaDefinitiva);
            boolean aprobadaArea = escala.esAprobada(notaAreaDefinitiva);

            CalificacionAreaPeriodo califArea = calificacionAreaPeriodoRepository
                    .findByMatriculaIdAndAreaIdAndPeriodoAcademicoId(matricula.getId(), area.getId(), periodo.getId())
                    .orElseGet(() -> new CalificacionAreaPeriodo(
                            matricula, area, periodo, notaAreaDefinitiva, desempenoArea, aprobadaArea
                    ));

            califArea.setNotaDefinitiva(notaAreaDefinitiva);
            califArea.setDesempeno(desempenoArea);
            califArea.setAprobada(aprobadaArea);
            calificacionAreaPeriodoRepository.save(califArea);
        }
    }

    private void recalcularDefinitivasSeguro(Long asignacionId, Long periodoId) {
        try {
            if (asignacionId != null && periodoId != null) {
                calcularDefinitivasPeriodo(asignacionId, periodoId);
            }
        } catch (Exception ignored) {
            // No bloquear el registro de notas si aún no se configuran ponderaciones completas o en casos descriptivos
        }
    }

    public CalificacionPeriodoResponse registrarRecuperacion(Long calificacionPeriodoId,
                                                             BigDecimal notaRecuperacion,
                                                             String motivo,
                                                             Long usuarioId,
                                                             String username) {
        CalificacionPeriodo calif = calificacionPeriodoRepository.findById(calificacionPeriodoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Calificación de periodo no encontrada con ID " + calificacionPeriodoId));

        PeriodoAcademico periodo = calif.getPeriodoAcademico();
        AnioLectivo anio = periodo.getAnioLectivo();
        ConfiguracionEscalaSiee escala = obtenerEscalaVigentePorAnio(anio.getId());

        validarNotaEnRango(notaRecuperacion, escala);

        BigDecimal valorAnterior = calif.getNotaFinal() != null ? calif.getNotaFinal() : calif.getNotaDefinitiva();
        Usuario usuario = usuarioId != null ? usuarioRepository.findById(usuarioId).orElse(null) : null;
        String userStr = username != null ? username : (usuario != null ? usuario.getUsername() : "sistema");

        AuditoriaCalificacion auditoria = new AuditoriaCalificacion(
                TipoEntidadAuditoriaCalificacion.PERIODO,
                calif.getId(),
                calif.getMatricula(),
                valorAnterior,
                notaRecuperacion,
                motivo != null ? motivo : "Registro de plan de apoyo / recuperación periódica",
                usuario,
                userStr
        );
        auditoriaRepository.save(auditoria);

        calif.setNotaRecuperacion(notaRecuperacion);
        BigDecimal notaFinal = notaRecuperacion.max(calif.getNotaDefinitiva());
        calif.setNotaFinal(notaFinal);
        calif.setDesempeno(escala.homologarDesempeno(notaFinal));
        calif.setAprobada(escala.esAprobada(notaFinal));

        CalificacionPeriodo actualizada = calificacionPeriodoRepository.save(calif);

        if (calif.getAsignatura().getArea() != null) {
            consolidarAreaCurricular(calif.getMatricula(), calif.getAsignatura().getArea(), periodo, escala);
        }

        return calificacionMapper.toResponse(actualizada);
    }

    @Transactional(readOnly = true)
    public ConsultaCalificacionesEstudianteResponse consultarMisCalificacionesEstudiante(Long usuarioId, Long anioLectivoId, Long periodoId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con ID " + usuarioId));

        if (usuario.getPersonaTipo() != TipoPersonaUsuario.ESTUDIANTE || usuario.getPersonaId() == null) {
            throw new AccessDeniedException("El usuario autenticado no posee perfil de Estudiante activo (UC-02 E2)");
        }

        Estudiante estudiante = estudianteRepository.findById(usuario.getPersonaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Expediente de estudiante no encontrado"));

        return construirConsultaEstudiante(estudiante, anioLectivoId, periodoId);
    }

    @Transactional(readOnly = true)
    public ConsultaCalificacionesEstudianteResponse consultarCalificacionesAcudiente(Long usuarioId, Long estudianteId, Long anioLectivoId, Long periodoId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con ID " + usuarioId));

        if (usuario.getPersonaTipo() != TipoPersonaUsuario.ACUDIENTE || usuario.getPersonaId() == null) {
            throw new AccessDeniedException("El usuario autenticado no posee perfil de Acudiente activo (UC-08 E1)");
        }

        Estudiante estudiante = estudianteRepository.findById(estudianteId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Estudiante no encontrado con ID " + estudianteId));

        // Validar vínculo activo y verificado en servidor (UC-08 E1, RN-09, RN-EST-02)
        List<EstudianteAcudiente> vinculos = estudianteAcudienteRepository.findByEstudiante(estudiante);
        boolean vinculoValido = vinculos.stream().anyMatch(v ->
                v.getAcudiente().getId().equals(usuario.getPersonaId()) &&
                        v.getEstadoVinculo() == EstadoVinculo.VERIFICADO
        );

        if (!vinculoValido) {
            throw new AccessDeniedException("Acceso denegado: el acudiente no posee vínculo verificado y activo con el menor o registra restricción legal (UC-08 E1)");
        }

        return construirConsultaEstudiante(estudiante, anioLectivoId, periodoId);
    }

    private ConsultaCalificacionesEstudianteResponse construirConsultaEstudiante(Estudiante estudiante, Long anioLectivoId, Long periodoId) {
        PeriodoAcademico periodo = periodoRepository.findById(periodoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Periodo académico no encontrado con ID " + periodoId));

        Long anioId = anioLectivoId != null ? anioLectivoId : periodo.getAnioLectivo().getId();
        AnioLectivo anio = anioLectivoRepository.findById(anioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Año lectivo no encontrado"));

        Matricula matricula = matriculaRepository
                .findByEstudianteIdAndAnioLectivoIdAndEstado(estudiante.getId(), anio.getId(), EstadoMatricula.MATRICULADA)
                .orElseThrow(() -> new ConflictoNegocioException("El estudiante no registra matrícula activa para el año lectivo seleccionado (UC-02 E1)"));

        ConfiguracionEscalaSiee escala = obtenerEscalaVigentePorAnio(anio.getId());

        ConsultaCalificacionesEstudianteResponse resp = new ConsultaCalificacionesEstudianteResponse();
        resp.setMatriculaId(matricula.getId());
        resp.setNumeroMatricula(matricula.getNumeroMatricula());
        resp.setEstudianteId(estudiante.getId());
        resp.setEstudianteNombreCompleto(estudiante.getNombres() + " " + estudiante.getApellidos());
        resp.setEstudianteDocumento(estudiante.getDocumento());
        resp.setAnioLectivoId(anio.getId());
        resp.setAnioLectivo(anio.getAnio());
        resp.setPeriodoAcademicoId(periodo.getId());
        resp.setPeriodoNombre(periodo.getNombre());
        resp.setPeriodoNumero(periodo.getNumero());
        resp.setEstadoPeriodo(periodo.getEstado().name());

        // Obtener definitivas de áreas
        List<CalificacionAreaPeriodo> areasPeriodo = calificacionAreaPeriodoRepository
                .findByMatriculaIdAndPeriodoAcademicoId(matricula.getId(), periodo.getId());
        Map<Long, CalificacionAreaPeriodo> areaMap = areasPeriodo.stream()
                .collect(Collectors.toMap(cap -> cap.getArea().getId(), cap -> cap, (a, b) -> a));

        // Obtener definitivas de asignaturas
        List<CalificacionPeriodo> asigPeriodo = calificacionPeriodoRepository
                .findByMatriculaIdAndPeriodoAcademicoId(matricula.getId(), periodo.getId());
        Map<Long, CalificacionPeriodo> asigMap = asigPeriodo.stream()
                .collect(Collectors.toMap(cp -> cp.getAsignatura().getId(), cp -> cp, (a, b) -> a));

        // Plan de estudios del grado
        List<GradoAsignatura> gradoAsignaturas = gradoAsignaturaRepository
                .findByGradoIdAndAnioLectivoIdAndActivoTrue(matricula.getGrupo().getGrado().getId(), anio.getId());

        if (gradoAsignaturas.isEmpty()) {
            gradoAsignaturas = gradoAsignaturaRepository.findByGradoIdAndActivoTrue(matricula.getGrupo().getGrado().getId());
        }

        Map<Area, List<GradoAsignatura>> porArea = gradoAsignaturas.stream()
                .collect(Collectors.groupingBy(ga -> ga.getAsignatura().getArea() != null ? ga.getAsignatura().getArea() : new Area()));

        for (Map.Entry<Area, List<GradoAsignatura>> entry : porArea.entrySet()) {
            Area area = entry.getKey();
            ConsultaCalificacionesEstudianteResponse.AreaConsultaItem areaItem = new ConsultaCalificacionesEstudianteResponse.AreaConsultaItem();
            areaItem.setAreaId(area.getId());
            areaItem.setAreaNombre(area.getNombre());

            CalificacionAreaPeriodo cap = areaMap.get(area.getId());
            if (cap != null) {
                areaItem.setNotaDefinitiva(cap.getNotaDefinitiva());
                areaItem.setDesempeno(cap.getDesempeno());
                areaItem.setAprobada(cap.getAprobada());
            }

            for (GradoAsignatura ga : entry.getValue()) {
                ConsultaCalificacionesEstudianteResponse.AsignaturaConsultaItem asigItem = new ConsultaCalificacionesEstudianteResponse.AsignaturaConsultaItem();
                asigItem.setAsignaturaId(ga.getAsignatura().getId());
                asigItem.setAsignaturaNombre(ga.getAsignatura().getNombre());

                CalificacionPeriodo cp = asigMap.get(ga.getAsignatura().getId());
                if (cp != null) {
                    asigItem.setNotaDefinitiva(cp.getNotaDefinitiva());
                    asigItem.setDesempeno(cp.getDesempeno());
                    asigItem.setAprobada(cp.getAprobada());
                    asigItem.setNotaRecuperacion(cp.getNotaRecuperacion());
                    asigItem.setNotaFinal(cp.getNotaFinal());
                }

                // Actividades evaluativas publicadas para la asignatura
                List<CalificacionActividad> notasActividades = calificacionActividadRepository
                        .findByActividadPeriodoAcademicoIdAndMatriculaId(periodo.getId(), matricula.getId()).stream()
                        .filter(ca -> ca.getActividad().getAsignacionAcademica().getAsignatura().getId().equals(ga.getAsignatura().getId()))
                        .filter(ca -> ca.getActividad().getEstado() == EstadoActividadEvaluativa.PUBLICADA)
                        .toList();

                asigItem.setActividades(notasActividades.stream()
                        .map(na -> calificacionMapper.toResponse(na, escala))
                        .toList());

                areaItem.getAsignaturas().add(asigItem);
            }

            resp.getAreas().add(areaItem);
        }

        // Nivelaciones del estudiante
        resp.setNivelaciones(actaNivelacionRepository.findByMatriculaIdAndPeriodoAcademicoId(matricula.getId(), periodo.getId())
                .stream().map(calificacionMapper::toResponse).toList());

        return resp;
    }

    // =========================================================================
    // 6. SÁBANA CONSOLIDADA, AVANCE DOCENTE Y ALERTAS (UC-PROV-17)
    // =========================================================================

    @Transactional(readOnly = true)
    public SabanaConsolidadaResponse consultarSabanaConsolidada(Long grupoId, Long periodoId, Long usuarioId, String rol) {
        PeriodoAcademico periodo = periodoRepository.findById(periodoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Periodo académico no encontrado con ID " + periodoId));

        List<Matricula> matriculados = matriculaRepository.findByGrupoIdAndEstado(grupoId, EstadoMatricula.MATRICULADA);
        if (matriculados.isEmpty()) {
            throw new ConflictoNegocioException("No existen estudiantes matriculados en el grupo para consolidar la sábana (UC-PROV-17 E2)");
        }

        Grupo grupo = matriculados.get(0).getGrupo();

        // UC-PROV-17 E1: Director de grupo contextual
        if ("PROFESOR".equalsIgnoreCase(rol)) {
            Profesor docente = profesorRepository.findByUsuarioId(usuarioId)
                    .orElseThrow(() -> new AccessDeniedException("Perfil de profesor no encontrado"));

            Optional<AsignacionAcademica> dirOpt = asignacionRepository
                    .findByGrupoIdAndEsDirectorGrupoTrueAndActivoTrue(grupo.getId());

            boolean esDirector = dirOpt.isPresent() && dirOpt.get().getProfesor() != null &&
                    dirOpt.get().getProfesor().getId().equals(docente.getId());

            if (!esDirector) {
                throw new AccessDeniedException("Acceso denegado: solo el Director de Grupo asignado o directivos pueden consultar la sábana consolidada del curso (UC-PROV-17 E1)");
            }
        }

        SabanaConsolidadaResponse response = new SabanaConsolidadaResponse();
        response.setGrupoId(grupo.getId());
        response.setGrupoNombre(grupo.getNombre());
        response.setPeriodoId(periodo.getId());
        response.setPeriodoNombre(periodo.getNombre());

        asignacionRepository.findByGrupoIdAndEsDirectorGrupoTrueAndActivoTrue(grupo.getId()).ifPresent(dir -> {
            if (dir.getProfesor() != null) {
                response.setDirectorGrupoNombre(dir.getProfesor().getNombres() + " " + dir.getProfesor().getApellidos());
            }
        });

        int enRiesgoContador = 0;

        for (Matricula mat : matriculados) {
            SabanaConsolidadaResponse.EstudianteSabanaItem estItem = new SabanaConsolidadaResponse.EstudianteSabanaItem();
            estItem.setMatriculaId(mat.getId());
            estItem.setNumeroMatricula(mat.getNumeroMatricula());
            estItem.setEstudianteId(mat.getEstudiante().getId());
            estItem.setEstudianteNombreCompleto(mat.getEstudiante().getNombres() + " " + mat.getEstudiante().getApellidos());

            List<CalificacionPeriodo> notasAsig = calificacionPeriodoRepository.findByMatriculaIdAndPeriodoAcademicoId(mat.getId(), periodo.getId());
            List<CalificacionAreaPeriodo> notasArea = calificacionAreaPeriodoRepository.findByMatriculaIdAndPeriodoAcademicoId(mat.getId(), periodo.getId());

            int reprobadas = 0;
            BigDecimal sumaNotas = BigDecimal.ZERO;
            int totalAsigs = 0;

            for (CalificacionPeriodo cp : notasAsig) {
                BigDecimal notaF = cp.getNotaFinal() != null ? cp.getNotaFinal() : cp.getNotaDefinitiva();
                sumaNotas = sumaNotas.add(notaF);
                totalAsigs++;
                if (cp.getDesempeno() == DesempenoNacional.BAJO) {
                    reprobadas++;
                }
            }

            estItem.setAsignaturasReprobadas(reprobadas);
            boolean enRiesgo = reprobadas > 0;
            estItem.setEnRiesgoAcademico(enRiesgo);
            if (enRiesgo) {
                enRiesgoContador++;
            }

            if (totalAsigs > 0) {
                estItem.setPromedioGeneral(sumaNotas.divide(BigDecimal.valueOf(totalAsigs), 2, RoundingMode.HALF_UP));
            }

            // Mapear áreas
            for (CalificacionAreaPeriodo cap : notasArea) {
                SabanaConsolidadaResponse.AreaSabanaItem aItem = new SabanaConsolidadaResponse.AreaSabanaItem();
                aItem.setAreaId(cap.getArea().getId());
                aItem.setAreaNombre(cap.getArea().getNombre());
                aItem.setNotaDefinitiva(cap.getNotaDefinitiva());
                aItem.setDesempeno(cap.getDesempeno());
                aItem.setAprobada(cap.getAprobada());
                estItem.getAreas().add(aItem);
            }

            response.getEstudiantes().add(estItem);
        }

        response.setTotalEstudiantes(matriculados.size());
        response.setTotalEstudiantesEnRiesgo(enRiesgoContador);
        return response;
    }

    @Transactional(readOnly = true)
    public List<TableroAvanceDocenteResponse> consultarTableroAvanceDocente(Long anioLectivoId, Long periodoId) {
        PeriodoAcademico periodo = periodoRepository.findById(periodoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Periodo no encontrado con ID " + periodoId));

        List<AsignacionAcademica> asignaciones = asignacionRepository.findAll().stream()
                .filter(a -> Boolean.TRUE.equals(a.getActivo()))
                .filter(a -> a.getGrupo() != null && a.getGrupo().getAnioLectivo() != null &&
                        a.getGrupo().getAnioLectivo().getId().equals(anioLectivoId))
                .toList();

        List<TableroAvanceDocenteResponse> tablero = new ArrayList<>();

        for (AsignacionAcademica asig : asignaciones) {
            TableroAvanceDocenteResponse item = new TableroAvanceDocenteResponse();
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

            List<ActividadEvaluativa> acts = actividadRepository
                    .findByAsignacionAcademicaIdAndPeriodoAcademicoIdAndActivoTrue(asig.getId(), periodo.getId());

            BigDecimal porcentajeSum = acts.stream().map(ActividadEvaluativa::getPorcentaje).reduce(BigDecimal.ZERO, BigDecimal::add);
            item.setPorcentajePlaneado(porcentajeSum);
            item.setTotalActividades(acts.size());
            item.setPlaneacionCompleta(porcentajeSum.compareTo(new BigDecimal("100.00")) == 0);

            List<Matricula> mats = matriculaRepository.findByGrupoIdAndEstado(asig.getGrupo().getId(), EstadoMatricula.MATRICULADA);
            item.setEstudiantesMatriculados(mats.size());

            int totalEsperadas = acts.size() * mats.size();
            int totalRegistradas = 0;
            for (ActividadEvaluativa act : acts) {
                totalRegistradas += calificacionActividadRepository.findByActividadId(act.getId()).size();
            }

            item.setCalificacionesRegistradas(totalRegistradas);
            item.setCalificacionesPendientes(Math.max(0, totalEsperadas - totalRegistradas));
            if (totalEsperadas > 0) {
                item.setPorcentajeAvance(BigDecimal.valueOf(totalRegistradas * 100.0 / totalEsperadas).setScale(2, RoundingMode.HALF_UP));
            } else {
                item.setPorcentajeAvance(BigDecimal.ZERO);
            }

            tablero.add(item);
        }

        return tablero;
    }

    // =========================================================================
    // 7. CONSULTAS GENERALES Y AUDITORÍA
    // =========================================================================

    @Transactional(readOnly = true)
    public List<CalificacionPeriodoResponse> listarCalificacionesPeriodo(Long grupoId, Long asignaturaId, Long periodoId) {
        return calificacionPeriodoRepository.findByAsignaturaIdAndPeriodoAcademicoId(asignaturaId, periodoId).stream()
                .filter(cp -> cp.getMatricula().getGrupo().getId().equals(grupoId))
                .map(calificacionMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CalificacionPeriodoResponse> listarCalificacionesEstudiantePeriodo(Long matriculaId, Long periodoId) {
        return calificacionPeriodoRepository.findByMatriculaIdAndPeriodoAcademicoId(matriculaId, periodoId).stream()
                .map(calificacionMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AuditoriaCalificacionResponse> listarHistorialAuditoriaMatricula(Long matriculaId) {
        return auditoriaRepository.findByMatriculaIdOrderByFechaAuditoriaDesc(matriculaId).stream()
                .map(calificacionMapper::toResponse)
                .toList();
    }

    // =========================================================================
    // VALIDACIONES PRIVADAS DE DOMINIO SIEE Y RBAC CONTEXTUAL
    // =========================================================================

    private void validarDocenteAsignado(AsignacionAcademica asignacion, Long usuarioId, String rol) {
        if (rol == null || !"PROFESOR".equalsIgnoreCase(rol)) {
            return; // Administradores, rectores y coordinadores tienen alcance institucional
        }

        if (usuarioId == null) {
            throw new AccessDeniedException("Acceso denegado: usuario no autenticado (UC-16 E5)");
        }

        Profesor docente = profesorRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new AccessDeniedException("Acceso denegado: no se encontró perfil de docente para el usuario en sesión (UC-16 E5)"));

        if (asignacion.getProfesor() == null || !asignacion.getProfesor().getId().equals(docente.getId())) {
            throw new AccessDeniedException("Acceso denegado: el docente no tiene asignación académica activa sobre este curso y asignatura (UC-16 E5, UC-17 E3)");
        }
    }

    private void validarCoherenciaEscala(ConfiguracionEscalaSieeRequest request) {
        BigDecimal min = request.getNotaMinima();
        BigDecimal max = request.getNotaMaxima();
        BigDecimal aprob = request.getNotaAprobatoria();
        BigDecimal bas = request.getEscalaBasicoMin();
        BigDecimal alt = request.getEscalaAltoMin();
        BigDecimal sup = request.getEscalaSuperiorMin();

        if (min.compareTo(bas) >= 0) {
            throw new ConflictoNegocioException("La nota mínima (" + min + ") debe ser estrictamente menor al umbral Básico (" + bas + ")");
        }
        if (bas.compareTo(aprob) > 0) {
            throw new ConflictoNegocioException("El umbral Básico (" + bas + ") no puede ser superior a la nota aprobatoria (" + aprob + ")");
        }
        if (bas.compareTo(alt) >= 0) {
            throw new ConflictoNegocioException("El umbral Básico (" + bas + ") debe ser menor al umbral Alto (" + alt + ")");
        }
        if (alt.compareTo(sup) >= 0) {
            throw new ConflictoNegocioException("El umbral Alto (" + alt + ") debe ser menor al umbral Superior (" + sup + ")");
        }
        if (sup.compareTo(max) > 0) {
            throw new ConflictoNegocioException("El umbral Superior (" + sup + ") no puede superar la nota máxima (" + max + ")");
        }
    }

    private void validarNotaEnRango(BigDecimal nota, ConfiguracionEscalaSiee escala) {
        if (nota == null) {
            throw new ConflictoNegocioException("La calificación no puede ser nula");
        }
        if (nota.compareTo(escala.getNotaMinima()) < 0 || nota.compareTo(escala.getNotaMaxima()) > 0) {
            throw new ConflictoNegocioException("La calificación (" + nota + ") está fuera del rango permitido por el SIEE [" +
                    escala.getNotaMinima() + " - " + escala.getNotaMaxima() + "] (UC-17 E1)");
        }
    }
}
