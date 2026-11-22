import { describe, it, expect, vi, beforeEach } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router, provideRouter } from '@angular/router';
import { of } from 'rxjs';
import { PrimerIngresoComponent } from './primer-ingreso.component';
import { AutenticacinService } from '../../core/api/generated/api/autenticacin.service';
import { AuthService } from '../../core/services/auth.service';
import { NotificationService } from '../../core/services/notification.service';

describe('PrimerIngresoComponent', () => {
  let component: PrimerIngresoComponent;
  let fixture: ComponentFixture<PrimerIngresoComponent>;
  let mockAutenticacionService: any;
  let mockAuthService: any;
  let mockNotificationService: any;
  let router: Router;

  beforeEach(async () => {
    mockAutenticacionService = {
      apiAuthCambiarPasswordPost: vi.fn().mockReturnValue(of({}))
    };
    mockAuthService = {};
    mockNotificationService = {
      showSuccess: vi.fn(),
      showError: vi.fn()
    };

    await TestBed.configureTestingModule({
      imports: [PrimerIngresoComponent],
      providers: [
        provideRouter([]),
        { provide: AutenticacinService, useValue: mockAutenticacionService },
        { provide: AuthService, useValue: mockAuthService },
        { provide: NotificationService, useValue: mockNotificationService }
      ]
    }).compileComponents();

    router = TestBed.inject(Router);
    vi.spyOn(router, 'navigate').mockImplementation(() => Promise.resolve(true));

    fixture = TestBed.createComponent(PrimerIngresoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('debe crearse con formulario inválido por defecto', () => {
    expect(component).toBeTruthy();
    expect(component.activacionForm.invalid).toBe(true);
  });

  it('debe requerir aceptación explícita de política Habeas Data (Ley 1581)', () => {
    component.activacionForm.patchValue({
      passwordActual: 'Temp12345',
      passwordNuevo: 'NuevaSegura123',
      confirmacionPassword: 'NuevaSegura123',
      aceptaPolitica: false
    });

    expect(component.activacionForm.get('aceptaPolitica')?.hasError('required')).toBe(true);
    expect(component.activacionForm.invalid).toBe(true);
  });

  it('debe activar la cuenta cuando el formulario es válido y enviar petición', () => {
    component.activacionForm.setValue({
      passwordActual: 'Temp12345',
      passwordNuevo: 'NuevaSegura123',
      confirmacionPassword: 'NuevaSegura123',
      aceptaPolitica: true
    });

    expect(component.activacionForm.valid).toBe(true);

    component.onSubmit();

    expect(mockAutenticacionService.apiAuthCambiarPasswordPost).toHaveBeenCalledWith({
      passwordActual: 'Temp12345',
      passwordNuevo: 'NuevaSegura123',
      confirmacionPassword: 'NuevaSegura123'
    });
    expect(mockNotificationService.showSuccess).toHaveBeenCalled();
    expect(router.navigate).toHaveBeenCalledWith(['/estudiantes']);
  });
});
