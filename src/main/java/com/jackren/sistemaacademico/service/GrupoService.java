package com.jackren.sistemaacademico.service;

import com.jackren.sistemaacademico.dto.GrupoRequest;
import com.jackren.sistemaacademico.dto.GrupoResponse;
import com.jackren.sistemaacademico.entity.AnioLectivo;
import com.jackren.sistemaacademico.entity.EstadoAnioLectivo;
import com.jackren.sistemaacademico.entity.Grado;
import com.jackren.sistemaacademico.entity.Grupo;
import com.jackren.sistemaacademico.exception.ConflictoNegocioException;
import com.jackren.sistemaacademico.exception.RecursoNoEncontradoException;
import com.jackren.sistemaacademico.mapper.GrupoMapper;
import com.jackren.sistemaacademico.repository.AnioLectivoRepository;
import com.jackren.sistemaacademico.repository.GradoRepository;
import com.jackren.sistemaacademico.repository.GrupoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class GrupoService {

    private final GrupoRepository grupoRepository;
    private final AnioLectivoRepository anioLectivoRepository;
    private final GradoRepository gradoRepository;
    private final GrupoMapper grupoMapper;

    public GrupoService(GrupoRepository grupoRepository,
                        AnioLectivoRepository anioLectivoRepository,
                        GradoRepository gradoRepository,
                        GrupoMapper grupoMapper) {
        this.grupoRepository = grupoRepository;
        this.anioLectivoRepository = anioLectivoRepository;
        this.gradoRepository = gradoRepository;
        this.grupoMapper = grupoMapper;
    }

    public List<GrupoResponse> listarPorAnio(Long anioLectivoId) {
        if (!anioLectivoRepository.existsById(anioLectivoId)) {
            throw new RecursoNoEncontradoException("No se encontró el año lectivo con ID: " + anioLectivoId);
        }

        return grupoRepository.findByAnioLectivoIdAndActivoTrue(anioLectivoId)
                .stream()
                .map(grupoMapper::toResponse)
                .toList();
    }

    public List<GrupoResponse> listarPorAnioYGrado(Long anioLectivoId, Long gradoId) {
        if (!anioLectivoRepository.existsById(anioLectivoId)) {
            throw new RecursoNoEncontradoException("No se encontró el año lectivo con ID: " + anioLectivoId);
        }

        if (!gradoRepository.existsById(gradoId)) {
            throw new RecursoNoEncontradoException("No se encontró el grado con ID: " + gradoId);
        }

        return grupoRepository.findByAnioLectivoIdAndGradoIdAndActivoTrue(anioLectivoId, gradoId)
                .stream()
                .map(grupoMapper::toResponse)
                .toList();
    }

    public GrupoResponse obtenerPorId(Long id) {
        Grupo grupo = grupoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el grupo con ID: " + id));
        return grupoMapper.toResponse(grupo);
    }

    @Transactional
    public GrupoResponse guardar(GrupoRequest request) {
        AnioLectivo anio = anioLectivoRepository.findById(request.getAnioLectivoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el año lectivo con ID: " + request.getAnioLectivoId()));

        if (anio.getEstado() == EstadoAnioLectivo.CERRADO) {
            throw new ConflictoNegocioException("No se pueden crear grupos en un año lectivo que ya ha sido CERRADO.");
        }

        Grado grado = gradoRepository.findById(request.getGradoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el grado con ID: " + request.getGradoId()));

        String nombreNormalizado = request.getNombre().trim().toUpperCase();
        request.setNombre(nombreNormalizado);

        if (grupoRepository.existsByAnioLectivoIdAndGradoIdAndNombre(anio.getId(), grado.getId(), nombreNormalizado)) {
            throw new ConflictoNegocioException("Ya existe el grupo '" + nombreNormalizado + "' para el grado "
                    + grado.getNombre() + " en el año lectivo " + anio.getAnio());
        }

        Grupo grupo = grupoMapper.toEntity(request, anio, grado);
        Grupo guardado = grupoRepository.save(grupo);
        return grupoMapper.toResponse(guardado);
    }

    @Transactional
    public GrupoResponse actualizar(Long id, GrupoRequest request) {
        Grupo grupo = grupoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el grupo con ID: " + id));

        AnioLectivo anio = anioLectivoRepository.findById(request.getAnioLectivoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el año lectivo con ID: " + request.getAnioLectivoId()));

        Grado grado = gradoRepository.findById(request.getGradoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el grado con ID: " + request.getGradoId()));

        String nombreNormalizado = request.getNombre().trim().toUpperCase();
        request.setNombre(nombreNormalizado);

        boolean cambioClave = !grupo.getAnioLectivo().getId().equals(anio.getId())
                || !grupo.getGrado().getId().equals(grado.getId())
                || !grupo.getNombre().equalsIgnoreCase(nombreNormalizado);

        if (cambioClave && grupoRepository.existsByAnioLectivoIdAndGradoIdAndNombre(anio.getId(), grado.getId(), nombreNormalizado)) {
            throw new ConflictoNegocioException("Ya existe el grupo '" + nombreNormalizado + "' para el grado "
                    + grado.getNombre() + " en el año lectivo " + anio.getAnio());
        }

        grupoMapper.updateEntityFromRequest(grupo, request, anio, grado);
        Grupo actualizado = grupoRepository.save(grupo);
        return grupoMapper.toResponse(actualizado);
    }

    @Transactional
    public void desactivar(Long id) {
        Grupo grupo = grupoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el grupo con ID: " + id));
        grupo.setActivo(false);
        grupoRepository.save(grupo);
    }

    @Transactional
    public void activar(Long id) {
        Grupo grupo = grupoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el grupo con ID: " + id));
        grupo.setActivo(true);
        grupoRepository.save(grupo);
    }
}
