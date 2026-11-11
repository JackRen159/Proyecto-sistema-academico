import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { EstudiantesService } from '../../core/api/generated/api/estudiantes.service';
import { EstudianteResponse } from '../../core/api/generated/model/estudiante-response';
import { PageMetadata } from '../../core/api/generated/model/page-metadata';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-estudiantes-list',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="estudiantes-container">
      <header class="header">
        <div>
          <h2>Módulo de Estudiantes</h2>
          <p class="subtitle">Gestión y consulta de estudiantes registrados</p>
        </div>
        <div class="user-info">
          <span>Usuario: <strong>{{ authService.user()?.username }}</strong> ({{ authService.user()?.rol }})</span>
          <button (click)="logout()" class="btn-logout">Cerrar Sesión</button>
        </div>
      </header>

      @if (cargando()) {
        <div class="state-card">
          <p>Cargando listado de estudiantes...</p>
        </div>
      } @else if (errorMensaje()) {
        <div class="state-card alert-danger">
          <p>{{ errorMensaje() }}</p>
          <button (click)="cargarEstudiantes(paginaActual())" class="btn-retry">Reintentar</button>
        </div>
      } @else if (estudiantes().length === 0) {
        <div class="state-card">
          <p>No se encontraron estudiantes registrados en el sistema.</p>
        </div>
      } @else {
        <div class="table-card">
          <table>
            <thead>
              <tr>
                <th>Documento</th>
                <th>Apellidos</th>
                <th>Nombres</th>
                <th>Correo</th>
                <th>Teléfono</th>
                <th>Estado</th>
              </tr>
            </thead>
            <tbody>
              @for (estudiante of estudiantes(); track estudiante.id) {
                <tr>
                  <td><code>{{ estudiante.documento }}</code></td>
                  <td>{{ estudiante.apellidos }}</td>
                  <td>{{ estudiante.nombres }}</td>
                  <td>{{ estudiante.correo || '-' }}</td>
                  <td>{{ estudiante.telefono || '-' }}</td>
                  <td>
                    <span [class]="estudiante.activo ? 'badge-success' : 'badge-danger'">
                      {{ estudiante.activo ? 'Activo' : 'Inactivo' }}
                    </span>
                  </td>
                </tr>
              }
            </tbody>
          </table>

          <div class="pagination-bar">
            <span>
              Total: {{ paginacion()?.totalElements || 0 }} estudiante(s) | Página {{ (paginacion()?.number ?? 0) + 1 }} de {{ paginacion()?.totalPages ?? 1 }}
            </span>
            <div class="pagination-buttons">
              <button
                [disabled]="(paginacion()?.number ?? 0) === 0"
                (click)="cargarEstudiantes(paginaActual() - 1)"
                class="btn-page"
              >
                Anterior
              </button>
              <button
                [disabled]="(paginacion()?.number ?? 0) + 1 >= (paginacion()?.totalPages ?? 1)"
                (click)="cargarEstudiantes(paginaActual() + 1)"
                class="btn-page"
              >
                Siguiente
              </button>
            </div>
          </div>
        </div>
      }
    </div>
  `,
  styles: [`
    .estudiantes-container {
      max-width: 1000px;
      margin: 2rem auto;
      padding: 0 1rem;
      font-family: system-ui, -apple-system, sans-serif;
    }
    .header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 2rem;
      border-bottom: 1px solid #e2e8f0;
      padding-bottom: 1rem;
    }
    h2 { margin: 0; color: #1e293b; }
    .subtitle { margin: 0.25rem 0 0; color: #64748b; font-size: 0.9rem; }
    .user-info { display: flex; align-items: center; gap: 1rem; font-size: 0.9rem; color: #334155; }
    .btn-logout {
      padding: 0.4rem 0.8rem;
      background: #ef4444;
      color: white;
      border: none;
      border-radius: 4px;
      cursor: pointer;
      font-weight: 500;
    }
    .table-card {
      background: white;
      border: 1px solid #e2e8f0;
      border-radius: 8px;
      overflow: hidden;
      box-shadow: 0 1px 3px rgba(0,0,0,0.05);
    }
    table { width: 100%; border-collapse: collapse; text-align: left; }
    th { background: #f8fafc; color: #475569; font-weight: 600; padding: 0.75rem 1rem; border-bottom: 1px solid #e2e8f0; }
    td { padding: 0.75rem 1rem; border-bottom: 1px solid #f1f5f9; color: #1e293b; }
    .badge-success { background: #dcfce7; color: #166534; padding: 0.2rem 0.5rem; border-radius: 4px; font-size: 0.8rem; font-weight: 500; }
    .badge-danger { background: #fee2e2; color: #991b1b; padding: 0.2rem 0.5rem; border-radius: 4px; font-size: 0.8rem; font-weight: 500; }
    .pagination-bar {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 0.75rem 1rem;
      background: #f8fafc;
      font-size: 0.85rem;
      color: #64748b;
    }
    .pagination-buttons { display: flex; gap: 0.5rem; }
    .btn-page {
      padding: 0.35rem 0.75rem;
      border: 1px solid #cbd5e1;
      background: white;
      border-radius: 4px;
      cursor: pointer;
    }
    .btn-page:disabled { opacity: 0.5; cursor: not-allowed; }
    .state-card {
      padding: 2rem;
      text-align: center;
      background: white;
      border: 1px solid #e2e8f0;
      border-radius: 8px;
      color: #64748b;
    }
    .alert-danger { background: #fef2f2; border-color: #fecaca; color: #991b1b; }
    .btn-retry { margin-top: 0.5rem; padding: 0.4rem 0.8rem; background: #2563eb; color: white; border: none; border-radius: 4px; cursor: pointer; }
  `]
})
export class EstudiantesListComponent implements OnInit {
  private readonly estudiantesService = inject(EstudiantesService);
  readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  readonly cargando = signal(true);
  readonly errorMensaje = signal<string | null>(null);
  readonly estudiantes = signal<EstudianteResponse[]>([]);
  readonly paginacion = signal<PageMetadata | undefined>(undefined);
  readonly paginaActual = signal(0);

  ngOnInit(): void {
    this.cargarEstudiantes(0);
  }

  cargarEstudiantes(page: number): void {
    this.cargando.set(true);
    this.errorMensaje.set(null);

    this.estudiantesService.apiEstudiantesGet(page, 10, 'apellidos,asc').subscribe({
      next: (response) => {
        this.cargando.set(false);
        this.estudiantes.set(response.content || []);
        this.paginacion.set(response.page);
        this.paginaActual.set(page);
      },
      error: (err) => {
        this.cargando.set(false);
        if (err.status === 403) {
          this.errorMensaje.set('No cuenta con los permisos necesarios para consultar estudiantes.');
        } else {
          this.errorMensaje.set('Error al recuperar el listado de estudiantes.');
        }
      }
    });
  }

  logout(): void {
    this.authService.logout().subscribe(() => {
      this.router.navigate(['/login']);
    });
  }
}
