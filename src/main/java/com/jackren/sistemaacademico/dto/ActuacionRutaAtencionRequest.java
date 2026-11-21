package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.TipoActuacionRutaAtencion;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ActuacionRutaAtencionRequest(
    @NotNull(message = "El tipo de actuación es obligatorio")
    TipoActuacionRutaAtencion tipoActuacion,

    @NotBlank(message = "La descripción de la actuación es obligatoria")
    String descripcionActuacion,

    LocalDate fechaVencimientoSeguimiento,

    String compromisosAcordados
) {}
