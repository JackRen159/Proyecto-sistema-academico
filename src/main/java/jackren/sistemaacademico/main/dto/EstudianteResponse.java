package jackren.sistemaacademico.main.dto;

import java.time.LocalDate;

public class EstudianteResponse {

    private Long id;
    private String nombres;
    private String apellidos;
    private String documento;
    private LocalDate fechaNacimiento;
    private String correo;
    private String telefono;
    private Boolean activo;

    public EstudianteResponse(Long id, String nombres, String apellidos, String documento, LocalDate fechaNacimiento, String correo,
        String telefono, Boolean activo){
            this.id = id;
            this.nombres = nombres;
            this.apellidos = apellidos;
            this.documento = documento;
            this.fechaNacimiento = fechaNacimiento;
            this.correo = correo;
            this.telefono = telefono;
            this.activo = activo;
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
    public Boolean getActivo(){
        return activo;
    }
}
