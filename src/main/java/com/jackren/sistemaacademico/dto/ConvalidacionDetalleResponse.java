package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.DesempenoNacional;

import java.math.BigDecimal;

public class ConvalidacionDetalleResponse {

    private Long id;
    private Long asignaturaId;
    private String asignaturaNombre;
    private BigDecimal notaConvalidada;
    private DesempenoNacional desempeno;
    private String observaciones;

    public ConvalidacionDetalleResponse() {
    }

    public ConvalidacionDetalleResponse(Long id, Long asignaturaId, String asignaturaNombre, BigDecimal notaConvalidada, DesempenoNacional desempeno, String observaciones) {
        this.id = id;
        this.asignaturaId = asignaturaId;
        this.asignaturaNombre = asignaturaNombre;
        this.notaConvalidada = notaConvalidada;
        this.desempeno = desempeno;
        this.observaciones = observaciones;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAsignaturaId() {
        return asignaturaId;
    }

    public void setAsignaturaId(Long asignaturaId) {
        this.asignaturaId = asignaturaId;
    }

    public String getAsignaturaNombre() {
        return asignaturaNombre;
    }

    public void setAsignaturaNombre(String asignaturaNombre) {
        this.asignaturaNombre = asignaturaNombre;
    }

    public BigDecimal getNotaConvalidada() {
        return notaConvalidada;
    }

    public void setNotaConvalidada(BigDecimal notaConvalidada) {
        this.notaConvalidada = notaConvalidada;
    }

    public DesempenoNacional getDesempeno() {
        return desempeno;
    }

    public void setDesempeno(DesempenoNacional desempeno) {
        this.desempeno = desempeno;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}
