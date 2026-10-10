package jackren.sistemaacademico.main.controller;

import jackren.sistemaacademico.main.dto.EstudianteRequest;
import jackren.sistemaacademico.main.dto.EstudianteResponse;
import jackren.sistemaacademico.main.service.EstudianteService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<EstudianteResponse> buscarPorId(
            @PathVariable Long id) {

        return estudianteService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<EstudianteResponse> guardar(
            @Valid @RequestBody EstudianteRequest request) {

        return ResponseEntity.ok(
                estudianteService.guardar(request)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {

        estudianteService.eliminar(id);

        return ResponseEntity.noContent().build();
    }
}
