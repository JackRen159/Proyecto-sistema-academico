package com.jackren.sistemaacademico.mapper;

import com.jackren.sistemaacademico.dto.PeriodoAcademicoRequest;
import com.jackren.sistemaacademico.dto.PeriodoAcademicoResponse;
import com.jackren.sistemaacademico.entity.AnioLectivo;
import com.jackren.sistemaacademico.entity.EstadoPeriodo;
import com.jackren.sistemaacademico.entity.PeriodoAcademico;
import org.springframework.stereotype.Component;

@Component
public class PeriodoAcademicoMapper {

    public PeriodoAcademico toEntity(PeriodoAcademicoRequest request, AnioLectivo anioLectivo) {
        if (request == null) {
            return null;
        }

        PeriodoAcademico periodo = new PeriodoAcademico();
        periodo.setAnioLectivo(anioLectivo);
        periodo.setNumero(request.getNumero());
        periodo.setNombre(request.getNombre());
        periodo.setFechaInicio(request.getFechaInicio());
        periodo.setFechaFin(request.getFechaFin());
        periodo.setPorcentaje(request.getPorcentaje());
        periodo.setEstado(EstadoPeriodo.CERRADO);
        return periodo;
    }

    public void updateEntityFromRequest(PeriodoAcademico periodo, PeriodoAcademicoRequest request) {
        if (periodo == null || request == null) {
            return;
        }

        periodo.setNumero(request.getNumero());
        periodo.setNombre(request.getNombre());
        periodo.setFechaInicio(request.getFechaInicio());
        periodo.setFechaFin(request.getFechaFin());
        periodo.setPorcentaje(request.getPorcentaje());
    }

    public PeriodoAcademicoResponse toResponse(PeriodoAcademico periodo) {
        if (periodo == null) {
            return null;
        }

        Long anioId = periodo.getAnioLectivo() != null ? periodo.getAnioLectivo().getId() : null;

        return new PeriodoAcademicoResponse(
                periodo.getId(),
                anioId,
                periodo.getNumero(),
                periodo.getNombre(),
                periodo.getFechaInicio(),
                periodo.getFechaFin(),
                periodo.getPorcentaje(),
                periodo.getEstado(),
                periodo.getFechaCreacion(),
                periodo.getFechaActualizacion()
        );
    }
}
