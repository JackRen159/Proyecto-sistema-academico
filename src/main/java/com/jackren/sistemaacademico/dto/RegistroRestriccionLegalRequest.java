package com.jackren.sistemaacademico.dto;

import jakarta.validation.constraints.NotBlank;

public class RegistroRestriccionLegalRequest {

    @NotBlank(message = "El motivo y orden judicial o de Defensoría de la restricción es obligatorio")
    private String motivoBloqueoLegal;

    private String soporteMedidaUrl;

    public RegistroRestriccionLegalRequest() {
    }

    public RegistroRestriccionLegalRequest(String motivoBloqueoLegal, String soporteMedidaUrl) {
        this.motivoBloqueoLegal = motivoBloqueoLegal;
        this.soporteMedidaUrl = soporteMedidaUrl;
    }

    public String getMotivoBloqueoLegal() { return motivoBloqueoLegal; }
    public void setMotivoBloqueoLegal(String motivoBloqueoLegal) { this.motivoBloqueoLegal = motivoBloqueoLegal; }
    public String getSoporteMedidaUrl() { return soporteMedidaUrl; }
    public void setSoporteMedidaUrl(String soporteMedidaUrl) { this.soporteMedidaUrl = soporteMedidaUrl; }
}
