package com.jackren.sistemaacademico.dto;

import jakarta.validation.constraints.NotBlank;

public class RadicarSolicitudRectificacionRequest {

    @NotBlank(message = "El JSON de los campos propuestos a modificar es obligatorio")
    private String camposAModificarJson;

    @NotBlank(message = "El motivo de la solicitud de rectificación es obligatorio")
    private String motivoSolicitud;

    private String soporteUrl;

    public RadicarSolicitudRectificacionRequest() {
    }

    public RadicarSolicitudRectificacionRequest(String camposAModificarJson, String motivoSolicitud, String soporteUrl) {
        this.camposAModificarJson = camposAModificarJson;
        this.motivoSolicitud = motivoSolicitud;
        this.soporteUrl = soporteUrl;
    }

    public String getCamposAModificarJson() { return camposAModificarJson; }
    public void setCamposAModificarJson(String camposAModificarJson) { this.camposAModificarJson = camposAModificarJson; }
    public String getMotivoSolicitud() { return motivoSolicitud; }
    public void setMotivoSolicitud(String motivoSolicitud) { this.motivoSolicitud = motivoSolicitud; }
    public String getSoporteUrl() { return soporteUrl; }
    public void setSoporteUrl(String soporteUrl) { this.soporteUrl = soporteUrl; }
}
