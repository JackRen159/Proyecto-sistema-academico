package com.jackren.sistemaacademico.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AperturaCargosRequest(
    @NotNull(message = "El ID del caso de convivencia es obligatorio")
    Long casoConvivenciaId,

    @NotNull(message = "El ID del estudiante es obligatorio")
    Long estudianteId,

    @NotBlank(message = "La relación precisa de hechos de tiempo, modo y lugar es obligatoria")
    String autoAperturaHechos,

    @NotBlank(message = "Las normas y artículos infringidos del Manual de Convivencia vigente son obligatorios")
    String normasManualInfringidas,

    @NotBlank(message = "Las pruebas trasladadas anonimizadas son obligatorias")
    String pruebasTrasladadasAnonimizadas,

    Integer diasHabilesDescargos,

    Boolean acompanamientoPersonero,

    String conceptoOrientacion
) {}
