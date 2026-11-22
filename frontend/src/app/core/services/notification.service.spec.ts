import { TestBed } from '@angular/core/testing';
import { NotificationService } from './notification.service';

describe('NotificationService', () => {
  let service: NotificationService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(NotificationService);
    service.clear();
  });

  it('debe inicializarse sin notificaciones', () => {
    expect(service.toasts().length).toBe(0);
  });

  it('debe agregar notificaciones de éxito y error a la lista', () => {
    service.showSuccess('Guardado con éxito', 'Éxito');
    service.showError('Falló la operación', 'Error');

    const lista = service.toasts();
    expect(lista.length).toBe(2);
    expect(lista[0].type).toBe('success');
    expect(lista[0].mensaje).toBe('Guardado con éxito');
    expect(lista[1].type).toBe('error');
    expect(lista[1].mensaje).toBe('Falló la operación');
  });

  it('debe permitir cerrar manualmente una notificación por ID', () => {
    service.showInfo('Mensaje 1');
    service.showWarning('Mensaje 2');

    const idAEliminar = service.toasts()[0].id;
    service.dismiss(idAEliminar);

    expect(service.toasts().length).toBe(1);
    expect(service.toasts()[0].mensaje).toBe('Mensaje 2');
  });

  it('debe limpiar todas las notificaciones con clear()', () => {
    service.showSuccess('Uno');
    service.showError('Dos');
    service.clear();

    expect(service.toasts().length).toBe(0);
  });
});
