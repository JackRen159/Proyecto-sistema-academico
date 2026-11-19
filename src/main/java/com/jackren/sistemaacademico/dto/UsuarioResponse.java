package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.EstadoUsuario;
import com.jackren.sistemaacademico.entity.Rol;

import java.util.List;

public class UsuarioResponse {

    private Long id;
    private String username;
    private Rol rol;
    private Boolean activo;
    private EstadoUsuario estado;
    private Boolean debeCambiarPassword;
    private List<Rol> roles;

    public UsuarioResponse() {
    }

    public UsuarioResponse(Long id, String username, Rol rol, Boolean activo) {
        this.id = id;
        this.username = username;
        this.rol = rol;
        this.activo = activo;
        this.estado = Boolean.TRUE.equals(activo) ? EstadoUsuario.ACTIVA : EstadoUsuario.INACTIVA;
        this.debeCambiarPassword = false;
    }

    public UsuarioResponse(Long id, String username, Rol rol, Boolean activo, EstadoUsuario estado, Boolean debeCambiarPassword, List<Rol> roles) {
        this.id = id;
        this.username = username;
        this.rol = rol;
        this.activo = activo;
        this.estado = estado;
        this.debeCambiarPassword = debeCambiarPassword;
        this.roles = roles;
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

    public EstadoUsuario getEstado() {
        return estado;
    }

    public void setEstado(EstadoUsuario estado) {
        this.estado = estado;
    }

    public Boolean getDebeCambiarPassword() {
        return debeCambiarPassword;
    }

    public void setDebeCambiarPassword(Boolean debeCambiarPassword) {
        this.debeCambiarPassword = debeCambiarPassword;
    }

    public List<Rol> getRoles() {
        return roles;
    }

    public void setRoles(List<Rol> roles) {
        this.roles = roles;
    }
}
