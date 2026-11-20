package com.jackren.sistemaacademico.controller;

import com.jackren.sistemaacademico.dto.PeriodoAcademicoRequest;
import com.jackren.sistemaacademico.dto.PeriodoAcademicoResponse;
import com.jackren.sistemaacademico.service.PeriodoAcademicoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/periodos")
public class PeriodoAcademicoController {

    private final PeriodoAcademicoService periodoAcademicoService;

    public PeriodoAcademicoController(PeriodoAcademicoService periodoAcademicoService) {
        this.periodoAcademicoService = periodoAcademicoService;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'SECRETARIA', 'PROFESOR')")
    public ResponseEntity<PeriodoAcademicoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(periodoAcademicoService.obtenerPorId(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR')")
    public ResponseEntity<PeriodoAcademicoResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody PeriodoAcademicoRequest request) {
        return ResponseEntity.ok(periodoAcademicoService.actualizar(id, request));
    }

    @PatchMapping("/{id}/abrir")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR')")
    public ResponseEntity<PeriodoAcademicoResponse> abrir(@PathVariable Long id) {
        return ResponseEntity.ok(periodoAcademicoService.abrir(id));
    }

    @PatchMapping("/{id}/cerrar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR')")
    public ResponseEntity<PeriodoAcademicoResponse> cerrar(@PathVariable Long id) {
        return ResponseEntity.ok(periodoAcademicoService.cerrar(id));
    }

    @PatchMapping("/{id}/ajuste-excepcional")
    @PreAuthorize("hasRole('RECTOR')")
    public ResponseEntity<PeriodoAcademicoResponse> ajusteExcepcional(
            @PathVariable Long id,
            @Valid @RequestBody PeriodoAcademicoRequest request,
            @RequestParam String motivo,
            @RequestParam String resolucionRectoral) {
        return ResponseEntity.ok(periodoAcademicoService.ajusteExcepcionalRectoral(id, request, motivo, resolucionRectoral));
    }
}
