package com.jackren.sistemaacademico.controller;

import com.jackren.sistemaacademico.dto.*;
import com.jackren.sistemaacademico.service.RepositorioDocumentalService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/repositorio")
public class RepositorioDocumentalController {

    private final RepositorioDocumentalService repositorioService;

    public RepositorioDocumentalController(RepositorioDocumentalService repositorioService) {
        this.repositorioService = repositorioService;
    }

    @PostMapping("/documentos")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'COORDINADOR_ACADEMICO', 'COORDINADOR_CONVIVENCIA', 'SECRETARIA', 'PROFESOR')")
    public ResponseEntity<RepositorioDocumentoResponse> cargarDocumento(
            @Valid @RequestBody CargarDocumentoRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        Long usuarioId = extraerUsuarioId(jwt);
        return ResponseEntity.ok(repositorioService.cargarDocumento(request, usuarioId));
    }

    @PostMapping("/documentos/{id}/versiones")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'SECRETARIA')")
    public ResponseEntity<RepositorioDocumentoVersionResponse> crearNuevaVersion(
            @PathVariable Long id,
            @Valid @RequestBody NuevaVersionDocumentoRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        Long usuarioId = extraerUsuarioId(jwt);
        return ResponseEntity.ok(repositorioService.crearNuevaVersion(id, request, usuarioId));
    }

    @PatchMapping("/documentos/{id}/anular")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'SECRETARIA')")
    public ResponseEntity<RepositorioDocumentoResponse> anularDocumento(
            @PathVariable Long id,
            @Valid @RequestBody AnularDocumentoRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        Long usuarioId = extraerUsuarioId(jwt);
        return ResponseEntity.ok(repositorioService.anularDocumentoLogico(id, request.motivoAnulacion(), usuarioId));
    }

    @DeleteMapping("/documentos/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR')")
    public ResponseEntity<Void> eliminarDocumentoFisico(@PathVariable Long id) {
        repositorioService.eliminarDocumentoFisico(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/documentos/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'SECRETARIA', 'PROFESOR', 'ESTUDIANTE', 'ACUDIENTE')")
    public ResponseEntity<RepositorioDocumentoResponse> obtenerPorId(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {
        Long usuarioId = extraerUsuarioId(jwt);
        return ResponseEntity.ok(repositorioService.obtenerPorId(id, usuarioId));
    }

    @GetMapping("/documentos/estudiante/{estudianteId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'SECRETARIA', 'PROFESOR', 'ESTUDIANTE', 'ACUDIENTE')")
    public ResponseEntity<List<RepositorioDocumentoResponse>> listarPorEstudiante(
            @PathVariable Long estudianteId,
            @AuthenticationPrincipal Jwt jwt) {
        Long usuarioId = extraerUsuarioId(jwt);
        return ResponseEntity.ok(repositorioService.listarPorEstudiante(estudianteId, usuarioId));
    }

    @GetMapping("/documentos/{id}/versiones")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'SECRETARIA')")
    public ResponseEntity<List<RepositorioDocumentoVersionResponse>> listarVersiones(@PathVariable Long id) {
        return ResponseEntity.ok(repositorioService.listarVersiones(id));
    }

    @GetMapping("/documentos/{id}/enlace-descarga")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'SECRETARIA', 'PROFESOR', 'ESTUDIANTE', 'ACUDIENTE')")
    public ResponseEntity<EnlaceDescargaResponse> generarEnlaceDescarga(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {
        Long usuarioId = extraerUsuarioId(jwt);
        return ResponseEntity.ok(repositorioService.generarEnlaceDescargaEfimero(id, usuarioId));
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
