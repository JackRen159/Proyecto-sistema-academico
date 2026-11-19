package com.jackren.sistemaacademico.dto;

import java.time.LocalDateTime;
import java.util.List;

public class PaqueteAuditoriaExportadoResponse {

    private String numeroOficioRequerimiento;
    private String autoridadSolicitante;
    private String motivo;
    private int totalRegistrosExportados;
    private String manifiestoSha256;
    private LocalDateTime fechaGeneracion;
    private List<AuditoriaEventoResponse> eventos;

    public PaqueteAuditoriaExportadoResponse() {
    }

    public PaqueteAuditoriaExportadoResponse(String numeroOficioRequerimiento,
                                             String autoridadSolicitante,
                                             String motivo,
                                             int totalRegistrosExportados,
                                             String manifiestoSha256,
                                             List<AuditoriaEventoResponse> eventos) {
        this.numeroOficioRequerimiento = numeroOficioRequerimiento;
        this.autoridadSolicitante = autoridadSolicitante;
        this.motivo = motivo;
        this.totalRegistrosExportados = totalRegistrosExportados;
        this.manifiestoSha256 = manifiestoSha256;
        this.eventos = eventos;
        this.fechaGeneracion = LocalDateTime.now();
    }

    public String getNumeroOficioRequerimiento() {
        return numeroOficioRequerimiento;
    }

    public void setNumeroOficioRequerimiento(String numeroOficioRequerimiento) {
        this.numeroOficioRequerimiento = numeroOficioRequerimiento;
    }

    public String getAutoridadSolicitante() {
        return autoridadSolicitante;
    }

    public void setAutoridadSolicitante(String autoridadSolicitante) {
        this.autoridadSolicitante = autoridadSolicitante;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public int getTotalRegistrosExportados() {
        return totalRegistrosExportados;
    }

    public void setTotalRegistrosExportados(int totalRegistrosExportados) {
        this.totalRegistrosExportados = totalRegistrosExportados;
    }

    public String getManifiestoSha256() {
        return manifiestoSha256;
    }

    public void setManifiestoSha256(String manifiestoSha256) {
        this.manifiestoSha256 = manifiestoSha256;
    }

    public LocalDateTime getFechaGeneracion() {
        return fechaGeneracion;
    }

    public void setFechaGeneracion(LocalDateTime fechaGeneracion) {
        this.fechaGeneracion = fechaGeneracion;
    }

    public List<AuditoriaEventoResponse> getEventos() {
        return eventos;
    }

    public void setEventos(List<AuditoriaEventoResponse> eventos) {
        this.eventos = eventos;
    }
}
