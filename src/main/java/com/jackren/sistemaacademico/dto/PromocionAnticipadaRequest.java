package com.jackren.sistemaacademico.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class PromocionAnticipadaRequest {

    @NotNull(message = "La matrícula es obligatoria")
    private Long matriculaId;

    @NotBlank(message = "El acta de concepto del Consejo Académico es obligatoria")
    @Size(max = 100, message = "El acta de Consejo Académico no puede superar 100 caracteres")
    private String actaConsejoAcademico;

    @NotBlank(message = "El acta de aprobación del Consejo Directivo es obligatoria")
    @Size(max = 100, message = "El acta de Consejo Directivo no puede superar 100 caracteres")
    private String actaConsejoDirectivo;

    @NotBlank(message = "El soporte de consentimiento escrito del acudiente es obligatorio")
    @Size(max = 255, message = "La URL de consentimiento no puede superar 255 caracteres")
    private String consentimientoAcudienteUrl;

    @NotNull(message = "El grado destino es obligatorio")
    private Long gradoDestinoId;

    @Size(max = 2000, message = "El motivo no puede superar 2000 caracteres")
    private String motivo;

    public PromocionAnticipadaRequest() {
    }

    public Long getMatriculaId() {
        return matriculaId;
    }

    public void setMatriculaId(Long matriculaId) {
        this.matriculaId = matriculaId;
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

    public String getConsentimientoAcudienteUrl() {
        return consentimientoAcudienteUrl;
    }

    public void setConsentimientoAcudienteUrl(String consentimientoAcudienteUrl) {
        this.consentimientoAcudienteUrl = consentimientoAcudienteUrl;
    }

    public Long getGradoDestinoId() {
        return gradoDestinoId;
    }

    public void setGradoDestinoId(Long gradoDestinoId) {
        this.gradoDestinoId = gradoDestinoId;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}
