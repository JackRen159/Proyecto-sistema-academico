package com.jackren.sistemaacademico.controller;

import com.jackren.sistemaacademico.dto.*;
import com.jackren.sistemaacademico.service.ConfiguracionInstitucionalService;
import com.jackren.sistemaacademico.service.PoliticaDatosService;
import com.jackren.sistemaacademico.service.SieeConfiguracionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controlador REST para administración y consulta de configuración institucional, SIEE, Manual y Política de Datos (UC-26, UC-PROV-13, UC-PROV-14).
 */
@RestController
@RequestMapping("/api/configuracion")
public class ConfiguracionInstitucionalController {

    private final ConfiguracionInstitucionalService configuracionService;
    private final PoliticaDatosService politicaService;
    private final SieeConfiguracionService sieeService;

    public ConfiguracionInstitucionalController(ConfiguracionInstitucionalService configuracionService,
                                                PoliticaDatosService politicaService,
                                                SieeConfiguracionService sieeService) {
        this.configuracionService = configuracionService;
        this.politicaService = politicaService;
        this.sieeService = sieeService;
    }

    // ── IDENTIFICACIÓN INSTITUCIONAL Y FIRMAS (UC-PROV-13, UC-PROV-14) ──────────

    @GetMapping("/institucion")
    public ResponseEntity<InstitucionDatosLegalesResponse> obtenerDatosLegales() {
        return ResponseEntity.ok(configuracionService.obtenerDatosLegales());
    }

    @PutMapping("/institucion")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR')")
    public ResponseEntity<InstitucionDatosLegalesResponse> actualizarDatosLegales(
            @Valid @RequestBody ActualizarInstitucionRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(configuracionService.actualizarDatosLegales(request, jwt.getSubject()));
    }

    // ── PARÁMETROS INSTITUCIONALES (UC-PROV-13) ─────────────────────────────────

    @GetMapping("/parametros")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR')")
    public ResponseEntity<List<ParametroInstitucionalResponse>> listarParametros() {
        return ResponseEntity.ok(configuracionService.listarParametros());
    }

    @GetMapping("/parametros/{clave}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR')")
    public ResponseEntity<ParametroInstitucionalResponse> obtenerParametro(@PathVariable String clave) {
        return ResponseEntity.ok(configuracionService.obtenerParametroPorClave(clave));
    }

    @PutMapping("/parametros/{clave}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR')")
    public ResponseEntity<ParametroInstitucionalResponse> actualizarParametro(
            @PathVariable String clave,
            @Valid @RequestBody ActualizarParametroRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(configuracionService.actualizarParametro(clave, request, jwt.getSubject()));
    }

    // ── POLÍTICA DE TRATAMIENTO DE DATOS PERSONALES (UC-PROV-13 A1, UC-PROV-14 A2) ─

    @GetMapping("/politica-datos/vigente")
    public ResponseEntity<PoliticaDatosResponse> obtenerPoliticaVigente() {
        return ResponseEntity.ok(politicaService.obtenerPoliticaVigente());
    }

    @GetMapping("/politica-datos")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR')")
    public ResponseEntity<List<PoliticaDatosResponse>> listarVersionesPolitica() {
        return ResponseEntity.ok(politicaService.listarVersiones());
    }

    @PostMapping("/politica-datos")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR')")
    public ResponseEntity<PoliticaDatosResponse> crearNuevaVersionPolitica(
            @Valid @RequestBody CrearPoliticaDatosRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        PoliticaDatosResponse response = politicaService.crearNuevaVersion(request, jwt.getSubject());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/politica-datos/consentimiento")
    public ResponseEntity<Map<String, String>> registrarConsentimiento(
            @Valid @RequestBody RegistrarConsentimientoRequest request,
            @AuthenticationPrincipal Jwt jwt,
            HttpServletRequest httpRequest) {
        Long usuarioId = jwt.getClaim("id");
        String ip = httpRequest != null ? httpRequest.getRemoteAddr() : "127.0.0.1";
        String userAgent = httpRequest != null ? httpRequest.getHeader("User-Agent") : "Cliente Web";

        politicaService.registrarConsentimiento(usuarioId, request, ip, userAgent);
        return ResponseEntity.ok(Map.of("mensaje", "Consentimiento registrado exitosamente"));
    }

    // ── SIEE INSTITUCIONAL (UC-26, UC-PROV-14) ──────────────────────────────────

    @GetMapping("/siee/vigente")
    public ResponseEntity<ConfiguracionSieeResponse> obtenerSieeVigente() {
        return ResponseEntity.ok(sieeService.obtenerSieeVigente());
    }

    @GetMapping("/siee")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'COORDINADOR_ACADEMICO')")
    public ResponseEntity<List<ConfiguracionSieeResponse>> listarVersionesSiee() {
        return ResponseEntity.ok(sieeService.listarVersionesSiee());
    }

    @PostMapping("/siee/borrador")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'COORDINADOR_ACADEMICO')")
    public ResponseEntity<ConfiguracionSieeResponse> crearBorradorSiee(
            @Valid @RequestBody CrearSieeBorradorRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        ConfiguracionSieeResponse response = sieeService.crearBorradorSiee(request, jwt.getSubject());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/siee/{id}/aprobar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR')")
    public ResponseEntity<ConfiguracionSieeResponse> aprobarSiee(
            @PathVariable Long id,
            @Valid @RequestBody AprobarSieeRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(sieeService.aprobarYPublicarSiee(id, request, jwt.getSubject()));
    }

    // ── MANUAL DE CONVIVENCIA ESCOLAR (UC-26, UC-PROV-14) ───────────────────────

    @GetMapping("/manual-convivencia/vigente")
    public ResponseEntity<ConfiguracionManualConvivenciaResponse> obtenerManualVigente() {
        return ResponseEntity.ok(sieeService.obtenerManualVigente());
    }

    @PostMapping("/manual-convivencia/{id}/aprobar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR')")
    public ResponseEntity<ConfiguracionManualConvivenciaResponse> aprobarManual(
            @PathVariable Long id,
            @Valid @RequestBody AprobarManualConvivenciaRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(sieeService.aprobarManualConvivencia(id, request, jwt.getSubject()));
    }
}
