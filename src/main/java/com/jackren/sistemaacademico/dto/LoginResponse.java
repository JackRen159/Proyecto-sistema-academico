package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.Rol;

import java.util.List;

public class LoginResponse {

    private String token;
    private String tokenType;
    private String username;
    private Rol rol;
    private Long expiresIn;
    private Boolean debeCambiarPassword = false;
    private Boolean politicaAceptada = true;
    private List<Rol> rolesDisponibles;

    public LoginResponse() {
    }

    public LoginResponse(String token, String tokenType, String username, Rol rol, Long expiresIn) {
        this.token = token;
        this.tokenType = tokenType;
        this.username = username;
        this.rol = rol;
        this.expiresIn = expiresIn;
        this.debeCambiarPassword = false;
        this.politicaAceptada = true;
    }

    public LoginResponse(String token, String tokenType, String username, Rol rol, Long expiresIn,
                         Boolean debeCambiarPassword, Boolean politicaAceptada, List<Rol> rolesDisponibles) {
        this.token = token;
        this.tokenType = tokenType;
        this.username = username;
        this.rol = rol;
        this.expiresIn = expiresIn;
        this.debeCambiarPassword = debeCambiarPassword != null ? debeCambiarPassword : false;
        this.politicaAceptada = politicaAceptada != null ? politicaAceptada : true;
        this.rolesDisponibles = rolesDisponibles;
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

    public Boolean getDebeCambiarPassword() {
        return debeCambiarPassword;
    }

    public void setDebeCambiarPassword(Boolean debeCambiarPassword) {
        this.debeCambiarPassword = debeCambiarPassword;
    }

    public Boolean getPoliticaAceptada() {
        return politicaAceptada;
    }

    public void setPoliticaAceptada(Boolean politicaAceptada) {
        this.politicaAceptada = politicaAceptada;
    }

    public List<Rol> getRolesDisponibles() {
        return rolesDisponibles;
    }

    public void setRolesDisponibles(List<Rol> rolesDisponibles) {
        this.rolesDisponibles = rolesDisponibles;
    }
}
