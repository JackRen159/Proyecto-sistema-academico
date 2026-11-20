package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.DiaSemana;
import jakarta.validation.constraints.NotNull;

public class HorarioBloqueRequest {

    @NotNull(message = "La asignación académica es obligatoria")
    private Long asignacionAcademicaId;

    @NotNull(message = "El aula es obligatoria")
    private Long aulaId;

    @NotNull(message = "El día de la semana es obligatorio")
    private DiaSemana diaSemana;

    @NotNull(message = "La franja horaria es obligatoria")
    private Long franjaHorariaId;

    public HorarioBloqueRequest() {
    }

    public HorarioBloqueRequest(Long asignacionAcademicaId, Long aulaId, DiaSemana diaSemana, Long franjaHorariaId) {
        this.asignacionAcademicaId = asignacionAcademicaId;
        this.aulaId = aulaId;
        this.diaSemana = diaSemana;
        this.franjaHorariaId = franjaHorariaId;
    }

    public Long getAsignacionAcademicaId() {
        return asignacionAcademicaId;
    }

    public void setAsignacionAcademicaId(Long asignacionAcademicaId) {
        this.asignacionAcademicaId = asignacionAcademicaId;
    }

    public Long getAulaId() {
        return aulaId;
    }

    public void setAulaId(Long aulaId) {
        this.aulaId = aulaId;
    }

    public DiaSemana getDiaSemana() {
        return diaSemana;
    }

    public void setDiaSemana(DiaSemana diaSemana) {
        this.diaSemana = diaSemana;
    }

    public Long getFranjaHorariaId() {
        return franjaHorariaId;
    }

    public void setFranjaHorariaId(Long franjaHorariaId) {
        this.franjaHorariaId = franjaHorariaId;
    }
}
