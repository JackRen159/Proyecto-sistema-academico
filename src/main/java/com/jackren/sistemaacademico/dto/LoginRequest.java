package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.Rol;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class LoginRequest {

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(max = 50, message = "El nombre de usuario no puede superar los 50 caracteres")
    private String username;

    @NotBlank(message = "La contrasena es obligatoria")
    private String password;

    private Rol rolSolicitado;

    public LoginRequest() {
    }

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public LoginRequest(String username, String password, Rol rolSolicitado) {
        this.username = username;
        this.password = password;
        this.rolSolicitado = rolSolicitado;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Rol getRolSolicitado() {
        return rolSolicitado;
    }

    public void setRolSolicitado(Rol rolSolicitado) {
        this.rolSolicitado = rolSolicitado;
    }
}
