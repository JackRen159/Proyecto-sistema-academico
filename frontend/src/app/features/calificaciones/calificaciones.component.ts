import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { CalificacionesYSIEEService } from '../../core/api/generated/api/calificaciones-ysiee.service';
import { AsignacionesAcadmicasService } from '../../core/api/generated/api/asignaciones-acadmicas.service';
import { EstructuraAcadmicaPeriodosService } from '../../core/api/generated/api/estructura-acadmica-periodos.service';
import { EstructuraAcadmicaGruposService } from '../../core/api/generated/api/estructura-acadmica-grupos.service';
import { EstructuraAcadmicaAosLectivosService } from '../../core/api/generated/api/estructura-acadmica-aos-lectivos.service';
import { MatrculasService } from '../../core/api/generated/api/matrculas.service';
import { NotificationService } from '../../core/services/notification.service';
import { AsignacionAcademicaResponse } from '../../core/api/generated/model/asignacion-academica-response';
import { PeriodoAcademicoResponse } from '../../core/api/generated/model/periodo-academico-response';
import { AnioLectivoResponse } from '../../core/api/generated/model/anio-lectivo-response';
import { MatriculaResponse } from '../../core/api/generated/model/matricula-response';
import { GrupoResponse } from '../../core/api/generated/model/grupo-response';
import { ActividadEvaluativaResponse } from '../../core/api/generated/model/actividad-evaluativa-response';
import { EstadoCalificacionActividad } from '../../core/api/generated/model/estado-calificacion-actividad';
import { EstadoAnioLectivo } from '../../core/api/generated/model/estado-anio-lectivo';
import { SabanaConsolidadaResponse } from '../../core/api/generated/model/sabana-consolidada-response';

interface FilaCalificacion {
  matriculaId: number;
  estudianteNombre: string;
  valor: number;
  estado: EstadoCalificacionActividad;
  observaciones?: string;
}

@Component({
  selector: 'app-calificaciones',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <div class="calificaciones-container">
      <header class="header">
        <div>
          <h2>Calificaciones y Evaluación SIEE V2</h2>
          <p class="subtitle">Planilla docente por actividades, sábanas consolidadas y actas de nivelación (UC-16..18, UC-PROV-15..17)</p>
        </div>
      </header>

      <!-- TABS -->
      <div class="tabs-bar">
        <button
          type="button"
          class="tab-btn"
          [class.active]="tabActiva() === 'planilla'"
          (click)="tabActiva.set('planilla')"
        >
          📝 Planilla de Calificaciones
        </button>
        <button
          type="button"
          class="tab-btn"
          [class.active]="tabActiva() === 'sabana'"
          (click)="tabActiva.set('sabana')"
        >
          📊 Sábana Consolidada Grupal
        </button>
        <button
          type="button"
          class="tab-btn"
          [class.active]="tabActiva() === 'nivelaciones'"
          (click)="tabActiva.set('nivelaciones')"
        >
          📈 Nivelaciones SIEE
        </button>
      </div>

      <!-- TAB 1: PLANILLA DOCENTE -->
      @if (tabActiva() === 'planilla') {
        <div class="tab-content">
          <div class="filtro-panel">
            <div class="form-row">
              <div class="form-col">
                <label for="cAnio">Año Lectivo:</label>
                <select id="cAnio" [value]="anioSeleccionadoId()" (change)="onAnioCalificacionesChange($event)" class="form-control">
                  @for (a of anios(); track a.id) {
                    <option [value]="a.id">Año {{ a.anio }} ({{ a.estado }})</option>
                  }
                </select>
              </div>
              <div class="form-col">
                <label for="cAsignacion">Carga / Asignación:</label>
                <select id="cAsignacion" [value]="asignacionSeleccionadaId()" (change)="onAsignacionChange($event)" class="form-control">
                  @if (asignaciones().length === 0) {
                    <option [value]="null">-- Sin asignaciones en este año --</option>
                  }
                  @for (asig of asignaciones(); track asig.id) {
                    <option [value]="asig.id">{{ asig.grupoNombre }} — {{ asig.asignaturaNombre }} ({{ asig.profesorNombreCompleto }})</option>
                  }
                </select>
              </div>
              <div class="form-col">
                <label for="cPeriodo">Periodo Académico:</label>
                <select id="cPeriodo" [value]="periodoSeleccionadoId()" (change)="onPeriodoChange($event)" class="form-control">
                  @if (periodos().length === 0) {
                    <option [value]="null">-- Sin periodos en este año --</option>
                  }
                  @for (p of periodos(); track p.id) {
                    <option [value]="p.id">{{ p.nombre }} ({{ p.porcentaje }}%)</option>
                  }
                </select>
              </div>
              <div class="form-col btn-align">
                <button type="button" class="btn-primary" (click)="mostrarFormActividad.set(true)">
                  + Nueva Actividad Evaluativa
                </button>
              </div>
            </div>
          </div>

          <!-- FORMULARIO NUEVA ACTIVIDAD -->
          @if (mostrarFormActividad()) {
            <form [formGroup]="formActividad" (ngSubmit)="crearActividad()" class="mini-form-card">
              <h4>Crear Actividad Evaluativa (SIEE, tope 100%)</h4>
              <div class="form-row">
                <div class="form-col">
                  <label for="actNombre">Nombre de la Actividad: *</label>
                  <input id="actNombre" type="text" formControlName="nombre" placeholder="Ej. Taller 1: Ecuaciones" class="form-control" />
                </div>
                <div class="form-col">
                  <label for="actPorcentaje">Porcentaje (%): *</label>
                  <input id="actPorcentaje" type="number" formControlName="porcentaje" placeholder="Ej. 20" class="form-control" />
                </div>
              </div>
              <div class="form-actions">
                <button type="button" class="btn-secondary" (click)="mostrarFormActividad.set(false)">Cancelar</button>
                <button type="submit" [disabled]="formActividad.invalid || guardandoActividad()" class="btn-primary">
                  Guardar Actividad
                </button>
              </div>
            </form>
          }

          <!-- SELECTOR DE ACTIVIDAD Y ACCIONES -->
          @if (actividades().length > 0) {
            <div class="toolbar-actividades">
              <div class="actividad-selector">
                <label for="selActividad">Actividad a Calificar:</label>
                <select id="selActividad" [value]="actividadSeleccionadaId()" (change)="onActividadChange($event)" class="form-control inline-select">
                  @for (act of actividades(); track act.id) {
                    <option [value]="act.id">{{ act.nombre }} ({{ act.porcentaje }}%)</option>
                  }
                </select>
              </div>

              <div class="botones-acciones">
                <button type="button" class="btn-secondary" (click)="consolidarDefinitivas()">
                  ⚡ Consolidar Definitivas
                </button>
                <button type="button" [disabled]="guardandoNotas()" class="btn-primary" (click)="guardarNotasLote()">
                  @if (guardandoNotas()) {
                    <span>Guardando...</span>
                  } @else {
                    <span>💾 Guardar Notas por Lote</span>
                  }
                </button>
              </div>
            </div>

            <div class="table-card">
              <table>
                <thead>
                  <tr>
                    <th>Estudiante</th>
                    <th>Calificación (Escala SIEE)</th>
                    <th>Estado de la Evaluación</th>
                    <th>Observación Descriptiva</th>
                  </tr>
                </thead>
                <tbody>
                  @for (fila of filasCalificaciones(); track fila.matriculaId) {
                    <tr>
                      <td><strong>{{ fila.estudianteNombre }}</strong></td>
                      <td>
                        <input
                          type="number"
                          step="0.01"
                          min="1"
                          max="5"
                          [value]="fila.valor"
                          (input)="onNotaChange(fila, $event)"
                          class="form-control input-nota"
                        />
                      </td>
                      <td>
                        <select
                          [value]="fila.estado"
                          (change)="onEstadoChange(fila, $event)"
                          class="form-control select-sm"
                        >
                          <option [value]="EstadoCalificacionActividad.Calificada">Calificada</option>
                          <option [value]="EstadoCalificacionActividad.PendientePorExcusaJustificada">Pendiente Excusa Médica</option>
                          <option [value]="EstadoCalificacionActividad.NoPresento">No Presentó</option>
                        </select>
                      </td>
                      <td>
                        <input
                          type="text"
                          [value]="fila.observaciones || ''"
                          (input)="onObservacionChange(fila, $event)"
                          placeholder="Criterio descriptivo..."
                          class="form-control input-obs"
                        />
                      </td>
                    </tr>
                  }
                </tbody>
              </table>
            </div>
          } @else {
            <div class="empty-state">
              <p>No hay actividades evaluativas creadas para esta asignación en este periodo. Cree una actividad para comenzar a calificar.</p>
            </div>
          }
        </div>
      }

      <!-- TAB 2: SÁBANA CONSOLIDADA -->
      @if (tabActiva() === 'sabana') {
        <div class="tab-content">
          <div class="filtro-panel">
            <div class="form-row">
              <div class="form-col">
                <label for="sabAnio">Año Lectivo:</label>
                <select id="sabAnio" [value]="anioSeleccionadoId()" (change)="onAnioCalificacionesChange($event)" class="form-control">
                  @for (a of anios(); track a.id) {
                    <option [value]="a.id">Año {{ a.anio }} ({{ a.estado }})</option>
                  }
                </select>
              </div>
              <div class="form-col">
                <label for="sabGrupo">Grupo:</label>
                <select id="sabGrupo" [value]="grupoSabanaId()" (change)="onGrupoSabanaChange($event)" class="form-control">
                  @if (grupos().length === 0) {
                    <option [value]="null">-- Sin grupos en este año --</option>
                  }
                  @for (g of grupos(); track g.id) {
                    <option [value]="g.id">{{ g.nombre }}</option>
                  }
                </select>
              </div>
              <div class="form-col">
                <label for="sabPeriodo">Periodo:</label>
                <select id="sabPeriodo" [value]="periodoSabanaId()" (change)="onPeriodoSabanaChange($event)" class="form-control">
                  @if (periodos().length === 0) {
                    <option [value]="null">-- Sin periodos en este año --</option>
                  }
                  @for (p of periodos(); track p.id) {
                    <option [value]="p.id">{{ p.nombre }}</option>
                  }
                </select>
              </div>
              <div class="form-col btn-align">
                <button type="button" class="btn-primary" (click)="cargarSabana()">
                  Cargar Sábana Grupal
                </button>
              </div>
            </div>
          </div>

          @if (sabana()) {
            <div class="table-card">
              <div class="sabana-header">
                <h3>Sábana Consolidada: {{ sabana()?.grupoNombre }}</h3>
                <span class="badge badge-active">{{ sabana()?.periodoNombre || 'Periodo Activo' }}</span>
              </div>
              <p class="sabana-sub">
                Director de Grupo: <strong>{{ sabana()?.directorGrupoNombre || 'Sin asignar' }}</strong> |
                Total Alumnos: <strong>{{ sabana()?.totalEstudiantes || 0 }}</strong> |
                En Riesgo Académico: <strong class="text-danger">{{ sabana()?.totalEstudiantesEnRiesgo || 0 }}</strong>
              </p>

              <div class="metrics-grid">
                <div class="metric-box">
                  <span class="metric-label">Total Alumnos</span>
                  <span class="metric-value">{{ sabana()?.totalEstudiantes || 0 }}</span>
                </div>
                <div class="metric-box">
                  <span class="metric-label">En Riesgo SIEE</span>
                  <span class="metric-value text-danger">{{ sabana()?.totalEstudiantesEnRiesgo || 0 }}</span>
                </div>
              </div>
            </div>
          }
        </div>
      }

      <!-- TAB 3: NIVELACIONES SIEE -->
      @if (tabActiva() === 'nivelaciones') {
        <div class="tab-content">
          <form [formGroup]="formNivelacion" (ngSubmit)="guardarNivelacion()" class="form-card">
            <h3>Acta de Nivelación y Principio de No Desmejoramiento (UC-18, RN-CAL-07)</h3>
            <p class="card-desc">
              Aplica la fórmula institucional del SIEE garantizando que la calificación vigente nunca desmejore la nota previa obtenida por el alumno.
            </p>

            <div class="form-row">
              <div class="form-col">
                <label for="nivAsignacion">Asignación Académica / Asignatura: *</label>
                <select id="nivAsignacion" class="form-control" (change)="onAsignacionNivelacionChange($event)">
                  <option [ngValue]="null">-- Seleccione Asignatura y Grupo --</option>
                  @for (a of asignaciones(); track a.id) {
                    <option [value]="a.id">
                      {{ a.asignaturaNombre }} - Grupo: {{ a.grupoNombre }} (Docente: {{ a.profesorNombreCompleto || 'Titular' }})
                    </option>
                  }
                </select>
              </div>
              <div class="form-col">
                <label for="nivPeriodo">Periodo Académico: *</label>
                <select id="nivPeriodo" formControlName="periodoId" class="form-control">
                  @for (p of periodos(); track p.id) {
                    <option [ngValue]="p.id">{{ p.nombre }}</option>
                  }
                </select>
              </div>
              <div class="form-col">
                <label for="nivMatricula">Estudiante a Nivelar: *</label>
                <select id="nivMatricula" formControlName="matriculaId" class="form-control">
                  <option [ngValue]="null">-- Seleccione Estudiante --</option>
                  @for (m of matriculasNivelacion(); track m.id) {
                    <option [ngValue]="m.id">
                      {{ m.estudianteNombreCompleto || m.estudianteDocumento }} ({{ m.numeroMatricula || ('MAT-' + m.id) }})
                    </option>
                  }
                </select>
                @if (formNivelacion.get('matriculaId')?.touched && formNivelacion.get('matriculaId')?.invalid) {
                  <small class="text-danger">Debe seleccionar un estudiante.</small>
                }
              </div>
            </div>

            <div class="form-row">
              <div class="form-col">
                <label for="nivNota">Nota Obtenida en Prueba de Nivelación: *</label>
                <input id="nivNota" type="number" step="0.01" min="1" max="5" formControlName="notaNivelacion" class="form-control" />
              </div>
              <div class="form-col">
                <label for="nivActa">Número de Acta de Nivelación: *</label>
                <input id="nivActa" type="text" formControlName="actaNumero" placeholder="Ej. ACT-NIV-2026-04" class="form-control" />
              </div>
            </div>

            <div class="form-row">
              <div class="form-col">
                <label for="nivObservaciones">Observaciones Pedagógicas y Estrategia de Apoyo:</label>
                <textarea id="nivObservaciones" formControlName="observaciones" rows="2" class="form-control"></textarea>
              </div>
            </div>

            <div class="form-actions">
              <button type="submit" [disabled]="formNivelacion.invalid || guardandoNivelacion()" class="btn-primary">
                @if (guardandoNivelacion()) {
                  <span>Registrando Acta...</span>
                } @else {
                  <span>Formalizar Acta de Nivelación</span>
                }
              </button>
            </div>
          </form>
        </div>
      }
    </div>
  `,
  styles: [`
    .calificaciones-container {
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
    .filtro-panel {
      background: white;
      border: 1px solid #e2e8f0;
      border-radius: 8px;
      padding: 1.25rem;
      margin-bottom: 1.25rem;
      box-shadow: 0 1px 3px rgba(0,0,0,0.05);
    }
    .form-row { display: flex; gap: 1rem; align-items: flex-end; }
    .form-col { flex: 1; display: flex; flex-direction: column; }
    .btn-align { flex: 0 0 auto; }
    label { font-size: 0.85rem; font-weight: 500; margin-bottom: 0.25rem; color: #475569; }
    .form-control { padding: 0.5rem 0.75rem; border: 1px solid #cbd5e1; border-radius: 4px; font-size: 0.9rem; }
    .mini-form-card {
      background: #eff6ff;
      border: 1px solid #bfdbfe;
      border-radius: 8px;
      padding: 1.25rem;
      margin-bottom: 1.25rem;
    }
    .mini-form-card h4 { margin: 0 0 1rem; color: #1e40af; }
    .form-actions { display: flex; justify-content: flex-end; gap: 0.5rem; margin-top: 1rem; }
    .toolbar-actividades {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 1rem;
      background: white;
      padding: 0.75rem 1rem;
      border-radius: 6px;
      border: 1px solid #e2e8f0;
    }
    .actividad-selector { display: flex; align-items: center; gap: 0.75rem; }
    .inline-select { min-width: 200px; }
    .botones-acciones { display: flex; gap: 0.5rem; }
    .table-card {
      background: white;
      border: 1px solid #e2e8f0;
      border-radius: 8px;
      overflow: hidden;
      box-shadow: 0 1px 3px rgba(0,0,0,0.05);
    }
    table { width: 100%; border-collapse: collapse; text-align: left; font-size: 0.9rem; }
    th { background: #f8fafc; color: #475569; font-weight: 600; padding: 0.75rem 1rem; border-bottom: 1px solid #e2e8f0; }
    td { padding: 0.65rem 1rem; border-bottom: 1px solid #f1f5f9; color: #1e293b; }
    .input-nota { width: 80px; font-weight: 700; text-align: center; }
    .select-sm { min-width: 160px; font-size: 0.82rem; }
    .input-obs { width: 100%; font-size: 0.85rem; }
    .empty-state { background: white; padding: 2rem; border-radius: 8px; text-align: center; color: #64748b; border: 1px solid #e2e8f0; }
    .sabana-header { display: flex; justify-content: space-between; align-items: center; padding: 1rem 1rem 0; }
    .sabana-header h3 { margin: 0; font-size: 1.1rem; }
    .sabana-sub { font-size: 0.85rem; color: #64748b; padding: 0 1rem 0.5rem; margin: 0.25rem 0 0; }
    .btn-primary { background: #2563eb; color: white; border: none; padding: 0.55rem 1.15rem; border-radius: 4px; font-weight: 600; font-size: 0.88rem; cursor: pointer; }
    .btn-primary:disabled { background: #93c5fd; cursor: not-allowed; }
    .btn-secondary { background: #e2e8f0; color: #334155; border: none; padding: 0.55rem 1rem; border-radius: 4px; font-size: 0.88rem; cursor: pointer; font-weight: 500; }
    .form-card { background: white; border: 1px solid #e2e8f0; border-radius: 8px; padding: 1.5rem; box-shadow: 0 1px 3px rgba(0,0,0,0.05); }
    .card-desc { font-size: 0.85rem; color: #64748b; margin-bottom: 1.25rem; }
    .badge { padding: 0.2rem 0.5rem; border-radius: 4px; font-size: 0.75rem; font-weight: 600; background: #e2e8f0; color: #475569; }
    .badge-active, .badge-success { background: #dcfce7; color: #166534; }
    .badge-danger { background: #fee2e2; color: #991b1b; }
    .weight-chip { background: #f8fafc; padding: 0.15rem 0.45rem; border-radius: 4px; font-size: 0.8rem; font-weight: 600; color: #0284c7; }
  `]
})
export class CalificacionesComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly calificacionService = inject(CalificacionesYSIEEService);
  private readonly asignacionesService = inject(AsignacionesAcadmicasService);
  private readonly aniosService = inject(EstructuraAcadmicaAosLectivosService);
  private readonly periodosService = inject(EstructuraAcadmicaPeriodosService);
  private readonly gruposService = inject(EstructuraAcadmicaGruposService);
  private readonly matriculasService = inject(MatrculasService);
  private readonly notificationService = inject(NotificationService);

  readonly tabActiva = signal<'planilla' | 'sabana' | 'nivelaciones'>('planilla');
  readonly guardandoActividad = signal(false);
  readonly guardandoNotas = signal(false);
  readonly guardandoNivelacion = signal(false);

  readonly anios = signal<AnioLectivoResponse[]>([]);
  readonly asignaciones = signal<AsignacionAcademicaResponse[]>([]);
  readonly periodos = signal<PeriodoAcademicoResponse[]>([]);
  readonly grupos = signal<GrupoResponse[]>([]);
  readonly actividades = signal<ActividadEvaluativaResponse[]>([]);
  readonly filasCalificaciones = signal<FilaCalificacion[]>([]);
  readonly matriculasNivelacion = signal<MatriculaResponse[]>([]);
  readonly sabana = signal<SabanaConsolidadaResponse | null>(null);

  readonly anioSeleccionadoId = signal<number | null>(null);
  readonly asignacionSeleccionadaId = signal<number>(1);
  readonly periodoSeleccionadoId = signal<number>(1);
  readonly actividadSeleccionadaId = signal<number | null>(null);

  readonly grupoSabanaId = signal<number>(10);
  readonly periodoSabanaId = signal<number>(1);

  readonly mostrarFormActividad = signal(false);

  readonly EstadoCalificacionActividad = EstadoCalificacionActividad;

  readonly formActividad = this.fb.group({
    nombre: ['', [Validators.required]],
    porcentaje: [20, [Validators.required, Validators.min(1), Validators.max(100)]]
  });

  readonly formNivelacion = this.fb.group({
    matriculaId: [null as number | null, [Validators.required]],
    asignaturaId: [null as number | null, [Validators.required]],
    periodoId: [null as number | null, [Validators.required]],
    notaNivelacion: [3.0, [Validators.required, Validators.min(1), Validators.max(5)]],
    actaNumero: ['', [Validators.required]],
    observaciones: ['']
  });

  ngOnInit(): void {
    this.cargarCatalogos();
  }

  cargarCatalogos(): void {
    this.aniosService.apiAniosLectivosGet().subscribe({
      next: (anios) => {
        this.anios.set(anios);
        if (anios.length > 0) {
          const activo = anios.find(a => a.estado === EstadoAnioLectivo.Activo) || anios[0];
          const anioId = activo.id!;
          this.anioSeleccionadoId.set(anioId);
          this.cargarCatalogosPorAnio(anioId);
        }
      }
    });
  }

  cargarCatalogosPorAnio(anioId: number): void {
    this.asignacionesService.apiAsignacionesAcademicasGet(undefined, undefined, anioId).subscribe({
      next: (asigs) => {
        this.asignaciones.set(asigs);
        if (asigs.length > 0) {
          this.asignacionSeleccionadaId.set(asigs[0].id!);
          this.cargarActividades(asigs[0].id!, this.periodoSeleccionadoId());
          if (asigs[0].grupoId) {
            this.formNivelacion.patchValue({
              asignaturaId: asigs[0].asignaturaId,
              periodoId: this.periodoSeleccionadoId()
            });
            this.cargarMatriculasNivelacion(asigs[0].grupoId);
          }
        } else {
          this.asignacionSeleccionadaId.set(1);
          this.actividades.set([]);
          this.filasCalificaciones.set([]);
        }
      }
    });

    this.periodosService.apiAniosLectivosAnioIdPeriodosGet(anioId).subscribe({
      next: (pers) => {
        this.periodos.set(pers);
        if (pers.length > 0) {
          this.periodoSeleccionadoId.set(pers[0].id!);
          this.periodoSabanaId.set(pers[0].id!);
        }
      }
    });

    this.gruposService.apiGruposGet(anioId).subscribe({
      next: (grps) => {
        this.grupos.set(grps);
        if (grps.length > 0) this.grupoSabanaId.set(grps[0].id!);
      }
    });
  }

  onAnioCalificacionesChange(event: Event): void {
    const aId = Number((event.target as HTMLSelectElement).value);
    this.anioSeleccionadoId.set(aId);
    this.cargarCatalogosPorAnio(aId);
  }

  cargarMatriculasNivelacion(grupoId: number): void {
    this.matriculasService.listarMatriculas(grupoId).subscribe({
      next: (mats) => {
        this.matriculasNivelacion.set(mats);
        if (mats.length > 0 && !this.formNivelacion.get('matriculaId')?.value) {
          this.formNivelacion.patchValue({ matriculaId: mats[0].id! });
        }
      }
    });
  }

  onAsignacionNivelacionChange(event: Event): void {
    const asigId = Number((event.target as HTMLSelectElement).value);
    const asig = this.asignaciones().find(a => a.id === asigId);
    if (asig && asig.grupoId) {
      this.formNivelacion.patchValue({ asignaturaId: asig.asignaturaId });
      this.cargarMatriculasNivelacion(asig.grupoId);
    }
  }

  onAsignacionChange(event: Event): void {
    const id = Number((event.target as HTMLSelectElement).value);
    this.asignacionSeleccionadaId.set(id);
    this.cargarActividades(id, this.periodoSeleccionadoId());
  }

  onPeriodoChange(event: Event): void {
    const id = Number((event.target as HTMLSelectElement).value);
    this.periodoSeleccionadoId.set(id);
    this.cargarActividades(this.asignacionSeleccionadaId(), id);
  }

  onActividadChange(event: Event): void {
    const id = Number((event.target as HTMLSelectElement).value);
    this.actividadSeleccionadaId.set(id);
    this.cargarNotasActividad(id);
  }

  cargarActividades(asignacionId: number, periodoId: number): void {
    this.calificacionService.listarActividadesEvaluativas(asignacionId, periodoId).subscribe({
      next: (data) => {
        this.actividades.set(data);
        if (data.length > 0) {
          this.actividadSeleccionadaId.set(data[0].id!);
          this.cargarNotasActividad(data[0].id!);
        } else {
          this.actividadSeleccionadaId.set(null);
          this.filasCalificaciones.set([]);
        }
      }
    });
  }

  cargarNotasActividad(actividadId: number): void {
    const asig = this.asignaciones().find(a => a.id === this.asignacionSeleccionadaId());
    const grupoId = asig?.grupoId || 10;

    this.matriculasService.listarMatriculas(grupoId).subscribe({
      next: (mats) => {
        this.calificacionService.listarNotasActividad(actividadId).subscribe({
          next: (notasExistentes) => {
            const filas: FilaCalificacion[] = mats.map(m => {
              const notaGuardada = notasExistentes.find(n => n.matriculaId === m.id);
              return {
                matriculaId: m.id!,
                estudianteNombre: m.estudianteNombreCompleto || `Estudiante MAT-${m.id}`,
                valor: notaGuardada?.valor || 3.0,
                estado: notaGuardada?.estado || EstadoCalificacionActividad.Calificada,
                observaciones: notaGuardada?.observaciones || ''
              };
            });
            this.filasCalificaciones.set(filas);
          }
        });
      }
    });
  }

  onNotaChange(fila: FilaCalificacion, event: Event): void {
    fila.valor = Number((event.target as HTMLInputElement).value);
  }

  onEstadoChange(fila: FilaCalificacion, event: Event): void {
    fila.estado = (event.target as HTMLSelectElement).value as EstadoCalificacionActividad;
  }

  onObservacionChange(fila: FilaCalificacion, event: Event): void {
    fila.observaciones = (event.target as HTMLInputElement).value;
  }

  crearActividad(): void {
    if (this.formActividad.invalid) return;

    this.guardandoActividad.set(true);
    const req = {
      asignacionAcademicaId: this.asignacionSeleccionadaId(),
      periodoAcademicoId: this.periodoSeleccionadoId(),
      nombre: this.formActividad.get('nombre')?.value!,
      porcentaje: this.formActividad.get('porcentaje')?.value!
    };

    this.calificacionService.crearActividadEvaluativa(req as any).subscribe({
      next: (act) => {
        this.guardandoActividad.set(false);
        this.mostrarFormActividad.set(false);
        this.notificationService.showSuccess(`Actividad ${act.nombre} creada.`);
        this.cargarActividades(this.asignacionSeleccionadaId(), this.periodoSeleccionadoId());
      },
      error: () => {
        this.guardandoActividad.set(false);
      }
    });
  }

  guardarNotasLote(): void {
    const actId = this.actividadSeleccionadaId();
    if (!actId) return;

    this.guardandoNotas.set(true);
    const req = {
      actividadId: actId,
      calificaciones: this.filasCalificaciones().map(f => ({
        matriculaId: f.matriculaId,
        valor: f.valor,
        estado: f.estado,
        observaciones: f.observaciones
      }))
    };

    this.calificacionService.registrarNotasLote(req as any).subscribe({
      next: () => {
        this.guardandoNotas.set(false);
        this.notificationService.showSuccess('Calificaciones por lote guardadas exitosamente.');
      },
      error: () => {
        this.guardandoNotas.set(false);
      }
    });
  }

  consolidarDefinitivas(): void {
    const asigId = this.asignacionSeleccionadaId();
    const perId = this.periodoSeleccionadoId();

    this.calificacionService.calcularDefinitivasPeriodo(asigId, perId).subscribe({
      next: () => {
        this.notificationService.showSuccess('Definitivas del periodo consolidadas con motor BigDecimal.');
      }
    });
  }

  onGrupoSabanaChange(event: Event): void {
    this.grupoSabanaId.set(Number((event.target as HTMLSelectElement).value));
  }

  onPeriodoSabanaChange(event: Event): void {
    this.periodoSabanaId.set(Number((event.target as HTMLSelectElement).value));
  }

  cargarSabana(): void {
    const gId = this.grupoSabanaId();
    const pId = this.periodoSabanaId();

    this.calificacionService.consultarSabanaConsolidada(gId, pId).subscribe({
      next: (data) => {
        this.sabana.set(data);
        this.notificationService.showInfo('Sábana grupal cargada.');
      }
    });
  }

  guardarNivelacion(): void {
    if (this.formNivelacion.invalid) return;

    this.guardandoNivelacion.set(true);
    const raw = this.formNivelacion.getRawValue();
    const req = {
      ...raw,
      notaObtenida: raw.notaNivelacion,
      fechaSustentacion: new Date().toISOString().substring(0, 10),
      asistencia: true
    };
    this.calificacionService.registrarActaNivelacion(req as any).subscribe({
      next: () => {
        this.guardandoNivelacion.set(false);
        this.notificationService.showSuccess('Acta de nivelación formalizada bajo principio de no desmejoramiento.');
        this.formNivelacion.reset({
          matriculaId: null,
          asignaturaId: null,
          periodoId: null,
          notaNivelacion: 3.0,
          actaNumero: '',
          observaciones: ''
        });
      },
      error: () => {
        this.guardandoNivelacion.set(false);
      }
    });
  }
}
