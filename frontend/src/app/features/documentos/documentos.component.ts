import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { DocumentosYReportesAcadmicosService } from '../../core/api/generated/api/documentos-y-reportes-acadmicos.service';
import { ReportesInstitucionalesService } from '../../core/api/generated/api/reportes-institucionales.service';
import { EstructuraAcadmicaAosLectivosService } from '../../core/api/generated/api/estructura-acadmica-aos-lectivos.service';
import { MatrculasService } from '../../core/api/generated/api/matrculas.service';
import { NotificationService } from '../../core/services/notification.service';
import { RegistroEmisionResponse } from '../../core/api/generated/model/registro-emision-response';
import { ReporteCoberturaSimatResponse } from '../../core/api/generated/model/reporte-cobertura-simat-response';
import { AnioLectivoResponse } from '../../core/api/generated/model/anio-lectivo-response';
import { MatriculaResponse } from '../../core/api/generated/model/matricula-response';
import { EstadoAnioLectivo } from '../../core/api/generated/model/estado-anio-lectivo';

@Component({
  selector: 'app-documentos',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <div class="documentos-container">
      <header class="header">
        <div>
          <h2>Documentos Oficiales y Reportes Regulatorios</h2>
          <p class="subtitle">Expedición de certificados con verificación QR, firmas oficiales y reportes SIMAT/DANE (UC-05, UC-11, UC-27, UC-PROV-27)</p>
        </div>
      </header>

      <!-- TABS -->
      <div class="tabs-bar">
        <button
          type="button"
          class="tab-btn"
          [class.active]="tabActiva() === 'expedicion'"
          (click)="tabActiva.set('expedicion')"
        >
          📜 Expedición de Certificados y Constancias
        </button>
        <button
          type="button"
          class="tab-btn"
          [class.active]="tabActiva() === 'verificacion'"
          (click)="tabActiva.set('verificacion')"
        >
          🔍 Verificación de Autenticidad (QR / Código)
        </button>
        <button
          type="button"
          class="tab-btn"
          [class.active]="tabActiva() === 'reportes'"
          (click)="tabActiva.set('reportes')"
        >
          📊 Reportes Sectoriales SIMAT / DANE
        </button>
      </div>

      <!-- TAB 1: EXPEDICIÓN -->
      @if (tabActiva() === 'expedicion') {
        <div class="tab-content">
          <form [formGroup]="formExpedicion" (ngSubmit)="expedirDocumento()" class="form-card">
            <h3>Generación de Documento Académico Oficial (UC-PROV-27)</h3>
            <p class="card-desc">
              Los documentos oficiales se generan exclusivamente en backend incorporando firmas digitales de Rectoría y Secretaría Académica con código único de verificación.
            </p>

            <div class="form-row">
              <div class="form-col" style="max-width: 220px;">
                <label for="docAnio">Año Lectivo:</label>
                <select id="docAnio" class="form-control" [value]="anioSeleccionadoId()" (change)="onAnioDocumentosChange($event)">
                  @for (a of anios(); track a.id) {
                    <option [value]="a.id">Año {{ a.anio }} ({{ a.estado }})</option>
                  }
                </select>
              </div>
              <div class="form-col">
                <label for="tipoDoc">Tipo de Documento Oficial: *</label>
                <select id="tipoDoc" formControlName="tipoDocumento" class="form-control">
                  <option value="CONSTANCIA">Constancia de Matrícula Activa</option>
                  <option value="CERTIFICADO_ANUAL">Certificado Oficial de Estudio Anual (Años Cerrados)</option>
                  <option value="CERTIFICADO_PARCIAL">Certificado de Notas Parciales por Retiro (RN-DOC-02)</option>
                </select>
              </div>
              <div class="form-col">
                <label for="matId">Estudiante Matriculado: *</label>
                <select id="matId" formControlName="matriculaId" class="form-control">
                  @if (matriculas().length === 0) {
                    <option [ngValue]="null">-- Sin estudiantes matriculados en este año --</option>
                  } @else {
                    <option [ngValue]="null">-- Seleccione Estudiante Matriculado --</option>
                  }
                  @for (m of matriculas(); track m.id) {
                    <option [ngValue]="m.id">
                      {{ m.estudianteNombreCompleto || m.estudianteDocumento }} ({{ m.numeroMatricula || ('MAT-' + m.id) }} - Grupo: {{ m.grupoNombre }})
                    </option>
                  }
                </select>
                @if (formExpedicion.get('matriculaId')?.touched && formExpedicion.get('matriculaId')?.invalid) {
                  <small class="text-danger">Debe seleccionar un estudiante matriculado.</small>
                }
              </div>
            </div>

            <div class="form-actions">
              <button type="submit" [disabled]="formExpedicion.invalid || expidiendo()" class="btn-primary">
                @if (expidiendo()) {
                  <span>Generando Documento...</span>
                } @else {
                  <span>Expedir Documento Oficial</span>
                }
              </button>
            </div>
          </form>

          @if (documentoGenerado()) {
            <div class="resultado-doc-card">
              <div class="doc-header">
                <h3>Documento Expedido Exitosamente</h3>
                <span class="badge badge-success">Vigente</span>
              </div>

              <div class="doc-meta">
                <p><strong>Tipo de Documento:</strong> {{ documentoGenerado()?.tipoDocumento }}</p>
                <p><strong>Código Único de Verificación:</strong> <code>{{ documentoGenerado()?.codigoVerificacion }}</code></p>
                <p><strong>Fecha de Expedición:</strong> {{ documentoGenerado()?.fechaEmision }}</p>
                <p><strong>Número Consecutivo:</strong> {{ documentoGenerado()?.numeroConsecutivo }}</p>
              </div>
            </div>
          }
        </div>
      }

      <!-- TAB 2: VERIFICACIÓN -->
      @if (tabActiva() === 'verificacion') {
        <div class="tab-content">
          <div class="form-card">
            <h3>Verificación Pública de Autenticidad (QR / Código — UC-05, UC-11)</h3>
            <p class="card-desc">
              Permite validar la autenticidad e integridad de cualquier certificado o constancia emitida por la institución.
            </p>

            <div class="verif-row">
              <input
                type="text"
                [value]="codigoVerificacionBusqueda()"
                (input)="onCodigoChange($event)"
                placeholder="Ingrese el código de verificación (ej. CERT-2026-X9A)"
                class="form-control"
              />
              <button type="button" [disabled]="verificando()" class="btn-primary" (click)="verificar()">
                Verificar Autenticidad
              </button>
            </div>

            @if (documentoVerificado()) {
              <div class="verif-result" [class.result-ok]="!documentoVerificado()?.anulado">
                <h4>Dictamen de Verificación Oficial</h4>
                <p>Estado del Documento: <strong>{{ documentoVerificado()?.anulado ? 'ANULADO' : 'EMITIDO_VIGENTE' }}</strong></p>
                <p>Consecutivo: <strong>{{ documentoVerificado()?.numeroConsecutivoCompleto }}</strong></p>
                <p>Fecha de Emisión: {{ documentoVerificado()?.fechaEmision }}</p>
                <p>Firmantes Registrados: Rectoría y Secretaría Académica</p>
              </div>
            }
          </div>
        </div>
      }

      <!-- TAB 3: REPORTES SECTORIALES -->
      @if (tabActiva() === 'reportes') {
        <div class="tab-content">
          <div class="content-toolbar" style="margin-bottom: 1.25rem; display: flex; align-items: center; gap: 0.75rem;">
            <label for="repAnio" style="font-weight: 600;">Año Lectivo de Reporte:</label>
            <select id="repAnio" class="form-control" style="width: auto; min-width: 200px;" [value]="anioSeleccionadoId()" (change)="onAnioDocumentosChange($event)">
              @for (a of anios(); track a.id) {
                <option [value]="a.id">Año {{ a.anio }} ({{ a.estado }})</option>
              }
            </select>
          </div>

          <div class="cards-grid">
            <div class="reporte-card">
              <h4>Reporte de Cobertura SIMAT / DANE (UC-27)</h4>
              <p class="card-desc">Matrículas activas, desglose por género y cupos institucionales para reporte oficial.</p>
              <button type="button" [disabled]="cargandoReporte()" class="btn-secondary" (click)="generarSimat()">
                Generar Reporte SIMAT
              </button>

              @if (reporteSimat()) {
                <div class="reporte-datos">
                  <p>Año Lectivo: <strong>{{ reporteSimat()?.anio }}</strong></p>
                  <p>Total Matriculados: <strong>{{ reporteSimat()?.totalMatriculados }}</strong></p>
                  <p>Total Cupos Disponibles: {{ reporteSimat()?.totalCupos }}</p>
                </div>
              }
            </div>

            <div class="reporte-card">
              <h4>Exportación Oficial a Hoja de Cálculo</h4>
              <p class="card-desc">Descarga regulada de reportes agregados para secretarías de educación en formato CSV/XLSX.</p>
              <button type="button" class="btn-secondary" (click)="exportarReporte('COBERTURA_SIMAT')">
                📥 Exportar Cobertura (CSV)
              </button>
            </div>
          </div>
        </div>
      }
    </div>
  `,
  styles: [`
    .documentos-container {
      max-width: 1000px;
      margin: 0 auto;
      font-family: system-ui, -apple-system, sans-serif;
    }
    .header { margin-bottom: 1.5rem; }
    h2 { margin: 0; color: #1e293b; font-size: 1.4rem; }
    .subtitle { margin: 0.25rem 0 0; color: #64748b; font-size: 0.88rem; }
    .tabs-bar {
      display: flex;
      gap: 0.5rem;
      border-bottom: 2px solid #e2e8f0;
      margin-bottom: 1.5rem;
    }
    .tab-btn {
      padding: 0.65rem 1.25rem;
      border: none;
      background: none;
      font-size: 0.95rem;
      font-weight: 600;
      color: #64748b;
      cursor: pointer;
      border-bottom: 2px solid transparent;
      margin-bottom: -2px;
      transition: all 0.15s;
    }
    .tab-btn:hover { color: #1e293b; }
    .tab-btn.active { color: #2563eb; border-bottom-color: #2563eb; }
    .form-card, .reporte-card {
      background: white;
      border: 1px solid #e2e8f0;
      border-radius: 8px;
      padding: 1.5rem;
      box-shadow: 0 1px 3px rgba(0,0,0,0.05);
      margin-bottom: 1.5rem;
    }
    h3 { margin-top: 0; color: #1e293b; font-size: 1.15rem; }
    h4 { margin-top: 0; color: #1e40af; font-size: 1rem; }
    .card-desc { font-size: 0.85rem; color: #64748b; margin-bottom: 1.25rem; line-height: 1.45; }
    .form-row { display: flex; gap: 1rem; margin-bottom: 1rem; }
    .form-col { flex: 1; display: flex; flex-direction: column; }
    label { font-size: 0.85rem; font-weight: 500; margin-bottom: 0.25rem; color: #475569; }
    .form-control { padding: 0.55rem 0.75rem; border: 1px solid #cbd5e1; border-radius: 4px; font-size: 0.9rem; }
    .form-actions { display: flex; justify-content: flex-end; margin-top: 1rem; }
    .btn-primary { background: #2563eb; color: white; border: none; padding: 0.6rem 1.25rem; border-radius: 4px; font-weight: 600; font-size: 0.9rem; cursor: pointer; }
    .btn-primary:disabled { background: #93c5fd; cursor: not-allowed; }
    .btn-secondary { background: #e2e8f0; color: #334155; border: none; padding: 0.55rem 1.15rem; border-radius: 4px; font-size: 0.88rem; cursor: pointer; font-weight: 500; }
    .resultado-doc-card {
      background: #f0fdf4;
      border: 1px solid #bbf7d0;
      border-radius: 8px;
      padding: 1.25rem;
    }
    .doc-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 0.5rem; }
    .doc-header h3 { margin: 0; color: #166534; }
    .doc-meta { font-size: 0.9rem; color: #334155; line-height: 1.5; }
    .doc-meta code { background: #e2e8f0; padding: 0.2rem 0.4rem; border-radius: 4px; font-weight: 600; }
    .verif-row { display: flex; gap: 0.75rem; margin-bottom: 1.5rem; max-width: 550px; }
    .verif-result { background: #f8fafc; border: 1px solid #e2e8f0; border-radius: 6px; padding: 1rem; font-size: 0.9rem; }
    .result-ok { border-left: 4px solid #22c55e; }
    .cards-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 1.5rem; }
    .reporte-datos { margin-top: 1rem; background: #f8fafc; padding: 0.85rem; border-radius: 6px; font-size: 0.88rem; color: #334155; }
    .badge { padding: 0.2rem 0.5rem; border-radius: 4px; font-size: 0.75rem; font-weight: 600; }
    .badge-success { background: #dcfce7; color: #166534; }
  `]
})
export class DocumentosComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly documentosService = inject(DocumentosYReportesAcadmicosService);
  private readonly reportesService = inject(ReportesInstitucionalesService);
  private readonly aniosService = inject(EstructuraAcadmicaAosLectivosService);
  private readonly matriculasService = inject(MatrculasService);
  private readonly notificationService = inject(NotificationService);

  readonly tabActiva = signal<'expedicion' | 'verificacion' | 'reportes'>('expedicion');
  readonly expidiendo = signal(false);
  readonly verificando = signal(false);
  readonly cargandoReporte = signal(false);

  readonly anios = signal<AnioLectivoResponse[]>([]);
  readonly matriculas = signal<MatriculaResponse[]>([]);
  readonly anioSeleccionadoId = signal<number | null>(null);

  readonly documentoGenerado = signal<any | null>(null);
  readonly documentoVerificado = signal<RegistroEmisionResponse | null>(null);
  readonly reporteSimat = signal<ReporteCoberturaSimatResponse | null>(null);

  readonly codigoVerificacionBusqueda = signal<string>('');

  readonly formExpedicion = this.fb.group({
    tipoDocumento: ['CONSTANCIA', [Validators.required]],
    matriculaId: [null as number | null, [Validators.required]]
  });

  ngOnInit(): void {
    this.cargarAnios();
  }

  cargarAnios(): void {
    this.aniosService.apiAniosLectivosGet().subscribe({
      next: (data) => {
        this.anios.set(data);
        if (data.length > 0) {
          const activo = data.find(a => a.estado === EstadoAnioLectivo.Activo) || data[0];
          this.anioSeleccionadoId.set(activo.id!);
          this.cargarMatriculas(activo.id!);
        }
      }
    });
  }

  cargarMatriculas(anioId: number): void {
    this.matriculasService.listarMatriculasPorAnioLectivo(anioId, 0, 100).subscribe({
      next: (paged) => {
        const list = paged.content || [];
        this.matriculas.set(list);
        if (list.length > 0 && !this.formExpedicion.get('matriculaId')?.value) {
          this.formExpedicion.patchValue({ matriculaId: list[0].id! });
        } else if (list.length === 0) {
          this.formExpedicion.patchValue({ matriculaId: null });
        }
      }
    });
  }

  onAnioDocumentosChange(event: Event): void {
    const aId = Number((event.target as HTMLSelectElement).value);
    this.anioSeleccionadoId.set(aId);
    this.cargarMatriculas(aId);
    this.reporteSimat.set(null);
  }

  onCodigoChange(event: Event): void {
    this.codigoVerificacionBusqueda.set((event.target as HTMLInputElement).value);
  }

  expedirDocumento(): void {
    if (this.formExpedicion.invalid) return;

    this.expidiendo.set(true);
    const { tipoDocumento, matriculaId } = this.formExpedicion.getRawValue();

    if (tipoDocumento === 'CONSTANCIA') {
      this.documentosService.generarConstanciaMatricula(matriculaId!).subscribe({
        next: (doc) => {
          this.expidiendo.set(false);
          this.documentoGenerado.set(doc);
          this.notificationService.showSuccess('Constancia de matrícula expedida exitosamente.');
        },
        error: () => this.expidiendo.set(false)
      });
    } else if (tipoDocumento === 'CERTIFICADO_ANUAL') {
      this.documentosService.generarCertificadoEstudio(matriculaId!).subscribe({
        next: (doc) => {
          this.expidiendo.set(false);
          this.documentoGenerado.set(doc);
          this.notificationService.showSuccess('Certificado oficial de estudio expedido exitosamente.');
        },
        error: () => this.expidiendo.set(false)
      });
    } else {
      this.documentosService.generarCertificadoNotasParciales(matriculaId!).subscribe({
        next: (doc) => {
          this.expidiendo.set(false);
          this.documentoGenerado.set(doc);
          this.notificationService.showSuccess('Certificado de notas parciales expedido exitosamente.');
        },
        error: () => this.expidiendo.set(false)
      });
    }
  }

  verificar(): void {
    const cod = this.codigoVerificacionBusqueda();
    if (!cod) return;

    this.verificando.set(true);
    this.documentosService.verificarDocumento(cod).subscribe({
      next: (data) => {
        this.verificando.set(false);
        this.documentoVerificado.set(data);
        this.notificationService.showSuccess('Documento verificado como auténtico y válido.');
      },
      error: () => {
        this.verificando.set(false);
        this.documentoVerificado.set(null);
      }
    });
  }

  generarSimat(): void {
    const anioId = this.anioSeleccionadoId() || 1;
    this.cargandoReporte.set(true);
    this.reportesService.generarReporteCoberturaSimat(anioId).subscribe({
      next: (rep) => {
        this.cargandoReporte.set(false);
        this.reporteSimat.set(rep);
        this.notificationService.showInfo('Reporte de cobertura SIMAT generado.');
      },
      error: () => this.cargandoReporte.set(false)
    });
  }

  exportarReporte(tipo: string): void {
    const anioId = this.anioSeleccionadoId() || 1;
    this.reportesService.exportarReporteInstitucional(tipo, anioId, 'CSV').subscribe({
      next: (blobData: any) => {
        const content = blobData instanceof Blob ? blobData : blobData;
        const blob = new Blob(['\uFEFF', content], { type: 'text/csv;charset=utf-8;' });
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `Reporte_${tipo}_${anioId}.csv`;
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        window.URL.revokeObjectURL(url);
        this.notificationService.showSuccess('Reporte descargado correctamente en formato CSV.', 'Descarga Exitosa');
      }
    });
  }
}
