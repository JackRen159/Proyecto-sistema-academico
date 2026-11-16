package com.jackren.sistemaacademico.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class ActualizarCalificacionRequest {

    @NotNull(message = "El nuevo valor de la calificación es obligatorio")
    private BigDecimal valorNuevo;

    @NotBlank(message = "El motivo de la modificación es obligatorio")
    @Size(max = 255, message = "El motivo no puede superar 255 caracteres")
    private String motivo;

    @Size(max = 255, message = "Las observaciones no pueden superar 255 caracteres")
    private String observaciones;

    public ActualizarCalificacionRequest() {
    }

    public ActualizarCalificacionRequest(BigDecimal valorNuevo, String motivo, String observaciones) {
        this.valorNuevo = valorNuevo;
        this.motivo = motivo;
        this.observaciones = observaciones;
    }

    public BigDecimal getValorNuevo() {
        return valorNuevo;
    }

    public void setValorNuevo(BigDecimal valorNuevo) {
        this.valorNuevo = valorNuevo;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}
