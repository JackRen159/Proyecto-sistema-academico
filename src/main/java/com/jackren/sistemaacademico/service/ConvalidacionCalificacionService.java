package com.jackren.sistemaacademico.service;

import com.jackren.sistemaacademico.dto.ConvalidacionCalificacionRequest;
import com.jackren.sistemaacademico.dto.ConvalidacionCalificacionResponse;
import com.jackren.sistemaacademico.dto.ConvalidacionDetalleRequest;
import com.jackren.sistemaacademico.entity.*;
import com.jackren.sistemaacademico.exception.ConflictoNegocioException;
import com.jackren.sistemaacademico.exception.RecursoNoEncontradoException;
import com.jackren.sistemaacademico.exception.ValidacionException;
import com.jackren.sistemaacademico.mapper.CalificacionMapper;
import com.jackren.sistemaacademico.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class ConvalidacionCalificacionService {

    private final ConvalidacionCalificacionRepository convalidacionRepository;
    private final CalificacionPeriodoRepository calificacionPeriodoRepository;
    private final AsignaturaRepository asignaturaRepository;
    private final MatriculaRepository matriculaRepository;
    private final PeriodoAcademicoRepository periodoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CalificacionService calificacionService;
    private final CalificacionMapper calificacionMapper;

    public ConvalidacionCalificacionService(ConvalidacionCalificacionRepository convalidacionRepository,
                                           CalificacionPeriodoRepository calificacionPeriodoRepository,
                                           AsignaturaRepository asignaturaRepository,
                                           MatriculaRepository matriculaRepository,
                                           PeriodoAcademicoRepository periodoRepository,
                                           UsuarioRepository usuarioRepository,
                                           CalificacionService calificacionService,
                                           CalificacionMapper calificacionMapper) {
        this.convalidacionRepository = convalidacionRepository;
        this.calificacionPeriodoRepository = calificacionPeriodoRepository;
        this.asignaturaRepository = asignaturaRepository;
        this.matriculaRepository = matriculaRepository;
        this.periodoRepository = periodoRepository;
        this.usuarioRepository = usuarioRepository;
        this.calificacionService = calificacionService;
        this.calificacionMapper = calificacionMapper;
    }

    public ConvalidacionCalificacionResponse registrarConvalidacion(ConvalidacionCalificacionRequest request, Long usuarioId) {
        Matricula matricula = matriculaRepository.findById(request.getMatriculaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Matrícula no encontrada con ID " + request.getMatriculaId()));

        PeriodoAcademico periodo = periodoRepository.findById(request.getPeriodoAcademicoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Periodo académico no encontrado con ID " + request.getPeriodoAcademicoId()));

        if (request.getDetalles() == null || request.getDetalles().isEmpty()) {
            throw new ValidacionException("Debe ingresar al menos una asignatura convalidada (UC-PROV-16)");
        }

        ConfiguracionEscalaSiee escala = calificacionService.obtenerEscalaVigentePorAnio(periodo.getAnioLectivo().getId());

        long count = convalidacionRepository.count() + 1;
        String numeroActa = String.format("ACT-CONV-%d-%04d", periodo.getAnioLectivo().getAnio(), count);

        Usuario coordinador = usuarioId != null ? usuarioRepository.findById(usuarioId).orElse(null) : null;

        ConvalidacionCalificacion convalidacion = new ConvalidacionCalificacion();
        convalidacion.setNumeroActa(numeroActa);
        convalidacion.setMatricula(matricula);
        convalidacion.setPeriodoAcademico(periodo);
        convalidacion.setTipoOrigen(request.getTipoOrigen());
        convalidacion.setInstitucionOrigen(request.getInstitucionOrigen());
        convalidacion.setActaSoporteUrl(request.getActaSoporteUrl());
        convalidacion.setObservaciones(request.getObservaciones());
        convalidacion.setCoordinador(coordinador);
        convalidacion.setFechaConvalidacion(request.getFechaConvalidacion() != null ? request.getFechaConvalidacion() : LocalDate.now());

        OrigenCalificacion origen = request.getTipoOrigen() == TipoOrigenConvalidacion.TRASLADO_ENTRANTE
                ? OrigenCalificacion.CONVALIDADA_TRASLADO
                : OrigenCalificacion.CONVALIDADA_PROMOCION_ANTICIPADA;

        for (ConvalidacionDetalleRequest detReq : request.getDetalles()) {
            Asignatura asig = asignaturaRepository.findById(detReq.getAsignaturaId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Asignatura no encontrada con ID " + detReq.getAsignaturaId()));

            if (detReq.getNotaConvalidada().compareTo(escala.getNotaMinima()) < 0 ||
                    detReq.getNotaConvalidada().compareTo(escala.getNotaMaxima()) > 0) {
                throw new ValidacionException("La nota convalidada (" + detReq.getNotaConvalidada() + ") está fuera de la escala institucional SIEE");
            }

            DesempenoNacional des = escala.homologarDesempeno(detReq.getNotaConvalidada());
            boolean aprobada = escala.esAprobada(detReq.getNotaConvalidada());

            ConvalidacionCalificacionDetalle det = new ConvalidacionCalificacionDetalle(
                    convalidacion, asig, detReq.getNotaConvalidada(), des, detReq.getObservaciones()
            );
            convalidacion.addDetalle(det);

            // Asentar CalificacionPeriodo
            CalificacionPeriodo cp = calificacionPeriodoRepository
                    .findByMatriculaIdAndAsignaturaIdAndPeriodoAcademicoId(matricula.getId(), asig.getId(), periodo.getId())
                    .orElseGet(() -> new CalificacionPeriodo(
                            matricula, asig, periodo, detReq.getNotaConvalidada(), des, aprobada
                    ));

            cp.setNotaDefinitiva(detReq.getNotaConvalidada());
            cp.setNotaFinal(detReq.getNotaConvalidada());
            cp.setDesempeno(des);
            cp.setAprobada(aprobada);
            cp.setOrigen(origen);
            cp.setObservaciones("Convalidación asentada mediante Acta " + numeroActa);
            calificacionPeriodoRepository.save(cp);

            // Consolidar Área Curricular
            if (asig.getArea() != null) {
                calificacionService.consolidarAreaCurricular(matricula, asig.getArea(), periodo, escala);
            }
        }

        ConvalidacionCalificacion guardada = convalidacionRepository.save(convalidacion);
        return calificacionMapper.toResponse(guardada);
    }

    @Transactional(readOnly = true)
    public List<ConvalidacionCalificacionResponse> listarConvalidacionesMatricula(Long matriculaId) {
        return convalidacionRepository.findByMatriculaId(matriculaId).stream()
                .map(calificacionMapper::toResponse)
                .toList();
    }
}
