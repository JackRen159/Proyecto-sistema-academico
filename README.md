# Sistema de Gestión Escolar - Backend

Monolito modular en Spring Boot 4 / Java 21 para la gestión escolar, administración académica, control de acceso RBAC y vinculación de acudientes.

## Contrato de API y Documentación (OpenAPI 3.x)

- **Fuente oficial del contrato (versionado):** [`openapi/openapi.yaml`](openapi/openapi.yaml)
- **Especificación viva en ejecución:** `http://localhost:8080/v3/api-docs` (o `/v3/api-docs.yaml`)
- **Swagger UI interactivo:** `http://localhost:8080/swagger-ui.html`

### Esquema de Seguridad
La API utiliza autenticación stateless basada en **JWT (Bearer token)**.
1. Obtener token mediante `POST /api/auth/login`.
2. En Swagger UI o clientes HTTP, incluir la cabecera:
   ```http
   Authorization: Bearer <token_jwt>
   ```

## Módulos Implementados

- **Autenticación y Seguridad:** `/api/auth` (login con rate limiting de 5 intentos/min, logout con revocación en servidor, perfil actual).
- **Estudiantes:** `/api/estudiantes` (CRUD, paginación estable, desactivación lógica y restauración).
- **Acudientes:** `/api/acudientes` (CRUD, datos de contacto, ocupación, desactivación lógica).
- **Vinculaciones:** `/api/vinculaciones` (asociación estudiante-acudiente con parentesco, facultades y garantía de acudiente principal único a nivel de base de datos).

## Ejecución de Pruebas

```bash
./mvnw test
```
---

## Documentación y Especificación V2

La especificación funcional consolidada (12 módulos, 56 casos de uso y reglas transversales `RN-01` a `RN-12`) y la guía operativa para agentes están centralizadas en formato Markdown:

- **Guía de entrada del repositorio:** `AGENTS.md`
- **Documento Maestro (arquitectura global):** `docs/arquitectura/Documento_Maestro_Sistema_Escolar_V2.1.md`
- **Guía general y reglas transversales:** `docs/especificacion/00_Guia_General_y_Reglas_Transversales_v2.md`
- **Índice y secuencia de desarrollo (56 UCs):** `docs/especificacion/00_Indice_y_Secuencia_de_Desarrollo_v2.md`
- **Módulos funcionales (`01` a `12`):** `docs/especificacion/modulos/`
- **Plan maestro de evolución (`V11+`):** `odd/tasks/PLAN_MAESTRO_V2.md`

### Estado base implementado (`V1` – `V10`)
- Backend Spring Boot + PostgreSQL + Flyway (`V1`–`V10`): Autenticación JWT, Estudiantes, Acudientes y Vínculos, Estructura académica, Profesores y Asignaciones, Matrículas, Calificaciones SIEE, Asistencia y Documentos académicos base.
- Próximos frentes de evolución V2 (`V11+`): Hardening Seguridad/Configuración/Auditoría global (Frente 9), Horarios (Frente 10), Cierre y Promoción (Frente 11), Convivencia Escolar (Frente 12), cierre de brechas V1→V2 (Frente 13) y Frontend Angular SPA completo (Frente 14).
