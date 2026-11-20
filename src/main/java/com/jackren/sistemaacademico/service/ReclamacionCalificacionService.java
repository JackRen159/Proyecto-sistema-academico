package com.jackren.sistemaacademico.service;

import com.jackren.sistemaacademico.dto.ReclamacionCalificacionRequest;
import com.jackren.sistemaacademico.dto.ReclamacionCalificacionResponse;
import com.jackren.sistemaacademico.dto.ResolverReclamacionRequest;
import com.jackren.sistemaacademico.entity.*;
import com.jackren.sistemaacademico.exception.ConflictoNegocioException;
import com.jackren.sistemaacademico.exception.RecursoNoEncontradoException;
import com.jackren.sistemaacademico.exception.ValidacionException;
import com.jackren.sistemaacademico.mapper.CalificacionMapper;
import com.jackren.sistemaacademico.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ReclamacionCalificacionService {

    private final ReclamacionCalificacionRepository reclamacionRepository;
    private final CalificacionActividadRepository calificacionActividadRepository;
    private final CalificacionPeriodoRepository calificacionPeriodoRepository;
    private final AuditoriaCalificacionRepository auditoriaRepository;
    private final MatriculaRepository matriculaRepository;
    private final UsuarioRepository usuarioRepository;
    private final CalificacionService calificacionService;
    private final CalificacionMapper calificacionMapper;

    public ReclamacionCalificacionService(ReclamacionCalificacionRepository reclamacionRepository,
                                         CalificacionActividadRepository calificacionActividadRepository,
                                         CalificacionPeriodoRepository calificacionPeriodoRepository,
                                         AuditoriaCalificacionRepository auditoriaRepository,
                                         MatriculaRepository matriculaRepository,
                                         UsuarioRepository usuarioRepository,
                                         CalificacionService calificacionService,
                                         CalificacionMapper calificacionMapper) {
        this.reclamacionRepository = reclamacionRepository;
        this.calificacionActividadRepository = calificacionActividadRepository;
        this.calificacionPeriodoRepository = calificacionPeriodoRepository;
        this.auditoriaRepository = auditoriaRepository;
        this.matriculaRepository = matriculaRepository;
        this.usuarioRepository = usuarioRepository;
        this.calificacionService = calificacionService;
        this.calificacionMapper = calificacionMapper;
    }

    public ReclamacionCalificacionResponse radicarReclamacion(ReclamacionCalificacionRequest request, Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario solicitante no encontrado con ID " + usuarioId));

        Matricula matricula = matriculaRepository.findById(request.getMatriculaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Matrícula no encontrada con ID " + request.getMatriculaId()));

        RolSolicitanteReclamacion rolSolicitante = usuario.getPersonaTipo() == TipoPersonaUsuario.ESTUDIANTE
                ? RolSolicitanteReclamacion.ESTUDIANTE
                : RolSolicitanteReclamacion.ACUDIENTE;

        // UC-PROV-15 E2: Control de duplicidad - no más de una reclamación activa sobre la misma calificación
        List<EstadoReclamacionCalificacion> estadosActivos = List.of(
                EstadoReclamacionCalificacion.RADICADA_PRIMERA_INSTANCIA,
                EstadoReclamacionCalificacion.ESCALADA_SEGUNDA_INSTANCIA
        );

        if (reclamacionRepository.existsByTipoCalificacionAndReferenciaIdAndEstadoIn(
                request.getTipoCalificacion(), request.getReferenciaId(), estadosActivos)) {
            throw new ConflictoNegocioException("Ya existe una reclamación activa en trámite sobre esta calificación (UC-PROV-15 E2)");
        }

        long countTotal = reclamacionRepository.count() + 1;
        String numeroRadicado = String.format("REC-%d-%05d", LocalDate.now().getYear(), countTotal);

        // Fecha límite legal en días hábiles (por defecto 3 a 15 días hábiles, Ley 1581 Art. 15, RN-CAL-09)
        LocalDate fechaLimite = LocalDate.now().plusDays(5);

        ReclamacionCalificacion rec = new ReclamacionCalificacion();
        rec.setNumeroRadicado(numeroRadicado);
        rec.setTipoCalificacion(request.getTipoCalificacion());
        rec.setReferenciaId(request.getReferenciaId());
        rec.setMatricula(matricula);
        rec.setSolicitanteUsuario(usuario);
        rec.setRolSolicitante(rolSolicitante);
        rec.setMotivoReclamacion(request.getMotivoReclamacion());
        rec.setSoporteUrl(request.getSoporteUrl());
        rec.setEstado(EstadoReclamacionCalificacion.RADICADA_PRIMERA_INSTANCIA);
        rec.setFechaLimiteRespuesta(fechaLimite);

        ReclamacionCalificacion guardada = reclamacionRepository.save(rec);
        return calificacionMapper.toResponse(guardada);
    }

    public ReclamacionCalificacionResponse resolverPrimeraInstancia(Long reclamacionId,
                                                                   ResolverReclamacionRequest request,
                                                                   Long usuarioId,
                                                                   String username) {
        ReclamacionCalificacion rec = reclamacionRepository.findById(reclamacionId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Reclamación no encontrada con ID " + reclamacionId));

        if (rec.getEstado() != EstadoReclamacionCalificacion.RADICADA_PRIMERA_INSTANCIA) {
            throw new ConflictoNegocioException("La reclamación no se encuentra en estado RADICADA_PRIMERA_INSTANCIA");
        }

        if (request.getMotivo() == null || request.getMotivo().trim().isEmpty()) {
            throw new ValidacionException("La resolución de una reclamación académica exige motivación escrita obligatoria (UC-PROV-15 E3)");
        }

        Usuario docente = usuarioRepository.findById(usuarioId).orElse(null);

        rec.setDecisionPrimeraInstancia(request.getDecision());
        rec.setMotivoPrimeraInstancia(request.getMotivo());
        rec.setFechaDecisionPrimeraInstancia(LocalDateTime.now());
        rec.setDocenteResolutor(docente);

        if (request.getDecision() == DecisionReclamacion.APROBAR_RECTIFICACION) {
            if (request.getNuevaNotaAprobada() == null) {
                throw new ValidacionException("Debe especificar la nueva calificación aprobada al aceptar la rectificación");
            }
            rec.setNuevaNotaAprobada(request.getNuevaNotaAprobada());
            rec.setEstado(EstadoReclamacionCalificacion.RESUELTA_FAVORABLE_DOCENTE);

            // Rectificación atómica de la calificación (UC-17 A2)
            aplicarRectificacionNota(rec, request.getNuevaNotaAprobada(), docente, username);
        } else {
            rec.setEstado(EstadoReclamacionCalificacion.RECHAZADA_DOCENTE);
        }

        ReclamacionCalificacion guardada = reclamacionRepository.save(rec);
        return calificacionMapper.toResponse(guardada);
    }

    public ReclamacionCalificacionResponse escalarSegundaInstancia(Long reclamacionId, Long usuarioId) {
        ReclamacionCalificacion rec = reclamacionRepository.findById(reclamacionId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Reclamación no encontrada con ID " + reclamacionId));

        if (rec.getEstado() != EstadoReclamacionCalificacion.RECHAZADA_DOCENTE &&
                rec.getEstado() != EstadoReclamacionCalificacion.RADICADA_PRIMERA_INSTANCIA) {
            throw new ConflictoNegocioException("Solo se pueden escalar a segunda instancia reclamaciones rechazadas o con plazo vencido");
        }

        rec.setEstado(EstadoReclamacionCalificacion.ESCALADA_SEGUNDA_INSTANCIA);
        ReclamacionCalificacion guardada = reclamacionRepository.save(rec);
        return calificacionMapper.toResponse(guardada);
    }

    public ReclamacionCalificacionResponse resolverSegundaInstancia(Long reclamacionId,
                                                                   ResolverReclamacionRequest request,
                                                                   Long usuarioId,
                                                                   String username) {
        ReclamacionCalificacion rec = reclamacionRepository.findById(reclamacionId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Reclamación no encontrada con ID " + reclamacionId));

        if (rec.getEstado() != EstadoReclamacionCalificacion.ESCALADA_SEGUNDA_INSTANCIA) {
            throw new ConflictoNegocioException("La reclamación no se encuentra en estado ESCALADA_SEGUNDA_INSTANCIA");
        }

        if (request.getMotivo() == null || request.getMotivo().trim().isEmpty()) {
            throw new ValidacionException("La resolución en segunda instancia exige motivación escrita obligatoria");
        }

        Usuario coordinador = usuarioRepository.findById(usuarioId).orElse(null);

        rec.setDecisionSegundaInstancia(request.getDecision());
        rec.setMotivoSegundaInstancia(request.getMotivo());
        rec.setFechaDecisionSegundaInstancia(LocalDateTime.now());
        rec.setCoordinadorResolutor(coordinador);

        if (request.getDecision() == DecisionReclamacion.APROBAR_RECTIFICACION) {
            if (request.getNuevaNotaAprobada() == null) {
                throw new ValidacionException("Debe especificar la nueva calificación aprobada al aceptar la rectificación");
            }
            rec.setNuevaNotaAprobada(request.getNuevaNotaAprobada());
            rec.setEstado(EstadoReclamacionCalificacion.RESUELTA_FAVORABLE_COORDINACION);

            // Rectificación atómica
            aplicarRectificacionNota(rec, request.getNuevaNotaAprobada(), coordinador, username);
        } else {
            rec.setEstado(EstadoReclamacionCalificacion.RECHAZADA_COORDINACION);
        }

        ReclamacionCalificacion guardada = reclamacionRepository.save(rec);
        return calificacionMapper.toResponse(guardada);
    }

    private void aplicarRectificacionNota(ReclamacionCalificacion rec, BigDecimal nuevaNota, Usuario actor, String username) {
        String userStr = username != null ? username : (actor != null ? actor.getUsername() : "sistema");

        if (rec.getTipoCalificacion() == TipoCalificacionReclamacion.ACTIVIDAD) {
            CalificacionActividad ca = calificacionActividadRepository.findById(rec.getReferenciaId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Calificación de actividad no encontrada con ID " + rec.getReferenciaId()));

            BigDecimal valorAnterior = ca.getValor();
            ca.setValor(nuevaNota);
            calificacionActividadRepository.save(ca);

            AuditoriaCalificacion audit = new AuditoriaCalificacion(
                    TipoEntidadAuditoriaCalificacion.ACTIVIDAD,
                    ca.getId(),
                    ca.getMatricula(),
                    valorAnterior,
                    nuevaNota,
                    "Rectificación por Reclamación Favorable " + rec.getNumeroRadicado(),
                    actor,
                    userStr
            );
            auditoriaRepository.save(audit);

        } else if (rec.getTipoCalificacion() == TipoCalificacionReclamacion.PERIODO) {
            CalificacionPeriodo cp = calificacionPeriodoRepository.findById(rec.getReferenciaId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Calificación de periodo no encontrada con ID " + rec.getReferenciaId()));

            BigDecimal valorAnterior = cp.getNotaFinal() != null ? cp.getNotaFinal() : cp.getNotaDefinitiva();
            cp.setNotaDefinitiva(nuevaNota);
            cp.setNotaFinal(nuevaNota);

            ConfiguracionEscalaSiee escala = calificacionService.obtenerEscalaVigentePorAnio(cp.getPeriodoAcademico().getAnioLectivo().getId());
            cp.setDesempeno(escala.homologarDesempeno(nuevaNota));
            cp.setAprobada(escala.esAprobada(nuevaNota));
            calificacionPeriodoRepository.save(cp);

            // Consolidar Área
            if (cp.getAsignatura().getArea() != null) {
                calificacionService.consolidarAreaCurricular(cp.getMatricula(), cp.getAsignatura().getArea(), cp.getPeriodoAcademico(), escala);
            }

            AuditoriaCalificacion audit = new AuditoriaCalificacion(
                    TipoEntidadAuditoriaCalificacion.PERIODO,
                    cp.getId(),
                    cp.getMatricula(),
                    valorAnterior,
                    nuevaNota,
                    "Rectificación por Reclamación Favorable " + rec.getNumeroRadicado(),
                    actor,
                    userStr
            );
            auditoriaRepository.save(audit);
        }
    }

    @Transactional(readOnly = true)
    public List<ReclamacionCalificacionResponse> listarReclamacionesMatricula(Long matriculaId) {
        return reclamacionRepository.findByMatriculaId(matriculaId).stream()
                .map(calificacionMapper::toResponse)
                .toList();
    }
}
