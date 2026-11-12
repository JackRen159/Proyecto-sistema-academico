package com.jackren.sistemaacademico.service;

import com.jackren.sistemaacademico.dto.AsignaturaRequest;
import com.jackren.sistemaacademico.dto.AsignaturaResponse;
import com.jackren.sistemaacademico.entity.Area;
import com.jackren.sistemaacademico.entity.Asignatura;
import com.jackren.sistemaacademico.exception.ConflictoNegocioException;
import com.jackren.sistemaacademico.mapper.AsignaturaMapper;
import com.jackren.sistemaacademico.repository.AreaRepository;
import com.jackren.sistemaacademico.repository.AsignaturaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
@DisplayName("Pruebas Unitarias - AsignaturaService (Catálogo Curricular Adaptable S2)")
class AsignaturaServiceTest {

    @Mock
    private AsignaturaRepository asignaturaRepository;

    @Mock
    private AreaRepository areaRepository;

    @Spy
    private AsignaturaMapper asignaturaMapper = new AsignaturaMapper();

    @InjectMocks
    private AsignaturaService asignaturaService;

    private Area areaComercial;
    private Asignatura contabilidad;

    @BeforeEach
    void setUp() {
        areaComercial = new Area("COM", "Ciencias Económicas", "Área de formación comercial");
        areaComercial.setId(1L);

        contabilidad = new Asignatura(areaComercial, "CONT-01", "Contabilidad Básica", "Asignatura adaptable");
        contabilidad.setId(10L);
    }

    @Test
    @DisplayName("Debe permitir crear una nueva asignatura adaptable vinculada a un área")
    void debeGuardarAsignaturaAdaptable() {
        AsignaturaRequest request = new AsignaturaRequest(1L, "cont-01", "Contabilidad Básica", "Asignatura adaptable");

        when(asignaturaRepository.existsByCodigo("CONT-01")).thenReturn(false);
        when(areaRepository.findById(1L)).thenReturn(Optional.of(areaComercial));
        when(asignaturaRepository.save(any(Asignatura.class))).thenReturn(contabilidad);

        AsignaturaResponse response = asignaturaService.guardar(request);

        assertNotNull(response);
        assertEquals("CONT-01", response.getCodigo());
        assertEquals("Contabilidad Básica", response.getNombre());
        assertEquals(1L, response.getAreaId());
        assertTrue(response.getActivo());
    }

    @Test
    @DisplayName("Debe fallar al crear asignatura con código duplicado")
    void debeFallarSiCodigoDuplicado() {
        AsignaturaRequest request = new AsignaturaRequest(1L, "CONT-01", "Contabilidad", null);

        when(asignaturaRepository.existsByCodigo("CONT-01")).thenReturn(true);

        assertThrows(ConflictoNegocioException.class, () -> asignaturaService.guardar(request));
        verify(asignaturaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe permitir desactivar lógicamente una asignatura sin eliminar histórico")
    void debeDesactivarAsignaturaLogicamente() {
        when(asignaturaRepository.findById(10L)).thenReturn(Optional.of(contabilidad));
        when(asignaturaRepository.save(any(Asignatura.class))).thenReturn(contabilidad);

        asignaturaService.desactivar(10L);

        assertFalse(contabilidad.getActivo());
    }
}
