package com.jackren.sistemaacademico.dto;

import jakarta.validation.constraints.NotBlank;

public record AdendaObservadorRequest(
    @NotBlank(message = "El motivo de la adenda es obligatorio")
    String motivoAdenda,

    @NotBlank(message = "El contenido de la adenda es obligatorio")
    String contenidoAdenda
) {}
