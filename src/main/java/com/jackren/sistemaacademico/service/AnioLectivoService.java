package com.jackren.sistemaacademico.service;

import com.jackren.sistemaacademico.dto.AnioLectivoRequest;
import com.jackren.sistemaacademico.dto.AnioLectivoResponse;
import com.jackren.sistemaacademico.entity.AnioLectivo;
import com.jackren.sistemaacademico.entity.EstadoAnioLectivo;
import com.jackren.sistemaacademico.exception.ConflictoNegocioException;
import com.jackren.sistemaacademico.exception.RecursoNoEncontradoException;
import com.jackren.sistemaacademico.mapper.AnioLectivoMapper;
import com.jackren.sistemaacademico.repository.AnioLectivoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class AnioLectivoService {

    private final AnioLectivoRepository anioLectivoRepository;
    private final AnioLectivoMapper anioLectivoMapper;

    public AnioLectivoService(AnioLectivoRepository anioLectivoRepository,
                              AnioLectivoMapper anioLectivoMapper) {
        this.anioLectivoRepository = anioLectivoRepository;
        this.anioLectivoMapper = anioLectivoMapper;
    }

    public List<AnioLectivoResponse> listarTodos() {
        return anioLectivoRepository.findAllByOrderByAnioDesc()
                .stream()
                .map(anioLectivoMapper::toResponse)
                .toList();
    }

    public AnioLectivoResponse obtenerPorId(Long id) {
        AnioLectivo anio = anioLectivoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el año lectivo con ID: " + id));
        return anioLectivoMapper.toResponse(anio);
    }

    public AnioLectivoResponse obtenerPorAnio(Integer anio) {
        AnioLectivo anioLectivo = anioLectivoRepository.findByAnio(anio)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el año lectivo correspondiente a: " + anio));
        return anioLectivoMapper.toResponse(anioLectivo);
    }

    public AnioLectivoResponse obtenerActivo() {
        AnioLectivo anioActivo = anioLectivoRepository.findByEstado(EstadoAnioLectivo.ACTIVO)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe ningún año lectivo actualmente ACTIVO."));
        return anioLectivoMapper.toResponse(anioActivo);
    }

    public Optional<AnioLectivoResponse> buscarActivo() {
        return anioLectivoRepository.findByEstado(EstadoAnioLectivo.ACTIVO)
                .map(anioLectivoMapper::toResponse);
    }

    @Transactional
    public AnioLectivoResponse guardar(AnioLectivoRequest request) {
        validarRangoFechas(request.getFechaInicio(), request.getFechaFin());

        if (anioLectivoRepository.existsByAnio(request.getAnio())) {
            throw new ConflictoNegocioException("Ya existe un año lectivo registrado para el año: " + request.getAnio());
        }

        AnioLectivo anio = anioLectivoMapper.toEntity(request);
        AnioLectivo anioGuardado = anioLectivoRepository.save(anio);
        return anioLectivoMapper.toResponse(anioGuardado);
    }

    @Transactional
    public AnioLectivoResponse actualizar(Long id, AnioLectivoRequest request) {
        AnioLectivo anio = anioLectivoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el año lectivo con ID: " + id));

        validarRangoFechas(request.getFechaInicio(), request.getFechaFin());

        if (!anio.getAnio().equals(request.getAnio()) && anioLectivoRepository.existsByAnio(request.getAnio())) {
            throw new ConflictoNegocioException("Ya existe un año lectivo registrado para el año: " + request.getAnio());
        }

        anioLectivoMapper.updateEntityFromRequest(anio, request);
        AnioLectivo actualizado = anioLectivoRepository.save(anio);
        return anioLectivoMapper.toResponse(actualizado);
    }

    @Transactional
    public AnioLectivoResponse activar(Long id) {
        AnioLectivo anio = anioLectivoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el año lectivo con ID: " + id));

        if (anio.getEstado() == EstadoAnioLectivo.CERRADO) {
            throw new ConflictoNegocioException("No se puede activar un año lectivo que ya ha sido CERRADO formalmente.");
        }

        if (anio.getEstado() == EstadoAnioLectivo.ACTIVO) {
            return anioLectivoMapper.toResponse(anio);
        }

        // Invariante de negocio: solo puede haber un año activo
        Optional<AnioLectivo> activoExistente = anioLectivoRepository.findByEstado(EstadoAnioLectivo.ACTIVO);
        if (activoExistente.isPresent() && !activoExistente.get().getId().equals(id)) {
            throw new ConflictoNegocioException("No se puede activar el año lectivo porque el año "
                    + activoExistente.get().getAnio() + " ya se encuentra ACTIVO. Debe cerrarlo primero.");
        }

        anio.setEstado(EstadoAnioLectivo.ACTIVO);
        AnioLectivo guardado = anioLectivoRepository.save(anio);
        return anioLectivoMapper.toResponse(guardado);
    }

    @Transactional
    public AnioLectivoResponse cerrar(Long id) {
        AnioLectivo anio = anioLectivoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el año lectivo con ID: " + id));

        anio.setEstado(EstadoAnioLectivo.CERRADO);
        AnioLectivo guardado = anioLectivoRepository.save(anio);
        return anioLectivoMapper.toResponse(guardado);
    }

    private void validarRangoFechas(java.time.LocalDate inicio, java.time.LocalDate fin) {
        if (inicio != null && fin != null && fin.isBefore(inicio)) {
            throw new ConflictoNegocioException("La fecha de fin no puede ser anterior a la fecha de inicio.");
        }
    }
}
