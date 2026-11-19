package com.jackren.sistemaacademico.service;

import com.jackren.sistemaacademico.dto.EvolucionDocumentoRequest;
import com.jackren.sistemaacademico.dto.HistorialDocumentoIdentidadResponse;
import com.jackren.sistemaacademico.dto.RegistroAuditoriaEventoRequest;
import com.jackren.sistemaacademico.entity.*;
import com.jackren.sistemaacademico.exception.ConflictoException;
import com.jackren.sistemaacademico.exception.RecursoNoEncontradoException;
import com.jackren.sistemaacademico.repository.EstudianteRepository;
import com.jackren.sistemaacademico.repository.HistorialDocumentoIdentidadRepository;
import com.jackren.sistemaacademico.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio para la evolución legal de documentos de identidad (RC a TI, TI a CC, NES a PPT/CE)
 * preservando la continuidad del expediente académico (UC-PROV-04 A1, RN-EST-03).
 */
@Service
@Transactional
public class EvolucionDocumentoService {

    private final EstudianteRepository estudianteRepository;
    private final HistorialDocumentoIdentidadRepository historialRepository;
    private final UsuarioRepository usuarioRepository;
    private final AuditoriaGlobalService auditoriaService;

    public EvolucionDocumentoService(EstudianteRepository estudianteRepository,
                                     HistorialDocumentoIdentidadRepository historialRepository,
                                     UsuarioRepository usuarioRepository,
                                     AuditoriaGlobalService auditoriaService) {
        this.estudianteRepository = estudianteRepository;
        this.historialRepository = historialRepository;
        this.usuarioRepository = usuarioRepository;
        this.auditoriaService = auditoriaService;
    }

    public HistorialDocumentoIdentidadResponse registrarEvolucionDocumento(Long estudianteId, EvolucionDocumentoRequest request, String autorizador) {
        Estudiante estudiante = estudianteRepository.findById(estudianteId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Estudiante no encontrado con ID: " + estudianteId));

        // Validación de unicidad: que el nuevo número no pertenezca ya a otra persona (E2)
        if (!estudiante.getDocumento().equals(request.getNumeroDocumentoNuevo()) &&
                estudianteRepository.existsByDocumento(request.getNumeroDocumentoNuevo())) {
            throw new ConflictoException("El nuevo número de documento ya pertenece a otra persona registrada (UC-PROV-04 E2): " + request.getNumeroDocumentoNuevo());
        }

        TipoDocumento tipoAnterior = estudiante.getTipoDocumento();
        String docAnterior = estudiante.getDocumento();

        Usuario autorizadorUsuario = autorizador != null ? usuarioRepository.findByUsername(autorizador).orElse(null) : null;

        // 1. Guardar en historial inmutable de documentos
        HistorialDocumentoIdentidad historial = new HistorialDocumentoIdentidad(
                estudiante,
                tipoAnterior,
                docAnterior,
                request.getTipoDocumentoNuevo(),
                request.getNumeroDocumentoNuevo(),
                request.getMotivoCambio(),
                request.getSoporteDocumentoUrl(),
                autorizadorUsuario
        );
        HistorialDocumentoIdentidad guardado = historialRepository.save(historial);

        // 2. Actualizar documento vigente en el estudiante manteniendo el mismo ID
        estudiante.setTipoDocumento(request.getTipoDocumentoNuevo());
        estudiante.setDocumento(request.getNumeroDocumentoNuevo());
        estudianteRepository.save(estudiante);

        // 3. Trazabilidad en Auditoría Global (RN-10)
        auditoriaService.registrarEvento(new RegistroAuditoriaEventoRequest(
                autorizador != null ? autorizador : "SECRETARIA",
                "SECRETARIA",
                "ESTUDIANTES",
                "EVOLUCION_DOCUMENTO_IDENTIDAD",
                "Estudiante",
                String.valueOf(estudiante.getId()),
                "Evolución de documento de " + tipoAnterior + " " + docAnterior + " a " + request.getTipoDocumentoNuevo() + " " + request.getNumeroDocumentoNuevo() + ". Motivo: " + request.getMotivoCambio(),
                ResultadoAuditoria.EXITO,
                "DOC-EVOL-" + estudiante.getId()
        ));

        return toResponse(guardado);
    }

    @Transactional(readOnly = true)
    public List<HistorialDocumentoIdentidadResponse> listarHistorialPorEstudiante(Long estudianteId) {
        return historialRepository.findByEstudianteIdOrderByFechaCambioDesc(estudianteId).stream()
                .map(this::toResponse)
                .toList();
    }

    private HistorialDocumentoIdentidadResponse toResponse(HistorialDocumentoIdentidad h) {
        HistorialDocumentoIdentidadResponse r = new HistorialDocumentoIdentidadResponse();
        r.setId(h.getId());
        r.setEstudianteId(h.getEstudiante().getId());
        r.setTipoDocumentoAnterior(h.getTipoDocumentoAnterior());
        r.setNumeroDocumentoAnterior(h.getNumeroDocumentoAnterior());
        r.setTipoDocumentoNuevo(h.getTipoDocumentoNuevo());
        r.setNumeroDocumentoNuevo(h.getNumeroDocumentoNuevo());
        r.setFechaCambio(h.getFechaCambio());
        r.setMotivoCambio(h.getMotivoCambio());
        r.setSoporteDocumentoUrl(h.getSoporteDocumentoUrl());
        r.setUsuarioAutorizador(h.getUsuarioAutorizador() != null ? h.getUsuarioAutorizador().getUsername() : "SISTEMA");
        return r;
    }
}
