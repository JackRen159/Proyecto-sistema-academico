package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.EstadoCasoConvivencia;
import com.jackren.sistemaacademico.entity.TipoSituacionConvivencia;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record CasoConvivenciaResponse(
    Long id,
    String codigoExpediente,
    Long anioLectivoId,
    Long estudianteImplicadoId,
    String nombreEstudiante,
    TipoSituacionConvivencia tipoSituacion,
    EstadoCasoConvivencia estadoCaso,
    LocalDate fechaHechos,
    String lugarHechos,
    String descripcionSituacion,
    Long reportanteUsuarioId,
    String nombreReportante,
    Boolean esReporteConfidencial,
    Boolean atencionMedicaUrgente,
    String detalleAtencionMedica,
    Boolean reservaProteccionContraAcudiente,
    Long acudienteAgresorId,
    String codigoSiuce,
    Boolean remitidoAutoridadExterna,
    String autoridadExternaDetalle,
    LocalDate fechaRemisionExterna,
    String radicadoRemisionExterna,
    LocalDateTime fechaApertura,
    LocalDateTime fechaCierre,
    String motivoCierre
) {}
