package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.TipoOrganoColegiado;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class AsignarOrganoColegiadoRequest {

    @NotNull(message = "El tipo de organo colegiado es obligatorio")
    private TipoOrganoColegiado tipoOrgano;

    @NotBlank(message = "El acta de designacion es obligatoria")
    private String actaDesignacion;

    @NotNull(message = "El anio lectivo es obligatorio")
    private Long anioLectivoId;

    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String motivo;

    public AsignarOrganoColegiadoRequest() {
    }

    public AsignarOrganoColegiadoRequest(TipoOrganoColegiado tipoOrgano, String actaDesignacion, Long anioLectivoId) {
        this.tipoOrgano = tipoOrgano;
        this.actaDesignacion = actaDesignacion;
        this.anioLectivoId = anioLectivoId;
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

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}
