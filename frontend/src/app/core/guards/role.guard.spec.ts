import { TestBed } from '@angular/core/testing';
import { Router, ActivatedRouteSnapshot, UrlTree } from '@angular/router';
import { roleGuard } from './role.guard';
import { AuthService } from '../services/auth.service';
import { Rol } from '../api/generated/model/rol';

describe('roleGuard', () => {
  let mockAuthService: { hasAnyRole: ReturnType<typeof vi.fn> };
  let mockRouter: { createUrlTree: ReturnType<typeof vi.fn> };

  beforeEach(() => {
    mockAuthService = {
      hasAnyRole: vi.fn()
    };
    mockRouter = {
      createUrlTree: vi.fn()
    };

    TestBed.configureTestingModule({
      providers: [
        { provide: AuthService, useValue: mockAuthService },
        { provide: Router, useValue: mockRouter }
      ]
    });
  });

  it('debe permitir la navegación si la ruta no especifica roles permitidos', () => {
    const route = { data: {} } as unknown as ActivatedRouteSnapshot;

    const result = TestBed.runInInjectionContext(() => roleGuard(route, {} as any));

    expect(result).toBe(true);
  });

  it('debe permitir la navegación si el usuario posee el rol permitido', () => {
    mockAuthService.hasAnyRole.mockReturnValue(true);
    const route = {
      data: { roles: [Rol.Administrador, Rol.Rector] }
    } as unknown as ActivatedRouteSnapshot;

    const result = TestBed.runInInjectionContext(() => roleGuard(route, {} as any));

    expect(mockAuthService.hasAnyRole).toHaveBeenCalledWith([Rol.Administrador, Rol.Rector]);
    expect(result).toBe(true);
  });

  it('debe redirigir a /sin-acceso si el usuario no tiene los roles permitidos', () => {
    mockAuthService.hasAnyRole.mockReturnValue(false);
    const mockTree = {} as UrlTree;
    mockRouter.createUrlTree.mockReturnValue(mockTree);

    const route = {
      data: { roles: [Rol.Administrador] }
    } as unknown as ActivatedRouteSnapshot;

    const result = TestBed.runInInjectionContext(() => roleGuard(route, {} as any));

    expect(mockRouter.createUrlTree).toHaveBeenCalledWith(['/sin-acceso']);
    expect(result).toBe(mockTree);
  });
});
