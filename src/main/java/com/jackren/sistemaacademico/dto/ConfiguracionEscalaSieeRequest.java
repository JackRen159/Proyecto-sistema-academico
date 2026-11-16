package com.jackren.sistemaacademico.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public class ConfiguracionEscalaSieeRequest {

    @NotNull(message = "El año lectivo es obligatorio")
    private Long anioLectivoId;

    @NotNull(message = "La nota mínima es obligatoria")
    @DecimalMin(value = "0.00", message = "La nota mínima no puede ser negativa")
    private BigDecimal notaMinima = new BigDecimal("1.00");

    @NotNull(message = "La nota máxima es obligatoria")
    @DecimalMin(value = "1.00", message = "La nota máxima debe ser al menos 1.00")
    private BigDecimal notaMaxima = new BigDecimal("5.00");

    @NotNull(message = "La nota aprobatoria es obligatoria")
    private BigDecimal notaAprobatoria = new BigDecimal("3.00");

    @NotNull(message = "El umbral de desempeño Básico es obligatorio")
    private BigDecimal escalaBasicoMin = new BigDecimal("3.00");

    @NotNull(message = "El umbral de desempeño Alto es obligatorio")
    private BigDecimal escalaAltoMin = new BigDecimal("4.00");

    @NotNull(message = "El umbral de desempeño Superior es obligatorio")
    private BigDecimal escalaSuperiorMin = new BigDecimal("4.60");

    @Min(value = 0, message = "Los decimales de redondeo deben ser entre 0 y 2")
    @Max(value = 2, message = "Los decimales de redondeo deben ser entre 0 y 2")
    private Integer decimalesRedondeo = 1;

    @Size(max = 255, message = "La descripción no puede superar 255 caracteres")
    private String descripcion;

    public ConfiguracionEscalaSieeRequest() {
    }

    public Long getAnioLectivoId() {
        return anioLectivoId;
    }

    public void setAnioLectivoId(Long anioLectivoId) {
        this.anioLectivoId = anioLectivoId;
    }

    public BigDecimal getNotaMinima() {
        return notaMinima;
    }

    public void setNotaMinima(BigDecimal notaMinima) {
        this.notaMinima = notaMinima;
    }

    public BigDecimal getNotaMaxima() {
        return notaMaxima;
    }

    public void setNotaMaxima(BigDecimal notaMaxima) {
        this.notaMaxima = notaMaxima;
    }

    public BigDecimal getNotaAprobatoria() {
        return notaAprobatoria;
    }

    public void setNotaAprobatoria(BigDecimal notaAprobatoria) {
        this.notaAprobatoria = notaAprobatoria;
    }

    public BigDecimal getEscalaBasicoMin() {
        return escalaBasicoMin;
    }

    public void setEscalaBasicoMin(BigDecimal escalaBasicoMin) {
        this.escalaBasicoMin = escalaBasicoMin;
    }

    public BigDecimal getEscalaAltoMin() {
        return escalaAltoMin;
    }

    public void setEscalaAltoMin(BigDecimal escalaAltoMin) {
        this.escalaAltoMin = escalaAltoMin;
    }

    public BigDecimal getEscalaSuperiorMin() {
        return escalaSuperiorMin;
    }

    public void setEscalaSuperiorMin(BigDecimal escalaSuperiorMin) {
        this.escalaSuperiorMin = escalaSuperiorMin;
    }

    public Integer getDecimalesRedondeo() {
        return decimalesRedondeo;
    }

    public void setDecimalesRedondeo(Integer decimalesRedondeo) {
        this.decimalesRedondeo = decimalesRedondeo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
