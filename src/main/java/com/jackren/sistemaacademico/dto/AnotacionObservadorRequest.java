package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.TipoAnotacionObservador;
import com.jackren.sistemaacademico.entity.TipoSituacionConvivencia;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;

public record AnotacionObservadorRequest(
    @NotNull(message = "El ID del estudiante es obligatorio")
    Long estudianteId,

    Long periodoAcademicoId,

    @NotNull(message = "El tipo de anotación es obligatorio")
    TipoAnotacionObservador tipoAnotacion,

    @NotNull(message = "La fecha del hecho es obligatoria")
    LocalDate fechaHecho,

    LocalTime horaHecho,

    @NotBlank(message = "El lugar del hecho es obligatorio")
    @Size(max = 150, message = "El lugar no puede superar 150 caracteres")
    String lugarHecho,

    @NotBlank(message = "La descripción fáctica es obligatoria")
    String descripcionFactica,

    String accionesPedagogicas,

    Boolean esZonaComun,

    Boolean requiereActivacionRuta,

    TipoSituacionConvivencia tipoSituacionSugerida
) {}
