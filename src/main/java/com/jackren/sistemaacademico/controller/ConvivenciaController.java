package com.jackren.sistemaacademico.controller;

import com.jackren.sistemaacademico.dto.*;
import com.jackren.sistemaacademico.service.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/convivencia")
public class ConvivenciaController {

    private final ObservadorService observadorService;
    private final RutaAtencionService rutaAtencionService;
    private final DebidoProcesoService debidoProcesoService;
    private final MedidaSancionService medidaSancionService;
    private final ComiteConvivenciaService comiteConvivenciaService;
    private final ConvivenciaConsultaService convivenciaConsultaService;

    public ConvivenciaController(
            ObservadorService observadorService,
            RutaAtencionService rutaAtencionService,
            DebidoProcesoService debidoProcesoService,
            MedidaSancionService medidaSancionService,
            ComiteConvivenciaService comiteConvivenciaService,
            ConvivenciaConsultaService convivenciaConsultaService) {
        this.observadorService = observadorService;
        this.rutaAtencionService = rutaAtencionService;
        this.debidoProcesoService = debidoProcesoService;
        this.medidaSancionService = medidaSancionService;
        this.comiteConvivenciaService = comiteConvivenciaService;
        this.convivenciaConsultaService = convivenciaConsultaService;
    }

    // =========================================================================
    // 1. OBSERVADOR DEL ESTUDIANTE (UC-22)
    // =========================================================================

    @PostMapping("/observador")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'COORDINADOR_CONVIVENCIA', 'COORDINADOR_ACADEMICO', 'ORIENTADOR_ESCOLAR', 'PROFESOR')")
    public ResponseEntity<AnotacionObservadorResponse> registrarAnotacion(
            @Valid @RequestBody AnotacionObservadorRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        Long usuarioId = extraerUsuarioId(jwt);
        return ResponseEntity.ok(observadorService.registrarAnotacion(request, usuarioId));
    }

    @PostMapping("/observador/{anotacionId}/version-estudiante")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ESTUDIANTE', 'COORDINADOR_CONVIVENCIA')")
    public ResponseEntity<AnotacionObservadorResponse> registrarVersionEstudiante(
            @PathVariable Long anotacionId,
            @Valid @RequestBody VersionEstudianteRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        Long usuarioId = extraerUsuarioId(jwt);
        return ResponseEntity.ok(observadorService.registrarVersionEstudiante(anotacionId, request, usuarioId));
    }

    @PostMapping("/observador/{anotacionId}/enterado-acudiente")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ACUDIENTE')")
    public ResponseEntity<AnotacionObservadorResponse> registrarEnteradoAcudiente(
            @PathVariable Long anotacionId,
            @AuthenticationPrincipal Jwt jwt) {
        Long usuarioId = extraerUsuarioId(jwt);
        return ResponseEntity.ok(observadorService.registrarEnteradoAcudiente(anotacionId, usuarioId));
    }

    @PostMapping("/observador/{anotacionId}/adendas")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'COORDINADOR_CONVIVENCIA', 'ORIENTADOR_ESCOLAR', 'PROFESOR')")
    public ResponseEntity<AdendaObservadorResponse> agregarAdenda(
            @PathVariable Long anotacionId,
            @Valid @RequestBody AdendaObservadorRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        Long usuarioId = extraerUsuarioId(jwt);
        return ResponseEntity.ok(observadorService.agregarAdenda(anotacionId, request, usuarioId));
    }

    @GetMapping("/observador/estudiante/{estudianteId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'COORDINADOR_CONVIVENCIA', 'COORDINADOR_ACADEMICO', 'ORIENTADOR_ESCOLAR', 'PROFESOR', 'ESTUDIANTE', 'ACUDIENTE')")
    public ResponseEntity<List<AnotacionObservadorResponse>> listarAnotacionesEstudiante(@PathVariable Long estudianteId) {
        return ResponseEntity.ok(observadorService.listarPorEstudiante(estudianteId));
    }

    @GetMapping("/observador/{anotacionId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'COORDINADOR_CONVIVENCIA', 'COORDINADOR_ACADEMICO', 'ORIENTADOR_ESCOLAR', 'PROFESOR', 'ESTUDIANTE', 'ACUDIENTE')")
    public ResponseEntity<AnotacionObservadorResponse> obtenerAnotacionPorId(@PathVariable Long anotacionId) {
        return ResponseEntity.ok(observadorService.obtenerPorId(anotacionId));
    }

    @DeleteMapping("/observador/{anotacionId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'COORDINADOR_CONVIVENCIA', 'PROFESOR')")
    public ResponseEntity<Void> bloquearBorradoDirecto(@PathVariable Long anotacionId) {
        observadorService.bloquearEdicionOBorradoDirecto(anotacionId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/observador/{anotacionId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'COORDINADOR_CONVIVENCIA', 'PROFESOR')")
    public ResponseEntity<Void> bloquearModificacionDirecta(@PathVariable Long anotacionId) {
        observadorService.bloquearEdicionOBorradoDirecto(anotacionId);
        return ResponseEntity.ok().build();
    }

    // =========================================================================
    // 2. RUTA DE ATENCIÓN INTEGRAL TIPO I, II Y III (UC-23)
    // =========================================================================

    @PostMapping("/casos")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'COORDINADOR_CONVIVENCIA', 'ORIENTADOR_ESCOLAR', 'PROFESOR')")
    public ResponseEntity<CasoConvivenciaResponse> abrirCaso(
            @Valid @RequestBody CasoConvivenciaRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        Long usuarioId = extraerUsuarioId(jwt);
        return ResponseEntity.ok(rutaAtencionService.abrirCaso(request, usuarioId));
    }

    @PutMapping("/casos/{casoId}/reclasificar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR_CONVIVENCIA')")
    public ResponseEntity<CasoConvivenciaResponse> reclasificarCaso(
            @PathVariable Long casoId,
            @Valid @RequestBody ReclasificarCasoRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        Long usuarioId = extraerUsuarioId(jwt);
        return ResponseEntity.ok(rutaAtencionService.reclasificarCaso(casoId, request, usuarioId));
    }

    @PostMapping("/casos/{casoId}/actuaciones")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR_CONVIVENCIA', 'ORIENTADOR_ESCOLAR')")
    public ResponseEntity<ActuacionRutaAtencionResponse> registrarActuacion(
            @PathVariable Long casoId,
            @Valid @RequestBody ActuacionRutaAtencionRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        Long usuarioId = extraerUsuarioId(jwt);
        return ResponseEntity.ok(rutaAtencionService.registrarActuacion(casoId, request, usuarioId));
    }

    @PostMapping("/casos/{casoId}/cerrar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR_CONVIVENCIA')")
    public ResponseEntity<CasoConvivenciaResponse> cerrarCaso(
            @PathVariable Long casoId,
            @RequestParam(required = false) String motivoCierre,
            @RequestParam(required = false) String codigoSiuce,
            @AuthenticationPrincipal Jwt jwt) {
        Long usuarioId = extraerUsuarioId(jwt);
        return ResponseEntity.ok(rutaAtencionService.cerrarCaso(casoId, motivoCierre, codigoSiuce, usuarioId));
    }

    @GetMapping("/casos/{casoId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR_CONVIVENCIA', 'ORIENTADOR_ESCOLAR')")
    public ResponseEntity<CasoConvivenciaResponse> obtenerCasoPorId(@PathVariable Long casoId) {
        return ResponseEntity.ok(rutaAtencionService.obtenerPorId(casoId));
    }

    @GetMapping("/casos/{casoId}/actuaciones")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR_CONVIVENCIA', 'ORIENTADOR_ESCOLAR')")
    public ResponseEntity<List<ActuacionRutaAtencionResponse>> listarActuacionesCaso(@PathVariable Long casoId) {
        return ResponseEntity.ok(rutaAtencionService.listarActuacionesPorCaso(casoId));
    }

    // =========================================================================
    // 3. DEBIDO PROCESO Y RECURSOS DE IMPUGNACIÓN (UC-PROV-25)
    // =========================================================================

    @PostMapping("/debido-proceso/apertura")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR_CONVIVENCIA')")
    public ResponseEntity<ExpedienteDebidoProcesoResponse> abrirExpediente(
            @Valid @RequestBody AperturaCargosRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        Long usuarioId = extraerUsuarioId(jwt);
        return ResponseEntity.ok(debidoProcesoService.abrirExpedienteYNotificarCargos(request, usuarioId));
    }

    @PostMapping("/debido-proceso/{expedienteId}/descargos")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ESTUDIANTE', 'ACUDIENTE', 'COORDINADOR_CONVIVENCIA')")
    public ResponseEntity<DescargoDebidoProcesoResponse> radicarDescargos(
            @PathVariable Long expedienteId,
            @Valid @RequestBody RadicarDescargosRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        Long usuarioId = extraerUsuarioId(jwt);
        return ResponseEntity.ok(debidoProcesoService.radicarDescargos(expedienteId, request, usuarioId));
    }

    @PostMapping("/debido-proceso/{expedienteId}/certificar-vencimiento")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR_CONVIVENCIA')")
    public ResponseEntity<ExpedienteDebidoProcesoResponse> certificarVencimientoDescargos(
            @PathVariable Long expedienteId,
            @RequestParam(required = false) String constancia,
            @AuthenticationPrincipal Jwt jwt) {
        Long usuarioId = extraerUsuarioId(jwt);
        return ResponseEntity.ok(debidoProcesoService.certificarVencimientoDescargos(expedienteId, constancia, usuarioId));
    }

    @PostMapping("/medidas/{medidaId}/recursos")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ESTUDIANTE', 'ACUDIENTE')")
    public ResponseEntity<RecursoImpugnacionResponse> radicarRecurso(
            @PathVariable Long medidaId,
            @Valid @RequestBody RadicarRecursoRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        Long usuarioId = extraerUsuarioId(jwt);
        return ResponseEntity.ok(debidoProcesoService.radicarRecurso(medidaId, request, usuarioId));
    }

    @PostMapping("/recursos/{recursoId}/resolver")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR')")
    public ResponseEntity<RecursoImpugnacionResponse> resolverRecurso(
            @PathVariable Long recursoId,
            @Valid @RequestBody ResolverRecursoRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        Long usuarioId = extraerUsuarioId(jwt);
        return ResponseEntity.ok(debidoProcesoService.resolverRecurso(recursoId, request, usuarioId));
    }

    @GetMapping("/debido-proceso/{expedienteId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR_CONVIVENCIA', 'ORIENTADOR_ESCOLAR', 'ESTUDIANTE', 'ACUDIENTE')")
    public ResponseEntity<ExpedienteDebidoProcesoResponse> obtenerExpedientePorId(@PathVariable Long expedienteId) {
        return ResponseEntity.ok(debidoProcesoService.obtenerPorId(expedienteId));
    }

    // =========================================================================
    // 4. MEDIDAS Y SANCIONES CON PLAN DE CONTINUIDAD ACADÉMICA (UC-24)
    // =========================================================================

    @PostMapping("/medidas")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR_CONVIVENCIA')")
    public ResponseEntity<MedidaSancionResponse> decidirMedidaOSancion(
            @Valid @RequestBody MedidaSancionRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        Long usuarioId = extraerUsuarioId(jwt);
        return ResponseEntity.ok(medidaSancionService.decidirMedidaOSancion(request, usuarioId));
    }

    @GetMapping("/medidas/{medidaId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR_CONVIVENCIA', 'ORIENTADOR_ESCOLAR', 'ESTUDIANTE', 'ACUDIENTE')")
    public ResponseEntity<MedidaSancionResponse> obtenerMedidaPorId(@PathVariable Long medidaId) {
        return ResponseEntity.ok(medidaSancionService.obtenerPorId(medidaId));
    }

    // =========================================================================
    // 5. ACTAS DEL COMITÉ ESCOLAR DE CONVIVENCIA (UC-PROV-26)
    // =========================================================================

    @PostMapping("/comite/actas")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR_CONVIVENCIA')")
    public ResponseEntity<ActaComiteConvivenciaResponse> registrarActaComite(
            @Valid @RequestBody ActaComiteConvivenciaRequest request) {
        return ResponseEntity.ok(comiteConvivenciaService.registrarActa(request));
    }

    @GetMapping("/comite/actas/{actaId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR_CONVIVENCIA', 'ORIENTADOR_ESCOLAR')")
    public ResponseEntity<ActaComiteConvivenciaResponse> obtenerActaComitePorId(@PathVariable Long actaId) {
        return ResponseEntity.ok(comiteConvivenciaService.obtenerPorId(actaId));
    }

    @GetMapping("/comite/actas/anio/{anioLectivoId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR_CONVIVENCIA', 'ORIENTADOR_ESCOLAR')")
    public ResponseEntity<List<ActaComiteConvivenciaResponse>> listarActasComiteAnio(@PathVariable Long anioLectivoId) {
        return ResponseEntity.ok(comiteConvivenciaService.listarPorAnioLectivo(anioLectivoId));
    }

    // =========================================================================
    // 6. CONSULTA DE SEGUIMIENTO PROYECTADA Y SEGURA (UC-25)
    // =========================================================================

    @GetMapping("/seguimiento/estudiante/{estudianteId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'COORDINADOR_CONVIVENCIA', 'COORDINADOR_ACADEMICO', 'ORIENTADOR_ESCOLAR', 'PROFESOR', 'ESTUDIANTE', 'ACUDIENTE')")
    public ResponseEntity<SeguimientoConvivenciaEstudianteResponse> consultarSeguimientoEstudiante(
            @PathVariable Long estudianteId,
            @AuthenticationPrincipal Jwt jwt) {
        Long usuarioId = extraerUsuarioId(jwt);
        return ResponseEntity.ok(convivenciaConsultaService.consultarSeguimientoEstudiante(estudianteId, usuarioId));
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
