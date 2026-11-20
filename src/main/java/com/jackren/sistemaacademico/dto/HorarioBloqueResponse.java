package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.DiaSemana;
import com.jackren.sistemaacademico.entity.TipoFranjaHoraria;

import java.time.LocalTime;

public class HorarioBloqueResponse {

    private Long id;
    private Long horarioVersionId;
    private Long asignacionAcademicaId;
    private Long profesorId;
    private String profesorNombreCompleto;
    private Long asignaturaId;
    private String asignaturaNombre;
    private Long aulaId;
    private String aulaCodigo;
    private String aulaNombre;
    private DiaSemana diaSemana;
    private Long franjaHorariaId;
    private Integer numeroBloque;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private TipoFranjaHoraria tipoFranja;

    public HorarioBloqueResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getHorarioVersionId() {
        return horarioVersionId;
    }

    public void setHorarioVersionId(Long horarioVersionId) {
        this.horarioVersionId = horarioVersionId;
    }

    public Long getAsignacionAcademicaId() {
        return asignacionAcademicaId;
    }

    public void setAsignacionAcademicaId(Long asignacionAcademicaId) {
        this.asignacionAcademicaId = asignacionAcademicaId;
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

    public Long getAsignaturaId() {
        return asignaturaId;
    }

    public void setAsignaturaId(Long asignaturaId) {
        this.asignaturaId = asignaturaId;
    }

    public String getAsignaturaNombre() {
        return asignaturaNombre;
    }

    public void setAsignaturaNombre(String asignaturaNombre) {
        this.asignaturaNombre = asignaturaNombre;
    }

    public Long getAulaId() {
        return aulaId;
    }

    public void setAulaId(Long aulaId) {
        this.aulaId = aulaId;
    }

    public String getAulaCodigo() {
        return aulaCodigo;
    }

    public void setAulaCodigo(String aulaCodigo) {
        this.aulaCodigo = aulaCodigo;
    }

    public String getAulaNombre() {
        return aulaNombre;
    }

    public void setAulaNombre(String aulaNombre) {
        this.aulaNombre = aulaNombre;
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

    public Integer getNumeroBloque() {
        return numeroBloque;
    }

    public void setNumeroBloque(Integer numeroBloque) {
        this.numeroBloque = numeroBloque;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(LocalTime horaFin) {
        this.horaFin = horaFin;
    }

    public TipoFranjaHoraria getTipoFranja() {
        return tipoFranja;
    }

    public void setTipoFranja(TipoFranjaHoraria tipoFranja) {
        this.tipoFranja = tipoFranja;
    }
}
