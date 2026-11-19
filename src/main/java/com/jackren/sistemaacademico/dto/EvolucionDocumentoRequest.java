package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.TipoDocumento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class EvolucionDocumentoRequest {

    @NotNull(message = "El nuevo tipo de documento es obligatorio")
    private TipoDocumento tipoDocumentoNuevo;

    @NotBlank(message = "El nuevo número de documento es obligatorio")
    @Size(max = 20)
    private String numeroDocumentoNuevo;

    @NotBlank(message = "El motivo de la evolución o cambio legal es obligatorio")
    private String motivoCambio;

    private String soporteDocumentoUrl;

    public EvolucionDocumentoRequest() {
    }

    public EvolucionDocumentoRequest(TipoDocumento tipoDocumentoNuevo, String numeroDocumentoNuevo, String motivoCambio) {
        this.tipoDocumentoNuevo = tipoDocumentoNuevo;
        this.numeroDocumentoNuevo = numeroDocumentoNuevo;
        this.motivoCambio = motivoCambio;
    }

    public TipoDocumento getTipoDocumentoNuevo() { return tipoDocumentoNuevo; }
    public void setTipoDocumentoNuevo(TipoDocumento tipoDocumentoNuevo) { this.tipoDocumentoNuevo = tipoDocumentoNuevo; }
    public String getNumeroDocumentoNuevo() { return numeroDocumentoNuevo; }
    public void setNumeroDocumentoNuevo(String numeroDocumentoNuevo) { this.numeroDocumentoNuevo = numeroDocumentoNuevo; }
    public String getMotivoCambio() { return motivoCambio; }
    public void setMotivoCambio(String motivoCambio) { this.motivoCambio = motivoCambio; }
    public String getSoporteDocumentoUrl() { return soporteDocumentoUrl; }
    public void setSoporteDocumentoUrl(String soporteDocumentoUrl) { this.soporteDocumentoUrl = soporteDocumentoUrl; }
}
