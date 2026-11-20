package com.jackren.sistemaacademico.dto;

import java.util.ArrayList;
import java.util.List;

public class DiagnosticoPreCierreResponse {

    private Long periodoId;
    private String periodoNombre;
    private Integer anioLectivo;
    private Boolean aptoParaCierre = true;
    private List<String> bloqueosCriticos = new ArrayList<>();
    private Integer totalGruposValidados = 0;
    private Integer totalEstudiantesEvaluados = 0;

    public DiagnosticoPreCierreResponse() {
    }

    public Long getPeriodoId() {
        return periodoId;
    }

    public void setPeriodoId(Long periodoId) {
        this.periodoId = periodoId;
    }

    public String getPeriodoNombre() {
        return periodoNombre;
    }

    public void setPeriodoNombre(String periodoNombre) {
        this.periodoNombre = periodoNombre;
    }

    public Integer getAnioLectivo() {
        return anioLectivo;
    }

    public void setAnioLectivo(Integer anioLectivo) {
        this.anioLectivo = anioLectivo;
    }

    public Boolean getAptoParaCierre() {
        return aptoParaCierre;
    }

    public void setAptoParaCierre(Boolean aptoParaCierre) {
        this.aptoParaCierre = aptoParaCierre;
    }

    public List<String> getBloqueosCriticos() {
        return bloqueosCriticos;
    }

    public void setBloqueosCriticos(List<String> bloqueosCriticos) {
        this.bloqueosCriticos = bloqueosCriticos;
    }

    public Integer getTotalGruposValidados() {
        return totalGruposValidados;
    }

    public void setTotalGruposValidados(Integer totalGruposValidados) {
        this.totalGruposValidados = totalGruposValidados;
    }

    public Integer getTotalEstudiantesEvaluados() {
        return totalEstudiantesEvaluados;
    }

    public void setTotalEstudiantesEvaluados(Integer totalEstudiantesEvaluados) {
        this.totalEstudiantesEvaluados = totalEstudiantesEvaluados;
    }
}
