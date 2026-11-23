import { describe, it, expect, vi, beforeEach } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { CalificacionesComponent } from './calificaciones.component';
import { CalificacionesYSIEEService } from '../../core/api/generated/api/calificaciones-ysiee.service';
import { AsignacionesAcadmicasService } from '../../core/api/generated/api/asignaciones-acadmicas.service';
import { EstructuraAcadmicaPeriodosService } from '../../core/api/generated/api/estructura-acadmica-periodos.service';
import { EstructuraAcadmicaGruposService } from '../../core/api/generated/api/estructura-acadmica-grupos.service';
import { EstructuraAcadmicaAosLectivosService } from '../../core/api/generated/api/estructura-acadmica-aos-lectivos.service';
import { MatrculasService } from '../../core/api/generated/api/matrculas.service';
import { NotificationService } from '../../core/services/notification.service';
import { EstadoCalificacionActividad } from '../../core/api/generated/model/estado-calificacion-actividad';

describe('CalificacionesComponent', () => {
  let component: CalificacionesComponent;
  let fixture: ComponentFixture<CalificacionesComponent>;
  let mockCalificacionService: any;
  let mockAsignacionesService: any;
  let mockAniosService: any;
  let mockPeriodosService: any;
  let mockGruposService: any;
  let mockMatriculasService: any;
  let mockNotificationService: any;

  const mockAsignaciones = [
    { id: 1, grupoId: 10, grupoNombre: '7A', asignaturaNombre: 'Matemáticas', profesorNombreCompleto: 'Juan Pérez' }
  ];
  const mockPeriodos = [{ id: 1, nombre: 'Periodo 1', porcentaje: 25 }];
  const mockActividades = [{ id: 5, nombre: 'Taller 1', porcentaje: 20 }];
  const mockMatriculas = [{ id: 101, estudianteNombreCompleto: 'Carlos Sánchez' }];
  const mockNotas = [{ matriculaId: 101, valor: 4.2, estado: EstadoCalificacionActividad.Calificada }];

  beforeEach(async () => {
    mockCalificacionService = {
      listarActividadesEvaluativas: vi.fn().mockReturnValue(of(mockActividades)),
      listarNotasActividad: vi.fn().mockReturnValue(of(mockNotas)),
      crearActividadEvaluativa: vi.fn().mockReturnValue(of({ id: 6, nombre: 'Quiz 1' })),
      registrarNotasLote: vi.fn().mockReturnValue(of([])),
      calcularDefinitivasPeriodo: vi.fn().mockReturnValue(of([])),
      consultarSabanaConsolidada: vi.fn().mockReturnValue(of({ grupoNombre: '7A', periodoNumero: 1, estudiantes: [] })),
      registrarActaNivelacion: vi.fn().mockReturnValue(of({ id: 80 }))
    };

    mockAsignacionesService = {
      apiAsignacionesAcademicasGet: vi.fn().mockReturnValue(of(mockAsignaciones))
    };

    mockAniosService = {
      apiAniosLectivosGet: vi.fn().mockReturnValue(of([{ id: 1, anio: 2026, estado: 'ACTIVO' }]))
    };

    mockPeriodosService = {
      apiAniosLectivosAnioIdPeriodosGet: vi.fn().mockReturnValue(of(mockPeriodos))
    };

    mockGruposService = {
      apiGruposGet: vi.fn().mockReturnValue(of([{ id: 10, nombre: '7A' }]))
    };

    mockMatriculasService = {
      listarMatriculas: vi.fn().mockReturnValue(of(mockMatriculas))
    };

    mockNotificationService = {
      showSuccess: vi.fn(),
      showInfo: vi.fn(),
      showError: vi.fn(),
      showWarning: vi.fn()
    };

    await TestBed.configureTestingModule({
      imports: [CalificacionesComponent],
      providers: [
        { provide: CalificacionesYSIEEService, useValue: mockCalificacionService },
        { provide: AsignacionesAcadmicasService, useValue: mockAsignacionesService },
        { provide: EstructuraAcadmicaAosLectivosService, useValue: mockAniosService },
        { provide: EstructuraAcadmicaPeriodosService, useValue: mockPeriodosService },
        { provide: EstructuraAcadmicaGruposService, useValue: mockGruposService },
        { provide: MatrculasService, useValue: mockMatriculasService },
        { provide: NotificationService, useValue: mockNotificationService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(CalificacionesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('debe crearse y cargar asignaciones, periodos y actividades', () => {
    expect(component).toBeTruthy();
    expect(mockAsignacionesService.apiAsignacionesAcademicasGet).toHaveBeenCalled();
    expect(component.actividades().length).toBe(1);
    expect(component.filasCalificaciones().length).toBe(1);
  });

  it('debe guardar notas por lote atómico', () => {
    component.guardarNotasLote();

    expect(mockCalificacionService.registrarNotasLote).toHaveBeenCalledWith({
      actividadId: 5,
      calificaciones: expect.arrayContaining([
        expect.objectContaining({ matriculaId: 101, valor: 4.2 })
      ])
    });
    expect(mockNotificationService.showSuccess).toHaveBeenCalled();
  });

  it('debe registrar acta de nivelación con principio de no desmejoramiento (RN-CAL-07)', () => {
    component.tabActiva.set('nivelaciones');
    component.formNivelacion.setValue({
      matriculaId: 101,
      asignaturaId: 1,
      periodoId: 1,
      notaNivelacion: 3.5,
      actaNumero: 'ACTA-2026-NIV-01',
      observaciones: 'Nivelación aprobada'
    });

    component.guardarNivelacion();

    expect(mockCalificacionService.registrarActaNivelacion).toHaveBeenCalled();
    expect(mockNotificationService.showSuccess).toHaveBeenCalled();
  });
});
