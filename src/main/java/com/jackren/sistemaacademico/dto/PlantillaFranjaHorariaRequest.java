package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.Jornada;
import com.jackren.sistemaacademico.entity.TipoFranjaHoraria;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalTime;

public class PlantillaFranjaHorariaRequest {

    @NotNull(message = "El año lectivo es obligatorio")
    private Long anioLectivoId;

    @NotNull(message = "La jornada es obligatoria")
    private Jornada jornada;

    @NotNull(message = "El número de bloque es obligatorio")
    @Min(value = 1, message = "El bloque debe ser mayor o igual a 1")
    private Integer numeroBloque;

    @NotNull(message = "La hora de inicio es obligatoria")
    private LocalTime horaInicio;

    @NotNull(message = "La hora de fin es obligatoria")
    private LocalTime horaFin;

    private TipoFranjaHoraria tipoFranja = TipoFranjaHoraria.BLOQUE_LECTIVO;

    @Size(max = 100, message = "La descripción no puede superar 100 caracteres")
    private String descripcion;

    public PlantillaFranjaHorariaRequest() {
    }

    public Long getAnioLectivoId() {
        return anioLectivoId;
    }

    public void setAnioLectivoId(Long anioLectivoId) {
        this.anioLectivoId = anioLectivoId;
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
