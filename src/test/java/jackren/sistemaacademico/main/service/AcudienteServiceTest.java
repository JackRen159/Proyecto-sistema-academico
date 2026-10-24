package jackren.sistemaacademico.main.service;

import jackren.sistemaacademico.main.dto.AcudienteRequest;
import jackren.sistemaacademico.main.dto.AcudienteResponse;
import jackren.sistemaacademico.main.entity.Acudiente;
import jackren.sistemaacademico.main.mapper.AcudienteMapper;
import jackren.sistemaacademico.main.repository.AcudienteRepository;
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
    void guardar_CuandoDocumentoYaExiste_DebeLanzarExcepcion() {
        when(acudienteRepository.existsByDocumento("50607080")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            acudienteService.guardar(requestValido);
        });

        assertTrue(ex.getMessage().contains("Ya existe un acudiente con el documento"));
        verify(acudienteRepository, never()).save(any(Acudiente.class));
    }

    @Test
    void guardar_CuandoCorreoYaExiste_DebeLanzarExcepcion() {
        when(acudienteRepository.existsByDocumento("50607080")).thenReturn(false);
        when(acudienteRepository.existsByCorreo("maria.rodriguez@email.com")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            acudienteService.guardar(requestValido);
        });

        assertTrue(ex.getMessage().contains("Ya existe un acudiente con el correo"));
        verify(acudienteRepository, never()).save(any(Acudiente.class));
    }

    @Test
    void desactivar_DebeCambiarEstadoActivoAFalso() {
        Acudiente acudiente = new Acudiente();
        acudiente.setActivo(true);
        when(acudienteRepository.findById(1L)).thenReturn(Optional.of(acudiente));

        acudienteService.desactivar(1L);

        assertFalse(acudiente.getActivo());
        verify(acudienteRepository, times(1)).save(acudiente);
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
    void eliminarPermanente_SinConfirmacion_DebeLanzarExcepcion() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            acudienteService.eliminarPermanente(1L, false);
        });

        assertTrue(ex.getMessage().contains("Debe confirmar explícitamente"));
        verify(acudienteRepository, never()).deleteById(anyLong());
    }

    @Test
    void eliminarPermanente_ConConfirmacion_DebeEliminar() {
        when(acudienteRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> acudienteService.eliminarPermanente(1L, true));

        verify(acudienteRepository, times(1)).deleteById(1L);
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
