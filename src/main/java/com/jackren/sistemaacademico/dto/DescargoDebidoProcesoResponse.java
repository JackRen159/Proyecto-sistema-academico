package com.jackren.sistemaacademico.dto;

import java.time.LocalDateTime;

public record DescargoDebidoProcesoResponse(
    Long id,
    Long expedienteId,
    Long presentadoPorUsuarioId,
    String nombrePresentador,
    String contenidoDescargos,
    String pruebasAportadas,
    LocalDateTime fechaRadicacion,
    Boolean esVersionLibreAudiencia,
    Boolean participoPersonero
) {}
