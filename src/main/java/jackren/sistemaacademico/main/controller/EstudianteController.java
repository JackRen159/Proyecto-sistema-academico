package jackren.sistemaacademico.main.controller;

import jackren.sistemaacademico.main.dto.EstudianteRequest;
import jackren.sistemaacademico.main.dto.EstudianteResponse;
import jackren.sistemaacademico.main.service.EstudianteService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/estudiantes")
public class EstudianteController {

    private final EstudianteService estudianteService;

    public EstudianteController(EstudianteService estudianteService) {
        this.estudianteService = estudianteService;
    }

    @GetMapping
    public ResponseEntity<List<EstudianteResponse>> listarTodos() {
        return ResponseEntity.ok(
                estudianteService.listarTodos()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<EstudianteResponse> buscarPorId(@PathVariable Long id) {
        return estudianteService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/documento/{documento}")
    public ResponseEntity<EstudianteResponse> buscarPorDocumento(@PathVariable String documento) {
        return estudianteService.buscarPorDocumento(documento)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<EstudianteResponse> guardar(@Valid @RequestBody EstudianteRequest request) {
        EstudianteResponse response = estudianteService.guardar(request);
        URI location = URI.create("/api/estudiantes/" + response.getId());
        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EstudianteResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody EstudianteRequest request) {
        return ResponseEntity.ok(estudianteService.actualizar(id, request));
    }

    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        estudianteService.desactivar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/restaurar")
    public ResponseEntity<Void> restaurar(@PathVariable Long id) {
        estudianteService.restaurar(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPermanente(
            @PathVariable Long id,
            @RequestParam(defaultValue = "false") boolean confirmar) {
        estudianteService.eliminarPermanente(id, confirmar);
        return ResponseEntity.noContent().build();
    }
}
