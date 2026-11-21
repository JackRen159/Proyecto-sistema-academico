package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.SeveridadAlertaSeguridad;
import com.jackren.sistemaacademico.entity.TipoAlertaSeguridad;

import java.time.LocalDateTime;

public record AlertaSeguridadResponse(
    Long id,
    TipoAlertaSeguridad tipoAlerta,
    SeveridadAlertaSeguridad severidad,
    String descripcionEvento,
    Long usuarioInvolucradoId,
    String usernameInvolucrado,
    String ipOrigen,
    String correlationId,
    Boolean leidaPorRector,
    LocalDateTime fechaAlerta
) {}
