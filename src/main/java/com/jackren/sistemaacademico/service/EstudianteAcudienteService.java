package com.jackren.sistemaacademico.service;

import com.jackren.sistemaacademico.dto.EstudianteAcudienteRequest;
import com.jackren.sistemaacademico.dto.EstudianteAcudienteResponse;
import com.jackren.sistemaacademico.dto.EstudianteAsociadoAcudienteResponse;
import com.jackren.sistemaacademico.dto.RegistroAuditoriaEventoRequest;
import com.jackren.sistemaacademico.dto.RegistroRestriccionLegalRequest;
import com.jackren.sistemaacademico.entity.*;
import com.jackren.sistemaacademico.exception.ConflictoException;
import com.jackren.sistemaacademico.exception.ConflictoNegocioException;
import com.jackren.sistemaacademico.exception.RecursoNoEncontradoException;
import com.jackren.sistemaacademico.mapper.EstudianteAcudienteMapper;
import com.jackren.sistemaacademico.repository.AcudienteRepository;
import com.jackren.sistemaacademico.repository.EstudianteAcudienteRepository;
import com.jackren.sistemaacademico.repository.EstudianteRepository;
import com.jackren.sistemaacademico.repository.UsuarioRepository;
import com.jackren.sistemaacademico.security.TokenRevocationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class EstudianteAcudienteService {

    private final EstudianteAcudienteRepository estudianteAcudienteRepository;
    private final EstudianteRepository estudianteRepository;
    private final AcudienteRepository acudienteRepository;
    private final EstudianteAcudienteMapper estudianteAcudienteMapper;
    private final UsuarioRepository usuarioRepository;
    private final AuditoriaGlobalService auditoriaService;
    private final TokenRevocationService tokenRevocationService;

    public EstudianteAcudienteService(EstudianteAcudienteRepository estudianteAcudienteRepository,
                                      EstudianteRepository estudianteRepository,
                                      AcudienteRepository acudienteRepository,
                                      EstudianteAcudienteMapper estudianteAcudienteMapper,
                                      UsuarioRepository usuarioRepository,
                                      AuditoriaGlobalService auditoriaService,
                                      TokenRevocationService tokenRevocationService) {
        this.estudianteAcudienteRepository = estudianteAcudienteRepository;
        this.estudianteRepository = estudianteRepository;
        this.acudienteRepository = acudienteRepository;
        this.estudianteAcudienteMapper = estudianteAcudienteMapper;
        this.usuarioRepository = usuarioRepository;
        this.auditoriaService = auditoriaService;
        this.tokenRevocationService = tokenRevocationService;
    }

    // Asociar un acudiente a un estudiante mediante sus documentos
    @Transactional
    public EstudianteAcudienteResponse asociar(EstudianteAcudienteRequest request) {
        Estudiante estudiante = estudianteRepository.findByDocumento(request.getDocumentoEstudiante())
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontro el estudiante con documento: " + request.getDocumentoEstudiante()));

        if (!estudiante.getActivo()) {
            throw new ConflictoNegocioException("El estudiante con documento " + request.getDocumentoEstudiante() + " se encuentra inactivo");
        }

        Acudiente acudiente = acudienteRepository.findByDocumento(request.getDocumentoAcudiente())
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontro el acudiente con documento: " + request.getDocumentoAcudiente()));

        if (!acudiente.getActivo()) {
            throw new ConflictoNegocioException("El acudiente con documento " + request.getDocumentoAcudiente() + " se encuentra inactivo");
        }

        if (estudianteAcudienteRepository.existsByEstudianteAndAcudiente(estudiante, acudiente)) {
            throw new ConflictoNegocioException("El acudiente con documento " + request.getDocumentoAcudiente()
                    + " ya se encuentra vinculado al estudiante con documento " + request.getDocumentoEstudiante());
        }

        // Si es el primer acudiente del estudiante, obligatoriamente es el principal
        boolean esElPrimero = estudianteAcudienteRepository.countByEstudiante(estudiante) == 0;
        boolean marcarComoPrincipal = esElPrimero || Boolean.TRUE.equals(request.getEsPrincipal());

        // Si se marca como principal, desmarcar a cualquier acudiente principal previo
        if (marcarComoPrincipal) {
            estudianteAcudienteRepository.findByEstudianteAndEsPrincipalTrue(estudiante)
                    .ifPresent(principalAnterior -> {
                        principalAnterior.setEsPrincipal(false);
                        estudianteAcudienteRepository.save(principalAnterior);
                    });
        }

        EstudianteAcudiente relacion = estudianteAcudienteMapper.toEntity(estudiante, acudiente, request);
        relacion.setEsPrincipal(marcarComoPrincipal);
        relacion.setEstadoVinculo(EstadoVinculo.VERIFICADO);

        EstudianteAcudiente guardada = estudianteAcudienteRepository.save(relacion);
        return estudianteAcudienteMapper.toResponse(guardada);
    }

    // Listar todos los acudientes de un estudiante por su documento
    public List<EstudianteAcudienteResponse> listarAcudientesPorEstudiante(String documentoEstudiante) {
        Estudiante estudiante = estudianteRepository.findByDocumento(documentoEstudiante)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontro el estudiante con documento: " + documentoEstudiante));

        return estudianteAcudienteRepository.findByEstudiante(estudiante)
                .stream()
                .map(estudianteAcudienteMapper::toResponse)
                .toList();
    }

    // Listar todos los estudiantes a cargo de un acudiente por su documento
    public List<EstudianteAcudienteResponse> listarEstudiantesPorAcudiente(String documentoAcudiente) {
        Acudiente acudiente = acudienteRepository.findByDocumento(documentoAcudiente)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontro el acudiente con documento: " + documentoAcudiente));

        return estudianteAcudienteRepository.findByAcudiente(acudiente)
                .stream()
                .map(estudianteAcudienteMapper::toResponse)
                .toList();
    }

    /**
     * Consultar estudiantes asociados al acudiente autenticado desde el token JWT (UC-06).
     * Excluye estrictamente vínculos en disputa, revocados o con restricción legal (RN-EST-02).
     */
    public List<EstudianteAsociadoAcudienteResponse> listarMisEstudiantesAsociados(String acudienteUsername) {
        Usuario usuario = usuarioRepository.findByUsername(acudienteUsername)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario acudiente no encontrado: " + acudienteUsername));

        Optional<Acudiente> acudienteOpt = Optional.empty();
        if (usuario.getPersonaTipo() == TipoPersonaUsuario.ACUDIENTE && usuario.getPersonaId() != null) {
            acudienteOpt = acudienteRepository.findById(usuario.getPersonaId());
        }
        if (acudienteOpt.isEmpty()) {
            acudienteOpt = acudienteRepository.findByDocumento(usuario.getUsername());
        }

        if (acudienteOpt.isEmpty()) {
            return Collections.emptyList();
        }

        Acudiente acudiente = acudienteOpt.get();
        List<EstudianteAcudiente> vinculosValidos = estudianteAcudienteRepository
                .findByAcudienteAndEstadoVinculo(acudiente, EstadoVinculo.VERIFICADO);

        return vinculosValidos.stream().map(v -> {
            Estudiante e = v.getEstudiante();
            EstudianteAsociadoAcudienteResponse r = new EstudianteAsociadoAcudienteResponse();
            r.setEstudianteId(e.getId());
            r.setTipoDocumento(e.getTipoDocumento());
            r.setDocumento(e.getDocumento());
            r.setNombres(e.getNombres());
            r.setApellidos(e.getApellidos());
            r.setNombreCompleto(e.getNombres() + " " + e.getApellidos());
            r.setParentesco(v.getParentesco());
            r.setEsPrincipal(v.getEsPrincipal());
            r.setEstadoVinculo(v.getEstadoVinculo());
            r.setAutorizadoRetiro(v.getAutorizadoRetiro());
            r.setEstadoMatricula(Boolean.TRUE.equals(e.getActivo()) ? "ACTIVA" : "INACTIVA");
            return r;
        }).toList();
    }

    // Cambiar el acudiente principal de un estudiante
    @Transactional
    public EstudianteAcudienteResponse cambiarAcudientePrincipal(String documentoEstudiante, String documentoNuevoPrincipal) {
        Estudiante estudiante = estudianteRepository.findByDocumento(documentoEstudiante)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontro el estudiante con documento: " + documentoEstudiante));

        Acudiente nuevoPrincipal = acudienteRepository.findByDocumento(documentoNuevoPrincipal)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontro el acudiente con documento: " + documentoNuevoPrincipal));

        EstudianteAcudiente relacionNuevoPrincipal = estudianteAcudienteRepository.findByEstudianteAndAcudiente(estudiante, nuevoPrincipal)
                .orElseThrow(() -> new ConflictoNegocioException("El acudiente no esta vinculado a este estudiante"));

        if (Boolean.TRUE.equals(relacionNuevoPrincipal.getEsPrincipal())) {
            return estudianteAcudienteMapper.toResponse(relacionNuevoPrincipal);
        }

        // Desmarcar al anterior principal
        estudianteAcudienteRepository.findByEstudianteAndEsPrincipalTrue(estudiante)
                .ifPresent(principalAnterior -> {
                    principalAnterior.setEsPrincipal(false);
                    estudianteAcudienteRepository.save(principalAnterior);
                });

        relacionNuevoPrincipal.setEsPrincipal(true);
        EstudianteAcudiente guardada = estudianteAcudienteRepository.save(relacionNuevoPrincipal);
        return estudianteAcudienteMapper.toResponse(guardada);
    }

    // Desvincular un acudiente de un estudiante sin borrado físico (RN-EST-05, RN-EST-07, RN-DOC-07)
    @Transactional
    public void desvincular(String documentoEstudiante, String documentoAcudiente) {
        Estudiante estudiante = estudianteRepository.findByDocumento(documentoEstudiante)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontro el estudiante con documento: " + documentoEstudiante));

        Acudiente acudiente = acudienteRepository.findByDocumento(documentoAcudiente)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontro el acudiente con documento: " + documentoAcudiente));

        EstudianteAcudiente relacion = estudianteAcudienteRepository.findByEstudianteAndAcudiente(estudiante, acudiente)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe vinculo entre el estudiante y el acudiente especificados"));

        // Regla institucional RN-EST-05, RN-EST-07, UC-PROV-05 E1: Nunca dejar al menor sin acudiente principal
        if (Boolean.TRUE.equals(relacion.getEsPrincipal())) {
            throw new ConflictoNegocioException("No se puede desvincular al acudiente principal. Primero debe designar y verificar atómicamente a su reemplazo (RN-EST-05, RN-EST-07, E1).");
        }

        // Transición inmutable a estado REVOCADO en lugar de borrado físico destructivo (RN-DOC-07)
        relacion.setEstadoVinculo(EstadoVinculo.REVOCADO);
        relacion.setAutorizadoRetiro(false);
        estudianteAcudienteRepository.save(relacion);
    }

    /**
     * Registro de medida de protección o restricción legal judicial/ICBF sobre un vínculo (UC-PROV-05 A3, RN-SEG-06).
     */
    @Transactional
    public void registrarRestriccionLegal(Long vinculacionId, RegistroRestriccionLegalRequest request, String autorizador) {
        EstudianteAcudiente vinculo = estudianteAcudienteRepository.findById(vinculacionId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Vínculo estudiante-acudiente no encontrado con ID: " + vinculacionId));

        if (Boolean.TRUE.equals(vinculo.getEsPrincipal())) {
            throw new ConflictoException("No es posible aplicar restricción legal al acudiente principal sin haber designado previamente a un nuevo representante legal principal verificado (UC-PROV-05 A3, E1).");
        }

        vinculo.setEstadoVinculo(EstadoVinculo.BLOQUEADO_RESTRICCION_LEGAL);
        vinculo.setAutorizadoRetiro(false);
        vinculo.setMotivoBloqueoLegal(request.getMotivoBloqueoLegal());
        estudianteAcudienteRepository.save(vinculo);

        // Expulsar forzosamente al acudiente de cualquier sesión activa en el servidor (RN-SEG-06)
        usuarioRepository.findByUsername(vinculo.getAcudiente().getDocumento()).ifPresent(u -> {
            tokenRevocationService.revokeUserTokens(u.getId(), Instant.now());
        });

        auditoriaService.registrarEvento(new RegistroAuditoriaEventoRequest(
                autorizador != null ? autorizador : "RECTOR",
                "RECTOR",
                "ESTUDIANTES",
                "BLOQUEO_RESTRICCION_LEGAL",
                "EstudianteAcudiente",
                String.valueOf(vinculo.getId()),
                "Medida cautelar registrada. Vínculo bloqueado y retiro revocado. Motivo: " + request.getMotivoBloqueoLegal(),
                ResultadoAuditoria.EXITO,
                "LEGAL-BLOCK-" + vinculo.getId()
        ));
    }
}
