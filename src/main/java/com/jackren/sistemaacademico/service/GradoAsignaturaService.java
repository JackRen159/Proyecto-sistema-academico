package com.jackren.sistemaacademico.service;

import com.jackren.sistemaacademico.dto.GradoAsignaturaRequest;
import com.jackren.sistemaacademico.dto.GradoAsignaturaResponse;
import com.jackren.sistemaacademico.entity.Asignatura;
import com.jackren.sistemaacademico.entity.Grado;
import com.jackren.sistemaacademico.entity.GradoAsignatura;
import com.jackren.sistemaacademico.exception.ConflictoNegocioException;
import com.jackren.sistemaacademico.exception.RecursoNoEncontradoException;
import com.jackren.sistemaacademico.mapper.GradoAsignaturaMapper;
import com.jackren.sistemaacademico.repository.AsignaturaRepository;
import com.jackren.sistemaacademico.repository.GradoAsignaturaRepository;
import com.jackren.sistemaacademico.repository.GradoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class GradoAsignaturaService {

    private final GradoAsignaturaRepository gradoAsignaturaRepository;
    private final GradoRepository gradoRepository;
    private final AsignaturaRepository asignaturaRepository;
    private final GradoAsignaturaMapper gradoAsignaturaMapper;

    public GradoAsignaturaService(GradoAsignaturaRepository gradoAsignaturaRepository,
                                  GradoRepository gradoRepository,
                                  AsignaturaRepository asignaturaRepository,
                                  GradoAsignaturaMapper gradoAsignaturaMapper) {
        this.gradoAsignaturaRepository = gradoAsignaturaRepository;
        this.gradoRepository = gradoRepository;
        this.asignaturaRepository = asignaturaRepository;
        this.gradoAsignaturaMapper = gradoAsignaturaMapper;
    }

    public List<GradoAsignaturaResponse> listarPorGrado(Long gradoId) {
        if (!gradoRepository.existsById(gradoId)) {
            throw new RecursoNoEncontradoException("No se encontró el grado con ID: " + gradoId);
        }

        return gradoAsignaturaRepository.findByGradoIdAndActivoTrue(gradoId)
                .stream()
                .map(gradoAsignaturaMapper::toResponse)
                .toList();
    }

    @Transactional
    public GradoAsignaturaResponse asignar(GradoAsignaturaRequest request) {
        Grado grado = gradoRepository.findById(request.getGradoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el grado con ID: " + request.getGradoId()));

        Asignatura asignatura = asignaturaRepository.findById(request.getAsignaturaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró la asignatura con ID: " + request.getAsignaturaId()));

        return gradoAsignaturaRepository.findByGradoIdAndAsignaturaId(grado.getId(), asignatura.getId())
                .map(existente -> {
                    if (Boolean.TRUE.equals(existente.getActivo())) {
                        throw new ConflictoNegocioException("La asignatura ya se encuentra asignada a este grado en el plan de estudios.");
                    }
                    existente.setActivo(true);
                    existente.setIntensidadHoraria(request.getIntensidadHoraria());
                    existente.setEsOptativa(request.getEsOptativa() != null ? request.getEsOptativa() : false);
                    GradoAsignatura guardado = gradoAsignaturaRepository.save(existente);
                    return gradoAsignaturaMapper.toResponse(guardado);
                })
                .orElseGet(() -> {
                    GradoAsignatura nueva = gradoAsignaturaMapper.toEntity(request, grado, asignatura);
                    GradoAsignatura guardada = gradoAsignaturaRepository.save(nueva);
                    return gradoAsignaturaMapper.toResponse(guardada);
                });
    }

    @Transactional
    public GradoAsignaturaResponse actualizar(Long id, Integer intensidadHoraria, Boolean esOptativa) {
        GradoAsignatura ga = gradoAsignaturaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró la asignación de plan de estudios con ID: " + id));

        if (intensidadHoraria != null) {
            if (intensidadHoraria <= 0) {
                throw new IllegalArgumentException("La intensidad horaria debe ser mayor a 0");
            }
            ga.setIntensidadHoraria(intensidadHoraria);
        }

        if (esOptativa != null) {
            ga.setEsOptativa(esOptativa);
        }

        GradoAsignatura actualizada = gradoAsignaturaRepository.save(ga);
        return gradoAsignaturaMapper.toResponse(actualizada);
    }

    @Transactional
    public void desactivar(Long id) {
        GradoAsignatura ga = gradoAsignaturaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró la asignación de plan de estudios con ID: " + id));
        ga.setActivo(false);
        gradoAsignaturaRepository.save(ga);
    }
}
