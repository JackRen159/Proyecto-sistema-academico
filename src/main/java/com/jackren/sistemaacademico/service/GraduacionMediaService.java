package com.jackren.sistemaacademico.service;

import com.jackren.sistemaacademico.dto.*;
import com.jackren.sistemaacademico.entity.*;
import com.jackren.sistemaacademico.exception.ConflictoNegocioException;
import com.jackren.sistemaacademico.exception.RecursoNoEncontradoException;
import com.jackren.sistemaacademico.mapper.CierrePromocionMapper;
import com.jackren.sistemaacademico.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class GraduacionMediaService {

    private final TitulacionGraduandoMediaRepository titulacionRepository;
    private final MatriculaRepository matriculaRepository;
    private final PromocionEstudianteAnualRepository promocionRepository;
    private final AnioLectivoRepository anioLectivoRepository;
    private final PeriodoAcademicoRepository periodoRepository;
    private final UsuarioRepository usuarioRepository;
    private final AuditoriaGlobalService auditoriaGlobalService;
    private final CierrePromocionMapper cierrePromocionMapper;

    public GraduacionMediaService(TitulacionGraduandoMediaRepository titulacionRepository,
                                  MatriculaRepository matriculaRepository,
                                  PromocionEstudianteAnualRepository promocionRepository,
                                  AnioLectivoRepository anioLectivoRepository,
                                  PeriodoAcademicoRepository periodoRepository,
                                  UsuarioRepository usuarioRepository,
                                  AuditoriaGlobalService auditoriaGlobalService,
                                  CierrePromocionMapper cierrePromocionMapper) {
        this.titulacionRepository = titulacionRepository;
        this.matriculaRepository = matriculaRepository;
        this.promocionRepository = promocionRepository;
        this.anioLectivoRepository = anioLectivoRepository;
        this.periodoRepository = periodoRepository;
        this.usuarioRepository = usuarioRepository;
        this.auditoriaGlobalService = auditoriaGlobalService;
        this.cierrePromocionMapper = cierrePromocionMapper;
    }

    public TitulacionGraduandoResponse registrarRequisitosTitulacion(TitulacionMediaRequest request, Long usuarioId) {
        Matricula matricula = matriculaRepository.findById(request.getMatriculaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Matrícula no encontrada con ID " + request.getMatriculaId()));

        Usuario autorizador = usuarioId != null ? usuarioRepository.findById(usuarioId).orElse(null) : null;

        TitulacionGraduandoMedia tit = titulacionRepository.findByMatriculaId(matricula.getId())
                .orElseGet(TitulacionGraduandoMedia::new);

        tit.setMatricula(matricula);
        tit.setModalidadTitulo(request.getModalidadTitulo());
        tit.setEspecialidad(request.getEspecialidad());
        tit.setHorasServicioSocial(request.getHorasServicioSocial());
        tit.setSoporteServicioSocialUrl(request.getSoporteServicioSocialUrl());
        tit.setCodigoRegistroIcfes(request.getCodigoRegistroIcfes());
        tit.setDocumentoIdentidadVerificado(Boolean.TRUE.equals(request.getDocumentoIdentidadVerificado()));
        tit.setUsuarioAutorizador(autorizador);

        // Si cumple las 80 horas mínimas (Resolución 4210 de 1996) y tiene Saber 11
        if (request.getHorasServicioSocial() >= 80 && request.getCodigoRegistroIcfes() != null &&
                !request.getCodigoRegistroIcfes().trim().isEmpty() && tit.getDocumentoIdentidadVerificado()) {
            tit.setEstadoTitulacion(EstadoTitulacionGraduando.APTO_GRADUACION);
        } else {
            tit.setEstadoTitulacion(EstadoTitulacionGraduando.APROBADO_PENDIENTE_TITULACION);
        }

        TitulacionGraduandoMedia guardada = titulacionRepository.save(tit);
        return cierrePromocionMapper.toResponse(guardada);
    }

    public List<TitulacionGraduandoResponse> aprobarGraduacionMedia(AprobarGraduacionRequest request, Long usuarioId) {
        AnioLectivo anio = anioLectivoRepository.findById(request.getAnioLectivoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Año lectivo no encontrado con ID " + request.getAnioLectivoId()));

        Usuario rector = usuarioId != null ? usuarioRepository.findById(usuarioId).orElse(null) : null;
        List<TitulacionGraduandoMedia> graduandos = titulacionRepository.findByEstadoTitulacion(EstadoTitulacionGraduando.APTO_GRADUACION);

        List<TitulacionGraduandoResponse> aprobados = new ArrayList<>();
        int folio = 1;

        for (TitulacionGraduandoMedia g : graduandos) {
            // UC-PROV-24 E1: Comprobar que esté promovido académicamente en 11.°
            PromocionEstudianteAnual prom = promocionRepository.findByMatriculaId(g.getMatricula().getId())
                    .orElseThrow(() -> new ConflictoNegocioException("El estudiante no tiene decisión de promoción anual registrada (UC-PROV-24 E1)"));

            if (prom.getEstadoPromocion() != EstadoPromocionEstudiante.PROMOVIDO_DIRECTO &&
                    prom.getEstadoPromocion() != EstadoPromocionEstudiante.PROMOVIDO_POR_RECUPERACION_FINAL) {
                throw new ConflictoNegocioException("No se puede graduar al estudiante " + g.getMatricula().getNumeroMatricula() +
                        ": su estado anual es " + prom.getEstadoPromocion() + " (UC-PROV-24 E1, Decreto 1290 Art. 18)");
            }

            g.setEstadoTitulacion(EstadoTitulacionGraduando.GRADUADO_EGRESADO);
            g.setNumeroActaGeneral(request.getNumeroActaGeneral());
            g.setNumeroActaIndividual(String.format("IND-%s-%03d", anio.getAnio(), folio));
            g.setNumeroDiploma(String.format("DIP-%s-%04d", anio.getAnio(), folio));
            g.setNumeroLibro(request.getNumeroLibro());
            g.setNumeroFolio(String.format("%03d", folio));
            g.setFechaGraduacion(request.getFechaGraduacion() != null ? request.getFechaGraduacion() : LocalDate.now());
            g.setUsuarioAutorizador(rector);

            // Transicionar estado de matrícula
            g.getMatricula().setEstado(EstadoMatricula.FINALIZADA);
            matriculaRepository.save(g.getMatricula());

            TitulacionGraduandoMedia guardada = titulacionRepository.save(g);
            aprobados.add(cierrePromocionMapper.toResponse(guardada));
            folio++;
        }

        if (auditoriaGlobalService != null) {
            auditoriaGlobalService.registrarEvento(
                    "APROBACION_ACTA_GRADUACION_MEDIA",
                    "TitulacionGraduandoMedia",
                    request.getNumeroActaGeneral(),
                    "Estado: APTO_GRADUACION",
                    "Estado: GRADUADO_EGRESADO",
                    "Aprobada acta general con " + aprobados.size() + " bachilleres titulados",
                    "EXITOSO"
            );
        }

        return aprobados;
    }

    public void cerrarAnioLectivoEscolar(Long anioLectivoId, Long usuarioId) {
        AnioLectivo anio = anioLectivoRepository.findById(anioLectivoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Año lectivo no encontrado con ID " + anioLectivoId));

        if (anio.getEstado() == EstadoAnioLectivo.CERRADO) {
            throw new ConflictoNegocioException("El año lectivo ya se encuentra CERRADO");
        }

        // UC-PROV-24 E3: Comprobar que no existan periodos abiertos
        List<PeriodoAcademico> periodos = periodoRepository.findByAnioLectivoIdOrderByNumeroAsc(anio.getId());
        boolean hayPeriodosAbiertos = periodos.stream().anyMatch(p -> p.getEstado() != EstadoPeriodo.CERRADO);
        if (hayPeriodosAbiertos) {
            throw new ConflictoNegocioException("No se puede cerrar el año escolar: existen periodos académicos abiertos (UC-PROV-24 E3)");
        }

        // Comprobar que no existan estudiantes en PENDIENTE_RECUPERACION_FINAL
        long pendientesHabilitacion = promocionRepository
                .countByActaPromocionAnioLectivoIdAndEstadoPromocion(anio.getId(), EstadoPromocionEstudiante.PENDIENTE_RECUPERACION_FINAL);
        if (pendientesHabilitacion > 0) {
            throw new ConflictoNegocioException("No se puede cerrar el año escolar: existen " + pendientesHabilitacion +
                    " estudiantes pendientes de habilitación/recuperación final (UC-PROV-24 E3, BRE-020)");
        }

        anio.setEstado(EstadoAnioLectivo.CERRADO);
        anioLectivoRepository.save(anio);

        if (auditoriaGlobalService != null) {
            auditoriaGlobalService.registrarEvento(
                    "CIERRE_DEFINITIVO_ANIO_LECTIVO",
                    "AnioLectivo",
                    anio.getId().toString(),
                    "Estado: ACTIVO",
                    "Estado: CERRADO",
                    "Cierre definitivo anual con decisiones de promoción y graduación en firme",
                    "EXITOSO"
            );
        }
    }
}
