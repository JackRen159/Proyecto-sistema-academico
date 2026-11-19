package com.jackren.sistemaacademico.controller;

import com.jackren.sistemaacademico.dto.*;
import com.jackren.sistemaacademico.service.EstudianteAcudienteService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vinculaciones")
public class EstudianteAcudienteController {

    private final EstudianteAcudienteService estudianteAcudienteService;

    public EstudianteAcudienteController(EstudianteAcudienteService estudianteAcudienteService) {
        this.estudianteAcudienteService = estudianteAcudienteService;
    }

    // Asociar acudiente a estudiante mediante documentos
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'SECRETARIA')")
    public ResponseEntity<EstudianteAcudienteResponse> asociar(@Valid @RequestBody EstudianteAcudienteRequest request) {
        EstudianteAcudienteResponse response = estudianteAcudienteService.asociar(request);
        URI location = URI.create("/api/vinculaciones/estudiante/" + response.getEstudianteDocumento());
        return ResponseEntity.created(location).body(response);
    }

    // Consultar todos los acudientes de un estudiante por su documento
    @GetMapping("/estudiante/{documentoEstudiante}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'SECRETARIA', 'PROFESOR')")
    public ResponseEntity<List<EstudianteAcudienteResponse>> listarAcudientesPorEstudiante(@PathVariable String documentoEstudiante) {
        return ResponseEntity.ok(estudianteAcudienteService.listarAcudientesPorEstudiante(documentoEstudiante));
    }

    // Consultar todos los estudiantes a cargo de un acudiente por su documento
    @GetMapping("/acudiente/{documentoAcudiente}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'SECRETARIA', 'PROFESOR')")
    public ResponseEntity<List<EstudianteAcudienteResponse>> listarEstudiantesPorAcudiente(@PathVariable String documentoAcudiente) {
        return ResponseEntity.ok(estudianteAcudienteService.listarEstudiantesPorAcudiente(documentoAcudiente));
    }

    // Cambiar el acudiente principal de un estudiante
    @PatchMapping("/estudiante/{documentoEstudiante}/principal/{documentoNuevoPrincipal}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'SECRETARIA')")
    public ResponseEntity<EstudianteAcudienteResponse> cambiarAcudientePrincipal(@PathVariable String documentoEstudiante,
                                                                                @PathVariable String documentoNuevoPrincipal) {
        return ResponseEntity.ok(estudianteAcudienteService.cambiarAcudientePrincipal(documentoEstudiante, documentoNuevoPrincipal));
    }

    // Consultar estudiantes asociados al acudiente autenticado desde el JWT (UC-06)
    @GetMapping("/mis-estudiantes")
    @PreAuthorize("hasRole('ACUDIENTE')")
    public ResponseEntity<List<EstudianteAsociadoAcudienteResponse>> misEstudiantes(
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(estudianteAcudienteService.listarMisEstudiantesAsociados(jwt.getSubject()));
    }

    // Registrar medida de protección o restricción legal sobre un vínculo (UC-PROV-05 A3, RN-SEG-06)
    @PostMapping("/{id}/restriccion-legal")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'COORDINADOR_CONVIVENCIA')")
    public ResponseEntity<Map<String, String>> registrarRestriccionLegal(
            @PathVariable Long id,
            @Valid @RequestBody RegistroRestriccionLegalRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        estudianteAcudienteService.registrarRestriccionLegal(id, request, jwt.getSubject());
        return ResponseEntity.ok(Map.of("mensaje", "Medida cautelar registrada exitosamente. Vínculo bloqueado y accesos del adulto revocados inmediatamente en servidor (RN-SEG-06)."));
    }

    // Desvincular un acudiente de un estudiante sin borrado físico (RN-EST-05, RN-EST-07, RN-DOC-07)
    @DeleteMapping("/estudiante/{documentoEstudiante}/acudiente/{documentoAcudiente}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'SECRETARIA')")
    public ResponseEntity<Void> desvincular(@PathVariable String documentoEstudiante,
                                           @PathVariable String documentoAcudiente) {
        estudianteAcudienteService.desvincular(documentoEstudiante, documentoAcudiente);
        return ResponseEntity.noContent().build();
    }
}
