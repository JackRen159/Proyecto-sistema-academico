package jackren.sistemaacademico.main.service;

import jackren.sistemaacademico.main.dto.AcudienteRequest;
import jackren.sistemaacademico.main.dto.AcudienteResponse;
import jackren.sistemaacademico.main.entity.Acudiente;
import jackren.sistemaacademico.main.mapper.AcudienteMapper;
import jackren.sistemaacademico.main.repository.AcudienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class AcudienteService {

    private final AcudienteRepository acudienteRepository;
    private final AcudienteMapper acudienteMapper;

    public AcudienteService(AcudienteRepository acudienteRepository, AcudienteMapper acudienteMapper) {
        this.acudienteRepository = acudienteRepository;
        this.acudienteMapper = acudienteMapper;
    }

    // Obtener todos los acudientes activos
    public List<AcudienteResponse> listarTodos() {
        return acudienteRepository.findByActivoTrue()
                .stream()
                .map(acudienteMapper::toResponse)
                .toList();
    }

    // Buscar un acudiente por ID
    public Optional<AcudienteResponse> buscarPorId(Long id) {
        return acudienteRepository.findById(id)
                .map(acudienteMapper::toResponse);
    }

    // Buscar un acudiente por documento
    public Optional<AcudienteResponse> buscarPorDocumento(String documento) {
        return acudienteRepository.findByDocumento(documento)
                .map(acudienteMapper::toResponse);
    }

    // Crear un acudiente
    @Transactional
    public AcudienteResponse guardar(AcudienteRequest request) {
        if (acudienteRepository.existsByDocumento(request.getDocumento())) {
            throw new IllegalArgumentException("Ya existe un acudiente con el documento: " + request.getDocumento());
        }

        if (acudienteRepository.existsByCorreo(request.getCorreo())) {
            throw new IllegalArgumentException("Ya existe un acudiente con el correo: " + request.getCorreo());
        }

        Acudiente acudiente = acudienteMapper.toEntity(request);
        Acudiente acudienteGuardado = acudienteRepository.save(acudiente);

        return acudienteMapper.toResponse(acudienteGuardado);
    }

    // Actualizar un acudiente existente
    @Transactional
    public AcudienteResponse actualizar(Long id, AcudienteRequest request) {
        Acudiente acudiente = acudienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el acudiente con ID: " + id));

        // Validar unicidad de documento si cambió
        if (!acudiente.getDocumento().equals(request.getDocumento())
                && acudienteRepository.existsByDocumento(request.getDocumento())) {
            throw new IllegalArgumentException("Ya existe un acudiente con el documento: " + request.getDocumento());
        }

        // Validar unicidad de correo si cambió
        if (!request.getCorreo().equalsIgnoreCase(acudiente.getCorreo())
                && acudienteRepository.existsByCorreo(request.getCorreo())) {
            throw new IllegalArgumentException("Ya existe un acudiente con el correo: " + request.getCorreo());
        }

        acudienteMapper.updateEntityFromRequest(acudiente, request);
        Acudiente acudienteActualizado = acudienteRepository.save(acudiente);

        return acudienteMapper.toResponse(acudienteActualizado);
    }

    // Desactivar / Eliminar lógicamente
    @Transactional
    public void desactivar(Long id) {
        Acudiente acudiente = acudienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el acudiente con ID: " + id));

        acudiente.setActivo(false);
        acudienteRepository.save(acudiente);
    }

    // Deshacer eliminación / Restaurar acudiente
    @Transactional
    public void restaurar(Long id) {
        Acudiente acudiente = acudienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el acudiente con ID: " + id));

        acudiente.setActivo(true);
        acudienteRepository.save(acudiente);
    }

    // Eliminar definitivamente de la base de datos (IRREVERSIBLE - requiere confirmación)
    @Transactional
    public void eliminarPermanente(Long id, boolean confirmar) {
        if (!confirmar) {
            throw new IllegalArgumentException("Debe confirmar explícitamente la eliminación definitiva del acudiente.");
        }

        if (!acudienteRepository.existsById(id)) {
            throw new IllegalArgumentException("No se encontró el acudiente con ID: " + id);
        }

        acudienteRepository.deleteById(id);
    }
}
