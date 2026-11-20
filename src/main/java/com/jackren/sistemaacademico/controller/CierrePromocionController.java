package com.jackren.sistemaacademico.controller;

import com.jackren.sistemaacademico.dto.*;
import com.jackren.sistemaacademico.service.CierrePeriodoService;
import com.jackren.sistemaacademico.service.ComisionPeriodicaService;
import com.jackren.sistemaacademico.service.GraduacionMediaService;
import com.jackren.sistemaacademico.service.PromocionAnualService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cierre-promocion")
public class CierrePromocionController {

    private final CierrePeriodoService cierrePeriodoService;
    private final ComisionPeriodicaService comisionPeriodicaService;
    private final PromocionAnualService promocionAnualService;
    private final GraduacionMediaService graduacionMediaService;

    public CierrePromocionController(CierrePeriodoService cierrePeriodoService,
                                     ComisionPeriodicaService comisionPeriodicaService,
                                     PromocionAnualService promocionAnualService,
                                     GraduacionMediaService graduacionMediaService) {
        this.cierrePeriodoService = cierrePeriodoService;
        this.comisionPeriodicaService = comisionPeriodicaService;
        this.promocionAnualService = promocionAnualService;
        this.graduacionMediaService = graduacionMediaService;
    }

    // =========================================================================
    // 1. CIERRE Y REAPERTURA DE PERIODO ACADÉMICO (UC-20)
    // =========================================================================

    @GetMapping("/periodos/{periodoId}/diagnostico")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR')")
    public ResponseEntity<DiagnosticoPreCierreResponse> diagnosticarPreCierre(@PathVariable Long periodoId) {
        return ResponseEntity.ok(cierrePeriodoService.diagnosticarPreCierre(periodoId));
    }

    @PostMapping("/periodos/cerrar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR')")
    public ResponseEntity<List<BoletinPublicadoResponse>> cerrarPeriodo(
            @Valid @RequestBody CierrePeriodoRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        Long usuarioId = extraerUsuarioId(jwt);
        return ResponseEntity.ok(cierrePeriodoService.cerrarPeriodo(request, usuarioId));
    }

    @PostMapping("/periodos/reabrir")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR')")
    public ResponseEntity<Void> reabrirPeriodo(
            @Valid @RequestBody ReaperturaPeriodoRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        Long usuarioId = extraerUsuarioId(jwt);
        cierrePeriodoService.reabrirPeriodo(request, usuarioId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/boletines/matricula/{matriculaId}/periodo/{periodoId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'SECRETARIA', 'PROFESOR', 'ESTUDIANTE', 'ACUDIENTE')")
    public ResponseEntity<List<BoletinPublicadoResponse>> listarBoletinesMatricula(
            @PathVariable Long matriculaId,
            @PathVariable Long periodoId) {
        return ResponseEntity.ok(cierrePeriodoService.listarBoletinesMatricula(matriculaId, periodoId));
    }

    // =========================================================================
    // 2. OBSERVACIONES DE GRUPO Y COMISIONES PERIÓDICAS (UC-PROV-23)
    // =========================================================================

    @PostMapping("/observaciones-boletin")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'PROFESOR')")
    public ResponseEntity<ObservacionBoletinGrupoResponse> registrarObservacionGrupo(
            @Valid @RequestBody ObservacionBoletinGrupoRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        Long usuarioId = extraerUsuarioId(jwt);
        String rol = extraerRol(jwt);
        return ResponseEntity.ok(comisionPeriodicaService.registrarObservacionGrupo(request, usuarioId, rol));
    }

    @GetMapping("/observaciones-boletin/matricula/{matriculaId}/periodo/{periodoId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'SECRETARIA', 'PROFESOR', 'ESTUDIANTE', 'ACUDIENTE')")
    public ResponseEntity<ObservacionBoletinGrupoResponse> obtenerObservacionEstudiante(
            @PathVariable Long matriculaId,
            @PathVariable Long periodoId) {
        return ResponseEntity.ok(comisionPeriodicaService.obtenerObservacionEstudiante(matriculaId, periodoId));
    }

    @PostMapping("/comisiones-periodicas")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR')")
    public ResponseEntity<ActaComisionPeriodicaResponse> registrarActaComisionPeriodica(
            @Valid @RequestBody ActaComisionPeriodicaRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        Long usuarioId = extraerUsuarioId(jwt);
        return ResponseEntity.ok(comisionPeriodicaService.registrarActaComisionPeriodica(request, usuarioId));
    }

    // =========================================================================
    // 3. PROMOCIÓN ANUAL Y PROMOCIÓN ANTICIPADA (UC-21)
    // =========================================================================

    @GetMapping("/promocion/preclasificacion/grupo/{grupoId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR')")
    public ResponseEntity<List<PreclasificacionPromocionResponse>> preclasificarPromocionGrupo(
            @PathVariable Long grupoId) {
        return ResponseEntity.ok(promocionAnualService.preclasificarPromocionGrupo(grupoId));
    }

    @PostMapping("/promocion/actas")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR')")
    public ResponseEntity<ActaPromocionAnualResponse> registrarActaPromocionAnual(
            @Valid @RequestBody ActaPromocionAnualRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        Long usuarioId = extraerUsuarioId(jwt);
        return ResponseEntity.ok(promocionAnualService.registrarActaPromocionAnual(request, usuarioId));
    }

    @PostMapping("/promocion/anticipada")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR')")
    public ResponseEntity<PromocionEstudianteResponse> tramitarPromocionAnticipada(
            @Valid @RequestBody PromocionAnticipadaRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        Long usuarioId = extraerUsuarioId(jwt);
        return ResponseEntity.ok(promocionAnualService.tramitarPromocionAnticipada(request, usuarioId));
    }

    // =========================================================================
    // 4. GRADUACIÓN DE EDUCACIÓN MEDIA Y CIERRE ANUAL (UC-PROV-24)
    // =========================================================================

    @PostMapping("/graduacion/requisitos")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'SECRETARIA')")
    public ResponseEntity<TitulacionGraduandoResponse> registrarRequisitosTitulacion(
            @Valid @RequestBody TitulacionMediaRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        Long usuarioId = extraerUsuarioId(jwt);
        return ResponseEntity.ok(graduacionMediaService.registrarRequisitosTitulacion(request, usuarioId));
    }

    @PostMapping("/graduacion/aprobar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR')")
    public ResponseEntity<List<TitulacionGraduandoResponse>> aprobarGraduacionMedia(
            @Valid @RequestBody AprobarGraduacionRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        Long usuarioId = extraerUsuarioId(jwt);
        return ResponseEntity.ok(graduacionMediaService.aprobarGraduacionMedia(request, usuarioId));
    }

    @PostMapping("/anio-lectivo/{anioLectivoId}/cerrar-definitivo")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR')")
    public ResponseEntity<Void> cerrarAnioLectivoEscolar(
            @PathVariable Long anioLectivoId,
            @AuthenticationPrincipal Jwt jwt) {
        Long usuarioId = extraerUsuarioId(jwt);
        graduacionMediaService.cerrarAnioLectivoEscolar(anioLectivoId, usuarioId);
        return ResponseEntity.ok().build();
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
