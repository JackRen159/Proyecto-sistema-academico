package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.Jornada;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class GrupoRequest {

    @NotNull(message = "El ID del año lectivo es obligatorio")
    private Long anioLectivoId;

    @NotNull(message = "El ID del grado es obligatorio")
    private Long gradoId;

    @NotBlank(message = "El nombre o letra del grupo es obligatorio (ej: A, B, 01)")
    @Size(max = 20, message = "El nombre del grupo no puede superar los 20 caracteres")
    private String nombre;

    @NotNull(message = "La jornada es obligatoria")
    private Jornada jornada = Jornada.MANANA;

    @NotNull(message = "El cupo máximo es obligatorio")
    @Min(value = 1, message = "El cupo máximo debe ser al menos 1")
    private Integer cupoMaximo = 35;

    public GrupoRequest() {
    }

    public GrupoRequest(Long anioLectivoId, Long gradoId, String nombre, Jornada jornada, Integer cupoMaximo) {
        this.anioLectivoId = anioLectivoId;
        this.gradoId = gradoId;
        this.nombre = nombre;
        this.jornada = jornada != null ? jornada : Jornada.MANANA;
        this.cupoMaximo = cupoMaximo != null ? cupoMaximo : 35;
    }

    public Long getAnioLectivoId() {
        return anioLectivoId;
    }

    public void setAnioLectivoId(Long anioLectivoId) {
        this.anioLectivoId = anioLectivoId;
    }

    public Long getGradoId() {
        return gradoId;
    }

    public void setGradoId(Long gradoId) {
        this.gradoId = gradoId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Jornada getJornada() {
        return jornada;
    }

    public void setJornada(Jornada jornada) {
        this.jornada = jornada;
    }

    public Integer getCupoMaximo() {
        return cupoMaximo;
    }

    public void setCupoMaximo(Integer cupoMaximo) {
        this.cupoMaximo = cupoMaximo;
    }
}
