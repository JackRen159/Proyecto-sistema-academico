package com.jackren.sistemaacademico.dto;

import jakarta.validation.constraints.NotBlank;

public class SolicitudRecuperacionPasswordRequest {

    @NotBlank(message = "El identificador de acceso o correo institucional es obligatorio")
    private String identificador;

    public SolicitudRecuperacionPasswordRequest() {
    }

    public SolicitudRecuperacionPasswordRequest(String identificador) {
        this.identificador = identificador;
    }

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }
}
