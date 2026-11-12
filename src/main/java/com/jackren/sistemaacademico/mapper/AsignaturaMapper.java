package com.jackren.sistemaacademico.mapper;

import com.jackren.sistemaacademico.dto.AsignaturaRequest;
import com.jackren.sistemaacademico.dto.AsignaturaResponse;
import com.jackren.sistemaacademico.entity.Area;
import com.jackren.sistemaacademico.entity.Asignatura;
import org.springframework.stereotype.Component;

@Component
public class AsignaturaMapper {

    public Asignatura toEntity(AsignaturaRequest request, Area area) {
        if (request == null) {
            return null;
        }

        Asignatura asignatura = new Asignatura();
        asignatura.setArea(area);
        asignatura.setCodigo(request.getCodigo());
        asignatura.setNombre(request.getNombre());
        asignatura.setDescripcion(request.getDescripcion());
        asignatura.setActivo(true);
        return asignatura;
    }

    public void updateEntityFromRequest(Asignatura asignatura, AsignaturaRequest request, Area area) {
        if (asignatura == null || request == null) {
            return;
        }

        asignatura.setArea(area);
        asignatura.setCodigo(request.getCodigo());
        asignatura.setNombre(request.getNombre());
        asignatura.setDescripcion(request.getDescripcion());
    }

    public AsignaturaResponse toResponse(Asignatura asignatura) {
        if (asignatura == null) {
            return null;
        }

        Long areaId = asignatura.getArea() != null ? asignatura.getArea().getId() : null;
        String areaNombre = asignatura.getArea() != null ? asignatura.getArea().getNombre() : null;

        return new AsignaturaResponse(
                asignatura.getId(),
                areaId,
                areaNombre,
                asignatura.getCodigo(),
                asignatura.getNombre(),
                asignatura.getDescripcion(),
                asignatura.getActivo(),
                asignatura.getFechaCreacion(),
                asignatura.getFechaActualizacion()
        );
    }
}
