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
        if (request.getTipoCalendario() != null) {
            anio.setTipoCalendario(request.getTipoCalendario());
        }
        if (request.getSemanasLectivas() != null) {
            anio.setSemanasLectivas(request.getSemanasLectivas());
        }
        anio.setNumeroActaAprobacion(request.getNumeroActaAprobacion());
        anio.setFechaActaAprobacion(request.getFechaActaAprobacion());
        anio.setOrganoAprobador(request.getOrganoAprobador());
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
        if (request.getTipoCalendario() != null) {
            anio.setTipoCalendario(request.getTipoCalendario());
        }
        if (request.getSemanasLectivas() != null) {
            anio.setSemanasLectivas(request.getSemanasLectivas());
        }
        if (request.getNumeroActaAprobacion() != null) {
            anio.setNumeroActaAprobacion(request.getNumeroActaAprobacion());
        }
        if (request.getFechaActaAprobacion() != null) {
            anio.setFechaActaAprobacion(request.getFechaActaAprobacion());
        }
        if (request.getOrganoAprobador() != null) {
            anio.setOrganoAprobador(request.getOrganoAprobador());
        }
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
                anio.getTipoCalendario(),
                anio.getSemanasLectivas(),
                anio.getNumeroActaAprobacion(),
                anio.getFechaActaAprobacion(),
                anio.getOrganoAprobador(),
                anio.getFechaCreacion(),
                anio.getFechaActualizacion()
        );
    }
}
