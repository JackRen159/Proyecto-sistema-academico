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

          <div class="demo-credentials-box">
            <h4>Acceso Rápido para Demostración</h4>
            <div class="demo-buttons">
              <button type="button" class="btn-demo-chip" (click)="rellenarCredenciales('rector1', 'Rector123*')">Rector</button>
              <button type="button" class="btn-demo-chip" (click)="rellenarCredenciales('coordinador1', 'Coord123*')">Coordinador</button>
              <button type="button" class="btn-demo-chip" (click)="rellenarCredenciales('secretaria1', 'Secretaria123*')">Secretaría</button>
              <button type="button" class="btn-demo-chip" (click)="rellenarCredenciales('profesor1', 'Profe123*')">Docente</button>
              <button type="button" class="btn-demo-chip" (click)="rellenarCredenciales('estudiante1', 'Estudiante123*')">Estudiante</button>
              <button type="button" class="btn-demo-chip" (click)="rellenarCredenciales('admin', 'Admin123*')">Admin</button>
              <button type="button" class="btn-demo-chip demo-temporal" (click)="rellenarCredenciales('usuario.temporal', 'Temp123*')">1.er Ingreso</button>
            </div>
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
    .demo-credentials-box {
      margin-top: 1.5rem;
      padding-top: 1rem;
      border-top: 1px dashed #cbd5e1;
    }
    .demo-credentials-box h4 {
      margin: 0 0 0.5rem;
      font-size: 0.82rem;
      color: #64748b;
      text-transform: uppercase;
      letter-spacing: 0.5px;
      text-align: center;
    }
    .demo-buttons {
      display: flex;
      flex-wrap: wrap;
      gap: 0.35rem;
      justify-content: center;
    }
    .btn-demo-chip {
      background: #f1f5f9;
      border: 1px solid #cbd5e1;
      border-radius: 4px;
      padding: 0.25rem 0.5rem;
      font-size: 0.78rem;
      color: #334155;
      cursor: pointer;
      font-weight: 500;
      transition: all 0.15s;
    }
    .btn-demo-chip:hover {
      background: #e2e8f0;
      border-color: #94a3b8;
    }
    .demo-temporal {
      background: #fef3c7;
      border-color: #fde68a;
      color: #92400e;
    }
    .demo-temporal:hover {
      background: #fde68a;
    }
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

  rellenarCredenciales(usuario: string, clave: string): void {
    this.loginForm.patchValue({
      username: usuario,
      password: clave
    });
  }

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
