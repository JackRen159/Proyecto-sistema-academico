package com.jackren.sistemaacademico.service;

import com.jackren.sistemaacademico.dto.ActaNivelacionRequest;
import com.jackren.sistemaacademico.dto.ActaNivelacionResponse;
import com.jackren.sistemaacademico.entity.*;
import com.jackren.sistemaacademico.exception.ConflictoNegocioException;
import com.jackren.sistemaacademico.exception.RecursoNoEncontradoException;
import com.jackren.sistemaacademico.exception.ValidacionException;
import com.jackren.sistemaacademico.mapper.CalificacionMapper;
import com.jackren.sistemaacademico.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class NivelacionCalificacionService {

    private final ActaNivelacionRepository actaNivelacionRepository;
    private final CalificacionPeriodoRepository calificacionPeriodoRepository;
    private final CalificacionService calificacionService;
    private final AuditoriaCalificacionRepository auditoriaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProfesorRepository profesorRepository;
    private final CalificacionMapper calificacionMapper;

    public NivelacionCalificacionService(ActaNivelacionRepository actaNivelacionRepository,
                                         CalificacionPeriodoRepository calificacionPeriodoRepository,
                                         CalificacionService calificacionService,
                                         AuditoriaCalificacionRepository auditoriaRepository,
                                         UsuarioRepository usuarioRepository,
                                         ProfesorRepository profesorRepository,
                                         CalificacionMapper calificacionMapper) {
        this.actaNivelacionRepository = actaNivelacionRepository;
        this.calificacionPeriodoRepository = calificacionPeriodoRepository;
        this.calificacionService = calificacionService;
        this.auditoriaRepository = auditoriaRepository;
        this.usuarioRepository = usuarioRepository;
        this.profesorRepository = profesorRepository;
        this.calificacionMapper = calificacionMapper;
    }

    public ActaNivelacionResponse registrarActaNivelacion(ActaNivelacionRequest request, Long usuarioId, String username) {
        CalificacionPeriodo calif;
        if (request.getCalificacionPeriodoId() != null) {
            calif = calificacionPeriodoRepository.findById(request.getCalificacionPeriodoId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Calificación de periodo no encontrada con ID " + request.getCalificacionPeriodoId()));
        } else if (request.getMatriculaId() != null && request.getAsignaturaId() != null && request.getPeriodoId() != null) {
            calif = calificacionPeriodoRepository.findByMatriculaIdAndAsignaturaIdAndPeriodoAcademicoId(
                    request.getMatriculaId(), request.getAsignaturaId(), request.getPeriodoId()
            ).orElseThrow(() -> new RecursoNoEncontradoException("No existe calificación de periodo registrada para el estudiante, asignatura y periodo indicados"));
        } else {
            throw new ValidacionException("Debe proporcionar calificacionPeriodoId o la terna (matriculaId, asignaturaId, periodoId)");
        }

        if (request.getFechaSustentacion() == null) {
            request.setFechaSustentacion(LocalDate.now());
        }

        PeriodoAcademico periodo = calif.getPeriodoAcademico();
        AnioLectivo anio = periodo.getAnioLectivo();
        ConfiguracionEscalaSiee escala = calificacionService.obtenerEscalaVigentePorAnio(anio.getId());

        // UC-18 E3: Control de duplicidad de acta para el mismo corte
        if (actaNivelacionRepository.existsByMatriculaIdAndAsignaturaIdAndPeriodoAcademicoId(
                calif.getMatricula().getId(), calif.getAsignatura().getId(), periodo.getId())) {
            throw new ConflictoNegocioException("Ya existe un Acta de Nivelación registrada para este estudiante, asignatura y periodo (UC-18 E3)");
        }

        BigDecimal notaOriginal = calif.getNotaDefinitiva();
        BigDecimal notaObtenida = request.getNotaObtenida();
        boolean asistencia = Boolean.TRUE.equals(request.getAsistencia());

        if (asistencia && notaObtenida != null) {
            if (notaObtenida.compareTo(escala.getNotaMinima()) < 0 || notaObtenida.compareTo(escala.getNotaMaxima()) > 0) {
                throw new ValidacionException("La calificación de nivelación (" + notaObtenida + ") está fuera del rango permitido [" +
                        escala.getNotaMinima() + " - " + escala.getNotaMaxima() + "] (UC-18 E4)");
            }
        }

        // Determinar fórmula configurada en el SIEE institucional (RN-CFG-04)
        FormulaRecuperacionSiee formula = FormulaRecuperacionSiee.REEMPLAZO_TOTAL;

        BigDecimal notaCalculada;
        if (!asistencia || notaObtenida == null) {
            notaCalculada = notaOriginal;
        } else {
            switch (formula) {
                case TOPE_BASICO -> {
                    BigDecimal notaConTope = notaObtenida.min(escala.getEscalaBasicoMin());
                    notaCalculada = notaOriginal.max(notaConTope);
                }
                case PROMEDIO_PONDERADO -> {
                    BigDecimal pondOriginal = notaOriginal.multiply(new BigDecimal("0.40"));
                    BigDecimal pondRecup = notaObtenida.multiply(new BigDecimal("0.60"));
                    BigDecimal pondSuma = pondOriginal.add(pondRecup).setScale(escala.getDecimalesRedondeo(), RoundingMode.HALF_UP);
                    notaCalculada = notaOriginal.max(pondSuma);
                }
                default -> notaCalculada = notaOriginal.max(notaObtenida); // REEMPLAZO_TOTAL con no desmejoramiento
            }
        }

        BigDecimal notaFinalResultante = escala.redondear(notaCalculada);
        DesempenoNacional desempenoResultante = escala.homologarDesempeno(notaFinalResultante);
        boolean aprobadaResultante = escala.esAprobada(notaFinalResultante);

        // Generar número de acta único
        long consecutivo = actaNivelacionRepository.countByPeriodoAcademicoId(periodo.getId()) + 1;
        String numeroActa = String.format("ACT-NIV-%d-%04d", anio.getAnio(), consecutivo);

        Usuario usuario = usuarioId != null ? usuarioRepository.findById(usuarioId).orElse(null) : null;
        Profesor docente = null;
        if (usuario != null && usuario.getPersonaTipo() == TipoPersonaUsuario.PROFESOR && usuario.getPersonaId() != null) {
            docente = profesorRepository.findById(usuario.getPersonaId()).orElse(null);
        }

        ActaNivelacion acta = new ActaNivelacion();
        acta.setNumeroActa(numeroActa);
        acta.setCalificacionPeriodo(calif);
        acta.setMatricula(calif.getMatricula());
        acta.setAsignatura(calif.getAsignatura());
        acta.setPeriodoAcademico(periodo);
        acta.setDocente(docente);
        acta.setFechaSustentacion(request.getFechaSustentacion() != null ? request.getFechaSustentacion() : LocalDate.now());
        acta.setAsistencia(asistencia);
        acta.setNotaOriginal(notaOriginal);
        acta.setNotaObtenida(notaObtenida);
        acta.setFormulaAplicada(formula);
        acta.setNotaFinalResultante(notaFinalResultante);
        acta.setDesempenoResultante(desempenoResultante);
        acta.setEstrategiaPedagogica(request.getEstrategiaPedagogica());
        acta.setEvidenciasSoporte(request.getEvidenciasSoporte());
        acta.setObservaciones(request.getObservaciones());
        acta.setUsuarioRegistro(usuario);

        ActaNivelacion guardada = actaNivelacionRepository.save(acta);

        // Actualizar CalificacionPeriodo
        calif.setNotaRecuperacion(notaObtenida);
        calif.setNotaFinal(notaFinalResultante);
        calif.setDesempeno(desempenoResultante);
        calif.setAprobada(aprobadaResultante);
        calificacionPeriodoRepository.save(calif);

        // Consolidar Área Curricular actualizada
        if (calif.getAsignatura().getArea() != null) {
            calificacionService.consolidarAreaCurricular(calif.getMatricula(), calif.getAsignatura().getArea(), periodo, escala);
        }

        // Auditoría inmutable (RN-10)
        String userStr = username != null ? username : (usuario != null ? usuario.getUsername() : "sistema");
        AuditoriaCalificacion auditoria = new AuditoriaCalificacion(
                TipoEntidadAuditoriaCalificacion.PERIODO,
                calif.getId(),
                calif.getMatricula(),
                notaOriginal,
                notaFinalResultante,
                "Registro de Acta de Nivelación " + numeroActa + " con fórmula " + formula,
                usuario,
                userStr
        );
        auditoriaRepository.save(auditoria);

        return calificacionMapper.toResponse(guardada);
    }

    @Transactional(readOnly = true)
    public List<ActaNivelacionResponse> listarActasPorMatricula(Long matriculaId) {
        return actaNivelacionRepository.findByMatriculaId(matriculaId).stream()
                .map(calificacionMapper::toResponse)
                .toList();
    }
}
