import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { HorariosEscolaresService } from '../../core/api/generated/api/horarios-escolares.service';
import { AuthService } from '../../core/services/auth.service';
import { NotificationService } from '../../core/services/notification.service';
import { AulaResponse } from '../../core/api/generated/model/aula-response';
import { TipoEspacioFisico } from '../../core/api/generated/model/tipo-espacio-fisico';
import { EstadoAula } from '../../core/api/generated/model/estado-aula';
import { AgendaDocenteResponse } from '../../core/api/generated/model/agenda-docente-response';
import { UbicacionActualResponse } from '../../core/api/generated/model/ubicacion-actual-response';
import { Rol } from '../../core/api/generated/model/rol';

@Component({
  selector: 'app-horarios',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <div class="horarios-container">
      <header class="header">
        <div>
          <h2>Horarios Escolares, Aulas y Localizador Físico</h2>
          <p class="subtitle">Catálogo de espacios físicos, validación de Triple No-Colisión y agenda institucional (UC-04, UC-PROV-20..22)</p>
        </div>
      </header>

      <!-- TABS -->
      <div class="tabs-bar">
        <button
          type="button"
          class="tab-btn"
          [class.active]="tabActiva() === 'aulas'"
          (click)="tabActiva.set('aulas')"
        >
          🚪 Catálogo de Aulas y Espacios
        </button>
        <button
          type="button"
          class="tab-btn"
          [class.active]="tabActiva() === 'agenda'"
          (click)="tabActiva.set('agenda')"
        >
          📅 Agenda y Localizador Físico
        </button>
      </div>

      <!-- TAB 1: AULAS -->
      @if (tabActiva() === 'aulas') {
        <div class="tab-content">
          <div class="content-toolbar">
            <h3>Espacios Físicos Institucionales</h3>
            <button type="button" class="btn-primary" (click)="mostrarFormAula.set(true)">
              + Registrar Nueva Aula
            </button>
          </div>

          @if (mostrarFormAula()) {
            <form [formGroup]="formAula" (ngSubmit)="guardarAula()" class="form-card">
              <h4>Registrar Espacio Físico / Aula</h4>

              <div class="form-row">
                <div class="form-col">
                  <label for="aCodigo">Código Único: *</label>
                  <input id="aCodigo" type="text" formControlName="codigo" placeholder="Ej. A-101" class="form-control" />
                </div>
                <div class="form-col">
                  <label for="aNombre">Nombre del Aula: *</label>
                  <input id="aNombre" type="text" formControlName="nombre" placeholder="Ej. Aula 101 - Bloque Central" class="form-control" />
                </div>
                <div class="form-col">
                  <label for="aTipo">Tipo de Espacio: *</label>
                  <select id="aTipo" formControlName="tipoEspacio" class="form-control">
                    <option [value]="TipoEspacioFisico.AulaEstandar">Aula Estándar</option>
                    <option [value]="TipoEspacioFisico.LaboratorioCiencias">Laboratorio de Ciencias</option>
                    <option [value]="TipoEspacioFisico.SalaInformatica">Sala de Informática</option>
                    <option [value]="TipoEspacioFisico.EscenarioDeportivo">Escenario Deportivo</option>
                    <option [value]="TipoEspacioFisico.Auditorio">Auditorio</option>
                  </select>
                </div>
              </div>

              <div class="form-row">
                <div class="form-col">
                  <label for="aCapacidad">Capacidad Máxima de Alumnos: *</label>
                  <input id="aCapacidad" type="number" formControlName="capacidadMaxima" placeholder="Ej. 35" class="form-control" />
                </div>
                <div class="form-col">
                  <label for="aSede">Sede:</label>
                  <input id="aSede" type="text" formControlName="sede" placeholder="Ej. Sede Principal" class="form-control" />
                </div>
              </div>

              <div class="form-actions">
                <button type="button" class="btn-secondary" (click)="mostrarFormAula.set(false)">Cancelar</button>
                <button type="submit" [disabled]="formAula.invalid || guardandoAula()" class="btn-primary">
                  @if (guardandoAula()) {
                    <span>Guardando Aula...</span>
                  } @else {
                    <span>Guardar Espacio Físico</span>
                  }
                </button>
              </div>
            </form>
          }

          <div class="table-card">
            <table>
              <thead>
                <tr>
                  <th>Código</th>
                  <th>Nombre del Espacio</th>
                  <th>Tipo de Espacio</th>
                  <th>Sede</th>
                  <th>Aforo Máximo</th>
                  <th>Estado</th>
                </tr>
              </thead>
              <tbody>
                @for (aula of aulas(); track aula.id) {
                  <tr>
                    <td><code>{{ aula.codigo }}</code></td>
                    <td><strong>{{ aula.nombre }}</strong></td>
                    <td><span class="chip-tipo">{{ aula.tipoEspacio }}</span></td>
                    <td>{{ aula.sede || 'Principal' }}</td>
                    <td><span class="weight-chip">{{ aula.capacidadMaxima }} estudiantes</span></td>
                    <td>
                      <span class="badge" [class.badge-active]="aula.estado === 'DISPONIBLE'">
                        {{ aula.estado }}
                      </span>
                    </td>
                  </tr>
                }
              </tbody>
            </table>
          </div>
        </div>
      }

      <!-- TAB 2: AGENDA Y LOCALIZADOR -->
      @if (tabActiva() === 'agenda') {
        <div class="tab-content">
          <div class="cards-grid">
            <!-- LOCALIZADOR EN TIEMPO REAL -->
            <div class="feature-card">
              <h3>Localizador Físico en Tiempo Real (Portería y Emergencias)</h3>
              <p class="card-desc">
                Permite a portería y coordinación conocer la ubicación espacial inmediata de un estudiante o grupo (UC-PROV-22 A1).
              </p>

              <div class="form-row">
                <div class="form-col">
                  <label for="locTipo">Tipo de Búsqueda:</label>
                  <select id="locTipo" [value]="tipoBusquedaLocalizador()" (change)="onTipoBusquedaChange($event)" class="form-control">
                    <option value="GRUPO">Grupo / Curso</option>
                    <option value="ESTUDIANTE">Estudiante</option>
                  </select>
                </div>
                <div class="form-col">
                  <label for="locId">ID del Recurso:</label>
                  <input id="locId" type="number" [value]="idRecursoLocalizador()" (input)="onIdRecursoChange($event)" placeholder="Ej. 1" class="form-control" />
                </div>
              </div>

              <button type="button" class="btn-primary" (click)="localizarUbicacion()">
                Localizar en Tiempo Real
              </button>

              @if (ubicacionActual()) {
                <div class="ubicacion-result">
                  <div class="res-item">
                    <span class="res-label">Espacio Físico Actual:</span>
                    <strong class="res-val text-primary">{{ ubicacionActual()?.aulaNombre }} ({{ ubicacionActual()?.aulaCodigo }})</strong>
                  </div>
                  <div class="res-item">
                    <span class="res-label">Actividad / Asignatura:</span>
                    <span class="res-val">{{ ubicacionActual()?.asignaturaNombre || 'Sesión Lectiva' }}</span>
                  </div>
                  <div class="res-item">
                    <span class="res-label">Docente a Cargo:</span>
                    <span class="res-val">{{ ubicacionActual()?.profesorNombre || 'Sin asignar' }}</span>
                  </div>
                </div>
              }
            </div>

            <!-- MI AGENDA DOCENTE -->
            <div class="feature-card">
              <h3>Agenda y Compromisos Lectivos Semanales</h3>
              <p class="card-desc">
                Horario estructurado con garantía de Triple No-Colisión: Cero solapamiento de docente, grupo o aula física (RN-HOR-01).
              </p>

              @if (esProfesor()) {
                <button type="button" class="btn-secondary" (click)="consultarAgenda()">
                  Cargar Mi Agenda Semanal
                </button>
              } @else {
                <div class="rol-notice">
                  <p>ℹ️ La agenda individual está reservada para usuarios con rol <strong>Docente / Profesor</strong>.</p>
                  <p class="rol-subnotice">Rol activo: <code>{{ authService.rolActivo() || 'Administrativo' }}</code></p>
                </div>
              }

              @if (agendaDocente()) {
                <div class="agenda-resumen">
                  <p><strong>Docente:</strong> {{ agendaDocente()?.profesorNombreCompleto }}</p>
                  <p><strong>Bloques Semanales:</strong> {{ agendaDocente()?.totalBloquesSemanales }} bloques</p>
                  <p><strong>Sesiones Registradas:</strong> {{ agendaDocente()?.bloques?.length || 0 }} sesiones</p>
                </div>
              }
            </div>
          </div>
        </div>
      }
    </div>
  `,
  styles: [`
    .horarios-container {
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
    .form-card {
      background: white;
      border: 1px solid #e2e8f0;
      border-radius: 8px;
      padding: 1.5rem;
      box-shadow: 0 1px 3px rgba(0,0,0,0.05);
      margin-bottom: 1.5rem;
    }
    h3 { margin-top: 0; color: #1e293b; font-size: 1.1rem; }
    h4 { margin: 0 0 1rem; color: #1e40af; font-size: 1rem; }
    .form-row { display: flex; gap: 1rem; margin-bottom: 1rem; }
    .form-col { flex: 1; display: flex; flex-direction: column; }
    label { font-size: 0.85rem; font-weight: 500; margin-bottom: 0.25rem; color: #475569; }
    .form-control { padding: 0.55rem 0.75rem; border: 1px solid #cbd5e1; border-radius: 4px; font-size: 0.9rem; }
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
    .chip-tipo { background: #f1f5f9; color: #475569; padding: 0.2rem 0.5rem; border-radius: 4px; font-size: 0.78rem; font-weight: 500; }
    .weight-chip { background: #f8fafc; padding: 0.15rem 0.45rem; border-radius: 4px; font-size: 0.8rem; font-weight: 600; color: #0284c7; }
    .badge { padding: 0.2rem 0.5rem; border-radius: 4px; font-size: 0.75rem; font-weight: 600; background: #e2e8f0; color: #475569; }
    .badge-active { background: #dcfce7; color: #166534; }
    .btn-primary { background: #2563eb; color: white; border: none; padding: 0.55rem 1.15rem; border-radius: 4px; font-weight: 600; font-size: 0.88rem; cursor: pointer; }
    .btn-secondary { background: #e2e8f0; color: #334155; border: none; padding: 0.55rem 1rem; border-radius: 4px; font-size: 0.88rem; cursor: pointer; font-weight: 500; }
    .cards-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 1.5rem; }
    .feature-card { background: white; border: 1px solid #e2e8f0; border-radius: 8px; padding: 1.5rem; box-shadow: 0 1px 3px rgba(0,0,0,0.05); }
    .card-desc { font-size: 0.85rem; color: #64748b; line-height: 1.4; margin-bottom: 1.25rem; }
    .ubicacion-result { margin-top: 1.25rem; background: #f0fdf4; border: 1px solid #bbf7d0; border-radius: 6px; padding: 1rem; display: flex; flex-direction: column; gap: 0.5rem; }
    .res-item { display: flex; justify-content: space-between; font-size: 0.85rem; }
    .res-label { color: #166534; }
    .res-val { color: #1e293b; font-weight: 600; }
    .text-primary { color: #2563eb; }
    .agenda-resumen { margin-top: 1rem; background: #f8fafc; padding: 0.85rem; border-radius: 6px; font-size: 0.85rem; line-height: 1.5; color: #334155; }
    .rol-notice { margin-top: 0.75rem; background: #f8fafc; border: 1px solid #e2e8f0; padding: 0.75rem; border-radius: 6px; font-size: 0.85rem; color: #475569; }
    .rol-notice p { margin: 0; }
    .rol-subnotice { margin-top: 0.25rem !important; font-size: 0.8rem; color: #64748b; }
  `]
})
export class HorariosComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly horariosService = inject(HorariosEscolaresService);
  readonly authService = inject(AuthService);
  private readonly notificationService = inject(NotificationService);

  readonly tabActiva = signal<'aulas' | 'agenda'>('aulas');
  readonly guardandoAula = signal(false);

  readonly esProfesor = computed(() => this.authService.rolActivo() === Rol.Profesor);

  readonly aulas = signal<AulaResponse[]>([]);
  readonly agendaDocente = signal<AgendaDocenteResponse | null>(null);
  readonly ubicacionActual = signal<UbicacionActualResponse | null>(null);

  readonly mostrarFormAula = signal(false);

  readonly tipoBusquedaLocalizador = signal<string>('GRUPO');
  readonly idRecursoLocalizador = signal<number>(1);

  readonly TipoEspacioFisico = TipoEspacioFisico;

  readonly formAula = this.fb.group({
    codigo: ['', [Validators.required]],
    nombre: ['', [Validators.required]],
    tipoEspacio: [TipoEspacioFisico.AulaEstandar, [Validators.required]],
    capacidadMaxima: [35, [Validators.required, Validators.min(1)]],
    sede: ['Sede Principal']
  });

  ngOnInit(): void {
    this.cargarAulas();
  }

  cargarAulas(): void {
    this.horariosService.listarAulas().subscribe({
      next: (data) => {
        this.aulas.set(data);
      }
    });
  }

  guardarAula(): void {
    if (this.formAula.invalid) return;

    this.guardandoAula.set(true);
    const req = {
      ...this.formAula.getRawValue(),
      estado: EstadoAula.Disponible
    };

    this.horariosService.crearAula(req as any).subscribe({
      next: (aula) => {
        this.guardandoAula.set(false);
        this.notificationService.showSuccess(
          `Aula ${aula.nombre} (${aula.codigo}) registrada con capacidad de ${aula.capacidadMaxima} alumnos.`,
          'Aula Registrada'
        );
        this.mostrarFormAula.set(false);
        this.cargarAulas();
      },
      error: () => {
        this.guardandoAula.set(false);
      }
    });
  }

  onTipoBusquedaChange(event: Event): void {
    this.tipoBusquedaLocalizador.set((event.target as HTMLSelectElement).value);
  }

  onIdRecursoChange(event: Event): void {
    this.idRecursoLocalizador.set(Number((event.target as HTMLInputElement).value));
  }

  localizarUbicacion(): void {
    const tipo = this.tipoBusquedaLocalizador();
    const id = this.idRecursoLocalizador();

    this.horariosService.consultarUbicacionActual(tipo, id).subscribe({
      next: (ub) => {
        this.ubicacionActual.set(ub);
        this.notificationService.showInfo(
          `Ubicación localizada: ${ub.aulaNombre || 'Aula base'}`,
          'Localizador en Tiempo Real'
        );
      }
    });
  }

  consultarAgenda(): void {
    if (!this.esProfesor()) {
      this.notificationService.showWarning(
        'La agenda lectiva individual requiere rol Docente / Profesor.',
        'Perfil No Autorizado'
      );
      return;
    }
    this.horariosService.consultarMiAgendaDocente().subscribe({
      next: (ag) => {
        this.agendaDocente.set(ag);
        this.notificationService.showInfo('Agenda semanal docente cargada.', 'Horario Docente');
      }
    });
  }
}
