package com.jackren.sistemaacademico.controller;

import com.jackren.sistemaacademico.dto.*;
import com.jackren.sistemaacademico.service.ReporteInstitucionalService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reportes")
public class ReporteInstitucionalController {

    private final ReporteInstitucionalService reporteService;

    public ReporteInstitucionalController(ReporteInstitucionalService reporteService) {
        this.reporteService = reporteService;
    }

    @GetMapping("/cobertura-simat/{anioLectivoId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'COORDINADOR_ACADEMICO', 'SECRETARIA')")
    public ResponseEntity<ReporteCoberturaSimatResponse> generarReporteCoberturaSimat(
            @PathVariable Long anioLectivoId,
            @AuthenticationPrincipal Jwt jwt) {
        Long usuarioId = extraerUsuarioId(jwt);
        return ResponseEntity.ok(reporteService.generarReporteCoberturaSimat(anioLectivoId, usuarioId));
    }

    @GetMapping("/rendimiento-mortalidad/{anioLectivoId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'COORDINADOR_ACADEMICO')")
    public ResponseEntity<ReporteRendimientoMortalidadResponse> generarReporteRendimientoMortalidad(
            @PathVariable Long anioLectivoId,
            @AuthenticationPrincipal Jwt jwt) {
        Long usuarioId = extraerUsuarioId(jwt);
        return ResponseEntity.ok(reporteService.generarReporteRendimientoMortalidad(anioLectivoId, usuarioId));
    }

    @GetMapping("/convivencia-siuce/{anioLectivoId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR_CONVIVENCIA', 'ORIENTADOR_ESCOLAR')")
    public ResponseEntity<ReporteConvivenciaSiuceResponse> generarReporteConvivenciaSiuce(
            @PathVariable Long anioLectivoId,
            @AuthenticationPrincipal Jwt jwt) {
        Long usuarioId = extraerUsuarioId(jwt);
        return ResponseEntity.ok(reporteService.generarReporteConvivenciaSiuce(anioLectivoId, usuarioId));
    }

    @GetMapping("/exportar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'SECRETARIA')")
    public ResponseEntity<byte[]> exportarReporte(
            @RequestParam String tipoReporte,
            @RequestParam(defaultValue = "CSV") String formato,
            @RequestParam Long anioLectivoId,
            @AuthenticationPrincipal Jwt jwt) {
        Long usuarioId = extraerUsuarioId(jwt);
        byte[] contenido = reporteService.exportarReporte(tipoReporte, formato, anioLectivoId, usuarioId);

        String filename = "reporte_" + tipoReporte.toLowerCase() + "_" + anioLectivoId + "." + formato.toLowerCase();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType("CSV".equalsIgnoreCase(formato) ? MediaType.TEXT_PLAIN : MediaType.APPLICATION_OCTET_STREAM)
                .body(contenido);
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
}
