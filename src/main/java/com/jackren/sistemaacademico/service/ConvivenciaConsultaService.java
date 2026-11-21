package com.jackren.sistemaacademico.service;

import com.jackren.sistemaacademico.dto.*;
import com.jackren.sistemaacademico.entity.*;
import com.jackren.sistemaacademico.exception.RecursoNoEncontradoException;
import com.jackren.sistemaacademico.repository.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ConvivenciaConsultaService {

    private final EstudianteRepository estudianteRepository;
    private final UsuarioRepository usuarioRepository;
    private final AcudienteRepository acudienteRepository;
    private final EstudianteAcudienteRepository estudianteAcudienteRepository;
    private final AsignacionAcademicaRepository asignacionAcademicaRepository;
    private final MatriculaRepository matriculaRepository;
    private final ObservadorService observadorService;
    private final RutaAtencionService rutaAtencionService;
    private final DebidoProcesoService debidoProcesoService;
    private final MedidaSancionService medidaSancionService;
    private final PlanContinuidadAcademicaRepository planContinuidadRepository;
    private final CasoConvivenciaRepository casoRepository;
    private final ExpedienteDebidoProcesoRepository expedienteRepository;
    private final MedidaSancionConvivenciaRepository medidaRepository;
    private final AuditoriaGlobalService auditoriaGlobalService;

    public ConvivenciaConsultaService(
            EstudianteRepository estudianteRepository,
            UsuarioRepository usuarioRepository,
            AcudienteRepository acudienteRepository,
            EstudianteAcudienteRepository estudianteAcudienteRepository,
            AsignacionAcademicaRepository asignacionAcademicaRepository,
            MatriculaRepository matriculaRepository,
            ObservadorService observadorService,
            RutaAtencionService rutaAtencionService,
            DebidoProcesoService debidoProcesoService,
            MedidaSancionService medidaSancionService,
            PlanContinuidadAcademicaRepository planContinuidadRepository,
            CasoConvivenciaRepository casoRepository,
            ExpedienteDebidoProcesoRepository expedienteRepository,
            MedidaSancionConvivenciaRepository medidaRepository,
            AuditoriaGlobalService auditoriaGlobalService) {
        this.estudianteRepository = estudianteRepository;
        this.usuarioRepository = usuarioRepository;
        this.acudienteRepository = acudienteRepository;
        this.estudianteAcudienteRepository = estudianteAcudienteRepository;
        this.asignacionAcademicaRepository = asignacionAcademicaRepository;
        this.matriculaRepository = matriculaRepository;
        this.observadorService = observadorService;
        this.rutaAtencionService = rutaAtencionService;
        this.debidoProcesoService = debidoProcesoService;
        this.medidaSancionService = medidaSancionService;
        this.planContinuidadRepository = planContinuidadRepository;
        this.casoRepository = casoRepository;
        this.expedienteRepository = expedienteRepository;
        this.medidaRepository = medidaRepository;
        this.auditoriaGlobalService = auditoriaGlobalService;
    }

    public SeguimientoConvivenciaEstudianteResponse consultarSeguimientoEstudiante(Long estudianteId, Long usuarioConsultaId) {
        Estudiante estudiante = estudianteRepository.findById(estudianteId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Estudiante no encontrado con ID: " + estudianteId));

        Usuario usuario = usuarioRepository.findById(usuarioConsultaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con ID: " + usuarioConsultaId));

        Rol rol = usuario.getRol();
        boolean esDirectivo = rol == Rol.RECTOR || rol == Rol.COORDINADOR || rol == Rol.COORDINADOR_CONVIVENCIA ||
                rol == Rol.COORDINADOR_ACADEMICO || rol == Rol.ORIENTADOR_ESCOLAR || rol == Rol.ADMINISTRADOR;

        Acudiente acudiente = null;
        if (rol == Rol.ACUDIENTE) {
            if (usuario.getPersonaId() != null) {
                acudiente = acudienteRepository.findById(usuario.getPersonaId()).orElse(null);
            }
            if (acudiente == null) {
                acudiente = acudienteRepository.findByDocumento(usuario.getUsername()).orElse(null);
            }
            if (acudiente == null) {
                throw new AccessDeniedException("No se encontró perfil de acudiente vinculado a la cuenta (E1)");
            }

            EstudianteAcudiente vinculo = estudianteAcudienteRepository.findByEstudianteAndAcudiente(estudiante, acudiente)
                    .orElseThrow(() -> new AccessDeniedException("No existe vínculo legal registrado con el estudiante (E1)"));

            // Invariante E1: Bloqueo inmediato ante restricción legal
            if (vinculo.getEstadoVinculo() == EstadoVinculo.BLOQUEADO_RESTRICCION_LEGAL) {
                throw new AccessDeniedException("Acceso bloqueado por orden judicial o restricción legal (E1, UC-PROV-05)");
            }
        } else if (rol == Rol.ESTUDIANTE) {
            if (usuario.getPersonaId() != null && !usuario.getPersonaId().equals(estudianteId)) {
                throw new AccessDeniedException("No puede consultar el expediente de convivencia de otro estudiante (E1)");
            }
        } else if (rol == Rol.PROFESOR) {
            // Verificar si es director de grupo del estudiante o docente con asignación
            List<Matricula> matriculas = matriculaRepository.findByEstudianteIdOrderByFechaMatriculaDesc(estudianteId);
            boolean autorizadoDocente = false;
            for (Matricula m : matriculas) {
                if (m.getEstado() == EstadoMatricula.MATRICULADA || m.getEstado() == EstadoMatricula.MATRICULADO_ACTIVO) {
                    if (asignacionAcademicaRepository.existsByProfesorUsuarioIdAndGrupoIdAndActivoTrue(usuarioConsultaId, m.getGrupo().getId())) {
                        autorizadoDocente = true;
                        break;
                    }
                }
            }
            if (!autorizadoDocente) {
                throw new AccessDeniedException("Docente sin asignación académica en el curso del estudiante (E3)");
            }
        } else if (!esDirectivo) {
            throw new AccessDeniedException("Rol no autorizado para consultar el seguimiento de convivencia");
        }

        // Obtener anotaciones del observador
        List<AnotacionObservadorResponse> anotaciones = observadorService.listarPorEstudiante(estudianteId);

        // Obtener casos de convivencia con filtrado y proyección minimizada (RN-09, RN-CON-10)
        List<CasoConvivenciaResponse> casos = new ArrayList<>();
        for (CasoConvivencia c : casoRepository.findByEstudianteImplicadoId(estudianteId)) {
            // Si el consultante es acudiente y el caso tiene reserva contra acudiente agresor, omitir
            if (rol == Rol.ACUDIENTE && Boolean.TRUE.equals(c.getReservaProteccionContraAcudiente())) {
                if (c.getAcudienteAgresor() != null && acudiente != null && c.getAcudienteAgresor().getId().equals(acudiente.getId())) {
                    continue; // No revelar existencia del caso al acudiente agresor (UC-23 A4)
                }
            }
            // Si es docente ordinario, no accede a Tipo III reservado (E3)
            if (rol == Rol.PROFESOR && c.getTipoSituacion() == TipoSituacionConvivencia.TIPO_III) {
                continue;
            }

            CasoConvivenciaResponse resp = rutaAtencionService.toResponse(c);
            // Proyección anonimizada de descripción si es vista externa (A1)
            if (rol == Rol.ESTUDIANTE || rol == Rol.ACUDIENTE) {
                resp = anonimizarTercerosCaso(resp);
            }
            casos.add(resp);
        }

        // Obtener expedientes de debido proceso
        List<ExpedienteDebidoProcesoResponse> expedientes = new ArrayList<>();
        for (ExpedienteDebidoProceso e : expedienteRepository.findByEstudianteId(estudianteId)) {
            // Ocultar al docente ordinario si no es directivo
            if (rol == Rol.PROFESOR) {
                continue;
            }
            ExpedienteDebidoProcesoResponse resp = debidoProcesoService.toResponse(e);
            expedientes.add(resp);
        }

        // Obtener medidas y sanciones
        List<MedidaSancionResponse> medidas = new ArrayList<>();
        for (ExpedienteDebidoProceso e : expedienteRepository.findByEstudianteId(estudianteId)) {
            for (MedidaSancionConvivencia m : medidaRepository.findByExpedienteId(e.getId())) {
                PlanContinuidadResponse plan = planContinuidadRepository.findByMedidaSancionId(m.getId())
                        .map(medidaSancionService::toPlanResponse)
                        .orElse(null);
                medidas.add(medidaSancionService.toResponse(m, plan));
            }
        }

        // Obtener planes de continuidad académica
        List<PlanContinuidadResponse> planes = planContinuidadRepository.findByEstudianteId(estudianteId).stream()
                .map(medidaSancionService::toPlanResponse)
                .toList();

        if (auditoriaGlobalService != null) {
            auditoriaGlobalService.registrarEvento(
                    "CONSULTA_SEGUIMIENTO_CONVIVENCIA",
                    "SeguimientoConvivencia",
                    estudianteId.toString(),
                    "N/A",
                    "Consultante: " + usuario.getUsername() + ", Rol: " + rol,
                    "Consulta autorizada y proyectada de convivencia escolar (UC-25)",
                    "EXITOSO"
            );
        }

        return new SeguimientoConvivenciaEstudianteResponse(
                estudiante.getId(),
                estudiante.getNombres() + " " + estudiante.getApellidos(),
                anotaciones,
                casos,
                expedientes,
                medidas,
                planes
        );
    }

    private CasoConvivenciaResponse anonimizarTercerosCaso(CasoConvivenciaResponse original) {
        // Sanitizar referencias reservadas de terceros menores
        return new CasoConvivenciaResponse(
                original.id(),
                original.codigoExpediente(),
                original.anioLectivoId(),
                original.estudianteImplicadoId(),
                original.nombreEstudiante(),
                original.tipoSituacion(),
                original.estadoCaso(),
                original.fechaHechos(),
                original.lugarHechos(),
                original.descripcionSituacion(),
                null,
                "Personal Institucional Autorizado",
                original.esReporteConfidencial(),
                original.atencionMedicaUrgente(),
                null, // Ocultar detalles médicos íntimos en proyección externa (RN-09)
                original.reservaProteccionContraAcudiente(),
                null, // Ocultar ID acudiente agresor
                original.codigoSiuce(),
                original.remitidoAutoridadExterna(),
                original.remitidoAutoridadExterna() ? "Remisión a entidad de protección externa" : null,
                original.fechaRemisionExterna(),
                null, // Ocultar radicado específico reservado
                original.fechaApertura(),
                original.fechaCierre(),
                original.motivoCierre()
        );
    }
}
