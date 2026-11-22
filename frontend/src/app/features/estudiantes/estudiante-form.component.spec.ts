import { describe, it, expect, vi, beforeEach } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router, provideRouter } from '@angular/router';
import { of } from 'rxjs';
import { EstudianteFormComponent } from './estudiante-form.component';
import { EstudiantesService } from '../../core/api/generated/api/estudiantes.service';
import { NotificationService } from '../../core/services/notification.service';
import { TipoDocumento } from '../../core/api/generated/model/tipo-documento';
import { Parentesco } from '../../core/api/generated/model/parentesco';

describe('EstudianteFormComponent', () => {
  let component: EstudianteFormComponent;
  let fixture: ComponentFixture<EstudianteFormComponent>;
  let mockEstudiantesService: any;
  let mockNotificationService: any;
  let router: Router;

  beforeEach(async () => {
    mockEstudiantesService = {
      apiEstudiantesExpedienteCompletoPost: vi.fn().mockReturnValue(of({
        estudianteId: 1,
        estudianteDocumento: '1001',
        estudianteNombreCompleto: 'Santiago Gómez',
        mensaje: 'Expediente registrado exitosamente'
      }))
    };

    mockNotificationService = {
      showSuccess: vi.fn(),
      showError: vi.fn()
    };

    await TestBed.configureTestingModule({
      imports: [EstudianteFormComponent],
      providers: [
        provideRouter([]),
        { provide: EstudiantesService, useValue: mockEstudiantesService },
        { provide: NotificationService, useValue: mockNotificationService }
      ]
    }).compileComponents();

    router = TestBed.inject(Router);
    vi.spyOn(router, 'navigate').mockImplementation(() => Promise.resolve(true));

    fixture = TestBed.createComponent(EstudianteFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('debe crearse con formulario inválido por campos obligatorios faltantes', () => {
    expect(component).toBeTruthy();
    expect(component.form.invalid).toBe(true);
  });

  it('debe enviar la petición de expediente completo y redirigir al completar el formulario', () => {
    component.form.patchValue({
      nombres: 'Santiago',
      apellidos: 'Gómez',
      tipoDocumento: TipoDocumento.Ti,
      documento: '10203040',
      fechaNacimiento: '2012-05-10',
      direccion: 'Calle 10 # 20-30',
      acudienteNombres: 'Martha',
      acudienteApellidos: 'Gómez',
      acudienteTipoDocumento: TipoDocumento.Cc,
      acudienteDocumento: '40506070',
      acudienteTelefono: '3001234567',
      parentesco: Parentesco.Madre
    });

    expect(component.form.valid).toBe(true);

    component.guardarExpediente();

    expect(mockEstudiantesService.apiEstudiantesExpedienteCompletoPost).toHaveBeenCalled();
    expect(mockNotificationService.showSuccess).toHaveBeenCalled();
    expect(router.navigate).toHaveBeenCalledWith(['/estudiantes']);
  });
});
