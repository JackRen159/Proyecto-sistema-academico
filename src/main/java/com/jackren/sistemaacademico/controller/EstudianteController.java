package com.jackren.sistemaacademico.controller;

import com.jackren.sistemaacademico.dto.*;
import com.jackren.sistemaacademico.service.EstudianteService;
import com.jackren.sistemaacademico.service.EvolucionDocumentoService;
import com.jackren.sistemaacademico.service.ExpedienteEstudianteService;
import com.jackren.sistemaacademico.service.SolicitudRectificacionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/estudiantes")
public class EstudianteController {

    private final EstudianteService estudianteService;
    private final ExpedienteEstudianteService expedienteEstudianteService;
    private final EvolucionDocumentoService evolucionDocumentoService;
    private final SolicitudRectificacionService rectificacionService;

    public EstudianteController(EstudianteService estudianteService,
                                ExpedienteEstudianteService expedienteEstudianteService,
                                EvolucionDocumentoService evolucionDocumentoService,
                                SolicitudRectificacionService rectificacionService) {
        this.estudianteService = estudianteService;
        this.expedienteEstudianteService = expedienteEstudianteService;
        this.evolucionDocumentoService = evolucionDocumentoService;
        this.rectificacionService = rectificacionService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'COORDINADOR_ACADEMICO', 'COORDINADOR_CONVIVENCIA', 'SECRETARIA', 'PROFESOR')")
    public ResponseEntity<Page<EstudianteResponse>> listar(
            @PageableDefault(size = 20, sort = "apellidos") Pageable pageable) {
        return ResponseEntity.ok(estudianteService.listarPaginado(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'COORDINADOR_ACADEMICO', 'COORDINADOR_CONVIVENCIA', 'SECRETARIA', 'PROFESOR')")
    public ResponseEntity<EstudianteResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(estudianteService.obtenerPorId(id));
    }

    @GetMapping("/documento/{documento}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'COORDINADOR_ACADEMICO', 'COORDINADOR_CONVIVENCIA', 'SECRETARIA', 'PROFESOR')")
    public ResponseEntity<EstudianteResponse> buscarPorDocumento(@PathVariable String documento) {
        return ResponseEntity.ok(estudianteService.obtenerPorDocumento(documento));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'SECRETARIA')")
    public ResponseEntity<EstudianteResponse> guardar(@Valid @RequestBody EstudianteRequest request) {
        EstudianteResponse response = estudianteService.guardar(request);
        URI location = URI.create("/api/estudiantes/" + response.getId());
        return ResponseEntity.created(location).body(response);
    }

    // ── REGISTRO ATÓMICO DEL EXPEDIENTE COMPLETO (UC-12) ─────────────────────────
    @PostMapping("/expediente-completo")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'COORDINADOR_ACADEMICO', 'SECRETARIA')")
    public ResponseEntity<ExpedienteCompletoResponse> registrarExpedienteCompleto(
            @Valid @RequestBody ExpedienteCompletoRequest request,
            @AuthenticationPrincipal Jwt jwt,
            HttpServletRequest httpRequest) {
        String ip = httpRequest != null ? httpRequest.getRemoteAddr() : "127.0.0.1";
        String userAgent = httpRequest != null ? httpRequest.getHeader("User-Agent") : "Cliente Web";

        ExpedienteCompletoResponse response = expedienteEstudianteService.registrarExpedienteCompleto(
                request, jwt != null ? jwt.getSubject() : "SECRETARIA", ip, userAgent);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ── CONSULTA DE LA PROPIA FICHA ESCOLAR POR EL ESTUDIANTE (UC-07) ────────────
    @GetMapping("/mi-ficha")
    @PreAuthorize("hasRole('ESTUDIANTE')")
    public ResponseEntity<FichaEscolarEstudianteResponse> miFicha(
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(estudianteService.obtenerMiFichaEscolar(jwt.getSubject()));
    }

    // ── CONSULTA DE FICHA ESCOLAR CON MÍNIMO PRIVILEGIO (UC-07, RN-09, RN-EST-08) ─
    @GetMapping("/{id}/ficha-escolar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'COORDINADOR_ACADEMICO', 'COORDINADOR_CONVIVENCIA', 'SECRETARIA', 'PROFESOR', 'ACUDIENTE')")
    public ResponseEntity<FichaEscolarEstudianteResponse> obtenerFichaEscolar(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {
        String rol = jwt != null ? jwt.getClaimAsString("rol") : "PROFESOR";
        String username = jwt != null ? jwt.getSubject() : "desconocido";
        return ResponseEntity.ok(estudianteService.obtenerFichaEscolarAutorizada(id, username, rol));
    }

    // ── EVOLUCIÓN LEGAL DE DOCUMENTO DE IDENTIDAD (UC-PROV-04 A1, RN-EST-03) ─────
    @PutMapping("/{id}/evolucion-documento")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'SECRETARIA')")
    public ResponseEntity<HistorialDocumentoIdentidadResponse> evolucionDocumento(
            @PathVariable Long id,
            @Valid @RequestBody EvolucionDocumentoRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(evolucionDocumentoService.registrarEvolucionDocumento(
                id, request, jwt != null ? jwt.getSubject() : "SECRETARIA"));
    }

    @GetMapping("/{id}/historial-documentos")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'SECRETARIA')")
    public ResponseEntity<List<HistorialDocumentoIdentidadResponse>> listarHistorialDocumentos(
            @PathVariable Long id) {
        return ResponseEntity.ok(evolucionDocumentoService.listarHistorialPorEstudiante(id));
    }

    // ── RADICACIÓN DE SOLICITUD DE RECTIFICACIÓN HABEAS DATA (UC-07 A2, RN-EST-04) ─
    @PostMapping("/{id}/solicitudes-rectificacion")
    @PreAuthorize("hasAnyRole('ACUDIENTE', 'ESTUDIANTE', 'SECRETARIA')")
    public ResponseEntity<SolicitudRectificacionResponse> radicarSolicitudRectificacion(
            @PathVariable Long id,
            @Valid @RequestBody RadicarSolicitudRectificacionRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        SolicitudRectificacionResponse response = rectificacionService.radicarSolicitud(
                id, request, jwt != null ? jwt.getSubject() : "solicitante");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'SECRETARIA')")
    public ResponseEntity<EstudianteResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody EstudianteRequest request) {
        return ResponseEntity.ok(estudianteService.actualizar(id, request));
    }

    @PatchMapping("/{id}/desactivar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'SECRETARIA')")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        estudianteService.desactivar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/restaurar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'SECRETARIA')")
    public ResponseEntity<Void> restaurar(@PathVariable Long id) {
        estudianteService.restaurar(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> eliminarPermanente(
            @PathVariable Long id,
            @RequestParam(defaultValue = "false") boolean confirmar) {
        estudianteService.eliminarPermanente(id, confirmar);
        return ResponseEntity.noContent().build();
    }
}
