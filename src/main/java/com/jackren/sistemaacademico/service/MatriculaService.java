package com.jackren.sistemaacademico.service;

import com.jackren.sistemaacademico.dto.*;
import com.jackren.sistemaacademico.entity.*;
import com.jackren.sistemaacademico.exception.ConflictoNegocioException;
import com.jackren.sistemaacademico.exception.RecursoNoEncontradoException;
import com.jackren.sistemaacademico.mapper.MatriculaMapper;
import com.jackren.sistemaacademico.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MatriculaService {

    private final MatriculaRepository matriculaRepository;
    private final HistorialTrasladoMatriculaRepository historialTrasladoRepository;
    private final HistorialNovedadMatriculaRepository historialNovedadRepository;
    private final EstudianteRepository estudianteRepository;
    private final AcudienteRepository acudienteRepository;
    private final EstudianteAcudienteRepository estudianteAcudienteRepository;
    private final GrupoRepository grupoRepository;
    private final UsuarioRepository usuarioRepository;
    private final AsignacionAcademicaRepository asignacionAcademicaRepository;
    private final CalificacionActividadRepository calificacionActividadRepository;
    private final AuditoriaGlobalService auditoriaGlobalService;
    private final MatriculaMapper matriculaMapper;

    @Autowired
    public MatriculaService(MatriculaRepository matriculaRepository,
                            HistorialTrasladoMatriculaRepository historialTrasladoRepository,
                            HistorialNovedadMatriculaRepository historialNovedadRepository,
                            EstudianteRepository estudianteRepository,
                            AcudienteRepository acudienteRepository,
                            EstudianteAcudienteRepository estudianteAcudienteRepository,
                            GrupoRepository grupoRepository,
                            UsuarioRepository usuarioRepository,
                            AsignacionAcademicaRepository asignacionAcademicaRepository,
                            CalificacionActividadRepository calificacionActividadRepository,
                            AuditoriaGlobalService auditoriaGlobalService,
                            MatriculaMapper matriculaMapper) {
        this.matriculaRepository = matriculaRepository;
        this.historialTrasladoRepository = historialTrasladoRepository;
        this.historialNovedadRepository = historialNovedadRepository;
        this.estudianteRepository = estudianteRepository;
        this.acudienteRepository = acudienteRepository;
        this.estudianteAcudienteRepository = estudianteAcudienteRepository;
        this.grupoRepository = grupoRepository;
        this.usuarioRepository = usuarioRepository;
        this.asignacionAcademicaRepository = asignacionAcademicaRepository;
        this.calificacionActividadRepository = calificacionActividadRepository;
        this.auditoriaGlobalService = auditoriaGlobalService;
        this.matriculaMapper = matriculaMapper;
    }

    public MatriculaService(MatriculaRepository matriculaRepository,
                            HistorialTrasladoMatriculaRepository historialTrasladoRepository,
                            EstudianteRepository estudianteRepository,
                            GrupoRepository grupoRepository,
                            UsuarioRepository usuarioRepository,
                            MatriculaMapper matriculaMapper) {
        this(matriculaRepository, historialTrasladoRepository, null, estudianteRepository,
                null, null, grupoRepository, usuarioRepository, null, null, null, matriculaMapper);
    }

    public MatriculaResponse matricular(MatriculaRequest request) {
        Estudiante estudiante = estudianteRepository.findById(request.getEstudianteId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Estudiante no encontrado con ID " + request.getEstudianteId()));

        if (!Boolean.TRUE.equals(estudiante.getActivo())) {
            throw new ConflictoNegocioException("No se puede matricular un estudiante en estado inactivo");
        }

        // Validación de expediente en estado LISTO_PARA_MATRICULA (UC-13 E2)
        if (estudiante.getEstadoExpediente() != null
                && estudiante.getEstadoExpediente() != EstadoExpediente.LISTO_PARA_MATRICULA
                && estudiante.getEstadoExpediente() != EstadoExpediente.MATRICULADO) {
            throw new ConflictoNegocioException("El expediente del estudiante no se encuentra en estado LISTO_PARA_MATRICULA "
                    + "(estado actual: " + estudiante.getEstadoExpediente() + ") (UC-13 E2).");
        }

        // Validación de acudiente principal único sin restricciones legales (RN-EST-05, UC-13 E2, E3, E4)
        Acudiente acudientePrincipal = null;
        if (estudianteAcudienteRepository != null) {
            Optional<EstudianteAcudiente> vinculoPrincipalOpt = estudianteAcudienteRepository.findByEstudianteAndEsPrincipalTrue(estudiante);
            if (vinculoPrincipalOpt.isEmpty()) {
                throw new ConflictoNegocioException("El estudiante no cuenta con un acudiente principal activo y verificado en su expediente (UC-13 E2, E3, RN-EST-05).");
            }

            EstudianteAcudiente vinculoPrincipal = vinculoPrincipalOpt.get();
            if (vinculoPrincipal.getMotivoBloqueoLegal() != null || vinculoPrincipal.getEstadoVinculo() == EstadoVinculo.BLOQUEADO_RESTRICCION_LEGAL) {
                throw new ConflictoNegocioException("El acudiente principal registra una medida de restricción legal vigente y no puede formalizar la matrícula (UC-13 E4, RN-EST-02).");
            }
            acudientePrincipal = vinculoPrincipal.getAcudiente();
        } else if (request.getAcudientePrincipalId() != null && acudienteRepository != null) {
            acudientePrincipal = acudienteRepository.findById(request.getAcudientePrincipalId()).orElse(null);
        }

        Grupo grupo = grupoRepository.findByIdWithLock(request.getGrupoId())
                .or(() -> grupoRepository.findById(request.getGrupoId()))
                .orElseThrow(() -> new RecursoNoEncontradoException("Grupo no encontrado con ID " + request.getGrupoId()));

        if (!Boolean.TRUE.equals(grupo.getActivo())) {
            throw new ConflictoNegocioException("No se pueden realizar matrículas en un grupo inactivo");
        }

        AnioLectivo anioLectivo = grupo.getAnioLectivo();
        if (anioLectivo == null || anioLectivo.getEstado() == EstadoAnioLectivo.CERRADO || anioLectivo.getEstado() == EstadoAnioLectivo.CERRADO_HISTORICO) {
            throw new ConflictoNegocioException("No se pueden realizar matrículas en un año lectivo cerrado (UC-13 E7).");
        }

        if (matriculaRepository.existsByEstudianteIdAndAnioLectivoIdAndEstado(estudiante.getId(), anioLectivo.getId(), EstadoMatricula.MATRICULADA)) {
            throw new ConflictoNegocioException("El estudiante ya cuenta con una matrícula activa en el año lectivo " + anioLectivo.getAnio() + " (UC-13 E1, RN-MAT-01).");
        }

        long matriculadosActuales = matriculaRepository.countByGrupoIdAndEstado(grupo.getId(), EstadoMatricula.MATRICULADA);
        int cupoMaximo = grupo.getCupoMaximo() != null ? grupo.getCupoMaximo() : 35;
        if (matriculadosActuales >= cupoMaximo) {
            throw new ConflictoNegocioException("El grupo '" + grupo.getNombre() + "' ha alcanzado su cupo máximo de " + cupoMaximo + " estudiantes (UC-13 A1, RN-MAT-05).");
        }

        String numeroMatricula = generarNumeroMatricula(anioLectivo);
        Matricula matricula = matriculaMapper.toEntity(request, estudiante, grupo, numeroMatricula);
        if (acudientePrincipal != null) {
            matricula.setAcudientePrincipal(acudientePrincipal);
        }

        Matricula guardada = matriculaRepository.save(matricula);
        estudiante.setEstadoExpediente(EstadoExpediente.MATRICULADO);
        estudianteRepository.save(estudiante);

        if (auditoriaGlobalService != null) {
            auditoriaGlobalService.registrarEvento(
                    "CREACION_MATRICULA",
                    "Matricula",
                    guardada.getId().toString(),
                    null,
                    "Matrícula " + guardada.getNumeroMatricula() + " en grupo " + grupo.getNombre() + " (" + anioLectivo.getAnio() + ")",
                    "Formalización de matrícula de estudiante",
                    "EXITOSO"
            );
        }

        return matriculaMapper.toResponse(guardada);
    }

    public MatriculaResponse trasladarDeGrupo(Long matriculaId, TrasladoGrupoRequest request, Long usuarioId) {
        Matricula matricula = matriculaRepository.findById(matriculaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Matrícula no encontrada con ID " + matriculaId));

        if (matricula.getEstado() != EstadoMatricula.MATRICULADA && matricula.getEstado() != EstadoMatricula.MATRICULADO_ACTIVO) {
            throw new ConflictoNegocioException("Solo se pueden trasladar estudiantes con matrícula en estado MATRICULADA");
        }

        Grupo grupoOrigen = matricula.getGrupo();
        if (grupoOrigen.getId().equals(request.getNuevoGrupoId())) {
            throw new ConflictoNegocioException("El grupo destino debe ser diferente al grupo actual");
        }

        Grupo grupoDestino = grupoRepository.findByIdWithLock(request.getNuevoGrupoId())
                .or(() -> grupoRepository.findById(request.getNuevoGrupoId()))
                .orElseThrow(() -> new RecursoNoEncontradoException("Grupo destino no encontrado con ID " + request.getNuevoGrupoId()));

        if (!Boolean.TRUE.equals(grupoDestino.getActivo())) {
            throw new ConflictoNegocioException("No se puede trasladar a un grupo inactivo");
        }

        if (grupoDestino.getAnioLectivo() == null || grupoDestino.getAnioLectivo().getEstado() == EstadoAnioLectivo.CERRADO || grupoDestino.getAnioLectivo().getEstado() == EstadoAnioLectivo.CERRADO_HISTORICO) {
            throw new ConflictoNegocioException("No se pueden realizar traslados en un año lectivo cerrado");
        }

        if (!grupoDestino.getAnioLectivo().getId().equals(matricula.getAnioLectivo().getId())) {
            throw new ConflictoNegocioException("El grupo destino debe pertenecer al mismo año lectivo de la matrícula");
        }

        if (!grupoDestino.getGrado().getId().equals(grupoOrigen.getGrado().getId())) {
            throw new ConflictoNegocioException("El traslado interno de grupo solo está permitido entre cursos del mismo grado (" +
                    grupoOrigen.getGrado().getNombre() + ") (RN-MAT-06). Para cambio de grado aplique Promoción Anticipada.");
        }

        long matriculadosDestino = matriculaRepository.countByGrupoIdAndEstado(grupoDestino.getId(), EstadoMatricula.MATRICULADA);
        int cupoMaximoDestino = grupoDestino.getCupoMaximo() != null ? grupoDestino.getCupoMaximo() : 35;
        if (matriculadosDestino >= cupoMaximoDestino) {
            throw new ConflictoNegocioException("El grupo destino '" + grupoDestino.getNombre() + "' ha alcanzado su cupo máximo de " +
                    cupoMaximoDestino + " estudiantes (UC-PROV-09 E1).");
        }

        Usuario usuario = null;
        if (usuarioId != null && usuarioRepository != null) {
            usuario = usuarioRepository.findById(usuarioId).orElse(null);
        }

        HistorialTrasladoMatricula historial = new HistorialTrasladoMatricula(
                matricula, grupoOrigen, grupoDestino, request.getMotivo(), usuario
        );
        historialTrasladoRepository.save(historial);

        if (historialNovedadRepository != null) {
            HistorialNovedadMatricula novedad = new HistorialNovedadMatricula(
                    matricula, TipoNovedadMatricula.CAMBIO_GRUPO, grupoOrigen, grupoDestino, request.getMotivo(), usuario
            );
            historialNovedadRepository.save(novedad);
        }

        matricula.setGrupo(grupoDestino);
        Matricula actualizada = matriculaRepository.save(matricula);

        if (auditoriaGlobalService != null) {
            auditoriaGlobalService.registrarEvento(
                    "TRASLADO_GRUPO_MATRICULA",
                    "Matricula",
                    actualizada.getId().toString(),
                    "Grupo: " + grupoOrigen.getNombre(),
                    "Grupo: " + grupoDestino.getNombre(),
                    request.getMotivo(),
                    "EXITOSO"
            );
        }

        return matriculaMapper.toResponse(actualizada);
    }

    public MatriculaResponse promocionAnticipada(Long matriculaId, NovedadMatriculaRequest request, Long usuarioId) {
        Matricula matricula = matriculaRepository.findById(matriculaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Matrícula no encontrada con ID: " + matriculaId));

        if (matricula.getEstado() != EstadoMatricula.MATRICULADA && matricula.getEstado() != EstadoMatricula.MATRICULADO_ACTIVO) {
            throw new ConflictoNegocioException("Solo se puede aplicar promoción anticipada a matrículas activas");
        }

        if (request.getNumeroActaResolucion() == null || request.getNumeroActaResolucion().isBlank()) {
            throw new ConflictoNegocioException("Se requiere el número de acta del Consejo Directivo para formalizar la Promoción Anticipada (UC-PROV-09 A2, E2, RN-MAT-07).");
        }

        if (!Boolean.TRUE.equals(request.getConsentimientoAcudiente())) {
            throw new ConflictoNegocioException("Se requiere el consentimiento expreso del acudiente principal para la Promoción Anticipada (Art. 7 Decreto 1290 de 2009).");
        }

        if (request.getNuevoGrupoId() == null) {
            throw new ConflictoNegocioException("Debe seleccionar el grupo destino en el grado superior para la promoción anticipada");
        }

        Grupo grupoDestino = grupoRepository.findByIdWithLock(request.getNuevoGrupoId())
                .or(() -> grupoRepository.findById(request.getNuevoGrupoId()))
                .orElseThrow(() -> new RecursoNoEncontradoException("Grupo destino no encontrado con ID: " + request.getNuevoGrupoId()));

        Grupo grupoOrigen = matricula.getGrupo();
        Grado gradoOrigen = grupoOrigen.getGrado();
        Grado gradoDestino = grupoDestino.getGrado();

        // Validar que el grado destino sea el gradoSiguiente del grado origen
        if (gradoOrigen.getGradoSiguiente() != null && !gradoOrigen.getGradoSiguiente().getId().equals(gradoDestino.getId())) {
            throw new ConflictoNegocioException("El grado destino (" + gradoDestino.getNombre()
                    + ") debe ser el grado inmediatamente siguiente a " + gradoOrigen.getNombre() + " (UC-PROV-09 A2).");
        }

        long matriculadosDestino = matriculaRepository.countByGrupoIdAndEstado(grupoDestino.getId(), EstadoMatricula.MATRICULADA);
        int cupoMaximoDestino = grupoDestino.getCupoMaximo() != null ? grupoDestino.getCupoMaximo() : 35;
        if (matriculadosDestino >= cupoMaximoDestino) {
            throw new ConflictoNegocioException("El grupo destino '" + grupoDestino.getNombre() + "' ha alcanzado su cupo máximo de " + cupoMaximoDestino + " estudiantes (UC-PROV-09 E1).");
        }

        LocalDate fechaEfectiva = request.getFechaEfectiva() != null ? request.getFechaEfectiva() : LocalDate.now();

        if (historialNovedadRepository != null) {
            HistorialNovedadMatricula novedad = new HistorialNovedadMatricula();
            novedad.setMatricula(matricula);
            novedad.setTipoNovedad(TipoNovedadMatricula.PROMOCION_ANTICIPADA);
            novedad.setGrupoOrigen(grupoOrigen);
            novedad.setGrupoDestino(grupoDestino);
            novedad.setGradoOrigen(gradoOrigen);
            novedad.setGradoDestino(gradoDestino);
            novedad.setFechaEfectiva(fechaEfectiva);
            novedad.setMotivo(request.getMotivo());
            novedad.setSoporteDocumento(request.getSoporteDocumento());
            novedad.setNumeroActaResolucion(request.getNumeroActaResolucion());
            novedad.setFechaActaResolucion(request.getFechaActaResolucion());
            novedad.setSolicitante(request.getSolicitante());
            if (usuarioId != null && usuarioRepository != null) {
                novedad.setUsuario(usuarioRepository.findById(usuarioId).orElse(null));
            }
            historialNovedadRepository.save(novedad);
        }

        matricula.setGrupo(grupoDestino);
        matricula.setActaConsejoDirectivo(request.getNumeroActaResolucion());
        matricula.setFechaActaConsejoDirectivo(request.getFechaActaResolucion() != null ? request.getFechaActaResolucion() : LocalDate.now());
        matricula.setConsentimientoAcudiente(true);

        Matricula guardada = matriculaRepository.save(matricula);

        if (auditoriaGlobalService != null) {
            auditoriaGlobalService.registrarEvento(
                    "PROMOCION_ANTICIPADA_MATRICULA",
                    "Matricula",
                    guardada.getId().toString(),
                    "Grado: " + gradoOrigen.getNombre() + ", Grupo: " + grupoOrigen.getNombre(),
                    "Grado: " + gradoDestino.getNombre() + ", Grupo: " + grupoDestino.getNombre() + " (Acta CD: " + request.getNumeroActaResolucion() + ")",
                    request.getMotivo(),
                    "EXITOSO"
            );
        }

        return matriculaMapper.toResponse(guardada);
    }

    public MatriculaResponse retirarEstudiante(Long matriculaId, NovedadMatriculaRequest request, Long usuarioId) {
        Matricula matricula = matriculaRepository.findById(matriculaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Matrícula no encontrada con ID: " + matriculaId));

        if (matricula.getEstado() != EstadoMatricula.MATRICULADA && matricula.getEstado() != EstadoMatricula.MATRICULADO_ACTIVO) {
            throw new ConflictoNegocioException("Solo se pueden retirar estudiantes con matrícula activa");
        }

        TipoNovedadMatricula tipo = request.getTipoNovedad() != null ? request.getTipoNovedad() : TipoNovedadMatricula.RETIRO_VOLUNTARIO;
        LocalDate fechaEfectiva = request.getFechaEfectiva() != null ? request.getFechaEfectiva() : LocalDate.now();
        matricula.setFechaEfectivaRetiro(fechaEfectiva);

        EstadoMatricula nuevoEstado = switch (tipo) {
            case TRASLADADO_SALIENTE -> EstadoMatricula.TRASLADADO_SALIENTE;
            case DESERCION -> EstadoMatricula.RETIRADO_DESERCION;
            case CANCELACION_EXPULSION -> EstadoMatricula.CANCELADA_EXPULSION;
            default -> EstadoMatricula.RETIRADO_VOLUNTARIO;
        };
        matricula.setEstado(nuevoEstado);

        if (historialNovedadRepository != null) {
            HistorialNovedadMatricula novedad = new HistorialNovedadMatricula();
            novedad.setMatricula(matricula);
            novedad.setTipoNovedad(tipo);
            novedad.setGrupoOrigen(matricula.getGrupo());
            novedad.setGradoOrigen(matricula.getGrupo().getGrado());
            novedad.setFechaEfectiva(fechaEfectiva);
            novedad.setMotivo(request.getMotivo());
            novedad.setSoporteDocumento(request.getSoporteDocumento());
            novedad.setNumeroActaResolucion(request.getNumeroActaResolucion());
            novedad.setFechaActaResolucion(request.getFechaActaResolucion());
            novedad.setSolicitante(request.getSolicitante());
            if (usuarioId != null && usuarioRepository != null) {
                novedad.setUsuario(usuarioRepository.findById(usuarioId).orElse(null));
            }
            historialNovedadRepository.save(novedad);
        }

        Matricula guardada = matriculaRepository.save(matricula);

        if (auditoriaGlobalService != null) {
            auditoriaGlobalService.registrarEvento(
                    "RETIRO_MATRICULA",
                    "Matricula",
                    guardada.getId().toString(),
                    "Estado: MATRICULADA",
                    "Estado: " + nuevoEstado + " (Fecha corte: " + fechaEfectiva + ")",
                    request.getMotivo(),
                    "EXITOSO"
            );
        }

        return matriculaMapper.toResponse(guardada);
    }

    public MatriculaResponse anularMatricula(Long matriculaId, String motivo, Long usuarioId) {
        Matricula matricula = matriculaRepository.findById(matriculaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Matrícula no encontrada con ID: " + matriculaId));

        if (motivo == null || motivo.trim().isBlank()) {
            throw new IllegalArgumentException("El motivo de la anulación administrativa es obligatorio.");
        }

        // Bloquear anulación si ya registra actividad académica (UC-PROV-09 E4)
        if (calificacionActividadRepository != null && calificacionActividadRepository.existsByMatriculaId(matriculaId)) {
            throw new ConflictoNegocioException("No se puede anular administrativamente la matrícula porque ya registra calificaciones causadas en el año lectivo (UC-PROV-09 E4). Debe tramitarse como retiro o traslado.");
        }

        EstadoMatricula anterior = matricula.getEstado();
        matricula.setEstado(EstadoMatricula.ANULADA_ADMINISTRATIVAMENTE);
        matricula.setActivo(false);

        if (historialNovedadRepository != null) {
            HistorialNovedadMatricula novedad = new HistorialNovedadMatricula();
            novedad.setMatricula(matricula);
            novedad.setTipoNovedad(TipoNovedadMatricula.ANULADA_ADMINISTRATIVAMENTE);
            novedad.setGrupoOrigen(matricula.getGrupo());
            novedad.setGradoOrigen(matricula.getGrupo().getGrado());
            novedad.setFechaEfectiva(LocalDate.now());
            novedad.setMotivo(motivo);
            if (usuarioId != null && usuarioRepository != null) {
                novedad.setUsuario(usuarioRepository.findById(usuarioId).orElse(null));
            }
            historialNovedadRepository.save(novedad);
        }

        Matricula guardada = matriculaRepository.save(matricula);

        if (auditoriaGlobalService != null) {
            auditoriaGlobalService.registrarEvento(
                    "ANULACION_ADMINISTRATIVA_MATRICULA",
                    "Matricula",
                    guardada.getId().toString(),
                    "Estado: " + anterior,
                    "Estado: ANULADA_ADMINISTRATIVAMENTE",
                    motivo,
                    "EXITOSO"
            );
        }

        return matriculaMapper.toResponse(guardada);
    }

    public MatriculaResponse cambiarEstado(Long matriculaId, CambioEstadoMatriculaRequest request) {
        Matricula matricula = matriculaRepository.findById(matriculaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Matrícula no encontrada con ID " + matriculaId));

        EstadoMatricula estadoAnterior = matricula.getEstado();
        EstadoMatricula nuevoEstado = request.getNuevoEstado();

        if (estadoAnterior == nuevoEstado) {
            return matriculaMapper.toResponse(matricula);
        }

        if (nuevoEstado == EstadoMatricula.MATRICULADA && estadoAnterior != EstadoMatricula.MATRICULADA) {
            AnioLectivo anio = matricula.getAnioLectivo();
            if (anio != null && (anio.getEstado() == EstadoAnioLectivo.CERRADO || anio.getEstado() == EstadoAnioLectivo.CERRADO_HISTORICO)) {
                throw new ConflictoNegocioException("No se puede reactivar una matrícula en un año lectivo cerrado");
            }

            if (matriculaRepository.existsByEstudianteIdAndAnioLectivoIdAndEstado(
                    matricula.getEstudiante().getId(), matricula.getAnioLectivo().getId(), EstadoMatricula.MATRICULADA)) {
                throw new ConflictoNegocioException("El estudiante ya cuenta con otra matrícula activa en este año lectivo");
            }

            long cupoActual = matriculaRepository.countByGrupoIdAndEstado(matricula.getGrupo().getId(), EstadoMatricula.MATRICULADA);
            int cupoMax = matricula.getGrupo().getCupoMaximo() != null ? matricula.getGrupo().getCupoMaximo() : 35;
            if (cupoActual >= cupoMax) {
                throw new ConflictoNegocioException("El grupo ha alcanzado su cupo máximo de " + cupoMax + " estudiantes");
            }
        }

        matricula.setEstado(nuevoEstado);
        if (request.getMotivo() != null && !request.getMotivo().isBlank()) {
            String obsPrevia = matricula.getObservaciones() != null ? matricula.getObservaciones() + " | " : "";
            matricula.setObservaciones(obsPrevia + "Estado cambiado a " + nuevoEstado + ": " + request.getMotivo());
        }

        Matricula guardada = matriculaRepository.save(matricula);
        return matriculaMapper.toResponse(guardada);
    }

    @Transactional(readOnly = true)
    public MatriculaResponse obtenerPorId(Long id) {
        Matricula matricula = matriculaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Matrícula no encontrada con ID " + id));
        return matriculaMapper.toResponse(matricula);
    }

    @Transactional(readOnly = true)
    public MatriculaResponse obtenerPorNumero(String numeroMatricula) {
        Matricula matricula = matriculaRepository.findByNumeroMatricula(numeroMatricula)
                .orElseThrow(() -> new RecursoNoEncontradoException("Matrícula no encontrada con número " + numeroMatricula));
        return matriculaMapper.toResponse(matricula);
    }

    @Transactional(readOnly = true)
    public List<MatriculaResponse> listarPorGrupo(Long grupoId) {
        return matriculaRepository.findByGrupoIdAndEstado(grupoId, EstadoMatricula.MATRICULADA).stream()
                .map(matriculaMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MatriculaResponse> listarListadoOficialGrupo(Long grupoId) {
        return matriculaRepository.findListadoOficialPorGrupo(grupoId).stream()
                .map(matriculaMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MatriculaResponse> listarPorEstudiante(Long estudianteId) {
        return matriculaRepository.findByEstudianteIdOrderByFechaMatriculaDesc(estudianteId).stream()
                .map(matriculaMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MatriculaResponse> listarMiHistorial(String username) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el usuario: " + username));

        if (usuario.getPersonaTipo() == TipoPersonaUsuario.ESTUDIANTE && usuario.getPersonaId() != null) {
            return listarPorEstudiante(usuario.getPersonaId());
        }
        return Collections.emptyList();
    }

    @Transactional(readOnly = true)
    public List<MatriculaResponse> listarMatriculasMisEstudiantes(String username) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el usuario: " + username));

        if (usuario.getPersonaTipo() == TipoPersonaUsuario.ACUDIENTE && usuario.getPersonaId() != null && acudienteRepository != null) {
            Acudiente acudiente = acudienteRepository.findById(usuario.getPersonaId()).orElse(null);
            if (acudiente != null && estudianteAcudienteRepository != null) {
                List<EstudianteAcudiente> vinculos = estudianteAcudienteRepository
                        .findByAcudienteAndEstadoVinculo(acudiente, EstadoVinculo.VERIFICADO);
                return vinculos.stream()
                        .flatMap(v -> matriculaRepository.findByEstudianteIdOrderByFechaMatriculaDesc(v.getEstudiante().getId()).stream())
                        .map(matriculaMapper::toResponse)
                        .toList();
            }
        }
        return Collections.emptyList();
    }

    @Transactional(readOnly = true)
    public Page<MatriculaResponse> listarPorAnioLectivo(Long anioLectivoId, Pageable pageable) {
        return matriculaRepository.findByAnioLectivoId(anioLectivoId, pageable)
                .map(matriculaMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public List<HistorialTrasladoResponse> listarHistorialTraslados(Long matriculaId) {
        return historialTrasladoRepository.findByMatriculaIdOrderByFechaTrasladoDesc(matriculaId).stream()
                .map(matriculaMapper::toHistorialResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<HistorialNovedadResponse> listarHistorialNovedades(Long matriculaId) {
        if (historialNovedadRepository == null) {
            return Collections.emptyList();
        }
        return historialNovedadRepository.findByMatriculaIdOrderByFechaEfectivaDesc(matriculaId).stream()
                .map(matriculaMapper::toNovedadResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public boolean tieneMatriculaActivaEnGrupo(Long estudianteId, Long grupoId) {
        if (estudianteId == null || grupoId == null) {
            return false;
        }
        return matriculaRepository.existsByEstudianteIdAndGrupoIdAndEstado(
                estudianteId, grupoId, EstadoMatricula.MATRICULADA
        );
    }

    private String generarNumeroMatricula(AnioLectivo anioLectivo) {
        int anio = anioLectivo != null ? anioLectivo.getAnio() : 2026;
        long conteo = matriculaRepository.countByAnioLectivoId(anioLectivo != null ? anioLectivo.getId() : 0L);
        String numero = String.format("MAT-%d-%04d", anio, conteo + 1);
        int intento = 1;
        while (matriculaRepository.existsByNumeroMatricula(numero)) {
            numero = String.format("MAT-%d-%04d-%d", anio, conteo + 1, intento++);
        }
        return numero;
    }
}
