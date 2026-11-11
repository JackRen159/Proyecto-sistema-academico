import { TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { AuthService } from './auth.service';
import { AutenticacinService } from '../api/generated/api/autenticacin.service';
import { LoginResponse } from '../api/generated/model/login-response';
import { Rol } from '../api/generated/model/rol';

describe('AuthService', () => {
  let service: AuthService;
  let mockAutenticacionService: any;

  beforeEach(() => {
    mockAutenticacionService = {
      apiAuthLoginPost: vi.fn(),
      apiAuthLogoutPost: vi.fn()
    };

    TestBed.configureTestingModule({
      providers: [
        AuthService,
        { provide: AutenticacinService, useValue: mockAutenticacionService }
      ]
    });

    service = TestBed.inject(AuthService);
  });

  it('debe inicializarse sin sesión activa (token null e isAuthenticated false)', () => {
    expect(service.getToken()).toBeNull();
    expect(service.isAuthenticated()).toBe(false);
    expect(service.user()).toBeNull();
  });

  it('debe almacenar el token y usuario en memoria tras un login exitoso', () => {
    const mockResponse: LoginResponse = {
      token: 'jwt.token.mock',
      tokenType: 'Bearer',
      username: 'admin',
      rol: Rol.Administrador,
      expiresIn: 28800
    };

    mockAutenticacionService.apiAuthLoginPost.mockReturnValue(of(mockResponse));

    service.login({ username: 'admin', password: 'password' }).subscribe();

    expect(service.getToken()).toBe('jwt.token.mock');
    expect(service.isAuthenticated()).toBe(true);
    expect(service.user()?.username).toBe('admin');
    expect(service.hasRole(Rol.Administrador)).toBe(true);
    expect(service.hasRole(Rol.Profesor)).toBe(false);
  });

  it('debe limpiar las credenciales de memoria tras logout', () => {
    const mockResponse: LoginResponse = {
      token: 'jwt.token.mock',
      tokenType: 'Bearer',
      username: 'admin',
      rol: Rol.Administrador,
      expiresIn: 28800
    };
    mockAutenticacionService.apiAuthLoginPost.mockReturnValue(of(mockResponse));
    service.login({ username: 'admin', password: 'password' }).subscribe();

    mockAutenticacionService.apiAuthLogoutPost.mockReturnValue(of({ mensaje: 'Sesión cerrada' }));
    service.logout().subscribe();

    expect(service.getToken()).toBeNull();
    expect(service.isAuthenticated()).toBe(false);
    expect(service.user()).toBeNull();
  });
});
