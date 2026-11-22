# Frente 14: Arquitectura Base y Desarrollo Frontend Angular SPA V2

**Documento de especificación y seguimiento de tareas para el Frente 14**  
**Alcance:** Arquitectura base del frontend, Layout maestro con navegación dinámica RBAC, conmutación de rol, notificaciones y pantallas progresivas conectadas al contrato OpenAPI 3.x.

## Specs
S1: Conmutación de rol activo en sesión (`POST /api/auth/conmutar-rol`, `UC-01 A2`, `RN-SEG-04`): el usuario con múltiples roles debe poder cambiar su rol activo en sesión sin perder la autenticación en memoria.
S2: Servicio centralizado de notificaciones y alertas integrado con `errorInterceptor` para traducir códigos HTTP `400`, `401`, `403`, `404`, `409`, `429`, `500` a mensajes orientados al usuario.
S3: Shell y Layout maestro responsivo (`AppLayoutComponent`): Sidebar dinámico por rol RBAC, Navbar con selector de rol activo, datos de usuario autenticado y botón de cierre de sesión.
S4: Enrutamiento base por módulos (`app.routes.ts`) con protección de acceso mediante `authGuard` y `roleGuard`, y suite de pruebas unitarias Angular pasando al 100%.
S5: Recuperación de contraseña (`UC-PROV-03`): flujo de solicitud y confirmación por token sin enumerar cuentas (`POST /api/auth/recuperar-password/*`).
S6: Activación forzosa en primer ingreso (`UC-01 A1`): cambio forzado de contraseña temporal y consentimiento de tratamiento de datos personales conforme a la Ley 1581 de 2012.
S7: Formulario de expediente de estudiante completo (`UC-12`): datos personales, documentos migrantes (`PPT`/`PEP`/`NES`), salud/EPS y marcador confidencial PIAR.
S8: Detalle de estudiante y gestión de acudientes (`UC-PROV-05`): designación obligatoria de acudiente principal atómico, autorizaciones de retiro físico y medidas judiciales `BLOQUEADO_RESTRICCION_LEGAL`.
S9: Estructura Académica (`UC-14`, `UC-PROV-06..08`): años lectivos con ciclo de vida, periodos ponderados al 100% y grados/grupos con control de cupo máximo.
S10: Configuración Institucional y SIEE (`UC-26`, `UC-PROV-13..14`): datos del colegio, versiones inmutables del SIEE con escala continua, simulador de notas y ciclo del Manual de Convivencia.
S11: Matrículas y Novedades (`UC-13`, `UC-PROV-09..10`): formalización con control de cupos, aceptación de reglamentos, y registro de novedades (traslados, retiros, promoción anticipada, deserción).
S12: Profesores y Asignaciones (`UC-15`, `UC-PROV-11..12`): expediente docente con inhabilidades Ley 1918 de 2018, asignación de carga horaria por grupo/asignatura y directores de grupo.
S13: Horarios Escolares y Aulas (`UC-04`, `UC-10`, `UC-PROV-20..22`): catálogo de aulas con aforo, visor de horario semanal, validación de Triple No-Colisión y agenda docente.
S14: Asistencia Escolar y Ausentismo (`UC-19`, `UC-PROV-18..19`): planilla de toma de lista por sesión con evasión interna, radicación de excusas con reserva médica y salidas en portería.

## Tasks
- [x] T1: S1 Implementar conmutación de rol en `AuthService` y refresco de estado reactivo (frontend) — *Completado, 4 pruebas en auth.service.spec.ts*
- [x] T2: S2 Crear servicio de notificaciones `NotificationService`, componente visual Toast y conectar `errorInterceptor` — *Completado, 4 pruebas en notification.service.spec.ts, 3 pruebas en error.interceptor.spec.ts*
- [x] T3: S3 Implementar componente `AppLayoutComponent` con navegación modular dinámica según el rol activo RBAC — *Completado, 6 pruebas en app-layout.component.spec.ts*
- [x] T4: S4 Integrar `AppLayoutComponent` en `app.routes.ts`, adaptar `app.component` y verificar pruebas frontend — *Completado, 26 pruebas pasando al 100%, build limpio*
- [x] T5: S5 Implementar vista de recuperación de contraseña `RecuperarPasswordComponent` (UC-PROV-03) — *Completado, 4 pruebas en recuperar-password.component.spec.ts*
- [x] T6: S6 Implementar flujo de activación de primer ingreso `PrimerIngresoComponent` (UC-01 A1) — *Completado, 3 pruebas en primer-ingreso.component.spec.ts*
- [x] T7: S7 Implementar formulario de registro y expediente completo de estudiante `EstudianteFormComponent` (UC-12) — *Completado, 2 pruebas en estudiante-form.component.spec.ts*
- [x] T8: S8 Implementar vista de detalle del estudiante y administración de acudientes `EstudianteDetalleComponent` (UC-PROV-05) — *Completado, 3 pruebas en estudiante-detalle.component.spec.ts*
- [x] T9: S4 Integrar rutas hijas en `app.routes.ts` y suite completa de pruebas unitarias frontend — *Completado, 38 pruebas pasando al 100%, build de producción limpio (411.41 kB)*
- [x] T10: S9 Implementar componente de gestión de estructura académica `EstructuraAcademicaComponent` (UC-14, UC-PROV-06..08) — *Completado, 3 pruebas en estructura-academica.component.spec.ts*
- [x] T11: S10 Implementar componente de configuración institucional y SIEE `ConfiguracionInstitucionalComponent` (UC-26, UC-PROV-13..14) — *Completado, 3 pruebas en configuracion-institucional.component.spec.ts*
- [x] T12: S4 Registrar rutas `/estructura` y `/configuracion` en `app.routes.ts` y pruebas unitarias frontend de Fase C.2 — *Completado, lazy loading de componentes configurado, 44 pruebas pasando al 100%, bundle inicial optimizado a 293.18 kB*
- [x] T13: S11 Implementar componente de gestión de matrículas y novedades `MatriculasComponent` (UC-13, UC-PROV-09..10) — *Completado, 3 pruebas en matriculas.component.spec.ts*
- [x] T14: S12 Implementar componente de profesores y asignaciones docentes `ProfesoresComponent` (UC-15, UC-PROV-11..12) — *Completado, 4 pruebas en profesores.component.spec.ts*
- [x] T15: S4 Registrar rutas `/matriculas` y `/profesores` en `app.routes.ts` y pruebas unitarias frontend de Fase C.3 — *Completado, 51 pruebas unitarias pasando al 100%, bundle de producción limpio (293.37 kB inicial)*
- [x] T16: S13 Implementar componente de gestión de horarios escolares y aulas `HorariosComponent` (UC-04, UC-10, UC-PROV-20..22) — *Completado, 3 pruebas en horarios.component.spec.ts*
- [x] T17: S14 Implementar componente de control de asistencia escolar y excusas `AsistenciaComponent` (UC-19, UC-PROV-18..19) — *Completado, 5 pruebas en asistencia.component.spec.ts*
- [x] T18: S4 Registrar rutas `/horarios` y `/asistencia` en `app.routes.ts` y pruebas unitarias frontend de Fase C.4 — *Completado, 59 pruebas unitarias pasando al 100%, bundle de producción limpio (293.57 kB inicial)*

## Log
- L1: "procede con el plan de accion propuesto para dejar el proyecto al 100%, comienza por la fase A"
- L2: "procede con la fase B"
- L3: "procede" (autorización de Fase C.1 - Módulos 01 y 02 en Frontend)
- L4: "si por favor" (commit/push C.1 y autorización de Fase C.2 - Módulos 03 y 06 en Frontend)
- L5: "si por favor" (commit/push C.2 y autorización de Fase C.3 - Módulos 04 y 05 en Frontend)
- L6: "has el commit y subelo a github" (commit/push C.3 y autorización de Fase C.4 - Módulos 09 y 08 en Frontend)
