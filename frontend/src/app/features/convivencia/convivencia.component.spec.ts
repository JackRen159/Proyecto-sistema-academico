import { describe, it, expect, vi, beforeEach } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { ConvivenciaComponent } from './convivencia.component';
import { ConvivenciaEscolarService } from '../../core/api/generated/api/convivencia-escolar.service';
import { NotificationService } from '../../core/services/notification.service';
import {
  AnotacionObservadorRequestTipoAnotacionEnum
} from '../../core/api/generated/model/anotacion-observador-request';
import { CasoConvivenciaRequestTipoSituacionEnum } from '../../core/api/generated/model/caso-convivencia-request';

describe('ConvivenciaComponent', () => {
  let component: ConvivenciaComponent;
  let fixture: ComponentFixture<ConvivenciaComponent>;
  let mockConvivenciaService: any;
  let mockNotificationService: any;

  const mockAnotaciones = [
    {
      id: 1,
      tipoAnotacion: 'SEGUIMIENTO_FORMATIVO',
      fechaHecho: '2026-03-01',
      lugarHecho: 'Patio central',
      descripcionFactica: 'Diálogo pedagógico sobre convivencia en recreo.',
      nombreAutor: 'Prof. Martha Gómez',
      versionLibreEstudiante: 'Versión del alumno',
      enteradoAcudiente: true
    }
  ];

  beforeEach(async () => {
    mockConvivenciaService = {
      listarAnotacionesEstudiante: vi.fn().mockReturnValue(of(mockAnotaciones)),
      registrarAnotacionObservador: vi.fn().mockReturnValue(of({ id: 2 })),
      abrirCasoConvivencia: vi.fn().mockReturnValue(of({ id: 10, codigoExpediente: 'CASO-2026-001' }))
    };

    mockNotificationService = {
      showSuccess: vi.fn(),
      showWarning: vi.fn(),
      showError: vi.fn()
    };

    await TestBed.configureTestingModule({
      imports: [ConvivenciaComponent],
      providers: [
        { provide: ConvivenciaEscolarService, useValue: mockConvivenciaService },
        { provide: NotificationService, useValue: mockNotificationService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ConvivenciaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('debe crearse y cargar anotaciones del estudiante al iniciar', () => {
    expect(component).toBeTruthy();
    expect(mockConvivenciaService.listarAnotacionesEstudiante).toHaveBeenCalledWith(1);
    expect(component.anotaciones().length).toBe(1);
  });

  it('debe registrar anotación inmutable en el observador', () => {
    component.formAnotacion.patchValue({
      tipoAnotacion: AnotacionObservadorRequestTipoAnotacionEnum.SeguimientoFormativo,
      fechaHecho: '2026-03-15',
      lugarHecho: 'Aula 202',
      descripcionFactica: 'Compromiso formativo acordado con el estudiante.'
    });

    component.guardarAnotacion();

    expect(mockConvivenciaService.registrarAnotacionObservador).toHaveBeenCalled();
    expect(mockNotificationService.showSuccess).toHaveBeenCalled();
  });

  it('debe abrir caso en Ruta de Atención Integral (Ley 1620)', () => {
    component.tabActiva.set('ruta');
    component.formRuta.patchValue({
      anioLectivoId: 1,
      estudianteImplicadoId: 1,
      tipoSituacion: CasoConvivenciaRequestTipoSituacionEnum.TipoIi,
      fechaHechos: '2026-03-14',
      lugarHechos: 'Cancha deportiva',
      descripcionSituacion: 'Agresión verbal reiterada.'
    });

    component.abrirCasoRuta();

    expect(mockConvivenciaService.abrirCasoConvivencia).toHaveBeenCalled();
    expect(mockNotificationService.showSuccess).toHaveBeenCalled();
  });
});
