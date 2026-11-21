package com.jackren.sistemaacademico.service;

import com.jackren.sistemaacademico.dto.*;
import com.jackren.sistemaacademico.entity.*;
import com.jackren.sistemaacademico.exception.ConflictoNegocioException;
import com.jackren.sistemaacademico.exception.ValidacionException;
import com.jackren.sistemaacademico.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias - Repositorio Documental Seguro y TRD (UC-PROV-28)")
class RepositorioDocumentalServiceTest {

    @Mock private RepositorioDocumentoRepository documentoRepository;
    @Mock private RepositorioDocumentoVersionRepository versionRepository;
    @Mock private TablaRetencionDocumentalRepository trdRepository;
    @Mock private EstudianteRepository estudianteRepository;
    @Mock private MatriculaRepository matriculaRepository;
    @Mock private AnioLectivoRepository anioLectivoRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private DocumentoAcademicoService documentoAcademicoService;
    @Mock private AuditoriaGlobalService auditoriaGlobalService;

    private RepositorioDocumentalService repositorioService;
    private Usuario usuarioAdmin;
    private Usuario usuarioEstudiante;

    @BeforeEach
    void setUp() {
        repositorioService = new RepositorioDocumentalService(
                documentoRepository,
                versionRepository,
                trdRepository,
                estudianteRepository,
                matriculaRepository,
                anioLectivoRepository,
                usuarioRepository,
                documentoAcademicoService,
                auditoriaGlobalService
        );

        usuarioAdmin = new Usuario("admin1", "hash", Rol.ADMINISTRADOR);
        usuarioAdmin.setId(1L);

        usuarioEstudiante = new Usuario("estudiante1", "hash", Rol.ESTUDIANTE);
        usuarioEstudiante.setId(2L);
        usuarioEstudiante.setPersonaId(10L);
    }

    @Test
    @DisplayName("UC-PROV-28: Carga inicial de documento genera código, hash SHA-256 y versión 1")
    void cargarDocumento_Exitoso() {
        CargarDocumentoRequest request = new CargarDocumentoRequest(
                "Certificado de Estudio 2026", "Certificado oficial anual",
                "CERTIFICADO_ESTUDIO", NivelConfidencialidadDocumental.ACADEMICO_ESTANDAR,
                10L, 50L, 1L, null, "certificado.pdf", "application/pdf",
                2048L, "/storage/cert.pdf", "SGVsbG8gV29ybGQ="
        );

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioAdmin));
        when(documentoRepository.save(any(RepositorioDocumento.class))).thenAnswer(i -> {
            RepositorioDocumento d = i.getArgument(0);
            d.setId(100L);
            return d;
        });

        RepositorioDocumentoResponse response = repositorioService.cargarDocumento(request, 1L);

        assertNotNull(response);
        assertEquals(1, response.versionActual());
        assertNotNull(response.hashSha256Actual());
        assertEquals(EstadoCicloVidaDocumental.PUBLICADO_VIGENTE, response.estadoCicloVida());
        verify(versionRepository).save(any(RepositorioDocumentoVersion.class));
    }

    @Test
    @DisplayName("UC-PROV-28 E1: Rechazo de archivos ejecutables o peligrosos (.exe, .sh)")
    void cargarDocumento_ArchivoPeligroso_LanzaValidacionException() {
        CargarDocumentoRequest request = new CargarDocumentoRequest(
                "Script malicioso", "Script",
                "SOPORTE", NivelConfidencialidadDocumental.ADMINISTRATIVO_EXPEDIENTE,
                null, null, null, null, "malware.exe", "application/x-msdownload",
                1024L, null, null
        );

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioAdmin));

        assertThrows(ValidacionException.class, () -> repositorioService.cargarDocumento(request, 1L));
    }

    @Test
    @DisplayName("UC-PROV-28 A1: Versionamiento inmutable incrementa versión a V2 con nuevo hash")
    void crearNuevaVersion_Exitoso() {
        RepositorioDocumento doc = new RepositorioDocumento();
        doc.setId(100L);
        doc.setCodigoDocumento("DOC-2026-0001");
        doc.setVersionActual(1);
        doc.setHashSha256Actual("hash_v1");
        doc.setEstadoCicloVida(EstadoCicloVidaDocumental.PUBLICADO_VIGENTE);

        NuevaVersionDocumentoRequest request = new NuevaVersionDocumentoRequest(
                "certificado_v2.pdf", "application/pdf", 3000L,
                "/storage/cert_v2.pdf", "Regeneración tras rectificación", "U29tZSBuZXcgZGF0YQ=="
        );

        when(documentoRepository.findById(100L)).thenReturn(Optional.of(doc));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioAdmin));
        when(versionRepository.save(any(RepositorioDocumentoVersion.class))).thenAnswer(i -> {
            RepositorioDocumentoVersion v = i.getArgument(0);
            v.setId(201L);
            return v;
        });

        RepositorioDocumentoVersionResponse response = repositorioService.crearNuevaVersion(100L, request, 1L);

        assertNotNull(response);
        assertEquals(2, response.numeroVersion());
        assertEquals(2, doc.getVersionActual());
    }

    @Test
    @DisplayName("UC-PROV-28 E3: Prohibición absoluta de borrado físico (Ley 594 de 2000)")
    void eliminarDocumentoFisico_LanzaConflictoNegocioException() {
        assertThrows(ConflictoNegocioException.class, () -> repositorioService.eliminarDocumentoFisico(100L));
    }

    @Test
    @DisplayName("UC-PROV-28 A2: Anulación lógica motivada permitida")
    void anularDocumentoLogico_Exitoso() {
        RepositorioDocumento doc = new RepositorioDocumento();
        doc.setId(100L);
        doc.setCodigoDocumento("DOC-2026-0001");
        doc.setEstadoCicloVida(EstadoCicloVidaDocumental.PUBLICADO_VIGENTE);
        doc.setUsuarioCreador(usuarioAdmin);

        when(documentoRepository.findById(100L)).thenReturn(Optional.of(doc));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioAdmin));
        when(documentoRepository.save(any(RepositorioDocumento.class))).thenAnswer(i -> i.getArgument(0));

        RepositorioDocumentoResponse response = repositorioService.anularDocumentoLogico(100L, "Documento emitido con error en nombres", 1L);

        assertNotNull(response);
        assertEquals(EstadoCicloVidaDocumental.ANULADO_MOTIVADO, response.estadoCicloVida());
        assertEquals("Documento emitido con error en nombres", response.motivoAnulacion());
    }

    @Test
    @DisplayName("UC-PROV-28 E2: Estudiante sin permiso no puede acceder a documento reservado")
    void generarEnlaceEfimero_DocumentoReservado_LanzaAccessDenied() {
        RepositorioDocumento doc = new RepositorioDocumento();
        doc.setId(100L);
        doc.setCodigoDocumento("DOC-2026-0001");
        doc.setNivelConfidencialidad(NivelConfidencialidadDocumental.RESERVADO_CONVIVENCIA_Y_PROTECCION);

        when(documentoRepository.findById(100L)).thenReturn(Optional.of(doc));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(usuarioEstudiante));

        assertThrows(AccessDeniedException.class, () -> repositorioService.generarEnlaceDescargaEfimero(100L, 2L));
    }
}
