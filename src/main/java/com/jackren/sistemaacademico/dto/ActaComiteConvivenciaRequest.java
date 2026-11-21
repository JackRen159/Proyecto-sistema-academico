package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.TipoSesionComiteConvivencia;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;

public record ActaComiteConvivenciaRequest(
    @NotNull(message = "El ID del año lectivo es obligatorio")
    Long anioLectivoId,

    @NotBlank(message = "El número de acta es obligatorio")
    @Size(max = 60, message = "El número de acta no puede superar 60 caracteres")
    String numeroActa,

    @NotNull(message = "La fecha de la sesión es obligatoria")
    LocalDate fechaSesion,

    @NotNull(message = "La hora de inicio es obligatoria")
    LocalTime horaInicio,

    LocalTime horaFin,

    @NotBlank(message = "El lugar de la sesión es obligatorio")
    @Size(max = 150, message = "El lugar no puede superar 150 caracteres")
    String lugarSesion,

    TipoSesionComiteConvivencia tipoSesion,

    @NotBlank(message = "Los integrantes nominativos asistentes son obligatorios")
    String integrantesAsistentesJson,

    String impedimentosDeclaradosJson,

    @NotBlank(message = "Los casos analizados son obligatorios")
    String casosAnalizadosJson,

    @NotBlank(message = "Los acuerdos y compromisos son obligatorios")
    String acuerdosCompromisosJson,

    String accionesPrevencionPromocion,

    @NotNull(message = "El ID del usuario secretario es obligatorio")
    Long usuarioSecretarioId,

    @NotNull(message = "El ID del usuario presidente es obligatorio")
    Long usuarioPresidenteId
) {}
