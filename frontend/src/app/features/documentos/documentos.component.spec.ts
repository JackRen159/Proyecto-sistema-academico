import { describe, it, expect, vi, beforeEach } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { DocumentosComponent } from './documentos.component';
import { DocumentosYReportesAcadmicosService } from '../../core/api/generated/api/documentos-y-reportes-acadmicos.service';
import { ReportesInstitucionalesService } from '../../core/api/generated/api/reportes-institucionales.service';
import { EstructuraAcadmicaAosLectivosService } from '../../core/api/generated/api/estructura-acadmica-aos-lectivos.service';
import { MatrculasService } from '../../core/api/generated/api/matrculas.service';
import { NotificationService } from '../../core/services/notification.service';

describe('DocumentosComponent', () => {
  let component: DocumentosComponent;
  let fixture: ComponentFixture<DocumentosComponent>;
  let mockDocumentosService: any;
  let mockReportesService: any;
  let mockAniosService: any;
  let mockMatriculasService: any;
  let mockNotificationService: any;

  beforeEach(async () => {
    mockDocumentosService = {
      generarConstanciaMatricula: vi.fn().mockReturnValue(of({
        codigoVerificacion: 'CONST-2026-X1',
        tipoDocumento: 'CONSTANCIA_ESTUDIO',
        numeroConsecutivo: '001',
        fechaEmision: '2026-03-10'
      })),
      generarCertificadoEstudio: vi.fn().mockReturnValue(of({})),
      generarCertificadoNotasParciales: vi.fn().mockReturnValue(of({})),
      verificarDocumento: vi.fn().mockReturnValue(of({
        codigoVerificacion: 'CONST-2026-X1',
        anulado: false,
        numeroConsecutivoCompleto: '001',
        fechaEmision: '2026-03-10'
      }))
    };

    mockReportesService = {
      generarReporteCoberturaSimat: vi.fn().mockReturnValue(of({
        anio: 2026,
        totalMatriculados: 450,
        totalCupos: 500
      })),
      exportarReporteInstitucional: vi.fn().mockReturnValue(of(new Blob()))
    };

    mockAniosService = {
      apiAniosLectivosGet: vi.fn().mockReturnValue(of([{ id: 1, anio: 2026, estado: 'ACTIVO' }]))
    };

    mockMatriculasService = {
      listarMatriculasPorAnioLectivo: vi.fn().mockReturnValue(of({ content: [{ id: 101, numeroMatricula: 'MAT-2026-001', estudianteNombreCompleto: 'Juan Pérez', grupoNombre: '10A' }] }))
    };

    mockNotificationService = {
      showSuccess: vi.fn(),
      showInfo: vi.fn(),
      showError: vi.fn(),
      showWarning: vi.fn()
    };

    await TestBed.configureTestingModule({
      imports: [DocumentosComponent],
      providers: [
        { provide: DocumentosYReportesAcadmicosService, useValue: mockDocumentosService },
        { provide: ReportesInstitucionalesService, useValue: mockReportesService },
        { provide: EstructuraAcadmicaAosLectivosService, useValue: mockAniosService },
        { provide: MatrculasService, useValue: mockMatriculasService },
        { provide: NotificationService, useValue: mockNotificationService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(DocumentosComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('debe crearse correctamente', () => {
    expect(component).toBeTruthy();
  });

  it('debe expedir constancia de matrícula oficial', () => {
    component.formExpedicion.setValue({
      tipoDocumento: 'CONSTANCIA',
      matriculaId: 101
    });

    component.expedirDocumento();

    expect(mockDocumentosService.generarConstanciaMatricula).toHaveBeenCalledWith(101);
    expect(mockNotificationService.showSuccess).toHaveBeenCalled();
    expect(component.documentoGenerado()?.codigoVerificacion).toBe('CONST-2026-X1');
  });

  it('debe verificar autenticidad de un documento por código', () => {
    component.tabActiva.set('verificacion');
    component.codigoVerificacionBusqueda.set('CONST-2026-X1');

    component.verificar();

    expect(mockDocumentosService.verificarDocumento).toHaveBeenCalledWith('CONST-2026-X1');
    expect(mockNotificationService.showSuccess).toHaveBeenCalled();
    expect(component.documentoVerificado()?.anulado).toBe(false);
  });

  it('debe generar reporte de cobertura SIMAT / DANE', () => {
    component.tabActiva.set('reportes');
    component.generarSimat();

    expect(mockReportesService.generarReporteCoberturaSimat).toHaveBeenCalledWith(1);
    expect(component.reporteSimat()?.totalMatriculados).toBe(450);
  });
});
