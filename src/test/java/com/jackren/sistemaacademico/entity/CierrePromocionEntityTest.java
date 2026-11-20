package com.jackren.sistemaacademico.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Unitarias de Entidades y Dominio - Cierre Académico y Promoción")
class CierrePromocionEntityTest {

    @Test
    @DisplayName("ObservacionBoletinGrupo debe instanciarse con campos obligatorios")
    void observacionBoletinGrupo_InstanciacionCorrecta() {
        ObservacionBoletinGrupo obs = new ObservacionBoletinGrupo();
        obs.setObservacionFormativa("Demuestra alto compromiso y espíritu colaborativo");
        obs.setRecomendacionesPedagogicas("Reforzar comprensión lectora en ciencias");

        assertEquals("Demuestra alto compromiso y espíritu colaborativo", obs.getObservacionFormativa());
        assertEquals("Reforzar comprensión lectora en ciencias", obs.getRecomendacionesPedagogicas());
    }

    @Test
    @DisplayName("BoletinPublicadoVersion debe almacenar número de versión y estado")
    void boletinPublicadoVersion_InstanciacionCorrecta() {
        BoletinPublicadoVersion boletin = new BoletinPublicadoVersion();
        boletin.setNumeroVersion(1);
        boletin.setEstadoBoletin(EstadoBoletinVersion.PUBLICADO_OFICIAL);
        boletin.setDatosJson("{\"periodo\":\"P1\",\"promedio\":4.5}");

        assertEquals(1, boletin.getNumeroVersion());
        assertEquals(EstadoBoletinVersion.PUBLICADO_OFICIAL, boletin.getEstadoBoletin());
        assertNotNull(boletin.getDatosJson());
    }

    @Test
    @DisplayName("ActaPromocionAnual debe almacenar tipo y número de acta")
    void actaPromocionAnual_InstanciacionCorrecta() {
        ActaPromocionAnual acta = new ActaPromocionAnual();
        acta.setNumeroActa("ACT-PROM-2026-09A");
        acta.setTipoActa(TipoActaPromocion.ORDINARIA_COMISION);
        acta.setFechaActa(LocalDate.of(2026, 11, 28));
        acta.setIntegrantesNominativosJson("[\"Rector: Dr. López\",\"Coord: Lic. Gómez\"]");

        assertEquals("ACT-PROM-2026-09A", acta.getNumeroActa());
        assertEquals(TipoActaPromocion.ORDINARIA_COMISION, acta.getTipoActa());
        assertEquals(LocalDate.of(2026, 11, 28), acta.getFechaActa());
    }

    @Test
    @DisplayName("PromocionEstudianteAnual debe almacenar promedio y estado de promoción")
    void promocionEstudianteAnual_InstanciacionCorrecta() {
        PromocionEstudianteAnual prom = new PromocionEstudianteAnual();
        prom.setEstadoPromocion(EstadoPromocionEstudiante.PROMOVIDO_DIRECTO);
        prom.setPromedioGeneral(new BigDecimal("4.35"));
        prom.setAreasReprobadas(0);
        prom.setAsignaturasReprobadas(0);
        prom.setPorcentajeInasistenciaAnual(new BigDecimal("3.50"));

        assertEquals(EstadoPromocionEstudiante.PROMOVIDO_DIRECTO, prom.getEstadoPromocion());
        assertEquals(new BigDecimal("4.35"), prom.getPromedioGeneral());
        assertEquals(0, prom.getAreasReprobadas());
        assertEquals(new BigDecimal("3.50"), prom.getPorcentajeInasistenciaAnual());
    }

    @Test
    @DisplayName("TitulacionGraduandoMedia debe verificar servicio social y Saber 11")
    void titulacionGraduandoMedia_InstanciacionCorrecta() {
        TitulacionGraduandoMedia tit = new TitulacionGraduandoMedia();
        tit.setModalidadTitulo(ModalidadTituloBachiller.BACHILLER_ACADEMICO);
        tit.setHorasServicioSocial(80);
        tit.setCodigoRegistroIcfes("AC202641234567");
        tit.setDocumentoIdentidadVerificado(true);
        tit.setEstadoTitulacion(EstadoTitulacionGraduando.APTO_GRADUACION);

        assertEquals(ModalidadTituloBachiller.BACHILLER_ACADEMICO, tit.getModalidadTitulo());
        assertEquals(80, tit.getHorasServicioSocial());
        assertEquals("AC202641234567", tit.getCodigoRegistroIcfes());
        assertTrue(tit.getDocumentoIdentidadVerificado());
        assertEquals(EstadoTitulacionGraduando.APTO_GRADUACION, tit.getEstadoTitulacion());
    }
}
