package com.jackren.sistemaacademico.controller;

import com.jackren.sistemaacademico.dto.AsignaturaRequest;
import com.jackren.sistemaacademico.dto.AsignaturaResponse;
import com.jackren.sistemaacademico.service.AsignaturaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/asignaturas")
public class AsignaturaController {

    private final AsignaturaService asignaturaService;

    public AsignaturaController(AsignaturaService asignaturaService) {
        this.asignaturaService = asignaturaService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'SECRETARIA', 'PROFESOR')")
    public ResponseEntity<List<AsignaturaResponse>> listar(
            @RequestParam(required = false) Long areaId,
            @RequestParam(defaultValue = "true") boolean soloActivas) {
        if (areaId != null) {
            return ResponseEntity.ok(asignaturaService.listarPorArea(areaId));
        }
        if (soloActivas) {
            return ResponseEntity.ok(asignaturaService.listarActivas());
        }
        return ResponseEntity.ok(asignaturaService.listarTodas());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'SECRETARIA', 'PROFESOR')")
    public ResponseEntity<AsignaturaResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(asignaturaService.obtenerPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR')")
    public ResponseEntity<AsignaturaResponse> guardar(@Valid @RequestBody AsignaturaRequest request) {
        AsignaturaResponse response = asignaturaService.guardar(request);
        URI location = URI.create("/api/asignaturas/" + response.getId());
        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR')")
    public ResponseEntity<AsignaturaResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody AsignaturaRequest request) {
        return ResponseEntity.ok(asignaturaService.actualizar(id, request));
    }

    @PatchMapping("/{id}/desactivar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR')")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        asignaturaService.desactivar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR')")
    public ResponseEntity<Void> activar(@PathVariable Long id) {
        asignaturaService.activar(id);
        return ResponseEntity.noContent().build();
    }
}
