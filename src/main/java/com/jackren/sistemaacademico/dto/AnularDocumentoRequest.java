package com.jackren.sistemaacademico.dto;

import jakarta.validation.constraints.NotBlank;

public record AnularDocumentoRequest(
    @NotBlank(message = "El motivo de la anulación es obligatorio")
    String motivoAnulacion
) {}
