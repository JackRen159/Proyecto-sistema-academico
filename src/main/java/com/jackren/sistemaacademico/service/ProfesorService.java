package com.jackren.sistemaacademico.service;

import com.jackren.sistemaacademico.dto.ProfesorRequest;
import com.jackren.sistemaacademico.dto.ProfesorResponse;
import com.jackren.sistemaacademico.entity.EstadoAdministrativoDocente;
import com.jackren.sistemaacademico.entity.Profesor;
import com.jackren.sistemaacademico.entity.Usuario;
import com.jackren.sistemaacademico.exception.ConflictoNegocioException;
import com.jackren.sistemaacademico.exception.RecursoNoEncontradoException;
import com.jackren.sistemaacademico.mapper.ProfesorMapper;
import com.jackren.sistemaacademico.repository.AsignacionAcademicaRepository;
import com.jackren.sistemaacademico.repository.ProfesorRepository;
import com.jackren.sistemaacademico.repository.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class ProfesorService {

    private final ProfesorRepository profesorRepository;
    private final UsuarioRepository usuarioRepository;
    private final AsignacionAcademicaRepository asignacionAcademicaRepository;
    private final AuditoriaGlobalService auditoriaGlobalService;
    private final ProfesorMapper profesorMapper;

    public ProfesorService(ProfesorRepository profesorRepository,
                           UsuarioRepository usuarioRepository,
                           AsignacionAcademicaRepository asignacionAcademicaRepository,
                           AuditoriaGlobalService auditoriaGlobalService,
                           ProfesorMapper profesorMapper) {
        this.profesorRepository = profesorRepository;
        this.usuarioRepository = usuarioRepository;
        this.asignacionAcademicaRepository = asignacionAcademicaRepository;
        this.auditoriaGlobalService = auditoriaGlobalService;
        this.profesorMapper = profesorMapper;
    }

    public ProfesorService(ProfesorRepository profesorRepository,
                           UsuarioRepository usuarioRepository,
                           ProfesorMapper profesorMapper) {
        this(profesorRepository, usuarioRepository, null, null, profesorMapper);
    }

    public ProfesorResponse crear(ProfesorRequest request) {
        if (profesorRepository.existsByDocumento(request.getDocumento())) {
            throw new ConflictoNegocioException("Ya existe un profesor registrado con el documento " + request.getDocumento());
        }

        if (profesorRepository.existsByCorreo(request.getCorreo())) {
            throw new ConflictoNegocioException("Ya existe un profesor registrado con el correo " + request.getCorreo());
        }

        // Verificación obligatoria de inhabilidades por delitos contra menores (Ley 1918 de 2018, RN-PRO-02, UC-PROV-11 E1)
        if (!Boolean.TRUE.equals(request.getVerificadoInhabilidades())) {
            throw new ConflictoNegocioException("No se puede registrar ni activar un expediente docente sin la verificación vigente "
                    + "de ausencia de inhabilidades por delitos sexuales contra menores de edad conforme a la Ley 1918 de 2018 (RN-PRO-02, E1).");
        }

        Usuario usuario = null;
        if (request.getUsuarioId() != null) {
            usuario = usuarioRepository.findById(request.getUsuarioId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con ID " + request.getUsuarioId()));

            if (profesorRepository.existsByUsuarioId(request.getUsuarioId())) {
                throw new ConflictoNegocioException("El usuario con ID " + request.getUsuarioId() + " ya está vinculado a otro profesor");
            }
        }

        Profesor profesor = profesorMapper.toEntity(request, usuario);
        Profesor guardado = profesorRepository.save(profesor);

        if (auditoriaGlobalService != null) {
            auditoriaGlobalService.registrarEvento(
                    "CREACION_EXPEDIENTE_DOCENTE",
                    "Profesor",
                    guardado.getId().toString(),
                    null,
                    "Docente: " + guardado.getNombres() + " " + guardado.getApellidos() + " (" + guardado.getDocumento() + ")",
                    "Registro de expediente maestro de profesor",
                    "EXITOSO"
            );
        }

        return convertirAResponseConHoras(guardado);
    }

    public ProfesorResponse actualizar(Long id, ProfesorRequest request) {
        Profesor profesor = profesorRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Profesor no encontrado con ID " + id));

        if (!profesor.getDocumento().equals(request.getDocumento()) &&
                profesorRepository.existsByDocumento(request.getDocumento())) {
            throw new ConflictoNegocioException("Ya existe un profesor registrado con el documento " + request.getDocumento());
        }

        if (!profesor.getCorreo().equalsIgnoreCase(request.getCorreo()) &&
                profesorRepository.existsByCorreo(request.getCorreo())) {
            throw new ConflictoNegocioException("Ya existe un profesor registrado con el correo " + request.getCorreo());
        }

        if (request.getVerificadoInhabilidades() != null && !request.getVerificadoInhabilidades() && Boolean.TRUE.equals(profesor.getActivo())) {
            throw new ConflictoNegocioException("No se puede mantener activo a un docente sin la verificación vigente de inhabilidades Ley 1918 de 2018.");
        }

        Usuario usuario = profesor.getUsuario();
        if (request.getUsuarioId() != null) {
            if (usuario == null || !usuario.getId().equals(request.getUsuarioId())) {
                usuario = usuarioRepository.findById(request.getUsuarioId())
                        .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con ID " + request.getUsuarioId()));

                if (profesorRepository.existsByUsuarioId(request.getUsuarioId())) {
                    throw new ConflictoNegocioException("El usuario con ID " + request.getUsuarioId() + " ya está vinculado a otro profesor");
                }
            }
        } else {
            usuario = null;
        }

        profesorMapper.updateEntityFromRequest(profesor, request, usuario);
        Profesor actualizado = profesorRepository.save(profesor);

        if (auditoriaGlobalService != null) {
            auditoriaGlobalService.registrarEvento(
                    "ACTUALIZACION_EXPEDIENTE_DOCENTE",
                    "Profesor",
                    actualizado.getId().toString(),
                    null,
                    "Docente actualizado: " + actualizado.getNombres() + " " + actualizado.getApellidos(),
                    "Actualización de datos de expediente",
                    "EXITOSO"
            );
        }

        return convertirAResponseConHoras(actualizado);
    }

    @Transactional(readOnly = true)
    public ProfesorResponse obtenerPorId(Long id) {
        Profesor profesor = profesorRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Profesor no encontrado con ID " + id));
        return convertirAResponseConHoras(profesor);
    }

    @Transactional(readOnly = true)
    public List<ProfesorResponse> listarTodos() {
        return profesorRepository.findAll().stream()
                .map(this::convertirAResponseConHoras)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProfesorResponse> listarActivos() {
        return profesorRepository.findByActivoTrue().stream()
                .map(this::convertirAResponseConHoras)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<ProfesorResponse> listarPaginado(Pageable pageable) {
        return profesorRepository.findByActivoTrue(pageable)
                .map(this::convertirAResponseConHoras);
    }

    public void desactivar(Long id) {
        Profesor profesor = profesorRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Profesor no encontrado con ID " + id));

        // Bloqueo de inactivación si tiene cursos activos sin relevo (UC-PROV-11 E2)
        if (asignacionAcademicaRepository != null && asignacionAcademicaRepository.existsByProfesorIdAndActivoTrue(id)) {
            throw new ConflictoNegocioException("No se puede inactivar o pasar a retiro el docente con ID " + id
                    + " porque cuenta con cursos o asignaciones académicas activas en curso. Debe ejecutar previamente el relevo docente en UC-15 (UC-PROV-11 E2).");
        }

        profesor.setActivo(false);
        profesor.setEstadoAdministrativo(EstadoAdministrativoDocente.RETIRADO_INACTIVO);
        profesorRepository.save(profesor);

        if (auditoriaGlobalService != null) {
            auditoriaGlobalService.registrarEvento(
                    "DESACTIVACION_EXPEDIENTE_DOCENTE",
                    "Profesor",
                    id.toString(),
                    "Activo: true",
                    "Activo: false, Estado: RETIRADO_INACTIVO",
                    "Baja / Retiro definitivo de docente",
                    "EXITOSO"
            );
        }
    }

    public ProfesorResponse registrarSituacionAdministrativa(Long id, EstadoAdministrativoDocente nuevoEstado,
                                                            LocalDate fechaInicio, LocalDate fechaFin, String motivo) {
        Profesor profesor = profesorRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Profesor no encontrado con ID " + id));

        EstadoAdministrativoDocente estadoAnterior = profesor.getEstadoAdministrativo();
        profesor.setEstadoAdministrativo(nuevoEstado);
        profesor.setFechaInicioSituacion(fechaInicio);
        profesor.setFechaFinSituacion(fechaFin);
        profesor.setMotivoSituacion(motivo);

        if (nuevoEstado == EstadoAdministrativoDocente.ACTIVO) {
            profesor.setActivo(true);
        } else if (nuevoEstado == EstadoAdministrativoDocente.RETIRADO_INACTIVO) {
            if (asignacionAcademicaRepository != null && asignacionAcademicaRepository.existsByProfesorIdAndActivoTrue(id)) {
                throw new ConflictoNegocioException("No se puede retirar al docente porque tiene cursos activos asignados. Ejecute primero el relevo docente en UC-15 (E2).");
            }
            profesor.setActivo(false);
        }

        Profesor guardado = profesorRepository.save(profesor);

        if (auditoriaGlobalService != null) {
            auditoriaGlobalService.registrarEvento(
                    "SITUACION_ADMINISTRATIVA_DOCENTE",
                    "Profesor",
                    id.toString(),
                    "Estado: " + estadoAnterior,
                    "Estado: " + nuevoEstado + " (Motivo: " + motivo + ")",
                    motivo,
                    "EXITOSO"
            );
        }

        return convertirAResponseConHoras(guardado);
    }

    private ProfesorResponse convertirAResponseConHoras(Profesor profesor) {
        int horas = 0;
        if (asignacionAcademicaRepository != null && profesor.getId() != null) {
            Integer suma = asignacionAcademicaRepository.sumHorasAsignadasPorProfesor(profesor.getId());
            horas = suma != null ? suma : 0;
        }
        return profesorMapper.toResponse(profesor, horas);
    }
}
