package com.jackren.sistemaacademico.controller;

import com.jackren.sistemaacademico.dto.*;
import com.jackren.sistemaacademico.service.AuditoriaGlobalService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controlador REST para consulta de la bitácora transversal inmutable y verificación criptográfica de integridad (UC-27, UC-PROV-29).
 */
@RestController
@RequestMapping("/api/auditoria")
public class AuditoriaGlobalController {

    private final AuditoriaGlobalService auditoriaService;

    public AuditoriaGlobalController(AuditoriaGlobalService auditoriaService) {
        this.auditoriaService = auditoriaService;
    }

    @GetMapping("/eventos")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR')")
    public ResponseEntity<Page<AuditoriaEventoResponse>> listarEventos(
            @RequestParam(required = false) String modulo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta,
            Pageable pageable) {
        return ResponseEntity.ok(auditoriaService.listarEventosPaginados(modulo, desde, hasta, pageable));
    }

    @GetMapping("/eventos/correlation/{correlationId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR')")
    public ResponseEntity<List<AuditoriaEventoResponse>> listarPorCorrelationId(
            @PathVariable String correlationId) {
        return ResponseEntity.ok(auditoriaService.listarPorCorrelationId(correlationId));
    }

    @GetMapping("/eventos/entidad/{entidad}/{entidadId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR')")
    public ResponseEntity<List<AuditoriaEventoResponse>> listarPorEntidad(
            @PathVariable String entidad,
            @PathVariable String entidadId) {
        return ResponseEntity.ok(auditoriaService.listarPorEntidad(entidad, entidadId));
    }

    @GetMapping("/verificar-integridad")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR')")
    public ResponseEntity<VerificacionIntegridadResponse> verificarIntegridad() {
        return ResponseEntity.ok(auditoriaService.verificarIntegridadCadena());
    }

    @PostMapping("/exportar")
    @PreAuthorize("hasRole('RECTOR')")
    public ResponseEntity<PaqueteAuditoriaExportadoResponse> exportarPaquete(
            @Valid @RequestBody ExportacionAuditoriaRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(auditoriaService.exportarPaqueteAuditoria(request, jwt.getSubject()));
    }

    @GetMapping("/alertas")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR')")
    public ResponseEntity<List<AlertaSeguridadResponse>> listarAlertasPendientes() {
        return ResponseEntity.ok(auditoriaService.listarAlertasPendientesRector());
    }

    @PatchMapping("/alertas/{alertaId}/leer")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR')")
    public ResponseEntity<Void> marcarAlertaLeida(
            @PathVariable Long alertaId,
            @AuthenticationPrincipal Jwt jwt) {
        auditoriaService.marcarAlertaLeida(alertaId, null);
        return ResponseEntity.ok().build();
    }
}
