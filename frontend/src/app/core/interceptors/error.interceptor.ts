import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';
import { NotificationService } from '../services/notification.service';

/**
 * Interceptor de Errores HTTP Centralizado.
 *
 * Mapea y notifica respuestas semánticas del backend:
 * - 400 Bad Request: Error de validación o motivación faltante.
 * - 401 Unauthorized: Sesión inválida o expirada. Limpia memoria y redirige a login.
 * - 403 Forbidden: Permisos insuficientes para el recurso o restricción legal.
 * - 404 Not Found: Recurso no encontrado.
 * - 409 Conflict: Conflicto con regla académica/convivencial o estado del registro.
 * - 429 Too Many Requests: Límite de tasa excedido (rate limiting).
 * - 500 / 0: Error de servidor o corte de conectividad de red.
 */
export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const notificationService = inject(NotificationService);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      const errorMsg = error.error?.mensaje || error.error?.message;

      if (error.status === 401) {
        // Distinguir petición de login fallida de sesión revocada/expirada
        if (!req.url.includes('/api/auth/login')) {
          authService.clearSession();
          notificationService.showWarning(
            'Su sesión ha expirado o no es válida. Por favor inicie sesión nuevamente.',
            'Sesión Finalizada'
          );
          router.navigate(['/login'], {
            queryParams: { sesionExpirada: 'true' }
          });
        }
      } else if (error.status === 403) {
        notificationService.showError(
          errorMsg || 'No tiene permisos suficientes para realizar esta acción o existe una restricción legal aplicable.',
          'Acceso Restringido (403)'
        );
      } else if (error.status === 400) {
        notificationService.showError(
          errorMsg || 'La solicitud contiene datos inválidos o incompletos. Verifique la información ingresada.',
          'Datos Inválidos (400)'
        );
      } else if (error.status === 404) {
        notificationService.showWarning(
          errorMsg || 'El registro o recurso solicitado no fue encontrado en el sistema.',
          'No Encontrado (404)'
        );
      } else if (error.status === 409) {
        notificationService.showError(
          errorMsg || 'La operación entra en conflicto con las reglas del SIEE, cronograma o estado del registro.',
          'Conflicto de Negocio (409)'
        );
      } else if (error.status === 429) {
        notificationService.showWarning(
          'Ha superado el límite de intentos permitidos. Por favor espere unos momentos antes de reintentar.',
          'Límite Excedido (429)'
        );
      } else if (error.status === 0) {
        notificationService.showError(
          'No fue posible establecer conexión con el servidor. Verifique su red local o el estado del backend.',
          'Error de Conexión'
        );
      } else if (error.status >= 500) {
        notificationService.showError(
          'Ocurrió un error inesperado en el servidor. Si el problema persiste, contacte al soporte institucional.',
          'Error del Servidor (500)'
        );
      }

      return throwError(() => error);
    })
  );
};

