package com.jackren.sistemaacademico.controller;

import com.jackren.sistemaacademico.dto.*;
import com.jackren.sistemaacademico.service.AsistenciaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/asistencia")
public class AsistenciaController {

    private final AsistenciaService asistenciaService;

    public AsistenciaController(AsistenciaService asistenciaService) {
        this.asistenciaService = asistenciaService;
    }

    // =========================================================================
    // 1. CONFIGURACIÓN DE ALERTAS DE AUSENTISMO
    // =========================================================================

    @PostMapping("/alertas/configuracion")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR')")
    public ResponseEntity<ConfiguracionAlertaAsistenciaResponse> configurarAlertas(
            @Valid @RequestBody ConfiguracionAlertaAsistenciaRequest request) {
        ConfiguracionAlertaAsistenciaResponse response = asistenciaService.guardarOActualizarConfiguracionAlerta(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/alertas/configuracion/{anioLectivoId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'SECRETARIA', 'PROFESOR')")
    public ResponseEntity<ConfiguracionAlertaAsistenciaResponse> obtenerConfiguracionAlertas(
            @PathVariable Long anioLectivoId) {
        return ResponseEntity.ok(asistenciaService.obtenerConfiguracionAlerta(anioLectivoId));
    }

    // =========================================================================
    // 2. SESIONES DE ASISTENCIA
    // =========================================================================

    @PostMapping("/sesiones")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'PROFESOR')")
    public ResponseEntity<SesionAsistenciaResponse> crearSesion(
            @Valid @RequestBody SesionAsistenciaRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        Long usuarioId = extraerUsuarioId(jwt);
        SesionAsistenciaResponse response = asistenciaService.crearSesion(request, usuarioId);
        URI location = URI.create("/api/asistencia/sesiones/" + response.getId());
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/sesiones/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'SECRETARIA', 'PROFESOR')")
    public ResponseEntity<SesionAsistenciaResponse> buscarSesionPorId(@PathVariable Long id) {
        return ResponseEntity.ok(asistenciaService.obtenerSesionPorId(id));
    }

    @GetMapping("/sesiones")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'SECRETARIA', 'PROFESOR')")
    public ResponseEntity<List<SesionAsistenciaResponse>> listarSesiones(
            @RequestParam(required = false) Long asignacionId,
            @RequestParam(required = false) Long grupoId,
            @RequestParam Long periodoId) {
        if (asignacionId != null) {
            return ResponseEntity.ok(asistenciaService.listarSesionesPorAsignacionYPeriodo(asignacionId, periodoId));
        }
        if (grupoId != null) {
            return ResponseEntity.ok(asistenciaService.listarSesionesPorGrupoYPeriodo(grupoId, periodoId));
        }
        throw new IllegalArgumentException("Debe proporcionar al menos un filtro: asignacionId o grupoId junto con periodoId");
    }

    // =========================================================================
    // 3. TOMA DE ASISTENCIA Y CONSULTA DE REGISTROS
    // =========================================================================

    @PostMapping("/sesiones/planilla")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'PROFESOR')")
    public ResponseEntity<List<RegistroAsistenciaResponse>> registrarPlanillaLote(
            @Valid @RequestBody PlanillaAsistenciaLoteRequest request) {
        return ResponseEntity.ok(asistenciaService.registrarPlanillaLote(request));
    }

    @GetMapping("/sesiones/{sesionId}/registros")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'SECRETARIA', 'PROFESOR')")
    public ResponseEntity<List<RegistroAsistenciaResponse>> listarRegistrosPorSesion(
            @PathVariable Long sesionId) {
        return ResponseEntity.ok(asistenciaService.listarRegistrosPorSesion(sesionId));
    }

    // =========================================================================
    // 4. JUSTIFICACIÓN DE INASISTENCIA
    // =========================================================================

    @PostMapping("/registros/{registroId}/justificar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'SECRETARIA')")
    public ResponseEntity<JustificacionAsistenciaResponse> justificarInasistencia(
            @PathVariable Long registroId,
            @Valid @RequestBody JustificacionAsistenciaRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        Long usuarioId = extraerUsuarioId(jwt);
        String username = extraerUsername(jwt);
        return ResponseEntity.ok(asistenciaService.justificarInasistencia(registroId, request, usuarioId, username));
    }

    // =========================================================================
    // 5. RESÚMENES, PORCENTAJES Y ALERTAS TEMPRANAS
    // =========================================================================

    @GetMapping("/estudiante/{matriculaId}/resumen")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'SECRETARIA', 'PROFESOR', 'ESTUDIANTE', 'ACUDIENTE')")
    public ResponseEntity<ResumenAsistenciaEstudianteResponse> obtenerResumenEstudiante(
            @PathVariable Long matriculaId,
            @RequestParam Long periodoId,
            @RequestParam Long asignacionId) {
        return ResponseEntity.ok(asistenciaService.obtenerResumenEstudianteAsignatura(matriculaId, periodoId, asignacionId));
    }

    @GetMapping("/grupo/{grupoId}/alertas")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'SECRETARIA', 'PROFESOR')")
    public ResponseEntity<List<ResumenAsistenciaEstudianteResponse>> listarAlertasGrupo(
            @PathVariable Long grupoId,
            @RequestParam Long periodoId,
            @RequestParam Long asignacionId) {
        return ResponseEntity.ok(asistenciaService.listarAlertasPorGrupoYPeriodo(grupoId, periodoId, asignacionId));
    }

    private Long extraerUsuarioId(Jwt jwt) {
        if (jwt != null && jwt.hasClaim("id")) {
            Object idClaim = jwt.getClaim("id");
            if (idClaim instanceof Number number) {
                return number.longValue();
            }
        }
        return null;
    }

    private String extraerUsername(Jwt jwt) {
        return jwt != null ? jwt.getSubject() : "sistema";
    }
}
