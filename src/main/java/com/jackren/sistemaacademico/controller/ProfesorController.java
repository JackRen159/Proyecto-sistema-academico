package com.jackren.sistemaacademico.controller;

import com.jackren.sistemaacademico.dto.ProfesorRequest;
import com.jackren.sistemaacademico.dto.ProfesorResponse;
import com.jackren.sistemaacademico.service.ProfesorService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/profesores")
public class ProfesorController {

    private final ProfesorService profesorService;

    public ProfesorController(ProfesorService profesorService) {
        this.profesorService = profesorService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'SECRETARIA', 'PROFESOR')")
    public ResponseEntity<List<ProfesorResponse>> listar(
            @RequestParam(required = false, defaultValue = "true") boolean soloActivos) {
        if (soloActivos) {
            return ResponseEntity.ok(profesorService.listarActivos());
        }
        return ResponseEntity.ok(profesorService.listarTodos());
    }

    @GetMapping("/paginado")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'SECRETARIA', 'PROFESOR')")
    public ResponseEntity<Page<ProfesorResponse>> listarPaginado(Pageable pageable) {
        return ResponseEntity.ok(profesorService.listarPaginado(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'SECRETARIA', 'PROFESOR')")
    public ResponseEntity<ProfesorResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(profesorService.obtenerPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'SECRETARIA')")
    public ResponseEntity<ProfesorResponse> crear(@Valid @RequestBody ProfesorRequest request) {
        ProfesorResponse response = profesorService.crear(request);
        URI location = URI.create("/api/profesores/" + response.getId());
        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'SECRETARIA')")
    public ResponseEntity<ProfesorResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ProfesorRequest request) {
        return ResponseEntity.ok(profesorService.actualizar(id, request));
    }

    @PatchMapping("/{id}/desactivar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR')")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        profesorService.desactivar(id);
        return ResponseEntity.noContent().build();
    }
}
