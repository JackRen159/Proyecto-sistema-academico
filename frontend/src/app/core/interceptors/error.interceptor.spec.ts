import { TestBed } from '@angular/core/testing';
import { HttpClient, HttpErrorResponse, provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { Router } from '@angular/router';
import { errorInterceptor } from './error.interceptor';
import { AuthService } from '../services/auth.service';
import { NotificationService } from '../services/notification.service';

describe('errorInterceptor', () => {
  let http: HttpClient;
  let httpMock: HttpTestingController;
  let authServiceSpy: any;
  let routerSpy: any;
  let notificationServiceSpy: any;

  beforeEach(() => {
    authServiceSpy = { clearSession: vi.fn() };
    routerSpy = { navigate: vi.fn() };
    notificationServiceSpy = {
      showError: vi.fn(),
      showWarning: vi.fn(),
      showSuccess: vi.fn(),
      showInfo: vi.fn()
    };

    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(withInterceptors([errorInterceptor])),
        provideHttpClientTesting(),
        { provide: AuthService, useValue: authServiceSpy },
        { provide: Router, useValue: routerSpy },
        { provide: NotificationService, useValue: notificationServiceSpy }
      ]
    });

    http = TestBed.inject(HttpClient);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('debe manejar error 401 redirigiendo a login y emitiendo advertencia', () => {
    http.get('/api/estudiantes').subscribe({
      next: () => { throw new Error('debió fallar con 401'); },
      error: (err: HttpErrorResponse) => {
        expect(err.status).toBe(401);
      }
    });

    const req = httpMock.expectOne('/api/estudiantes');
    req.flush('Unauthorized', { status: 401, statusText: 'Unauthorized' });

    expect(authServiceSpy.clearSession).toHaveBeenCalled();
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/login'], {
      queryParams: { sesionExpirada: 'true' }
    });
    expect(notificationServiceSpy.showWarning).toHaveBeenCalled();
  });

  it('debe manejar error 403 emitiendo mensaje de acceso denegado sin desloguear', () => {
    http.post('/api/calificaciones/cerrar', {}).subscribe({
      next: () => { throw new Error('debió fallar con 403'); },
      error: (err: HttpErrorResponse) => {
        expect(err.status).toBe(403);
      }
    });

    const req = httpMock.expectOne('/api/calificaciones/cerrar');
    req.flush({ mensaje: 'No tiene asignación vigente sobre este grupo' }, { status: 403, statusText: 'Forbidden' });

    expect(authServiceSpy.clearSession).not.toHaveBeenCalled();
    expect(notificationServiceSpy.showError).toHaveBeenCalledWith(
      'No tiene asignación vigente sobre este grupo',
      'Acceso Restringido (403)'
    );
  });

  it('debe manejar error 409 notificando conflicto con regla de negocio', () => {
    http.post('/api/asistencia/planilla', {}).subscribe({
      next: () => { throw new Error('debió fallar con 409'); },
      error: (err: HttpErrorResponse) => {
        expect(err.status).toBe(409);
      }
    });

    const req = httpMock.expectOne('/api/asistencia/planilla');
    req.flush({ mensaje: 'Día no lectivo según calendario institucional' }, { status: 409, statusText: 'Conflict' });

    expect(notificationServiceSpy.showError).toHaveBeenCalledWith(
      'Día no lectivo según calendario institucional',
      'Conflicto de Negocio (409)'
    );
  });
});
