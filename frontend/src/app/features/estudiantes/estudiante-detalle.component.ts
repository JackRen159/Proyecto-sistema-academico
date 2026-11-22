import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { EstudiantesService } from '../../core/api/generated/api/estudiantes.service';
import { VinculacionesService } from '../../core/api/generated/api/vinculaciones.service';
import { NotificationService } from '../../core/services/notification.service';
import { FichaEscolarEstudianteResponse } from '../../core/api/generated/model/ficha-escolar-estudiante-response';
import { EstudianteAcudienteResponse } from '../../core/api/generated/model/estudiante-acudiente-response';

@Component({
  selector: 'app-estudiante-detalle',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  template: `
    <div class="detalle-container">
      @if (cargando()) {
        <div class="state-card">
          <p>Cargando información del estudiante y expediente...</p>
        </div>
      } @else if (error()) {
        <div class="state-card alert-danger">
          <p>{{ error() }}</p>
          <a routerLink="/estudiantes" class="btn-secondary">Volver al Listado</a>
        </div>
      } @else if (ficha()) {
        <div class="header-section">
          <div>
            <div class="title-row">
              <h2>{{ ficha()?.nombreCompleto }}</h2>
              <span class="badge" [class.badge-active]="ficha()?.estadoMatricula === 'MATRICULADO_ACTIVO'">
                {{ ficha()?.estadoMatricula || 'REGISTRADO' }}
              </span>
            </div>
            <p class="subtitle">
              Documento: <strong>{{ ficha()?.tipoDocumento }} {{ ficha()?.documento }}</strong> |
              Curso: <strong>{{ ficha()?.cursoActual || 'Sin asignar' }}</strong> |
              Edad: <strong>{{ ficha()?.edadCalculada || '-' }} años</strong>
            </p>
          </div>
          <div class="actions">
            <a routerLink="/estudiantes" class="btn-secondary">← Volver al Listado</a>
          </div>
        </div>

        <div class="cards-grid">
          <!-- TARJETA 1: FICHA ESCOLAR Y SALUD -->
          <div class="info-card">
            <div class="card-title">Ficha Escolar y Datos Clínicos</div>
            <div class="info-list">
              <div class="info-item">
                <span class="label">Fecha de Nacimiento:</span>
                <span class="value">{{ ficha()?.fechaNacimiento || '-' }}</span>
              </div>
              <div class="info-item">
                <span class="label">Dirección:</span>
                <span class="value">{{ ficha()?.direccion || '-' }}</span>
              </div>
              <div class="info-item">
                <span class="label">Teléfono:</span>
                <span class="value">{{ ficha()?.telefono || '-' }}</span>
              </div>
              <div class="info-item">
                <span class="label">EPS:</span>
                <span class="value">{{ ficha()?.eps || '-' }}</span>
              </div>
              <div class="info-item">
                <span class="label">Grupo Sanguíneo y RH:</span>
                <span class="value">{{ ficha()?.grupoSanguineoRh || '-' }}</span>
              </div>
              <div class="info-item">
                <span class="label">Ajustes PIAR (Decreto 1421):</span>
                <span class="value">
                  @if (ficha()?.tieneDiscapacidadPiar) {
                    <strong class="text-piar">Sí (Ajustes Razonables Activos)</strong>
                  } @else {
                    No requerido
                  }
                </span>
              </div>
              @if (ficha()?.detallesSaludEmergencia) {
                <div class="info-item full-width">
                  <span class="label">Observaciones Médicas:</span>
                  <span class="value highlight-box">{{ ficha()?.detallesSaludEmergencia }}</span>
                </div>
              }
            </div>
          </div>

          <!-- TARJETA 2: GESTIÓN DE ACUDIENTES Y GUARDIAS (UC-PROV-05) -->
          <div class="info-card">
            <div class="card-title-row">
              <span class="card-title">Acudientes y Representantes (UC-PROV-05)</span>
            </div>

            @if (vinculaciones().length === 0) {
              <p class="empty-text">No hay acudientes registrados para este estudiante.</p>
            } @else {
              <div class="acudientes-list">
                @for (v of vinculaciones(); track v.id) {
                  <div class="acudiente-item" [class.principal-border]="v.esPrincipal">
                    <div class="acudiente-info">
                      <div class="acudiente-header">
                        <strong>{{ v.acudienteNombres }} {{ v.acudienteApellidos }}</strong>
                        <span class="parentesco-chip">{{ v.parentesco }}</span>
                        @if (v.esPrincipal) {
                          <span class="badge-principal">Principal</span>
                        }
                      </div>
                      <div class="acudiente-meta">
                        <span>Doc: {{ v.acudienteDocumento }}</span> |
                        <span>Tel: {{ v.acudienteTelefono || '-' }}</span> |
                        <span>Retiro portería: <strong>{{ v.autorizadoRetiro ? 'Autorizado' : 'No' }}</strong></span>
                      </div>
                    </div>

                    <div class="acudiente-actions">
                      @if (!v.esPrincipal) {
                        <button
                          type="button"
                          class="btn-action btn-sm"
                          (click)="establecerComoPrincipal(v)"
                          title="Designar como acudiente principal único (RN-EST-05)"
                        >
                          Hacer Principal
                        </button>
                      }

                      <button
                        type="button"
                        class="btn-danger-action btn-sm"
                        (click)="abrirModalRestriccion(v)"
                        title="Registrar medida cautelar judicial o restricción legal (UC-PROV-05 A3)"
                      >
                        Restricción Legal
                      </button>
                    </div>
                  </div>
                }
              </div>
            }
          </div>
        </div>

        <!-- MODAL DE RESTRICCIÓN LEGAL JUDICIAL (UC-PROV-05 A3) -->
        @if (vinculacionSeleccionadaParaRestriccion()) {
          <div class="modal-backdrop">
            <div class="modal-card">
              <h3>Registro de Medida Cautelar de Alejamiento / Restricción Legal</h3>
              <p class="modal-subtitle">
                Acudiente: <strong>{{ vinculacionSeleccionadaParaRestriccion()?.acudienteNombres }} {{ vinculacionSeleccionadaParaRestriccion()?.acudienteApellidos }}</strong>
              </p>

              <div class="alert alert-warning">
                <strong>ATENCIÓN LEGAL:</strong> Esta acción bloqueará de inmediato el acceso a notas, asistencia y retiros
                en portería para este adulto, revocando forzosamente cualquier sesión activa en el servidor.
              </div>

              <form [formGroup]="restriccionForm" (ngSubmit)="confirmarRestriccionLegal()">
                <div class="form-group">
                  <label for="motivo">Motivo o Providencia Judicial: *</label>
                  <textarea
                    id="motivo"
                    formControlName="motivoBloqueoLegal"
                    rows="3"
                    class="form-control"
                    placeholder="Ej. Oficio Juzgado de Familia No. 452 - Medida de protección y alejamiento"
                  ></textarea>
                </div>

                <div class="form-group">
                  <label for="soporte">URL del Documento Soporte (Opcional):</label>
                  <input
                    id="soporte"
                    type="text"
                    formControlName="soporteMedidaUrl"
                    placeholder="https://..."
                    class="form-control"
                  />
                </div>

                <div class="modal-footer">
                  <button type="button" class="btn-secondary" (click)="cerrarModalRestriccion()">Cancelar</button>
                  <button type="submit" [disabled]="restriccionForm.invalid || procesandoRestriccion()" class="btn-danger">
                    @if (procesandoRestriccion()) {
                      <span>Aplicando Bloqueo...</span>
                    } @else {
                      <span>Aplicar Bloqueo Judicial</span>
                    }
                  </button>
                </div>
              </form>
            </div>
          </div>
        }
      }
    </div>
  `,
  styles: [`
    .detalle-container {
      max-width: 1000px;
      margin: 0 auto;
      font-family: system-ui, -apple-system, sans-serif;
    }
    .header-section {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 1.5rem;
      border-bottom: 1px solid #e2e8f0;
      padding-bottom: 1rem;
    }
    .title-row {
      display: flex;
      align-items: center;
      gap: 0.75rem;
    }
    h2 { margin: 0; color: #1e293b; font-size: 1.4rem; }
    .subtitle { margin: 0.35rem 0 0; color: #64748b; font-size: 0.88rem; }
    .badge {
      background: #e2e8f0;
      color: #475569;
      font-size: 0.75rem;
      padding: 0.2rem 0.5rem;
      border-radius: 4px;
      font-weight: 600;
    }
    .badge-active { background: #dcfce7; color: #166534; }
    .btn-secondary {
      padding: 0.45rem 0.85rem;
      background: #e2e8f0;
      color: #334155;
      text-decoration: none;
      border-radius: 4px;
      font-size: 0.85rem;
      font-weight: 500;
      cursor: pointer;
      border: none;
    }
    .btn-secondary:hover { background: #cbd5e1; }
    .cards-grid {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 1.5rem;
    }
    .info-card {
      background: white;
      border: 1px solid #e2e8f0;
      border-radius: 8px;
      padding: 1.25rem;
      box-shadow: 0 1px 3px rgba(0,0,0,0.05);
    }
    .card-title {
      font-weight: 600;
      color: #1e293b;
      font-size: 1rem;
      margin-bottom: 1rem;
      border-bottom: 1px solid #f1f5f9;
      padding-bottom: 0.5rem;
    }
    .info-list {
      display: flex;
      flex-direction: column;
      gap: 0.65rem;
    }
    .info-item {
      display: flex;
      justify-content: space-between;
      font-size: 0.88rem;
    }
    .info-item.full-width {
      flex-direction: column;
      gap: 0.25rem;
    }
    .label { color: #64748b; }
    .value { font-weight: 500; color: #1e293b; }
    .text-piar { color: #2563eb; font-weight: 600; }
    .highlight-box {
      background: #f8fafc;
      padding: 0.5rem;
      border-radius: 4px;
      border-left: 3px solid #3b82f6;
      font-size: 0.85rem;
    }

    /* ACUDIENTES */
    .acudientes-list {
      display: flex;
      flex-direction: column;
      gap: 0.75rem;
    }
    .acudiente-item {
      border: 1px solid #e2e8f0;
      border-radius: 6px;
      padding: 0.85rem;
      display: flex;
      justify-content: space-between;
      align-items: center;
      background: #fafafa;
    }
    .principal-border {
      border-color: #86efac;
      background: #f0fdf4;
    }
    .acudiente-header {
      display: flex;
      align-items: center;
      gap: 0.5rem;
      margin-bottom: 0.25rem;
    }
    .parentesco-chip {
      font-size: 0.75rem;
      background: #e2e8f0;
      color: #475569;
      padding: 0.15rem 0.4rem;
      border-radius: 4px;
    }
    .badge-principal {
      font-size: 0.75rem;
      background: #22c55e;
      color: white;
      font-weight: 600;
      padding: 0.15rem 0.45rem;
      border-radius: 4px;
    }
    .acudiente-meta {
      font-size: 0.8rem;
      color: #64748b;
    }
    .acudiente-actions {
      display: flex;
      gap: 0.4rem;
    }
    .btn-sm {
      padding: 0.35rem 0.6rem;
      font-size: 0.78rem;
      border-radius: 4px;
      cursor: pointer;
      font-weight: 500;
    }
    .btn-action {
      background: #eff6ff;
      color: #2563eb;
      border: 1px solid #bfdbfe;
    }
    .btn-action:hover { background: #dbeafe; }
    .btn-danger-action {
      background: #fef2f2;
      color: #dc2626;
      border: 1px solid #fecaca;
    }
    .btn-danger-action:hover { background: #fee2e2; }

    /* MODAL */
    .modal-backdrop {
      position: fixed;
      top: 0; left: 0; width: 100vw; height: 100vh;
      background: rgba(0,0,0,0.5);
      display: flex;
      justify-content: center;
      align-items: center;
      z-index: 1000;
    }
    .modal-card {
      background: white;
      padding: 1.75rem;
      border-radius: 8px;
      max-width: 500px;
      width: 90%;
      box-shadow: 0 10px 25px rgba(0,0,0,0.2);
    }
    .modal-card h3 { margin-top: 0; color: #991b1b; font-size: 1.15rem; }
    .modal-subtitle { font-size: 0.9rem; color: #475569; margin-bottom: 1rem; }
    .alert { padding: 0.75rem; border-radius: 6px; font-size: 0.82rem; margin-bottom: 1rem; line-height: 1.4; }
    .alert-warning { background: #fffbeb; color: #92400e; border: 1px solid #fde68a; }
    .form-group { margin-bottom: 0.85rem; }
    label { display: block; font-size: 0.85rem; font-weight: 500; margin-bottom: 0.25rem; }
    .form-control { width: 100%; box-sizing: border-box; padding: 0.5rem; border: 1px solid #cbd5e1; border-radius: 4px; }
    .modal-footer { display: flex; justify-content: flex-end; gap: 0.75rem; margin-top: 1.25rem; }
    .btn-danger {
      background: #dc2626;
      color: white;
      border: none;
      padding: 0.5rem 1rem;
      border-radius: 4px;
      font-weight: 600;
      cursor: pointer;
    }
    .btn-danger:disabled { background: #fca5a5; cursor: not-allowed; }
  `]
})
export class EstudianteDetalleComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly fb = inject(FormBuilder);
  private readonly estudiantesService = inject(EstudiantesService);
  private readonly vinculacionesService = inject(VinculacionesService);
  private readonly notificationService = inject(NotificationService);

  readonly cargando = signal(true);
  readonly error = signal<string | null>(null);
  readonly ficha = signal<FichaEscolarEstudianteResponse | null>(null);
  readonly vinculaciones = signal<EstudianteAcudienteResponse[]>([]);

  readonly vinculacionSeleccionadaParaRestriccion = signal<EstudianteAcudienteResponse | null>(null);
  readonly procesandoRestriccion = signal(false);

  readonly restriccionForm = this.fb.group({
    motivoBloqueoLegal: ['', [Validators.required]],
    soporteMedidaUrl: ['']
  });

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.cargarDetalle(Number(idParam));
    } else {
      this.error.set('Identificador de estudiante inválido.');
      this.cargando.set(false);
    }
  }

  cargarDetalle(id: number): void {
    this.cargando.set(true);
    this.error.set(null);

    this.estudiantesService.apiEstudiantesIdFichaEscolarGet(id).subscribe({
      next: (f) => {
        this.ficha.set(f);
        this.cargando.set(false);

        if (f.documento) {
          this.cargarVinculaciones(f.documento);
        }
      },
      error: () => {
        this.cargando.set(false);
        this.error.set('No fue posible cargar el expediente del estudiante.');
      }
    });
  }

  cargarVinculaciones(documentoEstudiante: string): void {
    this.vinculacionesService.apiVinculacionesEstudianteDocumentoEstudianteGet(documentoEstudiante).subscribe({
      next: (vinculos) => {
        this.vinculaciones.set(vinculos);
      }
    });
  }

  establecerComoPrincipal(v: EstudianteAcudienteResponse): void {
    const docEstudiante = this.ficha()?.documento;
    const docNuevoPrincipal = v.acudienteDocumento;

    if (!docEstudiante || !docNuevoPrincipal) return;

    this.vinculacionesService
      .apiVinculacionesEstudianteDocumentoEstudiantePrincipalDocumentoNuevoPrincipalPatch(
        docEstudiante,
        docNuevoPrincipal
      )
      .subscribe({
        next: () => {
          this.notificationService.showSuccess(
            `${v.acudienteNombres} ${v.acudienteApellidos} ha sido establecido como Acudiente Principal.`,
            'Acudiente Principal Actualizado'
          );
          this.cargarVinculaciones(docEstudiante);
        }
      });
  }

  abrirModalRestriccion(v: EstudianteAcudienteResponse): void {
    this.vinculacionSeleccionadaParaRestriccion.set(v);
    this.restriccionForm.reset();
  }

  cerrarModalRestriccion(): void {
    this.vinculacionSeleccionadaParaRestriccion.set(null);
  }

  confirmarRestriccionLegal(): void {
    const v = this.vinculacionSeleccionadaParaRestriccion();
    if (!v || !v.id || this.restriccionForm.invalid) return;

    this.procesandoRestriccion.set(true);
    const req = {
      motivoBloqueoLegal: this.restriccionForm.get('motivoBloqueoLegal')?.value!,
      soporteMedidaUrl: this.restriccionForm.get('soporteMedidaUrl')?.value || undefined
    };

    this.vinculacionesService.apiVinculacionesIdRestriccionLegalPost(v.id, req).subscribe({
      next: () => {
        this.procesandoRestriccion.set(false);
        this.cerrarModalRestriccion();
        this.notificationService.showWarning(
          `Medida de restricción legal aplicada a ${v.acudienteNombres} ${v.acudienteApellidos}. Sesiones y accesos bloqueados.`,
          'Restricción Legal Activa'
        );
        if (this.ficha()?.documento) {
          this.cargarVinculaciones(this.ficha()!.documento!);
        }
      },
      error: () => {
        this.procesandoRestriccion.set(false);
      }
    });
  }
}
