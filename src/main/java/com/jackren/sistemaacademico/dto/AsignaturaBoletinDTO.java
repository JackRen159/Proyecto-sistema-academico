package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.DesempenoNacional;

import java.math.BigDecimal;

public class AsignaturaBoletinDTO {

    private Long asignaturaId;
    private String nombreAsignatura;
    private Integer intensidadHoraria;
    private String docente;
    private BigDecimal calificacionDefinitiva;
    private DesempenoNacional desempenoNacional;
    private Long fallasJustificadas;
    private Long fallasInjustificadas;
    private Long retardos;
    private String observaciones;

    public AsignaturaBoletinDTO() {
    }

    public AsignaturaBoletinDTO(Long asignaturaId,
                                String nombreAsignatura,
                                Integer intensidadHoraria,
                                String docente,
                                BigDecimal calificacionDefinitiva,
                                DesempenoNacional desempenoNacional,
                                Long fallasJustificadas,
                                Long fallasInjustificadas,
                                Long retardos,
                                String observaciones) {
        this.asignaturaId = asignaturaId;
        this.nombreAsignatura = nombreAsignatura;
        this.intensidadHoraria = intensidadHoraria;
        this.docente = docente;
        this.calificacionDefinitiva = calificacionDefinitiva;
        this.desempenoNacional = desempenoNacional;
        this.fallasJustificadas = fallasJustificadas != null ? fallasJustificadas : 0L;
        this.fallasInjustificadas = fallasInjustificadas != null ? fallasInjustificadas : 0L;
        this.retardos = retardos != null ? retardos : 0L;
        this.observaciones = observaciones;
    }

    public Long getAsignaturaId() {
        return asignaturaId;
    }

    public void setAsignaturaId(Long asignaturaId) {
        this.asignaturaId = asignaturaId;
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

    public String getDocente() {
        return docente;
    }

    public void setDocente(String docente) {
        this.docente = docente;
    }

    public BigDecimal getCalificacionDefinitiva() {
        return calificacionDefinitiva;
    }

    public void setCalificacionDefinitiva(BigDecimal calificacionDefinitiva) {
        this.calificacionDefinitiva = calificacionDefinitiva;
    }

    public DesempenoNacional getDesempenoNacional() {
        return desempenoNacional;
    }

    public void setDesempenoNacional(DesempenoNacional desempenoNacional) {
        this.desempenoNacional = desempenoNacional;
    }

    public Long getFallasJustificadas() {
        return fallasJustificadas;
    }

    public void setFallasJustificadas(Long fallasJustificadas) {
        this.fallasJustificadas = fallasJustificadas;
    }

    public Long getFallasInjustificadas() {
        return fallasInjustificadas;
    }

    public void setFallasInjustificadas(Long fallasInjustificadas) {
        this.fallasInjustificadas = fallasInjustificadas;
    }

    public Long getRetardos() {
        return retardos;
    }

    public void setRetardos(Long retardos) {
        this.retardos = retardos;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}
