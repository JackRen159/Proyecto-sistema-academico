package com.jackren.sistemaacademico.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record PlanContinuidadResponse(
    Long id,
    Long medidaSancionId,
    Long estudianteId,
    String nombreEstudiante,
    LocalDate fechaInicio,
    LocalDate fechaFin,
    String guiasTalleresDescripcion,
    String mecanismoEvaluacion,
    Long docenteTutorSeguimientoId,
    String nombreTutor,
    Boolean cumplidoSatisfactoriamente,
    String observacionesEvaluacion,
    LocalDateTime fechaRegistro
) {}
