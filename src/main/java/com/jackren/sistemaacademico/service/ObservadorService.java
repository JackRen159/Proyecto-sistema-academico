package com.jackren.sistemaacademico.service;

import com.jackren.sistemaacademico.dto.*;
import com.jackren.sistemaacademico.entity.*;
import com.jackren.sistemaacademico.exception.ConflictoNegocioException;
import com.jackren.sistemaacademico.exception.RecursoNoEncontradoException;
import com.jackren.sistemaacademico.exception.ValidacionException;
import com.jackren.sistemaacademico.repository.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Service
@Transactional
public class ObservadorService {

    private final AnotacionObservadorRepository anotacionRepository;
    private final AnotacionObservadorAdendaRepository adendaRepository;
    private final EstudianteRepository estudianteRepository;
    private final PeriodoAcademicoRepository periodoAcademicoRepository;
    private final UsuarioRepository usuarioRepository;
    private final MatriculaRepository matriculaRepository;
    private final AsignacionAcademicaRepository asignacionAcademicaRepository;
    private final EstudianteAcudienteRepository estudianteAcudienteRepository;
    private final AcudienteRepository acudienteRepository;
    private final RutaAtencionService rutaAtencionService;
    private final AuditoriaGlobalService auditoriaGlobalService;

    public ObservadorService(
            AnotacionObservadorRepository anotacionRepository,
            AnotacionObservadorAdendaRepository adendaRepository,
            EstudianteRepository estudianteRepository,
            PeriodoAcademicoRepository periodoAcademicoRepository,
            UsuarioRepository usuarioRepository,
            MatriculaRepository matriculaRepository,
            AsignacionAcademicaRepository asignacionAcademicaRepository,
            EstudianteAcudienteRepository estudianteAcudienteRepository,
            AcudienteRepository acudienteRepository,
            RutaAtencionService rutaAtencionService,
            AuditoriaGlobalService auditoriaGlobalService) {
        this.anotacionRepository = anotacionRepository;
        this.adendaRepository = adendaRepository;
        this.estudianteRepository = estudianteRepository;
        this.periodoAcademicoRepository = periodoAcademicoRepository;
        this.usuarioRepository = usuarioRepository;
        this.matriculaRepository = matriculaRepository;
        this.asignacionAcademicaRepository = asignacionAcademicaRepository;
        this.estudianteAcudienteRepository = estudianteAcudienteRepository;
        this.acudienteRepository = acudienteRepository;
        this.rutaAtencionService = rutaAtencionService;
        this.auditoriaGlobalService = auditoriaGlobalService;
    }

    public AnotacionObservadorResponse registrarAnotacion(AnotacionObservadorRequest request, Long autorUsuarioId) {
        Estudiante estudiante = estudianteRepository.findById(request.estudianteId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Estudiante no encontrado con ID: " + request.estudianteId()));

        Usuario autor = usuarioRepository.findById(autorUsuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario autor no encontrado con ID: " + autorUsuarioId));

        // 1. Validación de precondición contextual de autorización docente (E2, A4, RN-CON-02)
        boolean esZonaComun = request.esZonaComun() != null && request.esZonaComun();
        if (!esZonaComun) {
            Rol rolAutor = autor.getRol();
            boolean esDirectivo = rolAutor == Rol.RECTOR ||
                    rolAutor == Rol.COORDINADOR ||
                    rolAutor == Rol.COORDINADOR_CONVIVENCIA ||
                    rolAutor == Rol.COORDINADOR_ACADEMICO ||
                    rolAutor == Rol.ORIENTADOR_ESCOLAR;

            if (!esDirectivo) {
                // Verificar si tiene asignación docente activa en el grupo del estudiante
                List<Matricula> matriculas = matriculaRepository.findByEstudianteIdOrderByFechaMatriculaDesc(estudiante.getId());
                boolean tieneAsignacionEnGrupo = false;
                for (Matricula m : matriculas) {
                    if (m.getEstado() == EstadoMatricula.MATRICULADA || m.getEstado() == EstadoMatricula.MATRICULADO_ACTIVO) {
                        if (asignacionAcademicaRepository.existsByProfesorUsuarioIdAndGrupoIdAndActivoTrue(autorUsuarioId, m.getGrupo().getId())) {
                            tieneAsignacionEnGrupo = true;
                            break;
                        }
                    }
                }
                if (!tieneAsignacionEnGrupo) {
                    throw new AccessDeniedException(
                            "No tiene permiso contextual sobre el estudiante: falta asignación académica en su curso (E2)"
                    );
                }
            }
        }

        // 2. Validación de descripción fáctica objetiva (E1)
        if (request.descripcionFactica() == null || request.descripcionFactica().trim().length() < 10) {
            throw new ValidacionException("La descripción fáctica debe ser objetiva y contener al menos 10 caracteres (E1)");
        }

        // 3. Bloqueo de datos clínicos o sensibles indebidos en observador general (E3, RN-CON-03)
        String textoLower = request.descripcionFactica().toLowerCase(Locale.ROOT);
        if (textoLower.contains("diagnóstico psiquiátrico") ||
            textoLower.contains("historia clínica reservada") ||
            textoLower.contains("enfermedad de transmisión sexual") ||
            textoLower.contains("tratamiento psiquiátrico reservado")) {
            throw new ValidacionException(
                    "Intento de incluir datos clínicos o sensibles reservados en el observador general. Derive a Orientación Escolar o Ruta de Atención (E3, Ley 1581 de 2012)"
            );
        }

        PeriodoAcademico periodo = null;
        if (request.periodoAcademicoId() != null) {
            periodo = periodoAcademicoRepository.findById(request.periodoAcademicoId()).orElse(null);
        }

        AnotacionObservador anotacion = new AnotacionObservador();
        anotacion.setEstudiante(estudiante);
        anotacion.setPeriodoAcademico(periodo);
        anotacion.setAutorUsuario(autor);
        anotacion.setTipoAnotacion(request.tipoAnotacion());
        anotacion.setFechaHecho(request.fechaHecho());
        anotacion.setHoraHecho(request.horaHecho());
        anotacion.setLugarHecho(request.lugarHecho());
        anotacion.setDescripcionFactica(request.descripcionFactica());
        anotacion.setAccionesPedagogicas(request.accionesPedagogicas());
        anotacion.setEsZonaComun(esZonaComun);

        // A1: Derivación hacia la Ruta de Atención Integral ante situaciones de convivencia (UC-23)
        boolean requiereRuta = (request.requiereActivacionRuta() != null && request.requiereActivacionRuta())
                || request.tipoAnotacion() == TipoAnotacionObservador.REPORTE_SITUACION_CONVIVENCIA;
        anotacion.setRequiereActivacionRuta(requiereRuta);

        if (requiereRuta && request.tipoSituacionSugerida() != null && periodo != null) {
            CasoConvivenciaRequest casoReq = new CasoConvivenciaRequest(
                    periodo.getAnioLectivo().getId(),
                    estudiante.getId(),
                    request.tipoSituacionSugerida(),
                    request.fechaHecho(),
                    request.lugarHecho(),
                    request.descripcionFactica(),
                    false,
                    false,
                    null,
                    false,
                    null,
                    false,
                    null,
                    null,
                    null
            );
            CasoConvivenciaResponse casoResp = rutaAtencionService.abrirCaso(casoReq, autorUsuarioId);
            CasoConvivencia casoEntity = new CasoConvivencia();
            casoEntity.setId(casoResp.id());
            anotacion.setCasoConvivencia(casoEntity);
        }

        anotacion.setAnotacionFirmada(true);

        AnotacionObservador guardada = anotacionRepository.save(anotacion);

        if (auditoriaGlobalService != null) {
            auditoriaGlobalService.registrarEvento(
                    "REGISTRO_ANOTACION_OBSERVADOR",
                    "AnotacionObservador",
                    guardada.getId().toString(),
                    "N/A",
                    "Tipo: " + guardada.getTipoAnotacion() + ", Estudiante: " + estudiante.getId(),
                    "Registro inmutable en observador escolar",
                    "EXITOSO"
            );
        }

        return toResponse(guardada);
    }

    public AnotacionObservadorResponse registrarVersionEstudiante(Long anotacionId, VersionEstudianteRequest request, Long estudianteUsuarioId) {
        AnotacionObservador anotacion = anotacionRepository.findById(anotacionId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Anotación no encontrada con ID: " + anotacionId));

        if (request.versionLibre() == null || request.versionLibre().trim().isEmpty()) {
            throw new ValidacionException("La versión libre o compromiso formativo no puede estar vacía (A2)");
        }

        anotacion.setVersionLibreEstudiante(request.versionLibre().trim());
        anotacion.setFechaVersionEstudiante(LocalDateTime.now());

        AnotacionObservador actualizada = anotacionRepository.save(anotacion);

        if (auditoriaGlobalService != null) {
            auditoriaGlobalService.registrarEvento(
                    "REGISTRO_VERSION_LIBRE_ESTUDIANTE",
                    "AnotacionObservador",
                    anotacionId.toString(),
                    "Versión previa: " + (anotacion.getVersionLibreEstudiante() != null ? "EXISTENTE" : "PENDIENTE"),
                    "Versión registrada",
                    "Ejercicio del derecho a ser escuchado en observador (Ley 1098 Art. 26)",
                    "EXITOSO"
            );
        }

        return toResponse(actualizada);
    }

    public AnotacionObservadorResponse registrarEnteradoAcudiente(Long anotacionId, Long acudienteUsuarioId) {
        AnotacionObservador anotacion = anotacionRepository.findById(anotacionId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Anotación no encontrada con ID: " + anotacionId));

        Usuario usuarioAcudiente = usuarioRepository.findById(acudienteUsuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario acudiente no encontrado con ID: " + acudienteUsuarioId));

        Acudiente acudiente = null;
        if (usuarioAcudiente.getPersonaId() != null) {
            acudiente = acudienteRepository.findById(usuarioAcudiente.getPersonaId()).orElse(null);
        }
        if (acudiente == null) {
            acudiente = acudienteRepository.findByDocumento(usuarioAcudiente.getUsername()).orElse(null);
        }
        if (acudiente == null) {
            throw new AccessDeniedException("El usuario no tiene perfil de acudiente registrado (E1)");
        }

        EstudianteAcudiente vinculo = estudianteAcudienteRepository.findByEstudianteAndAcudiente(anotacion.getEstudiante(), acudiente)
                .orElseThrow(() -> new AccessDeniedException("No existe vínculo legal registrado con el estudiante (E1)"));

        // Invariante E1: Bloqueo si registra BLOQUEADO_RESTRICCION_LEGAL o reserva contra acudiente
        if (vinculo.getEstadoVinculo() == EstadoVinculo.BLOQUEADO_RESTRICCION_LEGAL) {
            throw new AccessDeniedException("El vínculo con el estudiante se encuentra bloqueado por restricción legal (E1)");
        }

        if (anotacion.getCasoConvivencia() != null) {
            CasoConvivencia caso = anotacion.getCasoConvivencia();
            if (Boolean.TRUE.equals(caso.getReservaProteccionContraAcudiente()) &&
                caso.getAcudienteAgresor() != null &&
                caso.getAcudienteAgresor().getId().equals(acudiente.getId())) {
                throw new AccessDeniedException("Acceso bloqueado por reserva de protección contra acudiente agresor (UC-23 A4, E1)");
            }
        }

        anotacion.setEnteradoAcudiente(true);
        anotacion.setAcudienteUsuario(usuarioAcudiente);
        anotacion.setFechaEnteradoAcudiente(LocalDateTime.now());

        AnotacionObservador actualizada = anotacionRepository.save(anotacion);

        if (auditoriaGlobalService != null) {
            auditoriaGlobalService.registrarEvento(
                    "CONSTANCIA_ENTERADO_ACUDIENTE",
                    "AnotacionObservador",
                    anotacionId.toString(),
                    "Enterado: false",
                    "Enterado: true",
                    "Acuse de enterado de anotación por acudiente verificado",
                    "EXITOSO"
            );
        }

        return toResponse(actualizada);
    }

    public AdendaObservadorResponse agregarAdenda(Long anotacionId, AdendaObservadorRequest request, Long autorUsuarioId) {
        AnotacionObservador anotacion = anotacionRepository.findById(anotacionId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Anotación no encontrada con ID: " + anotacionId));

        Usuario autor = usuarioRepository.findById(autorUsuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario autor no encontrado con ID: " + autorUsuarioId));

        if (request.motivoAdenda() == null || request.motivoAdenda().trim().isEmpty()) {
            throw new ValidacionException("El motivo de la adenda es obligatorio para preservar la inmutabilidad (A3)");
        }
        if (request.contenidoAdenda() == null || request.contenidoAdenda().trim().isEmpty()) {
            throw new ValidacionException("El contenido de la adenda es obligatorio (A3)");
        }

        AnotacionObservadorAdenda adenda = new AnotacionObservadorAdenda();
        adenda.setAnotacion(anotacion);
        adenda.setAutorUsuario(autor);
        adenda.setMotivoAdenda(request.motivoAdenda().trim());
        adenda.setContenidoAdenda(request.contenidoAdenda().trim());

        AnotacionObservadorAdenda guardada = adendaRepository.save(adenda);

        if (auditoriaGlobalService != null) {
            auditoriaGlobalService.registrarEvento(
                    "REGISTRO_ADENDA_OBSERVADOR",
                    "AnotacionObservadorAdenda",
                    guardada.getId().toString(),
                    "N/A",
                    "Adenda sobre anotación: " + anotacionId,
                    "Motivo adenda: " + guardada.getMotivoAdenda(),
                    "EXITOSO"
            );
        }

        return new AdendaObservadorResponse(
                guardada.getId(),
                anotacion.getId(),
                autor.getId(),
                autor.getUsername(),
                guardada.getMotivoAdenda(),
                guardada.getContenidoAdenda(),
                guardada.getFechaAdenda()
        );
    }

    // Invariante E4: Prohibición de edición in-situ o borrado físico (RN-CON-01)
    public void bloquearEdicionOBorradoDirecto(Long anotacionId) {
        throw new ConflictoNegocioException(
                "Prohibido el borrado físico o modificación in-situ del observador. Toda aclaración debe realizarse mediante adenda inmutable vinculada (RN-CON-01, E4)"
        );
    }

    @Transactional(readOnly = true)
    public AnotacionObservadorResponse obtenerPorId(Long anotacionId) {
        AnotacionObservador anotacion = anotacionRepository.findById(anotacionId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Anotación no encontrada con ID: " + anotacionId));
        return toResponse(anotacion);
    }

    @Transactional(readOnly = true)
    public List<AnotacionObservadorResponse> listarPorEstudiante(Long estudianteId) {
        return anotacionRepository.findByEstudianteIdOrderByFechaRegistroDesc(estudianteId).stream()
                .map(this::toResponse)
                .toList();
    }

    public AnotacionObservadorResponse toResponse(AnotacionObservador a) {
        List<AdendaObservadorResponse> adendas = adendaRepository.findByAnotacionIdOrderByFechaAdendaAsc(a.getId()).stream()
                .map(ad -> new AdendaObservadorResponse(
                        ad.getId(),
                        a.getId(),
                        ad.getAutorUsuario().getId(),
                        ad.getAutorUsuario().getUsername(),
                        ad.getMotivoAdenda(),
                        ad.getContenidoAdenda(),
                        ad.getFechaAdenda()
                ))
                .toList();

        return new AnotacionObservadorResponse(
                a.getId(),
                a.getEstudiante().getId(),
                a.getEstudiante().getNombres() + " " + a.getEstudiante().getApellidos(),
                a.getPeriodoAcademico() != null ? a.getPeriodoAcademico().getId() : null,
                a.getAutorUsuario() != null ? a.getAutorUsuario().getId() : null,
                a.getAutorUsuario() != null ? a.getAutorUsuario().getUsername() : null,
                a.getTipoAnotacion(),
                a.getFechaHecho(),
                a.getHoraHecho(),
                a.getLugarHecho(),
                a.getDescripcionFactica(),
                a.getAccionesPedagogicas(),
                a.getEsZonaComun(),
                a.getRequiereActivacionRuta(),
                a.getCasoConvivencia() != null ? a.getCasoConvivencia().getId() : null,
                a.getVersionLibreEstudiante(),
                a.getFechaVersionEstudiante(),
                a.getEnteradoAcudiente(),
                a.getFechaEnteradoAcudiente(),
                a.getAnotacionFirmada(),
                a.getFechaRegistro(),
                adendas
        );
    }
}
