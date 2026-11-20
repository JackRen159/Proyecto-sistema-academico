package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.TipoNovedadMatricula;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class NovedadMatriculaRequest {

    @NotNull(message = "El tipo de novedad es obligatorio")
    private TipoNovedadMatricula tipoNovedad;

    private Long nuevoGrupoId;

    private LocalDate fechaEfectiva;

    @NotBlank(message = "El motivo de la novedad es obligatorio")
    @Size(max = 500, message = "El motivo no puede superar 500 caracteres")
    private String motivo;

    @Size(max = 255, message = "El soporte documental no puede superar 255 caracteres")
    private String soporteDocumento;

    @Size(max = 100, message = "El número de acta o resolución no puede superar 100 caracteres")
    private String numeroActaResolucion;

    private LocalDate fechaActaResolucion;

    @Size(max = 100, message = "El solicitante no puede superar 100 caracteres")
    private String solicitante;

    private Boolean consentimientoAcudiente = false;

    @Size(max = 150, message = "El colegio destino no puede superar 150 caracteres")
    private String colegioDestino;

    public NovedadMatriculaRequest() {
    }

    public TipoNovedadMatricula getTipoNovedad() {
        return tipoNovedad;
    }

    public void setTipoNovedad(TipoNovedadMatricula tipoNovedad) {
        this.tipoNovedad = tipoNovedad;
    }

    public Long getNuevoGrupoId() {
        return nuevoGrupoId;
    }

    public void setNuevoGrupoId(Long nuevoGrupoId) {
        this.nuevoGrupoId = nuevoGrupoId;
    }

    public LocalDate getFechaEfectiva() {
        return fechaEfectiva;
    }

    public void setFechaEfectiva(LocalDate fechaEfectiva) {
        this.fechaEfectiva = fechaEfectiva;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getSoporteDocumento() {
        return soporteDocumento;
    }

    public void setSoporteDocumento(String soporteDocumento) {
        this.soporteDocumento = soporteDocumento;
    }

    public String getNumeroActaResolucion() {
        return numeroActaResolucion;
    }

    public void setNumeroActaResolucion(String numeroActaResolucion) {
        this.numeroActaResolucion = numeroActaResolucion;
    }

    public LocalDate getFechaActaResolucion() {
        return fechaActaResolucion;
    }

    public void setFechaActaResolucion(LocalDate fechaActaResolucion) {
        this.fechaActaResolucion = fechaActaResolucion;
    }

    public String getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(String solicitante) {
        this.solicitante = solicitante;
    }

    public Boolean getConsentimientoAcudiente() {
        return consentimientoAcudiente;
    }

    public void setConsentimientoAcudiente(Boolean consentimientoAcudiente) {
        this.consentimientoAcudiente = consentimientoAcudiente;
    }

    public String getColegioDestino() {
        return colegioDestino;
    }

    public void setColegioDestino(String colegioDestino) {
        this.colegioDestino = colegioDestino;
    }
}
