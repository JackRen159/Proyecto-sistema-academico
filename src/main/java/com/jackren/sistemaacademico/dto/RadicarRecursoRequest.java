package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.TipoRecursoImpugnacion;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RadicarRecursoRequest(
    @NotNull(message = "El tipo de recurso es obligatorio")
    TipoRecursoImpugnacion tipoRecurso,

    @NotBlank(message = "La sustentación y argumentos de inconformidad son obligatorios")
    String sustentacionRecurso,

    String pruebasAdjuntas
) {}
