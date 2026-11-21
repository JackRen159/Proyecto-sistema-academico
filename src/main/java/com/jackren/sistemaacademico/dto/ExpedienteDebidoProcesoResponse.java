package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.EstadoExpedienteDebidoProceso;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record ExpedienteDebidoProcesoResponse(
    Long id,
    String numeroExpediente,
    Long casoConvivenciaId,
    String codigoCaso,
    Long estudianteId,
    String nombreEstudiante,
    EstadoExpedienteDebidoProceso estadoExpediente,
    Long instructorUsuarioId,
    String nombreInstructor,
    String autoAperturaHechos,
    String normasManualInfringidas,
    String pruebasTrasladadasAnonimizadas,
    LocalDateTime fechaNotificacionCargos,
    Integer diasHabilesDescargos,
    LocalDate fechaLimiteDescargos,
    String constanciaVencimientoDescargos,
    Boolean acompanamientoPersonero,
    String conceptoOrientacion,
    List<DescargoDebidoProcesoResponse> descargos
) {}
