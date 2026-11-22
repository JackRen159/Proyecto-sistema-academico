import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AutenticacinService } from '../../core/api/generated/api/autenticacin.service';
import { NotificationService } from '../../core/services/notification.service';

@Component({
  selector: 'app-recuperar-password',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  template: `
    <div class="auth-container">
      <div class="auth-card">
        <h2>Recuperación de Contraseña</h2>
        <p class="subtitle">Autoservicio seguro de restablecimiento de acceso</p>

        @if (paso() === 1) {
          <!-- PASO 1: SOLICITAR TOKEN -->
          <form [formGroup]="solicitudForm" (ngSubmit)="enviarSolicitud()">
            <p class="info-text">
              Ingrese su nombre de usuario o correo electrónico registrado. Si coincide con una cuenta activa,
              se generará un token de recuperación temporal.
            </p>

            <div class="form-group">
              <label for="identificador">Usuario o Correo:</label>
              <input
                id="identificador"
                type="text"
                formControlName="identificador"
                placeholder="Ej. docente@colegio.edu.co"
                class="form-control"
              />
              @if (solicitudForm.get('identificador')?.touched && solicitudForm.get('identificador')?.hasError('required')) {
                <small class="text-danger">El identificador es obligatorio.</small>
              }
            </div>

            <button type="submit" [disabled]="solicitudForm.invalid || cargando()" class="btn-primary">
              @if (cargando()) {
                <span>Enviando solicitud...</span>
              } @else {
                <span>Solicitar Token de Recuperación</span>
              }
            </button>

            <div class="link-footer">
              <a (click)="paso.set(2)" class="text-link">¿Ya tienes un token? Ingresar aquí</a>
              <a routerLink="/login" class="text-link">Volver al inicio de sesión</a>
            </div>
          </form>
        } @else {
          <!-- PASO 2: CONFIRMAR TOKEN Y NUEVA CONTRASEÑA -->
          <form [formGroup]="confirmacionForm" (ngSubmit)="confirmarRecuperacion()">
            <p class="info-text">
              Ingrese el token recibido junto con su nueva contraseña segura.
            </p>

            <div class="form-group">
              <label for="token">Token de Recuperación:</label>
              <input
                id="token"
                type="text"
                formControlName="token"
                placeholder="Token alfanumérico"
                class="form-control"
              />
              @if (confirmacionForm.get('token')?.touched && confirmacionForm.get('token')?.hasError('required')) {
                <small class="text-danger">El token es obligatorio.</small>
              }
            </div>

            <div class="form-group">
              <label for="passwordNuevo">Nueva Contraseña:</label>
              <input
                id="passwordNuevo"
                type="password"
                formControlName="passwordNuevo"
                placeholder="Mínimo 8 caracteres"
                class="form-control"
              />
              @if (confirmacionForm.get('passwordNuevo')?.touched && confirmacionForm.get('passwordNuevo')?.hasError('minlength')) {
                <small class="text-danger">Debe tener al menos 8 caracteres.</small>
              }
            </div>

            <div class="form-group">
              <label for="confirmacionPassword">Confirmar Contraseña:</label>
              <input
                id="confirmacionPassword"
                type="password"
                formControlName="confirmacionPassword"
                placeholder="Repita la nueva contraseña"
                class="form-control"
              />
              @if (confirmacionForm.hasError('passwordsNoCoinciden') && confirmacionForm.get('confirmacionPassword')?.touched) {
                <small class="text-danger">Las contraseñas no coinciden.</small>
              }
            </div>

            <button type="submit" [disabled]="confirmacionForm.invalid || cargando()" class="btn-primary">
              @if (cargando()) {
                <span>Actualizando contraseña...</span>
              } @else {
                <span>Establecer Nueva Contraseña</span>
              }
            </button>

            <div class="link-footer">
              <a (click)="paso.set(1)" class="text-link">← Volver a solicitar token</a>
              <a routerLink="/login" class="text-link">Ir al inicio de sesión</a>
            </div>
          </form>
        }
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
      max-width: 440px;
      padding: 2rem;
      border: 1px solid #e2e8f0;
      border-radius: 8px;
      box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
      background: white;
    }
    h2 { margin-top: 0; color: #1e293b; font-size: 1.4rem; text-align: center; }
    .subtitle { color: #64748b; font-size: 0.88rem; text-align: center; margin-bottom: 1.25rem; }
    .info-text { font-size: 0.85rem; color: #475569; line-height: 1.4; margin-bottom: 1.2rem; background: #f8fafc; padding: 0.75rem; border-radius: 6px; border-left: 3px solid #3b82f6; }
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
      margin-top: 0.5rem;
    }
    .btn-primary:disabled { background: #93c5fd; cursor: not-allowed; }
    .text-danger { color: #dc2626; font-size: 0.8rem; display: block; margin-top: 0.25rem; }
    .link-footer {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 0.5rem;
      margin-top: 1.5rem;
      padding-top: 1rem;
      border-top: 1px solid #f1f5f9;
      font-size: 0.85rem;
    }
    .text-link { color: #2563eb; text-decoration: none; cursor: pointer; }
    .text-link:hover { text-decoration: underline; }
  `]
})
export class RecuperarPasswordComponent {
  private readonly fb = inject(FormBuilder);
  private readonly autenticacionService = inject(AutenticacinService);
  private readonly notificationService = inject(NotificationService);
  private readonly router = inject(Router);

  readonly paso = signal<1 | 2>(1);
  readonly cargando = signal(false);

  readonly solicitudForm = this.fb.group({
    identificador: ['', [Validators.required]]
  });

  readonly confirmacionForm = this.fb.group({
    token: ['', [Validators.required]],
    passwordNuevo: ['', [Validators.required, Validators.minLength(8)]],
    confirmacionPassword: ['', [Validators.required]]
  }, {
    validators: (group) => {
      const p1 = group.get('passwordNuevo')?.value;
      const p2 = group.get('confirmacionPassword')?.value;
      return p1 === p2 ? null : { passwordsNoCoinciden: true };
    }
  });

  enviarSolicitud(): void {
    if (this.solicitudForm.invalid) return;

    this.cargando.set(true);
    const identificador = this.solicitudForm.get('identificador')?.value!;

    this.autenticacionService.apiAuthRecuperarPasswordSolicitarPost({ identificador }).subscribe({
      next: () => {
        this.cargando.set(false);
        this.notificationService.showSuccess(
          'Si la cuenta existe, se ha generado el proceso de restablecimiento. Revise su correo.',
          'Solicitud Procesada'
        );
        this.paso.set(2);
      },
      error: () => {
        this.cargando.set(false);
        // La política de seguridad en tiempo constante anti-enumeración responde genéricamente
        this.notificationService.showInfo(
          'Solicitud enviada. Si la cuenta existe, recibirá las instrucciones.',
          'Proceso de Recuperación'
        );
        this.paso.set(2);
      }
    });
  }

  confirmarRecuperacion(): void {
    if (this.confirmacionForm.invalid) return;

    this.cargando.set(true);
    const req = {
      token: this.confirmacionForm.get('token')?.value!,
      passwordNuevo: this.confirmacionForm.get('passwordNuevo')?.value!,
      confirmacionPassword: this.confirmacionForm.get('confirmacionPassword')?.value!
    };

    this.autenticacionService.apiAuthRecuperarPasswordConfirmarPost(req).subscribe({
      next: () => {
        this.cargando.set(false);
        this.notificationService.showSuccess(
          'Su contraseña ha sido actualizada con éxito. Puede ingresar al sistema.',
          'Contraseña Restablecida'
        );
        this.router.navigate(['/login']);
      },
      error: () => {
        this.cargando.set(false);
      }
    });
  }
}
