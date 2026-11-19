package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.Rol;

import java.time.LocalDate;

public class UsuarioRolResponse {

    private Long id;
    private Long usuarioId;
    private String username;
    private Rol rol;
    private Boolean activo;
    private Boolean esPrincipal;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Long anioLectivoId;
    private String motivoAsignacion;

    public UsuarioRolResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
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

    public Boolean getEsPrincipal() {
        return esPrincipal;
    }

    public void setEsPrincipal(Boolean esPrincipal) {
        this.esPrincipal = esPrincipal;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public Long getAnioLectivoId() {
        return anioLectivoId;
    }

    public void setAnioLectivoId(Long anioLectivoId) {
        this.anioLectivoId = anioLectivoId;
    }

    public String getMotivoAsignacion() {
        return motivoAsignacion;
    }

    public void setMotivoAsignacion(String motivoAsignacion) {
        this.motivoAsignacion = motivoAsignacion;
    }
}
