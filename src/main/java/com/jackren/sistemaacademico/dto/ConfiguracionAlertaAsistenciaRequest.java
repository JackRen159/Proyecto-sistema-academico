package com.jackren.sistemaacademico.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public class ConfiguracionAlertaAsistenciaRequest {

    @NotNull(message = "El año lectivo es obligatorio")
    private Long anioLectivoId;

    @NotNull(message = "El porcentaje para alerta amarilla es obligatorio")
    @DecimalMin(value = "1.00", message = "El porcentaje debe ser al menos 1%")
    @DecimalMax(value = "99.00", message = "El porcentaje de alerta amarilla debe ser menor al 100%")
    private BigDecimal porcentajeAlertaAmarilla = new BigDecimal("15.00");

    @NotNull(message = "El porcentaje para alerta roja es obligatorio")
    @DecimalMin(value = "1.00", message = "El porcentaje debe ser al menos 1%")
    @DecimalMax(value = "100.00", message = "El porcentaje de alerta roja no puede superar 100%")
    private BigDecimal porcentajeAlertaRoja = new BigDecimal("25.00");

    @Size(max = 255, message = "La descripción no puede superar 255 caracteres")
    private String descripcion;

    public ConfiguracionAlertaAsistenciaRequest() {
    }

    public Long getAnioLectivoId() {
        return anioLectivoId;
    }

    public void setAnioLectivoId(Long anioLectivoId) {
        this.anioLectivoId = anioLectivoId;
    }

    public BigDecimal getPorcentajeAlertaAmarilla() {
        return porcentajeAlertaAmarilla;
    }

    public void setPorcentajeAlertaAmarilla(BigDecimal porcentajeAlertaAmarilla) {
        this.porcentajeAlertaAmarilla = porcentajeAlertaAmarilla;
    }

    public BigDecimal getPorcentajeAlertaRoja() {
        return porcentajeAlertaRoja;
    }

    public void setPorcentajeAlertaRoja(BigDecimal porcentajeAlertaRoja) {
        this.porcentajeAlertaRoja = porcentajeAlertaRoja;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
