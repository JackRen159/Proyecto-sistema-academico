package com.jackren.sistemaacademico.entity;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum SexoDocumental {
    MASCULINO,
    FEMENINO,
    OTRO,
    NO_ESPECIFICADO;

    @JsonCreator
    public static SexoDocumental fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return NO_ESPECIFICADO;
        }
        String clean = value.trim().toUpperCase();
        if ("M".equals(clean) || "MASCULINO".equals(clean)) {
            return MASCULINO;
        }
        if ("F".equals(clean) || "FEMENINO".equals(clean)) {
            return FEMENINO;
        }
        if ("OTRO".equals(clean)) {
            return OTRO;
        }
        return NO_ESPECIFICADO;
    }
}
