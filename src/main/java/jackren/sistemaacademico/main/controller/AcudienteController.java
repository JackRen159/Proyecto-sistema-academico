package jackren.sistemaacademico.main.controller;

import jackren.sistemaacademico.main.dto.AcudienteRequest;
import jackren.sistemaacademico.main.dto.AcudienteResponse;
import jackren.sistemaacademico.main.service.AcudienteService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/acudientes")
public class AcudienteController {

    private final AcudienteService acudienteService;

    public AcudienteController(AcudienteService acudienteService) {
        this.acudienteService = acudienteService;
    }

    @GetMapping
    public ResponseEntity<List<AcudienteResponse>> listarTodos() {
        return ResponseEntity.ok(
                acudienteService.listarTodos()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<AcudienteResponse> buscarPorId(@PathVariable Long id) {
        return acudienteService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/documento/{documento}")
    public ResponseEntity<AcudienteResponse> buscarPorDocumento(@PathVariable String documento) {
        return acudienteService.buscarPorDocumento(documento)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<AcudienteResponse> guardar(@Valid @RequestBody AcudienteRequest request) {
        AcudienteResponse response = acudienteService.guardar(request);
        URI location = URI.create("/api/acudientes/" + response.getId());
        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AcudienteResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody AcudienteRequest request) {
        return ResponseEntity.ok(acudienteService.actualizar(id, request));
    }

    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        acudienteService.desactivar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/restaurar")
    public ResponseEntity<Void> restaurar(@PathVariable Long id) {
        acudienteService.restaurar(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPermanente(
            @PathVariable Long id,
            @RequestParam(defaultValue = "false") boolean confirmar) {
        acudienteService.eliminarPermanente(id, confirmar);
        return ResponseEntity.noContent().build();
    }
}
