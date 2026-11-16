package com.jackren.sistemaacademico.service;

import com.jackren.sistemaacademico.dto.*;
import com.jackren.sistemaacademico.entity.*;
import com.jackren.sistemaacademico.exception.ConflictoNegocioException;
import com.jackren.sistemaacademico.exception.RecursoNoEncontradoException;
import com.jackren.sistemaacademico.mapper.CalificacionMapper;
import com.jackren.sistemaacademico.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CalificacionService {

    private final ConfiguracionEscalaSieeRepository escalaRepository;
    private final ActividadEvaluativaRepository actividadRepository;
    private final CalificacionActividadRepository calificacionActividadRepository;
    private final CalificacionPeriodoRepository calificacionPeriodoRepository;
    private final AuditoriaCalificacionRepository auditoriaRepository;
    private final AsignacionAcademicaRepository asignacionRepository;
    private final PeriodoAcademicoRepository periodoRepository;
    private final AnioLectivoRepository anioLectivoRepository;
    private final MatriculaRepository matriculaRepository;
    private final UsuarioRepository usuarioRepository;
    private final CalificacionMapper calificacionMapper;

    public CalificacionService(ConfiguracionEscalaSieeRepository escalaRepository,
                               ActividadEvaluativaRepository actividadRepository,
                               CalificacionActividadRepository calificacionActividadRepository,
                               CalificacionPeriodoRepository calificacionPeriodoRepository,
                               AuditoriaCalificacionRepository auditoriaRepository,
                               AsignacionAcademicaRepository asignacionRepository,
                               PeriodoAcademicoRepository periodoRepository,
                               AnioLectivoRepository anioLectivoRepository,
                               MatriculaRepository matriculaRepository,
                               UsuarioRepository usuarioRepository,
                               CalificacionMapper calificacionMapper) {
        this.escalaRepository = escalaRepository;
        this.actividadRepository = actividadRepository;
        this.calificacionActividadRepository = calificacionActividadRepository;
        this.calificacionPeriodoRepository = calificacionPeriodoRepository;
        this.auditoriaRepository = auditoriaRepository;
        this.asignacionRepository = asignacionRepository;
        this.periodoRepository = periodoRepository;
        this.anioLectivoRepository = anioLectivoRepository;
        this.matriculaRepository = matriculaRepository;
        this.usuarioRepository = usuarioRepository;
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
                .orElseGet(() -> new ConfiguracionEscalaSiee());

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
        ConfiguracionEscalaSiee escala = escalaRepository.findByAnioLectivoId(anioLectivoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe escala SIEE configurada para el año lectivo ID " + anioLectivoId));
        return calificacionMapper.toResponse(escala);
    }

    public ConfiguracionEscalaSiee obtenerOCrearEscalaPorDefecto(AnioLectivo anioLectivo) {
        if (anioLectivo == null) {
            ConfiguracionEscalaSiee def = new ConfiguracionEscalaSiee();
            return def;
        }
        return escalaRepository.findByAnioLectivoId(anioLectivo.getId())
                .orElseGet(() -> {
                    ConfiguracionEscalaSiee def = new ConfiguracionEscalaSiee();
                    def.setAnioLectivo(anioLectivo);
                    def.setNotaMinima(new BigDecimal("1.00"));
                    def.setNotaMaxima(new BigDecimal("5.00"));
                    def.setNotaAprobatoria(new BigDecimal("3.00"));
                    def.setEscalaBasicoMin(new BigDecimal("3.00"));
                    def.setEscalaAltoMin(new BigDecimal("4.00"));
                    def.setEscalaSuperiorMin(new BigDecimal("4.60"));
                    def.setDecimalesRedondeo(1);
                    def.setDescripcion("Escala estándar SIEE generada automáticamente");
                    return escalaRepository.save(def);
                });
    }

    // =========================================================================
    // 2. GESTIÓN DE ACTIVIDADES EVALUATIVAS
    // =========================================================================

    public ActividadEvaluativaResponse crearActividad(ActividadEvaluativaRequest request) {
        AsignacionAcademica asignacion = asignacionRepository.findById(request.getAsignacionAcademicaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Asignación académica no encontrada con ID " + request.getAsignacionAcademicaId()));

        if (!Boolean.TRUE.equals(asignacion.getActivo())) {
            throw new ConflictoNegocioException("No se pueden crear actividades en una asignación académica inactiva");
        }

        PeriodoAcademico periodo = periodoRepository.findById(request.getPeriodoAcademicoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Periodo académico no encontrado con ID " + request.getPeriodoAcademicoId()));

        if (periodo.getEstado() != EstadoPeriodo.ABIERTO) {
            throw new ConflictoNegocioException("Solo se pueden crear actividades evaluativas en periodos académicos con estado ABIERTO");
        }

        if (asignacion.getGrupo().getAnioLectivo() == null ||
                !asignacion.getGrupo().getAnioLectivo().getId().equals(periodo.getAnioLectivo().getId())) {
            throw new ConflictoNegocioException("El periodo académico no corresponde al año lectivo de la asignación del grupo");
        }

        BigDecimal sumaActual = actividadRepository.sumPorcentajeByAsignacionAndPeriodo(asignacion.getId(), periodo.getId());
        if (sumaActual == null) {
            sumaActual = BigDecimal.ZERO;
        }
        BigDecimal nuevaSuma = sumaActual.add(request.getPorcentaje());
        if (nuevaSuma.compareTo(new BigDecimal("100.00")) > 0) {
            throw new ConflictoNegocioException("La suma acumulada de porcentajes para esta asignatura en el periodo superaría el 100% (actual: " +
                    sumaActual + "%, solicitada: " + request.getPorcentaje() + "%)");
        }

        ActividadEvaluativa actividad = calificacionMapper.toEntity(request, asignacion, periodo);
        ActividadEvaluativa guardada = actividadRepository.save(actividad);
        return calificacionMapper.toResponse(guardada);
    }

    @Transactional(readOnly = true)
    public List<ActividadEvaluativaResponse> listarActividadesPorAsignacionYPeriodo(Long asignacionId, Long periodoId) {
        return actividadRepository.findByAsignacionAcademicaIdAndPeriodoAcademicoIdAndActivoTrue(asignacionId, periodoId).stream()
                .map(calificacionMapper::toResponse)
                .toList();
    }

    // =========================================================================
    // 3. REGISTRO Y CALIFICACIÓN DE ACTIVIDADES
    // =========================================================================

    public List<CalificacionActividadResponse> registrarCalificacionesLote(RegistroCalificacionesLoteRequest request,
                                                                         Long usuarioId,
                                                                         String username) {
        ActividadEvaluativa actividad = actividadRepository.findById(request.getActividadId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Actividad evaluativa no encontrada con ID " + request.getActividadId()));

        if (!Boolean.TRUE.equals(actividad.getActivo())) {
            throw new ConflictoNegocioException("La actividad evaluativa está inactiva");
        }

        if (actividad.getPeriodoAcademico().getEstado() != EstadoPeriodo.ABIERTO) {
            throw new ConflictoNegocioException("No se pueden registrar calificaciones en un periodo académico cerrado");
        }

        AnioLectivo anio = actividad.getPeriodoAcademico().getAnioLectivo();
        ConfiguracionEscalaSiee escala = obtenerOCrearEscalaPorDefecto(anio);

        Grupo grupoAsignado = actividad.getAsignacionAcademica().getGrupo();
        Usuario usuario = usuarioId != null ? usuarioRepository.findById(usuarioId).orElse(null) : null;
        String userStr = username != null ? username : (usuario != null ? usuario.getUsername() : "sistema");

        List<CalificacionActividadResponse> responses = new ArrayList<>();

        for (RegistroCalificacionRequest califReq : request.getCalificaciones()) {
            validarNotaEnRango(califReq.getValor(), escala);

            Matricula matricula = matriculaRepository.findById(califReq.getMatriculaId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Matrícula no encontrada con ID " + califReq.getMatriculaId()));

            if (matricula.getEstado() != EstadoMatricula.MATRICULADA) {
                throw new ConflictoNegocioException("El estudiante con matrícula " + matricula.getNumeroMatricula() +
                        " no se encuentra en estado MATRICULADA");
            }

            if (!matricula.getGrupo().getId().equals(grupoAsignado.getId())) {
                throw new ConflictoNegocioException("La matrícula " + matricula.getNumeroMatricula() +
                        " pertenece al grupo '" + matricula.getGrupo().getNombre() + "', no al grupo de la actividad ('" +
                        grupoAsignado.getNombre() + "')");
            }

            Optional<CalificacionActividad> existenteOpt = calificacionActividadRepository
                    .findByActividadIdAndMatriculaId(actividad.getId(), matricula.getId());

            CalificacionActividad calificacion;
            if (existenteOpt.isPresent()) {
                calificacion = existenteOpt.get();
                BigDecimal valorAnterior = calificacion.getValor();
                if (valorAnterior.compareTo(califReq.getValor()) != 0) {
                    AuditoriaCalificacion auditoria = new AuditoriaCalificacion(
                            TipoEntidadAuditoriaCalificacion.ACTIVIDAD,
                            calificacion.getId(),
                            matricula,
                            valorAnterior,
                            califReq.getValor(),
                            "Actualización por registro de lote",
                            usuario,
                            userStr
                    );
                    auditoriaRepository.save(auditoria);
                }
                calificacion.setValor(califReq.getValor());
                calificacion.setObservaciones(califReq.getObservaciones());
            } else {
                calificacion = calificacionMapper.toEntity(califReq, actividad, matricula);
            }

            CalificacionActividad guardada = calificacionActividadRepository.save(calificacion);
            responses.add(calificacionMapper.toResponse(guardada, escala));
        }

        return responses;
    }

    public CalificacionActividadResponse actualizarCalificacionActividad(Long calificacionId,
                                                                         ActualizarCalificacionRequest request,
                                                                         Long usuarioId,
                                                                         String username) {
        CalificacionActividad calificacion = calificacionActividadRepository.findById(calificacionId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Calificación de actividad no encontrada con ID " + calificacionId));

        AnioLectivo anio = calificacion.getActividad().getPeriodoAcademico().getAnioLectivo();
        ConfiguracionEscalaSiee escala = obtenerOCrearEscalaPorDefecto(anio);

        validarNotaEnRango(request.getValorNuevo(), escala);

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
        return calificacionMapper.toResponse(actualizada, escala);
    }

    @Transactional(readOnly = true)
    public List<CalificacionActividadResponse> listarCalificacionesPorActividad(Long actividadId) {
        ActividadEvaluativa actividad = actividadRepository.findById(actividadId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Actividad evaluativa no encontrada con ID " + actividadId));
        ConfiguracionEscalaSiee escala = obtenerOCrearEscalaPorDefecto(actividad.getPeriodoAcademico().getAnioLectivo());
        return calificacionActividadRepository.findByActividadId(actividadId).stream()
                .map(c -> calificacionMapper.toResponse(c, escala))
                .toList();
    }

    // =========================================================================
    // 4. CÁLCULO Y CIERRE DE DEFINITIVAS DE PERIODO
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
        ConfiguracionEscalaSiee escala = obtenerOCrearEscalaPorDefecto(anio);

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
                    BigDecimal valor = notaOpt.get().getValor();
                    sumaPonderada = sumaPonderada.add(valor.multiply(act.getPorcentaje()));
                    pesoCalificado = pesoCalificado.add(act.getPorcentaje());
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
        }

        return responses;
    }

    public CalificacionPeriodoResponse registrarRecuperacion(Long calificacionPeriodoId,
                                                             BigDecimal notaRecuperacion,
                                                             String motivo,
                                                             Long usuarioId,
                                                             String username) {
        CalificacionPeriodo calificacion = calificacionPeriodoRepository.findById(calificacionPeriodoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Calificación de periodo no encontrada con ID " + calificacionPeriodoId));

        AnioLectivo anio = calificacion.getPeriodoAcademico().getAnioLectivo();
        ConfiguracionEscalaSiee escala = obtenerOCrearEscalaPorDefecto(anio);

        validarNotaEnRango(notaRecuperacion, escala);

        BigDecimal valorAnterior = calificacion.getNotaFinal() != null ? calificacion.getNotaFinal() : calificacion.getNotaDefinitiva();
        Usuario usuario = usuarioId != null ? usuarioRepository.findById(usuarioId).orElse(null) : null;
        String userStr = username != null ? username : (usuario != null ? usuario.getUsername() : "sistema");

        AuditoriaCalificacion auditoria = new AuditoriaCalificacion(
                TipoEntidadAuditoriaCalificacion.PERIODO,
                calificacion.getId(),
                calificacion.getMatricula(),
                valorAnterior,
                notaRecuperacion,
                motivo != null ? motivo : "Registro de plan de apoyo / recuperación periódica",
                usuario,
                userStr
        );
        auditoriaRepository.save(auditoria);

        calificacion.setNotaRecuperacion(notaRecuperacion);
        BigDecimal notaFinal = notaRecuperacion.max(calificacion.getNotaDefinitiva());
        calificacion.setNotaFinal(notaFinal);
        calificacion.setDesempeno(escala.homologarDesempeno(notaFinal));
        calificacion.setAprobada(escala.esAprobada(notaFinal));

        CalificacionPeriodo actualizada = calificacionPeriodoRepository.save(calificacion);
        return calificacionMapper.toResponse(actualizada);
    }

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
    // VALIDACIONES PRIVADAS DE DOMINIO SIEE
    // =========================================================================

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
                    escala.getNotaMinima() + " - " + escala.getNotaMaxima() + "]");
        }
    }
}
