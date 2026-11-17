package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.DesempenoNacional;

import java.math.BigDecimal;

public class AsignaturaCertificadoDTO {

    private String nombreAsignatura;
    private Integer intensidadHoraria;
    private BigDecimal calificacionFinal;
    private DesempenoNacional desempenoNacional;
    private String estado;

    public AsignaturaCertificadoDTO() {
    }

    public AsignaturaCertificadoDTO(String nombreAsignatura,
                                   Integer intensidadHoraria,
                                   BigDecimal calificacionFinal,
                                   DesempenoNacional desempenoNacional,
                                   String estado) {
        this.nombreAsignatura = nombreAsignatura;
        this.intensidadHoraria = intensidadHoraria;
        this.calificacionFinal = calificacionFinal;
        this.desempenoNacional = desempenoNacional;
        this.estado = estado;
    }

    public String getNombreAsignatura() {
        return nombreAsignatura;
    }

    public void setNombreAsignatura(String nombreAsignatura) {
        this.nombreAsignatura = nombreAsignatura;
    }

    public Integer getIntensidadHoraria() {
        return intensidadHoraria;
    }

    public void setIntensidadHoraria(Integer intensidadHoraria) {
        this.intensidadHoraria = intensidadHoraria;
    }

    public BigDecimal getCalificacionFinal() {
        return calificacionFinal;
    }

    public void setCalificacionFinal(BigDecimal calificacionFinal) {
        this.calificacionFinal = calificacionFinal;
    }

    public DesempenoNacional getDesempenoNacional() {
        return desempenoNacional;
    }

    public void setDesempenoNacional(DesempenoNacional desempenoNacional) {
        this.desempenoNacional = desempenoNacional;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
