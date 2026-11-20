package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.DesempenoNacional;
import com.jackren.sistemaacademico.entity.EstadoCalificacionActividad;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class CalificacionActividadResponse {

    private Long id;
    private Long actividadId;
    private String actividadNombre;
    private BigDecimal actividadPorcentaje;
    private Long matriculaId;
    private String numeroMatricula;
    private Long estudianteId;
    private String estudianteNombreCompleto;
    private String estudianteDocumento;
    private BigDecimal valor;
    private EstadoCalificacionActividad estado;
    private LocalDate fechaSupletoria;
    private String valorCualitativo;
    private String observacionDescriptiva;
    private DesempenoNacional desempeno;
    private String observaciones;
    private LocalDateTime fechaRegistro;
    private LocalDateTime fechaActualizacion;

    public CalificacionActividadResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getActividadId() {
        return actividadId;
    }

    public void setActividadId(Long actividadId) {
        this.actividadId = actividadId;
    }

    public String getActividadNombre() {
        return actividadNombre;
    }

    public void setActividadNombre(String actividadNombre) {
        this.actividadNombre = actividadNombre;
    }

    public BigDecimal getActividadPorcentaje() {
        return actividadPorcentaje;
    }

    public void setActividadPorcentaje(BigDecimal actividadPorcentaje) {
        this.actividadPorcentaje = actividadPorcentaje;
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

    public String getEstudianteDocumento() {
        return estudianteDocumento;
    }

    public void setEstudianteDocumento(String estudianteDocumento) {
        this.estudianteDocumento = estudianteDocumento;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public EstadoCalificacionActividad getEstado() {
        return estado;
    }

    public void setEstado(EstadoCalificacionActividad estado) {
        this.estado = estado;
    }

    public LocalDate getFechaSupletoria() {
        return fechaSupletoria;
    }

    public void setFechaSupletoria(LocalDate fechaSupletoria) {
        this.fechaSupletoria = fechaSupletoria;
    }

    public String getValorCualitativo() {
        return valorCualitativo;
    }

    public void setValorCualitativo(String valorCualitativo) {
        this.valorCualitativo = valorCualitativo;
    }

    public String getObservacionDescriptiva() {
        return observacionDescriptiva;
    }

    public void setObservacionDescriptiva(String observacionDescriptiva) {
        this.observacionDescriptiva = observacionDescriptiva;
    }

    public DesempenoNacional getDesempeno() {
        return desempeno;
    }

    public void setDesempeno(DesempenoNacional desempeno) {
        this.desempeno = desempeno;
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

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }
}
