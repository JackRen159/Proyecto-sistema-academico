package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.EstadoDecisionSancion;
import com.jackren.sistemaacademico.entity.TipoMedidaSancion;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record MedidaSancionResponse(
    Long id,
    Long expedienteId,
    String numeroResolucion,
    TipoMedidaSancion tipoMedida,
    Boolean esSancionDisciplinaria,
    String motivacionHechosNormas,
    String analisisProporcionalidad,
    Integer diasSuspension,
    LocalDate fechaInicioSuspension,
    LocalDate fechaFinSuspension,
    String actaConsejoDirectivo,
    LocalDate fechaActaConsejoDirectivo,
    Long autoridadFirmanteId,
    String nombreFirmante,
    String rolFirmante,
    EstadoDecisionSancion estadoDecision,
    LocalDateTime fechaEmision,
    LocalDateTime fechaNotificacion,
    LocalDateTime fechaFirmeza,
    PlanContinuidadResponse planContinuidad
) {}
