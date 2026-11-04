package jackren.sistemaacademico.main.service;

import jackren.sistemaacademico.main.dto.AcudienteRequest;
import jackren.sistemaacademico.main.dto.AcudienteResponse;
import jackren.sistemaacademico.main.entity.Acudiente;
import jackren.sistemaacademico.main.exception.ConflictoNegocioException;
import jackren.sistemaacademico.main.exception.RecursoNoEncontradoException;
import jackren.sistemaacademico.main.mapper.AcudienteMapper;
import jackren.sistemaacademico.main.repository.AcudienteRepository;
import jackren.sistemaacademico.main.repository.EstudianteAcudienteRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class AcudienteService {

    private final AcudienteRepository acudienteRepository;
    private final EstudianteAcudienteRepository estudianteAcudienteRepository;
    private final AcudienteMapper acudienteMapper;

    public AcudienteService(AcudienteRepository acudienteRepository,
                            EstudianteAcudienteRepository estudianteAcudienteRepository,
                            AcudienteMapper acudienteMapper) {
        this.acudienteRepository = acudienteRepository;
        this.estudianteAcudienteRepository = estudianteAcudienteRepository;
        this.acudienteMapper = acudienteMapper;
    }

    // Listado paginado de acudientes activos
    public Page<AcudienteResponse> listarPaginado(Pageable pageable) {
        return acudienteRepository.findByActivoTrue(pageable)
                .map(acudienteMapper::toResponse);
    }

    // Obtener todos los acudientes activos (sin paginacion)
    public List<AcudienteResponse> listarTodos() {
        return acudienteRepository.findByActivoTrue()
                .stream()
                .map(acudienteMapper::toResponse)
                .toList();
    }

    // Buscar un acudiente por ID con excepcion si no existe
    public AcudienteResponse obtenerPorId(Long id) {
        return acudienteRepository.findById(id)
                .map(acudienteMapper::toResponse)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontro el acudiente con ID: " + id));
    }

    // Buscar un acudiente por ID (retorno Optional)
    public Optional<AcudienteResponse> buscarPorId(Long id) {
        return acudienteRepository.findById(id)
                .map(acudienteMapper::toResponse);
    }

    // Buscar un acudiente por documento con excepcion si no existe
    public AcudienteResponse obtenerPorDocumento(String documento) {
        return acudienteRepository.findByDocumento(documento)
                .map(acudienteMapper::toResponse)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontro el acudiente con documento: " + documento));
    }

    // Buscar un acudiente por documento (retorno Optional)
    public Optional<AcudienteResponse> buscarPorDocumento(String documento) {
        return acudienteRepository.findByDocumento(documento)
                .map(acudienteMapper::toResponse);
    }

    // Crear un acudiente
    @Transactional
    public AcudienteResponse guardar(AcudienteRequest request) {
        if (acudienteRepository.existsByDocumento(request.getDocumento())) {
            throw new ConflictoNegocioException("Ya existe un acudiente con el documento: " + request.getDocumento());
        }

        String correoNormalizado = normalizarCorreo(request.getCorreo());
        request.setCorreo(correoNormalizado);

        if (correoNormalizado != null && acudienteRepository.existsByCorreo(correoNormalizado)) {
            throw new ConflictoNegocioException("Ya existe un acudiente con el correo: " + correoNormalizado);
        }

        Acudiente acudiente = acudienteMapper.toEntity(request);
        Acudiente acudienteGuardado = acudienteRepository.save(acudiente);

        return acudienteMapper.toResponse(acudienteGuardado);
    }

    // Actualizar un acudiente existente
    @Transactional
    public AcudienteResponse actualizar(Long id, AcudienteRequest request) {
        Acudiente acudiente = acudienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontro el acudiente con ID: " + id));

        // Validar unicidad de documento si cambio
        if (!acudiente.getDocumento().equals(request.getDocumento())
                && acudienteRepository.existsByDocumento(request.getDocumento())) {
            throw new ConflictoNegocioException("Ya existe un acudiente con el documento: " + request.getDocumento());
        }

        String correoNormalizado = normalizarCorreo(request.getCorreo());
        request.setCorreo(correoNormalizado);

        // Validar unicidad de correo si cambio (seguro contra null)
        boolean correoCambio = (correoNormalizado == null && acudiente.getCorreo() != null)
                || (correoNormalizado != null && !correoNormalizado.equalsIgnoreCase(acudiente.getCorreo()));

        if (correoCambio && correoNormalizado != null && acudienteRepository.existsByCorreo(correoNormalizado)) {
            throw new ConflictoNegocioException("Ya existe un acudiente con el correo: " + correoNormalizado);
        }

        acudienteMapper.updateEntityFromRequest(acudiente, request);
        Acudiente acudienteActualizado = acudienteRepository.save(acudiente);

        return acudienteMapper.toResponse(acudienteActualizado);
    }

    // Desactivar / Eliminar logicamente
    @Transactional
    public void desactivar(Long id) {
        Acudiente acudiente = acudienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontro el acudiente con ID: " + id));

        // Validar si es principal de algun estudiante activo
        if (estudianteAcudienteRepository.existsByAcudienteAndEsPrincipalTrueAndEstudianteActivoTrue(acudiente)) {
            throw new ConflictoNegocioException("No se puede desactivar el acudiente porque es el acudiente principal de uno o mas estudiantes activos. Primero debe designar otro acudiente principal.");
        }

        acudiente.setActivo(false);
        acudienteRepository.save(acudiente);
    }

    // Deshacer eliminacion / Restaurar acudiente
    @Transactional
    public void restaurar(Long id) {
        Acudiente acudiente = acudienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontro el acudiente con ID: " + id));

        acudiente.setActivo(true);
        acudienteRepository.save(acudiente);
    }

    // Eliminar definitivamente de la base de datos (IRREVERSIBLE - requiere confirmacion)
    @Transactional
    public void eliminarPermanente(Long id, boolean confirmar) {
        if (!confirmar) {
            throw new IllegalArgumentException("Debe confirmar explicitamente la eliminacion definitiva del acudiente.");
        }

        Acudiente acudiente = acudienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontro el acudiente con ID: " + id));

        if (estudianteAcudienteRepository.existsByAcudiente(acudiente)) {
            throw new ConflictoNegocioException("No se puede eliminar fisicamente el acudiente porque cuenta con vinculaciones registradas. Debe utilizar la desactivacion logica.");
        }

        acudienteRepository.delete(acudiente);
    }

    private String normalizarCorreo(String correo) {
        if (correo == null || correo.isBlank()) {
            return null;
        }
        return correo.trim().toLowerCase();
    }
}
