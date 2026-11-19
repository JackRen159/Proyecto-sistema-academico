# CASOS DE USO — SISTEMA DE GESTIÓN ESCOLAR

## Módulo 07 — Calificaciones y evaluación

**Actividades evaluativas, registro y cálculo jerárquico de calificaciones, apoyo/recuperación, reclamaciones, convalidaciones y consultas**

- **Orden recomendado:** 07 de 12
- **Casos de uso incluidos:** `UC-16`, `UC-17`, `UC-18`, `UC-02`, `UC-08`, `UC-PROV-15`, `UC-PROV-16`, `UC-PROV-17`
- **Referencia de desarrollo:** Fase 5 — módulo académico
- **Dependencias:** Estudiante y matrícula (`UC-12`, `UC-13`, `UC-PROV-05`, `UC-PROV-09`); estructura, periodos y plan de estudios (`UC-14`, `UC-PROV-06`, `UC-PROV-07`); asignación docente (`UC-15`); SIEE aprobado/versionado (`UC-26`); asistencia justificada (`UC-19`).

> Se agrupan la configuración de actividades evaluativas, el registro y corrección de calificaciones con cálculo jerárquico hacia asignaturas y áreas, las estrategias de apoyo/nivelación, las consultas del estudiante y acudiente, y los casos provisionales de reclamaciones académicas (`UC-PROV-15`), convalidación de notas por traslado o promoción anticipada (`UC-PROV-16`) y consulta de consolidados grupales y avance docente (`UC-PROV-17`).
>
> *Fragmentación del documento fuente · versión actualizada tras auditoría de Fase 7*

---

## UC-16 · Crear y configurar actividad evaluativa

| Campo | Detalle |
| :--- | :--- |
| **ID** | `UC-16` |
| **Nombre** | Crear y configurar actividad evaluativa |
| **Actores principales** | Docente de área (titular o suplente activo en reemplazo temporal según `UC-15` `A3`); Docente de Preescolar / Primaria (para dimensiones del desarrollo o bloque unidocente según `UC-15` `A2`) |
| **Actores secundarios / sistemas** | Coordinador Académico (supervisión y autorización de excepciones de calendario); servicio de cálculo y motor de reglas SIEE (`UC-26`); servicio de estructura y plan de estudios (`UC-14`, `UC-PROV-07`); servicio de asignaciones docentes (`UC-15`); servicio de auditoría |
| **Descripción** | Permite crear, configurar, guardar en borrador, publicar o ajustar una actividad evaluativa ligada a una asignatura o dimensión del desarrollo (en Preescolar), grupo, periodo académico abierto, categoría/criterio evaluativo y peso porcentual conforme a la versión vigente del SIEE (`UC-26`). Contempla actividades de heteroevaluación, autoevaluación y coevaluación, continuidad operativa por docente suplente y la asociación confidencial de criterios flexibilizados para estudiantes con Ajustes Razonables (PIAR - Decreto 1421 de 2017). |
| **Propósito / valor** | Hacer explícitos y transparentes los criterios de evaluación desde el inicio del periodo (Decreto 1290 de 2009 Art. 12 compilado en Decreto 1075 de 2015) y permitir el cálculo determinista y reproducible de resultados con `BigDecimal`. |
| **Disparador** | El docente con asignación vigente selecciona un curso, asignatura o dimensión del desarrollo y periodo académico abierto, e inicia la creación o configuración de una actividad evaluativa. |
| **Precondiciones** | Sesión válida (`UC-01`); asignación docente vigente como titular o suplente activo sobre el grupo y asignatura/dimensión (`UC-15`); año lectivo activo y periodo académico en estado abierto o dentro de la ventana de planeación (`UC-14`); versión del SIEE aprobada y vigente para el periodo (`UC-26`). |
| **Invariantes y reglas de negocio** | Las categorías evaluativas (cognitiva/saber, procedimental/hacer, actitudinal/ser, autoevaluación, coevaluación, evaluación de periodo) y los tipos de componente (quiz, taller, tarea, proyecto, sustentación, examen) se obtienen de la versión vigente del SIEE sin asumir porcentajes universales (`RN-01`, `RN-03`). En Básica y Media, la planeación progresiva permite publicar actividades durante el periodo siempre que la suma acumulada de ponderaciones (`BigDecimal`) no supere el 100.00% de la categoría o periodo, exigiendo alcanzar exactamente el 100.00% para consolidar la definitiva hacia el cierre del periodo en `UC-20` (`RN-CAL-01`). En Educación Preescolar (Prejardín, Jardín y Transición), las actividades se configuran sobre las Dimensiones del Desarrollo bajo régimen cualitativo/descriptivo sin reprobación numérica conforme al Decreto 2247 de 1997 (`RN-CAL-02`, `UC-PROV-07` `A1`). Ninguna actividad que ya posea calificaciones registradas en `UC-17` puede eliminarse físicamente. Toda actividad queda vinculada de forma inmutable al ID de versión del SIEE vigente. |
| **Postcondiciones** | Actividad evaluativa persistida en estado `BORRADOR` o `PUBLICADA` con parámetros validados, vinculada a la versión vigente del SIEE y a la asignación docente, visible para el grupo autorizado cuando se publica, y trazada en auditoría. |
| **Tipo** | Básico |
| **Prioridad** | Alta |

### Flujo principal

| Paso | Actor / responsable | Acción e interacción del sistema |
| :--- | :--- | :--- |
| 1 | Docente de área | Selecciona el grupo, la asignatura o dimensión del desarrollo y el periodo académico abierto dentro de sus asignaciones vigentes (`UC-15`), y elige la opción Nueva actividad (o editar actividad existente). |
| 2 | Sistema | Verifica en backend la vigencia de la asignación docente (`UC-15`), el estado abierto del periodo (`UC-14`), el régimen del grado en el Plan de Estudios (`CUANTITATIVO_AREAS` en Básica/Media o `CUALITATIVO_DIMENSIONES` en Preescolar, `UC-PROV-07`) y recupera las categorías, escalas y reglas de la versión vigente del SIEE (`UC-26`). |
| 3 | Docente de área | Define el nombre de la actividad, descripción y desempeño evaluado, fecha programada, tipo de componente, categoría/criterio del SIEE, modalidad (heteroevaluación, autoevaluación o coevaluación), ponderación porcentual (o distribución equitativa dentro de la categoría según el SIEE), rúbrica/criterios y adaptaciones PIAR confidenciales cuando apliquen. |
| 4 | Sistema | Valida con precisión `BigDecimal` los rangos, la coherencia de las fechas respecto al periodo académico (`UC-14`), que el peso porcentual sea positivo y que la suma acumulada de pesos de las actividades de la categoría/periodo no supere el 100.00% (informando el porcentaje restante por planear para completar el 100.00%). |
| 5 | Docente de área | Revisa el resumen de configuración y guarda la actividad en estado `BORRADOR` o confirma su publicación (estado `PUBLICADA`) según los permisos. |
| 6 | Sistema | Persiste de forma transaccional la versión de la actividad vinculada al SIEE vigente, registra la operación en auditoría y, si fue publicada, la habilita en el libro de calificaciones (`UC-17`) y en la consulta de estudiantes y acudientes (`UC-02`, `UC-08`). |

### Flujos alternos y de excepción

Cada alternativa indica la condición de entrada, el tratamiento esperado y el resultado. Los flujos `A#` pueden retomar el principal cuando así se indique; los casos `E#` deben terminar de forma controlada, sin persistir cambios parciales.

| ID | Condición / paso de origen | Tratamiento / secuencia alterna | Resultado / estado final |
| :--- | :--- | :--- | :--- |
| **A1** | Configuración de actividad cualitativa por Dimensión del Desarrollo en Educación Preescolar (pasos 2 y 3) | Cuando el grupo pertenece al nivel Preescolar (Prejardín, Jardín o Transición, `UC-PROV-07` `A1`), el sistema habilita el registro de la actividad sobre la Dimensión del Desarrollo correspondiente bajo modalidad de valoración descriptiva/cualitativa conforme al SIEE y al Decreto 2247 de 1997, sin exigir pesos numéricos de reprobación. | Actividad de Preescolar configurada por dimensión del desarrollo en cumplimiento de `RN-CAL-02`. |
| **A2** | Asociación de criterios flexibilizados confidenciales para estudiantes con PIAR (paso 3) | Si en el grupo existen estudiantes matriculados con marcador confidencial PIAR activo (`UC-12`, `RN-EST-08`), el docente registra los ajustes razonables o criterios de evaluación flexibilizados aplicables a dichos estudiantes en la actividad, manteniendo la reserva legal frente a terceros (Decreto 1421 de 2017). | Ajustes razonables vinculados a la actividad para evaluación inclusiva en `UC-17` (`RN-CAL-08`). |
| **A3** | Continuidad y administración de actividades por docente suplente en reemplazo temporal (pasos 1 y 2) | Cuando el docente titular se encuentra en licencia o incapacidad y se ha asignado un docente suplente en `UC-15` (`A3`), el sistema habilita al suplente activo para administrar las actividades abiertas del periodo y crear las restantes, conservando en auditoría la autoría histórica de cada versión (`RN-PRO-06`). | Continuidad pedagógica del curso garantizada sin dejar actividades huérfanas. |
| **A4** | Modificación de ponderación o fecha de una actividad ya calificada mientras el periodo sigue abierto (pasos 3 a 6) [parte de A4 original] | Si el docente necesita ajustar el peso porcentual o la fecha de una actividad publicada que ya tiene calificaciones en `UC-17` mientras el periodo está abierto, el sistema exige registrar justificación obligatoria, valida que la nueva suma no exceda el 100.00%, recalcula atómicamente los promedios parciales del grupo y guarda la versión anterior y nueva en auditoría. | Actividad ajustada con recálculo consistente y trazabilidad completa en auditoría. |
| **E1** | Ponderación fuera de rango o suma acumulada de pesos supera el 100.00% (paso 4) [antes A1] | El sistema detecta que el peso ingresado es menor o igual a cero o que la suma de ponderaciones de la categoría/periodo excede el 100.00% (o no suma exactamente 100.00% al validar completitud para cierre); señala los componentes que causan el descuadre y rechaza la publicación con `HTTP 400/409`. | No se publica ni se calcula con pesos inconsistentes (`RN-CAL-01`). |
| **E2** | Fecha de la actividad fuera del rango del periodo académico o después de la restricción configurada (paso 4) [antes A2] | El sistema comprueba que la fecha programada cae fuera de `[fechaInicio, fechaFin]` del periodo académico (`UC-14`) o de la ventana autorizada; rechaza la operación con `HTTP 409` o exige autorización excepcional motivada y auditada de Coordinación Académica. | La actividad conserva un calendario coherente con el periodo lectivo. |
| **E3** | No existe versión del SIEE aprobada y vigente para el periodo (paso 2) [antes A3] | El sistema detecta ausencia de SIEE vigente para el año lectivo/periodo (`UC-26`) y bloquea la publicación con `HTTP 409` hasta resolver la configuración institucional, sin utilizar escalas ni porcentajes por defecto inventados. | Configuración institucional explícita requerida antes de evaluar (`RN-01`). |
| **E4** | Periodo académico cerrado o intento de eliminar físicamente una actividad ya calificada (paso 2 o 5) [parte de A4 original] | El sistema detecta que el periodo académico se encuentra en estado `CERRADO` (`UC-20`) o que se solicita borrar una actividad que ya posee notas registradas en `UC-17`; bloquea la operación con `HTTP 409` salvo reapertura formal autorizada con motivo y versión histórica. | No se altera un periodo cerrado ni se destruyen calificaciones registradas. |
| **E5** | Docente no asignado a la asignatura/grupo o con asignación vencida/suspendida (paso 2) [antes A5] | El backend comprueba en `UC-15` que el docente autenticado no posee asignación activa sobre el grupo y asignatura/dimensión y devuelve `HTTP 403 Forbidden` aunque la interfaz o una URL directa intenten invocar el `endpoint`, registrando el intento en auditoría. | Sin escritura indebida; control de acceso contextual garantizado. |

---

## UC-17 · Registrar y modificar calificaciones con cálculo de desempeño nacional

| Campo | Detalle |
| :--- | :--- |
| **ID** | `UC-17` |
| **Nombre** | Registrar y modificar calificaciones con cálculo de desempeño nacional |
| **Actores principales** | Docente de área (titular o suplente vigente en `UC-15`); Estudiante (únicamente para el envío de su autoevaluación/coevaluación cuando la actividad de `UC-16` sea de dicha modalidad y el SIEE lo habilite, sujeta a validación del docente) |
| **Actores secundarios / sistemas** | Coordinador Académico / Rector (autorización de ventana excepcional); motor de cálculo SIEE/`Strategy` con `BigDecimal` (`UC-26`); servicio de estructura y Plan de Estudios (`UC-PROV-07`); servicio de matrícula (`UC-13`, `UC-PROV-09`); servicio de asistencia (`UC-19`); repositorio; auditoría/eventos |
| **Descripción** | Permite registrar o corregir (con justificación obligatoria) las calificaciones y retroalimentaciones de los estudiantes con matrícula activa en una actividad publicada (`UC-16`), gestionar el estado pendiente por inasistencia justificada (`UC-19`), y calcular en backend de forma determinista con `BigDecimal`: (1) el promedio por categoría evaluativa, (2) la calificación definitiva parcial/acumulada de la Asignatura con su equivalencia en la Escala Nacional (Superior, Alto, Básico, Bajo), y (3) la calificación definitiva ponderada del Área Curricular conforme a los pesos del Plan de Estudios (`UC-PROV-07` `A2`). |
| **Propósito / valor** | Producir resultados académicos exactos, trazables, explicables y reproducibles en el servidor (Documento Maestro V2.0 Secciones 8 y 9), impidiendo cambios no autorizados, notas punitivas por convivencia o inconsistencias por guardado parcial. |
| **Disparador** | El docente asignado abre el libro de calificaciones de una actividad publicada en su grupo e ingresa o modifica las calificaciones. |
| **Precondiciones** | Actividad en estado `PUBLICADA` (`UC-16`); asignación docente vigente sobre el grupo y asignatura/dimensión (`UC-15`); estudiantes con matrícula activa en el grupo en la fecha de la actividad (`UC-13`, `UC-PROV-09`); periodo académico en estado abierto y dentro de ventana de calificación (`UC-14`); escala institucional y Plan de Estudios versionados y completos (`UC-26`, `UC-PROV-07`). |
| **Invariantes y reglas de negocio** | Toda validación y cálculo se ejecuta en backend utilizando `BigDecimal` con la escala decimal y modo de redondeo configurados en la versión vigente del SIEE (`DEC-DOM-02`, `RN-CAL-03`), mapeando explícitamente cada resultado a la Escala Nacional (Superior, Alto, Básico, Bajo, `RN-02`) y consolidando automáticamente las asignaturas hacia su Área Curricular según los porcentajes de `UC-PROV-07`. En Educación Preescolar se registran valoraciones descriptivas/cualitativas por dimensión sin reprobación (`RN-CAL-02`). Cuando un estudiante tiene inasistencia justificada avalada en `UC-19` para la fecha de la actividad, se registra el estado `PENDIENTE_POR_EXCUSA_JUSTIFICADA` sin penalizar con cero (`RN-CAL-04`). Se prohíbe rebajar o anular calificaciones académicas como sanción disciplinaria de convivencia (`RN-08`, `RN-CAL-05`, Sentencia T-004 de 2026). Se respeta la coherencia temporal con las novedades de matrícula (traslados entrantes, cambios internos de grupo y bloqueo posterior a la fecha efectiva de retiro, `RN-CAL-06`). Toda corrección de una nota existente exige guardar valor anterior, valor nuevo, actor, fecha/hora y justificación obligatoria (`RN-10`). El guardado por lote es atómico (`RN-11`): si una fila contiene un error inválido, se rechaza el lote completo informando el detalle por fila sin persistir cambios parciales. |
| **Postcondiciones** | Calificaciones de la actividad, promedios de categoría, definitiva de asignatura, definitiva ponderada de área curricular y desempeños nacionales calculados y persistidos en una transacción atómica; correcciones trazadas en auditoría con valor anterior, valor nuevo y motivo. |
| **Tipo** | Básico |
| **Prioridad** | Alta |

### Flujo principal

| Paso | Actor / responsable | Acción e interacción del sistema |
| :--- | :--- | :--- |
| 1 | Docente de área | Selecciona el grupo, la asignatura/dimensión y la actividad publicada (`UC-16`); el sistema carga la planilla oficial del grupo (`UC-PROV-10`) identificando los estudiantes con matrícula activa, las novedades de traslado/cambio de grupo (`UC-PROV-09`) y las alertas de inasistencia justificada en la fecha de la actividad (`UC-19`). |
| 2 | Docente de área | Ingresa las calificaciones (numéricas en Básica/Media o descriptivas en Preescolar), el estado de evaluación diferida cuando exista excusa justificada y la retroalimentación pedagógica; si modifica una calificación previamente guardada, registra la justificación obligatoria del cambio. |
| 3 | Sistema | Valida en backend la identidad y asignación vigente del docente (`UC-15`), el estado editable del periodo (`UC-14`), la vigencia de la matrícula de cada estudiante en la fecha de la actividad (`RN-CAL-06`), el rango y precisión `BigDecimal` de cada nota según la escala del SIEE (`UC-26`), y la existencia de motivo en toda nota corregida. |
| 4 | Sistema | Ejecuta el motor de cálculo SIEE (`Strategy` con `BigDecimal`): calcula el resultado de la actividad y de su categoría, actualiza la definitiva acumulada de la Asignatura con su equivalencia en la Escala Nacional (Superior, Alto, Básico, Bajo) y recalcula la definitiva ponderada del Área Curricular aplicando los pesos del Plan de Estudios (`UC-PROV-07` `A2`). |
| 5 | Sistema | Persiste en una única transacción atómica con control de concurrencia optimista las calificaciones de la actividad, su versión, los consolidados de asignatura y área del periodo y los registros obligatorios de auditoría (con valor anterior, valor nuevo y justificación en correcciones). |
| 6 | Sistema | Devuelve el resultado calculado de asignatura y área, el desempeño nacional y el estado de persistencia; nunca reporta éxito si la transacción falló. |

### Flujos alternos y de excepción

Cada alternativa indica la condición de entrada, el tratamiento esperado y el resultado. Los flujos `A#` pueden retomar el principal cuando así se indique; los casos `E#` deben terminar de forma controlada, sin persistir cambios parciales.

| ID | Condición / paso de origen | Tratamiento / secuencia alterna | Resultado / estado final |
| :--- | :--- | :--- | :--- |
| **A1** | Estudiante con inasistencia justificada en la fecha de la actividad evaluativa (pasos 1 y 2) | Cuando el estudiante registra ausencia justificada en `UC-19` para la fecha de la evaluación (incapacidad médica, calamidad o representación institucional), el sistema habilita marcar la celda en estado `PENDIENTE_POR_EXCUSA_JUSTIFICADA` con fecha de evaluación supletoria, impidiendo computar un cero punitivo hasta que se registre la nota de la prueba diferida. | Derecho de evaluación supletoria garantizado conforme al SIEE y a `RN-CAL-04`. |
| **A2** | Corrección justificada de calificación dentro de periodo abierto o en cumplimiento de fallo de reclamación (pasos 2 a 5) | Cuando el docente corrige un error material en periodo abierto o aplica una rectificación aprobada en `UC-PROV-15`, ingresa el motivo y la referencia de la reclamación (cuando aplique); el sistema conserva la versión anterior en el historial de la calificación, recalcula atómicamente asignatura y área y audita el cambio. | Calificación rectificada con trazabilidad completa del valor anterior, valor nuevo y motivo. |
| **A3** | Registro de valoración descriptiva y cualitativa en Educación Preescolar (pasos 2 y 4) | En grupos de Prejardín, Jardín o Transición (`UC-PROV-07` `A1`), el docente registra el nivel cualitativo de avance y la observación descriptiva sobre la Dimensión del Desarrollo evaluada sin aplicar promedios numéricos reprobatorios. | Evaluación cualitativa de Preescolar registrada conforme al Decreto 2247 de 1997 (`RN-CAL-02`). |
| **A4** | Visualización y coherencia ante cambios internos de grupo, traslados entrantes o retiros (pasos 1 y 3) | Si un estudiante fue reubicado de otro grupo del mismo grado (`UC-PROV-09` `A1`), el sistema integra y conserva sus notas previas; si ingresó por traslado posterior a la fecha de la actividad (`UC-13` `A5`), marca la actividad anterior como `EXIMIDA_CONVALIDADA` según el SIEE; y si se encuentra retirado (`UC-PROV-09` `A3`/`A4`), preserva sus notas previas como solo lectura y bloquea capturas posteriores a la `fechaEfectivaRetiro`. | Consistencia integral entre el libro de calificaciones y el historial de matrícula (`RN-CAL-06`). |
| **A5** | Registro de autoevaluación/coevaluación por el estudiante con validación del docente (pasos 1 y 2) | Cuando la actividad configurada en `UC-16` es de modalidad `AUTOEVALUACION` o `COEVALUACION`, el estudiante registra su valoración fundamentada dentro del plazo habilitado y el docente titular la revisa y consolida en la planilla de `UC-17`. | Participación del estudiante en su autoevaluación trazada conforme al Decreto 1290 Art. 4. |
| **E1** | Nota fuera de rango, formato inválido o nulo no permitido en el lote (paso 3) [antes A1] | Si una o más filas contienen una calificación fuera del intervalo `[escalaMínima, escalaMáxima]` del SIEE o un formato inválido, el backend aplica atomicidad transaccional estricta (`RN-11`): rechaza el guardado del lote completo con `HTTP 400` y devuelve el detalle exacto de los errores por fila y campo para su corrección sin persistir datos parciales. | No se persiste ningún dato inválido ni lotes parcialmente guardados. |
| **E2** | Periodo académico cerrado o ventana de digitación vencida (paso 3) [antes A2] | El sistema detecta que el periodo se encuentra en estado `CERRADO` (`UC-20`) o fuera de la ventana de captura (`UC-14`) sin contar con autorización excepcional de reapertura; deniega la escritura ordinaria con `HTTP 409`. | Nota sin cambios; se protege la inmutabilidad del cierre de periodo. |
| **E3** | Docente sin asignación vigente, estudiante de otro grupo o intento de aplicar nota punitiva por convivencia (paso 3) [antes A3] | El backend detecta que el docente no tiene asignación activa sobre el grupo/asignatura (`UC-15`), que el estudiante no pertenece al grupo o que se intenta alterar una nota por sanción disciplinaria de convivencia en contra de `RN-08`; deniega la operación con `HTTP 403/409` y registra el intento en auditoría. | No se modifica expediente ajeno ni se vulnera la continuidad educativa (`RN-CAL-05`). |
| **E4** | Falta equivalencia en la escala de valoración o descuadre en ponderación de área (paso 4) [antes A4] | El sistema detecta una inconsistencia en la configuración de equivalencias del SIEE o en los pesos del área; no inventa el desempeño nacional ni persiste un resultado incompleto, aborta la transacción y genera incidencia a Coordinación Académica. | Resultado inconsistente bloqueado sin suposiciones arbitrarias. |
| **E5** | Corrección de calificación existente sin justificación obligatoria (paso 3) [antes A5] | El sistema comprueba que se modificó el valor de una nota previamente registrada sin ingresar el motivo de la corrección; rechaza la solicitud con `HTTP 400` y conserva intacta la nota original. | No se permite ninguna modificación de nota sin motivo auditable. |
| **E6** | Conflicto concurrente sobre la misma calificación (paso 5) [antes A6] | El sistema detecta mediante versión optimista (`@Version`) que el registro fue modificado simultáneamente por otra transacción; aborta con `HTTP 409` y solicita recargar y confirmar contra el valor actual. | Se evita sobrescritura silenciosa por concurrencia. |
| **E7** | Error en base de datos o al persistir el registro obligatorio de auditoría (paso 5) [antes A7] | Si falla la persistencia de negocio o el registro atómico en auditoría/`outbox` dentro de la transacción, el sistema revierte íntegramente todos los cambios (`rollback`). | No existen calificaciones ni correcciones sin su trazabilidad obligatoria. |

---

## UC-18 · Gestionar actividades de apoyo, nivelación y recuperación

| Campo | Detalle |
| :--- | :--- |
| **ID** | `UC-18` |
| **Nombre** | Gestionar actividades de apoyo, nivelación y recuperación |
| **Actores principales** | Docente de área (titular o suplente vigente en `UC-15`); Coordinador Académico (supervisión, validación de cronogramas de nivelación y autorización excepcional) |
| **Actores secundarios / sistemas** | Estudiante; Acudiente / adulto responsable validado (`UC-06`); Docente titular/director de grupo (seguimiento de su curso); motor de reglas SIEE (`UC-26`); servicio de asistencia (`UC-19`); servicio de notificaciones; servicio de auditoría |
| **Descripción** | Permite identificar a los estudiantes con dificultades académicas o con resultados en Desempeño Bajo, programar y notificar formalmente al estudiante y a su acudiente el plan de apoyo pedagógico o actividad de nivelación/recuperación, registrar la asistencia, las evidencias, el Acta de Nivelación / Superación de Debilidades y la calificación obtenida, y recalcular la nota definitiva aplicando la fórmula matemática configurada en la versión vigente del SIEE (`UC-26`) bajo el principio de no desmejoramiento y conservando intacta la calificación original. |
| **Propósito / valor** | Garantizar oportunidades pedagógicas oportunas y documentadas para la superación de debilidades conforme al Decreto 1290 de 2009 (Arts. 4, 11, 12 y 13 compilados en el Decreto 1075 de 2015), asegurando un cálculo transparente y auditable ante las Comisiones de Evaluación y Promoción (`UC-21`). |
| **Disparador** | El sistema identifica automáticamente los estudiantes elegibles con Desempeño Bajo o dificultades según el SIEE (incluyendo estudiantes repitentes con seguimiento activo de `UC-13` `A6`), o el docente/coordinador programa una actividad de apoyo o nivelación. |
| **Precondiciones** | Versión del SIEE vigente con criterios de elegibilidad, plazos y fórmula matemática de recuperación configurados (`UC-26`, `RN-CFG-04`); asignación docente vigente (`UC-15`); matrícula activa del estudiante (`UC-13`); periodo o ventana de nivelación/recuperación habilitada en el calendario académico (`UC-14`). |
| **Invariantes y reglas de negocio** | La elegibilidad, el momento y la fórmula matemática de impacto de la nivelación/recuperación (`REEMPLAZO_TOTAL`, `TOPE_BASICO` o `PROMEDIO_PONDERADO`) provienen exclusivamente de la versión vigente del SIEE (`UC-26`, `RN-CFG-04`). Jamás se borra ni se sobrescribe la calificación original: el sistema almacena siempre `notaOriginal`, `notaObtenidaNivelacion`, `formulaAplicada`, `notaDefinitivaVigente` y la referencia al Acta de Nivelación (`RN-10`, `RN-CAL-07`). Rige la invariante de no desmejoramiento: si la nota calculada tras la nivelación es inferior a la `notaOriginal` (o si el estudiante no asiste sin excusa justificada), se conserva la `notaOriginal` como `notaDefinitivaVigente` sin rebajarla. Toda convocatoria y todo resultado de nivelación deben notificarse de forma trazable al acudiente con vínculo verificado (`RN-12`, Decreto 1290 Art. 13). |
| **Postcondiciones** | Plan de apoyo, convocatoria notificada, Acta de Nivelación, asistencia, evidencia y resultado de recuperación persistidos transaccionalmente; definitiva de asignatura y de área curricular recalculadas conforme a la fórmula del SIEE respetando el no desmejoramiento; notificaciones emitidas y evento registrado en auditoría. |
| **Tipo** | Básico |
| **Prioridad** | Alta |

### Flujo principal

| Paso | Actor / responsable | Acción e interacción del sistema |
| :--- | :--- | :--- |
| 1 | Sistema | Identifica y permite filtrar a los estudiantes del grupo y asignatura que presentan Desempeño Bajo o cumplen los criterios de apoyo/nivelación configurados en la versión vigente del SIEE (`UC-26`), resaltando también a los estudiantes en condición de repitencia (`UC-13` `A6`). |
| 2 | Docente de área / Coordinador Académico | Define el plan de apoyo o actividad de nivelación: desempeños por superar, estrategia pedagógica, actividades de refuerzo, instrumento de evaluación/sustentación, fechas dentro de la ventana del calendario (`UC-14`) y estudiantes convocados. |
| 3 | Sistema | Valida la elegibilidad de los estudiantes y las fechas respecto del SIEE vigente (`UC-26`), registra la convocatoria y envía notificación formal con acuse de entrega al estudiante y a su acudiente verificado (`RN-12`). |
| 4 | Docente de área / Coordinador Académico | Registra la asistencia del estudiante a la jornada de nivelación, las evidencias entregadas, el soporte del Acta de Nivelación / Superación de Debilidades y la calificación obtenida en la evaluación de recuperación. |
| 5 | Sistema | Ejecuta en una transacción atómica con `BigDecimal` la fórmula de recuperación configurada en el SIEE (reemplazo total, reemplazo con tope máximo en Básico o promedio ponderado), aplica la invariante de no desmejoramiento (`notaVigente = máx(notaOriginal, notaCalculada)`), recalcula la definitiva de la asignatura y del área curricular conservando intacto el historial de la nota original, y genera el registro en auditoría. |
| 6 | Sistema | Notifica el resultado oficial y el estado de superación o persistencia de la dificultad al estudiante, al acudiente verificado, al Director de grupo y a Coordinación Académica (dejando el antecedente listo para la Comisión de Evaluación y Promoción en `UC-21`). |

### Flujos alternos y de excepción

Cada alternativa indica la condición de entrada, el tratamiento esperado y el resultado. Los flujos `A#` pueden retomar el principal cuando así se indique; los casos `E#` deben terminar de forma controlada, sin persistir cambios parciales.

| ID | Condición / paso de origen | Tratamiento / secuencia alterna | Resultado / estado final |
| :--- | :--- | :--- | :--- |
| **A1** | El estudiante obtiene en la nivelación una nota inferior a su nota original o no se presenta sin excusa (pasos 4 y 5) | Si el estudiante presenta la nivelación pero obtiene un resultado inferior a su `notaOriginal`, o no asiste a la citación sin excusa justificada vencido el plazo, el sistema registra en el Acta de Nivelación la calificación obtenida o el estado `NO_PRESENTO`, conserva intacta la `notaOriginal` como `notaDefinitivaVigente` sin disminuirla y deja trazada la evidencia de que el colegio garantizó la oportunidad pedagógica. | Se respeta la invariante de no desmejoramiento (`RN-CAL-07`) y queda documentada el acta de seguimiento. |
| **A2** | Inasistencia a la citación de nivelación con excusa justificada avalada en `UC-19` (paso 4) | Cuando el estudiante no pudo presentarse en la fecha de la nivelación por incapacidad médica o calamidad validada en `UC-19`, el sistema permite reprogramar por única vez la fecha supletoria de sustentación dentro del plazo autorizado por Coordinación Académica. | Se garantiza el derecho a presentar la nivelación ante fuerza mayor justificada. |
| **A3** | Plan de apoyo formativo continuo o preventivo que no modifica directamente la nota definitiva (pasos 4 y 5) [antes A3] | Cuando la actividad registrada corresponde a un plan de acompañamiento continuo durante el periodo o en Preescolar que según el SIEE tiene carácter formativo sin sustituir una calificación definitiva, el sistema registra la participación, evidencia y avance cualitativo sin alterar el promedio numérico. | Se respeta la regla institucional del SIEE conservando la evidencia pedagógica. |
| **A4** | Ejecución de Habilitación / Recuperación Final de Año dispuesta por la Comisión de Evaluación y Promoción (pasos 1 a 6) | Cuando la recuperación corresponde a la habilitación final de áreas pendientes al cierre del año lectivo autorizada en `UC-21`, el sistema habilita el registro del Acta de Habilitación Final y remite el resultado actualizado a `UC-21` para definir la promoción anual del estudiante. | Integración completa entre el módulo de evaluación y la decisión de promoción anual (`UC-21`). |
| **E1** | Estudiante no cumple el criterio de elegibilidad configurado en el SIEE (paso 3) [antes A1] | El sistema detecta que el estudiante no cumple las condiciones del SIEE para nivelación modificatoria de nota (por ejemplo, ya cuenta con Desempeño Alto/Superior o supera el tope de áreas cuando aplique); explica el motivo del bloqueo y permite solicitar revisión a Coordinación Académica. | No se crean nivelaciones irregulares por fuera de las reglas del SIEE. |
| **E2** | Plazo de ventana de nivelación vencido o periodo bloqueado sin autorización (paso 3 o 4) [antes A2] | El sistema comprueba que la ventana de nivelación venció o que el periodo ya fue cerrado definitivamente (`UC-20`) sin autorización excepcional motivada de Coordinación/Rectoría; rechaza el registro ordinario con `HTTP 409`. | No se altera una calificación cerrada sin reapertura controlada y auditada. |
| **E3** | Actividad de nivelación duplicada o acta repetida para el mismo corte (paso 3 o 4) [antes A4] | El sistema detecta duplicidad de convocatoria o de resultado de recuperación para el mismo estudiante, asignatura, periodo y momento evaluativo; bloquea el duplicado con `HTTP 409` y solicita revisar el registro existente. | Se evita la doble contabilización de nivelaciones. |
| **E4** | Nota de recuperación fuera de rango o inconsistencia en la escala/fórmula del SIEE (paso 5) [antes A5] | El sistema detecta que la calificación ingresada está fuera de la escala institucional o que falta parametrización de equivalencia en el SIEE; conserva intacta la versión anterior, rechaza la transacción y remite incidencia a Coordinación Académica. | No se publica ningún resultado ambiguo ni fuera de escala. |
| **E5** | Docente sin asignación vigente intenta programar o calificar una nivelación (paso 2 o 4) | El backend comprueba que el usuario no tiene asignación activa sobre el grupo y asignatura en `UC-15`; deniega la operación con `HTTP 403 Forbidden` y registra el intento en auditoría. | Operación protegida por autorización contextual. |

---

## UC-02 · Consultar calificaciones propias

| Campo | Detalle |
| :--- | :--- |
| **ID** | `UC-02` |
| **Nombre** | Consultar calificaciones propias |
| **Actores principales** | Estudiante (cuyo nivel educativo tenga habilitado el acceso directo conforme al parámetro institucional `RN-SEG-09` de `UC-01` y `UC-PROV-13`) |
| **Actores secundarios / sistemas** | Servicio académico de calificaciones; servicio de configuración institucional SIEE (`UC-PROV-14`); servicio de reclamaciones académicas (`UC-PROV-15`); servicio de auditoría de consultas |
| **Descripción** | Permite al estudiante autenticado consultar exclusivamente sus propias calificaciones publicadas por año lectivo y periodo académico, mostrando la estructura jerárquica por Área Curricular y Asignatura (o Dimensiones en Preescolar), el desglose de actividades evaluativas publicadas (`UC-16`) con su peso, nota y retroalimentación, la trazabilidad de nivelaciones (`UC-18`: nota original, nota de recuperación y nota vigente), las convalidaciones (`UC-PROV-16`) y su equivalencia en la Escala Nacional, habilitando la radicación de reclamaciones académicas (`UC-PROV-15`) dentro del plazo del SIEE. |
| **Propósito / valor** | Facilitar el autoseguimiento del proceso de aprendizaje y garantizar el derecho del estudiante a conocer oportunamente sus valoraciones (Decreto 1290 de 2009 Art. 12 num. 3), sin exponer calificaciones de otros estudiantes ni borradores privados. |
| **Disparador** | El estudiante autenticado abre la sección Mis calificaciones y filtra por año lectivo, periodo, área o asignatura. |
| **Precondiciones** | Sesión válida con rol activo Estudiante (`UC-01`, incluyendo modo histórico restringido `UC-01` `A3` para egresados o retirados); el estudiante posee matrícula vigente o historial académico asociado (`UC-13`, `UC-PROV-10`). |
| **Invariantes y reglas de negocio** | La consulta se limita estrictamente al estudiante titular de la sesión autenticada obtenida en el servidor; cualquier identificador enviado desde el cliente es ignorado o rechazado con `HTTP 403` si no coincide (`RN-09`). Solo se exponen actividades y resultados en estado publicado según el estado del periodo. Se muestra de forma transparente tanto la nota consolidada de cada Área Curricular como la de sus Asignaturas componentes y el detalle de nivelaciones (nota original vs. nota de recuperación). Ninguna consulta recalcula ni modifica registros en la base de datos. |
| **Postcondiciones** | Se presenta la información académica autorizada del estudiante indicando año lectivo, periodo, estado (En curso parcial vs. Cerrado oficial), versión del SIEE aplicada y fecha de última actualización. |
| **Tipo** | Básico |
| **Prioridad** | Alta |

### Flujo principal

| Paso | Actor / responsable | Acción e interacción del sistema |
| :--- | :--- | :--- |
| 1 | Estudiante | Selecciona el año lectivo (actual o histórico cursado), el periodo académico y el área o asignatura que desea consultar. |
| 2 | Sistema | Obtiene la identidad del estudiante exclusivamente desde el contexto seguro de la sesión en el servidor (`UC-01`) y verifica su matrícula asociada para el año lectivo seleccionado (`UC-13` / `UC-PROV-10`). |
| 3 | Sistema | Consulta los consolidados publicables de áreas y asignaturas, el detalle de actividades publicadas (`UC-16`/`17`), las actas de nivelación aplicadas (`UC-18`), las convalidaciones (`UC-PROV-16`), las equivalencias de la versión del SIEE aplicable (`UC-26`) y el estado del periodo. |
| 4 | Sistema | Presenta los resultados organizados jerárquicamente por Área Curricular y Asignatura (o Dimensiones), mostrando el desglose de actividades por categoría, las notas, el desempeño nacional (Superior, Alto, Básico, Bajo), la trazabilidad de recuperaciones y la retroalimentación pedagógica publicada. |
| 5 | Estudiante | Visualiza el detalle o cambia los filtros de consulta (sin modificar registros); si advierte una inconsistencia en una nota publicada dentro del plazo legal del SIEE, selecciona Presentar reclamación enlazando con `UC-PROV-15`. |

### Flujos alternos y de excepción

Cada alternativa indica la condición de entrada, el tratamiento esperado y el resultado. Los flujos `A#` pueden retomar el principal cuando así se indique; los casos `E#` deben terminar de forma controlada, sin persistir cambios parciales.

| ID | Condición / paso de origen | Tratamiento / secuencia alterna | Resultado / estado final |
| :--- | :--- | :--- | :--- |
| **A1** | Aún no existen calificaciones registradas para la actividad o asignatura (paso 3) [antes A1] | El sistema presenta explícitamente el estado «Sin calificaciones disponibles / Pendiente de calificación» (o «Pendiente por excusa justificada» cuando aplique `RN-CAL-04`), diferenciando con claridad la ausencia de captura de una calificación igual a cero o mínima. | Consulta completada con transparencia sin inducir a error. |
| **A2** | Periodo académico en captura parcial o aún no cerrado (paso 3 y 4) [antes A2] | Mientras el periodo está en estado `ABIERTO`, el sistema muestra únicamente las actividades ya publicadas por los docentes, etiqueta el acumulado como «Promedio parcial a la fecha (porcentaje evaluado)» y oculta los borradores no publicados. | No se revela información en borrador y se distingue el avance parcial de la definitiva de cierre. |
| **A3** | Solicitud de revisión o reclamación sobre una calificación publicada (paso 5) | Cuando el estudiante selecciona reclamar una calificación de actividad, nivelación o periodo que se encuentra dentro del término de días hábiles del SIEE (`UC-26`), el sistema abre el formulario de radicación de reclamación académica hacia `UC-PROV-15`. | Derecho de reclamación académica habilitado de forma contextual (`RN-CAL-09`). |
| **E1** | No existe matrícula para el año lectivo seleccionado (paso 2) [antes A3] | El sistema informa que el estudiante no registra matrícula ni historial para el año filtrado y muestra únicamente los años lectivos autorizados en su trayectoria. | Respuesta controlada sin acceso a datos ajenos. |
| **E2** | Se solicita un estudiante distinto manipulando parámetros de la petición (paso 2) [antes A4] | El backend detecta que la petición intenta consultar un identificador de estudiante distinto al titular de la sesión autenticada; rechaza la solicitud con `HTTP 403 Forbidden` y registra el intento de acceso horizontal indebido en auditoría. | No se divulgan calificaciones de terceros; evento de seguridad auditado. |
| **E3** | Falla técnica en la consulta o se detectan datos con configuración incompleta (paso 3) [antes A5] | El sistema devuelve un mensaje de error controlado (`HTTP 500/409`), no expone resultados parciales corruptos como definitivos y registra la incidencia técnica en los logs protegidos. | Integridad y confiabilidad de la vista preservadas. |

---

## UC-08 · Consultar calificaciones del estudiante vinculado

| Campo | Detalle |
| :--- | :--- |
| **ID** | `UC-08` |
| **Nombre** | Consultar calificaciones del estudiante vinculado |
| **Actores principales** | Acudiente / adulto responsable validado |
| **Actores secundarios / sistemas** | Servicio de relaciones acudiente-estudiante (`UC-06`, `UC-PROV-05`); servicio académico de calificaciones; servicio de reclamaciones académicas (`UC-PROV-15`); servicio de consulta de boletines publicados (`UC-20`, `UC-11`); servicio de auditoría |
| **Descripción** | Permite al acudiente autenticado consultar en línea las calificaciones publicadas por área curricular, asignatura o dimensión del desarrollo, el desglose de actividades evaluativas (`UC-16`/`17`), las retroalimentaciones docentes y el historial de nivelaciones (`UC-18`) exclusivamente para el estudiante con el cual posee vínculo activo, verificado y libre de restricciones legales (`UC-06`, `UC-PROV-05`). Permite radicar reclamaciones académicas (`UC-PROV-15`) y enlazar hacia la visualización/descarga del boletín oficial cuando el periodo ha sido cerrado y publicado en `UC-20` / `UC-11`. |
| **Propósito / valor** | Garantizar el derecho y deber de los padres de familia o acudientes de realizar seguimiento académico continuo en los términos del Decreto 1290 de 2009 (Arts. 14 y 15 compilados en el Decreto 1075 de 2015), protegiendo la confidencialidad del menor (Ley 1098 de 2006 y Ley 1581 de 2012). |
| **Disparador** | El acudiente selecciona un estudiante vinculado (desde `UC-06`) y abre la sección Calificaciones. |
| **Precondiciones** | Sesión válida con rol activo Acudiente (`UC-01`); vínculo estudiante-acudiente en estado `ACTIVO` y `VERIFICADO` y sin medida de restricción legal (`UC-12`, `UC-PROV-05`). |
| **Invariantes y reglas de negocio** | El backend revalida en cada solicitud que el estudiante consultado pertenezca a los vínculos activos, verificados y sin restricción legal del acudiente en sesión (`RN-09`, `RN-EST-02`). No se muestran borradores ni notas no publicadas; las equivalencias a la Escala Nacional (Superior, Alto, Básico, Bajo) se obtienen de la versión del SIEE aplicable al periodo (`UC-26`). La emisión oficial de boletines pertenece a `UC-20` y su repositorio a `UC-11`; `UC-08` presenta el seguimiento en línea del libro de calificaciones y solo habilita el enlace al boletín oficial cuando este ha sido emitido y publicado. |
| **Postcondiciones** | Se presentan al acudiente las calificaciones publicables por área, asignatura/dimensión, actividades y nivelaciones del estudiante vinculado, indicando el estado del periodo y habilitando el acceso a reclamaciones (`UC-PROV-15`) o al boletín publicado (`UC-20`/`UC-11`). |
| **Tipo** | Básico |
| **Prioridad** | Alta |

### Flujo principal

| Paso | Actor / responsable | Acción e interacción del sistema |
| :--- | :--- | :--- |
| 1 | Acudiente | Selecciona al estudiante dentro de su lista de estudiantes vinculados (`UC-06`) y elige el año lectivo y periodo académico a consultar. |
| 2 | Sistema | Valida en backend la vigencia y verificación del vínculo estudiante-acudiente, comprueba la ausencia absoluta de restricciones legales (`UC-PROV-05`) y verifica el estado de publicación del periodo. |
| 3 | Sistema | Consulta los consolidados de áreas y asignaturas/dimensiones, las calificaciones de actividades publicadas (`UC-16`/`17`), las actas de nivelación (`UC-18`), las convalidaciones (`UC-PROV-16`) y las equivalencias de la versión del SIEE correspondiente (`UC-26`). |
| 4 | Sistema | Presenta los resultados jerarquizados por Área Curricular y Asignatura (o Dimensiones en Preescolar), incluyendo notas de actividades, desempeño nacional, retroalimentación docente, trazabilidad de recuperaciones y enlace al boletín oficial si el periodo ya fue publicado en `UC-20`. |
| 5 | Acudiente | Revisa el seguimiento académico del estudiante; puede iniciar una reclamación sobre una calificación en `UC-PROV-15` o abrir/descargar el boletín oficial publicado cuando el periodo esté cerrado (`UC-20` / `UC-11`). |

### Flujos alternos y de excepción

Cada alternativa indica la condición de entrada, el tratamiento esperado y el resultado. Los flujos `A#` pueden retomar el principal cuando así se indique; los casos `E#` deben terminar de forma controlada, sin persistir cambios parciales.

| ID | Condición / paso de origen | Tratamiento / secuencia alterna | Resultado / estado final |
| :--- | :--- | :--- | :--- |
| **A1** | No existen calificaciones publicadas aún en el periodo (paso 3) [antes A1] | El sistema informa que aún no se han registrado calificaciones publicadas para las actividades del periodo, diferenciando ausencia de nota de un cero, y muestra el calendario del periodo vigente (`UC-PROV-08`). | No se exponen borradores; información clara para la familia. |
| **A2** | Calificación corregida tras el cierre del periodo o por resolución de reclamación (paso 3 y 4) [antes A3] | Cuando una nota fue rectificada mediante el flujo excepcional de cierre (`UC-20`) o por fallo favorable de reclamación (`UC-PROV-15`) después de haberse publicado un boletín, el sistema muestra la calificación vigente actualizada, conserva la traza visible de la rectificación y enlaza a la nueva versión vigente del boletín regenerado. | No se oculta la corrección ni se presentan versiones contradictorias del resultado. |
| **A3** | Consulta de valoración descriptiva por Dimensiones del Desarrollo en Educación Preescolar (pasos 3 y 4) | Si el estudiante vinculado cursa un grado del nivel Preescolar (Prejardín, Jardín o Transición), el sistema presenta el seguimiento cualitativo y descriptivo por cada Dimensión del Desarrollo conforme al Decreto 2247 de 1997 (`RN-CAL-02`). | Seguimiento familiar adaptado al régimen legal de Preescolar. |
| **A4** | Radicación de reclamación académica sobre una calificación por parte del acudiente (paso 5) | Si el acudiente detecta un error o inconformidad en una calificación publicada dentro del plazo en días hábiles del SIEE (`UC-26`), selecciona Presentar reclamación académica y el sistema inicia el trámite en `UC-PROV-15`. | Canal formal de reclamación familiar habilitado con trazabilidad. |
| **E1** | Vínculo no autorizado, en disputa, revocado o bloqueado por restricción legal (paso 2) [antes A2] | El backend comprueba que el usuario no tiene vínculo activo y verificado con el estudiante o que pesa sobre él una medida de protección o restricción legal (`UC-PROV-05` `A3`); deniega inmediatamente la consulta con `HTTP 403 Forbidden` independientemente de que el cliente conozca el ID del estudiante y registra alerta en auditoría. | Sin acceso a datos académicos del menor; protección reforzada garantizada. |
| **E2** | Inconsistencia en la equivalencia configurada de la escala de valoración (paso 3) [antes A4] | El sistema detecta un error de configuración en la escala del SIEE; no inventa el desempeño nacional, bloquea la visualización del cálculo afectado y reporta la incidencia a Coordinación Académica. | Se evita mostrar un resultado normativamente inconsistente. |
| **E3** | Intento de consultar un año lectivo no cursado o posterior al retiro en cuenta histórica (paso 2) | Si la cuenta del acudiente se encuentra en estado `INACTIVA_HISTORICA` (`UC-01` `A3`) e intenta consultar un año lectivo en el que el estudiante no estuvo matriculado, el sistema rechaza la consulta e informa únicamente los años históricos autorizados. | Alcance histórico restringido respetado estrictamente. |

---

## UC-PROV-15 · Presentar y resolver reclamaciones académicas sobre calificaciones

| Campo | Detalle |
| :--- | :--- |
| **ID** | `UC-PROV-15` (Identificador provisional — Brecha `BRC-NUEVA-05`) |
| **Nombre** | Presentar y resolver reclamaciones académicas sobre calificaciones |
| **Actores principales** | Estudiante y Acudiente / adulto responsable validado (como solicitantes de la reclamación); Docente de área titular o suplente vigente (resolución en primera instancia); Coordinador Académico (resolución en segunda instancia / apelación o por vencimiento de término) |
| **Actores secundarios / sistemas** | Rector / Consejo Académico / Comisión de Evaluación y Promoción (cuando el SIEE les asigne instancia colegiada sobre definitivas de periodo); servicio de calificaciones (`UC-17`, `UC-18`); servicio de cierre y boletines (`UC-20`); servicio de configuración SIEE (`UC-26`); servicio de notificaciones; auditoría |
| **Descripción** | Permite al estudiante o a su acudiente verificado radicar una reclamación formal y motivada sobre una calificación publicada de actividad (`UC-17`), de nivelación (`UC-18`) o de definitiva de periodo, y gestiona su resolución trazable en primera instancia ante el Docente de área y, en caso de apelación o falta de respuesta oportuna, en segunda instancia ante el Coordinador Académico conforme a los plazos en días hábiles configurados en el SIEE (`UC-26`), ejecutando atómicamente la rectificación de la nota cuando la decisión sea favorable. |
| **Propósito / valor** | Cumplir el requisito del Documento Maestro V2.0 (Secciones 8 y 9) y la obligación legal del Decreto 1290 de 2009 (Art. 4 num. 10 y Art. 11 num. 6) y la Ley 1581 de 2012 (Art. 15) de garantizar el debido proceso, el derecho de contradicción y la rectificación auditable de calificaciones. |
| **Disparador** | El Estudiante (desde `UC-02`) o el Acudiente verificado (desde `UC-08`) selecciona una calificación publicada y radica una reclamación académica, o el Docente / Coordinador Académico atiende una reclamación pendiente en su bandeja. |
| **Precondiciones** | Sesión válida (`UC-01`); vínculo verificado sin restricción legal si el solicitante es Acudiente (`UC-06`, `UC-PROV-05`); calificación objeto de reclamo publicada; solicitud presentada dentro del plazo de días hábiles parametrizado en la versión vigente del SIEE (`UC-26`, `diasHabilesReclamacion`). |
| **Invariantes y reglas de negocio** | Toda reclamación recibe un número de radicado único, fecha/hora de presentación y fecha límite de respuesta calculada en días hábiles según el SIEE vigente (`UC-26`) y el calendario escolar (`UC-14`), sin superar el tope máximo legal del Art. 15 de la Ley 1581 de 2012 (`RN-CAL-09`). No puede existir más de una reclamación activa simultánea sobre la misma calificación para el mismo estudiante. Toda decisión (favorable o desfavorable) exige motivación escrita obligatoria. Si la resolución en primera o segunda instancia es `FAVORABLE_RECTIFICAR`, el sistema actualiza la calificación en la misma transacción atómica (`UC-17` `A2`) conservando el valor anterior, el valor nuevo y el ID del radicado de reclamación, y recalcula los promedios de asignatura y área. Ningún periodo académico debería cerrarse definitivamente en `UC-20` mientras tenga reclamaciones vencidas o sin resolver sobre sus calificaciones. |
| **Postcondiciones** | Reclamación académica registrada y cerrada en estado `RESUELTA_FAVORABLE` o `RESUELTA_DESFAVORABLE` (en primera o segunda instancia); calificación y promedios de asignatura/área actualizados atómicamente si procedió la rectificación; notificación formal enviada al solicitante y trazabilidad completa en auditoría. |
| **Tipo** | Primario / Debido Proceso Académico y Habeas Data |
| **Prioridad** | Alta |

### Flujo principal

| Paso | Actor / responsable | Acción e interacción del sistema |
| :--- | :--- | :--- |
| 1 | Estudiante / Acudiente verificado | Desde la consulta de calificaciones (`UC-02` o `UC-08`), selecciona la calificación de actividad, nivelación o definitiva de periodo objeto de inconformidad, ingresa el motivo detallado de la reclamación (error aritmético, error de digitación, excusa justificada omitida o revisión frente a rúbrica), adjunta los soportes y confirma la radicación. |
| 2 | Sistema | Verifica la legitimación del solicitante, comprueba que la reclamación se encuentre dentro del plazo de días hábiles configurado en el SIEE (`UC-26`) y que no exista otro reclamo abierto sobre la misma nota, genera el número de radicado único en estado `RADICADA_PRIMERA_INSTANCIA` y notifica al Docente de área asignado (`UC-15`). |
| 3 | Docente de área | Revisa en su bandeja el detalle de la reclamación, los soportes adjuntos, la evidencia de la actividad o nivelación y el plazo restante para responder. |
| 4 | Docente de área | Registra su decisión motivada de primera instancia: (a) `APROBAR_RECTIFICACION` indicando el nuevo valor correcto de la calificación, o (b) `RECHAZAR_MANTENER_NOTA` explicando técnicamente y pedagógicamente por qué la calificación original se ajusta a la rúbrica y al SIEE. |
| 5 | Sistema | Si la decisión es `APROBAR_RECTIFICACION`, actualiza en una transacción atómica la calificación y recalcula los consolidados de asignatura y área (`UC-17` `A2`) vinculando el radicado del reclamo en auditoría; si es `RECHAZAR_MANTENER_NOTA`, conserva la nota original e informa el plazo para interponer apelación en segunda instancia. |
| 6 | Sistema | Notifica formalmente la resolución motivada al Estudiante y al Acudiente verificado, y registra toda la actuación en auditoría. |

### Flujos alternos y de excepción

Cada alternativa indica la condición de entrada, el tratamiento esperado y el resultado. Los flujos `A#` pueden retomar el principal cuando así se indique; los casos `E#` deben terminar de forma controlada, sin persistir cambios parciales.

| ID | Condición / paso de origen | Tratamiento / secuencia alterna | Resultado / estado final |
| :--- | :--- | :--- | :--- |
| **A1** | Apelación o escalamiento a segunda instancia ante el Coordinador Académico (pasos 5 y 6) | Si el Docente rechaza la reclamación en primera instancia y el Estudiante o Acudiente interpone recurso de apelación dentro del plazo del SIEE (o si vence el plazo de primera instancia sin respuesta del docente), el sistema cambia el estado a `ESCALADA_SEGUNDA_INSTANCIA` y la asigna a la bandeja del Coordinador Académico. El Coordinador revisa el expediente, emite el fallo definitivo motivado y, si aprueba la rectificación, el sistema actualiza atómicamente la nota y los promedios. | Garantizada la doble instancia y la resolución oportuna conforme al SIEE (Decreto 1290 Art. 4 num. 10). |
| **A2** | Rectificación por reclamación sobre un periodo cuyo boletín ya había sido emitido (paso 5) | Cuando una reclamación interpuesta dentro del plazo reglamentario posterior a la entrega de boletines se resuelve favorablemente, el Coordinador Académico autoriza la rectificación sobre el periodo, el sistema actualiza la definitiva de asignatura y área, versiona el cambio y marca el boletín del estudiante para su regeneración oficial en `UC-20` / `UC-08` (`A2`). | Resultado oficial y boletín sincronizados con trazabilidad de la versión rectificada. |
| **E1** | Solicitud de reclamación presentada fuera del plazo de días hábiles configurado en el SIEE (paso 2) | El sistema comprueba que ha vencido el término de días hábiles desde la publicación de la calificación según la versión vigente del SIEE (`UC-26`); rechaza la radicación ordinaria con `HTTP 409` e informa la fecha en que venció el plazo (salvo error aritmético manifiesto tramitado por Coordinación). | Se respetan los términos procesales definidos en el SIEE. |
| **E2** | Ya existe una reclamación activa en trámite sobre la misma calificación (paso 2) | El sistema detecta una reclamación abierta para el mismo estudiante y calificación; impide crear un radicado duplicado (`HTTP 409`) y dirige al actor a consultar el estado del radicado existente. | Se evita duplicidad de trámites sobre el mismo registro. |
| **E3** | Intento de resolver una reclamación sin motivación escrita o por un actor sin competencia (paso 4) | El sistema rechaza cualquier fallo (aprobatorio o denegatorio) que no incluya justificación escrita suficiente (`HTTP 400`) o que intente ser resuelto por un docente no asignado al curso (`HTTP 403`). | Decisiones motivadas y competencia funcional garantizadas. |

---

## UC-PROV-16 · Convalidar y homologar calificaciones de periodos previos por traslado o promoción anticipada

| Campo | Detalle |
| :--- | :--- |
| **ID** | `UC-PROV-16` (Identificador provisional — Brechas `BRC-NUEVA-02` y `BRC-NUEVA-04`) |
| **Nombre** | Convalidar y homologar calificaciones de periodos previos por traslado o promoción anticipada |
| **Actores principales** | Coordinador Académico; Rector (aprobación de actas de convalidación y de promoción anticipada conforme al SIEE) |
| **Actores secundarios / sistemas** | Secretaría (carga previa de certificados externos en matrícula por traslado `UC-13` `A5` o registro de novedad de promoción anticipada `UC-PROV-09` `A2`); Consejo Directivo (acta de aprobación en promoción anticipada); motor de cálculo SIEE (`UC-26`); servicio de Plan de Estudios (`UC-PROV-07`); servicio de auditoría |
| **Descripción** | Permite al Coordinador Académico y al Rector registrar formalmente mediante Acta de Convalidación Académica las calificaciones definitivas de los periodos académicos anteriores ya cerrados para: (1) estudiantes que ingresan por traslado desde otra institución educativa una vez iniciado el año lectivo (`UC-13` `A5`), convirtiendo u homologando sus certificados externos a las áreas y asignaturas del Plan de Estudios (`UC-PROV-07`) y a la escala vigente del SIEE (`UC-26`); y (2) estudiantes promovidos anticipadamente de grado durante el primer periodo académico (`UC-PROV-09` `A2`, Decreto 1290 Art. 7), consolidando sus notas del primer periodo en el nuevo grado. |
| **Propósito / valor** | Cerrar la dependencia crítica entre el Módulo 04 (Matrícula) y los Módulos 07 y 10 (Evaluación, Cierre y Promoción), evitando que los estudiantes trasladados a mitad de año o promovidos anticipadamente queden con periodos vacíos que distorsionen su promedio anual en `UC-20` y `UC-21`. |
| **Disparador** | La formalización de una matrícula por traslado entrante en curso (`UC-13` `A5`) o la ejecución de un ascenso por Promoción Anticipada (`UC-PROV-09` `A2`) genera una tarea pendiente de convalidación en la bandeja de Coordinación Académica. |
| **Precondiciones** | Actor autenticado con rol Coordinador Académico o Rector (`UC-01`); estudiante con matrícula activa bajo modalidad `TRASLADO_ENTRANTE` (`UC-13` `A5`) o con novedad de `PROMOCION_ANTICIPADA` registrada (`UC-PROV-09` `A2`); certificados oficiales de los periodos previos o acta del Consejo Directivo disponibles. |
| **Invariantes y reglas de negocio** | Todo periodo académico anterior a la fecha efectiva de ingreso por traslado o al ascenso por promoción anticipada debe quedar cubierto mediante un registro de consolidado con origen explícito (`CONVALIDADA_TRASLADO` o `CONVALIDADA_PROMOCION_ANTICIPADA`) antes del cierre anual del año lectivo (`RN-CAL-10`). Las calificaciones provenientes de otra institución deben convertirse a la escala numérica y de desempeño nacional (Superior, Alto, Básico, Bajo) de la versión vigente del SIEE (`UC-26`) con precisión `BigDecimal` y mapearse a las áreas y asignaturas del Plan de Estudios del grado (`UC-PROV-07`), recalculando los promedios de área al 100.00%. Si el SIEE institucional contempla la homologación por desempeño (asignar al periodo previo sin certificado la nota obtenida por el estudiante en el primer periodo cursado en el plantel), debe registrarse el fundamento del SIEE y el acta firmada. Toda convalidación exige adjuntar o referenciar el Acta de Convalidación Académica y queda trazada en auditoría. |
| **Postcondiciones** | Consolidados de asignatura y de área curricular de los periodos previos persistidos con estado `CONVALIDADA_TRASLADO` o `CONVALIDADA_PROMOCION_ANTICIPADA` y su equivalencia nacional; Acta de Convalidación Académica registrada; estudiante habilitado con historial completo de periodos para el cálculo anual en `UC-20` y `UC-21`; operación auditada. |
| **Tipo** | Primario / Convalidación Curricular y Movilidad Escolar |
| **Prioridad** | Alta |

### Flujo principal

| Paso | Actor / responsable | Acción e interacción del sistema |
| :--- | :--- | :--- |
| 1 | Coordinador Académico / Rector | Selecciona en la bandeja de convalidaciones el expediente del estudiante matriculado por traslado entrante (`UC-13` `A5`) o ascendido por promoción anticipada (`UC-PROV-09` `A2`) y elige los periodos académicos previos a convalidar. |
| 2 | Sistema | Verifica los permisos del actor, recupera la matrícula del estudiante, el Plan de Estudios vigente del grado actual (`UC-PROV-07`), la escala y reglas de convalidación de la versión vigente del SIEE (`UC-26`) y los soportes documentales adjuntados en matrícula. |
| 3 | Coordinador Académico / Rector | Para cada área y asignatura del Plan de Estudios en los periodos previos, registra la calificación equivalente en la escala institucional a partir del certificado de la institución de origen (o selecciona la regla de homologación prevista en el SIEE / en el acta del Consejo Directivo para Promoción Anticipada) e ingresa el número, fecha y observaciones del Acta de Convalidación Académica. |
| 4 | Sistema | Valida con precisión `BigDecimal` que todas las notas convalidadas estén dentro del rango de la escala vigente del SIEE, calcula automáticamente la equivalencia en la Escala Nacional (Superior, Alto, Básico, Bajo) y consolida la nota definitiva ponderada de cada Área Curricular según los pesos del Plan de Estudios (`UC-PROV-07` `A2`). |
| 5 | Coordinador Académico / Rector | Revisa el cuadro comparativo de convalidación por áreas, asignaturas y periodos, y confirma la aprobación del Acta de Convalidación Académica. |
| 6 | Sistema | Guarda en una transacción atómica los consolidados de periodo con marca de origen `CONVALIDADA_TRASLADO` o `CONVALIDADA_PROMOCION_ANTICIPADA`, vincula el Acta de Convalidación, notifica al estudiante, acudiente y Secretaría, y registra la operación en auditoría. |

### Flujos alternos y de excepción

Cada alternativa indica la condición de entrada, el tratamiento esperado y el resultado. Los flujos `A#` pueden retomar el principal cuando así se indique; los casos `E#` deben terminar de forma controlada, sin persistir cambios parciales.

| ID | Condición / paso de origen | Tratamiento / secuencia alterna | Resultado / estado final |
| :--- | :--- | :--- | :--- |
| **A1** | Homologación de periodo previo mediante el desempeño obtenido en el periodo siguiente cursado en la institución según el SIEE (paso 3) | Cuando un estudiante trasladado proviene de una institución con diferente número de periodos o llega sin notas cerradas del primer corte y el SIEE vigente autoriza homologar el periodo previo con la definitiva que el alumno obtenga en el primer periodo cursado en el colegio, el Coordinador Académico registra el compromiso de homologación en estado `PENDIENTE_DESEMPENO_PERIODO_SIGUIENTE`; una vez cerrado dicho periodo, el sistema aplica la nota homologada y genera el acta definitiva. | Flexibilidad normativa del SIEE soportada con trazabilidad completa. |
| **A2** | Convalidación de calificaciones del primer periodo por Promoción Anticipada de Grado (pasos 1 a 6) | Cuando el Consejo Directivo aprueba la Promoción Anticipada de un estudiante durante el primer periodo conforme al Art. 7 del Decreto 1290 de 2009 (`UC-21` y `UC-PROV-09` `A2`), el Coordinador Académico registra las calificaciones del primer periodo para el nuevo grado según el acta de aprobación y el plan de nivelación/adaptación cumplido. | Primer periodo del nuevo grado debidamente consolidado conforme al Decreto 1290 Art. 7 (`RN-MAT-07`, `RN-CAL-10`). |
| **E1** | Falta soporte del certificado de la institución de origen o del acta del Consejo Directivo (paso 2 o 5) | El sistema detecta que no se ha referenciado ni adjuntado el certificado oficial de notas parciales (en traslado) o el acta del Consejo Directivo (en promoción anticipada); bloquea la confirmación definitiva con `HTTP 409`. | Ninguna nota convalidada se persiste sin respaldo documental verificable. |
| **E2** | Asignaturas obligatorias del Plan de Estudios del grado quedan sin nota convalidada ni regla de homologación asignada (paso 4) | El sistema comprueba que una o más asignaturas del Plan de Estudios del grado (`UC-PROV-07`) quedaron vacías en el periodo convalidado; señala las asignaturas faltantes e impide cerrar el acta hasta completar la totalidad de la malla del grado. | Se garantiza que el promedio de todas las áreas del grado quede completo para el cierre anual. |
| **E3** | Usuario sin competencia intenta registrar o alterar convalidaciones de periodos cerrados (paso 2) | Un usuario con rol Secretaría, Docente, Estudiante o Acudiente intenta ejecutar directamente una convalidación de notas; el backend rechaza la operación con `HTTP 403 Forbidden` y registra el intento en auditoría. | Competencia exclusiva de Coordinación Académica y Rectoría protegida. |

---

## UC-PROV-17 · Consultar consolidado grupal de calificaciones, avance de digitación docente y alertas académicas

| Campo | Detalle |
| :--- | :--- |
| **ID** | `UC-PROV-17` (Identificador provisional — Brecha `BRC-NUEVA-33`) |
| **Nombre** | Consultar consolidado grupal de calificaciones, avance de digitación docente y alertas académicas |
| **Actores principales** | Docente titular/director de grupo (para el curso vigente que tiene asignado en `UC-15` `A1`); Coordinador Académico; Rector; Comisión de Evaluación y Promoción (como insumo para sus sesiones en `UC-21`) |
| **Actores secundarios / sistemas** | Servicio académico de calificaciones (`UC-16`, `UC-17`, `UC-18`); servicio de asignaciones docentes (`UC-15`); servicio de matrícula (`UC-PROV-10`); servicio de auditoría |
| **Descripción** | Permite al Director de grupo, al Coordinador Académico y al Rector consultar la sábana consolidada de calificaciones de un grupo por periodo y acumulada anual (matriz integral de todos los estudiantes del curso frente a todas las áreas y asignaturas del Plan de Estudios con sus notas, desempeños nacionales y nivelaciones), monitorear el porcentaje de avance de planeación y digitación de notas de cada docente antes del cierre de periodo (`UC-20`) e identificar alertas tempranas de estudiantes con múltiples áreas o asignaturas en Desempeño Bajo. |
| **Propósito / valor** | Habilitar el seguimiento integral del curso asignado al Director de grupo y la supervisión académica del Coordinador y del Rector previstas en la Guía General (Sección 2), suministrando el control previo indispensable para ejecutar el cierre de periodo (`UC-20`) y las Comisiones de Evaluación y Promoción (`UC-21`). |
| **Disparador** | El Director de grupo abre el consolidado de su curso asignado, o el Coordinador Académico / Rector consulta las sábanas grupales, el tablero de avance docente o el reporte de alertas académicas. |
| **Precondiciones** | Sesión válida (`UC-01`); para el Director de grupo, designación vigente sobre el curso en `UC-15` (`A1`); para Coordinador Académico y Rector, permisos de alcance institucional. |
| **Invariantes y reglas de negocio** | Se aplica autorización contextual estricta en backend (`RN-09`): el Director de grupo solo puede consultar la sábana consolidada de todas las asignaturas para el grupo específico del cual es director titular o encargado vigente en `UC-15` (`A1`); un docente que solo dicta una asignatura sin ser director de grupo únicamente ve su propia materia en `UC-17`. El Coordinador Académico y el Rector pueden consultar todos los grupos, sedes y jornadas. La consulta es estrictamente de lectura y no modifica calificaciones. |
| **Postcondiciones** | Se presenta la sábana consolidada del grupo, el estado de avance de digitación por asignatura/docente y el listado de estudiantes en alerta por Desempeño Bajo, permitiendo exportar el informe autorizado para seguimiento o insumo de comisión. |
| **Tipo** | Básico / Seguimiento Grupal y Supervisión Académica |
| **Prioridad** | Alta |

### Flujo principal

| Paso | Actor / responsable | Acción e interacción del sistema |
| :--- | :--- | :--- |
| 1 | Director de grupo / Coordinador Académico / Rector | Selecciona el año lectivo, el periodo académico, el grupo (acotado a su curso asignado si el actor es Director de grupo) y el tipo de vista: (a) Sábana consolidada de calificaciones del grupo, (b) Estado de avance de digitación docente, o (c) Alertas de riesgo académico por Desempeño Bajo. |
| 2 | Sistema | Verifica en backend el rol activo y comprueba que el Director de grupo posea asignación vigente sobre el curso en `UC-15` (`A1`) o que el actor sea Coordinador Académico / Rector. |
| 3 | Sistema | Recupera la lista oficial de estudiantes matriculados en el grupo (`UC-PROV-10`), el Plan de Estudios del grado (`UC-PROV-07`), los consolidados de todas las áreas y asignaturas (`UC-17`), el estado de las nivelaciones (`UC-18`) y las reclamaciones abiertas (`UC-PROV-15`). |
| 4 | Sistema | Presenta la matriz consolidada del grupo con la nota y desempeño nacional de cada estudiante en cada área y asignatura, el promedio general, el conteo de áreas/asignaturas en Desempeño Bajo por estudiante y los indicadores de completitud docente. |

### Flujos alternos y de excepción

Cada alternativa indica la condición de entrada, el tratamiento esperado y el resultado. Los flujos `A#` pueden retomar el principal cuando así se indique; los casos `E#` deben terminar de forma controlada, sin persistir cambios parciales.

| ID | Condición / paso de origen | Tratamiento / secuencia alterna | Resultado / estado final |
| :--- | :--- | :--- | :--- |
| **A1** | Monitoreo de avance de planeación y digitación docente previo al cierre de periodo (pasos 1 a 4) | El Coordinador Académico o Rector (o el Director de grupo para su curso) consulta el tablero de completitud del periodo; el sistema muestra por cada grupo y asignatura el docente responsable (`UC-15`), el porcentaje de ponderaciones configuradas en `UC-16` (alertando si es menor a 100.00%), la cantidad de estudiantes con notas pendientes de captura o con excusa justificada pendiente en `UC-17` y las reclamaciones sin resolver en `UC-PROV-15`. | Control preventivo exacto de pendientes antes de ejecutar el cierre de periodo en `UC-20`. |
| **A2** | Identificación de alertas tempranas de pérdida académica y preparación de citación a acudientes o Comisión de Evaluación (pasos 1 a 4) | El sistema filtra y ordena a los estudiantes del grupo que registran una o más áreas/asignaturas en Desempeño Bajo (o que son repitentes en riesgo), mostrando si ya fueron convocados a nivelación en `UC-18` para facilitar la citación oportuna a los acudientes y generar el insumo para la Comisión de Evaluación y Promoción (`UC-21`). | Detección temprana de mortalidad académica y acompañamiento oportuno al estudiante. |
| **E1** | Docente intenta consultar la sábana integral de un grupo del cual no es Director de Grupo vigente (paso 2) | El backend detecta que el usuario con rol docente no posee designación activa como Director de Grupo sobre el curso solicitado en `UC-15` (`A1`); deniega el acceso a las calificaciones de las demás asignaturas con `HTTP 403 Forbidden` y registra el intento en auditoría. | Se protege el principio de mínimo privilegio y alcance contextual entre docentes. |
| **E2** | Grupo sin estudiantes matriculados o sin actividades registradas en el periodo (paso 3) | El sistema informa claramente que el grupo o periodo consultado aún no registra calificaciones consolidadas y muestra la estructura base de asignaturas y docentes asignados. | Respuesta informativa controlada. |