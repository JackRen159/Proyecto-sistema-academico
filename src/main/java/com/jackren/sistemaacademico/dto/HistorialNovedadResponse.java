package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.TipoNovedadMatricula;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class HistorialNovedadResponse {

    private Long id;
    private Long matriculaId;
    private String numeroMatricula;
    private TipoNovedadMatricula tipoNovedad;
    private Long grupoOrigenId;
    private String grupoOrigenNombre;
    private Long grupoDestinoId;
    private String grupoDestinoNombre;
    private Long gradoOrigenId;
    private String gradoOrigenNombre;
    private Long gradoDestinoId;
    private String gradoDestinoNombre;
    private LocalDate fechaEfectiva;
    private String motivo;
    private String soporteDocumento;
    private String numeroActaResolucion;
    private LocalDate fechaActaResolucion;
    private String solicitante;
    private Long usuarioId;
    private String username;
    private LocalDateTime fechaCreacion;

    public HistorialNovedadResponse() {
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

    public TipoNovedadMatricula getTipoNovedad() {
        return tipoNovedad;
    }

    public void setTipoNovedad(TipoNovedadMatricula tipoNovedad) {
        this.tipoNovedad = tipoNovedad;
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

    public Long getGradoOrigenId() {
        return gradoOrigenId;
    }

    public void setGradoOrigenId(Long gradoOrigenId) {
        this.gradoOrigenId = gradoOrigenId;
    }

    public String getGradoOrigenNombre() {
        return gradoOrigenNombre;
    }

    public void setGradoOrigenNombre(String gradoOrigenNombre) {
        this.gradoOrigenNombre = gradoOrigenNombre;
    }

    public Long getGradoDestinoId() {
        return gradoDestinoId;
    }

    public void setGradoDestinoId(Long gradoDestinoId) {
        this.gradoDestinoId = gradoDestinoId;
    }

    public String getGradoDestinoNombre() {
        return gradoDestinoNombre;
    }

    public void setGradoDestinoNombre(String gradoDestinoNombre) {
        this.gradoDestinoNombre = gradoDestinoNombre;
    }

    public LocalDate getFechaEfectiva() {
        return fechaEfectiva;
    }

    public void setFechaEfectiva(LocalDate fechaEfectiva) {
        this.fechaEfectiva = fechaEfectiva;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getSoporteDocumento() {
        return soporteDocumento;
    }

    public void setSoporteDocumento(String soporteDocumento) {
        this.soporteDocumento = soporteDocumento;
    }

    public String getNumeroActaResolucion() {
        return numeroActaResolucion;
    }

    public void setNumeroActaResolucion(String numeroActaResolucion) {
        this.numeroActaResolucion = numeroActaResolucion;
    }

    public LocalDate getFechaActaResolucion() {
        return fechaActaResolucion;
    }

    public void setFechaActaResolucion(LocalDate fechaActaResolucion) {
        this.fechaActaResolucion = fechaActaResolucion;
    }

    public String getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(String solicitante) {
        this.solicitante = solicitante;
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

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
