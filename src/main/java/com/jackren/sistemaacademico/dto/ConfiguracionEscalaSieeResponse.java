package com.jackren.sistemaacademico.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ConfiguracionEscalaSieeResponse {

    private Long id;
    private Long anioLectivoId;
    private Integer anioLectivo;
    private BigDecimal notaMinima;
    private BigDecimal notaMaxima;
    private BigDecimal notaAprobatoria;
    private BigDecimal escalaBasicoMin;
    private BigDecimal escalaAltoMin;
    private BigDecimal escalaSuperiorMin;
    private Integer decimalesRedondeo;
    private String descripcion;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    public ConfiguracionEscalaSieeResponse() {
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
