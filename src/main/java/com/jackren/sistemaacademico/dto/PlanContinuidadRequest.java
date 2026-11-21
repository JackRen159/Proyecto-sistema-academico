package com.jackren.sistemaacademico.dto;

import jakarta.validation.constraints.NotBlank;

public record PlanContinuidadRequest(
    @NotBlank(message = "La descripción de guías y talleres equivalentes es obligatoria")
    String guiasTalleresDescripcion,

    @NotBlank(message = "El mecanismo de evaluación pedagógica sin notas punitivas es obligatorio")
    String mecanismoEvaluacion,

    Long docenteTutorSeguimientoId
) {}
