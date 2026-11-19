package com.jackren.sistemaacademico.service;

import com.jackren.sistemaacademico.dto.EstudianteRequest;
import com.jackren.sistemaacademico.dto.EstudianteResponse;
import com.jackren.sistemaacademico.entity.Estudiante;
import com.jackren.sistemaacademico.exception.ConflictoNegocioException;
import com.jackren.sistemaacademico.exception.RecursoNoEncontradoException;
import com.jackren.sistemaacademico.mapper.EstudianteMapper;
import com.jackren.sistemaacademico.repository.EstudianteAcudienteRepository;
import com.jackren.sistemaacademico.repository.EstudianteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EstudianteServiceTest {

    @Mock
    private EstudianteRepository estudianteRepository;

    @Mock
    private EstudianteAcudienteRepository estudianteAcudienteRepository;

    @Spy
    private EstudianteMapper estudianteMapper;

    @InjectMocks
    private EstudianteService estudianteService;

    private EstudianteRequest requestValido;

    @BeforeEach
    void setUp() {
        requestValido = new EstudianteRequest();
        requestValido.setNombres("Carlos");
        requestValido.setApellidos("Gomez");
        requestValido.setDocumento("10203040");
        requestValido.setFechaNacimiento(LocalDate.of(2005, 5, 15));
        requestValido.setCorreo("carlos.gomez@colegio.edu");
        requestValido.setTelefono("3001234567");
        requestValido.setDireccion("Calle 10 # 20-30");
    }

    @Test
    void guardar_CuandoDatosSonValidos_DebeGuardarExitosamente() {
        when(estudianteRepository.existsByDocumento("10203040")).thenReturn(false);
        when(estudianteRepository.existsByCorreo("carlos.gomez@colegio.edu")).thenReturn(false);
        when(estudianteRepository.save(any(Estudiante.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EstudianteResponse response = estudianteService.guardar(requestValido);

        assertNotNull(response);
        assertEquals("Carlos", response.getNombres());
        assertEquals("10203040", response.getDocumento());
        assertTrue(response.getActivo());
        verify(estudianteRepository, times(1)).save(any(Estudiante.class));
    }

    @Test
    void guardar_CuandoDocumentoYaExiste_DebeLanzarConflictoNegocioException() {
        when(estudianteRepository.existsByDocumento("10203040")).thenReturn(true);

        ConflictoNegocioException ex = assertThrows(ConflictoNegocioException.class, () -> {
            estudianteService.guardar(requestValido);
        });

        assertTrue(ex.getMessage().contains("Ya existe un estudiante con el documento"));
        verify(estudianteRepository, never()).save(any(Estudiante.class));
    }

    @Test
    void guardar_CuandoCorreoYaExiste_DebeLanzarConflictoNegocioException() {
        when(estudianteRepository.existsByDocumento("10203040")).thenReturn(false);
        when(estudianteRepository.existsByCorreo("carlos.gomez@colegio.edu")).thenReturn(true);

        ConflictoNegocioException ex = assertThrows(ConflictoNegocioException.class, () -> {
            estudianteService.guardar(requestValido);
        });

        assertTrue(ex.getMessage().contains("Ya existe un estudiante con el correo"));
        verify(estudianteRepository, never()).save(any(Estudiante.class));
    }

    @Test
    void eliminarPermanente_DebeProhibirBorradoFisicoConConflictoNegocioException() {
        ConflictoNegocioException ex = assertThrows(ConflictoNegocioException.class, () -> {
            estudianteService.eliminarPermanente(1L, true);
        });

        assertTrue(ex.getMessage().contains("Prohibido el borrado físico"));
        verify(estudianteRepository, never()).delete(any());
    }

    @Test
    void desactivar_DebeCambiarEstadoActivoAFalso() {
        Estudiante estudiante = new Estudiante();
        estudiante.setActivo(true);
        when(estudianteRepository.findById(1L)).thenReturn(Optional.of(estudiante));

        estudianteService.desactivar(1L);

        assertFalse(estudiante.getActivo());
        verify(estudianteRepository, times(1)).save(estudiante);
    }

    @Test
    void desactivar_CuandoNoExiste_DebeLanzarRecursoNoEncontradoException() {
        when(estudianteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> estudianteService.desactivar(99L));
    }

    @Test
    void restaurar_DebeCambiarEstadoActivoAVerdadero() {
        Estudiante estudiante = new Estudiante();
        estudiante.setActivo(false);
        when(estudianteRepository.findById(1L)).thenReturn(Optional.of(estudiante));

        estudianteService.restaurar(1L);

        assertTrue(estudiante.getActivo());
        verify(estudianteRepository, times(1)).save(estudiante);
    }

    @Test
    void buscarPorDocumento_CuandoExiste_DebeRetornarEstudiante() {
        Estudiante estudiante = new Estudiante();
        estudiante.setDocumento("10203040");
        estudiante.setNombres("Carlos");
        when(estudianteRepository.findByDocumento("10203040")).thenReturn(Optional.of(estudiante));

        Optional<EstudianteResponse> resultado = estudianteService.buscarPorDocumento("10203040");

        assertTrue(resultado.isPresent());
        assertEquals("10203040", resultado.get().getDocumento());
        assertEquals("Carlos", resultado.get().getNombres());
        verify(estudianteRepository, times(1)).findByDocumento("10203040");
    }
}
