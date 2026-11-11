---
name: codegraph
description: CodeGraph static code analysis and structural symbol navigation for call paths, callers, callees, and dependencies.
---

# CodeGraph — Navegación Estructural de Código

Usa esta skill para mapear relaciones de código, impacto de cambios y jerarquías de llamadas en el repositorio sin depender de lecturas lineales o búsquedas difusas de texto.

## Casos de uso
- Rastrear llamadores (`callers`) y dependientes (`callees`) de métodos de servicio o repositorios.
- Evaluar el impacto de modificar entidades del dominio o firmas de métodos en la API.
- Navegar jerarquías de herencia, interfaces de Spring Data JPA y mappers DTO.

## Instrucciones de uso
1. Para inicializar el grafo en el repositorio: ejecutar `codegraph` con la operación `init` en la raíz del workspace.
2. Usar operaciones `query` y `explore` para consultar símbolos específicos y rutas de llamada.
