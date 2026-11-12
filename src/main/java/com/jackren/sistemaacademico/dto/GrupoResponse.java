package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.Jornada;

import java.time.LocalDateTime;

public class GrupoResponse {

    private Long id;
    private Long anioLectivoId;
    private Integer anioLectivoAnio;
    private Long gradoId;
    private String gradoCodigo;
    private String gradoNombre;
    private String nombre;
    private Jornada jornada;
    private Integer cupoMaximo;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    public GrupoResponse(Long id, Long anioLectivoId, Integer anioLectivoAnio, Long gradoId, String gradoCodigo,
                         String gradoNombre, String nombre, Jornada jornada, Integer cupoMaximo, Boolean activo,
                         LocalDateTime fechaCreacion, LocalDateTime fechaActualizacion) {
        this.id = id;
        this.anioLectivoId = anioLectivoId;
        this.anioLectivoAnio = anioLectivoAnio;
        this.gradoId = gradoId;
        this.gradoCodigo = gradoCodigo;
        this.gradoNombre = gradoNombre;
        this.nombre = nombre;
        this.jornada = jornada;
        this.cupoMaximo = cupoMaximo;
        this.activo = activo;
        this.fechaCreacion = fechaCreacion;
        this.fechaActualizacion = fechaActualizacion;
    }

    public Long getId() {
        return id;
    }

    public Long getAnioLectivoId() {
        return anioLectivoId;
    }

    public Integer getAnioLectivoAnio() {
        return anioLectivoAnio;
    }

    public Long getGradoId() {
        return gradoId;
    }

    public String getGradoCodigo() {
        return gradoCodigo;
    }

    public String getGradoNombre() {
        return gradoNombre;
    }

    public String getNombre() {
        return nombre;
    }

    public Jornada getJornada() {
        return jornada;
    }

    public Integer getCupoMaximo() {
        return cupoMaximo;
    }

    public Boolean getActivo() {
        return activo;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }
}
