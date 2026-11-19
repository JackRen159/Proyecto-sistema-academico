package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.TipoOrganoColegiado;

import java.time.LocalDate;

public class OrganoColegiadoMiembroResponse {

    private Long id;
    private Long usuarioId;
    private String username;
    private TipoOrganoColegiado tipoOrgano;
    private String actaDesignacion;
    private Long anioLectivoId;
    private String anioLectivoNombre;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Boolean activo;
    private String motivo;

    public OrganoColegiadoMiembroResponse() {
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

    public TipoOrganoColegiado getTipoOrgano() {
        return tipoOrgano;
    }

    public void setTipoOrgano(TipoOrganoColegiado tipoOrgano) {
        this.tipoOrgano = tipoOrgano;
    }

    public String getActaDesignacion() {
        return actaDesignacion;
    }

    public void setActaDesignacion(String actaDesignacion) {
        this.actaDesignacion = actaDesignacion;
    }

    public Long getAnioLectivoId() {
        return anioLectivoId;
    }

    public void setAnioLectivoId(Long anioLectivoId) {
        this.anioLectivoId = anioLectivoId;
    }

    public String getAnioLectivoNombre() {
        return anioLectivoNombre;
    }

    public void setAnioLectivoNombre(String anioLectivoNombre) {
        this.anioLectivoNombre = anioLectivoNombre;
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

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}
