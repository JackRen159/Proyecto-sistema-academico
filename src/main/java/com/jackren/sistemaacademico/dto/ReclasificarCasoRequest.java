package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.TipoSituacionConvivencia;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReclasificarCasoRequest(
    @NotNull(message = "La nueva clasificación es obligatoria")
    TipoSituacionConvivencia nuevaSituacion,

    @NotBlank(message = "El motivo de la reclasificación es obligatorio")
    String motivoReclasificacion
) {}
