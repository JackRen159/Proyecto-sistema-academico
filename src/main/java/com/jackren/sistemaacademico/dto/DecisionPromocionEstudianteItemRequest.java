package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.EstadoPromocionEstudiante;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class DecisionPromocionEstudianteItemRequest {

    @NotNull(message = "La matrícula es obligatoria")
    private Long matriculaId;

    @NotNull(message = "El estado de promoción es obligatorio")
    private EstadoPromocionEstudiante estadoPromocion;

    @NotNull(message = "El promedio general es obligatorio")
    private BigDecimal promedioGeneral;

    private Integer areasReprobadas = 0;

    private Integer asignaturasReprobadas = 0;

    private BigDecimal porcentajeInasistenciaAnual = BigDecimal.ZERO;

    private Long gradoDestinoId;

    private String motivoDecision;

    public DecisionPromocionEstudianteItemRequest() {
    }

    public Long getMatriculaId() {
        return matriculaId;
    }

    public void setMatriculaId(Long matriculaId) {
        this.matriculaId = matriculaId;
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

    public String getMotivoDecision() {
        return motivoDecision;
    }

    public void setMotivoDecision(String motivoDecision) {
        this.motivoDecision = motivoDecision;
    }
}
