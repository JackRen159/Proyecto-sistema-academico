package com.jackren.sistemaacademico.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AreaRequest {

    @NotBlank(message = "El código del área es obligatorio")
    @Size(max = 30, message = "El código no puede superar los 30 caracteres")
    private String codigo;

    @NotBlank(message = "El nombre del área es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    private String nombre;

    @Size(max = 255, message = "La descripción no puede superar los 255 caracteres")
    private String descripcion;

    public AreaRequest() {
    }

    public AreaRequest(String codigo, String nombre, String descripcion) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
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
