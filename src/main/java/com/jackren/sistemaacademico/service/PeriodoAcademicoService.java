package com.jackren.sistemaacademico.service;

import com.jackren.sistemaacademico.dto.PeriodoAcademicoRequest;
import com.jackren.sistemaacademico.dto.PeriodoAcademicoResponse;
import com.jackren.sistemaacademico.entity.AnioLectivo;
import com.jackren.sistemaacademico.entity.EstadoPeriodo;
import com.jackren.sistemaacademico.entity.PeriodoAcademico;
import com.jackren.sistemaacademico.exception.ConflictoNegocioException;
import com.jackren.sistemaacademico.exception.RecursoNoEncontradoException;
import com.jackren.sistemaacademico.mapper.PeriodoAcademicoMapper;
import com.jackren.sistemaacademico.repository.AnioLectivoRepository;
import com.jackren.sistemaacademico.repository.PeriodoAcademicoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class PeriodoAcademicoService {

    private final PeriodoAcademicoRepository periodoAcademicoRepository;
    private final AnioLectivoRepository anioLectivoRepository;
    private final PeriodoAcademicoMapper periodoAcademicoMapper;

    public PeriodoAcademicoService(PeriodoAcademicoRepository periodoAcademicoRepository,
                                   AnioLectivoRepository anioLectivoRepository,
                                   PeriodoAcademicoMapper periodoAcademicoMapper) {
        this.periodoAcademicoRepository = periodoAcademicoRepository;
        this.anioLectivoRepository = anioLectivoRepository;
        this.periodoAcademicoMapper = periodoAcademicoMapper;
    }

    public List<PeriodoAcademicoResponse> listarPorAnio(Long anioLectivoId) {
        if (!anioLectivoRepository.existsById(anioLectivoId)) {
            throw new RecursoNoEncontradoException("No se encontró el año lectivo con ID: " + anioLectivoId);
        }

        return periodoAcademicoRepository.findByAnioLectivoIdOrderByNumeroAsc(anioLectivoId)
                .stream()
                .map(periodoAcademicoMapper::toResponse)
                .toList();
    }

    public PeriodoAcademicoResponse obtenerPorId(Long id) {
        PeriodoAcademico periodo = periodoAcademicoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el periodo académico con ID: " + id));
        return periodoAcademicoMapper.toResponse(periodo);
    }

    @Transactional
    public PeriodoAcademicoResponse guardar(Long anioLectivoId, PeriodoAcademicoRequest request) {
        AnioLectivo anio = anioLectivoRepository.findById(anioLectivoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el año lectivo con ID: " + anioLectivoId));

        validarFechasDentroDeAnio(anio, request);

        if (periodoAcademicoRepository.existsByAnioLectivoIdAndNumero(anioLectivoId, request.getNumero())) {
            throw new ConflictoNegocioException("Ya existe el periodo número " + request.getNumero()
                    + " para el año lectivo " + anio.getAnio());
        }

        validarSumaPorcentajes(anioLectivoId, null, request.getPorcentaje());

        PeriodoAcademico periodo = periodoAcademicoMapper.toEntity(request, anio);
        PeriodoAcademico guardado = periodoAcademicoRepository.save(periodo);
        return periodoAcademicoMapper.toResponse(guardado);
    }

    @Transactional
    public PeriodoAcademicoResponse actualizar(Long id, PeriodoAcademicoRequest request) {
        PeriodoAcademico periodo = periodoAcademicoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el periodo académico con ID: " + id));

        AnioLectivo anio = periodo.getAnioLectivo();
        validarFechasDentroDeAnio(anio, request);

        if (!periodo.getNumero().equals(request.getNumero())
                && periodoAcademicoRepository.existsByAnioLectivoIdAndNumero(anio.getId(), request.getNumero())) {
            throw new ConflictoNegocioException("Ya existe el periodo número " + request.getNumero()
                    + " para el año lectivo " + anio.getAnio());
        }

        validarSumaPorcentajes(anio.getId(), id, request.getPorcentaje());

        periodoAcademicoMapper.updateEntityFromRequest(periodo, request);
        PeriodoAcademico actualizado = periodoAcademicoRepository.save(periodo);
        return periodoAcademicoMapper.toResponse(actualizado);
    }

    @Transactional
    public PeriodoAcademicoResponse abrir(Long id) {
        PeriodoAcademico periodo = periodoAcademicoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el periodo académico con ID: " + id));

        periodo.setEstado(EstadoPeriodo.ABIERTO);
        PeriodoAcademico guardado = periodoAcademicoRepository.save(periodo);
        return periodoAcademicoMapper.toResponse(guardado);
    }

    @Transactional
    public PeriodoAcademicoResponse cerrar(Long id) {
        PeriodoAcademico periodo = periodoAcademicoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el periodo académico con ID: " + id));

        periodo.setEstado(EstadoPeriodo.CERRADO);
        PeriodoAcademico guardado = periodoAcademicoRepository.save(periodo);
        return periodoAcademicoMapper.toResponse(guardado);
    }

    private void validarFechasDentroDeAnio(AnioLectivo anio, PeriodoAcademicoRequest request) {
        if (request.getFechaFin().isBefore(request.getFechaInicio())) {
            throw new ConflictoNegocioException("La fecha de fin del periodo no puede ser anterior a la fecha de inicio.");
        }

        if (request.getFechaInicio().isBefore(anio.getFechaInicio())) {
            throw new ConflictoNegocioException("La fecha de inicio del periodo (" + request.getFechaInicio()
                    + ") no puede ser anterior al inicio del año lectivo (" + anio.getFechaInicio() + ").");
        }

        if (request.getFechaFin().isAfter(anio.getFechaFin())) {
            throw new ConflictoNegocioException("La fecha de fin del periodo (" + request.getFechaFin()
                    + ") no puede ser posterior al fin del año lectivo (" + anio.getFechaFin() + ").");
        }
    }

    private void validarSumaPorcentajes(Long anioLectivoId, Long periodoIdExcluir, BigDecimal nuevoPorcentaje) {
        List<PeriodoAcademico> periodos = periodoAcademicoRepository.findByAnioLectivoIdOrderByNumeroAsc(anioLectivoId);

        BigDecimal suma = periodos.stream()
                .filter(p -> periodoIdExcluir == null || !p.getId().equals(periodoIdExcluir))
                .map(PeriodoAcademico::getPorcentaje)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal total = suma.add(nuevoPorcentaje);

        if (total.compareTo(new BigDecimal("100.00")) > 0) {
            throw new ConflictoNegocioException("La suma de los porcentajes de los periodos no puede superar el 100%. "
                    + "Total acumulado resultante: " + total + "%");
        }
    }
}
