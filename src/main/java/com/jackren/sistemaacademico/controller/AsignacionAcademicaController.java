package com.jackren.sistemaacademico.controller;

import com.jackren.sistemaacademico.dto.AsignacionAcademicaRequest;
import com.jackren.sistemaacademico.dto.AsignacionAcademicaResponse;
import com.jackren.sistemaacademico.service.AsignacionAcademicaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/asignaciones-academicas")
public class AsignacionAcademicaController {

    private final AsignacionAcademicaService asignacionService;

    public AsignacionAcademicaController(AsignacionAcademicaService asignacionService) {
        this.asignacionService = asignacionService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'SECRETARIA', 'PROFESOR')")
    public ResponseEntity<List<AsignacionAcademicaResponse>> listar(
            @RequestParam(required = false) Long profesorId,
            @RequestParam(required = false) Long grupoId,
            @RequestParam(required = false) Long anioLectivoId) {
        if (profesorId != null) {
            return ResponseEntity.ok(asignacionService.listarPorProfesor(profesorId));
        }
        if (grupoId != null) {
            return ResponseEntity.ok(asignacionService.listarPorGrupo(grupoId));
        }
        if (anioLectivoId != null) {
            return ResponseEntity.ok(asignacionService.listarPorAnioLectivo(anioLectivoId));
        }
        throw new IllegalArgumentException("Debe proporcionar al menos un filtro: profesorId, grupoId o anioLectivoId");
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'SECRETARIA', 'PROFESOR')")
    public ResponseEntity<AsignacionAcademicaResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(asignacionService.obtenerPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR')")
    public ResponseEntity<AsignacionAcademicaResponse> crear(@Valid @RequestBody AsignacionAcademicaRequest request) {
        AsignacionAcademicaResponse response = asignacionService.crear(request);
        URI location = URI.create("/api/asignaciones-academicas/" + response.getId());
        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR')")
    public ResponseEntity<AsignacionAcademicaResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody AsignacionAcademicaRequest request) {
        return ResponseEntity.ok(asignacionService.actualizar(id, request));
    }

    @PatchMapping("/{id}/desactivar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR')")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        asignacionService.desactivar(id);
        return ResponseEntity.noContent().build();
    }
}
