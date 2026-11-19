package com.jackren.sistemaacademico.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class CrearSieeBorradorRequest {

    @NotBlank(message = "El nombre de la versión es obligatorio")
    private String nombreVersion;

    private Long anioLectivoVigenciaId;

    @NotNull(message = "La escala mínima es obligatoria")
    @DecimalMin(value = "0.00", message = "La escala mínima no puede ser negativa")
    private BigDecimal escalaMinima = new BigDecimal("1.00");

    @NotNull(message = "La escala máxima es obligatoria")
    @DecimalMax(value = "100.00", message = "La escala máxima supera el límite institucional")
    private BigDecimal escalaMaxima = new BigDecimal("5.00");

    @NotNull(message = "La escala aprobatoria es obligatoria")
    private BigDecimal escalaAprobatoria = new BigDecimal("3.00");

    private Integer decimales = 2;
    private String modoRedondeo = "HALF_UP";

    @NotBlank(message = "Los rangos de equivalencia institucional y nacional en formato JSON son obligatorios")
    private String rangosEquivalenciaJson;

    private String ponderacionPeriodosJson;
    private String criteriosPromocionJson;
    private BigDecimal umbralAsistenciaReprobacion;
    private Integer plazoReclamacionesDias = 3;

    public CrearSieeBorradorRequest() {
    }

    public String getNombreVersion() {
        return nombreVersion;
    }

    public void setNombreVersion(String nombreVersion) {
        this.nombreVersion = nombreVersion;
    }

    public Long getAnioLectivoVigenciaId() {
        return anioLectivoVigenciaId;
    }

    public void setAnioLectivoVigenciaId(Long anioLectivoVigenciaId) {
        this.anioLectivoVigenciaId = anioLectivoVigenciaId;
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
}
