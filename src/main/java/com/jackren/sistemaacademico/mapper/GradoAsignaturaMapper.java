package com.jackren.sistemaacademico.mapper;

import com.jackren.sistemaacademico.dto.GradoAsignaturaRequest;
import com.jackren.sistemaacademico.dto.GradoAsignaturaResponse;
import com.jackren.sistemaacademico.entity.Asignatura;
import com.jackren.sistemaacademico.entity.Grado;
import com.jackren.sistemaacademico.entity.GradoAsignatura;
import org.springframework.stereotype.Component;

@Component
public class GradoAsignaturaMapper {

    public GradoAsignatura toEntity(GradoAsignaturaRequest request, Grado grado, Asignatura asignatura) {
        if (request == null) {
            return null;
        }

        GradoAsignatura gradoAsignatura = new GradoAsignatura();
        gradoAsignatura.setGrado(grado);
        gradoAsignatura.setAsignatura(asignatura);
        gradoAsignatura.setIntensidadHoraria(request.getIntensidadHoraria());
        gradoAsignatura.setEsOptativa(request.getEsOptativa() != null ? request.getEsOptativa() : false);
        gradoAsignatura.setActivo(true);
        return gradoAsignatura;
    }

    public GradoAsignaturaResponse toResponse(GradoAsignatura ga) {
        if (ga == null) {
            return null;
        }

        Long gradoId = ga.getGrado() != null ? ga.getGrado().getId() : null;
        String gradoCodigo = ga.getGrado() != null ? ga.getGrado().getCodigo() : null;
        String gradoNombre = ga.getGrado() != null ? ga.getGrado().getNombre() : null;

        Long asigId = ga.getAsignatura() != null ? ga.getAsignatura().getId() : null;
        String asigCodigo = ga.getAsignatura() != null ? ga.getAsignatura().getCodigo() : null;
        String asigNombre = ga.getAsignatura() != null ? ga.getAsignatura().getNombre() : null;

        return new GradoAsignaturaResponse(
                ga.getId(),
                gradoId,
                gradoCodigo,
                gradoNombre,
                asigId,
                asigCodigo,
                asigNombre,
                ga.getIntensidadHoraria(),
                ga.getEsOptativa(),
                ga.getActivo(),
                ga.getFechaCreacion(),
                ga.getFechaActualizacion()
        );
    }
}
