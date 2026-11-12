package com.jackren.sistemaacademico.mapper;

import com.jackren.sistemaacademico.dto.GrupoRequest;
import com.jackren.sistemaacademico.dto.GrupoResponse;
import com.jackren.sistemaacademico.entity.AnioLectivo;
import com.jackren.sistemaacademico.entity.Grado;
import com.jackren.sistemaacademico.entity.Grupo;
import com.jackren.sistemaacademico.entity.Jornada;
import org.springframework.stereotype.Component;

@Component
public class GrupoMapper {

    public Grupo toEntity(GrupoRequest request, AnioLectivo anioLectivo, Grado grado) {
        if (request == null) {
            return null;
        }

        Grupo grupo = new Grupo();
        grupo.setAnioLectivo(anioLectivo);
        grupo.setGrado(grado);
        grupo.setNombre(request.getNombre());
        grupo.setJornada(request.getJornada() != null ? request.getJornada() : Jornada.MANANA);
        grupo.setCupoMaximo(request.getCupoMaximo() != null ? request.getCupoMaximo() : 35);
        grupo.setActivo(true);
        return grupo;
    }

    public void updateEntityFromRequest(Grupo grupo, GrupoRequest request, AnioLectivo anioLectivo, Grado grado) {
        if (grupo == null || request == null) {
            return;
        }

        grupo.setAnioLectivo(anioLectivo);
        grupo.setGrado(grado);
        grupo.setNombre(request.getNombre());
        if (request.getJornada() != null) {
            grupo.setJornada(request.getJornada());
        }
        if (request.getCupoMaximo() != null) {
            grupo.setCupoMaximo(request.getCupoMaximo());
        }
    }

    public GrupoResponse toResponse(Grupo grupo) {
        if (grupo == null) {
            return null;
        }

        Long anioId = grupo.getAnioLectivo() != null ? grupo.getAnioLectivo().getId() : null;
        Integer anioValor = grupo.getAnioLectivo() != null ? grupo.getAnioLectivo().getAnio() : null;

        Long gradoId = grupo.getGrado() != null ? grupo.getGrado().getId() : null;
        String gradoCodigo = grupo.getGrado() != null ? grupo.getGrado().getCodigo() : null;
        String gradoNombre = grupo.getGrado() != null ? grupo.getGrado().getNombre() : null;

        return new GrupoResponse(
                grupo.getId(),
                anioId,
                anioValor,
                gradoId,
                gradoCodigo,
                gradoNombre,
                grupo.getNombre(),
                grupo.getJornada(),
                grupo.getCupoMaximo(),
                grupo.getActivo(),
                grupo.getFechaCreacion(),
                grupo.getFechaActualizacion()
        );
    }
}
