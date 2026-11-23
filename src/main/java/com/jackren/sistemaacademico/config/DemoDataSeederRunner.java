package com.jackren.sistemaacademico.config;

import com.jackren.sistemaacademico.entity.*;
import com.jackren.sistemaacademico.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Precargador de datos de demostración y simulación para la presentación de los 7 casos de uso
 * (Rama: demo/presentacion-7-casos).
 *
 * Crea de forma idempotente y normativamente consistente:
 * 1. Usuarios con roles (ADMINISTRADOR, RECTOR, COORDINADOR, SECRETARIA, PROFESOR, ESTUDIANTE, ACUDIENTE).
 * 2. Estudiante "Sofía Herrera" y su Acudiente Principal "Marta Gómez" con Habeas Data firmado (UC-12).
 * 3. Año Lectivo 2026 ACTIVO con 4 periodos que suman 100% y Escala SIEE 1.00-5.00 (UC-14).
 * 4. Grado Décimo (10°), Grupo 10-A (cupo: 35) y Asignatura Física en Ciencias Naturales (UC-14).
 * 5. Profesor "Albert Einstein" con verificación Ley 1918 (UC-15).
 */
@Component
@Order(10)
public class DemoDataSeederRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DemoDataSeederRunner.class);

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final PoliticaTratamientoDatosRepository politicaRepository;
    private final AnioLectivoRepository anioLectivoRepository;
    private final PeriodoAcademicoRepository periodoRepository;
    private final ConfiguracionEscalaSieeRepository escalaRepository;
    private final GradoRepository gradoRepository;
    private final GrupoRepository grupoRepository;
    private final AreaRepository areaRepository;
    private final AsignaturaRepository asignaturaRepository;
    private final GradoAsignaturaRepository gradoAsignaturaRepository;
    private final ProfesorRepository profesorRepository;
    private final EstudianteRepository estudianteRepository;
    private final AcudienteRepository acudienteRepository;
    private final EstudianteAcudienteRepository estudianteAcudienteRepository;
    private final AutorizacionTratamientoDatosMenorRepository autorizacionMenorRepository;
    private final AsignacionAcademicaRepository asignacionAcademicaRepository;
    private final MatriculaRepository matriculaRepository;
    private final ActividadEvaluativaRepository actividadEvaluativaRepository;

    @Value("${demo.seed.enabled:true}")
    private boolean enabled;

    public DemoDataSeederRunner(UsuarioRepository usuarioRepository,
                                PasswordEncoder passwordEncoder,
                                PoliticaTratamientoDatosRepository politicaRepository,
                                AnioLectivoRepository anioLectivoRepository,
                                PeriodoAcademicoRepository periodoRepository,
                                ConfiguracionEscalaSieeRepository escalaRepository,
                                GradoRepository gradoRepository,
                                GrupoRepository grupoRepository,
                                AreaRepository areaRepository,
                                AsignaturaRepository asignaturaRepository,
                                GradoAsignaturaRepository gradoAsignaturaRepository,
                                ProfesorRepository profesorRepository,
                                EstudianteRepository estudianteRepository,
                                AcudienteRepository acudienteRepository,
                                EstudianteAcudienteRepository estudianteAcudienteRepository,
                                AutorizacionTratamientoDatosMenorRepository autorizacionMenorRepository,
                                AsignacionAcademicaRepository asignacionAcademicaRepository,
                                MatriculaRepository matriculaRepository,
                                ActividadEvaluativaRepository actividadEvaluativaRepository) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.politicaRepository = politicaRepository;
        this.anioLectivoRepository = anioLectivoRepository;
        this.periodoRepository = periodoRepository;
        this.escalaRepository = escalaRepository;
        this.gradoRepository = gradoRepository;
        this.grupoRepository = grupoRepository;
        this.areaRepository = areaRepository;
        this.asignaturaRepository = asignaturaRepository;
        this.gradoAsignaturaRepository = gradoAsignaturaRepository;
        this.profesorRepository = profesorRepository;
        this.estudianteRepository = estudianteRepository;
        this.acudienteRepository = acudienteRepository;
        this.estudianteAcudienteRepository = estudianteAcudienteRepository;
        this.autorizacionMenorRepository = autorizacionMenorRepository;
        this.asignacionAcademicaRepository = asignacionAcademicaRepository;
        this.matriculaRepository = matriculaRepository;
        this.actividadEvaluativaRepository = actividadEvaluativaRepository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (!enabled) {
            log.info("[DEMO SEEDER] Precarga de datos desactivada por configuración.");
            return;
        }

        log.info("[DEMO SEEDER] Inicializando precondiciones para la simulación de los 7 Casos de Uso...");

        // 1. Usuarios y Roles Institucionales
        Usuario rector = seedUsuario("rector1", "Rector123*", Rol.RECTOR, false);
        Usuario coordinador = seedUsuario("coordinador1", "Coord123*", Rol.COORDINADOR, false);
        Usuario secretaria = seedUsuario("secretaria1", "Secretaria123*", Rol.SECRETARIA, false);
        Usuario profeUser = seedUsuario("profesor1", "Profe123*", Rol.PROFESOR, false);
        Usuario estudianteUser = seedUsuario("estudiante1", "Estudiante123*", Rol.ESTUDIANTE, false);
        Usuario acudienteUser = seedUsuario("acudiente1", "Acudiente123*", Rol.ACUDIENTE, false);
        seedUsuario("usuario.temporal", "Temp123*", Rol.ESTUDIANTE, true);

        // 2. Política de Datos Personales (Ley 1581)
        PoliticaTratamientoDatos politica = seedPoliticaDatos();

        // 3. Estructura Académica: Año 2026, Escala SIEE y Periodos
        AnioLectivo anio2026 = seedAnioLectivo2026();
        seedEscalaSiee(anio2026);
        PeriodoAcademico periodo1 = seedPeriodos(anio2026);

        // 4. Grado 10°, Grupo 10-A, Área y Asignatura
        Grado grado10 = seedGrado10();
        Grupo grupo10A = seedGrupo10A(anio2026, grado10);
        Area areaNaturales = seedAreaNaturales();
        Asignatura fisica = seedAsignaturaFisica(areaNaturales);
        seedPlanEstudios(grado10, fisica, anio2026);

        // 5. Expediente Docente: Albert Einstein y Carga Académica (UC-15)
        Profesor albert = seedProfesor(profeUser);
        AsignacionAcademica asigAlbert = seedAsignacion(albert, grupo10A, fisica);
        seedActividadEvaluativa(asigAlbert, periodo1);

        // 6. Expediente Escolar Base Matriculado (Lucas Martínez en 10-A)
        seedExpedienteLucasMatriculado(anio2026, grupo10A, politica);

        // 7. Expediente Escolar Listo para Matrícula en Vivo (Sofía Herrera - UC-12 / UC-13)
        seedExpedienteSofia(politica);

        log.info("[DEMO SEEDER] Precondiciones cargadas exitosamente. Entorno listo para la demostración.");
    }

    private Usuario seedUsuario(String username, String rawPassword, Rol rol, boolean debeCambiarPassword) {
        Optional<Usuario> opt = usuarioRepository.findByUsername(username);
        if (opt.isPresent()) {
            return opt.get();
        }
        Usuario u = new Usuario(username, passwordEncoder.encode(rawPassword), rol);
        u.setDebeCambiarPassword(debeCambiarPassword);
        u.setActivo(true);
        u.setEstado(EstadoUsuario.ACTIVA);
        return usuarioRepository.save(u);
    }

    private PoliticaTratamientoDatos seedPoliticaDatos() {
        Optional<PoliticaTratamientoDatos> opt = politicaRepository.findFirstByEsVigenteTrueOrderByNumeroVersionDesc();
        if (opt.isPresent()) {
            return opt.get();
        }
        PoliticaTratamientoDatos p = new PoliticaTratamientoDatos();
        p.setNumeroVersion(1);
        p.setTitulo("Política Institucional de Tratamiento de Datos Personales V1.0");
        p.setContenidoPolitica("En cumplimiento de la Ley 1581 de 2012 y el Decreto 1377 de 2013, los datos de los estudiantes y sus familias se tratan con fines educativos y de protección integral.");
        p.setAvisoPrivacidad("Sus datos son tratados confidencialmente para fines educativos institucionales.");
        p.setFechaVigencia(LocalDate.of(2026, 1, 15));
        p.setEsVigente(true);
        p.setCambiosSustanciales(false);
        p.setActaAprobacion("Acta Consejo Directivo 01-2026");
        return politicaRepository.save(p);
    }

    private AnioLectivo seedAnioLectivo2026() {
        Optional<AnioLectivo> opt = anioLectivoRepository.findByAnio(2026);
        if (opt.isPresent()) {
            AnioLectivo a = opt.get();
            if (a.getEstado() != EstadoAnioLectivo.ACTIVO) {
                a.setEstado(EstadoAnioLectivo.ACTIVO);
                return anioLectivoRepository.save(a);
            }
            return a;
        }
        AnioLectivo anio = new AnioLectivo(2026, "Año Escolar 2026", LocalDate.of(2026, 1, 15), LocalDate.of(2026, 11, 30));
        anio.setEstado(EstadoAnioLectivo.ACTIVO);
        return anioLectivoRepository.save(anio);
    }

    private void seedEscalaSiee(AnioLectivo anio) {
        if (escalaRepository.findByAnioLectivoId(anio.getId()).isPresent()) {
            return;
        }
        ConfiguracionEscalaSiee escala = new ConfiguracionEscalaSiee(
                anio,
                new BigDecimal("1.00"),
                new BigDecimal("5.00"),
                new BigDecimal("3.00"),
                new BigDecimal("3.00"),
                new BigDecimal("4.00"),
                new BigDecimal("4.60")
        );
        escala.setDecimalesRedondeo(1);
        escala.setDescripcion("Escala institucional oficial SIEE 1.0 a 5.0 (Decreto 1290 de 2009)");
        escalaRepository.save(escala);
    }

    private PeriodoAcademico seedPeriodos(AnioLectivo anio) {
        var existentes = periodoRepository.findByAnioLectivoIdOrderByNumeroAsc(anio.getId());
        if (!existentes.isEmpty()) {
            return existentes.get(0);
        }
        PeriodoAcademico p1 = new PeriodoAcademico();
        p1.setAnioLectivo(anio);
        p1.setNumero(1);
        p1.setNombre("Periodo 1");
        p1.setFechaInicio(LocalDate.of(2026, 1, 20));
        p1.setFechaFin(LocalDate.of(2026, 4, 10));
        p1.setPorcentaje(new BigDecimal("25.00"));
        p1.setEstado(EstadoPeriodo.ABIERTO);
        p1.setFechaInicioDigitacion(LocalDate.of(2026, 1, 20));
        p1.setFechaFinDigitacion(LocalDate.of(2026, 4, 17));
        PeriodoAcademico p1Guardado = periodoRepository.save(p1);

        PeriodoAcademico p2 = new PeriodoAcademico();
        p2.setAnioLectivo(anio);
        p2.setNumero(2);
        p2.setNombre("Periodo 2");
        p2.setFechaInicio(LocalDate.of(2026, 4, 13));
        p2.setFechaFin(LocalDate.of(2026, 6, 26));
        p2.setPorcentaje(new BigDecimal("25.00"));
        p2.setEstado(EstadoPeriodo.CERRADO);
        periodoRepository.save(p2);

        PeriodoAcademico p3 = new PeriodoAcademico();
        p3.setAnioLectivo(anio);
        p3.setNumero(3);
        p3.setNombre("Periodo 3");
        p3.setFechaInicio(LocalDate.of(2026, 7, 13));
        p3.setFechaFin(LocalDate.of(2026, 9, 18));
        p3.setPorcentaje(new BigDecimal("25.00"));
        p3.setEstado(EstadoPeriodo.CERRADO);
        periodoRepository.save(p3);

        PeriodoAcademico p4 = new PeriodoAcademico();
        p4.setAnioLectivo(anio);
        p4.setNumero(4);
        p4.setNombre("Periodo 4");
        p4.setFechaInicio(LocalDate.of(2026, 9, 21));
        p4.setFechaFin(LocalDate.of(2026, 11, 27));
        p4.setPorcentaje(new BigDecimal("25.00"));
        p4.setEstado(EstadoPeriodo.CERRADO);
        periodoRepository.save(p4);

        return p1Guardado;
    }

    private Grado seedGrado10() {
        Optional<Grado> opt = gradoRepository.findByCodigo("10");
        if (opt.isPresent()) {
            return opt.get();
        }
        Grado g = new Grado();
        g.setCodigo("10");
        g.setNombre("Décimo");
        g.setNivel(NivelEducativo.MEDIA);
        g.setOrdenJerarquico(10);
        g.setRegimenEvaluacion(RegimenEvaluacion.CUANTITATIVO_AREAS);
        g.setActivo(true);
        return gradoRepository.save(g);
    }

    private Grupo seedGrupo10A(AnioLectivo anio, Grado grado) {
        Optional<Grupo> opt = grupoRepository.findByAnioLectivoIdAndGradoIdAndNombre(anio.getId(), grado.getId(), "10-A");
        if (opt.isPresent()) {
            return opt.get();
        }
        Grupo g = new Grupo();
        g.setAnioLectivo(anio);
        g.setGrado(grado);
        g.setNombre("10-A");
        g.setJornada(Jornada.MANANA);
        g.setCupoMaximo(35);
        g.setSede("SEDE_PRINCIPAL");
        g.setAulaBase("Aula 201 - Bloque B");
        g.setEstado(EstadoGrupo.ACTIVO);
        g.setActivo(true);
        return grupoRepository.save(g);
    }

    private Area seedAreaNaturales() {
        Optional<Area> opt = areaRepository.findByCodigo("CN");
        if (opt.isPresent()) {
            return opt.get();
        }
        Area a = new Area("CN", "Ciencias Naturales y Educación Ambiental", "Área fundamental según Ley 115 Art. 23");
        a.setActivo(true);
        return areaRepository.save(a);
    }

    private Asignatura seedAsignaturaFisica(Area area) {
        Optional<Asignatura> opt = asignaturaRepository.findByCodigo("FIS");
        if (opt.isPresent()) {
            return opt.get();
        }
        Asignatura asig = new Asignatura();
        asig.setArea(area);
        asig.setCodigo("FIS");
        asig.setNombre("Física");
        asig.setDescripcion("Física mecánica y termodinámica para Educación Media");
        asig.setActivo(true);
        return asignaturaRepository.save(asig);
    }

    private void seedPlanEstudios(Grado grado, Asignatura asignatura, AnioLectivo anio) {
        if (gradoAsignaturaRepository.existsByGradoIdAndAsignaturaId(grado.getId(), asignatura.getId())) {
            return;
        }
        GradoAsignatura ga = new GradoAsignatura();
        ga.setGrado(grado);
        ga.setAsignatura(asignatura);
        ga.setAnioLectivo(anio);
        ga.setIntensidadHoraria(4);
        ga.setEsOptativa(false);
        ga.setPesoPorcentualEnArea(new BigDecimal("100.00"));
        ga.setTipoComponente(TipoComponenteCurricular.ASIGNATURA);
        ga.setComputaPromocion(true);
        ga.setActivo(true);
        gradoAsignaturaRepository.save(ga);
    }

    private Profesor seedProfesor(Usuario usuarioProfesor) {
        var opt = profesorRepository.findByDocumento("79123456");
        if (opt.isPresent()) {
            return opt.get();
        }
        Profesor p = new Profesor();
        p.setNombres("Albert");
        p.setApellidos("Einstein");
        p.setDocumento("79123456");
        p.setTipoDocumento(TipoDocumento.CC);
        p.setCorreo("profesor1@colegio.edu.co");
        p.setTelefono("3001234567");
        p.setDireccion("Cra 7 # 45-10");
        p.setTituloAcademico("Licenciado en Física y Matemáticas");
        p.setEspecialidad("Física Teórica");
        p.setEstadoAdministrativo(EstadoAdministrativoDocente.ACTIVO);
        p.setTipoVinculacion(TipoVinculacionDocente.PROPIEDAD_ESTATUTO_1278);
        p.setHorasSemanalesOrdinarias(22);
        p.setVerificadoInhabilidades(true);
        p.setFechaVerificacionInhabilidades(LocalDate.of(2026, 1, 10));
        p.setNumeroCertificadoInhabilidades("CERT-INHAB-2026-0089");
        p.setUsuario(usuarioProfesor);
        p.setActivo(true);
        return profesorRepository.save(p);
    }

    private AsignacionAcademica seedAsignacion(Profesor profesor, Grupo grupo, Asignatura asignatura) {
        var opt = asignacionAcademicaRepository.findByGrupoIdAndAsignaturaId(grupo.getId(), asignatura.getId());
        if (opt.isPresent()) {
            return opt.get();
        }
        AsignacionAcademica asig = new AsignacionAcademica();
        asig.setProfesor(profesor);
        asig.setGrupo(grupo);
        asig.setAsignatura(asignatura);
        asig.setIntensidadHorariaSemanal(4);
        asig.setEsDirectorGrupo(true);
        asig.setEstadoAsignacion(EstadoAsignacionAcademica.TITULAR);
        asig.setFechaInicioVigencia(LocalDate.of(2026, 1, 20));
        asig.setActivo(true);
        return asignacionAcademicaRepository.save(asig);
    }

    private void seedActividadEvaluativa(AsignacionAcademica asignacion, PeriodoAcademico periodo) {
        if (!actividadEvaluativaRepository.findByAsignacionAcademicaIdAndPeriodoAcademicoId(asignacion.getId(), periodo.getId()).isEmpty()) {
            return;
        }
        ActividadEvaluativa act = new ActividadEvaluativa();
        act.setAsignacionAcademica(asignacion);
        act.setPeriodoAcademico(periodo);
        act.setNombre("Taller 1: Mecánica Clásica y Cinemática");
        act.setDescripcion("Solución de problemas de movimiento rectilíneo uniforme y acelerado.");
        act.setPorcentaje(new BigDecimal("25.00"));
        act.setFechaEvaluacion(LocalDate.of(2026, 2, 15));
        act.setCategoriaEvaluativa(CategoriaEvaluativa.COGNITIVA);
        act.setTipoComponente(TipoComponenteEvaluativo.TALLER);
        act.setModalidad(ModalidadEvaluativa.HETEROEVALUACION);
        act.setEstado(EstadoActividadEvaluativa.PUBLICADA);
        act.setActivo(true);
        actividadEvaluativaRepository.save(act);
    }

    private void seedExpedienteLucasMatriculado(AnioLectivo anio, Grupo grupo, PoliticaTratamientoDatos politica) {
        if (estudianteRepository.findByDocumento("1020304050").isPresent()) {
            return;
        }

        Estudiante lucas = new Estudiante();
        lucas.setNombres("Lucas");
        lucas.setApellidos("Martínez");
        lucas.setTipoDocumento(TipoDocumento.TI);
        lucas.setDocumento("1020304050");
        lucas.setFechaNacimiento(LocalDate.of(2010, 3, 10));
        lucas.setSexo(SexoDocumental.MASCULINO);
        lucas.setCorreo("lucas.martinez@colegio.edu.co");
        lucas.setTelefono("3123456789");
        lucas.setDireccion("Cra 15 # 80-45");
        lucas.setEps("Sura EPS");
        lucas.setGrupoSanguineoRh("O+");
        lucas.setTieneDiscapacidadPiar(false);
        lucas.setEstadoExpediente(EstadoExpediente.MATRICULADO);
        lucas.setActivo(true);
        Estudiante lucasGuardado = estudianteRepository.save(lucas);

        Acudiente pedro = new Acudiente();
        pedro.setNombres("Pedro");
        pedro.setApellidos("Martínez");
        pedro.setTipoDocumento(TipoDocumento.CC);
        pedro.setDocumento("51123456");
        pedro.setFechaNacimiento(LocalDate.of(1980, 4, 12));
        pedro.setCorreo("pedro.martinez@colegio.edu.co");
        pedro.setTelefono("3159876543");
        pedro.setDireccion("Cra 15 # 80-45");
        pedro.setOcupacion("Arquitecto");
        pedro.setActivo(true);
        Acudiente pedroGuardado = acudienteRepository.save(pedro);

        EstudianteAcudiente vinculo = new EstudianteAcudiente();
        vinculo.setEstudiante(lucasGuardado);
        vinculo.setAcudiente(pedroGuardado);
        vinculo.setParentesco(Parentesco.PADRE);
        vinculo.setEsPrincipal(true);
        vinculo.setConviveConEstudiante(true);
        vinculo.setAutorizadoRetiro(true);
        vinculo.setTieneCustodia(true);
        vinculo.setRepresentacionLegal(true);
        vinculo.setPrioridadEmergencia(1);
        vinculo.setEstadoVinculo(EstadoVinculo.VERIFICADO);
        estudianteAcudienteRepository.save(vinculo);

        AutorizacionTratamientoDatosMenor aut = new AutorizacionTratamientoDatosMenor();
        aut.setEstudiante(lucasGuardado);
        aut.setAcudiente(pedroGuardado);
        aut.setPolitica(politica);
        aut.setAutorizaFinalidadesEducativasObligatorias(true);
        aut.setAutorizaUsoImagenFotografiaOpcional(true);
        aut.setDireccionIp("127.0.0.1");
        aut.setUserAgent("Demostracion Base");
        autorizacionMenorRepository.save(aut);

        Matricula mat = new Matricula();
        mat.setNumeroMatricula("MAT-2026-10A-001");
        mat.setEstudiante(lucasGuardado);
        mat.setGrupo(grupo);
        mat.setAnioLectivo(anio);
        mat.setEstado(EstadoMatricula.MATRICULADA);
        mat.setTipoMatricula(TipoMatricula.REGULAR);
        mat.setFechaMatricula(LocalDate.of(2026, 1, 20));
        mat.setFechaEfectivaInicio(LocalDate.of(2026, 1, 20));
        mat.setFolio("FOL-10A-001");
        mat.setConsentimientoAcudiente(true);
        mat.setFechaAceptacionReglamentos(LocalDateTime.now());
        mat.setAcudientePrincipal(pedroGuardado);
        mat.setActivo(true);
        matriculaRepository.save(mat);
    }

    private void seedExpedienteSofia(PoliticaTratamientoDatos politica) {
        if (estudianteRepository.findByDocumento("1030405060").isPresent()) {
            return;
        }

        // 1. Estudiante
        Estudiante sofia = new Estudiante();
        sofia.setNombres("Sofía");
        sofia.setApellidos("Herrera");
        sofia.setTipoDocumento(TipoDocumento.TI);
        sofia.setDocumento("1030405060");
        sofia.setFechaNacimiento(LocalDate.of(2010, 5, 15));
        sofia.setSexo(SexoDocumental.FEMENINO);
        sofia.setCorreo("estudiante1@colegio.edu.co");
        sofia.setTelefono("3119876543");
        sofia.setDireccion("Calle 100 # 15-20");
        sofia.setEps("Sanitas EPS");
        sofia.setGrupoSanguineoRh("O+");
        sofia.setTieneDiscapacidadPiar(false);
        sofia.setEstadoExpediente(EstadoExpediente.LISTO_PARA_MATRICULA);
        sofia.setActivo(true);
        Estudiante sofiaGuardada = estudianteRepository.save(sofia);

        // 2. Acudiente Principal
        Acudiente marta = new Acudiente();
        marta.setNombres("Marta");
        marta.setApellidos("Gómez");
        marta.setTipoDocumento(TipoDocumento.CC);
        marta.setDocumento("52123456");
        marta.setFechaNacimiento(LocalDate.of(1982, 8, 20));
        marta.setCorreo("acudiente1@colegio.edu.co");
        marta.setTelefono("3101234567");
        marta.setDireccion("Calle 100 # 15-20");
        marta.setOcupacion("Ingeniera de Sistemas");
        marta.setActivo(true);
        Acudiente martaGuardada = acudienteRepository.save(marta);

        // 3. Vínculo Estudiante-Acudiente
        EstudianteAcudiente vinculo = new EstudianteAcudiente();
        vinculo.setEstudiante(sofiaGuardada);
        vinculo.setAcudiente(martaGuardada);
        vinculo.setParentesco(Parentesco.MADRE);
        vinculo.setEsPrincipal(true);
        vinculo.setConviveConEstudiante(true);
        vinculo.setAutorizadoRetiro(true);
        vinculo.setTieneCustodia(true);
        vinculo.setRepresentacionLegal(true);
        vinculo.setPrioridadEmergencia(1);
        vinculo.setEstadoVinculo(EstadoVinculo.VERIFICADO);
        estudianteAcudienteRepository.save(vinculo);

        // 4. Autorización Habeas Data
        AutorizacionTratamientoDatosMenor aut = new AutorizacionTratamientoDatosMenor();
        aut.setEstudiante(sofiaGuardada);
        aut.setAcudiente(martaGuardada);
        aut.setPolitica(politica);
        aut.setAutorizaFinalidadesEducativasObligatorias(true);
        aut.setAutorizaUsoImagenFotografiaOpcional(true);
        aut.setDireccionIp("127.0.0.1");
        aut.setUserAgent("Demostracion Presidencial");
        autorizacionMenorRepository.save(aut);
    }
}
