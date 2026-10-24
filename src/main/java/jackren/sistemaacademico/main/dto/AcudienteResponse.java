package jackren.sistemaacademico.main.dto;

import java.time.LocalDateTime;

public class AcudienteResponse {

    private Long id;
    private String nombres;
    private String apellidos;
    private String documento;
    private String telefono;
    private String telefonoAlternativo;
    private String correo;
    private String direccion;
    private String ocupacion;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    public AcudienteResponse(Long id, String nombres, String apellidos, String documento,
                             String telefono, String telefonoAlternativo, String correo,
                             String direccion, String ocupacion, Boolean activo,
                             LocalDateTime fechaCreacion, LocalDateTime fechaActualizacion) {
        this.id = id;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.documento = documento;
        this.telefono = telefono;
        this.telefonoAlternativo = telefonoAlternativo;
        this.correo = correo;
        this.direccion = direccion;
        this.ocupacion = ocupacion;
        this.activo = activo;
        this.fechaCreacion = fechaCreacion;
        this.fechaActualizacion = fechaActualizacion;
    }

    public Long getId() {
        return id;
    }

    public String getNombres() {
        return nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public String getDocumento() {
        return documento;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getTelefonoAlternativo() {
        return telefonoAlternativo;
    }

    public String getCorreo() {
        return correo;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getOcupacion() {
        return ocupacion;
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
