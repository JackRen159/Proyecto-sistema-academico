package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.EstadoCicloVidaDocumental;
import com.jackren.sistemaacademico.entity.NivelConfidencialidadDocumental;

import java.time.LocalDateTime;

public record RepositorioDocumentoResponse(
    Long id,
    String codigoDocumento,
    String titulo,
    String descripcion,
    String tipoDocumento,
    NivelConfidencialidadDocumental nivelConfidencialidad,
    EstadoCicloVidaDocumental estadoCicloVida,
    Long estudianteId,
    String nombreEstudiante,
    Long matriculaId,
    Long anioLectivoId,
    Long trdId,
    Integer versionActual,
    String hashSha256Actual,
    String nombreArchivoOriginal,
    String mimeType,
    Long tamanoBytes,
    Long usuarioCreadorId,
    String nombreCreador,
    LocalDateTime fechaCreacion,
    LocalDateTime fechaPublicacion,
    String motivoAnulacion,
    String enlaceDescargaEfimero
) {}
