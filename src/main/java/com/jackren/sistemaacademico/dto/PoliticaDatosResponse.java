package com.jackren.sistemaacademico.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class PoliticaDatosResponse {

    private Long id;
    private Integer numeroVersion;
    private String titulo;
    private String contenidoPolitica;
    private String avisoPrivacidad;
    private LocalDate fechaVigencia;
    private Boolean esVigente;
    private Boolean cambiosSustanciales;
    private String actaAprobacion;
    private String soporteDocumentoUrl;
    private LocalDateTime fechaCreacion;

    public PoliticaDatosResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNumeroVersion() {
        return numeroVersion;
    }

    public void setNumeroVersion(Integer numeroVersion) {
        this.numeroVersion = numeroVersion;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getContenidoPolitica() {
        return contenidoPolitica;
    }

    public void setContenidoPolitica(String contenidoPolitica) {
        this.contenidoPolitica = contenidoPolitica;
    }

    public String getAvisoPrivacidad() {
        return avisoPrivacidad;
    }

    public void setAvisoPrivacidad(String avisoPrivacidad) {
        this.avisoPrivacidad = avisoPrivacidad;
    }

    public LocalDate getFechaVigencia() {
        return fechaVigencia;
    }

    public void setFechaVigencia(LocalDate fechaVigencia) {
        this.fechaVigencia = fechaVigencia;
    }

    public Boolean getEsVigente() {
        return esVigente;
    }

    public void setEsVigente(Boolean esVigente) {
        this.esVigente = esVigente;
    }

    public Boolean getCambiosSustanciales() {
        return cambiosSustanciales;
    }

    public void setCambiosSustanciales(Boolean cambiosSustanciales) {
        this.cambiosSustanciales = cambiosSustanciales;
    }

    public String getActaAprobacion() {
        return actaAprobacion;
    }

    public void setActaAprobacion(String actaAprobacion) {
        this.actaAprobacion = actaAprobacion;
    }

    public String getSoporteDocumentoUrl() {
        return soporteDocumentoUrl;
    }

    public void setSoporteDocumentoUrl(String soporteDocumentoUrl) {
        this.soporteDocumentoUrl = soporteDocumentoUrl;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
