package com.jackren.sistemaacademico.service;

import com.jackren.sistemaacademico.dto.*;
import com.jackren.sistemaacademico.entity.*;
import com.jackren.sistemaacademico.exception.ConflictoException;
import com.jackren.sistemaacademico.exception.RecursoNoEncontradoException;
import com.jackren.sistemaacademico.exception.ValidacionException;
import com.jackren.sistemaacademico.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Servicio para estructuración, validación matemática y aprobación colegiada del SIEE y Manual de Convivencia (UC-26, UC-PROV-14).
 */
@Service
@Transactional
public class SieeConfiguracionService {

    private final ConfiguracionSieeVersionRepository sieeRepository;
    private final ConfiguracionManualConvivenciaVersionRepository manualRepository;
    private final AnioLectivoRepository anioLectivoRepository;
    private final UsuarioRepository usuarioRepository;
    private final AuditoriaGlobalService auditoriaService;

    public SieeConfiguracionService(ConfiguracionSieeVersionRepository sieeRepository,
                                    ConfiguracionManualConvivenciaVersionRepository manualRepository,
                                    AnioLectivoRepository anioLectivoRepository,
                                    UsuarioRepository usuarioRepository,
                                    AuditoriaGlobalService auditoriaService) {
        this.sieeRepository = sieeRepository;
        this.manualRepository = manualRepository;
        this.anioLectivoRepository = anioLectivoRepository;
        this.usuarioRepository = usuarioRepository;
        this.auditoriaService = auditoriaService;
    }

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

    public ConfiguracionSieeResponse crearBorradorSiee(CrearSieeBorradorRequest request, String autorizador) {
        int siguienteVersion = sieeRepository.findTopByOrderByNumeroVersionDesc()
                .map(s -> s.getNumeroVersion() + 1)
                .orElse(1);

        validarConsistenciaMatematicaSiee(request.getEscalaMinima(), request.getEscalaAprobatoria(), request.getEscalaMaxima(), request.getRangosEquivalenciaJson());

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

    public ConfiguracionSieeResponse aprobarYPublicarSiee(Long sieeId, AprobarSieeRequest request, String rectorUsername) {
        ConfiguracionSieeVersion siee = sieeRepository.findById(sieeId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Versión del SIEE no encontrada con ID: " + sieeId));

        // Regla E5: Inmutabilidad estricta de versiones ya vigentes o históricas
        if (siee.getEstado() == EstadoVersionConfiguracion.VIGENTE || siee.getEstado() == EstadoVersionConfiguracion.HISTORICA) {
            throw new ConflictoException("No es posible modificar una versión que ya ha sido aprobada, vigente o histórica (E5). Debe clonarse a un nuevo borrador.");
        }

        // Regla E2: Obligatoriedad legal de actas de órganos colegiados (Consejo Académico y Directivo)
        if (request.getActaConsejoAcademico() == null || request.getActaConsejoAcademico().isBlank()) {
            throw new ConflictoException("Se requiere obligatoriamente el acta de estudio y aval del Consejo Académico (E2, RN-CFG-01)");
        }
        if (request.getActaConsejoDirectivo() == null || request.getActaConsejoDirectivo().isBlank()) {
            throw new ConflictoException("Se requiere obligatoriamente el acta de adopción oficial del Consejo Directivo (E2, RN-CFG-02)");
        }

        // Regla E1: Validación matemática de consistencia
        validarConsistenciaMatematicaSiee(siee.getEscalaMinima(), siee.getEscalaAprobatoria(), siee.getEscalaMaxima(), siee.getRangosEquivalenciaJson());

        // Regla E3: Irretroactividad sobre años lectivos cerrados
        if (siee.getAnioLectivoVigencia() != null && siee.getAnioLectivoVigencia().getEstado() == EstadoAnioLectivo.CERRADO) {
            throw new ConflictoException("No es posible aplicar una nueva versión del SIEE sobre un año lectivo que ya fue cerrado (E3, RN-CFG-06)");
        }

        // Desactivar vigencia de versión anterior pasando a HISTORICA
        sieeRepository.findFirstByEstadoOrderByNumeroVersionDesc(EstadoVersionConfiguracion.VIGENTE).ifPresent(v -> {
            v.setEstado(EstadoVersionConfiguracion.HISTORICA);
            sieeRepository.save(v);
        });

        Usuario rector = rectorUsername != null ? usuarioRepository.findByUsername(rectorUsername).orElse(null) : null;

        siee.setEstado(EstadoVersionConfiguracion.VIGENTE);
        siee.setFechaInicioVigencia(request.getFechaInicioVigencia());
        siee.setActaConsejoAcademico(request.getActaConsejoAcademico());
        siee.setActaConsejoDirectivo(request.getActaConsejoDirectivo());
        siee.setSoportePdfUrl(request.getSoportePdfUrl());
        siee.setFechaAprobacion(java.time.LocalDateTime.now());
        siee.setUsuarioAprobador(rector);

        ConfiguracionSieeVersion publicada = sieeRepository.save(siee);

        auditoriaService.registrarEvento(new RegistroAuditoriaEventoRequest(
                rectorUsername != null ? rectorUsername : "SISTEMA",
                "RECTOR",
                "CONFIGURACION",
                "PUBLICAR_SIEE_VERSION",
                "ConfiguracionSieeVersion",
                String.valueOf(publicada.getId()),
                "Publicación oficial de versión SIEE " + publicada.getNumeroVersion() + " con actas colegiadas",
                ResultadoAuditoria.EXITO,
                "SIEE-PUB-" + publicada.getId()
        ));

        return toSieeResponse(publicada);
    }

    @Transactional(readOnly = true)
    public ConfiguracionManualConvivenciaResponse obtenerManualVigente() {
        ConfiguracionManualConvivenciaVersion m = manualRepository.findFirstByEstadoOrderByNumeroVersionDesc(EstadoVersionConfiguracion.VIGENTE)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe versión vigente del Manual de Convivencia"));
        return toManualResponse(m);
    }

    public ConfiguracionManualConvivenciaResponse aprobarManualConvivencia(Long manualId, AprobarManualConvivenciaRequest request, String rectorUsername) {
        ConfiguracionManualConvivenciaVersion m = manualRepository.findById(manualId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Manual de convivencia no encontrado"));

        if (m.getEstado() == EstadoVersionConfiguracion.VIGENTE || m.getEstado() == EstadoVersionConfiguracion.HISTORICA) {
            throw new ConflictoException("Inmutabilidad: No se puede modificar una versión ya vigente o histórica (E5)");
        }

        if (request.getActaComiteConvivencia() == null || request.getActaComiteConvivencia().isBlank() ||
            request.getActaConsejoDirectivo() == null || request.getActaConsejoDirectivo().isBlank()) {
            throw new ConflictoException("Se requieren obligatoriamente las actas del Comité de Convivencia y Consejo Directivo (E2, Ley 1620)");
        }

        manualRepository.findFirstByEstadoOrderByNumeroVersionDesc(EstadoVersionConfiguracion.VIGENTE).ifPresent(v -> {
            v.setEstado(EstadoVersionConfiguracion.HISTORICA);
            manualRepository.save(v);
        });

        m.setEstado(EstadoVersionConfiguracion.VIGENTE);
        m.setFechaInicioVigencia(request.getFechaInicioVigencia());
        m.setActaComiteConvivencia(request.getActaComiteConvivencia());
        m.setActaConsejoDirectivo(request.getActaConsejoDirectivo());
        m.setSoportePdfUrl(request.getSoportePdfUrl());
        m.setFechaAprobacion(java.time.LocalDateTime.now());

        ConfiguracionManualConvivenciaVersion publicada = manualRepository.save(m);
        return toManualResponse(publicada);
    }

    private void validarConsistenciaMatematicaSiee(BigDecimal min, BigDecimal aprob, BigDecimal max, String rangosJson) {
        if (min == null || aprob == null || max == null) {
            throw new ValidacionException("La escala de valoración no puede tener valores nulos (E1)");
        }
        if (min.compareTo(aprob) >= 0 || aprob.compareTo(max) >= 0) {
            throw new ValidacionException("La escala es inconsistente: Debe cumplirse estrictamente escalaMinima < escalaAprobatoria < escalaMaxima (E1)");
        }
        if (rangosJson == null || rangosJson.isBlank()) {
            throw new ValidacionException("Los rangos de equivalencia institucional con la Escala Nacional son obligatorios (E1)");
        }
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
        r.setFechaInicioVigencia(m.getFechaInicioVigencia());
        r.setActaComiteConvivencia(m.getActaComiteConvivencia());
        r.setActaConsejoDirectivo(m.getActaConsejoDirectivo());
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
