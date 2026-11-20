package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.EstadoPromocionEstudiante;

import java.math.BigDecimal;

public class PreclasificacionPromocionResponse {

    private Long matriculaId;
    private String numeroMatricula;
    private Long estudianteId;
    private String estudianteNombreCompleto;
    private BigDecimal promedioGeneral;
    private Integer areasReprobadas;
    private Integer asignaturasReprobadas;
    private BigDecimal porcentajeInasistenciaAnual;
    private EstadoPromocionEstudiante clasificacionSugerida;
    private Long gradoDestinoSugeridoId;
    private String gradoDestinoSugeridoNombre;
    private String motivoSugerido;

    public PreclasificacionPromocionResponse() {
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

    public EstadoPromocionEstudiante getClasificacionSugerida() {
        return clasificacionSugerida;
    }

    public void setClasificacionSugerida(EstadoPromocionEstudiante clasificacionSugerida) {
        this.clasificacionSugerida = clasificacionSugerida;
    }

    public Long getGradoDestinoSugeridoId() {
        return gradoDestinoSugeridoId;
    }

    public void setGradoDestinoSugeridoId(Long gradoDestinoSugeridoId) {
        this.gradoDestinoSugeridoId = gradoDestinoSugeridoId;
    }

    public String getGradoDestinoSugeridoNombre() {
        return gradoDestinoSugeridoNombre;
    }

    public void setGradoDestinoSugeridoNombre(String gradoDestinoSugeridoNombre) {
        this.gradoDestinoSugeridoNombre = gradoDestinoSugeridoNombre;
    }

    public String getMotivoSugerido() {
        return motivoSugerido;
    }

    public void setMotivoSugerido(String motivoSugerido) {
        this.motivoSugerido = motivoSugerido;
    }
}
