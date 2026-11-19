package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.ResultadoAuditoria;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class RegistroAuditoriaEventoRequest {

    private Long usuarioId;

    @NotBlank(message = "El username es obligatorio")
    private String username;

    @NotBlank(message = "El rol activo es obligatorio")
    private String rolActivo;

    @NotBlank(message = "El modulo es obligatorio")
    private String modulo;

    @NotBlank(message = "La accion es obligatoria")
    private String accion;

    @NotBlank(message = "La entidad es obligatoria")
    private String entidad;

    private String entidadId;
    private String valorAnterior;
    private String valorNuevo;
    private String motivo;

    @NotNull(message = "El resultado es obligatorio")
    private ResultadoAuditoria resultado = ResultadoAuditoria.EXITO;

    @NotBlank(message = "El correlationId es obligatorio")
    private String correlationId;

    private String direccionIp;
    private String userAgent;

    public RegistroAuditoriaEventoRequest() {
    }

    public RegistroAuditoriaEventoRequest(String username, String rolActivo, String modulo, String accion, String entidad, String entidadId, String motivo, ResultadoAuditoria resultado, String correlationId) {
        this.username = username;
        this.rolActivo = rolActivo;
        this.modulo = modulo;
        this.accion = accion;
        this.entidad = entidad;
        this.entidadId = entidadId;
        this.motivo = motivo;
        this.resultado = resultado;
        this.correlationId = correlationId;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRolActivo() {
        return rolActivo;
    }

    public void setRolActivo(String rolActivo) {
        this.rolActivo = rolActivo;
    }

    public String getModulo() {
        return modulo;
    }

    public void setModulo(String modulo) {
        this.modulo = modulo;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public String getEntidad() {
        return entidad;
    }

    public void setEntidad(String entidad) {
        this.entidad = entidad;
    }

    public String getEntidadId() {
        return entidadId;
    }

    public void setEntidadId(String entidadId) {
        this.entidadId = entidadId;
    }

    public String getValorAnterior() {
        return valorAnterior;
    }

    public void setValorAnterior(String valorAnterior) {
        this.valorAnterior = valorAnterior;
    }

    public String getValorNuevo() {
        return valorNuevo;
    }

    public void setValorNuevo(String valorNuevo) {
        this.valorNuevo = valorNuevo;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public ResultadoAuditoria getResultado() {
        return resultado;
    }

    public void setResultado(ResultadoAuditoria resultado) {
        this.resultado = resultado;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public String getDireccionIp() {
        return direccionIp;
    }

    public void setDireccionIp(String direccionIp) {
        this.direccionIp = direccionIp;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
}
