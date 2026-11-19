package com.jackren.sistemaacademico.service;

import com.jackren.sistemaacademico.dto.*;
import com.jackren.sistemaacademico.entity.*;
import com.jackren.sistemaacademico.exception.RecursoNoEncontradoException;
import com.jackren.sistemaacademico.repository.EstudianteRepository;
import com.jackren.sistemaacademico.repository.SolicitudRectificacionHabeasDataRepository;
import com.jackren.sistemaacademico.repository.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio de atención y resolución de solicitudes de rectificación Habeas Data (UC-07 A2, UC-PROV-04, RN-EST-04).
 * Garantiza el cumplimiento del plazo perentorio de 15 días hábiles fijado por el Art. 15 de la Ley 1581 de 2012.
 */
@Service
@Transactional
public class SolicitudRectificacionService {

    private final SolicitudRectificacionHabeasDataRepository rectificacionRepository;
    private final EstudianteRepository estudianteRepository;
    private final UsuarioRepository usuarioRepository;
    private final AuditoriaGlobalService auditoriaService;

    public SolicitudRectificacionService(SolicitudRectificacionHabeasDataRepository rectificacionRepository,
                                         EstudianteRepository estudianteRepository,
                                         UsuarioRepository usuarioRepository,
                                         AuditoriaGlobalService auditoriaService) {
        this.rectificacionRepository = rectificacionRepository;
        this.estudianteRepository = estudianteRepository;
        this.usuarioRepository = usuarioRepository;
        this.auditoriaService = auditoriaService;
    }

    public SolicitudRectificacionResponse radicarSolicitud(Long estudianteId, RadicarSolicitudRectificacionRequest request, String username) {
        Estudiante estudiante = estudianteRepository.findById(estudianteId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Estudiante no encontrado con ID: " + estudianteId));

        Usuario solicitante = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario solicitante no encontrado: " + username));

        String radicado = "ARCO-" + LocalDate.now().getYear() + "-" + System.currentTimeMillis() % 1000000;
        // 15 días hábiles equivalen a 21 días calendario estándar
        LocalDate fechaLimiteLegal = LocalDate.now().plusDays(21);

        SolicitudRectificacionHabeasData solicitud = new SolicitudRectificacionHabeasData(
                radicado,
                estudiante,
                solicitante,
                request.getCamposAModificarJson(),
                request.getMotivoSolicitud(),
                fechaLimiteLegal
        );
        solicitud.setSoporteUrl(request.getSoporteUrl());

        SolicitudRectificacionHabeasData guardada = rectificacionRepository.save(solicitud);

        auditoriaService.registrarEvento(new RegistroAuditoriaEventoRequest(
                username,
                solicitante.getRol().name(),
                "ESTUDIANTES",
                "RADICAR_SOLICITUD_RECTIFICACION",
                "SolicitudRectificacionHabeasData",
                guardada.getNumeroRadicado(),
                "Radicación de solicitud ARCO para estudiante " + estudiante.getDocumento() + ". Plazo legal límite: " + fechaLimiteLegal,
                ResultadoAuditoria.EXITO,
                "ARCO-RAD-" + guardada.getId()
        ));

        return toResponse(guardada);
    }

    @Transactional(readOnly = true)
    public Page<SolicitudRectificacionResponse> listarSolicitudes(EstadoSolicitudRectificacion estado, Pageable pageable) {
        if (estado != null) {
            return rectificacionRepository.findByEstadoOrderByFechaRadicacionAsc(estado, pageable)
                    .map(this::toResponse);
        }
        return rectificacionRepository.findAll(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public List<SolicitudRectificacionResponse> listarPorEstudiante(Long estudianteId) {
        return rectificacionRepository.findByEstudianteIdOrderByFechaRadicacionDesc(estudianteId).stream()
                .map(this::toResponse)
                .toList();
    }

    public SolicitudRectificacionResponse resolverSolicitud(Long solicitudId, ResolverSolicitudRectificacionRequest request, String autorizador) {
        SolicitudRectificacionHabeasData solicitud = rectificacionRepository.findById(solicitudId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Solicitud de rectificación no encontrada con ID: " + solicitudId));

        Usuario resolutor = autorizador != null ? usuarioRepository.findByUsername(autorizador).orElse(null) : null;

        solicitud.setEstado(request.getEstado());
        solicitud.setRespuestaMotivada(request.getRespuestaMotivada());
        solicitud.setFechaResolucion(LocalDateTime.now());
        solicitud.setUsuarioResolutor(resolutor);

        SolicitudRectificacionHabeasData guardada = rectificacionRepository.save(solicitud);

        auditoriaService.registrarEvento(new RegistroAuditoriaEventoRequest(
                autorizador != null ? autorizador : "SECRETARIA",
                "SECRETARIA",
                "ESTUDIANTES",
                "RESOLVER_SOLICITUD_RECTIFICACION",
                "SolicitudRectificacionHabeasData",
                guardada.getNumeroRadicado(),
                "Resolución de solicitud " + guardada.getNumeroRadicado() + " como " + request.getEstado() + ". Motivo: " + request.getRespuestaMotivada(),
                ResultadoAuditoria.EXITO,
                "ARCO-RES-" + guardada.getId()
        ));

        return toResponse(guardada);
    }

    private SolicitudRectificacionResponse toResponse(SolicitudRectificacionHabeasData s) {
        SolicitudRectificacionResponse r = new SolicitudRectificacionResponse();
        r.setId(s.getId());
        r.setNumeroRadicado(s.getNumeroRadicado());
        r.setEstudianteId(s.getEstudiante().getId());
        r.setEstudianteDocumento(s.getEstudiante().getDocumento());
        r.setEstudianteNombreCompleto(s.getEstudiante().getNombres() + " " + s.getEstudiante().getApellidos());
        r.setSolicitanteUsername(s.getUsuarioSolicitante().getUsername());
        r.setCamposAModificarJson(s.getCamposAModificarJson());
        r.setMotivoSolicitud(s.getMotivoSolicitud());
        r.setSoporteUrl(s.getSoporteUrl());
        r.setFechaRadicacion(s.getFechaRadicacion());
        r.setFechaLimiteLegal(s.getFechaLimiteLegal());
        r.setEstado(s.getEstado());
        r.setRespuestaMotivada(s.getRespuestaMotivada());
        r.setFechaResolucion(s.getFechaResolucion());
        r.setResolutorUsername(s.getUsuarioResolutor() != null ? s.getUsuarioResolutor().getUsername() : null);
        return r;
    }
}
