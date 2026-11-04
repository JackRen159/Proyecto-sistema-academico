package jackren.sistemaacademico.main.service;

import jackren.sistemaacademico.main.dto.EstudianteAcudienteRequest;
import jackren.sistemaacademico.main.dto.EstudianteAcudienteResponse;
import jackren.sistemaacademico.main.entity.Acudiente;
import jackren.sistemaacademico.main.entity.Estudiante;
import jackren.sistemaacademico.main.entity.EstudianteAcudiente;
import jackren.sistemaacademico.main.entity.Parentesco;
import jackren.sistemaacademico.main.exception.ConflictoNegocioException;
import jackren.sistemaacademico.main.exception.RecursoNoEncontradoException;
import jackren.sistemaacademico.main.mapper.EstudianteAcudienteMapper;
import jackren.sistemaacademico.main.repository.AcudienteRepository;
import jackren.sistemaacademico.main.repository.EstudianteAcudienteRepository;
import jackren.sistemaacademico.main.repository.EstudianteRepository;
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
class EstudianteAcudienteServiceTest {

    @Mock
    private EstudianteAcudienteRepository estudianteAcudienteRepository;

    @Mock
    private EstudianteRepository estudianteRepository;

    @Mock
    private AcudienteRepository acudienteRepository;

    @Spy
    private EstudianteAcudienteMapper estudianteAcudienteMapper;

    @InjectMocks
    private EstudianteAcudienteService estudianteAcudienteService;

    private Estudiante estudiante;
    private Acudiente acudiente;
    private EstudianteAcudienteRequest request;

    @BeforeEach
    void setUp() {
        estudiante = new Estudiante();
        estudiante.setDocumento("10203040");
        estudiante.setNombres("Carlos");
        estudiante.setApellidos("Gomez");
        estudiante.setActivo(true);

        acudiente = new Acudiente();
        acudiente.setDocumento("50607080");
        acudiente.setNombres("Maria");
        acudiente.setApellidos("Rodriguez");
        acudiente.setTelefono("3119876543");
        acudiente.setCorreo("maria@email.com");
        acudiente.setActivo(true);

        request = new EstudianteAcudienteRequest();
        request.setDocumentoEstudiante("10203040");
        request.setDocumentoAcudiente("50607080");
        request.setParentesco(Parentesco.MADRE);
        request.setEsPrincipal(false);
        request.setConviveConEstudiante(true);
        request.setAutorizadoRetiro(true);
    }

    @Test
    void asociar_PrimerAcudiente_DebeSerForzadoComoPrincipal() {
        when(estudianteRepository.findByDocumento("10203040")).thenReturn(Optional.of(estudiante));
        when(acudienteRepository.findByDocumento("50607080")).thenReturn(Optional.of(acudiente));
        when(estudianteAcudienteRepository.existsByEstudianteAndAcudiente(estudiante, acudiente)).thenReturn(false);
        when(estudianteAcudienteRepository.countByEstudiante(estudiante)).thenReturn(0L);
        when(estudianteAcudienteRepository.save(any(EstudianteAcudiente.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EstudianteAcudienteResponse response = estudianteAcudienteService.asociar(request);

        assertNotNull(response);
        assertTrue(response.getEsPrincipal(), "El primer acudiente debe ser asignado automaticamente como principal");
        assertTrue(response.getConviveConEstudiante());
        assertTrue(response.getAutorizadoRetiro());
        verify(estudianteAcudienteRepository, times(1)).save(any(EstudianteAcudiente.class));
    }

    @Test
    void asociar_CuandoYaExisteVinculo_DebeLanzarConflictoNegocioException() {
        when(estudianteRepository.findByDocumento("10203040")).thenReturn(Optional.of(estudiante));
        when(acudienteRepository.findByDocumento("50607080")).thenReturn(Optional.of(acudiente));
        when(estudianteAcudienteRepository.existsByEstudianteAndAcudiente(estudiante, acudiente)).thenReturn(true);

        ConflictoNegocioException ex = assertThrows(ConflictoNegocioException.class, () ->
                estudianteAcudienteService.asociar(request));

        assertTrue(ex.getMessage().contains("ya se encuentra vinculado"));
        verify(estudianteAcudienteRepository, never()).save(any(EstudianteAcudiente.class));
    }

    @Test
    void asociar_EstudianteInactivo_DebeLanzarConflictoNegocioException() {
        estudiante.setActivo(false);
        when(estudianteRepository.findByDocumento("10203040")).thenReturn(Optional.of(estudiante));

        ConflictoNegocioException ex = assertThrows(ConflictoNegocioException.class, () ->
                estudianteAcudienteService.asociar(request));

        assertTrue(ex.getMessage().contains("se encuentra inactivo"));
    }

    @Test
    void asociar_AcudienteInactivo_DebeLanzarConflictoNegocioException() {
        acudiente.setActivo(false);
        when(estudianteRepository.findByDocumento("10203040")).thenReturn(Optional.of(estudiante));
        when(acudienteRepository.findByDocumento("50607080")).thenReturn(Optional.of(acudiente));

        ConflictoNegocioException ex = assertThrows(ConflictoNegocioException.class, () ->
                estudianteAcudienteService.asociar(request));

        assertTrue(ex.getMessage().contains("se encuentra inactivo"));
    }

    @Test
    void asociar_EstudianteNoEncontrado_DebeLanzarRecursoNoEncontradoException() {
        when(estudianteRepository.findByDocumento("10203040")).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> estudianteAcudienteService.asociar(request));
    }

    @Test
    void asociar_NuevoPrincipal_DebeDesmarcarPrincipalAnterior() {
        request.setEsPrincipal(true);

        EstudianteAcudiente principalAnterior = new EstudianteAcudiente();
        principalAnterior.setEstudiante(estudiante);
        principalAnterior.setEsPrincipal(true);

        when(estudianteRepository.findByDocumento("10203040")).thenReturn(Optional.of(estudiante));
        when(acudienteRepository.findByDocumento("50607080")).thenReturn(Optional.of(acudiente));
        when(estudianteAcudienteRepository.existsByEstudianteAndAcudiente(estudiante, acudiente)).thenReturn(false);
        when(estudianteAcudienteRepository.countByEstudiante(estudiante)).thenReturn(1L);
        when(estudianteAcudienteRepository.findByEstudianteAndEsPrincipalTrue(estudiante)).thenReturn(Optional.of(principalAnterior));
        when(estudianteAcudienteRepository.save(any(EstudianteAcudiente.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EstudianteAcudienteResponse response = estudianteAcudienteService.asociar(request);

        assertNotNull(response);
        assertTrue(response.getEsPrincipal());
        assertFalse(principalAnterior.getEsPrincipal(), "El principal anterior debe quedar en false");
        verify(estudianteAcudienteRepository, times(1)).save(principalAnterior);
    }

    @Test
    void desvincular_AcudientePrincipalConOtrosAcudientes_DebeLanzarConflictoNegocioException() {
        EstudianteAcudiente relacionPrincipal = new EstudianteAcudiente();
        relacionPrincipal.setEstudiante(estudiante);
        relacionPrincipal.setAcudiente(acudiente);
        relacionPrincipal.setEsPrincipal(true);

        when(estudianteRepository.findByDocumento("10203040")).thenReturn(Optional.of(estudiante));
        when(acudienteRepository.findByDocumento("50607080")).thenReturn(Optional.of(acudiente));
        when(estudianteAcudienteRepository.findByEstudianteAndAcudiente(estudiante, acudiente)).thenReturn(Optional.of(relacionPrincipal));
        when(estudianteAcudienteRepository.countByEstudiante(estudiante)).thenReturn(2L);

        ConflictoNegocioException ex = assertThrows(ConflictoNegocioException.class, () ->
                estudianteAcudienteService.desvincular("10203040", "50607080"));

        assertTrue(ex.getMessage().contains("No se puede desvincular al acudiente principal"));
        verify(estudianteAcudienteRepository, never()).delete(any(EstudianteAcudiente.class));
    }

    @Test
    void desvincular_AcudienteSecundario_DebeEliminarExitosamente() {
        EstudianteAcudiente relacionSecundaria = new EstudianteAcudiente();
        relacionSecundaria.setEstudiante(estudiante);
        relacionSecundaria.setAcudiente(acudiente);
        relacionSecundaria.setEsPrincipal(false);

        when(estudianteRepository.findByDocumento("10203040")).thenReturn(Optional.of(estudiante));
        when(acudienteRepository.findByDocumento("50607080")).thenReturn(Optional.of(acudiente));
        when(estudianteAcudienteRepository.findByEstudianteAndAcudiente(estudiante, acudiente)).thenReturn(Optional.of(relacionSecundaria));

        assertDoesNotThrow(() -> estudianteAcudienteService.desvincular("10203040", "50607080"));
        verify(estudianteAcudienteRepository, times(1)).delete(relacionSecundaria);
    }

    @Test
    void cambiarAcudientePrincipal_DebeActualizarCorrectamente() {
        Acudiente nuevoAcudiente = new Acudiente();
        nuevoAcudiente.setDocumento("99887766");
        nuevoAcudiente.setNombres("Juan");
        nuevoAcudiente.setApellidos("Gomez");

        EstudianteAcudiente principalActual = new EstudianteAcudiente();
        principalActual.setEstudiante(estudiante);
        principalActual.setAcudiente(acudiente);
        principalActual.setEsPrincipal(true);

        EstudianteAcudiente nuevoPrincipal = new EstudianteAcudiente();
        nuevoPrincipal.setEstudiante(estudiante);
        nuevoPrincipal.setAcudiente(nuevoAcudiente);
        nuevoPrincipal.setEsPrincipal(false);

        when(estudianteRepository.findByDocumento("10203040")).thenReturn(Optional.of(estudiante));
        when(acudienteRepository.findByDocumento("99887766")).thenReturn(Optional.of(nuevoAcudiente));
        when(estudianteAcudienteRepository.findByEstudianteAndAcudiente(estudiante, nuevoAcudiente)).thenReturn(Optional.of(nuevoPrincipal));
        when(estudianteAcudienteRepository.findByEstudianteAndEsPrincipalTrue(estudiante)).thenReturn(Optional.of(principalActual));
        when(estudianteAcudienteRepository.save(any(EstudianteAcudiente.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EstudianteAcudienteResponse response = estudianteAcudienteService.cambiarAcudientePrincipal("10203040", "99887766");

        assertNotNull(response);
        assertTrue(nuevoPrincipal.getEsPrincipal());
        assertFalse(principalActual.getEsPrincipal());
        verify(estudianteAcudienteRepository, times(1)).save(principalActual);
        verify(estudianteAcudienteRepository, times(1)).save(nuevoPrincipal);
    }
}
