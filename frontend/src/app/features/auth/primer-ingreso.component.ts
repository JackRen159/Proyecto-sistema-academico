import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AutenticacinService } from '../../core/api/generated/api/autenticacin.service';
import { AuthService } from '../../core/services/auth.service';
import { NotificationService } from '../../core/services/notification.service';

@Component({
  selector: 'app-primer-ingreso',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <div class="auth-container">
      <div class="auth-card">
        <h2>Activación de Cuenta y Primer Ingreso</h2>
        <p class="subtitle">Garantías de seguridad y protección de datos (UC-01 A1)</p>

        <div class="alert alert-info">
          <strong>Bienvenido(a) a la plataforma institucional.</strong>
          Por motivos de seguridad, debe cambiar su contraseña temporal por una personal y definitiva, y
          otorgar su consentimiento informado para el tratamiento de datos personales conforme a la Ley 1581 de 2012.
        </div>

        <form [formGroup]="activacionForm" (ngSubmit)="onSubmit()">
          <div class="form-group">
            <label for="passwordActual">Contraseña Temporal / Actual:</label>
            <input
              id="passwordActual"
              type="password"
              formControlName="passwordActual"
              placeholder="Contraseña con la que ingresó"
              class="form-control"
            />
            @if (activacionForm.get('passwordActual')?.touched && activacionForm.get('passwordActual')?.hasError('required')) {
              <small class="text-danger">La contraseña actual es requerida.</small>
            }
          </div>

          <div class="form-group">
            <label for="passwordNuevo">Nueva Contraseña Definitiva:</label>
            <input
              id="passwordNuevo"
              type="password"
              formControlName="passwordNuevo"
              placeholder="Mínimo 8 caracteres"
              class="form-control"
            />
            @if (activacionForm.get('passwordNuevo')?.touched && activacionForm.get('passwordNuevo')?.hasError('minlength')) {
              <small class="text-danger">La contraseña debe tener mínimo 8 caracteres.</small>
            }
          </div>

          <div class="form-group">
            <label for="confirmacionPassword">Confirmar Nueva Contraseña:</label>
            <input
              id="confirmacionPassword"
              type="password"
              formControlName="confirmacionPassword"
              placeholder="Repita la nueva contraseña"
              class="form-control"
            />
            @if (activacionForm.hasError('passwordsNoCoinciden') && activacionForm.get('confirmacionPassword')?.touched) {
              <small class="text-danger">Las contraseñas no coinciden.</small>
            }
          </div>

          <!-- AUTORIZACIÓN LEY 1581 DE 2012 -->
          <div class="habeas-data-box">
            <div class="checkbox-group">
              <input
                id="aceptaPolitica"
                type="checkbox"
                formControlName="aceptaPolitica"
              />
              <label for="aceptaPolitica" class="checkbox-label">
                Autorizo de manera libre, previa, expresa e informada a la institución educativa para recolectar, almacenar y tratar mis datos personales y académicos conforme a la Ley Estatutaria 1581 de 2012 y el Decreto 1377 de 2013, según la Política Institucional de Tratamiento de Datos.
              </label>
            </div>
            @if (activacionForm.get('aceptaPolitica')?.touched && activacionForm.get('aceptaPolitica')?.hasError('required')) {
              <small class="text-danger">Debe aceptar la política de tratamiento de datos para activar su cuenta.</small>
            }
          </div>

          <button type="submit" [disabled]="activacionForm.invalid || cargando()" class="btn-primary">
            @if (cargando()) {
              <span>Activando cuenta...</span>
            } @else {
              <span>Activar Cuenta y Continuar</span>
            }
          </button>
        </form>
      </div>
    </div>
  `,
  styles: [`
    .auth-container {
      display: flex;
      justify-content: center;
      align-items: center;
      min-height: 80vh;
      font-family: system-ui, -apple-system, sans-serif;
      padding: 1rem;
    }
    .auth-card {
      width: 100%;
      max-width: 480px;
      padding: 2rem;
      border: 1px solid #e2e8f0;
      border-radius: 8px;
      box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
      background: white;
    }
    h2 { margin-top: 0; color: #1e293b; font-size: 1.4rem; text-align: center; }
    .subtitle { color: #64748b; font-size: 0.88rem; text-align: center; margin-bottom: 1.25rem; }
    .alert { padding: 0.85rem; border-radius: 6px; font-size: 0.85rem; line-height: 1.4; margin-bottom: 1.25rem; }
    .alert-info { background: #eff6ff; color: #1e40af; border: 1px solid #bfdbfe; }
    .form-group { margin-bottom: 1rem; }
    label { display: block; margin-bottom: 0.35rem; font-weight: 500; color: #334155; font-size: 0.9rem; }
    .form-control {
      width: 100%;
      box-sizing: border-box;
      padding: 0.6rem;
      border: 1px solid #cbd5e1;
      border-radius: 4px;
      font-size: 0.95rem;
    }
    .habeas-data-box {
      background: #f8fafc;
      padding: 0.85rem;
      border-radius: 6px;
      border: 1px solid #e2e8f0;
      margin: 1.2rem 0;
    }
    .checkbox-group {
      display: flex;
      align-items: flex-start;
      gap: 0.6rem;
    }
    .checkbox-label {
      font-size: 0.8rem;
      color: #475569;
      line-height: 1.4;
      cursor: pointer;
      margin: 0;
    }
    .btn-primary {
      width: 100%;
      padding: 0.75rem;
      background: #2563eb;
      color: white;
      border: none;
      border-radius: 4px;
      font-size: 0.95rem;
      cursor: pointer;
      font-weight: 600;
    }
    .btn-primary:disabled { background: #93c5fd; cursor: not-allowed; }
    .text-danger { color: #dc2626; font-size: 0.8rem; display: block; margin-top: 0.25rem; }
  `]
})
export class PrimerIngresoComponent {
  private readonly fb = inject(FormBuilder);
  private readonly autenticacionService = inject(AutenticacinService);
  private readonly authService = inject(AuthService);
  private readonly notificationService = inject(NotificationService);
  private readonly router = inject(Router);

  readonly cargando = signal(false);

  readonly activacionForm = this.fb.group({
    passwordActual: ['', [Validators.required]],
    passwordNuevo: ['', [Validators.required, Validators.minLength(8)]],
    confirmacionPassword: ['', [Validators.required]],
    aceptaPolitica: [false, [Validators.requiredTrue]]
  }, {
    validators: (group) => {
      const p1 = group.get('passwordNuevo')?.value;
      const p2 = group.get('confirmacionPassword')?.value;
      return p1 === p2 ? null : { passwordsNoCoinciden: true };
    }
  });

  onSubmit(): void {
    if (this.activacionForm.invalid) return;

    this.cargando.set(true);
    const req = {
      passwordActual: this.activacionForm.get('passwordActual')?.value!,
      passwordNuevo: this.activacionForm.get('passwordNuevo')?.value!,
      confirmacionPassword: this.activacionForm.get('confirmacionPassword')?.value!
    };

    this.autenticacionService.apiAuthCambiarPasswordPost(req).subscribe({
      next: () => {
        this.cargando.set(false);
        this.notificationService.showSuccess(
          'Su cuenta ha sido activada y su contraseña actualizada correctamente.',
          'Primer Ingreso Completado'
        );
        this.router.navigate(['/estudiantes']);
      },
      error: () => {
        this.cargando.set(false);
      }
    });
  }
}
