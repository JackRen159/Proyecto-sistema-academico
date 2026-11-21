package com.jackren.sistemaacademico.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Unitarias de Entidades y Dominio - Repositorio Documental, TRD y Auditoría V2")
class RepositorioYDocumentosEntityTest {

    @Test
    @DisplayName("TablaRetencionDocumental debe instanciarse con campos y disposición final")
    void trd_InstanciacionCorrecta() {
        TablaRetencionDocumental trd = new TablaRetencionDocumental();
        trd.setCodigoSerie("300-CALIFICACIONES");
        trd.setNombreSerie("Evaluación y Calificaciones");
        trd.setSubserie("Registros Escolares de Valoración");
        trd.setAniosRetencionGestion(5);
        trd.setAniosRetencionCentral(50);
        trd.setDisposicionFinal(DisposicionFinalTRD.CONSERVACION_TOTAL);

        assertEquals("300-CALIFICACIONES", trd.getCodigoSerie());
        assertEquals(5, trd.getAniosRetencionGestion());
        assertEquals(50, trd.getAniosRetencionCentral());
        assertEquals(DisposicionFinalTRD.CONSERVACION_TOTAL, trd.getDisposicionFinal());
        assertTrue(trd.getActivo());
    }

    @Test
    @DisplayName("RepositorioDocumento debe instanciarse con nivel de confidencialidad y hash SHA-256")
    void repositorioDocumento_InstanciacionCorrecta() {
        RepositorioDocumento doc = new RepositorioDocumento();
        doc.setCodigoDocumento("DOC-2026-0001");
        doc.setTitulo("Certificado Oficial de Estudio 2026");
        doc.setTipoDocumento("CERTIFICADO_ESTUDIO");
        doc.setNivelConfidencialidad(NivelConfidencialidadDocumental.ACADEMICO_ESTANDAR);
        doc.setEstadoCicloVida(EstadoCicloVidaDocumental.PUBLICADO_VIGENTE);
        doc.setHashSha256Actual("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855");
        doc.setRutaAlmacenamiento("/storage/docs/2026/DOC-2026-0001.pdf");
        doc.setNombreArchivoOriginal("Certificado_Juan_Perez.pdf");
        doc.setMimeType("application/pdf");
        doc.setTamanoBytes(102400L);

        assertEquals("DOC-2026-0001", doc.getCodigoDocumento());
        assertEquals(NivelConfidencialidadDocumental.ACADEMICO_ESTANDAR, doc.getNivelConfidencialidad());
        assertEquals(EstadoCicloVidaDocumental.PUBLICADO_VIGENTE, doc.getEstadoCicloVida());
        assertEquals(1, doc.getVersionActual());
        assertNotNull(doc.getHashSha256Actual());
    }

    @Test
    @DisplayName("RepositorioDocumentoVersion debe almacenar versionamiento inmutable")
    void repositorioVersion_InstanciacionCorrecta() {
        RepositorioDocumento doc = new RepositorioDocumento();
        doc.setId(10L);

        RepositorioDocumentoVersion ver = new RepositorioDocumentoVersion();
        ver.setDocumento(doc);
        ver.setNumeroVersion(2);
        ver.setHashSha256("c2e8b28...64chars");
        ver.setRutaAlmacenamiento("/storage/docs/2026/DOC-2026-0001_v2.pdf");
        ver.setNombreArchivo("Certificado_v2.pdf");
        ver.setMimeType("application/pdf");
        ver.setTamanoBytes(105000L);
        ver.setMotivoVersion("Regeneración tras rectificación oficial de nota");

        assertSame(doc, ver.getDocumento());
        assertEquals(2, ver.getNumeroVersion());
        assertEquals("Regeneración tras rectificación oficial de nota", ver.getMotivoVersion());
    }

    @Test
    @DisplayName("SolicitudDocumentoAcademico debe instanciarse con radicado y estado")
    void solicitudDocumento_InstanciacionCorrecta() {
        SolicitudDocumentoAcademico sol = new SolicitudDocumentoAcademico();
        sol.setNumeroRadicado("RAD-DOC-2026-005");
        sol.setRolSolicitante("ACUDIENTE");
        sol.setTipoDocumento(TipoDocumentoAcademico.CONSTANCIA_MATRICULA);
        sol.setMotivoSolicitud("Trámite para Caja de Compensación Familiar");
        sol.setEstadoSolicitud(EstadoSolicitudDocumento.RADICADA);

        assertEquals("RAD-DOC-2026-005", sol.getNumeroRadicado());
        assertEquals(TipoDocumentoAcademico.CONSTANCIA_MATRICULA, sol.getTipoDocumento());
        assertEquals(EstadoSolicitudDocumento.RADICADA, sol.getEstadoSolicitud());
    }

    @Test
    @DisplayName("LibroReglamentarioFolio debe instanciarse con tomo, folio y fecha de asiento")
    void libroReglamentario_InstanciacionCorrecta() {
        LibroReglamentarioFolio folio = new LibroReglamentarioFolio();
        folio.setTipoLibro(TipoLibroReglamentario.LIBRO_CALIFICACIONES_VALORACION);
        folio.setNumeroTomo(1);
        folio.setNumeroFolio(45);
        folio.setFechaAsiento(LocalDate.of(2026, 11, 30));
        folio.setResumenRegistroJson("{\"estudiante\":\"Juan Pérez\",\"promedio\":4.8,\"fallo\":\"PROMOVIDO\"}");

        assertEquals(TipoLibroReglamentario.LIBRO_CALIFICACIONES_VALORACION, folio.getTipoLibro());
        assertEquals(1, folio.getNumeroTomo());
        assertEquals(45, folio.getNumeroFolio());
        assertEquals(LocalDate.of(2026, 11, 30), folio.getFechaAsiento());
    }

    @Test
    @DisplayName("AlertaSeguridadAuditoria debe instanciarse con tipo y severidad")
    void alertaSeguridad_InstanciacionCorrecta() {
        AlertaSeguridadAuditoria alerta = new AlertaSeguridadAuditoria();
        alerta.setTipoAlerta(TipoAlertaSeguridad.ACCESO_ACUDIENTE_BLOQUEADO_LEGAL);
        alerta.setSeveridad(SeveridadAlertaSeguridad.CRITICA);
        alerta.setDescripcionEvento("Intento de acceso a expediente por adulto con orden de restricción legal");
        alerta.setCorrelationId("CORR-SEC-9988");

        assertEquals(TipoAlertaSeguridad.ACCESO_ACUDIENTE_BLOQUEADO_LEGAL, alerta.getTipoAlerta());
        assertEquals(SeveridadAlertaSeguridad.CRITICA, alerta.getSeveridad());
        assertFalse(alerta.getLeidaPorRector());
    }
}
