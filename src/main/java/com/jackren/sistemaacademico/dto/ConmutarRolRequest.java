package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.Rol;
import jakarta.validation.constraints.NotNull;

public class ConmutarRolRequest {

    @NotNull(message = "El rol solicitado es obligatorio")
    private Rol rol;

    public ConmutarRolRequest() {
    }

    public ConmutarRolRequest(Rol rol) {
        this.rol = rol;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }
}
