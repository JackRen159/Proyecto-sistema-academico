# Plan Maestro de Evolución V2 (desde Migración V10 en adelante)

## Línea base completada (V1 – V10)
- `V1`–`V4`: Estudiantes, Acudientes, Vínculos (principal único), Usuarios y Auth JWT base.
- `V5`: Estructura académica base (`frente-2`).
- `V6`: Profesores y asignaciones académicas base (`frente-4`).
- `V7`: Matrículas y traslados base (`frente-5`).
- `V8`: Calificaciones, escala SIEE y auditoría de notas base (`frente-6`).
- `V9`: Asistencia, sesiones, justificaciones y alertas base (`frente-7`).
- `V10`: Documentos académicos y consecutivos base (`frente-8`).
- Frontend Angular (`frontend/`): únicamente `auth` y `estudiantes-list` implementados.

## Siguientes frentes ordenados por dependencia real (V11+)
> **Regla:** No modificar migraciones `V1`–`V10`. Todo cambio nuevo entra en `V11+`.

| Frente | Archivo de tareas | Migración estimada | Módulos / UCs V2 que cubre |
| :--- | :--- | :--- | :--- |
| **Frente 9** | `odd/tasks/frente-9-hardening-seguridad-config-auditoria-v2.md` | `V11`–`V12` | Mód. 01 (`UC-PROV-01..03`, separar `COORDINADOR_ACADEMICO`/`COORDINADOR_CONVIVENCIA` + `ORIENTADOR_ESCOLAR`, órganos colegiados), Mód. 06 (`UC-26`, `UC-PROV-13..14` Política de Datos y DANE/firmas) y Mód. 12 base (`UC-PROV-29` auditoría global `append-only` SHA-256). |
| **Frente 10** | `odd/tasks/frente-10-horarios.md` | `V13` | Mód. 09 (`UC-04`, `UC-10`, `UC-PROV-20..22`: aulas, franjas, Triple No-Colisión, IHS, novedades y horario docente). Alimenta sesiones válidas a Mód. 08. |
| **Frente 11** | `odd/tasks/frente-11-cierre-promocion-graduacion.md` | `V14` | Mód. 10 (`UC-20`, `UC-21`, `UC-PROV-23..24`: comisiones periódicas, cierre/reapertura con boletín `V+1`, promoción anual/anticipada, habilitaciones finales y graduación de Media). |
| **Frente 12** | `odd/tasks/frente-12-convivencia-escolar.md` | `V15` | Mód. 11 (`UC-22..25`, `UC-PROV-25..26`: observador inmutable, Ruta Tipo I–III, debido proceso con recursos, continuidad académica `RN-08` y Comité Escolar de Convivencia). |
| **Frente 13** | `odd/tasks/frente-13-brechas-nucleo-y-documentos-v2.md` | `V16` | Cierre de brechas V1→V2 en módulos ya existentes: Mód. 02 (evolución RC/TI/CC/NES, Habeas Data, restricciones legales), Mód. 03 (días no lectivos, Preescolar), Mód. 04 (promoción anticipada/deserción), Mód. 05 (Ley 1918/suplencias), Mód. 07 (reclamaciones, convalidaciones, consolidación por Área), Mód. 08 (portería/salidas anticipadas/deserción) y Mód. 12 (ciclo documental TRD). |
| **Frente 14** | `odd/tasks/frente-14-frontend-angular-v2.md` | N/A | Regenerar cliente OpenAPI y construir pantallas Angular progresivas por módulo (`features/`) conectadas al contrato oficial. |

## Estado de seguimiento
- [x] Frente 9 — Hardening Seguridad, Configuración Institucional V2 y Auditoría Global
- [ ] Frente 10 — Módulo Horarios
- [ ] Frente 11 — Cierre Académico, Promoción y Graduación
- [ ] Frente 12 — Convivencia Escolar
- [ ] Frente 13 — Brechas funcionales V1→V2 en módulos `V1`–`V10`
- [ ] Frente 14 — Frontend Angular SPA completo