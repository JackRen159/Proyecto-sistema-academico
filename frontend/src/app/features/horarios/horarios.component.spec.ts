import { describe, it, expect, vi, beforeEach } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { HorariosComponent } from './horarios.component';
import { HorariosEscolaresService } from '../../core/api/generated/api/horarios-escolares.service';
import { NotificationService } from '../../core/services/notification.service';
import { TipoEspacioFisico } from '../../core/api/generated/model/tipo-espacio-fisico';

describe('HorariosComponent', () => {
  let component: HorariosComponent;
  let fixture: ComponentFixture<HorariosComponent>;
  let mockHorariosService: any;
  let mockNotificationService: any;

  const mockAulas = [
    {
      id: 1,
      codigo: 'A-101',
      nombre: 'Aula 101',
      tipoEspacio: TipoEspacioFisico.AulaEstandar,
      capacidadMaxima: 35,
      estado: 'DISPONIBLE'
    }
  ];

  beforeEach(async () => {
    mockHorariosService = {
      listarAulas: vi.fn().mockReturnValue(of(mockAulas)),
      crearAula: vi.fn().mockReturnValue(of({ id: 2, codigo: 'LAB-1', nombre: 'Lab Ciencias', capacidadMaxima: 30 })),
      consultarUbicacionActual: vi.fn().mockReturnValue(of({ aulaCodigo: 'A-101', aulaNombre: 'Aula 101' })),
      consultarMiAgendaDocente: vi.fn().mockReturnValue(of({ profesorNombreCompleto: 'Juan Pérez', totalBloquesSemanales: 20 }))
    };

    mockNotificationService = {
      showSuccess: vi.fn(),
      showInfo: vi.fn(),
      showError: vi.fn()
    };

    await TestBed.configureTestingModule({
      imports: [HorariosComponent],
      providers: [
        { provide: HorariosEscolaresService, useValue: mockHorariosService },
        { provide: NotificationService, useValue: mockNotificationService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(HorariosComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('debe crearse y cargar lista de aulas', () => {
    expect(component).toBeTruthy();
    expect(mockHorariosService.listarAulas).toHaveBeenCalled();
    expect(component.aulas().length).toBe(1);
    expect(component.aulas()[0].codigo).toBe('A-101');
  });

  it('debe guardar nueva aula física', () => {
    component.formAula.setValue({
      codigo: 'LAB-1',
      nombre: 'Lab Ciencias',
      tipoEspacio: TipoEspacioFisico.LaboratorioCiencias,
      capacidadMaxima: 30,
      sede: 'Sede Principal'
    });

    component.guardarAula();

    expect(mockHorariosService.crearAula).toHaveBeenCalled();
    expect(mockNotificationService.showSuccess).toHaveBeenCalled();
  });

  it('debe localizar ubicación física en tiempo real (UC-PROV-22 A1)', () => {
    component.tipoBusquedaLocalizador.set('GRUPO');
    component.idRecursoLocalizador.set(10);

    component.localizarUbicacion();

    expect(mockHorariosService.consultarUbicacionActual).toHaveBeenCalledWith('GRUPO', 10);
    expect(mockNotificationService.showInfo).toHaveBeenCalled();
    expect(component.ubicacionActual()?.aulaCodigo).toBe('A-101');
  });
});
