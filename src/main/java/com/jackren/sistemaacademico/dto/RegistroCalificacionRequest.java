package com.jackren.sistemaacademico.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class RegistroCalificacionRequest {

    @NotNull(message = "La matrícula del estudiante es obligatoria")
    private Long matriculaId;

    @NotNull(message = "El valor de la calificación es obligatorio")
    private BigDecimal valor;

    @Size(max = 255, message = "Las observaciones no pueden superar 255 caracteres")
    private String observaciones;

    public RegistroCalificacionRequest() {
    }

    public RegistroCalificacionRequest(Long matriculaId, BigDecimal valor, String observaciones) {
        this.matriculaId = matriculaId;
        this.valor = valor;
        this.observaciones = observaciones;
    }

    public Long getMatriculaId() {
        return matriculaId;
    }

    public void setMatriculaId(Long matriculaId) {
        this.matriculaId = matriculaId;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}
