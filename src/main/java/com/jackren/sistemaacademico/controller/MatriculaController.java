package com.jackren.sistemaacademico.controller;

import com.jackren.sistemaacademico.dto.*;
import com.jackren.sistemaacademico.service.MatriculaService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/matriculas")
public class MatriculaController {

    private final MatriculaService matriculaService;

    public MatriculaController(MatriculaService matriculaService) {
        this.matriculaService = matriculaService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'SECRETARIA')")
    public ResponseEntity<MatriculaResponse> matricular(@Valid @RequestBody MatriculaRequest request) {
        MatriculaResponse response = matriculaService.matricular(request);
        URI location = URI.create("/api/matriculas/" + response.getId());
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'SECRETARIA', 'PROFESOR')")
    public ResponseEntity<MatriculaResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(matriculaService.obtenerPorId(id));
    }

    @GetMapping("/numero/{numeroMatricula}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'SECRETARIA', 'PROFESOR')")
    public ResponseEntity<MatriculaResponse> buscarPorNumero(@PathVariable String numeroMatricula) {
        return ResponseEntity.ok(matriculaService.obtenerPorNumero(numeroMatricula));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'SECRETARIA', 'PROFESOR')")
    public ResponseEntity<List<MatriculaResponse>> listar(
            @RequestParam(required = false) Long grupoId,
            @RequestParam(required = false) Long estudianteId) {
        if (grupoId != null) {
            return ResponseEntity.ok(matriculaService.listarPorGrupo(grupoId));
        }
        if (estudianteId != null) {
            return ResponseEntity.ok(matriculaService.listarPorEstudiante(estudianteId));
        }
        throw new IllegalArgumentException("Debe proporcionar al menos un filtro: grupoId o estudianteId");
    }

    @GetMapping("/anio-lectivo/{anioLectivoId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'SECRETARIA', 'PROFESOR')")
    public ResponseEntity<Page<MatriculaResponse>> listarPorAnioLectivo(
            @PathVariable Long anioLectivoId,
            Pageable pageable) {
        return ResponseEntity.ok(matriculaService.listarPorAnioLectivo(anioLectivoId, pageable));
    }

    @PostMapping("/{id}/traslado")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'SECRETARIA')")
    public ResponseEntity<MatriculaResponse> trasladarDeGrupo(
            @PathVariable Long id,
            @Valid @RequestBody TrasladoGrupoRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        Long usuarioId = null;
        if (jwt != null && jwt.hasClaim("id")) {
            Object idClaim = jwt.getClaim("id");
            if (idClaim instanceof Number number) {
                usuarioId = number.longValue();
            }
        }
        return ResponseEntity.ok(matriculaService.trasladarDeGrupo(id, request, usuarioId));
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'SECRETARIA')")
    public ResponseEntity<MatriculaResponse> cambiarEstado(
            @PathVariable Long id,
            @Valid @RequestBody CambioEstadoMatriculaRequest request) {
        return ResponseEntity.ok(matriculaService.cambiarEstado(id, request));
    }

    @GetMapping("/{id}/historial-traslados")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'SECRETARIA', 'PROFESOR')")
    public ResponseEntity<List<HistorialTrasladoResponse>> listarHistorialTraslados(@PathVariable Long id) {
        return ResponseEntity.ok(matriculaService.listarHistorialTraslados(id));
    }

    @GetMapping("/verificar-activa")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'SECRETARIA', 'PROFESOR')")
    public ResponseEntity<Map<String, Boolean>> verificarMatriculaActiva(
            @RequestParam Long estudianteId,
            @RequestParam Long grupoId) {
        boolean activa = matriculaService.tieneMatriculaActivaEnGrupo(estudianteId, grupoId);
        return ResponseEntity.ok(Map.of("activa", activa));
    }
}
