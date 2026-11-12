package com.jackren.sistemaacademico.service;

import com.jackren.sistemaacademico.dto.ProfesorRequest;
import com.jackren.sistemaacademico.dto.ProfesorResponse;
import com.jackren.sistemaacademico.entity.Profesor;
import com.jackren.sistemaacademico.entity.Usuario;
import com.jackren.sistemaacademico.exception.ConflictoNegocioException;
import com.jackren.sistemaacademico.exception.RecursoNoEncontradoException;
import com.jackren.sistemaacademico.mapper.ProfesorMapper;
import com.jackren.sistemaacademico.repository.ProfesorRepository;
import com.jackren.sistemaacademico.repository.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProfesorService {

    private final ProfesorRepository profesorRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProfesorMapper profesorMapper;

    public ProfesorService(ProfesorRepository profesorRepository,
                           UsuarioRepository usuarioRepository,
                           ProfesorMapper profesorMapper) {
        this.profesorRepository = profesorRepository;
        this.usuarioRepository = usuarioRepository;
        this.profesorMapper = profesorMapper;
    }

    public ProfesorResponse crear(ProfesorRequest request) {
        if (profesorRepository.existsByDocumento(request.getDocumento())) {
            throw new ConflictoNegocioException("Ya existe un profesor registrado con el documento " + request.getDocumento());
        }

        if (profesorRepository.existsByCorreo(request.getCorreo())) {
            throw new ConflictoNegocioException("Ya existe un profesor registrado con el correo " + request.getCorreo());
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
        return profesorMapper.toResponse(guardado);
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
        return profesorMapper.toResponse(actualizado);
    }

    @Transactional(readOnly = true)
    public ProfesorResponse obtenerPorId(Long id) {
        Profesor profesor = profesorRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Profesor no encontrado con ID " + id));
        return profesorMapper.toResponse(profesor);
    }

    @Transactional(readOnly = true)
    public List<ProfesorResponse> listarTodos() {
        return profesorRepository.findAll().stream()
                .map(profesorMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProfesorResponse> listarActivos() {
        return profesorRepository.findByActivoTrue().stream()
                .map(profesorMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<ProfesorResponse> listarPaginado(Pageable pageable) {
        return profesorRepository.findByActivoTrue(pageable)
                .map(profesorMapper::toResponse);
    }

    public void desactivar(Long id) {
        Profesor profesor = profesorRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Profesor no encontrado con ID " + id));
        profesor.setActivo(false);
        profesorRepository.save(profesor);
    }
}
