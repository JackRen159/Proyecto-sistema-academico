package com.jackren.sistemaacademico.controller;

import com.jackren.sistemaacademico.dto.*;
import com.jackren.sistemaacademico.entity.Rol;
import com.jackren.sistemaacademico.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controlador REST para administración de usuarios, asignación de roles y órganos colegiados (UC-PROV-01, UC-PROV-02).
 */
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'SECRETARIA')")
    public ResponseEntity<Page<UsuarioResponse>> listarUsuarios(Pageable pageable) {
        return ResponseEntity.ok(usuarioService.listarUsuarios(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'SECRETARIA')")
    public ResponseEntity<UsuarioResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.obtenerPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'SECRETARIA')")
    public ResponseEntity<UsuarioResponse> crearUsuario(
            @Valid @RequestBody CrearUsuarioRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        // Regla E3: Secretaría únicamente puede crear cuentas de roles ESTUDIANTE o ACUDIENTE
        String rolEmisor = jwt.getClaimAsString("rol");
        if ("SECRETARIA".equalsIgnoreCase(rolEmisor) &&
                (request.getRol() != Rol.ESTUDIANTE && request.getRol() != Rol.ACUDIENTE)) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "El rol Secretaría solo tiene competencia para aprovisionar cuentas de Estudiantes y Acudientes (E3)"
            );
        }

        UsuarioResponse response = usuarioService.crearUsuario(request, jwt.getSubject());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR')")
    public ResponseEntity<UsuarioResponse> cambiarEstado(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarEstadoUsuarioRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(usuarioService.cambiarEstado(id, request, jwt.getSubject()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR')")
    public ResponseEntity<Void> eliminarUsuario(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {
        usuarioService.eliminarUsuario(id, jwt.getSubject());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/roles")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'SECRETARIA')")
    public ResponseEntity<List<UsuarioRolResponse>> listarRoles(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.listarRolesPorUsuario(id));
    }

    @PostMapping("/{id}/roles")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR')")
    public ResponseEntity<UsuarioRolResponse> asignarRol(
            @PathVariable Long id,
            @Valid @RequestBody AsignarRolUsuarioRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        UsuarioRolResponse response = usuarioService.asignarRol(id, request, jwt.getSubject());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}/roles/{rolId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR')")
    public ResponseEntity<Map<String, String>> revocarRol(
            @PathVariable Long id,
            @PathVariable Long rolId,
            @AuthenticationPrincipal Jwt jwt) {
        usuarioService.revocarRol(rolId, jwt.getSubject());
        return ResponseEntity.ok(Map.of("mensaje", "Rol revocado exitosamente"));
    }

    @GetMapping("/{id}/organos-colegiados")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR')")
    public ResponseEntity<List<OrganoColegiadoMiembroResponse>> listarOrganos(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.listarOrganosPorUsuario(id));
    }

    @PostMapping("/{id}/organos-colegiados")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR')")
    public ResponseEntity<OrganoColegiadoMiembroResponse> asignarMiembroOrgano(
            @PathVariable Long id,
            @Valid @RequestBody AsignarOrganoColegiadoRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        OrganoColegiadoMiembroResponse response = usuarioService.asignarMiembroOrganoColegiado(id, request, jwt.getSubject());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/organos-colegiados/{miembroId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR')")
    public ResponseEntity<Map<String, String>> removerMiembroOrgano(
            @PathVariable Long miembroId,
            @AuthenticationPrincipal Jwt jwt) {
        usuarioService.removerMiembroOrganoColegiado(miembroId, jwt.getSubject());
        return ResponseEntity.ok(Map.of("mensaje", "Membresía en órgano colegiado revocada exitosamente"));
    }
}
