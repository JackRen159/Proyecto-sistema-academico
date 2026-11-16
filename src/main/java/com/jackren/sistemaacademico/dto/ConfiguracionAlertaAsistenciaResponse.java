package com.jackren.sistemaacademico.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ConfiguracionAlertaAsistenciaResponse {

    private Long id;
    private Long anioLectivoId;
    private Integer anioLectivo;
    private BigDecimal porcentajeAlertaAmarilla;
    private BigDecimal porcentajeAlertaRoja;
    private String descripcion;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    public ConfiguracionAlertaAsistenciaResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAnioLectivoId() {
        return anioLectivoId;
    }

    public void setAnioLectivoId(Long anioLectivoId) {
        this.anioLectivoId = anioLectivoId;
    }

    public Integer getAnioLectivo() {
        return anioLectivo;
    }

    public void setAnioLectivo(Integer anioLectivo) {
        this.anioLectivo = anioLectivo;
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

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }
}
