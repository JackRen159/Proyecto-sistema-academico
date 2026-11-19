package com.jackren.sistemaacademico.service;

import com.jackren.sistemaacademico.dto.*;
import com.jackren.sistemaacademico.entity.*;
import com.jackren.sistemaacademico.exception.ConflictoException;
import com.jackren.sistemaacademico.exception.ValidacionException;
import com.jackren.sistemaacademico.repository.*;
import com.jackren.sistemaacademico.security.TokenRevocationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias - UsuarioService (Ciclo de Vida V2, Roles y Órganos Colegiados)")
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private UsuarioRolRepository usuarioRolRepository;

    @Mock
    private OrganoColegiadoMiembroRepository organoRepository;

    @Mock
    private AnioLectivoRepository anioLectivoRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuditoriaGlobalService auditoriaService;

    @Mock
    private TokenRevocationService tokenRevocationService;

    @Mock
    private AcudienteRepository acudienteRepository;

    @Mock
    private EstudianteAcudienteRepository estudianteAcudienteRepository;

    @Mock
    private ParametroInstitucionalRepository parametroRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    @DisplayName("crearUsuario debe aprovisionar cuenta en PENDIENTE_ACTIVACION con rol inicial")
    void testCrearUsuarioExitoso() {
        CrearUsuarioRequest request = new CrearUsuarioRequest(
                "coordinador1", null, Rol.COORDINADOR_ACADEMICO, TipoPersonaUsuario.PROFESOR, 10L);

        when(usuarioRepository.existsByUsername("coordinador1")).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashed_pass");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(i -> {
            Usuario u = i.getArgument(0);
            u.setId(100L);
            return u;
        });

        UsuarioResponse response = usuarioService.crearUsuario(request, "admin");

        assertNotNull(response);
        assertEquals("coordinador1", response.getUsername());
        assertEquals(EstadoUsuario.PENDIENTE_ACTIVACION, response.getEstado());
        assertTrue(response.getDebeCambiarPassword());
        verify(usuarioRolRepository, times(1)).save(any(UsuarioRol.class));
        verify(auditoriaService, times(1)).registrarEvento(any());
    }

    @Test
    @DisplayName("crearUsuario para acudiente sin vínculo validado debe lanzar ConflictoException (RN-SEG-02, E2)")
    void testCrearUsuarioAcudienteSinVinculo() {
        CrearUsuarioRequest req = new CrearUsuarioRequest("acudiente.sin.hijo", "pass", Rol.ACUDIENTE, TipoPersonaUsuario.ACUDIENTE, 20L);
        when(usuarioRepository.existsByUsername("acudiente.sin.hijo")).thenReturn(false);

        Acudiente acudiente = new Acudiente();
        acudiente.setId(20L);
        when(acudienteRepository.findById(20L)).thenReturn(Optional.of(acudiente));
        when(estudianteAcudienteRepository.existsByAcudiente(acudiente)).thenReturn(false);

        assertThrows(ConflictoException.class, () -> usuarioService.crearUsuario(req, "admin"));
    }

    @Test
    @DisplayName("crearUsuario para estudiante con login deshabilitado institucionalmente debe lanzar ConflictoException (RN-SEG-08, E2)")
    void testCrearUsuarioEstudianteLoginDeshabilitado() {
        CrearUsuarioRequest req = new CrearUsuarioRequest("estudiante1", "pass", Rol.ESTUDIANTE, TipoPersonaUsuario.ESTUDIANTE, 30L);
        when(usuarioRepository.existsByUsername("estudiante1")).thenReturn(false);

        ParametroInstitucional p = new ParametroInstitucional("LOGIN_ESTUDIANTES_HABILITADO", "false", TipoDatoParametro.BOOLEAN, "Login", "ACCESO");
        when(parametroRepository.findByClave("LOGIN_ESTUDIANTES_HABILITADO")).thenReturn(Optional.of(p));

        assertThrows(ConflictoException.class, () -> usuarioService.crearUsuario(req, "admin"));
    }

    @Test
    @DisplayName("crearUsuario debe rechazar nombre de usuario duplicado con ConflictoException (E1)")
    void testCrearUsuarioDuplicado() {
        CrearUsuarioRequest request = new CrearUsuarioRequest("existente", "pass", Rol.PROFESOR, null, null);
        when(usuarioRepository.existsByUsername("existente")).thenReturn(true);

        assertThrows(ConflictoException.class, () -> usuarioService.crearUsuario(request, "admin"));
    }

    @Test
    @DisplayName("cambiarEstado a BLOQUEADA debe invalidar sesiones forzosamente en servidor (RN-SEG-06, A2)")
    void testCambiarEstadoBloqueadaInvalidaSesiones() {
        Usuario usuario = new Usuario("usuario1", "hash", Rol.ESTUDIANTE);
        usuario.setId(1L);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        ActualizarEstadoUsuarioRequest req = new ActualizarEstadoUsuarioRequest(EstadoUsuario.BLOQUEADA, "Sanción preventiva");
        UsuarioResponse resp = usuarioService.cambiarEstado(1L, req, "rector");

        assertEquals(EstadoUsuario.BLOQUEADA, resp.getEstado());
        verify(tokenRevocationService, times(1)).revokeUserTokens(eq(1L), any());
    }

    @Test
    @DisplayName("restablecerPresencial debe generar password temporal, marcar PENDIENTE_ACTIVACION y revocar sesiones (UC-PROV-03 A2)")
    void testRestablecerPresencial() {
        Usuario usuario = new Usuario("usuario1", "hashAnterior", Rol.ESTUDIANTE);
        usuario.setId(5L);
        usuario.setEstado(EstadoUsuario.ACTIVA);

        when(usuarioRepository.findById(5L)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.encode(anyString())).thenReturn("nuevoHashTemporal");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        RestablecimientoPresencialResponse resp = usuarioService.restablecerPresencial(5L, "secretaria1", "Entrega presencial de credencial");

        assertNotNull(resp);
        assertEquals(5L, resp.getUsuarioId());
        assertNotNull(resp.getPasswordTemporal());
        assertEquals(EstadoUsuario.PENDIENTE_ACTIVACION, usuario.getEstado());
        assertTrue(usuario.getDebeCambiarPassword());
        verify(tokenRevocationService, times(1)).revokeUserTokens(eq(5L), any());
        verify(auditoriaService, times(1)).registrarEvento(any());
    }

    @Test
    @DisplayName("eliminarUsuario debe prohibir borrado físico con ConflictoException (E4, RN-SEG-01)")
    void testEliminarUsuarioProhibido() {
        assertThrows(ConflictoException.class, () -> usuarioService.eliminarUsuario(1L, "admin"));
        verify(auditoriaService, times(1)).registrarEvento(any());
    }

    @Test
    @DisplayName("asignarRol con esPrincipal=true debe sincronizar usuario.setRol (Deuda Técnica D)")
    void testAsignarRolSincronizaRolPrincipal() {
        Usuario docente = new Usuario("docente1", "hash", Rol.PROFESOR);
        docente.setId(10L);

        when(usuarioRepository.findById(10L)).thenReturn(Optional.of(docente));
        when(usuarioRolRepository.save(any(UsuarioRol.class))).thenAnswer(i -> {
            UsuarioRol ur = i.getArgument(0);
            ur.setId(1L);
            return ur;
        });

        AsignarRolUsuarioRequest req = new AsignarRolUsuarioRequest();
        req.setRol(Rol.COORDINADOR_ACADEMICO);
        req.setEsPrincipal(true);
        req.setMotivoAsignacion("Nuevo rol principal");

        UsuarioRolResponse resp = usuarioService.asignarRol(10L, req, "rector");

        assertNotNull(resp);
        assertEquals(Rol.COORDINADOR_ACADEMICO, docente.getRol()); // Sincronizado en la entidad Usuario
        verify(usuarioRepository, times(1)).save(docente);
    }
}
