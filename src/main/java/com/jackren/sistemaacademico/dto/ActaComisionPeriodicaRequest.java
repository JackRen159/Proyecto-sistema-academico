package com.jackren.sistemaacademico.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public class ActaComisionPeriodicaRequest {

    @NotNull(message = "El periodo académico es obligatorio")
    private Long periodoAcademicoId;

    @NotNull(message = "El grado es obligatorio")
    private Long gradoId;

    @NotBlank(message = "El número de acta es obligatorio")
    @Size(max = 50, message = "El número de acta no puede superar 50 caracteres")
    private String numeroActa;

    private LocalDate fechaSesion;

    @NotEmpty(message = "Debe registrar al menos un integrante de la comisión")
    private List<String> integrantesNominativos;

    private String conclusionesGenerales;

    private String compromisosPedagogicosJson;

    public ActaComisionPeriodicaRequest() {
    }

    public Long getPeriodoAcademicoId() {
        return periodoAcademicoId;
    }

    public void setPeriodoAcademicoId(Long periodoAcademicoId) {
        this.periodoAcademicoId = periodoAcademicoId;
    }

    public Long getGradoId() {
        return gradoId;
    }

    public void setGradoId(Long gradoId) {
        this.gradoId = gradoId;
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

    public List<String> getIntegrantesNominativos() {
        return integrantesNominativos;
    }

    public void setIntegrantesNominativos(List<String> integrantesNominativos) {
        this.integrantesNominativos = integrantesNominativos;
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
}
