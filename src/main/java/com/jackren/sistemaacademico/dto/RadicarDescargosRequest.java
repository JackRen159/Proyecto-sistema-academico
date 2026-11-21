package com.jackren.sistemaacademico.dto;

import jakarta.validation.constraints.NotBlank;

public record RadicarDescargosRequest(
    @NotBlank(message = "El contenido de los descargos es obligatorio")
    String contenidoDescargos,

    String pruebasAportadas,

    Boolean esVersionLibreAudiencia,

    Boolean participoPersonero
) {}
