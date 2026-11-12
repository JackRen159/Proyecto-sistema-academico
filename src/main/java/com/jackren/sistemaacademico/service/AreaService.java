package com.jackren.sistemaacademico.service;

import com.jackren.sistemaacademico.dto.AreaRequest;
import com.jackren.sistemaacademico.dto.AreaResponse;
import com.jackren.sistemaacademico.entity.Area;
import com.jackren.sistemaacademico.exception.ConflictoNegocioException;
import com.jackren.sistemaacademico.exception.RecursoNoEncontradoException;
import com.jackren.sistemaacademico.mapper.AreaMapper;
import com.jackren.sistemaacademico.repository.AreaRepository;
import com.jackren.sistemaacademico.repository.AsignaturaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class AreaService {

    private final AreaRepository areaRepository;
    private final AsignaturaRepository asignaturaRepository;
    private final AreaMapper areaMapper;

    public AreaService(AreaRepository areaRepository,
                       AsignaturaRepository asignaturaRepository,
                       AreaMapper areaMapper) {
        this.areaRepository = areaRepository;
        this.asignaturaRepository = asignaturaRepository;
        this.areaMapper = areaMapper;
    }

    public List<AreaResponse> listarTodas() {
        return areaRepository.findByOrderByNombreAsc()
                .stream()
                .map(areaMapper::toResponse)
                .toList();
    }

    public List<AreaResponse> listarActivas() {
        return areaRepository.findByActivoTrueOrderByNombreAsc()
                .stream()
                .map(areaMapper::toResponse)
                .toList();
    }

    public AreaResponse obtenerPorId(Long id) {
        Area area = areaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el área con ID: " + id));
        return areaMapper.toResponse(area);
    }

    @Transactional
    public AreaResponse guardar(AreaRequest request) {
        String codigoNormalizado = request.getCodigo().trim().toUpperCase();
        request.setCodigo(codigoNormalizado);

        if (areaRepository.existsByCodigo(codigoNormalizado)) {
            throw new ConflictoNegocioException("Ya existe un área con el código: " + codigoNormalizado);
        }

        if (areaRepository.existsByNombreIgnoreCase(request.getNombre().trim())) {
            throw new ConflictoNegocioException("Ya existe un área con el nombre: " + request.getNombre().trim());
        }

        Area area = areaMapper.toEntity(request);
        Area areaGuardada = areaRepository.save(area);
        return areaMapper.toResponse(areaGuardada);
    }

    @Transactional
    public AreaResponse actualizar(Long id, AreaRequest request) {
        Area area = areaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el área con ID: " + id));

        String codigoNormalizado = request.getCodigo().trim().toUpperCase();
        request.setCodigo(codigoNormalizado);

        if (!area.getCodigo().equalsIgnoreCase(codigoNormalizado)
                && areaRepository.existsByCodigo(codigoNormalizado)) {
            throw new ConflictoNegocioException("Ya existe un área con el código: " + codigoNormalizado);
        }

        if (!area.getNombre().equalsIgnoreCase(request.getNombre().trim())
                && areaRepository.existsByNombreIgnoreCase(request.getNombre().trim())) {
            throw new ConflictoNegocioException("Ya existe un área con el nombre: " + request.getNombre().trim());
        }

        areaMapper.updateEntityFromRequest(area, request);
        Area areaActualizada = areaRepository.save(area);
        return areaMapper.toResponse(areaActualizada);
    }

    @Transactional
    public void desactivar(Long id) {
        Area area = areaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el área con ID: " + id));
        area.setActivo(false);
        areaRepository.save(area);
    }

    @Transactional
    public void activar(Long id) {
        Area area = areaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el área con ID: " + id));
        area.setActivo(true);
        areaRepository.save(area);
    }
}
