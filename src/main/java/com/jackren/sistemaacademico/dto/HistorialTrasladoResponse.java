package com.jackren.sistemaacademico.dto;

import java.time.LocalDateTime;

public class HistorialTrasladoResponse {

    private Long id;
    private Long matriculaId;
    private String numeroMatricula;
    private Long grupoOrigenId;
    private String grupoOrigenNombre;
    private Long grupoDestinoId;
    private String grupoDestinoNombre;
    private String motivo;
    private Long usuarioId;
    private String username;
    private LocalDateTime fechaTraslado;

    public HistorialTrasladoResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMatriculaId() {
        return matriculaId;
    }

    public void setMatriculaId(Long matriculaId) {
        this.matriculaId = matriculaId;
    }

    public String getNumeroMatricula() {
        return numeroMatricula;
    }

    public void setNumeroMatricula(String numeroMatricula) {
        this.numeroMatricula = numeroMatricula;
    }

    public Long getGrupoOrigenId() {
        return grupoOrigenId;
    }

    public void setGrupoOrigenId(Long grupoOrigenId) {
        this.grupoOrigenId = grupoOrigenId;
    }

    public String getGrupoOrigenNombre() {
        return grupoOrigenNombre;
    }

    public void setGrupoOrigenNombre(String grupoOrigenNombre) {
        this.grupoOrigenNombre = grupoOrigenNombre;
    }

    public Long getGrupoDestinoId() {
        return grupoDestinoId;
    }

    public void setGrupoDestinoId(Long grupoDestinoId) {
        this.grupoDestinoId = grupoDestinoId;
    }

    public String getGrupoDestinoNombre() {
        return grupoDestinoNombre;
    }

    public void setGrupoDestinoNombre(String grupoDestinoNombre) {
        this.grupoDestinoNombre = grupoDestinoNombre;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
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

    public LocalDateTime getFechaTraslado() {
        return fechaTraslado;
    }

    public void setFechaTraslado(LocalDateTime fechaTraslado) {
        this.fechaTraslado = fechaTraslado;
    }
}
