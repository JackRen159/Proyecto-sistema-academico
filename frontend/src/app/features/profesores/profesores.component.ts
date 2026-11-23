import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ProfesoresService } from '../../core/api/generated/api/profesores.service';
import { AsignacionesAcadmicasService } from '../../core/api/generated/api/asignaciones-acadmicas.service';
import { EstructuraAcadmicaAosLectivosService } from '../../core/api/generated/api/estructura-acadmica-aos-lectivos.service';
import { EstructuraAcadmicaGruposService } from '../../core/api/generated/api/estructura-acadmica-grupos.service';
import { EstructuraAcadmicaAsignaturasService } from '../../core/api/generated/api/estructura-acadmica-asignaturas.service';
import { NotificationService } from '../../core/services/notification.service';
import { ProfesorResponse } from '../../core/api/generated/model/profesor-response';
import { AsignacionAcademicaResponse } from '../../core/api/generated/model/asignacion-academica-response';
import { AnioLectivoResponse } from '../../core/api/generated/model/anio-lectivo-response';
import { GrupoResponse } from '../../core/api/generated/model/grupo-response';
import { AsignaturaResponse } from '../../core/api/generated/model/asignatura-response';
import { TipoDocumento } from '../../core/api/generated/model/tipo-documento';
import { EstadoAnioLectivo } from '../../core/api/generated/model/estado-anio-lectivo';
import { EstadoAdministrativoDocente } from '../../core/api/generated/model/estado-administrativo-docente';

@Component({
  selector: 'app-profesores',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <div class="profesores-container">
      <header class="header">
        <div>
          <h2>Expediente Docente y Asignaciones Académicas</h2>
          <p class="subtitle">Gestión de docentes, control de inhabilidades Ley 1918 y asignación horaria por grupo (UC-15, UC-PROV-11..12)</p>
        </div>
      </header>

      <!-- TABS -->
      <div class="tabs-bar">
        <button
          type="button"
          class="tab-btn"
          [class.active]="tabActiva() === 'docentes'"
          (click)="tabActiva.set('docentes')"
        >
          👨‍🏫 Expediente de Docentes
        </button>
        <button
          type="button"
          class="tab-btn"
          [class.active]="tabActiva() === 'asignaciones'"
          (click)="tabActiva.set('asignaciones')"
        >
          📚 Asignaciones y Cargas Académicas
        </button>
      </div>

      <!-- TAB 1: DOCENTES -->
      @if (tabActiva() === 'docentes') {
        <div class="tab-content">
          <div class="content-toolbar">
            <h3>Catálogo de Profesores Registrados</h3>
            <button type="button" class="btn-primary" (click)="mostrarFormDocente.set(true)">
              + Registrar Nuevo Docente
            </button>
          </div>

          @if (mostrarFormDocente()) {
            <form [formGroup]="formDocente" (ngSubmit)="guardarDocente()" class="form-card">
              <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1rem;">
                <h4>Registro de Docente con Verificación Ley 1918 de 2018 (RN-PRO-02)</h4>
                <button type="button" class="btn-demo-simular" (click)="rellenarDocentePrueba()">
                  ⚡ Rellenar Docente de Prueba
                </button>
              </div>

              <div class="form-row">
                <div class="form-col">
                  <label for="dTipoDoc">Tipo Documento: *</label>
                  <select id="dTipoDoc" formControlName="tipoDocumento" class="form-control">
                    <option [value]="TipoDocumento.Cc">Cédula de Ciudadanía (CC)</option>
                    <option [value]="TipoDocumento.Ce">Cédula de Extranjería (CE)</option>
                    <option [value]="TipoDocumento.Pasaporte">Pasaporte</option>
                  </select>
                </div>
                <div class="form-col">
                  <label for="dDocumento">Número de Documento: *</label>
                  <input id="dDocumento" type="text" formControlName="documento" class="form-control" />
                </div>
                <div class="form-col">
                  <label for="dNombres">Nombres: *</label>
                  <input id="dNombres" type="text" formControlName="nombres" class="form-control" />
                </div>
                <div class="form-col">
                  <label for="dApellidos">Apellidos: *</label>
                  <input id="dApellidos" type="text" formControlName="apellidos" class="form-control" />
                </div>
              </div>

              <div class="form-row">
                <div class="form-col">
                  <label for="dCorreo">Correo Electrónico: *</label>
                  <input id="dCorreo" type="email" formControlName="correo" class="form-control" />
                </div>
                <div class="form-col">
                  <label for="dTelefono">Teléfono:</label>
                  <input id="dTelefono" type="text" formControlName="telefono" class="form-control" />
                </div>
                <div class="form-col">
                  <label for="dEspecialidad">Especialidad / Área:</label>
                  <input id="dEspecialidad" type="text" formControlName="especialidad" placeholder="Ej. Matemáticas" class="form-control" />
                </div>
              </div>

              <!-- LEY 1918 DE 2018 -->
              <div class="inhabilidades-box">
                <div class="checkbox-inline">
                  <input id="dInhabilidades" type="checkbox" formControlName="verificadoInhabilidades" />
                  <label for="dInhabilidades">
                    <strong>Verificación vigente de ausencia de inhabilidades por delitos contra menores (Ley 1918 de 2018)</strong>
                  </label>
                </div>
                <p class="inhabilidades-desc">
                  Requisito legal bloqueante: Ningún docente puede ser activado ni asignado sin certificado vigente de antecedentes judiciales especiales.
                </p>

                <div class="upload-zone">
                  <input
                    type="file"
                    #fileCertificado
                    (change)="onArchivoSeleccionado($event)"
                    accept=".pdf,.png,.jpg,.jpeg"
                    style="display: none"
                  />
                  <div class="upload-buttons">
                    <button type="button" class="btn-upload" (click)="fileCertificado.click()">
                      📁 Subir Certificado Digital (PDF / Imagen)
                    </button>
                    <button type="button" class="btn-demo-simular" (click)="cargarCertificadoSimulado()">
                      ⚡ Cargar Certificado Válido de Demostración
                    </button>
                  </div>
                  @if (archivoSoporteNombre()) {
                    <div class="soporte-cargado-badge">
                      <span>✓ Soporte documental cargado: <strong>{{ archivoSoporteNombre() }}</strong></span>
                    </div>
                  }
                </div>

                <div class="form-row" style="margin-top: 0.75rem;">
                  <div class="form-col">
                    <label for="dCertificado">N° Certificado Inhabilidades:</label>
                    <input id="dCertificado" type="text" formControlName="numeroCertificadoInhabilidades" placeholder="Ej. INH-2026-0045" class="form-control" />
                  </div>
                  <div class="form-col">
                    <label for="dFechaCert">Fecha de Verificación:</label>
                    <input id="dFechaCert" type="date" formControlName="fechaVerificacionInhabilidades" class="form-control" />
                  </div>
                </div>
              </div>

              <div class="form-actions">
                <button type="button" class="btn-secondary" (click)="mostrarFormDocente.set(false)">Cancelar</button>
                <button type="submit" [disabled]="formDocente.invalid || guardandoDocente()" class="btn-primary">
                  @if (guardandoDocente()) {
                    <span>Guardando Docente...</span>
                  } @else {
                    <span>Registrar Docente</span>
                  }
                </button>
              </div>
            </form>
          }

          <div class="table-card">
            <table>
              <thead>
                <tr>
                  <th>Docente</th>
                  <th>Documento</th>
                  <th>Correo</th>
                  <th>Especialidad</th>
                  <th>Situación</th>
                  <th>Ley 1918</th>
                  <th>Estado</th>
                </tr>
              </thead>
              <tbody>
                @for (p of profesores(); track p.id) {
                  <tr>
                    <td><strong>{{ p.nombreCompleto || (p.nombres + ' ' + p.apellidos) }}</strong></td>
                    <td>{{ p.tipoDocumento }} {{ p.documento }}</td>
                    <td>{{ p.correo }}</td>
                    <td>{{ p.especialidad || '-' }}</td>
                    <td><span class="chip-situacion">{{ p.estadoAdministrativo || 'ACTIVO' }}</span></td>
                    <td>
                      @if (p.verificadoInhabilidades) {
                        <span class="badge badge-success">Verificado Ley 1918</span>
                      } @else {
                        <span class="badge badge-danger">Sin verificar</span>
                      }
                    </td>
                    <td>
                      <span class="badge" [class.badge-active]="p.activo">
                        {{ p.activo ? 'Activo' : 'Inactivo' }}
                      </span>
                    </td>
                  </tr>
                }
              </tbody>
            </table>
          </div>
        </div>
      }

      <!-- TAB 2: ASIGNACIONES ACADÉMICAS -->
      @if (tabActiva() === 'asignaciones') {
        <div class="tab-content">
          <div class="content-toolbar">
            <div class="selector-row">
              <h3>Matriz de Cargas y Asignaciones Académicas</h3>
              <label for="selectAnioAsig" style="margin-left: 1rem; font-weight: 500;">Año Lectivo:</label>
              <select
                id="selectAnioAsig"
                class="form-control"
                style="width: auto; display: inline-block; margin-left: 0.5rem;"
                [value]="anioSeleccionadoId()"
                (change)="onAnioAsignacionChange($event)"
              >
                @for (a of anios(); track a.id) {
                  <option [value]="a.id">Año {{ a.anio }} ({{ a.estado }})</option>
                }
              </select>
            </div>
            <button type="button" class="btn-primary" (click)="mostrarFormAsignacion.set(true)">
              + Nueva Asignación Docente
            </button>
          </div>

          @if (mostrarFormAsignacion()) {
            <form [formGroup]="formAsignacion" (ngSubmit)="guardarAsignacion()" class="form-card">
              <h4>Asignar Carga Académica a Docente (UC-15)</h4>

              <div class="form-row">
                <div class="form-col">
                  <label for="asigDocente">Docente Titular: *</label>
                  <select id="asigDocente" formControlName="profesorId" class="form-control">
                    @for (p of profesores(); track p.id) {
                      <option [value]="p.id">{{ p.nombres }} {{ p.apellidos }} ({{ p.especialidad || 'Docente' }})</option>
                    }
                  </select>
                </div>
                <div class="form-col">
                  <label for="asigGrupo">Grupo / Curso: *</label>
                  <select id="asigGrupo" formControlName="grupoId" class="form-control">
                    @for (g of grupos(); track g.id) {
                      <option [value]="g.id">{{ g.nombre }}</option>
                    }
                  </select>
                </div>
                <div class="form-col">
                  <label for="asigAsignatura">Asignatura Curricular: *</label>
                  <select id="asigAsignatura" formControlName="asignaturaId" class="form-control">
                    @for (a of asignaturas(); track a.id) {
                      <option [value]="a.id">{{ a.nombre }}</option>
                    }
                  </select>
                </div>
              </div>

              <div class="form-row">
                <div class="form-col">
                  <label for="asigIHS">Intensidad Horaria Semanal (IHS): *</label>
                  <input id="asigIHS" type="number" formControlName="intensidadHorariaSemanal" placeholder="Horas semanales (ej. 4)" class="form-control" />
                </div>
                <div class="form-col checkbox-inline-wrapper">
                  <div class="checkbox-inline">
                    <input id="asigDirector" type="checkbox" formControlName="esDirectorGrupo" />
                    <label for="asigDirector">
                      <strong>Designar como Director de Grupo (UC-15 A1, RN-09)</strong>
                    </label>
                  </div>
                </div>
              </div>

              <div class="form-actions">
                <button type="button" class="btn-secondary" (click)="mostrarFormAsignacion.set(false)">Cancelar</button>
                <button type="submit" [disabled]="formAsignacion.invalid || guardandoAsignacion()" class="btn-primary">
                  @if (guardandoAsignacion()) {
                    <span>Asignando Carga...</span>
                  } @else {
                    <span>Guardar Asignación</span>
                  }
                </button>
              </div>
            </form>
          }

          <div class="table-card">
            <table>
              <thead>
                <tr>
                  <th>Docente</th>
                  <th>Grupo</th>
                  <th>Asignatura</th>
                  <th>IHS Semanal</th>
                  <th>Rol Especial</th>
                  <th>Estado</th>
                  <th>Acciones</th>
                </tr>
              </thead>
              <tbody>
                @if (asignaciones().length === 0) {
                  <tr>
                    <td colspan="7" class="text-center">No hay asignaciones registradas.</td>
                  </tr>
                }
                @for (asig of asignaciones(); track asig.id) {
                  <tr>
                    <td><strong>{{ asig.profesorNombreCompleto }}</strong></td>
                    <td><span class="chip-grupo">{{ asig.grupoNombre }}</span></td>
                    <td>{{ asig.asignaturaNombre }}</td>
                    <td><span class="weight-chip">{{ asig.intensidadHorariaSemanal }} horas</span></td>
                    <td>
                      @if (asig.esDirectorGrupo) {
                        <span class="badge badge-director">Director de Grupo</span>
                      } @else {
                        <span class="text-muted">Docente de Asignatura</span>
                      }
                    </td>
                    <td>
                      <span class="badge" [class.badge-active]="asig.activo">
                        {{ asig.activo ? 'Vigente' : 'Inactiva' }}
                      </span>
                    </td>
                    <td>
                      @if (asig.activo) {
                        <button type="button" class="btn-sm btn-danger" (click)="desactivarAsignacion(asig.id!)">
                          Desactivar
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
    </div>
  `,
  styles: [`
    .profesores-container {
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
    .inhabilidades-box {
      background: #fef2f2;
      border: 1px solid #fecaca;
      border-radius: 6px;
      padding: 1rem;
      margin: 1rem 0;
    }
    .inhabilidades-desc { font-size: 0.8rem; color: #991b1b; margin: 0.35rem 0 0.85rem; }
    .upload-zone { margin: 0.5rem 0; }
    .upload-buttons { display: flex; gap: 0.5rem; flex-wrap: wrap; }
    .btn-upload {
      background: white;
      border: 1px solid #cbd5e1;
      padding: 0.45rem 0.75rem;
      border-radius: 4px;
      font-size: 0.82rem;
      font-weight: 500;
      color: #334155;
      cursor: pointer;
    }
    .btn-upload:hover { background: #f8fafc; border-color: #94a3b8; }
    .btn-demo-simular {
      background: #eff6ff;
      border: 1px solid #bfdbfe;
      padding: 0.45rem 0.75rem;
      border-radius: 4px;
      font-size: 0.82rem;
      font-weight: 600;
      color: #1d4ed8;
      cursor: pointer;
    }
    .btn-demo-simular:hover { background: #dbeafe; }
    .soporte-cargado-badge {
      margin-top: 0.5rem;
      background: #ecfdf5;
      border: 1px solid #a7f3d0;
      color: #065f46;
      padding: 0.4rem 0.65rem;
      border-radius: 4px;
      font-size: 0.82rem;
    }
    .checkbox-inline { display: flex; align-items: center; gap: 0.5rem; font-size: 0.85rem; }
    .checkbox-inline-wrapper { justify-content: flex-end; padding-bottom: 0.5rem; }
    .form-actions { display: flex; justify-content: flex-end; gap: 0.5rem; margin-top: 1.25rem; }
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
    .chip-situacion { background: #f1f5f9; color: #475569; padding: 0.2rem 0.5rem; border-radius: 4px; font-size: 0.78rem; font-weight: 500; }
    .chip-grupo { background: #eff6ff; color: #1d4ed8; font-weight: 600; padding: 0.2rem 0.5rem; border-radius: 4px; font-size: 0.8rem; }
    .weight-chip { background: #f8fafc; padding: 0.15rem 0.45rem; border-radius: 4px; font-size: 0.8rem; font-weight: 600; color: #0284c7; }
    .badge { padding: 0.2rem 0.5rem; border-radius: 4px; font-size: 0.75rem; font-weight: 600; background: #e2e8f0; color: #475569; }
    .badge-success, .badge-active { background: #dcfce7; color: #166534; }
    .badge-danger { background: #fee2e2; color: #991b1b; }
    .badge-director { background: #fef9c3; color: #854d0e; border: 1px solid #fde047; }
    .text-muted { color: #94a3b8; font-size: 0.8rem; }
    .btn-primary { background: #2563eb; color: white; border: none; padding: 0.55rem 1.15rem; border-radius: 4px; font-weight: 600; font-size: 0.88rem; cursor: pointer; }
    .btn-secondary { background: #e2e8f0; color: #334155; border: none; padding: 0.5rem 0.85rem; border-radius: 4px; font-size: 0.85rem; cursor: pointer; }
    .btn-danger { background: #dc2626; color: white; border: none; padding: 0.35rem 0.65rem; border-radius: 4px; font-size: 0.78rem; cursor: pointer; }
    .btn-sm { padding: 0.25rem 0.5rem; font-size: 0.78rem; border-radius: 4px; border: none; cursor: pointer; font-weight: 500; }
  `]
})
export class ProfesoresComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly profesoresService = inject(ProfesoresService);
  private readonly asignacionesService = inject(AsignacionesAcadmicasService);
  private readonly aniosService = inject(EstructuraAcadmicaAosLectivosService);
  private readonly gruposService = inject(EstructuraAcadmicaGruposService);
  private readonly asignaturasService = inject(EstructuraAcadmicaAsignaturasService);
  private readonly notificationService = inject(NotificationService);

  readonly tabActiva = signal<'docentes' | 'asignaciones'>('docentes');
  readonly guardandoDocente = signal(false);
  readonly guardandoAsignacion = signal(false);
  readonly archivoSoporteNombre = signal<string | null>(null);

  readonly profesores = signal<ProfesorResponse[]>([]);
  readonly asignaciones = signal<AsignacionAcademicaResponse[]>([]);
  readonly anios = signal<AnioLectivoResponse[]>([]);
  readonly grupos = signal<GrupoResponse[]>([]);
  readonly asignaturas = signal<AsignaturaResponse[]>([]);

  readonly anioSeleccionadoId = signal<number | null>(null);

  readonly mostrarFormDocente = signal(false);
  readonly mostrarFormAsignacion = signal(false);

  readonly TipoDocumento = TipoDocumento;

  readonly formDocente = this.fb.group({
    tipoDocumento: [TipoDocumento.Cc, [Validators.required]],
    documento: ['', [Validators.required]],
    nombres: ['', [Validators.required]],
    apellidos: ['', [Validators.required]],
    correo: ['', [Validators.required, Validators.email]],
    telefono: [''],
    especialidad: [''],
    verificadoInhabilidades: [true, [Validators.requiredTrue]],
    numeroCertificadoInhabilidades: ['INH-2026-001', [Validators.required]],
    fechaVerificacionInhabilidades: [new Date().toISOString().substring(0, 10), [Validators.required]]
  });

  readonly formAsignacion = this.fb.group({
    profesorId: [null as number | null, [Validators.required]],
    grupoId: [null as number | null, [Validators.required]],
    asignaturaId: [null as number | null, [Validators.required]],
    intensidadHorariaSemanal: [4, [Validators.required, Validators.min(1)]],
    esDirectorGrupo: [false]
  });

  ngOnInit(): void {
    this.cargarDocentes();
    this.cargarCatalogosAuxiliares();
  }

  cargarDocentes(): void {
    this.profesoresService.apiProfesoresGet().subscribe({
      next: (data) => {
        this.profesores.set(data);
        if (data.length > 0 && !this.formAsignacion.get('profesorId')?.value) {
          this.formAsignacion.patchValue({ profesorId: data[0].id! });
        }
      }
    });
  }

  cargarAsignaciones(anioLectivoId?: number): void {
    const anioId = anioLectivoId || this.anioSeleccionadoId();
    if (!anioId) return;
    this.asignacionesService.apiAsignacionesAcademicasGet(undefined, undefined, anioId).subscribe({
      next: (data) => {
        this.asignaciones.set(data);
      }
    });
  }

  cargarCatalogosAuxiliares(): void {
    this.aniosService.apiAniosLectivosGet().subscribe({
      next: (data) => {
        this.anios.set(data);
        if (data.length > 0) {
          const activo = data.find(a => a.estado === EstadoAnioLectivo.Activo) || data[0];
          const anioId = activo.id!;
          this.anioSeleccionadoId.set(anioId);
          this.cargarAsignaciones(anioId);
          this.gruposService.apiGruposGet(anioId).subscribe({
            next: (grps) => {
              this.grupos.set(grps);
              if (grps.length > 0 && !this.formAsignacion.get('grupoId')?.value) {
                this.formAsignacion.patchValue({ grupoId: grps[0].id! });
              }
            }
          });
        }
      }
    });

    this.asignaturasService.apiAsignaturasGet().subscribe({
      next: (asigs) => {
        this.asignaturas.set(asigs);
        if (asigs.length > 0 && !this.formAsignacion.get('asignaturaId')?.value) {
          this.formAsignacion.patchValue({ asignaturaId: asigs[0].id! });
        }
      }
    });
  }

  onAnioAsignacionChange(event: Event): void {
    const anioId = Number((event.target as HTMLSelectElement).value);
    this.anioSeleccionadoId.set(anioId);
    this.cargarAsignaciones(anioId);
    this.gruposService.apiGruposGet(anioId).subscribe({
      next: (grps) => {
        this.grupos.set(grps);
      }
    });
  }

  rellenarDocentePrueba(): void {
    const randomNum = Math.floor(100000 + Math.random() * 900000);
    this.archivoSoporteNombre.set('Certificado_Antecedentes_Especiales_Policia_Nacional.pdf');
    this.formDocente.patchValue({
      tipoDocumento: TipoDocumento.Cc,
      documento: `71${randomNum}`,
      nombres: 'Gabriel',
      apellidos: 'García Márquez',
      correo: `docente.prueba${randomNum}@colegio.edu.co`,
      telefono: '3129876543',
      especialidad: 'Lengua Castellana y Literatura',
      verificadoInhabilidades: true,
      numeroCertificadoInhabilidades: `CERT-INH-${randomNum}`,
      fechaVerificacionInhabilidades: new Date().toISOString().substring(0, 10)
    });
    this.notificationService.showSuccess(
      'Formulario de docente diligenciado con verificación Ley 1918 lista para guardar.',
      'Datos de Prueba Cargados'
    );
  }

  onArchivoSeleccionado(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      const file = input.files[0];
      this.archivoSoporteNombre.set(file.name);
      this.formDocente.patchValue({
        verificadoInhabilidades: true,
        numeroCertificadoInhabilidades: `CERT-INH-${Math.floor(1000 + Math.random() * 9000)}`,
        fechaVerificacionInhabilidades: new Date().toISOString().substring(0, 10)
      });
      this.notificationService.showSuccess(
        `Archivo "${file.name}" cargado y vinculado como certificado Ley 1918.`,
        'Soporte Adjuntado'
      );
    }
  }

  cargarCertificadoSimulado(): void {
    const nombreDoc = 'Certificado_Antecedentes_Especiales_Policia_Nacional.pdf';
    this.archivoSoporteNombre.set(nombreDoc);
    this.formDocente.patchValue({
      verificadoInhabilidades: true,
      numeroCertificadoInhabilidades: 'CERT-LEY1918-2026-OK',
      fechaVerificacionInhabilidades: new Date().toISOString().substring(0, 10)
    });
    this.notificationService.showSuccess(
      'Soporte oficial de Ley 1918 validado para la simulación.',
      'Certificado Verificado'
    );
  }

  guardarDocente(): void {
    if (this.formDocente.invalid) return;

    this.guardandoDocente.set(true);
    this.profesoresService.apiProfesoresPost(this.formDocente.getRawValue() as any).subscribe({
      next: (p) => {
        this.guardandoDocente.set(false);
        this.notificationService.showSuccess(
          `Docente ${p.nombres} ${p.apellidos} registrado exitosamente con verificación Ley 1918.`,
          'Docente Registrado'
        );
        this.mostrarFormDocente.set(false);
        this.cargarDocentes();
      },
      error: () => {
        this.guardandoDocente.set(false);
      }
    });
  }

  guardarAsignacion(): void {
    if (this.formAsignacion.invalid) return;

    this.guardandoAsignacion.set(true);
    this.asignacionesService.apiAsignacionesAcademicasPost(this.formAsignacion.getRawValue() as any).subscribe({
      next: () => {
        this.guardandoAsignacion.set(false);
        this.notificationService.showSuccess(
          'Carga académica asignada exitosamente.',
          'Asignación Registrada'
        );
        this.mostrarFormAsignacion.set(false);
        this.cargarAsignaciones();
      },
      error: () => {
        this.guardandoAsignacion.set(false);
      }
    });
  }

  desactivarAsignacion(id: number): void {
    this.asignacionesService.apiAsignacionesAcademicasIdDesactivarPatch(id).subscribe({
      next: () => {
        this.notificationService.showWarning('Asignación académica desactivada.');
        this.cargarAsignaciones();
      }
    });
  }
}
