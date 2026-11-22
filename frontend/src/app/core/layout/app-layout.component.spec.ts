import { ComponentFixture, TestBed } from '@angular/core/testing';
import { signal } from '@angular/core';
import { Router, provideRouter } from '@angular/router';
import { of } from 'rxjs';
import { AppLayoutComponent } from './app-layout.component';
import { AuthService } from '../services/auth.service';
import { NotificationService } from '../services/notification.service';
import { Rol } from '../api/generated/model/rol';

describe('AppLayoutComponent', () => {
  let component: AppLayoutComponent;
  let fixture: ComponentFixture<AppLayoutComponent>;
  let router: Router;
  let authServiceSpy: any;
  let notificationServiceSpy: any;

  const mockRolActivo = signal<Rol | null>(Rol.Profesor);
  const mockRolesDisponibles = signal<Rol[]>([Rol.Profesor, Rol.Acudiente]);
  const mockUser = signal<any>({ username: 'profe_juan', rol: Rol.Profesor });

  beforeEach(async () => {
    mockRolActivo.set(Rol.Profesor);
    mockRolesDisponibles.set([Rol.Profesor, Rol.Acudiente]);
    mockUser.set({ username: 'profe_juan', rol: Rol.Profesor });

    authServiceSpy = {
      rolActivo: mockRolActivo,
      rolesDisponibles: mockRolesDisponibles,
      user: mockUser,
      conmutarRol: vi.fn().mockReturnValue(of({})),
      logout: vi.fn().mockReturnValue(of({}))
    };

    notificationServiceSpy = {
      showSuccess: vi.fn(),
      showError: vi.fn(),
      toasts: signal([])
    };

    await TestBed.configureTestingModule({
      imports: [AppLayoutComponent],
      providers: [
        provideRouter([]),
        { provide: AuthService, useValue: authServiceSpy },
        { provide: NotificationService, useValue: notificationServiceSpy }
      ]
    }).compileComponents();

    router = TestBed.inject(Router);
    vi.spyOn(router, 'navigate').mockImplementation(() => Promise.resolve(true));

    fixture = TestBed.createComponent(AppLayoutComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('debe crearse correctamente', () => {
    expect(component).toBeTruthy();
  });

  it('debe filtrar el menú según el rol activo (Profesor)', () => {
    const menu = component.menuFiltrado();
    expect(menu.some(m => m.route === '/calificaciones')).toBe(true);
    expect(menu.some(m => m.route === '/asistencia')).toBe(true);
    expect(menu.some(m => m.route === '/auditoria')).toBe(false); // No permitido para profesor
  });

  it('debe actualizar el menú al cambiar el rol a Administrador', () => {
    mockRolActivo.set(Rol.Administrador);
    fixture.detectChanges();

    const menu = component.menuFiltrado();
    expect(menu.some(m => m.route === '/auditoria')).toBe(true);
    expect(menu.some(m => m.route === '/configuracion')).toBe(true);
  });

  it('debe colapsar y expandir la barra lateral con toggleSidebar()', () => {
    expect(component.sidebarColapsado()).toBe(false);
    component.toggleSidebar();
    expect(component.sidebarColapsado()).toBe(true);
    component.toggleSidebar();
    expect(component.sidebarColapsado()).toBe(false);
  });

  it('debe invocar conmutarRol en AuthService cuando se selecciona un nuevo rol', () => {
    const fakeEvent = {
      target: { value: Rol.Acudiente }
    } as unknown as Event;

    component.onRolCambiado(fakeEvent);

    expect(authServiceSpy.conmutarRol).toHaveBeenCalledWith(Rol.Acudiente);
    expect(notificationServiceSpy.showSuccess).toHaveBeenCalled();
  });

  it('debe cerrar sesión y redirigir a /login', () => {
    component.cerrarSesion();
    expect(authServiceSpy.logout).toHaveBeenCalled();
    expect(router.navigate).toHaveBeenCalledWith(['/login']);
  });
});
