package com.jackren.sistemaacademico.service;

import com.jackren.sistemaacademico.dto.*;
import com.jackren.sistemaacademico.entity.*;
import com.jackren.sistemaacademico.exception.ConflictoException;
import com.jackren.sistemaacademico.exception.ValidacionException;
import com.jackren.sistemaacademico.repository.AnioLectivoRepository;
import com.jackren.sistemaacademico.repository.OrganoColegiadoMiembroRepository;
import com.jackren.sistemaacademico.repository.UsuarioRepository;
import com.jackren.sistemaacademico.repository.UsuarioRolRepository;
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
    @DisplayName("crearUsuario debe rechazar nombre de usuario duplicado con ConflictoException (E1)")
    void testCrearUsuarioDuplicado() {
        CrearUsuarioRequest request = new CrearUsuarioRequest("existente", "pass", Rol.ESTUDIANTE, null, null);
        when(usuarioRepository.existsByUsername("existente")).thenReturn(true);

        assertThrows(ConflictoException.class, () -> usuarioService.crearUsuario(request, "admin"));
    }

    @Test
    @DisplayName("cambiarEstado debe exigir motivo obligatorio (A2)")
    void testCambiarEstadoExigeMotivo() {
        Usuario usuario = new Usuario("usuario1", "hash", Rol.ESTUDIANTE);
        usuario.setId(1L);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        ActualizarEstadoUsuarioRequest sinMotivo = new ActualizarEstadoUsuarioRequest(EstadoUsuario.BLOQUEADA, "");
        assertThrows(ValidacionException.class, () -> usuarioService.cambiarEstado(1L, sinMotivo, "admin"));

        ActualizarEstadoUsuarioRequest conMotivo = new ActualizarEstadoUsuarioRequest(EstadoUsuario.BLOQUEADA, "Incumplimiento de normas");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        UsuarioResponse resp = usuarioService.cambiarEstado(1L, conMotivo, "admin");
        assertEquals(EstadoUsuario.BLOQUEADA, resp.getEstado());
    }

    @Test
    @DisplayName("eliminarUsuario debe prohibir borrado físico con ConflictoException (E4, RN-SEG-01)")
    void testEliminarUsuarioProhibido() {
        assertThrows(ConflictoException.class, () -> usuarioService.eliminarUsuario(1L, "admin"));
        verify(auditoriaService, times(1)).registrarEvento(any());
    }

    @Test
    @DisplayName("asignarRol debe impedir que estudiante adquiera roles directivos o docentes (E1)")
    void testAsignarRolIncompatibilidadEstudiante() {
        Usuario estudiante = new Usuario("estudiante1", "hash", Rol.ESTUDIANTE);
        estudiante.setId(2L);
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(estudiante));

        AsignarRolUsuarioRequest req = new AsignarRolUsuarioRequest(Rol.PROFESOR, null, "Asignación indebida");
        assertThrows(ConflictoException.class, () -> usuarioService.asignarRol(2L, req, "admin"));
    }

    @Test
    @DisplayName("revocarRol debe rechazar la revocación del último administrador activo (E3)")
    void testRevocarUltimoAdminRechazado() {
        UsuarioRol rolAdmin = new UsuarioRol();
        rolAdmin.setId(5L);
        rolAdmin.setRol(Rol.ADMINISTRADOR);
        rolAdmin.setActivo(true);
        rolAdmin.setUsuario(new Usuario("admin1", "hash", Rol.ADMINISTRADOR));

        when(usuarioRolRepository.findById(5L)).thenReturn(Optional.of(rolAdmin));
        when(usuarioRolRepository.countByRolAndActivoTrue(Rol.ADMINISTRADOR)).thenReturn(1L);

        assertThrows(ConflictoException.class, () -> usuarioService.revocarRol(5L, "rector"));
    }

    @Test
    @DisplayName("asignarMiembroOrganoColegiado debe registrar pertenencia nominativa con soporte de acta (A2)")
    void testAsignarMiembroOrgano() {
        Usuario docente = new Usuario("docente1", "hash", Rol.PROFESOR);
        docente.setId(3L);
        AnioLectivo anio = new AnioLectivo();
        anio.setId(2026L);
        anio.setNombre("Año 2026");

        when(usuarioRepository.findById(3L)).thenReturn(Optional.of(docente));
        when(anioLectivoRepository.findById(2026L)).thenReturn(Optional.of(anio));
        when(organoRepository.findByTipoOrganoAndUsuarioIdAndAnioLectivoId(any(), any(), any())).thenReturn(Optional.empty());
        when(organoRepository.save(any(OrganoColegiadoMiembro.class))).thenAnswer(i -> {
            OrganoColegiadoMiembro m = i.getArgument(0);
            m.setId(10L);
            return m;
        });

        AsignarOrganoColegiadoRequest req = new AsignarOrganoColegiadoRequest(
                TipoOrganoColegiado.COMITE_CONVIVENCIA, "Acta 01-2026", 2026L);

        OrganoColegiadoMiembroResponse resp = usuarioService.asignarMiembroOrganoColegiado(3L, req, "rector");

        assertNotNull(resp);
        assertEquals(TipoOrganoColegiado.COMITE_CONVIVENCIA, resp.getTipoOrgano());
        assertEquals("Acta 01-2026", resp.getActaDesignacion());
        assertTrue(resp.getActivo());
    }
}
