package com.jackren.sistemaacademico.dto;

import jakarta.validation.constraints.NotEmpty;

import java.math.BigDecimal;
import java.util.List;

public class SimulacionSieeRequest {

    @NotEmpty(message = "Debe proporcionar al menos una calificación para la simulación")
    private List<BigDecimal> calificaciones;

    private List<BigDecimal> ponderaciones;
    private BigDecimal calificacionRecuperacion;

    public SimulacionSieeRequest() {
    }

    public SimulacionSieeRequest(List<BigDecimal> calificaciones, List<BigDecimal> ponderaciones, BigDecimal calificacionRecuperacion) {
        this.calificaciones = calificaciones;
        this.ponderaciones = ponderaciones;
        this.calificacionRecuperacion = calificacionRecuperacion;
    }

    public List<BigDecimal> getCalificaciones() {
        return calificaciones;
    }

    public void setCalificaciones(List<BigDecimal> calificaciones) {
        this.calificaciones = calificaciones;
    }

    public List<BigDecimal> getPonderaciones() {
        return ponderaciones;
    }

    public void setPonderaciones(List<BigDecimal> ponderaciones) {
        this.ponderaciones = ponderaciones;
    }

    public BigDecimal getCalificacionRecuperacion() {
        return calificacionRecuperacion;
    }

    public void setCalificacionRecuperacion(BigDecimal calificacionRecuperacion) {
        this.calificacionRecuperacion = calificacionRecuperacion;
    }
}
