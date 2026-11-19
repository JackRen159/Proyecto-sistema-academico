# CASOS DE USO — SISTEMA DE GESTIÓN ESCOLAR

## Índice y secuencia de desarrollo

**Fragmentación modular consolidada del documento Casos de Uso (V2.0)**

- **Orden recomendado:** Documento de navegación
- **Casos de uso incluidos:** `UC-01` a `UC-27` (27 originales) y `UC-PROV-01` a `UC-PROV-29` (29 provisionales aprobados; total: 56 casos de uso)
- **Referencia de desarrollo:** Orden ajustado a dependencias reales y al roadmap del Documento Maestro V2.0 (y actualización V2.1)
- **Dependencias:** Consultar primero `00_Guia_General_y_Reglas_Transversales_v2.docx` y `Documento_Maestro_Sistema_Escolar_V2.md`.

> Los 27 identificadores `UC` originales (`UC-01` a `UC-27`) se mantienen intactos. Los 29 nuevos procesos de negocio aprobados tras la auditoría normativa y operacional de los 12 módulos llevan identificador provisional (`UC-PROV-01` a `UC-PROV-29`) hasta su ratificación definitiva. La numeración de módulos expresa el orden de navegación e implementación recomendado.
>
> *Fragmentación del documento fuente · versión 2.0 consolidada tras auditoría de Fases 1 a 12*

## 1. Criterio de organización

El paquete funcional se organiza en 12 módulos cohesivos por dominio y dependencias de desarrollo. Cada caso de uso aparece una sola vez. Tras completar la auditoría normativa y funcional de las Fases 1 a 12, todos los módulos V2 conservan los 27 casos de uso originales corregidos, incorporan los 29 casos de uso provisionales autorizados (`UC-PROV-01` a `UC-PROV-29`) y separan formalmente los Flujos Alternos (`A#`) de los Flujos de Excepción (`E#`) con sus códigos `HTTP` esperados.

La secuencia sigue las fases del Documento Maestro V2.0: fundación de seguridad; núcleo administrativo; estructura, matrícula y asignaciones; configuración institucional aprobada; procesos académicos; cierre/promoción; convivencia; y documentos/reportes/auditoría. Nota técnica de implementación: (1) aunque el Módulo 06 (Configuración institucional) mantiene su numeración 06, sus versiones aprobadas de Política de Datos, SIEE y parámetros generales (`UC-26`, `UC-PROV-13`) deben inicializarse temprano porque alimentan validaciones desde los Módulos 01 a 05; y (2) aunque el Módulo 09 (Horarios) mantiene su numeración 09, la programación de franjas y sesiones válidas (`UC-PROV-20`) alimenta el denominador del Módulo 08 (Asistencia, `UC-19`), por lo que conviene implementar su núcleo antes o en paralelo con Asistencia.

## 2. Orden recomendado de módulos

| Orden | Módulo | Casos de uso | Fase / motivo |
| :--- | :--- | :--- | :--- |
| 00 | Guía general y reglas transversales | — | Fase 0; lectura obligatoria antes de implementar (convenciones, actores, `RN-01` a `RN-12`, familias modulares y cobertura). |
| 01 | Seguridad y acceso | `UC-01`, `UC-PROV-01`, `UC-PROV-02`, `UC-PROV-03` | Fases 1 y 2 — fundación del backend, seguridad base, cuentas, roles/membresías colegiadas y contraseñas. |
| 02 | Estudiantes y acudientes | `UC-12`, `UC-06`, `UC-07`, `UC-PROV-04`, `UC-PROV-05` | Fase 4 — núcleo administrativo: expediente personal, vínculos, rectificaciones Habeas Data, evolución documental y restricciones legales. |
| 03 | Estructura académica | `UC-14`, `UC-PROV-06`, `UC-PROV-07`, `UC-PROV-08` | Fase 4 — núcleo administrativo: año lectivo, calendario/días no lectivos, grados/grupos con cupos y Plan de Estudios (Áreas/IHS y Preescolar). |
| 04 | Matrícula | `UC-13`, `UC-PROV-09`, `UC-PROV-10` | Fase 4 — núcleo administrativo: formalización, cambios de grupo, promoción anticipada, retiros/traslados e historial. |
| 05 | Profesores y asignaciones docentes | `UC-15`, `UC-PROV-11`, `UC-PROV-12` | Fase 4 — núcleo administrativo: expediente docente (Ley 1918), asignaciones por IHS, dirección de grupo y suplencias. |
| 06 | Configuración institucional | `UC-26`, `UC-PROV-13`, `UC-PROV-14` | Fundación de reglas institucionales (SIEE, Manual de Convivencia, Política de Datos y datos legales/firmas DANE); prerrequisito funcional de evaluación y convivencia. |
| 07 | Calificaciones y evaluación | `UC-16`, `UC-17`, `UC-18`, `UC-02`, `UC-08`, `UC-PROV-15`, `UC-PROV-16`, `UC-PROV-17` | Fase 5 — módulo académico: actividades, cálculo jerárquico `BigDecimal`, nivelaciones, reclamaciones, convalidaciones y sábanas. |
| 08 | Asistencia | `UC-19`, `UC-03`, `UC-09`, `UC-PROV-18`, `UC-PROV-19` | Fase 5 — módulo académico: registro por sesión/jornada, excusas/salidas anticipadas en portería y seguimiento contra deserción. |
| 09 | Horarios | `UC-04`, `UC-10`, `UC-PROV-20`, `UC-PROV-21`, `UC-PROV-22` | Fase 5 — módulo académico: aulas, franjas, horarios sin colisión (grupo/docente/aula), novedades y consultas (provee sesiones válidas a Mód. 08). |
| 10 | Cierre académico y promoción | `UC-20`, `UC-21`, `UC-PROV-23`, `UC-PROV-24` | Fase 5 — cierre del ciclo académico: comisiones periódicas, cierres/boletines, promoción anual/anticipada, habilitaciones finales y graduación de Media. |
| 11 | Convivencia escolar | `UC-22`, `UC-23`, `UC-24`, `UC-25`, `UC-PROV-25`, `UC-PROV-26` | Dominio funcional específico integrado tras Mód. 06: observador, Ruta de Atención Tipo I-III, debido proceso/recursos, medidas con continuidad académica y Comité Escolar de Convivencia. |
| 12 | Documentos, reportes y auditoría | `UC-05`, `UC-11`, `UC-27`, `UC-PROV-27`, `UC-PROV-28`, `UC-PROV-29` | Fase 6 — documentos, expedición de certificados/constancias, ciclo de vida documental, reportes y gobierno inmutable de auditoría (captura transversal desde Fase 1). |

## 3. Dependencias funcionales principales

- **01 · Seguridad y acceso:** `UC-01`, `UC-PROV-01`, `UC-PROV-02` y `UC-PROV-03` establecen cuentas nominativas, roles vigentes, membresías de órganos colegiados, modo histórico para egresados y la autenticación/autorización contextual que consumen todos los módulos.
- **02 · Estudiantes y acudientes:** `UC-12`, `UC-PROV-04` y `UC-PROV-05` gestionan el expediente, la evolución documental, rectificaciones Habeas Data, único acudiente principal, retiros físicos y bloqueos por restricción legal; `UC-06`/`07` exponen consultas minimizadas.
- **03 · Estructura académica:** `UC-14`, `UC-PROV-06`, `UC-PROV-07` y `UC-PROV-08` proveen año lectivo, calendario y días no lectivos, secuencia de grados, grupos con `cupoMaximo` y el Plan de Estudios (Áreas/Asignaturas con IHS y Dimensiones de Preescolar).
- **04 · Matrícula:** `UC-13`, `UC-PROV-09` y `UC-PROV-10` dependen del expediente (Mód. 02), cupos (Mód. 03) y promoción previa (`UC-21`); gestionan la vigencia de la matrícula, cambios de grupo, promoción anticipada y retiros que delimitan notas, asistencia y horarios.
- **05 · Profesores y asignaciones docentes:** `UC-PROV-11` valida idoneidad e inhabilidades (Ley 1918); `UC-15` y `UC-PROV-12` asignan carga por IHS, dirección de grupo y suplencias temporales, definiendo el alcance de escritura docente en los módulos académicos y el observador.
- **06 · Configuración institucional:** `UC-26`, `UC-PROV-13` y `UC-PROV-14` proveen versiones inmutables del SIEE, Manual de Convivencia, Política de Tratamiento de Datos, datos legales/firmas DANE y parámetros operativos transversales que consumen todos los módulos.
- **07 · Calificaciones y evaluación:** `UC-16`, `UC-17`, `UC-18`, `UC-02`, `UC-08` y `UC-PROV-15`/`16`/`17` dependen de matrícula, Plan de Estudios, asignaciones y SIEE vigente; ejecutan el cálculo jerárquico `BigDecimal` hasta Área, nivelaciones, reclamaciones, convalidaciones y sábanas, integrándose con excusas de `UC-PROV-18`.
- **08 · Asistencia:** `UC-19`, `UC-03`, `UC-09`, `UC-PROV-18` y `UC-PROV-19` dependen de matrícula, días lectivos (`UC-14` `A4`), asignaciones, horarios publicados (`UC-PROV-20`) y autorizados de retiro físico (`UC-PROV-05`); propagan excusas hacia `UC-17`/`18` y soportan bajas por deserción en `UC-PROV-09`.
- **09 · Horarios:** `UC-PROV-20`, `UC-PROV-21`, `UC-PROV-22`, `UC-04` y `UC-10` dependen de grupos, IHS del Plan de Estudios y asignaciones docentes; previenen cruces de grupo, docente y aula, reflejan suplencias (`UC-15` `A3`) y proveen las sesiones programadas válidas a `UC-19`.
- **10 · Cierre académico y promoción:** `UC-PROV-23`, `UC-20`, `UC-21` y `UC-PROV-24` consolidan resultados de Mód. 07 y Mód. 08 bajo el SIEE vigente; emiten boletines versionados, formalizan actas de promoción/habilitación/promoción anticipada y ejecutan la graduación de Media y el cierre anual (`UC-14`).
- **11 · Convivencia escolar:** `UC-22` a `UC-25` y `UC-PROV-25`/`26` dependen del expediente, vínculos sin restricción legal y Manual aprobado (`UC-26`); instruyen el debido proceso y recursos, sincronizan el Plan de Continuidad Académica con Mód. 07/08 y proveen firmeza sancionatoria a `UC-PROV-09` (`A4`).
- **12 · Documentos, reportes y auditoría:** `UC-05`, `UC-11`, `UC-27` y `UC-PROV-27`/`28`/`29` expiden constancias y certificados usando versiones históricas de Plan de Estudios/SIEE y firmas de `UC-PROV-13`, administran la clasificación documental y gobiernan la auditoría inmutable `append-only` generada desde la Fase 1.

## 4. Correspondencia con las fases del Documento Maestro V2.0

| Fase | Módulos / cobertura |
| :--- | :--- |
| Fase 0 — Análisis y alcance | 00 Guía General V2 y 00 Índice V2; cierre de actores, 56 `UC`s, reglas transversales y modulares, y validación global. |
| Fase 1 — Fundación del backend | Configuración técnica (`Spring Boot`, `PostgreSQL`, `Flyway`, manejo global de errores y captura base de auditoría `append-only` `RN-10`). |
| Fase 2 — Contrato API y seguridad base | 01 Seguridad y Acceso (`UC-01`, `UC-PROV-01`, `UC-PROV-02`, `UC-PROV-03`) + `OpenAPI 3.x`, `RBAC`, autorización contextual, `CORS` y `CSRF`. |
| Fase 3 — Fundación Angular SPA | Arquitectura `Angular` + `TypeScript` (`core`/`shared`/`features`, cliente generado `OpenAPI`, interceptores, `guards` y manejo de errores `HTTP`). |
| Fase 4 — Núcleo administrativo | 02 Estudiantes y Acudientes (`UC-12`, `UC-06`, `UC-07`, `UC-PROV-04`, `UC-PROV-05`); 03 Estructura Académica (`UC-14`, `UC-PROV-06` a `UC-PROV-08`); 04 Matrícula (`UC-13`, `UC-PROV-09`, `UC-PROV-10`); 05 Profesores y Asignaciones (`UC-15`, `UC-PROV-11`, `UC-PROV-12`). |
| Fase 5 — Académico | 06 Configuración Institucional (`UC-26`, `UC-PROV-13`, `UC-PROV-14` como prerrequisito); 07 Calificaciones (`UC-16` a `UC-18`, `UC-02`, `UC-08`, `UC-PROV-15` a `UC-PROV-17`); 08 Asistencia (`UC-19`, `UC-03`, `UC-09`, `UC-PROV-18`, `UC-PROV-19`); 09 Horarios (`UC-04`, `UC-10`, `UC-PROV-20` a `UC-PROV-22`); 10 Cierre y Promoción (`UC-20`, `UC-21`, `UC-PROV-23`, `UC-PROV-24`). |
| Convivencia — dominio funcional integrado | 11 Convivencia Escolar (`UC-22` a `UC-25`, `UC-PROV-25`, `UC-PROV-26`); integrado tras contar con Manual de Convivencia aprobado (`UC-26`) y núcleo administrativo. Incluir formalmente en Documento Maestro V2.1. |
| Fase 6 — Documentos, reportes y auditoría | 12 Documentos, Reportes y Auditoría (`UC-05`, `UC-11`, `UC-27`, `UC-PROV-27`, `UC-PROV-28`, `UC-PROV-29`); boletines en `UC-20` y captura de auditoría desde Fase 1. |
| Fase 7 — Comunicación (evolución) | Pendiente de especificación autónoma: actualmente las notificaciones operan como servicio transversal de apoyo en los módulos 01 a 12; falta diseñar avisos masivos, mensajería interna y preferencias si entran en alcance futuro. |
| Fase 8 — Finanzas (evolución) | Pendiente de especificación autónoma: pensiones, pagos, facturación, cartera, becas y descuentos quedan fuera de la V1 académica/administrativa base. |
| Fase 9 — Calidad e infraestructura | Pruebas unitarias/integración/seguridad/concurrencia derivadas de `A#` y `E#`, `Docker Compose`, `Nginx/HTTPS`, respaldos y `CI/CD`. |

## 5. Estado de cobertura y brechas pendientes tras auditoría V2

Tras completar las Fases 1 a 12 de auditoría y reescritura autorizada, se cerraron 7 de las 9 brechas inicialmente identificadas en `BRECHAS_DE_COBERTURA.md` mediante casos de uso con identificador provisional (`UC-PROV-01` a `UC-PROV-29`). Únicamente Comunicación (Fase 7) y Finanzas (Fase 8) permanecen abiertas como módulos de evolución futura:

| Área | Estado actual de cobertura (V2) |
| :--- | :--- |
| Usuarios y seguridad | `CUBIERTA EN V2` — Cerrada mediante `UC-01` ampliado y `UC-PROV-01` (cuentas/aprovisionamiento), `UC-PROV-02` (roles, vigencias y órganos colegiados) y `UC-PROV-03` (cambio y recuperación de contraseñas). |
| Estudiante y acudiente | `CUBIERTA EN V2` — Cerrada mediante `UC-12`/`06`/`07` ampliados y `UC-PROV-04` (actualización, evolución documental RC/TI/CC/NES y reclamos Habeas Data) y `UC-PROV-05` (novedades de vínculos, cambio de principal, retiros físicos y restricciones legales). |
| Profesores | `CUBIERTA EN V2` — Cerrada mediante `UC-15` ampliado (suplencias y dirección de grupo) y `UC-PROV-11` (expediente maestro docente, inhabilidades Ley 1918 y situaciones administrativas) y `UC-PROV-12` (consultas de carga y planta). |
| Configuración general | `CUBIERTA EN V2` — Cerrada mediante `UC-26` ampliado (parámetros estructurados SIEE/Manual) y `UC-PROV-13` (datos legales DANE/NIT/firmas, Política de Datos y parámetros transversales) y `UC-PROV-14` (consulta y socialización). |
| Horarios | `CUBIERTA EN V2` — Cerrada mediante `UC-04`/`10` ampliados y `UC-PROV-20` (aulas, franjas, armado, validación de IHS y Triple No-Colisión, publicación), `UC-PROV-21` (cambios temporales, permutas, aulas y versiones) y `UC-PROV-22` (horario docente y matriz institucional). |
| Documentos | `CUBIERTA EN V2` — Cerrada mediante `UC-05`/`11` ampliados y `UC-PROV-27` (expedición de certificados anuales/parciales y constancias oficiales) y `UC-PROV-28` (carga, clasificación de confidencialidad, versionamiento y ciclo de vida documental TRD). |
| Auditoría | `CUBIERTA EN V2` — Cerrada mediante `RN-10`, `UC-27` ampliado (incluyendo rol Administrador y campos canónicos) y `UC-PROV-29` (retención, verificación criptográfica `SHA-256` de cadena `append-only`, alertas y exportación forense regulada). |
| Comunicación (Fase 7) | `PENDIENTE` (Evolución futura) — Notificaciones y citaciones operan hoy como servicio transversal secundario en los 12 módulos; no existen aún casos autónomos para noticias institucionales, mensajería bidireccional ni gestión de preferencias. |
| Finanzas (Fase 8) | `PENDIENTE` (Evolución futura) — No existen casos de uso para pensiones, facturación, recaudo, cartera, becas ni descuentos; se mantiene como módulo de evolución posterior a la V1. |

## 6. Reglas de uso del paquete

- Antes de trabajar en un módulo, leer `00_Guia_General_y_Reglas_Transversales_v2.md` y la sección correspondiente del docs/arquitectura/Documento_Maestro_Sistema_Escolar_V2.1.md.
- Implementar los módulos respetando sus dependencias reales: habilitar las bases de Configuración Institucional (Módulo 06) antes de validar procesos académicos y coordinar la estructura de sesiones de Horarios (Módulo 09) antes o en paralelo con Asistencia (Módulo 08).
- Conservar intactos los 27 identificadores `UC-01` a `UC-27` y mantener la trazabilidad de `UC-PROV-01` a `UC-PROV-29` hasta aprobar su ratificación definitiva en el catálogo maestro; no fusionar actores ni alterar reglas sin actualizar el análisis.
- Las reglas institucionales que dependen del SIEE, Manual de Convivencia, PEI, calendario y competencias colegiadas deben leerse siempre desde versiones aprobadas e inmutables (Módulo 06), sin codificar constantes arbitrarias.
- Convertir todas las tablas de Flujos Alternos (`A#`) y Flujos de Excepción (`E#`) con sus códigos `HTTP` esperados en criterios de aceptación, definiciones `OpenAPI 3.x` y pruebas automatizadas funcionales, negativas, de autorización contextual y de concurrencia.
- La auditoría `append-only` (`RN-10`), la autorización contextual en `Spring Security`, la protección de datos de menores (Ley 1581 y Ley 1098) y el control transaccional son transversales desde la Fase 1; nunca deben aplazarse hasta el Módulo 12.