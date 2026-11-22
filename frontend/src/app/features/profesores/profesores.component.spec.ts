import { describe, it, expect, vi, beforeEach } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { ProfesoresComponent } from './profesores.component';
import { ProfesoresService } from '../../core/api/generated/api/profesores.service';
import { AsignacionesAcadmicasService } from '../../core/api/generated/api/asignaciones-acadmicas.service';
import { EstructuraAcadmicaAosLectivosService } from '../../core/api/generated/api/estructura-acadmica-aos-lectivos.service';
import { EstructuraAcadmicaGruposService } from '../../core/api/generated/api/estructura-acadmica-grupos.service';
import { EstructuraAcadmicaAsignaturasService } from '../../core/api/generated/api/estructura-acadmica-asignaturas.service';
import { NotificationService } from '../../core/services/notification.service';
import { TipoDocumento } from '../../core/api/generated/model/tipo-documento';

describe('ProfesoresComponent', () => {
  let component: ProfesoresComponent;
  let fixture: ComponentFixture<ProfesoresComponent>;
  let mockProfesoresService: any;
  let mockAsignacionesService: any;
  let mockAniosService: any;
  let mockGruposService: any;
  let mockAsignaturasService: any;
  let mockNotificationService: any;

  const mockProfesores = [
    {
      id: 1,
      documento: '70123456',
      nombres: 'Juan',
      apellidos: 'Pérez',
      correo: 'juan.perez@colegio.edu.co',
      especialidad: 'Matemáticas',
      verificadoInhabilidades: true,
      activo: true
    }
  ];

  const mockAsignaciones = [
    {
      id: 10,
      profesorNombreCompleto: 'Juan Pérez',
      grupoNombre: '7A',
      asignaturaNombre: 'Álgebra',
      intensidadHorariaSemanal: 4,
      esDirectorGrupo: true,
      activo: true
    }
  ];

  beforeEach(async () => {
    mockProfesoresService = {
      apiProfesoresGet: vi.fn().mockReturnValue(of(mockProfesores)),
      apiProfesoresPost: vi.fn().mockReturnValue(of({ id: 2, nombres: 'Ana', apellidos: 'López' }))
    };

    mockAsignacionesService = {
      apiAsignacionesAcademicasGet: vi.fn().mockReturnValue(of(mockAsignaciones)),
      apiAsignacionesAcademicasPost: vi.fn().mockReturnValue(of({ id: 11 })),
      apiAsignacionesAcademicasIdDesactivarPatch: vi.fn().mockReturnValue(of({}))
    };

    mockAniosService = {
      apiAniosLectivosGet: vi.fn().mockReturnValue(of([{ id: 1, anio: 2026 }]))
    };

    mockGruposService = {
      apiGruposGet: vi.fn().mockReturnValue(of([{ id: 10, nombre: '7A' }]))
    };

    mockAsignaturasService = {
      apiAsignaturasGet: vi.fn().mockReturnValue(of([{ id: 50, nombre: 'Álgebra' }]))
    };

    mockNotificationService = {
      showSuccess: vi.fn(),
      showError: vi.fn(),
      showWarning: vi.fn()
    };

    await TestBed.configureTestingModule({
      imports: [ProfesoresComponent],
      providers: [
        { provide: ProfesoresService, useValue: mockProfesoresService },
        { provide: AsignacionesAcadmicasService, useValue: mockAsignacionesService },
        { provide: EstructuraAcadmicaAosLectivosService, useValue: mockAniosService },
        { provide: EstructuraAcadmicaGruposService, useValue: mockGruposService },
        { provide: EstructuraAcadmicaAsignaturasService, useValue: mockAsignaturasService },
        { provide: NotificationService, useValue: mockNotificationService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ProfesoresComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('debe crearse y cargar lista de profesores y asignaciones', () => {
    expect(component).toBeTruthy();
    expect(mockProfesoresService.apiProfesoresGet).toHaveBeenCalled();
    expect(mockAsignacionesService.apiAsignacionesAcademicasGet).toHaveBeenCalled();
    expect(component.profesores().length).toBe(1);
    expect(component.asignaciones().length).toBe(1);
  });

  it('debe exigir verificación obligatoria Ley 1918 de 2018 para registrar docente', () => {
    component.formDocente.patchValue({
      tipoDocumento: TipoDocumento.Cc,
      documento: '80901020',
      nombres: 'Carlos',
      apellidos: 'Ramírez',
      correo: 'carlos@colegio.edu.co',
      verificadoInhabilidades: false // Violación Ley 1918
    });

    expect(component.formDocente.invalid).toBe(true);
  });

  it('debe guardar docente cuando cumple todos los requisitos legales', () => {
    component.formDocente.patchValue({
      tipoDocumento: TipoDocumento.Cc,
      documento: '80901020',
      nombres: 'Carlos',
      apellidos: 'Ramírez',
      correo: 'carlos@colegio.edu.co',
      verificadoInhabilidades: true,
      numeroCertificadoInhabilidades: 'INH-2026-999',
      fechaVerificacionInhabilidades: '2026-01-15'
    });

    expect(component.formDocente.valid).toBe(true);

    component.guardarDocente();

    expect(mockProfesoresService.apiProfesoresPost).toHaveBeenCalled();
    expect(mockNotificationService.showSuccess).toHaveBeenCalled();
  });

  it('debe guardar asignación académica con dirección de grupo', () => {
    component.formAsignacion.setValue({
      profesorId: 1,
      grupoId: 10,
      asignaturaId: 50,
      intensidadHorariaSemanal: 5,
      esDirectorGrupo: true
    });

    expect(component.formAsignacion.valid).toBe(true);

    component.guardarAsignacion();

    expect(mockAsignacionesService.apiAsignacionesAcademicasPost).toHaveBeenCalled();
    expect(mockNotificationService.showSuccess).toHaveBeenCalled();
  });
});
