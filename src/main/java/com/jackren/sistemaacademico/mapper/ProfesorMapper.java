package com.jackren.sistemaacademico.mapper;

import com.jackren.sistemaacademico.dto.ProfesorRequest;
import com.jackren.sistemaacademico.dto.ProfesorResponse;
import com.jackren.sistemaacademico.entity.Profesor;
import com.jackren.sistemaacademico.entity.TipoDocumento;
import com.jackren.sistemaacademico.entity.Usuario;
import org.springframework.stereotype.Component;

@Component
public class ProfesorMapper {

    public Profesor toEntity(ProfesorRequest request, Usuario usuario) {
        if (request == null) {
            return null;
        }

        Profesor profesor = new Profesor();
        profesor.setUsuario(usuario);
        profesor.setTipoDocumento(request.getTipoDocumento() != null ? request.getTipoDocumento() : TipoDocumento.CC);
        profesor.setDocumento(request.getDocumento());
        profesor.setNombres(request.getNombres());
        profesor.setApellidos(request.getApellidos());
        profesor.setCorreo(request.getCorreo());
        profesor.setTelefono(request.getTelefono());
        profesor.setDireccion(request.getDireccion());
        profesor.setTituloAcademico(request.getTituloAcademico());
        profesor.setEspecialidad(request.getEspecialidad());
        profesor.setActivo(true);
        return profesor;
    }

    public void updateEntityFromRequest(Profesor profesor, ProfesorRequest request, Usuario usuario) {
        if (profesor == null || request == null) {
            return;
        }

        profesor.setUsuario(usuario);
        if (request.getTipoDocumento() != null) {
            profesor.setTipoDocumento(request.getTipoDocumento());
        }
        profesor.setDocumento(request.getDocumento());
        profesor.setNombres(request.getNombres());
        profesor.setApellidos(request.getApellidos());
        profesor.setCorreo(request.getCorreo());
        profesor.setTelefono(request.getTelefono());
        profesor.setDireccion(request.getDireccion());
        profesor.setTituloAcademico(request.getTituloAcademico());
        profesor.setEspecialidad(request.getEspecialidad());
    }

    public ProfesorResponse toResponse(Profesor profesor) {
        if (profesor == null) {
            return null;
        }

        ProfesorResponse response = new ProfesorResponse();
        response.setId(profesor.getId());
        response.setTipoDocumento(profesor.getTipoDocumento());
        response.setDocumento(profesor.getDocumento());
        response.setNombres(profesor.getNombres());
        response.setApellidos(profesor.getApellidos());
        response.setNombreCompleto(profesor.getNombres() + " " + profesor.getApellidos());
        response.setCorreo(profesor.getCorreo());
        response.setTelefono(profesor.getTelefono());
        response.setDireccion(profesor.getDireccion());
        response.setTituloAcademico(profesor.getTituloAcademico());
        response.setEspecialidad(profesor.getEspecialidad());
        response.setActivo(profesor.getActivo());
        response.setFechaCreacion(profesor.getFechaCreacion());
        response.setFechaActualizacion(profesor.getFechaActualizacion());

        if (profesor.getUsuario() != null) {
            response.setUsuarioId(profesor.getUsuario().getId());
            response.setUsername(profesor.getUsuario().getUsername());
        }

        return response;
    }
}
