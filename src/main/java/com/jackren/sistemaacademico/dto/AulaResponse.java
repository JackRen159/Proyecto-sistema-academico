package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.EstadoAula;
import com.jackren.sistemaacademico.entity.TipoEspacioFisico;

public class AulaResponse {

    private Long id;
    private String sede;
    private String codigo;
    private String nombre;
    private TipoEspacioFisico tipoEspacio;
    private Integer capacidadMaxima;
    private Boolean esMultigrupo;
    private EstadoAula estado;
    private String observaciones;

    public AulaResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Boolean getEsMultigrupo() {
        return esMultigrupo;
    }

    public void setEsMultigrupo(Boolean esMultigrupo) {
        this.esMultigrupo = esMultigrupo;
    }

    public EstadoAula getEstado() {
        return estado;
    }

    public void setEstado(EstadoAula estado) {
        this.estado = estado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}
