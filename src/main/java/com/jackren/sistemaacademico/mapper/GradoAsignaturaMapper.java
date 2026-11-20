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
        if (request.getPesoPorcentualEnArea() != null) {
            gradoAsignatura.setPesoPorcentualEnArea(request.getPesoPorcentualEnArea());
        }
        if (request.getTipoComponente() != null) {
            gradoAsignatura.setTipoComponente(request.getTipoComponente());
        }
        if (request.getComputaPromocion() != null) {
            gradoAsignatura.setComputaPromocion(request.getComputaPromocion());
        }
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

        Long anioId = ga.getAnioLectivo() != null ? ga.getAnioLectivo().getId() : null;
        Integer anioValor = ga.getAnioLectivo() != null ? ga.getAnioLectivo().getAnio() : null;

        return new GradoAsignaturaResponse(
                ga.getId(),
                gradoId,
                gradoCodigo,
                gradoNombre,
                asigId,
                asigCodigo,
                asigNombre,
                anioId,
                anioValor,
                ga.getIntensidadHoraria(),
                ga.getEsOptativa(),
                ga.getPesoPorcentualEnArea(),
                ga.getTipoComponente(),
                ga.getComputaPromocion(),
                ga.getActivo(),
                ga.getFechaCreacion(),
                ga.getFechaActualizacion()
        );
    }
}
