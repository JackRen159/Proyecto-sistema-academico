package com.jackren.sistemaacademico.controller;

import com.jackren.sistemaacademico.dto.GradoAsignaturaRequest;
import com.jackren.sistemaacademico.dto.GradoAsignaturaResponse;
import com.jackren.sistemaacademico.dto.GradoResponse;
import com.jackren.sistemaacademico.entity.NivelEducativo;
import com.jackren.sistemaacademico.service.GradoAsignaturaService;
import com.jackren.sistemaacademico.service.GradoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/grados")
public class GradoController {

    private final GradoService gradoService;
    private final GradoAsignaturaService gradoAsignaturaService;

    public GradoController(GradoService gradoService, GradoAsignaturaService gradoAsignaturaService) {
        this.gradoService = gradoService;
        this.gradoAsignaturaService = gradoAsignaturaService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'SECRETARIA', 'PROFESOR')")
    public ResponseEntity<List<GradoResponse>> listar(
            @RequestParam(required = false) NivelEducativo nivel,
            @RequestParam(defaultValue = "true") boolean soloActivos) {
        if (nivel != null) {
            return ResponseEntity.ok(gradoService.listarPorNivel(nivel));
        }
        if (soloActivos) {
            return ResponseEntity.ok(gradoService.listarActivos());
        }
        return ResponseEntity.ok(gradoService.listarTodos());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'SECRETARIA', 'PROFESOR')")
    public ResponseEntity<GradoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(gradoService.obtenerPorId(id));
    }

    @GetMapping("/codigo/{codigo}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'SECRETARIA', 'PROFESOR')")
    public ResponseEntity<GradoResponse> buscarPorCodigo(@PathVariable String codigo) {
        return ResponseEntity.ok(gradoService.obtenerPorCodigo(codigo));
    }

    @GetMapping("/{gradoId}/plan-estudios")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'SECRETARIA', 'PROFESOR')")
    public ResponseEntity<List<GradoAsignaturaResponse>> listarPlanEstudios(@PathVariable Long gradoId) {
        return ResponseEntity.ok(gradoAsignaturaService.listarPorGrado(gradoId));
    }

    @PostMapping("/{gradoId}/plan-estudios")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR')")
    public ResponseEntity<GradoAsignaturaResponse> asignarAsignatura(
            @PathVariable Long gradoId,
            @Valid @RequestBody GradoAsignaturaRequest request) {
        request.setGradoId(gradoId);
        GradoAsignaturaResponse response = gradoAsignaturaService.asignar(request);
        URI location = URI.create("/api/grados/" + gradoId + "/plan-estudios/" + response.getId());
        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/plan-estudios/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR')")
    public ResponseEntity<GradoAsignaturaResponse> actualizarAsignacion(
            @PathVariable Long id,
            @RequestParam(required = false) Integer intensidadHoraria,
            @RequestParam(required = false) Boolean esOptativa) {
        return ResponseEntity.ok(gradoAsignaturaService.actualizar(id, intensidadHoraria, esOptativa));
    }

    @DeleteMapping("/plan-estudios/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR')")
    public ResponseEntity<Void> eliminarAsignacion(@PathVariable Long id) {
        gradoAsignaturaService.desactivar(id);
        return ResponseEntity.noContent().build();
    }
}
