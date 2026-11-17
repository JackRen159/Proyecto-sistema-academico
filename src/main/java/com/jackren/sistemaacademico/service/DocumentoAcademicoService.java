package com.jackren.sistemaacademico.service;

import com.jackren.sistemaacademico.dto.*;
import com.jackren.sistemaacademico.entity.*;
import com.jackren.sistemaacademico.exception.ConflictoNegocioException;
import com.jackren.sistemaacademico.exception.RecursoNoEncontradoException;
import com.jackren.sistemaacademico.mapper.DocumentoMapper;
import com.jackren.sistemaacademico.repository.*;
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
        consecutivo.setPrefijo(request.getPrefijo() != null ? request.getPrefijo() : "");
        consecutivo.setUltimoNumero(request.getUltimoNumero() != null ? request.getUltimoNumero() : 0L);
        consecutivo.setLongitudRelleno(request.getLongitudRelleno() != null ? request.getLongitudRelleno() : 5);
        consecutivo.setTextoPiePagina(request.getTextoPiePagina());

        ConsecutivoDocumento guardado = consecutivoRepository.save(consecutivo);
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
                        case CONSTANCIA_MATRICULA -> "CONST";
                        case ACTA_COMISION -> "ACTA";
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

        ConsecutivoDocumento consecutivo = obtenerOCrearConsecutivo(matricula.getAnioLectivo(), TipoDocumentoAcademico.BOLETIN_PERIODO);
        String numeroConsecutivo = consecutivo.incrementarYFormatear(matricula.getAnioLectivo().getAnio());
        String codigoVerificacion = UUID.randomUUID().toString();

        // Registrar auditoría de emisión
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
                request != null ? request.getDestinatario() : "A quien interese",
                request != null ? request.getMotivo() : "Boletín periódico oficial de calificaciones SIEE",
                null
        );
        emisionRepository.save(emision);

        // Consultar escala SIEE del año
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

        // Asignaturas del plan de estudios del grado
        Long gradoId = matricula.getGrupo().getGrado().getId();
        List<GradoAsignatura> gradoAsignaturas = gradoAsignaturaRepository.findByGradoIdAndActivoTrue(gradoId);

        // Calificaciones del periodo para la matrícula
        List<CalificacionPeriodo> calificaciones = calificacionPeriodoRepository
                .findByMatriculaIdAndPeriodoAcademicoId(matriculaId, periodoAcademicoId);
        Map<Long, CalificacionPeriodo> califMap = calificaciones.stream()
                .collect(Collectors.toMap(c -> c.getAsignatura().getId(), c -> c, (c1, c2) -> c1));

        // Asistencias del periodo para la matrícula
        List<RegistroAsistencia> asistencias = registroAsistenciaRepository
                .findByMatriculaIdAndSesionAsistenciaPeriodoAcademicoId(matriculaId, periodoAcademicoId);

        // Mapear docentes asignados al grupo
        List<AsignacionAcademica> asignaciones = asignacionRepository.findByGrupoIdAndActivoTrue(matricula.getGrupo().getId());
        Map<Long, AsignacionAcademica> asignacionMap = asignaciones.stream()
                .collect(Collectors.toMap(a -> a.getAsignatura().getId(), a -> a, (a1, a2) -> a1));

        // Director de grupo
        String directorGrupo = asignacionRepository.findByGrupoIdAndEsDirectorGrupoTrueAndActivoTrue(matricula.getGrupo().getId())
                .map(a -> a.getProfesor().getNombres() + " " + a.getProfesor().getApellidos())
                .orElse("No asignado");

        // Agrupar por áreas
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

                // Conteo de asistencias por asignatura
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

        BoletinPeriodoResponse response = new BoletinPeriodoResponse();
        response.setCodigoVerificacion(codigoVerificacion);
        response.setNumeroConsecutivo(numeroConsecutivo);
        response.setFechaEmision(emision.getFechaEmision());
        response.setNombreInstitucion(INSTITUCION_NOMBRE);
        response.setNitInstitucion(INSTITUCION_NIT);
        response.setDaneInstitucion(INSTITUCION_DANE);
        response.setResolucionAprobacion(INSTITUCION_RESOLUCION);

        Estudiante est = matricula.getEstudiante();
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
        String textoCertificacion = "La Rectoría y Secretaría del " + INSTITUCION_NOMBRE +
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
        response.setNombreInstitucion(INSTITUCION_NOMBRE);
        response.setNitInstitucion(INSTITUCION_NIT);
        response.setDaneInstitucion(INSTITUCION_DANE);
        response.setResolucionAprobacion(INSTITUCION_RESOLUCION);

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

        return response;
    }

    // =========================================================================
    // 4. GENERACIÓN DE CERTIFICADO OFICIAL DE ESTUDIO
    // =========================================================================

    public CertificadoEstudioResponse generarCertificadoEstudio(Long matriculaId,
                                                               EmisionDocumentoRequest request,
                                                               Usuario usuarioEmisor) {
        Matricula matricula = matriculaRepository.findById(matriculaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Matrícula no encontrada con ID: " + matriculaId));

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

        // Agrupar calificaciones por asignatura
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

        String resultadoPromocion;
        if (asignaturasConNota == 0) {
            resultadoPromocion = "EN CURSO";
        } else if (asignaturasReprobadas == 0) {
            resultadoPromocion = "PROMOVIDO(A)";
        } else {
            resultadoPromocion = "NO PROMOVIDO(A)";
        }

        Estudiante est = matricula.getEstudiante();
        CertificadoEstudioResponse response = new CertificadoEstudioResponse();
        response.setCodigoVerificacion(codigoVerificacion);
        response.setNumeroConsecutivo(numeroConsecutivo);
        response.setFechaEmision(emision.getFechaEmision());
        response.setNombreInstitucion(INSTITUCION_NOMBRE);
        response.setNitInstitucion(INSTITUCION_NIT);
        response.setDaneInstitucion(INSTITUCION_DANE);
        response.setResolucionAprobacion(INSTITUCION_RESOLUCION);

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

        return response;
    }

    // =========================================================================
    // 5. AUDITORÍA, ANULACIÓN Y CONSULTA PÚBLICA DE EMISIONES
    // =========================================================================

    @Transactional(readOnly = true)
    public RegistroEmisionResponse consultarEmisionPorCodigoVerificacion(String codigoVerificacion) {
        RegistroEmisionDocumento emision = emisionRepository.findByCodigoVerificacion(codigoVerificacion)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe ningún documento oficial emitido con el código: " + codigoVerificacion));
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

    public RegistroEmisionResponse anularEmision(Long emisionId,
                                                AnulacionEmisionRequest request,
                                                Usuario usuarioAnulador) {
        RegistroEmisionDocumento emision = emisionRepository.findById(emisionId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Emisión no encontrada con ID: " + emisionId));

        if (emision.getAnulado()) {
            throw new ConflictoNegocioException("La emisión ya se encuentra formalmente anulada desde: " + emision.getFechaAnulacion());
        }

        emision.anular(request.getMotivoAnulacion(), usuarioAnulador);
        RegistroEmisionDocumento guardado = emisionRepository.save(emision);
        return documentoMapper.toRegistroEmisionResponse(guardado);
    }
}
