package com.jackren.sistemaacademico.service;

import com.jackren.sistemaacademico.dto.DiaNoLectivoRequest;
import com.jackren.sistemaacademico.dto.DiaNoLectivoResponse;
import com.jackren.sistemaacademico.entity.AnioLectivo;
import com.jackren.sistemaacademico.entity.DiaNoLectivo;
import com.jackren.sistemaacademico.exception.ConflictoNegocioException;
import com.jackren.sistemaacademico.exception.RecursoNoEncontradoException;
import com.jackren.sistemaacademico.mapper.DiaNoLectivoMapper;
import com.jackren.sistemaacademico.repository.AnioLectivoRepository;
import com.jackren.sistemaacademico.repository.DiaNoLectivoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class DiaNoLectivoService {

    private final DiaNoLectivoRepository diaNoLectivoRepository;
    private final AnioLectivoRepository anioLectivoRepository;
    private final DiaNoLectivoMapper diaNoLectivoMapper;
    private final AuditoriaGlobalService auditoriaGlobalService;

    public DiaNoLectivoService(DiaNoLectivoRepository diaNoLectivoRepository,
                               AnioLectivoRepository anioLectivoRepository,
                               DiaNoLectivoMapper diaNoLectivoMapper,
                               AuditoriaGlobalService auditoriaGlobalService) {
        this.diaNoLectivoRepository = diaNoLectivoRepository;
        this.anioLectivoRepository = anioLectivoRepository;
        this.diaNoLectivoMapper = diaNoLectivoMapper;
        this.auditoriaGlobalService = auditoriaGlobalService;
    }

    public List<DiaNoLectivoResponse> listarPorAnio(Long anioLectivoId) {
        if (!anioLectivoRepository.existsById(anioLectivoId)) {
            throw new RecursoNoEncontradoException("No se encontró el año lectivo con ID: " + anioLectivoId);
        }

        return diaNoLectivoRepository.findByAnioLectivoIdAndActivoTrueOrderByFechaInicioAsc(anioLectivoId)
                .stream()
                .map(diaNoLectivoMapper::toResponse)
                .toList();
    }

    public DiaNoLectivoResponse obtenerPorId(Long id) {
        DiaNoLectivo dia = diaNoLectivoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el día no lectivo con ID: " + id));
        return diaNoLectivoMapper.toResponse(dia);
    }

    public boolean esFechaNoLectiva(Long anioLectivoId, LocalDate fecha) {
        return diaNoLectivoRepository.esDiaNoLectivo(anioLectivoId, fecha);
    }

    @Transactional
    public DiaNoLectivoResponse guardar(Long anioLectivoId, DiaNoLectivoRequest request) {
        AnioLectivo anio = anioLectivoRepository.findById(anioLectivoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el año lectivo con ID: " + anioLectivoId));

        validarFechas(anio, request);

        DiaNoLectivo dia = diaNoLectivoMapper.toEntity(request, anio);
        DiaNoLectivo guardado = diaNoLectivoRepository.save(dia);

        auditoriaGlobalService.registrarEvento(
                "CREACION_DIA_NO_LECTIVO",
                "DiaNoLectivo",
                guardado.getId().toString(),
                null,
                "Evento: " + guardado.getNombre() + " (" + guardado.getTipo() + ", " + guardado.getFechaInicio() + " a " + guardado.getFechaFin() + ")",
                "Registro de fecha no lectiva en calendario escolar (UC-14 A4)",
                "EXITOSO"
        );

        return diaNoLectivoMapper.toResponse(guardado);
    }

    @Transactional
    public DiaNoLectivoResponse actualizar(Long id, DiaNoLectivoRequest request) {
        DiaNoLectivo dia = diaNoLectivoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el día no lectivo con ID: " + id));

        validarFechas(dia.getAnioLectivo(), request);

        String valorAnterior = "Nombre: " + dia.getNombre() + ", Rango: " + dia.getFechaInicio() + " a " + dia.getFechaFin();
        diaNoLectivoMapper.updateEntityFromRequest(dia, request);
        DiaNoLectivo actualizado = diaNoLectivoRepository.save(dia);

        auditoriaGlobalService.registrarEvento(
                "ACTUALIZACION_DIA_NO_LECTIVO",
                "DiaNoLectivo",
                actualizado.getId().toString(),
                valorAnterior,
                "Nombre: " + actualizado.getNombre() + ", Rango: " + actualizado.getFechaInicio() + " a " + actualizado.getFechaFin(),
                "Actualización de día no lectivo en calendario",
                "EXITOSO"
        );

        return diaNoLectivoMapper.toResponse(actualizado);
    }

    @Transactional
    public void desactivar(Long id) {
        DiaNoLectivo dia = diaNoLectivoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el día no lectivo con ID: " + id));

        dia.setActivo(false);
        diaNoLectivoRepository.save(dia);

        auditoriaGlobalService.registrarEvento(
                "DESACTIVACION_DIA_NO_LECTIVO",
                "DiaNoLectivo",
                id.toString(),
                "Activo: true",
                "Activo: false",
                "Eliminación lógica de día no lectivo",
                "EXITOSO"
        );
    }

    private void validarFechas(AnioLectivo anio, DiaNoLectivoRequest request) {
        if (request.getFechaFin().isBefore(request.getFechaInicio())) {
            throw new ConflictoNegocioException("La fecha de fin no puede ser anterior a la fecha de inicio.");
        }

        if (request.getFechaInicio().isBefore(anio.getFechaInicio()) || request.getFechaFin().isAfter(anio.getFechaFin())) {
            throw new ConflictoNegocioException("Las fechas del evento no lectivo deben encontrarse dentro del rango del año lectivo ("
                    + anio.getFechaInicio() + " a " + anio.getFechaFin() + ").");
        }
    }
}
