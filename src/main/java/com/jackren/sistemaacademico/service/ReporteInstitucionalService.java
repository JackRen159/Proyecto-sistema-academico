package com.jackren.sistemaacademico.service;

import com.jackren.sistemaacademico.dto.*;
import com.jackren.sistemaacademico.entity.*;
import com.jackren.sistemaacademico.exception.RecursoNoEncontradoException;
import com.jackren.sistemaacademico.exception.ValidacionException;
import com.jackren.sistemaacademico.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ReporteInstitucionalService {

    private final AnioLectivoRepository anioLectivoRepository;
    private final GradoRepository gradoRepository;
    private final GrupoRepository grupoRepository;
    private final MatriculaRepository matriculaRepository;
    private final GradoAsignaturaRepository gradoAsignaturaRepository;
    private final CalificacionPeriodoRepository calificacionPeriodoRepository;
    private final CasoConvivenciaRepository casoConvivenciaRepository;
    private final ConfiguracionEscalaSieeRepository escalaSieeRepository;
    private final AuditoriaGlobalService auditoriaGlobalService;

    public ReporteInstitucionalService(
            AnioLectivoRepository anioLectivoRepository,
            GradoRepository gradoRepository,
            GrupoRepository grupoRepository,
            MatriculaRepository matriculaRepository,
            GradoAsignaturaRepository gradoAsignaturaRepository,
            CalificacionPeriodoRepository calificacionPeriodoRepository,
            CasoConvivenciaRepository casoConvivenciaRepository,
            ConfiguracionEscalaSieeRepository escalaSieeRepository,
            AuditoriaGlobalService auditoriaGlobalService) {
        this.anioLectivoRepository = anioLectivoRepository;
        this.gradoRepository = gradoRepository;
        this.grupoRepository = grupoRepository;
        this.matriculaRepository = matriculaRepository;
        this.gradoAsignaturaRepository = gradoAsignaturaRepository;
        this.calificacionPeriodoRepository = calificacionPeriodoRepository;
        this.casoConvivenciaRepository = casoConvivenciaRepository;
        this.escalaSieeRepository = escalaSieeRepository;
        this.auditoriaGlobalService = auditoriaGlobalService;
    }

    public ReporteCoberturaSimatResponse generarReporteCoberturaSimat(Long anioLectivoId, Long usuarioId) {
        AnioLectivo anio = anioLectivoRepository.findById(anioLectivoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Año lectivo no encontrado con ID: " + anioLectivoId));

        List<Grado> grados = gradoRepository.findAll();
        List<Matricula> matriculasAnio = matriculaRepository.findByAnioLectivoIdAndEstado(anioLectivoId, EstadoMatricula.MATRICULADA);

        Map<Long, List<Matricula>> matPorGrado = matriculasAnio.stream()
                .collect(Collectors.groupingBy(m -> m.getGrupo().getGrado().getId()));

        List<ItemCoberturaGradoDTO> items = new ArrayList<>();
        int totalMatriculados = 0;
        int totalCuposGlobal = 0;

        for (Grado g : grados) {
            List<Matricula> mats = matPorGrado.getOrDefault(g.getId(), Collections.emptyList());
            int matriculadosGrado = mats.size();
            totalMatriculados += matriculadosGrado;

            // Calcular cupos desde grupos del grado
            int cuposGrado = grupoRepository.findByAnioLectivoIdAndActivoTrue(anioLectivoId).stream()
                    .filter(grp -> grp.getGrado().getId().equals(g.getId()))
                    .mapToInt(Grupo::getCupoMaximo)
                    .sum();
            if (cuposGrado == 0) cuposGrado = matriculadosGrado + 10;
            totalCuposGlobal += cuposGrado;

            int masc = (int) mats.stream()
                    .filter(m -> m.getEstudiante().getSexo() == SexoDocumental.MASCULINO)
                    .count();
            int fem = (int) mats.stream()
                    .filter(m -> m.getEstudiante().getSexo() == SexoDocumental.FEMENINO)
                    .count();

            items.add(new ItemCoberturaGradoDTO(
                    g.getId(),
                    g.getNombre(),
                    cuposGrado,
                    matriculadosGrado,
                    Math.max(0, cuposGrado - matriculadosGrado),
                    masc,
                    fem
            ));
        }

        if (auditoriaGlobalService != null) {
            auditoriaGlobalService.registrarEvento(
                    "CONSULTA_REPORTE_COBERTURA_SIMAT",
                    "ReporteInstitucional",
                    anioLectivoId.toString(),
                    "N/A",
                    "Total matriculados: " + totalMatriculados,
                    "Generación oficial de reporte de cobertura SIMAT/DANE",
                    "EXITOSO"
            );
        }

        return new ReporteCoberturaSimatResponse(
                anio.getId(),
                anio.getAnio(),
                LocalDate.now(),
                totalMatriculados,
                totalCuposGlobal,
                items
        );
    }

    public ReporteRendimientoMortalidadResponse generarReporteRendimientoMortalidad(Long anioLectivoId, Long usuarioId) {
        AnioLectivo anio = anioLectivoRepository.findById(anioLectivoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Año lectivo no encontrado con ID: " + anioLectivoId));

        ConfiguracionEscalaSiee escala = escalaSieeRepository.findByAnioLectivoId(anioLectivoId)
                .orElseGet(() -> new ConfiguracionEscalaSiee(
                        anio, new BigDecimal("1.00"), new BigDecimal("5.00"),
                        new BigDecimal("3.00"), new BigDecimal("3.00"),
                        new BigDecimal("4.00"), new BigDecimal("4.60")
                ));

        List<Matricula> matriculas = matriculaRepository.findByAnioLectivoIdAndEstado(anioLectivoId, EstadoMatricula.MATRICULADA);
        Map<Long, List<CalificacionPeriodo>> califsPorAsignatura = new HashMap<>();

        BigDecimal sumaNotasGlobal = BigDecimal.ZERO;
        int totalNotasGlobal = 0;
        int totalEstudiantesConReprobadas = 0;

        for (Matricula m : matriculas) {
            List<CalificacionPeriodo> cps = calificacionPeriodoRepository.findByMatriculaId(m.getId());
            boolean tieneReprobada = false;
            for (CalificacionPeriodo cp : cps) {
                if (cp.getNotaDefinitiva() != null) {
                    sumaNotasGlobal = sumaNotasGlobal.add(cp.getNotaDefinitiva());
                    totalNotasGlobal++;
                    califsPorAsignatura.computeIfAbsent(cp.getAsignatura().getId(), k -> new ArrayList<>()).add(cp);
                    if (!escala.esAprobada(cp.getNotaDefinitiva())) {
                        tieneReprobada = true;
                    }
                }
            }
            if (tieneReprobada) {
                totalEstudiantesConReprobadas++;
            }
        }

        BigDecimal promedioGlobal = totalNotasGlobal > 0
                ? sumaNotasGlobal.divide(BigDecimal.valueOf(totalNotasGlobal), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        BigDecimal tasaMortalidadGlobal = !matriculas.isEmpty()
                ? BigDecimal.valueOf(totalEstudiantesConReprobadas * 100.0 / matriculas.size()).setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        List<ItemMortalidadAsignaturaDTO> asignaturasCriticas = new ArrayList<>();
        for (Map.Entry<Long, List<CalificacionPeriodo>> entry : califsPorAsignatura.entrySet()) {
            List<CalificacionPeriodo> lista = entry.getValue();
            Asignatura asig = lista.get(0).getAsignatura();
            int total = lista.size();
            int reprobados = (int) lista.stream().filter(c -> !escala.esAprobada(c.getNotaDefinitiva())).count();
            BigDecimal tasa = total > 0
                    ? BigDecimal.valueOf(reprobados * 100.0 / total).setScale(2, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;

            asignaturasCriticas.add(new ItemMortalidadAsignaturaDTO(
                    asig.getId(),
                    asig.getNombre(),
                    asig.getArea() != null ? asig.getArea().getNombre() : "Área General",
                    total,
                    reprobados,
                    tasa
            ));
        }

        asignaturasCriticas.sort(Comparator.comparing(ItemMortalidadAsignaturaDTO::tasaReprobacionPorcentaje).reversed());

        if (auditoriaGlobalService != null) {
            auditoriaGlobalService.registrarEvento(
                    "CONSULTA_REPORTE_MORTALIDAD_ACADEMICA",
                    "ReporteInstitucional",
                    anioLectivoId.toString(),
                    "N/A",
                    "Tasa mortalidad global: " + tasaMortalidadGlobal + "%",
                    "Consolidación de rendimiento y mortalidad académica institucional (UC-27)",
                    "EXITOSO"
            );
        }

        return new ReporteRendimientoMortalidadResponse(
                anio.getId(),
                anio.getAnio(),
                promedioGlobal,
                tasaMortalidadGlobal,
                asignaturasCriticas
        );
    }

    public ReporteConvivenciaSiuceResponse generarReporteConvivenciaSiuce(Long anioLectivoId, Long usuarioId) {
        AnioLectivo anio = anioLectivoRepository.findById(anioLectivoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Año lectivo no encontrado con ID: " + anioLectivoId));

        List<CasoConvivencia> casos = casoConvivenciaRepository.findByAnioLectivoId(anioLectivoId);

        int total = casos.size();
        int tipoI = (int) casos.stream().filter(c -> c.getTipoSituacion() == TipoSituacionConvivencia.TIPO_I).count();
        int tipoII = (int) casos.stream().filter(c -> c.getTipoSituacion() == TipoSituacionConvivencia.TIPO_II).count();
        int tipoIII = (int) casos.stream().filter(c -> c.getTipoSituacion() == TipoSituacionConvivencia.TIPO_III).count();
        int remitidos = (int) casos.stream().filter(c -> Boolean.TRUE.equals(c.getRemitidoAutoridadExterna())).count();
        int cerrados = (int) casos.stream().filter(c -> c.getEstadoCaso() == EstadoCasoConvivencia.CERRADO_SUPERADO).count();
        int enSeguimiento = total - cerrados;

        if (auditoriaGlobalService != null) {
            auditoriaGlobalService.registrarEvento(
                    "CONSULTA_REPORTE_CONVIVENCIA_SIUCE",
                    "ReporteInstitucional",
                    anioLectivoId.toString(),
                    "N/A",
                    "Total casos: " + total + ", Tipo III: " + tipoIII,
                    "Consolidación estadística de convivencia escolar anonimizada para SIUCE (Ley 1620, RN-DOC-08)",
                    "EXITOSO"
            );
        }

        return new ReporteConvivenciaSiuceResponse(
                anio.getId(),
                anio.getAnio(),
                total,
                tipoI,
                tipoII,
                tipoIII,
                remitidos,
                cerrados,
                enSeguimiento
        );
    }

    @Transactional
    public byte[] exportarReporte(String tipoReporte, String formato, Long anioLectivoId, Long usuarioId) {
        String tipoNorm = tipoReporte.toUpperCase(Locale.ROOT);
        String formNorm = formato.toUpperCase(Locale.ROOT);

        StringBuilder sb = new StringBuilder();

        if ("COBERTURA_SIMAT".equals(tipoNorm)) {
            ReporteCoberturaSimatResponse r = generarReporteCoberturaSimat(anioLectivoId, usuarioId);
            sb.append("CODIGO_GRADO,NOMBRE_GRADO,CUPOS_TOTALES,MATRICULADOS,DISPONIBLES,MASCULINO,FEMENINO\n");
            for (ItemCoberturaGradoDTO item : r.grados()) {
                sb.append(item.gradoId()).append(",")
                  .append("\"").append(item.nombreGrado()).append("\",")
                  .append(item.cuposTotales()).append(",")
                  .append(item.matriculados()).append(",")
                  .append(item.cuposDisponibles()).append(",")
                  .append(item.masculino()).append(",")
                  .append(item.femenino()).append("\n");
            }
        } else if ("CONVIVENCIA_SIUCE".equals(tipoNorm)) {
            ReporteConvivenciaSiuceResponse r = generarReporteConvivenciaSiuce(anioLectivoId, usuarioId);
            sb.append("ANIO,TOTAL_CASOS,TIPO_I,TIPO_II,TIPO_III,REMITIDOS_EXTERNA,CERRADOS_SUPERADOS,EN_SEGUIMIENTO\n");
            sb.append(r.anio()).append(",")
              .append(r.totalCasos()).append(",")
              .append(r.casosTipoI()).append(",")
              .append(r.casosTipoII()).append(",")
              .append(r.casosTipoIII()).append(",")
              .append(r.remitidosAutoridadExterna()).append(",")
              .append(r.casosCerradosSuperados()).append(",")
              .append(r.casosEnSeguimiento()).append("\n");
        } else {
            ReporteRendimientoMortalidadResponse r = generarReporteRendimientoMortalidad(anioLectivoId, usuarioId);
            sb.append("ASIGNATURA_ID,NOMBRE_ASIGNATURA,AREA,EVALUADOS,REPROBADOS,TASA_REPROBACION_PCT\n");
            for (ItemMortalidadAsignaturaDTO item : r.asignaturasCriticas()) {
                sb.append(item.asignaturaId()).append(",")
                  .append("\"").append(item.nombreAsignatura()).append("\",")
                  .append("\"").append(item.area()).append("\",")
                  .append(item.estudiantesEvaluados()).append(",")
                  .append(item.reprobados()).append(",")
                  .append(item.tasaReprobacionPorcentaje()).append("\n");
            }
        }

        byte[] contenido = sb.toString().getBytes(StandardCharsets.UTF_8);

        if (auditoriaGlobalService != null) {
            auditoriaGlobalService.registrarEvento(
                    "EXPORTACION_REPORTE_INSTITUCIONAL",
                    "ReporteInstitucional",
                    anioLectivoId.toString(),
                    "N/A",
                    "Tipo: " + tipoNorm + ", Formato: " + formNorm + ", Tamaño: " + contenido.length + " bytes",
                    "Exportación autorizada de reporte oficial (UC-27 Paso 6)",
                    "EXITOSO"
            );
        }

        return contenido;
    }
}
