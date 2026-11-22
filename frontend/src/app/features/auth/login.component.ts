import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../core/services/auth.service';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  template: `
    <div class="login-container">
      <div class="login-card">
        <h2>Sistema de Gestión Escolar</h2>
        <p class="subtitle">Inicio de sesión institucional</p>

        @if (mensajeSesionExpirada()) {
          <div class="alert alert-warning">
            Su sesión ha expirado o fue cerrada. Ingrese sus credenciales nuevamente.
          </div>
        }

        @if (errorMensaje()) {
          <div class="alert alert-danger">
            {{ errorMensaje() }}
          </div>
        }

        <form [formGroup]="loginForm" (ngSubmit)="onSubmit()">
          <div class="form-group">
            <label for="username">Usuario:</label>
            <input
              id="username"
              type="text"
              formControlName="username"
              placeholder="Ej. admin"
              class="form-control"
            />
            @if (loginForm.get('username')?.touched && loginForm.get('username')?.hasError('required')) {
              <small class="text-danger">El usuario es obligatorio.</small>
            }
          </div>

          <div class="form-group">
            <label for="password">Contraseña:</label>
            <input
              id="password"
              type="password"
              formControlName="password"
              placeholder="Contraseña"
              class="form-control"
            />
            @if (loginForm.get('password')?.touched && loginForm.get('password')?.hasError('required')) {
              <small class="text-danger">La contraseña es obligatoria.</small>
            }
          </div>

          <button type="submit" [disabled]="loginForm.invalid || cargando()" class="btn-primary">
            @if (cargando()) {
              <span>Iniciando sesión...</span>
            } @else {
              <span>Ingresar</span>
            }
          </button>

          <div class="login-footer">
            <a routerLink="/recuperar-password" class="text-link">¿Olvidó su contraseña?</a>
          </div>
        </form>
      </div>
    </div>
  `,
  styles: [`
    .login-container {
      display: flex;
      justify-content: center;
      align-items: center;
      min-height: 80vh;
      font-family: system-ui, -apple-system, sans-serif;
    }
    .login-card {
      width: 100%;
      max-width: 400px;
      padding: 2rem;
      border: 1px solid #e2e8f0;
      border-radius: 8px;
      box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
      background: white;
    }
    h2 { margin-top: 0; color: #1e293b; font-size: 1.5rem; text-align: center; }
    .subtitle { color: #64748b; font-size: 0.9rem; text-align: center; margin-bottom: 1.5rem; }
    .form-group { margin-bottom: 1rem; }
    label { display: block; margin-bottom: 0.35rem; font-weight: 500; color: #334155; }
    .form-control {
      width: 100%;
      box-sizing: border-box;
      padding: 0.6rem;
      border: 1px solid #cbd5e1;
      border-radius: 4px;
      font-size: 1rem;
    }
    .btn-primary {
      width: 100%;
      padding: 0.75rem;
      background: #2563eb;
      color: white;
      border: none;
      border-radius: 4px;
      font-size: 1rem;
      cursor: pointer;
      font-weight: 600;
      margin-top: 1rem;
    }
    .btn-primary:disabled { background: #93c5fd; cursor: not-allowed; }
    .login-footer { margin-top: 1.25rem; text-align: center; }
    .text-link { color: #2563eb; text-decoration: none; font-size: 0.88rem; }
    .text-link:hover { text-decoration: underline; }
    .alert { padding: 0.75rem; border-radius: 4px; margin-bottom: 1rem; font-size: 0.9rem; }
    .alert-danger { background: #fee2e2; color: #991b1b; }
    .alert-warning { background: #fef3c7; color: #92400e; }
    .text-danger { color: #dc2626; font-size: 0.8rem; }
  `]
})
export class LoginComponent {
  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);

  readonly cargando = signal(false);
  readonly errorMensaje = signal<string | null>(null);
  readonly mensajeSesionExpirada = signal(false);

  readonly loginForm = this.fb.group({
    username: ['', [Validators.required]],
    password: ['', [Validators.required]]
  });

  constructor() {
    this.route.queryParams.subscribe((params) => {
      if (params['sesionExpirada'] === 'true') {
        this.mensajeSesionExpirada.set(true);
      }
    });
  }

  onSubmit(): void {
    if (this.loginForm.invalid) return;

    this.cargando.set(true);
    this.errorMensaje.set(null);

    const { username, password } = this.loginForm.getRawValue();

    this.authService.login({ username: username!, password: password! }).subscribe({
      next: (response) => {
        this.cargando.set(false);
        if (response.debeCambiarPassword) {
          this.router.navigate(['/primer-ingreso']);
        } else {
          const returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/estudiantes';
          this.router.navigateByUrl(returnUrl);
        }
      },
      error: (err: HttpErrorResponse) => {
        this.cargando.set(false);
        if (err.status === 401) {
          this.errorMensaje.set('Credenciales inválidas. Verifique usuario y contraseña.');
        } else if (err.status === 429) {
          this.errorMensaje.set('Límite de intentos superado (máximo 5 por minuto). Intente en 60 segundos.');
        } else if (err.status === 0) {
          this.errorMensaje.set('No fue posible conectar con el servidor backend (verifique que esté ejecutándose).');
        } else {
          this.errorMensaje.set('Ocurrió un error inesperado al procesar la autenticación.');
        }
      }
    });
  }
}
