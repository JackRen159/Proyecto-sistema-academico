package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.TipoEspacioFisico;

public class DisponibilidadAulaResponse {

    private Long aulaId;
    private String sede;
    private String codigo;
    private String nombre;
    private TipoEspacioFisico tipoEspacio;
    private Integer capacidadMaxima;
    private Boolean disponible;
    private String grupoOcupante;
    private String asignaturaOcupante;
    private String docenteOcupante;

    public DisponibilidadAulaResponse() {
    }

    public Long getAulaId() {
        return aulaId;
    }

    public void setAulaId(Long aulaId) {
        this.aulaId = aulaId;
    }

    public String getSede() {
        return sede;
    }

    public void setSede(String sede) {
        this.sede = sede;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public TipoEspacioFisico getTipoEspacio() {
        return tipoEspacio;
    }

    public void setTipoEspacio(TipoEspacioFisico tipoEspacio) {
        this.tipoEspacio = tipoEspacio;
    }

    public Integer getCapacidadMaxima() {
        return capacidadMaxima;
    }

    public void setCapacidadMaxima(Integer capacidadMaxima) {
        this.capacidadMaxima = capacidadMaxima;
    }

    public Boolean getDisponible() {
        return disponible;
    }

    public void setDisponible(Boolean disponible) {
        this.disponible = disponible;
    }

    public String getGrupoOcupante() {
        return grupoOcupante;
    }

    public void setGrupoOcupante(String grupoOcupante) {
        this.grupoOcupante = grupoOcupante;
    }

    public String getAsignaturaOcupante() {
        return asignaturaOcupante;
    }

    public void setAsignaturaOcupante(String asignaturaOcupante) {
        this.asignaturaOcupante = asignaturaOcupante;
    }

    public String getDocenteOcupante() {
        return docenteOcupante;
    }

    public void setDocenteOcupante(String docenteOcupante) {
        this.docenteOcupante = docenteOcupante;
    }
}
