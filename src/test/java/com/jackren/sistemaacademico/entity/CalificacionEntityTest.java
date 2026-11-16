package com.jackren.sistemaacademico.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Unitarias de Entidades y Dominio - Calificaciones y SIEE")
class CalificacionEntityTest {

    @Test
    @DisplayName("ConfiguracionEscalaSiee debe homologar notas a la escala nacional Decreto 1290")
    void homologacionDesempenoNacional_DebeClasificarCorrectamente() {
        AnioLectivo anio = new AnioLectivo();
        anio.setAnio(2026);

        ConfiguracionEscalaSiee escala = new ConfiguracionEscalaSiee(
                anio,
                new BigDecimal("1.00"),
                new BigDecimal("5.00"),
                new BigDecimal("3.00"),
                new BigDecimal("3.00"),
                new BigDecimal("4.00"),
                new BigDecimal("4.60")
        );

        // Límites inferiores y rango Bajo [1.00, 3.00)
        assertEquals(DesempenoNacional.BAJO, escala.homologarDesempeno(new BigDecimal("1.00")));
        assertEquals(DesempenoNacional.BAJO, escala.homologarDesempeno(new BigDecimal("2.99")));

        // Rango Básico [3.00, 4.00)
        assertEquals(DesempenoNacional.BASICO, escala.homologarDesempeno(new BigDecimal("3.00")));
        assertEquals(DesempenoNacional.BASICO, escala.homologarDesempeno(new BigDecimal("3.50")));
        assertEquals(DesempenoNacional.BASICO, escala.homologarDesempeno(new BigDecimal("3.99")));

        // Rango Alto [4.00, 4.60)
        assertEquals(DesempenoNacional.ALTO, escala.homologarDesempeno(new BigDecimal("4.00")));
        assertEquals(DesempenoNacional.ALTO, escala.homologarDesempeno(new BigDecimal("4.59")));

        // Rango Superior [4.60, 5.00]
        assertEquals(DesempenoNacional.SUPERIOR, escala.homologarDesempeno(new BigDecimal("4.60")));
        assertEquals(DesempenoNacional.SUPERIOR, escala.homologarDesempeno(new BigDecimal("5.00")));

        // Nota nula por defecto
        assertEquals(DesempenoNacional.BAJO, escala.homologarDesempeno(null));
    }

    @Test
    @DisplayName("ConfiguracionEscalaSiee debe validar estado de aprobación según nota institucional")
    void esAprobada_DebeEvaluarCorrectamenteSegunNotaAprobatoria() {
        ConfiguracionEscalaSiee escala = new ConfiguracionEscalaSiee();
        escala.setNotaAprobatoria(new BigDecimal("3.00"));

        assertTrue(escala.esAprobada(new BigDecimal("3.00")));
        assertTrue(escala.esAprobada(new BigDecimal("4.50")));
        assertFalse(escala.esAprobada(new BigDecimal("2.99")));
        assertFalse(escala.esAprobada(null));
    }

    @Test
    @DisplayName("ConfiguracionEscalaSiee debe aplicar redondeo según precisión institucional")
    void redondear_DebeRespetarPrecisionConfigurada() {
        ConfiguracionEscalaSiee escala = new ConfiguracionEscalaSiee();
        escala.setDecimalesRedondeo(1);

        assertEquals(new BigDecimal("3.5"), escala.redondear(new BigDecimal("3.46")));
        assertEquals(new BigDecimal("3.4"), escala.redondear(new BigDecimal("3.44")));
        assertNull(escala.redondear(null));
    }

    @Test
    @DisplayName("ActividadEvaluativa debe instanciarse con campos obligatorios y porcentaje")
    void actividadEvaluativa_InstanciacionCorrecta() {
        ActividadEvaluativa actividad = new ActividadEvaluativa();
        actividad.setNombre("Taller de Ondas");
        actividad.setPorcentaje(new BigDecimal("25.00"));
        actividad.setActivo(true);

        assertEquals("Taller de Ondas", actividad.getNombre());
        assertEquals(new BigDecimal("25.00"), actividad.getPorcentaje());
        assertTrue(actividad.getActivo());
    }

    @Test
    @DisplayName("CalificacionPeriodo debe almacenar definitivas y homologación de desempeño")
    void calificacionPeriodo_InstanciacionYDefinitiva() {
        CalificacionPeriodo calif = new CalificacionPeriodo();
        calif.setNotaDefinitiva(new BigDecimal("4.20"));
        calif.setNotaFinal(new BigDecimal("4.20"));
        calif.setDesempeno(DesempenoNacional.ALTO);
        calif.setAprobada(true);

        assertEquals(new BigDecimal("4.20"), calif.getNotaDefinitiva());
        assertEquals(DesempenoNacional.ALTO, calif.getDesempeno());
        assertTrue(calif.getAprobada());
    }

    @Test
    @DisplayName("AuditoriaCalificacion debe registrar trazabilidad inmutable de cambios")
    void auditoriaCalificacion_RegistroTrazabilidad() {
        AuditoriaCalificacion audit = new AuditoriaCalificacion();
        audit.setTipoEntidad(TipoEntidadAuditoriaCalificacion.ACTIVIDAD);
        audit.setEntidadId(15L);
        audit.setValorAnterior(new BigDecimal("2.50"));
        audit.setValorNuevo(new BigDecimal("4.00"));
        audit.setMotivo("Corrección por reclamo justificado con sustentación física");
        audit.setUsername("profesor1");

        assertEquals(TipoEntidadAuditoriaCalificacion.ACTIVIDAD, audit.getTipoEntidad());
        assertEquals(15L, audit.getEntidadId());
        assertEquals(new BigDecimal("2.50"), audit.getValorAnterior());
        assertEquals(new BigDecimal("4.00"), audit.getValorNuevo());
        assertEquals("profesor1", audit.getUsername());
    }
}
