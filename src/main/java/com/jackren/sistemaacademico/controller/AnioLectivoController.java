package com.jackren.sistemaacademico.controller;

import com.jackren.sistemaacademico.dto.AnioLectivoRequest;
import com.jackren.sistemaacademico.dto.AnioLectivoResponse;
import com.jackren.sistemaacademico.dto.PeriodoAcademicoRequest;
import com.jackren.sistemaacademico.dto.PeriodoAcademicoResponse;
import com.jackren.sistemaacademico.service.AnioLectivoService;
import com.jackren.sistemaacademico.service.PeriodoAcademicoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/anios-lectivos")
public class AnioLectivoController {

    private final AnioLectivoService anioLectivoService;
    private final PeriodoAcademicoService periodoAcademicoService;

    public AnioLectivoController(AnioLectivoService anioLectivoService,
                                 PeriodoAcademicoService periodoAcademicoService) {
        this.anioLectivoService = anioLectivoService;
        this.periodoAcademicoService = periodoAcademicoService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'SECRETARIA', 'PROFESOR')")
    public ResponseEntity<List<AnioLectivoResponse>> listar() {
        return ResponseEntity.ok(anioLectivoService.listarTodos());
    }

    @GetMapping("/activo")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'SECRETARIA', 'PROFESOR', 'ESTUDIANTE', 'ACUDIENTE')")
    public ResponseEntity<AnioLectivoResponse> obtenerActivo() {
        return ResponseEntity.ok(anioLectivoService.obtenerActivo());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'SECRETARIA', 'PROFESOR')")
    public ResponseEntity<AnioLectivoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(anioLectivoService.obtenerPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR')")
    public ResponseEntity<AnioLectivoResponse> guardar(@Valid @RequestBody AnioLectivoRequest request) {
        AnioLectivoResponse response = anioLectivoService.guardar(request);
        URI location = URI.create("/api/anios-lectivos/" + response.getId());
        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR')")
    public ResponseEntity<AnioLectivoResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody AnioLectivoRequest request) {
        return ResponseEntity.ok(anioLectivoService.actualizar(id, request));
    }

    @PatchMapping("/{id}/activar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR')")
    public ResponseEntity<AnioLectivoResponse> activar(@PathVariable Long id) {
        return ResponseEntity.ok(anioLectivoService.activar(id));
    }

    @PatchMapping("/{id}/cerrar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR')")
    public ResponseEntity<AnioLectivoResponse> cerrar(@PathVariable Long id) {
        return ResponseEntity.ok(anioLectivoService.cerrar(id));
    }

    @GetMapping("/{anioId}/periodos")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'SECRETARIA', 'PROFESOR')")
    public ResponseEntity<List<PeriodoAcademicoResponse>> listarPeriodos(@PathVariable Long anioId) {
        return ResponseEntity.ok(periodoAcademicoService.listarPorAnio(anioId));
    }

    @PostMapping("/{anioId}/periodos")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR')")
    public ResponseEntity<PeriodoAcademicoResponse> crearPeriodo(
            @PathVariable Long anioId,
            @Valid @RequestBody PeriodoAcademicoRequest request) {
        PeriodoAcademicoResponse response = periodoAcademicoService.guardar(anioId, request);
        URI location = URI.create("/api/periodos/" + response.getId());
        return ResponseEntity.created(location).body(response);
    }
}
