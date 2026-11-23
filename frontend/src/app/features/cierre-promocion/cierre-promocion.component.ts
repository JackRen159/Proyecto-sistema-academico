import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { CierreAcadmicoYPromocinService } from '../../core/api/generated/api/cierre-acadmico-y-promocin.service';
import { EstructuraAcadmicaPeriodosService } from '../../core/api/generated/api/estructura-acadmica-periodos.service';
import { EstructuraAcadmicaGruposService } from '../../core/api/generated/api/estructura-acadmica-grupos.service';
import { EstructuraAcadmicaAosLectivosService } from '../../core/api/generated/api/estructura-acadmica-aos-lectivos.service';
import { MatrculasService } from '../../core/api/generated/api/matrculas.service';
import { NotificationService } from '../../core/services/notification.service';
import { PeriodoAcademicoResponse } from '../../core/api/generated/model/periodo-academico-response';
import { GrupoResponse } from '../../core/api/generated/model/grupo-response';
import { AnioLectivoResponse } from '../../core/api/generated/model/anio-lectivo-response';
import { MatriculaResponse } from '../../core/api/generated/model/matricula-response';
import { EstadoAnioLectivo } from '../../core/api/generated/model/estado-anio-lectivo';
import { DiagnosticoPreCierreResponse } from '../../core/api/generated/model/diagnostico-pre-cierre-response';
import { PreclasificacionPromocionResponse } from '../../core/api/generated/model/preclasificacion-promocion-response';
import { ModalidadTituloBachiller } from '../../core/api/generated/model/modalidad-titulo-bachiller';

@Component({
  selector: 'app-cierre-promocion',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <div class="cierre-container">
      <header class="header">
        <div>
          <h2>Cierre Académico, Boletines y Promoción Anual</h2>
          <p class="subtitle">Gatekeeper de pre-cierre, comisiones periódicas, promoción anual y titulación de bachilleres (UC-20..21, UC-PROV-23..24)</p>
        </div>
      </header>

      <!-- TABS -->
      <div class="tabs-bar">
        <button
          type="button"
          class="tab-btn"
          [class.active]="tabActiva() === 'cierre'"
          (click)="tabActiva.set('cierre')"
        >
          🔒 Pre-Cierre y Boletines
        </button>
        <button
          type="button"
          class="tab-btn"
          [class.active]="tabActiva() === 'promocion'"
          (click)="tabActiva.set('promocion')"
        >
          🎓 Promoción Anual SIEE
        </button>
        <button
          type="button"
          class="tab-btn"
          [class.active]="tabActiva() === 'titulacion'"
          (click)="tabActiva.set('titulacion')"
        >
          📜 Titulación de Media (Grado 11.°)
        </button>
      </div>

      <!-- TAB 1: PRE-CIERRE Y CIERRE DE PERIODO -->
      @if (tabActiva() === 'cierre') {
        <div class="tab-content">
          <div class="filtro-panel">
            <div class="form-row">
              <div class="form-col">
                <label for="cPeriodo">Periodo Académico a Diagnosticar:</label>
                <select id="cPeriodo" [value]="periodoSeleccionadoId()" (change)="onPeriodoChange($event)" class="form-control">
                  @for (p of periodos(); track p.id) {
                    <option [value]="p.id">{{ p.nombre }} ({{ p.estado }})</option>
                  }
                </select>
              </div>
              <div class="form-col btn-align">
                <button type="button" [disabled]="diagnosticando()" class="btn-primary" (click)="ejecutarDiagnostico()">
                  @if (diagnosticando()) {
                    <span>Analizando Gatekeeper...</span>
                  } @else {
                    <span>🔍 Diagnosticar Pre-Cierre (RN-CIE-01)</span>
                  }
                </button>
              </div>
            </div>
          </div>

          <!-- RESULTADO DEL DIAGNÓSTICO -->
          @if (diagnostico()) {
            <div class="diagnostico-card" [class.diag-apto]="diagnostico()?.aptoParaCierre" [class.diag-bloqueado]="!diagnostico()?.aptoParaCierre">
              <div class="diag-header">
                <h3>Dictamen de Pre-Cierre: {{ diagnostico()?.periodoNombre }}</h3>
                <span class="badge" [class.badge-success]="diagnostico()?.aptoParaCierre" [class.badge-danger]="!diagnostico()?.aptoParaCierre">
                  {{ diagnostico()?.aptoParaCierre ? 'APTO PARA CIERRE FORMAL' : 'CIERRE BLOQUEADO' }}
                </span>
              </div>

              <div class="diag-stats">
                <span>Grupos validados: <strong>{{ diagnostico()?.totalGruposValidados }}</strong></span>
                <span>Alumnos evaluados: <strong>{{ diagnostico()?.totalEstudiantesEvaluados }}</strong></span>
              </div>

              @if (!diagnostico()?.aptoParaCierre) {
                <div class="bloqueos-box">
                  <strong>Requisitos bloqueantes pendientes de resolver:</strong>
                  <ul>
                    @for (bloqueo of diagnostico()?.bloqueosCriticos; track bloqueo) {
                      <li>{{ bloqueo }}</li>
                    }
                  </ul>
                </div>
              } @else {
                <div class="cierre-action-box">
                  <p class="text-success">
                    ✓ El 100% de las notas ha sido digitado, no existen excusas médicas pendientes y las reclamaciones están cerradas.
                  </p>
                  <button type="button" [disabled]="cerrandoPeriodo()" class="btn-danger" (click)="formalizarCierrePeriodo()">
                    @if (cerrandoPeriodo()) {
                      <span>Generando Boletines Versionados...</span>
                    } @else {
                      <span>Formalizar Cierre y Emitir Boletines V1 (UC-20)</span>
                    }
                  </button>
                </div>
              }
            </div>
          }
        </div>
      }

      <!-- TAB 2: PROMOCIÓN ANUAL -->
      @if (tabActiva() === 'promocion') {
        <div class="tab-content">
          <div class="filtro-panel">
            <div class="form-row">
              <div class="form-col">
                <label for="promGrupo">Grupo a Preclasificar:</label>
                <select id="promGrupo" [value]="grupoSeleccionadoId()" (change)="onGrupoChange($event)" class="form-control">
                  @for (g of grupos(); track g.id) {
                    <option [value]="g.id">{{ g.nombre }}</option>
                  }
                </select>
              </div>
              <div class="form-col btn-align">
                <button type="button" [disabled]="preclasificando()" class="btn-primary" (click)="cargarPreclasificacion()">
                  @if (preclasificando()) {
                    <span>Calculando Promoción...</span>
                  } @else {
                    <span>Calcular Preclasificación Anual</span>
                  }
                </button>
              </div>
            </div>
          </div>

          @if (preclasificaciones().length > 0) {
            <div class="table-card">
              <table>
                <thead>
                  <tr>
                    <th>Estudiante</th>
                    <th>Promedio General</th>
                    <th>Áreas Reprobadas</th>
                    <th>Inasistencia Anual</th>
                    <th>Dictamen Sugerido SIEE</th>
                    <th>Motivo Técnico</th>
                  </tr>
                </thead>
                <tbody>
                  @for (p of preclasificaciones(); track p.matriculaId) {
                    <tr>
                      <td><strong>{{ p.estudianteNombreCompleto }}</strong></td>
                      <td><span class="weight-chip">{{ p.promedioGeneral }}</span></td>
                      <td>{{ p.areasReprobadas || 0 }}</td>
                      <td>{{ p.porcentajeInasistenciaAnual }}%</td>
                      <td>
                        <span class="badge" [ngClass]="getBadgeClassPromocion(p.clasificacionSugerida)">
                          {{ p.clasificacionSugerida }}
                        </span>
                      </td>
                      <td><small class="text-muted">{{ p.motivoSugerido || '-' }}</small></td>
                    </tr>
                  }
                </tbody>
              </table>
            </div>
          }
        </div>
      }

      <!-- TAB 3: TITULACIÓN DE MEDIA -->
      @if (tabActiva() === 'titulacion') {
        <div class="tab-content">
          <form [formGroup]="formTitulacion" (ngSubmit)="guardarTitulacion()" class="form-card">
            <h3>Verificación de Requisitos de Titulación de Educación Media (UC-PROV-24, RN-CIE-09)</h3>
            <p class="card-desc">
              Conforme a la Ley 115 de 1994, Resolución 4210 de 1996 y Decreto 869 de 2010: Se exige cumplimiento de Servicio Social Obligatorio (&ge; 80h) y registro del examen Saber 11.
            </p>

            <div class="form-row">
              <div class="form-col">
                <label for="tMatricula">Estudiante Candidato a Grado: *</label>
                <select id="tMatricula" formControlName="matriculaId" class="form-control">
                  <option [ngValue]="null">-- Seleccione Estudiante --</option>
                  @for (m of matriculas(); track m.id) {
                    <option [ngValue]="m.id">
                      {{ m.estudianteNombreCompleto || m.estudianteDocumento }} ({{ m.numeroMatricula || ('MAT-' + m.id) }} - Grupo: {{ m.grupoNombre }})
                    </option>
                  }
                </select>
                @if (formTitulacion.get('matriculaId')?.touched && formTitulacion.get('matriculaId')?.invalid) {
                  <small class="text-danger">Debe seleccionar un estudiante.</small>
                }
              </div>
              <div class="form-col">
                <label for="tModalidad">Modalidad de Título: *</label>
                <select id="tModalidad" formControlName="modalidadTitulo" class="form-control">
                  <option [value]="ModalidadTituloBachiller.BachillerAcademico">Bachiller Académico</option>
                  <option [value]="ModalidadTituloBachiller.BachillerTecnico">Bachiller Técnico</option>
                </select>
              </div>
            </div>

            <div class="form-row">
              <div class="form-col">
                <label for="tHoras">Horas Servicio Social Obligatorio: *</label>
                <input id="tHoras" type="number" formControlName="horasServicioSocial" placeholder="Mínimo 80 horas" class="form-control" />
                @if (formTitulacion.get('horasServicioSocial')?.touched && formTitulacion.get('horasServicioSocial')?.hasError('min')) {
                  <small class="text-danger">El Servicio Social Obligatorio debe ser de mínimo 80 horas (Resolución 4210).</small>
                }
              </div>
              <div class="form-col">
                <label for="tIcfes">Código Registro Examen Saber 11: *</label>
                <input id="tIcfes" type="text" formControlName="codigoRegistroIcfes" placeholder="Ej. AC202612345678" class="form-control" />
              </div>
            </div>

            <div class="checkbox-inline-box">
              <div class="checkbox-inline">
                <input id="tDocVerificado" type="checkbox" formControlName="documentoIdentidadVerificado" />
                <label for="tDocVerificado">
                  <strong>Documento de identidad definitivo verificado en ventanilla (Cédula de Ciudadanía para mayores de edad)</strong>
                </label>
              </div>
            </div>

            <div class="form-actions">
              <button type="submit" [disabled]="formTitulacion.invalid || guardandoTitulacion()" class="btn-primary">
                @if (guardandoTitulacion()) {
                  <span>Aprobando Titulación...</span>
                } @else {
                  <span>Aprobar Requisitos de Titulación</span>
                }
              </button>
            </div>
          </form>
        </div>
      }
    </div>
  `,
  styles: [`
    .cierre-container {
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
    .form-row { display: flex; gap: 1rem; align-items: flex-end; margin-bottom: 0.85rem; }
    .form-col { flex: 1; display: flex; flex-direction: column; }
    .btn-align { flex: 0 0 auto; }
    label { font-size: 0.85rem; font-weight: 500; margin-bottom: 0.25rem; color: #475569; }
    .form-control { padding: 0.5rem 0.75rem; border: 1px solid #cbd5e1; border-radius: 4px; font-size: 0.9rem; }
    .btn-primary { background: #2563eb; color: white; border: none; padding: 0.55rem 1.15rem; border-radius: 4px; font-weight: 600; font-size: 0.88rem; cursor: pointer; }
    .btn-primary:disabled { background: #93c5fd; cursor: not-allowed; }
    .btn-danger { background: #dc2626; color: white; border: none; padding: 0.65rem 1.25rem; border-radius: 4px; font-weight: 600; font-size: 0.9rem; cursor: pointer; }
    .btn-danger:disabled { background: #fca5a5; cursor: not-allowed; }
    .diagnostico-card {
      border-radius: 8px;
      padding: 1.5rem;
      margin-bottom: 1.5rem;
      border: 1px solid transparent;
    }
    .diag-apto { background: #f0fdf4; border-color: #bbf7d0; }
    .diag-bloqueado { background: #fef2f2; border-color: #fecaca; }
    .diag-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 0.75rem; }
    .diag-header h3 { margin: 0; font-size: 1.1rem; }
    .diag-stats { display: flex; gap: 2rem; font-size: 0.88rem; margin-bottom: 1rem; color: #475569; }
    .bloqueos-box { background: white; padding: 1rem; border-radius: 6px; border-left: 4px solid #ef4444; margin-top: 1rem; }
    .bloqueos-box ul { margin: 0.5rem 0 0; padding-left: 1.25rem; color: #991b1b; font-size: 0.88rem; line-height: 1.5; }
    .cierre-action-box { margin-top: 1rem; display: flex; justify-content: space-between; align-items: center; }
    .text-success { color: #16a34a; font-weight: 500; }
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
    .weight-chip { background: #f8fafc; padding: 0.15rem 0.45rem; border-radius: 4px; font-size: 0.8rem; font-weight: 600; color: #0284c7; }
    .badge { padding: 0.2rem 0.5rem; border-radius: 4px; font-size: 0.75rem; font-weight: 600; background: #e2e8f0; color: #475569; }
    .badge-success { background: #dcfce7; color: #166534; }
    .badge-danger { background: #fee2e2; color: #991b1b; }
    .badge-warning { background: #fef9c3; color: #854d0e; }
    .badge-info { background: #eff6ff; color: #1d4ed8; }
    .text-muted { color: #64748b; }
    .form-card { background: white; border: 1px solid #e2e8f0; border-radius: 8px; padding: 1.5rem; box-shadow: 0 1px 3px rgba(0,0,0,0.05); }
    .card-desc { font-size: 0.85rem; color: #64748b; margin-bottom: 1.25rem; line-height: 1.4; }
    .checkbox-inline-box { background: #f8fafc; padding: 0.85rem; border-radius: 6px; border: 1px solid #e2e8f0; margin: 1.25rem 0; }
    .checkbox-inline { display: flex; align-items: center; gap: 0.5rem; font-size: 0.85rem; }
    .form-actions { display: flex; justify-content: flex-end; margin-top: 1rem; }
    .text-danger { color: #dc2626; font-size: 0.8rem; }
  `]
})
export class CierrePromocionComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly cierreService = inject(CierreAcadmicoYPromocinService);
  private readonly aniosService = inject(EstructuraAcadmicaAosLectivosService);
  private readonly periodosService = inject(EstructuraAcadmicaPeriodosService);
  private readonly gruposService = inject(EstructuraAcadmicaGruposService);
  private readonly matriculasService = inject(MatrculasService);
  private readonly notificationService = inject(NotificationService);

  readonly tabActiva = signal<'cierre' | 'promocion' | 'titulacion'>('cierre');
  readonly diagnosticando = signal(false);
  readonly cerrandoPeriodo = signal(false);
  readonly preclasificando = signal(false);
  readonly guardandoTitulacion = signal(false);

  readonly anios = signal<AnioLectivoResponse[]>([]);
  readonly periodos = signal<PeriodoAcademicoResponse[]>([]);
  readonly grupos = signal<GrupoResponse[]>([]);
  readonly matriculas = signal<MatriculaResponse[]>([]);
  readonly anioSeleccionadoId = signal<number | null>(null);
  readonly periodoSeleccionadoId = signal<number>(1);
  readonly grupoSeleccionadoId = signal<number>(10);

  readonly diagnostico = signal<DiagnosticoPreCierreResponse | null>(null);
  readonly preclasificaciones = signal<PreclasificacionPromocionResponse[]>([]);

  readonly ModalidadTituloBachiller = ModalidadTituloBachiller;

  readonly formTitulacion = this.fb.group({
    matriculaId: [null as number | null, [Validators.required]],
    modalidadTitulo: [ModalidadTituloBachiller.BachillerAcademico, [Validators.required]],
    horasServicioSocial: [80, [Validators.required, Validators.min(80)]],
    codigoRegistroIcfes: ['', [Validators.required]],
    documentoIdentidadVerificado: [true, [Validators.requiredTrue]]
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
          const aId = activo.id!;
          this.anioSeleccionadoId.set(aId);

          this.periodosService.apiAniosLectivosAnioIdPeriodosGet(aId).subscribe({
            next: (pers) => {
              this.periodos.set(pers);
              if (pers.length > 0) this.periodoSeleccionadoId.set(pers[0].id!);
            }
          });

          this.gruposService.apiGruposGet(aId).subscribe({
            next: (grps) => {
              this.grupos.set(grps);
              if (grps.length > 0) this.grupoSeleccionadoId.set(grps[0].id!);
            }
          });

          this.matriculasService.listarMatriculasPorAnioLectivo(aId).subscribe({
            next: (paged) => {
              const list = paged.content || [];
              this.matriculas.set(list);
              if (list.length > 0 && !this.formTitulacion.get('matriculaId')?.value) {
                this.formTitulacion.patchValue({ matriculaId: list[0].id! });
              }
            }
          });
        }
      }
    });
  }

  onPeriodoChange(event: Event): void {
    this.periodoSeleccionadoId.set(Number((event.target as HTMLSelectElement).value));
    this.diagnostico.set(null);
  }

  onGrupoChange(event: Event): void {
    this.grupoSeleccionadoId.set(Number((event.target as HTMLSelectElement).value));
  }

  ejecutarDiagnostico(): void {
    const id = this.periodoSeleccionadoId();
    this.diagnosticando.set(true);

    this.cierreService.diagnosticarPreCierre(id).subscribe({
      next: (data) => {
        this.diagnosticando.set(false);
        this.diagnostico.set(data);
        if (data.aptoParaCierre) {
          this.notificationService.showSuccess('Periodo apto para formalizar cierre.');
        } else {
          this.notificationService.showWarning('Periodo con bloqueos pendientes para cierre.');
        }
      },
      error: () => {
        this.diagnosticando.set(false);
      }
    });
  }

  formalizarCierrePeriodo(): void {
    const id = this.periodoSeleccionadoId();
    this.cerrandoPeriodo.set(true);

    this.cierreService.cerrarPeriodo({ periodoId: id, permitirEstudiantesAplazadosIncapacidad: true }).subscribe({
      next: () => {
        this.cerrandoPeriodo.set(false);
        this.notificationService.showSuccess('Periodo cerrado formalmente. Boletines emitidos.');
        this.diagnostico.set(null);
      },
      error: () => {
        this.cerrandoPeriodo.set(false);
      }
    });
  }

  cargarPreclasificacion(): void {
    const gId = this.grupoSeleccionadoId();
    this.preclasificando.set(true);

    this.cierreService.preclasificarPromocionGrupo(gId).subscribe({
      next: (data) => {
        this.preclasificando.set(false);
        this.preclasificaciones.set(data);
      },
      error: () => {
        this.preclasificando.set(false);
      }
    });
  }

  guardarTitulacion(): void {
    if (this.formTitulacion.invalid) return;

    this.guardandoTitulacion.set(true);
    this.cierreService.registrarRequisitosTitulacion(this.formTitulacion.getRawValue() as any).subscribe({
      next: () => {
        this.guardandoTitulacion.set(false);
        this.notificationService.showSuccess('Requisitos de titulación registrados exitosamente.');
        this.formTitulacion.reset({
          matriculaId: null,
          modalidadTitulo: ModalidadTituloBachiller.BachillerAcademico,
          horasServicioSocial: 80,
          codigoRegistroIcfes: '',
          documentoIdentidadVerificado: true
        });
      },
      error: () => {
        this.guardandoTitulacion.set(false);
      }
    });
  }

  getBadgeClassPromocion(estado?: string): string {
    switch (estado) {
      case 'PROMOVIDO':
      case 'PROMOVIDO_PREESCOLAR':
        return 'badge-success';
      case 'NO_PROMOVIDO':
        return 'badge-danger';
      case 'PENDIENTE_RECUPERACION_FINAL':
        return 'badge-warning';
      default:
        return 'badge-info';
    }
  }
}
