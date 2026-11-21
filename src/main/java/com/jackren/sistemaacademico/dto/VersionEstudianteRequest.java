package com.jackren.sistemaacademico.dto;

import jakarta.validation.constraints.NotBlank;

public record VersionEstudianteRequest(
    @NotBlank(message = "La versión libre o compromiso formativo es obligatorio")
    String versionLibre
) {}
