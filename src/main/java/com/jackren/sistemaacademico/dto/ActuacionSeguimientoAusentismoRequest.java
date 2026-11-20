package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.TipoActuacionSeguimiento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class ActuacionSeguimientoAusentismoRequest {

    @NotNull(message = "La matrícula es obligatoria")
    private Long matriculaId;

    @NotNull(message = "El periodo académico es obligatorio")
    private Long periodoAcademicoId;

    @NotNull(message = "El tipo de actuación es obligatorio")
    private TipoActuacionSeguimiento tipoActuacion;

    private LocalDate fechaActuacion;

    @NotBlank(message = "La descripción de la gestión es obligatoria")
    @Size(max = 2000, message = "La descripción no puede superar 2000 caracteres")
    private String descripcionGestion;

    @Size(max = 2000, message = "Los compromisos no pueden superar 2000 caracteres")
    private String compromisosAcordados;

    @Size(max = 255, message = "La URL del soporte no puede superar 255 caracteres")
    private String soporteActaUrl;

    public ActuacionSeguimientoAusentismoRequest() {
    }

    public Long getMatriculaId() {
        return matriculaId;
    }

    public void setMatriculaId(Long matriculaId) {
        this.matriculaId = matriculaId;
    }

    public Long getPeriodoAcademicoId() {
        return periodoAcademicoId;
    }

    public void setPeriodoAcademicoId(Long periodoAcademicoId) {
        this.periodoAcademicoId = periodoAcademicoId;
    }

    public TipoActuacionSeguimiento getTipoActuacion() {
        return tipoActuacion;
    }

    public void setTipoActuacion(TipoActuacionSeguimiento tipoActuacion) {
        this.tipoActuacion = tipoActuacion;
    }

    public LocalDate getFechaActuacion() {
        return fechaActuacion;
    }

    public void setFechaActuacion(LocalDate fechaActuacion) {
        this.fechaActuacion = fechaActuacion;
    }

    public String getDescripcionGestion() {
        return descripcionGestion;
    }

    public void setDescripcionGestion(String descripcionGestion) {
        this.descripcionGestion = descripcionGestion;
    }

    public String getCompromisosAcordados() {
        return compromisosAcordados;
    }

    public void setCompromisosAcordados(String compromisosAcordados) {
        this.compromisosAcordados = compromisosAcordados;
    }

    public String getSoporteActaUrl() {
        return soporteActaUrl;
    }

    public void setSoporteActaUrl(String soporteActaUrl) {
        this.soporteActaUrl = soporteActaUrl;
    }
}
