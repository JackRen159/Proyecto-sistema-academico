package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.EstadoPromocionEstudiante;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PromocionEstudianteResponse {

    private Long id;
    private Long actaPromocionId;
    private String numeroActa;
    private Long matriculaId;
    private String numeroMatricula;
    private Long estudianteId;
    private String estudianteNombreCompleto;
    private EstadoPromocionEstudiante estadoPromocion;
    private BigDecimal promedioGeneral;
    private Integer areasReprobadas;
    private Integer asignaturasReprobadas;
    private BigDecimal porcentajeInasistenciaAnual;
    private Long gradoDestinoId;
    private String gradoDestinoNombre;
    private String motivoDecision;
    private LocalDateTime fechaRegistro;

    public PromocionEstudianteResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getActaPromocionId() {
        return actaPromocionId;
    }

    public void setActaPromocionId(Long actaPromocionId) {
        this.actaPromocionId = actaPromocionId;
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

    public EstadoPromocionEstudiante getEstadoPromocion() {
        return estadoPromocion;
    }

    public void setEstadoPromocion(EstadoPromocionEstudiante estadoPromocion) {
        this.estadoPromocion = estadoPromocion;
    }

    public BigDecimal getPromedioGeneral() {
        return promedioGeneral;
    }

    public void setPromedioGeneral(BigDecimal promedioGeneral) {
        this.promedioGeneral = promedioGeneral;
    }

    public Integer getAreasReprobadas() {
        return areasReprobadas;
    }

    public void setAreasReprobadas(Integer areasReprobadas) {
        this.areasReprobadas = areasReprobadas;
    }

    public Integer getAsignaturasReprobadas() {
        return asignaturasReprobadas;
    }

    public void setAsignaturasReprobadas(Integer asignaturasReprobadas) {
        this.asignaturasReprobadas = asignaturasReprobadas;
    }

    public BigDecimal getPorcentajeInasistenciaAnual() {
        return porcentajeInasistenciaAnual;
    }

    public void setPorcentajeInasistenciaAnual(BigDecimal porcentajeInasistenciaAnual) {
        this.porcentajeInasistenciaAnual = porcentajeInasistenciaAnual;
    }

    public Long getGradoDestinoId() {
        return gradoDestinoId;
    }

    public void setGradoDestinoId(Long gradoDestinoId) {
        this.gradoDestinoId = gradoDestinoId;
    }

    public String getGradoDestinoNombre() {
        return gradoDestinoNombre;
    }

    public void setGradoDestinoNombre(String gradoDestinoNombre) {
        this.gradoDestinoNombre = gradoDestinoNombre;
    }

    public String getMotivoDecision() {
        return motivoDecision;
    }

    public void setMotivoDecision(String motivoDecision) {
        this.motivoDecision = motivoDecision;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}
