package com.jackren.sistemaacademico.service;

import com.jackren.sistemaacademico.dto.*;
import com.jackren.sistemaacademico.entity.AuditoriaEvento;
import com.jackren.sistemaacademico.entity.ResultadoAuditoria;
import com.jackren.sistemaacademico.repository.AuditoriaEventoRepository;
import com.jackren.sistemaacademico.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias - AuditoriaGlobalService (Encadenamiento Criptográfico e Integridad)")
class AuditoriaGlobalServiceTest {

    @Mock
    private AuditoriaEventoRepository auditoriaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private AuditoriaGlobalService auditoriaService;

    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("Primer evento registrado debe enlazar con HASH_GENESIS")
    void testRegistrarPrimerEventoGenesis() {
        when(auditoriaRepository.findTopByOrderByIdDesc()).thenReturn(Optional.empty());
        when(auditoriaRepository.save(any(AuditoriaEvento.class))).thenAnswer(invocation -> {
            AuditoriaEvento ev = invocation.getArgument(0);
            ev.setId(1L);
            return ev;
        });

        RegistroAuditoriaEventoRequest request = new RegistroAuditoriaEventoRequest(
                "admin", "ADMINISTRADOR", "SEGURIDAD", "CREAR_USUARIO", "Usuario", "1", "Alta de rector", ResultadoAuditoria.EXITO, "corr-001");

        AuditoriaEventoResponse response = auditoriaService.registrarEvento(request);

        assertNotNull(response);
        assertEquals(AuditoriaGlobalService.HASH_GENESIS, response.getHashAnterior());
        assertNotNull(response.getHashEvento());
        assertEquals(64, response.getHashEvento().length());
        verify(auditoriaRepository, times(1)).save(any(AuditoriaEvento.class));
    }

    @Test
    @DisplayName("Segundo evento registrado debe encadenar con hash del evento anterior")
    void testRegistrarSegundoEventoEncadenado() {
        AuditoriaEvento primerEvento = new AuditoriaEvento();
        primerEvento.setId(1L);
        primerEvento.setHashEvento("1111111111111111111111111111111111111111111111111111111111111111");

        when(auditoriaRepository.findTopByOrderByIdDesc()).thenReturn(Optional.of(primerEvento));
        when(auditoriaRepository.save(any(AuditoriaEvento.class))).thenAnswer(invocation -> {
            AuditoriaEvento ev = invocation.getArgument(0);
            ev.setId(2L);
            return ev;
        });

        RegistroAuditoriaEventoRequest request = new RegistroAuditoriaEventoRequest(
                "rector", "RECTOR", "CONFIGURACION", "PUBLICAR_SIEE", "ConfiguracionSiee", "1", "Aprobación Consejo", ResultadoAuditoria.EXITO, "corr-002");

        AuditoriaEventoResponse response = auditoriaService.registrarEvento(request);

        assertNotNull(response);
        assertEquals("1111111111111111111111111111111111111111111111111111111111111111", response.getHashAnterior());
        assertNotEquals(response.getHashAnterior(), response.getHashEvento());
    }

    @Test
    @DisplayName("Verificación de integridad debe retornar CADENA_INTEGRA_VERIFICADA cuando no hay alteraciones")
    void testVerificarIntegridadCadenaValida() {
        LocalDateTime t1 = LocalDateTime.now().minusHours(2);
        String fechaIso1 = t1.format(ISO_FORMATTER);
        String hGenesis = AuditoriaGlobalService.HASH_GENESIS;
        String h1 = AuditoriaGlobalService.calcularHashCanónico(
                "admin", "ADMINISTRADOR", "SEGURIDAD", "LOGIN", "Usuario", "1", "Acceso", "EXITO", "c-1", fechaIso1, hGenesis);

        AuditoriaEvento ev1 = new AuditoriaEvento(null, "admin", "ADMINISTRADOR", "SEGURIDAD", "LOGIN", "Usuario", "1", null, null, "Acceso", ResultadoAuditoria.EXITO, "c-1", "127.0.0.1", null, hGenesis, h1);
        ev1.setId(1L);
        ev1.setFechaEvento(t1);

        LocalDateTime t2 = LocalDateTime.now().minusHours(1);
        String fechaIso2 = t2.format(ISO_FORMATTER);
        String h2 = AuditoriaGlobalService.calcularHashCanónico(
                "admin", "ADMINISTRADOR", "CONFIG", "PARAM", "Parametro", "2", "Update", "EXITO", "c-2", fechaIso2, h1);

        AuditoriaEvento ev2 = new AuditoriaEvento(null, "admin", "ADMINISTRADOR", "CONFIG", "PARAM", "Parametro", "2", null, null, "Update", ResultadoAuditoria.EXITO, "c-2", "127.0.0.1", null, h1, h2);
        ev2.setId(2L);
        ev2.setFechaEvento(t2);

        when(auditoriaRepository.findAllByOrderByIdAsc()).thenReturn(List.of(ev1, ev2));

        VerificacionIntegridadResponse resultado = auditoriaService.verificarIntegridadCadena();

        assertNotNull(resultado);
        assertEquals("CADENA_INTEGRA_VERIFICADA", resultado.getEstado());
        assertEquals(2, resultado.getTotalEventosVerificados());
        assertNull(resultado.getIdEventoDiscrepancia());
    }

    @Test
    @DisplayName("Verificación de integridad debe detectar ALERTA_CRITICA_INTEGRIDAD_AUDITORIA si un registro fue manipulado")
    void testVerificarIntegridadCadenaConManipulacion() {
        LocalDateTime t1 = LocalDateTime.now();
        String fechaIso1 = t1.format(ISO_FORMATTER);
        String hGenesis = AuditoriaGlobalService.HASH_GENESIS;
        String h1 = AuditoriaGlobalService.calcularHashCanónico(
                "admin", "ADMINISTRADOR", "SEGURIDAD", "LOGIN", "Usuario", "1", "Acceso", "EXITO", "c-1", fechaIso1, hGenesis);

        // Simulamos que alguien alteró la entidad de "Usuario" a "Otro" en la base de datos sin recalcular el hash
        AuditoriaEvento ev1 = new AuditoriaEvento(null, "admin", "ADMINISTRADOR", "SEGURIDAD", "LOGIN", "OTRO_VALOR_ALTERADO", "1", null, null, "Acceso", ResultadoAuditoria.EXITO, "c-1", "127.0.0.1", null, hGenesis, h1);
        ev1.setId(1L);
        ev1.setFechaEvento(t1);

        when(auditoriaRepository.findAllByOrderByIdAsc()).thenReturn(List.of(ev1));

        VerificacionIntegridadResponse resultado = auditoriaService.verificarIntegridadCadena();

        assertNotNull(resultado);
        assertEquals("ALERTA_CRITICA_INTEGRIDAD_AUDITORIA", resultado.getEstado());
        assertEquals(1L, resultado.getIdEventoDiscrepancia());
        assertTrue(resultado.getDetalle().contains("hashEvento grabado"));
    }

    @Test
    @DisplayName("Exportación de auditoría debe calcular manifiesto SHA-256 y registrar auditoría de la exportación")
    void testExportarPaqueteAuditoria() {
        AuditoriaEvento ev = new AuditoriaEvento();
        ev.setId(1L);
        ev.setHashEvento("abc123hash");
        ev.setUsername("admin");
        ev.setRolActivo("ADMINISTRADOR");
        ev.setModulo("SEGURIDAD");
        ev.setAccion("LOGIN");
        ev.setEntidad("Usuario");
        ev.setResultado(ResultadoAuditoria.EXITO);
        ev.setCorrelationId("c-1");
        ev.setFechaEvento(LocalDateTime.now());
        ev.setHashAnterior(AuditoriaGlobalService.HASH_GENESIS);

        when(auditoriaRepository.findAllByOrderByIdAsc()).thenReturn(List.of(ev));
        when(auditoriaRepository.findTopByOrderByIdDesc()).thenReturn(Optional.of(ev));
        when(auditoriaRepository.save(any(AuditoriaEvento.class))).thenAnswer(i -> i.getArgument(0));

        ExportacionAuditoriaRequest req = new ExportacionAuditoriaRequest(
                "OFICIO-2026-99", "Inspección de Secretaría de Educación", "Secretaría de Educación Distrital");

        PaqueteAuditoriaExportadoResponse paquete = auditoriaService.exportarPaqueteAuditoria(req, "rector1");

        assertNotNull(paquete);
        assertEquals("OFICIO-2026-99", paquete.getNumeroOficioRequerimiento());
        assertEquals(1, paquete.getTotalRegistrosExportados());
        assertNotNull(paquete.getManifiestoSha256());
        assertEquals(64, paquete.getManifiestoSha256().length());
        verify(auditoriaRepository, times(1)).save(any(AuditoriaEvento.class)); // Autotrazabilidad de la exportación
    }
}
