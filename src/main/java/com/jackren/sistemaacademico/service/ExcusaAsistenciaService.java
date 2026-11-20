package com.jackren.sistemaacademico.service;

import com.jackren.sistemaacademico.dto.ResolverSolicitudExcusaRequest;
import com.jackren.sistemaacademico.dto.SolicitudExcusaAsistenciaRequest;
import com.jackren.sistemaacademico.dto.SolicitudExcusaAsistenciaResponse;
import com.jackren.sistemaacademico.entity.*;
import com.jackren.sistemaacademico.exception.ConflictoNegocioException;
import com.jackren.sistemaacademico.exception.RecursoNoEncontradoException;
import com.jackren.sistemaacademico.exception.ValidacionException;
import com.jackren.sistemaacademico.mapper.AsistenciaMapper;
import com.jackren.sistemaacademico.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ExcusaAsistenciaService {

    private final SolicitudExcusaAsistenciaRepository solicitudExcusaRepository;
    private final RegistroAsistenciaRepository registroRepository;
    private final CalificacionActividadRepository calificacionActividadRepository;
    private final MatriculaRepository matriculaRepository;
    private final UsuarioRepository usuarioRepository;
    private final AsistenciaMapper asistenciaMapper;

    public ExcusaAsistenciaService(SolicitudExcusaAsistenciaRepository solicitudExcusaRepository,
                                  RegistroAsistenciaRepository registroRepository,
                                  CalificacionActividadRepository calificacionActividadRepository,
                                  MatriculaRepository matriculaRepository,
                                  UsuarioRepository usuarioRepository,
                                  AsistenciaMapper asistenciaMapper) {
        this.solicitudExcusaRepository = solicitudExcusaRepository;
        this.registroRepository = registroRepository;
        this.calificacionActividadRepository = calificacionActividadRepository;
        this.matriculaRepository = matriculaRepository;
        this.usuarioRepository = usuarioRepository;
        this.asistenciaMapper = asistenciaMapper;
    }

    public SolicitudExcusaAsistenciaResponse radicarExcusa(SolicitudExcusaAsistenciaRequest request, Long usuarioId) {
        Matricula matricula = matriculaRepository.findById(request.getMatriculaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Matrícula no encontrada con ID " + request.getMatriculaId()));

        if (request.getFechaInicio().isAfter(request.getFechaFin())) {
            throw new ValidacionException("La fecha de inicio no puede ser posterior a la fecha de fin de la excusa (UC-PROV-18)");
        }

        Usuario solicitante = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con ID " + usuarioId));

        RolSolicitanteExcusa rolSolicitante = RolSolicitanteExcusa.ACUDIENTE;
        if (solicitante.getPersonaTipo() == TipoPersonaUsuario.ESTUDIANTE) {
            rolSolicitante = RolSolicitanteExcusa.ESTUDIANTE;
        } else if (solicitante.getPersonaTipo() == TipoPersonaUsuario.ADMINISTRATIVO) {
            rolSolicitante = RolSolicitanteExcusa.SECRETARIA;
        }

        long count = solicitudExcusaRepository.count() + 1;
        String numeroRadicado = String.format("EXC-%d-%05d", LocalDate.now().getYear(), count);

        SolicitudExcusaAsistencia excusa = new SolicitudExcusaAsistencia();
        excusa.setNumeroRadicado(numeroRadicado);
        excusa.setMatricula(matricula);
        excusa.setSolicitanteUsuario(solicitante);
        excusa.setRolSolicitante(rolSolicitante);
        excusa.setTipoJustificacion(request.getTipoJustificacion());
        excusa.setFechaInicio(request.getFechaInicio());
        excusa.setFechaFin(request.getFechaFin());
        excusa.setMotivoExplicacion(request.getMotivoExplicacion());
        excusa.setSoporteUrl(request.getSoporteUrl());
        excusa.setEsDatoSaludSensible(request.getEsDatoSaludSensible() != null ? request.getEsDatoSaludSensible() : true);
        excusa.setEstado(EstadoSolicitudExcusa.PENDIENTE_REVISION);

        SolicitudExcusaAsistencia guardada = solicitudExcusaRepository.save(excusa);
        return asistenciaMapper.toResponse(guardada);
    }

    public SolicitudExcusaAsistenciaResponse resolverExcusa(Long excusaId,
                                                           ResolverSolicitudExcusaRequest request,
                                                           Long usuarioId) {
        SolicitudExcusaAsistencia excusa = solicitudExcusaRepository.findById(excusaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Solicitud de excusa no encontrada con ID " + excusaId));

        if (excusa.getEstado() == EstadoSolicitudExcusa.APROBADA) {
            throw new ConflictoNegocioException("La solicitud de excusa ya se encuentra aprobada en firme");
        }

        Usuario resolutor = usuarioRepository.findById(usuarioId).orElse(null);
        excusa.setObservacionesResolucion(request.getObservacionesResolucion());
        excusa.setUsuarioResolutor(resolutor);
        excusa.setFechaResolucion(LocalDateTime.now());

        if (request.getDecision() == DecisionSolicitudExcusa.APROBAR) {
            excusa.setEstado(EstadoSolicitudExcusa.APROBADA);

            // 1. Actualizar transaccionalmente a AUSENCIA_JUSTIFICADA todas las sesiones del rango
            List<RegistroAsistencia> registrosEnRango = registroRepository
                    .findByMatriculaIdAndSesionAsistenciaFechaBetween(
                            excusa.getMatricula().getId(), excusa.getFechaInicio(), excusa.getFechaFin());

            for (RegistroAsistencia r : registrosEnRango) {
                if (r.getEstado() == EstadoAsistencia.AUSENTE_INJUSTIFICADA ||
                        r.getEstado() == EstadoAsistencia.EVASION_INTERNA_CLASE) {
                    r.setEstado(EstadoAsistencia.AUSENTE_JUSTIFICADA);
                    registroRepository.save(r);
                }
            }

            // 2. Propagar a Módulo 07: actividades evaluativas en ese rango quedan PENDIENTE_POR_EXCUSA_JUSTIFICADA
            actualizarEvaluacionesEnRango(excusa.getMatricula().getId(), excusa.getFechaInicio(), excusa.getFechaFin());

        } else if (request.getDecision() == DecisionSolicitudExcusa.SOLICITAR_SUBSANACION) {
            excusa.setEstado(EstadoSolicitudExcusa.SUBSANACION_REQUERIDA);
            excusa.setFechaLimiteSubsanacion(request.getFechaLimiteSubsanacion() != null
                    ? request.getFechaLimiteSubsanacion()
                    : LocalDate.now().plusDays(3));
        } else {
            excusa.setEstado(EstadoSolicitudExcusa.RECHAZADA_MOTIVADA);
        }

        SolicitudExcusaAsistencia guardada = solicitudExcusaRepository.save(excusa);
        return asistenciaMapper.toResponse(guardada);
    }

    private void actualizarEvaluacionesEnRango(Long matriculaId, LocalDate fechaInicio, LocalDate fechaFin) {
        List<CalificacionActividad> notas = calificacionActividadRepository.findByMatriculaId(matriculaId);
        for (CalificacionActividad ca : notas) {
            LocalDate fechaAct = ca.getActividad().getFechaEvaluacion();
            if (fechaAct != null && !fechaAct.isBefore(fechaInicio) && !fechaAct.isAfter(fechaFin)) {
                if (ca.getValor() == null || ca.getEstado() == EstadoCalificacionActividad.CALIFICADA) {
                    ca.setEstado(EstadoCalificacionActividad.PENDIENTE_POR_EXCUSA_JUSTIFICADA);
                    ca.setFechaSupletoria(fechaFin.plusDays(5));
                    calificacionActividadRepository.save(ca);
                }
            }
        }
    }

    @Transactional(readOnly = true)
    public List<SolicitudExcusaAsistenciaResponse> listarExcusasMatricula(Long matriculaId) {
        return solicitudExcusaRepository.findByMatriculaIdOrderByFechaRadicacionDesc(matriculaId).stream()
                .map(asistenciaMapper::toResponse)
                .toList();
    }
}
