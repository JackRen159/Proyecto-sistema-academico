package com.jackren.sistemaacademico.controller;

import com.jackren.sistemaacademico.dto.*;
import com.jackren.sistemaacademico.entity.DiaSemana;
import com.jackren.sistemaacademico.entity.EstadoAula;
import com.jackren.sistemaacademico.entity.Jornada;
import com.jackren.sistemaacademico.service.HorarioService;
import com.jackren.sistemaacademico.service.NovedadHorarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/horarios")
public class HorarioController {

    private final HorarioService horarioService;
    private final NovedadHorarioService novedadHorarioService;

    public HorarioController(HorarioService horarioService, NovedadHorarioService novedadHorarioService) {
        this.horarioService = horarioService;
        this.novedadHorarioService = novedadHorarioService;
    }

    // =========================================================================
    // 1. CATÁLOGO DE AULAS Y ESPACIOS FÍSICOS (UC-PROV-20)
    // =========================================================================

    @PostMapping("/aulas")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR')")
    public ResponseEntity<AulaResponse> crearAula(@Valid @RequestBody AulaRequest request) {
        AulaResponse response = horarioService.crearAula(request);
        URI location = URI.create("/api/horarios/aulas/" + response.getId());
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/aulas")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'SECRETARIA', 'PROFESOR')")
    public ResponseEntity<List<AulaResponse>> listarAulas(@RequestParam(required = false) String sede) {
        return ResponseEntity.ok(horarioService.listarAulasPorSede(sede));
    }

    @PatchMapping("/aulas/{id}/estado")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR')")
    public ResponseEntity<AulaResponse> actualizarEstadoAula(
            @PathVariable Long id,
            @RequestParam EstadoAula nuevoEstado) {
        return ResponseEntity.ok(horarioService.actualizarEstadoAula(id, nuevoEstado));
    }

    // =========================================================================
    // 2. PLANTILLAS DE FRANJAS HORARIAS (UC-PROV-20)
    // =========================================================================

    @PostMapping("/franjas")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR')")
    public ResponseEntity<PlantillaFranjaHorariaResponse> crearFranja(
            @Valid @RequestBody PlantillaFranjaHorariaRequest request) {
        PlantillaFranjaHorariaResponse response = horarioService.crearFranja(request);
        URI location = URI.create("/api/horarios/franjas/" + response.getId());
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/franjas")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'SECRETARIA', 'PROFESOR')")
    public ResponseEntity<List<PlantillaFranjaHorariaResponse>> listarFranjas(
            @RequestParam Long anioLectivoId,
            @RequestParam Jornada jornada) {
        return ResponseEntity.ok(horarioService.listarFranjasPorJornada(anioLectivoId, jornada));
    }

    // =========================================================================
    // 3. ESTRUCTURACIÓN Y PUBLICACIÓN DE HORARIOS (UC-PROV-20)
    // =========================================================================

    @PostMapping("/versiones")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR')")
    public ResponseEntity<HorarioGrupoVersionResponse> crearHorarioBorrador(
            @RequestParam Long grupoId,
            @AuthenticationPrincipal Jwt jwt) {
        Long usuarioId = extraerUsuarioId(jwt);
        HorarioGrupoVersionResponse response = horarioService.crearHorarioBorrador(grupoId, usuarioId);
        URI location = URI.create("/api/horarios/versiones/" + response.getId());
        return ResponseEntity.created(location).body(response);
    }

    @PostMapping("/bloques")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR')")
    public ResponseEntity<HorarioBloqueResponse> agregarBloqueHorario(
            @RequestParam Long horarioVersionId,
            @Valid @RequestBody HorarioBloqueRequest request) {
        HorarioBloqueResponse response = horarioService.agregarBloqueHorario(horarioVersionId, request);
        URI location = URI.create("/api/horarios/bloques/" + response.getId());
        return ResponseEntity.created(location).body(response);
    }

    @DeleteMapping("/bloques/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR')")
    public ResponseEntity<Void> eliminarBloqueHorario(@PathVariable Long id) {
        horarioService.eliminarBloqueHorario(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/versiones/{id}/validar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR')")
    public ResponseEntity<ValidacionHorarioResponse> validarHorario(@PathVariable Long id) {
        return ResponseEntity.ok(horarioService.validarHorario(id));
    }

    @PostMapping("/versiones/{id}/publicar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR')")
    public ResponseEntity<HorarioGrupoVersionResponse> publicarHorario(
            @PathVariable Long id,
            @RequestParam(required = false) LocalDate fechaInicioVigencia,
            @AuthenticationPrincipal Jwt jwt) {
        Long usuarioId = extraerUsuarioId(jwt);
        return ResponseEntity.ok(horarioService.publicarHorario(id, fechaInicioVigencia, usuarioId));
    }

    // =========================================================================
    // 4. NOVEDADES TEMPORALES Y REASIGNACIONES (UC-PROV-21)
    // =========================================================================

    @PostMapping("/novedades")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR')")
    public ResponseEntity<NovedadHorarioResponse> registrarNovedad(
            @Valid @RequestBody NovedadHorarioRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        Long usuarioId = extraerUsuarioId(jwt);
        return ResponseEntity.ok(novedadHorarioService.registrarNovedad(request, usuarioId));
    }

    @GetMapping("/novedades/bloque/{bloqueId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'SECRETARIA', 'PROFESOR')")
    public ResponseEntity<List<NovedadHorarioResponse>> listarNovedadesPorBloque(@PathVariable Long bloqueId) {
        return ResponseEntity.ok(novedadHorarioService.listarNovedadesPorBloque(bloqueId));
    }

    // =========================================================================
    // 5. CONSULTAS CONTEXTUALES SEGURAS ANTI-IDOR (UC-04, UC-10, UC-PROV-22)
    // =========================================================================

    @GetMapping("/mis-horarios")
    @PreAuthorize("hasRole('ESTUDIANTE')")
    public ResponseEntity<ConsultaHorarioEstudianteResponse> consultarMisHorarios(
            @RequestParam(required = false) LocalDate fecha,
            @AuthenticationPrincipal Jwt jwt) {
        Long usuarioId = extraerUsuarioId(jwt);
        return ResponseEntity.ok(horarioService.consultarHorarioEstudiante(usuarioId, fecha));
    }

    @GetMapping("/acudiente/estudiante/{estudianteId}")
    @PreAuthorize("hasRole('ACUDIENTE')")
    public ResponseEntity<ConsultaHorarioEstudianteResponse> consultarHorarioAcudiente(
            @PathVariable Long estudianteId,
            @RequestParam(required = false) LocalDate fecha,
            @AuthenticationPrincipal Jwt jwt) {
        Long usuarioId = extraerUsuarioId(jwt);
        return ResponseEntity.ok(horarioService.consultarHorarioAcudiente(usuarioId, estudianteId, fecha));
    }

    @GetMapping("/docente/mi-agenda")
    @PreAuthorize("hasRole('PROFESOR')")
    public ResponseEntity<AgendaDocenteResponse> consultarMiAgendaDocente(
            @AuthenticationPrincipal Jwt jwt) {
        Long usuarioId = extraerUsuarioId(jwt);
        return ResponseEntity.ok(horarioService.consultarAgendaDocente(usuarioId));
    }

    @GetMapping("/aulas/disponibilidad")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'SECRETARIA', 'PROFESOR')")
    public ResponseEntity<List<DisponibilidadAulaResponse>> consultarDisponibilidadAulas(
            @RequestParam(required = false) String sede,
            @RequestParam DiaSemana dia,
            @RequestParam Long franjaId) {
        return ResponseEntity.ok(horarioService.consultarDisponibilidadAulas(sede, dia, franjaId));
    }

    @GetMapping("/ubicacion-actual")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'SECRETARIA')")
    public ResponseEntity<UbicacionActualResponse> consultarUbicacionActual(
            @RequestParam String tipoConsulta,
            @RequestParam Long id) {
        return ResponseEntity.ok(horarioService.consultarUbicacionActual(tipoConsulta, id));
    }

    private Long extraerUsuarioId(Jwt jwt) {
        if (jwt != null && jwt.hasClaim("id")) {
            Object idClaim = jwt.getClaim("id");
            if (idClaim instanceof Number number) {
                return number.longValue();
            }
        }
        return null;
    }
}
