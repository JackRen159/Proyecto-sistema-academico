package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.DesempenoNacional;

import java.math.BigDecimal;

public class ResumenBoletinDTO {

    private BigDecimal promedioGeneral;
    private DesempenoNacional desempenoGeneral;
    private Integer totalAsignaturas;
    private Integer asignaturasAprobadas;
    private Integer asignaturasReprobadas;
    private Long totalInasistencias;
    private Long totalRetardos;
    private Integer puestoEnGrupo;
    private String escalaValoracionInfo;

    public ResumenBoletinDTO() {
    }

    public ResumenBoletinDTO(BigDecimal promedioGeneral,
                             DesempenoNacional desempenoGeneral,
                             Integer totalAsignaturas,
                             Integer asignaturasAprobadas,
                             Integer asignaturasReprobadas,
                             Long totalInasistencias,
                             Long totalRetardos,
                             Integer puestoEnGrupo,
                             String escalaValoracionInfo) {
        this.promedioGeneral = promedioGeneral;
        this.desempenoGeneral = desempenoGeneral;
        this.totalAsignaturas = totalAsignaturas != null ? totalAsignaturas : 0;
        this.asignaturasAprobadas = asignaturasAprobadas != null ? asignaturasAprobadas : 0;
        this.asignaturasReprobadas = asignaturasReprobadas != null ? asignaturasReprobadas : 0;
        this.totalInasistencias = totalInasistencias != null ? totalInasistencias : 0L;
        this.totalRetardos = totalRetardos != null ? totalRetardos : 0L;
        this.puestoEnGrupo = puestoEnGrupo;
        this.escalaValoracionInfo = escalaValoracionInfo;
    }

    public BigDecimal getPromedioGeneral() {
        return promedioGeneral;
    }

    public void setPromedioGeneral(BigDecimal promedioGeneral) {
        this.promedioGeneral = promedioGeneral;
    }

    public DesempenoNacional getDesempenoGeneral() {
        return desempenoGeneral;
    }

    public void setDesempenoGeneral(DesempenoNacional desempenoGeneral) {
        this.desempenoGeneral = desempenoGeneral;
    }

    public Integer getTotalAsignaturas() {
        return totalAsignaturas;
    }

    public void setTotalAsignaturas(Integer totalAsignaturas) {
        this.totalAsignaturas = totalAsignaturas;
    }

    public Integer getAsignaturasAprobadas() {
        return asignaturasAprobadas;
    }

    public void setAsignaturasAprobadas(Integer asignaturasAprobadas) {
        this.asignaturasAprobadas = asignaturasAprobadas;
    }

    public Integer getAsignaturasReprobadas() {
        return asignaturasReprobadas;
    }

    public void setAsignaturasReprobadas(Integer asignaturasReprobadas) {
        this.asignaturasReprobadas = asignaturasReprobadas;
    }

    public Long getTotalInasistencias() {
        return totalInasistencias;
    }

    public void setTotalInasistencias(Long totalInasistencias) {
        this.totalInasistencias = totalInasistencias;
    }

    public Long getTotalRetardos() {
        return totalRetardos;
    }

    public void setTotalRetardos(Long totalRetardos) {
        this.totalRetardos = totalRetardos;
    }

    public Integer getPuestoEnGrupo() {
        return puestoEnGrupo;
    }

    public void setPuestoEnGrupo(Integer puestoEnGrupo) {
        this.puestoEnGrupo = puestoEnGrupo;
    }

    public String getEscalaValoracionInfo() {
        return escalaValoracionInfo;
    }

    public void setEscalaValoracionInfo(String escalaValoracionInfo) {
        this.escalaValoracionInfo = escalaValoracionInfo;
    }
}
