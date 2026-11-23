import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatrculasService } from '../../core/api/generated/api/matrculas.service';
import { MatrculasNovedadesDePermanenciaService } from '../../core/api/generated/api/matrculas-novedades-de-permanencia.service';
import { EstructuraAcadmicaAosLectivosService } from '../../core/api/generated/api/estructura-acadmica-aos-lectivos.service';
import { EstructuraAcadmicaGruposService } from '../../core/api/generated/api/estructura-acadmica-grupos.service';
import { EstudiantesService } from '../../core/api/generated/api/estudiantes.service';
import { NotificationService } from '../../core/services/notification.service';
import { MatriculaResponse } from '../../core/api/generated/model/matricula-response';
import { AnioLectivoResponse } from '../../core/api/generated/model/anio-lectivo-response';
import { GrupoResponse } from '../../core/api/generated/model/grupo-response';
import { EstudianteResponse } from '../../core/api/generated/model/estudiante-response';
import { TipoMatricula } from '../../core/api/generated/model/tipo-matricula';
import { TipoNovedadMatricula } from '../../core/api/generated/model/tipo-novedad-matricula';
import { EstadoAnioLectivo } from '../../core/api/generated/model/estado-anio-lectivo';

@Component({
  selector: 'app-matriculas',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <div class="matriculas-container">
      <header class="header">
        <div>
          <h2>Gestión de Matrículas y Novedades de Permanencia</h2>
          <p class="subtitle">Formalización de matrícula y registro de novedades escolares (UC-13, UC-PROV-09..10)</p>
        </div>
      </header>

      <!-- TABS -->
      <div class="tabs-bar">
        <button
          type="button"
          class="tab-btn"
          [class.active]="tabActiva() === 'listado'"
          (click)="tabActiva.set('listado')"
        >
          📋 Estudiantes Matriculados
        </button>
        <button
          type="button"
          class="tab-btn"
          [class.active]="tabActiva() === 'formalizar'"
          (click)="tabActiva.set('formalizar')"
        >
          ✍️ Formalizar Matrícula
        </button>
      </div>

      <!-- TAB 1: LISTADO DE MATRICULADOS -->
      @if (tabActiva() === 'listado') {
        <div class="tab-content">
          <div class="content-toolbar">
            <div class="selector-row">
              <label for="selectAnioMat">Año Lectivo:</label>
              <select
                id="selectAnioMat"
                class="form-control inline-select"
                [value]="anioSeleccionadoId()"
                (change)="onAnioChange($event)"
              >
                @for (a of anios(); track a.id) {
                  <option [value]="a.id">Año {{ a.anio }} ({{ a.estado }})</option>
                }
              </select>

              <label for="selectGrupoMat">Grupo:</label>
              <select
                id="selectGrupoMat"
                class="form-control inline-select"
                [value]="grupoSeleccionadoId()"
                (change)="onGrupoChange($event)"
              >
                <option [value]="null">Todos los Grupos</option>
                @for (g of grupos(); track g.id) {
                  <option [value]="g.id">{{ g.nombre }} (Capacidad: {{ g.cupoMaximo }})</option>
                }
              </select>
            </div>
          </div>

          <div class="table-card">
            <table>
              <thead>
                <tr>
                  <th>N° Matrícula</th>
                  <th>Estudiante</th>
                  <th>Grupo</th>
                  <th>Tipo</th>
                  <th>Fecha</th>
                  <th>Estado</th>
                  <th>Acciones</th>
                </tr>
              </thead>
              <tbody>
                @if (matriculas().length === 0) {
                  <tr>
                    <td colspan="7" class="text-center">No se encontraron matrículas para los filtros seleccionados.</td>
                  </tr>
                }
                @for (m of matriculas(); track m.id) {
                  <tr>
                    <td><code>{{ m.numeroMatricula || ('MAT-' + m.id) }}</code></td>
                    <td><strong>{{ m.estudianteNombreCompleto || m.estudianteDocumento }}</strong></td>
                    <td><span class="chip-grupo">{{ m.grupoNombre }}</span></td>
                    <td>{{ m.tipoMatricula }}</td>
                    <td>{{ m.fechaMatricula }}</td>
                    <td>
                      <span class="badge" [class.badge-active]="m.estado === 'MATRICULADO_ACTIVO'">
                        {{ m.estado }}
                      </span>
                    </td>
                    <td>
                      @if (m.estado === 'MATRICULADO_ACTIVO') {
                        <button
                          type="button"
                          class="btn-sm btn-action"
                          (click)="abrirModalNovedad(m)"
                        >
                          Novedad
                        </button>
                      }
                    </td>
                  </tr>
                }
              </tbody>
            </table>
          </div>
        </div>
      }

      <!-- TAB 2: FORMALIZAR MATRÍCULA -->
      @if (tabActiva() === 'formalizar') {
        <div class="tab-content">
          <form [formGroup]="formMatricula" (ngSubmit)="formalizarMatricula()" class="form-card">
            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1rem;">
              <h3 style="margin: 0;">Formalización de Matrícula (UC-13, RN-MAT-01, RN-MAT-05)</h3>
              <button type="button" class="btn-demo-simular" (click)="seleccionarSofiaListo()">
                ⚡ Seleccionar Estudiante Lista (Sofía Herrera)
              </button>
            </div>

            <div class="form-row">
              <div class="form-col">
                <label for="fEstudianteId">Estudiante: *</label>
                <select id="fEstudianteId" formControlName="estudianteId" class="form-control">
                  <option [ngValue]="null">-- Seleccione Estudiante --</option>
                  @for (e of estudiantes(); track e.id) {
                    <option [ngValue]="e.id">{{ e.nombres }} {{ e.apellidos }} (Doc: {{ e.documento }})</option>
                  }
                </select>
                @if (formMatricula.get('estudianteId')?.touched && formMatricula.get('estudianteId')?.invalid) {
                  <small class="text-danger">Selección de estudiante requerida.</small>
                }
              </div>
              <div class="form-col">
                <label for="fGrupoId">Grupo Académico a Asignar: *</label>
                <select id="fGrupoId" formControlName="grupoId" class="form-control">
                  @for (g of grupos(); track g.id) {
                    <option [value]="g.id">{{ g.nombre }} (Cupo máx: {{ g.cupoMaximo }})</option>
                  }
                </select>
              </div>
              <div class="form-col">
                <label for="fTipoMatricula">Tipo de Matrícula: *</label>
                <select id="fTipoMatricula" formControlName="tipoMatricula" class="form-control">
                  <option [value]="TipoMatricula.Nuevo">Nuevo</option>
                  <option [value]="TipoMatricula.RenovacionAntiguo">Renovación Antiguo</option>
                  <option [value]="TipoMatricula.TrasladoEntrante">Traslado Entrante</option>
                </select>
              </div>
            </div>

            <div class="form-row">
              <div class="form-col">
                <label for="fFechaMatricula">Fecha de Matrícula: *</label>
                <input id="fFechaMatricula" type="date" formControlName="fechaMatricula" class="form-control" />
              </div>
              <div class="form-col">
                <label for="fFolio">Folio / Libro de Matrículas:</label>
                <input id="fFolio" type="text" formControlName="folio" placeholder="Ej. Libro 2026 Folio 15" class="form-control" />
              </div>
            </div>

            <div class="info-box">
              <p><strong>Aceptación Digital de Reglamentos Institucionales (PEI, SIEE y Manual):</strong></p>
              <div class="checkbox-inline">
                <input id="fAceptaReglamentos" type="checkbox" formControlName="aceptaReglamentos" />
                <label for="fAceptaReglamentos">
                  El acudiente y estudiante aceptan expresamente los reglamentos institucionales (PEI, SIEE aprobado y Manual de Convivencia vigente)
                </label>
              </div>
            </div>

            <div class="form-actions">
              <button type="submit" [disabled]="formMatricula.invalid || guardando()" class="btn-primary">
                @if (guardando()) {
                  <span>Formalizando Matrícula...</span>
                } @else {
                  <span>Formalizar Matrícula</span>
                }
              </button>
            </div>
          </form>
        </div>
      }

      <!-- MODAL DE NOVEDADES DE PERMANENCIA (UC-PROV-09) -->
      @if (matriculaSeleccionada()) {
        <div class="modal-backdrop">
          <div class="modal-card">
            <h3>Registro de Novedad de Permanencia Escolar</h3>
            <p class="modal-subtitle">
              Estudiante: <strong>{{ matriculaSeleccionada()?.estudianteNombreCompleto }}</strong> |
              Matrícula: <strong>{{ matriculaSeleccionada()?.numeroMatricula }}</strong>
            </p>

            <form [formGroup]="formNovedad" (ngSubmit)="aplicarNovedad()">
              <div class="form-group">
                <label for="tipoNovedad">Tipo de Novedad: *</label>
                <select id="tipoNovedad" formControlName="tipoNovedad" class="form-control">
                  <option [value]="TipoNovedadMatricula.RetiroVoluntario">Retiro Voluntario</option>
                  <option [value]="TipoNovedadMatricula.TrasladadoSaliente">Traslado Saliente</option>
                  <option [value]="TipoNovedadMatricula.PromocionAnticipada">Promoción Anticipada de Grado</option>
                  <option [value]="TipoNovedadMatricula.Desercion">Deserción Escolar</option>
                </select>
              </div>

              <div class="form-group">
                <label for="motivoNovedad">Motivo / Justificación Obligatoria: *</label>
                <textarea
                  id="motivoNovedad"
                  formControlName="motivo"
                  rows="3"
                  class="form-control"
                  placeholder="Justifique la novedad conforme a la normatividad institucional..."
                ></textarea>
              </div>

              <div class="form-group">
                <label for="numeroActa">Acta o Resolución Rectoral Soporte:</label>
                <input id="numeroActa" type="text" formControlName="numeroActaResolucion" placeholder="Ej. Resolución Rectoral 012" class="form-control" />
              </div>

              <div class="modal-footer">
                <button type="button" class="btn-secondary" (click)="cerrarModalNovedad()">Cancelar</button>
                <button type="submit" [disabled]="formNovedad.invalid || guardandoNovedad()" class="btn-danger">
                  @if (guardandoNovedad()) {
                    <span>Registrando...</span>
                  } @else {
                    <span>Registrar Novedad</span>
                  }
                </button>
              </div>
            </form>
          </div>
        </div>
      }
    </div>
  `,
  styles: [`
    .matriculas-container {
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
    .content-toolbar {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 1rem;
    }
    .selector-row { display: flex; align-items: center; gap: 0.75rem; }
    .selector-row label { font-size: 0.88rem; font-weight: 500; color: #475569; }
    .inline-select { min-width: 170px; }
    .form-card {
      background: white;
      border: 1px solid #e2e8f0;
      border-radius: 8px;
      padding: 1.5rem;
      box-shadow: 0 1px 3px rgba(0,0,0,0.05);
    }
    h3 { margin-top: 0; color: #1e293b; font-size: 1.1rem; }
    .form-row { display: flex; gap: 1rem; margin-bottom: 1rem; }
    .form-col { flex: 1; display: flex; flex-direction: column; }
    label { font-size: 0.85rem; font-weight: 500; margin-bottom: 0.25rem; color: #475569; }
    .form-control { padding: 0.55rem 0.75rem; border: 1px solid #cbd5e1; border-radius: 4px; font-size: 0.9rem; }
    .info-box {
      background: #f8fafc;
      padding: 0.85rem;
      border-radius: 6px;
      border: 1px solid #e2e8f0;
      margin: 1.25rem 0;
    }
    .info-box p { margin: 0 0 0.5rem; font-size: 0.85rem; }
    .checkbox-inline { display: flex; align-items: center; gap: 0.5rem; font-size: 0.85rem; }
    .form-actions { display: flex; justify-content: flex-end; margin-top: 1.25rem; }
    .table-card {
      background: white;
      border: 1px solid #e2e8f0;
      border-radius: 8px;
      overflow: hidden;
      box-shadow: 0 1px 3px rgba(0,0,0,0.05);
    }
    table { width: 100%; border-collapse: collapse; text-align: left; font-size: 0.9rem; }
    th { background: #f8fafc; color: #475569; font-weight: 600; padding: 0.75rem 1rem; border-bottom: 1px solid #e2e8f0; }
    td { padding: 0.75rem 1rem; border-bottom: 1px solid #f1f5f9; color: #1e293b; }
    .text-center { text-align: center; color: #64748b; padding: 2rem !important; }
    .chip-grupo { background: #eff6ff; color: #1d4ed8; font-weight: 600; padding: 0.2rem 0.5rem; border-radius: 4px; font-size: 0.8rem; }
    .badge { padding: 0.2rem 0.5rem; border-radius: 4px; font-size: 0.75rem; font-weight: 600; background: #e2e8f0; color: #475569; }
    .badge-active { background: #dcfce7; color: #166534; }
    .btn-primary { background: #2563eb; color: white; border: none; padding: 0.6rem 1.25rem; border-radius: 4px; font-weight: 600; font-size: 0.9rem; cursor: pointer; }
    .btn-secondary { background: #e2e8f0; color: #334155; border: none; padding: 0.5rem 0.85rem; border-radius: 4px; font-size: 0.85rem; cursor: pointer; }
    .btn-danger { background: #dc2626; color: white; border: none; padding: 0.5rem 1rem; border-radius: 4px; font-weight: 600; cursor: pointer; }
    .btn-sm { padding: 0.25rem 0.5rem; font-size: 0.78rem; border-radius: 4px; border: none; cursor: pointer; font-weight: 500; }
    .btn-action { background: #eff6ff; color: #2563eb; border: 1px solid #bfdbfe; }
    .btn-demo-simular {
      background: #eff6ff;
      border: 1px solid #bfdbfe;
      padding: 0.45rem 0.85rem;
      border-radius: 4px;
      font-size: 0.82rem;
      font-weight: 600;
      color: #1d4ed8;
      cursor: pointer;
    }
    .btn-demo-simular:hover { background: #dbeafe; }
    .text-danger { color: #dc2626; font-size: 0.8rem; margin-top: 0.25rem; }

    /* MODAL */
    .modal-backdrop {
      position: fixed;
      top: 0; left: 0; width: 100vw; height: 100vh;
      background: rgba(0,0,0,0.5);
      display: flex; justify-content: center; align-items: center;
      z-index: 1000;
    }
    .modal-card {
      background: white; padding: 1.75rem; border-radius: 8px; max-width: 480px; width: 90%;
      box-shadow: 0 10px 25px rgba(0,0,0,0.2);
    }
    .modal-card h3 { margin-top: 0; color: #1e293b; font-size: 1.15rem; }
    .modal-subtitle { font-size: 0.88rem; color: #475569; margin-bottom: 1rem; }
    .form-group { margin-bottom: 1rem; }
    .modal-footer { display: flex; justify-content: flex-end; gap: 0.75rem; margin-top: 1.25rem; }
  `]
})
export class MatriculasComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly matriculasService = inject(MatrculasService);
  private readonly novedadesService = inject(MatrculasNovedadesDePermanenciaService);
  private readonly aniosService = inject(EstructuraAcadmicaAosLectivosService);
  private readonly gruposService = inject(EstructuraAcadmicaGruposService);
  private readonly estudiantesService = inject(EstudiantesService);
  private readonly notificationService = inject(NotificationService);

  readonly tabActiva = signal<'listado' | 'formalizar'>('listado');
  readonly guardando = signal(false);
  readonly guardandoNovedad = signal(false);

  readonly anios = signal<AnioLectivoResponse[]>([]);
  readonly grupos = signal<GrupoResponse[]>([]);
  readonly matriculas = signal<MatriculaResponse[]>([]);
  readonly estudiantes = signal<EstudianteResponse[]>([]);

  readonly anioSeleccionadoId = signal<number | null>(null);
  readonly grupoSeleccionadoId = signal<number | null>(null);

  readonly matriculaSeleccionada = signal<MatriculaResponse | null>(null);

  readonly TipoMatricula = TipoMatricula;
  readonly TipoNovedadMatricula = TipoNovedadMatricula;

  readonly formMatricula = this.fb.group({
    estudianteId: [null as number | null, [Validators.required]],
    grupoId: [null as number | null, [Validators.required]],
    tipoMatricula: [TipoMatricula.Nuevo, [Validators.required]],
    fechaMatricula: [new Date().toISOString().substring(0, 10), [Validators.required]],
    folio: [''],
    aceptaReglamentos: [true, [Validators.requiredTrue]]
  });

  readonly formNovedad = this.fb.group({
    tipoNovedad: [TipoNovedadMatricula.RetiroVoluntario, [Validators.required]],
    motivo: ['', [Validators.required]],
    numeroActaResolucion: ['']
  });

  ngOnInit(): void {
    this.cargarAnios();
    this.cargarEstudiantes();
  }

  seleccionarSofiaListo(): void {
    const sofia = this.estudiantes().find(e => e.documento === '1030405060' || e.nombres?.includes('Sof'));
    if (sofia) {
      this.formMatricula.patchValue({
        estudianteId: sofia.id!,
        tipoMatricula: TipoMatricula.Nuevo,
        folio: `Libro 2026 Folio ${Math.floor(10 + Math.random() * 90)}`
      });
      this.notificationService.showSuccess(`Estudiante ${sofia.nombres} ${sofia.apellidos} seleccionada para matrícula formal.`, 'Estudiante Seleccionada');
    }
  }

  cargarEstudiantes(): void {
    this.estudiantesService.apiEstudiantesGet(0, 100).subscribe({
      next: (data) => {
        const list = data.content || [];
        this.estudiantes.set(list);
        if (list.length > 0) {
          const sofia = list.find(e => e.documento === '1030405060') || list[0];
          this.formMatricula.patchValue({
            estudianteId: sofia.id!,
            folio: `Libro 2026 Folio 15`
          });
        }
      }
    });
  }

  cargarAnios(): void {
    this.aniosService.apiAniosLectivosGet().subscribe({
      next: (data) => {
        this.anios.set(data);
        if (data.length > 0 && !this.anioSeleccionadoId()) {
          const activo = data.find(a => a.estado === EstadoAnioLectivo.Activo) || data[0];
          this.anioSeleccionadoId.set(activo.id!);
          this.cargarGrupos(activo.id!);
          this.cargarMatriculas(activo.id!);
        }
      }
    });
  }

  cargarGrupos(anioId: number): void {
    this.gruposService.apiGruposGet(anioId).subscribe({
      next: (data) => {
        this.grupos.set(data);
        if (data.length > 0 && !this.formMatricula.get('grupoId')?.value) {
          this.formMatricula.patchValue({ grupoId: data[0].id! });
        }
      }
    });
  }

  cargarMatriculas(anioId: number, grupoId?: number | null): void {
    if (grupoId) {
      this.matriculasService.listarMatriculas(grupoId).subscribe({
        next: (data) => {
          this.matriculas.set(data);
        }
      });
    } else {
      this.matriculasService.listarMatriculasPorAnioLectivo(anioId).subscribe({
        next: (data) => {
          this.matriculas.set(data.content || []);
        }
      });
    }
  }

  onAnioChange(event: Event): void {
    const id = Number((event.target as HTMLSelectElement).value);
    this.anioSeleccionadoId.set(id);
    this.cargarGrupos(id);
    this.cargarMatriculas(id, this.grupoSeleccionadoId());
  }

  onGrupoChange(event: Event): void {
    const val = (event.target as HTMLSelectElement).value;
    const gId = val && val !== 'null' ? Number(val) : null;
    this.grupoSeleccionadoId.set(gId);
    if (this.anioSeleccionadoId()) {
      this.cargarMatriculas(this.anioSeleccionadoId()!, gId);
    }
  }

  formalizarMatricula(): void {
    if (this.formMatricula.invalid) return;

    this.guardando.set(true);
    const raw = this.formMatricula.getRawValue();
    const req = {
      estudianteId: Number(raw.estudianteId),
      grupoId: Number(raw.grupoId),
      tipoMatricula: raw.tipoMatricula!,
      fechaMatricula: raw.fechaMatricula!,
      folio: raw.folio || undefined
    };

    this.matriculasService.matricular(req as any).subscribe({
      next: (resp) => {
        this.guardando.set(false);
        this.notificationService.showSuccess(
          `Matrícula formalizada exitosamente con código ${resp.numeroMatricula || resp.id}. Cupo descontado.`,
          'Matrícula Exitosa'
        );
        this.tabActiva.set('listado');
        if (this.anioSeleccionadoId()) {
          this.cargarMatriculas(this.anioSeleccionadoId()!, this.grupoSeleccionadoId());
        }
      },
      error: () => {
        this.guardando.set(false);
      }
    });
  }

  abrirModalNovedad(m: MatriculaResponse): void {
    this.matriculaSeleccionada.set(m);
    this.formNovedad.reset({
      tipoNovedad: TipoNovedadMatricula.RetiroVoluntario,
      motivo: '',
      numeroActaResolucion: ''
    });
  }

  cerrarModalNovedad(): void {
    this.matriculaSeleccionada.set(null);
  }

  aplicarNovedad(): void {
    const m = this.matriculaSeleccionada();
    if (!m || !m.id || this.formNovedad.invalid) return;

    this.guardandoNovedad.set(true);
    const req = {
      ...this.formNovedad.getRawValue(),
      fechaEfectiva: new Date().toISOString().substring(0, 10)
    };

    this.novedadesService.apiMatriculasIdRetiroPost(m.id, req as any).subscribe({
      next: () => {
        this.guardandoNovedad.set(false);
        this.cerrarModalNovedad();
        this.notificationService.showSuccess('Novedad de permanencia registrada exitosamente.');
        if (this.anioSeleccionadoId()) {
          this.cargarMatriculas(this.anioSeleccionadoId()!, this.grupoSeleccionadoId());
        }
      },
      error: () => {
        this.guardandoNovedad.set(false);
      }
    });
  }
}
