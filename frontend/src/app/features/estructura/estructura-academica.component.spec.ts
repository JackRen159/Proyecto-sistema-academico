import { describe, it, expect, vi, beforeEach } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { EstructuraAcademicaComponent } from './estructura-academica.component';
import { EstructuraAcadmicaAosLectivosService } from '../../core/api/generated/api/estructura-acadmica-aos-lectivos.service';
import { EstructuraAcadmicaPeriodosService } from '../../core/api/generated/api/estructura-acadmica-periodos.service';
import { EstructuraAcadmicaGradosService } from '../../core/api/generated/api/estructura-acadmica-grados.service';
import { EstructuraAcadmicaGruposService } from '../../core/api/generated/api/estructura-acadmica-grupos.service';
import { NotificationService } from '../../core/services/notification.service';

import { EstadoAnioLectivo } from '../../core/api/generated/model/estado-anio-lectivo';

describe('EstructuraAcademicaComponent', () => {
  let component: EstructuraAcademicaComponent;
  let fixture: ComponentFixture<EstructuraAcademicaComponent>;
  let mockAniosService: any;
  let mockPeriodosService: any;
  let mockGradosService: any;
  let mockGruposService: any;
  let mockNotificationService: any;

  const mockAnios = [
    { id: 1, anio: 2026, estado: EstadoAnioLectivo.Activo, fechaInicio: '2026-01-20', fechaFin: '2026-11-30' },
    { id: 2, anio: 2027, estado: EstadoAnioLectivo.EnPlaneacion, fechaInicio: '2027-01-20', fechaFin: '2027-11-30' }
  ];

  const mockGrados = [
    { id: 10, codigo: '7', nombre: 'Séptimo' }
  ];

  beforeEach(async () => {
    mockAniosService = {
      apiAniosLectivosGet: vi.fn().mockReturnValue(of(mockAnios)),
      apiAniosLectivosPost: vi.fn().mockReturnValue(of({ id: 3, anio: 2028 })),
      apiAniosLectivosIdActivarPatch: vi.fn().mockReturnValue(of({ id: 2, anio: 2027, estado: 'EN_CURSO' })),
      apiAniosLectivosIdCerrarPatch: vi.fn().mockReturnValue(of({ id: 1, anio: 2026, estado: 'CERRADO_HISTORICO' }))
    };

    mockPeriodosService = {
      apiAniosLectivosAnioIdPeriodosGet: vi.fn().mockReturnValue(of([])),
      apiAniosLectivosAnioIdPeriodosPost: vi.fn().mockReturnValue(of({ id: 100, nombre: 'Periodo 1' })),
      apiPeriodosIdAbrirPatch: vi.fn().mockReturnValue(of({})),
      apiPeriodosIdCerrarPatch: vi.fn().mockReturnValue(of({}))
    };

    mockGradosService = {
      apiGradosGet: vi.fn().mockReturnValue(of(mockGrados))
    };

    mockGruposService = {
      apiGruposGet: vi.fn().mockReturnValue(of([])),
      apiGruposPost: vi.fn().mockReturnValue(of({ id: 200, nombre: '7A', cupoMaximo: 35 }))
    };

    mockNotificationService = {
      showSuccess: vi.fn(),
      showWarning: vi.fn(),
      showError: vi.fn()
    };

    await TestBed.configureTestingModule({
      imports: [EstructuraAcademicaComponent],
      providers: [
        { provide: EstructuraAcadmicaAosLectivosService, useValue: mockAniosService },
        { provide: EstructuraAcadmicaPeriodosService, useValue: mockPeriodosService },
        { provide: EstructuraAcadmicaGradosService, useValue: mockGradosService },
        { provide: EstructuraAcadmicaGruposService, useValue: mockGruposService },
        { provide: NotificationService, useValue: mockNotificationService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(EstructuraAcademicaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('debe crearse y cargar años lectivos y grados', () => {
    expect(component).toBeTruthy();
    expect(mockAniosService.apiAniosLectivosGet).toHaveBeenCalled();
    expect(mockGradosService.apiGradosGet).toHaveBeenCalled();
    expect(component.anios().length).toBe(2);
    expect(component.anioSeleccionadoId()).toBe(1);
  });

  it('debe conmutar de pestaña a periodos y grupos', () => {
    component.tabActiva.set('periodos');
    expect(component.tabActiva()).toBe('periodos');

    component.tabActiva.set('grupos');
    expect(component.tabActiva()).toBe('grupos');
  });

  it('debe activar año lectivo en planeación', () => {
    component.activarAnio(2);
    expect(mockAniosService.apiAniosLectivosIdActivarPatch).toHaveBeenCalledWith(2);
    expect(mockNotificationService.showSuccess).toHaveBeenCalled();
  });
});
