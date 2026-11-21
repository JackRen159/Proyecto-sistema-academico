package com.jackren.sistemaacademico.service;

import com.jackren.sistemaacademico.dto.ActaComiteConvivenciaRequest;
import com.jackren.sistemaacademico.dto.ActaComiteConvivenciaResponse;
import com.jackren.sistemaacademico.entity.ActaComiteConvivencia;
import com.jackren.sistemaacademico.entity.AnioLectivo;
import com.jackren.sistemaacademico.entity.TipoSesionComiteConvivencia;
import com.jackren.sistemaacademico.entity.Usuario;
import com.jackren.sistemaacademico.exception.RecursoNoEncontradoException;
import com.jackren.sistemaacademico.exception.ValidacionException;
import com.jackren.sistemaacademico.repository.ActaComiteConvivenciaRepository;
import com.jackren.sistemaacademico.repository.AnioLectivoRepository;
import com.jackren.sistemaacademico.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ComiteConvivenciaService {

    private final ActaComiteConvivenciaRepository actaRepository;
    private final AnioLectivoRepository anioLectivoRepository;
    private final UsuarioRepository usuarioRepository;
    private final AuditoriaGlobalService auditoriaGlobalService;

    public ComiteConvivenciaService(
            ActaComiteConvivenciaRepository actaRepository,
            AnioLectivoRepository anioLectivoRepository,
            UsuarioRepository usuarioRepository,
            AuditoriaGlobalService auditoriaGlobalService) {
        this.actaRepository = actaRepository;
        this.anioLectivoRepository = anioLectivoRepository;
        this.usuarioRepository = usuarioRepository;
        this.auditoriaGlobalService = auditoriaGlobalService;
    }

    public ActaComiteConvivenciaResponse registrarActa(ActaComiteConvivenciaRequest request) {
        AnioLectivo anio = anioLectivoRepository.findById(request.anioLectivoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Año lectivo no encontrado con ID: " + request.anioLectivoId()));

        Usuario secretario = usuarioRepository.findById(request.usuarioSecretarioId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario secretario no encontrado con ID: " + request.usuarioSecretarioId()));

        Usuario presidente = usuarioRepository.findById(request.usuarioPresidenteId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario presidente no encontrado con ID: " + request.usuarioPresidenteId()));

        // Invariante RN-SEG-01: Quórum e integrantes nominativos sin cuentas genéricas (Ley 1620 Art. 12)
        if (request.integrantesAsistentesJson() == null || request.integrantesAsistentesJson().trim().length() < 5) {
            throw new ValidacionException("Las actas del Comité exigen registro nominativo de los integrantes asistentes con quórum (RN-SEG-01)");
        }
        if (request.casosAnalizadosJson() == null || request.casosAnalizadosJson().trim().isEmpty()) {
            throw new ValidacionException("Debe incluir la relación de casos de convivencia analizados en la sesión");
        }
        if (request.acuerdosCompromisosJson() == null || request.acuerdosCompromisosJson().trim().isEmpty()) {
            throw new ValidacionException("Debe registrar los acuerdos y compromisos adoptados por el Comité");
        }

        ActaComiteConvivencia acta = new ActaComiteConvivencia();
        acta.setAnioLectivo(anio);
        acta.setNumeroActa(request.numeroActa().trim());
        acta.setFechaSesion(request.fechaSesion());
        acta.setHoraInicio(request.horaInicio());
        acta.setHoraFin(request.horaFin());
        acta.setLugarSesion(request.lugarSesion().trim());
        acta.setTipoSesion(request.tipoSesion() != null ? request.tipoSesion() : TipoSesionComiteConvivencia.ORDINARIA);
        acta.setIntegrantesAsistentesJson(request.integrantesAsistentesJson().trim());
        acta.setImpedimentosDeclaradosJson(request.impedimentosDeclaradosJson());
        acta.setCasosAnalizadosJson(request.casosAnalizadosJson().trim());
        acta.setAcuerdosCompromisosJson(request.acuerdosCompromisosJson().trim());
        acta.setAccionesPrevencionPromocion(request.accionesPrevencionPromocion());
        acta.setUsuarioSecretario(secretario);
        acta.setUsuarioPresidente(presidente);

        ActaComiteConvivencia guardada = actaRepository.save(acta);

        if (auditoriaGlobalService != null) {
            auditoriaGlobalService.registrarEvento(
                    "REGISTRO_ACTA_COMITE_CONVIVENCIA",
                    "ActaComiteConvivencia",
                    guardada.getId().toString(),
                    "N/A",
                    "Acta: " + guardada.getNumeroActa() + ", Sesión: " + guardada.getTipoSesion(),
                    "Sesión formal del Comité Escolar de Convivencia (Ley 1620 Art. 12)",
                    "EXITOSO"
            );
        }

        return toResponse(guardada);
    }

    @Transactional(readOnly = true)
    public ActaComiteConvivenciaResponse obtenerPorId(Long actaId) {
        ActaComiteConvivencia a = actaRepository.findById(actaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Acta de comité no encontrada con ID: " + actaId));
        return toResponse(a);
    }

    @Transactional(readOnly = true)
    public List<ActaComiteConvivenciaResponse> listarPorAnioLectivo(Long anioLectivoId) {
        return actaRepository.findByAnioLectivoId(anioLectivoId).stream()
                .map(this::toResponse)
                .toList();
    }

    public ActaComiteConvivenciaResponse toResponse(ActaComiteConvivencia a) {
        return new ActaComiteConvivenciaResponse(
                a.getId(),
                a.getAnioLectivo().getId(),
                a.getNumeroActa(),
                a.getFechaSesion(),
                a.getHoraInicio(),
                a.getHoraFin(),
                a.getLugarSesion(),
                a.getTipoSesion(),
                a.getIntegrantesAsistentesJson(),
                a.getImpedimentosDeclaradosJson(),
                a.getCasosAnalizadosJson(),
                a.getAcuerdosCompromisosJson(),
                a.getAccionesPrevencionPromocion(),
                a.getUsuarioSecretario().getId(),
                a.getUsuarioSecretario().getUsername(),
                a.getUsuarioPresidente().getId(),
                a.getUsuarioPresidente().getUsername(),
                a.getFechaRegistro()
        );
    }
}
