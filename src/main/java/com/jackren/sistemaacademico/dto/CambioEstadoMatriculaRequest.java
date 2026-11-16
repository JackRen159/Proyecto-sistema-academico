package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.EstadoMatricula;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CambioEstadoMatriculaRequest {

    @NotNull(message = "El nuevo estado es obligatorio")
    private EstadoMatricula nuevoEstado;

    @Size(max = 255, message = "El motivo no puede superar 255 caracteres")
    private String motivo;

    public CambioEstadoMatriculaRequest() {
    }

    public EstadoMatricula getNuevoEstado() {
        return nuevoEstado;
    }

    public void setNuevoEstado(EstadoMatricula nuevoEstado) {
        this.nuevoEstado = nuevoEstado;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}
