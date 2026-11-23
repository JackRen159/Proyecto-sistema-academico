import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { EstudiantesService } from '../../core/api/generated/api/estudiantes.service';
import { NotificationService } from '../../core/services/notification.service';
import { TipoDocumento } from '../../core/api/generated/model/tipo-documento';
import { Parentesco } from '../../core/api/generated/model/parentesco';
import { SexoDocumental } from '../../core/api/generated/model/sexo-documental';
import { ExpedienteCompletoRequest } from '../../core/api/generated/model/expediente-completo-request';

@Component({
  selector: 'app-estudiante-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  template: `
    <div class="form-container">
      <div class="form-header">
        <div>
          <h2>Registro de Expediente Escolar Completo</h2>
          <p class="subtitle">Inscripción integral de estudiante y acudiente principal (UC-12, Ley 1098 / Ley 1581)</p>
        </div>
        <a routerLink="/estudiantes" class="btn-secondary">← Volver al Listado</a>
      </div>

      <form [formGroup]="form" (ngSubmit)="guardarExpediente()" class="form-card">
        <!-- SECCIÓN 1: DATOS PERSONALES DEL ESTUDIANTE -->
        <fieldset class="form-section">
          <legend class="section-legend">1. Datos Personales del Estudiante</legend>

          <div class="form-row">
            <div class="form-col">
              <label for="nombres">Nombres: *</label>
              <input id="nombres" type="text" formControlName="nombres" class="form-control" />
              @if (form.get('nombres')?.touched && form.get('nombres')?.invalid) {
                <small class="text-danger">Los nombres son obligatorios.</small>
              }
            </div>
            <div class="form-col">
              <label for="apellidos">Apellidos: *</label>
              <input id="apellidos" type="text" formControlName="apellidos" class="form-control" />
              @if (form.get('apellidos')?.touched && form.get('apellidos')?.invalid) {
                <small class="text-danger">Los apellidos son obligatorios.</small>
              }
            </div>
          </div>

          <div class="form-row">
            <div class="form-col">
              <label for="tipoDocumento">Tipo de Documento: *</label>
              <select id="tipoDocumento" formControlName="tipoDocumento" class="form-control">
                @for (tipo of tiposDocumento; track tipo) {
                  <option [value]="tipo">{{ tipo }}</option>
                }
              </select>
            </div>
            <div class="form-col">
              <label for="documento">Número de Documento: *</label>
              <input id="documento" type="text" formControlName="documento" class="form-control" />
              @if (form.get('documento')?.touched && form.get('documento')?.invalid) {
                <small class="text-danger">El documento es obligatorio.</small>
              }
            </div>
            <div class="form-col">
              <label for="fechaNacimiento">Fecha de Nacimiento: *</label>
              <input id="fechaNacimiento" type="date" formControlName="fechaNacimiento" class="form-control" />
              @if (form.get('fechaNacimiento')?.touched && form.get('fechaNacimiento')?.invalid) {
                <small class="text-danger">Fecha requerida.</small>
              }
            </div>
          </div>

          <div class="form-row">
            <div class="form-col">
              <label for="sexo">Sexo Documental:</label>
              <select id="sexo" formControlName="sexo" class="form-control">
                <option [value]="SexoDocumental.Masculino">Masculino</option>
                <option [value]="SexoDocumental.Femenino">Femenino</option>
                <option [value]="SexoDocumental.Otro">Otro</option>
                <option [value]="SexoDocumental.NoEspecificado">No especificado</option>
              </select>
            </div>
            <div class="form-col">
              <label for="direccion">Dirección Residencial: *</label>
              <input id="direccion" type="text" formControlName="direccion" class="form-control" />
              @if (form.get('direccion')?.touched && form.get('direccion')?.invalid) {
                <small class="text-danger">La dirección es obligatoria.</small>
              }
            </div>
            <div class="form-col">
              <label for="telefono">Teléfono:</label>
              <input id="telefono" type="text" formControlName="telefono" class="form-control" />
            </div>
          </div>
        </fieldset>

        <!-- SECCIÓN 2: SALUD E INCLUSIÓN (DECRETO 1421 / LEY 1581) -->
        <fieldset class="form-section">
          <legend class="section-legend">2. Salud y Enfoque Inclusivo</legend>

          <div class="form-row">
            <div class="form-col">
              <label for="eps">EPS / Entidad de Salud:</label>
              <input id="eps" type="text" formControlName="eps" placeholder="Ej. Sura, Nueva EPS" class="form-control" />
            </div>
            <div class="form-col">
              <label for="grupoSanguineoRh">Grupo Sanguíneo y RH:</label>
              <input id="grupoSanguineoRh" type="text" formControlName="grupoSanguineoRh" placeholder="Ej. O+, A+" class="form-control" />
            </div>
          </div>

          <div class="form-row">
            <div class="form-col checkbox-inline">
              <input id="tieneDiscapacidadPiar" type="checkbox" formControlName="tieneDiscapacidadPiar" />
              <label for="tieneDiscapacidadPiar">
                Estudiante requiere ajustes razonables PIAR (Decreto 1421 de 2017)
              </label>
            </div>
          </div>

          <div class="form-row">
            <div class="form-col">
              <label for="detallesSaludEmergencia">Detalles de Salud y Emergencia (Alergias, condiciones especiales):</label>
              <textarea id="detallesSaludEmergencia" formControlName="detallesSaludEmergencia" rows="2" class="form-control"></textarea>
            </div>
          </div>
        </fieldset>

        <!-- SECCIÓN 3: ACUDIENTE PRINCIPAL OBLIGATORIO (RN-EST-05, UC-PROV-05) -->
        <fieldset class="form-section">
          <legend class="section-legend">3. Acudiente Principal Obligatorio</legend>

          <div class="form-row">
            <div class="form-col">
              <label for="acudienteNombres">Nombres del Acudiente: *</label>
              <input id="acudienteNombres" type="text" formControlName="acudienteNombres" class="form-control" />
              @if (form.get('acudienteNombres')?.touched && form.get('acudienteNombres')?.invalid) {
                <small class="text-danger">Requerido.</small>
              }
            </div>
            <div class="form-col">
              <label for="acudienteApellidos">Apellidos del Acudiente: *</label>
              <input id="acudienteApellidos" type="text" formControlName="acudienteApellidos" class="form-control" />
              @if (form.get('acudienteApellidos')?.touched && form.get('acudienteApellidos')?.invalid) {
                <small class="text-danger">Requerido.</small>
              }
            </div>
          </div>

          <div class="form-row">
            <div class="form-col">
              <label for="acudienteTipoDocumento">Tipo de Documento:</label>
              <select id="acudienteTipoDocumento" formControlName="acudienteTipoDocumento" class="form-control">
                @for (tipo of tiposDocumento; track tipo) {
                  <option [value]="tipo">{{ tipo }}</option>
                }
              </select>
            </div>
            <div class="form-col">
              <label for="acudienteDocumento">Número de Documento: *</label>
              <input id="acudienteDocumento" type="text" formControlName="acudienteDocumento" class="form-control" />
              @if (form.get('acudienteDocumento')?.touched && form.get('acudienteDocumento')?.invalid) {
                <small class="text-danger">Requerido.</small>
              }
            </div>
            <div class="form-col">
              <label for="acudienteTelefono">Teléfono de Contacto: *</label>
              <input id="acudienteTelefono" type="text" formControlName="acudienteTelefono" class="form-control" />
              @if (form.get('acudienteTelefono')?.touched && form.get('acudienteTelefono')?.invalid) {
                <small class="text-danger">Requerido.</small>
              }
            </div>
          </div>

          <div class="form-row">
            <div class="form-col">
              <label for="parentesco">Parentesco: *</label>
              <select id="parentesco" formControlName="parentesco" class="form-control">
                @for (p of parentescos; track p) {
                  <option [value]="p">{{ p }}</option>
                }
              </select>
            </div>
            <div class="form-col">
              <label for="acudienteCorreo">Correo Electrónico:</label>
              <input id="acudienteCorreo" type="email" formControlName="acudienteCorreo" class="form-control" />
            </div>
          </div>

          <div class="checkbox-grid">
            <div class="checkbox-inline">
              <input id="representacionLegal" type="checkbox" formControlName="representacionLegal" />
              <label for="representacionLegal">Ostenta Representación Legal</label>
            </div>
            <div class="checkbox-inline">
              <input id="tieneCustodia" type="checkbox" formControlName="tieneCustodia" />
              <label for="tieneCustodia">Tiene Custodia Legal</label>
            </div>
            <div class="checkbox-inline">
              <input id="autorizadoRetiro" type="checkbox" formControlName="autorizadoRetiro" />
              <label for="autorizadoRetiro">Autorizado para Retiro Físico en Portería</label>
            </div>
            <div class="checkbox-inline">
              <input id="conviveConEstudiante" type="checkbox" formControlName="conviveConEstudiante" />
              <label for="conviveConEstudiante">Convive en el mismo domicilio</label>
            </div>
          </div>
        </fieldset>

        <div class="form-actions">
          <button type="submit" [disabled]="form.invalid || guardando()" class="btn-primary">
            @if (guardando()) {
              <span>Guardando Expediente...</span>
            } @else {
              <span>Formalizar Expediente Escolar</span>
            }
          </button>
        </div>
      </form>
    </div>
  `,
  styles: [`
    .form-container {
      max-width: 900px;
      margin: 0 auto;
      font-family: system-ui, -apple-system, sans-serif;
    }
    .form-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 1.5rem;
    }
    h2 { margin: 0; color: #1e293b; font-size: 1.35rem; }
    .subtitle { margin: 0.25rem 0 0; color: #64748b; font-size: 0.85rem; }
    .btn-secondary {
      padding: 0.45rem 0.85rem;
      background: #e2e8f0;
      color: #334155;
      text-decoration: none;
      border-radius: 4px;
      font-size: 0.85rem;
      font-weight: 500;
    }
    .btn-secondary:hover { background: #cbd5e1; }
    .form-card {
      background: white;
      border: 1px solid #e2e8f0;
      border-radius: 8px;
      padding: 1.5rem;
      box-shadow: 0 1px 3px rgba(0,0,0,0.05);
    }
    .form-section {
      border: 1px solid #e2e8f0;
      border-radius: 6px;
      padding: 1rem 1.25rem;
      margin-bottom: 1.5rem;
    }
    .section-legend {
      font-weight: 600;
      color: #1e293b;
      font-size: 0.95rem;
      padding: 0 0.5rem;
    }
    .form-row {
      display: flex;
      gap: 1rem;
      margin-bottom: 0.85rem;
    }
    .form-col {
      flex: 1;
      display: flex;
      flex-direction: column;
    }
    label {
      font-size: 0.85rem;
      font-weight: 500;
      color: #475569;
      margin-bottom: 0.25rem;
    }
    .form-control {
      padding: 0.5rem 0.75rem;
      border: 1px solid #cbd5e1;
      border-radius: 4px;
      font-size: 0.9rem;
    }
    .form-control:focus {
      outline: none;
      border-color: #3b82f6;
    }
    .checkbox-inline {
      display: flex;
      align-items: center;
      gap: 0.5rem;
    }
    .checkbox-inline label {
      margin: 0;
      cursor: pointer;
    }
    .checkbox-grid {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 0.75rem;
      margin-top: 1rem;
      background: #f8fafc;
      padding: 0.85rem;
      border-radius: 6px;
    }
    .form-actions {
      display: flex;
      justify-content: flex-end;
      margin-top: 1rem;
    }
    .btn-primary {
      padding: 0.65rem 1.5rem;
      background: #2563eb;
      color: white;
      border: none;
      border-radius: 4px;
      font-weight: 600;
      font-size: 0.95rem;
      cursor: pointer;
    }
    .btn-primary:disabled { background: #93c5fd; cursor: not-allowed; }
    .text-danger { color: #dc2626; font-size: 0.78rem; margin-top: 0.2rem; }
  `]
})
export class EstudianteFormComponent {
  private readonly fb = inject(FormBuilder);
  private readonly estudiantesService = inject(EstudiantesService);
  private readonly notificationService = inject(NotificationService);
  private readonly router = inject(Router);

  readonly guardando = signal(false);

  readonly SexoDocumental = SexoDocumental;
  readonly tiposDocumento = Object.values(TipoDocumento);
  readonly parentescos = Object.values(Parentesco);

  readonly form = this.fb.group({
    nombres: ['', [Validators.required]],
    apellidos: ['', [Validators.required]],
    tipoDocumento: [TipoDocumento.Ti, [Validators.required]],
    documento: ['', [Validators.required]],
    fechaNacimiento: ['', [Validators.required]],
    sexo: [SexoDocumental.Masculino],
    direccion: ['', [Validators.required]],
    telefono: [''],
    correo: [''],
    eps: [''],
    grupoSanguineoRh: [''],
    tieneDiscapacidadPiar: [false],
    detallesSaludEmergencia: [''],
    acudienteNombres: ['', [Validators.required]],
    acudienteApellidos: ['', [Validators.required]],
    acudienteTipoDocumento: [TipoDocumento.Cc, [Validators.required]],
    acudienteDocumento: ['', [Validators.required]],
    acudienteTelefono: ['', [Validators.required]],
    acudienteCorreo: [''],
    parentesco: [Parentesco.Madre, [Validators.required]],
    representacionLegal: [true],
    tieneCustodia: [true],
    autorizadoRetiro: [true],
    conviveConEstudiante: [true],
    autorizaUsoImagenFotografiaOpcional: [false]
  });

  guardarExpediente(): void {
    if (this.form.invalid) return;

    this.guardando.set(true);
    const raw = this.form.getRawValue();
    const req: ExpedienteCompletoRequest = {
      nombres: raw.nombres!.trim(),
      apellidos: raw.apellidos!.trim(),
      tipoDocumento: raw.tipoDocumento!,
      documento: raw.documento!.trim(),
      fechaNacimiento: raw.fechaNacimiento!,
      direccion: raw.direccion!.trim(),
      sexo: raw.sexo || undefined,
      correo: raw.correo?.trim() || undefined,
      telefono: raw.telefono?.trim() || undefined,
      eps: raw.eps?.trim() || 'Sin asegurar / En trámite',
      grupoSanguineoRh: raw.grupoSanguineoRh?.trim() || 'O+',
      tieneDiscapacidadPiar: Boolean(raw.tieneDiscapacidadPiar),
      detallesSaludEmergencia: raw.detallesSaludEmergencia?.trim() || undefined,
      acudienteNombres: raw.acudienteNombres!.trim(),
      acudienteApellidos: raw.acudienteApellidos!.trim(),
      acudienteTipoDocumento: raw.acudienteTipoDocumento || undefined,
      acudienteDocumento: raw.acudienteDocumento!.trim(),
      acudienteTelefono: raw.acudienteTelefono!.trim(),
      acudienteCorreo: raw.acudienteCorreo?.trim() || undefined,
      parentesco: raw.parentesco!,
      representacionLegal: Boolean(raw.representacionLegal),
      tieneCustodia: Boolean(raw.tieneCustodia),
      autorizadoRetiro: Boolean(raw.autorizadoRetiro),
      conviveConEstudiante: Boolean(raw.conviveConEstudiante),
      autorizaUsoImagenFotografiaOpcional: Boolean(raw.autorizaUsoImagenFotografiaOpcional)
    };

    this.estudiantesService.apiEstudiantesExpedienteCompletoPost(req).subscribe({
      next: (resp) => {
        this.guardando.set(false);
        this.notificationService.showSuccess(
          resp.mensaje || `Expediente escolar registrado exitosamente para ${resp.estudianteNombreCompleto || req.nombres} (ID: ${resp.estudianteId}).`,
          'Expediente Formalizado'
        );
        this.router.navigate(['/estudiantes']);
      },
      error: () => {
        this.guardando.set(false);
      }
    });
  }
}
