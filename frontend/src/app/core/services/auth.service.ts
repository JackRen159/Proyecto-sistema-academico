import { Injectable, computed, signal } from '@angular/core';
import { Observable, tap, catchError, of, finalize } from 'rxjs';
import { AutenticacinService } from '../api/generated/api/autenticacin.service';
import { LoginRequest } from '../api/generated/model/login-request';
import { LoginResponse } from '../api/generated/model/login-response';
import { UsuarioResponse } from '../api/generated/model/usuario-response';
import { Rol } from '../api/generated/model/rol';

/**
 * Servicio de Autenticación en memoria.
 *
 * REGLA ESTRICTA DE SEGURIDAD ARQUITECTÓNICA:
 * El token JWT reside exclusivamente en memoria volátil de la aplicación.
 * Bajo ninguna circunstancia se almacena en localStorage ni sessionStorage para prevenir
 * vulnerabilidades de exfiltración de credenciales por ataques XSS.
 */
@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly tokenSignal = signal<string | null>(null);
  private readonly userSignal = signal<UsuarioResponse | null>(null);

  readonly token = this.tokenSignal.asReadonly();
  readonly user = this.userSignal.asReadonly();
  readonly isAuthenticated = computed(() => !!this.tokenSignal());

  constructor(private readonly autenticacionService: AutenticacinService) {}

  login(credentials: LoginRequest): Observable<LoginResponse> {
    return this.autenticacionService.apiAuthLoginPost(credentials).pipe(
      tap((response: LoginResponse) => {
        if (response.token) {
          this.tokenSignal.set(response.token);
          this.userSignal.set({
            username: response.username,
            rol: response.rol,
            activo: true
          });
        }
      })
    );
  }

  logout(): Observable<unknown> {
    return this.autenticacionService.apiAuthLogoutPost().pipe(
      finalize(() => {
        this.clearSession();
      }),
      catchError(() => {
        this.clearSession();
        return of(null);
      })
    );
  }

  clearSession(): void {
    this.tokenSignal.set(null);
    this.userSignal.set(null);
  }

  getToken(): string | null {
    return this.tokenSignal();
  }

  hasRole(expectedRol: Rol): boolean {
    const current = this.userSignal();
    return current?.rol === expectedRol;
  }

  hasAnyRole(expectedRoles: Rol[]): boolean {
    const current = this.userSignal();
    if (!current?.rol) return false;
    return expectedRoles.includes(current.rol);
  }
}
