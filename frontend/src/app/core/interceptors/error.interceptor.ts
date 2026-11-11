import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';

/**
 * Interceptor de Errores HTTP Centralizado.
 *
 * Maneja respuestas de error semánticas:
 * - 401 Unauthorized: Sesión inválida o expirada. Limpia memoria y redirige a login sin generar bucles.
 * - 403 Forbidden: Permisos insuficientes para el recurso solicitado. No desloguea al usuario.
 * - 404, 409, 429, 500: Propaga el error estructurado.
 */
export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401) {
        // Distinguir petición de login fallida de sesión revocada/expirada
        if (!req.url.includes('/api/auth/login')) {
          authService.clearSession();
          router.navigate(['/login'], {
            queryParams: { sesionExpirada: 'true' }
          });
        }
      } else if (error.status === 403) {
        console.warn('Acceso denegado (403): Permisos insuficientes para la acción.');
      }

      return throwError(() => error);
    })
  );
};
