package com.jackren.sistemaacademico.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class JustificacionAsistenciaRequest {

    @NotBlank(message = "El motivo de la justificación es obligatorio")
    @Size(max = 255, message = "El motivo no puede superar 255 caracteres")
    private String motivo;

    @Size(max = 50, message = "El número de radicado o soporte no puede superar 50 caracteres")
    private String numeroRadicadoSoporte;

    private LocalDate fechaRadicacion;

    @Size(max = 255, message = "Las observaciones no pueden superar 255 caracteres")
    private String observaciones;

    public JustificacionAsistenciaRequest() {
    }

    public JustificacionAsistenciaRequest(String motivo, String numeroRadicadoSoporte, LocalDate fechaRadicacion, String observaciones) {
        this.motivo = motivo;
        this.numeroRadicadoSoporte = numeroRadicadoSoporte;
        this.fechaRadicacion = fechaRadicacion;
        this.observaciones = observaciones;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getNumeroRadicadoSoporte() {
        return numeroRadicadoSoporte;
    }

    public void setNumeroRadicadoSoporte(String numeroRadicadoSoporte) {
        this.numeroRadicadoSoporte = numeroRadicadoSoporte;
    }

    public LocalDate getFechaRadicacion() {
        return fechaRadicacion;
    }

    public void setFechaRadicacion(LocalDate fechaRadicacion) {
        this.fechaRadicacion = fechaRadicacion;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}
