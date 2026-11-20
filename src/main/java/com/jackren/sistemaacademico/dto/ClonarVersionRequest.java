package com.jackren.sistemaacademico.dto;

import jakarta.validation.constraints.NotBlank;

public class ClonarVersionRequest {

    @NotBlank(message = "El nombre de la nueva versión es obligatorio")
    private String nuevoNombreVersion;

    private Long anioLectivoVigenciaId;

    public ClonarVersionRequest() {
    }

    public ClonarVersionRequest(String nuevoNombreVersion, Long anioLectivoVigenciaId) {
        this.nuevoNombreVersion = nuevoNombreVersion;
        this.anioLectivoVigenciaId = anioLectivoVigenciaId;
    }

    public String getNuevoNombreVersion() {
        return nuevoNombreVersion;
    }

    public void setNuevoNombreVersion(String nuevoNombreVersion) {
        this.nuevoNombreVersion = nuevoNombreVersion;
    }

    public Long getAnioLectivoVigenciaId() {
        return anioLectivoVigenciaId;
    }

    public void setAnioLectivoVigenciaId(Long anioLectivoVigenciaId) {
        this.anioLectivoVigenciaId = anioLectivoVigenciaId;
    }
}
