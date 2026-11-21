package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.TipoActuacionRutaAtencion;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ActuacionRutaAtencionResponse(
    Long id,
    Long casoConvivenciaId,
    TipoActuacionRutaAtencion tipoActuacion,
    String descripcionActuacion,
    Long responsableUsuarioId,
    String nombreResponsable,
    LocalDateTime fechaActuacion,
    LocalDate fechaVencimientoSeguimiento,
    String compromisosAcordados
) {}
