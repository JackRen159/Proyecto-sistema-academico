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
        periodo.setFechaInicioDigitacion(request.getFechaInicioDigitacion());
        periodo.setFechaFinDigitacion(request.getFechaFinDigitacion());
        periodo.setNumeroActaAprobacion(request.getNumeroActaAprobacion());
        periodo.setFechaActaAprobacion(request.getFechaActaAprobacion());
        periodo.setOrganoAprobador(request.getOrganoAprobador());
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
        if (request.getFechaInicioDigitacion() != null) {
            periodo.setFechaInicioDigitacion(request.getFechaInicioDigitacion());
        }
        if (request.getFechaFinDigitacion() != null) {
            periodo.setFechaFinDigitacion(request.getFechaFinDigitacion());
        }
        if (request.getNumeroActaAprobacion() != null) {
            periodo.setNumeroActaAprobacion(request.getNumeroActaAprobacion());
        }
        if (request.getFechaActaAprobacion() != null) {
            periodo.setFechaActaAprobacion(request.getFechaActaAprobacion());
        }
        if (request.getOrganoAprobador() != null) {
            periodo.setOrganoAprobador(request.getOrganoAprobador());
        }
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
                periodo.getFechaInicioDigitacion(),
                periodo.getFechaFinDigitacion(),
                periodo.getNumeroActaAprobacion(),
                periodo.getFechaActaAprobacion(),
                periodo.getOrganoAprobador(),
                periodo.getFechaCreacion(),
                periodo.getFechaActualizacion()
        );
    }
}
