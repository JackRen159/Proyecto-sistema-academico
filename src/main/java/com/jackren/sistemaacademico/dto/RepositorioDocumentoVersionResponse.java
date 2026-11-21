package com.jackren.sistemaacademico.dto;

import java.time.LocalDateTime;

public record RepositorioDocumentoVersionResponse(
    Long id,
    Long documentoId,
    Integer numeroVersion,
    String hashSha256,
    String nombreArchivo,
    String mimeType,
    Long tamanoBytes,
    String motivoVersion,
    Long usuarioAutorId,
    String nombreAutor,
    LocalDateTime fechaRegistro
) {}
