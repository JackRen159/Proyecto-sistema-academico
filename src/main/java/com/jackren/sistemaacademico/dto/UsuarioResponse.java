package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.Rol;

public class UsuarioResponse {

    private Long id;
    private String username;
    private Rol rol;
    private Boolean activo;

    public UsuarioResponse() {
    }

    public UsuarioResponse(Long id, String username, Rol rol, Boolean activo) {
        this.id = id;
        this.username = username;
        this.rol = rol;
        this.activo = activo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}

