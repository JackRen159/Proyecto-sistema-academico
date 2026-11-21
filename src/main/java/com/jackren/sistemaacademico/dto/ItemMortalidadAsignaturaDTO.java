package com.jackren.sistemaacademico.dto;

import java.math.BigDecimal;

public record ItemMortalidadAsignaturaDTO(
    Long asignaturaId,
    String nombreAsignatura,
    String area,
    int estudiantesEvaluados,
    int reprobados,
    BigDecimal tasaReprobacionPorcentaje
) {}
