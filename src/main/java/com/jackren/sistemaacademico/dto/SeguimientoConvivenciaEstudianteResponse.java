package com.jackren.sistemaacademico.dto;

import java.util.List;

public record SeguimientoConvivenciaEstudianteResponse(
    Long estudianteId,
    String nombreEstudiante,
    List<AnotacionObservadorResponse> anotaciones,
    List<CasoConvivenciaResponse> casos,
    List<ExpedienteDebidoProcesoResponse> expedientesDebidoProceso,
    List<MedidaSancionResponse> medidasSanciones,
    List<PlanContinuidadResponse> planesContinuidad
) {}
