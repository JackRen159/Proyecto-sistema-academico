package com.jackren.sistemaacademico.dto;

public record ReporteConvivenciaSiuceResponse(
    Long anioLectivoId,
    Integer anio,
    int totalCasos,
    int casosTipoI,
    int casosTipoII,
    int casosTipoIII,
    int remitidosAutoridadExterna,
    int casosCerradosSuperados,
    int casosEnSeguimiento
) {}
