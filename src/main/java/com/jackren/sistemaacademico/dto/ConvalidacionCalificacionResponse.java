package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.TipoOrigenConvalidacion;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ConvalidacionCalificacionResponse {

    private Long id;
    private String numeroActa;
    private Long matriculaId;
    private String numeroMatricula;
    private Long estudianteId;
    private String estudianteNombreCompleto;
    private Long periodoAcademicoId;
    private String periodoNombre;
    private TipoOrigenConvalidacion tipoOrigen;
    private String institucionOrigen;
    private String actaSoporteUrl;
    private String observaciones;
    private String coordinadorNombre;
    private LocalDate fechaConvalidacion;
    private LocalDateTime fechaCreacion;
    private List<ConvalidacionDetalleResponse> detalles = new ArrayList<>();

    public ConvalidacionCalificacionResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroActa() {
        return numeroActa;
    }

    public void setNumeroActa(String numeroActa) {
        this.numeroActa = numeroActa;
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

    public Long getPeriodoAcademicoId() {
        return periodoAcademicoId;
    }

    public void setPeriodoAcademicoId(Long periodoAcademicoId) {
        this.periodoAcademicoId = periodoAcademicoId;
    }

    public String getPeriodoNombre() {
        return periodoNombre;
    }

    public void setPeriodoNombre(String periodoNombre) {
        this.periodoNombre = periodoNombre;
    }

    public TipoOrigenConvalidacion getTipoOrigen() {
        return tipoOrigen;
    }

    public void setTipoOrigen(TipoOrigenConvalidacion tipoOrigen) {
        this.tipoOrigen = tipoOrigen;
    }

    public String getInstitucionOrigen() {
        return institucionOrigen;
    }

    public void setInstitucionOrigen(String institucionOrigen) {
        this.institucionOrigen = institucionOrigen;
    }

    public String getActaSoporteUrl() {
        return actaSoporteUrl;
    }

    public void setActaSoporteUrl(String actaSoporteUrl) {
        this.actaSoporteUrl = actaSoporteUrl;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getCoordinadorNombre() {
        return coordinadorNombre;
    }

    public void setCoordinadorNombre(String coordinadorNombre) {
        this.coordinadorNombre = coordinadorNombre;
    }

    public LocalDate getFechaConvalidacion() {
        return fechaConvalidacion;
    }

    public void setFechaConvalidacion(LocalDate fechaConvalidacion) {
        this.fechaConvalidacion = fechaConvalidacion;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public List<ConvalidacionDetalleResponse> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<ConvalidacionDetalleResponse> detalles) {
        this.detalles = detalles;
    }
}
