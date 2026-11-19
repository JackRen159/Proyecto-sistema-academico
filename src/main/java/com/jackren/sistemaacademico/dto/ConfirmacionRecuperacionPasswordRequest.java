package com.jackren.sistemaacademico.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ConfirmacionRecuperacionPasswordRequest {

    @NotBlank(message = "El token de recuperación es obligatorio")
    private String token;

    @NotBlank(message = "La nueva contraseña es obligatoria")
    @Size(min = 8, message = "La nueva contraseña debe tener al menos 8 caracteres")
    private String passwordNuevo;

    @NotBlank(message = "La confirmación de contraseña es obligatoria")
    private String confirmacionPassword;

    public ConfirmacionRecuperacionPasswordRequest() {
    }

    public ConfirmacionRecuperacionPasswordRequest(String token, String passwordNuevo, String confirmacionPassword) {
        this.token = token;
        this.passwordNuevo = passwordNuevo;
        this.confirmacionPassword = confirmacionPassword;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPasswordNuevo() {
        return passwordNuevo;
    }

    public void setPasswordNuevo(String passwordNuevo) {
        this.passwordNuevo = passwordNuevo;
    }

    public String getConfirmacionPassword() {
        return confirmacionPassword;
    }

    public void setConfirmacionPassword(String confirmacionPassword) {
        this.confirmacionPassword = confirmacionPassword;
    }
}
