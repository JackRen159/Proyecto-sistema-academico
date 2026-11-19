package com.jackren.sistemaacademico.dto;

import java.time.LocalDateTime;

public class RestablecimientoPresencialResponse {

    private Long usuarioId;
    private String username;
    private String passwordTemporal;
    private LocalDateTime fechaGeneracion;
    private String mensaje;

    public RestablecimientoPresencialResponse() {
    }

    public RestablecimientoPresencialResponse(Long usuarioId, String username, String passwordTemporal) {
        this.usuarioId = usuarioId;
        this.username = username;
        this.passwordTemporal = passwordTemporal;
        this.fechaGeneracion = LocalDateTime.now();
        this.mensaje = "Credencial temporal generada exitosamente. Debe entregarse de forma presencial con firma de comprobante. Obliga al cambio de clave en el próximo inicio de sesión (UC-PROV-03 A2).";
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

    public String getPasswordTemporal() {
        return passwordTemporal;
    }

    public void setPasswordTemporal(String passwordTemporal) {
        this.passwordTemporal = passwordTemporal;
    }

    public LocalDateTime getFechaGeneracion() {
        return fechaGeneracion;
    }

    public void setFechaGeneracion(LocalDateTime fechaGeneracion) {
        this.fechaGeneracion = fechaGeneracion;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
