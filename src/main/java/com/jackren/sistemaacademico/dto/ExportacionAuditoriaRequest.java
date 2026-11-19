package com.jackren.sistemaacademico.dto;

import jakarta.validation.constraints.NotBlank;

public class ExportacionAuditoriaRequest {

    @NotBlank(message = "El numero de oficio o requerimiento oficial es obligatorio")
    private String numeroOficioRequerimiento;

    @NotBlank(message = "El motivo de la exportacion es obligatorio")
    private String motivo;

    @NotBlank(message = "La autoridad solicitante es obligatoria")
    private String autoridadSolicitante;

    private String modulo;
    private String correlationId;

    public ExportacionAuditoriaRequest() {
    }

    public ExportacionAuditoriaRequest(String numeroOficioRequerimiento, String motivo, String autoridadSolicitante) {
        this.numeroOficioRequerimiento = numeroOficioRequerimiento;
        this.motivo = motivo;
        this.autoridadSolicitante = autoridadSolicitante;
    }

    public String getNumeroOficioRequerimiento() {
        return numeroOficioRequerimiento;
    }

    public void setNumeroOficioRequerimiento(String numeroOficioRequerimiento) {
        this.numeroOficioRequerimiento = numeroOficioRequerimiento;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getAutoridadSolicitante() {
        return autoridadSolicitante;
    }

    public void setAutoridadSolicitante(String autoridadSolicitante) {
        this.autoridadSolicitante = autoridadSolicitante;
    }

    public String getModulo() {
        return modulo;
    }

    public void setModulo(String modulo) {
        this.modulo = modulo;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }
}
