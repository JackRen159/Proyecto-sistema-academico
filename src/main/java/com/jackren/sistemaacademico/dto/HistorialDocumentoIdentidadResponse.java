package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.TipoDocumento;

import java.time.LocalDateTime;

public class HistorialDocumentoIdentidadResponse {

    private Long id;
    private Long estudianteId;
    private TipoDocumento tipoDocumentoAnterior;
    private String numeroDocumentoAnterior;
    private TipoDocumento tipoDocumentoNuevo;
    private String numeroDocumentoNuevo;
    private LocalDateTime fechaCambio;
    private String motivoCambio;
    private String soporteDocumentoUrl;
    private String usuarioAutorizador;

    public HistorialDocumentoIdentidadResponse() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getEstudianteId() { return estudianteId; }
    public void setEstudianteId(Long estudianteId) { this.estudianteId = estudianteId; }
    public TipoDocumento getTipoDocumentoAnterior() { return tipoDocumentoAnterior; }
    public void setTipoDocumentoAnterior(TipoDocumento tipoDocumentoAnterior) { this.tipoDocumentoAnterior = tipoDocumentoAnterior; }
    public String getNumeroDocumentoAnterior() { return numeroDocumentoAnterior; }
    public void setNumeroDocumentoAnterior(String numeroDocumentoAnterior) { this.numeroDocumentoAnterior = numeroDocumentoAnterior; }
    public TipoDocumento getTipoDocumentoNuevo() { return tipoDocumentoNuevo; }
    public void setTipoDocumentoNuevo(TipoDocumento tipoDocumentoNuevo) { this.tipoDocumentoNuevo = tipoDocumentoNuevo; }
    public String getNumeroDocumentoNuevo() { return numeroDocumentoNuevo; }
    public void setNumeroDocumentoNuevo(String numeroDocumentoNuevo) { this.numeroDocumentoNuevo = numeroDocumentoNuevo; }
    public LocalDateTime getFechaCambio() { return fechaCambio; }
    public void setFechaCambio(LocalDateTime fechaCambio) { this.fechaCambio = fechaCambio; }
    public String getMotivoCambio() { return motivoCambio; }
    public void setMotivoCambio(String motivoCambio) { this.motivoCambio = motivoCambio; }
    public String getSoporteDocumentoUrl() { return soporteDocumentoUrl; }
    public void setSoporteDocumentoUrl(String soporteDocumentoUrl) { this.soporteDocumentoUrl = soporteDocumentoUrl; }
    public String getUsuarioAutorizador() { return usuarioAutorizador; }
    public void setUsuarioAutorizador(String usuarioAutorizador) { this.usuarioAutorizador = usuarioAutorizador; }
}
