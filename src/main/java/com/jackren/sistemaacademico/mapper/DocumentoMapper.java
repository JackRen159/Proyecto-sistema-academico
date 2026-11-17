package com.jackren.sistemaacademico.mapper;

import com.jackren.sistemaacademico.dto.ConfiguracionConsecutivoResponse;
import com.jackren.sistemaacademico.dto.RegistroEmisionResponse;
import com.jackren.sistemaacademico.entity.ConsecutivoDocumento;
import com.jackren.sistemaacademico.entity.RegistroEmisionDocumento;
import org.springframework.stereotype.Component;

@Component
public class DocumentoMapper {

    public ConfiguracionConsecutivoResponse toConsecutivoResponse(ConsecutivoDocumento entidad) {
        if (entidad == null) {
            return null;
        }

        ConfiguracionConsecutivoResponse dto = new ConfiguracionConsecutivoResponse();
        dto.setId(entidad.getId());
        if (entidad.getAnioLectivo() != null) {
            dto.setAnioLectivoId(entidad.getAnioLectivo().getId());
            dto.setAnioLectivo(entidad.getAnioLectivo().getAnio());
        }
        dto.setTipoDocumento(entidad.getTipoDocumento());
        dto.setPrefijo(entidad.getPrefijo());
        dto.setUltimoNumero(entidad.getUltimoNumero());
        dto.setLongitudRelleno(entidad.getLongitudRelleno());
        dto.setTextoPiePagina(entidad.getTextoPiePagina());
        dto.setFechaActualizacion(entidad.getFechaActualizacion());
        return dto;
    }

    public RegistroEmisionResponse toRegistroEmisionResponse(RegistroEmisionDocumento entidad) {
        if (entidad == null) {
            return null;
        }

        RegistroEmisionResponse dto = new RegistroEmisionResponse();
        dto.setId(entidad.getId());
        dto.setTipoDocumento(entidad.getTipoDocumento());
        dto.setNumeroConsecutivoCompleto(entidad.getNumeroConsecutivoCompleto());
        dto.setCodigoVerificacion(entidad.getCodigoVerificacion());

        if (entidad.getAnioLectivo() != null) {
            dto.setAnioLectivoId(entidad.getAnioLectivo().getId());
            dto.setAnioLectivo(entidad.getAnioLectivo().getAnio());
        }

        if (entidad.getEstudiante() != null) {
            dto.setEstudianteId(entidad.getEstudiante().getId());
            dto.setNombreEstudiante(entidad.getEstudiante().getNombres() + " " + entidad.getEstudiante().getApellidos());
            dto.setDocumentoEstudiante(entidad.getEstudiante().getDocumento());
        }

        if (entidad.getMatricula() != null) {
            dto.setMatriculaId(entidad.getMatricula().getId());
        }

        if (entidad.getPeriodoAcademico() != null) {
            dto.setPeriodoAcademicoId(entidad.getPeriodoAcademico().getId());
            dto.setNombrePeriodo(entidad.getPeriodoAcademico().getNombre());
        }

        if (entidad.getGrupo() != null) {
            dto.setGrupoId(entidad.getGrupo().getId());
            dto.setNombreGrupo(entidad.getGrupo().getNombre());
        }

        dto.setDestinatario(entidad.getDestinatario());
        dto.setMotivo(entidad.getMotivo());
        dto.setAnulado(entidad.getAnulado());
        dto.setMotivoAnulacion(entidad.getMotivoAnulacion());
        dto.setFechaAnulacion(entidad.getFechaAnulacion());
        dto.setFechaEmision(entidad.getFechaEmision());
        return dto;
    }
}
