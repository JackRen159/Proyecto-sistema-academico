package com.jackren.sistemaacademico.service;

import com.jackren.sistemaacademico.dto.GradoResponse;
import com.jackren.sistemaacademico.entity.Grado;
import com.jackren.sistemaacademico.entity.NivelEducativo;
import com.jackren.sistemaacademico.exception.RecursoNoEncontradoException;
import com.jackren.sistemaacademico.mapper.GradoMapper;
import com.jackren.sistemaacademico.repository.GradoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class GradoService {

    private final GradoRepository gradoRepository;
    private final GradoMapper gradoMapper;

    public GradoService(GradoRepository gradoRepository, GradoMapper gradoMapper) {
        this.gradoRepository = gradoRepository;
        this.gradoMapper = gradoMapper;
    }

    public List<GradoResponse> listarTodos() {
        return gradoRepository.findByOrderByOrdenJerarquicoAsc()
                .stream()
                .map(gradoMapper::toResponse)
                .toList();
    }

    public List<GradoResponse> listarActivos() {
        return gradoRepository.findByActivoTrueOrderByOrdenJerarquicoAsc()
                .stream()
                .map(gradoMapper::toResponse)
                .toList();
    }

    public List<GradoResponse> listarPorNivel(NivelEducativo nivel) {
        return gradoRepository.findByNivelAndActivoTrueOrderByOrdenJerarquicoAsc(nivel)
                .stream()
                .map(gradoMapper::toResponse)
                .toList();
    }

    public GradoResponse obtenerPorId(Long id) {
        Grado grado = gradoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el grado con ID: " + id));
        return gradoMapper.toResponse(grado);
    }

    public GradoResponse obtenerPorCodigo(String codigo) {
        Grado grado = gradoRepository.findByCodigo(codigo)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el grado con código: " + codigo));
        return gradoMapper.toResponse(grado);
    }
}
