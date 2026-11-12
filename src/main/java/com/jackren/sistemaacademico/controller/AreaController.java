package com.jackren.sistemaacademico.controller;

import com.jackren.sistemaacademico.dto.AreaRequest;
import com.jackren.sistemaacademico.dto.AreaResponse;
import com.jackren.sistemaacademico.service.AreaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/areas")
public class AreaController {

    private final AreaService areaService;

    public AreaController(AreaService areaService) {
        this.areaService = areaService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'SECRETARIA', 'PROFESOR')")
    public ResponseEntity<List<AreaResponse>> listar(
            @RequestParam(defaultValue = "false") boolean soloActivas) {
        if (soloActivas) {
            return ResponseEntity.ok(areaService.listarActivas());
        }
        return ResponseEntity.ok(areaService.listarTodas());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'SECRETARIA', 'PROFESOR')")
    public ResponseEntity<AreaResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(areaService.obtenerPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR')")
    public ResponseEntity<AreaResponse> guardar(@Valid @RequestBody AreaRequest request) {
        AreaResponse response = areaService.guardar(request);
        URI location = URI.create("/api/areas/" + response.getId());
        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR')")
    public ResponseEntity<AreaResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody AreaRequest request) {
        return ResponseEntity.ok(areaService.actualizar(id, request));
    }

    @PatchMapping("/{id}/desactivar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR')")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        areaService.desactivar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR')")
    public ResponseEntity<Void> activar(@PathVariable Long id) {
        areaService.activar(id);
        return ResponseEntity.noContent().build();
    }
}
