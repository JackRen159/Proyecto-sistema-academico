package com.jackren.sistemaacademico.controller;

import com.jackren.sistemaacademico.dto.*;
import com.jackren.sistemaacademico.service.CalificacionService;
import com.jackren.sistemaacademico.service.ConvalidacionCalificacionService;
import com.jackren.sistemaacademico.service.NivelacionCalificacionService;
import com.jackren.sistemaacademico.service.ReclamacionCalificacionService;
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
    private final NivelacionCalificacionService nivelacionService;
    private final ReclamacionCalificacionService reclamacionService;
    private final ConvalidacionCalificacionService convalidacionService;

    public CalificacionController(CalificacionService calificacionService,
                                  NivelacionCalificacionService nivelacionService,
                                  ReclamacionCalificacionService reclamacionService,
                                  ConvalidacionCalificacionService convalidacionService) {
        this.calificacionService = calificacionService;
        this.nivelacionService = nivelacionService;
        this.reclamacionService = reclamacionService;
        this.convalidacionService = convalidacionService;
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
    // 2. ACTIVIDADES EVALUATIVAS (UC-16)
    // =========================================================================

    @PostMapping("/actividades")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'PROFESOR')")
    public ResponseEntity<ActividadEvaluativaResponse> crearActividad(
            @Valid @RequestBody ActividadEvaluativaRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        Long usuarioId = extraerUsuarioId(jwt);
        String rol = extraerRol(jwt);
        ActividadEvaluativaResponse response = calificacionService.crearActividad(request, usuarioId, rol);
        URI location = URI.create("/api/calificaciones/actividades/" + response.getId());
        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/actividades/{actividadId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'PROFESOR')")
    public ResponseEntity<ActividadEvaluativaResponse> actualizarActividad(
            @PathVariable Long actividadId,
            @Valid @RequestBody ActividadEvaluativaRequest request,
            @RequestParam(required = false) String motivoJustificacion,
            @AuthenticationPrincipal Jwt jwt) {
        Long usuarioId = extraerUsuarioId(jwt);
        String rol = extraerRol(jwt);
        return ResponseEntity.ok(calificacionService.actualizarActividad(actividadId, request, motivoJustificacion, usuarioId, rol));
    }

    @DeleteMapping("/actividades/{actividadId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'PROFESOR')")
    public ResponseEntity<Void> eliminarActividad(
            @PathVariable Long actividadId,
            @AuthenticationPrincipal Jwt jwt) {
        Long usuarioId = extraerUsuarioId(jwt);
        String rol = extraerRol(jwt);
        calificacionService.eliminarActividad(actividadId, usuarioId, rol);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/actividades")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'SECRETARIA', 'PROFESOR', 'ESTUDIANTE', 'ACUDIENTE')")
    public ResponseEntity<List<ActividadEvaluativaResponse>> listarActividades(
            @RequestParam Long asignacionId,
            @RequestParam Long periodoId) {
        return ResponseEntity.ok(calificacionService.listarActividadesPorAsignacionYPeriodo(asignacionId, periodoId));
    }

    // =========================================================================
    // 3. REGISTRO Y MODIFICACIÓN DE NOTAS DE ACTIVIDADES (UC-17)
    // =========================================================================

    @PostMapping("/actividades/notas/lote")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'PROFESOR')")
    public ResponseEntity<List<CalificacionActividadResponse>> registrarNotasLote(
            @Valid @RequestBody RegistroCalificacionesLoteRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        Long usuarioId = extraerUsuarioId(jwt);
        String username = extraerUsername(jwt);
        String rol = extraerRol(jwt);
        return ResponseEntity.ok(calificacionService.registrarCalificacionesLote(request, usuarioId, username, rol));
    }

    @PutMapping("/actividades/notas/{calificacionId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'PROFESOR')")
    public ResponseEntity<CalificacionActividadResponse> actualizarNotaActividad(
            @PathVariable Long calificacionId,
            @Valid @RequestBody ActualizarCalificacionRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        Long usuarioId = extraerUsuarioId(jwt);
        String username = extraerUsername(jwt);
        String rol = extraerRol(jwt);
        return ResponseEntity.ok(calificacionService.actualizarCalificacionActividad(calificacionId, request, usuarioId, username, rol));
    }

    @GetMapping("/actividades/{actividadId}/notas")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'SECRETARIA', 'PROFESOR')")
    public ResponseEntity<List<CalificacionActividadResponse>> listarNotasActividad(
            @PathVariable Long actividadId) {
        return ResponseEntity.ok(calificacionService.listarCalificacionesPorActividad(actividadId));
    }

    // =========================================================================
    // 4. DEFINITIVAS DE PERIODO (ASIGNATURA Y ÁREA) (UC-17)
    // =========================================================================

    @PostMapping("/periodo/calcular")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'PROFESOR')")
    public ResponseEntity<List<CalificacionPeriodoResponse>> calcularDefinitivasPeriodo(
            @RequestParam Long asignacionId,
            @RequestParam Long periodoId) {
        return ResponseEntity.ok(calificacionService.calcularDefinitivasPeriodo(asignacionId, periodoId));
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
    // 5. ACTIVIDADES DE APOYO Y ACTAS DE NIVELACIÓN (UC-18)
    // =========================================================================

    @PostMapping("/nivelaciones")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'PROFESOR')")
    public ResponseEntity<ActaNivelacionResponse> registrarActaNivelacion(
            @Valid @RequestBody ActaNivelacionRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        Long usuarioId = extraerUsuarioId(jwt);
        String username = extraerUsername(jwt);
        return ResponseEntity.ok(nivelacionService.registrarActaNivelacion(request, usuarioId, username));
    }

    @PostMapping("/periodo/{calificacionPeriodoId}/recuperacion")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'PROFESOR')")
    public ResponseEntity<ActaNivelacionResponse> registrarRecuperacion(
            @PathVariable Long calificacionPeriodoId,
            @RequestParam BigDecimal notaRecuperacion,
            @RequestParam(required = false) String motivo,
            @AuthenticationPrincipal Jwt jwt) {
        Long usuarioId = extraerUsuarioId(jwt);
        String username = extraerUsername(jwt);
        ActaNivelacionRequest req = new ActaNivelacionRequest();
        req.setCalificacionPeriodoId(calificacionPeriodoId);
        req.setNotaObtenida(notaRecuperacion);
        req.setObservaciones(motivo);
        return ResponseEntity.ok(nivelacionService.registrarActaNivelacion(req, usuarioId, username));
    }

    @GetMapping("/nivelaciones/matricula/{matriculaId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'SECRETARIA', 'PROFESOR')")
    public ResponseEntity<List<ActaNivelacionResponse>> listarNivelacionesMatricula(
            @PathVariable Long matriculaId) {
        return ResponseEntity.ok(nivelacionService.listarActasPorMatricula(matriculaId));
    }

    // =========================================================================
    // 6. CONSULTAS CONTEXTUALES SEGURAS ANTI-IDOR (UC-02 y UC-08)
    // =========================================================================

    @GetMapping("/mis-calificaciones")
    @PreAuthorize("hasRole('ESTUDIANTE')")
    public ResponseEntity<ConsultaCalificacionesEstudianteResponse> consultarMisCalificaciones(
            @RequestParam(required = false) Long anioLectivoId,
            @RequestParam Long periodoId,
            @AuthenticationPrincipal Jwt jwt) {
        Long usuarioId = extraerUsuarioId(jwt);
        return ResponseEntity.ok(calificacionService.consultarMisCalificacionesEstudiante(usuarioId, anioLectivoId, periodoId));
    }

    @GetMapping("/acudiente/estudiante/{estudianteId}")
    @PreAuthorize("hasRole('ACUDIENTE')")
    public ResponseEntity<ConsultaCalificacionesEstudianteResponse> consultarCalificacionesAcudiente(
            @PathVariable Long estudianteId,
            @RequestParam(required = false) Long anioLectivoId,
            @RequestParam Long periodoId,
            @AuthenticationPrincipal Jwt jwt) {
        Long usuarioId = extraerUsuarioId(jwt);
        return ResponseEntity.ok(calificacionService.consultarCalificacionesAcudiente(usuarioId, estudianteId, anioLectivoId, periodoId));
    }

    // =========================================================================
    // 7. RECLAMACIONES ACADÉMICAS SOBRE CALIFICACIONES (UC-PROV-15)
    // =========================================================================

    @PostMapping("/reclamaciones")
    @PreAuthorize("hasAnyRole('ESTUDIANTE', 'ACUDIENTE')")
    public ResponseEntity<ReclamacionCalificacionResponse> radicarReclamacion(
            @Valid @RequestBody ReclamacionCalificacionRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        Long usuarioId = extraerUsuarioId(jwt);
        return ResponseEntity.ok(reclamacionService.radicarReclamacion(request, usuarioId));
    }

    @PutMapping("/reclamaciones/{reclamacionId}/primera-instancia")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'PROFESOR')")
    public ResponseEntity<ReclamacionCalificacionResponse> resolverPrimeraInstancia(
            @PathVariable Long reclamacionId,
            @Valid @RequestBody ResolverReclamacionRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        Long usuarioId = extraerUsuarioId(jwt);
        String username = extraerUsername(jwt);
        return ResponseEntity.ok(reclamacionService.resolverPrimeraInstancia(reclamacionId, request, usuarioId, username));
    }

    @PutMapping("/reclamaciones/{reclamacionId}/escalar")
    @PreAuthorize("hasAnyRole('ESTUDIANTE', 'ACUDIENTE', 'COORDINADOR')")
    public ResponseEntity<ReclamacionCalificacionResponse> escalarSegundaInstancia(
            @PathVariable Long reclamacionId,
            @AuthenticationPrincipal Jwt jwt) {
        Long usuarioId = extraerUsuarioId(jwt);
        return ResponseEntity.ok(reclamacionService.escalarSegundaInstancia(reclamacionId, usuarioId));
    }

    @PutMapping("/reclamaciones/{reclamacionId}/segunda-instancia")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR')")
    public ResponseEntity<ReclamacionCalificacionResponse> resolverSegundaInstancia(
            @PathVariable Long reclamacionId,
            @Valid @RequestBody ResolverReclamacionRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        Long usuarioId = extraerUsuarioId(jwt);
        String username = extraerUsername(jwt);
        return ResponseEntity.ok(reclamacionService.resolverSegundaInstancia(reclamacionId, request, usuarioId, username));
    }

    @GetMapping("/reclamaciones/matricula/{matriculaId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'SECRETARIA', 'PROFESOR', 'ESTUDIANTE', 'ACUDIENTE')")
    public ResponseEntity<List<ReclamacionCalificacionResponse>> listarReclamacionesMatricula(
            @PathVariable Long matriculaId) {
        return ResponseEntity.ok(reclamacionService.listarReclamacionesMatricula(matriculaId));
    }

    // =========================================================================
    // 8. CONVALIDACIÓN Y HOMOLOGACIÓN DE CALIFICACIONES (UC-PROV-16)
    // =========================================================================

    @PostMapping("/convalidaciones")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR')")
    public ResponseEntity<ConvalidacionCalificacionResponse> registrarConvalidacion(
            @Valid @RequestBody ConvalidacionCalificacionRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        Long usuarioId = extraerUsuarioId(jwt);
        return ResponseEntity.ok(convalidacionService.registrarConvalidacion(request, usuarioId));
    }

    @GetMapping("/convalidaciones/matricula/{matriculaId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'SECRETARIA')")
    public ResponseEntity<List<ConvalidacionCalificacionResponse>> listarConvalidacionesMatricula(
            @PathVariable Long matriculaId) {
        return ResponseEntity.ok(convalidacionService.listarConvalidacionesMatricula(matriculaId));
    }

    // =========================================================================
    // 9. SÁBANA CONSOLIDADA, AVANCE DOCENTE Y ALERTAS (UC-PROV-17)
    // =========================================================================

    @GetMapping("/sabana")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'PROFESOR')")
    public ResponseEntity<SabanaConsolidadaResponse> consultarSabanaConsolidada(
            @RequestParam Long grupoId,
            @RequestParam Long periodoId,
            @AuthenticationPrincipal Jwt jwt) {
        Long usuarioId = extraerUsuarioId(jwt);
        String rol = extraerRol(jwt);
        return ResponseEntity.ok(calificacionService.consultarSabanaConsolidada(grupoId, periodoId, usuarioId, rol));
    }

    @GetMapping("/avance-docente")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR')")
    public ResponseEntity<List<TableroAvanceDocenteResponse>> consultarTableroAvanceDocente(
            @RequestParam Long anioLectivoId,
            @RequestParam Long periodoId) {
        return ResponseEntity.ok(calificacionService.consultarTableroAvanceDocente(anioLectivoId, periodoId));
    }

    // =========================================================================
    // 10. AUDITORÍA INMUTABLE
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

    private String extraerRol(Jwt jwt) {
        if (jwt != null && jwt.hasClaim("rol")) {
            return jwt.getClaimAsString("rol");
        }
        if (jwt != null && jwt.hasClaim("roles")) {
            Object rolesObj = jwt.getClaim("roles");
            if (rolesObj instanceof List<?> list && !list.isEmpty()) {
                return list.get(0).toString().replace("ROLE_", "");
            }
        }
        return null;
    }
}
