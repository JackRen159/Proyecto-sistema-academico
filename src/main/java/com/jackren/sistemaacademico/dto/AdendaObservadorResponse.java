package com.jackren.sistemaacademico.dto;

import java.time.LocalDateTime;

public record AdendaObservadorResponse(
    Long id,
    Long anotacionId,
    Long autorUsuarioId,
    String nombreAutor,
    String motivoAdenda,
    String contenidoAdenda,
    LocalDateTime fechaAdenda
) {}
