package com.jackren.sistemaacademico.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Unitarias de Entidades y Dominio - Control de Asistencia Escolar")
class AsistenciaEntityTest {

    @Test
    @DisplayName("ConfiguracionAlertaAsistencia debe evaluar alertas amarilla y roja según umbrales institucionales")
    void configuracionAlerta_EvaluacionUmbrales() {
        AnioLectivo anio = new AnioLectivo();
        anio.setAnio(2026);

        ConfiguracionAlertaAsistencia config = new ConfiguracionAlertaAsistencia(
                anio,
                new BigDecimal("15.00"),
                new BigDecimal("25.00"),
                "Alertas según reglamento institucional"
        );

        // Menor a 15% -> Sin alerta
        assertFalse(config.esAlertaAmarilla(new BigDecimal("10.00")));
        assertFalse(config.esAlertaRoja(new BigDecimal("10.00")));

        // 15% exacto -> Alerta Amarilla
        assertTrue(config.esAlertaAmarilla(new BigDecimal("15.00")));
        assertFalse(config.esAlertaRoja(new BigDecimal("15.00")));

        // 20% -> Alerta Amarilla
        assertTrue(config.esAlertaAmarilla(new BigDecimal("20.00")));
        assertFalse(config.esAlertaRoja(new BigDecimal("20.00")));

        // 25% exacto -> Alerta Roja
        assertFalse(config.esAlertaAmarilla(new BigDecimal("25.00")));
        assertTrue(config.esAlertaRoja(new BigDecimal("25.00")));

        // 35% -> Alerta Roja
        assertFalse(config.esAlertaAmarilla(new BigDecimal("35.00")));
        assertTrue(config.esAlertaRoja(new BigDecimal("35.00")));

        // Nulo
        assertFalse(config.esAlertaAmarilla(null));
        assertFalse(config.esAlertaRoja(null));
    }

    @Test
    @DisplayName("SesionAsistencia debe instanciarse para modalidad por asignatura y diaria")
    void sesionAsistencia_ModalidadesDual() {
        SesionAsistencia sesionClase = new SesionAsistencia();
        sesionClase.setTipoRegistro(TipoRegistroAsistencia.SESION_ASIGNATURA);
        sesionClase.setBloqueHorario(2);
        sesionClase.setTema("Leyes de Newton");

        assertEquals(TipoRegistroAsistencia.SESION_ASIGNATURA, sesionClase.getTipoRegistro());
        assertEquals(2, sesionClase.getBloqueHorario());
        assertEquals("Leyes de Newton", sesionClase.getTema());

        SesionAsistencia sesionDiaria = new SesionAsistencia();
        sesionDiaria.setTipoRegistro(TipoRegistroAsistencia.DIARIA_JORNADA);
        assertEquals(TipoRegistroAsistencia.DIARIA_JORNADA, sesionDiaria.getTipoRegistro());
    }

    @Test
    @DisplayName("RegistroAsistencia debe almacenar estados y minutos de retardo")
    void registroAsistencia_EstadosYRetardos() {
        RegistroAsistencia reg = new RegistroAsistencia();
        reg.setEstado(EstadoAsistencia.RETARDO);
        reg.setMinutosRetardo(15);
        reg.setObservaciones("Llegada tardía por transporte");

        assertEquals(EstadoAsistencia.RETARDO, reg.getEstado());
        assertEquals(15, reg.getMinutosRetardo());
        assertEquals("Llegada tardía por transporte", reg.getObservaciones());
    }

    @Test
    @DisplayName("JustificacionAsistencia debe registrar soporte y usuario autorizador")
    void justificacionAsistencia_RegistroAuditable() {
        JustificacionAsistencia just = new JustificacionAsistencia();
        just.setMotivo("Cita médica con especialista pediátrico");
        just.setNumeroRadicadoSoporte("RAD-2026-MED-045");
        just.setFechaRadicacion(LocalDate.of(2026, 3, 2));
        just.setUsername("coordinador_academico");

        assertEquals("Cita médica con especialista pediátrico", just.getMotivo());
        assertEquals("RAD-2026-MED-045", just.getNumeroRadicadoSoporte());
        assertEquals(LocalDate.of(2026, 3, 2), just.getFechaRadicacion());
        assertEquals("coordinador_academico", just.getUsername());
    }

    @Test
    @DisplayName("AuditoriaAsistencia debe registrar cambios de estado con motivo obligatorio")
    void auditoriaAsistencia_RegistroCambio() {
        AuditoriaAsistencia audit = new AuditoriaAsistencia();
        audit.setEstadoAnterior(EstadoAsistencia.AUSENTE_INJUSTIFICADA);
        audit.setEstadoNuevo(EstadoAsistencia.RETARDO);
        audit.setMotivoCorreccion("El estudiante llegó 10 minutos después con autorización de portería");
        audit.setUsername("docente1");

        assertEquals(EstadoAsistencia.AUSENTE_INJUSTIFICADA, audit.getEstadoAnterior());
        assertEquals(EstadoAsistencia.RETARDO, audit.getEstadoNuevo());
        assertEquals("El estudiante llegó 10 minutos después con autorización de portería", audit.getMotivoCorreccion());
        assertEquals("docente1", audit.getUsername());
    }

    @Test
    @DisplayName("SolicitudExcusaAsistencia debe registrar radicado y rango de fechas")
    void solicitudExcusaAsistencia_RegistroValido() {
        SolicitudExcusaAsistencia excusa = new SolicitudExcusaAsistencia();
        excusa.setNumeroRadicado("EXC-2026-0001");
        excusa.setTipoJustificacion(TipoJustificacionAsistencia.INCAPACIDAD_MEDICA_EPS);
        excusa.setFechaInicio(LocalDate.of(2026, 3, 10));
        excusa.setFechaFin(LocalDate.of(2026, 3, 12));
        excusa.setMotivoExplicacion("Incapacidad médica por cuadro viral");
        excusa.setEstado(EstadoSolicitudExcusa.PENDIENTE_REVISION);
        excusa.setEsDatoSaludSensible(true);

        assertEquals("EXC-2026-0001", excusa.getNumeroRadicado());
        assertEquals(TipoJustificacionAsistencia.INCAPACIDAD_MEDICA_EPS, excusa.getTipoJustificacion());
        assertEquals(LocalDate.of(2026, 3, 10), excusa.getFechaInicio());
        assertEquals(LocalDate.of(2026, 3, 12), excusa.getFechaFin());
        assertTrue(excusa.getEsDatoSaludSensible());
    }

    @Test
    @DisplayName("AutorizacionSalidaAnticipada debe registrar adulto que retira al menor")
    void autorizacionSalidaAnticipada_RegistroPorteria() {
        AutorizacionSalidaAnticipada salida = new AutorizacionSalidaAnticipada();
        salida.setNumeroAutorizacion("SAL-2026-0001");
        salida.setHoraSalida("11:30");
        salida.setMotivo("Cita médica odontológica");
        salida.setAdultoRetiraNombre("María Newton");
        salida.setAdultoRetiraDocumento("43210987");
        salida.setParentescoAutorizado("MADRE");
        salida.setEstado(EstadoAutorizacionSalida.AUTORIZADA_VERIFICADA);

        assertEquals("SAL-2026-0001", salida.getNumeroAutorizacion());
        assertEquals("11:30", salida.getHoraSalida());
        assertEquals("María Newton", salida.getAdultoRetiraNombre());
        assertEquals(EstadoAutorizacionSalida.AUTORIZADA_VERIFICADA, salida.getEstado());
    }

    @Test
    @DisplayName("ActuacionSeguimientoAusentismo debe registrar gestión del protocolo de permanencia")
    void actuacionSeguimientoAusentismo_RegistroGestion() {
        ActuacionSeguimientoAusentismo actuacion = new ActuacionSeguimientoAusentismo();
        actuacion.setTipoActuacion(TipoActuacionSeguimiento.CITACION_ACUDIENTE);
        actuacion.setFechaActuacion(LocalDate.of(2026, 3, 20));
        actuacion.setDescripcionGestion("Citación presencial con acudiente por 3 ausencias injustificadas");
        actuacion.setCompromisosAcordados("Presentar incapacidades médicas y garantizar puntualidad");

        assertEquals(TipoActuacionSeguimiento.CITACION_ACUDIENTE, actuacion.getTipoActuacion());
        assertEquals(LocalDate.of(2026, 3, 20), actuacion.getFechaActuacion());
    }
}
