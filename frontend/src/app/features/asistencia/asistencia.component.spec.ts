import { describe, it, expect, vi, beforeEach } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { AsistenciaComponent } from './asistencia.component';
import { ControlDeAsistenciaService } from '../../core/api/generated/api/control-de-asistencia.service';
import { EstructuraAcadmicaPeriodosService } from '../../core/api/generated/api/estructura-acadmica-periodos.service';
import { EstructuraAcadmicaGruposService } from '../../core/api/generated/api/estructura-acadmica-grupos.service';
import { MatrculasService } from '../../core/api/generated/api/matrculas.service';
import { NotificationService } from '../../core/services/notification.service';
import { EstadoAsistencia } from '../../core/api/generated/model/estado-asistencia';
import { TipoJustificacionAsistencia } from '../../core/api/generated/model/tipo-justificacion-asistencia';

describe('AsistenciaComponent', () => {
  let component: AsistenciaComponent;
  let fixture: ComponentFixture<AsistenciaComponent>;
  let mockAsistenciaService: any;
  let mockPeriodosService: any;
  let mockGruposService: any;
  let mockMatriculasService: any;
  let mockNotificationService: any;

  const mockPeriodos = [{ id: 1, nombre: 'Primer Periodo' }];
  const mockGrupos = [{ id: 10, nombre: '7A' }];
  const mockMatriculas = [
    { id: 101, estudianteNombreCompleto: 'Juan Gómez' },
    { id: 102, estudianteNombreCompleto: 'Ana López' }
  ];

  beforeEach(async () => {
    mockAsistenciaService = {
      crearSesionAsistencia: vi.fn().mockReturnValue(of({ id: 50, fecha: '2026-03-15' })),
      registrarPlanillaAsistenciaLote: vi.fn().mockReturnValue(of([])),
      radicarExcusa: vi.fn().mockReturnValue(of({ id: 99, numeroRadicado: 'EXC-2026-001' }))
    };

    mockPeriodosService = {
      apiAniosLectivosAnioIdPeriodosGet: vi.fn().mockReturnValue(of(mockPeriodos))
    };

    mockGruposService = {
      apiGruposGet: vi.fn().mockReturnValue(of(mockGrupos))
    };

    mockMatriculasService = {
      listarMatriculas: vi.fn().mockReturnValue(of(mockMatriculas))
    };

    mockNotificationService = {
      showSuccess: vi.fn(),
      showWarning: vi.fn(),
      showError: vi.fn()
    };

    await TestBed.configureTestingModule({
      imports: [AsistenciaComponent],
      providers: [
        { provide: ControlDeAsistenciaService, useValue: mockAsistenciaService },
        { provide: EstructuraAcadmicaPeriodosService, useValue: mockPeriodosService },
        { provide: EstructuraAcadmicaGruposService, useValue: mockGruposService },
        { provide: MatrculasService, useValue: mockMatriculasService },
        { provide: NotificationService, useValue: mockNotificationService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(AsistenciaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('debe crearse y cargar periodos y grupos', () => {
    expect(component).toBeTruthy();
    expect(mockPeriodosService.apiAniosLectivosAnioIdPeriodosGet).toHaveBeenCalled();
    expect(mockGruposService.apiGruposGet).toHaveBeenCalled();
    expect(component.periodos().length).toBe(1);
  });

  it('debe abrir sesión de planilla y cargar alumnos matriculados', () => {
    component.iniciarSesionPlanilla();

    expect(mockAsistenciaService.crearSesionAsistencia).toHaveBeenCalled();
    expect(mockMatriculasService.listarMatriculas).toHaveBeenCalledWith(10);
    expect(component.sesionActiva()?.id).toBe(50);
    expect(component.filasPlanilla().length).toBe(2);
    expect(mockNotificationService.showSuccess).toHaveBeenCalled();
  });

  it('debe emitir alerta de custodia al marcar evasión interna (RN-ASI-07, Ley 1098)', () => {
    component.iniciarSesionPlanilla();
    const fila = component.filasPlanilla()[0];

    const fakeEvent = {
      target: { value: EstadoAsistencia.EvasionInternaClase }
    } as unknown as Event;

    component.onEstadoFilaChange(fila, fakeEvent);

    expect(fila.estado).toBe(EstadoAsistencia.EvasionInternaClase);
    expect(mockNotificationService.showWarning).toHaveBeenCalledWith(
      expect.stringContaining('Evasión interna registrada'),
      'Alerta de Custodia'
    );
  });

  it('debe guardar la planilla por lote', () => {
    component.iniciarSesionPlanilla();
    component.guardarPlanilla();

    expect(mockAsistenciaService.registrarPlanillaAsistenciaLote).toHaveBeenCalledWith({
      sesionId: 50,
      registros: expect.arrayContaining([
        expect.objectContaining({ matriculaId: 101, estado: EstadoAsistencia.Presente })
      ])
    });
    expect(mockNotificationService.showSuccess).toHaveBeenCalled();
  });

  it('debe radicar excusa médica con reserva de datos de salud (Ley 1581)', () => {
    component.tabActiva.set('excusas');
    component.formExcusa.setValue({
      matriculaId: 101,
      tipoJustificacion: TipoJustificacionAsistencia.IncapacidadMedicaEps,
      fechaInicio: '2026-03-10',
      fechaFin: '2026-03-12',
      motivoExplicacion: 'Incapacidad médica por cuadro viral',
      esDatoSaludSensible: true
    });

    component.radicarExcusa();

    expect(mockAsistenciaService.radicarExcusa).toHaveBeenCalled();
    expect(mockNotificationService.showSuccess).toHaveBeenCalled();
  });
});
