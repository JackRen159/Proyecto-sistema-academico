---
name: sistema-escolar-api-openapi
description: Define o modifica endpoints REST, DTO, errores HTTP, versionado OpenAPI 3.x y generación del cliente Angular para el Sistema de Gestión Escolar.
---

# API REST y OpenAPI

## Fuente de verdad

- OpenAPI 3.x versionado es la fuente oficial del contrato HTTP; ejemplo de ubicación: `openapi/openapi.yaml`.
- El contrato define rutas, métodos, parámetros, DTO/schemas, requeridos y opcionales, formatos, seguridad, códigos HTTP, respuestas de error y ejemplos relevantes.
- El backend debe ajustarse al contrato.
- Si se usa `springdoc-openapi` para exponer la especificación en ejecución, verifica que no contradiga al contrato versionado.

## Reglas para diseñar cambios

1. Identifica el caso de uso, actores, permisos y contexto del recurso.
2. Define request/response DTO; no expongas entidades JPA.
3. Documenta respuestas exitosas y fallidas. Distingue `401` (no autenticado) de `403` (autenticado sin permiso).
4. Usa `404` para recurso inexistente y `409` para conflictos de estado/duplicidad cuando corresponda; `400` para solicitud/datos inválidos.
5. Evita filtrar trazas, secretos o datos personales innecesarios. El formato de error puede incluir código interno, mensaje seguro, errores de validación permitidos y correlation/request ID.
6. Analiza compatibilidad antes de retirar, renombrar o cambiar el significado de una ruta/campo.
7. No alteres el contrato público solo para resolver una conveniencia local de UI sin analizar impacto en otros clientes.

## Cliente Angular generado

- Utiliza OpenAPI Generator con el generador `typescript-angular`.
- Separa el código generado, por ejemplo en `src/app/core/api/generated/`.
- No edites manualmente los archivos generados. Modifica el contrato o la configuración del generador y regenera.
- La generación debe ser reproducible y estar documentada/automatizada.
- Los DTO Java siguen existiendo y deben respetar el contrato. El esquema de PostgreSQL no define directamente el contrato público.

## Pruebas y cierre

- Prueba rutas, validación, códigos HTTP, estructura de errores y seguridad documentada.
- Comprueba que backend y contrato coincidan.
- Regenera el cliente cuando cambie el contrato y ejecuta build/tests Angular pertinentes.
- Identifica cambios incompatibles de forma explícita.

## Límites

No inventes endpoints, nombres de campos, paginación, filtros, políticas de idempotencia ni permisos que no estén definidos. Propónlos como decisiones si resultan necesarios.

## Referencia

Documento Maestro V2.0, secciones 2, 3, 6.4, 11, 14–16 y 20.
