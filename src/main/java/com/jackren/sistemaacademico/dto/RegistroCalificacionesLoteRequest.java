package com.jackren.sistemaacademico.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class RegistroCalificacionesLoteRequest {

    @NotNull(message = "La actividad evaluativa es obligatoria")
    private Long actividadId;

    @NotEmpty(message = "Debe proporcionar al menos una calificación")
    private List<@Valid RegistroCalificacionRequest> calificaciones;

    public RegistroCalificacionesLoteRequest() {
    }

    public RegistroCalificacionesLoteRequest(Long actividadId, List<RegistroCalificacionRequest> calificaciones) {
        this.actividadId = actividadId;
        this.calificaciones = calificaciones;
    }

    public Long getActividadId() {
        return actividadId;
    }

    public void setActividadId(Long actividadId) {
        this.actividadId = actividadId;
    }

    public List<RegistroCalificacionRequest> getCalificaciones() {
        return calificaciones;
    }

    public void setCalificaciones(List<RegistroCalificacionRequest> calificaciones) {
        this.calificaciones = calificaciones;
    }
}
