package com.jackren.sistemaacademico.dto;

import java.util.ArrayList;
import java.util.List;

public class AgendaDocenteResponse {

    private Long profesorId;
    private String profesorNombreCompleto;
    private String documento;
    private Long totalBloquesSemanales;
    private List<HorarioBloqueResponse> bloques = new ArrayList<>();

    public AgendaDocenteResponse() {
    }

    public Long getProfesorId() {
        return profesorId;
    }

    public void setProfesorId(Long profesorId) {
        this.profesorId = profesorId;
    }

    public String getProfesorNombreCompleto() {
        return profesorNombreCompleto;
    }

    public void setProfesorNombreCompleto(String profesorNombreCompleto) {
        this.profesorNombreCompleto = profesorNombreCompleto;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public Long getTotalBloquesSemanales() {
        return totalBloquesSemanales;
    }

    public void setTotalBloquesSemanales(Long totalBloquesSemanales) {
        this.totalBloquesSemanales = totalBloquesSemanales;
    }

    public List<HorarioBloqueResponse> getBloques() {
        return bloques;
    }

    public void setBloques(List<HorarioBloqueResponse> bloques) {
        this.bloques = bloques;
    }
}
