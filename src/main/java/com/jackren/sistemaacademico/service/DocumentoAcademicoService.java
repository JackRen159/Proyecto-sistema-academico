package com.jackren.sistemaacademico.service;

import com.jackren.sistemaacademico.dto.*;
import com.jackren.sistemaacademico.entity.*;
import com.jackren.sistemaacademico.exception.ConflictoNegocioException;
import com.jackren.sistemaacademico.exception.RecursoNoEncontradoException;
import com.jackren.sistemaacademico.mapper.DocumentoMapper;
import com.jackren.sistemaacademico.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class DocumentoAcademicoService {

    private final ConsecutivoDocumentoRepository consecutivoRepository;
    private final RegistroEmisionDocumentoRepository emisionRepository;
    private final MatriculaRepository matriculaRepository;
    private final PeriodoAcademicoRepository periodoRepository;
    private final AnioLectivoRepository anioLectivoRepository;
    private final GradoAsignaturaRepository gradoAsignaturaRepository;
    private final AsignacionAcademicaRepository asignacionRepository;
    private final CalificacionPeriodoRepository calificacionPeriodoRepository;
    private final RegistroAsistenciaRepository registroAsistenciaRepository;
    private final ConfiguracionEscalaSieeRepository escalaSieeRepository;
    private final DocumentoMapper documentoMapper;

    @Autowired(required = false)
    private InstitucionDatosLegalesRepository institucionDatosLegalesRepository;

    @Autowired(required = false)
    private PromocionEstudianteAnualRepository promocionEstudianteAnualRepository;

    @Autowired(required = false)
    private AuditoriaGlobalService auditoriaGlobalService;

    @Autowired(required = false)
    private EstudianteAcudienteRepository estudianteAcudienteRepository;

    @Autowired(required = false)
    private AcudienteRepository acudienteRepository;

    @Autowired(required = false)
    private CasoConvivenciaRepository casoConvivenciaRepository;

    // Metadatos institucionales por defecto
    private static final String INSTITUCION_NOMBRE = "Colegio Integrado San José";
    private static final String INSTITUCION_NIT = "890.123.456-7";
    private static final String INSTITUCION_DANE = "111001012345";
    private static final String INSTITUCION_RESOLUCION = "Resolución No. 004523 del 15 de Noviembre de 2018 - Secretaría de Educación";

    public DocumentoAcademicoService(ConsecutivoDocumentoRepository consecutivoRepository,
                                     RegistroEmisionDocumentoRepository emisionRepository,
                                     MatriculaRepository matriculaRepository,
                                     PeriodoAcademicoRepository periodoRepository,
                                     AnioLectivoRepository anioLectivoRepository,
                                     GradoAsignaturaRepository gradoAsignaturaRepository,
                                     AsignacionAcademicaRepository asignacionRepository,
                                     CalificacionPeriodoRepository calificacionPeriodoRepository,
                                     RegistroAsistenciaRepository registroAsistenciaRepository,
                                     ConfiguracionEscalaSieeRepository escalaSieeRepository,
                                     DocumentoMapper documentoMapper) {
        this.consecutivoRepository = consecutivoRepository;
        this.emisionRepository = emisionRepository;
        this.matriculaRepository = matriculaRepository;
        this.periodoRepository = periodoRepository;
        this.anioLectivoRepository = anioLectivoRepository;
        this.gradoAsignaturaRepository = gradoAsignaturaRepository;
        this.asignacionRepository = asignacionRepository;
        this.calificacionPeriodoRepository = calificacionPeriodoRepository;
        this.registroAsistenciaRepository = registroAsistenciaRepository;
        this.escalaSieeRepository = escalaSieeRepository;
        this.documentoMapper = documentoMapper;
    }

    @Autowired
    public DocumentoAcademicoService(ConsecutivoDocumentoRepository consecutivoRepository,
                                     RegistroEmisionDocumentoRepository emisionRepository,
                                     MatriculaRepository matriculaRepository,
                                     PeriodoAcademicoRepository periodoRepository,
                                     AnioLectivoRepository anioLectivoRepository,
                                     GradoAsignaturaRepository gradoAsignaturaRepository,
                                     AsignacionAcademicaRepository asignacionRepository,
                                     CalificacionPeriodoRepository calificacionPeriodoRepository,
                                     RegistroAsistenciaRepository registroAsistenciaRepository,
                                     ConfiguracionEscalaSieeRepository escalaSieeRepository,
                                     DocumentoMapper documentoMapper,
                                     InstitucionDatosLegalesRepository institucionDatosLegalesRepository,
                                     PromocionEstudianteAnualRepository promocionEstudianteAnualRepository,
                                     AuditoriaGlobalService auditoriaGlobalService,
                                     EstudianteAcudienteRepository estudianteAcudienteRepository,
                                     AcudienteRepository acudienteRepository,
                                     CasoConvivenciaRepository casoConvivenciaRepository) {
        this(consecutivoRepository, emisionRepository, matriculaRepository, periodoRepository,
             anioLectivoRepository, gradoAsignaturaRepository, asignacionRepository,
             calificacionPeriodoRepository, registroAsistenciaRepository, escalaSieeRepository, documentoMapper);
        this.institucionDatosLegalesRepository = institucionDatosLegalesRepository;
        this.promocionEstudianteAnualRepository = promocionEstudianteAnualRepository;
        this.auditoriaGlobalService = auditoriaGlobalService;
        this.estudianteAcudienteRepository = estudianteAcudienteRepository;
        this.acudienteRepository = acudienteRepository;
        this.casoConvivenciaRepository = casoConvivenciaRepository;
    }

    // =========================================================================
    // 0. CONTROL CONTEXTUAL DE AUTORIZACIÓN (UC-05, UC-11, RN-DOC-04)
    // =========================================================================

    public void validarAccesoContextualPorMatricula(Long matriculaId, Usuario usuario) {
        if (usuario == null) return;
        Matricula m = matriculaRepository.findById(matriculaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Matrícula no encontrada con ID: " + matriculaId));
        validarAccesoContextual(m.getEstudiante().getId(), usuario);
    }

    public void validarAccesoContextual(Long estudianteId, Usuario usuario) {
        if (usuario == null) return;
        Rol rol = usuario.getRol();
        if (rol == Rol.ADMINISTRADOR || rol == Rol.RECTOR || rol == Rol.COORDINADOR ||
            rol == Rol.COORDINADOR_ACADEMICO || rol == Rol.COORDINADOR_CONVIVENCIA || rol == Rol.SECRETARIA) {
            return;
        }

        if (rol == Rol.ESTUDIANTE) {
            if (usuario.getPersonaId() != null && !usuario.getPersonaId().equals(estudianteId)) {
                throw new AccessDeniedException("No puede consultar ni emitir documentos de otro estudiante (E2, RN-DOC-04)");
            }
            return;
        }

        if (rol == Rol.ACUDIENTE) {
            Acudiente acudiente = null;
            if (acudienteRepository != null) {
                if (usuario.getPersonaId() != null) {
                    acudiente = acudienteRepository.findById(usuario.getPersonaId()).orElse(null);
                }
                if (acudiente == null) {
                    acudiente = acudienteRepository.findByDocumento(usuario.getUsername()).orElse(null);
                }
            }
            if (acudiente == null) {
                throw new AccessDeniedException("No se encontró perfil de acudiente vinculado a la cuenta (E1)");
            }

            Estudiante estudiante = matriculaRepository.findByEstudianteIdOrderByFechaMatriculaDesc(estudianteId).stream()
                    .findFirst().map(Matricula::getEstudiante).orElse(null);
            if (estudiante == null) {
                estudiante = new Estudiante();
                estudiante.setId(estudianteId);
            }

            if (estudianteAcudienteRepository != null) {
                EstudianteAcudiente vinculo = estudianteAcudienteRepository.findByEstudianteAndAcudiente(estudiante, acudiente)
                        .orElseThrow(() -> new AccessDeniedException("No existe vínculo legal registrado con el estudiante (E1, RN-DOC-04)"));

                if (vinculo.getEstadoVinculo() == EstadoVinculo.BLOQUEADO_RESTRICCION_LEGAL) {
                    throw new AccessDeniedException("Acceso bloqueado por orden judicial o restricción legal (E1, UC-PROV-05, RN-DOC-04)");
                }
            }
            return;
        }

        if (rol == Rol.PROFESOR) {
            List<Matricula> matriculas = matriculaRepository.findByEstudianteIdOrderByFechaMatriculaDesc(estudianteId);
            boolean asignado = false;
            for (Matricula m : matriculas) {
                if (m.getEstado() == EstadoMatricula.MATRICULADA || m.getEstado() == EstadoMatricula.MATRICULADO_ACTIVO) {
                    if (asignacionRepository.existsByProfesorUsuarioIdAndGrupoIdAndActivoTrue(usuario.getId(), m.getGrupo().getId())) {
                        asignado = true;
                        break;
                    }
                }
            }
            if (!asignado) {
                throw new AccessDeniedException("Docente sin asignación académica en el curso del estudiante (E2)");
            }
            return;
        }

        throw new AccessDeniedException("Rol no autorizado para acceder a documentos del estudiante");
    }

    private InstitucionDatosLegales obtenerInstitucion() {
        if (institucionDatosLegalesRepository != null) {
            Optional<InstitucionDatosLegales> instOpt = institucionDatosLegalesRepository.findFirstByActivoTrue();
            if (instOpt.isPresent()) {
                InstitucionDatosLegales inst = instOpt.get();
                if (inst.getNombreOficial() != null && !inst.getNombreOficial().isBlank()) {
                    return inst;
                }
            }
        }
        InstitucionDatosLegales fallback = new InstitucionDatosLegales();
        fallback.setNombreOficial(INSTITUCION_NOMBRE);
        fallback.setNit(INSTITUCION_NIT);
        fallback.setCodigoDane(INSTITUCION_DANE);
        fallback.setResolucionAprobacion(INSTITUCION_RESOLUCION);
        fallback.setNombreRectorFirmante("Rector Institucional");
        fallback.setNombreSecretarioFirmante("Secretaria Académica");
        return fallback;
    }

    public void validarDatosLegalesParaCertificacion() {
        if (institucionDatosLegalesRepository != null) {
            InstitucionDatosLegales inst = institucionDatosLegalesRepository.findFirstByActivoTrue().orElse(null);
            if (inst == null || inst.getCodigoDane() == null || inst.getCodigoDane().isBlank() ||
                inst.getNit() == null || inst.getNit().isBlank() ||
                inst.getResolucionAprobacion() == null || inst.getResolucionAprobacion().isBlank()) {
                throw new ConflictoNegocioException(
                        "Faltan datos oficiales del establecimiento o firmas autorizadas en configuración institucional (E1, RN-CFG-08)"
                );
            }
        }
    }

    // =========================================================================
    // 1. GESTIÓN DE CONSECUTIVOS Y FOLIADOS POR AÑO LECTIVO
    // =========================================================================

    public ConfiguracionConsecutivoResponse guardarOActualizarConsecutivo(ConfiguracionConsecutivoRequest request) {
        AnioLectivo anioLectivo = anioLectivoRepository.findById(request.getAnioLectivoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Año lectivo no encontrado con ID: " + request.getAnioLectivoId()));

        ConsecutivoDocumento consecutivo = consecutivoRepository
                .findByAnioLectivoIdAndTipoDocumento(request.getAnioLectivoId(), request.getTipoDocumento())
                .orElse(new ConsecutivoDocumento());

        consecutivo.setAnioLectivo(anioLectivo);
        consecutivo.setTipoDocumento(request.getTipoDocumento());
        consecutivo.setPrefijo(request.getPrefijo() != null ? request.getPrefijo().trim().toUpperCase() : "");
        consecutivo.setUltimoNumero(request.getUltimoNumero() != null ? request.getUltimoNumero() : 0L);
        consecutivo.setLongitudRelleno(request.getLongitudRelleno() != null ? request.getLongitudRelleno() : 5);
        consecutivo.setTextoPiePagina(request.getTextoPiePagina());

        ConsecutivoDocumento guardado = consecutivoRepository.save(consecutivo);

        if (auditoriaGlobalService != null) {
            auditoriaGlobalService.registrarEvento(
                    "CONFIGURACION_CONSECUTIVO_DOCUMENTO",
                    "ConsecutivoDocumento",
                    guardado.getId().toString(),
                    "N/A",
                    "Prefijo: " + guardado.getPrefijo() + ", Tipo: " + guardado.getTipoDocumento(),
                    "Configuración formal de foliado anual atómico",
                    "EXITOSO"
            );
        }

        return documentoMapper.toConsecutivoResponse(guardado);
    }

    @Transactional(readOnly = true)
    public List<ConfiguracionConsecutivoResponse> obtenerConsecutivosPorAnio(Long anioLectivoId) {
        return consecutivoRepository.findByAnioLectivoId(anioLectivoId).stream()
                .map(documentoMapper::toConsecutivoResponse)
                .collect(Collectors.toList());
    }

    private ConsecutivoDocumento obtenerOCrearConsecutivo(AnioLectivo anioLectivo, TipoDocumentoAcademico tipo) {
        return consecutivoRepository.findByAnioLectivoIdAndTipoDocumentoConBloqueo(anioLectivo.getId(), tipo)
                .orElseGet(() -> {
                    String prefijo = switch (tipo) {
                        case BOLETIN_PERIODO -> "BOL";
                        case BOLETIN_FINAL -> "BFIN";
                        case CERTIFICADO_ESTUDIO -> "CERT";
                        case CERTIFICADO_NOTAS_PARCIALES_RETIRO -> "CPAR";
                        case CONSTANCIA_MATRICULA -> "CONST";
                        case ACTA_COMISION -> "ACTA";
                        case COPIA_ACTA_INDIVIDUAL_GRADO -> "AGRD";
                    };
                    ConsecutivoDocumento nuevo = new ConsecutivoDocumento(
                            anioLectivo, tipo, prefijo, 0L, 5, "Documento oficial del Sistema Escolar"
                    );
                    return consecutivoRepository.save(nuevo);
                });
    }

    // =========================================================================
    // 2. GENERACIÓN Y CONSOLIDACIÓN DE BOLETINES PERIÓDICOS SIEE
    // =========================================================================

    public BoletinPeriodoResponse generarBoletinPeriodo(Long matriculaId,
                                                        Long periodoAcademicoId,
                                                        EmisionDocumentoRequest request,
                                                        Usuario usuarioEmisor) {
        Matricula matricula = matriculaRepository.findById(matriculaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Matrícula no encontrada con ID: " + matriculaId));

        PeriodoAcademico periodo = periodoRepository.findById(periodoAcademicoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Periodo académico no encontrado con ID: " + periodoAcademicoId));

        if (!periodo.getAnioLectivo().getId().equals(matricula.getAnioLectivo().getId())) {
            throw new ConflictoNegocioException("El periodo académico no corresponde al año lectivo de la matrícula");
        }

        InstitucionDatosLegales institucion = obtenerInstitucion();

        ConsecutivoDocumento consecutivo = obtenerOCrearConsecutivo(matricula.getAnioLectivo(), TipoDocumentoAcademico.BOLETIN_PERIODO);
        String numeroConsecutivo = consecutivo.incrementarYFormatear(matricula.getAnioLectivo().getAnio());
        String codigoVerificacion = UUID.randomUUID().toString();

        String destinatario = (request != null && request.getDestinatario() != null && !request.getDestinatario().isBlank())
                ? request.getDestinatario().trim()
                : "A quien interese";
        String motivo = (request != null && request.getMotivo() != null)
                ? request.getMotivo().trim()
                : "Boletín periódico oficial de calificaciones SIEE";

        RegistroEmisionDocumento emision = new RegistroEmisionDocumento(
                consecutivo,
                TipoDocumentoAcademico.BOLETIN_PERIODO,
                numeroConsecutivo,
                matricula.getAnioLectivo(),
                matricula.getEstudiante(),
                matricula,
                periodo,
                matricula.getGrupo(),
                codigoVerificacion,
                usuarioEmisor,
                destinatario,
                motivo,
                null
        );

        // Versionamiento ante regeneración (RN-DOC-03)
        emisionRepository.findByMatriculaIdAndPeriodoAcademicoIdAndTipoDocumentoAndAnuladoFalse(
                matriculaId, periodoAcademicoId, TipoDocumentoAcademico.BOLETIN_PERIODO
        ).ifPresent(previa -> {
            emision.setNumeroVersion(previa.getNumeroVersion() + 1);
            emision.setEmisionPrevia(previa);
            previa.setEstadoEmision(EstadoEmisionDocumento.HISTORICA_REEMPLAZADA_POR_RECTIFICACION);
            emisionRepository.save(previa);
        });

        emisionRepository.save(emision);

        ConfiguracionEscalaSiee escalaSiee = escalaSieeRepository.findByAnioLectivoId(matricula.getAnioLectivo().getId())
                .orElseGet(() -> new ConfiguracionEscalaSiee(
                        matricula.getAnioLectivo(),
                        new BigDecimal("1.00"),
                        new BigDecimal("5.00"),
                        new BigDecimal("3.00"),
                        new BigDecimal("3.00"),
                        new BigDecimal("4.00"),
                        new BigDecimal("4.60")
                ));

        Long gradoId = matricula.getGrupo().getGrado().getId();
        List<GradoAsignatura> gradoAsignaturas = gradoAsignaturaRepository.findByGradoIdAndActivoTrue(gradoId);

        List<CalificacionPeriodo> calificaciones = calificacionPeriodoRepository
                .findByMatriculaIdAndPeriodoAcademicoId(matriculaId, periodoAcademicoId);
        Map<Long, CalificacionPeriodo> califMap = calificaciones.stream()
                .collect(Collectors.toMap(c -> c.getAsignatura().getId(), c -> c, (c1, c2) -> c1));

        List<RegistroAsistencia> asistencias = registroAsistenciaRepository
                .findByMatriculaIdAndSesionAsistenciaPeriodoAcademicoId(matriculaId, periodoAcademicoId);

        List<AsignacionAcademica> asignaciones = asignacionRepository.findByGrupoIdAndActivoTrue(matricula.getGrupo().getId());
        Map<Long, AsignacionAcademica> asignacionMap = asignaciones.stream()
                .collect(Collectors.toMap(a -> a.getAsignatura().getId(), a -> a, (a1, a2) -> a1));

        String directorGrupo = asignacionRepository.findByGrupoIdAndEsDirectorGrupoTrueAndActivoTrue(matricula.getGrupo().getId())
                .map(a -> a.getProfesor().getNombres() + " " + a.getProfesor().getApellidos())
                .orElse("No asignado");

        Map<Area, List<GradoAsignatura>> asignaturasPorArea = gradoAsignaturas.stream()
                .collect(Collectors.groupingBy(ga -> ga.getAsignatura().getArea(), LinkedHashMap::new, Collectors.toList()));

        List<AreaBoletinDTO> areasDTO = new ArrayList<>();
        BigDecimal sumaTotalNotas = BigDecimal.ZERO;
        int totalAsignaturasEvaluadas = 0;
        int aprobadas = 0;
        int reprobadas = 0;
        long totalInasistencias = 0;
        long totalRetardos = 0;

        for (Map.Entry<Area, List<GradoAsignatura>> entry : asignaturasPorArea.entrySet()) {
            Area area = entry.getKey();
            List<GradoAsignatura> gas = entry.getValue();

            AreaBoletinDTO areaDTO = new AreaBoletinDTO();
            areaDTO.setAreaId(area.getId());
            areaDTO.setNombreArea(area.getNombre());

            List<AsignaturaBoletinDTO> asignaturasDTO = new ArrayList<>();
            BigDecimal sumaArea = BigDecimal.ZERO;
            int conteoArea = 0;

            for (GradoAsignatura ga : gas) {
                Asignatura asig = ga.getAsignatura();
                AsignacionAcademica asigAcad = asignacionMap.get(asig.getId());
                String docente = asigAcad != null && asigAcad.getProfesor() != null
                        ? asigAcad.getProfesor().getNombres() + " " + asigAcad.getProfesor().getApellidos()
                        : "Sin asignar";

                CalificacionPeriodo cp = califMap.get(asig.getId());
                BigDecimal nota = cp != null ? cp.getNotaDefinitiva() : null;
                DesempenoNacional desempeno = cp != null ? cp.getDesempeno() : null;
                String observaciones = cp != null ? cp.getObservaciones() : null;

                long fallasJust = 0;
                long fallasInjust = 0;
                long ret = 0;

                for (RegistroAsistencia r : asistencias) {
                    if (r.getSesionAsistencia().getAsignacionAcademica() != null &&
                            r.getSesionAsistencia().getAsignacionAcademica().getAsignatura().getId().equals(asig.getId())) {
                        if (r.getEstado() == EstadoAsistencia.AUSENTE_JUSTIFICADA) {
                            fallasJust++;
                        } else if (r.getEstado() == EstadoAsistencia.AUSENTE_INJUSTIFICADA) {
                            fallasInjust++;
                        } else if (r.getEstado() == EstadoAsistencia.RETARDO) {
                            ret++;
                        }
                    }
                }

                totalInasistencias += (fallasJust + fallasInjust);
                totalRetardos += ret;

                if (nota != null) {
                    sumaArea = sumaArea.add(nota);
                    conteoArea++;
                    sumaTotalNotas = sumaTotalNotas.add(nota);
                    totalAsignaturasEvaluadas++;
                    if (escalaSiee.esAprobada(nota)) {
                        aprobadas++;
                    } else {
                        reprobadas++;
                    }
                    if (desempeno == null) {
                        desempeno = escalaSiee.homologarDesempeno(nota);
                    }
                }

                AsignaturaBoletinDTO asigDTO = new AsignaturaBoletinDTO(
                        asig.getId(),
                        asig.getNombre(),
                        ga.getIntensidadHoraria(),
                        docente,
                        nota,
                        desempeno,
                        fallasJust,
                        fallasInjust,
                        ret,
                        observaciones
                );
                asignaturasDTO.add(asigDTO);
            }

            areaDTO.setAsignaturas(asignaturasDTO);
            if (conteoArea > 0) {
                BigDecimal promedioArea = sumaArea.divide(BigDecimal.valueOf(conteoArea), escalaSiee.getDecimalesRedondeo(), RoundingMode.HALF_UP);
                areaDTO.setPromedioArea(promedioArea);
                areaDTO.setDesempenoArea(escalaSiee.homologarDesempeno(promedioArea));
            }
            areasDTO.add(areaDTO);
        }

        BigDecimal promedioGeneral = totalAsignaturasEvaluadas > 0
                ? sumaTotalNotas.divide(BigDecimal.valueOf(totalAsignaturasEvaluadas), escalaSiee.getDecimalesRedondeo(), RoundingMode.HALF_UP)
                : null;
        DesempenoNacional desempenoGeneral = promedioGeneral != null ? escalaSiee.homologarDesempeno(promedioGeneral) : null;

        String escalaInfo = "Escala SIEE: Bajo (" + escalaSiee.getNotaMinima() + " - " +
                escalaSiee.getEscalaBasicoMin().subtract(new BigDecimal("0.01")) + "), Básico (" +
                escalaSiee.getEscalaBasicoMin() + " - " +
                escalaSiee.getEscalaAltoMin().subtract(new BigDecimal("0.01")) + "), Alto (" +
                escalaSiee.getEscalaAltoMin() + " - " +
                escalaSiee.getEscalaSuperiorMin().subtract(new BigDecimal("0.01")) + "), Superior (" +
                escalaSiee.getEscalaSuperiorMin() + " - " + escalaSiee.getNotaMaxima() + "). Nota aprobatoria: " +
                escalaSiee.getNotaAprobatoria();

        ResumenBoletinDTO resumenDTO = new ResumenBoletinDTO(
                promedioGeneral,
                desempenoGeneral,
                gradoAsignaturas.size(),
                aprobadas,
                reprobadas,
                totalInasistencias,
                totalRetardos,
                null,
                escalaInfo
        );

        Estudiante est = matricula.getEstudiante();
        BoletinPeriodoResponse response = new BoletinPeriodoResponse();
        response.setCodigoVerificacion(codigoVerificacion);
        response.setNumeroConsecutivo(numeroConsecutivo);
        response.setFechaEmision(emision.getFechaEmision());
        response.setNombreInstitucion(institucion.getNombreOficial());
        response.setNitInstitucion(institucion.getNit());
        response.setDaneInstitucion(institucion.getCodigoDane());
        response.setResolucionAprobacion(institucion.getResolucionAprobacion());

        response.setEstudianteId(est.getId());
        response.setNombreEstudiante(est.getNombres() + " " + est.getApellidos());
        response.setTipoDocumentoEstudiante("DOCUMENTO");
        response.setNumeroDocumentoEstudiante(est.getDocumento());
        response.setCodigoMatricula(matricula.getNumeroMatricula());

        response.setAnioLectivo(matricula.getAnioLectivo().getAnio());
        response.setGrado(matricula.getGrupo().getGrado().getNombre());
        response.setGrupo(matricula.getGrupo().getNombre());
        response.setJornada(matricula.getGrupo().getJornada() != null ? matricula.getGrupo().getJornada().name() : "MAÑANA");

        response.setPeriodoEvaluado(periodo.getNombre());
        response.setNumeroPeriodo(periodo.getNumero());
        response.setDirectorGrupo(directorGrupo);

        response.setAreas(areasDTO);
        response.setResumen(resumenDTO);

        if (auditoriaGlobalService != null) {
            auditoriaGlobalService.registrarEvento(
                    "EMISION_BOLETIN_PERIODO",
                    "RegistroEmisionDocumento",
                    emision.getId() != null ? emision.getId().toString() : "N/A",
                    "N/A",
                    "Consecutivo: " + numeroConsecutivo + ", Estudiante: " + est.getId(),
                    "Emisión oficial de boletín de periodo " + periodo.getNumero(),
                    "EXITOSO"
            );
        }

        return response;
    }

    // =========================================================================
    // 3. GENERACIÓN DE CONSTANCIA DE MATRÍCULA
    // =========================================================================

    public ConstanciaMatriculaResponse generarConstanciaMatricula(Long matriculaId,
                                                                 EmisionDocumentoRequest request,
                                                                 Usuario usuarioEmisor) {
        Matricula matricula = matriculaRepository.findById(matriculaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Matrícula no encontrada con ID: " + matriculaId));

        InstitucionDatosLegales institucion = obtenerInstitucion();

        ConsecutivoDocumento consecutivo = obtenerOCrearConsecutivo(matricula.getAnioLectivo(), TipoDocumentoAcademico.CONSTANCIA_MATRICULA);
        String numeroConsecutivo = consecutivo.incrementarYFormatear(matricula.getAnioLectivo().getAnio());
        String codigoVerificacion = UUID.randomUUID().toString();

        String destinatario = (request != null && request.getDestinatario() != null && !request.getDestinatario().isBlank())
                ? request.getDestinatario().trim()
                : "A quien interese";
        String motivo = (request != null && request.getMotivo() != null) ? request.getMotivo().trim() : "Trámites personales";

        RegistroEmisionDocumento emision = new RegistroEmisionDocumento(
                consecutivo,
                TipoDocumentoAcademico.CONSTANCIA_MATRICULA,
                numeroConsecutivo,
                matricula.getAnioLectivo(),
                matricula.getEstudiante(),
                matricula,
                null,
                matricula.getGrupo(),
                codigoVerificacion,
                usuarioEmisor,
                destinatario,
                motivo,
                null
        );
        emisionRepository.save(emision);

        Estudiante est = matricula.getEstudiante();
        String textoCertificacion = "La Rectoría y Secretaría del " + institucion.getNombreOficial() +
                " certifican que el(la) estudiante " + est.getNombres().toUpperCase() + " " + est.getApellidos().toUpperCase() +
                ", identificado(a) con documento " + est.getDocumento() +
                ", se encuentra formalmente matriculado(a) y cursando el grado " +
                matricula.getGrupo().getGrado().getNombre().toUpperCase() +
                " (Grupo " + matricula.getGrupo().getNombre() + ") en la jornada " +
                (matricula.getGrupo().getJornada() != null ? matricula.getGrupo().getJornada().name() : "MAÑANA") +
                " durante el año lectivo " + matricula.getAnioLectivo().getAnio() +
                ", con estado de matrícula " + matricula.getEstado().name() + ".";

        ConstanciaMatriculaResponse response = new ConstanciaMatriculaResponse();
        response.setCodigoVerificacion(codigoVerificacion);
        response.setNumeroConsecutivo(numeroConsecutivo);
        response.setFechaEmision(emision.getFechaEmision());
        response.setNombreInstitucion(institucion.getNombreOficial());
        response.setNitInstitucion(institucion.getNit());
        response.setDaneInstitucion(institucion.getCodigoDane());
        response.setResolucionAprobacion(institucion.getResolucionAprobacion());

        response.setEstudianteId(est.getId());
        response.setNombreEstudiante(est.getNombres() + " " + est.getApellidos());
        response.setTipoDocumentoEstudiante("DOCUMENTO");
        response.setNumeroDocumentoEstudiante(est.getDocumento());
        response.setCodigoMatricula(matricula.getNumeroMatricula());

        response.setAnioLectivo(matricula.getAnioLectivo().getAnio());
        response.setGrado(matricula.getGrupo().getGrado().getNombre());
        response.setGrupo(matricula.getGrupo().getNombre());
        response.setJornada(matricula.getGrupo().getJornada() != null ? matricula.getGrupo().getJornada().name() : "MAÑANA");
        response.setEstadoMatricula(matricula.getEstado().name());
        response.setDestinatario(destinatario);
        response.setMotivo(motivo);
        response.setTextoCertificacion(textoCertificacion);

        if (auditoriaGlobalService != null) {
            auditoriaGlobalService.registrarEvento(
                    "EMISION_CONSTANCIA_MATRICULA",
                    "RegistroEmisionDocumento",
                    emision.getId() != null ? emision.getId().toString() : "N/A",
                    "N/A",
                    "Consecutivo: " + numeroConsecutivo + ", Estudiante: " + est.getId(),
                    "Emisión oficial de constancia de matrícula",
                    "EXITOSO"
            );
        }

        return response;
    }

    // =========================================================================
    // 4. GENERACIÓN DE CERTIFICADO OFICIAL DE ESTUDIO (UC-PROV-27, RN-DOC-01..03)
    // =========================================================================

    public CertificadoEstudioResponse generarCertificadoEstudio(Long matriculaId,
                                                               EmisionDocumentoRequest request,
                                                               Usuario usuarioEmisor) {
        Matricula matricula = matriculaRepository.findById(matriculaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Matrícula no encontrada con ID: " + matriculaId));

        // Invariante E2: Rechazo si el año lectivo solicitado aún se encuentra abierto
        EstadoAnioLectivo estadoAnio = matricula.getAnioLectivo().getEstado();
        if (estadoAnio != null && estadoAnio != EstadoAnioLectivo.CERRADO && estadoAnio != EstadoAnioLectivo.CERRADO_HISTORICO) {
            throw new ConflictoNegocioException(
                    "El año lectivo solicitado aún se encuentra abierto; no se puede emitir certificado definitivo anual (UC-PROV-27 E2). Utilice una Constancia de Estudio o Certificado de Notas Parciales."
            );
        }

        InstitucionDatosLegales institucion = obtenerInstitucion();

        ConsecutivoDocumento consecutivo = obtenerOCrearConsecutivo(matricula.getAnioLectivo(), TipoDocumentoAcademico.CERTIFICADO_ESTUDIO);
        String numeroConsecutivo = consecutivo.incrementarYFormatear(matricula.getAnioLectivo().getAnio());
        String codigoVerificacion = UUID.randomUUID().toString();

        String destinatario = (request != null && request.getDestinatario() != null && !request.getDestinatario().isBlank())
                ? request.getDestinatario().trim()
                : "A quien interese";
        String motivo = (request != null && request.getMotivo() != null) ? request.getMotivo().trim() : "Certificado de estudios oficial";

        RegistroEmisionDocumento emision = new RegistroEmisionDocumento(
                consecutivo,
                TipoDocumentoAcademico.CERTIFICADO_ESTUDIO,
                numeroConsecutivo,
                matricula.getAnioLectivo(),
                matricula.getEstudiante(),
                matricula,
                null,
                matricula.getGrupo(),
                codigoVerificacion,
                usuarioEmisor,
                destinatario,
                motivo,
                null
        );

        // Versionamiento ante regeneración por rectificación (A3, RN-DOC-03)
        emisionRepository.findByMatriculaIdAndTipoDocumentoAndAnuladoFalse(
                matriculaId, TipoDocumentoAcademico.CERTIFICADO_ESTUDIO
        ).ifPresent(previa -> {
            emision.setNumeroVersion(previa.getNumeroVersion() + 1);
            emision.setEmisionPrevia(previa);
            previa.setEstadoEmision(EstadoEmisionDocumento.HISTORICA_REEMPLAZADA_POR_RECTIFICACION);
            emisionRepository.save(previa);
        });

        emisionRepository.save(emision);

        ConfiguracionEscalaSiee escalaSiee = escalaSieeRepository.findByAnioLectivoId(matricula.getAnioLectivo().getId())
                .orElseGet(() -> new ConfiguracionEscalaSiee(
                        matricula.getAnioLectivo(),
                        new BigDecimal("1.00"),
                        new BigDecimal("5.00"),
                        new BigDecimal("3.00"),
                        new BigDecimal("3.00"),
                        new BigDecimal("4.00"),
                        new BigDecimal("4.60")
                ));

        List<GradoAsignatura> gradoAsignaturas = gradoAsignaturaRepository.findByGradoIdAndActivoTrue(matricula.getGrupo().getGrado().getId());
        List<CalificacionPeriodo> todasCalificaciones = calificacionPeriodoRepository.findByMatriculaId(matriculaId);

        Map<Long, List<CalificacionPeriodo>> califPorAsig = todasCalificaciones.stream()
                .collect(Collectors.groupingBy(c -> c.getAsignatura().getId()));

        List<AsignaturaCertificadoDTO> asignaturasDTO = new ArrayList<>();
        BigDecimal sumaPromedios = BigDecimal.ZERO;
        int asignaturasConNota = 0;
        int asignaturasReprobadas = 0;

        for (GradoAsignatura ga : gradoAsignaturas) {
            Asignatura asig = ga.getAsignatura();
            List<CalificacionPeriodo> cps = califPorAsig.getOrDefault(asig.getId(), Collections.emptyList());

            BigDecimal promedioAsig = null;
            DesempenoNacional desempeno = null;
            String estado = "PENDIENTE";

            if (!cps.isEmpty()) {
                BigDecimal suma = cps.stream()
                        .map(CalificacionPeriodo::getNotaDefinitiva)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                promedioAsig = suma.divide(BigDecimal.valueOf(cps.size()), escalaSiee.getDecimalesRedondeo(), RoundingMode.HALF_UP);
                desempeno = escalaSiee.homologarDesempeno(promedioAsig);
                boolean aprobada = escalaSiee.esAprobada(promedioAsig);
                estado = aprobada ? "APROBADA" : "REPROBADA";

                sumaPromedios = sumaPromedios.add(promedioAsig);
                asignaturasConNota++;
                if (!aprobada) {
                    asignaturasReprobadas++;
                }
            }

            asignaturasDTO.add(new AsignaturaCertificadoDTO(
                    asig.getNombre(),
                    ga.getIntensidadHoraria(),
                    promedioAsig,
                    desempeno,
                    estado
            ));
        }

        BigDecimal promedioFinal = asignaturasConNota > 0
                ? sumaPromedios.divide(BigDecimal.valueOf(asignaturasConNota), escalaSiee.getDecimalesRedondeo(), RoundingMode.HALF_UP)
                : null;
        DesempenoNacional desempenoFinal = promedioFinal != null ? escalaSiee.homologarDesempeno(promedioFinal) : null;

        String resultadoPromocion = "EN CURSO";
        if (promocionEstudianteAnualRepository != null) {
            Optional<PromocionEstudianteAnual> promOpt = promocionEstudianteAnualRepository.findByMatriculaId(matriculaId);
            if (promOpt.isPresent()) {
                resultadoPromocion = promOpt.get().getEstadoPromocion().name();
            } else if (asignaturasConNota > 0) {
                resultadoPromocion = asignaturasReprobadas == 0 ? "PROMOVIDO(A)" : "NO PROMOVIDO(A)";
            }
        } else if (asignaturasConNota > 0) {
            resultadoPromocion = asignaturasReprobadas == 0 ? "PROMOVIDO(A)" : "NO PROMOVIDO(A)";
        }

        Estudiante est = matricula.getEstudiante();
        CertificadoEstudioResponse response = new CertificadoEstudioResponse();
        response.setCodigoVerificacion(codigoVerificacion);
        response.setNumeroConsecutivo(numeroConsecutivo);
        response.setFechaEmision(emision.getFechaEmision());
        response.setNombreInstitucion(institucion.getNombreOficial());
        response.setNitInstitucion(institucion.getNit());
        response.setDaneInstitucion(institucion.getCodigoDane());
        response.setResolucionAprobacion(institucion.getResolucionAprobacion());

        response.setEstudianteId(est.getId());
        response.setNombreEstudiante(est.getNombres() + " " + est.getApellidos());
        response.setTipoDocumentoEstudiante("DOCUMENTO");
        response.setNumeroDocumentoEstudiante(est.getDocumento());

        response.setAnioLectivo(matricula.getAnioLectivo().getAnio());
        response.setGrado(matricula.getGrupo().getGrado().getNombre());
        response.setJornada(matricula.getGrupo().getJornada() != null ? matricula.getGrupo().getJornada().name() : "MAÑANA");

        response.setAsignaturas(asignaturasDTO);
        response.setPromedioFinal(promedioFinal);
        response.setDesempenoFinal(desempenoFinal);
        response.setResultadoPromocion(resultadoPromocion);
        response.setDestinatario(destinatario);
        response.setMotivo(motivo);

        if (auditoriaGlobalService != null) {
            auditoriaGlobalService.registrarEvento(
                    "EMISION_CERTIFICADO_ESTUDIO",
                    "RegistroEmisionDocumento",
                    emision.getId() != null ? emision.getId().toString() : "N/A",
                    "N/A",
                    "Consecutivo: " + numeroConsecutivo + ", Estudiante: " + est.getId() + ", Fallo: " + resultadoPromocion,
                    "Emisión oficial de certificado de estudio anual (Decreto 1290)",
                    "EXITOSO"
            );
        }

        return response;
    }

    // =========================================================================
    // 5. CERTIFICADO OFICIAL DE NOTAS PARCIALES POR RETIRO (UC-PROV-27 A1, RN-DOC-02)
    // =========================================================================

    public CertificadoEstudioResponse generarCertificadoNotasParciales(Long matriculaId,
                                                                      EmisionDocumentoRequest request,
                                                                      Usuario usuarioEmisor) {
        Matricula matricula = matriculaRepository.findById(matriculaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Matrícula no encontrada con ID: " + matriculaId));

        InstitucionDatosLegales institucion = obtenerInstitucion();

        ConsecutivoDocumento consecutivo = obtenerOCrearConsecutivo(matricula.getAnioLectivo(), TipoDocumentoAcademico.CERTIFICADO_NOTAS_PARCIALES_RETIRO);
        String numeroConsecutivo = consecutivo.incrementarYFormatear(matricula.getAnioLectivo().getAnio());
        String codigoVerificacion = UUID.randomUUID().toString();

        String destinatario = (request != null && request.getDestinatario() != null && !request.getDestinatario().isBlank())
                ? request.getDestinatario().trim()
                : "Institución educativa receptora";
        String motivo = (request != null && request.getMotivo() != null)
                ? request.getMotivo().trim()
                : "Certificado de notas parciales por retiro/traslado";

        RegistroEmisionDocumento emision = new RegistroEmisionDocumento(
                consecutivo,
                TipoDocumentoAcademico.CERTIFICADO_NOTAS_PARCIALES_RETIRO,
                numeroConsecutivo,
                matricula.getAnioLectivo(),
                matricula.getEstudiante(),
                matricula,
                null,
                matricula.getGrupo(),
                codigoVerificacion,
                usuarioEmisor,
                destinatario,
                motivo,
                null
        );
        emisionRepository.save(emision);

        ConfiguracionEscalaSiee escalaSiee = escalaSieeRepository.findByAnioLectivoId(matricula.getAnioLectivo().getId())
                .orElseGet(() -> new ConfiguracionEscalaSiee(
                        matricula.getAnioLectivo(),
                        new BigDecimal("1.00"),
                        new BigDecimal("5.00"),
                        new BigDecimal("3.00"),
                        new BigDecimal("3.00"),
                        new BigDecimal("4.00"),
                        new BigDecimal("4.60")
                ));

        List<GradoAsignatura> gradoAsignaturas = gradoAsignaturaRepository.findByGradoIdAndActivoTrue(matricula.getGrupo().getGrado().getId());
        List<CalificacionPeriodo> calificaciones = calificacionPeriodoRepository.findByMatriculaId(matriculaId);

        Map<Long, List<CalificacionPeriodo>> califPorAsig = calificaciones.stream()
                .collect(Collectors.groupingBy(c -> c.getAsignatura().getId()));

        List<AsignaturaCertificadoDTO> asignaturasDTO = new ArrayList<>();
        BigDecimal sumaPromedios = BigDecimal.ZERO;
        int asignaturasConNota = 0;

        for (GradoAsignatura ga : gradoAsignaturas) {
            Asignatura asig = ga.getAsignatura();
            List<CalificacionPeriodo> cps = califPorAsig.getOrDefault(asig.getId(), Collections.emptyList());

            BigDecimal promedioAsig = null;
            DesempenoNacional desempeno = null;
            String estado = "EN CURSO AL RETIRO";

            if (!cps.isEmpty()) {
                BigDecimal suma = cps.stream()
                        .map(CalificacionPeriodo::getNotaDefinitiva)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                promedioAsig = suma.divide(BigDecimal.valueOf(cps.size()), escalaSiee.getDecimalesRedondeo(), RoundingMode.HALF_UP);
                desempeno = escalaSiee.homologarDesempeno(promedioAsig);
                estado = escalaSiee.esAprobada(promedioAsig) ? "APROBADA PARCIAL" : "REPROBADA PARCIAL";

                sumaPromedios = sumaPromedios.add(promedioAsig);
                asignaturasConNota++;
            }

            asignaturasDTO.add(new AsignaturaCertificadoDTO(
                    asig.getNombre(),
                    ga.getIntensidadHoraria(),
                    promedioAsig,
                    desempeno,
                    estado
            ));
        }

        BigDecimal promedioFinal = asignaturasConNota > 0
                ? sumaPromedios.divide(BigDecimal.valueOf(asignaturasConNota), escalaSiee.getDecimalesRedondeo(), RoundingMode.HALF_UP)
                : null;
        DesempenoNacional desempenoFinal = promedioFinal != null ? escalaSiee.homologarDesempeno(promedioFinal) : null;

        Estudiante est = matricula.getEstudiante();
        CertificadoEstudioResponse response = new CertificadoEstudioResponse();
        response.setCodigoVerificacion(codigoVerificacion);
        response.setNumeroConsecutivo(numeroConsecutivo);
        response.setFechaEmision(emision.getFechaEmision());
        response.setNombreInstitucion(institucion.getNombreOficial());
        response.setNitInstitucion(institucion.getNit());
        response.setDaneInstitucion(institucion.getCodigoDane());
        response.setResolucionAprobacion(institucion.getResolucionAprobacion());

        response.setEstudianteId(est.getId());
        response.setNombreEstudiante(est.getNombres() + " " + est.getApellidos());
        response.setTipoDocumentoEstudiante("DOCUMENTO");
        response.setNumeroDocumentoEstudiante(est.getDocumento());

        response.setAnioLectivo(matricula.getAnioLectivo().getAnio());
        response.setGrado(matricula.getGrupo().getGrado().getNombre());
        response.setJornada(matricula.getGrupo().getJornada() != null ? matricula.getGrupo().getJornada().name() : "MAÑANA");

        response.setAsignaturas(asignaturasDTO);
        response.setPromedioFinal(promedioFinal);
        response.setDesempenoFinal(desempenoFinal);
        response.setResultadoPromocion("PERIODO_PARCIAL_POR_RETIRO");
        response.setDestinatario(destinatario);
        response.setMotivo(motivo);

        if (auditoriaGlobalService != null) {
            auditoriaGlobalService.registrarEvento(
                    "EMISION_CERTIFICADO_NOTAS_PARCIALES",
                    "RegistroEmisionDocumento",
                    emision.getId() != null ? emision.getId().toString() : "N/A",
                    "N/A",
                    "Consecutivo: " + numeroConsecutivo + ", Estudiante: " + est.getId(),
                    "Certificado de notas parciales por retiro/traslado (Decreto 1290 Art. 17)",
                    "EXITOSO"
            );
        }

        return response;
    }

    // =========================================================================
    // 6. CONSULTA PÚBLICA / VERIFICACIÓN DE AUTENTICIDAD Y ANULACIÓN
    // =========================================================================

    @Transactional(readOnly = true)
    public RegistroEmisionResponse consultarEmisionPorCodigoVerificacion(String codigoVerificacion) {
        RegistroEmisionDocumento emision = emisionRepository.findByCodigoVerificacion(codigoVerificacion)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró ningún documento con el código de verificación: " + codigoVerificacion));
        return documentoMapper.toRegistroEmisionResponse(emision);
    }

    @Transactional(readOnly = true)
    public List<RegistroEmisionResponse> consultarEmisionesPorEstudiante(Long estudianteId) {
        return emisionRepository.findByEstudianteIdOrderByFechaEmisionDesc(estudianteId).stream()
                .map(documentoMapper::toRegistroEmisionResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RegistroEmisionResponse> consultarEmisionesPorMatricula(Long matriculaId) {
        return emisionRepository.findByMatriculaIdOrderByFechaEmisionDesc(matriculaId).stream()
                .map(documentoMapper::toRegistroEmisionResponse)
                .collect(Collectors.toList());
    }

    public RegistroEmisionResponse anularEmision(Long emisionId, AnulacionEmisionRequest request, Usuario usuarioAnulador) {
        RegistroEmisionDocumento emision = emisionRepository.findById(emisionId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Emisión no encontrada con ID: " + emisionId));

        if (emision.getAnulado()) {
            throw new ConflictoNegocioException("La emisión ya se encuentra anulada previamente");
        }

        emision.setAnulado(true);
        emision.setEstadoEmision(EstadoEmisionDocumento.ANULADA);
        emision.setMotivoAnulacion(request.getMotivoAnulacion().trim());
        emision.setFechaAnulacion(java.time.LocalDateTime.now());
        emision.setUsuarioAnulador(usuarioAnulador);

        RegistroEmisionDocumento guardada = emisionRepository.save(emision);

        if (auditoriaGlobalService != null) {
            auditoriaGlobalService.registrarEvento(
                    "ANULACION_DOCUMENTO_OFICIAL",
                    "RegistroEmisionDocumento",
                    guardada.getId().toString(),
                    "Estado: " + EstadoEmisionDocumento.PUBLICADA_VIGENTE,
                    "Estado: " + EstadoEmisionDocumento.ANULADA,
                    guardada.getMotivoAnulacion(),
                    "EXITOSO"
            );
        }

        return documentoMapper.toRegistroEmisionResponse(guardada);
    }
}
