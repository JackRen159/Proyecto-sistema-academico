package com.jackren.sistemaacademico.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class CrearPoliticaDatosRequest {

    @NotBlank(message = "El título de la política es obligatorio")
    private String titulo;

    @NotBlank(message = "El contenido íntegro de la política es obligatorio")
    private String contenidoPolitica;

    @NotBlank(message = "El aviso de privacidad es obligatorio")
    private String avisoPrivacidad;

    @NotNull(message = "La fecha de vigencia es obligatoria")
    private LocalDate fechaVigencia;

    private Boolean esVigente = true;
    private Boolean cambiosSustanciales = false;
    private String actaAprobacion;
    private String soporteDocumentoUrl;

    public CrearPoliticaDatosRequest() {
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
}
