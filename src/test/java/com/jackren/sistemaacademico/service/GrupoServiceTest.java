package com.jackren.sistemaacademico.service;

import com.jackren.sistemaacademico.dto.GrupoRequest;
import com.jackren.sistemaacademico.dto.GrupoResponse;
import com.jackren.sistemaacademico.entity.*;
import com.jackren.sistemaacademico.exception.ConflictoNegocioException;
import com.jackren.sistemaacademico.mapper.GrupoMapper;
import com.jackren.sistemaacademico.repository.AnioLectivoRepository;
import com.jackren.sistemaacademico.repository.GradoRepository;
import com.jackren.sistemaacademico.repository.GrupoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
@DisplayName("Pruebas Unitarias - GrupoService (Invariantes de Cursos y Salones)")
class GrupoServiceTest {

    @Mock
    private GrupoRepository grupoRepository;

    @Mock
    private AnioLectivoRepository anioLectivoRepository;

    @Mock
    private GradoRepository gradoRepository;

    @Mock
    private com.jackren.sistemaacademico.repository.MatriculaRepository matriculaRepository;

    @Mock
    private AuditoriaGlobalService auditoriaGlobalService;

    @Spy
    private GrupoMapper grupoMapper = new GrupoMapper();

    @InjectMocks
    private GrupoService grupoService;

    private AnioLectivo anio;
    private Grado grado;
    private Grupo grupo;

    @BeforeEach
    void setUp() {
        anio = new AnioLectivo(2026, "Año 2026", LocalDate.of(2026, 1, 15), LocalDate.of(2026, 11, 30));
        anio.setId(1L);
        anio.setEstado(EstadoAnioLectivo.ACTIVO);

        grado = new Grado("10", "Décimo", NivelEducativo.MEDIA, 10);
        grado.setId(5L);

        grupo = new Grupo(anio, grado, "A", Jornada.MANANA, 35);
        grupo.setId(20L);
    }

    @Test
    @DisplayName("Debe guardar un grupo si los datos son correctos y no está duplicado")
    void debeGuardarGrupoExitosamente() {
        GrupoRequest request = new GrupoRequest(1L, 5L, "A", Jornada.MANANA, 35);

        when(anioLectivoRepository.findById(1L)).thenReturn(Optional.of(anio));
        when(gradoRepository.findById(5L)).thenReturn(Optional.of(grado));
        when(grupoRepository.existsByAnioLectivoIdAndGradoIdAndNombre(1L, 5L, "A")).thenReturn(false);
        when(grupoRepository.save(any(Grupo.class))).thenReturn(grupo);

        GrupoResponse response = grupoService.guardar(request);

        assertNotNull(response);
        assertEquals("A", response.getNombre());
        assertEquals(Jornada.MANANA, response.getJornada());
        assertEquals(35, response.getCupoMaximo());
    }

    @Test
    @DisplayName("Debe fallar al intentar crear grupo en un año lectivo CERRADO")
    void debeFallarSiAnioLectivoEstaCerrado() {
        anio.setEstado(EstadoAnioLectivo.CERRADO);
        GrupoRequest request = new GrupoRequest(1L, 5L, "A", Jornada.MANANA, 35);

        when(anioLectivoRepository.findById(1L)).thenReturn(Optional.of(anio));

        ConflictoNegocioException ex = assertThrows(ConflictoNegocioException.class, () -> grupoService.guardar(request));
        assertTrue(ex.getMessage().contains("CERRADO"));
    }

    @Test
    @DisplayName("Debe fallar si ya existe un grupo con el mismo nombre para el grado y año lectivo")
    void debeFallarSiGrupoYaExiste() {
        GrupoRequest request = new GrupoRequest(1L, 5L, "A", Jornada.MANANA, 35);

        when(anioLectivoRepository.findById(1L)).thenReturn(Optional.of(anio));
        when(gradoRepository.findById(5L)).thenReturn(Optional.of(grado));
        when(grupoRepository.existsByAnioLectivoIdAndGradoIdAndNombre(1L, 5L, "A")).thenReturn(true);

        assertThrows(ConflictoNegocioException.class, () -> grupoService.guardar(request));
    }
}
