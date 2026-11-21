package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.NivelConfidencialidadDocumental;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CargarDocumentoRequest(
    @NotBlank(message = "El título del documento es obligatorio")
    String titulo,

    String descripcion,

    @NotBlank(message = "El tipo de documento es obligatorio")
    String tipoDocumento,

    @NotNull(message = "El nivel de confidencialidad es obligatorio")
    NivelConfidencialidadDocumental nivelConfidencialidad,

    Long estudianteId,
    Long matriculaId,
    Long anioLectivoId,
    Long trdId,

    @NotBlank(message = "El nombre del archivo original es obligatorio")
    String nombreArchivoOriginal,

    @NotBlank(message = "El tipo MIME es obligatorio")
    String mimeType,

    Long tamanoBytes,
    String rutaAlmacenamiento,
    String contenidoBase64
) {}
