# AGENTS.md — Sistema de Gestión Escolar

## 1. Arquitectura y estado real confirmado
- **Backend:** Monolito modular Java 21 + Spring Boot + Spring Data JPA + PostgreSQL + Flyway + Spring Security + OpenAPI 3.x (`openapi/openapi.yaml`). Paquete raíz: `com.jackren.sistemaacademico`.
- **Frontend:** Angular + TypeScript (`frontend/`), con cliente tipado generado desde OpenAPI en `frontend/src/app/core/api/generated/` (**prohibido editar archivos generados manualmente**).
- **Línea base implementada:** Migraciones Flyway `V1` a `V10` y frentes `odd/tasks/frente-2` a `frente-8` completados (núcleo administrativo base, calificaciones SIEE base, asistencia base y documentos base).
- **Evolución de esquema:** Toda migración nueva inicia a partir de **`V11__*.sql`** (nunca modificar migraciones `V1`–`V10` ya aplicadas).

## 2. Fuente de verdad documental (Carga Progresiva Obligatoria)
NO leas los archivos en `docs/entregables-docx/` ni en `docs/arquitectura/archivo-historico/`. Usa exclusivamente los Markdown vigentes:
1. **Arquitectura global:** `docs/arquitectura/Documento_Maestro_Sistema_Escolar_V2.1.md`
2. **Reglas transversales y actores:** `docs/especificacion/00_Guia_General_y_Reglas_Transversales_v2.md`
3. **Secuencia y dependencias (56 UCs):** `docs/especificacion/00_Indice_y_Secuencia_de_Desarrollo_v2.md` y `docs/especificacion/MAPA_UC_Y_DEPENDENCIAS_v2.md`
4. **Especificación por módulo:** `docs/especificacion/modulos/01_..._v2.md` a `12_..._v2.md`
5. **Hoja de ruta operativa:** `odd/tasks/PLAN_MAESTRO_V2.md` y el frente activo en `odd/tasks/frente-*.md`

## 3. Protocolo obligatorio de trabajo (agente único)
Antes de diseñar o escribir código:
1. Consulta `.atl/skill-registry.md` como índice y lee directamente **solo** los archivos `SKILL.md` pertinentes a la tarea (incluye siempre `.agents/skills/sistema-escolar-maestro/SKILL.md` + la skill del módulo objetivo).
2. Lee únicamente el archivo `docs/especificacion/modulos/XX_*_v2.md` correspondiente al módulo que vas a trabajar (**no cargues los 12 módulos a la vez**).
3. Inspecciona el código real en `src/main/`, `src/test/`, `frontend/` y `openapi/openapi.yaml` para confirmar el estado actual antes de proponer cambios.
4. Convierte cada fila `A#` (flujos alternos) y `E#` (excepciones con código HTTP esperado) del módulo `.md` en validaciones de backend, contrato OpenAPI y pruebas automatizadas.
5. Si trabajas sobre un frente en `odd/tasks/`, actualiza las casillas `- [x]` únicamente tras verificar que el cambio compila y sus pruebas pasan.

## 4. Invariantes no negociables
- La lógica de negocio crítica y la autorización contextual (RBAC + validación de recurso/vínculo/asignación) se ejecutan siempre en el backend Spring Boot; nunca confiar en IDs enviados por el cliente ni en guards de Angular.
- Usar `BigDecimal` con la escala y modo de redondeo del SIEE vigente en cálculos académicos; nunca codificar notas mínimas, pesos ni umbrales fijos.
- Nunca guardar tokens ni credenciales en `localStorage` o `sessionStorage`, ni registrar contraseñas, hashes, JWT, cookies o secretos en logs o auditoría.
- Prohibido el borrado físico de expedientes, matrículas, calificaciones, actas o registros de auditoría (`append-only`).
- Mantener sincronizados en el mismo conjunto de cambios: código Java + migración Flyway (`V11+`) + `openapi/openapi.yaml` + pruebas + regeneración del cliente Angular si cambió el contrato API.

## 5. Comandos de verificación
- **Backend:** `./mvnw test`
- **Frontend:** `cd frontend && npm run build` (y las pruebas unitarias de los archivos afectados)