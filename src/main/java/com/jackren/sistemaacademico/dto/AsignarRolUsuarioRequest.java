package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.Rol;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class AsignarRolUsuarioRequest {

    @NotNull(message = "El rol es obligatorio")
    private Rol rol;

    private Long anioLectivoId;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String motivoAsignacion;
    private Boolean esPrincipal = false;

    public AsignarRolUsuarioRequest() {
    }

    public AsignarRolUsuarioRequest(Rol rol, Long anioLectivoId, String motivoAsignacion) {
        this.rol = rol;
        this.anioLectivoId = anioLectivoId;
        this.motivoAsignacion = motivoAsignacion;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public Long getAnioLectivoId() {
        return anioLectivoId;
    }

    public void setAnioLectivoId(Long anioLectivoId) {
        this.anioLectivoId = anioLectivoId;
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

    public String getMotivoAsignacion() {
        return motivoAsignacion;
    }

    public void setMotivoAsignacion(String motivoAsignacion) {
        this.motivoAsignacion = motivoAsignacion;
    }

    public Boolean getEsPrincipal() {
        return esPrincipal;
    }

    public void setEsPrincipal(Boolean esPrincipal) {
        this.esPrincipal = esPrincipal;
    }
}
