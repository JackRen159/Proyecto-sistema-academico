package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.TipoSituacionConvivencia;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CasoConvivenciaRequest(
    @NotNull(message = "El ID del año lectivo es obligatorio")
    Long anioLectivoId,

    @NotNull(message = "El ID del estudiante implicado es obligatorio")
    Long estudianteImplicadoId,

    @NotNull(message = "El tipo de situación es obligatorio")
    TipoSituacionConvivencia tipoSituacion,

    @NotNull(message = "La fecha de los hechos es obligatoria")
    LocalDate fechaHechos,

    @NotBlank(message = "El lugar de los hechos es obligatorio")
    @Size(max = 150, message = "El lugar no puede superar 150 caracteres")
    String lugarHechos,

    @NotBlank(message = "La descripción de la situación es obligatoria")
    String descripcionSituacion,

    Boolean esReporteConfidencial,

    Boolean atencionMedicaUrgente,

    String detalleAtencionMedica,

    Boolean reservaProteccionContraAcudiente,

    Long acudienteAgresorId,

    Boolean remitidoAutoridadExterna,

    String autoridadExternaDetalle,

    LocalDate fechaRemisionExterna,

    String radicadoRemisionExterna
) {}
