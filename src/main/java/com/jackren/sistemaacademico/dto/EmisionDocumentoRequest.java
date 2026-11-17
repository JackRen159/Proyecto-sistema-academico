package com.jackren.sistemaacademico.dto;

import jakarta.validation.constraints.Size;

public class EmisionDocumentoRequest {

    @Size(max = 200, message = "El destinatario no puede superar 200 caracteres")
    private String destinatario;

    @Size(max = 255, message = "El motivo no puede superar 255 caracteres")
    private String motivo;

    public EmisionDocumentoRequest() {
    }

    public EmisionDocumentoRequest(String destinatario, String motivo) {
        this.destinatario = destinatario;
        this.motivo = motivo;
    }

    public String getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}
