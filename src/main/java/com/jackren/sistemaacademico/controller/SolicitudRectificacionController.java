package com.jackren.sistemaacademico.controller;

import com.jackren.sistemaacademico.dto.ResolverSolicitudRectificacionRequest;
import com.jackren.sistemaacademico.dto.SolicitudRectificacionResponse;
import com.jackren.sistemaacademico.entity.EstadoSolicitudRectificacion;
import com.jackren.sistemaacademico.service.SolicitudRectificacionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para atención de solicitudes de rectificación Habeas Data en Secretaría (UC-PROV-04, RN-EST-04).
 */
@RestController
@RequestMapping("/api/solicitudes-rectificacion")
public class SolicitudRectificacionController {

    private final SolicitudRectificacionService rectificacionService;

    public SolicitudRectificacionController(SolicitudRectificacionService rectificacionService) {
        this.rectificacionService = rectificacionService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'SECRETARIA')")
    public ResponseEntity<Page<SolicitudRectificacionResponse>> listarSolicitudes(
            @RequestParam(required = false) EstadoSolicitudRectificacion estado,
            Pageable pageable) {
        return ResponseEntity.ok(rectificacionService.listarSolicitudes(estado, pageable));
    }

    @PostMapping("/{id}/resolver")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'SECRETARIA')")
    public ResponseEntity<SolicitudRectificacionResponse> resolverSolicitud(
            @PathVariable Long id,
            @Valid @RequestBody ResolverSolicitudRectificacionRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(rectificacionService.resolverSolicitud(id, request, jwt.getSubject()));
    }
}
