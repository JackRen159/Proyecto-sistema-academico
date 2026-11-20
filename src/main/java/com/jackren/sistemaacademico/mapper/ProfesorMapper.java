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
        if (request.getEstadoAdministrativo() != null) {
            profesor.setEstadoAdministrativo(request.getEstadoAdministrativo());
        }
        if (request.getTipoVinculacion() != null) {
            profesor.setTipoVinculacion(request.getTipoVinculacion());
        }
        if (request.getHorasSemanalesOrdinarias() != null) {
            profesor.setHorasSemanalesOrdinarias(request.getHorasSemanalesOrdinarias());
        }
        if (request.getHorasExtrasPermitidas() != null) {
            profesor.setHorasExtrasPermitidas(request.getHorasExtrasPermitidas());
        }
        profesor.setVerificadoInhabilidades(Boolean.TRUE.equals(request.getVerificadoInhabilidades()));
        profesor.setFechaVerificacionInhabilidades(request.getFechaVerificacionInhabilidades());
        profesor.setNumeroCertificadoInhabilidades(request.getNumeroCertificadoInhabilidades());
        profesor.setFechaInicioSituacion(request.getFechaInicioSituacion());
        profesor.setFechaFinSituacion(request.getFechaFinSituacion());
        profesor.setMotivoSituacion(request.getMotivoSituacion());
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
        if (request.getEstadoAdministrativo() != null) {
            profesor.setEstadoAdministrativo(request.getEstadoAdministrativo());
        }
        if (request.getTipoVinculacion() != null) {
            profesor.setTipoVinculacion(request.getTipoVinculacion());
        }
        if (request.getHorasSemanalesOrdinarias() != null) {
            profesor.setHorasSemanalesOrdinarias(request.getHorasSemanalesOrdinarias());
        }
        if (request.getHorasExtrasPermitidas() != null) {
            profesor.setHorasExtrasPermitidas(request.getHorasExtrasPermitidas());
        }
        if (request.getVerificadoInhabilidades() != null) {
            profesor.setVerificadoInhabilidades(request.getVerificadoInhabilidades());
        }
        if (request.getFechaVerificacionInhabilidades() != null) {
            profesor.setFechaVerificacionInhabilidades(request.getFechaVerificacionInhabilidades());
        }
        if (request.getNumeroCertificadoInhabilidades() != null) {
            profesor.setNumeroCertificadoInhabilidades(request.getNumeroCertificadoInhabilidades());
        }
        if (request.getFechaInicioSituacion() != null) {
            profesor.setFechaInicioSituacion(request.getFechaInicioSituacion());
        }
        if (request.getFechaFinSituacion() != null) {
            profesor.setFechaFinSituacion(request.getFechaFinSituacion());
        }
        if (request.getMotivoSituacion() != null) {
            profesor.setMotivoSituacion(request.getMotivoSituacion());
        }
    }

    public ProfesorResponse toResponse(Profesor profesor) {
        return toResponse(profesor, 0);
    }

    public ProfesorResponse toResponse(Profesor profesor, int horasAsignadas) {
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

        response.setEstadoAdministrativo(profesor.getEstadoAdministrativo());
        response.setTipoVinculacion(profesor.getTipoVinculacion());
        response.setHorasSemanalesOrdinarias(profesor.getHorasSemanalesOrdinarias());
        response.setHorasExtrasPermitidas(profesor.getHorasExtrasPermitidas());
        response.setHorasAsignadasActuales(horasAsignadas);
        response.setVerificadoInhabilidades(profesor.getVerificadoInhabilidades());
        response.setFechaVerificacionInhabilidades(profesor.getFechaVerificacionInhabilidades());
        response.setNumeroCertificadoInhabilidades(profesor.getNumeroCertificadoInhabilidades());
        response.setFechaInicioSituacion(profesor.getFechaInicioSituacion());
        response.setFechaFinSituacion(profesor.getFechaFinSituacion());
        response.setMotivoSituacion(profesor.getMotivoSituacion());

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
