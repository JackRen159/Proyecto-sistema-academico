package com.jackren.sistemaacademico.mapper;

import com.jackren.sistemaacademico.dto.GradoResponse;
import com.jackren.sistemaacademico.entity.Grado;
import org.springframework.stereotype.Component;

@Component
public class GradoMapper {

    public GradoResponse toResponse(Grado grado) {
        if (grado == null) {
            return null;
        }

        return new GradoResponse(
                grado.getId(),
                grado.getCodigo(),
                grado.getNombre(),
                grado.getNivel(),
                grado.getOrdenJerarquico(),
                grado.getActivo()
        );
    }
}
