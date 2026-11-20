package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.TipoJustificacionAsistencia;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class SolicitudExcusaAsistenciaRequest {

    @NotNull(message = "La matrícula es obligatoria")
    private Long matriculaId;

    @NotNull(message = "El tipo de justificación es obligatorio")
    private TipoJustificacionAsistencia tipoJustificacion;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate fechaInicio;

    @NotNull(message = "La fecha de fin es obligatoria")
    private LocalDate fechaFin;

    @NotBlank(message = "La explicación o motivo es obligatorio")
    @Size(max = 2000, message = "El motivo no puede superar 2000 caracteres")
    private String motivoExplicacion;

    @Size(max = 255, message = "La URL del soporte no puede superar 255 caracteres")
    private String soporteUrl;

    private Boolean esDatoSaludSensible = true;

    public SolicitudExcusaAsistenciaRequest() {
    }

    public Long getMatriculaId() {
        return matriculaId;
    }

    public void setMatriculaId(Long matriculaId) {
        this.matriculaId = matriculaId;
    }

    public TipoJustificacionAsistencia getTipoJustificacion() {
        return tipoJustificacion;
    }

    public void setTipoJustificacion(TipoJustificacionAsistencia tipoJustificacion) {
        this.tipoJustificacion = tipoJustificacion;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getMotivoExplicacion() {
        return motivoExplicacion;
    }

    public void setMotivoExplicacion(String motivoExplicacion) {
        this.motivoExplicacion = motivoExplicacion;
    }

    public String getSoporteUrl() {
        return soporteUrl;
    }

    public void setSoporteUrl(String soporteUrl) {
        this.soporteUrl = soporteUrl;
    }

    public Boolean getEsDatoSaludSensible() {
        return esDatoSaludSensible;
    }

    public void setEsDatoSaludSensible(Boolean esDatoSaludSensible) {
        this.esDatoSaludSensible = esDatoSaludSensible;
    }
}
