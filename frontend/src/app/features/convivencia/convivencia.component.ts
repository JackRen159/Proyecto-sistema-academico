import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ConvivenciaEscolarService } from '../../core/api/generated/api/convivencia-escolar.service';
import { EstudiantesService } from '../../core/api/generated/api/estudiantes.service';
import { NotificationService } from '../../core/services/notification.service';
import { AnotacionObservadorResponse } from '../../core/api/generated/model/anotacion-observador-response';
import {
  AnotacionObservadorRequestTipoAnotacionEnum,
  AnotacionObservadorRequestTipoSituacionSugeridaEnum
} from '../../core/api/generated/model/anotacion-observador-request';
import { CasoConvivenciaRequestTipoSituacionEnum } from '../../core/api/generated/model/caso-convivencia-request';
import { PagedEstudianteResponse } from '../../core/api/generated/model/paged-estudiante-response';

@Component({
  selector: 'app-convivencia',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <div class="convivencia-container">
      <header class="header">
        <div>
          <h2>Convivencia Escolar y Ruta de Atención Integral</h2>
          <p class="subtitle">Observador inmutable, Ruta Tipo I-III, debido proceso y Plan de Continuidad Académica (UC-22..25, Ley 1620 / Dec. 1965 / T-004 de 2026)</p>
        </div>
      </header>

      <!-- TABS -->
      <div class="tabs-bar">
        <button
          type="button"
          class="tab-btn"
          [class.active]="tabActiva() === 'observador'"
          (click)="tabActiva.set('observador')"
        >
          📖 Observador del Estudiante
        </button>
        <button
          type="button"
          class="tab-btn"
          [class.active]="tabActiva() === 'ruta'"
          (click)="tabActiva.set('ruta')"
        >
          🚨 Ruta de Atención Integral (Tipo I, II, III)
        </button>
        <button
          type="button"
          class="tab-btn"
          [class.active]="tabActiva() === 'garantias'"
          (click)="tabActiva.set('garantias')"
        >
          ⚖️ Debido Proceso y Garantías Constitucionales
        </button>
      </div>

      <!-- TAB 1: OBSERVADOR DEL ESTUDIANTE -->
      @if (tabActiva() === 'observador') {
        <div class="tab-content">
          <div class="content-toolbar">
            <div class="selector-row">
              <label for="selEstudiante">ID del Estudiante:</label>
              <input
                id="selEstudiante"
                type="number"
                [value]="estudianteSeleccionadoId()"
                (input)="onEstudianteIdChange($event)"
                placeholder="ID estudiante"
                class="form-control inline-input"
              />
              <button type="button" class="btn-primary" (click)="cargarAnotaciones()">
                Cargar Observador
              </button>
            </div>

            <button type="button" class="btn-primary" (click)="mostrarFormAnotacion.set(true)">
              + Nueva Anotación en Observador
            </button>
          </div>

          <!-- FORMULARIO NUEVA ANOTACIÓN INMUTABLE (RN-CON-01) -->
          @if (mostrarFormAnotacion()) {
            <form [formGroup]="formAnotacion" (ngSubmit)="guardarAnotacion()" class="form-card">
              <h4>Registrar Anotación en el Observador (Inmutable — RN-CON-01)</h4>
              <p class="card-desc">
                Conforme al principio de inmutabilidad y debido proceso: Una vez registrada, la anotación no puede modificarse ni eliminarse in-situ; solo admite adendas bilaterales.
              </p>

              <div class="form-row">
                <div class="form-col">
                  <label for="anoTipo">Tipo de Anotación: *</label>
                  <select id="anoTipo" formControlName="tipoAnotacion" class="form-control">
                    <option [value]="AnotacionTipo.SeguimientoFormativo">Seguimiento Formativo</option>
                    <option [value]="AnotacionTipo.PositivaReconocimiento">Positiva / Reconocimiento</option>
                    <option [value]="AnotacionTipo.ReporteSituacionConvivencia">Reporte de Situación de Convivencia</option>
                  </select>
                </div>
                <div class="form-col">
                  <label for="anoFecha">Fecha de los Hechos: *</label>
                  <input id="anoFecha" type="date" formControlName="fechaHecho" class="form-control" />
                </div>
                <div class="form-col">
                  <label for="anoLugar">Lugar del Hecho: *</label>
                  <input id="anoLugar" type="text" formControlName="lugarHecho" placeholder="Ej. Aula 101, Patio recreo" class="form-control" />
                </div>
              </div>

              <div class="form-row">
                <div class="form-col">
                  <label for="anoDescripcion">Relato Fáctico Objetivo (Sin juicios de valor punitivos): *</label>
                  <textarea id="anoDescripcion" formControlName="descripcionFactica" rows="3" class="form-control"></textarea>
                </div>
              </div>

              <div class="form-row">
                <div class="form-col">
                  <label for="anoAcciones">Acciones Pedagógicas y Compromisos Acordados:</label>
                  <textarea id="anoAcciones" formControlName="accionesPedagogicas" rows="2" class="form-control"></textarea>
                </div>
              </div>

              <div class="form-actions">
                <button type="button" class="btn-secondary" (click)="mostrarFormAnotacion.set(false)">Cancelar</button>
                <button type="submit" [disabled]="formAnotacion.invalid || guardandoAnotacion()" class="btn-primary">
                  @if (guardandoAnotacion()) {
                    <span>Registrando Anotación...</span>
                  } @else {
                    <span>Registrar en Observador</span>
                  }
                </button>
              </div>
            </form>
          }

          <!-- LISTA DE ANOTACIONES INMUTABLES -->
          <div class="anotaciones-list">
            @if (anotaciones().length === 0) {
              <div class="empty-state">
                <p>No se registran anotaciones para este estudiante. Ingrese un ID válido y presione Cargar.</p>
              </div>
            }
            @for (an of anotaciones(); track an.id) {
              <div class="anotacion-card" [class.card-positiva]="an.tipoAnotacion === 'POSITIVA_RECONOCIMIENTO'">
                <div class="an-header">
                  <div>
                    <span class="badge" [ngClass]="an.tipoAnotacion === 'POSITIVA_RECONOCIMIENTO' ? 'badge-success' : 'badge-warning'">
                      {{ an.tipoAnotacion }}
                    </span>
                    <strong class="an-lugar">Lugar: {{ an.lugarHecho }}</strong>
                  </div>
                  <span class="an-fecha">{{ an.fechaHecho }} {{ an.horaHecho || '' }}</span>
                </div>

                <div class="an-body">
                  <p class="an-relato">{{ an.descripcionFactica }}</p>
                  @if (an.accionesPedagogicas) {
                    <div class="an-compromisos">
                      <strong>Acciones Pedagógicas:</strong> {{ an.accionesPedagogicas }}
                    </div>
                  }
                </div>

                <div class="an-footer">
                  <span>Registrado por: <strong>{{ an.nombreAutor || 'Docente' }}</strong></span>
                  <div class="an-status">
                    <span class="chip-status" [class.chip-ok]="!!an.versionLibreEstudiante">
                      {{ an.versionLibreEstudiante ? '✓ Versión del alumno registrada' : 'Pendiente versión del menor' }}
                    </span>
                    <span class="chip-status" [class.chip-ok]="an.enteradoAcudiente">
                      {{ an.enteradoAcudiente ? '✓ Acudiente enterado' : 'Pendiente enterado acudiente' }}
                    </span>
                  </div>
                </div>
              </div>
            }
          </div>
        </div>
      }

      <!-- TAB 2: RUTA DE ATENCIÓN INTEGRAL -->
      @if (tabActiva() === 'ruta') {
        <div class="tab-content">
          <form [formGroup]="formRuta" (ngSubmit)="abrirCasoRuta()" class="form-card">
            <h3>Activación de la Ruta de Atención Integral (Ley 1620 de 2013 / Dec. 1965)</h3>
            <p class="card-desc">
              Protocolo formativo no sancionatorio para atención, mediación y garantía de derechos de niños, niñas y adolescentes.
            </p>

            <div class="form-row">
              <div class="form-col">
                <label for="rEstudiante">ID Estudiante Implicado: *</label>
                <input id="rEstudiante" type="number" formControlName="estudianteImplicadoId" class="form-control" />
              </div>
              <div class="form-col">
                <label for="rTipo">Clasificación de la Situación: *</label>
                <select id="rTipo" formControlName="tipoSituacion" class="form-control">
                  <option [value]="SituacionTipo.TipoI">Situación Tipo I (Conflictos cotidianos / Mediación)</option>
                  <option [value]="SituacionTipo.TipoIi">Situación Tipo II (Acoso escolar / Daño sin incapacidad)</option>
                  <option [value]="SituacionTipo.TipoIii">Situación Tipo III (Presuntos delitos / Violencia sexual)</option>
                </select>
              </div>
            </div>

            <!-- ADVERTENCIA LEGAL OBLIGATORIA TIPO III -->
            @if (formRuta.get('tipoSituacion')?.value === SituacionTipo.TipoIii) {
              <div class="alert-tipo3">
                <strong>PROHIBICIÓN LEGAL ABSOLUTA (Decreto 1965 de 2013 Art. 44):</strong>
                En situaciones Tipo III está <strong>estrictamente prohibida la mediación o el careo</strong> entre la víctima y el presunto agresor. Se requiere remisión obligatoria e inmediata a Fiscalía General de la Nación, Policía de Infancia y Adolescencia o ICBF.
              </div>
            }

            <div class="form-row">
              <div class="form-col">
                <label for="rFecha">Fecha de los Hechos: *</label>
                <input id="rFecha" type="date" formControlName="fechaHechos" class="form-control" />
              </div>
              <div class="form-col">
                <label for="rLugar">Lugar del Evento: *</label>
                <input id="rLugar" type="text" formControlName="lugarHechos" placeholder="Ej. Baños Sede Principal" class="form-control" />
              </div>
            </div>

            <div class="form-row">
              <div class="form-col">
                <label for="rRelato">Descripción Detallada de la Situación: *</label>
                <textarea id="rRelato" formControlName="descripcionSituacion" rows="3" class="form-control"></textarea>
              </div>
            </div>

            <div class="checkbox-inline-wrapper">
              <div class="checkbox-inline">
                <input id="rUrgente" type="checkbox" formControlName="atencionMedicaUrgente" />
                <label for="rUrgente">Requiere atención médica urgente o primeros auxilios inmediatos</label>
              </div>
              <div class="checkbox-inline">
                <input id="rReserva" type="checkbox" formControlName="reservaProteccionContraAcudiente" />
                <label for="rReserva">Reserva de protección contra acudiente presunto agresor (Inhibe notificaciones a este adulto)</label>
              </div>
            </div>

            <div class="form-actions">
              <button type="submit" [disabled]="formRuta.invalid || abriendoCaso()" class="btn-primary">
                @if (abriendoCaso()) {
                  <span>Activando Ruta...</span>
                } @else {
                  <span>Activar Ruta de Atención Integral</span>
                }
              </button>
            </div>
          </form>
        </div>
      }

      <!-- TAB 3: DEBIDO PROCESO Y GARANTÍAS -->
      @if (tabActiva() === 'garantias') {
        <div class="tab-content">
          <div class="garantias-card">
            <h3>Garantías del Debido Proceso Disciplinario Escolar (Sentencia T-004 de 2026 / Art. 29 CP)</h3>

            <div class="principios-grid">
              <div class="principio-box">
                <span class="p-num">1</span>
                <h4>Cómputo en Días Hábiles Lectivos</h4>
                <p>Los términos para rendición de descargos se computan exclusivamente sobre días hábiles escolares según el calendario institucional, excluyendo festivos, recesos y fines de semana.</p>
              </div>
              <div class="principio-box">
                <span class="p-num">2</span>
                <h4>Plan de Continuidad Académica Obligatorio</h4>
                <p>Conforme a la Sentencia T-004 de 2026, ninguna medida formativa o suspensión puede privar al estudiante de su derecho a la educación ni aplicar ceros punitivos. Se activa obligatoriamente el Plan de Continuidad Académica coordinado con el SIEE.</p>
              </div>
              <div class="principio-box">
                <span class="p-num">3</span>
                <h4>Segregación de Instancias y Recursos</h4>
                <p>El estudiante cuenta con recurso de reposición y apelación con efecto suspensivo. La autoridad que profiere la medida en primera instancia no puede resolver la apelación.</p>
              </div>
              <div class="principio-box">
                <span class="p-num">4</span>
                <h4>Acompañamiento de Personería Estudiantil</h4>
                <p>El menor tiene derecho a ser asistido por su acudiente y por la Personería Estudiantil durante toda la diligencia de descargos y versiones libres.</p>
              </div>
            </div>
          </div>
        </div>
      }
    </div>
  `,
  styles: [`
    .convivencia-container { max-width: 1000px; margin: 0 auto; font-family: system-ui, -apple-system, sans-serif; }
    .header { margin-bottom: 1.5rem; }
    h2 { margin: 0; color: #1e293b; font-size: 1.4rem; }
    .subtitle { margin: 0.25rem 0 0; color: #64748b; font-size: 0.88rem; }
    .tabs-bar { display: flex; gap: 0.5rem; border-bottom: 2px solid #e2e8f0; margin-bottom: 1.5rem; }
    .tab-btn { padding: 0.65rem 1.25rem; border: none; background: none; font-size: 0.95rem; font-weight: 600; color: #64748b; cursor: pointer; border-bottom: 2px solid transparent; margin-bottom: -2px; }
    .tab-btn.active { color: #2563eb; border-bottom-color: #2563eb; }
    .content-toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.25rem; }
    .selector-row { display: flex; align-items: center; gap: 0.75rem; font-size: 0.88rem; font-weight: 500; color: #475569; }
    .inline-input { width: 140px; }
    .form-card, .garantias-card { background: white; border: 1px solid #e2e8f0; border-radius: 8px; padding: 1.5rem; box-shadow: 0 1px 3px rgba(0,0,0,0.05); margin-bottom: 1.5rem; }
    h3 { margin-top: 0; color: #1e293b; font-size: 1.15rem; }
    h4 { margin: 0 0 0.5rem; color: #1e40af; font-size: 1rem; }
    .card-desc { font-size: 0.85rem; color: #64748b; margin-bottom: 1.25rem; }
    .form-row { display: flex; gap: 1rem; margin-bottom: 1rem; }
    .form-col { flex: 1; display: flex; flex-direction: column; }
    label { font-size: 0.85rem; font-weight: 500; margin-bottom: 0.25rem; color: #475569; }
    .form-control { padding: 0.5rem 0.75rem; border: 1px solid #cbd5e1; border-radius: 4px; font-size: 0.9rem; }
    .form-actions { display: flex; justify-content: flex-end; gap: 0.5rem; margin-top: 1rem; }
    .btn-primary { background: #2563eb; color: white; border: none; padding: 0.55rem 1.15rem; border-radius: 4px; font-weight: 600; font-size: 0.88rem; cursor: pointer; }
    .btn-primary:disabled { background: #93c5fd; cursor: not-allowed; }
    .btn-secondary { background: #e2e8f0; color: #334155; border: none; padding: 0.5rem 1rem; border-radius: 4px; font-size: 0.88rem; cursor: pointer; font-weight: 500; }
    .alert-tipo3 { background: #fef2f2; border: 2px solid #ef4444; color: #991b1b; padding: 1rem; border-radius: 6px; margin-bottom: 1rem; font-size: 0.88rem; line-height: 1.45; }
    .checkbox-inline-wrapper { display: flex; flex-direction: column; gap: 0.5rem; margin: 1rem 0; font-size: 0.85rem; }
    .checkbox-inline { display: flex; align-items: center; gap: 0.5rem; }
    .anotaciones-list { display: flex; flex-direction: column; gap: 1rem; }
    .anotacion-card { background: white; border: 1px solid #e2e8f0; border-left: 5px solid #f59e0b; border-radius: 8px; padding: 1.25rem; box-shadow: 0 1px 3px rgba(0,0,0,0.05); }
    .card-positiva { border-left-color: #22c55e; }
    .an-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 0.75rem; }
    .an-lugar { margin-left: 0.75rem; font-size: 0.85rem; color: #475569; }
    .an-fecha { font-size: 0.82rem; color: #64748b; }
    .an-relato { font-size: 0.9rem; line-height: 1.5; color: #1e293b; margin: 0 0 0.75rem; }
    .an-compromisos { background: #f8fafc; padding: 0.65rem; border-radius: 4px; font-size: 0.85rem; color: #334155; }
    .an-footer { display: flex; justify-content: space-between; align-items: center; margin-top: 1rem; padding-top: 0.75rem; border-top: 1px solid #f1f5f9; font-size: 0.8rem; color: #64748b; }
    .an-status { display: flex; gap: 0.5rem; }
    .chip-status { background: #f1f5f9; padding: 0.2rem 0.5rem; border-radius: 4px; font-size: 0.75rem; color: #64748b; }
    .chip-ok { background: #dcfce7; color: #166534; font-weight: 600; }
    .badge { padding: 0.2rem 0.5rem; border-radius: 4px; font-size: 0.75rem; font-weight: 600; }
    .badge-success { background: #dcfce7; color: #166534; }
    .badge-warning { background: #fef9c3; color: #854d0e; }
    .empty-state { background: white; padding: 2rem; border-radius: 8px; text-align: center; color: #64748b; border: 1px solid #e2e8f0; }
    .principios-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 1.25rem; margin-top: 1.25rem; }
    .principio-box { background: #f8fafc; border: 1px solid #e2e8f0; border-radius: 8px; padding: 1.25rem; position: relative; }
    .p-num { position: absolute; top: 1rem; right: 1rem; width: 28px; height: 28px; background: #2563eb; color: white; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-weight: 700; font-size: 0.85rem; }
    .principio-box h4 { margin: 0 0 0.5rem; color: #1e293b; font-size: 0.95rem; }
    .principio-box p { margin: 0; font-size: 0.85rem; color: #475569; line-height: 1.45; }
  `]
})
export class ConvivenciaComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly convivenciaService = inject(ConvivenciaEscolarService);
  private readonly notificationService = inject(NotificationService);

  readonly tabActiva = signal<'observador' | 'ruta' | 'garantias'>('observador');
  readonly guardandoAnotacion = signal(false);
  readonly abriendoCaso = signal(false);

  readonly estudianteSeleccionadoId = signal<number>(1);
  readonly anotaciones = signal<AnotacionObservadorResponse[]>([]);
  readonly mostrarFormAnotacion = signal(false);

  readonly AnotacionTipo = AnotacionObservadorRequestTipoAnotacionEnum;
  readonly SituacionTipo = CasoConvivenciaRequestTipoSituacionEnum;

  readonly formAnotacion = this.fb.group({
    tipoAnotacion: [AnotacionObservadorRequestTipoAnotacionEnum.SeguimientoFormativo, [Validators.required]],
    fechaHecho: [new Date().toISOString().substring(0, 10), [Validators.required]],
    lugarHecho: ['', [Validators.required]],
    descripcionFactica: ['', [Validators.required]],
    accionesPedagogicas: ['']
  });

  readonly formRuta = this.fb.group({
    anioLectivoId: [1, [Validators.required]],
    estudianteImplicadoId: [1, [Validators.required]],
    tipoSituacion: [CasoConvivenciaRequestTipoSituacionEnum.TipoI, [Validators.required]],
    fechaHechos: [new Date().toISOString().substring(0, 10), [Validators.required]],
    lugarHechos: ['', [Validators.required]],
    descripcionSituacion: ['', [Validators.required]],
    atencionMedicaUrgente: [false],
    reservaProteccionContraAcudiente: [false]
  });

  ngOnInit(): void {
    this.cargarAnotaciones();
  }

  onEstudianteIdChange(event: Event): void {
    this.estudianteSeleccionadoId.set(Number((event.target as HTMLInputElement).value));
  }

  cargarAnotaciones(): void {
    const id = this.estudianteSeleccionadoId();
    if (!id) return;

    this.convivenciaService.listarAnotacionesEstudiante(id).subscribe({
      next: (data) => {
        this.anotaciones.set(data);
      }
    });
  }

  guardarAnotacion(): void {
    const estId = this.estudianteSeleccionadoId();
    if (!estId || this.formAnotacion.invalid) return;

    this.guardandoAnotacion.set(true);
    const req = {
      ...this.formAnotacion.getRawValue(),
      estudianteId: estId
    };

    this.convivenciaService.registrarAnotacionObservador(req as any).subscribe({
      next: () => {
        this.guardandoAnotacion.set(false);
        this.mostrarFormAnotacion.set(false);
        this.notificationService.showSuccess('Anotación registrada en el observador inmutable.');
        this.formAnotacion.reset({
          tipoAnotacion: AnotacionObservadorRequestTipoAnotacionEnum.SeguimientoFormativo,
          fechaHecho: new Date().toISOString().substring(0, 10),
          lugarHecho: '',
          descripcionFactica: '',
          accionesPedagogicas: ''
        });
        this.cargarAnotaciones();
      },
      error: () => {
        this.guardandoAnotacion.set(false);
      }
    });
  }

  abrirCasoRuta(): void {
    if (this.formRuta.invalid) return;

    this.abriendoCaso.set(true);
    const req = this.formRuta.getRawValue();

    this.convivenciaService.abrirCasoConvivencia(req as any).subscribe({
      next: (caso) => {
        this.abriendoCaso.set(false);
        this.notificationService.showSuccess(`Ruta de atención activada con radicado ${caso.codigoExpediente || caso.id}.`);
        this.formRuta.reset({
          anioLectivoId: 1,
          estudianteImplicadoId: 1,
          tipoSituacion: CasoConvivenciaRequestTipoSituacionEnum.TipoI,
          fechaHechos: new Date().toISOString().substring(0, 10),
          lugarHechos: '',
          descripcionSituacion: '',
          atencionMedicaUrgente: false,
          reservaProteccionContraAcudiente: false
        });
      },
      error: () => {
        this.abriendoCaso.set(false);
      }
    });
  }
}
