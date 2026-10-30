package jackren.sistemaacademico.main.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class EstudianteResponse {

    private Long id;
    private String nombres;
    private String apellidos;
    private String documento;
    private LocalDate fechaNacimiento;
    private String correo;
    private String telefono;
    private String direccion;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    public EstudianteResponse(Long id, String nombres, String apellidos, String documento, LocalDate fechaNacimiento, String correo,
        String telefono, String direccion, Boolean activo, LocalDateTime fechaCreacion, LocalDateTime fechaActualizacion){
            this.id = id;
            this.nombres = nombres;
            this.apellidos = apellidos;
            this.documento = documento;
            this.fechaNacimiento = fechaNacimiento;
            this.correo = correo;
            this.telefono = telefono;
            this.direccion = direccion;
            this.activo = activo;
            this.fechaCreacion = fechaCreacion;
            this.fechaActualizacion = fechaActualizacion;
        }

    public Long getId(){
        return id;
    }
    public String getNombres(){
        return nombres;
    }
    public String getApellidos(){
        return apellidos;
    }
    public String getDocumento(){
        return documento;
    }
    public LocalDate getFechaNacimiento(){
        return fechaNacimiento;
    }
    public String getCorreo(){
        return correo;
    }
    public String getTelefono(){
        return telefono;
    }
    public String getDireccion(){
        return direccion;
    }
    public Boolean getActivo(){
        return activo;
    }
    public LocalDateTime getFechaCreacion(){
        return fechaCreacion;
    }
    public LocalDateTime getFechaActualizacion(){
        return fechaActualizacion;
    }
}
