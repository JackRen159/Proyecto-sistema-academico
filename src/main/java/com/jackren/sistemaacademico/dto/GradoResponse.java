package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.NivelEducativo;

public class GradoResponse {

    private Long id;
    private String codigo;
    private String nombre;
    private NivelEducativo nivel;
    private Integer ordenJerarquico;
    private Boolean activo;

    public GradoResponse(Long id, String codigo, String nombre, NivelEducativo nivel, Integer ordenJerarquico, Boolean activo) {
        this.id = id;
        this.codigo = codigo;
        this.nombre = nombre;
        this.nivel = nivel;
        this.ordenJerarquico = ordenJerarquico;
        this.activo = activo;
    }

    public Long getId() {
        return id;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public NivelEducativo getNivel() {
        return nivel;
    }

    public Integer getOrdenJerarquico() {
        return ordenJerarquico;
    }

    public Boolean getActivo() {
        return activo;
    }
}
