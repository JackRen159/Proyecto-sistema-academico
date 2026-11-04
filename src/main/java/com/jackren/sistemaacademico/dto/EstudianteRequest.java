package com.jackren.sistemaacademico.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class EstudianteRequest {

    @NotBlank (message = "Llenar los espacios de los nombres es obligatorio")
    @Size (max = 50, message = "Los nombres no pueden superar los 50 caracteres")
    private String nombres;

    @NotBlank (message = "Llenar los espacios de los apellidos es obligatorio")
    @Size (max = 50, message = "Los apellidos no pueden superar los 50 caracteres")
    private String apellidos;

    @NotBlank (message = "El documento es obligatorio")
    @Size (max = 20, message = "El documento no puede superar los 20 caracteres")
    private String documento;

    @NotNull (message = "La fecha de nacimiento es obligatoria")
    @Past (message = "La fecha de nacimiento debe de ser una fecha pasada")
    private LocalDate fechaNacimiento;

    @Email(message = "El correo electronico no tiene un formato valido")
    @Size (max = 100, message = "El correo no puede superar los 100 caracteres")
    private String correo;

    @Size (max = 20, message = "El telefono no puede superar los 20 caracteres")
    private String telefono;

    @NotBlank (message = "La direccion es obligatoria")
    @Size (max = 150, message = "La direccion no puede superar los 150 caracteres")
    private String direccion;

    public String getNombres(){
        return nombres;
    }
    public void setNombres(String nombres){
        this.nombres = nombres;
    }
    public String getApellidos(){
        return apellidos;
    }
    public void setApellidos(String apellidos){
        this.apellidos = apellidos;
    }
    public String getDocumento(){
        return documento;
    }
    public void setDocumento(String documento){
        this.documento = documento;
    }
    public LocalDate getFechaNacimiento(){
        return fechaNacimiento;
    }
    public void setFechaNacimiento(LocalDate fechaNacimiento){
        this.fechaNacimiento = fechaNacimiento;
    }
    public String getCorreo(){
        return correo;
    }
    public void setCorreo(String correo){
        this.correo = correo;
    }
    public String getTelefono(){
        return telefono;
    }
    public void setTelefono(String telefono){
        this.telefono = telefono;
    }
    public String getDireccion(){
        return direccion;
    }
    public void setDireccion(String direccion){
        this.direccion = direccion;
    }
}
