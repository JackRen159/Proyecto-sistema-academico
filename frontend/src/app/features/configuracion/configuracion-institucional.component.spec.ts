import { describe, it, expect, vi, beforeEach } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { ConfiguracionInstitucionalComponent } from './configuracion-institucional.component';
import { ConfiguracinInstitucionalService } from '../../core/api/generated/api/configuracin-institucional.service';
import { NotificationService } from '../../core/services/notification.service';

describe('ConfiguracionInstitucionalComponent', () => {
  let component: ConfiguracionInstitucionalComponent;
  let fixture: ComponentFixture<ConfiguracionInstitucionalComponent>;
  let mockConfigService: any;
  let mockNotificationService: any;

  const mockInstitucion = {
    id: 1,
    nombreOficial: 'Colegio Departamental San José',
    codigoDane: '111001012345',
    nit: '890.123.456-7',
    caracter: 'OFICIAL',
    resolucionAprobacion: 'Res 004523 de 2018',
    nombreRectorFirmante: 'Dr. Alejandro Morales',
    documentoRectorFirmante: '70123456',
    nombreSecretarioFirmante: 'Lic. Claudia Restrepo',
    documentoSecretarioFirmante: '43987654'
  };

  const mockSiee = {
    id: 5,
    numeroVersion: 2,
    nombreVersion: 'SIEE Aprobado 2026',
    estado: 'APROBADA_VIGENTE',
    escalaMinima: 1.0,
    escalaMaxima: 5.0,
    escalaAprobatoria: 3.0,
    decimales: 2,
    modoRedondeo: 'HALF_UP',
    plazoReclamacionesDias: 3
  };

  const mockManual = {
    id: 3,
    numeroVersion: 1,
    estado: 'APROBADA_VIGENTE',
    actaConsejoDirectivo: 'Acta CD-004 de 2026'
  };

  beforeEach(async () => {
    mockConfigService = {
      apiConfiguracionInstitucionGet: vi.fn().mockReturnValue(of(mockInstitucion)),
      apiConfiguracionInstitucionPut: vi.fn().mockReturnValue(of(mockInstitucion)),
      apiConfiguracionSieeVigenteGet: vi.fn().mockReturnValue(of(mockSiee)),
      apiConfiguracionManualConvivenciaVigenteGet: vi.fn().mockReturnValue(of(mockManual)),
      apiConfiguracionSieeIdSimularPost: vi.fn().mockReturnValue(of({
        notaFinal: 3.5,
        desempenoNacional: 'BASICO',
        aprobado: true
      }))
    };

    mockNotificationService = {
      showSuccess: vi.fn(),
      showError: vi.fn()
    };

    await TestBed.configureTestingModule({
      imports: [ConfiguracionInstitucionalComponent],
      providers: [
        { provide: ConfiguracinInstitucionalService, useValue: mockConfigService },
        { provide: NotificationService, useValue: mockNotificationService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ConfiguracionInstitucionalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('debe crearse y cargar datos institucionales, SIEE y manual vigentes', () => {
    expect(component).toBeTruthy();
    expect(mockConfigService.apiConfiguracionInstitucionGet).toHaveBeenCalled();
    expect(mockConfigService.apiConfiguracionSieeVigenteGet).toHaveBeenCalled();
    expect(mockConfigService.apiConfiguracionManualConvivenciaVigenteGet).toHaveBeenCalled();

    expect(component.institucion()?.nombreOficial).toBe('Colegio Departamental San José');
    expect(component.sieeVigente()?.escalaAprobatoria).toBe(3.0);
  });

  it('debe guardar cambios en datos institucionales', () => {
    component.formInstitucion.patchValue({
      nombreOficial: 'Colegio Departamental San José Actualizado'
    });

    component.guardarDatosInstitucion();

    expect(mockConfigService.apiConfiguracionInstitucionPut).toHaveBeenCalled();
    expect(mockNotificationService.showSuccess).toHaveBeenCalled();
  });

  it('debe ejecutar simulación experimental de notas del SIEE', () => {
    component.tabActiva.set('siee');
    component.notaSimulacion.set(3.5);
    component.ejecutarSimulacion();

    expect(mockConfigService.apiConfiguracionSieeIdSimularPost).toHaveBeenCalledWith(5, {
      calificaciones: [3.5],
      calificacionesActividades: [3.5]
    });
    expect(component.resultadoSimulacion()?.aprobado).toBe(true);
    expect(component.resultadoSimulacion()?.desempenoNacional).toBe('BASICO');
  });
});
