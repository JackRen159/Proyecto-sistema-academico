package com.jackren.sistemaacademico.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class TrasladoGrupoRequest {

    @NotNull(message = "El grupo destino es obligatorio")
    private Long nuevoGrupoId;

    @NotBlank(message = "El motivo del traslado es obligatorio")
    @Size(max = 255, message = "El motivo no puede superar 255 caracteres")
    private String motivo;

    public TrasladoGrupoRequest() {
    }

    public Long getNuevoGrupoId() {
        return nuevoGrupoId;
    }

    public void setNuevoGrupoId(Long nuevoGrupoId) {
        this.nuevoGrupoId = nuevoGrupoId;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}
