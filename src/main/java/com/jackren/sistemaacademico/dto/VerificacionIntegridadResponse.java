package com.jackren.sistemaacademico.dto;

import java.time.LocalDateTime;

public class VerificacionIntegridadResponse {

    private String estado; // CADENA_INTEGRA_VERIFICADA | ALERTA_CRITICA_INTEGRIDAD_AUDITORIA
    private long totalEventosVerificados;
    private Long idEventoDiscrepancia;
    private String detalle;
    private LocalDateTime fechaVerificacion;

    public VerificacionIntegridadResponse() {
    }

    public VerificacionIntegridadResponse(String estado, long totalEventosVerificados, Long idEventoDiscrepancia, String detalle) {
        this.estado = estado;
        this.totalEventosVerificados = totalEventosVerificados;
        this.idEventoDiscrepancia = idEventoDiscrepancia;
        this.detalle = detalle;
        this.fechaVerificacion = LocalDateTime.now();
    }

    public static VerificacionIntegridadResponse integra(long total) {
        return new VerificacionIntegridadResponse(
                "CADENA_INTEGRA_VERIFICADA",
                total,
                null,
                "Se verifico matematicamente la cadena completa de hashes SHA-256 sin detectar alteraciones ni omisiones (RN-DOC-07)."
        );
    }

    public static VerificacionIntegridadResponse discrepancia(long total, Long idEvento, String detalle) {
        return new VerificacionIntegridadResponse(
                "ALERTA_CRITICA_INTEGRIDAD_AUDITORIA",
                total,
                idEvento,
                detalle
        );
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public long getTotalEventosVerificados() {
        return totalEventosVerificados;
    }

    public void setTotalEventosVerificados(long totalEventosVerificados) {
        this.totalEventosVerificados = totalEventosVerificados;
    }

    public Long getIdEventoDiscrepancia() {
        return idEventoDiscrepancia;
    }

    public void setIdEventoDiscrepancia(Long idEventoDiscrepancia) {
        this.idEventoDiscrepancia = idEventoDiscrepancia;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public LocalDateTime getFechaVerificacion() {
        return fechaVerificacion;
    }

    public void setFechaVerificacion(LocalDateTime fechaVerificacion) {
        this.fechaVerificacion = fechaVerificacion;
    }
}
