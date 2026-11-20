package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.TipoComponenteCurricular;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class GradoAsignaturaRequest {

    @NotNull(message = "El ID del grado es obligatorio")
    private Long gradoId;

    @NotNull(message = "El ID de la asignatura es obligatorio")
    private Long asignaturaId;

    @NotNull(message = "La intensidad horaria es obligatoria")
    @Min(value = 1, message = "La intensidad horaria debe ser mayor a 0")
    private Integer intensidadHoraria = 1;

    private Boolean esOptativa = false;

    private Long anioLectivoId;

    @DecimalMin(value = "0.01", message = "El peso porcentual debe ser mayor a 0")
    @DecimalMax(value = "100.00", message = "El peso porcentual no puede superar 100")
    private BigDecimal pesoPorcentualEnArea = new BigDecimal("100.00");

    private TipoComponenteCurricular tipoComponente = TipoComponenteCurricular.ASIGNATURA;

    private Boolean computaPromocion = true;

    public GradoAsignaturaRequest() {
    }

    public GradoAsignaturaRequest(Long gradoId, Long asignaturaId, Integer intensidadHoraria, Boolean esOptativa) {
        this.gradoId = gradoId;
        this.asignaturaId = asignaturaId;
        this.intensidadHoraria = intensidadHoraria;
        this.esOptativa = esOptativa != null ? esOptativa : false;
    }

    public Long getGradoId() {
        return gradoId;
    }

    public void setGradoId(Long gradoId) {
        this.gradoId = gradoId;
    }

    public Long getAsignaturaId() {
        return asignaturaId;
    }

    public void setAsignaturaId(Long asignaturaId) {
        this.asignaturaId = asignaturaId;
    }

    public Integer getIntensidadHoraria() {
        return intensidadHoraria;
    }

    public void setIntensidadHoraria(Integer intensidadHoraria) {
        this.intensidadHoraria = intensidadHoraria;
    }

    public Boolean getEsOptativa() {
        return esOptativa;
    }

    public void setEsOptativa(Boolean esOptativa) {
        this.esOptativa = esOptativa;
    }

    public Long getAnioLectivoId() {
        return anioLectivoId;
    }

    public void setAnioLectivoId(Long anioLectivoId) {
        this.anioLectivoId = anioLectivoId;
    }

    public BigDecimal getPesoPorcentualEnArea() {
        return pesoPorcentualEnArea;
    }

    public void setPesoPorcentualEnArea(BigDecimal pesoPorcentualEnArea) {
        this.pesoPorcentualEnArea = pesoPorcentualEnArea;
    }

    public TipoComponenteCurricular getTipoComponente() {
        return tipoComponente;
    }

    public void setTipoComponente(TipoComponenteCurricular tipoComponente) {
        this.tipoComponente = tipoComponente;
    }

    public Boolean getComputaPromocion() {
        return computaPromocion;
    }

    public void setComputaPromocion(Boolean computaPromocion) {
        this.computaPromocion = computaPromocion;
    }
}
