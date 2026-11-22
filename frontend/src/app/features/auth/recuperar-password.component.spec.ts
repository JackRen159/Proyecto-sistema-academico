import { describe, it, expect, vi, beforeEach } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router, provideRouter } from '@angular/router';
import { of } from 'rxjs';
import { RecuperarPasswordComponent } from './recuperar-password.component';
import { AutenticacinService } from '../../core/api/generated/api/autenticacin.service';
import { NotificationService } from '../../core/services/notification.service';

describe('RecuperarPasswordComponent', () => {
  let component: RecuperarPasswordComponent;
  let fixture: ComponentFixture<RecuperarPasswordComponent>;
  let mockAutenticacionService: any;
  let mockNotificationService: any;
  let router: Router;

  beforeEach(async () => {
    mockAutenticacionService = {
      apiAuthRecuperarPasswordSolicitarPost: vi.fn().mockReturnValue(of({})),
      apiAuthRecuperarPasswordConfirmarPost: vi.fn().mockReturnValue(of({}))
    };

    mockNotificationService = {
      showSuccess: vi.fn(),
      showInfo: vi.fn(),
      showError: vi.fn(),
      showWarning: vi.fn()
    };

    await TestBed.configureTestingModule({
      imports: [RecuperarPasswordComponent],
      providers: [
        provideRouter([]),
        { provide: AutenticacinService, useValue: mockAutenticacionService },
        { provide: NotificationService, useValue: mockNotificationService }
      ]
    }).compileComponents();

    router = TestBed.inject(Router);
    vi.spyOn(router, 'navigate').mockImplementation(() => Promise.resolve(true));

    fixture = TestBed.createComponent(RecuperarPasswordComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('debe crearse correctamente e iniciar en paso 1', () => {
    expect(component).toBeTruthy();
    expect(component.paso()).toBe(1);
  });

  it('debe solicitar token y avanzar al paso 2', () => {
    component.solicitudForm.setValue({ identificador: 'usuario_prueba' });
    component.enviarSolicitud();

    expect(mockAutenticacionService.apiAuthRecuperarPasswordSolicitarPost).toHaveBeenCalledWith({
      identificador: 'usuario_prueba'
    });
    expect(component.paso()).toBe(2);
    expect(mockNotificationService.showSuccess).toHaveBeenCalled();
  });

  it('debe validar que las contraseñas coincidan en el paso 2', () => {
    component.paso.set(2);
    component.confirmacionForm.patchValue({
      token: 'tok123',
      passwordNuevo: 'Password123',
      confirmacionPassword: 'OtraPassword'
    });

    expect(component.confirmacionForm.hasError('passwordsNoCoinciden')).toBe(true);
    expect(component.confirmacionForm.invalid).toBe(true);
  });

  it('debe confirmar el restablecimiento y redirigir a /login', () => {
    component.paso.set(2);
    component.confirmacionForm.setValue({
      token: 'tok123',
      passwordNuevo: 'PasswordSegura123',
      confirmacionPassword: 'PasswordSegura123'
    });

    component.confirmarRecuperacion();

    expect(mockAutenticacionService.apiAuthRecuperarPasswordConfirmarPost).toHaveBeenCalledWith({
      token: 'tok123',
      passwordNuevo: 'PasswordSegura123',
      confirmacionPassword: 'PasswordSegura123'
    });
    expect(mockNotificationService.showSuccess).toHaveBeenCalled();
    expect(router.navigate).toHaveBeenCalledWith(['/login']);
  });
});
