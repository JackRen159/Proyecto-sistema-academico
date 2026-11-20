package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.EstadoVersionConfiguracion;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ConfiguracionManualConvivenciaResponse {

    private Long id;
    private Integer numeroVersion;
    private String nombreVersion;
    private EstadoVersionConfiguracion estado;
    private Long anioLectivoVigenciaId;
    private String anioLectivoNombre;
    private LocalDate fechaInicioVigencia;
    private String actaComiteConvivencia;
    private String actaConsejoDirectivo;
    private String situacionesTipo1Json;
    private String situacionesTipo2Json;
    private String situacionesTipo3Json;
    private String plazosDebidoProcesoJson;
    private String soportePdfUrl;
    private String constanciaSocializacion;
    private LocalDate fechaSocializacion;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaAprobacion;

    public ConfiguracionManualConvivenciaResponse() {
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

    public String getActaComiteConvivencia() {
        return actaComiteConvivencia;
    }

    public void setActaComiteConvivencia(String actaComiteConvivencia) {
        this.actaComiteConvivencia = actaComiteConvivencia;
    }

    public String getActaConsejoDirectivo() {
        return actaConsejoDirectivo;
    }

    public void setActaConsejoDirectivo(String actaConsejoDirectivo) {
        this.actaConsejoDirectivo = actaConsejoDirectivo;
    }

    public String getSituacionesTipo1Json() {
        return situacionesTipo1Json;
    }

    public void setSituacionesTipo1Json(String situacionesTipo1Json) {
        this.situacionesTipo1Json = situacionesTipo1Json;
    }

    public String getSituacionesTipo2Json() {
        return situacionesTipo2Json;
    }

    public void setSituacionesTipo2Json(String situacionesTipo2Json) {
        this.situacionesTipo2Json = situacionesTipo2Json;
    }

    public String getSituacionesTipo3Json() {
        return situacionesTipo3Json;
    }

    public void setSituacionesTipo3Json(String situacionesTipo3Json) {
        this.situacionesTipo3Json = situacionesTipo3Json;
    }

    public String getPlazosDebidoProcesoJson() {
        return plazosDebidoProcesoJson;
    }

    public void setPlazosDebidoProcesoJson(String plazosDebidoProcesoJson) {
        this.plazosDebidoProcesoJson = plazosDebidoProcesoJson;
    }

    public String getSoportePdfUrl() {
        return soportePdfUrl;
    }

    public void setSoportePdfUrl(String soportePdfUrl) {
        this.soportePdfUrl = soportePdfUrl;
    }

    public String getConstanciaSocializacion() {
        return constanciaSocializacion;
    }

    public void setConstanciaSocializacion(String constanciaSocializacion) {
        this.constanciaSocializacion = constanciaSocializacion;
    }

    public LocalDate getFechaSocializacion() {
        return fechaSocializacion;
    }

    public void setFechaSocializacion(LocalDate fechaSocializacion) {
        this.fechaSocializacion = fechaSocializacion;
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
