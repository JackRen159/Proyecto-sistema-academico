package com.jackren.sistemaacademico.service;

import com.jackren.sistemaacademico.dto.*;
import com.jackren.sistemaacademico.entity.*;
import com.jackren.sistemaacademico.exception.ConflictoNegocioException;
import com.jackren.sistemaacademico.exception.RecursoNoEncontradoException;
import com.jackren.sistemaacademico.exception.ValidacionException;
import com.jackren.sistemaacademico.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class DebidoProcesoService {

    private final ExpedienteDebidoProcesoRepository expedienteRepository;
    private final DescargoDebidoProcesoRepository descargoRepository;
    private final RecursoImpugnacionRepository recursoRepository;
    private final MedidaSancionConvivenciaRepository medidaRepository;
    private final CasoConvivenciaRepository casoRepository;
    private final EstudianteRepository estudianteRepository;
    private final UsuarioRepository usuarioRepository;
    private final DiaNoLectivoRepository diaNoLectivoRepository;
    private final AuditoriaGlobalService auditoriaGlobalService;

    public DebidoProcesoService(
            ExpedienteDebidoProcesoRepository expedienteRepository,
            DescargoDebidoProcesoRepository descargoRepository,
            RecursoImpugnacionRepository recursoRepository,
            MedidaSancionConvivenciaRepository medidaRepository,
            CasoConvivenciaRepository casoRepository,
            EstudianteRepository estudianteRepository,
            UsuarioRepository usuarioRepository,
            DiaNoLectivoRepository diaNoLectivoRepository,
            AuditoriaGlobalService auditoriaGlobalService) {
        this.expedienteRepository = expedienteRepository;
        this.descargoRepository = descargoRepository;
        this.recursoRepository = recursoRepository;
        this.medidaRepository = medidaRepository;
        this.casoRepository = casoRepository;
        this.estudianteRepository = estudianteRepository;
        this.usuarioRepository = usuarioRepository;
        this.diaNoLectivoRepository = diaNoLectivoRepository;
        this.auditoriaGlobalService = auditoriaGlobalService;
    }

    public ExpedienteDebidoProcesoResponse abrirExpedienteYNotificarCargos(AperturaCargosRequest request, Long instructorUsuarioId) {
        CasoConvivencia caso = casoRepository.findById(request.casoConvivenciaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Caso de convivencia no encontrado con ID: " + request.casoConvivenciaId()));

        Estudiante estudiante = estudianteRepository.findById(request.estudianteId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Estudiante no encontrado con ID: " + request.estudianteId()));

        Usuario instructor = usuarioRepository.findById(instructorUsuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario instructor no encontrado con ID: " + instructorUsuarioId));

        // Invariante E1: Exigir formulación fáctica precisa, normas del Manual vigente y pruebas trasladadas (RN-CON-07, Sentencia T-004 de 2026)
        if (request.autoAperturaHechos() == null || request.autoAperturaHechos().trim().length() < 10) {
            throw new ValidacionException("El pliego de cargos exige una relación concreta y detallada de hechos de tiempo, modo y lugar (E1)");
        }
        if (request.normasManualInfringidas() == null || request.normasManualInfringidas().trim().isEmpty()) {
            throw new ValidacionException("El pliego de cargos exige citar los artículos específicos del Manual de Convivencia presuntamente infringidos (E1)");
        }
        if (request.pruebasTrasladadasAnonimizadas() == null || request.pruebasTrasladadasAnonimizadas().trim().isEmpty()) {
            throw new ValidacionException("El pliego de cargos exige adjuntar las pruebas trasladadas anonimizando datos de terceros menores (E1, RN-CON-10)");
        }

        int diasDescargos = (request.diasHabilesDescargos() != null && request.diasHabilesDescargos() > 0)
                ? request.diasHabilesDescargos()
                : 5;

        Long anioLectivoId = caso.getAnioLectivo().getId();
        LocalDate fechaLimite = calcularDiasHabilesLectivos(anioLectivoId, LocalDate.now(), diasDescargos);

        ExpedienteDebidoProceso expediente = new ExpedienteDebidoProceso();
        String numero = "EXP-DP-" + caso.getAnioLectivo().getAnio() + "-" + String.format("%04d", expedienteRepository.count() + 1);
        expediente.setNumeroExpediente(numero);
        expediente.setCasoConvivencia(caso);
        expediente.setEstudiante(estudiante);
        expediente.setInstructorUsuario(instructor);
        expediente.setAutoAperturaHechos(request.autoAperturaHechos().trim());
        expediente.setNormasManualInfringidas(request.normasManualInfringidas().trim());
        expediente.setPruebasTrasladadasAnonimizadas(request.pruebasTrasladadasAnonimizadas().trim());
        expediente.setDiasHabilesDescargos(diasDescargos);
        expediente.setFechaLimiteDescargos(fechaLimite);
        expediente.setAcompanamientoPersonero(request.acompanamientoPersonero() != null && request.acompanamientoPersonero());
        expediente.setConceptoOrientacion(request.conceptoOrientacion());
        expediente.setEstadoExpediente(EstadoExpedienteDebidoProceso.CARGOS_NOTIFICADOS_EN_TERMINO_DESCARGOS);

        ExpedienteDebidoProceso guardado = expedienteRepository.save(expediente);

        // Actualizar caso si corresponde
        caso.setEstadoCaso(EstadoCasoConvivencia.EN_DEBIDO_PROCESO);
        casoRepository.save(caso);

        if (auditoriaGlobalService != null) {
            auditoriaGlobalService.registrarEvento(
                    "APERTURA_DEBIDO_PROCESO",
                    "ExpedienteDebidoProceso",
                    guardado.getId().toString(),
                    "N/A",
                    "Expediente: " + guardado.getNumeroExpediente() + ", Vence: " + fechaLimite,
                    "Formulación formal de pliego de cargos bajo debido proceso (Art. 29 CP)",
                    "EXITOSO"
            );
        }

        return toResponse(guardado);
    }

    public DescargoDebidoProcesoResponse radicarDescargos(Long expedienteId, RadicarDescargosRequest request, Long presentadoPorUsuarioId) {
        ExpedienteDebidoProceso expediente = expedienteRepository.findById(expedienteId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Expediente no encontrado con ID: " + expedienteId));

        if (expediente.getEstadoExpediente() != EstadoExpedienteDebidoProceso.CARGOS_NOTIFICADOS_EN_TERMINO_DESCARGOS &&
            expediente.getEstadoExpediente() != EstadoExpedienteDebidoProceso.APERTURA_NOTIFICADA) {
            throw new ConflictoNegocioException("El expediente no se encuentra en término abierto para radicar descargos");
        }

        if (request.contenidoDescargos() == null || request.contenidoDescargos().trim().isEmpty()) {
            throw new ValidacionException("El contenido de los descargos o versión libre no puede estar vacío");
        }

        Usuario presentador = usuarioRepository.findById(presentadoPorUsuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario presentador no encontrado con ID: " + presentadoPorUsuarioId));

        DescargoDebidoProceso descargo = new DescargoDebidoProceso();
        descargo.setExpediente(expediente);
        descargo.setPresentadoPorUsuario(presentador);
        descargo.setContenidoDescargos(request.contenidoDescargos().trim());
        descargo.setPruebasAportadas(request.pruebasAportadas());
        descargo.setEsVersionLibreAudiencia(request.esVersionLibreAudiencia() != null && request.esVersionLibreAudiencia());
        descargo.setParticipoPersonero(request.participoPersonero() != null && request.participoPersonero());

        DescargoDebidoProceso guardado = descargoRepository.save(descargo);

        // Habilita transición hacia UC-24
        expediente.setEstadoExpediente(EstadoExpedienteDebidoProceso.DESCARGOS_RECIBIDOS_LISTO_PARA_DECISION);
        expedienteRepository.save(expediente);

        if (auditoriaGlobalService != null) {
            auditoriaGlobalService.registrarEvento(
                    "RADICACION_DESCARGOS_DEBIDO_PROCESO",
                    "DescargoDebidoProceso",
                    guardado.getId().toString(),
                    "Estado: CARGOS_NOTIFICADOS_EN_TERMINO_DESCARGOS",
                    "Estado: DESCARGOS_RECIBIDOS_LISTO_PARA_DECISION",
                    "Garantía de defensa ejercida (participación Personero: " + descargo.getParticipoPersonero() + ")",
                    "EXITOSO"
            );
        }

        return new DescargoDebidoProcesoResponse(
                guardadaId(guardado),
                expediente.getId(),
                presentador.getId(),
                presentador.getUsername(),
                guardado.getContenidoDescargos(),
                guardado.getPruebasAportadas(),
                guardado.getFechaRadicacion(),
                guardado.getEsVersionLibreAudiencia(),
                guardado.getParticipoPersonero()
        );
    }

    public ExpedienteDebidoProcesoResponse certificarVencimientoDescargos(Long expedienteId, String constancia, Long instructorUsuarioId) {
        ExpedienteDebidoProceso expediente = expedienteRepository.findById(expedienteId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Expediente no encontrado con ID: " + expedienteId));

        expediente.setEstadoExpediente(EstadoExpedienteDebidoProceso.PLAZO_DESCARGOS_VENCIDO_CERTIFICADO);
        expediente.setConstanciaVencimientoDescargos(constancia != null ? constancia : "Plazo legal en días hábiles lectivos vencido sin radicación de descargos");

        ExpedienteDebidoProceso actualizado = expedienteRepository.save(expediente);

        if (auditoriaGlobalService != null) {
            auditoriaGlobalService.registrarEvento(
                    "VENCIMIENTO_TERMINO_DESCARGOS",
                    "ExpedienteDebidoProceso",
                    expedienteId.toString(),
                    "Estado: CARGOS_NOTIFICADOS",
                    "Estado: PLAZO_DESCARGOS_VENCIDO_CERTIFICADO",
                    expediente.getConstanciaVencimientoDescargos(),
                    "EXITOSO"
            );
        }

        return toResponse(actualizado);
    }

    public RecursoImpugnacionResponse radicarRecurso(Long medidaSancionId, RadicarRecursoRequest request, Long recurrenteUsuarioId) {
        MedidaSancionConvivencia medida = medidaRepository.findById(medidaSancionId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Medida o sanción no encontrada con ID: " + medidaSancionId));

        if (medida.getEstadoDecision() != EstadoDecisionSancion.NOTIFICADA_EN_TERMINO_RECURSOS &&
            medida.getEstadoDecision() != EstadoDecisionSancion.EMITIDA_PENDIENTE_NOTIFICACION) {
            throw new ConflictoNegocioException("La decisión no se encuentra en término hábil para radicar recursos");
        }

        Usuario recurrente = usuarioRepository.findById(recurrenteUsuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario recurrente no encontrado con ID: " + recurrenteUsuarioId));

        RecursoImpugnacion recurso = new RecursoImpugnacion();
        recurso.setMedidaSancion(medida);
        recurso.setTipoRecurso(request.tipoRecurso());
        recurso.setRecurrenteUsuario(recurrente);
        recurso.setSustentacionRecurso(request.sustentacionRecurso());
        recurso.setPruebasAdjuntas(request.pruebasAdjuntas());
        recurso.setEstadoRecurso(EstadoRecursoImpugnacion.RADICADO_EN_TRAMITE);

        RecursoImpugnacion guardado = recursoRepository.save(recurso);

        // A2: Efecto suspensivo de recursos sobre sanciones graves (RN-CON-08)
        medida.setEstadoDecision(EstadoDecisionSancion.EN_RECURSO_EFECTO_SUSPENSIVO);
        medidaRepository.save(medida);

        ExpedienteDebidoProceso expediente = medida.getExpediente();
        expediente.setEstadoExpediente(EstadoExpedienteDebidoProceso.EN_RECURSO_EFECTO_SUSPENSIVO);
        expedienteRepository.save(expediente);

        if (auditoriaGlobalService != null) {
            auditoriaGlobalService.registrarEvento(
                    "RADICACION_RECURSO_IMPUGNACION",
                    "RecursoImpugnacion",
                    guardado.getId().toString(),
                    "Estado medida: NOTIFICADA",
                    "Estado medida: EN_RECURSO_EFECTO_SUSPENSIVO",
                    "Recurso " + request.tipoRecurso() + " con efecto suspensivo (RN-CON-08)",
                    "EXITOSO"
            );
        }

        return toRecursoResponse(guardado);
    }

    public RecursoImpugnacionResponse resolverRecurso(Long recursoId, ResolverRecursoRequest request, Long resolutorUsuarioId) {
        RecursoImpugnacion recurso = recursoRepository.findById(recursoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Recurso no encontrado con ID: " + recursoId));

        MedidaSancionConvivencia medida = recurso.getMedidaSancion();
        Usuario resolutor = usuarioRepository.findById(resolutorUsuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario resolutor no encontrado con ID: " + resolutorUsuarioId));

        // Invariante E3: Segregación estricta de instancias en apelación (RN-CON-08, E3)
        if (recurso.getTipoRecurso() == TipoRecursoImpugnacion.APELACION ||
            recurso.getTipoRecurso() == TipoRecursoImpugnacion.REPOSICION_Y_EN_SUBSIDIO_APELACION) {
            if (resolutorUsuarioId.equals(medida.getAutoridadFirmante().getId())) {
                throw new ConflictoNegocioException(
                        "La misma autoridad que impuso la sanción en primera instancia no puede resolver la apelación (E3, segregación de instancias)"
                );
            }
        }

        recurso.setResolutorUsuario(resolutor);
        recurso.setResolutorRol(resolutor.getRol().name());
        recurso.setActaConsejoDirectivoResolucion(request.actaConsejoDirectivo());
        recurso.setMotivoResolucionRecurso(request.motivoResolucion());
        recurso.setFechaResolucion(LocalDateTime.now());

        String sentido = request.sentidoFallo().toUpperCase();
        if ("REVOCAR_ARCHIVAR".equals(sentido)) {
            recurso.setEstadoRecurso(EstadoRecursoImpugnacion.RESUELTO_REVOCADA_MEDIDA);
            medida.setEstadoDecision(EstadoDecisionSancion.REVOCADA_POR_RECURSO);
            medida.getExpediente().setEstadoExpediente(EstadoExpedienteDebidoProceso.ARCHIVADO_REVOCADO);
        } else if ("MODIFICAR_ATENUAR_MEDIDA".equals(sentido)) {
            recurso.setEstadoRecurso(EstadoRecursoImpugnacion.RESUELTO_MODIFICADA_MEDIDA);
            medida.setEstadoDecision(EstadoDecisionSancion.EN_FIRME);
            medida.setFechaFirmeza(LocalDateTime.now());
            medida.getExpediente().setEstadoExpediente(EstadoExpedienteDebidoProceso.FALLO_SEGUNDA_INSTANCIA_EMITIDO);
        } else {
            // CONFIRMAR_DECISION
            recurso.setEstadoRecurso(EstadoRecursoImpugnacion.RESUELTO_CONFIRMADA_MEDIDA);
            medida.setEstadoDecision(EstadoDecisionSancion.EN_FIRME);
            medida.setFechaFirmeza(LocalDateTime.now());
            medida.getExpediente().setEstadoExpediente(EstadoExpedienteDebidoProceso.CERRADO_EN_FIRME);
        }

        RecursoImpugnacion guardado = recursoRepository.save(recurso);
        medidaRepository.save(medida);
        expedienteRepository.save(medida.getExpediente());

        if (auditoriaGlobalService != null) {
            auditoriaGlobalService.registrarEvento(
                    "RESOLUCION_RECURSO_IMPUGNACION",
                    "RecursoImpugnacion",
                    guardado.getId().toString(),
                    "Estado: RADICADO_EN_TRAMITE",
                    "Estado: " + guardado.getEstadoRecurso() + ", Decisión: " + medida.getEstadoDecision(),
                    "Fallo motivado de recurso por " + resolutor.getUsername(),
                    "EXITOSO"
            );
        }

        return toRecursoResponse(guardado);
    }

    @Transactional(readOnly = true)
    public ExpedienteDebidoProcesoResponse obtenerPorId(Long expedienteId) {
        ExpedienteDebidoProceso exp = expedienteRepository.findById(expedienteId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Expediente no encontrado con ID: " + expedienteId));
        return toResponse(exp);
    }

    @Transactional(readOnly = true)
    public List<ExpedienteDebidoProcesoResponse> listarPorEstudiante(Long estudianteId) {
        return expedienteRepository.findByEstudianteId(estudianteId).stream()
                .map(this::toResponse)
                .toList();
    }

    private Long guardadaId(DescargoDebidoProceso d) {
        return d.getId();
    }

    public LocalDate calcularDiasHabilesLectivos(Long anioLectivoId, LocalDate inicio, int diasHabiles) {
        LocalDate actual = inicio;
        int habilesContados = 0;
        while (habilesContados < diasHabiles) {
            actual = actual.plusDays(1);
            boolean esFinDeSemana = actual.getDayOfWeek() == DayOfWeek.SATURDAY || actual.getDayOfWeek() == DayOfWeek.SUNDAY;
            boolean esNoLectivo = diaNoLectivoRepository.esDiaNoLectivo(anioLectivoId, actual);
            if (!esFinDeSemana && !esNoLectivo) {
                habilesContados++;
            }
        }
        return actual;
    }

    public ExpedienteDebidoProcesoResponse toResponse(ExpedienteDebidoProceso e) {
        List<DescargoDebidoProcesoResponse> descargos = descargoRepository.findByExpedienteIdOrderByFechaRadicacionAsc(e.getId()).stream()
                .map(d -> new DescargoDebidoProcesoResponse(
                        d.getId(),
                        e.getId(),
                        d.getPresentadoPorUsuario().getId(),
                        d.getPresentadoPorUsuario().getUsername(),
                        d.getContenidoDescargos(),
                        d.getPruebasAportadas(),
                        d.getFechaRadicacion(),
                        d.getEsVersionLibreAudiencia(),
                        d.getParticipoPersonero()
                ))
                .toList();

        return new ExpedienteDebidoProcesoResponse(
                e.getId(),
                e.getNumeroExpediente(),
                e.getCasoConvivencia().getId(),
                e.getCasoConvivencia().getCodigoExpediente(),
                e.getEstudiante().getId(),
                e.getEstudiante().getNombres() + " " + e.getEstudiante().getApellidos(),
                e.getEstadoExpediente(),
                e.getInstructorUsuario().getId(),
                e.getInstructorUsuario().getUsername(),
                e.getAutoAperturaHechos(),
                e.getNormasManualInfringidas(),
                e.getPruebasTrasladadasAnonimizadas(),
                e.getFechaNotificacionCargos(),
                e.getDiasHabilesDescargos(),
                e.getFechaLimiteDescargos(),
                e.getConstanciaVencimientoDescargos(),
                e.getAcompanamientoPersonero(),
                e.getConceptoOrientacion(),
                descargos
        );
    }

    public RecursoImpugnacionResponse toRecursoResponse(RecursoImpugnacion r) {
        return new RecursoImpugnacionResponse(
                r.getId(),
                r.getMedidaSancion().getId(),
                r.getTipoRecurso(),
                r.getRecurrenteUsuario().getId(),
                r.getRecurrenteUsuario().getUsername(),
                r.getSustentacionRecurso(),
                r.getPruebasAdjuntas(),
                r.getFechaRadicacion(),
                r.getEstadoRecurso(),
                r.getResolutorUsuario() != null ? r.getResolutorUsuario().getId() : null,
                r.getResolutorUsuario() != null ? r.getResolutorUsuario().getUsername() : null,
                r.getResolutorRol(),
                r.getActaConsejoDirectivoResolucion(),
                r.getMotivoResolucionRecurso(),
                r.getFechaResolucion()
        );
    }
}
