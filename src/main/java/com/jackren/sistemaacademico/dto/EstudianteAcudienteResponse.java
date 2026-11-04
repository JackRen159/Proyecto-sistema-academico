package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.Parentesco;

import java.time.LocalDateTime;

public class EstudianteAcudienteResponse {

    private Long id;

    // Datos del Estudiante
    private Long estudianteId;
    private String estudianteNombres;
    private String estudianteApellidos;
    private String estudianteDocumento;

    // Datos del Acudiente
    private Long acudienteId;
    private String acudienteNombres;
    private String acudienteApellidos;
    private String acudienteDocumento;
    private String acudienteTelefono;
    private String acudienteTelefonoAlternativo;
    private String acudienteCorreo;

    // Datos del Vínculo
    private Parentesco parentesco;
    private Boolean esPrincipal;
    private Boolean conviveConEstudiante;
    private Boolean autorizadoRetiro;
    private LocalDateTime fechaVinculacion;

    public EstudianteAcudienteResponse(Long id,
                                      Long estudianteId, String estudianteNombres, String estudianteApellidos, String estudianteDocumento,
                                      Long acudienteId, String acudienteNombres, String acudienteApellidos, String acudienteDocumento,
                                      String acudienteTelefono, String acudienteTelefonoAlternativo, String acudienteCorreo,
                                      Parentesco parentesco, Boolean esPrincipal, Boolean conviveConEstudiante,
                                      Boolean autorizadoRetiro, LocalDateTime fechaVinculacion) {
        this.id = id;
        this.estudianteId = estudianteId;
        this.estudianteNombres = estudianteNombres;
        this.estudianteApellidos = estudianteApellidos;
        this.estudianteDocumento = estudianteDocumento;
        this.acudienteId = acudienteId;
        this.acudienteNombres = acudienteNombres;
        this.acudienteApellidos = acudienteApellidos;
        this.acudienteDocumento = acudienteDocumento;
        this.acudienteTelefono = acudienteTelefono;
        this.acudienteTelefonoAlternativo = acudienteTelefonoAlternativo;
        this.acudienteCorreo = acudienteCorreo;
        this.parentesco = parentesco;
        this.esPrincipal = esPrincipal;
        this.conviveConEstudiante = conviveConEstudiante;
        this.autorizadoRetiro = autorizadoRetiro;
        this.fechaVinculacion = fechaVinculacion;
    }

    public Long getId() {
        return id;
    }

    public Long getEstudianteId() {
        return estudianteId;
    }

    public String getEstudianteNombres() {
        return estudianteNombres;
    }

    public String getEstudianteApellidos() {
        return estudianteApellidos;
    }

    public String getEstudianteDocumento() {
        return estudianteDocumento;
    }

    public Long getAcudienteId() {
        return acudienteId;
    }

    public String getAcudienteNombres() {
        return acudienteNombres;
    }

    public String getAcudienteApellidos() {
        return acudienteApellidos;
    }

    public String getAcudienteDocumento() {
        return acudienteDocumento;
    }

    public String getAcudienteTelefono() {
        return acudienteTelefono;
    }

    public String getAcudienteTelefonoAlternativo() {
        return acudienteTelefonoAlternativo;
    }

    public String getAcudienteCorreo() {
        return acudienteCorreo;
    }

    public Parentesco getParentesco() {
        return parentesco;
    }

    public Boolean getEsPrincipal() {
        return esPrincipal;
    }

    public Boolean getConviveConEstudiante() {
        return conviveConEstudiante;
    }

    public Boolean getAutorizadoRetiro() {
        return autorizadoRetiro;
    }

    public LocalDateTime getFechaVinculacion() {
        return fechaVinculacion;
    }
}

