package com.jackren.sistemaacademico.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Unitarias de Entidades y Dominio - Documentos y Reportes Académicos")
class DocumentoEntityTest {

    @Test
    @DisplayName("ConsecutivoDocumento debe incrementar y formatear correctamente con prefijo y longitud de relleno")
    void consecutivoDocumento_IncrementoYFormateo() {
        AnioLectivo anio = new AnioLectivo();
        anio.setAnio(2026);

        ConsecutivoDocumento consecutivo = new ConsecutivoDocumento(
                anio,
                TipoDocumentoAcademico.CERTIFICADO_ESTUDIO,
                "CERT",
                0L,
                5,
                "Certificado válido con firma digital"
        );

        String primerNumero = consecutivo.incrementarYFormatear(2026);
        assertEquals(1L, consecutivo.getUltimoNumero());
        assertEquals("CERT-2026-00001", primerNumero);

        String segundoNumero = consecutivo.incrementarYFormatear(2026);
        assertEquals(2L, consecutivo.getUltimoNumero());
        assertEquals("CERT-2026-00002", segundoNumero);
    }

    @Test
    @DisplayName("ConsecutivoDocumento sin prefijo debe formatear año y número correlativo")
    void consecutivoDocumento_SinPrefijo() {
        AnioLectivo anio = new AnioLectivo();
        anio.setAnio(2026);

        ConsecutivoDocumento consecutivo = new ConsecutivoDocumento(
                anio,
                TipoDocumentoAcademico.BOLETIN_PERIODO,
                "",
                41L,
                4,
                null
        );

        String numero = consecutivo.incrementarYFormatear(2026);
        assertEquals(42L, consecutivo.getUltimoNumero());
        assertEquals("2026-0042", numero);
    }

    @Test
    @DisplayName("RegistroEmisionDocumento debe crearse activo y anularse con trazabilidad")
    void registroEmisionDocumento_CreacionYAnulacion() {
        AnioLectivo anio = new AnioLectivo();
        anio.setAnio(2026);

        Estudiante estudiante = new Estudiante();
        estudiante.setId(10L);

        Usuario usuarioEmisor = new Usuario();
        usuarioEmisor.setId(1L);

        Usuario usuarioAnulador = new Usuario();
        usuarioAnulador.setId(2L);

        String codigoVerificacion = UUID.randomUUID().toString();

        RegistroEmisionDocumento emision = new RegistroEmisionDocumento(
                null,
                TipoDocumentoAcademico.CONSTANCIA_MATRICULA,
                "CONST-2026-00015",
                anio,
                estudiante,
                null,
                null,
                null,
                codigoVerificacion,
                usuarioEmisor,
                "A quien interese",
                "Trámite de subsidio familiar",
                "{\"grado\":\"Sexto\",\"jornada\":\"MAÑANA\"}"
        );

        assertFalse(emision.getAnulado());
        assertNull(emision.getMotivoAnulacion());
        assertNull(emision.getFechaAnulacion());
        assertNull(emision.getUsuarioAnulador());
        assertEquals("CONST-2026-00015", emision.getNumeroConsecutivoCompleto());
        assertEquals(codigoVerificacion, emision.getCodigoVerificacion());

        // Anular emisión
        emision.anular("Error en destinatario solicitado por el acudiente", usuarioAnulador);

        assertTrue(emision.getAnulado());
        assertEquals("Error en destinatario solicitado por el acudiente", emision.getMotivoAnulacion());
        assertEquals(usuarioAnulador, emision.getUsuarioAnulador());
        assertNotNull(emision.getFechaAnulacion());
    }
}
