package com.jackren.sistemaacademico.dto;

import java.time.LocalDateTime;

public class GradoAsignaturaResponse {

    private Long id;
    private Long gradoId;
    private String gradoCodigo;
    private String gradoNombre;
    private Long asignaturaId;
    private String asignaturaCodigo;
    private String asignaturaNombre;
    private Integer intensidadHoraria;
    private Boolean esOptativa;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    public GradoAsignaturaResponse(Long id, Long gradoId, String gradoCodigo, String gradoNombre,
                                  Long asignaturaId, String asignaturaCodigo, String asignaturaNombre,
                                  Integer intensidadHoraria, Boolean esOptativa, Boolean activo,
                                  LocalDateTime fechaCreacion, LocalDateTime fechaActualizacion) {
        this.id = id;
        this.gradoId = gradoId;
        this.gradoCodigo = gradoCodigo;
        this.gradoNombre = gradoNombre;
        this.asignaturaId = asignaturaId;
        this.asignaturaCodigo = asignaturaCodigo;
        this.asignaturaNombre = asignaturaNombre;
        this.intensidadHoraria = intensidadHoraria;
        this.esOptativa = esOptativa;
        this.activo = activo;
        this.fechaCreacion = fechaCreacion;
        this.fechaActualizacion = fechaActualizacion;
    }

    public Long getId() {
        return id;
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

    public Long getAsignaturaId() {
        return asignaturaId;
    }

    public String getAsignaturaCodigo() {
        return asignaturaCodigo;
    }

    public String getAsignaturaNombre() {
        return asignaturaNombre;
    }

    public Integer getIntensidadHoraria() {
        return intensidadHoraria;
    }

    public Boolean getEsOptativa() {
        return esOptativa;
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
