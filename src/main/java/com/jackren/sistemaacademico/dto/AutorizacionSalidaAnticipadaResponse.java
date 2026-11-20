package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.EstadoAutorizacionSalida;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AutorizacionSalidaAnticipadaResponse {

    private Long id;
    private String numeroAutorizacion;
    private Long matriculaId;
    private String numeroMatricula;
    private Long estudianteId;
    private String estudianteNombreCompleto;
    private LocalDate fecha;
    private String horaSalida;
    private String motivo;
    private String adultoRetiraNombre;
    private String adultoRetiraDocumento;
    private String parentescoAutorizado;
    private String usuarioPorteriaValidadorNombre;
    private EstadoAutorizacionSalida estado;
    private String observaciones;
    private LocalDateTime fechaRegistro;

    public AutorizacionSalidaAnticipadaResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroAutorizacion() {
        return numeroAutorizacion;
    }

    public void setNumeroAutorizacion(String numeroAutorizacion) {
        this.numeroAutorizacion = numeroAutorizacion;
    }

    public Long getMatriculaId() {
        return matriculaId;
    }

    public void setMatriculaId(Long matriculaId) {
        this.matriculaId = matriculaId;
    }

    public String getNumeroMatricula() {
        return numeroMatricula;
    }

    public void setNumeroMatricula(String numeroMatricula) {
        this.numeroMatricula = numeroMatricula;
    }

    public Long getEstudianteId() {
        return estudianteId;
    }

    public void setEstudianteId(Long estudianteId) {
        this.estudianteId = estudianteId;
    }

    public String getEstudianteNombreCompleto() {
        return estudianteNombreCompleto;
    }

    public void setEstudianteNombreCompleto(String estudianteNombreCompleto) {
        this.estudianteNombreCompleto = estudianteNombreCompleto;
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

    public String getUsuarioPorteriaValidadorNombre() {
        return usuarioPorteriaValidadorNombre;
    }

    public void setUsuarioPorteriaValidadorNombre(String usuarioPorteriaValidadorNombre) {
        this.usuarioPorteriaValidadorNombre = usuarioPorteriaValidadorNombre;
    }

    public EstadoAutorizacionSalida getEstado() {
        return estado;
    }

    public void setEstado(EstadoAutorizacionSalida estado) {
        this.estado = estado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}
