package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.Parentesco;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class EstudianteAcudienteRequest {

    @NotBlank(message = "El documento del estudiante es obligatorio")
    @Size(max = 20, message = "El documento del estudiante no puede superar los 20 caracteres")
    private String documentoEstudiante;

    @NotBlank(message = "El documento del acudiente es obligatorio")
    @Size(max = 20, message = "El documento del acudiente no puede superar los 20 caracteres")
    private String documentoAcudiente;

    @NotNull(message = "El parentesco es obligatorio")
    private Parentesco parentesco;

    @NotNull(message = "Debe indicar si es el acudiente principal")
    private Boolean esPrincipal;

    @NotNull(message = "Debe indicar si el acudiente convive con el estudiante")
    private Boolean conviveConEstudiante;

    @NotNull(message = "Debe indicar si el acudiente está autorizado para retirar al estudiante")
    private Boolean autorizadoRetiro;

    public String getDocumentoEstudiante() {
        return documentoEstudiante;
    }

    public void setDocumentoEstudiante(String documentoEstudiante) {
        this.documentoEstudiante = documentoEstudiante;
    }

    public String getDocumentoAcudiente() {
        return documentoAcudiente;
    }

    public void setDocumentoAcudiente(String documentoAcudiente) {
        this.documentoAcudiente = documentoAcudiente;
    }

    public Parentesco getParentesco() {
        return parentesco;
    }

    public void setParentesco(Parentesco parentesco) {
        this.parentesco = parentesco;
    }

    public Boolean getEsPrincipal() {
        return esPrincipal;
    }

    public void setEsPrincipal(Boolean esPrincipal) {
        this.esPrincipal = esPrincipal;
    }

    public Boolean getConviveConEstudiante() {
        return conviveConEstudiante;
    }

    public void setConviveConEstudiante(Boolean conviveConEstudiante) {
        this.conviveConEstudiante = conviveConEstudiante;
    }

    public Boolean getAutorizadoRetiro() {
        return autorizadoRetiro;
    }

    public void setAutorizadoRetiro(Boolean autorizadoRetiro) {
        this.autorizadoRetiro = autorizadoRetiro;
    }
}

