import { TestBed } from '@angular/core/testing';
import { HttpClient, provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { authInterceptor } from './auth.interceptor';
import { AuthService } from '../services/auth.service';
import { environment } from '../../../environments/environment';

describe('authInterceptor', () => {
  let http: HttpClient;
  let httpMock: HttpTestingController;
  let mockAuthService: { getToken: ReturnType<typeof vi.fn> };

  beforeEach(() => {
    mockAuthService = {
      getToken: vi.fn()
    };

    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(withInterceptors([authInterceptor])),
        provideHttpClientTesting(),
        { provide: AuthService, useValue: mockAuthService }
      ]
    });

    http = TestBed.inject(HttpClient);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('debe adjuntar la cabecera Authorization: Bearer cuando existe token y la URL es confiable', () => {
    mockAuthService.getToken.mockReturnValue('token-123');

    http.get(`${environment.apiUrl}/api/estudiantes`).subscribe();

    const req = httpMock.expectOne(`${environment.apiUrl}/api/estudiantes`);
    expect(req.request.headers.get('Authorization')).toBe('Bearer token-123');
    req.flush([]);
  });

  it('no debe adjuntar token al endpoint público de login', () => {
    mockAuthService.getToken.mockReturnValue('token-123');

    http.post(`${environment.apiUrl}/api/auth/login`, {}).subscribe();

    const req = httpMock.expectOne(`${environment.apiUrl}/api/auth/login`);
    expect(req.request.headers.has('Authorization')).toBe(false);
    req.flush({});
  });

  it('no debe adjuntar token a URLs externas no confiables', () => {
    mockAuthService.getToken.mockReturnValue('token-123');

    http.get('https://externo.com/api/data').subscribe();

    const req = httpMock.expectOne('https://externo.com/api/data');
    expect(req.request.headers.has('Authorization')).toBe(false);
    req.flush({});
  });
});
