package com.jackren.sistemaacademico.dto;

import java.time.LocalDateTime;

public record EnlaceDescargaResponse(
    String codigoDocumento,
    String urlFirmada,
    LocalDateTime fechaExpiracion
) {}
