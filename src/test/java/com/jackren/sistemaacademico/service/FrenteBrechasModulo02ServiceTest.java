package com.jackren.sistemaacademico.service;

import com.jackren.sistemaacademico.dto.*;
import com.jackren.sistemaacademico.entity.*;
import com.jackren.sistemaacademico.exception.ConflictoException;
import com.jackren.sistemaacademico.repository.*;
import com.jackren.sistemaacademico.security.TokenRevocationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias - Módulo 02 V2 (Expediente Completo, Evolución Documental, Rectificaciones y Restricciones)")
class FrenteBrechasModulo02ServiceTest {

    @Mock
    private EstudianteRepository estudianteRepository;

    @Mock
    private AcudienteRepository acudienteRepository;

    @Mock
    private EstudianteAcudienteRepository estudianteAcudienteRepository;

    @Mock
    private PoliticaTratamientoDatosRepository politicaRepository;

    @Mock
    private AutorizacionTratamientoDatosMenorRepository autorizacionMenorRepository;

    @Mock
    private HistorialDocumentoIdentidadRepository historialRepository;

    @Mock
    private SolicitudRectificacionHabeasDataRepository rectificacionRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private AuditoriaGlobalService auditoriaService;

    @Mock
    private TokenRevocationService tokenRevocationService;

    @InjectMocks
    private ExpedienteEstudianteService expedienteService;

    @InjectMocks
    private EvolucionDocumentoService evolucionService;

    @InjectMocks
    private SolicitudRectificacionService rectificacionService;

    @InjectMocks
    private EstudianteAcudienteService estudianteAcudienteService;

    @Test
    @DisplayName("registrarExpedienteCompleto debe crear estudiante, acudiente principal y autorización menor atómicamente (UC-12, RN-EST-01)")
    void testRegistrarExpedienteCompleto() {
        ExpedienteCompletoRequest req = new ExpedienteCompletoRequest();
        req.setNombres("Juan");
        req.setApellidos("Pérez");
        req.setTipoDocumento(TipoDocumento.TI);
        req.setDocumento("10203040");
        req.setFechaNacimiento(LocalDate.of(2010, 5, 10));
        req.setDireccion("Calle 1 # 2-3");
        req.setEps("Sanitas");
        req.setGrupoSanguineoRh("O+");
        req.setTieneDiscapacidadPiar(false);

        req.setAcudienteNombres("María");
        req.setAcudienteApellidos("López");
        req.setAcudienteTipoDocumento(TipoDocumento.CC);
        req.setAcudienteDocumento("52001002");
        req.setAcudienteTelefono("3001234567");
        req.setParentesco(Parentesco.MADRE);

        when(estudianteRepository.findByDocumento("10203040")).thenReturn(Optional.empty());
        when(estudianteRepository.save(any(Estudiante.class))).thenAnswer(i -> {
            Estudiante e = i.getArgument(0);
            e.setId(10L);
            return e;
        });

        when(acudienteRepository.findByDocumento("52001002")).thenReturn(Optional.empty());
        when(acudienteRepository.save(any(Acudiente.class))).thenAnswer(i -> {
            Acudiente a = i.getArgument(0);
            a.setId(20L);
            return a;
        });

        when(estudianteAcudienteRepository.findByEstudianteAndAcudiente(any(), any())).thenReturn(Optional.empty());
        when(estudianteAcudienteRepository.findByEstudianteAndEsPrincipalTrue(any())).thenReturn(Optional.empty());

        PoliticaTratamientoDatos pol = new PoliticaTratamientoDatos(1, "Pol V1", "Texto", "Aviso", true);
        pol.setId(1L);
        when(politicaRepository.findFirstByEsVigenteTrueOrderByNumeroVersionDesc()).thenReturn(Optional.of(pol));
        when(autorizacionMenorRepository.findByEstudianteIdAndPoliticaId(eq(10L), eq(1L))).thenReturn(Optional.empty());

        ExpedienteCompletoResponse resp = expedienteService.registrarExpedienteCompleto(req, "secretaria1", "127.0.0.1", "Chrome");

        assertNotNull(resp);
        assertEquals(10L, resp.getEstudianteId());
        assertEquals("10203040", resp.getEstudianteDocumento());
        assertEquals(EstadoExpediente.LISTO_PARA_MATRICULA, resp.getEstadoExpediente());
        assertTrue(resp.getAutorizacionDatosMenorRegistrada());
        verify(estudianteAcudienteRepository, times(1)).save(any(EstudianteAcudiente.class));
        verify(autorizacionMenorRepository, times(1)).save(any(AutorizacionTratamientoDatosMenor.class));
        verify(auditoriaService, times(1)).registrarEvento(any());
    }

    @Test
    @DisplayName("registrarEvolucionDocumento debe archivar antecedente inmutable y actualizar documento (UC-PROV-04 A1, RN-EST-03)")
    void testRegistrarEvolucionDocumento() {
        Estudiante est = new Estudiante();
        est.setId(1L);
        est.setTipoDocumento(TipoDocumento.RC);
        est.setDocumento("12345678");

        when(estudianteRepository.findById(1L)).thenReturn(Optional.of(est));
        when(estudianteRepository.existsByDocumento("10203040")).thenReturn(false);
        when(historialRepository.save(any(HistorialDocumentoIdentidad.class))).thenAnswer(i -> {
            HistorialDocumentoIdentidad h = i.getArgument(0);
            h.setId(100L);
            return h;
        });

        EvolucionDocumentoRequest req = new EvolucionDocumentoRequest(TipoDocumento.TI, "10203040", "Cumplimiento de 7 años");
        HistorialDocumentoIdentidadResponse resp = evolucionService.registrarEvolucionDocumento(1L, req, "secretaria1");

        assertNotNull(resp);
        assertEquals(TipoDocumento.RC, resp.getTipoDocumentoAnterior());
        assertEquals("12345678", resp.getNumeroDocumentoAnterior());
        assertEquals(TipoDocumento.TI, resp.getTipoDocumentoNuevo());
        assertEquals("10203040", resp.getNumeroDocumentoNuevo());
        assertEquals("10203040", est.getDocumento()); // Actualizado en la entidad Estudiante
        verify(estudianteRepository, times(1)).save(est);
        verify(historialRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("registrarEvolucionDocumento rechaza documento nuevo duplicado con ConflictoException (E2)")
    void testRegistrarEvolucionDocumentoDuplicado() {
        Estudiante est = new Estudiante();
        est.setId(1L);
        est.setDocumento("12345678");

        when(estudianteRepository.findById(1L)).thenReturn(Optional.of(est));
        when(estudianteRepository.existsByDocumento("99999999")).thenReturn(true);

        EvolucionDocumentoRequest req = new EvolucionDocumentoRequest(TipoDocumento.CC, "99999999", "Mayoría de edad");
        assertThrows(ConflictoException.class, () -> evolucionService.registrarEvolucionDocumento(1L, req, "secretaria1"));
    }

    @Test
    @DisplayName("radicarSolicitud rectificación asigna radicado ARCO y plazo de 15 días hábiles (UC-07 A2, RN-EST-04)")
    void testRadicarSolicitudRectificacion() {
        Estudiante est = new Estudiante();
        est.setId(1L);
        est.setDocumento("10203040");

        Usuario solicitante = new Usuario("acudiente1", "hash", Rol.ACUDIENTE);
        solicitante.setId(2L);

        when(estudianteRepository.findById(1L)).thenReturn(Optional.of(est));
        when(usuarioRepository.findByUsername("acudiente1")).thenReturn(Optional.of(solicitante));
        when(rectificacionRepository.save(any())).thenAnswer(i -> {
            SolicitudRectificacionHabeasData s = i.getArgument(0);
            s.setId(50L);
            return s;
        });

        RadicarSolicitudRectificacionRequest req = new RadicarSolicitudRectificacionRequest(
                "{\"direccion\":\"Nueva Carrera 15\"}", "Cambio de domicilio familiar", "url-recibo");

        SolicitudRectificacionResponse resp = rectificacionService.radicarSolicitud(1L, req, "acudiente1");

        assertNotNull(resp);
        assertTrue(resp.getNumeroRadicado().startsWith("ARCO-"));
        assertEquals(EstadoSolicitudRectificacion.PENDIENTE, resp.getEstado());
        assertNotNull(resp.getFechaLimiteLegal());
        verify(rectificacionRepository, times(1)).save(any());
        verify(auditoriaService, times(1)).registrarEvento(any());
    }

    @Test
    @DisplayName("listarMisEstudiantesAsociados excluye vínculos con restricción legal o revocados (UC-06, RN-EST-02)")
    void testListarMisEstudiantesAsociadosFiltroSeguro() {
        Usuario usuario = new Usuario("acudiente1", "hash", Rol.ACUDIENTE);
        usuario.setPersonaTipo(TipoPersonaUsuario.ACUDIENTE);
        usuario.setPersonaId(15L);

        Acudiente acudiente = new Acudiente();
        acudiente.setId(15L);

        Estudiante e1 = new Estudiante();
        e1.setId(1L);
        e1.setDocumento("1010");
        e1.setNombres("Hijo");
        e1.setApellidos("Uno");
        e1.setActivo(true);

        EstudianteAcudiente v1 = new EstudianteAcudiente();
        v1.setEstudiante(e1);
        v1.setAcudiente(acudiente);
        v1.setParentesco(Parentesco.MADRE);
        v1.setEsPrincipal(true);
        v1.setEstadoVinculo(EstadoVinculo.VERIFICADO);
        v1.setAutorizadoRetiro(true);

        when(usuarioRepository.findByUsername("acudiente1")).thenReturn(Optional.of(usuario));
        when(acudienteRepository.findById(15L)).thenReturn(Optional.of(acudiente));
        when(estudianteAcudienteRepository.findByAcudienteAndEstadoVinculo(acudiente, EstadoVinculo.VERIFICADO))
                .thenReturn(List.of(v1));

        List<EstudianteAsociadoAcudienteResponse> lista = estudianteAcudienteService.listarMisEstudiantesAsociados("acudiente1");

        assertEquals(1, lista.size());
        assertEquals("1010", lista.get(0).getDocumento());
        assertEquals("Hijo Uno", lista.get(0).getNombreCompleto());
        assertTrue(lista.get(0).getEsPrincipal());
    }

    @Test
    @DisplayName("registrarRestriccionLegal bloquea vínculo, retira autorización e invalida sesiones del adulto (UC-PROV-05 A3, RN-SEG-06)")
    void testRegistrarRestriccionLegal() {
        EstudianteAcudiente vinculo = new EstudianteAcudiente();
        vinculo.setId(99L);
        vinculo.setEsPrincipal(false); // Secundario
        vinculo.setAutorizadoRetiro(true);
        vinculo.setEstadoVinculo(EstadoVinculo.VERIFICADO);

        Acudiente acudiente = new Acudiente();
        acudiente.setDocumento("55443322");
        vinculo.setAcudiente(acudiente);

        Usuario usuarioAcudiente = new Usuario("55443322", "hash", Rol.ACUDIENTE);
        usuarioAcudiente.setId(45L);

        when(estudianteAcudienteRepository.findById(99L)).thenReturn(Optional.of(vinculo));
        when(usuarioRepository.findByUsername("55443322")).thenReturn(Optional.of(usuarioAcudiente));

        RegistroRestriccionLegalRequest req = new RegistroRestriccionLegalRequest(
                "Medida cautelar Comisaría de Familia Oficio 123-2026", "url-oficio");

        estudianteAcudienteService.registrarRestriccionLegal(99L, req, "rector");

        assertEquals(EstadoVinculo.BLOQUEADO_RESTRICCION_LEGAL, vinculo.getEstadoVinculo());
        assertFalse(vinculo.getAutorizadoRetiro());
        assertEquals("Medida cautelar Comisaría de Familia Oficio 123-2026", vinculo.getMotivoBloqueoLegal());
        verify(tokenRevocationService, times(1)).revokeUserTokens(eq(45L), any()); // Inmediata expulsión de sesiones activas (RN-SEG-06)
        verify(estudianteAcudienteRepository, times(1)).save(vinculo);
        verify(auditoriaService, times(1)).registrarEvento(any());
    }
}
