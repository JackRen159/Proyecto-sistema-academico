package com.jackren.sistemaacademico.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ReaperturaPeriodoRequest {

    @NotNull(message = "El periodo académico es obligatorio")
    private Long periodoId;

    @NotBlank(message = "El motivo de la reapertura excepcional es obligatorio")
    @Size(max = 2000, message = "El motivo no puede superar 2000 caracteres")
    private String motivoReapertura;

    @Size(max = 255, message = "La resolución rectoral no puede superar 255 caracteres")
    private String resolucionRectoralUrl;

    public ReaperturaPeriodoRequest() {
    }

    public Long getPeriodoId() {
        return periodoId;
    }

    public void setPeriodoId(Long periodoId) {
        this.periodoId = periodoId;
    }

    public String getMotivoReapertura() {
        return motivoReapertura;
    }

    public void setMotivoReapertura(String motivoReapertura) {
        this.motivoReapertura = motivoReapertura;
    }

    public String getResolucionRectoralUrl() {
        return resolucionRectoralUrl;
    }

    public void setResolucionRectoralUrl(String resolucionRectoralUrl) {
        this.resolucionRectoralUrl = resolucionRectoralUrl;
    }
}
