package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.NivelEducativo;
import com.jackren.sistemaacademico.entity.RegimenEvaluacion;

public class GradoResponse {

    private Long id;
    private String codigo;
    private String nombre;
    private NivelEducativo nivel;
    private Integer ordenJerarquico;
    private Long gradoSiguienteId;
    private String gradoSiguienteNombre;
    private Boolean esGradoTerminal;
    private RegimenEvaluacion regimenEvaluacion;
    private Boolean activo;

    public GradoResponse(Long id, String codigo, String nombre, NivelEducativo nivel, Integer ordenJerarquico,
                         Long gradoSiguienteId, String gradoSiguienteNombre, Boolean esGradoTerminal,
                         RegimenEvaluacion regimenEvaluacion, Boolean activo) {
        this.id = id;
        this.codigo = codigo;
        this.nombre = nombre;
        this.nivel = nivel;
        this.ordenJerarquico = ordenJerarquico;
        this.gradoSiguienteId = gradoSiguienteId;
        this.gradoSiguienteNombre = gradoSiguienteNombre;
        this.esGradoTerminal = esGradoTerminal != null ? esGradoTerminal : false;
        this.regimenEvaluacion = regimenEvaluacion != null ? regimenEvaluacion : RegimenEvaluacion.CUANTITATIVO_AREAS;
        this.activo = activo;
    }

    public GradoResponse(Long id, String codigo, String nombre, NivelEducativo nivel, Integer ordenJerarquico, Boolean activo) {
        this(id, codigo, nombre, nivel, ordenJerarquico, null, null, false, RegimenEvaluacion.CUANTITATIVO_AREAS, activo);
    }

    public Long getId() {
        return id;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public NivelEducativo getNivel() {
        return nivel;
    }

    public Integer getOrdenJerarquico() {
        return ordenJerarquico;
    }

    public Long getGradoSiguienteId() {
        return gradoSiguienteId;
    }

    public String getGradoSiguienteNombre() {
        return gradoSiguienteNombre;
    }

    public Boolean getEsGradoTerminal() {
        return esGradoTerminal;
    }

    public RegimenEvaluacion getRegimenEvaluacion() {
        return regimenEvaluacion;
    }

    public Boolean getActivo() {
        return activo;
    }
}
