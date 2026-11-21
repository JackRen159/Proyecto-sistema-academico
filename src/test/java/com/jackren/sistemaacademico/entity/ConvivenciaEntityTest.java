package com.jackren.sistemaacademico.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Unitarias de Entidades y Dominio - Convivencia Escolar V2")
class ConvivenciaEntityTest {

    @Test
    @DisplayName("CasoConvivencia debe instanciarse con campos y estado por defecto")
    void casoConvivencia_InstanciacionCorrecta() {
        CasoConvivencia caso = new CasoConvivencia();
        caso.setCodigoExpediente("EXP-CONV-2026-001");
        caso.setTipoSituacion(TipoSituacionConvivencia.TIPO_II);
        caso.setFechaHechos(LocalDate.of(2026, 5, 10));
        caso.setLugarHechos("Patio central");
        caso.setDescripcionSituacion("Presunta agresión verbal reiterada");

        assertEquals("EXP-CONV-2026-001", caso.getCodigoExpediente());
        assertEquals(TipoSituacionConvivencia.TIPO_II, caso.getTipoSituacion());
        assertEquals(EstadoCasoConvivencia.ABIERTO_PRELIMINAR_URGENTE, caso.getEstadoCaso());
        assertFalse(caso.getAtencionMedicaUrgente());
        assertFalse(caso.getReservaProteccionContraAcudiente());
    }

    @Test
    @DisplayName("AnotacionObservador y Adenda deben instanciarse correctamente")
    void anotacionObservador_InstanciacionCorrecta() {
        AnotacionObservador obs = new AnotacionObservador();
        obs.setTipoAnotacion(TipoAnotacionObservador.SEGUIMIENTO_FORMATIVO);
        obs.setFechaHecho(LocalDate.of(2026, 5, 12));
        obs.setHoraHecho(LocalTime.of(10, 30));
        obs.setLugarHecho("Aula 101");
        obs.setDescripcionFactica("El estudiante participa activamente en el diálogo restaurativo");
        obs.setEsZonaComun(false);

        assertEquals(TipoAnotacionObservador.SEGUIMIENTO_FORMATIVO, obs.getTipoAnotacion());
        assertTrue(obs.getAnotacionFirmada());
        assertFalse(obs.getEsZonaComun());

        AnotacionObservadorAdenda adenda = new AnotacionObservadorAdenda();
        adenda.setAnotacion(obs);
        adenda.setMotivoAdenda("Aclaración de hora exacta del hecho");
        adenda.setContenidoAdenda("El hecho se registró a las 10:35 am en cambio de hora");

        assertEquals("Aclaración de hora exacta del hecho", adenda.getMotivoAdenda());
        assertSame(obs, adenda.getAnotacion());
    }

    @Test
    @DisplayName("ExpedienteDebidoProceso y Descargo deben preservar garantías procesales")
    void debidoProceso_InstanciacionCorrecta() {
        ExpedienteDebidoProceso exp = new ExpedienteDebidoProceso();
        exp.setNumeroExpediente("EXP-DP-2026-005");
        exp.setDiasHabilesDescargos(5);
        exp.setFechaLimiteDescargos(LocalDate.of(2026, 5, 20));
        exp.setAutoAperturaHechos("Hechos objeto de análisis");
        exp.setNormasManualInfringidas("Artículo 45 numeral 3 Manual de Convivencia");
        exp.setPruebasTrasladadasAnonimizadas("Testimonios 1 y 2 anonimizados");

        assertEquals("EXP-DP-2026-005", exp.getNumeroExpediente());
        assertEquals(5, exp.getDiasHabilesDescargos());
        assertEquals(EstadoExpedienteDebidoProceso.APERTURA_NOTIFICADA, exp.getEstadoExpediente());

        DescargoDebidoProceso descargo = new DescargoDebidoProceso();
        descargo.setExpediente(exp);
        descargo.setContenidoDescargos("Versión de los hechos y argumentos de defensa");
        descargo.setParticipoPersonero(true);

        assertTrue(descargo.getParticipoPersonero());
        assertSame(exp, descargo.getExpediente());
    }

    @Test
    @DisplayName("MedidaSancionConvivencia y PlanContinuidadAcademica deben vincularse conforme a RN-08")
    void medidaYPlanContinuidad_InstanciacionCorrecta() {
        MedidaSancionConvivencia medida = new MedidaSancionConvivencia();
        medida.setNumeroResolucion("RES-RECT-2026-012");
        medida.setTipoMedida(TipoMedidaSancion.SUSPENSION_TEMPORAL_ASISTENCIA);
        medida.setEsSancionDisciplinaria(true);
        medida.setDiasSuspension(3);
        medida.setMotivacionHechosNormas("Falta grave debidamente comprobada");
        medida.setAnalisisProporcionalidad("Proporcional a la edad y reiteración");

        assertEquals("RES-RECT-2026-012", medida.getNumeroResolucion());
        assertEquals(TipoMedidaSancion.SUSPENSION_TEMPORAL_ASISTENCIA, medida.getTipoMedida());
        assertEquals(EstadoDecisionSancion.EMITIDA_PENDIENTE_NOTIFICACION, medida.getEstadoDecision());

        PlanContinuidadAcademica plan = new PlanContinuidadAcademica();
        plan.setMedidaSancion(medida);
        plan.setFechaInicio(LocalDate.of(2026, 6, 1));
        plan.setFechaFin(LocalDate.of(2026, 6, 3));
        plan.setGuiasTalleresDescripcion("Taller de ciencias y lectura comprensiva");
        plan.setMecanismoEvaluacion("Entrega virtual y retroalimentación sincrónica");

        assertSame(medida, plan.getMedidaSancion());
        assertEquals(LocalDate.of(2026, 6, 1), plan.getFechaInicio());
    }

    @Test
    @DisplayName("ActaComiteConvivencia y RecursoImpugnacion deben instanciarse correctamente")
    void comiteYRecursos_InstanciacionCorrecta() {
        ActaComiteConvivencia acta = new ActaComiteConvivencia();
        acta.setNumeroActa("ACT-COMITE-2026-02");
        acta.setFechaSesion(LocalDate.of(2026, 5, 25));
        acta.setHoraInicio(LocalTime.of(8, 0));
        acta.setLugarSesion("Sala de Juntas");
        acta.setTipoSesion(TipoSesionComiteConvivencia.ORDINARIA);
        acta.setIntegrantesAsistentesJson("[\"Rector\", \"Orientador\", \"Personero\"]");
        acta.setCasosAnalizadosJson("[\"EXP-CONV-2026-001\"]");
        acta.setAcuerdosCompromisosJson("[\"Compromiso pedagógico cumplido\"]");

        assertEquals("ACT-COMITE-2026-02", acta.getNumeroActa());
        assertEquals(TipoSesionComiteConvivencia.ORDINARIA, acta.getTipoSesion());

        RecursoImpugnacion recurso = new RecursoImpugnacion();
        recurso.setTipoRecurso(TipoRecursoImpugnacion.REPOSICION_Y_EN_SUBSIDIO_APELACION);
        recurso.setSustentacionRecurso("Se solicita atenuación de la medida");

        assertEquals(TipoRecursoImpugnacion.REPOSICION_Y_EN_SUBSIDIO_APELACION, recurso.getTipoRecurso());
        assertEquals(EstadoRecursoImpugnacion.RADICADO_EN_TRAMITE, recurso.getEstadoRecurso());
    }
}
