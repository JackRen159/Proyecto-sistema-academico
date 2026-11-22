import { describe, it, expect, vi, beforeEach } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { AuditoriaComponent } from './auditoria.component';
import { AuditoraGlobalService } from '../../core/api/generated/api/auditora-global.service';
import { NotificationService } from '../../core/services/notification.service';

describe('AuditoriaComponent', () => {
  let component: AuditoriaComponent;
  let fixture: ComponentFixture<AuditoriaComponent>;
  let mockAuditoriaService: any;
  let mockNotificationService: any;

  const mockIntegridad = {
    estado: 'INTEGRA',
    totalEventosVerificados: 850,
    fechaVerificacion: '2026-03-15 10:00:00',
    detalle: 'Todos los bloques SHA-256 encadenados coinciden exactamente.'
  };

  const mockAlertas = [
    {
      id: 1,
      tipoAlerta: 'RAFAGA_403_ACCESO_DENEGADO',
      severidad: 'ALTA',
      descripcionEvento: 'Múltiples accesos no autorizados detectados',
      fechaAlerta: '2026-03-15 09:45:00'
    }
  ];

  beforeEach(async () => {
    mockAuditoriaService = {
      apiAuditoriaVerificarIntegridadGet: vi.fn().mockReturnValue(of(mockIntegridad)),
      listarAlertasSeguridadPendientes: vi.fn().mockReturnValue(of(mockAlertas)),
      marcarAlertaSeguridadLeida: vi.fn().mockReturnValue(of({}))
    };

    mockNotificationService = {
      showSuccess: vi.fn(),
      showError: vi.fn(),
      showWarning: vi.fn(),
      showInfo: vi.fn()
    };

    await TestBed.configureTestingModule({
      imports: [AuditoriaComponent],
      providers: [
        { provide: AuditoraGlobalService, useValue: mockAuditoriaService },
        { provide: NotificationService, useValue: mockNotificationService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(AuditoriaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('debe crearse y cargar lista de alertas al iniciar', () => {
    expect(component).toBeTruthy();
    expect(mockAuditoriaService.listarAlertasSeguridadPendientes).toHaveBeenCalled();
    expect(component.alertas().length).toBe(1);
    expect(component.alertas()[0].tipoAlerta).toBe('RAFAGA_403_ACCESO_DENEGADO');
  });

  it('debe auditar la cadena de integridad criptográfica SHA-256 (UC-PROV-29)', () => {
    component.auditarCadena();

    expect(mockAuditoriaService.apiAuditoriaVerificarIntegridadGet).toHaveBeenCalled();
    expect(component.resultadoIntegridad()?.estado).toBe('INTEGRA');
    expect(component.resultadoIntegridad()?.totalEventosVerificados).toBe(850);
    expect(mockNotificationService.showSuccess).toHaveBeenCalled();
  });

  it('debe marcar una alerta de seguridad como atendida', () => {
    component.marcarLeida(1);

    expect(mockAuditoriaService.marcarAlertaSeguridadLeida).toHaveBeenCalledWith(1);
    expect(mockNotificationService.showSuccess).toHaveBeenCalled();
  });
});
