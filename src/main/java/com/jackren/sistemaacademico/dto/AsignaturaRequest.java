package com.jackren.sistemaacademico.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AsignaturaRequest {

    private Long areaId;

    @NotBlank(message = "El código de la asignatura es obligatorio")
    @Size(max = 30, message = "El código no puede superar los 30 caracteres")
    private String codigo;

    @NotBlank(message = "El nombre de la asignatura es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    private String nombre;

    @Size(max = 255, message = "La descripción no puede superar los 255 caracteres")
    private String descripcion;

    public AsignaturaRequest() {
    }

    public AsignaturaRequest(Long areaId, String codigo, String nombre, String descripcion) {
        this.areaId = areaId;
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public Long getAreaId() {
        return areaId;
    }

    public void setAreaId(Long areaId) {
        this.areaId = areaId;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
