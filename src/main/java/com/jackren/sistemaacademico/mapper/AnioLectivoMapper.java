package com.jackren.sistemaacademico.mapper;

import com.jackren.sistemaacademico.dto.AnioLectivoRequest;
import com.jackren.sistemaacademico.dto.AnioLectivoResponse;
import com.jackren.sistemaacademico.entity.AnioLectivo;
import com.jackren.sistemaacademico.entity.EstadoAnioLectivo;
import org.springframework.stereotype.Component;

@Component
public class AnioLectivoMapper {

    public AnioLectivo toEntity(AnioLectivoRequest request) {
        if (request == null) {
            return null;
        }

        AnioLectivo anio = new AnioLectivo();
        anio.setAnio(request.getAnio());
        anio.setNombre(request.getNombre());
        anio.setFechaInicio(request.getFechaInicio());
        anio.setFechaFin(request.getFechaFin());
        anio.setEstado(EstadoAnioLectivo.PLANIFICACION);
        return anio;
    }

    public void updateEntityFromRequest(AnioLectivo anio, AnioLectivoRequest request) {
        if (anio == null || request == null) {
            return;
        }

        anio.setAnio(request.getAnio());
        anio.setNombre(request.getNombre());
        anio.setFechaInicio(request.getFechaInicio());
        anio.setFechaFin(request.getFechaFin());
    }

    public AnioLectivoResponse toResponse(AnioLectivo anio) {
        if (anio == null) {
            return null;
        }

        return new AnioLectivoResponse(
                anio.getId(),
                anio.getAnio(),
                anio.getNombre(),
                anio.getFechaInicio(),
                anio.getFechaFin(),
                anio.getEstado(),
                anio.getFechaCreacion(),
                anio.getFechaActualizacion()
        );
    }
}
