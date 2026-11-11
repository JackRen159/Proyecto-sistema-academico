import { TestBed } from '@angular/core/testing';
import { Router, ActivatedRouteSnapshot, RouterStateSnapshot, UrlTree } from '@angular/router';
import { authGuard } from './auth.guard';
import { AuthService } from '../services/auth.service';

describe('authGuard', () => {
  let mockAuthService: { isAuthenticated: ReturnType<typeof vi.fn> };
  let mockRouter: { createUrlTree: ReturnType<typeof vi.fn> };

  beforeEach(() => {
    mockAuthService = {
      isAuthenticated: vi.fn()
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

  it('debe permitir la navegación si el usuario está autenticado', () => {
    mockAuthService.isAuthenticated.mockReturnValue(true);

    const route = {} as ActivatedRouteSnapshot;
    const state = { url: '/estudiantes' } as RouterStateSnapshot;

    const result = TestBed.runInInjectionContext(() => authGuard(route, state));

    expect(result).toBe(true);
  });

  it('debe redirigir al login si el usuario no está autenticado', () => {
    mockAuthService.isAuthenticated.mockReturnValue(false);
    const mockTree = {} as UrlTree;
    mockRouter.createUrlTree.mockReturnValue(mockTree);

    const route = {} as ActivatedRouteSnapshot;
    const state = { url: '/estudiantes' } as RouterStateSnapshot;

    const result = TestBed.runInInjectionContext(() => authGuard(route, state));

    expect(mockRouter.createUrlTree).toHaveBeenCalledWith(['/login'], {
      queryParams: { returnUrl: '/estudiantes' }
    });
    expect(result).toBe(mockTree);
  });
});
