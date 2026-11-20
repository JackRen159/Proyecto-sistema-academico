package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.EstadoHorarioVersion;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HorarioGrupoVersionResponse {

    private Long id;
    private Long grupoId;
    private String grupoNombre;
    private Integer numeroVersion;
    private EstadoHorarioVersion estado;
    private LocalDate fechaInicioVigencia;
    private LocalDate fechaFinVigencia;
    private String usuarioPublicadorNombre;
    private LocalDateTime fechaPublicacion;
    private List<HorarioBloqueResponse> bloques = new ArrayList<>();

    public HorarioGrupoVersionResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGrupoId() {
        return grupoId;
    }

    public void setGrupoId(Long grupoId) {
        this.grupoId = grupoId;
    }

    public String getGrupoNombre() {
        return grupoNombre;
    }

    public void setGrupoNombre(String grupoNombre) {
        this.grupoNombre = grupoNombre;
    }

    public Integer getNumeroVersion() {
        return numeroVersion;
    }

    public void setNumeroVersion(Integer numeroVersion) {
        this.numeroVersion = numeroVersion;
    }

    public EstadoHorarioVersion getEstado() {
        return estado;
    }

    public void setEstado(EstadoHorarioVersion estado) {
        this.estado = estado;
    }

    public LocalDate getFechaInicioVigencia() {
        return fechaInicioVigencia;
    }

    public void setFechaInicioVigencia(LocalDate fechaInicioVigencia) {
        this.fechaInicioVigencia = fechaInicioVigencia;
    }

    public LocalDate getFechaFinVigencia() {
        return fechaFinVigencia;
    }

    public void setFechaFinVigencia(LocalDate fechaFinVigencia) {
        this.fechaFinVigencia = fechaFinVigencia;
    }

    public String getUsuarioPublicadorNombre() {
        return usuarioPublicadorNombre;
    }

    public void setUsuarioPublicadorNombre(String usuarioPublicadorNombre) {
        this.usuarioPublicadorNombre = usuarioPublicadorNombre;
    }

    public LocalDateTime getFechaPublicacion() {
        return fechaPublicacion;
    }

    public void setFechaPublicacion(LocalDateTime fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }

    public List<HorarioBloqueResponse> getBloques() {
        return bloques;
    }

    public void setBloques(List<HorarioBloqueResponse> bloques) {
        this.bloques = bloques;
    }
}
