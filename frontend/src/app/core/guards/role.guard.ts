import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { Rol } from '../api/generated/model/rol';

/**
 * Guard de Roles Institucionales (RBAC).
 *
 * Restringe la navegación en UI según los roles autorizados definidos en los datos de la ruta.
 * Nota: Los guards en Angular mejoran la experiencia de usuario; la seguridad real es forzada
 * por Spring Security en el backend.
 */
export const roleGuard: CanActivateFn = (route) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const allowedRoles = route.data?.['roles'] as Rol[] | undefined;

  if (!allowedRoles || allowedRoles.length === 0) {
    return true;
  }

  if (authService.hasAnyRole(allowedRoles)) {
    return true;
  }

  return router.createUrlTree(['/sin-acceso']);
};
