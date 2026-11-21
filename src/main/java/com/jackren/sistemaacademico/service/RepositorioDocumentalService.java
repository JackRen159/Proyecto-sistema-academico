package com.jackren.sistemaacademico.service;

import com.jackren.sistemaacademico.dto.*;
import com.jackren.sistemaacademico.entity.*;
import com.jackren.sistemaacademico.exception.ConflictoNegocioException;
import com.jackren.sistemaacademico.exception.RecursoNoEncontradoException;
import com.jackren.sistemaacademico.exception.ValidacionException;
import com.jackren.sistemaacademico.repository.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class RepositorioDocumentalService {

    private final RepositorioDocumentoRepository documentoRepository;
    private final RepositorioDocumentoVersionRepository versionRepository;
    private final TablaRetencionDocumentalRepository trdRepository;
    private final EstudianteRepository estudianteRepository;
    private final MatriculaRepository matriculaRepository;
    private final AnioLectivoRepository anioLectivoRepository;
    private final UsuarioRepository usuarioRepository;
    private final DocumentoAcademicoService documentoAcademicoService;
    private final AuditoriaGlobalService auditoriaGlobalService;

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024L; // 10MB
    private static final Set<String> EXTENSIONES_PELIGROSAS = Set.of(
            "exe", "sh", "bat", "cmd", "js", "vbs", "jar", "war", "ear", "msi", "ps1"
    );

    public RepositorioDocumentalService(
            RepositorioDocumentoRepository documentoRepository,
            RepositorioDocumentoVersionRepository versionRepository,
            TablaRetencionDocumentalRepository trdRepository,
            EstudianteRepository estudianteRepository,
            MatriculaRepository matriculaRepository,
            AnioLectivoRepository anioLectivoRepository,
            UsuarioRepository usuarioRepository,
            DocumentoAcademicoService documentoAcademicoService,
            AuditoriaGlobalService auditoriaGlobalService) {
        this.documentoRepository = documentoRepository;
        this.versionRepository = versionRepository;
        this.trdRepository = trdRepository;
        this.estudianteRepository = estudianteRepository;
        this.matriculaRepository = matriculaRepository;
        this.anioLectivoRepository = anioLectivoRepository;
        this.usuarioRepository = usuarioRepository;
        this.documentoAcademicoService = documentoAcademicoService;
        this.auditoriaGlobalService = auditoriaGlobalService;
    }

    public RepositorioDocumentoResponse cargarDocumento(CargarDocumentoRequest request, Long usuarioCreadorId) {
        Usuario creador = usuarioRepository.findById(usuarioCreadorId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con ID: " + usuarioCreadorId));

        validarFormatoYTamano(request.nombreArchivoOriginal(), request.tamanoBytes());

        String hashCalculado = calcularSha256(request.contenidoBase64() != null
                ? request.contenidoBase64().getBytes(StandardCharsets.UTF_8)
                : (request.nombreArchivoOriginal() + System.currentTimeMillis()).getBytes(StandardCharsets.UTF_8));

        String ruta = request.rutaAlmacenamiento() != null
                ? request.rutaAlmacenamiento()
                : "/var/storage/documentos/" + UUID.randomUUID() + ".pdf";

        int anio = (request.anioLectivoId() != null)
                ? anioLectivoRepository.findById(request.anioLectivoId()).map(AnioLectivo::getAnio).orElse(2026)
                : 2026;

        String codigoDoc = "DOC-" + anio + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        RepositorioDocumento doc = new RepositorioDocumento();
        doc.setCodigoDocumento(codigoDoc);
        doc.setTitulo(request.titulo().trim());
        doc.setDescripcion(request.descripcion());
        doc.setTipoDocumento(request.tipoDocumento().trim());
        doc.setNivelConfidencialidad(request.nivelConfidencialidad());
        doc.setEstadoCicloVida(EstadoCicloVidaDocumental.PUBLICADO_VIGENTE);
        doc.setVersionActual(1);
        doc.setHashSha256Actual(hashCalculado);
        doc.setRutaAlmacenamiento(ruta);
        doc.setNombreArchivoOriginal(request.nombreArchivoOriginal().trim());
        doc.setMimeType(request.mimeType().trim());
        doc.setTamanoBytes(request.tamanoBytes() != null ? request.tamanoBytes() : 1024L);
        doc.setUsuarioCreador(creador);
        doc.setFechaPublicacion(LocalDateTime.now());

        if (request.estudianteId() != null) {
            estudianteRepository.findById(request.estudianteId()).ifPresent(doc::setEstudiante);
        }
        if (request.matriculaId() != null) {
            matriculaRepository.findById(request.matriculaId()).ifPresent(doc::setMatricula);
        }
        if (request.anioLectivoId() != null) {
            anioLectivoRepository.findById(request.anioLectivoId()).ifPresent(doc::setAnioLectivo);
        }
        if (request.trdId() != null) {
            trdRepository.findById(request.trdId()).ifPresent(doc::setTrd);
        }

        RepositorioDocumento guardado = documentoRepository.save(doc);

        // Crear versión inmutable V1
        RepositorioDocumentoVersion version1 = new RepositorioDocumentoVersion();
        version1.setDocumento(guardado);
        version1.setNumeroVersion(1);
        version1.setHashSha256(hashCalculado);
        version1.setRutaAlmacenamiento(ruta);
        version1.setNombreArchivo(guardado.getNombreArchivoOriginal());
        version1.setMimeType(guardado.getMimeType());
        version1.setTamanoBytes(guardado.getTamanoBytes());
        version1.setMotivoVersion("Carga inicial de documento oficial");
        version1.setUsuarioAutor(creador);
        versionRepository.save(version1);

        if (auditoriaGlobalService != null) {
            auditoriaGlobalService.registrarEvento(
                    "CARGA_DOCUMENTO_REPOSITORIO",
                    "RepositorioDocumento",
                    guardado.getId().toString(),
                    "N/A",
                    "Código: " + guardado.getCodigoDocumento() + ", Confidencialidad: " + guardado.getNivelConfidencialidad(),
                    "Carga oficial de documento en repositorio seguro (UC-PROV-28)",
                    "EXITOSO"
            );
        }

        return toResponse(guardado);
    }

    public RepositorioDocumentoVersionResponse crearNuevaVersion(Long documentoId, NuevaVersionDocumentoRequest request, Long usuarioAutorId) {
        RepositorioDocumento doc = documentoRepository.findById(documentoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Documento no encontrado con ID: " + documentoId));

        if (doc.getEstadoCicloVida() == EstadoCicloVidaDocumental.ANULADO_MOTIVADO) {
            throw new ConflictoNegocioException("No se puede crear nueva versión de un documento anulado");
        }

        Usuario autor = usuarioRepository.findById(usuarioAutorId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con ID: " + usuarioAutorId));

        validarFormatoYTamano(request.nombreArchivo(), request.tamanoBytes());

        String nuevoHash = calcularSha256(request.contenidoBase64() != null
                ? request.contenidoBase64().getBytes(StandardCharsets.UTF_8)
                : (request.nombreArchivo() + System.currentTimeMillis()).getBytes(StandardCharsets.UTF_8));

        int nuevaVersionNum = doc.getVersionActual() + 1;
        String nuevaRuta = request.rutaAlmacenamiento() != null
                ? request.rutaAlmacenamiento()
                : "/var/storage/documentos/" + doc.getCodigoDocumento() + "_v" + nuevaVersionNum + ".pdf";

        // Versionamiento inmutable (A1, RN-DOC-03)
        RepositorioDocumentoVersion versionNueva = new RepositorioDocumentoVersion();
        versionNueva.setDocumento(doc);
        versionNueva.setNumeroVersion(nuevaVersionNum);
        versionNueva.setHashSha256(nuevoHash);
        versionNueva.setRutaAlmacenamiento(nuevaRuta);
        versionNueva.setNombreArchivo(request.nombreArchivo().trim());
        versionNueva.setMimeType(request.mimeType().trim());
        versionNueva.setTamanoBytes(request.tamanoBytes());
        versionNueva.setMotivoVersion(request.motivoVersion().trim());
        versionNueva.setUsuarioAutor(autor);

        RepositorioDocumentoVersion guardada = versionRepository.save(versionNueva);

        doc.setVersionActual(nuevaVersionNum);
        doc.setHashSha256Actual(nuevoHash);
        doc.setRutaAlmacenamiento(nuevaRuta);
        doc.setNombreArchivoOriginal(request.nombreArchivo().trim());
        doc.setMimeType(request.mimeType().trim());
        doc.setTamanoBytes(request.tamanoBytes());
        documentoRepository.save(doc);

        if (auditoriaGlobalService != null) {
            auditoriaGlobalService.registrarEvento(
                    "VERSIONAMIENTO_DOCUMENTO_REPOSITORIO",
                    "RepositorioDocumentoVersion",
                    guardada.getId().toString(),
                    "Versión: " + (nuevaVersionNum - 1),
                    "Versión: " + nuevaVersionNum,
                    "Nueva versión inmutable por " + request.motivoVersion(),
                    "EXITOSO"
            );
        }

        return new RepositorioDocumentoVersionResponse(
                guardada.getId(),
                doc.getId(),
                guardada.getNumeroVersion(),
                guardada.getHashSha256(),
                guardada.getNombreArchivo(),
                guardada.getMimeType(),
                guardada.getTamanoBytes(),
                guardada.getMotivoVersion(),
                autor.getId(),
                autor.getUsername(),
                guardada.getFechaRegistro()
        );
    }

    public RepositorioDocumentoResponse anularDocumentoLogico(Long documentoId, String motivo, Long usuarioId) {
        RepositorioDocumento doc = documentoRepository.findById(documentoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Documento no encontrado con ID: " + documentoId));

        if (doc.getEstadoCicloVida() == EstadoCicloVidaDocumental.ANULADO_MOTIVADO) {
            throw new ConflictoNegocioException("El documento ya se encuentra anulado");
        }

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con ID: " + usuarioId));

        doc.setEstadoCicloVida(EstadoCicloVidaDocumental.ANULADO_MOTIVADO);
        doc.setMotivoAnulacion(motivo != null ? motivo.trim() : "Anulación formal motivada");
        doc.setUsuarioAnulador(usuario);

        RepositorioDocumento actualizado = documentoRepository.save(doc);

        if (auditoriaGlobalService != null) {
            auditoriaGlobalService.registrarEvento(
                    "ANULACION_LOGICA_DOCUMENTO",
                    "RepositorioDocumento",
                    documentoId.toString(),
                    "Estado: PUBLICADO_VIGENTE",
                    "Estado: ANULADO_MOTIVADO",
                    doc.getMotivoAnulacion(),
                    "EXITOSO"
            );
        }

        return toResponse(actualizado);
    }

    // Invariante E3: Prohibición absoluta de borrado físico (Ley 594 de 2000, DEC-DOM-03)
    public void eliminarDocumentoFisico(Long documentoId) {
        throw new ConflictoNegocioException(
                "Prohibido el borrado físico de documentos probatorios conforme a la Ley 594 de 2000 y DEC-DOM-03. Utilice la anulación motivada (E3)"
        );
    }

    public EnlaceDescargaResponse generarEnlaceDescargaEfimero(Long documentoId, Long usuarioConsultaId) {
        RepositorioDocumento doc = documentoRepository.findById(documentoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Documento no encontrado con ID: " + documentoId));

        Usuario usuario = usuarioRepository.findById(usuarioConsultaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con ID: " + usuarioConsultaId));

        if (doc.getEstudiante() != null && documentoAcademicoService != null) {
            documentoAcademicoService.validarAccesoContextual(doc.getEstudiante().getId(), usuario);
        }

        // Restricción por nivel de confidencialidad (RN-DOC-05)
        Rol rol = usuario.getRol();
        if ((doc.getNivelConfidencialidad() == NivelConfidencialidadDocumental.CONFIDENCIAL_SALUD_E_INCLUSION ||
             doc.getNivelConfidencialidad() == NivelConfidencialidadDocumental.RESERVADO_CONVIVENCIA_Y_PROTECCION) &&
            rol != Rol.RECTOR && rol != Rol.COORDINADOR_CONVIVENCIA && rol != Rol.ORIENTADOR_ESCOLAR && rol != Rol.ADMINISTRADOR) {
            throw new AccessDeniedException("No tiene permisos para acceder a documentos de categoría reservada o confidencial (E2, RN-DOC-05)");
        }

        String tokenEfimero = UUID.randomUUID().toString();
        LocalDateTime expiracion = LocalDateTime.now().plusMinutes(15);
        String urlFirmada = "/api/repositorio/descargas/" + doc.getCodigoDocumento() + "?token=" + tokenEfimero + "&exp=" + expiracion;

        return new EnlaceDescargaResponse(
                doc.getCodigoDocumento(),
                urlFirmada,
                expiracion
        );
    }

    @Transactional(readOnly = true)
    public RepositorioDocumentoResponse obtenerPorId(Long documentoId, Long usuarioConsultaId) {
        RepositorioDocumento doc = documentoRepository.findById(documentoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Documento no encontrado con ID: " + documentoId));
        return toResponse(doc);
    }

    @Transactional(readOnly = true)
    public List<RepositorioDocumentoResponse> listarPorEstudiante(Long estudianteId, Long usuarioConsultaId) {
        if (documentoAcademicoService != null) {
            Usuario usuario = usuarioRepository.findById(usuarioConsultaId).orElse(null);
            documentoAcademicoService.validarAccesoContextual(estudianteId, usuario);
        }
        return documentoRepository.findByEstudianteId(estudianteId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RepositorioDocumentoVersionResponse> listarVersiones(Long documentoId) {
        return versionRepository.findByDocumentoIdOrderByNumeroVersionDesc(documentoId).stream()
                .map(v -> new RepositorioDocumentoVersionResponse(
                        v.getId(),
                        v.getDocumento().getId(),
                        v.getNumeroVersion(),
                        v.getHashSha256(),
                        v.getNombreArchivo(),
                        v.getMimeType(),
                        v.getTamanoBytes(),
                        v.getMotivoVersion(),
                        v.getUsuarioAutor().getId(),
                        v.getUsuarioAutor().getUsername(),
                        v.getFechaRegistro()
                ))
                .toList();
    }

    private void validarFormatoYTamano(String nombreArchivo, Long tamanoBytes) {
        if (nombreArchivo == null || !nombreArchivo.contains(".")) {
            throw new ValidacionException("Nombre de archivo inválido");
        }
        String extension = nombreArchivo.substring(nombreArchivo.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
        if (EXTENSIONES_PELIGROSAS.contains(extension)) {
            throw new ValidacionException("Formato de archivo peligroso o no permitido (E1)");
        }
        if (tamanoBytes != null && tamanoBytes > MAX_FILE_SIZE) {
            throw new ValidacionException("El archivo excede el tamaño máximo permitido de 10MB (E1)");
        }
    }

    private String calcularSha256(byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(data);
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Algoritmo SHA-256 no disponible", e);
        }
    }

    public RepositorioDocumentoResponse toResponse(RepositorioDocumento d) {
        return new RepositorioDocumentoResponse(
                d.getId(),
                d.getCodigoDocumento(),
                d.getTitulo(),
                d.getDescripcion(),
                d.getTipoDocumento(),
                d.getNivelConfidencialidad(),
                d.getEstadoCicloVida(),
                d.getEstudiante() != null ? d.getEstudiante().getId() : null,
                d.getEstudiante() != null ? d.getEstudiante().getNombres() + " " + d.getEstudiante().getApellidos() : null,
                d.getMatricula() != null ? d.getMatricula().getId() : null,
                d.getAnioLectivo() != null ? d.getAnioLectivo().getId() : null,
                d.getTrd() != null ? d.getTrd().getId() : null,
                d.getVersionActual(),
                d.getHashSha256Actual(),
                d.getNombreArchivoOriginal(),
                d.getMimeType(),
                d.getTamanoBytes(),
                d.getUsuarioCreador().getId(),
                d.getUsuarioCreador().getUsername(),
                d.getFechaCreacion(),
                d.getFechaPublicacion(),
                d.getMotivoAnulacion(),
                "/api/repositorio/descargas/" + d.getCodigoDocumento()
        );
    }
}
