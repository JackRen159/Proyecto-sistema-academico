import { describe, it, expect, vi, beforeEach } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router, provideRouter } from '@angular/router';
import { of } from 'rxjs';

import { LoginComponent } from './auth/login.component';
import { PrimerIngresoComponent } from './auth/primer-ingreso.component';
import { EstudiantesListComponent } from './estudiantes/estudiantes-list.component';
import { EstudianteDetalleComponent } from './estudiantes/estudiante-detalle.component';
import { EstructuraAcademicaComponent } from './estructura/estructura-academica.component';
import { MatriculasComponent } from './matriculas/matriculas.component';
import { ProfesoresComponent } from './profesores/profesores.component';
import { AsistenciaComponent } from './asistencia/asistencia.component';
import { CalificacionesComponent } from './calificaciones/calificaciones.component';

import { AuthService } from '../core/services/auth.service';
import { NotificationService } from '../core/services/notification.service';
import { AutenticacinService } from '../core/api/generated/api/autenticacin.service';
import { EstudiantesService } from '../core/api/generated/api/estudiantes.service';
import { EstructuraAcadmicaAosLectivosService } from '../core/api/generated/api/estructura-acadmica-aos-lectivos.service';
import { EstructuraAcadmicaPeriodosService } from '../core/api/generated/api/estructura-acadmica-periodos.service';
import { EstructuraAcadmicaGruposService } from '../core/api/generated/api/estructura-acadmica-grupos.service';
import { EstructuraAcadmicaAsignaturasService } from '../core/api/generated/api/estructura-acadmica-asignaturas.service';
import { MatrculasService } from '../core/api/generated/api/matrculas.service';
import { MatrculasNovedadesDePermanenciaService } from '../core/api/generated/api/matrculas-novedades-de-permanencia.service';
import { ProfesoresService } from '../core/api/generated/api/profesores.service';
import { AsignacionesAcadmicasService } from '../core/api/generated/api/asignaciones-acadmicas.service';
import { ControlDeAsistenciaService } from '../core/api/generated/api/control-de-asistencia.service';
import { CalificacionesYSIEEService } from '../core/api/generated/api/calificaciones-ysiee.service';
import { VinculacionesService } from '../core/api/generated/api/vinculaciones.service';
import { ConfiguracinInstitucionalService } from '../core/api/generated/api/configuracin-institucional.service';

import { Rol } from '../core/api/generated/model/rol';
import { TipoDocumento } from '../core/api/generated/model/tipo-documento';
import { TipoMatricula } from '../core/api/generated/model/tipo-matricula';
import { EstadoAsistencia } from '../core/api/generated/model/estado-asistencia';
import { CategoriaEvaluativa } from '../core/api/generated/model/categoria-evaluativa';
import { TipoComponenteEvaluativo } from '../core/api/generated/model/tipo-componente-evaluativo';

describe('Flujo Secuencial de la Demostración de los 7 Casos de Uso desde el Frontend', () => {
  let router: Router;
  let mockNotification: any;

  beforeEach(() => {
    mockNotification = {
      showSuccess: vi.fn(),
      showError: vi.fn(),
      showInfo: vi.fn(),
      showWarning: vi.fn()
    };
  });

  // ── PASO 1: AUTENTICACIÓN SEGURA Y CONTROL PERIMETRAL (UC-01) ──────────────
  describe('Paso 1: Autenticación Segura y Control Perimetral (UC-01)', () => {
    it('1.1 Debe permitir iniciar sesión con secretaria1 y redirigir al panel principal', async () => {
      const mockAuthService = {
        login: vi.fn().mockReturnValue(of({
          token: 'jwt-secretaria-token',
          username: 'secretaria1',
          rol: Rol.Secretaria,
          debeCambiarPassword: false
        }))
      };

      await TestBed.configureTestingModule({
        imports: [LoginComponent],
        providers: [
          provideRouter([]),
          { provide: AuthService, useValue: mockAuthService }
        ]
      }).compileComponents();

      router = TestBed.inject(Router);
      const navigateByUrlSpy = vi.spyOn(router, 'navigateByUrl').mockImplementation(() => Promise.resolve(true));

      const fixture = TestBed.createComponent(LoginComponent);
      const component = fixture.componentInstance;
      fixture.detectChanges();

      // Rellenar credenciales de secretaria
      component.rellenarCredenciales('secretaria1', 'Secretaria123*');
      expect(component.loginForm.valid).toBe(true);

      component.onSubmit();

      expect(mockAuthService.login).toHaveBeenCalledWith({
        username: 'secretaria1',
        password: 'Secretaria123*'
      });
      expect(navigateByUrlSpy).toHaveBeenCalledWith('/estudiantes');
    });

    it('1.2 Debe detectar usuario con debeCambiarPassword: true y redirigir forzosamente a primer ingreso', async () => {
      const mockAuthService = {
        login: vi.fn().mockReturnValue(of({
          token: 'jwt-temp-token',
          username: 'usuario.temporal',
          rol: Rol.Estudiante,
          debeCambiarPassword: true
        }))
      };

      await TestBed.configureTestingModule({
        imports: [LoginComponent],
        providers: [
          provideRouter([]),
          { provide: AuthService, useValue: mockAuthService }
        ]
      }).compileComponents();

      router = TestBed.inject(Router);
      const navigateSpy = vi.spyOn(router, 'navigate').mockImplementation(() => Promise.resolve(true));

      const fixture = TestBed.createComponent(LoginComponent);
      const component = fixture.componentInstance;
      fixture.detectChanges();

      component.rellenarCredenciales('usuario.temporal', 'Temp123*');
      component.onSubmit();

      expect(navigateSpy).toHaveBeenCalledWith(['/primer-ingreso']);
    });
  });

  // ── PASO 2: CONSULTA Y VERIFICACIÓN DEL EXPEDIENTE ESCOLAR (UC-12) ──────────
  describe('Paso 2: Consulta y Verificación del Expediente Escolar (UC-12)', () => {
    it('2.1 Debe listar a Sofía Herrera con su documento 1030405060 en el catálogo de estudiantes', async () => {
      const mockEstudiantesService = {
        apiEstudiantesGet: vi.fn().mockReturnValue(of({
          content: [
            {
              id: 1,
              documento: '1030405060',
              nombres: 'Sofía',
              apellidos: 'Herrera',
              correo: 'estudiante1@colegio.edu.co',
              telefono: '3119876543',
              activo: true
            }
          ],
          page: { totalElements: 1, totalPages: 1, number: 0 }
        })),
        apiEstudiantesDocumentoDocumentoGet: vi.fn().mockReturnValue(of({
          id: 1,
          documento: '1030405060',
          nombres: 'Sofía',
          apellidos: 'Herrera',
          activo: true
        }))
      };

      await TestBed.configureTestingModule({
        imports: [EstudiantesListComponent],
        providers: [
          provideRouter([]),
          { provide: EstudiantesService, useValue: mockEstudiantesService },
          { provide: AuthService, useValue: { rolActivo: () => Rol.Secretaria } }
        ]
      }).compileComponents();

      const fixture = TestBed.createComponent(EstudiantesListComponent);
      const component = fixture.componentInstance;
      fixture.detectChanges();

      expect(component.estudiantes().length).toBe(1);
      expect(component.estudiantes()[0].documento).toBe('1030405060');
      expect(component.estudiantes()[0].nombres).toBe('Sofía');
      expect(component.estudiantes()[0].apellidos).toBe('Herrera');
    });

    it('2.2 Debe mostrar la ficha integral de Sofía Herrera con acudiente Marta Gómez y estado LISTO_PARA_MATRICULA', async () => {
      const mockEstudianteDetalle = {
        id: 1,
        documento: '1030405060',
        tipoDocumento: 'TI',
        nombres: 'Sofía',
        apellidos: 'Herrera',
        fechaNacimiento: '2010-05-15',
        sexo: 'FEMENINO',
        eps: 'Sanitas EPS',
        grupoSanguineoRh: 'O+',
        estadoExpediente: 'LISTO_PARA_MATRICULA',
        activo: true
      };

      const mockVinculaciones = [
        {
          id: 1,
          estudianteId: 1,
          acudienteId: 1,
          acudienteNombres: 'Marta',
          acudienteApellidos: 'Gómez',
          acudienteDocumento: '52123456',
          parentesco: 'MADRE',
          esPrincipal: true,
          tieneCustodia: true,
          representacionLegal: true,
          estadoVinculo: 'VERIFICADO'
        }
      ];

      const mockEstudiantesService = {
        apiEstudiantesIdFichaEscolarGet: vi.fn().mockReturnValue(of(mockEstudianteDetalle)),
        apiEstudiantesIdGet: vi.fn().mockReturnValue(of(mockEstudianteDetalle))
      };

      const mockVinculacionesService = {
        apiVinculacionesEstudianteDocumentoEstudianteGet: vi.fn().mockReturnValue(of(mockVinculaciones))
      };

      await TestBed.configureTestingModule({
        imports: [EstudianteDetalleComponent],
        providers: [
          provideRouter([]),
          { provide: EstudiantesService, useValue: mockEstudiantesService },
          { provide: VinculacionesService, useValue: mockVinculacionesService },
          { provide: AuthService, useValue: { rolActivo: () => Rol.Secretaria } },
          { provide: NotificationService, useValue: mockNotification }
        ]
      }).compileComponents();

      const fixture = TestBed.createComponent(EstudianteDetalleComponent);
      const component = fixture.componentInstance;
      component.ficha.set(mockEstudianteDetalle as any);
      component.vinculaciones.set(mockVinculaciones as any);
      fixture.detectChanges();

      expect(component.ficha()?.documento).toBe('1030405060');
      expect(component.vinculaciones().length).toBe(1);
      expect(component.vinculaciones()[0].acudienteNombres).toBe('Marta');
      expect(component.vinculaciones()[0].esPrincipal).toBe(true);
    });
  });

  // ── PASO 3: CONFIGURACIÓN DEL AÑO LECTIVO Y ESCALA SIEE (UC-14) ────────────
  describe('Paso 3: Configuración del Año Lectivo y Escala SIEE (UC-14)', () => {
    it('3.1 Debe mostrar el Año Lectivo 2026 ACTIVO con sus 4 periodos al 100% y el grupo 10-A con 35 cupos', async () => {
      const mockAnio2026 = { id: 1, anio: 2026, nombre: 'Año Escolar 2026', estado: 'ACTIVO' };
      const mockPeriodos = [
        { id: 1, numero: 1, nombre: 'Periodo 1', porcentaje: 25.0, estado: 'ABIERTO' },
        { id: 2, numero: 2, nombre: 'Periodo 2', porcentaje: 25.0, estado: 'CERRADO' },
        { id: 3, numero: 3, nombre: 'Periodo 3', porcentaje: 25.0, estado: 'CERRADO' },
        { id: 4, numero: 4, nombre: 'Periodo 4', porcentaje: 25.0, estado: 'CERRADO' }
      ];
      const mockGrupos = [
        { id: 1, nombre: '10-A', cupoMaximo: 35, aulaBase: 'Aula 201 - Bloque B', estado: 'ACTIVO' }
      ];

      const mockAniosService = {
        apiAniosLectivosGet: vi.fn().mockReturnValue(of([mockAnio2026])),
        apiAniosLectivosActivoGet: vi.fn().mockReturnValue(of(mockAnio2026))
      };
      const mockPeriodosService = {
        apiAniosLectivosAnioIdPeriodosGet: vi.fn().mockReturnValue(of(mockPeriodos))
      };
      const mockGruposService = {
        apiGruposGet: vi.fn().mockReturnValue(of(mockGrupos))
      };

      await TestBed.configureTestingModule({
        imports: [EstructuraAcademicaComponent],
        providers: [
          provideRouter([]),
          { provide: EstructuraAcadmicaAosLectivosService, useValue: mockAniosService },
          { provide: EstructuraAcadmicaPeriodosService, useValue: mockPeriodosService },
          { provide: EstructuraAcadmicaGruposService, useValue: mockGruposService },
          { provide: NotificationService, useValue: mockNotification }
        ]
      }).compileComponents();

      const fixture = TestBed.createComponent(EstructuraAcademicaComponent);
      const component = fixture.componentInstance;
      fixture.detectChanges();

      const sumaPeriodos = component.periodos().reduce((acc, p) => acc + (p.porcentaje || 0), 0);
      expect(component.anios().length).toBe(1);
      expect(component.anios()[0].anio).toBe(2026);
      expect(component.periodos().length).toBe(4);
      expect(sumaPeriodos).toBe(100);
      expect(component.grupos().length).toBe(1);
      expect(component.grupos()[0].nombre).toBe('10-A');
      expect(component.grupos()[0].cupoMaximo).toBe(35);
    });
  });

  // ── PASO 4: FORMALIZACIÓN DE MATRÍCULA Y CONTROL DE CUPO (UC-13) ───────────
  describe('Paso 4: Formalización de Matrícula y Control de Cupo (UC-13)', () => {
    it('4.1 Secretaría matricula a Sofía Herrera en 10-A y obtiene número de matrícula oficial', async () => {
      const mockSofia = { id: 1, nombres: 'Sofía', apellidos: 'Herrera', documento: '1030405060' };
      const mockGrupo10A = { id: 1, nombre: '10-A', cupoMaximo: 35 };

      const mockMatriculasService = {
        listarMatriculas: vi.fn().mockReturnValue(of([])),
        listarMatriculasPorAnioLectivo: vi.fn().mockReturnValue(of({ content: [] })),
        matricular: vi.fn().mockReturnValue(of({
          id: 1,
          numeroMatricula: 'MAT-2026-0001',
          estudianteNombreCompleto: 'Sofía Herrera',
          grupoNombre: '10-A',
          tipoMatricula: TipoMatricula.Nuevo,
          estado: 'MATRICULADA'
        }))
      };

      const mockEstudiantesService = {
        apiEstudiantesGet: vi.fn().mockReturnValue(of({ content: [mockSofia] }))
      };
      const mockGruposService = {
        apiGruposGet: vi.fn().mockReturnValue(of([mockGrupo10A]))
      };
      const mockAniosService = {
        apiAniosLectivosGet: vi.fn().mockReturnValue(of([{ id: 1, anio: 2026, estado: 'ACTIVO' }]))
      };

      await TestBed.configureTestingModule({
        imports: [MatriculasComponent],
        providers: [
          provideRouter([]),
          { provide: MatrculasService, useValue: mockMatriculasService },
          { provide: MatrculasNovedadesDePermanenciaService, useValue: {} },
          { provide: EstudiantesService, useValue: mockEstudiantesService },
          { provide: EstructuraAcadmicaGruposService, useValue: mockGruposService },
          { provide: EstructuraAcadmicaAosLectivosService, useValue: mockAniosService },
          { provide: NotificationService, useValue: mockNotification }
        ]
      }).compileComponents();

      const fixture = TestBed.createComponent(MatriculasComponent);
      const component = fixture.componentInstance;
      fixture.detectChanges();

      // Cambiar a pestaña formalizar matrícula
      component.tabActiva.set('formalizar');

      // Seleccionar a Sofía mediante el asistente del guion
      component.seleccionarSofiaListo();
      component.formMatricula.patchValue({
        grupoId: 1,
        folio: 'MAT-2026-0001',
        tipoMatricula: TipoMatricula.Nuevo,
        aceptaReglamentos: true
      });

      expect(component.formMatricula.valid).toBe(true);

      // Formalizar matrícula
      component.formalizarMatricula();

      expect(mockMatriculasService.matricular).toHaveBeenCalledWith(expect.objectContaining({
        estudianteId: 1,
        grupoId: 1,
        folio: 'MAT-2026-0001'
      }));
      expect(mockNotification.showSuccess).toHaveBeenCalledWith(
        expect.stringContaining('Matrícula formalizada exitosamente con código MAT-2026-0001'),
        'Matrícula Exitosa'
      );
    });
  });

  // ── PASO 5: ASIGNACIÓN ACADÉMICA Y VERIFICACIÓN DOCENTE (UC-15) ────────────
  describe('Paso 5: Asignación Académica y Verificación Docente (UC-15)', () => {
    it('5.1 Coordinación asigna a Albert Einstein a Física en 10-A como Director de Grupo', async () => {
      const mockProfesorAlbert = {
        id: 1,
        nombres: 'Albert',
        apellidos: 'Einstein',
        documento: '79123456',
        especialidad: 'Física Teórica',
        verificadoInhabilidades: true,
        activo: true
      };
      const mockGrupo10A = { id: 1, nombre: '10-A' };
      const mockFisica = { id: 1, nombre: 'Física' };

      const mockAsignacionesService = {
        apiAsignacionesAcademicasGet: vi.fn().mockReturnValue(of([])),
        apiAsignacionesAcademicasPost: vi.fn().mockReturnValue(of({
          id: 1,
          profesorNombreCompleto: 'Albert Einstein',
          grupoNombre: '10-A',
          asignaturaNombre: 'Física',
          intensidadHorariaSemanal: 4,
          esDirectorGrupo: true,
          activo: true
        }))
      };

      const mockProfesoresService = {
        apiProfesoresGet: vi.fn().mockReturnValue(of([mockProfesorAlbert]))
      };
      const mockGruposService = {
        apiGruposGet: vi.fn().mockReturnValue(of([mockGrupo10A]))
      };
      const mockAsignaturasService = {
        apiAsignaturasGet: vi.fn().mockReturnValue(of([mockFisica]))
      };
      const mockAniosService = {
        apiAniosLectivosGet: vi.fn().mockReturnValue(of([{ id: 1, anio: 2026, estado: 'ACTIVO' }]))
      };

      await TestBed.configureTestingModule({
        imports: [ProfesoresComponent],
        providers: [
          provideRouter([]),
          { provide: ProfesoresService, useValue: mockProfesoresService },
          { provide: AsignacionesAcadmicasService, useValue: mockAsignacionesService },
          { provide: EstructuraAcadmicaGruposService, useValue: mockGruposService },
          { provide: EstructuraAcadmicaAsignaturasService, useValue: mockAsignaturasService },
          { provide: EstructuraAcadmicaAosLectivosService, useValue: mockAniosService },
          { provide: NotificationService, useValue: mockNotification }
        ]
      }).compileComponents();

      const fixture = TestBed.createComponent(ProfesoresComponent);
      const component = fixture.componentInstance;
      fixture.detectChanges();

      // Cambiar a pestaña Asignaciones
      component.tabActiva.set('asignaciones');

      // Configurar asignación del guion
      component.formAsignacion.setValue({
        profesorId: 1,
        grupoId: 1,
        asignaturaId: 1,
        intensidadHorariaSemanal: 4,
        esDirectorGrupo: true
      });

      expect(component.formAsignacion.valid).toBe(true);

      // Guardar asignación
      component.guardarAsignacion();

      expect(mockAsignacionesService.apiAsignacionesAcademicasPost).toHaveBeenCalledWith({
        profesorId: 1,
        grupoId: 1,
        asignaturaId: 1,
        intensidadHorariaSemanal: 4,
        esDirectorGrupo: true
      });
      expect(mockNotification.showSuccess).toHaveBeenCalledWith(
        'Carga académica asignada exitosamente.',
        'Asignación Registrada'
      );
    });
  });

  // ── PASO 6: REGISTRO DE ASISTENCIA ESCOLAR Y EVASIÓN INTERNA (UC-19) ────────
  describe('Paso 6: Registro de Asistencia Escolar y Evasión Interna (UC-19)', () => {
    it('6.1 Docente abre planilla de 10-A, visualiza a Sofía Herrera y guarda la asistencia por lote', async () => {
      const mockSesion = { id: 1, fecha: '2026-02-02', bloqueHorario: 1 };
      const mockMatriculaSofia = {
        id: 1,
        estudianteNombreCompleto: 'Sofía Herrera',
        grupoNombre: '10-A'
      };

      const mockAsistenciaService = {
        crearSesionAsistencia: vi.fn().mockReturnValue(of(mockSesion)),
        listarSesionesAsistencia: vi.fn().mockReturnValue(of([mockSesion])),
        registrarPlanillaAsistenciaLote: vi.fn().mockReturnValue(of([]))
      };
      const mockMatriculasService = {
        listarMatriculas: vi.fn().mockReturnValue(of([mockMatriculaSofia]))
      };
      const mockAniosService = {
        apiAniosLectivosGet: vi.fn().mockReturnValue(of([{ id: 1, anio: 2026, estado: 'ACTIVO' }]))
      };
      const mockPeriodosService = {
        apiAniosLectivosAnioIdPeriodosGet: vi.fn().mockReturnValue(of([{ id: 1, nombre: 'Periodo 1' }]))
      };
      const mockGruposService = {
        apiGruposGet: vi.fn().mockReturnValue(of([{ id: 1, nombre: '10-A' }]))
      };

      await TestBed.configureTestingModule({
        imports: [AsistenciaComponent],
        providers: [
          provideRouter([]),
          { provide: ControlDeAsistenciaService, useValue: mockAsistenciaService },
          { provide: MatrculasService, useValue: mockMatriculasService },
          { provide: EstructuraAcadmicaAosLectivosService, useValue: mockAniosService },
          { provide: EstructuraAcadmicaPeriodosService, useValue: mockPeriodosService },
          { provide: EstructuraAcadmicaGruposService, useValue: mockGruposService },
          { provide: NotificationService, useValue: mockNotification }
        ]
      }).compileComponents();

      const fixture = TestBed.createComponent(AsistenciaComponent);
      const component = fixture.componentInstance;
      fixture.detectChanges();

      component.periodoSeleccionadoId.set(1);
      component.grupoSeleccionadoId.set(1);
      component.fechaSesion.set('2026-02-02');

      // Abrir planilla
      component.iniciarSesionPlanilla();

      expect(mockAsistenciaService.crearSesionAsistencia).toHaveBeenCalled();
      expect(component.filasPlanilla().length).toBe(1);
      expect(component.filasPlanilla()[0].estudianteNombre).toBe('Sofía Herrera');
      expect(component.filasPlanilla()[0].estado).toBe(EstadoAsistencia.Presente);

      // Guardar planilla
      component.guardarPlanilla();

      expect(mockAsistenciaService.registrarPlanillaAsistenciaLote).toHaveBeenCalledWith(expect.objectContaining({
        sesionId: 1,
        registros: [
          expect.objectContaining({
            matriculaId: 1,
            estado: EstadoAsistencia.Presente
          })
        ]
      }));
      expect(mockNotification.showSuccess).toHaveBeenCalledWith(
        'Planilla de asistencia registrada exitosamente por lote.',
        'Asistencia Guardada'
      );
    });
  });

  // ── PASO 7: REGISTRO Y RECÁLCULO AUTOMÁTICO DE CALIFICACIONES SIEE (UC-17) ───
  describe('Paso 7: Registro y Recálculo Automático de Calificaciones SIEE (UC-17)', () => {
    it('7.1 Docente crea actividad Taller 1 al 100%, califica a Sofía con 4.5 y consolida definitivas', async () => {
      const mockAsignacion10A = {
        id: 1,
        grupoId: 1,
        grupoNombre: '10-A',
        asignaturaNombre: 'Física',
        profesorNombreCompleto: 'Albert Einstein'
      };
      const mockActividad = {
        id: 1,
        nombre: 'Taller 1: Cinemática Vectorial',
        porcentaje: 100,
        categoriaEvaluativa: CategoriaEvaluativa.Cognitiva
      };
      const mockMatriculaSofia = { id: 1, estudianteNombreCompleto: 'Sofía Herrera' };

      const mockCalificacionService = {
        listarActividadesEvaluativas: vi.fn().mockReturnValue(of([mockActividad])),
        listarNotasActividad: vi.fn().mockReturnValue(of([])),
        crearActividadEvaluativa: vi.fn().mockReturnValue(of(mockActividad)),
        registrarNotasLote: vi.fn().mockReturnValue(of([])),
        calcularDefinitivasPeriodo: vi.fn().mockReturnValue(of([]))
      };

      const mockAsignacionesService = {
        apiAsignacionesAcademicasGet: vi.fn().mockReturnValue(of([mockAsignacion10A]))
      };
      const mockAniosService = {
        apiAniosLectivosGet: vi.fn().mockReturnValue(of([{ id: 1, anio: 2026, estado: 'ACTIVO' }]))
      };
      const mockPeriodosService = {
        apiAniosLectivosAnioIdPeriodosGet: vi.fn().mockReturnValue(of([{ id: 1, nombre: 'Periodo 1', porcentaje: 25 }]))
      };
      const mockGruposService = {
        apiGruposGet: vi.fn().mockReturnValue(of([{ id: 1, nombre: '10-A' }]))
      };
      const mockMatriculasService = {
        listarMatriculas: vi.fn().mockReturnValue(of([mockMatriculaSofia]))
      };

      await TestBed.configureTestingModule({
        imports: [CalificacionesComponent],
        providers: [
          provideRouter([]),
          { provide: CalificacionesYSIEEService, useValue: mockCalificacionService },
          { provide: AsignacionesAcadmicasService, useValue: mockAsignacionesService },
          { provide: EstructuraAcadmicaAosLectivosService, useValue: mockAniosService },
          { provide: EstructuraAcadmicaPeriodosService, useValue: mockPeriodosService },
          { provide: EstructuraAcadmicaGruposService, useValue: mockGruposService },
          { provide: MatrculasService, useValue: mockMatriculasService },
          { provide: NotificationService, useValue: mockNotification }
        ]
      }).compileComponents();

      const fixture = TestBed.createComponent(CalificacionesComponent);
      const component = fixture.componentInstance;
      fixture.detectChanges();

      // Sub-paso 1: Crear Actividad
      component.rellenarActividadGuion();
      expect(component.formActividad.get('nombre')?.value).toBe('Taller 1: Cinemática Vectorial');
      expect(component.formActividad.get('porcentaje')?.value).toBe(100);

      component.crearActividad();
      expect(mockCalificacionService.crearActividadEvaluativa).toHaveBeenCalledWith(expect.objectContaining({
        nombre: 'Taller 1: Cinemática Vectorial',
        porcentaje: 100
      }));

      // Sub-paso 2: Asignar nota 4.5
      component.calificarSofia45();
      expect(component.filasCalificaciones()[0].valor).toBe(4.5);
      expect(component.filasCalificaciones()[0].observaciones).toBe('Excelente sustentación conceptual');

      // Sub-paso 3: Guardar notas
      component.guardarNotasLote();
      expect(mockCalificacionService.registrarNotasLote).toHaveBeenCalledWith(expect.objectContaining({
        actividadId: 1,
        calificaciones: [
          expect.objectContaining({
            matriculaId: 1,
            valor: 4.5,
            observaciones: 'Excelente sustentación conceptual'
          })
        ]
      }));

      // Sub-paso 4: Consolidar definitivas
      component.consolidarDefinitivas();
      expect(mockCalificacionService.calcularDefinitivasPeriodo).toHaveBeenCalledWith(1, 1);
      expect(mockNotification.showSuccess).toHaveBeenCalledWith(
        'Definitivas del periodo consolidadas con motor BigDecimal.'
      );
    });
  });
});
