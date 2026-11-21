package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.TipoSesionComiteConvivencia;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record ActaComiteConvivenciaResponse(
    Long id,
    Long anioLectivoId,
    String numeroActa,
    LocalDate fechaSesion,
    LocalTime horaInicio,
    LocalTime horaFin,
    String lugarSesion,
    TipoSesionComiteConvivencia tipoSesion,
    String integrantesAsistentesJson,
    String impedimentosDeclaradosJson,
    String casosAnalizadosJson,
    String acuerdosCompromisosJson,
    String accionesPrevencionPromocion,
    Long usuarioSecretarioId,
    String nombreSecretario,
    Long usuarioPresidenteId,
    String nombrePresidente,
    LocalDateTime fechaRegistro
) {}
