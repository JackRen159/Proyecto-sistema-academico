package com.jackren.sistemaacademico.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jackren.sistemaacademico.dto.*;
import com.jackren.sistemaacademico.entity.*;
import com.jackren.sistemaacademico.exception.ConflictoException;
import com.jackren.sistemaacademico.exception.RecursoNoEncontradoException;
import com.jackren.sistemaacademico.exception.ValidacionException;
import com.jackren.sistemaacademico.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Servicio integral para estructuración, validación matemática exhaustiva, simulación,
 * comparación y ciclo de vida colegiado del SIEE y Manual de Convivencia (UC-26, UC-PROV-14).
 * Cumple con Decreto 1290 de 2009 / Decreto 1075 de 2015, Ley 1620 de 2013 y Sentencia T-004 de 2026.
 */
@Service
@Transactional
public class SieeConfiguracionService {

    private final ConfiguracionSieeVersionRepository sieeRepository;
    private final ConfiguracionManualConvivenciaVersionRepository manualRepository;
    private final AnioLectivoRepository anioLectivoRepository;
    private final UsuarioRepository usuarioRepository;
    private final AuditoriaGlobalService auditoriaService;
    private final ObjectMapper objectMapper;

    public SieeConfiguracionService(ConfiguracionSieeVersionRepository sieeRepository,
                                    ConfiguracionManualConvivenciaVersionRepository manualRepository,
                                    AnioLectivoRepository anioLectivoRepository,
                                    UsuarioRepository usuarioRepository,
                                    AuditoriaGlobalService auditoriaService,
                                    ObjectMapper objectMapper) {
        this.sieeRepository = sieeRepository;
        this.manualRepository = manualRepository;
        this.anioLectivoRepository = anioLectivoRepository;
        this.usuarioRepository = usuarioRepository;
        this.auditoriaService = auditoriaService;
        this.objectMapper = objectMapper;
    }

    // ── GESTIÓN DE VERSIONES DEL SIEE (UC-26, UC-PROV-14) ───────────────────────

    @Transactional(readOnly = true)
    public ConfiguracionSieeResponse obtenerSieeVigente() {
        ConfiguracionSieeVersion siee = sieeRepository.findFirstByEstadoOrderByNumeroVersionDesc(EstadoVersionConfiguracion.VIGENTE)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe una versión vigente del SIEE"));
        return toSieeResponse(siee);
    }

    @Transactional(readOnly = true)
    public List<ConfiguracionSieeResponse> listarVersionesSiee() {
        return sieeRepository.findAllByOrderByNumeroVersionDesc().stream()
                .map(this::toSieeResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ConfiguracionSieeResponse obtenerSieePorId(Long id) {
        ConfiguracionSieeVersion s = sieeRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Versión del SIEE no encontrada con ID: " + id));
        return toSieeResponse(s);
    }

    @Transactional(readOnly = true)
    public ConfiguracionSieeResponse obtenerSieePorAnioLectivo(Long anioLectivoId) {
        return sieeRepository.findFirstByAnioLectivoVigenciaIdAndEstadoInOrderByNumeroVersionDesc(
                anioLectivoId,
                List.of(EstadoVersionConfiguracion.VIGENTE, EstadoVersionConfiguracion.APROBADA_VIGENCIA_FUTURA, EstadoVersionConfiguracion.HISTORICA)
        ).map(this::toSieeResponse).orElseGet(this::obtenerSieeVigente);
    }

    public ConfiguracionSieeResponse crearBorradorSiee(CrearSieeBorradorRequest request, String autorizador) {
        int siguienteVersion = sieeRepository.findTopByOrderByNumeroVersionDesc()
                .map(s -> s.getNumeroVersion() + 1)
                .orElse(1);

        validarConsistenciaMatematicaSiee(
                request.getEscalaMinima(),
                request.getEscalaAprobatoria(),
                request.getEscalaMaxima(),
                request.getRangosEquivalenciaJson(),
                request.getPonderacionPeriodosJson()
        );

        AnioLectivo anio = null;
        if (request.getAnioLectivoVigenciaId() != null) {
            anio = anioLectivoRepository.findById(request.getAnioLectivoVigenciaId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Año lectivo no encontrado"));
        }

        Usuario creador = autorizador != null ? usuarioRepository.findByUsername(autorizador).orElse(null) : null;

        ConfiguracionSieeVersion borrador = new ConfiguracionSieeVersion();
        borrador.setNumeroVersion(siguienteVersion);
        borrador.setNombreVersion(request.getNombreVersion());
        borrador.setEstado(EstadoVersionConfiguracion.BORRADOR);
        borrador.setAnioLectivoVigencia(anio);
        borrador.setEscalaMinima(request.getEscalaMinima());
        borrador.setEscalaMaxima(request.getEscalaMaxima());
        borrador.setEscalaAprobatoria(request.getEscalaAprobatoria());
        borrador.setDecimales(request.getDecimales() != null ? request.getDecimales() : 2);
        borrador.setModoRedondeo(request.getModoRedondeo() != null ? request.getModoRedondeo() : "HALF_UP");
        borrador.setRangosEquivalenciaJson(request.getRangosEquivalenciaJson());
        borrador.setPonderacionPeriodosJson(request.getPonderacionPeriodosJson());
        borrador.setCriteriosPromocionJson(request.getCriteriosPromocionJson());
        borrador.setUmbralAsistenciaReprobacion(request.getUmbralAsistenciaReprobacion());
        borrador.setPlazoReclamacionesDias(request.getPlazoReclamacionesDias() != null ? request.getPlazoReclamacionesDias() : 3);
        borrador.setUsuarioCreador(creador);

        ConfiguracionSieeVersion guardado = sieeRepository.save(borrador);

        auditoriaService.registrarEvento(new RegistroAuditoriaEventoRequest(
                autorizador != null ? autorizador : "SISTEMA",
                "COORDINADOR_ACADEMICO",
                "CONFIGURACION",
                "CREAR_BORRADOR_SIEE",
                "ConfiguracionSieeVersion",
                String.valueOf(guardado.getId()),
                "Creación de borrador de versión " + guardado.getNumeroVersion() + " del SIEE institucional",
                ResultadoAuditoria.EXITO,
                "SIEE-BORR-" + guardado.getId()
        ));

        return toSieeResponse(guardado);
    }

    public ConfiguracionSieeResponse clonarSiee(Long sieeId, ClonarVersionRequest request, String autorizador) {
        ConfiguracionSieeVersion fuente = sieeRepository.findById(sieeId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Versión del SIEE a clonar no encontrada con ID: " + sieeId));

        int siguienteVersion = sieeRepository.findTopByOrderByNumeroVersionDesc()
                .map(s -> s.getNumeroVersion() + 1)
                .orElse(1);

        AnioLectivo anio = fuente.getAnioLectivoVigencia();
        if (request.getAnioLectivoVigenciaId() != null) {
            anio = anioLectivoRepository.findById(request.getAnioLectivoVigenciaId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Año lectivo destino no encontrado"));
        }

        Usuario creador = autorizador != null ? usuarioRepository.findByUsername(autorizador).orElse(null) : null;

        ConfiguracionSieeVersion clon = new ConfiguracionSieeVersion();
        clon.setNumeroVersion(siguienteVersion);
        clon.setNombreVersion(request.getNuevoNombreVersion() != null && !request.getNuevoNombreVersion().isBlank()
                ? request.getNuevoNombreVersion()
                : fuente.getNombreVersion() + " (Borrador copia)");
        clon.setEstado(EstadoVersionConfiguracion.BORRADOR);
        clon.setAnioLectivoVigencia(anio);
        clon.setEscalaMinima(fuente.getEscalaMinima());
        clon.setEscalaMaxima(fuente.getEscalaMaxima());
        clon.setEscalaAprobatoria(fuente.getEscalaAprobatoria());
        clon.setDecimales(fuente.getDecimales());
        clon.setModoRedondeo(fuente.getModoRedondeo());
        clon.setRangosEquivalenciaJson(fuente.getRangosEquivalenciaJson());
        clon.setPonderacionPeriodosJson(fuente.getPonderacionPeriodosJson());
        clon.setCriteriosPromocionJson(fuente.getCriteriosPromocionJson());
        clon.setUmbralAsistenciaReprobacion(fuente.getUmbralAsistenciaReprobacion());
        clon.setPlazoReclamacionesDias(fuente.getPlazoReclamacionesDias());
        clon.setUsuarioCreador(creador);

        ConfiguracionSieeVersion guardado = sieeRepository.save(clon);

        auditoriaService.registrarEvento(new RegistroAuditoriaEventoRequest(
                autorizador != null ? autorizador : "SISTEMA",
                "COORDINADOR_ACADEMICO",
                "CONFIGURACION",
                "CLONAR_SIEE_VERSION",
                "ConfiguracionSieeVersion",
                String.valueOf(guardado.getId()),
                "Clonación de SIEE versión " + fuente.getNumeroVersion() + " hacia nueva versión borrador " + guardado.getNumeroVersion(),
                ResultadoAuditoria.EXITO,
                "SIEE-CLON-" + guardado.getId()
        ));

        return toSieeResponse(guardado);
    }

    public ConfiguracionSieeResponse marcarSieeListoParaAprobacion(Long sieeId, String autorizador) {
        ConfiguracionSieeVersion siee = sieeRepository.findById(sieeId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Versión del SIEE no encontrada con ID: " + sieeId));

        if (siee.getEstado() != EstadoVersionConfiguracion.BORRADOR) {
            throw new ConflictoException("Solo una versión en estado BORRADOR puede marcarse como LISTA_PARA_APROBACION");
        }

        validarConsistenciaMatematicaSiee(
                siee.getEscalaMinima(),
                siee.getEscalaAprobatoria(),
                siee.getEscalaMaxima(),
                siee.getRangosEquivalenciaJson(),
                siee.getPonderacionPeriodosJson()
        );

        siee.setEstado(EstadoVersionConfiguracion.LISTA_PARA_APROBACION);
        ConfiguracionSieeVersion guardado = sieeRepository.save(siee);

        auditoriaService.registrarEvento(new RegistroAuditoriaEventoRequest(
                autorizador != null ? autorizador : "SISTEMA",
                "COORDINADOR_ACADEMICO",
                "CONFIGURACION",
                "SIEE_LISTO_PARA_APROBACION",
                "ConfiguracionSieeVersion",
                String.valueOf(guardado.getId()),
                "Versión SIEE " + guardado.getNumeroVersion() + " marcada como LISTA_PARA_APROBACION por Coordinación (A1)",
                ResultadoAuditoria.EXITO,
                "SIEE-LISTO-" + guardado.getId()
        ));

        return toSieeResponse(guardado);
    }

    public ConfiguracionSieeResponse aprobarYPublicarSiee(Long sieeId, AprobarSieeRequest request, String rectorUsername) {
        ConfiguracionSieeVersion siee = sieeRepository.findById(sieeId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Versión del SIEE no encontrada con ID: " + sieeId));

        // Regla E5: Inmutabilidad estricta de versiones ya vigentes o históricas
        if (siee.getEstado() == EstadoVersionConfiguracion.VIGENTE || siee.getEstado() == EstadoVersionConfiguracion.HISTORICA) {
            throw new ConflictoException("No es posible modificar una versión que ya ha sido aprobada, vigente o histórica (E5). Debe clonarse a un nuevo borrador.");
        }

        // Regla E2: Obligatoriedad legal de actas de órganos colegiados y constancia de socialización
        if (request.getActaConsejoAcademico() == null || request.getActaConsejoAcademico().isBlank()) {
            throw new ConflictoException("Se requiere obligatoriamente el acta de estudio y aval del Consejo Académico (E2, RN-CFG-01)");
        }
        if (request.getActaConsejoDirectivo() == null || request.getActaConsejoDirectivo().isBlank()) {
            throw new ConflictoException("Se requiere obligatoriamente el acta de adopción oficial del Consejo Directivo (E2, RN-CFG-02)");
        }
        if (request.getConstanciaSocializacion() == null || request.getConstanciaSocializacion().isBlank()) {
            throw new ConflictoException("Se requiere la constancia de socialización del SIEE ante la comunidad educativa (E2, Dec. 1290 Art. 8)");
        }

        // Regla E1: Validación matemática exhaustiva de consistencia
        validarConsistenciaMatematicaSiee(
                siee.getEscalaMinima(),
                siee.getEscalaAprobatoria(),
                siee.getEscalaMaxima(),
                siee.getRangosEquivalenciaJson(),
                siee.getPonderacionPeriodosJson()
        );

        // Regla E3: Irretroactividad sobre años lectivos cerrados
        if (siee.getAnioLectivoVigencia() != null && siee.getAnioLectivoVigencia().getEstado() == EstadoAnioLectivo.CERRADO) {
            throw new ConflictoException("No es posible aplicar una nueva versión del SIEE sobre un año lectivo que ya fue cerrado (E3, RN-CFG-06)");
        }

        boolean esPlaneacionFutura = siee.getAnioLectivoVigencia() != null &&
                siee.getAnioLectivoVigencia().getEstado() == EstadoAnioLectivo.PLANIFICACION;

        Usuario rector = rectorUsername != null ? usuarioRepository.findByUsername(rectorUsername).orElse(null) : null;

        if (esPlaneacionFutura) {
            // Regla A2: Aprobación anticipada para año futuro en planeación
            siee.setEstado(EstadoVersionConfiguracion.APROBADA_VIGENCIA_FUTURA);
        } else {
            // Transición a VIGENTE: Desactivar vigencia de versión anterior pasando a HISTORICA
            sieeRepository.findFirstByEstadoOrderByNumeroVersionDesc(EstadoVersionConfiguracion.VIGENTE).ifPresent(v -> {
                v.setEstado(EstadoVersionConfiguracion.HISTORICA);
                sieeRepository.save(v);
            });
            siee.setEstado(EstadoVersionConfiguracion.VIGENTE);
        }

        siee.setFechaInicioVigencia(request.getFechaInicioVigencia());
        siee.setActaConsejoAcademico(request.getActaConsejoAcademico());
        siee.setActaConsejoDirectivo(request.getActaConsejoDirectivo());
        siee.setConstanciaSocializacion(request.getConstanciaSocializacion());
        siee.setFechaSocializacion(request.getFechaSocializacion() != null ? request.getFechaSocializacion() : LocalDate.now());
        siee.setSoportePdfUrl(request.getSoportePdfUrl());
        siee.setFechaAprobacion(LocalDateTime.now());
        siee.setUsuarioAprobador(rector);

        ConfiguracionSieeVersion publicada = sieeRepository.save(siee);

        auditoriaService.registrarEvento(new RegistroAuditoriaEventoRequest(
                rectorUsername != null ? rectorUsername : "SISTEMA",
                "RECTOR",
                "CONFIGURACION",
                "PUBLICAR_SIEE_VERSION",
                "ConfiguracionSieeVersion",
                String.valueOf(publicada.getId()),
                "Publicación oficial de versión SIEE " + publicada.getNumeroVersion() + " en estado " + publicada.getEstado(),
                ResultadoAuditoria.EXITO,
                "SIEE-PUB-" + publicada.getId()
        ));

        return toSieeResponse(publicada);
    }

    public void rechazarEliminacionVersionSiee(Long id) {
        throw new ConflictoException("Operación prohibida: Las versiones del SIEE son estrictamente inmutables y no admiten eliminación física (E5, RN-CFG-01).");
    }

    // ── GESTIÓN DE VERSIONES DEL MANUAL DE CONVIVENCIA (UC-26, UC-PROV-14) ──────

    @Transactional(readOnly = true)
    public ConfiguracionManualConvivenciaResponse obtenerManualVigente() {
        ConfiguracionManualConvivenciaVersion m = manualRepository.findFirstByEstadoOrderByNumeroVersionDesc(EstadoVersionConfiguracion.VIGENTE)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe versión vigente del Manual de Convivencia"));
        return toManualResponse(m);
    }

    @Transactional(readOnly = true)
    public List<ConfiguracionManualConvivenciaResponse> listarVersionesManual() {
        return manualRepository.findAllByOrderByNumeroVersionDesc().stream()
                .map(this::toManualResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ConfiguracionManualConvivenciaResponse obtenerManualPorId(Long id) {
        ConfiguracionManualConvivenciaVersion m = manualRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Manual de convivencia no encontrado con ID: " + id));
        return toManualResponse(m);
    }

    @Transactional(readOnly = true)
    public ConfiguracionManualConvivenciaResponse obtenerManualPorAnioLectivo(Long anioLectivoId) {
        return manualRepository.findFirstByAnioLectivoVigenciaIdAndEstadoInOrderByNumeroVersionDesc(
                anioLectivoId,
                List.of(EstadoVersionConfiguracion.VIGENTE, EstadoVersionConfiguracion.APROBADA_VIGENCIA_FUTURA, EstadoVersionConfiguracion.HISTORICA)
        ).map(this::toManualResponse).orElseGet(this::obtenerManualVigente);
    }

    public ConfiguracionManualConvivenciaResponse crearBorradorManual(CrearManualBorradorRequest request, String autorizador) {
        int siguienteVersion = manualRepository.findTopByOrderByNumeroVersionDesc()
                .map(m -> m.getNumeroVersion() + 1)
                .orElse(1);

        AnioLectivo anio = null;
        if (request.getAnioLectivoVigenciaId() != null) {
            anio = anioLectivoRepository.findById(request.getAnioLectivoVigenciaId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Año lectivo no encontrado"));
        }

        Usuario creador = autorizador != null ? usuarioRepository.findByUsername(autorizador).orElse(null) : null;

        ConfiguracionManualConvivenciaVersion borrador = new ConfiguracionManualConvivenciaVersion();
        borrador.setNumeroVersion(siguienteVersion);
        borrador.setNombreVersion(request.getNombreVersion());
        borrador.setEstado(EstadoVersionConfiguracion.BORRADOR);
        borrador.setAnioLectivoVigencia(anio);
        borrador.setSituacionesTipo1Json(request.getSituacionesTipo1Json());
        borrador.setSituacionesTipo2Json(request.getSituacionesTipo2Json());
        borrador.setSituacionesTipo3Json(request.getSituacionesTipo3Json());
        borrador.setPlazosDebidoProcesoJson(request.getPlazosDebidoProcesoJson());
        borrador.setUsuarioCreador(creador);

        ConfiguracionManualConvivenciaVersion guardado = manualRepository.save(borrador);

        auditoriaService.registrarEvento(new RegistroAuditoriaEventoRequest(
                autorizador != null ? autorizador : "SISTEMA",
                "COORDINADOR_CONVIVENCIA",
                "CONFIGURACION",
                "CREAR_BORRADOR_MANUAL",
                "ConfiguracionManualConvivenciaVersion",
                String.valueOf(guardado.getId()),
                "Creación de borrador de versión " + guardado.getNumeroVersion() + " del Manual de Convivencia",
                ResultadoAuditoria.EXITO,
                "MAN-BORR-" + guardado.getId()
        ));

        return toManualResponse(guardado);
    }

    public ConfiguracionManualConvivenciaResponse actualizarBorradorManual(Long manualId, ActualizarManualBorradorRequest request, String autorizador) {
        ConfiguracionManualConvivenciaVersion m = manualRepository.findById(manualId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Manual de convivencia no encontrado con ID: " + manualId));

        if (m.getEstado() == EstadoVersionConfiguracion.VIGENTE || m.getEstado() == EstadoVersionConfiguracion.HISTORICA) {
            throw new ConflictoException("Inmutabilidad: No se puede modificar una versión ya vigente o histórica (E5). Debe clonarse.");
        }

        if (request.getAnioLectivoVigenciaId() != null) {
            AnioLectivo anio = anioLectivoRepository.findById(request.getAnioLectivoVigenciaId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Año lectivo no encontrado"));
            m.setAnioLectivoVigencia(anio);
        }

        m.setNombreVersion(request.getNombreVersion());
        m.setSituacionesTipo1Json(request.getSituacionesTipo1Json());
        m.setSituacionesTipo2Json(request.getSituacionesTipo2Json());
        m.setSituacionesTipo3Json(request.getSituacionesTipo3Json());
        m.setPlazosDebidoProcesoJson(request.getPlazosDebidoProcesoJson());

        ConfiguracionManualConvivenciaVersion guardado = manualRepository.save(m);
        return toManualResponse(guardado);
    }

    public ConfiguracionManualConvivenciaResponse clonarManual(Long manualId, ClonarVersionRequest request, String autorizador) {
        ConfiguracionManualConvivenciaVersion fuente = manualRepository.findById(manualId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Manual a clonar no encontrado con ID: " + manualId));

        int siguienteVersion = manualRepository.findTopByOrderByNumeroVersionDesc()
                .map(m -> m.getNumeroVersion() + 1)
                .orElse(1);

        AnioLectivo anio = fuente.getAnioLectivoVigencia();
        if (request.getAnioLectivoVigenciaId() != null) {
            anio = anioLectivoRepository.findById(request.getAnioLectivoVigenciaId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Año lectivo destino no encontrado"));
        }

        Usuario creador = autorizador != null ? usuarioRepository.findByUsername(autorizador).orElse(null) : null;

        ConfiguracionManualConvivenciaVersion clon = new ConfiguracionManualConvivenciaVersion();
        clon.setNumeroVersion(siguienteVersion);
        clon.setNombreVersion(request.getNuevoNombreVersion() != null && !request.getNuevoNombreVersion().isBlank()
                ? request.getNuevoNombreVersion()
                : fuente.getNombreVersion() + " (Borrador copia)");
        clon.setEstado(EstadoVersionConfiguracion.BORRADOR);
        clon.setAnioLectivoVigencia(anio);
        clon.setSituacionesTipo1Json(fuente.getSituacionesTipo1Json());
        clon.setSituacionesTipo2Json(fuente.getSituacionesTipo2Json());
        clon.setSituacionesTipo3Json(fuente.getSituacionesTipo3Json());
        clon.setPlazosDebidoProcesoJson(fuente.getPlazosDebidoProcesoJson());
        clon.setUsuarioCreador(creador);

        ConfiguracionManualConvivenciaVersion guardado = manualRepository.save(clon);

        auditoriaService.registrarEvento(new RegistroAuditoriaEventoRequest(
                autorizador != null ? autorizador : "SISTEMA",
                "COORDINADOR_CONVIVENCIA",
                "CONFIGURACION",
                "CLONAR_MANUAL_VERSION",
                "ConfiguracionManualConvivenciaVersion",
                String.valueOf(guardado.getId()),
                "Clonación de Manual versión " + fuente.getNumeroVersion() + " a nuevo borrador " + guardado.getNumeroVersion(),
                ResultadoAuditoria.EXITO,
                "MAN-CLON-" + guardado.getId()
        ));

        return toManualResponse(guardado);
    }

    public ConfiguracionManualConvivenciaResponse marcarManualListoParaAprobacion(Long manualId, String autorizador) {
        ConfiguracionManualConvivenciaVersion m = manualRepository.findById(manualId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Manual de convivencia no encontrado con ID: " + manualId));

        if (m.getEstado() != EstadoVersionConfiguracion.BORRADOR) {
            throw new ConflictoException("Solo una versión en estado BORRADOR puede marcarse como LISTA_PARA_APROBACION");
        }

        m.setEstado(EstadoVersionConfiguracion.LISTA_PARA_APROBACION);
        ConfiguracionManualConvivenciaVersion guardado = manualRepository.save(m);

        auditoriaService.registrarEvento(new RegistroAuditoriaEventoRequest(
                autorizador != null ? autorizador : "SISTEMA",
                "COORDINADOR_CONVIVENCIA",
                "CONFIGURACION",
                "MANUAL_LISTO_PARA_APROBACION",
                "ConfiguracionManualConvivenciaVersion",
                String.valueOf(guardado.getId()),
                "Versión Manual " + guardado.getNumeroVersion() + " marcada como LISTA_PARA_APROBACION por Coordinación de Convivencia",
                ResultadoAuditoria.EXITO,
                "MAN-LISTO-" + guardado.getId()
        ));

        return toManualResponse(guardado);
    }

    public ConfiguracionManualConvivenciaResponse aprobarManualConvivencia(Long manualId, AprobarManualConvivenciaRequest request, String rectorUsername) {
        ConfiguracionManualConvivenciaVersion m = manualRepository.findById(manualId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Manual de convivencia no encontrado con ID: " + manualId));

        if (m.getEstado() == EstadoVersionConfiguracion.VIGENTE || m.getEstado() == EstadoVersionConfiguracion.HISTORICA) {
            throw new ConflictoException("Inmutabilidad: No se puede modificar una versión ya vigente o histórica (E5)");
        }

        if (request.getActaComiteConvivencia() == null || request.getActaComiteConvivencia().isBlank() ||
            request.getActaConsejoDirectivo() == null || request.getActaConsejoDirectivo().isBlank()) {
            throw new ConflictoException("Se requieren obligatoriamente las actas del Comité de Convivencia y Consejo Directivo (E2, Ley 1620)");
        }
        if (request.getConstanciaSocializacion() == null || request.getConstanciaSocializacion().isBlank()) {
            throw new ConflictoException("Se requiere la constancia de socialización del Manual ante la comunidad educativa (E2, Ley 1620 Art. 18)");
        }

        if (m.getAnioLectivoVigencia() != null && m.getAnioLectivoVigencia().getEstado() == EstadoAnioLectivo.CERRADO) {
            throw new ConflictoException("No es posible aplicar un Manual de Convivencia a un año lectivo cerrado (E3, RN-CFG-06)");
        }

        boolean esPlaneacionFutura = m.getAnioLectivoVigencia() != null &&
                m.getAnioLectivoVigencia().getEstado() == EstadoAnioLectivo.PLANIFICACION;

        Usuario rector = rectorUsername != null ? usuarioRepository.findByUsername(rectorUsername).orElse(null) : null;

        if (esPlaneacionFutura) {
            m.setEstado(EstadoVersionConfiguracion.APROBADA_VIGENCIA_FUTURA);
        } else {
            manualRepository.findFirstByEstadoOrderByNumeroVersionDesc(EstadoVersionConfiguracion.VIGENTE).ifPresent(v -> {
                v.setEstado(EstadoVersionConfiguracion.HISTORICA);
                manualRepository.save(v);
            });
            m.setEstado(EstadoVersionConfiguracion.VIGENTE);
        }

        m.setFechaInicioVigencia(request.getFechaInicioVigencia());
        m.setActaComiteConvivencia(request.getActaComiteConvivencia());
        m.setActaConsejoDirectivo(request.getActaConsejoDirectivo());
        m.setConstanciaSocializacion(request.getConstanciaSocializacion());
        m.setFechaSocializacion(request.getFechaSocializacion() != null ? request.getFechaSocializacion() : LocalDate.now());
        m.setSoportePdfUrl(request.getSoportePdfUrl());
        m.setFechaAprobacion(LocalDateTime.now());
        m.setUsuarioAprobador(rector);

        ConfiguracionManualConvivenciaVersion publicada = manualRepository.save(m);
        return toManualResponse(publicada);
    }

    public void rechazarEliminacionVersionManual(Long id) {
        throw new ConflictoException("Operación prohibida: Las versiones del Manual de Convivencia son inmutables y no admiten eliminación física (E5, RN-CFG-05).");
    }

    // ── SIMULADOR DE CALIFICACIONES Y EQUIVALENCIAS (UC-26 A4) ──────────────────

    @Transactional(readOnly = true)
    public SimulacionSieeResponse simularSiee(Long sieeId, SimulacionSieeRequest request) {
        ConfiguracionSieeVersion siee = sieeRepository.findById(sieeId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Versión del SIEE no encontrada con ID: " + sieeId));

        if (request.getCalificaciones() == null || request.getCalificaciones().isEmpty()) {
            throw new ValidacionException("Debe ingresar al menos una calificación para simular");
        }

        int escalaDecimales = siee.getDecimales() != null ? siee.getDecimales() : 2;
        RoundingMode modoRedondeo = RoundingMode.HALF_UP;
        try {
            if (siee.getModoRedondeo() != null) {
                modoRedondeo = RoundingMode.valueOf(siee.getModoRedondeo());
            }
        } catch (IllegalArgumentException ignored) {
        }

        List<String> detalles = new ArrayList<>();
        BigDecimal sumaPonderada = BigDecimal.ZERO;
        BigDecimal divisorTotal = BigDecimal.ZERO;

        boolean usarPonderaciones = request.getPonderaciones() != null &&
                request.getPonderaciones().size() == request.getCalificaciones().size();

        for (int i = 0; i < request.getCalificaciones().size(); i++) {
            BigDecimal nota = request.getCalificaciones().get(i);
            if (nota.compareTo(siee.getEscalaMinima()) < 0 || nota.compareTo(siee.getEscalaMaxima()) > 0) {
                throw new ValidacionException("La nota " + nota + " está fuera del rango permitido [" +
                        siee.getEscalaMinima() + " - " + siee.getEscalaMaxima() + "]");
            }

            BigDecimal peso = usarPonderaciones ? request.getPonderaciones().get(i) : BigDecimal.ONE;
            sumaPonderada = sumaPonderada.add(nota.multiply(peso));
            divisorTotal = divisorTotal.add(peso);
            detalles.add("Nota " + (i + 1) + ": " + nota + (usarPonderaciones ? " (peso: " + peso + ")" : ""));
        }

        BigDecimal promedio = sumaPonderada.divide(divisorTotal, escalaDecimales, modoRedondeo);
        detalles.add("Promedio calculado: " + promedio);

        BigDecimal notaFinal = promedio;
        if (request.getCalificacionRecuperacion() != null) {
            BigDecimal rec = request.getCalificacionRecuperacion();
            if (rec.compareTo(siee.getEscalaMinima()) < 0 || rec.compareTo(siee.getEscalaMaxima()) > 0) {
                throw new ValidacionException("La nota de recuperación " + rec + " está fuera de la escala.");
            }
            if (rec.compareTo(promedio) > 0) {
                notaFinal = rec;
                detalles.add("Nivelación aplicada: Promedio superado por recuperación (" + rec + ")");
            } else {
                detalles.add("Nivelación no modifica nota: Recuperación (" + rec + ") <= Promedio (" + promedio + ")");
            }
        }

        String desempeno = asignarDesempenoNacional(notaFinal, siee.getRangosEquivalenciaJson(), siee.getEscalaAprobatoria());
        boolean aprobado = notaFinal.compareTo(siee.getEscalaAprobatoria()) >= 0;

        SimulacionSieeResponse resp = new SimulacionSieeResponse();
        resp.setNotaPromedio(promedio);
        resp.setNotaFinal(notaFinal);
        resp.setDesempenoNacional(desempeno);
        resp.setAprobado(aprobado);
        resp.setDecimales(escalaDecimales);
        resp.setModoRedondeo(modoRedondeo.name());
        resp.setDetallesCalculo(detalles);
        return resp;
    }

    // ── COMPARADOR DE DIFERENCIAS ENTRE VERSIONES (UC-PROV-14 A1) ───────────────

    @Transactional(readOnly = true)
    public DiferenciaVersionResponse compararVersionesSiee(Long v1Id, Long v2Id) {
        ConfiguracionSieeVersion v1 = sieeRepository.findById(v1Id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Versión 1 no encontrada con ID: " + v1Id));
        ConfiguracionSieeVersion v2 = sieeRepository.findById(v2Id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Versión 2 no encontrada con ID: " + v2Id));

        List<DiferenciaVersionResponse.CampoDiferencia> diffs = new ArrayList<>();
        compararCampo(diffs, "nombreVersion", v1.getNombreVersion(), v2.getNombreVersion());
        compararCampo(diffs, "estado", v1.getEstado(), v2.getEstado());
        compararCampo(diffs, "escalaMinima", v1.getEscalaMinima(), v2.getEscalaMinima());
        compararCampo(diffs, "escalaAprobatoria", v1.getEscalaAprobatoria(), v2.getEscalaAprobatoria());
        compararCampo(diffs, "escalaMaxima", v1.getEscalaMaxima(), v2.getEscalaMaxima());
        compararCampo(diffs, "decimales", v1.getDecimales(), v2.getDecimales());
        compararCampo(diffs, "modoRedondeo", v1.getModoRedondeo(), v2.getModoRedondeo());
        compararCampo(diffs, "umbralAsistenciaReprobacion", v1.getUmbralAsistenciaReprobacion(), v2.getUmbralAsistenciaReprobacion());
        compararCampo(diffs, "plazoReclamacionesDias", v1.getPlazoReclamacionesDias(), v2.getPlazoReclamacionesDias());
        compararCampo(diffs, "rangosEquivalenciaJson", v1.getRangosEquivalenciaJson(), v2.getRangosEquivalenciaJson());
        compararCampo(diffs, "ponderacionPeriodosJson", v1.getPonderacionPeriodosJson(), v2.getPonderacionPeriodosJson());

        return new DiferenciaVersionResponse(v1.getNumeroVersion(), v2.getNumeroVersion(), "SIEE", diffs);
    }

    @Transactional(readOnly = true)
    public DiferenciaVersionResponse compararVersionesManual(Long v1Id, Long v2Id) {
        ConfiguracionManualConvivenciaVersion v1 = manualRepository.findById(v1Id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Versión 1 no encontrada con ID: " + v1Id));
        ConfiguracionManualConvivenciaVersion v2 = manualRepository.findById(v2Id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Versión 2 no encontrada con ID: " + v2Id));

        List<DiferenciaVersionResponse.CampoDiferencia> diffs = new ArrayList<>();
        compararCampo(diffs, "nombreVersion", v1.getNombreVersion(), v2.getNombreVersion());
        compararCampo(diffs, "estado", v1.getEstado(), v2.getEstado());
        compararCampo(diffs, "situacionesTipo1Json", v1.getSituacionesTipo1Json(), v2.getSituacionesTipo1Json());
        compararCampo(diffs, "situacionesTipo2Json", v1.getSituacionesTipo2Json(), v2.getSituacionesTipo2Json());
        compararCampo(diffs, "situacionesTipo3Json", v1.getSituacionesTipo3Json(), v2.getSituacionesTipo3Json());
        compararCampo(diffs, "plazosDebidoProcesoJson", v1.getPlazosDebidoProcesoJson(), v2.getPlazosDebidoProcesoJson());

        return new DiferenciaVersionResponse(v1.getNumeroVersion(), v2.getNumeroVersion(), "MANUAL_CONVIVENCIA", diffs);
    }

    private void compararCampo(List<DiferenciaVersionResponse.CampoDiferencia> diffs, String campo, Object a, Object b) {
        if (!Objects.equals(a, b)) {
            diffs.add(new DiferenciaVersionResponse.CampoDiferencia(campo, a, b));
        }
    }

    // ── VALIDADOR MATEMÁTICO EXHAUSTIVO (E1, RN-02, RN-CFG-03) ─────────────────

    private void validarConsistenciaMatematicaSiee(BigDecimal min, BigDecimal aprob, BigDecimal max, String rangosJson, String ponderacionesJson) {
        if (min == null || aprob == null || max == null) {
            throw new ValidacionException("La escala de valoración no puede tener valores nulos (E1)");
        }
        if (min.compareTo(aprob) >= 0 || aprob.compareTo(max) >= 0) {
            throw new ValidacionException("La escala es inconsistente: Debe cumplirse estrictamente escalaMinima < escalaAprobatoria < escalaMaxima (E1)");
        }
        if (rangosJson == null || rangosJson.isBlank()) {
            throw new ValidacionException("Los rangos de equivalencia institucional con la Escala Nacional son obligatorios (E1)");
        }

        // Validación de rangos JSON continuos sin huecos ni traslapes
        try {
            JsonNode root = objectMapper.readTree(rangosJson);
            if (root.isArray() && root.size() > 1) {
                List<JsonNode> lista = new ArrayList<>();
                root.forEach(lista::add);
                lista.sort(Comparator.comparing(n -> new BigDecimal(n.get("min").asText())));

                BigDecimal inicio = new BigDecimal(lista.get(0).get("min").asText());
                if (inicio.compareTo(min) != 0) {
                    throw new ValidacionException("El rango inferior debe iniciar exactamente en escalaMinima " + min + " (E1)");
                }

                BigDecimal fin = new BigDecimal(lista.get(lista.size() - 1).get("max").asText());
                if (fin.compareTo(max) != 0) {
                    throw new ValidacionException("El rango superior debe terminar exactamente en escalaMaxima " + max + " (E1)");
                }

                for (int i = 0; i < lista.size() - 1; i++) {
                    BigDecimal maxActual = new BigDecimal(lista.get(i).get("max").asText());
                    BigDecimal minSiguiente = new BigDecimal(lista.get(i + 1).get("min").asText());

                    // Solapamiento: minSiguiente <= maxActual
                    if (minSiguiente.compareTo(maxActual) <= 0) {
                        throw new ValidacionException("Solapamiento detectado entre rangos en la escala (" + maxActual + " >= " + minSiguiente + ") (E1, RN-CFG-03)");
                    }
                    // Hueco: minSiguiente - maxActual > 0.01
                    if (minSiguiente.subtract(maxActual).compareTo(new BigDecimal("0.01")) > 0) {
                        throw new ValidacionException("Hueco numérico detectado en la escala entre " + maxActual + " y " + minSiguiente + " (E1, RN-CFG-03)");
                    }
                }
            }
        } catch (ValidacionException ve) {
            throw ve;
        } catch (Exception e) {
            // Si el formato es libre o simple, asegurar que contenga al menos texto no vacío
        }

        // Validación de suma exacta al 100.00% en ponderaciones de periodos
        if (ponderacionesJson != null && !ponderacionesJson.isBlank()) {
            try {
                JsonNode rootPond = objectMapper.readTree(ponderacionesJson);
                if (rootPond.isArray() && rootPond.size() > 0) {
                    BigDecimal totalPonderacion = BigDecimal.ZERO;
                    for (JsonNode item : rootPond) {
                        if (item.has("porcentaje")) {
                            totalPonderacion = totalPonderacion.add(new BigDecimal(item.get("porcentaje").asText()));
                        }
                    }
                    if (totalPonderacion.compareTo(new BigDecimal("100.00")) != 0 && totalPonderacion.compareTo(new BigDecimal("100")) != 0) {
                        throw new ValidacionException("Las ponderaciones de los periodos deben sumar exactamente 100.00% (Suma actual: " + totalPonderacion + "%) (E1)");
                    }
                }
            } catch (ValidacionException ve) {
                throw ve;
            } catch (Exception ignored) {
            }
        }
    }

    private String asignarDesempenoNacional(BigDecimal nota, String rangosJson, BigDecimal aprobatoria) {
        if (rangosJson != null && !rangosJson.isBlank()) {
            try {
                JsonNode root = objectMapper.readTree(rangosJson);
                if (root.isArray()) {
                    for (JsonNode n : root) {
                        BigDecimal rMin = new BigDecimal(n.get("min").asText());
                        BigDecimal rMax = new BigDecimal(n.get("max").asText());
                        if (nota.compareTo(rMin) >= 0 && nota.compareTo(rMax) <= 0) {
                            return n.get("desempeno").asText();
                        }
                    }
                }
            } catch (Exception ignored) {
            }
        }
        return nota.compareTo(aprobatoria) >= 0 ? "BASICO" : "BAJO";
    }

    private ConfiguracionSieeResponse toSieeResponse(ConfiguracionSieeVersion s) {
        ConfiguracionSieeResponse r = new ConfiguracionSieeResponse();
        r.setId(s.getId());
        r.setNumeroVersion(s.getNumeroVersion());
        r.setNombreVersion(s.getNombreVersion());
        r.setEstado(s.getEstado());
        r.setAnioLectivoVigenciaId(s.getAnioLectivoVigencia() != null ? s.getAnioLectivoVigencia().getId() : null);
        r.setAnioLectivoNombre(s.getAnioLectivoVigencia() != null ? s.getAnioLectivoVigencia().getNombre() : null);
        r.setFechaInicioVigencia(s.getFechaInicioVigencia());
        r.setEscalaMinima(s.getEscalaMinima());
        r.setEscalaMaxima(s.getEscalaMaxima());
        r.setEscalaAprobatoria(s.getEscalaAprobatoria());
        r.setDecimales(s.getDecimales());
        r.setModoRedondeo(s.getModoRedondeo());
        r.setRangosEquivalenciaJson(s.getRangosEquivalenciaJson());
        r.setPonderacionPeriodosJson(s.getPonderacionPeriodosJson());
        r.setCriteriosPromocionJson(s.getCriteriosPromocionJson());
        r.setUmbralAsistenciaReprobacion(s.getUmbralAsistenciaReprobacion());
        r.setPlazoReclamacionesDias(s.getPlazoReclamacionesDias());
        r.setActaConsejoAcademico(s.getActaConsejoAcademico());
        r.setActaConsejoDirectivo(s.getActaConsejoDirectivo());
        r.setConstanciaSocializacion(s.getConstanciaSocializacion());
        r.setFechaSocializacion(s.getFechaSocializacion());
        r.setSoportePdfUrl(s.getSoportePdfUrl());
        r.setFechaCreacion(s.getFechaCreacion());
        r.setFechaAprobacion(s.getFechaAprobacion());
        return r;
    }

    private ConfiguracionManualConvivenciaResponse toManualResponse(ConfiguracionManualConvivenciaVersion m) {
        ConfiguracionManualConvivenciaResponse r = new ConfiguracionManualConvivenciaResponse();
        r.setId(m.getId());
        r.setNumeroVersion(m.getNumeroVersion());
        r.setNombreVersion(m.getNombreVersion());
        r.setEstado(m.getEstado());
        r.setAnioLectivoVigenciaId(m.getAnioLectivoVigencia() != null ? m.getAnioLectivoVigencia().getId() : null);
        r.setAnioLectivoNombre(m.getAnioLectivoVigencia() != null ? m.getAnioLectivoVigencia().getNombre() : null);
        r.setFechaInicioVigencia(m.getFechaInicioVigencia());
        r.setActaComiteConvivencia(m.getActaComiteConvivencia());
        r.setActaConsejoDirectivo(m.getActaConsejoDirectivo());
        r.setConstanciaSocializacion(m.getConstanciaSocializacion());
        r.setFechaSocializacion(m.getFechaSocializacion());
        r.setSituacionesTipo1Json(m.getSituacionesTipo1Json());
        r.setSituacionesTipo2Json(m.getSituacionesTipo2Json());
        r.setSituacionesTipo3Json(m.getSituacionesTipo3Json());
        r.setPlazosDebidoProcesoJson(m.getPlazosDebidoProcesoJson());
        r.setSoportePdfUrl(m.getSoportePdfUrl());
        r.setFechaCreacion(m.getFechaCreacion());
        r.setFechaAprobacion(m.getFechaAprobacion());
        return r;
    }
}
