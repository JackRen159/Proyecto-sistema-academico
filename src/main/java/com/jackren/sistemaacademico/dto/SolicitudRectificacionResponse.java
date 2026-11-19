package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.EstadoSolicitudRectificacion;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class SolicitudRectificacionResponse {

    private Long id;
    private String numeroRadicado;
    private Long estudianteId;
    private String estudianteDocumento;
    private String estudianteNombreCompleto;
    private String solicitanteUsername;
    private String camposAModificarJson;
    private String motivoSolicitud;
    private String soporteUrl;
    private LocalDateTime fechaRadicacion;
    private LocalDate fechaLimiteLegal;
    private EstadoSolicitudRectificacion estado;
    private String respuestaMotivada;
    private LocalDateTime fechaResolucion;
    private String resolutorUsername;

    public SolicitudRectificacionResponse() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNumeroRadicado() { return numeroRadicado; }
    public void setNumeroRadicado(String numeroRadicado) { this.numeroRadicado = numeroRadicado; }
    public Long getEstudianteId() { return estudianteId; }
    public void setEstudianteId(Long estudianteId) { this.estudianteId = estudianteId; }
    public String getEstudianteDocumento() { return estudianteDocumento; }
    public void setEstudianteDocumento(String estudianteDocumento) { this.estudianteDocumento = estudianteDocumento; }
    public String getEstudianteNombreCompleto() { return estudianteNombreCompleto; }
    public void setEstudianteNombreCompleto(String estudianteNombreCompleto) { this.estudianteNombreCompleto = estudianteNombreCompleto; }
    public String getSolicitanteUsername() { return solicitanteUsername; }
    public void setSolicitanteUsername(String solicitanteUsername) { this.solicitanteUsername = solicitanteUsername; }
    public String getCamposAModificarJson() { return camposAModificarJson; }
    public void setCamposAModificarJson(String camposAModificarJson) { this.camposAModificarJson = camposAModificarJson; }
    public String getMotivoSolicitud() { return motivoSolicitud; }
    public void setMotivoSolicitud(String motivoSolicitud) { this.motivoSolicitud = motivoSolicitud; }
    public String getSoporteUrl() { return soporteUrl; }
    public void setSoporteUrl(String soporteUrl) { this.soporteUrl = soporteUrl; }
    public LocalDateTime getFechaRadicacion() { return fechaRadicacion; }
    public void setFechaRadicacion(LocalDateTime fechaRadicacion) { this.fechaRadicacion = fechaRadicacion; }
    public LocalDate getFechaLimiteLegal() { return fechaLimiteLegal; }
    public void setFechaLimiteLegal(LocalDate fechaLimiteLegal) { this.fechaLimiteLegal = fechaLimiteLegal; }
    public EstadoSolicitudRectificacion getEstado() { return estado; }
    public void setEstado(EstadoSolicitudRectificacion estado) { this.estado = estado; }
    public String getRespuestaMotivada() { return respuestaMotivada; }
    public void setRespuestaMotivada(String respuestaMotivada) { this.respuestaMotivada = respuestaMotivada; }
    public LocalDateTime getFechaResolucion() { return fechaResolucion; }
    public void setFechaResolucion(LocalDateTime fechaResolucion) { this.fechaResolucion = fechaResolucion; }
    public String getResolutorUsername() { return resolutorUsername; }
    public void setResolutorUsername(String resolutorUsername) { this.resolutorUsername = resolutorUsername; }
}
