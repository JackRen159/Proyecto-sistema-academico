package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.EstadoVersionConfiguracion;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ConfiguracionSieeResponse {

    private Long id;
    private Integer numeroVersion;
    private String nombreVersion;
    private EstadoVersionConfiguracion estado;
    private Long anioLectivoVigenciaId;
    private String anioLectivoNombre;
    private LocalDate fechaInicioVigencia;
    private BigDecimal escalaMinima;
    private BigDecimal escalaMaxima;
    private BigDecimal escalaAprobatoria;
    private Integer decimales;
    private String modoRedondeo;
    private String rangosEquivalenciaJson;
    private String ponderacionPeriodosJson;
    private String criteriosPromocionJson;
    private BigDecimal umbralAsistenciaReprobacion;
    private Integer plazoReclamacionesDias;
    private String actaConsejoAcademico;
    private String actaConsejoDirectivo;
    private String soportePdfUrl;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaAprobacion;

    public ConfiguracionSieeResponse() {
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

    public String getNombreVersion() {
        return nombreVersion;
    }

    public void setNombreVersion(String nombreVersion) {
        this.nombreVersion = nombreVersion;
    }

    public EstadoVersionConfiguracion getEstado() {
        return estado;
    }

    public void setEstado(EstadoVersionConfiguracion estado) {
        this.estado = estado;
    }

    public Long getAnioLectivoVigenciaId() {
        return anioLectivoVigenciaId;
    }

    public void setAnioLectivoVigenciaId(Long anioLectivoVigenciaId) {
        this.anioLectivoVigenciaId = anioLectivoVigenciaId;
    }

    public String getAnioLectivoNombre() {
        return anioLectivoNombre;
    }

    public void setAnioLectivoNombre(String anioLectivoNombre) {
        this.anioLectivoNombre = anioLectivoNombre;
    }

    public LocalDate getFechaInicioVigencia() {
        return fechaInicioVigencia;
    }

    public void setFechaInicioVigencia(LocalDate fechaInicioVigencia) {
        this.fechaInicioVigencia = fechaInicioVigencia;
    }

    public BigDecimal getEscalaMinima() {
        return escalaMinima;
    }

    public void setEscalaMinima(BigDecimal escalaMinima) {
        this.escalaMinima = escalaMinima;
    }

    public BigDecimal getEscalaMaxima() {
        return escalaMaxima;
    }

    public void setEscalaMaxima(BigDecimal escalaMaxima) {
        this.escalaMaxima = escalaMaxima;
    }

    public BigDecimal getEscalaAprobatoria() {
        return escalaAprobatoria;
    }

    public void setEscalaAprobatoria(BigDecimal escalaAprobatoria) {
        this.escalaAprobatoria = escalaAprobatoria;
    }

    public Integer getDecimales() {
        return decimales;
    }

    public void setDecimales(Integer decimales) {
        this.decimales = decimales;
    }

    public String getModoRedondeo() {
        return modoRedondeo;
    }

    public void setModoRedondeo(String modoRedondeo) {
        this.modoRedondeo = modoRedondeo;
    }

    public String getRangosEquivalenciaJson() {
        return rangosEquivalenciaJson;
    }

    public void setRangosEquivalenciaJson(String rangosEquivalenciaJson) {
        this.rangosEquivalenciaJson = rangosEquivalenciaJson;
    }

    public String getPonderacionPeriodosJson() {
        return ponderacionPeriodosJson;
    }

    public void setPonderacionPeriodosJson(String ponderacionPeriodosJson) {
        this.ponderacionPeriodosJson = ponderacionPeriodosJson;
    }

    public String getCriteriosPromocionJson() {
        return criteriosPromocionJson;
    }

    public void setCriteriosPromocionJson(String criteriosPromocionJson) {
        this.criteriosPromocionJson = criteriosPromocionJson;
    }

    public BigDecimal getUmbralAsistenciaReprobacion() {
        return umbralAsistenciaReprobacion;
    }

    public void setUmbralAsistenciaReprobacion(BigDecimal umbralAsistenciaReprobacion) {
        this.umbralAsistenciaReprobacion = umbralAsistenciaReprobacion;
    }

    public Integer getPlazoReclamacionesDias() {
        return plazoReclamacionesDias;
    }

    public void setPlazoReclamacionesDias(Integer plazoReclamacionesDias) {
        this.plazoReclamacionesDias = plazoReclamacionesDias;
    }

    public String getActaConsejoAcademico() {
        return actaConsejoAcademico;
    }

    public void setActaConsejoAcademico(String actaConsejoAcademico) {
        this.actaConsejoAcademico = actaConsejoAcademico;
    }

    public String getActaConsejoDirectivo() {
        return actaConsejoDirectivo;
    }

    public void setActaConsejoDirectivo(String actaConsejoDirectivo) {
        this.actaConsejoDirectivo = actaConsejoDirectivo;
    }

    public String getSoportePdfUrl() {
        return soportePdfUrl;
    }

    public void setSoportePdfUrl(String soportePdfUrl) {
        this.soportePdfUrl = soportePdfUrl;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaAprobacion() {
        return fechaAprobacion;
    }

    public void setFechaAprobacion(LocalDateTime fechaAprobacion) {
        this.fechaAprobacion = fechaAprobacion;
    }
}
