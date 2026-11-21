package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.EstadoRecursoImpugnacion;
import com.jackren.sistemaacademico.entity.TipoRecursoImpugnacion;

import java.time.LocalDateTime;

public record RecursoImpugnacionResponse(
    Long id,
    Long medidaSancionId,
    TipoRecursoImpugnacion tipoRecurso,
    Long recurrenteUsuarioId,
    String nombreRecurrente,
    String sustentacionRecurso,
    String pruebasAdjuntas,
    LocalDateTime fechaRadicacion,
    EstadoRecursoImpugnacion estadoRecurso,
    Long resolutorUsuarioId,
    String nombreResolutor,
    String resolutorRol,
    String actaConsejoDirectivoResolucion,
    String motivoResolucionRecurso,
    LocalDateTime fechaResolucion
) {}
