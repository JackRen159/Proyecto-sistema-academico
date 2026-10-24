package jackren.sistemaacademico.main.controller;

import jackren.sistemaacademico.main.dto.EstudianteAcudienteRequest;
import jackren.sistemaacademico.main.dto.EstudianteAcudienteResponse;
import jackren.sistemaacademico.main.service.EstudianteAcudienteService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/vinculaciones")
public class EstudianteAcudienteController {

    private final EstudianteAcudienteService estudianteAcudienteService;

    public EstudianteAcudienteController(EstudianteAcudienteService estudianteAcudienteService) {
        this.estudianteAcudienteService = estudianteAcudienteService;
    }

    // Asociar acudiente a estudiante mediante documentos
    @PostMapping
    public ResponseEntity<EstudianteAcudienteResponse> asociar(@Valid @RequestBody EstudianteAcudienteRequest request) {
        EstudianteAcudienteResponse response = estudianteAcudienteService.asociar(request);
        URI location = URI.create("/api/vinculaciones/estudiante/" + response.getEstudianteDocumento());
        return ResponseEntity.created(location).body(response);
    }

    // Consultar todos los acudientes de un estudiante por su documento
    @GetMapping("/estudiante/{documentoEstudiante}")
    public ResponseEntity<List<EstudianteAcudienteResponse>> listarAcudientesPorEstudiante(@PathVariable String documentoEstudiante) {
        return ResponseEntity.ok(estudianteAcudienteService.listarAcudientesPorEstudiante(documentoEstudiante));
    }

    // Consultar todos los estudiantes a cargo de un acudiente por su documento
    @GetMapping("/acudiente/{documentoAcudiente}")
    public ResponseEntity<List<EstudianteAcudienteResponse>> listarEstudiantesPorAcudiente(@PathVariable String documentoAcudiente) {
        return ResponseEntity.ok(estudianteAcudienteService.listarEstudiantesPorAcudiente(documentoAcudiente));
    }

    // Cambiar el acudiente principal de un estudiante
    @PatchMapping("/estudiante/{documentoEstudiante}/principal/{documentoNuevoPrincipal}")
    public ResponseEntity<EstudianteAcudienteResponse> cambiarAcudientePrincipal(@PathVariable String documentoEstudiante,
                                                                                @PathVariable String documentoNuevoPrincipal) {
        return ResponseEntity.ok(estudianteAcudienteService.cambiarAcudientePrincipal(documentoEstudiante, documentoNuevoPrincipal));
    }

    // Desvincular un acudiente de un estudiante
    @DeleteMapping("/estudiante/{documentoEstudiante}/acudiente/{documentoAcudiente}")
    public ResponseEntity<Void> desvincular(@PathVariable String documentoEstudiante,
                                           @PathVariable String documentoAcudiente) {
        estudianteAcudienteService.desvincular(documentoEstudiante, documentoAcudiente);
        return ResponseEntity.noContent().build();
    }
}

