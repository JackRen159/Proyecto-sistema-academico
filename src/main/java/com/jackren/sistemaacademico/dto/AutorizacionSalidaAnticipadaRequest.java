package com.jackren.sistemaacademico.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class AutorizacionSalidaAnticipadaRequest {

    @NotNull(message = "La matrícula es obligatoria")
    private Long matriculaId;

    private LocalDate fecha;

    @NotBlank(message = "La hora de salida es obligatoria")
    @Size(max = 10, message = "La hora de salida no puede superar 10 caracteres")
    private String horaSalida;

    @NotBlank(message = "El motivo de la salida es obligatorio")
    @Size(max = 255, message = "El motivo no puede superar 255 caracteres")
    private String motivo;

    @NotBlank(message = "El nombre del adulto que retira es obligatorio")
    @Size(max = 150, message = "El nombre no puede superar 150 caracteres")
    private String adultoRetiraNombre;

    @NotBlank(message = "El documento del adulto que retira es obligatorio")
    @Size(max = 30, message = "El documento no puede superar 30 caracteres")
    private String adultoRetiraDocumento;

    @NotBlank(message = "El parentesco o autorización es obligatorio")
    @Size(max = 50, message = "El parentesco no puede superar 50 caracteres")
    private String parentescoAutorizado;

    @Size(max = 255, message = "Las observaciones no pueden superar 255 caracteres")
    private String observaciones;

    public AutorizacionSalidaAnticipadaRequest() {
    }

    public Long getMatriculaId() {
        return matriculaId;
    }

    public void setMatriculaId(Long matriculaId) {
        this.matriculaId = matriculaId;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getHoraSalida() {
        return horaSalida;
    }

    public void setHoraSalida(String horaSalida) {
        this.horaSalida = horaSalida;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getAdultoRetiraNombre() {
        return adultoRetiraNombre;
    }

    public void setAdultoRetiraNombre(String adultoRetiraNombre) {
        this.adultoRetiraNombre = adultoRetiraNombre;
    }

    public String getAdultoRetiraDocumento() {
        return adultoRetiraDocumento;
    }

    public void setAdultoRetiraDocumento(String adultoRetiraDocumento) {
        this.adultoRetiraDocumento = adultoRetiraDocumento;
    }

    public String getParentescoAutorizado() {
        return parentescoAutorizado;
    }

    public void setParentescoAutorizado(String parentescoAutorizado) {
        this.parentescoAutorizado = parentescoAutorizado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}
