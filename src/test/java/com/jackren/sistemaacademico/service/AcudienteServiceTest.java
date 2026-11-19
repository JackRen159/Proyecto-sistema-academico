package com.jackren.sistemaacademico.service;

import com.jackren.sistemaacademico.dto.AcudienteRequest;
import com.jackren.sistemaacademico.dto.AcudienteResponse;
import com.jackren.sistemaacademico.entity.Acudiente;
import com.jackren.sistemaacademico.exception.ConflictoNegocioException;
import com.jackren.sistemaacademico.exception.RecursoNoEncontradoException;
import com.jackren.sistemaacademico.mapper.AcudienteMapper;
import com.jackren.sistemaacademico.repository.AcudienteRepository;
import com.jackren.sistemaacademico.repository.EstudianteAcudienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AcudienteServiceTest {

    @Mock
    private AcudienteRepository acudienteRepository;

    @Mock
    private EstudianteAcudienteRepository estudianteAcudienteRepository;

    @Spy
    private AcudienteMapper acudienteMapper;

    @InjectMocks
    private AcudienteService acudienteService;

    private AcudienteRequest requestValido;

    @BeforeEach
    void setUp() {
        requestValido = new AcudienteRequest();
        requestValido.setNombres("Maria");
        requestValido.setApellidos("Rodriguez");
        requestValido.setDocumento("50607080");
        requestValido.setTelefono("3119876543");
        requestValido.setTelefonoAlternativo("6012345678");
        requestValido.setCorreo("maria.rodriguez@email.com");
        requestValido.setDireccion("Calle 10 # 20 - 30");
        requestValido.setOcupacion("Ingeniera");
    }

    @Test
    void guardar_CuandoDatosSonValidos_DebeGuardarExitosamente() {
        when(acudienteRepository.existsByDocumento("50607080")).thenReturn(false);
        when(acudienteRepository.existsByCorreo("maria.rodriguez@email.com")).thenReturn(false);
        when(acudienteRepository.save(any(Acudiente.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AcudienteResponse response = acudienteService.guardar(requestValido);

        assertNotNull(response);
        assertEquals("Maria", response.getNombres());
        assertEquals("50607080", response.getDocumento());
        assertEquals("3119876543", response.getTelefono());
        assertTrue(response.getActivo());
        verify(acudienteRepository, times(1)).save(any(Acudiente.class));
    }

    @Test
    void guardar_CuandoDocumentoYaExiste_DebeLanzarConflictoNegocioException() {
        when(acudienteRepository.existsByDocumento("50607080")).thenReturn(true);

        ConflictoNegocioException ex = assertThrows(ConflictoNegocioException.class, () -> {
            acudienteService.guardar(requestValido);
        });

        assertTrue(ex.getMessage().contains("Ya existe un acudiente con el documento"));
        verify(acudienteRepository, never()).save(any(Acudiente.class));
    }

    @Test
    void guardar_CuandoCorreoYaExiste_DebeLanzarConflictoNegocioException() {
        when(acudienteRepository.existsByDocumento("50607080")).thenReturn(false);
        when(acudienteRepository.existsByCorreo("maria.rodriguez@email.com")).thenReturn(true);

        ConflictoNegocioException ex = assertThrows(ConflictoNegocioException.class, () -> {
            acudienteService.guardar(requestValido);
        });

        assertTrue(ex.getMessage().contains("Ya existe un acudiente con el correo"));
        verify(acudienteRepository, never()).save(any(Acudiente.class));
    }

    @Test
    void actualizar_CuandoCorreoEsNull_NoDebeLanzarNullPointerException() {
        Acudiente acudienteExistente = new Acudiente();
        acudienteExistente.setDocumento("50607080");
        acudienteExistente.setCorreo("antiguo@email.com");
        acudienteExistente.setNombres("Maria");
        acudienteExistente.setApellidos("Rodriguez");
        acudienteExistente.setTelefono("3119876543");
        acudienteExistente.setDireccion("Calle 10 # 20 - 30");
        acudienteExistente.setOcupacion("Ingeniera");

        requestValido.setCorreo(null); // Opcional según DM V2.0 §1.3

        when(acudienteRepository.findById(1L)).thenReturn(Optional.of(acudienteExistente));
        when(acudienteRepository.save(any(Acudiente.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assertDoesNotThrow(() -> {
            AcudienteResponse response = acudienteService.actualizar(1L, requestValido);
            assertNull(response.getCorreo());
        });
    }

    @Test
    void desactivar_CuandoNoEsPrincipalDeEstudianteActivo_DebeCambiarEstadoActivoAFalso() {
        Acudiente acudiente = new Acudiente();
        acudiente.setActivo(true);
        when(acudienteRepository.findById(1L)).thenReturn(Optional.of(acudiente));
        when(estudianteAcudienteRepository.existsByAcudienteAndEsPrincipalTrueAndEstudianteActivoTrue(acudiente)).thenReturn(false);

        acudienteService.desactivar(1L);

        assertFalse(acudiente.getActivo());
        verify(acudienteRepository, times(1)).save(acudiente);
    }

    @Test
    void desactivar_CuandoEsPrincipalDeEstudianteActivo_DebeLanzarConflictoNegocioException() {
        Acudiente acudiente = new Acudiente();
        acudiente.setActivo(true);
        when(acudienteRepository.findById(1L)).thenReturn(Optional.of(acudiente));
        when(estudianteAcudienteRepository.existsByAcudienteAndEsPrincipalTrueAndEstudianteActivoTrue(acudiente)).thenReturn(true);

        ConflictoNegocioException ex = assertThrows(ConflictoNegocioException.class, () -> {
            acudienteService.desactivar(1L);
        });

        assertTrue(ex.getMessage().contains("es el acudiente principal de uno o mas estudiantes activos"));
        verify(acudienteRepository, never()).save(acudiente);
    }

    @Test
    void restaurar_DebeCambiarEstadoActivoAVerdadero() {
        Acudiente acudiente = new Acudiente();
        acudiente.setActivo(false);
        when(acudienteRepository.findById(1L)).thenReturn(Optional.of(acudiente));

        acudienteService.restaurar(1L);

        assertTrue(acudiente.getActivo());
        verify(acudienteRepository, times(1)).save(acudiente);
    }

    @Test
    void eliminarPermanente_DebeProhibirBorradoFisicoConConflictoNegocioException() {
        ConflictoNegocioException ex = assertThrows(ConflictoNegocioException.class, () -> {
            acudienteService.eliminarPermanente(1L, true);
        });

        assertTrue(ex.getMessage().contains("Prohibido el borrado físico"));
        verify(acudienteRepository, never()).delete(any());
    }

    @Test
    void buscarPorDocumento_CuandoExiste_DebeRetornarAcudiente() {
        Acudiente acudiente = new Acudiente();
        acudiente.setDocumento("50607080");
        acudiente.setNombres("Maria");
        when(acudienteRepository.findByDocumento("50607080")).thenReturn(Optional.of(acudiente));

        Optional<AcudienteResponse> resultado = acudienteService.buscarPorDocumento("50607080");

        assertTrue(resultado.isPresent());
        assertEquals("50607080", resultado.get().getDocumento());
        assertEquals("Maria", resultado.get().getNombres());
        verify(acudienteRepository, times(1)).findByDocumento("50607080");
    }
}
