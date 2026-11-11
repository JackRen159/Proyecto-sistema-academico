import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { environment } from '../../../environments/environment';

/**
 * Interceptor HTTP de Autenticación.
 *
 * Adjunta el token JWT Bearer en memoria exclusivamente a las peticiones
 * dirigidas hacia la API institucional confiable (environment.apiUrl).
 * Nunca expone credenciales ante URLs externas o no autorizadas.
 */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const token = authService.getToken();

  // Validar si el destino es la API confiable
  const isApiTarget = req.url.startsWith(environment.apiUrl) || req.url.startsWith('/api/');
  const isLoginEndpoint = req.url.includes('/api/auth/login');

  if (token && isApiTarget && !isLoginEndpoint) {
    const authReq = req.clone({
      headers: req.headers.set('Authorization', `Bearer ${token}`)
    });
    return next(authReq);
  }

  return next(req);
};
