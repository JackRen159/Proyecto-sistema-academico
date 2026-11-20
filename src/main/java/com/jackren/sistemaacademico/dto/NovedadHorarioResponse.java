package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.TipoNovedadHorario;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class NovedadHorarioResponse {

    private Long id;
    private Long horarioBloqueId;
    private TipoNovedadHorario tipoNovedad;
    private Long aulaReemplazoId;
    private String aulaReemplazoCodigo;
    private Long docenteReemplazoId;
    private String docenteReemplazoNombre;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String motivo;
    private String usuarioAutorizadorNombre;
    private LocalDateTime fechaRegistro;

    public NovedadHorarioResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getHorarioBloqueId() {
        return horarioBloqueId;
    }

    public void setHorarioBloqueId(Long horarioBloqueId) {
        this.horarioBloqueId = horarioBloqueId;
    }

    public TipoNovedadHorario getTipoNovedad() {
        return tipoNovedad;
    }

    public void setTipoNovedad(TipoNovedadHorario tipoNovedad) {
        this.tipoNovedad = tipoNovedad;
    }

    public Long getAulaReemplazoId() {
        return aulaReemplazoId;
    }

    public void setAulaReemplazoId(Long aulaReemplazoId) {
        this.aulaReemplazoId = aulaReemplazoId;
    }

    public String getAulaReemplazoCodigo() {
        return aulaReemplazoCodigo;
    }

    public void setAulaReemplazoCodigo(String aulaReemplazoCodigo) {
        this.aulaReemplazoCodigo = aulaReemplazoCodigo;
    }

    public Long getDocenteReemplazoId() {
        return docenteReemplazoId;
    }

    public void setDocenteReemplazoId(Long docenteReemplazoId) {
        this.docenteReemplazoId = docenteReemplazoId;
    }

    public String getDocenteReemplazoNombre() {
        return docenteReemplazoNombre;
    }

    public void setDocenteReemplazoNombre(String docenteReemplazoNombre) {
        this.docenteReemplazoNombre = docenteReemplazoNombre;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getUsuarioAutorizadorNombre() {
        return usuarioAutorizadorNombre;
    }

    public void setUsuarioAutorizadorNombre(String usuarioAutorizadorNombre) {
        this.usuarioAutorizadorNombre = usuarioAutorizadorNombre;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}
