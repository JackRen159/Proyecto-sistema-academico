package com.jackren.sistemaacademico.dto;

import jakarta.validation.constraints.NotBlank;

public class CrearManualBorradorRequest {

    @NotBlank(message = "El nombre de la versión del Manual de Convivencia es obligatorio")
    private String nombreVersion;

    private Long anioLectivoVigenciaId;
    private String situacionesTipo1Json;
    private String situacionesTipo2Json;
    private String situacionesTipo3Json;
    private String plazosDebidoProcesoJson;

    public CrearManualBorradorRequest() {
    }

    public String getNombreVersion() {
        return nombreVersion;
    }

    public void setNombreVersion(String nombreVersion) {
        this.nombreVersion = nombreVersion;
    }

    public Long getAnioLectivoVigenciaId() {
        return anioLectivoVigenciaId;
    }

    public void setAnioLectivoVigenciaId(Long anioLectivoVigenciaId) {
        this.anioLectivoVigenciaId = anioLectivoVigenciaId;
    }

    public String getSituacionesTipo1Json() {
        return situacionesTipo1Json;
    }

    public void setSituacionesTipo1Json(String situacionesTipo1Json) {
        this.situacionesTipo1Json = situacionesTipo1Json;
    }

    public String getSituacionesTipo2Json() {
        return situacionesTipo2Json;
    }

    public void setSituacionesTipo2Json(String situacionesTipo2Json) {
        this.situacionesTipo2Json = situacionesTipo2Json;
    }

    public String getSituacionesTipo3Json() {
        return situacionesTipo3Json;
    }

    public void setSituacionesTipo3Json(String situacionesTipo3Json) {
        this.situacionesTipo3Json = situacionesTipo3Json;
    }

    public String getPlazosDebidoProcesoJson() {
        return plazosDebidoProcesoJson;
    }

    public void setPlazosDebidoProcesoJson(String plazosDebidoProcesoJson) {
        this.plazosDebidoProcesoJson = plazosDebidoProcesoJson;
    }
}
