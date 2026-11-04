package jackren.sistemaacademico.main.controller;

import jackren.sistemaacademico.main.dto.AcudienteRequest;
import jackren.sistemaacademico.main.dto.AcudienteResponse;
import jackren.sistemaacademico.main.service.AcudienteService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/acudientes")
public class AcudienteController {

    private final AcudienteService acudienteService;

    public AcudienteController(AcudienteService acudienteService) {
        this.acudienteService = acudienteService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'SECRETARIA', 'PROFESOR')")
    public ResponseEntity<Page<AcudienteResponse>> listar(
            @PageableDefault(size = 20, sort = "apellidos") Pageable pageable) {
        return ResponseEntity.ok(acudienteService.listarPaginado(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'SECRETARIA', 'PROFESOR')")
    public ResponseEntity<AcudienteResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(acudienteService.obtenerPorId(id));
    }

    @GetMapping("/documento/{documento}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'SECRETARIA', 'PROFESOR')")
    public ResponseEntity<AcudienteResponse> buscarPorDocumento(@PathVariable String documento) {
        return ResponseEntity.ok(acudienteService.obtenerPorDocumento(documento));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'SECRETARIA')")
    public ResponseEntity<AcudienteResponse> guardar(@Valid @RequestBody AcudienteRequest request) {
        AcudienteResponse response = acudienteService.guardar(request);
        URI location = URI.create("/api/acudientes/" + response.getId());
        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'SECRETARIA')")
    public ResponseEntity<AcudienteResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody AcudienteRequest request) {
        return ResponseEntity.ok(acudienteService.actualizar(id, request));
    }

    @PatchMapping("/{id}/desactivar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'SECRETARIA')")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        acudienteService.desactivar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/restaurar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'SECRETARIA')")
    public ResponseEntity<Void> restaurar(@PathVariable Long id) {
        acudienteService.restaurar(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> eliminarPermanente(
            @PathVariable Long id,
            @RequestParam(defaultValue = "false") boolean confirmar) {
        acudienteService.eliminarPermanente(id, confirmar);
        return ResponseEntity.noContent().build();
    }
}
