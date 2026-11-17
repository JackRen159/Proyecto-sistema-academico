package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.DesempenoNacional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AreaBoletinDTO {

    private Long areaId;
    private String nombreArea;
    private BigDecimal promedioArea;
    private DesempenoNacional desempenoArea;
    private List<AsignaturaBoletinDTO> asignaturas = new ArrayList<>();

    public AreaBoletinDTO() {
    }

    public AreaBoletinDTO(Long areaId, String nombreArea, BigDecimal promedioArea, DesempenoNacional desempenoArea) {
        this.areaId = areaId;
        this.nombreArea = nombreArea;
        this.promedioArea = promedioArea;
        this.desempenoArea = desempenoArea;
    }

    public Long getAreaId() {
        return areaId;
    }

    public void setAreaId(Long areaId) {
        this.areaId = areaId;
    }

    public String getNombreArea() {
        return nombreArea;
    }

    public void setNombreArea(String nombreArea) {
        this.nombreArea = nombreArea;
    }

    public BigDecimal getPromedioArea() {
        return promedioArea;
    }

    public void setPromedioArea(BigDecimal promedioArea) {
        this.promedioArea = promedioArea;
    }

    public DesempenoNacional getDesempenoArea() {
        return desempenoArea;
    }

    public void setDesempenoArea(DesempenoNacional desempenoArea) {
        this.desempenoArea = desempenoArea;
    }

    public List<AsignaturaBoletinDTO> getAsignaturas() {
        return asignaturas;
    }

    public void setAsignaturas(List<AsignaturaBoletinDTO> asignaturas) {
        this.asignaturas = asignaturas != null ? asignaturas : new ArrayList<>();
    }
}
