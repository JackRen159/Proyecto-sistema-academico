package com.jackren.sistemaacademico.service;

import com.jackren.sistemaacademico.dto.*;
import com.jackren.sistemaacademico.entity.*;
import com.jackren.sistemaacademico.exception.RecursoNoEncontradoException;
import com.jackren.sistemaacademico.repository.ConsentimientoPoliticaDatosRepository;
import com.jackren.sistemaacademico.repository.PoliticaTratamientoDatosRepository;
import com.jackren.sistemaacademico.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio de versionamiento inmutable de la Política de Tratamiento de Datos Personales y registro
 * de consentimientos informados (UC-PROV-13 A1, UC-PROV-14 A2, Ley 1581 de 2012 / Decreto 1377 de 2013).
 */
@Service
@Transactional
public class PoliticaDatosService {

    private final PoliticaTratamientoDatosRepository politicaRepository;
    private final ConsentimientoPoliticaDatosRepository consentimientoRepository;
    private final UsuarioRepository usuarioRepository;
    private final AuditoriaGlobalService auditoriaService;

    public PoliticaDatosService(PoliticaTratamientoDatosRepository politicaRepository,
                                ConsentimientoPoliticaDatosRepository consentimientoRepository,
                                UsuarioRepository usuarioRepository,
                                AuditoriaGlobalService auditoriaService) {
        this.politicaRepository = politicaRepository;
        this.consentimientoRepository = consentimientoRepository;
        this.usuarioRepository = usuarioRepository;
        this.auditoriaService = auditoriaService;
    }

    @Transactional(readOnly = true)
    public PoliticaDatosResponse obtenerPoliticaVigente() {
        PoliticaTratamientoDatos p = politicaRepository.findFirstByEsVigenteTrueOrderByNumeroVersionDesc()
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe una versión vigente de la Política de Tratamiento de Datos Personales"));
        return toResponse(p);
    }

    @Transactional(readOnly = true)
    public List<PoliticaDatosResponse> listarVersiones() {
        return politicaRepository.findAllByOrderByNumeroVersionDesc().stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Publica una nueva versión inmutable de la Política de Datos (UC-PROV-13 A1).
     * Nunca sobrescribe versiones existentes ya aceptadas por usuarios (E2).
     */
    public PoliticaDatosResponse crearNuevaVersion(CrearPoliticaDatosRequest request, String autorizador) {
        int siguienteVersion = politicaRepository.findFirstByEsVigenteTrueOrderByNumeroVersionDesc()
                .map(p -> p.getNumeroVersion() + 1)
                .orElse(1);

        if (Boolean.TRUE.equals(request.getEsVigente())) {
            // Desmarcar vigencia de versiones anteriores sin eliminarlas físicamente
            politicaRepository.findAll().forEach(p -> {
                if (Boolean.TRUE.equals(p.getEsVigente())) {
                    p.setEsVigente(false);
                    politicaRepository.save(p);
                }
            });
        }

        PoliticaTratamientoDatos nueva = new PoliticaTratamientoDatos();
        nueva.setNumeroVersion(siguienteVersion);
        nueva.setTitulo(request.getTitulo());
        nueva.setContenidoPolitica(request.getContenidoPolitica());
        nueva.setAvisoPrivacidad(request.getAvisoPrivacidad());
        nueva.setFechaVigencia(request.getFechaVigencia());
        nueva.setEsVigente(Boolean.TRUE.equals(request.getEsVigente()));
        nueva.setCambiosSustanciales(Boolean.TRUE.equals(request.getCambiosSustanciales()));
        nueva.setActaAprobacion(request.getActaAprobacion());
        nueva.setSoporteDocumentoUrl(request.getSoporteDocumentoUrl());

        PoliticaTratamientoDatos guardada = politicaRepository.save(nueva);

        auditoriaService.registrarEvento(new RegistroAuditoriaEventoRequest(
                autorizador != null ? autorizador : "SISTEMA",
                "RECTOR",
                "CONFIGURACION",
                "PUBLICAR_POLITICA_DATOS",
                "PoliticaTratamientoDatos",
                String.valueOf(guardada.getId()),
                "Publicación de versión inmutable " + guardada.getNumeroVersion() + " de la Política de Tratamiento de Datos Personales (Ley 1581)",
                ResultadoAuditoria.EXITO,
                "POL-PUB-" + guardada.getId()
        ));

        return toResponse(guardada);
    }

    /**
     * Registra el consentimiento explícito auditable de un usuario (UC-01 A1, RN-CFG-07).
     */
    public void registrarConsentimiento(Long usuarioId, RegistrarConsentimientoRequest request, String ip, String userAgent) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con ID: " + usuarioId));

        PoliticaTratamientoDatos politica = politicaRepository.findById(request.getPoliticaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Versión de política no encontrada con ID: " + request.getPoliticaId()));

        if (!consentimientoRepository.existsByUsuarioIdAndPoliticaId(usuarioId, request.getPoliticaId())) {
            ConsentimientoPoliticaDatos c = new ConsentimientoPoliticaDatos(
                    usuario,
                    politica,
                    ip != null ? ip : "127.0.0.1",
                    userAgent != null ? userAgent : "Cliente Web",
                    request.getOrigen() != null ? request.getOrigen() : OrigenConsentimiento.LOGIN_PRIMER_INGRESO
            );
            consentimientoRepository.save(c);

            auditoriaService.registrarEvento(new RegistroAuditoriaEventoRequest(
                    usuario.getUsername(),
                    usuario.getRol().name(),
                    "SEGURIDAD",
                    "CONSENTIMIENTO_POLITICA_DATOS",
                    "ConsentimientoPoliticaDatos",
                    String.valueOf(politica.getId()),
                    "Consentimiento informado otorgado para la Política Versión " + politica.getNumeroVersion() + " (Origen: " + request.getOrigen() + ")",
                    ResultadoAuditoria.EXITO,
                    "CONSENT-" + usuario.getId() + "-" + politica.getId()
            ));
        }
    }

    private PoliticaDatosResponse toResponse(PoliticaTratamientoDatos p) {
        PoliticaDatosResponse r = new PoliticaDatosResponse();
        r.setId(p.getId());
        r.setNumeroVersion(p.getNumeroVersion());
        r.setTitulo(p.getTitulo());
        r.setContenidoPolitica(p.getContenidoPolitica());
        r.setAvisoPrivacidad(p.getAvisoPrivacidad());
        r.setFechaVigencia(p.getFechaVigencia());
        r.setEsVigente(p.getEsVigente());
        r.setCambiosSustanciales(p.getCambiosSustanciales());
        r.setActaAprobacion(p.getActaAprobacion());
        r.setSoporteDocumentoUrl(p.getSoporteDocumentoUrl());
        r.setFechaCreacion(p.getFechaCreacion());
        return r;
    }
}
