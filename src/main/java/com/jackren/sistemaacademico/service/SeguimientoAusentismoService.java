package com.jackren.sistemaacademico.service;

import com.jackren.sistemaacademico.dto.ActuacionSeguimientoAusentismoRequest;
import com.jackren.sistemaacademico.dto.ActuacionSeguimientoAusentismoResponse;
import com.jackren.sistemaacademico.entity.ActuacionSeguimientoAusentismo;
import com.jackren.sistemaacademico.entity.Matricula;
import com.jackren.sistemaacademico.entity.PeriodoAcademico;
import com.jackren.sistemaacademico.entity.TipoActuacionSeguimiento;
import com.jackren.sistemaacademico.entity.Usuario;
import com.jackren.sistemaacademico.exception.ConflictoNegocioException;
import com.jackren.sistemaacademico.exception.RecursoNoEncontradoException;
import com.jackren.sistemaacademico.mapper.AsistenciaMapper;
import com.jackren.sistemaacademico.repository.ActuacionSeguimientoAusentismoRepository;
import com.jackren.sistemaacademico.repository.MatriculaRepository;
import com.jackren.sistemaacademico.repository.PeriodoAcademicoRepository;
import com.jackren.sistemaacademico.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class SeguimientoAusentismoService {

    private final ActuacionSeguimientoAusentismoRepository actuacionRepository;
    private final MatriculaRepository matriculaRepository;
    private final PeriodoAcademicoRepository periodoRepository;
    private final UsuarioRepository usuarioRepository;
    private final AsistenciaMapper asistenciaMapper;

    public SeguimientoAusentismoService(ActuacionSeguimientoAusentismoRepository actuacionRepository,
                                       MatriculaRepository matriculaRepository,
                                       PeriodoAcademicoRepository periodoRepository,
                                       UsuarioRepository usuarioRepository,
                                       AsistenciaMapper asistenciaMapper) {
        this.actuacionRepository = actuacionRepository;
        this.matriculaRepository = matriculaRepository;
        this.periodoRepository = periodoRepository;
        this.usuarioRepository = usuarioRepository;
        this.asistenciaMapper = asistenciaMapper;
    }

    public ActuacionSeguimientoAusentismoResponse registrarActuacion(ActuacionSeguimientoAusentismoRequest request, Long usuarioId) {
        Matricula matricula = matriculaRepository.findById(request.getMatriculaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Matrícula no encontrada con ID " + request.getMatriculaId()));

        PeriodoAcademico periodo = periodoRepository.findById(request.getPeriodoAcademicoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Periodo académico no encontrado con ID " + request.getPeriodoAcademicoId()));

        // UC-PROV-19 E2 / RN-ASI-08: No se puede emitir constancia de deserción agotada sin citaciones previas
        if (request.getTipoActuacion() == TipoActuacionSeguimiento.CONSTANCIA_PROTOCOLO_DESERCION_AGOTADO) {
            boolean tieneCitacionPrevia = actuacionRepository.existsByMatriculaIdAndTipoActuacion(
                    matricula.getId(), TipoActuacionSeguimiento.CITACION_ACUDIENTE
            ) || actuacionRepository.existsByMatriculaIdAndTipoActuacion(
                    matricula.getId(), TipoActuacionSeguimiento.CONTACTO_TELEFONICO
            );

            if (!tieneCitacionPrevia) {
                throw new ConflictoNegocioException("No se puede emitir la constancia de deserción escolar sin haber registrado previamente citaciones o intentos de contacto con el acudiente (UC-PROV-19 E2, RN-ASI-08)");
            }
        }

        Usuario actuante = usuarioId != null ? usuarioRepository.findById(usuarioId).orElse(null) : null;

        ActuacionSeguimientoAusentismo actuacion = new ActuacionSeguimientoAusentismo();
        actuacion.setMatricula(matricula);
        actuacion.setPeriodoAcademico(periodo);
        actuacion.setTipoActuacion(request.getTipoActuacion());
        actuacion.setFechaActuacion(request.getFechaActuacion() != null ? request.getFechaActuacion() : LocalDate.now());
        actuacion.setDescripcionGestion(request.getDescripcionGestion());
        actuacion.setCompromisosAcordados(request.getCompromisosAcordados());
        actuacion.setSoporteActaUrl(request.getSoporteActaUrl());
        actuacion.setUsuarioActuante(actuante);

        ActuacionSeguimientoAusentismo guardada = actuacionRepository.save(actuacion);
        return asistenciaMapper.toResponse(guardada);
    }

    @Transactional(readOnly = true)
    public List<ActuacionSeguimientoAusentismoResponse> listarActuacionesMatricula(Long matriculaId) {
        return actuacionRepository.findByMatriculaIdOrderByFechaActuacionDesc(matriculaId).stream()
                .map(asistenciaMapper::toResponse)
                .toList();
    }
}
