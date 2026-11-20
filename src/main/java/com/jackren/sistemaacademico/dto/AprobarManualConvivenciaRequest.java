package com.jackren.sistemaacademico.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class AprobarManualConvivenciaRequest {

    @NotBlank(message = "El acta del Comité Escolar de Convivencia es obligatoria")
    private String actaComiteConvivencia;

    @NotBlank(message = "El acta de adopción del Consejo Directivo es obligatoria")
    private String actaConsejoDirectivo;

    @NotNull(message = "La fecha de inicio de vigencia es obligatoria")
    private LocalDate fechaInicioVigencia;

    private String soportePdfUrl;
    private String constanciaSocializacion;
    private LocalDate fechaSocializacion;

    public AprobarManualConvivenciaRequest() {
    }

    public AprobarManualConvivenciaRequest(String actaComiteConvivencia, String actaConsejoDirectivo, LocalDate fechaInicioVigencia) {
        this.actaComiteConvivencia = actaComiteConvivencia;
        this.actaConsejoDirectivo = actaConsejoDirectivo;
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

    public LocalDate getFechaInicioVigencia() {
        return fechaInicioVigencia;
    }

    public void setFechaInicioVigencia(LocalDate fechaInicioVigencia) {
        this.fechaInicioVigencia = fechaInicioVigencia;
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
}
