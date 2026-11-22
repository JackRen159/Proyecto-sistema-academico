import { describe, it, expect, vi, beforeEach } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { CierrePromocionComponent } from './cierre-promocion.component';
import { CierreAcadmicoYPromocinService } from '../../core/api/generated/api/cierre-acadmico-y-promocin.service';
import { EstructuraAcadmicaPeriodosService } from '../../core/api/generated/api/estructura-acadmica-periodos.service';
import { EstructuraAcadmicaGruposService } from '../../core/api/generated/api/estructura-acadmica-grupos.service';
import { NotificationService } from '../../core/services/notification.service';
import { ModalidadTituloBachiller } from '../../core/api/generated/model/modalidad-titulo-bachiller';

describe('CierrePromocionComponent', () => {
  let component: CierrePromocionComponent;
  let fixture: ComponentFixture<CierrePromocionComponent>;
  let mockCierreService: any;
  let mockPeriodosService: any;
  let mockGruposService: any;
  let mockNotificationService: any;

  const mockPeriodos = [{ id: 1, nombre: 'Periodo 1', estado: 'ABIERTO' }];
  const mockGrupos = [{ id: 10, nombre: '11A' }];
  const mockDiagnostico = {
    periodoId: 1,
    periodoNombre: 'Periodo 1',
    aptoParaCierre: true,
    totalGruposValidados: 5,
    totalEstudiantesEvaluados: 120,
    bloqueosCriticos: []
  };

  const mockPreclasificacion = [
    {
      matriculaId: 101,
      estudianteNombreCompleto: 'Laura Ramírez',
      promedioGeneral: 4.5,
      areasReprobadas: 0,
      porcentajeInasistenciaAnual: 2,
      clasificacionSugerida: 'PROMOVIDO'
    }
  ];

  beforeEach(async () => {
    mockCierreService = {
      diagnosticarPreCierre: vi.fn().mockReturnValue(of(mockDiagnostico)),
      cerrarPeriodo: vi.fn().mockReturnValue(of([])),
      preclasificarPromocionGrupo: vi.fn().mockReturnValue(of(mockPreclasificacion)),
      registrarRequisitosTitulacion: vi.fn().mockReturnValue(of({}))
    };

    mockPeriodosService = {
      apiAniosLectivosAnioIdPeriodosGet: vi.fn().mockReturnValue(of(mockPeriodos))
    };

    mockGruposService = {
      apiGruposGet: vi.fn().mockReturnValue(of(mockGrupos))
    };

    mockNotificationService = {
      showSuccess: vi.fn(),
      showWarning: vi.fn(),
      showError: vi.fn()
    };

    await TestBed.configureTestingModule({
      imports: [CierrePromocionComponent],
      providers: [
        { provide: CierreAcadmicoYPromocinService, useValue: mockCierreService },
        { provide: EstructuraAcadmicaPeriodosService, useValue: mockPeriodosService },
        { provide: EstructuraAcadmicaGruposService, useValue: mockGruposService },
        { provide: NotificationService, useValue: mockNotificationService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(CierrePromocionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('debe crearse y cargar periodos y grupos', () => {
    expect(component).toBeTruthy();
    expect(mockPeriodosService.apiAniosLectivosAnioIdPeriodosGet).toHaveBeenCalled();
    expect(mockGruposService.apiGruposGet).toHaveBeenCalled();
    expect(component.periodos().length).toBe(1);
  });

  it('debe ejecutar diagnóstico pre-cierre y reflejar dictamen apto', () => {
    component.ejecutarDiagnostico();

    expect(mockCierreService.diagnosticarPreCierre).toHaveBeenCalledWith(1);
    expect(component.diagnostico()?.aptoParaCierre).toBe(true);
    expect(mockNotificationService.showSuccess).toHaveBeenCalled();
  });

  it('debe formalizar cierre de periodo emitiendo boletines', () => {
    component.formalizarCierrePeriodo();

    expect(mockCierreService.cerrarPeriodo).toHaveBeenCalledWith({
      periodoId: 1,
      permitirEstudiantesAplazadosIncapacidad: true
    });
    expect(mockNotificationService.showSuccess).toHaveBeenCalled();
  });

  it('debe preclasificar promoción anual del grupo', () => {
    component.tabActiva.set('promocion');
    component.cargarPreclasificacion();

    expect(mockCierreService.preclasificarPromocionGrupo).toHaveBeenCalledWith(10);
    expect(component.preclasificaciones().length).toBe(1);
    expect(component.preclasificaciones()[0].clasificacionSugerida).toBe('PROMOVIDO');
  });

  it('debe registrar requisitos de titulación con servicio social >= 80h (Resolución 4210)', () => {
    component.tabActiva.set('titulacion');
    component.formTitulacion.setValue({
      matriculaId: 101,
      modalidadTitulo: ModalidadTituloBachiller.BachillerAcademico,
      horasServicioSocial: 85,
      codigoRegistroIcfes: 'AC202611112222',
      documentoIdentidadVerificado: true
    });

    component.guardarTitulacion();

    expect(mockCierreService.registrarRequisitosTitulacion).toHaveBeenCalled();
    expect(mockNotificationService.showSuccess).toHaveBeenCalled();
  });
});
