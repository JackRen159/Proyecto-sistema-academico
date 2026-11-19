package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.Rol;
import com.jackren.sistemaacademico.entity.TipoPersonaUsuario;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CrearUsuarioRequest {

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(max = 50, message = "El username no puede superar los 50 caracteres")
    private String username;

    private String password;

    @NotNull(message = "El rol principal es obligatorio")
    private Rol rol;

    private TipoPersonaUsuario personaTipo;
    private Long personaId;
    private Boolean debeCambiarPassword = true;

    public CrearUsuarioRequest() {
    }

    public CrearUsuarioRequest(String username, String password, Rol rol, TipoPersonaUsuario personaTipo, Long personaId) {
        this.username = username;
        this.password = password;
        this.rol = rol;
        this.personaTipo = personaTipo;
        this.personaId = personaId;
        this.debeCambiarPassword = true;
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

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public TipoPersonaUsuario getPersonaTipo() {
        return personaTipo;
    }

    public void setPersonaTipo(TipoPersonaUsuario personaTipo) {
        this.personaTipo = personaTipo;
    }

    public Long getPersonaId() {
        return personaId;
    }

    public void setPersonaId(Long personaId) {
        this.personaId = personaId;
    }

    public Boolean getDebeCambiarPassword() {
        return debeCambiarPassword;
    }

    public void setDebeCambiarPassword(Boolean debeCambiarPassword) {
        this.debeCambiarPassword = debeCambiarPassword;
    }
}
