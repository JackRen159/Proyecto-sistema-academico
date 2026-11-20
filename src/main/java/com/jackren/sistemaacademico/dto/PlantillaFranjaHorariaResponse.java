package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.Jornada;
import com.jackren.sistemaacademico.entity.TipoFranjaHoraria;

import java.time.LocalTime;

public class PlantillaFranjaHorariaResponse {

    private Long id;
    private Long anioLectivoId;
    private Integer anioLectivo;
    private Jornada jornada;
    private Integer numeroBloque;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private TipoFranjaHoraria tipoFranja;
    private String descripcion;

    public PlantillaFranjaHorariaResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAnioLectivoId() {
        return anioLectivoId;
    }

    public void setAnioLectivoId(Long anioLectivoId) {
        this.anioLectivoId = anioLectivoId;
    }

    public Integer getAnioLectivo() {
        return anioLectivo;
    }

    public void setAnioLectivo(Integer anioLectivo) {
        this.anioLectivo = anioLectivo;
    }

    public Jornada getJornada() {
        return jornada;
    }

    public void setJornada(Jornada jornada) {
        this.jornada = jornada;
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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
