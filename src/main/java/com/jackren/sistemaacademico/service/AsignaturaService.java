package com.jackren.sistemaacademico.service;

import com.jackren.sistemaacademico.dto.AsignaturaRequest;
import com.jackren.sistemaacademico.dto.AsignaturaResponse;
import com.jackren.sistemaacademico.entity.Area;
import com.jackren.sistemaacademico.entity.Asignatura;
import com.jackren.sistemaacademico.exception.ConflictoNegocioException;
import com.jackren.sistemaacademico.exception.RecursoNoEncontradoException;
import com.jackren.sistemaacademico.mapper.AsignaturaMapper;
import com.jackren.sistemaacademico.repository.AreaRepository;
import com.jackren.sistemaacademico.repository.AsignaturaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class AsignaturaService {

    private final AsignaturaRepository asignaturaRepository;
    private final AreaRepository areaRepository;
    private final AsignaturaMapper asignaturaMapper;

    public AsignaturaService(AsignaturaRepository asignaturaRepository,
                             AreaRepository areaRepository,
                             AsignaturaMapper asignaturaMapper) {
        this.asignaturaRepository = asignaturaRepository;
        this.areaRepository = areaRepository;
        this.asignaturaMapper = asignaturaMapper;
    }

    public List<AsignaturaResponse> listarTodas() {
        return asignaturaRepository.findByOrderByNombreAsc()
                .stream()
                .map(asignaturaMapper::toResponse)
                .toList();
    }

    public List<AsignaturaResponse> listarActivas() {
        return asignaturaRepository.findByActivoTrueOrderByNombreAsc()
                .stream()
                .map(asignaturaMapper::toResponse)
                .toList();
    }

    public List<AsignaturaResponse> listarPorArea(Long areaId) {
        return asignaturaRepository.findByAreaIdAndActivoTrueOrderByNombreAsc(areaId)
                .stream()
                .map(asignaturaMapper::toResponse)
                .toList();
    }

    public AsignaturaResponse obtenerPorId(Long id) {
        Asignatura asignatura = asignaturaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró la asignatura con ID: " + id));
        return asignaturaMapper.toResponse(asignatura);
    }

    @Transactional
    public AsignaturaResponse guardar(AsignaturaRequest request) {
        String codigoNormalizado = request.getCodigo().trim().toUpperCase();
        request.setCodigo(codigoNormalizado);

        if (asignaturaRepository.existsByCodigo(codigoNormalizado)) {
            throw new ConflictoNegocioException("Ya existe una asignatura con el código: " + codigoNormalizado);
        }

        Area area = null;
        if (request.getAreaId() != null) {
            area = areaRepository.findById(request.getAreaId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el área con ID: " + request.getAreaId()));
        }

        Asignatura asignatura = asignaturaMapper.toEntity(request, area);
        Asignatura asignaturaGuardada = asignaturaRepository.save(asignatura);
        return asignaturaMapper.toResponse(asignaturaGuardada);
    }

    @Transactional
    public AsignaturaResponse actualizar(Long id, AsignaturaRequest request) {
        Asignatura asignatura = asignaturaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró la asignatura con ID: " + id));

        String codigoNormalizado = request.getCodigo().trim().toUpperCase();
        request.setCodigo(codigoNormalizado);

        if (!asignatura.getCodigo().equalsIgnoreCase(codigoNormalizado)
                && asignaturaRepository.existsByCodigo(codigoNormalizado)) {
            throw new ConflictoNegocioException("Ya existe una asignatura con el código: " + codigoNormalizado);
        }

        Area area = null;
        if (request.getAreaId() != null) {
            area = areaRepository.findById(request.getAreaId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el área con ID: " + request.getAreaId()));
        }

        asignaturaMapper.updateEntityFromRequest(asignatura, request, area);
        Asignatura asignaturaActualizada = asignaturaRepository.save(asignatura);
        return asignaturaMapper.toResponse(asignaturaActualizada);
    }

    @Transactional
    public void desactivar(Long id) {
        Asignatura asignatura = asignaturaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró la asignatura con ID: " + id));
        asignatura.setActivo(false);
        asignaturaRepository.save(asignatura);
    }

    @Transactional
    public void activar(Long id) {
        Asignatura asignatura = asignaturaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró la asignatura con ID: " + id));
        asignatura.setActivo(true);
        asignaturaRepository.save(asignatura);
    }
}
