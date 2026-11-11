---
name: sistema-escolar-calidad-testing
description: Planifica, crea o revisa pruebas unitarias, integración, seguridad, frontend y criterios de calidad del Sistema de Gestión Escolar.
---

# Calidad y testing

## Principio

Una compilación correcta no demuestra que las reglas de negocio sean correctas. Toda tarea debe recibir pruebas proporcionales a su riesgo; no elimines pruebas para lograr que pase el build.

## Backend

- JUnit + Mockito para reglas de negocio y servicios.
- Pruebas de controladores, DTO, validaciones y traducción de errores HTTP.
- Pruebas de integración de repositorios, persistencia, relaciones, constraints y transacciones.
- Testcontainers con PostgreSQL cuando aporte valor.
- Pruebas de seguridad: anónimo, rol insuficiente, fuera de alcance, credenciales/tokens inválidos o vencidos.
- Pruebas CORS: origen permitido, origen rechazado y preflight `OPTIONS`.
- Pruebas CSRF si se utiliza autenticación basada en cookies.
- Pruebas de concurrencia para invariantes sensibles (por ejemplo, acudiente principal o matrícula duplicada) cuando apliquen.
- Casos límite, conflictos, periodos cerrados y operaciones atómicas.
- Pruebas de migraciones desde esquema vacío y versión anterior cuando corresponda.

## Frontend Angular

- Pruebas de componentes, formularios, validaciones y estados de carga/vacío/error.
- Pruebas de servicios HTTP con el mecanismo compatible con la versión Angular.
- Pruebas de interceptores para credenciales/CSRF solo en destinos autorizados.
- Pruebas de guards para sesión/rol (sin confundirlos con autorización real).
- Presentación diferenciada de 400, 401, 403, 404, 409 y 500.
- Comprobación de compilación del cliente generado contra el contrato OpenAPI y pruebas E2E de flujos prioritarios cuando estén disponibles.

## Calidad general

- Mantén formato y convenciones coherentes.
- Investiga fallos antes de cambiar expectativas.
- No reportes como ejecutada una prueba que no corriste.
- Identifica pruebas omitidas y la razón.
- Revisa código, contrato, migraciones y documentación relacionados.

## Criterios de aceptación

Para cada caso de uso, derivar pruebas de sus precondiciones, flujo principal, alternos, errores, permisos, invariantes y resultados esperados. No inventes escenarios de negocio; registra los no definidos como pendientes.

## Referencia

Documento Maestro V2.0, secciones 8, 10–12, 16–18 y 22.
