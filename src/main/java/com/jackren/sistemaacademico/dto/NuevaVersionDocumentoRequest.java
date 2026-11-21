package com.jackren.sistemaacademico.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record NuevaVersionDocumentoRequest(
    @NotBlank(message = "El nombre del archivo es obligatorio")
    String nombreArchivo,

    @NotBlank(message = "El tipo MIME es obligatorio")
    String mimeType,

    @NotNull(message = "El tamaño en bytes es obligatorio")
    Long tamanoBytes,

    String rutaAlmacenamiento,

    @NotBlank(message = "El motivo de la nueva versión es obligatorio")
    String motivoVersion,

    String contenidoBase64
) {}
