package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.DecisionSolicitudExcusa;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class ResolverSolicitudExcusaRequest {

    @NotNull(message = "La decisión de la solicitud de excusa es obligatoria")
    private DecisionSolicitudExcusa decision;

    @Size(max = 2000, message = "Las observaciones no pueden superar 2000 caracteres")
    private String observacionesResolucion;

    private LocalDate fechaLimiteSubsanacion;

    public ResolverSolicitudExcusaRequest() {
    }

    public DecisionSolicitudExcusa getDecision() {
        return decision;
    }

    public void setDecision(DecisionSolicitudExcusa decision) {
        this.decision = decision;
    }

    public String getObservacionesResolucion() {
        return observacionesResolucion;
    }

    public void setObservacionesResolucion(String observacionesResolucion) {
        this.observacionesResolucion = observacionesResolucion;
    }

    public LocalDate getFechaLimiteSubsanacion() {
        return fechaLimiteSubsanacion;
    }

    public void setFechaLimiteSubsanacion(LocalDate fechaLimiteSubsanacion) {
        this.fechaLimiteSubsanacion = fechaLimiteSubsanacion;
    }
}
