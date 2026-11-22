import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ConfiguracinInstitucionalService } from '../../core/api/generated/api/configuracin-institucional.service';
import { NotificationService } from '../../core/services/notification.service';
import { InstitucionDatosLegalesResponse } from '../../core/api/generated/model/institucion-datos-legales-response';
import { ConfiguracionSieeResponse } from '../../core/api/generated/model/configuracion-siee-response';
import { ConfiguracionManualConvivenciaResponse } from '../../core/api/generated/model/configuracion-manual-convivencia-response';
import { SimulacionSieeResponse } from '../../core/api/generated/model/simulacion-siee-response';

@Component({
  selector: 'app-configuracion-institucional',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <div class="config-container">
      <header class="header">
        <div>
          <h2>Configuración Institucional y SIEE V2</h2>
          <p class="subtitle">Datos legales, firmas oficiales, SIEE versionado y Manual de Convivencia (UC-26, UC-PROV-13..14)</p>
        </div>
      </header>

      <!-- TABS -->
      <div class="tabs-bar">
        <button
          type="button"
          class="tab-btn"
          [class.active]="tabActiva() === 'institucion'"
          (click)="tabActiva.set('institucion')"
        >
          🏛️ Datos Legales del Colegio
        </button>
        <button
          type="button"
          class="tab-btn"
          [class.active]="tabActiva() === 'siee'"
          (click)="tabActiva.set('siee')"
        >
          📈 SIEE y Escala de Valoración
        </button>
        <button
          type="button"
          class="tab-btn"
          [class.active]="tabActiva() === 'manual'"
          (click)="tabActiva.set('manual')"
        >
          📜 Manual de Convivencia
        </button>
      </div>

      <!-- TAB 1: DATOS LEGALES -->
      @if (tabActiva() === 'institucion') {
        <div class="tab-content">
          <form [formGroup]="formInstitucion" (ngSubmit)="guardarDatosInstitucion()" class="form-card">
            <h3>Identificación y Registro Oficial</h3>
            <div class="form-row">
              <div class="form-col">
                <label for="nombreOficial">Nombre Oficial del Establecimiento: *</label>
                <input id="nombreOficial" type="text" formControlName="nombreOficial" class="form-control" />
              </div>
              <div class="form-col">
                <label for="codigoDane">Código DANE: *</label>
                <input id="codigoDane" type="text" formControlName="codigoDane" class="form-control" />
              </div>
              <div class="form-col">
                <label for="nit">NIT Institucional: *</label>
                <input id="nit" type="text" formControlName="nit" class="form-control" />
              </div>
            </div>

            <div class="form-row">
              <div class="form-col">
                <label for="resolucionAprobacion">Resolución de Aprobación de Estudios: *</label>
                <input id="resolucionAprobacion" type="text" formControlName="resolucionAprobacion" class="form-control" />
              </div>
              <div class="form-col">
                <label for="municipio">Municipio / Departamento:</label>
                <input id="municipio" type="text" formControlName="municipio" placeholder="Ej. Medellín, Antioquia" class="form-control" />
              </div>
            </div>

            <h3 class="section-title">Autoridades Firmantes Oficiales (Boletines y Certificados)</h3>
            <div class="form-row">
              <div class="form-col">
                <label for="nombreRectorFirmante">Nombre del Rector(a): *</label>
                <input id="nombreRectorFirmante" type="text" formControlName="nombreRectorFirmante" class="form-control" />
              </div>
              <div class="form-col">
                <label for="documentoRectorFirmante">Documento Rector(a): *</label>
                <input id="documentoRectorFirmante" type="text" formControlName="documentoRectorFirmante" class="form-control" />
              </div>
            </div>

            <div class="form-row">
              <div class="form-col">
                <label for="nombreSecretarioFirmante">Nombre de la Secretaria(o) Académica: *</label>
                <input id="nombreSecretarioFirmante" type="text" formControlName="nombreSecretarioFirmante" class="form-control" />
              </div>
              <div class="form-col">
                <label for="documentoSecretarioFirmante">Documento Secretaria(o): *</label>
                <input id="documentoSecretarioFirmante" type="text" formControlName="documentoSecretarioFirmante" class="form-control" />
              </div>
            </div>

            <div class="form-actions">
              <button type="submit" [disabled]="formInstitucion.invalid || guardando()" class="btn-primary">
                @if (guardando()) {
                  <span>Guardando...</span>
                } @else {
                  <span>Actualizar Datos Institucionales</span>
                }
              </button>
            </div>
          </form>
        </div>
      }

      <!-- TAB 2: SIEE Y ESCALA -->
      @if (tabActiva() === 'siee') {
        <div class="tab-content">
          @if (sieeVigente()) {
            <div class="info-card">
              <div class="card-title-row">
                <h3>SIEE Vigente — Versión {{ sieeVigente()?.numeroVersion }} ({{ sieeVigente()?.nombreVersion }})</h3>
                <span class="badge badge-active">{{ sieeVigente()?.estado }}</span>
              </div>

              <div class="metrics-grid">
                <div class="metric-box">
                  <span class="metric-label">Escala Mínima</span>
                  <span class="metric-value">{{ sieeVigente()?.escalaMinima }}</span>
                </div>
                <div class="metric-box">
                  <span class="metric-label">Escala Máxima</span>
                  <span class="metric-value">{{ sieeVigente()?.escalaMaxima }}</span>
                </div>
                <div class="metric-box">
                  <span class="metric-label">Mínima Aprobatoria</span>
                  <span class="metric-value approved">{{ sieeVigente()?.escalaAprobatoria }}</span>
                </div>
                <div class="metric-box">
                  <span class="metric-label">Modo Redondeo</span>
                  <span class="metric-value">{{ sieeVigente()?.modoRedondeo }} ({{ sieeVigente()?.decimales }} dec)</span>
                </div>
              </div>

              <div class="legal-references">
                <p><strong>Acta Consejo Académico:</strong> {{ sieeVigente()?.actaConsejoAcademico || 'En acta institucional' }}</p>
                <p><strong>Acta Consejo Directivo:</strong> {{ sieeVigente()?.actaConsejoDirectivo || 'En acta institucional' }}</p>
                <p><strong>Término de Reclamaciones:</strong> {{ sieeVigente()?.plazoReclamacionesDias }} días hábiles escolares (UC-PROV-15)</p>
              </div>

              <!-- SIMULADOR EXPERIMENTAL DE NOTAS SIEE -->
              <div class="simulador-card">
                <h4>Simulador Experimental de Notas del SIEE</h4>
                <p class="simulador-desc">
                  Ingrese una calificación numérica para verificar el cálculo del motor, escala continua institucional y la equivalencia nacional (Decreto 1290).
                </p>

                <div class="simulador-row">
                  <input
                    type="number"
                    step="0.01"
                    [value]="notaSimulacion()"
                    (input)="onNotaSimulacionChange($event)"
                    placeholder="Ej. 3.45"
                    class="form-control sim-input"
                  />
                  <button type="button" class="btn-primary" (click)="ejecutarSimulacion()">
                    Simular Cálculo
                  </button>
                </div>

                @if (resultadoSimulacion()) {
                  <div class="resultado-simulacion">
                    <div class="res-item">
                      <span class="res-label">Nota Definitiva Redondeada:</span>
                      <strong class="res-val">{{ resultadoSimulacion()?.notaFinal }}</strong>
                    </div>
                    <div class="res-item">
                      <span class="res-label">Desempeño Nacional:</span>
                      <span class="badge" [ngClass]="'badge-' + resultadoSimulacion()?.desempenoNacional?.toLowerCase()">
                        {{ resultadoSimulacion()?.desempenoNacional }}
                      </span>
                    </div>
                    <div class="res-item">
                      <span class="res-label">Resultado:</span>
                      <strong [class.text-success]="resultadoSimulacion()?.aprobado" [class.text-danger]="!resultadoSimulacion()?.aprobado">
                        {{ resultadoSimulacion()?.aprobado ? 'APROBADO' : 'REPROBADO' }}
                      </strong>
                    </div>
                  </div>
                }
              </div>
            </div>
          } @else {
            <p>Cargando configuración del SIEE...</p>
          }
        </div>
      }

      <!-- TAB 3: MANUAL DE CONVIVENCIA -->
      @if (tabActiva() === 'manual') {
        <div class="tab-content">
          @if (manualVigente()) {
            <div class="info-card">
              <div class="card-title-row">
                <h3>Manual de Convivencia — Versión {{ manualVigente()?.numeroVersion }}</h3>
                <span class="badge badge-active">{{ manualVigente()?.estado }}</span>
              </div>

              <div class="legal-references">
                <p><strong>Año Lectivo Vigencia:</strong> {{ manualVigente()?.anioLectivoNombre || 'Vigente Institucional' }}</p>
                <p><strong>Acta Comité de Convivencia:</strong> {{ manualVigente()?.actaComiteConvivencia || 'Registrada' }}</p>
                <p><strong>Acta Consejo Directivo:</strong> {{ manualVigente()?.actaConsejoDirectivo || 'Aprobada en firme' }}</p>
                <p><strong>Fecha de Socialización:</strong> {{ manualVigente()?.fechaSocializacion || 'Socializado a la comunidad educativa' }}</p>
              </div>

              <div class="normativa-box">
                <h4>Garantías Constitucionales y Tipificación (Ley 1620 / Dec. 1965)</h4>
                <ul>
                  <li><strong>Situaciones Tipo I:</strong> Conflictos manejados por mediación escolar y acuerdos formativos inmediatos.</li>
                  <li><strong>Situaciones Tipo II:</strong> Agresiones escolares, ciberacoso o lesiones sin incapacidad permanente; activa Ruta de Atención y Comité.</li>
                  <li><strong>Situaciones Tipo III:</strong> Presuntos delitos / violencia sexual; remisión inmediata a Fiscalía/ICBF con <strong>prohibición legal de careo</strong>.</li>
                  <li><strong>Debido Proceso (Sentencia T-004 de 2026):</strong> Cómputo de descargos en días hábiles lectivos y activación obligatoria de Plan de Continuidad Académica ante suspensiones.</li>
                </ul>
              </div>
            </div>
          } @else {
            <p>Cargando Manual de Convivencia...</p>
          }
        </div>
      }
    </div>
  `,
  styles: [`
    .config-container {
      max-width: 960px;
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
    .form-card, .info-card {
      background: white;
      border: 1px solid #e2e8f0;
      border-radius: 8px;
      padding: 1.5rem;
      box-shadow: 0 1px 3px rgba(0,0,0,0.05);
    }
    h3 { margin-top: 0; color: #1e293b; font-size: 1.1rem; }
    .section-title { margin-top: 1.5rem; }
    .card-title-row {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 1rem;
      border-bottom: 1px solid #f1f5f9;
      padding-bottom: 0.75rem;
    }
    .form-row { display: flex; gap: 1rem; margin-bottom: 1rem; }
    .form-col { flex: 1; display: flex; flex-direction: column; }
    label { font-size: 0.85rem; font-weight: 500; margin-bottom: 0.25rem; color: #475569; }
    .form-control { padding: 0.55rem 0.75rem; border: 1px solid #cbd5e1; border-radius: 4px; font-size: 0.9rem; }
    .form-actions { display: flex; justify-content: flex-end; margin-top: 1rem; }
    .btn-primary { background: #2563eb; color: white; border: none; padding: 0.6rem 1.25rem; border-radius: 4px; font-weight: 600; font-size: 0.9rem; cursor: pointer; }
    .btn-primary:disabled { background: #93c5fd; cursor: not-allowed; }
    .badge { padding: 0.25rem 0.6rem; border-radius: 4px; font-size: 0.8rem; font-weight: 600; background: #e2e8f0; color: #475569; }
    .badge-active { background: #dcfce7; color: #166534; }
    .badge-superior { background: #dcfce7; color: #166534; }
    .badge-alto { background: #eff6ff; color: #1d4ed8; }
    .badge-basico { background: #fef9c3; color: #854d0e; }
    .badge-bajo { background: #fee2e2; color: #991b1b; }
    .metrics-grid {
      display: grid;
      grid-template-columns: repeat(4, 1fr);
      gap: 1rem;
      margin: 1.25rem 0;
    }
    .metric-box {
      background: #f8fafc;
      padding: 0.85rem;
      border-radius: 6px;
      border: 1px solid #e2e8f0;
      text-align: center;
    }
    .metric-label { display: block; font-size: 0.78rem; color: #64748b; font-weight: 600; text-transform: uppercase; margin-bottom: 0.25rem; }
    .metric-value { font-size: 1.3rem; font-weight: 700; color: #1e293b; }
    .metric-value.approved { color: #16a34a; }
    .legal-references { font-size: 0.88rem; color: #475569; line-height: 1.5; margin-bottom: 1.5rem; }
    .simulador-card {
      background: #eff6ff;
      border: 1px solid #bfdbfe;
      border-radius: 8px;
      padding: 1.25rem;
    }
    .simulador-card h4 { margin: 0 0 0.5rem; color: #1e40af; }
    .simulador-desc { font-size: 0.85rem; color: #3b82f6; margin-bottom: 1rem; }
    .simulador-row { display: flex; gap: 0.75rem; align-items: center; max-width: 320px; }
    .sim-input { font-size: 1.1rem; font-weight: 700; text-align: center; }
    .resultado-simulacion {
      margin-top: 1rem;
      background: white;
      padding: 0.85rem 1rem;
      border-radius: 6px;
      display: flex;
      justify-content: space-around;
      align-items: center;
      border: 1px solid #bfdbfe;
    }
    .res-item { display: flex; flex-direction: column; align-items: center; gap: 0.25rem; font-size: 0.85rem; }
    .res-label { color: #64748b; }
    .res-val { font-size: 1.2rem; color: #1e293b; }
    .text-success { color: #16a34a; }
    .text-danger { color: #dc2626; }
    .normativa-box {
      background: #f8fafc;
      padding: 1rem;
      border-radius: 6px;
      border: 1px solid #e2e8f0;
      margin-top: 1rem;
    }
    .normativa-box h4 { margin-top: 0; color: #334155; }
    .normativa-box ul { margin: 0; padding-left: 1.25rem; font-size: 0.85rem; color: #475569; line-height: 1.5; }
  `]
})
export class ConfiguracionInstitucionalComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly configService = inject(ConfiguracinInstitucionalService);
  private readonly notificationService = inject(NotificationService);

  readonly tabActiva = signal<'institucion' | 'siee' | 'manual'>('institucion');
  readonly guardando = signal(false);

  readonly institucion = signal<InstitucionDatosLegalesResponse | null>(null);
  readonly sieeVigente = signal<ConfiguracionSieeResponse | null>(null);
  readonly manualVigente = signal<ConfiguracionManualConvivenciaResponse | null>(null);

  readonly notaSimulacion = signal<number>(3.5);
  readonly resultadoSimulacion = signal<SimulacionSieeResponse | null>(null);

  readonly formInstitucion = this.fb.group({
    nombreOficial: ['', [Validators.required]],
    codigoDane: ['', [Validators.required]],
    nit: ['', [Validators.required]],
    resolucionAprobacion: ['', [Validators.required]],
    municipio: [''],
    nombreRectorFirmante: ['', [Validators.required]],
    documentoRectorFirmante: ['', [Validators.required]],
    nombreSecretarioFirmante: ['', [Validators.required]],
    documentoSecretarioFirmante: ['', [Validators.required]]
  });

  ngOnInit(): void {
    this.cargarDatosInstitucion();
    this.cargarSieeVigente();
    this.cargarManualVigente();
  }

  cargarDatosInstitucion(): void {
    this.configService.apiConfiguracionInstitucionGet().subscribe({
      next: (data) => {
        this.institucion.set(data);
        this.formInstitucion.patchValue({
          nombreOficial: data.nombreOficial || '',
          codigoDane: data.codigoDane || '',
          nit: data.nit || '',
          resolucionAprobacion: data.resolucionAprobacion || '',
          municipio: data.municipio || '',
          nombreRectorFirmante: data.nombreRectorFirmante || '',
          documentoRectorFirmante: data.documentoRectorFirmante || '',
          nombreSecretarioFirmante: data.nombreSecretarioFirmante || '',
          documentoSecretarioFirmante: data.documentoSecretarioFirmante || ''
        });
      }
    });
  }

  cargarSieeVigente(): void {
    this.configService.apiConfiguracionSieeVigenteGet().subscribe({
      next: (data) => {
        this.sieeVigente.set(data);
      }
    });
  }

  cargarManualVigente(): void {
    this.configService.apiConfiguracionManualConvivenciaVigenteGet().subscribe({
      next: (data) => {
        this.manualVigente.set(data);
      }
    });
  }

  guardarDatosInstitucion(): void {
    if (this.formInstitucion.invalid) return;

    this.guardando.set(true);
    this.configService.apiConfiguracionInstitucionPut(this.formInstitucion.getRawValue() as any).subscribe({
      next: (act) => {
        this.guardando.set(false);
        this.institucion.set(act);
        this.notificationService.showSuccess('Datos institucionales actualizados correctamente.');
      },
      error: () => {
        this.guardando.set(false);
      }
    });
  }

  onNotaSimulacionChange(event: Event): void {
    const val = Number((event.target as HTMLInputElement).value);
    this.notaSimulacion.set(val);
  }

  ejecutarSimulacion(): void {
    const siee = this.sieeVigente();
    if (!siee || !siee.id) return;

    const nota = this.notaSimulacion();
    const req = {
      calificacionesActividades: [nota]
    };

    this.configService.apiConfiguracionSieeIdSimularPost(siee.id, req as any).subscribe({
      next: (res) => {
        this.resultadoSimulacion.set(res);
      }
    });
  }
}
