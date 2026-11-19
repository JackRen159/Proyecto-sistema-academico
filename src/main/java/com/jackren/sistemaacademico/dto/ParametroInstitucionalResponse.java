package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.TipoDatoParametro;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ParametroInstitucionalResponse {

    private Long id;
    private String clave;
    private String valor;
    private TipoDatoParametro tipoDato;
    private String descripcion;
    private String categoria;
    private BigDecimal limiteMinimoNumerico;
    private BigDecimal limiteMaximoNumerico;
    private Boolean editablePorUsuario;
    private LocalDateTime fechaActualizacion;

    public ParametroInstitucionalResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public TipoDatoParametro getTipoDato() {
        return tipoDato;
    }

    public void setTipoDato(TipoDatoParametro tipoDato) {
        this.tipoDato = tipoDato;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public BigDecimal getLimiteMinimoNumerico() {
        return limiteMinimoNumerico;
    }

    public void setLimiteMinimoNumerico(BigDecimal limiteMinimoNumerico) {
        this.limiteMinimoNumerico = limiteMinimoNumerico;
    }

    public BigDecimal getLimiteMaximoNumerico() {
        return limiteMaximoNumerico;
    }

    public void setLimiteMaximoNumerico(BigDecimal limiteMaximoNumerico) {
        this.limiteMaximoNumerico = limiteMaximoNumerico;
    }

    public Boolean getEditablePorUsuario() {
        return editablePorUsuario;
    }

    public void setEditablePorUsuario(Boolean editablePorUsuario) {
        this.editablePorUsuario = editablePorUsuario;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }
}
