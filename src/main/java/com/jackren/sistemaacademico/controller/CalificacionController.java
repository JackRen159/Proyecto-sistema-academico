package com.jackren.sistemaacademico.controller;

import com.jackren.sistemaacademico.dto.*;
import com.jackren.sistemaacademico.service.CalificacionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/calificaciones")
public class CalificacionController {

    private final CalificacionService calificacionService;

    public CalificacionController(CalificacionService calificacionService) {
        this.calificacionService = calificacionService;
    }

    // =========================================================================
    // 1. ESCALAS SIEE INSTITUCIONALES
    // =========================================================================

    @PostMapping("/escala")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR')")
    public ResponseEntity<ConfiguracionEscalaSieeResponse> guardarEscala(
            @Valid @RequestBody ConfiguracionEscalaSieeRequest request) {
        ConfiguracionEscalaSieeResponse response = calificacionService.guardarOActualizarEscala(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/escala/anio-lectivo/{anioLectivoId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'SECRETARIA', 'PROFESOR', 'ESTUDIANTE', 'ACUDIENTE')")
    public ResponseEntity<ConfiguracionEscalaSieeResponse> obtenerEscalaPorAnio(
            @PathVariable Long anioLectivoId) {
        return ResponseEntity.ok(calificacionService.obtenerEscalaPorAnio(anioLectivoId));
    }

    // =========================================================================
    // 2. ACTIVIDADES EVALUATIVAS
    // =========================================================================

    @PostMapping("/actividades")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'PROFESOR')")
    public ResponseEntity<ActividadEvaluativaResponse> crearActividad(
            @Valid @RequestBody ActividadEvaluativaRequest request) {
        ActividadEvaluativaResponse response = calificacionService.crearActividad(request);
        URI location = URI.create("/api/calificaciones/actividades/" + response.getId());
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/actividades")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'SECRETARIA', 'PROFESOR', 'ESTUDIANTE', 'ACUDIENTE')")
    public ResponseEntity<List<ActividadEvaluativaResponse>> listarActividades(
            @RequestParam Long asignacionId,
            @RequestParam Long periodoId) {
        return ResponseEntity.ok(calificacionService.listarActividadesPorAsignacionYPeriodo(asignacionId, periodoId));
    }

    // =========================================================================
    // 3. REGISTRO Y MODIFICACIÓN DE NOTAS DE ACTIVIDADES
    // =========================================================================

    @PostMapping("/actividades/notas/lote")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'PROFESOR')")
    public ResponseEntity<List<CalificacionActividadResponse>> registrarNotasLote(
            @Valid @RequestBody RegistroCalificacionesLoteRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        Long usuarioId = extraerUsuarioId(jwt);
        String username = extraerUsername(jwt);
        return ResponseEntity.ok(calificacionService.registrarCalificacionesLote(request, usuarioId, username));
    }

    @PutMapping("/actividades/notas/{calificacionId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'PROFESOR')")
    public ResponseEntity<CalificacionActividadResponse> actualizarNotaActividad(
            @PathVariable Long calificacionId,
            @Valid @RequestBody ActualizarCalificacionRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        Long usuarioId = extraerUsuarioId(jwt);
        String username = extraerUsername(jwt);
        return ResponseEntity.ok(calificacionService.actualizarCalificacionActividad(calificacionId, request, usuarioId, username));
    }

    @GetMapping("/actividades/{actividadId}/notas")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'SECRETARIA', 'PROFESOR')")
    public ResponseEntity<List<CalificacionActividadResponse>> listarNotasActividad(
            @PathVariable Long actividadId) {
        return ResponseEntity.ok(calificacionService.listarCalificacionesPorActividad(actividadId));
    }

    // =========================================================================
    // 4. DEFINITIVAS DE PERIODO Y RECUPERACIONES
    // =========================================================================

    @PostMapping("/periodo/calcular")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'PROFESOR')")
    public ResponseEntity<List<CalificacionPeriodoResponse>> calcularDefinitivasPeriodo(
            @RequestParam Long asignacionId,
            @RequestParam Long periodoId) {
        return ResponseEntity.ok(calificacionService.calcularDefinitivasPeriodo(asignacionId, periodoId));
    }

    @PostMapping("/periodo/{calificacionPeriodoId}/recuperacion")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'PROFESOR')")
    public ResponseEntity<CalificacionPeriodoResponse> registrarRecuperacion(
            @PathVariable Long calificacionPeriodoId,
            @RequestParam BigDecimal notaRecuperacion,
            @RequestParam(required = false) String motivo,
            @AuthenticationPrincipal Jwt jwt) {
        Long usuarioId = extraerUsuarioId(jwt);
        String username = extraerUsername(jwt);
        return ResponseEntity.ok(calificacionService.registrarRecuperacion(calificacionPeriodoId, notaRecuperacion, motivo, usuarioId, username));
    }

    @GetMapping("/periodo")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'SECRETARIA', 'PROFESOR')")
    public ResponseEntity<List<CalificacionPeriodoResponse>> listarCalificacionesPeriodo(
            @RequestParam Long grupoId,
            @RequestParam Long asignaturaId,
            @RequestParam Long periodoId) {
        return ResponseEntity.ok(calificacionService.listarCalificacionesPeriodo(grupoId, asignaturaId, periodoId));
    }

    @GetMapping("/estudiante/{matriculaId}/periodo/{periodoId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'SECRETARIA', 'PROFESOR', 'ESTUDIANTE', 'ACUDIENTE')")
    public ResponseEntity<List<CalificacionPeriodoResponse>> listarCalificacionesEstudiantePeriodo(
            @PathVariable Long matriculaId,
            @PathVariable Long periodoId) {
        return ResponseEntity.ok(calificacionService.listarCalificacionesEstudiantePeriodo(matriculaId, periodoId));
    }

    // =========================================================================
    // 5. AUDITORÍA INMUTABLE
    // =========================================================================

    @GetMapping("/auditoria/matricula/{matriculaId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'SECRETARIA')")
    public ResponseEntity<List<AuditoriaCalificacionResponse>> listarAuditoriaMatricula(
            @PathVariable Long matriculaId) {
        return ResponseEntity.ok(calificacionService.listarHistorialAuditoriaMatricula(matriculaId));
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
