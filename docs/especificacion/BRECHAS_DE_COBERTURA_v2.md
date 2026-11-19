# Brechas de cobertura — Estado consolidado tras auditoría V2 (56 Casos de Uso)

**Versión:** 2.0 — Consolidada tras auditoría normativa y operacional de los 12 módulos  
**Fecha de actualización:** 18 de noviembre de 2026  
**Documentos de referencia:** `Documento_Maestro_Sistema_Escolar_V2.1.md`, `00_Guia_General_y_Reglas_Transversales_v2.docx`, `00_Indice_y_Secuencia_de_Desarrollo_v2.docx`, `MAPA_UC_Y_DEPENDENCIAS_v2.md` y los 12 módulos `01_Seguridad_y_Acceso_v2.docx` a `12_Documentos_Reportes_y_Auditoria_v2.docx`.

Este documento actualiza el registro original de brechas de cobertura comparando el catálogo actual de **56 casos de uso** (`UC-01` a `UC-27` originales + `UC-PROV-01` a `UC-PROV-29` provisionales aprobados) frente al alcance integral del **Documento Maestro V2.1**. No se crean casos de uso ficticios para los módulos pospuestos; las áreas de evolución futura permanecen explícitamente marcadas como pendientes hasta que se apruebe su análisis funcional.

---

## 1. Estado de las 9 brechas originales de cobertura (`BRC-ORIG-01` a `BRC-ORIG-09`)

Tras completar las Fases 1 a 12 de auditoría y reescritura autorizada, **7 de las 9 brechas originales fueron cerradas íntegramente en los módulos `v2`**, mientras que **2 áreas permanecen abiertas como módulos de evolución futura** (Comunicación avanzada y Finanzas):

| ID Brecha | Área prevista | Estado actual | Lo que faltaba en V1 | Cobertura alcanzada en V2 (Módulo y UCs) |
| :---: | :--- | :---: | :--- | :--- |
| **BRC-ORIG-01** | **Usuarios y seguridad** | **CERRADA EN V2** | `UC-01` solo cubría inicio/cierre de sesión; faltaba administración de cuentas, roles, permisos, aprovisionamiento y recuperación de acceso. | **Módulo 01 (`01_Seguridad_y_Acceso_v2.docx`):** `UC-01` ampliado (activación en primer ingreso, rol activo único y modo histórico) + **`UC-PROV-01`** (cuentas y aprovisionamiento), **`UC-PROV-02`** (roles, vigencias y membresías nominativas en órganos colegiados) y **`UC-PROV-03`** (cambio y recuperación de contraseñas). |
| **BRC-ORIG-02** | **Profesores** | **CERRADA EN V2** | `UC-15` solo cubría asignaciones; faltaba registrar, actualizar, desactivar o consultar el expediente maestro del profesor. | **Módulo 05 (`05_Profesores_y_Asignaciones_v2.docx`):** `UC-15` ampliado (dirección de grupo, bloques unidocentes y suplencias) + **`UC-PROV-11`** (expediente maestro docente, verificación de inhabilidades Ley 1918 de 2018 y licencias/retiros) y **`UC-PROV-12`** (consultas de expediente, carga y cobertura). |
| **BRC-ORIG-03** | **Horarios** | **CERRADA EN V2** | `UC-04` y `UC-10` solo consultaban horarios; faltaba creación, edición, publicación, aulas, cambios y gestión integral de conflictos. | **Módulo 09 (`09_Horarios_v2.docx`):** `UC-04` y `UC-10` ampliados + **`UC-PROV-20`** (aulas, franjas, estructuración, validación de `IHS` y Triple No-Colisión grupo/docente/aula, publicación), **`UC-PROV-21`** (cambios temporales, aulas, permutas, suplencias y nuevas versiones) y **`UC-PROV-22`** (horario docente propio, aulas libres y ubicación en tiempo real). |
| **BRC-ORIG-04** | **Documentos** | **CERRADA EN V2** | `UC-05` y `UC-11` solo consultaban documentos; faltaba expedición oficial, carga, clasificación, versionamiento, aprobación y ciclo de vida documental. | **Módulo 12 (`12_Documentos_Reportes_y_Auditoria_v2.docx`):** `UC-05` y `UC-11` ampliados (incluyendo modo histórico) + **`UC-PROV-27`** (solicitud, generación en servidor, firma y expedición de certificados anuales/parciales, constancias y actas) y **`UC-PROV-28`** (carga validada, clasificación de confidencialidad en 5 niveles, versionamiento inmutable y retención TRD Ley 594 de 2000). |
| **BRC-ORIG-05** | **Auditoría** | **CERRADA EN V2** | `RN-10` exigía registrar eventos y `UC-27` permitía consultarlos; faltaba administración, retención, exportación y controles de integridad del registro. | **Módulo 12 (`12_Documentos_Reportes_y_Auditoria_v2.docx`):** `UC-27` ampliado (rol `Administrador`, reportes SIMAT/DANE/SIUCE y campos canónicos sin secretos) + **`UC-PROV-29`** (retención activa/archivo histórico firmado, verificación criptográfica SHA-256 de cadena `append-only`, alertas de seguridad y exportación forense regulada). |
| **BRC-ORIG-06** | **Comunicación** | **ABIERTA (Evolución — Fase 7)** | Notificaciones aparecen solo como servicios secundarios; faltan casos autónomos de avisos, mensajería, preferencias y evidencia de entrega. | **Cobertura transversal parcial en V2:** las notificaciones críticas con constancia de entrega (citaciones de nivelación, ausencias/evasiones, traslados de cargos y fallos en convivencia) están modeladas como servicio de soporte (`RN-12`). Sigue **pendiente** especificar el módulo autónomo de noticias, comunicados masivos, mensajería bidireccional y preferencias en la Fase 7. |
| **BRC-ORIG-07** | **Finanzas** | **ABIERTA (Evolución — Fase 8)** | No existen casos de uso para pensiones, pagos, facturación, cartera, becas o descuentos. | **Pospuesta fuera del MVP V1:** en `UC-13 (A4)` se blindó el respeto a la gratuidad en colegios oficiales (Decreto 4807 de 2011) y la parametrización de requisitos en privados sin bloqueos arbitrarios. Toda la gestión financiera queda **pendiente** para la Fase 8 de evolución. |
| **BRC-ORIG-08** | **Estudiante y acudiente** | **CERRADA EN V2** | `UC-12` y las consultas cubrían registro/vínculo/lectura; faltaba actualización, inactivación, restauración y desvinculación de acudientes. | **Módulo 02 (`02_Estudiantes_y_Acudientes_v2.docx`):** `UC-12`, `UC-06` y `UC-07` ampliados + **`UC-PROV-04`** (actualización de expediente, evolución documental `RC -> TI -> CC` y `NES/PEP -> PPT/CE`, reclamos Habeas Data Ley 1581 e inactivación/restauración lógica) y **`UC-PROV-05`** (novedades de vínculos, reemplazo atómico de único acudiente principal, autorizaciones de retiro físico y bloqueos `BLOQUEADO_RESTRICCION_LEGAL`). |
| **BRC-ORIG-09** | **Configuración general** | **CERRADA EN V2** | `UC-14` cubría año/periodos/estructura y `UC-26` cubría SIEE/Manual; faltaban otros parámetros institucionales del Documento Maestro. | **Módulo 06 (`06_Configuracion_Institucional_v2.docx`):** `UC-26` ampliado (catálogo estructurado de parámetros del SIEE y Manual con actas colegiadas) + **`UC-PROV-13`** (datos oficiales DANE/NIT/resoluciones/firmas, versionamiento de Política de Tratamiento de Datos y parámetros transversales) y **`UC-PROV-14`** (consulta y socialización vigente e histórica). |

---

## 2. Brechas normativas y operacionales adicionales descubiertas y cerradas en V2

Durante la auditoría independiente de los colegios colombianos (Fases 1 a 12), se detectaron y cerraron brechas críticas que no figuraban en la tabla original de 9 áreas, completando los **29 casos de uso provisionales (`UC-PROV-01` a `UC-PROV-29`)**:

| Módulo V2 | Brechas adicionales cerradas | Casos de uso provisionales incorporados | Fundamento normativo / operacional principal |
| :--- | :--- | :--- | :--- |
| **03 — Estructura académica** | Oferta de grupos con `cupoMaximo`, jerarquía Área $\rightarrow$ Asignatura con `IHS` al $100.00\%$, régimen cualitativo de Preescolar y consulta de estructura. | `UC-PROV-06`, `UC-PROV-07`, `UC-PROV-08` | Ley 115 de 1994 Arts. 23, 31 y 79; Decreto 2247 de 1997 (Preescolar); Decreto 1850 de 2002. |
| **04 — Matrícula** | Novedades en curso (cambio de grupo, ascenso por Promoción Anticipada, retiro voluntario, traslado saliente, deserción y cancelación en firme) y listados/historial. | `UC-PROV-09`, `UC-PROV-10` | Resolución MEN 07797 de 2015 (SIMAT); Decreto 1290 de 2009 Art. 7; Documento Maestro V2.1 Secc. 4 y 8. |
| **07 — Calificaciones y evaluación** | Reclamaciones académicas en 2 instancias, convalidación/homologación de notas por traslado o promoción anticipada, y sábanas grupales/avance docente. | `UC-PROV-15`, `UC-PROV-16`, `UC-PROV-17` | Decreto 1290 de 2009 Arts. 4.10, 7, 11, 16 y 17; Ley 1581 de 2012 Art. 15; Decreto 1421 de 2017 (PIAR). |
| **08 — Asistencia** | Validación y resolución multisesión de excusas con reserva médica y propagación a `UC-17`/`UC-18`, salidas anticipadas verificando retiro físico y protocolo contra deserción. | `UC-PROV-18`, `UC-PROV-19` | Ley 1098 de 2006 Arts. 18 y 42–44; Ley 1581 de 2012 Arts. 5–7; `UC-PROV-05` y `UC-PROV-09 (A4)`. |
| **10 — Cierre académico y promoción** | Comisiones periódicas y observaciones de Dirección de Grupo para boletines; verificación de graduación de Grado 11.° (Servicio Social e ICFES Saber 11) y cierre anual. | `UC-PROV-23`, `UC-PROV-24` | Decreto 1290 de 2009 Arts. 11 y 18; Ley 115 de 1994 Arts. 88 y 97; Res. MEN 4210 de 1996; Decreto 869 de 2010. |
| **11 — Convivencia escolar** | Instrucción formal del debido proceso disciplinario (cargos, pruebas, descargos y recursos de reposición/apelación); actas del Comité Escolar de Convivencia, SIUCE y cierre del caso. | `UC-PROV-25`, `UC-PROV-26` | Constitución Art. 29; Ley 1098 Art. 26; Ley 1620 de 2013 (y Leyes 2383/2024 y 2503/2025); Decreto 1965 de 2013; Sentencia T-004 de 2026. |

---

## 3. Detalle de las brechas que permanecen abiertas (Evolución posterior a la V1)

### 3.1. Comunicación institucional avanzada (`BRC-ORIG-06` — Fase 7 del Roadmap)
* **Qué existe hoy en V2:** El sistema ya incluye notificaciones transaccionales y citaciones trazables disparadas por los procesos de negocio (activación de cuentas, citación a nivelaciones, alertas de inasistencia/evasión, publicación de boletines, notificaciones de cargos y fallos disciplinarios con acuse de entrega, `RN-12`).
* **Qué queda pendiente por especificar cuando se priorice la Fase 7:**
  1. Publicación y segmentación de noticias, circulares y comunicados generales por sede, jornada, grado o grupo.
  2. Mensajería interna controlada entre docentes/directivos y acudientes verificados (con políticas de horario de atención, moderación y bloqueo ante restricciones legales `UC-PROV-05`).
  3. Gestión de preferencias de canales de notificación (correo, portal web, push móvil futuro) diferenciando avisos informativos opcionales de notificaciones legales obligatorias no desactivables.

### 3.2. Gestión financiera escolar (`BRC-ORIG-07` — Fase 8 del Roadmap)
* **Qué existe hoy en V2:** En `UC-PROV-13` se parametriza el carácter del establecimiento (`OFICIAL` vs. `PRIVADO`) y en `UC-13 (A4)` se garantiza el respeto a la gratuidad educativa en colegios oficiales (Decreto 4807 de 2011) y el manejo no arbitrario de paz y salvos en colegios privados.
* **Qué queda pendiente por especificar cuando se priorice la Fase 8 (orientada a instituciones privadas):**
  1. Definición de tarifas autorizadas por grado (matrícula, pensiones, otros cobros periódicos regulados por el MEN — Resolución anual de tarifas y Decreto 1075 de 2015).
  2. Gestión de responsables económicos diferenciados del acudiente pedagógico (`UC-12`), facturación/cuentas de cobro, recaudo, cartera, acuerdos de pago, becas y descuentos.
  3. Reglas jurídicas de protección al derecho a la educación (jurisprudencia constitucional que impide retener certificados o impedir evaluaciones por mora cuando se acredita imposibilidad sobreviniente y voluntad de pago).

---

## 4. Decisiones de gobierno pendientes antes de iniciar codificación a escala

Aunque la especificación funcional de los 12 módulos (`56 UCs`) está completa y coherente, quedan tres decisiones de proyecto que deben confirmarse antes de abrir el diseño detallado de base de datos y OpenAPI:

1. **Ratificación definitiva de los 29 identificadores `UC-PROV-01` a `UC-PROV-29`:** Confirmar si en la línea base de implementación se conservan los códigos `UC-PROV-01`..`UC-PROV-29` (para distinguir visualmente los agregados en V2) o si se consolidan en numeración secuencial definitiva `UC-28` a `UC-56` manteniendo intactos `UC-01` a `UC-27`.
2. **Acotación del alcance mínimo viable (MVP V1):** Confirmar qué subconjunto de los 12 módulos entrará en el primer despliegue productivo (por ejemplo: Corte 1 con Módulos 01, 06 base, 02, 03, 04 y 05; Corte 2 con Módulos 09, 08, 07 y 10; Corte 3 con Módulos 11 y 12 completos).
3. **Validación con documentos reales del colegio piloto:** Cargar en el Módulo 06 (`UC-26` y `UC-PROV-13`) el PEI, SIEE, Manual de Convivencia y Política de Tratamiento de Datos aprobados por la institución objetivo antes de ejecutar pruebas con datos reales.

---

## 5. Recomendación de análisis y transición a diseño técnico

* **Para los módulos de evolución pendientes (Fases 7 y 8):** Antes de implementar Comunicación avanzada o Finanzas, elaborar sus casos de uso con el mismo rigor de los 12 módulos `v2` (actores, disparador, precondiciones, invariantes, postcondiciones, flujo principal, flujos alternos `A#` y excepciones `E#` con códigos HTTP, autorización contextual, protección de datos y criterios de aceptación).
* **Para los 12 módulos V2 ya cerrados:** La documentación funcional está lista para ejecutar la **Fase de Validación Global** (verificación de cierre de consistencia de extremo a extremo) y derivar directamente los artefactos técnicos de diseño:
  1. **Matriz unificada de permisos RBAC + Autorización Contextual** por caso de uso.
  2. **Modelo Entidad-Relación (DER) y plan de migraciones Flyway** en PostgreSQL.
  3. **Contrato incremental `openapi/openapi.yaml`** derivando los códigos de respuesta y validaciones directamente desde las tablas `A#` y `E#`.