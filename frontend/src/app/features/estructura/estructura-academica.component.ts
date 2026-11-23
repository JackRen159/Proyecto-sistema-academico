import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { EstructuraAcadmicaAosLectivosService } from '../../core/api/generated/api/estructura-acadmica-aos-lectivos.service';
import { EstructuraAcadmicaPeriodosService } from '../../core/api/generated/api/estructura-acadmica-periodos.service';
import { EstructuraAcadmicaGradosService } from '../../core/api/generated/api/estructura-acadmica-grados.service';
import { EstructuraAcadmicaGruposService } from '../../core/api/generated/api/estructura-acadmica-grupos.service';
import { NotificationService } from '../../core/services/notification.service';
import { AnioLectivoResponse } from '../../core/api/generated/model/anio-lectivo-response';
import { PeriodoAcademicoResponse } from '../../core/api/generated/model/periodo-academico-response';
import { GradoResponse } from '../../core/api/generated/model/grado-response';
import { GrupoResponse } from '../../core/api/generated/model/grupo-response';
import { Jornada } from '../../core/api/generated/model/jornada';
import { EstadoAnioLectivo } from '../../core/api/generated/model/estado-anio-lectivo';
import { TipoCalendario } from '../../core/api/generated/model/tipo-calendario';

@Component({
  selector: 'app-estructura-academica',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <div class="estructura-container">
      <header class="header">
        <div>
          <h2>Estructura Académica Institucional</h2>
          <p class="subtitle">Gestión de Años Lectivos, Periodos, Grados y Grupos con control de cupos (UC-14, UC-PROV-06..08)</p>
        </div>
      </header>

      <div class="guion-step-banner">
        <div class="step-badge">Paso 3 del Guion</div>
        <div class="step-content">
          <strong>Configuración del Año Lectivo y Escala SIEE (UC-14):</strong>
          Visualiza que el <strong>Año 2026</strong> se encuentra <code>ACTIVO</code>. En la pestaña <em>Periodos Académicos</em> verifica que los 4 periodos suman exactamente <strong>100.00%</strong> (Periodo 1 en estado <code>ABIERTO</code>) y en <em>Grados y Cursos</em> observa la oferta del <strong>Grupo 10-A</strong> con su aforo regulado de <strong>35 cupos</strong>.
        </div>
      </div>

      <!-- TABS -->
      <div class="tabs-bar">
        <button
          type="button"
          class="tab-btn"
          [class.active]="tabActiva() === 'anios'"
          (click)="tabActiva.set('anios')"
        >
          📅 Años Lectivos
        </button>
        <button
          type="button"
          class="tab-btn"
          [class.active]="tabActiva() === 'periodos'"
          (click)="tabActiva.set('periodos')"
        >
          ⏱️ Periodos Académicos
        </button>
        <button
          type="button"
          class="tab-btn"
          [class.active]="tabActiva() === 'grupos'"
          (click)="tabActiva.set('grupos')"
        >
          🏫 Grados y Cursos
        </button>
      </div>

      <!-- TAB 1: AÑOS LECTIVOS -->
      @if (tabActiva() === 'anios') {
        <div class="tab-content">
          <div class="content-toolbar">
            <h3>Catálogo de Años Lectivos</h3>
            <button type="button" class="btn-primary" (click)="mostrarFormAnio.set(true)">
              + Nuevo Año Lectivo
            </button>
          </div>

          @if (mostrarFormAnio()) {
            <form [formGroup]="formAnio" (ngSubmit)="crearAnioLectivo()" class="mini-form-card">
              <h4>Crear Año Lectivo</h4>
              <div class="form-row">
                <div class="form-col">
                  <label for="anioNumero">Año Numérico: *</label>
                  <input id="anioNumero" type="number" formControlName="anio" placeholder="Ej. 2026" class="form-control" (input)="onAnioNumeroChange()" />
                </div>
                <div class="form-col">
                  <label for="anioNombre">Nombre del Año Lectivo: *</label>
                  <input id="anioNombre" type="text" formControlName="nombre" placeholder="Ej. Año Lectivo 2026" class="form-control" />
                </div>
                <div class="form-col">
                  <label for="tipoCalendario">Tipo de Calendario: *</label>
                  <select id="tipoCalendario" formControlName="tipoCalendario" class="form-control">
                    <option [value]="TipoCalendario.CalendarioA">Calendario A</option>
                    <option [value]="TipoCalendario.CalendarioB">Calendario B</option>
                  </select>
                </div>
              </div>
              <div class="form-row">
                <div class="form-col">
                  <label for="fechaInicio">Fecha Inicio: *</label>
                  <input id="fechaInicio" type="date" formControlName="fechaInicio" class="form-control" />
                </div>
                <div class="form-col">
                  <label for="fechaFin">Fecha Fin: *</label>
                  <input id="fechaFin" type="date" formControlName="fechaFin" class="form-control" />
                </div>
              </div>
              <div class="form-actions">
                <button type="button" class="btn-secondary" (click)="mostrarFormAnio.set(false)">Cancelar</button>
                <button type="submit" [disabled]="formAnio.invalid" class="btn-primary">Guardar Año</button>
              </div>
            </form>
          }

          <div class="table-card">
            <table>
              <thead>
                <tr>
                  <th>Año</th>
                  <th>Calendario</th>
                  <th>Fecha Inicio</th>
                  <th>Fecha Fin</th>
                  <th>Estado</th>
                  <th>Acciones</th>
                </tr>
              </thead>
              <tbody>
                @for (a of anios(); track a.id) {
                  <tr>
                    <td><strong>{{ a.anio }}</strong></td>
                    <td>{{ a.tipoCalendario || 'A' }}</td>
                    <td>{{ a.fechaInicio }}</td>
                    <td>{{ a.fechaFin }}</td>
                    <td>
                      <span class="badge" [ngClass]="'badge-' + a.estado?.toLowerCase()">
                        {{ a.estado }}
                      </span>
                    </td>
                    <td>
                      <div class="actions-cell">
                        @if (a.estado === EstadoAnioLectivo.EnPlaneacion) {
                          <button type="button" class="btn-sm btn-success" (click)="activarAnio(a.id!)">
                            Activar
                          </button>
                        }
                        @if (a.estado === EstadoAnioLectivo.Activo) {
                          <button type="button" class="btn-sm btn-danger" (click)="cerrarAnio(a.id!)">
                            Cerrar
                          </button>
                        }
                      </div>
                    </td>
                  </tr>
                }
              </tbody>
            </table>
          </div>
        </div>
      }

      <!-- TAB 2: PERIODOS ACADÉMICOS -->
      @if (tabActiva() === 'periodos') {
        <div class="tab-content">
          <div class="content-toolbar">
            <div class="selector-row">
              <label for="selectAnioPeriodo">Año Lectivo:</label>
              <select
                id="selectAnioPeriodo"
                class="form-control inline-select"
                [value]="anioSeleccionadoId()"
                (change)="onAnioSeleccionadoChange($event)"
              >
                @for (a of anios(); track a.id) {
                  <option [value]="a.id">Año {{ a.anio }} ({{ a.estado }})</option>
                }
              </select>
            </div>

            <button type="button" class="btn-primary" (click)="mostrarFormPeriodo.set(true)">
              + Nuevo Periodo
            </button>
          </div>

          @if (mostrarFormPeriodo()) {
            <form [formGroup]="formPeriodo" (ngSubmit)="crearPeriodo()" class="mini-form-card">
              <h4>Crear Periodo Académico</h4>
              <div class="form-row">
                <div class="form-col">
                  <label for="numeroPeriodo">Número: *</label>
                  <input id="numeroPeriodo" type="number" formControlName="numero" placeholder="Ej. 1" class="form-control" />
                </div>
                <div class="form-col">
                  <label for="nombrePeriodo">Nombre: *</label>
                  <input id="nombrePeriodo" type="text" formControlName="nombre" placeholder="Ej. Primer Periodo" class="form-control" />
                </div>
                <div class="form-col">
                  <label for="porcentajePeriodo">Peso Porcentual (%): *</label>
                  <input id="porcentajePeriodo" type="number" formControlName="porcentaje" placeholder="Ej. 25.00" class="form-control" />
                </div>
              </div>
              <div class="form-row">
                <div class="form-col">
                  <label for="pFechaInicio">Fecha Inicio: *</label>
                  <input id="pFechaInicio" type="date" formControlName="fechaInicio" class="form-control" />
                </div>
                <div class="form-col">
                  <label for="pFechaFin">Fecha Fin: *</label>
                  <input id="pFechaFin" type="date" formControlName="fechaFin" class="form-control" />
                </div>
              </div>
              <div class="form-actions">
                <button type="button" class="btn-secondary" (click)="mostrarFormPeriodo.set(false)">Cancelar</button>
                <button type="submit" [disabled]="formPeriodo.invalid" class="btn-primary">Guardar Periodo</button>
              </div>
            </form>
          }

          <div class="table-card">
            <table>
              <thead>
                <tr>
                  <th>N°</th>
                  <th>Nombre</th>
                  <th>Ponderación</th>
                  <th>Fechas</th>
                  <th>Estado</th>
                  <th>Acciones</th>
                </tr>
              </thead>
              <tbody>
                @for (p of periodos(); track p.id) {
                  <tr>
                    <td><strong>{{ p.numero }}</strong></td>
                    <td>{{ p.nombre }}</td>
                    <td><span class="weight-chip">{{ p.porcentaje }}%</span></td>
                    <td>{{ p.fechaInicio }} al {{ p.fechaFin }}</td>
                    <td>
                      <span class="badge" [class.badge-active]="p.estado === 'ABIERTO'">
                        {{ p.estado }}
                      </span>
                    </td>
                    <td>
                      @if (p.estado === 'CERRADO') {
                        <button type="button" class="btn-sm btn-action" (click)="abrirPeriodo(p.id!)">Abrir</button>
                      } @else if (p.estado === 'ABIERTO') {
                        <button type="button" class="btn-sm btn-danger" (click)="cerrarPeriodo(p.id!)">Cerrar</button>
                      }
                    </td>
                  </tr>
                }
              </tbody>
            </table>
          </div>
        </div>
      }

      <!-- TAB 3: GRADOS Y GRUPOS -->
      @if (tabActiva() === 'grupos') {
        <div class="tab-content">
          <div class="content-toolbar">
            <div class="selector-row">
              <label for="selectAnioGrupos">Año Lectivo:</label>
              <select
                id="selectAnioGrupos"
                class="form-control inline-select"
                [value]="anioSeleccionadoId()"
                (change)="onAnioSeleccionadoChange($event)"
              >
                @for (a of anios(); track a.id) {
                  <option [value]="a.id">Año {{ a.anio }}</option>
                }
              </select>

              <label for="selectGrado">Grado:</label>
              <select
                id="selectGrado"
                class="form-control inline-select"
                [value]="gradoSeleccionadoId()"
                (change)="onGradoSeleccionadoChange($event)"
              >
                <option [value]="null">Todos los Grados</option>
                @for (g of grados(); track g.id) {
                  <option [value]="g.id">{{ g.nombre }} ({{ g.codigo }})</option>
                }
              </select>
            </div>

            <button type="button" class="btn-primary" (click)="mostrarFormGrupo.set(true)">
              + Nuevo Grupo
            </button>
          </div>

          @if (mostrarFormGrupo()) {
            <form [formGroup]="formGrupo" (ngSubmit)="crearGrupo()" class="mini-form-card">
              <h4>Crear Grupo / Curso</h4>
              <div class="form-row">
                <div class="form-col">
                  <label for="gGrado">Grado Académico: *</label>
                  <select id="gGrado" formControlName="gradoId" class="form-control">
                    @for (g of grados(); track g.id) {
                      <option [value]="g.id">{{ g.nombre }}</option>
                    }
                  </select>
                </div>
                <div class="form-col">
                  <label for="gNombre">Nombre o Letra del Grupo: *</label>
                  <input id="gNombre" type="text" formControlName="nombre" placeholder="Ej. 7A, 10-1" class="form-control" />
                </div>
                <div class="form-col">
                  <label for="gJornada">Jornada: *</label>
                  <select id="gJornada" formControlName="jornada" class="form-control">
                    <option value="MANANA">Mañana</option>
                    <option value="TARDE">Tarde</option>
                    <option value="COMPLETA">Completa</option>
                  </select>
                </div>
                <div class="form-col">
                  <label for="gCupoMaximo">Cupo Máximo: *</label>
                  <input id="gCupoMaximo" type="number" formControlName="cupoMaximo" placeholder="Ej. 35" class="form-control" />
                </div>
              </div>
              <div class="form-actions">
                <button type="button" class="btn-secondary" (click)="mostrarFormGrupo.set(false)">Cancelar</button>
                <button type="submit" [disabled]="formGrupo.invalid" class="btn-primary">Guardar Grupo</button>
              </div>
            </form>
          }

          <div class="table-card">
            <table>
              <thead>
                <tr>
                  <th>Grupo / Salón</th>
                  <th>Jornada</th>
                  <th>Cupo Máximo</th>
                  <th>Estado</th>
                </tr>
              </thead>
              <tbody>
                @for (grp of grupos(); track grp.id) {
                  <tr>
                    <td><strong>{{ grp.nombre }}</strong></td>
                    <td>{{ grp.jornada }}</td>
                    <td><span class="weight-chip">{{ grp.cupoMaximo }} estudiantes</span></td>
                    <td>
                      <span class="badge" [class.badge-active]="grp.activo">
                        {{ grp.activo ? 'Operativo' : 'Inactivo' }}
                      </span>
                    </td>
                  </tr>
                }
              </tbody>
            </table>
          </div>
        </div>
      }
    </div>
  `,
  styles: [`
    .estructura-container {
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
    .selector-row {
      display: flex;
      align-items: center;
      gap: 0.75rem;
    }
    .selector-row label { font-size: 0.88rem; font-weight: 500; color: #475569; }
    .inline-select { width: auto; min-width: 180px; }
    .mini-form-card {
      background: white;
      border: 1px solid #bfdbfe;
      border-radius: 8px;
      padding: 1.25rem;
      margin-bottom: 1.5rem;
      box-shadow: 0 2px 4px rgba(37,99,235,0.05);
    }
    .mini-form-card h4 { margin: 0 0 1rem; color: #1e40af; font-size: 1rem; }
    .form-row { display: flex; gap: 1rem; margin-bottom: 0.85rem; }
    .form-col { flex: 1; display: flex; flex-direction: column; }
    label { font-size: 0.82rem; font-weight: 500; margin-bottom: 0.25rem; color: #475569; }
    .form-control { padding: 0.45rem 0.65rem; border: 1px solid #cbd5e1; border-radius: 4px; font-size: 0.9rem; }
    .form-actions { display: flex; justify-content: flex-end; gap: 0.5rem; margin-top: 1rem; }
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
    .btn-primary { background: #2563eb; color: white; border: none; padding: 0.45rem 0.9rem; border-radius: 4px; font-weight: 500; font-size: 0.85rem; cursor: pointer; }
    .btn-secondary { background: #e2e8f0; color: #334155; border: none; padding: 0.45rem 0.85rem; border-radius: 4px; font-size: 0.85rem; cursor: pointer; }
    .btn-sm { padding: 0.25rem 0.5rem; font-size: 0.78rem; border-radius: 4px; border: none; cursor: pointer; font-weight: 500; }
    .btn-success { background: #16a34a; color: white; }
    .btn-danger { background: #dc2626; color: white; }
    .btn-action { background: #eff6ff; color: #2563eb; border: 1px solid #bfdbfe; }
    .badge { padding: 0.2rem 0.5rem; border-radius: 4px; font-size: 0.75rem; font-weight: 600; background: #e2e8f0; color: #475569; }
    .badge-en_curso, .badge-active { background: #dcfce7; color: #166534; }
    .badge-cerrado_historico { background: #f1f5f9; color: #64748b; }
    .weight-chip { background: #f1f5f9; padding: 0.15rem 0.45rem; border-radius: 4px; font-size: 0.8rem; font-weight: 600; color: #0284c7; }
    .actions-cell { display: flex; gap: 0.35rem; }
  `]
})
export class EstructuraAcademicaComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  readonly aniosService = inject(EstructuraAcadmicaAosLectivosService);
  readonly periodosService = inject(EstructuraAcadmicaPeriodosService);
  readonly gradosService = inject(EstructuraAcadmicaGradosService);
  readonly gruposService = inject(EstructuraAcadmicaGruposService);
  private readonly notificationService = inject(NotificationService);

  readonly tabActiva = signal<'anios' | 'periodos' | 'grupos'>('anios');
  readonly EstadoAnioLectivo = EstadoAnioLectivo;
  readonly anios = signal<AnioLectivoResponse[]>([]);
  readonly periodos = signal<PeriodoAcademicoResponse[]>([]);
  readonly grados = signal<GradoResponse[]>([]);
  readonly grupos = signal<GrupoResponse[]>([]);

  readonly anioSeleccionadoId = signal<number | null>(null);
  readonly gradoSeleccionadoId = signal<number | null>(null);

  readonly mostrarFormAnio = signal(false);
  readonly mostrarFormPeriodo = signal(false);
  readonly mostrarFormGrupo = signal(false);

  readonly TipoCalendario = TipoCalendario;

  readonly formAnio = this.fb.group({
    anio: [new Date().getFullYear(), [Validators.required]],
    nombre: [`Año Lectivo ${new Date().getFullYear()}`, [Validators.required]],
    tipoCalendario: [TipoCalendario.CalendarioA, [Validators.required]],
    fechaInicio: ['', [Validators.required]],
    fechaFin: ['', [Validators.required]]
  });

  onAnioNumeroChange(): void {
    const anioVal = this.formAnio.get('anio')?.value;
    if (anioVal && (!this.formAnio.get('nombre')?.dirty || !this.formAnio.get('nombre')?.value)) {
      this.formAnio.patchValue({ nombre: `Año Lectivo ${anioVal}` });
    }
  }

  readonly formPeriodo = this.fb.group({
    numero: [1, [Validators.required, Validators.min(1)]],
    nombre: ['', [Validators.required]],
    porcentaje: [25, [Validators.required, Validators.min(1), Validators.max(100)]],
    fechaInicio: ['', [Validators.required]],
    fechaFin: ['', [Validators.required]]
  });

  readonly formGrupo = this.fb.group({
    gradoId: [null as number | null, [Validators.required]],
    nombre: ['', [Validators.required]],
    jornada: [Jornada.Manana, [Validators.required]],
    cupoMaximo: [35, [Validators.required, Validators.min(1)]]
  });

  ngOnInit(): void {
    this.cargarAnios();
    this.cargarGrados();
  }

  cargarAnios(): void {
    this.aniosService.apiAniosLectivosGet().subscribe({
      next: (data) => {
        this.anios.set(data);
        if (data.length > 0 && !this.anioSeleccionadoId()) {
          const activo = data.find(a => a.estado === EstadoAnioLectivo.Activo) || data[0];
          this.anioSeleccionadoId.set(activo.id!);
          this.cargarPeriodos(activo.id!);
          this.cargarGrupos(activo.id!);
        }
      }
    });
  }

  cargarGrados(): void {
    this.gradosService.apiGradosGet().subscribe({
      next: (data) => {
        this.grados.set(data);
        if (data.length > 0 && !this.formGrupo.get('gradoId')?.value) {
          this.formGrupo.patchValue({ gradoId: data[0].id! });
        }
      }
    });
  }

  cargarPeriodos(anioId: number): void {
    this.periodosService.apiAniosLectivosAnioIdPeriodosGet(anioId).subscribe({
      next: (data) => {
        this.periodos.set(data);
      }
    });
  }

  cargarGrupos(anioId: number, gradoId?: number | null): void {
    this.gruposService.apiGruposGet(anioId, gradoId || undefined).subscribe({
      next: (data) => {
        this.grupos.set(data);
      }
    });
  }

  onAnioSeleccionadoChange(event: Event): void {
    const id = Number((event.target as HTMLSelectElement).value);
    this.anioSeleccionadoId.set(id);
    this.cargarPeriodos(id);
    this.cargarGrupos(id, this.gradoSeleccionadoId());
  }

  onGradoSeleccionadoChange(event: Event): void {
    const val = (event.target as HTMLSelectElement).value;
    const gradoId = val && val !== 'null' ? Number(val) : null;
    this.gradoSeleccionadoId.set(gradoId);
    if (this.anioSeleccionadoId()) {
      this.cargarGrupos(this.anioSeleccionadoId()!, gradoId);
    }
  }

  crearAnioLectivo(): void {
    if (this.formAnio.invalid) return;

    this.aniosService.apiAniosLectivosPost(this.formAnio.getRawValue() as any).subscribe({
      next: (nuevo) => {
        this.notificationService.showSuccess(`Año lectivo ${nuevo.anio} creado exitosamente.`);
        this.mostrarFormAnio.set(false);
        this.cargarAnios();
      }
    });
  }

  activarAnio(id: number): void {
    this.aniosService.apiAniosLectivosIdActivarPatch(id).subscribe({
      next: (act) => {
        this.notificationService.showSuccess(`Año lectivo ${act.anio} activado en curso.`);
        this.cargarAnios();
      }
    });
  }

  cerrarAnio(id: number): void {
    this.aniosService.apiAniosLectivosIdCerrarPatch(id).subscribe({
      next: () => {
        this.notificationService.showWarning(`Año lectivo cerrado formalmente.`);
        this.cargarAnios();
      }
    });
  }

  crearPeriodo(): void {
    const anioId = this.anioSeleccionadoId();
    if (!anioId || this.formPeriodo.invalid) return;

    this.periodosService.apiAniosLectivosAnioIdPeriodosPost(anioId, this.formPeriodo.getRawValue() as any).subscribe({
      next: (p) => {
        this.notificationService.showSuccess(`Periodo ${p.nombre} creado exitosamente.`);
        this.mostrarFormPeriodo.set(false);
        this.cargarPeriodos(anioId);
      }
    });
  }

  abrirPeriodo(id: number): void {
    this.periodosService.apiPeriodosIdAbrirPatch(id).subscribe({
      next: () => {
        this.notificationService.showSuccess('Periodo abierto para digitación.');
        if (this.anioSeleccionadoId()) this.cargarPeriodos(this.anioSeleccionadoId()!);
      }
    });
  }

  cerrarPeriodo(id: number): void {
    this.periodosService.apiPeriodosIdCerrarPatch(id).subscribe({
      next: () => {
        this.notificationService.showWarning('Periodo cerrado. Bloqueadas calificaciones ordinarias.');
        if (this.anioSeleccionadoId()) this.cargarPeriodos(this.anioSeleccionadoId()!);
      }
    });
  }

  crearGrupo(): void {
    const anioId = this.anioSeleccionadoId();
    if (!anioId || this.formGrupo.invalid) return;

    const req = {
      ...this.formGrupo.getRawValue(),
      anioLectivoId: anioId
    };

    this.gruposService.apiGruposPost(req as any).subscribe({
      next: (grp) => {
        this.notificationService.showSuccess(`Grupo ${grp.nombre} creado con cupo máximo de ${grp.cupoMaximo}.`);
        this.mostrarFormGrupo.set(false);
        this.cargarGrupos(anioId, this.gradoSeleccionadoId());
      }
    });
  }
}
