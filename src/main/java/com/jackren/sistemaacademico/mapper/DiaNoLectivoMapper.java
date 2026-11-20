package com.jackren.sistemaacademico.mapper;

import com.jackren.sistemaacademico.dto.DiaNoLectivoRequest;
import com.jackren.sistemaacademico.dto.DiaNoLectivoResponse;
import com.jackren.sistemaacademico.entity.AnioLectivo;
import com.jackren.sistemaacademico.entity.DiaNoLectivo;
import org.springframework.stereotype.Component;

@Component
public class DiaNoLectivoMapper {

    public DiaNoLectivo toEntity(DiaNoLectivoRequest request, AnioLectivo anioLectivo) {
        if (request == null) {
            return null;
        }

        DiaNoLectivo dia = new DiaNoLectivo();
        dia.setAnioLectivo(anioLectivo);
        dia.setNombre(request.getNombre());
        dia.setFechaInicio(request.getFechaInicio());
        dia.setFechaFin(request.getFechaFin());
        dia.setTipo(request.getTipo());
        dia.setMotivo(request.getMotivo());
        dia.setSede(request.getSede());
        dia.setActivo(true);
        return dia;
    }

    public void updateEntityFromRequest(DiaNoLectivo dia, DiaNoLectivoRequest request) {
        if (dia == null || request == null) {
            return;
        }

        dia.setNombre(request.getNombre());
        dia.setFechaInicio(request.getFechaInicio());
        dia.setFechaFin(request.getFechaFin());
        dia.setTipo(request.getTipo());
        dia.setMotivo(request.getMotivo());
        dia.setSede(request.getSede());
    }

    public DiaNoLectivoResponse toResponse(DiaNoLectivo dia) {
        if (dia == null) {
            return null;
        }

        Long anioId = dia.getAnioLectivo() != null ? dia.getAnioLectivo().getId() : null;
        Integer anioValor = dia.getAnioLectivo() != null ? dia.getAnioLectivo().getAnio() : null;

        return new DiaNoLectivoResponse(
                dia.getId(),
                anioId,
                anioValor,
                dia.getNombre(),
                dia.getFechaInicio(),
                dia.getFechaFin(),
                dia.getTipo(),
                dia.getMotivo(),
                dia.getSede(),
                dia.getActivo(),
                dia.getFechaCreacion(),
                dia.getFechaActualizacion()
        );
    }
}
