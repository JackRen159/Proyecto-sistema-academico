package com.jackren.sistemaacademico.controller;

import com.jackren.sistemaacademico.dto.GrupoRequest;
import com.jackren.sistemaacademico.dto.GrupoResponse;
import com.jackren.sistemaacademico.service.GrupoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/grupos")
public class GrupoController {

    private final GrupoService grupoService;

    public GrupoController(GrupoService grupoService) {
        this.grupoService = grupoService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'SECRETARIA', 'PROFESOR')")
    public ResponseEntity<List<GrupoResponse>> listar(
            @RequestParam(required = false) Long anioLectivoId,
            @RequestParam(required = false) Long gradoId) {
        if (anioLectivoId != null && gradoId != null) {
            return ResponseEntity.ok(grupoService.listarPorAnioYGrado(anioLectivoId, gradoId));
        }
        if (anioLectivoId != null) {
            return ResponseEntity.ok(grupoService.listarPorAnio(anioLectivoId));
        }
        throw new IllegalArgumentException("Debe proporcionar al menos el parámetro anioLectivoId");
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'SECRETARIA', 'PROFESOR')")
    public ResponseEntity<GrupoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(grupoService.obtenerPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'SECRETARIA')")
    public ResponseEntity<GrupoResponse> guardar(@Valid @RequestBody GrupoRequest request) {
        GrupoResponse response = grupoService.guardar(request);
        URI location = URI.create("/api/grupos/" + response.getId());
        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'SECRETARIA')")
    public ResponseEntity<GrupoResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody GrupoRequest request) {
        return ResponseEntity.ok(grupoService.actualizar(id, request));
    }

    @PatchMapping("/{id}/desactivar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR')")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        grupoService.desactivar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR')")
    public ResponseEntity<Void> activar(@PathVariable Long id) {
        grupoService.activar(id);
        return ResponseEntity.noContent().build();
    }
}
