package com.jackren.sistemaacademico.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "politica_tratamiento_datos",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_politica_datos_version", columnNames = {"numero_version"})
    }
)
public class PoliticaTratamientoDatos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_version", nullable = false)
    private Integer numeroVersion;

    @Column(nullable = false, length = 200)
    private String titulo;

    @Column(name = "contenido_politica", nullable = false, columnDefinition = "TEXT")
    private String contenidoPolitica;

    @Column(name = "aviso_privacidad", nullable = false, columnDefinition = "TEXT")
    private String avisoPrivacidad;

    @Column(name = "fecha_vigencia", nullable = false)
    private LocalDate fechaVigencia = LocalDate.now();

    @Column(name = "es_vigente", nullable = false)
    private Boolean esVigente = false;

    @Column(name = "cambios_sustanciales", nullable = false)
    private Boolean cambiosSustanciales = false;

    @Column(name = "acta_aprobacion", length = 100)
    private String actaAprobacion;

    @Column(name = "soporte_documento_url", length = 255)
    private String soporteDocumentoUrl;

    @CreationTimestamp
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    public PoliticaTratamientoDatos() {
    }

    public PoliticaTratamientoDatos(Integer numeroVersion, String titulo, String contenidoPolitica, String avisoPrivacidad, Boolean esVigente) {
        this.numeroVersion = numeroVersion;
        this.titulo = titulo;
        this.contenidoPolitica = contenidoPolitica;
        this.avisoPrivacidad = avisoPrivacidad;
        this.esVigente = esVigente;
        this.fechaVigencia = LocalDate.now();
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
