package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.TipoMedidaSancion;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record MedidaSancionRequest(
    @NotNull(message = "El ID del expediente de debido proceso es obligatorio")
    Long expedienteId,

    @NotNull(message = "El tipo de medida o sanción es obligatorio")
    TipoMedidaSancion tipoMedida,

    Boolean esSancionDisciplinaria,

    @NotBlank(message = "La motivación fáctica y normativa es obligatoria")
    String motivacionHechosNormas,

    @NotBlank(message = "El análisis de proporcionalidad es obligatorio")
    String analisisProporcionalidad,

    Integer diasSuspension,

    LocalDate fechaInicioSuspension,

    LocalDate fechaFinSuspension,

    String actaConsejoDirectivo,

    LocalDate fechaActaConsejoDirectivo,

    PlanContinuidadRequest planContinuidad
) {}
