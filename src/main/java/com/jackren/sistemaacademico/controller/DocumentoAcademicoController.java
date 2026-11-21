package com.jackren.sistemaacademico.controller;

import com.jackren.sistemaacademico.dto.*;
import com.jackren.sistemaacademico.entity.Rol;
import com.jackren.sistemaacademico.entity.Usuario;
import com.jackren.sistemaacademico.repository.UsuarioRepository;
import com.jackren.sistemaacademico.service.DocumentoAcademicoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/documentos")
public class DocumentoAcademicoController {

    private final DocumentoAcademicoService documentoService;
    private final UsuarioRepository usuarioRepository;

    public DocumentoAcademicoController(DocumentoAcademicoService documentoService,
                                        UsuarioRepository usuarioRepository) {
        this.documentoService = documentoService;
        this.usuarioRepository = usuarioRepository;
    }

    // =========================================================================
    // 1. CONFIGURACIÓN Y CONSULTA DE CONSECUTIVOS POR AÑO
    // =========================================================================

    @PostMapping("/consecutivos")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'SECRETARIA')")
    public ResponseEntity<ConfiguracionConsecutivoResponse> guardarOActualizarConsecutivo(
            @Valid @RequestBody ConfiguracionConsecutivoRequest request) {
        return ResponseEntity.ok(documentoService.guardarOActualizarConsecutivo(request));
    }

    @GetMapping("/consecutivos/anio-lectivo/{anioLectivoId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'SECRETARIA')")
    public ResponseEntity<List<ConfiguracionConsecutivoResponse>> listarConsecutivosPorAnio(
            @PathVariable Long anioLectivoId) {
        return ResponseEntity.ok(documentoService.obtenerConsecutivosPorAnio(anioLectivoId));
    }

    // =========================================================================
    // 2. EMISIÓN DE BOLETINES PERIÓDICOS
    // =========================================================================

    @PostMapping("/boletines/matricula/{matriculaId}/periodo/{periodoId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'SECRETARIA', 'PROFESOR', 'ESTUDIANTE', 'ACUDIENTE')")
    public ResponseEntity<BoletinPeriodoResponse> generarBoletinPeriodo(
            @PathVariable Long matriculaId,
            @PathVariable Long periodoId,
            @RequestBody(required = false) EmisionDocumentoRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        Usuario usuario = obtenerUsuarioAutenticado(jwt);
        documentoService.validarAccesoContextualPorMatricula(matriculaId, usuario);
        BoletinPeriodoResponse response = documentoService.generarBoletinPeriodo(matriculaId, periodoId, request, usuario);
        return ResponseEntity.ok(response);
    }

    // =========================================================================
    // 3. EMISIÓN DE CONSTANCIAS DE MATRÍCULA
    // =========================================================================

    @PostMapping("/constancias/matricula/{matriculaId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'SECRETARIA', 'ESTUDIANTE', 'ACUDIENTE')")
    public ResponseEntity<ConstanciaMatriculaResponse> generarConstanciaMatricula(
            @PathVariable Long matriculaId,
            @RequestBody(required = false) EmisionDocumentoRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        Usuario usuario = obtenerUsuarioAutenticado(jwt);
        documentoService.validarAccesoContextualPorMatricula(matriculaId, usuario);
        ConstanciaMatriculaResponse response = documentoService.generarConstanciaMatricula(matriculaId, request, usuario);
        return ResponseEntity.ok(response);
    }

    // =========================================================================
    // 4. EMISIÓN DE CERTIFICADOS DE ESTUDIO (ANUAL Y PARCIAL POR RETIRO)
    // =========================================================================

    @PostMapping("/certificados/matricula/{matriculaId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'SECRETARIA')")
    public ResponseEntity<CertificadoEstudioResponse> generarCertificadoEstudio(
            @PathVariable Long matriculaId,
            @RequestBody(required = false) EmisionDocumentoRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        Usuario usuario = obtenerUsuarioAutenticado(jwt);
        documentoService.validarAccesoContextualPorMatricula(matriculaId, usuario);
        CertificadoEstudioResponse response = documentoService.generarCertificadoEstudio(matriculaId, request, usuario);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/certificados/notas-parciales/matricula/{matriculaId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'SECRETARIA')")
    public ResponseEntity<CertificadoEstudioResponse> generarCertificadoNotasParciales(
            @PathVariable Long matriculaId,
            @RequestBody(required = false) EmisionDocumentoRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        Usuario usuario = obtenerUsuarioAutenticado(jwt);
        documentoService.validarAccesoContextualPorMatricula(matriculaId, usuario);
        CertificadoEstudioResponse response = documentoService.generarCertificadoNotasParciales(matriculaId, request, usuario);
        return ResponseEntity.ok(response);
    }

    // =========================================================================
    // 5. CONSULTA PÚBLICA / VERIFICACIÓN DE AUTENTICIDAD
    // =========================================================================

    @GetMapping("/verificar/{codigoVerificacion}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<RegistroEmisionResponse> verificarDocumento(
            @PathVariable String codigoVerificacion) {
        return ResponseEntity.ok(documentoService.consultarEmisionPorCodigoVerificacion(codigoVerificacion));
    }

    // =========================================================================
    // 6. HISTORIAL DE EMISIONES Y AUDITORÍA CON CONTROL CONTEXTUAL (UC-05, UC-11)
    // =========================================================================

    @GetMapping("/emisiones/estudiante/{estudianteId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'SECRETARIA', 'PROFESOR', 'ESTUDIANTE', 'ACUDIENTE')")
    public ResponseEntity<List<RegistroEmisionResponse>> listarEmisionesPorEstudiante(
            @PathVariable Long estudianteId,
            @AuthenticationPrincipal Jwt jwt) {
        Usuario usuario = obtenerUsuarioAutenticado(jwt);
        documentoService.validarAccesoContextual(estudianteId, usuario);
        return ResponseEntity.ok(documentoService.consultarEmisionesPorEstudiante(estudianteId));
    }

    @GetMapping("/emisiones/matricula/{matriculaId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'SECRETARIA', 'PROFESOR', 'ESTUDIANTE', 'ACUDIENTE')")
    public ResponseEntity<List<RegistroEmisionResponse>> listarEmisionesPorMatricula(
            @PathVariable Long matriculaId,
            @AuthenticationPrincipal Jwt jwt) {
        Usuario usuario = obtenerUsuarioAutenticado(jwt);
        documentoService.validarAccesoContextualPorMatricula(matriculaId, usuario);
        return ResponseEntity.ok(documentoService.consultarEmisionesPorMatricula(matriculaId));
    }

    @PatchMapping("/emisiones/{emisionId}/anular")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'SECRETARIA')")
    public ResponseEntity<RegistroEmisionResponse> anularEmision(
            @PathVariable Long emisionId,
            @Valid @RequestBody AnulacionEmisionRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        Usuario usuario = obtenerUsuarioAutenticado(jwt);
        return ResponseEntity.ok(documentoService.anularEmision(emisionId, request, usuario));
    }

    private Usuario obtenerUsuarioAutenticado(Jwt jwt) {
        if (jwt == null) {
            return null;
        }
        String username = jwt.getSubject();
        if (username != null && usuarioRepository != null) {
            return usuarioRepository.findByUsername(username).orElse(null);
        }
        return null;
    }
}
