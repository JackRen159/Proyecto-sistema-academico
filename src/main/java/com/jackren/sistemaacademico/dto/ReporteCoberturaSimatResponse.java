package com.jackren.sistemaacademico.dto;

import java.time.LocalDate;
import java.util.List;

public record ReporteCoberturaSimatResponse(
    Long anioLectivoId,
    Integer anio,
    LocalDate fechaCorte,
    int totalMatriculados,
    int totalCupos,
    List<ItemCoberturaGradoDTO> grados
) {}
