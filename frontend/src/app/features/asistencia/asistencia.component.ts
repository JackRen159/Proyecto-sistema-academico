import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ControlDeAsistenciaService } from '../../core/api/generated/api/control-de-asistencia.service';
import { EstructuraAcadmicaPeriodosService } from '../../core/api/generated/api/estructura-acadmica-periodos.service';
import { EstructuraAcadmicaGruposService } from '../../core/api/generated/api/estructura-acadmica-grupos.service';
import { MatrculasService } from '../../core/api/generated/api/matrculas.service';
import { NotificationService } from '../../core/services/notification.service';
import { EstadoAsistencia } from '../../core/api/generated/model/estado-asistencia';
import { TipoJustificacionAsistencia } from '../../core/api/generated/model/tipo-justificacion-asistencia';
import { RegistroAsistenciaItemRequest } from '../../core/api/generated/model/registro-asistencia-item-request';
import { PeriodoAcademicoResponse } from '../../core/api/generated/model/periodo-academico-response';
import { GrupoResponse } from '../../core/api/generated/model/grupo-response';
import { MatriculaResponse } from '../../core/api/generated/model/matricula-response';
import { SesionAsistenciaResponse } from '../../core/api/generated/model/sesion-asistencia-response';

interface FilaPlanilla {
  matriculaId: number;
  estudianteNombre: string;
  estado: EstadoAsistencia;
  minutosRetardo: number;
  observaciones?: string;
}

@Component({
  selector: 'app-asistencia',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <div class="asistencia-container">
      <header class="header">
        <div>
          <h2>Control de Asistencia Escolar y Ausentismo</h2>
          <p class="subtitle">Toma diaria de lista, detección de evasión interna y excusas con reserva médica (UC-19, UC-PROV-18..19)</p>
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
          📋 Toma de Asistencia / Planilla
        </button>
        <button
          type="button"
          class="tab-btn"
          [class.active]="tabActiva() === 'excusas'"
          (click)="tabActiva.set('excusas')"
        >
          🩺 Radicación de Excusas
        </button>
      </div>

      <!-- TAB 1: PLANILLA DE ASISTENCIA -->
      @if (tabActiva() === 'planilla') {
        <div class="tab-content">
          <div class="filtro-panel">
            <div class="form-row">
              <div class="form-col">
                <label for="pPeriodo">Periodo:</label>
                <select id="pPeriodo" [value]="periodoSeleccionadoId()" (change)="onPeriodoChange($event)" class="form-control">
                  @for (p of periodos(); track p.id) {
                    <option [value]="p.id">{{ p.nombre }}</option>
                  }
                </select>
              </div>
              <div class="form-col">
                <label for="pGrupo">Grupo:</label>
                <select id="pGrupo" [value]="grupoSeleccionadoId()" (change)="onGrupoChange($event)" class="form-control">
                  @for (g of grupos(); track g.id) {
                    <option [value]="g.id">{{ g.nombre }}</option>
                  }
                </select>
              </div>
              <div class="form-col">
                <label for="pFecha">Fecha de la Sesión:</label>
                <input id="pFecha" type="date" [value]="fechaSesion()" (input)="onFechaChange($event)" class="form-control" />
              </div>
              <div class="form-col btn-align">
                <button type="button" class="btn-primary" (click)="iniciarSesionPlanilla()">
                  Abrir / Cargar Planilla
                </button>
              </div>
            </div>
          </div>

          @if (sesionActiva()) {
            <div class="alerta-sesion">
              <span>Sesión activa para el <strong>{{ sesionActiva()?.fecha }}</strong>. Total alumnos: <strong>{{ filasPlanilla().length }}</strong></span>
              <button type="button" [disabled]="guardandoPlanilla()" class="btn-primary btn-save" (click)="guardarPlanilla()">
                @if (guardandoPlanilla()) {
                  <span>Guardando...</span>
                } @else {
                  <span>💾 Guardar Planilla por Lote</span>
                }
              </button>
            </div>

            <!-- TABLA DE ALUMNOS EN PLANILLA -->
            <div class="table-card">
              <table>
                <thead>
                  <tr>
                    <th>Estudiante</th>
                    <th>Estado de Asistencia</th>
                    <th>Alerta de Custodia</th>
                    <th>Observaciones</th>
                  </tr>
                </thead>
                <tbody>
                  @for (fila of filasPlanilla(); track fila.matriculaId) {
                    <tr [class.row-evasion]="fila.estado === EstadoAsistencia.EvasionInternaClase">
                      <td><strong>{{ fila.estudianteNombre }}</strong></td>
                      <td>
                        <select
                          [value]="fila.estado"
                          (change)="onEstadoFilaChange(fila, $event)"
                          class="form-control select-estado"
                        >
                          <option [value]="EstadoAsistencia.Presente">Presente</option>
                          <option [value]="EstadoAsistencia.AusenteInjustificada">Ausencia Injustificada</option>
                          <option [value]="EstadoAsistencia.AusenteJustificada">Ausencia Justificada</option>
                          <option [value]="EstadoAsistencia.Retardo">Retardo</option>
                          <option [value]="EstadoAsistencia.EvasionInternaClase">⚠️ Evasión Interna</option>
                        </select>
                      </td>
                      <td>
                        @if (fila.estado === EstadoAsistencia.EvasionInternaClase) {
                          <span class="badge badge-evasion">
                            ALERTA CUSTODIA (Ley 1098)
                          </span>
                        } @else {
                          <span class="text-muted">Normal</span>
                        }
                      </td>
                      <td>
                        <input
                          type="text"
                          [value]="fila.observaciones || ''"
                          (input)="onObservacionFilaChange(fila, $event)"
                          placeholder="Nota u observación..."
                          class="form-control input-obs"
                        />
                      </td>
                    </tr>
                  }
                </tbody>
              </table>
            </div>
          }
        </div>
      }

      <!-- TAB 2: RADICACIÓN DE EXCUSAS -->
      @if (tabActiva() === 'excusas') {
        <div class="tab-content">
          <form [formGroup]="formExcusa" (ngSubmit)="radicarExcusa()" class="form-card">
            <h3>Radicación de Excusa Médica o Justificada (UC-PROV-18)</h3>
            <p class="card-desc">
              Aplica justificación por rango de fechas [Inicio, Fin] y propaga automáticamente el derecho a evaluación diferida en el SIEE.
            </p>

            <div class="form-row">
              <div class="form-col">
                <label for="eMatricula">ID de Matrícula del Alumno: *</label>
                <input id="eMatricula" type="number" formControlName="matriculaId" placeholder="ID matrícula" class="form-control" />
              </div>
              <div class="form-col">
                <label for="eTipo">Tipo de Justificación: *</label>
                <select id="eTipo" formControlName="tipoJustificacion" class="form-control">
                  <option [value]="TipoJustificacionAsistencia.IncapacidadMedicaEps">Médica / Incapacidad EPS</option>
                  <option [value]="TipoJustificacionAsistencia.CalamidadDomestica">Calamidad Doméstica</option>
                  <option [value]="TipoJustificacionAsistencia.FuerzaMayor">Fuerza Mayor / Caso Fortuito</option>
                </select>
              </div>
            </div>

            <div class="form-row">
              <div class="form-col">
                <label for="eFechaInicio">Fecha Inicio: *</label>
                <input id="eFechaInicio" type="date" formControlName="fechaInicio" class="form-control" />
              </div>
              <div class="form-col">
                <label for="eFechaFin">Fecha Fin: *</label>
                <input id="eFechaFin" type="date" formControlName="fechaFin" class="form-control" />
              </div>
            </div>

            <div class="form-row">
              <div class="form-col">
                <label for="eMotivo">Motivo / Explicación de la Inasistencia: *</label>
                <textarea id="eMotivo" formControlName="motivoExplicacion" rows="3" class="form-control"></textarea>
              </div>
            </div>

            <div class="reserva-salud-box">
              <div class="checkbox-inline">
                <input id="eReserva" type="checkbox" formControlName="esDatoSaludSensible" />
                <label for="eReserva">
                  <strong>Contiene información clínica sensible de salud (Reserva Legal Ley 1581 de 2012)</strong>
                </label>
              </div>
            </div>

            <div class="form-actions">
              <button type="submit" [disabled]="formExcusa.invalid || guardandoExcusa()" class="btn-primary">
                @if (guardandoExcusa()) {
                  <span>Radicando Excusa...</span>
                } @else {
                  <span>Radicar Excusa Formal</span>
                }
              </button>
            </div>
          </form>
        </div>
      }
    </div>
  `,
  styles: [`
    .asistencia-container {
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
    .alerta-sesion {
      display: flex;
      justify-content: space-between;
      align-items: center;
      background: #eff6ff;
      border: 1px solid #bfdbfe;
      border-radius: 6px;
      padding: 0.75rem 1rem;
      margin-bottom: 1rem;
      font-size: 0.9rem;
      color: #1e40af;
    }
    .btn-save { padding: 0.5rem 1rem; }
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
    .select-estado { min-width: 180px; }
    .input-obs { width: 100%; }
    .row-evasion { background: #fef2f2 !important; }
    .badge { padding: 0.2rem 0.5rem; border-radius: 4px; font-size: 0.75rem; font-weight: 600; }
    .badge-evasion { background: #ef4444; color: white; animation: pulse 1.5s infinite; }
    @keyframes pulse {
      0%, 100% { opacity: 1; }
      50% { opacity: 0.7; }
    }
    .text-muted { color: #94a3b8; font-size: 0.8rem; }
    .form-card {
      background: white;
      border: 1px solid #e2e8f0;
      border-radius: 8px;
      padding: 1.5rem;
      box-shadow: 0 1px 3px rgba(0,0,0,0.05);
    }
    h3 { margin-top: 0; color: #1e293b; font-size: 1.1rem; }
    .card-desc { font-size: 0.85rem; color: #64748b; margin-bottom: 1.25rem; }
    .reserva-salud-box {
      background: #fffbeb;
      border: 1px solid #fde68a;
      border-radius: 6px;
      padding: 0.85rem;
      margin: 1.25rem 0;
    }
    .checkbox-inline { display: flex; align-items: center; gap: 0.5rem; font-size: 0.85rem; color: #92400e; }
    .form-actions { display: flex; justify-content: flex-end; margin-top: 1rem; }
    .btn-primary { background: #2563eb; color: white; border: none; padding: 0.55rem 1.15rem; border-radius: 4px; font-weight: 600; font-size: 0.88rem; cursor: pointer; }
    .btn-primary:disabled { background: #93c5fd; cursor: not-allowed; }
  `]
})
export class AsistenciaComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly asistenciaService = inject(ControlDeAsistenciaService);
  private readonly periodosService = inject(EstructuraAcadmicaPeriodosService);
  private readonly gruposService = inject(EstructuraAcadmicaGruposService);
  private readonly matriculasService = inject(MatrculasService);
  private readonly notificationService = inject(NotificationService);

  readonly tabActiva = signal<'planilla' | 'excusas'>('planilla');
  readonly guardandoPlanilla = signal(false);
  readonly guardandoExcusa = signal(false);

  readonly periodos = signal<PeriodoAcademicoResponse[]>([]);
  readonly grupos = signal<GrupoResponse[]>([]);
  readonly sesionActiva = signal<SesionAsistenciaResponse | null>(null);
  readonly filasPlanilla = signal<FilaPlanilla[]>([]);

  readonly periodoSeleccionadoId = signal<number>(1);
  readonly grupoSeleccionadoId = signal<number>(10);
  readonly fechaSesion = signal<string>(new Date().toISOString().substring(0, 10));

  readonly EstadoAsistencia = EstadoAsistencia;
  readonly TipoJustificacionAsistencia = TipoJustificacionAsistencia;

  readonly formExcusa = this.fb.group({
    matriculaId: [null as number | null, [Validators.required]],
    tipoJustificacion: [TipoJustificacionAsistencia.IncapacidadMedicaEps, [Validators.required]],
    fechaInicio: [new Date().toISOString().substring(0, 10), [Validators.required]],
    fechaFin: [new Date().toISOString().substring(0, 10), [Validators.required]],
    motivoExplicacion: ['', [Validators.required]],
    esDatoSaludSensible: [true]
  });

  ngOnInit(): void {
    this.cargarCatalogos();
  }

  cargarCatalogos(): void {
    this.periodosService.apiAniosLectivosAnioIdPeriodosGet(1).subscribe({
      next: (pers) => {
        this.periodos.set(pers);
        if (pers.length > 0) this.periodoSeleccionadoId.set(pers[0].id!);
      }
    });

    this.gruposService.apiGruposGet(1).subscribe({
      next: (grps) => {
        this.grupos.set(grps);
        if (grps.length > 0) this.grupoSeleccionadoId.set(grps[0].id!);
      }
    });
  }

  onPeriodoChange(event: Event): void {
    this.periodoSeleccionadoId.set(Number((event.target as HTMLSelectElement).value));
  }

  onGrupoChange(event: Event): void {
    this.grupoSeleccionadoId.set(Number((event.target as HTMLSelectElement).value));
  }

  onFechaChange(event: Event): void {
    this.fechaSesion.set((event.target as HTMLInputElement).value);
  }

  iniciarSesionPlanilla(): void {
    const periodoId = this.periodoSeleccionadoId();
    const grupoId = this.grupoSeleccionadoId();
    const fecha = this.fechaSesion();

    const req = {
      periodoId,
      grupoId,
      fecha,
      horaInicio: '07:00:00'
    };

    this.asistenciaService.crearSesionAsistencia(req as any).subscribe({
      next: (sesion) => {
        this.sesionActiva.set(sesion);
        this.cargarAlumnosParaPlanilla(grupoId);
        this.notificationService.showSuccess(`Planilla abierta para el ${sesion.fecha}.`);
      }
    });
  }

  cargarAlumnosParaPlanilla(grupoId: number): void {
    this.matriculasService.listarMatriculas(grupoId).subscribe({
      next: (matriculas) => {
        const filas: FilaPlanilla[] = matriculas.map(m => ({
          matriculaId: m.id!,
          estudianteNombre: m.estudianteNombreCompleto || `Estudiante MAT-${m.id}`,
          estado: EstadoAsistencia.Presente,
          minutosRetardo: 0
        }));
        this.filasPlanilla.set(filas);
      }
    });
  }

  onEstadoFilaChange(fila: FilaPlanilla, event: Event): void {
    const nuevoEstado = (event.target as HTMLSelectElement).value as EstadoAsistencia;
    fila.estado = nuevoEstado;

    if (nuevoEstado === EstadoAsistencia.EvasionInternaClase) {
      this.notificationService.showWarning(
        `Evasión interna registrada para ${fila.estudianteNombre}. Se activa protocolo inmediato de custodia (Ley 1098).`,
        'Alerta de Custodia'
      );
    }
  }

  onObservacionFilaChange(fila: FilaPlanilla, event: Event): void {
    fila.observaciones = (event.target as HTMLInputElement).value;
  }

  guardarPlanilla(): void {
    const sesion = this.sesionActiva();
    if (!sesion || !sesion.id) return;

    this.guardandoPlanilla.set(true);
    const req = {
      sesionId: sesion.id,
      registros: this.filasPlanilla().map(f => ({
        matriculaId: f.matriculaId,
        estado: f.estado,
        minutosRetardo: f.minutosRetardo,
        observaciones: f.observaciones
      }))
    };

    this.asistenciaService.registrarPlanillaAsistenciaLote(req as any).subscribe({
      next: () => {
        this.guardandoPlanilla.set(false);
        this.notificationService.showSuccess(
          'Planilla de asistencia registrada exitosamente por lote.',
          'Asistencia Guardada'
        );
      },
      error: () => {
        this.guardandoPlanilla.set(false);
      }
    });
  }

  radicarExcusa(): void {
    if (this.formExcusa.invalid) return;

    this.guardandoExcusa.set(true);
    this.asistenciaService.radicarExcusa(this.formExcusa.getRawValue() as any).subscribe({
      next: (resp) => {
        this.guardandoExcusa.set(false);
        this.notificationService.showSuccess(
          `Excusa radicada exitosamente con radicado ${resp.numeroRadicado || resp.id}. Justificación aplicada.`,
          'Excusa Registrada'
        );
        this.formExcusa.reset({
          matriculaId: null,
          tipoJustificacion: TipoJustificacionAsistencia.IncapacidadMedicaEps,
          fechaInicio: new Date().toISOString().substring(0, 10),
          fechaFin: new Date().toISOString().substring(0, 10),
          motivoExplicacion: '',
          esDatoSaludSensible: true
        });
      },
      error: () => {
        this.guardandoExcusa.set(false);
      }
    });
  }
}
