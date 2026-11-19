# CASOS DE USO — SISTEMA DE GESTIÓN ESCOLAR

## Módulo 09 — Horarios

**Configuración de aulas y franjas, estructuración, validación de cruces, publicación, novedades y consultas de horarios**

- **Orden recomendado:** 09 de 12
- **Casos de uso incluidos:** `UC-04`, `UC-10`, `UC-PROV-20`, `UC-PROV-21`, `UC-PROV-22`
- **Referencia de desarrollo:** Fase 5 — módulo académico
- **Dependencias:** Estudiante y acudientes con verificación de restricciones legales (`UC-06`, `UC-PROV-05`); matrícula y novedades de grupo (`UC-13`, `UC-PROV-09`); año lectivo, calendario de días lectivos, grupos con cupos y plan de estudios con IHS (`UC-14`, `UC-PROV-06`, `UC-PROV-07`); asignaciones docentes y suplencias (`UC-15`).

> `UC-04` y `UC-10` cubren la consulta de horarios publicados para el estudiante y su acudiente validado. Los casos provisionales `UC-PROV-20`, `UC-PROV-21` y `UC-PROV-22` (con identificador provisional hasta su aprobación en el catálogo definitivo) cierran la brecha de creación, administración de aulas y franjas horarias, validación integral de conflictos (profesor, grupo y aula), cumplimiento de Intensidad Horaria Semanal (IHS), publicación versionada, gestión de cambios/permutas y consulta de horarios por docentes, dirección de grupo, coordinación, secretaría y rectoría.
>
> *Fragmentación del documento fuente · versión actualizada tras auditoría de Fase 9*

---

## UC-04 · Consultar horario académico propio

| Campo | Detalle |
| :--- | :--- |
| **ID** | `UC-04` |
| **Nombre** | Consultar horario académico propio |
| **Actores principales** | Estudiante (cuyo nivel educativo tenga habilitado el acceso directo conforme al parámetro institucional `RN-SEG-09` de `UC-01` y `UC-PROV-13`) |
| **Actores secundarios / sistemas** | Servicio de horarios (`UC-PROV-20`, `UC-PROV-21`); servicio de matrícula (`UC-13`, `UC-PROV-09`); servicio de estructura y calendario escolar (`UC-14`, `UC-PROV-07`); servicio de asignaciones docentes (`UC-15`); servicio de auditoría |
| **Descripción** | Permite al estudiante autenticado consultar exclusivamente la versión publicada y vigente (o histórica según el rango de fechas) del horario académico del grupo en que se encuentra matriculado (`UC-13`, `UC-PROV-09`), visualizando por día de la semana las franjas lectivas y descansos pedagógicos, la asignatura o dimensión del desarrollo, el nombre del docente activo (titular o suplente en reemplazo según `UC-15` `A3`), el aula o espacio físico asignado, los cambios temporales publicados (`UC-PROV-21`) y las alertas de días no lectivos del calendario escolar (`UC-14` `A4`). |
| **Propósito / valor** | Facilitar la asistencia puntual a clases y la organización escolar del estudiante con información oficial, actualizada y libre de conflictos. |
| **Disparador** | El estudiante autenticado selecciona la opción Horario y elige la fecha, semana o periodo a consultar. |
| **Precondiciones** | Sesión válida con rol activo Estudiante (`UC-01`, incluyendo modo histórico `UC-01` `A3`); matrícula activa o historial de matrícula asociado en el año lectivo consultado (`UC-13`, `UC-PROV-10`); versión de horario en estado `PUBLICADO_VIGENTE` (o `HISTORICO` para fechas pasadas) en el grupo correspondiente (`UC-PROV-20`). |
| **Invariantes y reglas de negocio** | El grupo, sede y jornada del estudiante se determinan exclusivamente en el servidor a partir de su matrícula vigente para la fecha consultada (`UC-13`, `UC-PROV-09`), ignorando o rechazando cualquier identificador de grupo arbitrario enviado por el cliente (`RN-09`, `RN-HOR-07`). Nunca se exponen horarios en estado `BORRADOR` ni cargas internas o datos privados de los docentes (`RN-HOR-06`). Cuando el estudiante ha cambiado de grupo durante el año lectivo, se respeta la vigencia temporal de cada grupo. |
| **Postcondiciones** | Se presenta la grilla del horario por día, franja (bloques lectivos y descansos), asignatura/dimensión, docente vigente, aula/ubicación y novedades publicadas para la fecha seleccionada, sin alterar registros. |
| **Tipo** | Básico |
| **Prioridad** | Media |

### Flujo principal

| Paso | Actor / responsable | Acción e interacción del sistema |
| :--- | :--- | :--- |
| 1 | Estudiante | Solicita consultar su horario académico y selecciona la fecha, semana o periodo de interés. |
| 2 | Sistema | Identifica en backend la identidad del estudiante desde la sesión segura (`UC-01`) y determina su grupo y vigencia mediante su matrícula en la fecha seleccionada (`UC-13`, `UC-PROV-09`), sin confiar en un grupo enviado por el navegador. |
| 3 | Sistema | Recupera la versión del horario en estado `PUBLICADO_VIGENTE` (o `HISTORICO` aplicable a la fecha) para ese grupo (`UC-PROV-20`), junto con los cambios temporales o suplencias docentes activas (`UC-PROV-21`, `UC-15` `A3`) y los días no lectivos de la semana (`UC-14` `A4`). |
| 4 | Sistema | Muestra la grilla semanal o diaria con las franjas horarias (hora de inicio y fin), descansos pedagógicos, asignaturas o dimensiones, docente vigente, aula/laboratorio y novedades publicadas. |
| 5 | Estudiante | Consulta o exporta su horario o selecciona otra fecha o periodo autorizado. |

### Flujos alternos y de excepción

Cada alternativa indica la condición de entrada, el tratamiento esperado y el resultado. Los flujos `A#` pueden retomar el principal cuando así se indique; los casos `E#` deben terminar de forma controlada, sin persistir cambios parciales.

| ID | Condición / paso de origen | Tratamiento / secuencia alterna | Resultado / estado final |
| :--- | :--- | :--- | :--- |
| **A1** | Consulta histórica o cambio de grupo durante el año lectivo (pasos 2 y 3) [amplía A1 original] | Si el estudiante no tiene matrícula activa en el día actual pero consulta un periodo histórico autorizado, o si cambió de grupo interno o ascendió por promoción anticipada durante el año (`UC-PROV-09` `A1`/`A2`), el sistema selecciona el horario del grupo que estuvo vigente para el estudiante en la fecha consultada sin confundirlo con el actual. | Se presenta el horario exacto según la vigencia histórica de la matrícula. |
| **A2** | Horario del grupo aún en preparación o pendiente de publicación (paso 3) [antes A2] | Si el horario del grupo se encuentra en estado `BORRADOR` en `UC-PROV-20`, el sistema muestra el estado «Pendiente de publicación oficial» y evita presentar versiones preliminares no aprobadas. | No se comunica programación en borrador (`RN-HOR-06`). |
| **A3** | Visualización de novedad temporal de aula, jornada especial o docente suplente en la semana consultada (pasos 3 y 4) | Cuando en la semana consultada rige una reasignación temporal de aula, un ajuste de jornada especial (`UC-PROV-21`) o un reemplazo temporal por licencia del docente titular (`UC-15` `A3`), el sistema destaca la novedad publicada y muestra el aula o docente suplente vigente para esa fecha. | Información operativa diaria actualizada para el estudiante. |
| **E1** | Intento de consultar horario de otro estudiante o de un grupo ajeno manipulando parámetros (paso 2) [antes A4] | El backend detecta que la petición intenta forzar la consulta de un estudiante o grupo distinto al asignado en la matrícula del usuario en sesión; deniega el acceso con `HTTP 403 Forbidden` y registra el intento en auditoría. | No hay exposición de horarios ni ubicación de terceros (`RN-HOR-07`). |
| **E2** | Horario con inconsistencia técnica detectada (paso 3) [antes A3] | Si el sistema detecta una inconsistencia en los datos persistidos del horario, muestra mensaje controlado de indisponibilidad por validación académica y registra incidencia para Coordinación Académica. | No se oculta silenciosamente el conflicto ni se muestran datos corruptos. |
| **E3** | Sin matrícula asociada para el año lectivo consultado (paso 2) | El sistema comprueba que el estudiante no registra matrícula en el año seleccionado e informa que no aplica un horario para ese filtro. | No se asigna ni muestra un grupo inexistente. |

---

## UC-10 · Consultar horario académico del estudiante

| Campo | Detalle |
| :--- | :--- |
| **ID** | `UC-10` |
| **Nombre** | Consultar horario académico del estudiante |
| **Actores principales** | Acudiente / adulto responsable validado |
| **Actores secundarios / sistemas** | Servicio de relaciones acudiente-estudiante (`UC-06`, `UC-PROV-05`); servicio de horarios (`UC-PROV-20`, `UC-PROV-21`); servicio de matrícula (`UC-13`, `UC-PROV-09`); servicio de calendario escolar (`UC-14`); servicio de auditoría |
| **Descripción** | Permite al acudiente autenticado consultar el horario semanal vigente o histórico publicado del estudiante con el cual mantiene un vínculo activo, verificado y libre de restricciones legales (`UC-06`, `UC-PROV-05`), incluyendo horas de inicio y fin de jornada, franjas de clase, descansos, asignaturas/dimensiones, docentes vigentes, aulas y cambios temporales o días no lectivos publicados. |
| **Propósito / valor** | Acompañar la organización escolar y el control seguro de los horarios de entrada y salida desde el hogar (Ley 1098 de 2006 Art. 39), protegiendo estrictamente la ubicación física del menor frente a personas no autorizadas o con medidas de protección. |
| **Disparador** | El acudiente selecciona un estudiante vinculado (desde `UC-06`) y elige la opción Horario. |
| **Precondiciones** | Sesión válida con rol activo Acudiente (`UC-01`); vínculo estudiante-acudiente en estado `ACTIVO` y `VERIFICADO` y sin medida de restricción legal (`UC-12`, `UC-PROV-05`); horario correspondiente publicado (`UC-PROV-20`). |
| **Invariantes y reglas de negocio** | El backend revalida en cada consulta la vigencia, verificación y ausencia absoluta de estado `BLOQUEADO_RESTRICCION_LEGAL` en el vínculo acudiente-estudiante (`RN-09`, `RN-EST-02`, `RN-HOR-07`). El horario deriva exclusivamente de la matrícula y del grupo vigente del estudiante para el rango de fechas solicitado (`UC-13`, `UC-PROV-09`); nunca se exponen borradores no publicados ni información laboral interna de los docentes. |
| **Postcondiciones** | El horario del estudiante vinculado se muestra en estado vigente o histórico con sus franjas, asignaturas, docentes, aulas y novedades publicadas, sin modificar datos. |
| **Tipo** | Básico |
| **Prioridad** | Media |

### Flujo principal

| Paso | Actor / responsable | Acción e interacción del sistema |
| :--- | :--- | :--- |
| 1 | Acudiente | Elige al estudiante dentro de sus estudiantes vinculados (`UC-06`) y selecciona la fecha, semana o periodo a consultar. |
| 2 | Sistema | Valida en backend la vigencia y verificación de la relación acudiente-estudiante, comprueba la ausencia de medida de protección `BLOQUEADO_RESTRICCION_LEGAL` (`UC-PROV-05`) y determina la matrícula y el grupo del estudiante para la fecha solicitada. |
| 3 | Sistema | Recupera la versión publicada del horario del grupo (`UC-PROV-20`), las novedades temporales o suplencias activas (`UC-PROV-21`, `UC-15` `A3`) y los días no lectivos del calendario escolar (`UC-14` `A4`). |
| 4 | Sistema | Muestra las franjas horarias (hora de ingreso, bloques lectivos, descansos y hora de salida), asignaturas/dimensiones, docente vigente y aula/ubicación publicada. |
| 5 | Acudiente | Consulta o descarga el horario del estudiante. |

### Flujos alternos y de excepción

Cada alternativa indica la condición de entrada, el tratamiento esperado y el resultado. Los flujos `A#` pueden retomar el principal cuando así se indique; los casos `E#` deben terminar de forma controlada, sin persistir cambios parciales.

| ID | Condición / paso de origen | Tratamiento / secuencia alterna | Resultado / estado final |
| :--- | :--- | :--- | :--- |
| **A1** | No hay horario publicado para el grupo en el periodo (paso 3) [antes A1] | El sistema informa que el horario del grupo aún no ha sido publicado oficialmente por Coordinación Académica y evita presentar borradores preliminares. | No se presenta borrador no aprobado (`RN-HOR-06`). |
| **A2** | Cambio de grupo, promoción anticipada o traslado durante el año lectivo (pasos 2 y 3) [antes A2] | Cuando el estudiante cambió de grupo interno o fue promovido anticipadamente durante el año (`UC-PROV-09` `A1`/`A2`), el sistema selecciona la versión aplicable a la fecha consultada y separa con claridad el horario del grupo anterior y el del grupo vigente. | El historial de grupos no se confunde con el horario actual. |
| **A3** | Visualización de cambios temporales de horario/aula o días no lectivos en la semana (pasos 3 y 4) | El sistema destaca en la grilla semanal los cambios puntuales de aula o jornada publicados en `UC-PROV-21`, el docente suplente activo (`UC-15` `A3`) y los días festivos, recesos o jornadas pedagógicas sin clase registrados en `UC-14` (`A4`). | La familia conoce con exactitud las novedades horarias y días sin asistencia. |
| **E1** | Vínculo no vigente, revocado, en disputa o con medida de protección `BLOQUEADO_RESTRICCION_LEGAL` (paso 2) [amplía A3 original] | El backend comprueba que el adulto no tiene vínculo activo y verificado con el estudiante o que registra una restricción legal de acercamiento/acceso en `UC-PROV-05` (`A3`); deniega de inmediato la consulta con `HTTP 403 Forbidden` sin revelar el grupo, aula, sede ni hora de salida del menor y registra alerta de seguridad en auditoría. | Datos y ubicación física del menor estrictamente protegidos (Ley 1098 de 2006, `RN-HOR-07`). |
| **E2** | Conflicto o inconsistencia detectada en la programación almacenada (paso 3) [antes A4] | El sistema informa que el dato horario requiere validación académica por Coordinación y genera incidencia técnica sin exponer una programación contradictoria como definitiva. | No se presenta un horario inconsistente como definitivo. |
| **E3** | Consulta de año lectivo no cursado en cuenta con acceso histórico restringido (paso 2) | Si el acudiente opera con cuenta en estado `INACTIVA_HISTORICA` (`UC-01` `A3`) e intenta consultar un año posterior al retiro o graduación del estudiante, el sistema rechaza la petición e indica los años históricos disponibles. | Alcance histórico restringido respetado. |

---

## UC-PROV-20 · Configurar aulas y franjas horarias y estructurar, validar y publicar horarios académicos

| Campo | Detalle |
| :--- | :--- |
| **ID** | `UC-PROV-20` (Identificador provisional — Brechas `BRC-ORIG-03A` y `BRC-NUEVA-36`) |
| **Nombre** | Configurar aulas y franjas horarias y estructurar, validar y publicar horarios académicos |
| **Actores principales** | Coordinador Académico; Rector (aprobación oficial de la jornada y publicación de horarios conforme a la Ley 715 de 2001 Art. 10.9 y Decreto 1850 de 2002 Art. 5) |
| **Actores secundarios / sistemas** | Administrador / Secretaría (apoyo en el registro del catálogo físico de aulas y espacios por sede); servicio de estructura académica, grupos y Plan de Estudios (`UC-14`, `UC-PROV-06`, `UC-PROV-07`); servicio de asignaciones docentes (`UC-15`); servicio de asistencia (`UC-19`); servicio de notificaciones; auditoría |
| **Descripción** | Permite configurar el catálogo de aulas y espacios físicos por sede (capacidad, tipo de espacio y disponibilidad) y las plantillas de franjas horarias por jornada y nivel educativo (bloques lectivos y descansos pedagógicos conforme al Decreto 1850 de 2002), estructurar en borrador el horario semanal de cada grupo vinculando las asignaciones docentes vigentes (`UC-15`), ejecutar el validador integral de restricciones (cumplimiento exacto de la Intensidad Horaria Semanal IHS del Plan de Estudios `UC-PROV-07` y prevención de colisiones simultáneas de grupo, docente y aula), y publicar de forma versionada el horario oficial. |
| **Propósito / valor** | Cerrar la brecha estructural de administración de horarios (`BRC-ORIG-03`), garantizando el cumplimiento de las intensidades horarias legales (Ley 115 de 1994 y Decreto 1075 de 2015), la ausencia de cruces entre profesor, grupo y aula (Documento Maestro V2.0 Secciones 4, 7 y 8) y la provisión de sesiones programadas válidas para el módulo de Asistencia (`UC-19`, `RN-04`). |
| **Disparador** | El Coordinador Académico o el Rector inicia la configuración de espacios físicos y grillas de franjas horarias o estructura y publica los horarios de los grupos para un año lectivo o periodo. |
| **Precondiciones** | Actor autenticado con rol Coordinador Académico o Rector (`UC-01`); año lectivo en estado `EN_PLANEACION` o `ACTIVO` (`UC-14`), grupos con `cupoMaximo` configurados (`UC-PROV-06`), Plan de Estudios del grado vigente con la IHS de cada asignatura/dimensión (`UC-PROV-07`) y asignaciones docentes registradas (`UC-15`). |
| **Invariantes y reglas de negocio** | Las plantillas de jornada definen franjas sin solapamiento interno diferenciando bloques lectivos de descansos pedagógicos (`RN-HOR-04`). Toda celda lectiva programada en el horario de un grupo debe corresponder a la asignación docente activa (`UC-15`) para ese `(Grupo, Asignatura/Dimensión)` (`RN-HOR-03`). Rige la invariante de Triple No-Colisión en backend y base de datos (`RN-HOR-01`): en un mismo día de la semana y franja horaria (o intervalos de minutos solapados) nunca puede asignarse (1) dos clases al mismo Grupo, (2) dos grupos distintos al mismo Docente, ni (3) dos grupos distintos a la misma Aula o espacio físico (salvo espacios abiertos expresamente configurados como multi-grupo), verificando además que la capacidad del aula sea compatible con el grupo (`RN-HOR-05`). Para publicar el horario de un grupo como `PUBLICADO_VIGENTE`, el total de bloques semanales programados por cada asignatura debe coincidir exactamente con su Intensidad Horaria Semanal (IHS) definida en el Plan de Estudios del grado (`RN-HOR-02`, `UC-PROV-07`). |
| **Postcondiciones** | Catálogo de aulas y plantillas de franjas horarias actualizados; horario del grupo persistido en estado `BORRADOR` o publicado en versión inmutable `PUBLICADO_VIGENTE` con fecha de inicio de vigencia; sesiones programadas válidas habilitadas para el módulo de Asistencia (`UC-19`); notificaciones de publicación emitidas y operación registrada en auditoría. |
| **Tipo** | Primario / Planificación Académica y Gestión de Espacios |
| **Prioridad** | Alta |

### Flujo principal

| Paso | Actor / responsable | Acción e interacción del sistema |
| :--- | :--- | :--- |
| 1 | Coordinador Académico / Rector | Selecciona el año lectivo (`EN_PLANEACION` o `ACTIVO`), la sede y la jornada, y configura o verifica el catálogo de Aulas/Espacios Físicos (código, tipo de aula/laboratorio, capacidad y estado) y la Plantilla de Franjas Horarias de la jornada (hora de inicio, hora de fin y tipo de franja: bloque lectivo, descanso pedagógico, dirección de grupo). |
| 2 | Sistema | Valida que las franjas de la plantilla no se solapen entre sí, que cubran las horas semanales exigidas para el nivel educativo (Decreto 1850 de 2002) y que las aulas pertenezcan a la sede y estén disponibles. |
| 3 | Coordinador Académico / Rector | Selecciona un grupo activo (`UC-PROV-06`) para crear o editar su horario en estado `BORRADOR` y asigna en la grilla semanal (Día de la semana x Franja lectiva) las asignaturas o dimensiones del Plan de Estudios con su docente asignado en `UC-15` y el aula o espacio físico correspondiente. |
| 4 | Sistema | Verifica en cada asignación y al validar el horario completo: (a) ausencia total de cruces de Grupo, de Docente y de Aula en la misma franja o minutos solapados (`RN-HOR-01`), (b) correspondencia exacta entre los bloques asignados a cada materia y su IHS en el Plan de Estudios (`RN-HOR-02`, `UC-PROV-07`), y (c) compatibilidad de la capacidad del aula con el cupo/matrículas del grupo (`RN-HOR-05`). |
| 5 | Coordinador Académico / Rector | Revisa el reporte de validación sin conflictos ni faltantes de IHS, define la fecha de entrada en vigencia y confirma la publicación oficial del horario (o lo guarda como `BORRADOR` si aún está en construcción). |
| 6 | Sistema | Ejecuta la publicación en una transacción atómica con control de concurrencia: cambia la versión del horario a estado `PUBLICADO_VIGENTE`, habilita la programación de sesiones válidas para Asistencia (`UC-19`), publica la vista para estudiantes (`UC-04`), acudientes (`UC-10`) y docentes (`UC-PROV-22`), y registra la operación en auditoría. |

### Flujos alternos y de excepción

Cada alternativa indica la condición de entrada, el tratamiento esperado y el resultado. Los flujos `A#` pueden retomar el principal cuando así se indique; los casos `E#` deben terminar de forma controlada, sin persistir cambios parciales.

| ID | Condición / paso de origen | Tratamiento / secuencia alterna | Resultado / estado final |
| :--- | :--- | :--- | :--- |
| **A1** | Clonación o proyección de estructura de horarios y plantillas de franjas desde el periodo o año lectivo anterior (pasos 1 y 3) | Al preparar un año lectivo en estado `EN_PLANEACION` o un nuevo periodo, el Coordinador Académico selecciona tomar como base las plantillas de franjas, el catálogo de aulas y la distribución base de bloques del año/periodo anterior; el sistema genera una copia en estado `BORRADOR` vinculada a las nuevas asignaciones de `UC-15` para agilizar los ajustes antes de validar y publicar. | Armado de horarios acelerado sin afectar la versión vigente del año en curso. |
| **A2** | Guardado parcial en estado `BORRADOR` durante la construcción de la matriz de horarios (paso 5) | Cuando el Coordinador Académico aún no ha terminado de ubicar todas las horas de IHS del grupo, solicita Guardar borrador; el sistema persiste el avance en estado `BORRADOR` indicando qué asignaturas tienen horas pendientes por ubicar, sin exponer el borrador en `UC-04` ni `UC-10` ni generar sesiones de asistencia en `UC-19`. | Trabajo en curso conservado de forma segura sin publicar horarios incompletos. |
| **A3** | Asignación de aulas especializadas rotativas (Laboratorios, Salas de Sistemas, Escenarios Deportivos) (paso 3 y 4) | Para asignaturas que requieren un espacio especializado en determinados bloques (por ejemplo, Química en Laboratorio o Informática en Sala de Sistemas) mientras el resto de clases se dictan en el aula base del grupo (`UC-PROV-06`), el actor selecciona el aula especializada para esa franja específica y el sistema verifica en tiempo real su disponibilidad sin colisión con otros grupos. | Uso compartido de laboratorios y espacios especiales controlado sin cruces. |
| **E1** | Detección de choque de horario de Docente, Grupo o Aula/Espacio físico (paso 4) | El sistema detecta que en el mismo día y franja (o en horas solapadas entre jornadas/sedes) el docente ya tiene clase asignada en otro grupo, el aula ya está ocupada por otro curso o el grupo tiene otra materia; rechaza la publicación con `HTTP 409` mostrando exactamente el docente, grupo, aula y franja en conflicto. | Se impide estrictamente guardar en firme o publicar cualquier cruce de horario (`RN-HOR-01`). |
| **E2** | Incumplimiento de la Intensidad Horaria Semanal (IHS) del Plan de Estudios al intentar publicar (paso 4) | Al solicitar publicar el horario como `PUBLICADO_VIGENTE`, el sistema comprueba que una o más asignaturas/dimensiones del grupo tienen programada una cantidad de bloques semanales menor o mayor que su IHS definida en `UC-PROV-07`; bloquea la publicación con `HTTP 409` y detalla las horas faltantes o sobrantes por asignatura. | Todo horario publicado cumple al 100% la intensidad horaria del Plan de Estudios (`RN-HOR-02`). |
| **E3** | Capacidad del aula insuficiente para el grupo o aula en estado `EN_MANTENIMIENTO` / `INACTIVA` (paso 4) | El sistema detecta que el aula seleccionada se encuentra en mantenimiento/inactiva o que su capacidad física es inferior a los estudiantes matriculados en el grupo (`UC-PROV-06`); bloquea la asignación del espacio con `HTTP 409` y sugiere las aulas disponibles con capacidad suficiente. | Prevención de hacinamiento o asignación de salones inhabilitados (`RN-HOR-05`). |
| **E4** | Asignatura sin docente asignado en `UC-15` o docente inactivo/suspendido (paso 3 o 4) | El actor intenta programar en una franja una asignatura que aún no tiene docente titular/suplente asignado para ese grupo en `UC-15`; el sistema rechaza la operación con `HTTP 409` e indica que primero debe completarse la asignación de carga en el Módulo 05. | Coherencia obligatoria entre asignaciones docentes y horarios (`RN-HOR-03`). |

---

## UC-PROV-21 · Gestionar novedades, cambios temporales o nuevas versiones de horario y reasignación de aulas

| Campo | Detalle |
| :--- | :--- |
| **ID** | `UC-PROV-21` (Identificador provisional — Brechas `BRC-ORIG-03B` y `BRC-NUEVA-37`) |
| **Nombre** | Gestionar novedades, cambios temporales o nuevas versiones de horario y reasignación de aulas |
| **Actores principales** | Coordinador Académico; Rector (aprobación de reestructuraciones permanentes o jornadas especiales); Docente de área (como solicitante de permuta puntual de bloque o reserva temporal de aula especial) |
| **Actores secundarios / sistemas** | Servicio de horarios y validador de colisiones (`UC-PROV-20`); servicio de asignaciones y relevos docentes (`UC-15`); servicio de asistencia (`UC-19`); servicio de notificaciones; auditoría |
| **Descripción** | Permite gestionar de manera controlada, libre de colisiones y auditable las modificaciones sobre horarios ya publicados durante el año lectivo: (1) reasignación temporal o definitiva de aula/espacio físico por mantenimiento o necesidad pedagógica; (2) permuta puntual de bloques entre docentes de un mismo grupo o ajuste por jornada institucional especial; (3) sincronización automática del docente responsable en el horario cuando se registra una suplencia o reemplazo en `UC-15` (`A3`); y (4) generación y publicación de una nueva versión vigente del horario cuando cambia la distribución permanente de clases, preservando intacta la versión histórica vinculada a las asistencias ya registradas en `UC-19`. |
| **Propósito / valor** | Permitir que el colegio adapte sus horarios y aulas a las contingencias reales del año escolar sin generar choques horarios ni destruir la trazabilidad de las sesiones de asistencia causadas en fechas anteriores (`RN-04`, `RN-HOR-06`). |
| **Disparador** | Coordinación Académica o Rectoría requiere cambiar un aula, autorizar una permuta temporal, aplicar un horario especial o publicar una nueva versión del horario de un grupo, o el Módulo 05 (`UC-15` `A3`) notifica un relevo docente. |
| **Precondiciones** | Actor autenticado y autorizado (`UC-01`); existencia de un horario en estado `PUBLICADO_VIGENTE` para el grupo afectado (`UC-PROV-20`); año lectivo y periodo académico abiertos (`UC-14`). |
| **Invariantes y reglas de negocio** | Todo cambio temporal o nueva versión de horario debe pasar por el validador de Triple No-Colisión (Grupo, Docente y Aula, `RN-HOR-01`). Ninguna modificación de horario puede alterar ni eliminar retroactivamente los bloques o sesiones sobre los cuales ya se registró asistencia en `UC-19` (`RN-HOR-06`): los cambios permanentes generan una nueva versión numerada con `fechaInicioVigencia` igual o posterior a la fecha actual y cierran la vigencia de la versión previa como `HISTORICA`. Los cambios temporales de aula o permutas acotadas tienen `fechaInicioAplicacion` y `fechaFinAplicacion` explícitas y no alteran la IHS semanal del Plan de Estudios (`RN-HOR-02`). |
| **Postcondiciones** | Novedad temporal registrada o nueva versión del horario publicada como `PUBLICADO_VIGENTE` (pasando la versión anterior a `HISTORICA` con fecha de corte); sesiones futuras de asistencia (`UC-19`) actualizadas conforme a la nueva vigencia; notificaciones enviadas a los docentes, estudiantes y acudientes del grupo; cambio registrado en auditoría con valor anterior, valor nuevo y motivo. |
| **Tipo** | Primario / Gestión Operativa de Cambios de Horario |
| **Prioridad** | Alta |

### Flujo principal

| Paso | Actor / responsable | Acción e interacción del sistema |
| :--- | :--- | :--- |
| 1 | Coordinador Académico / Rector | Selecciona el horario publicado del grupo o el aula afectada y elige el tipo de operación: (a) Reasignación temporal o definitiva de aula, (b) Permuta puntual de bloques entre docentes del grupo, (c) Ajuste temporal por jornada especial, o (d) Creación de nueva versión por cambio permanente de distribución horaria. |
| 2 | Sistema | Muestra la programación vigente del grupo, los docentes y aulas involucrados, y verifica si existen registros de asistencia ya tomados en `UC-19` para las fechas propuestas. |
| 3 | Coordinador Académico / Rector | Ingresa el detalle del cambio (nueva aula, bloques a permutar o nueva distribución de franjas), define la fecha o rango de fechas `[fechaInicioAplicacion, fechaFinAplicacion]` en que regirá y registra la justificación obligatoria. |
| 4 | Sistema | Ejecuta el validador de restricciones (`RN-HOR-01`, `RN-HOR-02`, `RN-HOR-05`): comprueba que el cambio no provoque ningún choque de docente, grupo ni aula en las fechas seleccionadas y que se mantenga intacta la IHS del Plan de Estudios. |
| 5 | Sistema | Guarda el cambio en una transacción atómica: si es una novedad temporal, registra la `NovedadHorarioSesion` acotada a sus fechas; si es una reestructuración permanente, cierra la versión anterior como `HISTORICA` en la fecha de corte y publica la nueva versión como `PUBLICADO_VIGENTE` desde la fecha efectiva sin alterar las asistencias históricas de `UC-19`. |
| 6 | Sistema | Registra la operación en auditoría (actor, valores anteriores, valores nuevos, fechas de vigencia y motivo) y emite notificación de cambio de horario/aula a los docentes, estudiantes y acudientes del grupo afectado. |

### Flujos alternos y de excepción

Cada alternativa indica la condición de entrada, el tratamiento esperado y el resultado. Los flujos `A#` pueden retomar el principal cuando así se indique; los casos `E#` deben terminar de forma controlada, sin persistir cambios parciales.

| ID | Condición / paso de origen | Tratamiento / secuencia alterna | Resultado / estado final |
| :--- | :--- | :--- | :--- |
| **A1** | Reasignación masiva de aulas por cierre temporal o mantenimiento de un salón o laboratorio (pasos 1 a 6) | Cuando un aula o laboratorio pasa a estado `EN_MANTENIMIENTO` durante un rango de fechas, el Coordinador Académico selecciona Reubicar clases del aula afectada; el sistema lista todos los bloques y grupos que ocupaban ese espacio, sugiere las aulas disponibles con capacidad suficiente en cada franja y, al confirmarse, reasigna los bloques en lote sin cruces. | Contingencia de infraestructura física resuelta ágilmente y notificada a los cursos. |
| **A2** | Sincronización automática de horario por relevo o suplencia docente registrada en `UC-15` (`A3`) (pasos 2 a 6) | Cuando en el Módulo 05 (`UC-15` `A3`) se registra un reemplazo temporal por licencia/incapacidad o una sustitución definitiva de un docente, el servicio de horarios vincula automáticamente al docente suplente sobre los bloques horarios de esa asignación durante el rango de fechas del encargo, validando ausencia de choques horarios y actualizando la agenda del docente suplente (`UC-PROV-22`) y la vista del grupo (`UC-04`, `UC-10`). | Continuidad horaria automática entre asignaciones docentes y horarios sin redigitar la grilla del grupo. |
| **A3** | Solicitud de permuta puntual de bloques o reserva de espacio especial iniciada por un Docente (pasos 1 a 6) | Un docente asignado al grupo solicita intercambiar puntualmente un bloque de clase de una semana específica con otro docente del mismo curso (con aceptación de ambos) o reservar un laboratorio/auditorio disponible; el Coordinador Académico revisa la solicitud en su bandeja, el sistema verifica cero colisiones y, al aprobarla, publica la novedad temporal. | Flexibilidad pedagógica controlada con autorización de Coordinación y cero choques. |
| **E1** | El cambio de aula, permuta o nueva versión genera un choque horario con otro grupo, docente o espacio (paso 4) | El sistema detecta que el docente o la nueva aula ya están ocupados en la franja de destino; rechaza la modificación con `HTTP 409` mostrando el detalle del conflicto. | Ningún cambio o permuta puede introducir colisiones en la programación publicada. |
| **E2** | Intento de modificar o eliminar retroactivamente bloques de fechas pasadas que ya cuentan con asistencia registrada en `UC-19` (paso 2 o 4) | El actor intenta aplicar un cambio de horario con fecha de vigencia retroactiva sobre días en los que ya se tomó asistencia en `UC-19`; el sistema bloquea la alteración retroactiva con `HTTP 409` y exige fijar la `fechaInicioVigencia` desde el día actual o futuro mediante nueva versión. | Se protege la inmutabilidad histórica de las sesiones de asistencia ya causadas (`RN-HOR-06`). |
| **E3** | Usuario sin permiso intenta alterar directamente un horario publicado (paso 1) | Un usuario con rol Estudiante, Acudiente, Secretaría o Docente (por fuera de una solicitud formal de `A3`) intenta modificar un horario publicado; el backend deniega la operación con `HTTP 403 Forbidden` y audita el intento. | Gobierno del horario protegido por control de acceso vertical. |

---

## UC-PROV-22 · Consultar horario docente propio, disponibilidad de aulas y matriz general de horarios institucionales

| Campo | Detalle |
| :--- | :--- |
| **ID** | `UC-PROV-22` (Identificador provisional — Brecha `BRC-NUEVA-38`) |
| **Nombre** | Consultar horario docente propio, disponibilidad de aulas y matriz general de horarios institucionales |
| **Actores principales** | Docente de área y Docente titular/director de grupo (consulta de su propio horario docente y del horario de su grupo de dirección); Coordinador Académico; Coordinador de Convivencia; Rector; Secretaría |
| **Actores secundarios / sistemas** | Servicio de horarios y aulas (`UC-PROV-20`, `UC-PROV-21`); servicio de asignaciones docentes (`UC-15`); servicio de asistencia (`UC-19`); servicio de auditoría |
| **Descripción** | Permite a los docentes consultar su horario semanal individual consolidado (todas las sesiones de clase que dictan en sus distintos grupos, asignaturas, sedes y aulas, su dirección de grupo y novedades temporales, con acceso directo al registro de asistencia en `UC-19`), y permite a Coordinación Académica, Coordinación de Convivencia, Rectoría y Secretaría consultar la matriz general de horarios de la institución por grupo, por docente, por ocupación/disponibilidad de aula y por ubicación actual en tiempo real. |
| **Propósito / valor** | Cumplir el requerimiento del Documento Maestro V2.0 (Sección 4) de proveer la consulta de horarios de profesores, cursos y aulas, facilitando la operación diaria del docente en el aula y permitiendo a Coordinación y Secretaría ubicar de inmediato a cualquier estudiante, grupo o profesor durante la jornada (incluyendo urgencias y salidas anticipadas en portería `UC-PROV-18` `A1`). |
| **Disparador** | El docente autenticado abre Mi horario docente, o un actor administrativo/directivo consulta la matriz de horarios por grupo, profesor, aula o franja actual. |
| **Precondiciones** | Sesión válida (`UC-01`); permisos contextuales según el rol activo; existencia de horarios publicados (o en borrador para revisión exclusiva de Coordinación Académica y Rectoría). |
| **Invariantes y reglas de negocio** | Se aplica autorización contextual en backend (`RN-09`): el Docente consulta su propio horario semanal consolidado y, si es Director de grupo vigente (`UC-15` `A1`), el horario completo de su grupo asignado, además de la disponibilidad de aulas especiales para solicitar reservas (`UC-PROV-21` `A3`). Coordinador Académico, Coordinador de Convivencia, Rector y Secretaría pueden consultar el horario de cualquier grupo, docente o aula de la institución; los horarios en estado `BORRADOR` solo son visibles para Coordinación Académica y Rectoría. |
| **Postcondiciones** | Se presenta o exporta el horario individual del docente, el mapa de ocupación y disponibilidad de aulas o la matriz general institucional según el filtro seleccionado, habilitando al docente el enlace directo hacia `UC-19` / `UC-16` / `UC-17` para sus sesiones del día. |
| **Tipo** | Básico / Consulta Operativa Docente y Administrativa |
| **Prioridad** | Alta |

### Flujo principal

| Paso | Actor / responsable | Acción e interacción del sistema |
| :--- | :--- | :--- |
| 1 | Docente / Coordinador / Rector / Secretaría | Selecciona el año lectivo, la semana o fecha y el modo de consulta autorizado según su rol: (a) Mi horario semanal docente, (b) Horario por grupo, (c) Horario por profesor, (d) Ocupación y aulas disponibles por franja, o (e) Ubicación actual en tiempo real. |
| 2 | Sistema | Verifica en backend el rol activo y el alcance contextual del usuario en sesión. |
| 3 | Sistema | Recupera los bloques horarios vigentes para la fecha consultada (`UC-PROV-20`), integrando las suplencias activas (`UC-15` `A3`), las novedades o cambios temporales de aula (`UC-PROV-21`) y los días no lectivos del calendario (`UC-14` `A4`). |
| 4 | Sistema | Presenta la grilla horaria con las franjas, grupos, asignaturas, aulas, sedes y total de horas semanales programadas (y para el Docente, habilita en cada sesión del día lectivo actual el acceso directo para tomar asistencia en `UC-19` o gestionar actividades en `UC-16`/`UC-17`). |

### Flujos alternos y de excepción

Cada alternativa indica la condición de entrada, el tratamiento esperado y el resultado. Los flujos `A#` pueden retomar el principal cuando así se indique; los casos `E#` deben terminar de forma controlada, sin persistir cambios parciales.

| ID | Condición / paso de origen | Tratamiento / secuencia alterna | Resultado / estado final |
| :--- | :--- | :--- | :--- |
| **A1** | Consulta de ubicación en tiempo real de un estudiante, grupo o docente para atención de urgencias o salida anticipada en portería (pasos 1 a 4) | Cuando Secretaría o Coordinación necesita localizar de inmediato a un estudiante (por ejemplo, para tramitar una salida anticipada en portería en `UC-PROV-18` `A1` o atender una urgencia) o a un docente, ingresa el nombre/documento y el sistema indica exactamente en qué aula/laboratorio, con qué asignatura y con qué docente se encuentra el grupo en la franja horaria actual. | Localización física inmediata del estudiante o docente dentro del plantel. |
| **A2** | Consulta de mapa de ocupación y búsqueda de aulas o laboratorios libres en una franja horaria (pasos 1 a 4) | El Coordinador Académico o un Docente filtra por sede, día/fecha y franja horaria y selecciona Ver aulas disponibles; el sistema muestra qué salones, laboratorios o salas de sistemas se encuentran desocupados y operativos con su respectiva capacidad de estudiantes para facilitar reservas o reubicaciones en `UC-PROV-21`. | Visibilidad instantánea de espacios físicos disponibles sin riesgo de cruce. |
| **E1** | Usuario sin permiso intenta consultar la matriz general de docentes o borradores no publicados (paso 2) | Un usuario sin rol administrativo/directivo intenta consultar horarios privados de otros docentes o versiones en estado `BORRADOR`; el backend rechaza la solicitud con `HTTP 403 Forbidden` y registra el evento en auditoría. | Control de acceso por rol y estado de publicación preservado. |
| **E2** | El docente o aula consultada no registra bloques horarios programados en el periodo (paso 3) | El sistema informa claramente que aún no existen bloques horarios publicados para el docente, grupo o aula en el rango seleccionado. | Respuesta informativa controlada. |