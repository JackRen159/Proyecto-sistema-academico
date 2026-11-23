package com.jackren.sistemaacademico.controller;

import com.jackren.sistemaacademico.dto.*;
import com.jackren.sistemaacademico.service.AsistenciaService;
import com.jackren.sistemaacademico.service.ExcusaAsistenciaService;
import com.jackren.sistemaacademico.service.SalidaAnticipadaService;
import com.jackren.sistemaacademico.service.SeguimientoAusentismoService;
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
    private final ExcusaAsistenciaService excusaService;
    private final SalidaAnticipadaService salidaService;
    private final SeguimientoAusentismoService seguimientoService;

    public AsistenciaController(AsistenciaService asistenciaService,
                                ExcusaAsistenciaService excusaService,
                                SalidaAnticipadaService salidaService,
                                SeguimientoAusentismoService seguimientoService) {
        this.asistenciaService = asistenciaService;
        this.excusaService = excusaService;
        this.salidaService = salidaService;
        this.seguimientoService = seguimientoService;
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
    // 2. SESIONES DE ASISTENCIA (UC-19)
    // =========================================================================

    @PostMapping("/sesiones")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'PROFESOR')")
    public ResponseEntity<SesionAsistenciaResponse> crearSesion(
            @Valid @RequestBody SesionAsistenciaRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        Long usuarioId = extraerUsuarioId(jwt);
        String rol = extraerRol(jwt);
        SesionAsistenciaResponse response = asistenciaService.crearSesion(request, usuarioId, rol);
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
    // 3. TOMA DE ASISTENCIA Y MODIFICACIÓN AUDITADA (UC-19)
    // =========================================================================

    @PostMapping("/sesiones/planilla")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'PROFESOR')")
    public ResponseEntity<List<RegistroAsistenciaResponse>> registrarPlanillaLote(
            @Valid @RequestBody PlanillaAsistenciaLoteRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        Long usuarioId = extraerUsuarioId(jwt);
        String username = extraerUsername(jwt);
        String rol = extraerRol(jwt);
        return ResponseEntity.ok(asistenciaService.registrarPlanillaLote(request, usuarioId, username, rol));
    }

    @GetMapping("/sesiones/{sesionId}/registros")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'SECRETARIA', 'PROFESOR')")
    public ResponseEntity<List<RegistroAsistenciaResponse>> listarRegistrosPorSesion(
            @PathVariable Long sesionId) {
        return ResponseEntity.ok(asistenciaService.listarRegistrosPorSesion(sesionId));
    }

    // =========================================================================
    // 4. JUSTIFICACIÓN DE INASISTENCIA (COMPATIBILIDAD)
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
    // 5. RESÚMENES, PORCENTAJES Y CONSULTAS CONTEXTUALES (UC-03 y UC-09)
    // =========================================================================

    @GetMapping("/estudiante/{matriculaId}/resumen")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'SECRETARIA', 'PROFESOR', 'ESTUDIANTE', 'ACUDIENTE')")
    public ResponseEntity<ResumenAsistenciaEstudianteResponse> obtenerResumenEstudiante(
            @PathVariable Long matriculaId,
            @RequestParam Long periodoId,
            @RequestParam Long asignacionId) {
        return ResponseEntity.ok(asistenciaService.obtenerResumenEstudianteAsignatura(matriculaId, periodoId, asignacionId));
    }

    @GetMapping("/mis-asistencias")
    @PreAuthorize("hasRole('ESTUDIANTE')")
    public ResponseEntity<ConsultaAsistenciaEstudianteResponse> consultarMisAsistencias(
            @RequestParam Long periodoId,
            @AuthenticationPrincipal Jwt jwt) {
        Long usuarioId = extraerUsuarioId(jwt);
        return ResponseEntity.ok(asistenciaService.consultarMisAsistenciasEstudiante(usuarioId, periodoId));
    }

    @GetMapping("/acudiente/estudiante/{estudianteId}")
    @PreAuthorize("hasRole('ACUDIENTE')")
    public ResponseEntity<ConsultaAsistenciaEstudianteResponse> consultarAsistenciasAcudiente(
            @PathVariable Long estudianteId,
            @RequestParam Long periodoId,
            @AuthenticationPrincipal Jwt jwt) {
        Long usuarioId = extraerUsuarioId(jwt);
        return ResponseEntity.ok(asistenciaService.consultarAsistenciasAcudiente(usuarioId, estudianteId, periodoId));
    }

    @GetMapping("/grupo/{grupoId}/alertas")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'SECRETARIA', 'PROFESOR')")
    public ResponseEntity<List<ResumenAsistenciaEstudianteResponse>> listarAlertasGrupo(
            @PathVariable Long grupoId,
            @RequestParam Long periodoId,
            @RequestParam Long asignacionId) {
        return ResponseEntity.ok(asistenciaService.listarAlertasPorGrupoYPeriodo(grupoId, periodoId, asignacionId));
    }

    // =========================================================================
    // 6. GESTIÓN DE EXCUSAS POR RANGO DE FECHAS (UC-PROV-18)
    // =========================================================================

    @PostMapping("/excusas")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'ESTUDIANTE', 'ACUDIENTE', 'SECRETARIA', 'COORDINADOR')")
    public ResponseEntity<SolicitudExcusaAsistenciaResponse> radicarExcusa(
            @Valid @RequestBody SolicitudExcusaAsistenciaRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        Long usuarioId = extraerUsuarioId(jwt);
        return ResponseEntity.ok(excusaService.radicarExcusa(request, usuarioId));
    }

    @PutMapping("/excusas/{excusaId}/resolver")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR')")
    public ResponseEntity<SolicitudExcusaAsistenciaResponse> resolverExcusa(
            @PathVariable Long excusaId,
            @Valid @RequestBody ResolverSolicitudExcusaRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        Long usuarioId = extraerUsuarioId(jwt);
        return ResponseEntity.ok(excusaService.resolverExcusa(excusaId, request, usuarioId));
    }

    @GetMapping("/excusas/matricula/{matriculaId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'SECRETARIA', 'PROFESOR', 'ESTUDIANTE', 'ACUDIENTE')")
    public ResponseEntity<List<SolicitudExcusaAsistenciaResponse>> listarExcusasMatricula(
            @PathVariable Long matriculaId) {
        return ResponseEntity.ok(excusaService.listarExcusasMatricula(matriculaId));
    }

    // =========================================================================
    // 7. AUTORIZACIONES DE SALIDA ANTICIPADA EN PORTERÍA (UC-PROV-18 A1)
    // =========================================================================

    @PostMapping("/salidas-anticipadas")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'SECRETARIA')")
    public ResponseEntity<AutorizacionSalidaAnticipadaResponse> registrarSalidaAnticipada(
            @Valid @RequestBody AutorizacionSalidaAnticipadaRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        Long usuarioId = extraerUsuarioId(jwt);
        return ResponseEntity.ok(salidaService.registrarSalidaAnticipada(request, usuarioId));
    }

    @GetMapping("/salidas-anticipadas/matricula/{matriculaId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'SECRETARIA', 'PROFESOR')")
    public ResponseEntity<List<AutorizacionSalidaAnticipadaResponse>> listarSalidasMatricula(
            @PathVariable Long matriculaId) {
        return ResponseEntity.ok(salidaService.listarSalidasMatricula(matriculaId));
    }

    // =========================================================================
    // 8. ACTUACIONES DE SEGUIMIENTO AL AUSENTISMO Y DESERCIÓN (UC-PROV-19)
    // =========================================================================

    @PostMapping("/seguimiento-ausentismo")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'PROFESOR')")
    public ResponseEntity<ActuacionSeguimientoAusentismoResponse> registrarActuacionSeguimiento(
            @Valid @RequestBody ActuacionSeguimientoAusentismoRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        Long usuarioId = extraerUsuarioId(jwt);
        return ResponseEntity.ok(seguimientoService.registrarActuacion(request, usuarioId));
    }

    @GetMapping("/seguimiento-ausentismo/matricula/{matriculaId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'SECRETARIA', 'PROFESOR')")
    public ResponseEntity<List<ActuacionSeguimientoAusentismoResponse>> listarActuacionesMatricula(
            @PathVariable Long matriculaId) {
        return ResponseEntity.ok(seguimientoService.listarActuacionesMatricula(matriculaId));
    }

    // =========================================================================
    // 9. SÁBANA GRUPAL Y CONTROL DOCENTE (UC-PROV-19)
    // =========================================================================

    @GetMapping("/sabana")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'PROFESOR')")
    public ResponseEntity<SabanaAsistenciaGrupalResponse> consultarSabanaGrupal(
            @RequestParam Long grupoId,
            @RequestParam Long periodoId,
            @AuthenticationPrincipal Jwt jwt) {
        Long usuarioId = extraerUsuarioId(jwt);
        String rol = extraerRol(jwt);
        return ResponseEntity.ok(asistenciaService.consultarSabanaGrupal(grupoId, periodoId, usuarioId, rol));
    }

    @GetMapping("/control-sesiones")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR')")
    public ResponseEntity<List<ControlSesionesDocenteResponse>> consultarControlSesiones(
            @RequestParam Long anioLectivoId,
            @RequestParam Long periodoId) {
        return ResponseEntity.ok(asistenciaService.consultarControlSesionesDocente(anioLectivoId, periodoId));
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
