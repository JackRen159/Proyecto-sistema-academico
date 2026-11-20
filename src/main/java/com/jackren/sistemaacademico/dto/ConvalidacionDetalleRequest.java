package com.jackren.sistemaacademico.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class ConvalidacionDetalleRequest {

    @NotNull(message = "La asignatura es obligatoria")
    private Long asignaturaId;

    @NotNull(message = "La nota convalidada es obligatoria")
    private BigDecimal notaConvalidada;

    @Size(max = 255, message = "Las observaciones no pueden superar 255 caracteres")
    private String observaciones;

    public ConvalidacionDetalleRequest() {
    }

    public ConvalidacionDetalleRequest(Long asignaturaId, BigDecimal notaConvalidada, String observaciones) {
        this.asignaturaId = asignaturaId;
        this.notaConvalidada = notaConvalidada;
        this.observaciones = observaciones;
    }

    public Long getAsignaturaId() {
        return asignaturaId;
    }

    public void setAsignaturaId(Long asignaturaId) {
        this.asignaturaId = asignaturaId;
    }

    public BigDecimal getNotaConvalidada() {
        return notaConvalidada;
    }

    public void setNotaConvalidada(BigDecimal notaConvalidada) {
        this.notaConvalidada = notaConvalidada;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}
