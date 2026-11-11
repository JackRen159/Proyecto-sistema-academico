---
name: context7
description: Context7 documentation lookup for current libraries, APIs, Spring Boot, Java, Angular, and framework standards.
---

# Context7 — Documentación y Referencias Técnicas

Usa esta skill para consultar documentación viva y actualizada de librerías y frameworks a través de Context7 en lugar de asumir APIs desactualizadas.

## Casos de uso
- Consultar especificaciones y sintaxis exacta de Spring Boot 3.x, Spring Data JPA, Spring Security, Hibernate y Java 17/21.
- Validar componentes y contratos de Angular (v17/v18/v19+), Standalone components, Signals y RxJS.
- Resolver dudas sobre OpenAPI 3.0 / Swagger y validaciones Jakarta (`jakarta.validation.*`).

## Reglas de fidelidad
1. Verifica siempre versiones declaradas en `pom.xml` o `package.json` antes de consultar.
2. Extrae contratos y ejemplos mínimos sin sobrediseñar dependencias no requeridas por el proyecto.
3. Prioriza la arquitectura definida en `sistema-escolar-arquitectura-backend` y `sistema-escolar-api-openapi`.
