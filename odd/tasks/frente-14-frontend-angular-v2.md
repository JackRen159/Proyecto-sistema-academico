# Frente 14: Arquitectura Base y Desarrollo Frontend Angular SPA V2

**Documento de especificación y seguimiento de tareas para el Frente 14**  
**Alcance:** Arquitectura base del frontend, Layout maestro con navegación dinámica RBAC, conmutación de rol, notificaciones y pantallas progresivas conectadas al contrato OpenAPI 3.x.

## Specs
S1: Conmutación de rol activo en sesión (`POST /api/auth/conmutar-rol`, `UC-01 A2`, `RN-SEG-04`): el usuario con múltiples roles debe poder cambiar su rol activo en sesión sin perder la autenticación en memoria.
S2: Servicio centralizado de notificaciones y alertas integrado con `errorInterceptor` para traducir códigos HTTP `400`, `401`, `403`, `404`, `409`, `429`, `500` a mensajes orientados al usuario.
S3: Shell y Layout maestro responsivo (`AppLayoutComponent`): Sidebar dinámico por rol RBAC, Navbar con selector de rol activo, datos de usuario autenticado y botón de cierre de sesión.
S4: Enrutamiento base por módulos (`app.routes.ts`) con protección de acceso mediante `authGuard` y `roleGuard`, y suite de pruebas unitarias Angular pasando al 100%.

## Tasks
- [x] T1: S1 Implementar conmutación de rol en `AuthService` y refresco de estado reactivo (frontend) — *Completado, 4 pruebas en auth.service.spec.ts*
- [x] T2: S2 Crear servicio de notificaciones `NotificationService`, componente visual Toast y conectar `errorInterceptor` — *Completado, 4 pruebas en notification.service.spec.ts, 3 pruebas en error.interceptor.spec.ts*
- [x] T3: S3 Implementar componente `AppLayoutComponent` con navegación modular dinámica según el rol activo RBAC — *Completado, 6 pruebas en app-layout.component.spec.ts*
- [x] T4: S4 Integrar `AppLayoutComponent` en `app.routes.ts`, adaptar `app.component` y verificar pruebas frontend — *Completado, 26 pruebas pasando al 100%, build limpio*

## Log
- L1: "procede con el plan de accion propuesto para dejar el proyecto al 100%, comienza por la fase A"
- L2: "procede con la fase B"
