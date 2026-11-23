package com.jackren.sistemaacademico.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ActaNivelacionRequest {

    private Long calificacionPeriodoId;

    private Long matriculaId;

    private Long asignaturaId;

    private Long periodoId;

    private LocalDate fechaSustentacion;

    private Boolean asistencia = true;

    @JsonAlias({"notaNivelacion"})
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

    public Long getMatriculaId() {
        return matriculaId;
    }

    public void setMatriculaId(Long matriculaId) {
        this.matriculaId = matriculaId;
    }

    public Long getAsignaturaId() {
        return asignaturaId;
    }

    public void setAsignaturaId(Long asignaturaId) {
        this.asignaturaId = asignaturaId;
    }

    public Long getPeriodoId() {
        return periodoId;
    }

    public void setPeriodoId(Long periodoId) {
        this.periodoId = periodoId;
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
