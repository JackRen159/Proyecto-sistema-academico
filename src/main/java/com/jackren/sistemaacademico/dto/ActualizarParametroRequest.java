package com.jackren.sistemaacademico.dto;

import jakarta.validation.constraints.NotBlank;

public class ActualizarParametroRequest {

    @NotBlank(message = "El valor del parámetro es obligatorio")
    private String valor;

    private String motivo;

    public ActualizarParametroRequest() {
    }

    public ActualizarParametroRequest(String valor, String motivo) {
        this.valor = valor;
        this.motivo = motivo;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}
