package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.EstadoUsuario;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ActualizarEstadoUsuarioRequest {

    @NotNull(message = "El nuevo estado es obligatorio")
    private EstadoUsuario estado;

    @NotBlank(message = "El motivo de la modificacion de estado es obligatorio")
    private String motivo;

    public ActualizarEstadoUsuarioRequest() {
    }

    public ActualizarEstadoUsuarioRequest(EstadoUsuario estado, String motivo) {
        this.estado = estado;
        this.motivo = motivo;
    }

    public EstadoUsuario getEstado() {
        return estado;
    }

    public void setEstado(EstadoUsuario estado) {
        this.estado = estado;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}
