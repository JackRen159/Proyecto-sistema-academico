package com.jackren.sistemaacademico.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

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
    @DisplayName("ActividadEvaluativa debe instanciarse con campos obligatorios y soporte V2")
    void actividadEvaluativa_InstanciacionCorrecta() {
        ActividadEvaluativa actividad = new ActividadEvaluativa();
        actividad.setNombre("Taller de Ondas");
        actividad.setPorcentaje(new BigDecimal("25.00"));
        actividad.setCategoriaEvaluativa(CategoriaEvaluativa.COGNITIVA);
        actividad.setTipoComponente(TipoComponenteEvaluativo.TALLER);
        actividad.setModalidad(ModalidadEvaluativa.HETEROEVALUACION);
        actividad.setEstado(EstadoActividadEvaluativa.PUBLICADA);
        actividad.setActivo(true);

        assertEquals("Taller de Ondas", actividad.getNombre());
        assertEquals(new BigDecimal("25.00"), actividad.getPorcentaje());
        assertEquals(CategoriaEvaluativa.COGNITIVA, actividad.getCategoriaEvaluativa());
        assertEquals(TipoComponenteEvaluativo.TALLER, actividad.getTipoComponente());
        assertEquals(ModalidadEvaluativa.HETEROEVALUACION, actividad.getModalidad());
        assertEquals(EstadoActividadEvaluativa.PUBLICADA, actividad.getEstado());
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
        calif.setOrigen(OrigenCalificacion.CALCULADA);

        assertEquals(new BigDecimal("4.20"), calif.getNotaDefinitiva());
        assertEquals(DesempenoNacional.ALTO, calif.getDesempeno());
        assertTrue(calif.getAprobada());
        assertEquals(OrigenCalificacion.CALCULADA, calif.getOrigen());
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

    @Test
    @DisplayName("CalificacionAreaPeriodo debe almacenar consolidado de área curricular")
    void calificacionAreaPeriodo_InstanciacionCorrecta() {
        CalificacionAreaPeriodo califArea = new CalificacionAreaPeriodo();
        califArea.setNotaDefinitiva(new BigDecimal("3.85"));
        califArea.setDesempeno(DesempenoNacional.BASICO);
        califArea.setAprobada(true);
        califArea.setOrigen(OrigenCalificacion.CALCULADA);

        assertEquals(new BigDecimal("3.85"), califArea.getNotaDefinitiva());
        assertEquals(DesempenoNacional.BASICO, califArea.getDesempeno());
        assertTrue(califArea.getAprobada());
        assertEquals(OrigenCalificacion.CALCULADA, califArea.getOrigen());
    }

    @Test
    @DisplayName("ActaNivelacion debe registrar la fórmula aplicada y la nota resultante")
    void actaNivelacion_InstanciacionCorrecta() {
        ActaNivelacion acta = new ActaNivelacion();
        acta.setNumeroActa("ACT-NIV-2026-0001");
        acta.setFechaSustentacion(LocalDate.of(2026, 4, 15));
        acta.setAsistencia(true);
        acta.setNotaOriginal(new BigDecimal("2.40"));
        acta.setNotaObtenida(new BigDecimal("3.50"));
        acta.setFormulaAplicada(FormulaRecuperacionSiee.REEMPLAZO_TOTAL);
        acta.setNotaFinalResultante(new BigDecimal("3.50"));
        acta.setDesempenoResultante(DesempenoNacional.BASICO);

        assertEquals("ACT-NIV-2026-0001", acta.getNumeroActa());
        assertEquals(FormulaRecuperacionSiee.REEMPLAZO_TOTAL, acta.getFormulaAplicada());
        assertEquals(new BigDecimal("3.50"), acta.getNotaFinalResultante());
        assertEquals(DesempenoNacional.BASICO, acta.getDesempenoResultante());
    }

    @Test
    @DisplayName("ReclamacionCalificacion debe registrar radicado, motivo y estado de debido proceso")
    void reclamacionCalificacion_InstanciacionCorrecta() {
        ReclamacionCalificacion rec = new ReclamacionCalificacion();
        rec.setNumeroRadicado("REC-2026-0001");
        rec.setTipoCalificacion(TipoCalificacionReclamacion.ACTIVIDAD);
        rec.setReferenciaId(100L);
        rec.setRolSolicitante(RolSolicitanteReclamacion.ESTUDIANTE);
        rec.setMotivoReclamacion("Error aritmético en suma de puntos de rúbrica");
        rec.setEstado(EstadoReclamacionCalificacion.RADICADA_PRIMERA_INSTANCIA);
        rec.setFechaLimiteRespuesta(LocalDate.of(2026, 4, 20));

        assertEquals("REC-2026-0001", rec.getNumeroRadicado());
        assertEquals(TipoCalificacionReclamacion.ACTIVIDAD, rec.getTipoCalificacion());
        assertEquals(EstadoReclamacionCalificacion.RADICADA_PRIMERA_INSTANCIA, rec.getEstado());
        assertEquals(LocalDate.of(2026, 4, 20), rec.getFechaLimiteRespuesta());
    }
}
