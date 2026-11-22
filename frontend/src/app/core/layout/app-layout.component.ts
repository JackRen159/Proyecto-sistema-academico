import { Component, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { Rol } from '../api/generated/model/rol';
import { ToastContainerComponent } from '../components/toast-container.component';
import { NotificationService } from '../services/notification.service';

interface MenuItem {
  label: string;
  route: string;
  icon: string;
  rolesPermitidos: Rol[];
}

@Component({
  selector: 'app-layout',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink, RouterLinkActive, ToastContainerComponent],
  template: `
    <div class="app-shell" [class.sidebar-collapsed]="sidebarColapsado()">
      <app-toast-container></app-toast-container>

      <!-- BARRA SUPERIOR (NAVBAR) -->
      <header class="top-navbar">
        <div class="navbar-left">
          <button
            type="button"
            class="btn-toggle-sidebar"
            (click)="toggleSidebar()"
            title="Colapsar / Expandir menú"
            aria-label="Alternar menú lateral"
          >
            &#9776;
          </button>
          <div class="brand">
            <span class="brand-title">Sistema de Gestión Escolar</span>
            <span class="brand-tag">V2</span>
          </div>
        </div>

        <div class="navbar-right">
          <!-- CONMUTADOR DE ROL ACTIVO (RN-SEG-04, UC-01 A2) -->
          @if (authService.rolesDisponibles().length > 1) {
            <div class="role-switcher">
              <label for="rol-select" class="role-label">Rol Activo:</label>
              <select
                id="rol-select"
                class="role-dropdown"
                [value]="authService.rolActivo()"
                (change)="onRolCambiado($event)"
              >
                @for (rol of authService.rolesDisponibles(); track rol) {
                  <option [value]="rol">{{ formatearNombreRol(rol) }}</option>
                }
              </select>
            </div>
          } @else if (authService.rolActivo()) {
            <div class="single-role-badge">
              <span class="badge">{{ formatearNombreRol(authService.rolActivo()!) }}</span>
            </div>
          }

          <!-- INFORMACIÓN DE USUARIO Y LOGOUT -->
          <div class="user-profile">
            <span class="user-greeting">
              Usuario: <strong>{{ authService.user()?.username }}</strong>
            </span>
            <button
              type="button"
              class="btn-logout"
              (click)="cerrarSesion()"
              title="Cerrar Sesión Segura"
            >
              Salir
            </button>
          </div>
        </div>
      </header>

      <div class="app-body">
        <!-- MENÚ LATERAL (SIDEBAR DINÁMICO POR ROL RBAC) -->
        <aside class="sidebar">
          <div class="sidebar-header">
            <span class="sidebar-caption">NAVEGACIÓN INSTITUCIONAL</span>
          </div>

          <nav class="sidebar-nav">
            @for (item of menuFiltrado(); track item.route) {
              <a
                [routerLink]="item.route"
                routerLinkActive="active-link"
                class="nav-item"
              >
                <span class="nav-icon">{{ item.icon }}</span>
                <span class="nav-label">{{ item.label }}</span>
              </a>
            }
          </nav>
        </aside>

        <!-- ÁREA PRINCIPAL DE CONTENIDO -->
        <main class="content-area">
          <router-outlet></router-outlet>
        </main>
      </div>
    </div>
  `,
  styles: [`
    :host {
      display: block;
      height: 100vh;
      font-family: system-ui, -apple-system, sans-serif;
      color: #1e293b;
      background-color: #f8fafc;
    }

    .app-shell {
      display: flex;
      flex-direction: column;
      height: 100vh;
      overflow: hidden;
    }

    /* NAVBAR */
    .top-navbar {
      height: 60px;
      background: #0f172a;
      color: white;
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 0 1.25rem;
      box-shadow: 0 2px 4px rgba(0,0,0,0.1);
      z-index: 100;
      flex-shrink: 0;
    }
    .navbar-left, .navbar-right {
      display: flex;
      align-items: center;
      gap: 1rem;
    }
    .btn-toggle-sidebar {
      background: none;
      border: none;
      color: #94a3b8;
      font-size: 1.3rem;
      cursor: pointer;
      padding: 0.25rem 0.5rem;
      border-radius: 4px;
    }
    .btn-toggle-sidebar:hover {
      background: #1e293b;
      color: white;
    }
    .brand {
      display: flex;
      align-items: center;
      gap: 0.5rem;
    }
    .brand-title {
      font-weight: 700;
      font-size: 1.05rem;
      letter-spacing: -0.02em;
    }
    .brand-tag {
      background: #3b82f6;
      color: white;
      font-size: 0.7rem;
      font-weight: 700;
      padding: 0.15rem 0.4rem;
      border-radius: 4px;
    }

    .role-switcher {
      display: flex;
      align-items: center;
      gap: 0.5rem;
      background: #1e293b;
      padding: 0.3rem 0.6rem;
      border-radius: 6px;
      font-size: 0.85rem;
    }
    .role-label {
      color: #94a3b8;
      font-size: 0.8rem;
    }
    .role-dropdown {
      background: #0f172a;
      color: #38bdf8;
      border: 1px solid #334155;
      border-radius: 4px;
      padding: 0.25rem 0.5rem;
      font-size: 0.85rem;
      cursor: pointer;
    }
    .single-role-badge .badge {
      background: #1e293b;
      color: #38bdf8;
      padding: 0.35rem 0.65rem;
      border-radius: 6px;
      font-size: 0.85rem;
      font-weight: 600;
      border: 1px solid #334155;
    }
    .user-profile {
      display: flex;
      align-items: center;
      gap: 0.75rem;
      font-size: 0.85rem;
      color: #cbd5e1;
    }
    .btn-logout {
      background: #dc2626;
      color: white;
      border: none;
      padding: 0.35rem 0.75rem;
      border-radius: 4px;
      cursor: pointer;
      font-weight: 500;
      transition: background 0.15s;
    }
    .btn-logout:hover {
      background: #b91c1c;
    }

    /* BODY */
    .app-body {
      display: flex;
      flex: 1;
      overflow: hidden;
    }

    /* SIDEBAR */
    .sidebar {
      width: 250px;
      background: #ffffff;
      border-right: 1px solid #e2e8f0;
      display: flex;
      flex-direction: column;
      flex-shrink: 0;
      transition: width 0.2s ease;
      overflow-y: auto;
    }
    .sidebar-collapsed .sidebar {
      width: 64px;
    }
    .sidebar-header {
      padding: 1rem 1rem 0.5rem;
    }
    .sidebar-caption {
      font-size: 0.7rem;
      font-weight: 700;
      color: #94a3b8;
      letter-spacing: 0.05em;
    }
    .sidebar-collapsed .sidebar-caption {
      display: none;
    }
    .sidebar-nav {
      display: flex;
      flex-direction: column;
      padding: 0.5rem;
      gap: 0.25rem;
    }
    .nav-item {
      display: flex;
      align-items: center;
      gap: 0.75rem;
      padding: 0.6rem 0.75rem;
      border-radius: 6px;
      color: #475569;
      text-decoration: none;
      font-size: 0.9rem;
      font-weight: 500;
      transition: all 0.15s;
    }
    .nav-item:hover {
      background: #f1f5f9;
      color: #0f172a;
    }
    .nav-item.active-link {
      background: #eff6ff;
      color: #2563eb;
      font-weight: 600;
    }
    .nav-icon {
      font-size: 1.1rem;
      width: 24px;
      text-align: center;
    }
    .sidebar-collapsed .nav-label {
      display: none;
    }

    /* MAIN CONTENT AREA */
    .content-area {
      flex: 1;
      overflow-y: auto;
      padding: 1.5rem;
      background: #f8fafc;
    }
  `]
})
export class AppLayoutComponent {
  readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  private readonly notificationService = inject(NotificationService);

  readonly sidebarColapsado = signal(false);

  // Catálogo completo de opciones del menú con filtrado RBAC
  private readonly catalogoMenu: MenuItem[] = [
    {
      label: 'Estudiantes y Acudientes',
      route: '/estudiantes',
      icon: '👥',
      rolesPermitidos: [
        Rol.Administrador,
        Rol.Rector,
        Rol.CoordinadorAcademico,
        Rol.CoordinadorConvivencia,
        Rol.Secretaria
      ]
    },
    {
      label: 'Estructura Académica',
      route: '/estructura',
      icon: '🏛️',
      rolesPermitidos: [
        Rol.Administrador,
        Rol.Rector,
        Rol.CoordinadorAcademico
      ]
    },
    {
      label: 'Matrículas y Traslados',
      route: '/matriculas',
      icon: '📝',
      rolesPermitidos: [
        Rol.Administrador,
        Rol.Rector,
        Rol.CoordinadorAcademico,
        Rol.Secretaria
      ]
    },
    {
      label: 'Profesores y Cargas',
      route: '/profesores',
      icon: '👨‍🏫',
      rolesPermitidos: [
        Rol.Administrador,
        Rol.Rector,
        Rol.CoordinadorAcademico,
        Rol.Secretaria
      ]
    },
    {
      label: 'Configuración y SIEE',
      route: '/configuracion',
      icon: '⚙️',
      rolesPermitidos: [
        Rol.Administrador,
        Rol.Rector,
        Rol.CoordinadorAcademico,
        Rol.CoordinadorConvivencia
      ]
    },
    {
      label: 'Calificaciones y SIEE',
      route: '/calificaciones',
      icon: '📊',
      rolesPermitidos: [
        Rol.Administrador,
        Rol.Rector,
        Rol.CoordinadorAcademico,
        Rol.Profesor,
        Rol.Estudiante,
        Rol.Acudiente
      ]
    },
    {
      label: 'Asistencia y Ausentismo',
      route: '/asistencia',
      icon: '📅',
      rolesPermitidos: [
        Rol.Administrador,
        Rol.Rector,
        Rol.CoordinadorAcademico,
        Rol.CoordinadorConvivencia,
        Rol.Profesor,
        Rol.Estudiante,
        Rol.Acudiente
      ]
    },
    {
      label: 'Horarios Escolares',
      route: '/horarios',
      icon: '🕒',
      rolesPermitidos: [
        Rol.Administrador,
        Rol.Rector,
        Rol.CoordinadorAcademico,
        Rol.Profesor,
        Rol.Estudiante,
        Rol.Acudiente
      ]
    },
    {
      label: 'Cierre y Promoción',
      route: '/cierre-promocion',
      icon: '🎓',
      rolesPermitidos: [
        Rol.Administrador,
        Rol.Rector,
        Rol.CoordinadorAcademico
      ]
    },
    {
      label: 'Convivencia Escolar',
      route: '/convivencia',
      icon: '🛡️',
      rolesPermitidos: [
        Rol.Administrador,
        Rol.Rector,
        Rol.CoordinadorConvivencia,
        Rol.OrientadorEscolar,
        Rol.Profesor,
        Rol.Estudiante,
        Rol.Acudiente
      ]
    },
    {
      label: 'Documentos y Reportes',
      route: '/documentos',
      icon: '📑',
      rolesPermitidos: [
        Rol.Administrador,
        Rol.Rector,
        Rol.CoordinadorAcademico,
        Rol.Secretaria,
        Rol.Estudiante,
        Rol.Acudiente
      ]
    },
    {
      label: 'Auditoría Forense',
      route: '/auditoria',
      icon: '🔒',
      rolesPermitidos: [
        Rol.Administrador,
        Rol.Rector
      ]
    }
  ];

  readonly menuFiltrado = computed(() => {
    const rolActual = this.authService.rolActivo();
    if (!rolActual) return [];
    return this.catalogoMenu.filter(item => item.rolesPermitidos.includes(rolActual));
  });

  toggleSidebar(): void {
    this.sidebarColapsado.update(v => !v);
  }

  onRolCambiado(event: Event): void {
    const select = event.target as HTMLSelectElement;
    const nuevoRol = select.value as Rol;

    if (nuevoRol && nuevoRol !== this.authService.rolActivo()) {
      this.authService.conmutarRol(nuevoRol).subscribe({
        next: () => {
          this.notificationService.showSuccess(
            `Rol conmutado a ${this.formatearNombreRol(nuevoRol)}. Permisos y menú actualizados.`,
            'Cambio de Rol en Sesión'
          );
        },
        error: () => {
          this.notificationService.showError(
            'No fue posible cambiar de rol en la sesión actual.',
            'Error al Conmutar Rol'
          );
        }
      });
    }
  }

  cerrarSesion(): void {
    this.authService.logout().subscribe({
      next: () => {
        this.router.navigate(['/login']);
      }
    });
  }

  formatearNombreRol(rol: Rol): string {
    switch (rol) {
      case Rol.Administrador: return 'Administrador';
      case Rol.Rector: return 'Rector';
      case Rol.CoordinadorAcademico: return 'Coord. Académico';
      case Rol.CoordinadorConvivencia: return 'Coord. Convivencia';
      case Rol.OrientadorEscolar: return 'Orientador Escolar';
      case Rol.Secretaria: return 'Secretaría';
      case Rol.Profesor: return 'Profesor';
      case Rol.Estudiante: return 'Estudiante';
      case Rol.Acudiente: return 'Acudiente';
      case Rol.HistoricoRestringido: return 'Histórico Restringido';
      default: return rol;
    }
  }
}
