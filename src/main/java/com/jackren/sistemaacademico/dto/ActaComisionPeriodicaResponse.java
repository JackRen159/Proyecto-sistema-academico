package com.jackren.sistemaacademico.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ActaComisionPeriodicaResponse {

    private Long id;
    private Long periodoId;
    private String periodoNombre;
    private Long gradoId;
    private String gradoNombre;
    private String numeroActa;
    private LocalDate fechaSesion;
    private String integrantesNominativosJson;
    private String conclusionesGenerales;
    private String compromisosPedagogicosJson;
    private String usuarioSecretarioNombre;
    private LocalDateTime fechaRegistro;

    public ActaComisionPeriodicaResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPeriodoId() {
        return periodoId;
    }

    public void setPeriodoId(Long periodoId) {
        this.periodoId = periodoId;
    }

    public String getPeriodoNombre() {
        return periodoNombre;
    }

    public void setPeriodoNombre(String periodoNombre) {
        this.periodoNombre = periodoNombre;
    }

    public Long getGradoId() {
        return gradoId;
    }

    public void setGradoId(Long gradoId) {
        this.gradoId = gradoId;
    }

    public String getGradoNombre() {
        return gradoNombre;
    }

    public void setGradoNombre(String gradoNombre) {
        this.gradoNombre = gradoNombre;
    }

    public String getNumeroActa() {
        return numeroActa;
    }

    public void setNumeroActa(String numeroActa) {
        this.numeroActa = numeroActa;
    }

    public LocalDate getFechaSesion() {
        return fechaSesion;
    }

    public void setFechaSesion(LocalDate fechaSesion) {
        this.fechaSesion = fechaSesion;
    }

    public String getIntegrantesNominativosJson() {
        return integrantesNominativosJson;
    }

    public void setIntegrantesNominativosJson(String integrantesNominativosJson) {
        this.integrantesNominativosJson = integrantesNominativosJson;
    }

    public String getConclusionesGenerales() {
        return conclusionesGenerales;
    }

    public void setConclusionesGenerales(String conclusionesGenerales) {
        this.conclusionesGenerales = conclusionesGenerales;
    }

    public String getCompromisosPedagogicosJson() {
        return compromisosPedagogicosJson;
    }

    public void setCompromisosPedagogicosJson(String compromisosPedagogicosJson) {
        this.compromisosPedagogicosJson = compromisosPedagogicosJson;
    }

    public String getUsuarioSecretarioNombre() {
        return usuarioSecretarioNombre;
    }

    public void setUsuarioSecretarioNombre(String usuarioSecretarioNombre) {
        this.usuarioSecretarioNombre = usuarioSecretarioNombre;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}
