package com.jackren.sistemaacademico.service;

import com.jackren.sistemaacademico.dto.*;
import com.jackren.sistemaacademico.entity.*;
import com.jackren.sistemaacademico.exception.ConflictoNegocioException;
import com.jackren.sistemaacademico.exception.RecursoNoEncontradoException;
import com.jackren.sistemaacademico.exception.ValidacionException;
import com.jackren.sistemaacademico.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class RutaAtencionService {

    private final CasoConvivenciaRepository casoRepository;
    private final ActuacionRutaAtencionRepository actuacionRepository;
    private final AnioLectivoRepository anioLectivoRepository;
    private final EstudianteRepository estudianteRepository;
    private final UsuarioRepository usuarioRepository;
    private final AcudienteRepository acudienteRepository;
    private final AuditoriaGlobalService auditoriaGlobalService;

    public RutaAtencionService(
            CasoConvivenciaRepository casoRepository,
            ActuacionRutaAtencionRepository actuacionRepository,
            AnioLectivoRepository anioLectivoRepository,
            EstudianteRepository estudianteRepository,
            UsuarioRepository usuarioRepository,
            AcudienteRepository acudienteRepository,
            AuditoriaGlobalService auditoriaGlobalService) {
        this.casoRepository = casoRepository;
        this.actuacionRepository = actuacionRepository;
        this.anioLectivoRepository = anioLectivoRepository;
        this.estudianteRepository = estudianteRepository;
        this.usuarioRepository = usuarioRepository;
        this.acudienteRepository = acudienteRepository;
        this.auditoriaGlobalService = auditoriaGlobalService;
    }

    public CasoConvivenciaResponse abrirCaso(CasoConvivenciaRequest request, Long reportanteUsuarioId) {
        AnioLectivo anioLectivo = anioLectivoRepository.findById(request.anioLectivoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Año lectivo no encontrado con ID: " + request.anioLectivoId()));

        Estudiante estudiante = estudianteRepository.findById(request.estudianteImplicadoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Estudiante no encontrado con ID: " + request.estudianteImplicadoId()));

        Usuario reportante = null;
        if (reportanteUsuarioId != null) {
            reportante = usuarioRepository.findById(reportanteUsuarioId).orElse(null);
        }

        CasoConvivencia caso = new CasoConvivencia();
        String codigoGenerado = "EXP-CONV-" + anioLectivo.getAnio() + "-" + String.format("%04d", casoRepository.count() + 1);
        caso.setCodigoExpediente(codigoGenerado);
        caso.setAnioLectivo(anioLectivo);
        caso.setEstudianteImplicado(estudiante);
        caso.setTipoSituacion(request.tipoSituacion());
        caso.setFechaHechos(request.fechaHechos());
        caso.setLugarHechos(request.lugarHechos());
        caso.setDescripcionSituacion(request.descripcionSituacion());
        caso.setReportanteUsuario(reportante);
        caso.setEsReporteConfidencial(request.esReporteConfidencial() != null && request.esReporteConfidencial());

        // A1: Atención médica urgente o apertura preliminar sin trabas burocráticas (RN-CON-05)
        if (request.atencionMedicaUrgente() != null && request.atencionMedicaUrgente()) {
            caso.setAtencionMedicaUrgente(true);
            caso.setDetalleAtencionMedica(request.detalleAtencionMedica());
            caso.setEstadoCaso(EstadoCasoConvivencia.ABIERTO_PRELIMINAR_URGENTE);
        } else {
            caso.setEstadoCaso(EstadoCasoConvivencia.EN_PROTOCOLO_ATENCION);
        }

        // A4: Reserva de protección contra acudiente agresor (RN-CON-06)
        if (request.reservaProteccionContraAcudiente() != null && request.reservaProteccionContraAcudiente()) {
            caso.setReservaProteccionContraAcudiente(true);
            if (request.acudienteAgresorId() != null) {
                Acudiente acudiente = acudienteRepository.findById(request.acudienteAgresorId())
                        .orElseThrow(() -> new RecursoNoEncontradoException("Acudiente no encontrado con ID: " + request.acudienteAgresorId()));
                caso.setAcudienteAgresor(acudiente);
            }
        }

        // A3: Enlace con autoridades externas en situaciones Tipo III (RN-CON-05)
        if (request.remitidoAutoridadExterna() != null && request.remitidoAutoridadExterna()) {
            caso.setRemitidoAutoridadExterna(true);
            caso.setAutoridadExternaDetalle(request.autoridadExternaDetalle());
            caso.setFechaRemisionExterna(request.fechaRemisionExterna());
            caso.setRadicadoRemisionExterna(request.radicadoRemisionExterna());
        }

        CasoConvivencia guardado = casoRepository.save(caso);

        if (auditoriaGlobalService != null) {
            auditoriaGlobalService.registrarEvento(
                    "APERTURA_CASO_CONVIVENCIA",
                    "CasoConvivencia",
                    guardado.getId().toString(),
                    "Estado: INEXISTENTE",
                    "Estado: " + guardado.getEstadoCaso() + ", Tipo: " + guardado.getTipoSituacion(),
                    "Apertura de caso de convivencia " + guardado.getCodigoExpediente(),
                    "EXITOSO"
            );
        }

        return toResponse(guardado);
    }

    public CasoConvivenciaResponse reclasificarCaso(Long casoId, ReclasificarCasoRequest request, Long usuarioId) {
        CasoConvivencia caso = casoRepository.findById(casoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Caso de convivencia no encontrado con ID: " + casoId));

        if (caso.getEstadoCaso() == EstadoCasoConvivencia.CERRADO_SUPERADO) {
            throw new ConflictoNegocioException("No se puede reclasificar un caso de convivencia ya cerrado formalmente");
        }

        TipoSituacionConvivencia anterior = caso.getTipoSituacion();
        caso.setTipoSituacion(request.nuevaSituacion());

        CasoConvivencia actualizado = casoRepository.save(caso);

        if (auditoriaGlobalService != null) {
            auditoriaGlobalService.registrarEvento(
                    "RECLASIFICACION_CASO_CONVIVENCIA",
                    "CasoConvivencia",
                    casoId.toString(),
                    "Tipo: " + anterior,
                    "Tipo: " + request.nuevaSituacion(),
                    request.motivoReclasificacion(),
                    "EXITOSO"
            );
        }

        return toResponse(actualizado);
    }

    public ActuacionRutaAtencionResponse registrarActuacion(Long casoId, ActuacionRutaAtencionRequest request, Long responsableUsuarioId) {
        CasoConvivencia caso = casoRepository.findById(casoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Caso de convivencia no encontrado con ID: " + casoId));

        // Invariante E3: Prohibición legal absoluta de conciliación o careo en Tipo III (Decreto 1965 Art. 44)
        if (caso.getTipoSituacion() == TipoSituacionConvivencia.TIPO_III
                && request.tipoActuacion() == TipoActuacionRutaAtencion.MEDIACION_CONCILIACION) {
            throw new ConflictoNegocioException(
                    "Prohibición legal absoluta de conciliación o careo entre agresor y víctima en situaciones Tipo III (Decreto 1965 de 2013 Art. 44, E3)"
            );
        }

        Usuario responsable = usuarioRepository.findById(responsableUsuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario responsable no encontrado con ID: " + responsableUsuarioId));

        ActuacionRutaAtencion actuacion = new ActuacionRutaAtencion();
        actuacion.setCasoConvivencia(caso);
        actuacion.setTipoActuacion(request.tipoActuacion());
        actuacion.setDescripcionActuacion(request.descripcionActuacion());
        actuacion.setResponsableUsuario(responsable);
        actuacion.setFechaVencimientoSeguimiento(request.fechaVencimientoSeguimiento());
        actuacion.setCompromisosAcordados(request.compromisosAcordados());

        ActuacionRutaAtencion guardada = actuacionRepository.save(actuacion);

        if (auditoriaGlobalService != null) {
            auditoriaGlobalService.registrarEvento(
                    "REGISTRO_ACTUACION_RUTA_ATENCION",
                    "ActuacionRutaAtencion",
                    guardada.getId().toString(),
                    "N/A",
                    "Tipo: " + guardada.getTipoActuacion(),
                    "Registro de actuación en caso " + caso.getCodigoExpediente(),
                    "EXITOSO"
            );
        }

        return new ActuacionRutaAtencionResponse(
                guardada.getId(),
                caso.getId(),
                guardada.getTipoActuacion(),
                guardada.getDescripcionActuacion(),
                responsable.getId(),
                responsable.getUsername(),
                guardada.getFechaActuacion(),
                guardada.getFechaVencimientoSeguimiento(),
                guardada.getCompromisosAcordados()
        );
    }

    public CasoConvivenciaResponse cerrarCaso(Long casoId, String motivoCierre, String codigoSiuce, Long usuarioId) {
        CasoConvivencia caso = casoRepository.findById(casoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Caso de convivencia no encontrado con ID: " + casoId));

        // Invariante E1: Prohibición de cerrar casos Tipo II o III sin código SIUCE ni actuaciones
        if (caso.getTipoSituacion() == TipoSituacionConvivencia.TIPO_II || caso.getTipoSituacion() == TipoSituacionConvivencia.TIPO_III) {
            if (codigoSiuce == null || codigoSiuce.trim().isEmpty()) {
                throw new ConflictoNegocioException(
                        "Prohibido cerrar un caso de convivencia Tipo II o Tipo III sin constancia o código de reporte al SIUCE (Ley 1620 Art. 28, E1)"
                );
            }
            long actuacionesCount = actuacionRepository.findByCasoConvivenciaIdOrderByFechaActuacionAsc(casoId).size();
            if (actuacionesCount == 0) {
                throw new ConflictoNegocioException(
                        "No se puede cerrar un caso Tipo II o Tipo III sin registrar las actuaciones de protección y seguimiento de la Ruta Integral (E4)"
                );
            }
            caso.setCodigoSiuce(codigoSiuce.trim());
        }

        caso.setEstadoCaso(EstadoCasoConvivencia.CERRADO_SUPERADO);
        caso.setFechaCierre(LocalDateTime.now());
        caso.setMotivoCierre(motivoCierre != null ? motivoCierre : "Caso de convivencia cerrado tras verificación de superación y no repetición");

        CasoConvivencia actualizado = casoRepository.save(caso);

        if (auditoriaGlobalService != null) {
            auditoriaGlobalService.registrarEvento(
                    "CIERRE_CASO_CONVIVENCIA",
                    "CasoConvivencia",
                    casoId.toString(),
                    "Estado: EN_PROTOCOLO",
                    "Estado: CERRADO_SUPERADO",
                    caso.getMotivoCierre(),
                    "EXITOSO"
            );
        }

        return toResponse(actualizado);
    }

    @Transactional(readOnly = true)
    public CasoConvivenciaResponse obtenerPorId(Long casoId) {
        CasoConvivencia caso = casoRepository.findById(casoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Caso de convivencia no encontrado con ID: " + casoId));
        return toResponse(caso);
    }

    @Transactional(readOnly = true)
    public List<CasoConvivenciaResponse> listarPorEstudiante(Long estudianteId) {
        return casoRepository.findByEstudianteImplicadoId(estudianteId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ActuacionRutaAtencionResponse> listarActuacionesPorCaso(Long casoId) {
        return actuacionRepository.findByCasoConvivenciaIdOrderByFechaActuacionAsc(casoId).stream()
                .map(a -> new ActuacionRutaAtencionResponse(
                        a.getId(),
                        a.getCasoConvivencia().getId(),
                        a.getTipoActuacion(),
                        a.getDescripcionActuacion(),
                        a.getResponsableUsuario().getId(),
                        a.getResponsableUsuario().getUsername(),
                        a.getFechaActuacion(),
                        a.getFechaVencimientoSeguimiento(),
                        a.getCompromisosAcordados()
                ))
                .toList();
    }

    public CasoConvivenciaResponse toResponse(CasoConvivencia c) {
        return new CasoConvivenciaResponse(
                c.getId(),
                c.getCodigoExpediente(),
                c.getAnioLectivo().getId(),
                c.getEstudianteImplicado().getId(),
                c.getEstudianteImplicado().getNombres() + " " + c.getEstudianteImplicado().getApellidos(),
                c.getTipoSituacion(),
                c.getEstadoCaso(),
                c.getFechaHechos(),
                c.getLugarHechos(),
                c.getDescripcionSituacion(),
                c.getReportanteUsuario() != null ? c.getReportanteUsuario().getId() : null,
                c.getReportanteUsuario() != null ? c.getReportanteUsuario().getUsername() : "Anónimo / Sistema",
                c.getEsReporteConfidencial(),
                c.getAtencionMedicaUrgente(),
                c.getDetalleAtencionMedica(),
                c.getReservaProteccionContraAcudiente(),
                c.getAcudienteAgresor() != null ? c.getAcudienteAgresor().getId() : null,
                c.getCodigoSiuce(),
                c.getRemitidoAutoridadExterna(),
                c.getAutoridadExternaDetalle(),
                c.getFechaRemisionExterna(),
                c.getRadicadoRemisionExterna(),
                c.getFechaApertura(),
                c.getFechaCierre(),
                c.getMotivoCierre()
        );
    }
}
