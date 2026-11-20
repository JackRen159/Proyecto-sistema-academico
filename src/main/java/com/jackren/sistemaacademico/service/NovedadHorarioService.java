package com.jackren.sistemaacademico.service;

import com.jackren.sistemaacademico.dto.NovedadHorarioRequest;
import com.jackren.sistemaacademico.dto.NovedadHorarioResponse;
import com.jackren.sistemaacademico.entity.*;
import com.jackren.sistemaacademico.exception.ConflictoNegocioException;
import com.jackren.sistemaacademico.exception.RecursoNoEncontradoException;
import com.jackren.sistemaacademico.exception.ValidacionException;
import com.jackren.sistemaacademico.mapper.HorarioMapper;
import com.jackren.sistemaacademico.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class NovedadHorarioService {

    private final NovedadHorarioRepository novedadRepository;
    private final HorarioBloqueRepository bloqueRepository;
    private final AulaRepository aulaRepository;
    private final ProfesorRepository profesorRepository;
    private final UsuarioRepository usuarioRepository;
    private final HorarioMapper horarioMapper;

    public NovedadHorarioService(NovedadHorarioRepository novedadRepository,
                                 HorarioBloqueRepository bloqueRepository,
                                 AulaRepository aulaRepository,
                                 ProfesorRepository profesorRepository,
                                 UsuarioRepository usuarioRepository,
                                 HorarioMapper horarioMapper) {
        this.novedadRepository = novedadRepository;
        this.bloqueRepository = bloqueRepository;
        this.aulaRepository = aulaRepository;
        this.profesorRepository = profesorRepository;
        this.usuarioRepository = usuarioRepository;
        this.horarioMapper = horarioMapper;
    }

    public NovedadHorarioResponse registrarNovedad(NovedadHorarioRequest request, Long usuarioId) {
        HorarioBloque bloque = bloqueRepository.findById(request.getHorarioBloqueId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Bloque de horario no encontrado con ID " + request.getHorarioBloqueId()));

        if (request.getFechaInicio().isAfter(request.getFechaFin())) {
            throw new ValidacionException("La fecha de inicio de la novedad no puede ser posterior a la fecha de fin");
        }

        Aula aulaReemplazo = null;
        if (request.getAulaReemplazoId() != null) {
            aulaReemplazo = aulaRepository.findById(request.getAulaReemplazoId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Aula de reemplazo no encontrada"));

            if (aulaReemplazo.getEstado() != EstadoAula.DISPONIBLE) {
                throw new ConflictoNegocioException("El aula de reemplazo no se encuentra en estado DISPONIBLE");
            }

            // Validar que el aula de reemplazo no esté ocupada en ese día y franja
            List<HorarioBloque> colisiones = bloqueRepository.findColisionesAula(
                    aulaReemplazo.getId(), bloque.getDiaSemana(), bloque.getFranjaHoraria().getId(),
                    EstadoHorarioVersion.PUBLICADO_VIGENTE, bloque.getId()
            );
            if (!colisiones.isEmpty()) {
                throw new ConflictoNegocioException("Conflicto de Aula: el aula de reemplazo ya está ocupada en el día y franja seleccionados (UC-PROV-21 E1)");
            }
        }

        Profesor docenteReemplazo = null;
        if (request.getDocenteReemplazoId() != null) {
            docenteReemplazo = profesorRepository.findById(request.getDocenteReemplazoId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Docente de reemplazo no encontrado"));

            List<HorarioBloque> colisionesDoc = bloqueRepository.findColisionesDocente(
                    docenteReemplazo.getId(), bloque.getDiaSemana(), bloque.getFranjaHoraria().getId(),
                    EstadoHorarioVersion.PUBLICADO_VIGENTE, bloque.getId()
            );
            if (!colisionesDoc.isEmpty()) {
                throw new ConflictoNegocioException("Conflicto de Docente: el profesor de reemplazo ya tiene clase asignada en el día y franja seleccionados (UC-PROV-21 E1)");
            }
        }

        Usuario autorizador = usuarioId != null ? usuarioRepository.findById(usuarioId).orElse(null) : null;

        NovedadHorario novedad = new NovedadHorario();
        novedad.setHorarioBloque(bloque);
        novedad.setTipoNovedad(request.getTipoNovedad());
        novedad.setAulaReemplazo(aulaReemplazo);
        novedad.setDocenteReemplazo(docenteReemplazo);
        novedad.setFechaInicio(request.getFechaInicio());
        novedad.setFechaFin(request.getFechaFin());
        novedad.setMotivo(request.getMotivo());
        novedad.setUsuarioAutorizador(autorizador);

        NovedadHorario guardada = novedadRepository.save(novedad);
        bloque.addNovedad(guardada);

        return horarioMapper.toResponse(guardada);
    }

    @Transactional(readOnly = true)
    public List<NovedadHorarioResponse> listarNovedadesPorBloque(Long bloqueId) {
        return novedadRepository.findByHorarioBloqueId(bloqueId).stream()
                .map(horarioMapper::toResponse)
                .toList();
    }
}
