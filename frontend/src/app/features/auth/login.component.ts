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
        <div class="brand-header">
          <div class="logo-circle">🏛️</div>
          <h2>Sistema de Gestión Escolar</h2>
          <p class="subtitle">Plataforma Institucional Integral V2.1</p>
        </div>

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
              placeholder="Ej. secretaria1, coordinador1, profesor1"
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
              placeholder="Contraseña de acceso"
              class="form-control"
            />
            @if (loginForm.get('password')?.touched && loginForm.get('password')?.hasError('required')) {
              <small class="text-danger">La contraseña es obligatoria.</small>
            }
          </div>

          <button type="submit" [disabled]="loginForm.invalid || cargando()" class="btn-primary btn-block">
            @if (cargando()) {
              <span>Iniciando sesión segura...</span>
            } @else {
              <span>Ingresar a la Plataforma</span>
            }
          </button>

          <div class="login-footer">
            <a routerLink="/recuperar-password" class="text-link">¿Olvidó su contraseña? Autoservicio</a>
          </div>

          <!-- ASISTENTE DE DEMOSTRACIÓN -->
          <div class="demo-credentials-box">
            <div class="demo-box-header">
              <span class="demo-tag">SIMULACIÓN</span>
              <h4>Roles Rápidos del Guion</h4>
            </div>
            <p class="demo-subtext">Haga clic en un rol para rellenar automáticamente sus credenciales:</p>
            <div class="demo-buttons">
              <button type="button" class="btn-demo-chip" (click)="rellenarCredenciales('secretaria1', 'Secretaria123*')">
                <strong>Secretaría</strong> (Pasos 1, 2 y 4)
              </button>
              <button type="button" class="btn-demo-chip" (click)="rellenarCredenciales('coordinador1', 'Coord123*')">
                <strong>Coordinador</strong> (Paso 5)
              </button>
              <button type="button" class="btn-demo-chip" (click)="rellenarCredenciales('profesor1', 'Profe123*')">
                <strong>Docente Einstein</strong> (Pasos 6 y 7)
              </button>
              <button type="button" class="btn-demo-chip demo-temporal" (click)="rellenarCredenciales('usuario.temporal', 'Temp123*')">
                <strong>1.er Ingreso</strong> (Seguridad UC-01)
              </button>
              <button type="button" class="btn-demo-chip" (click)="rellenarCredenciales('rector1', 'Rector123*')">
                <strong>Rector</strong>
              </button>
              <button type="button" class="btn-demo-chip" (click)="rellenarCredenciales('admin', 'Admin123*')">
                <strong>Admin</strong>
              </button>
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
      min-height: 100vh;
      background: radial-gradient(circle at 50% 20%, #1e293b 0%, #0f172a 100%);
      padding: 1.5rem;
    }
    .login-card {
      width: 100%;
      max-width: 460px;
      padding: 2.25rem;
      border: 1px solid rgba(255, 255, 255, 0.12);
      border-radius: 16px;
      box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.4), 0 8px 10px -6px rgba(0, 0, 0, 0.3);
      background: white;
    }
    .brand-header {
      text-align: center;
      margin-bottom: 1.5rem;
    }
    .logo-circle {
      display: inline-flex;
      align-items: center;
      justify-content: center;
      width: 52px;
      height: 52px;
      background: #eff6ff;
      border: 2px solid #bfdbfe;
      border-radius: 50%;
      font-size: 1.75rem;
      margin-bottom: 0.75rem;
    }
    h2 { margin: 0 0 0.35rem; color: #0f172a; font-size: 1.45rem; font-weight: 800; letter-spacing: -0.02em; }
    .subtitle { color: #64748b; font-size: 0.88rem; margin: 0; }
    .form-group { margin-bottom: 1.15rem; }
    label { display: block; margin-bottom: 0.35rem; font-weight: 600; color: #334155; font-size: 0.82rem; text-transform: uppercase; letter-spacing: 0.04em; }
    .form-control {
      width: 100%;
      box-sizing: border-box;
      padding: 0.7rem 0.85rem;
      border: 1.5px solid #cbd5e1;
      border-radius: 6px;
      font-size: 0.95rem;
      transition: all 0.15s ease;
    }
    .form-control:focus {
      outline: none;
      border-color: #2563eb;
      box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.15);
    }
    .btn-block { width: 100%; padding: 0.8rem; font-size: 0.95rem; margin-top: 0.5rem; }
    .login-footer { margin-top: 1rem; text-align: center; }
    .text-link { color: #2563eb; text-decoration: none; font-size: 0.88rem; font-weight: 500; }
    .text-link:hover { text-decoration: underline; }
    .demo-credentials-box {
      margin-top: 1.5rem;
      padding: 1rem;
      background: #f8fafc;
      border: 1.5px solid #e2e8f0;
      border-radius: 8px;
    }
    .demo-box-header {
      display: flex;
      align-items: center;
      gap: 0.5rem;
      margin-bottom: 0.35rem;
    }
    .demo-tag {
      background: #2563eb;
      color: white;
      font-size: 0.65rem;
      font-weight: 800;
      padding: 0.15rem 0.4rem;
      border-radius: 4px;
      letter-spacing: 0.05em;
    }
    .demo-box-header h4 {
      margin: 0;
      font-size: 0.85rem;
      color: #1e293b;
      font-weight: 700;
    }
    .demo-subtext {
      font-size: 0.78rem;
      color: #64748b;
      margin-bottom: 0.75rem;
    }
    .demo-buttons {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 0.45rem;
    }
    .btn-demo-chip {
      background: white;
      border: 1px solid #cbd5e1;
      border-radius: 6px;
      padding: 0.45rem 0.5rem;
      font-size: 0.75rem;
      color: #334155;
      cursor: pointer;
      font-weight: 400;
      text-align: left;
      transition: all 0.15s;
    }
    .btn-demo-chip:hover {
      background: #eff6ff;
      border-color: #93c5fd;
      color: #1d4ed8;
      transform: translateY(-1px);
    }
    .demo-temporal {
      background: #fffbeb;
      border-color: #fde68a;
      color: #92400e;
    }
    .demo-temporal:hover {
      background: #fef3c7;
      border-color: #f59e0b;
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
