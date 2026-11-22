import { describe, it, expect, vi, beforeEach } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, provideRouter } from '@angular/router';
import { of } from 'rxjs';
import { EstudianteDetalleComponent } from './estudiante-detalle.component';
import { EstudiantesService } from '../../core/api/generated/api/estudiantes.service';
import { VinculacionesService } from '../../core/api/generated/api/vinculaciones.service';
import { NotificationService } from '../../core/services/notification.service';
import { Parentesco } from '../../core/api/generated/model/parentesco';

describe('EstudianteDetalleComponent', () => {
  let component: EstudianteDetalleComponent;
  let fixture: ComponentFixture<EstudianteDetalleComponent>;
  let mockEstudiantesService: any;
  let mockVinculacionesService: any;
  let mockNotificationService: any;

  const mockFicha = {
    id: 1,
    documento: '10203040',
    nombreCompleto: 'Santiago Gómez',
    estadoMatricula: 'MATRICULADO_ACTIVO',
    cursoActual: '7A',
    edadCalculada: 12
  };

  const mockVinculaciones = [
    {
      id: 10,
      acudienteDocumento: '40506070',
      acudienteNombres: 'Martha',
      acudienteApellidos: 'Gómez',
      parentesco: Parentesco.Madre,
      esPrincipal: true,
      autorizadoRetiro: true
    },
    {
      id: 11,
      acudienteDocumento: '50607080',
      acudienteNombres: 'Carlos',
      acudienteApellidos: 'Gómez',
      parentesco: Parentesco.Padre,
      esPrincipal: false,
      autorizadoRetiro: false
    }
  ];

  beforeEach(async () => {
    mockEstudiantesService = {
      apiEstudiantesIdFichaEscolarGet: vi.fn().mockReturnValue(of(mockFicha))
    };

    mockVinculacionesService = {
      apiVinculacionesEstudianteDocumentoEstudianteGet: vi.fn().mockReturnValue(of(mockVinculaciones)),
      apiVinculacionesEstudianteDocumentoEstudiantePrincipalDocumentoNuevoPrincipalPatch: vi.fn().mockReturnValue(of({})),
      apiVinculacionesIdRestriccionLegalPost: vi.fn().mockReturnValue(of({}))
    };

    mockNotificationService = {
      showSuccess: vi.fn(),
      showWarning: vi.fn(),
      showError: vi.fn()
    };

    await TestBed.configureTestingModule({
      imports: [EstudianteDetalleComponent],
      providers: [
        provideRouter([]),
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { paramMap: { get: () => '1' } } }
        },
        { provide: EstudiantesService, useValue: mockEstudiantesService },
        { provide: VinculacionesService, useValue: mockVinculacionesService },
        { provide: NotificationService, useValue: mockNotificationService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(EstudianteDetalleComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('debe crearse y cargar la ficha escolar y vinculaciones al iniciar', () => {
    expect(component).toBeTruthy();
    expect(mockEstudiantesService.apiEstudiantesIdFichaEscolarGet).toHaveBeenCalledWith(1);
    expect(mockVinculacionesService.apiVinculacionesEstudianteDocumentoEstudianteGet).toHaveBeenCalledWith('10203040');
    expect(component.ficha()?.nombreCompleto).toBe('Santiago Gómez');
    expect(component.vinculaciones().length).toBe(2);
  });

  it('debe permitir designar un nuevo acudiente principal (UC-PROV-05 A2)', () => {
    const nuevoPrincipal = mockVinculaciones[1];
    component.establecerComoPrincipal(nuevoPrincipal);

    expect(
      mockVinculacionesService.apiVinculacionesEstudianteDocumentoEstudiantePrincipalDocumentoNuevoPrincipalPatch
    ).toHaveBeenCalledWith('10203040', '50607080');
    expect(mockNotificationService.showSuccess).toHaveBeenCalled();
  });

  it('debe abrir modal y aplicar medida cautelar de restricción legal (UC-PROV-05 A3)', () => {
    const acudienteARestringir = mockVinculaciones[1];
    component.abrirModalRestriccion(acudienteARestringir);

    expect(component.vinculacionSeleccionadaParaRestriccion()).toBe(acudienteARestringir);

    component.restriccionForm.setValue({
      motivoBloqueoLegal: 'Medida cautelar de alejamiento Comisaría de Familia No. 12',
      soporteMedidaUrl: 'https://soporte.local/medida.pdf'
    });

    component.confirmarRestriccionLegal();

    expect(mockVinculacionesService.apiVinculacionesIdRestriccionLegalPost).toHaveBeenCalledWith(11, {
      motivoBloqueoLegal: 'Medida cautelar de alejamiento Comisaría de Familia No. 12',
      soporteMedidaUrl: 'https://soporte.local/medida.pdf'
    });
    expect(mockNotificationService.showWarning).toHaveBeenCalled();
    expect(component.vinculacionSeleccionadaParaRestriccion()).toBeNull();
  });
});
