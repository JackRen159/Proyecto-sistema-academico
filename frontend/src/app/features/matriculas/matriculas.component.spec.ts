import { describe, it, expect, vi, beforeEach } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { MatriculasComponent } from './matriculas.component';
import { MatrculasService } from '../../core/api/generated/api/matrculas.service';
import { MatrculasNovedadesDePermanenciaService } from '../../core/api/generated/api/matrculas-novedades-de-permanencia.service';
import { EstructuraAcadmicaAosLectivosService } from '../../core/api/generated/api/estructura-acadmica-aos-lectivos.service';
import { EstructuraAcadmicaGruposService } from '../../core/api/generated/api/estructura-acadmica-grupos.service';
import { EstudiantesService } from '../../core/api/generated/api/estudiantes.service';
import { NotificationService } from '../../core/services/notification.service';
import { TipoMatricula } from '../../core/api/generated/model/tipo-matricula';
import { TipoNovedadMatricula } from '../../core/api/generated/model/tipo-novedad-matricula';

describe('MatriculasComponent', () => {
  let component: MatriculasComponent;
  let fixture: ComponentFixture<MatriculasComponent>;
  let mockMatriculasService: any;
  let mockNovedadesService: any;
  let mockAniosService: any;
  let mockGruposService: any;
  let mockEstudiantesService: any;
  let mockNotificationService: any;

  const mockAnios = [{ id: 1, anio: 2026, estado: 'ACTIVO' }];
  const mockGrupos = [{ id: 10, nombre: '7A', cupoMaximo: 35 }];
  const mockMatriculas = [
    {
      id: 100,
      numeroMatricula: 'MAT-2026-001',
      estudianteNombreCompleto: 'Santiago Gómez',
      grupoNombre: '7A',
      tipoMatricula: TipoMatricula.Nuevo,
      estado: 'MATRICULADO_ACTIVO'
    }
  ];

  beforeEach(async () => {
    mockMatriculasService = {
      listarMatriculas: vi.fn().mockReturnValue(of(mockMatriculas)),
      listarMatriculasPorAnioLectivo: vi.fn().mockReturnValue(of({ content: mockMatriculas })),
      matricular: vi.fn().mockReturnValue(of({ id: 101, numeroMatricula: 'MAT-2026-002' }))
    };

    mockNovedadesService = {
      apiMatriculasIdRetiroPost: vi.fn().mockReturnValue(of({}))
    };

    mockAniosService = {
      apiAniosLectivosGet: vi.fn().mockReturnValue(of(mockAnios))
    };

    mockGruposService = {
      apiGruposGet: vi.fn().mockReturnValue(of(mockGrupos))
    };

    mockEstudiantesService = {
      apiEstudiantesGet: vi.fn().mockReturnValue(of({ content: [{ id: 5, nombres: 'Carlos', apellidos: 'Pérez', documento: '12345678' }] }))
    };

    mockNotificationService = {
      showSuccess: vi.fn(),
      showError: vi.fn(),
      showWarning: vi.fn()
    };

    await TestBed.configureTestingModule({
      imports: [MatriculasComponent],
      providers: [
        { provide: MatrculasService, useValue: mockMatriculasService },
        { provide: MatrculasNovedadesDePermanenciaService, useValue: mockNovedadesService },
        { provide: EstructuraAcadmicaAosLectivosService, useValue: mockAniosService },
        { provide: EstructuraAcadmicaGruposService, useValue: mockGruposService },
        { provide: EstudiantesService, useValue: mockEstudiantesService },
        { provide: NotificationService, useValue: mockNotificationService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(MatriculasComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('debe crearse y cargar años lectivos, grupos y matrículas', () => {
    expect(component).toBeTruthy();
    expect(mockAniosService.apiAniosLectivosGet).toHaveBeenCalled();
    expect(component.matriculas().length).toBe(1);
    expect(component.matriculas()[0].numeroMatricula).toBe('MAT-2026-001');
  });

  it('debe formalizar matrícula y cambiar a pestaña de listado', () => {
    component.formMatricula.patchValue({
      estudianteId: 5,
      grupoId: 10,
      tipoMatricula: TipoMatricula.Nuevo,
      fechaMatricula: '2026-01-20',
      aceptaReglamentos: true
    });

    component.formalizarMatricula();

    expect(mockMatriculasService.matricular).toHaveBeenCalled();
    expect(mockNotificationService.showSuccess).toHaveBeenCalled();
    expect(component.tabActiva()).toBe('listado');
  });

  it('debe abrir modal de novedad y registrar retiro voluntario', () => {
    const mat = mockMatriculas[0];
    component.abrirModalNovedad(mat as any);

    expect(component.matriculaSeleccionada()).toBe(mat);

    component.formNovedad.patchValue({
      tipoNovedad: TipoNovedadMatricula.RetiroVoluntario,
      motivo: 'Traslado de ciudad por motivo laboral de los padres'
    });

    component.aplicarNovedad();

    expect(mockNovedadesService.apiMatriculasIdRetiroPost).toHaveBeenCalled();
    expect(mockNotificationService.showSuccess).toHaveBeenCalled();
    expect(component.matriculaSeleccionada()).toBeNull();
  });
});
