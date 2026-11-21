package com.jackren.sistemaacademico.dto;

public record ItemCoberturaGradoDTO(
    Long gradoId,
    String nombreGrado,
    int cuposTotales,
    int matriculados,
    int cuposDisponibles,
    int masculino,
    int femenino
) {}
