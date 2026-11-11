---
name: sistema-escolar-frontend-angular
description: Diseña o implementa la SPA Angular del Sistema de Gestión Escolar: componentes, Router, formularios, HttpClient, RxJS, guards, interceptores y cliente OpenAPI.
---

# Frontend Angular

## Rol del cliente

- El cliente inicial V1 es una SPA Angular + TypeScript.
- Node.js, npm y Angular CLI son herramientas de instalación, desarrollo, build y pruebas; Angular se ejecuta en el navegador.
- El frontend y backend son aplicaciones independientes que se comunican por HTTP/HTTPS + REST/JSON.
- Angular no se conecta a PostgreSQL ni contiene reglas críticas como única barrera.

## Organización

- Preparar separación por `core`, `shared` y `features`, adaptada a la estructura real del proyecto.
- Utilizar Angular Router, HttpClient, RxJS y formularios reactivos.
- Seleccionar versiones compatibles de Angular, TypeScript, Node.js/npm y herramientas; fijarlas con archivos de bloqueo.
- Mantener configuración de URL base por entorno. No incluir secretos en `environment`: el bundle puede inspeccionarse.

## Contrato y cliente API

- Consumir la API conforme a OpenAPI 3.x.
- Generar tipos y cliente mediante OpenAPI Generator (`typescript-angular`).
- Mantener el código generado aislado (por ejemplo `core/api/generated/`) y no editarlo a mano.
- Regenerar tras cambios del contrato y documentar/automatizar el proceso.
- Evitar duplicar a mano DTO Java en TypeScript cuando procedan del contrato oficial.

## Seguridad de interfaz

- No guardar JWT, access tokens, refresh tokens ni sesión en `localStorage` o `sessionStorage`.
- Seguir la estrategia de autenticación acordada (cookie segura preferida en el documento; alternativa Bearer solo en memoria).
- Si hay cookies, enviar CSRF conforme a la configuración del backend.
- Los interceptores adjuntan credenciales/CSRF solo a la API confiable; no a destinos arbitrarios.
- Los guards pueden ocultar rutas según sesión/rol para UX, pero Spring Security y servicios backend deciden permisos reales.
- Maneja `401` (no autenticado) distinto de `403` (sin permiso); evita bucles.
- Usa bindings seguros de Angular, evita HTML no confiable y no eludas sanitización sin justificación.

## Experiencia y errores

- Manejo centralizado de errores HTTP, manteniendo mensajes claros para el usuario.
- Contempla estados de carga, éxito, error y ausencia de datos.
- Evita que una petición fallida deje la pantalla cargando indefinidamente.
- No apliques reintentos automáticos indiscriminados a operaciones que podrían duplicar efectos.
- Muestra validaciones cerca de los campos y un resumen accesible de errores.
- No muestres trazas, secretos ni detalles internos.

## Pruebas y scripts

- Pruebas de componentes, formularios, servicios, guards e interceptores.
- Verifica que credenciales/CSRF solo vayan a la API autorizada.
- Prueba presentación de 400, 401, 403, 404, 409 y 500.
- Mantén scripts reproducibles para desarrollo, build, pruebas, lint y generación API.
- Ejecuta build/tests pertinentes y reporta exactamente resultados.

## Límites

No inventes diseño visual, bibliotecas UI, estrategia de estado, rutas, permisos ni formularios/campos que no estén aprobados. Inspecciona primero la versión y estructura real de Angular.

## Referencia

Documento Maestro V2.0, secciones 2, 3, 6.1, 6.4, 10.2–10.5, 11, 14–16 y 18–20.
