package com.jackren.sistemaacademico.dto;

import java.math.BigDecimal;
import java.util.List;

public record ReporteRendimientoMortalidadResponse(
    Long anioLectivoId,
    Integer anio,
    BigDecimal promedioGeneralInstitucional,
    BigDecimal tasaMortalidadGlobalPorcentaje,
    List<ItemMortalidadAsignaturaDTO> asignaturasCriticas
) {}
