package com.jackren.sistemaacademico.service;

import com.jackren.sistemaacademico.dto.EstudianteRequest;
import com.jackren.sistemaacademico.dto.EstudianteResponse;
import com.jackren.sistemaacademico.entity.Estudiante;
import com.jackren.sistemaacademico.exception.ConflictoNegocioException;
import com.jackren.sistemaacademico.exception.RecursoNoEncontradoException;
import com.jackren.sistemaacademico.mapper.EstudianteMapper;
import com.jackren.sistemaacademico.repository.EstudianteAcudienteRepository;
import com.jackren.sistemaacademico.repository.EstudianteRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class EstudianteService {

    private final EstudianteRepository estudianteRepository;
    private final EstudianteAcudienteRepository estudianteAcudienteRepository;
    private final EstudianteMapper estudianteMapper;

    public EstudianteService(EstudianteRepository estudianteRepository,
                             EstudianteAcudienteRepository estudianteAcudienteRepository,
                             EstudianteMapper estudianteMapper) {
        this.estudianteRepository = estudianteRepository;
        this.estudianteAcudienteRepository = estudianteAcudienteRepository;
        this.estudianteMapper = estudianteMapper;
    }

    // Listado paginado de estudiantes activos
    public Page<EstudianteResponse> listarPaginado(Pageable pageable) {
        return estudianteRepository.findByActivoTrue(pageable)
                .map(estudianteMapper::toResponse);
    }

    // Obtener todos los estudiantes activos (sin paginacion)
    public List<EstudianteResponse> listarTodos() {
        return estudianteRepository.findByActivoTrue()
                .stream()
                .map(estudianteMapper::toResponse)
                .toList();
    }

    // Buscar un estudiante por ID con excepcion si no existe
    public EstudianteResponse obtenerPorId(Long id) {
        return estudianteRepository.findById(id)
                .map(estudianteMapper::toResponse)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontro el estudiante con ID: " + id));
    }

    // Buscar un estudiante por ID (retorno Optional)
    public Optional<EstudianteResponse> buscarPorId(Long id) {
        return estudianteRepository.findById(id)
                .map(estudianteMapper::toResponse);
    }

    // Buscar un estudiante por documento con excepcion si no existe
    public EstudianteResponse obtenerPorDocumento(String documento) {
        return estudianteRepository.findByDocumento(documento)
                .map(estudianteMapper::toResponse)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontro el estudiante con documento: " + documento));
    }

    // Buscar un estudiante por documento (retorno Optional)
    public Optional<EstudianteResponse> buscarPorDocumento(String documento) {
        return estudianteRepository.findByDocumento(documento)
                .map(estudianteMapper::toResponse);
    }

    // Crear un estudiante
    @Transactional
    public EstudianteResponse guardar(EstudianteRequest request) {
        if (estudianteRepository.existsByDocumento(request.getDocumento())) {
            throw new ConflictoNegocioException("Ya existe un estudiante con el documento: " + request.getDocumento());
        }

        String correoNormalizado = normalizarCorreo(request.getCorreo());
        request.setCorreo(correoNormalizado);

        if (correoNormalizado != null && estudianteRepository.existsByCorreo(correoNormalizado)) {
            throw new ConflictoNegocioException("Ya existe un estudiante con el correo: " + correoNormalizado);
        }

        Estudiante estudiante = estudianteMapper.toEntity(request);
        Estudiante estudianteGuardado = estudianteRepository.save(estudiante);

        return estudianteMapper.toResponse(estudianteGuardado);
    }

    // Actualizar un estudiante existente
    @Transactional
    public EstudianteResponse actualizar(Long id, EstudianteRequest request) {
        Estudiante estudiante = estudianteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontro el estudiante con ID: " + id));

        // Validar unicidad de documento si cambio
        if (!estudiante.getDocumento().equals(request.getDocumento())
                && estudianteRepository.existsByDocumento(request.getDocumento())) {
            throw new ConflictoNegocioException("Ya existe un estudiante con el documento: " + request.getDocumento());
        }

        String correoNormalizado = normalizarCorreo(request.getCorreo());
        request.setCorreo(correoNormalizado);

        // Validar unicidad de correo si cambio
        if (correoNormalizado != null
                && !correoNormalizado.equalsIgnoreCase(estudiante.getCorreo())
                && estudianteRepository.existsByCorreo(correoNormalizado)) {
            throw new ConflictoNegocioException("Ya existe un estudiante con el correo: " + correoNormalizado);
        }

        estudianteMapper.updateEntityFromRequest(estudiante, request);
        Estudiante estudianteActualizado = estudianteRepository.save(estudiante);

        return estudianteMapper.toResponse(estudianteActualizado);
    }

    // Desactivar / Eliminar logicamente
    @Transactional
    public void desactivar(Long id) {
        Estudiante estudiante = estudianteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontro el estudiante con ID: " + id));

        estudiante.setActivo(false);
        estudiante.setEstadoExpediente(com.jackren.sistemaacademico.entity.EstadoExpediente.INACTIVO_HISTORICO);
        estudianteRepository.save(estudiante);
    }

    // Deshacer eliminacion / Restaurar estudiante
    @Transactional
    public void restaurar(Long id) {
        Estudiante estudiante = estudianteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontro el estudiante con ID: " + id));

        estudiante.setActivo(true);
        estudiante.setEstadoExpediente(com.jackren.sistemaacademico.entity.EstadoExpediente.LISTO_PARA_MATRICULA);
        estudianteRepository.save(estudiante);
    }

    // Prohibición de eliminación física (RN-SEG-01, UC-PROV-04 E3)
    @Transactional
    public void eliminarPermanente(Long id, boolean confirmar) {
        throw new ConflictoNegocioException("Prohibido el borrado físico de expedientes de estudiantes con trazabilidad institucional (RN-SEG-01, UC-PROV-04 E3). Debe utilizarse la inactivación lógica.");
    }

    private String normalizarCorreo(String correo) {
        if (correo == null || correo.isBlank()) {
            return null;
        }
        return correo.trim().toLowerCase();
    }

    /**
     * Consulta de la propia ficha escolar para el estudiante autenticado (UC-07).
     */
    public com.jackren.sistemaacademico.dto.FichaEscolarEstudianteResponse obtenerMiFichaEscolar(String username) {
        Estudiante estudiante = estudianteRepository.findByDocumento(username)
                .or(() -> estudianteRepository.findByCorreo(username))
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró expediente escolar asociado a la cuenta: " + username));

        return toFichaResponse(estudiante, true);
    }

    /**
     * Consulta contextual con aplicación estricta de la matriz de visibilidad y mínimo privilegio (RN-09, RN-EST-08).
     */
    public com.jackren.sistemaacademico.dto.FichaEscolarEstudianteResponse obtenerFichaEscolarAutorizada(Long estudianteId, String username, String rol) {
        Estudiante estudiante = estudianteRepository.findById(estudianteId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Estudiante no encontrado con ID: " + estudianteId));

        boolean verDetallesSensibles = "ADMINISTRADOR".equalsIgnoreCase(rol) ||
                                       "RECTOR".equalsIgnoreCase(rol) ||
                                       "COORDINADOR".equalsIgnoreCase(rol) ||
                                       "COORDINADOR_CONVIVENCIA".equalsIgnoreCase(rol) ||
                                       "SECRETARIA".equalsIgnoreCase(rol);

        return toFichaResponse(estudiante, verDetallesSensibles);
    }

    private com.jackren.sistemaacademico.dto.FichaEscolarEstudianteResponse toFichaResponse(Estudiante e, boolean incluirDetallesSensibles) {
        com.jackren.sistemaacademico.dto.FichaEscolarEstudianteResponse r = new com.jackren.sistemaacademico.dto.FichaEscolarEstudianteResponse();
        r.setId(e.getId());
        r.setTipoDocumento(e.getTipoDocumento());
        r.setDocumento(e.getDocumento());
        r.setNombres(e.getNombres());
        r.setApellidos(e.getApellidos());
        r.setNombreCompleto(e.getNombres() + " " + e.getApellidos());
        r.setFechaNacimiento(e.getFechaNacimiento());
        if (e.getFechaNacimiento() != null) {
            r.setEdadCalculada(java.time.Period.between(e.getFechaNacimiento(), java.time.LocalDate.now()).getYears());
        }
        r.setCorreo(e.getCorreo());
        r.setTelefono(e.getTelefono());
        r.setDireccion(e.getDireccion());
        r.setEps(e.getEps());
        r.setGrupoSanguineoRh(e.getGrupoSanguineoRh());
        r.setEstadoExpediente(e.getEstadoExpediente());
        r.setEstadoMatricula(Boolean.TRUE.equals(e.getActivo()) ? "MATRICULADO_ACTIVO" : "INACTIVO_HISTORICO");

        if (incluirDetallesSensibles) {
            r.setTieneDiscapacidadPiar(e.getTieneDiscapacidadPiar());
            r.setDetallesSaludEmergencia(e.getDetallesSaludEmergencia());
        } else {
            r.setTieneDiscapacidadPiar(null); // Oculto por mínimo privilegio (RN-EST-08)
            r.setDetallesSaludEmergencia(null);
        }

        java.util.List<com.jackren.sistemaacademico.dto.FichaEscolarEstudianteResponse.ContactoUrgenciaDTO> contactos = estudianteAcudienteRepository
                .findByEstudiante(e).stream()
                .filter(v -> v.getEstadoVinculo() == com.jackren.sistemaacademico.entity.EstadoVinculo.VERIFICADO)
                .map(v -> new com.jackren.sistemaacademico.dto.FichaEscolarEstudianteResponse.ContactoUrgenciaDTO(
                        v.getAcudiente().getNombres() + " " + v.getAcudiente().getApellidos(),
                        v.getParentesco().name(),
                        v.getAcudiente().getTelefono(),
                        v.getEsPrincipal(),
                        v.getAutorizadoRetiro(),
                        v.getPrioridadEmergencia()
                )).toList();
        r.setContactosEmergencia(contactos);

        return r;
    }
}
