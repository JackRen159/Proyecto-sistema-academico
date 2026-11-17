package com.jackren.sistemaacademico.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AnulacionEmisionRequest {

    @NotBlank(message = "El motivo de anulación es obligatorio")
    @Size(max = 255, message = "El motivo de anulación no puede superar 255 caracteres")
    private String motivoAnulacion;

    public AnulacionEmisionRequest() {
    }

    public AnulacionEmisionRequest(String motivoAnulacion) {
        this.motivoAnulacion = motivoAnulacion;
    }

    public String getMotivoAnulacion() {
        return motivoAnulacion;
    }

    public void setMotivoAnulacion(String motivoAnulacion) {
        this.motivoAnulacion = motivoAnulacion;
    }
}
