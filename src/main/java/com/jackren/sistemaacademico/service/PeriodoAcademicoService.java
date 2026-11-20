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
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class PeriodoAcademicoService {

    private final PeriodoAcademicoRepository periodoAcademicoRepository;
    private final AnioLectivoRepository anioLectivoRepository;
    private final PeriodoAcademicoMapper periodoAcademicoMapper;
    private final AuditoriaGlobalService auditoriaGlobalService;

    public PeriodoAcademicoService(PeriodoAcademicoRepository periodoAcademicoRepository,
                                   AnioLectivoRepository anioLectivoRepository,
                                   PeriodoAcademicoMapper periodoAcademicoMapper,
                                   AuditoriaGlobalService auditoriaGlobalService) {
        this.periodoAcademicoRepository = periodoAcademicoRepository;
        this.anioLectivoRepository = anioLectivoRepository;
        this.periodoAcademicoMapper = periodoAcademicoMapper;
        this.auditoriaGlobalService = auditoriaGlobalService;
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
        validarNoSolapamiento(anioLectivoId, null, request.getFechaInicio(), request.getFechaFin());

        if (periodoAcademicoRepository.existsByAnioLectivoIdAndNumero(anioLectivoId, request.getNumero())) {
            throw new ConflictoNegocioException("Ya existe el periodo número " + request.getNumero()
                    + " para el año lectivo " + anio.getAnio());
        }

        validarSumaPorcentajes(anioLectivoId, null, request.getPorcentaje());

        PeriodoAcademico periodo = periodoAcademicoMapper.toEntity(request, anio);
        PeriodoAcademico guardado = periodoAcademicoRepository.save(periodo);

        if (auditoriaGlobalService != null) {
            auditoriaGlobalService.registrarEvento(
                    "CREACION_PERIODO",
                    "PeriodoAcademico",
                    guardado.getId().toString(),
                    null,
                    "Periodo " + guardado.getNumero() + " (" + guardado.getNombre() + ") en año " + anio.getAnio(),
                    "Configuración de periodo académico",
                    "EXITOSO"
            );
        }

        return periodoAcademicoMapper.toResponse(guardado);
    }

    @Transactional
    public PeriodoAcademicoResponse actualizar(Long id, PeriodoAcademicoRequest request) {
        PeriodoAcademico periodo = periodoAcademicoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el periodo académico con ID: " + id));

        if (periodo.getEstado() == EstadoPeriodo.CERRADO) {
            throw new ConflictoNegocioException("No se puede modificar ordinariamente un periodo académico CERRADO (UC-14 E4). "
                    + "Se requiere el flujo rectoral de ajuste excepcional con número de resolución (A5).");
        }

        AnioLectivo anio = periodo.getAnioLectivo();
        validarFechasDentroDeAnio(anio, request);
        validarNoSolapamiento(anio.getId(), id, request.getFechaInicio(), request.getFechaFin());

        if (!periodo.getNumero().equals(request.getNumero())
                && periodoAcademicoRepository.existsByAnioLectivoIdAndNumero(anio.getId(), request.getNumero())) {
            throw new ConflictoNegocioException("Ya existe el periodo número " + request.getNumero()
                    + " para el año lectivo " + anio.getAnio());
        }

        validarSumaPorcentajes(anio.getId(), id, request.getPorcentaje());

        String anterior = "Nombre: " + periodo.getNombre() + ", Rango: " + periodo.getFechaInicio() + " a " + periodo.getFechaFin() + ", %: " + periodo.getPorcentaje();
        periodoAcademicoMapper.updateEntityFromRequest(periodo, request);
        PeriodoAcademico actualizado = periodoAcademicoRepository.save(periodo);

        if (auditoriaGlobalService != null) {
            auditoriaGlobalService.registrarEvento(
                    "ACTUALIZACION_PERIODO",
                    "PeriodoAcademico",
                    actualizado.getId().toString(),
                    anterior,
                    "Nombre: " + actualizado.getNombre() + ", Rango: " + actualizado.getFechaInicio() + " a " + actualizado.getFechaFin() + ", %: " + actualizado.getPorcentaje(),
                    "Actualización de parámetros del periodo",
                    "EXITOSO"
            );
        }

        return periodoAcademicoMapper.toResponse(actualizado);
    }

    @Transactional
    public PeriodoAcademicoResponse abrir(Long id) {
        PeriodoAcademico periodo = periodoAcademicoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el periodo académico con ID: " + id));

        periodo.setEstado(EstadoPeriodo.ABIERTO);
        PeriodoAcademico guardado = periodoAcademicoRepository.save(periodo);

        if (auditoriaGlobalService != null) {
            auditoriaGlobalService.registrarEvento(
                    "APERTURA_PERIODO",
                    "PeriodoAcademico",
                    guardado.getId().toString(),
                    "Estado: CERRADO",
                    "Estado: ABIERTO",
                    "Apertura de periodo académico para registro de evaluaciones",
                    "EXITOSO"
            );
        }

        return periodoAcademicoMapper.toResponse(guardado);
    }

    @Transactional
    public PeriodoAcademicoResponse cerrar(Long id) {
        PeriodoAcademico periodo = periodoAcademicoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el periodo académico con ID: " + id));

        periodo.setEstado(EstadoPeriodo.CERRADO);
        PeriodoAcademico guardado = periodoAcademicoRepository.save(periodo);

        if (auditoriaGlobalService != null) {
            auditoriaGlobalService.registrarEvento(
                    "CIERRE_PERIODO",
                    "PeriodoAcademico",
                    guardado.getId().toString(),
                    "Estado: ABIERTO",
                    "Estado: CERRADO",
                    "Cierre formal del periodo académico",
                    "EXITOSO"
            );
        }

        return periodoAcademicoMapper.toResponse(guardado);
    }

    @Transactional
    public PeriodoAcademicoResponse ajusteExcepcionalRectoral(Long id, PeriodoAcademicoRequest request, String motivo, String resolucionRectoral) {
        PeriodoAcademico periodo = periodoAcademicoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el periodo académico con ID: " + id));

        if (motivo == null || motivo.trim().isBlank()) {
            throw new IllegalArgumentException("El motivo del ajuste excepcional rectoral es obligatorio (UC-14 A5).");
        }
        if (resolucionRectoral == null || resolucionRectoral.trim().isBlank()) {
            throw new IllegalArgumentException("La resolución rectoral o acta de autorización es obligatoria para ajustes excepcionales.");
        }

        AnioLectivo anio = periodo.getAnioLectivo();
        validarFechasDentroDeAnio(anio, request);
        validarNoSolapamiento(anio.getId(), id, request.getFechaInicio(), request.getFechaFin());
        validarSumaPorcentajes(anio.getId(), id, request.getPorcentaje());

        String valorAnterior = "Estado: " + periodo.getEstado() + ", F.Inicio: " + periodo.getFechaInicio() + ", F.Fin: " + periodo.getFechaFin();
        periodoAcademicoMapper.updateEntityFromRequest(periodo, request);
        periodo.setMotivoAjusteExcepcional(motivo);
        periodo.setResolucionRectoralExcepcional(resolucionRectoral);
        PeriodoAcademico actualizado = periodoAcademicoRepository.save(periodo);

        if (auditoriaGlobalService != null) {
            auditoriaGlobalService.registrarEvento(
                    "AJUSTE_EXCEPCIONAL_PERIODO",
                    "PeriodoAcademico",
                    id.toString(),
                    valorAnterior,
                    "Ajuste excepcional: " + motivo + " (Resolución: " + resolucionRectoral + ")",
                    motivo,
                    "EXITOSO"
            );
        }

        return periodoAcademicoMapper.toResponse(actualizado);
    }

    public void validarDistribucionCompleta(Long anioLectivoId) {
        List<PeriodoAcademico> periodos = periodoAcademicoRepository.findByAnioLectivoIdOrderByNumeroAsc(anioLectivoId);
        if (periodos.isEmpty()) {
            throw new ConflictoNegocioException("El año lectivo no tiene periodos académicos configurados.");
        }

        BigDecimal suma = periodos.stream()
                .map(PeriodoAcademico::getPorcentaje)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (suma.compareTo(new BigDecimal("100.00")) != 0) {
            throw new ConflictoNegocioException("La suma de las ponderaciones de los periodos debe ser exactamente 100.00% conforme al SIEE (UC-14 E2, RN-03). Total acumulado actual: " + suma + "%");
        }
    }

    private void validarNoSolapamiento(Long anioLectivoId, Long periodoIdExcluir, LocalDate inicio, LocalDate fin) {
        List<PeriodoAcademico> periodos = periodoAcademicoRepository.findByAnioLectivoIdOrderByNumeroAsc(anioLectivoId);
        for (PeriodoAcademico p : periodos) {
            if (periodoIdExcluir != null && p.getId().equals(periodoIdExcluir)) {
                continue;
            }
            boolean solapa = !(fin.isBefore(p.getFechaInicio()) || inicio.isAfter(p.getFechaFin()));
            if (solapa) {
                throw new ConflictoNegocioException("Las fechas del periodo [" + inicio + " a " + fin
                        + "] se solapan con el periodo " + p.getNumero() + " (" + p.getNombre() + ": "
                        + p.getFechaInicio() + " a " + p.getFechaFin() + ") (UC-14 E1).");
            }
        }
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

        if (request.getFechaInicioDigitacion() != null && request.getFechaFinDigitacion() != null) {
            if (request.getFechaFinDigitacion().isBefore(request.getFechaInicioDigitacion())) {
                throw new ConflictoNegocioException("La fecha de fin de digitación no puede ser anterior a la fecha de inicio de digitación.");
            }
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
                    + "Total acumulado resultante: " + total + "% (UC-14 E2).");
        }
    }
}
