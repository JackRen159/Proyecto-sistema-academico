package com.jackren.sistemaacademico.dto;

import java.time.LocalDateTime;

public class AreaResponse {

    private Long id;
    private String codigo;
    private String nombre;
    private String descripcion;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    public AreaResponse(Long id, String codigo, String nombre, String descripcion, Boolean activo,
                        LocalDateTime fechaCreacion, LocalDateTime fechaActualizacion) {
        this.id = id;
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.activo = activo;
        this.fechaCreacion = fechaCreacion;
        this.fechaActualizacion = fechaActualizacion;
    }

    public Long getId() {
        return id;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
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
