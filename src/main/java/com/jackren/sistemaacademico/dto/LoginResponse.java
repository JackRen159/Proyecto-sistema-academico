package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.Rol;

public class LoginResponse {

    private String token;
    private String tokenType;
    private String username;
    private Rol rol;
    private Long expiresIn;

    public LoginResponse() {
    }

    public LoginResponse(String token, String tokenType, String username, Rol rol, Long expiresIn) {
        this.token = token;
        this.tokenType = tokenType;
        this.username = username;
        this.rol = rol;
        this.expiresIn = expiresIn;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }
}

