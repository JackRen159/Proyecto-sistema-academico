package com.jackren.sistemaacademico.mapper;

import com.jackren.sistemaacademico.dto.AreaRequest;
import com.jackren.sistemaacademico.dto.AreaResponse;
import com.jackren.sistemaacademico.entity.Area;
import org.springframework.stereotype.Component;

@Component
public class AreaMapper {

    public Area toEntity(AreaRequest request) {
        if (request == null) {
            return null;
        }

        Area area = new Area();
        area.setCodigo(request.getCodigo());
        area.setNombre(request.getNombre());
        area.setDescripcion(request.getDescripcion());
        area.setActivo(true);
        return area;
    }

    public void updateEntityFromRequest(Area area, AreaRequest request) {
        if (area == null || request == null) {
            return;
        }

        area.setCodigo(request.getCodigo());
        area.setNombre(request.getNombre());
        area.setDescripcion(request.getDescripcion());
    }

    public AreaResponse toResponse(Area area) {
        if (area == null) {
            return null;
        }

        return new AreaResponse(
                area.getId(),
                area.getCodigo(),
                area.getNombre(),
                area.getDescripcion(),
                area.getActivo(),
                area.getFechaCreacion(),
                area.getFechaActualizacion()
        );
    }
}
