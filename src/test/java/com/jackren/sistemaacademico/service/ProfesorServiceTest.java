package com.jackren.sistemaacademico.service;

import com.jackren.sistemaacademico.dto.ProfesorRequest;
import com.jackren.sistemaacademico.dto.ProfesorResponse;
import com.jackren.sistemaacademico.entity.Profesor;
import com.jackren.sistemaacademico.entity.TipoDocumento;
import com.jackren.sistemaacademico.exception.ConflictoNegocioException;
import com.jackren.sistemaacademico.exception.RecursoNoEncontradoException;
import com.jackren.sistemaacademico.mapper.ProfesorMapper;
import com.jackren.sistemaacademico.repository.ProfesorRepository;
import com.jackren.sistemaacademico.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfesorServiceTest {

    @Mock
    private ProfesorRepository profesorRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Spy
    private ProfesorMapper profesorMapper;

    @InjectMocks
    private ProfesorService profesorService;

    private ProfesorRequest requestValido;

    @BeforeEach
    void setUp() {
        requestValido = new ProfesorRequest();
        requestValido.setTipoDocumento(TipoDocumento.CC);
        requestValido.setDocumento("1020304050");
        requestValido.setNombres("Carlos");
        requestValido.setApellidos("Gómez");
        requestValido.setCorreo("carlos.gomez@colegio.edu.co");
        requestValido.setTelefono("3001234567");
        requestValido.setTituloAcademico("Licenciado en Matemáticas");
        requestValido.setEspecialidad("Álgebra");
    }

    @Test
    @DisplayName("Debe crear profesor exitosamente cuando los datos son válidos")
    void debeCrearProfesorExitosamente() {
        when(profesorRepository.existsByDocumento(requestValido.getDocumento())).thenReturn(false);
        when(profesorRepository.existsByCorreo(requestValido.getCorreo())).thenReturn(false);
        when(profesorRepository.save(any(Profesor.class))).thenAnswer(invocation -> {
            Profesor p = invocation.getArgument(0);
            p.setId(1L);
            return p;
        });

        ProfesorResponse response = profesorService.crear(requestValido);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getDocumento()).isEqualTo("1020304050");
        assertThat(response.getNombreCompleto()).isEqualTo("Carlos Gómez");
        assertThat(response.getActivo()).isTrue();
    }

    @Test
    @DisplayName("Debe lanzar excepción si el documento ya está registrado")
    void debeLanzarExcepcionPorDocumentoDuplicado() {
        when(profesorRepository.existsByDocumento(requestValido.getDocumento())).thenReturn(true);

        assertThatThrownBy(() -> profesorService.crear(requestValido))
                .isInstanceOf(ConflictoNegocioException.class)
                .hasMessageContaining("Ya existe un profesor registrado con el documento");

        verify(profesorRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar excepción si el correo ya está registrado")
    void debeLanzarExcepcionPorCorreoDuplicado() {
        when(profesorRepository.existsByDocumento(requestValido.getDocumento())).thenReturn(false);
        when(profesorRepository.existsByCorreo(requestValido.getCorreo())).thenReturn(true);

        assertThatThrownBy(() -> profesorService.crear(requestValido))
                .isInstanceOf(ConflictoNegocioException.class)
                .hasMessageContaining("Ya existe un profesor registrado con el correo");

        verify(profesorRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe desactivar profesor lógicamente")
    void debeDesactivarProfesorLogicamente() {
        Profesor profesor = new Profesor();
        profesor.setId(1L);
        profesor.setActivo(true);

        when(profesorRepository.findById(1L)).thenReturn(Optional.of(profesor));

        profesorService.desactivar(1L);

        assertThat(profesor.getActivo()).isFalse();
        verify(profesorRepository).save(profesor);
    }
}
