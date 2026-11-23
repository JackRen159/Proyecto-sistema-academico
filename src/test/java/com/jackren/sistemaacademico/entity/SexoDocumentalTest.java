package com.jackren.sistemaacademico.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Pruebas Unitarias - Enum SexoDocumental y Deserialización Flexible")
class SexoDocumentalTest {

    @Test
    @DisplayName("Debe deserializar 'M', 'm' y 'MASCULINO' como SexoDocumental.MASCULINO")
    void testDeserializacionMasculino() {
        assertEquals(SexoDocumental.MASCULINO, SexoDocumental.fromString("M"));
        assertEquals(SexoDocumental.MASCULINO, SexoDocumental.fromString("m"));
        assertEquals(SexoDocumental.MASCULINO, SexoDocumental.fromString("MASCULINO"));
        assertEquals(SexoDocumental.MASCULINO, SexoDocumental.fromString("  masculino  "));
    }

    @Test
    @DisplayName("Debe deserializar 'F', 'f' y 'FEMENINO' como SexoDocumental.FEMENINO")
    void testDeserializacionFemenino() {
        assertEquals(SexoDocumental.FEMENINO, SexoDocumental.fromString("F"));
        assertEquals(SexoDocumental.FEMENINO, SexoDocumental.fromString("f"));
        assertEquals(SexoDocumental.FEMENINO, SexoDocumental.fromString("FEMENINO"));
        assertEquals(SexoDocumental.FEMENINO, SexoDocumental.fromString("  femenino  "));
    }

    @Test
    @DisplayName("Debe deserializar 'OTRO' como SexoDocumental.OTRO")
    void testDeserializacionOtro() {
        assertEquals(SexoDocumental.OTRO, SexoDocumental.fromString("OTRO"));
        assertEquals(SexoDocumental.OTRO, SexoDocumental.fromString("otro"));
    }

    @Test
    @DisplayName("Debe resolver valores nulos, vacíos o desconocidos como NO_ESPECIFICADO")
    void testDeserializacionNoEspecificado() {
        assertEquals(SexoDocumental.NO_ESPECIFICADO, SexoDocumental.fromString(null));
        assertEquals(SexoDocumental.NO_ESPECIFICADO, SexoDocumental.fromString(""));
        assertEquals(SexoDocumental.NO_ESPECIFICADO, SexoDocumental.fromString("   "));
        assertEquals(SexoDocumental.NO_ESPECIFICADO, SexoDocumental.fromString("X"));
    }
}
