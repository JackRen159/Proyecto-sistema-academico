package com.jackren.sistemaacademico.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ActaNivelacionRequest {

    @NotNull(message = "La calificación de periodo es obligatoria")
    private Long calificacionPeriodoId;

    @NotNull(message = "La fecha de sustentación es obligatoria")
    private LocalDate fechaSustentacion;

    private Boolean asistencia = true;

    private BigDecimal notaObtenida;

    @Size(max = 1000, message = "La estrategia pedagógica no puede superar 1000 caracteres")
    private String estrategiaPedagogica;

    @Size(max = 255, message = "Las evidencias de soporte no pueden superar 255 caracteres")
    private String evidenciasSoporte;

    @Size(max = 255, message = "Las observaciones no pueden superar 255 caracteres")
    private String observaciones;

    public ActaNivelacionRequest() {
    }

    public Long getCalificacionPeriodoId() {
        return calificacionPeriodoId;
    }

    public void setCalificacionPeriodoId(Long calificacionPeriodoId) {
        this.calificacionPeriodoId = calificacionPeriodoId;
    }

    public LocalDate getFechaSustentacion() {
        return fechaSustentacion;
    }

    public void setFechaSustentacion(LocalDate fechaSustentacion) {
        this.fechaSustentacion = fechaSustentacion;
    }

    public Boolean getAsistencia() {
        return asistencia;
    }

    public void setAsistencia(Boolean asistencia) {
        this.asistencia = asistencia;
    }

    public BigDecimal getNotaObtenida() {
        return notaObtenida;
    }

    public void setNotaObtenida(BigDecimal notaObtenida) {
        this.notaObtenida = notaObtenida;
    }

    public String getEstrategiaPedagogica() {
        return estrategiaPedagogica;
    }

    public void setEstrategiaPedagogica(String estrategiaPedagogica) {
        this.estrategiaPedagogica = estrategiaPedagogica;
    }

    public String getEvidenciasSoporte() {
        return evidenciasSoporte;
    }

    public void setEvidenciasSoporte(String evidenciasSoporte) {
        this.evidenciasSoporte = evidenciasSoporte;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}
