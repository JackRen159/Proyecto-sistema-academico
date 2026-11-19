package com.jackren.sistemaacademico.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class AprobarSieeRequest {

    @NotBlank(message = "El acta de estudio del Consejo Académico es obligatoria")
    private String actaConsejoAcademico;

    @NotBlank(message = "El acta de aprobación y adopción del Consejo Directivo es obligatoria")
    private String actaConsejoDirectivo;

    @NotNull(message = "La fecha de entrada en vigencia es obligatoria")
    private LocalDate fechaInicioVigencia;

    private String soportePdfUrl;

    public AprobarSieeRequest() {
    }

    public AprobarSieeRequest(String actaConsejoAcademico, String actaConsejoDirectivo, LocalDate fechaInicioVigencia) {
        this.actaConsejoAcademico = actaConsejoAcademico;
        this.actaConsejoDirectivo = actaConsejoDirectivo;
        this.fechaInicioVigencia = fechaInicioVigencia;
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
}
