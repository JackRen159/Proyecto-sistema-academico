package com.jackren.sistemaacademico.dto;

import jakarta.validation.constraints.NotBlank;

public record ResolverRecursoRequest(
    @NotBlank(message = "El sentido del fallo (CONFIRMAR_DECISION, MODIFICAR_ATENUAR_MEDIDA, REVOCAR_ARCHIVAR) es obligatorio")
    String sentidoFallo,

    @NotBlank(message = "El motivo de la resolución del recurso es obligatorio")
    String motivoResolucion,

    String actaConsejoDirectivo
) {}
