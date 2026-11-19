# CASOS DE USO — SISTEMA DE GESTIÓN ESCOLAR

## Módulo 03 — Estructura académica

**Año lectivo, calendario escolar, periodos, niveles, grados, grupos con cupos y plan de estudios**

- **Orden recomendado:** 03 de 12
- **Casos de uso incluidos:** `UC-14`, `UC-PROV-06`, `UC-PROV-07`, `UC-PROV-08`
- **Referencia de desarrollo:** Fase 4 — núcleo administrativo; preparación de la fase académica
- **Dependencias:** Seguridad y acceso (`UC-01`, `UC-PROV-02`); decisiones institucionales sobre calendario, estructura y versión aprobada del SIEE (`UC-26`).

> Se ubica antes de matrícula y asignaciones porque los grupos, grados, áreas, asignaturas/dimensiones, cupos y periodos son referencias necesarias para esos procesos. `UC-14` gobierna la configuración del año lectivo, calendario y periodos; `UC-PROV-06`, `UC-PROV-07` y `UC-PROV-08` (con identificador provisional hasta su aprobación en el catálogo definitivo) especifican la administración de niveles/grados/grupos con cupos, el plan de estudios por grado y las consultas autorizadas de la estructura.
>
> *Fragmentación del documento fuente · versión actualizada tras auditoría de Fase 3*

---

## UC-14 · Configurar año lectivo, periodos y estructura académica

| Campo | Detalle |
| :--- | :--- |
| **ID** | `UC-14` |
| **Nombre** | Configurar año lectivo, periodos y estructura académica |
| **Actores principales** | Rector; Coordinador Académico |
| **Actores secundarios / sistemas** | Secretaría (apoyo operativo autorizado y consulta para matrícula/certificados); Consejo Académico / Consejo Directivo cuando su aprobación sea requerida (registrada mediante acta); servicio de configuración institucional SIEE (`UC-26`); servicio de auditoría |
| **Descripción** | Permite crear y gestionar el ciclo de vida del año lectivo (`EN_PLANEACION`, `ACTIVO`, `EN_CIERRE_ANUAL`, `CERRADO_HISTORICO`), proyectar o clonar la estructura desde el año lectivo anterior, configurar el calendario escolar (Calendario A o B, semanas lectivas y días no lectivos/recesos), definir los periodos académicos con sus fechas, ventanas de calificación y ponderaciones conforme al SIEE aprobado, y articular la publicación oficial de la estructura de grados/grupos (`UC-PROV-06`) y del plan de estudios (`UC-PROV-07`). |
| **Propósito / valor** | Asegurar que los procesos de matrícula (`UC-13`), asignaciones docentes (`UC-15`), horarios, asistencia sobre sesiones válidas (`RN-04`, `UC-19`), evaluación (`UC-16` a `UC-18`) y promoción (`UC-20`, `UC-21`) dependan de una estructura temporal y organizativa coherente, versionada y respaldada por las aprobaciones institucionales exigidas por la normativa educativa colombiana (Ley 115 de 1994 y Decreto 1075 de 2015). |
| **Disparador** | El Coordinador Académico o el Rector inicia la apertura o proyección de un año lectivo, configura o ajusta el calendario escolar y sus periodos académicos, o somete a aprobación/publicación la estructura del año lectivo. |
| **Precondiciones** | Actor autenticado con rol Coordinador Académico o Rector (`UC-01`); institución identificada; para publicar la configuración de un año lectivo como `VIGENTE`/`ACTIVO` debe existir una versión aprobada del SIEE (`UC-26`) y los soportes de aprobación del Consejo Académico / Consejo Directivo cuando correspondan. |
| **Invariantes y reglas de negocio** | No se solapan las fechas `[fechaInicio, fechaFin]` de los periodos académicos ordinarios dentro de un mismo año lectivo ni exceden los límites del año escolar; la cantidad de periodos y el peso porcentual de cada periodo en la definitiva anual se subordinan al SIEE vigente (`RN-01`, `RN-EST-ACA-05`) y la suma de sus ponderaciones debe ser exactamente 100.00% (usando `BigDecimal`) cuando el SIEE opere por promedio ponderado de periodos. El calendario escolar contempla las semanas de trabajo académico (estándar legal de 40 semanas / 200 días lectivos, Art. 86 Ley 115 de 1994 y Decreto 1850 de 2002 compilado en Decreto 1075 de 2015) y registra explícitamente los días no lectivos (festivos, recesos estudiantiles, jornadas pedagógicas sin alumnos y suspensiones extraordinarias) para excluirlos del cálculo de sesiones válidas de asistencia (`RN-04`, `RN-EST-ACA-02`). El sistema permite la coexistencia controlada de un año lectivo en estado `EN_PLANEACION` (para preparar cupos y matrículas del año siguiente) mientras el año lectivo en curso permanece en estado `ACTIVO` o `EN_CIERRE_ANUAL` (`RN-EST-ACA-09`). Los cambios críticos quedan versionados; la estructura utilizada por registros históricos, matrículas, asistencias o calificaciones nunca se elimina físicamente ni se sobrescribe recalculando silenciosamente periodos cerrados (`RN-EST-ACA-08`). La publicación de cambios estructurales del calendario o del SIEE exige registrar el número de acta, fecha y órgano colegiado aprobador (Consejo Directivo / Consejo Académico) conforme a la Ley 115 de 1994 Arts. 144-145 (`RN-EST-ACA-07`). |
| **Postcondiciones** | Año lectivo, calendario escolar (incluidos días no lectivos) y periodos académicos creados o actualizados en versión vigente o en estado `EN_PLANEACION` / `PENDIENTE_APROBACION`; referencia a la versión del SIEE y al acta de aprobación colegiada persistida; registro en auditoría con configuración anterior, configuración nueva, actor y motivo. |
| **Tipo** | Básico |
| **Prioridad** | Alta |

### Flujo principal

| Paso | Actor / responsable | Acción e interacción del sistema |
| :--- | :--- | :--- |
| 1 | Coordinador Académico / Rector | Selecciona el año lectivo (o solicita crear uno nuevo en estado `EN_PLANEACION`) y elige el componente de calendario, periodos o estructura general a configurar o modificar. |
| 2 | Sistema | Valida el rol y permisos del actor, comprueba el estado actual del año lectivo, recupera la versión vigente del SIEE (`UC-26`) y verifica dependencias o conflictos temporales y relacionales. |
| 3 | Coordinador Académico / Rector | Define los parámetros del año lectivo (tipo de calendario A o B, fecha de inicio y cierre, semanas lectivas), registra los días no lectivos o recesos del calendario y configura los periodos académicos (orden, nombre, fecha de inicio, fecha de fin, ventana de digitación de notas y peso porcentual según el SIEE), integrando la oferta de grados/grupos (`UC-PROV-06`) y el plan de estudios (`UC-PROV-07`). |
| 4 | Sistema | Verifica la consistencia integral: ausencia de solapamiento entre periodos, suma exacta del 100.00% en las ponderaciones de periodos cuando aplique, cumplimiento de semanas/horas lectivas mínimas por nivel y muestra un resumen detallado de impacto sobre matrículas, asignaciones docentes, horarios, asistencias y actividades existentes. |
| 5 | Coordinador Académico / Rector | Revisa el resumen de impacto, registra los datos del acto de aprobación institucional (número y fecha de acta del Consejo Directivo / Consejo Académico cuando sea requerido) y confirma la publicación o guarda la propuesta como borrador pendiente de aprobación. |
| 6 | Sistema | Ejecuta la actualización en una transacción atómica con control de concurrencia: publica la versión aprobada vinculándola al ID/versión del SIEE vigente, habilita la estructura para matrícula y procesos académicos (y consulta de Secretaría en `UC-PROV-08`) y registra en auditoría la configuración anterior, la nueva, el soporte de aprobación y el responsable. |

### Flujos alternos y de excepción

Cada alternativa indica la condición de entrada, el tratamiento esperado y el resultado. Los flujos `A#` pueden retomar el principal cuando así se indique; los casos `E#` deben terminar de forma controlada, sin persistir cambios parciales.

| ID | Condición / paso de origen | Tratamiento / secuencia alterna | Resultado / estado final |
| :--- | :--- | :--- | :--- |
| **A1** | Inicialización de nuevo año lectivo mediante clonación o proyección desde el año lectivo anterior (pasos 1 y 3) | Al crear el año lectivo siguiente (N+1) en estado `EN_PLANEACION`, el Coordinador Académico o Rector selecciona proyectar estructura desde el año lectivo N; el sistema clona como borrador editable los niveles, grados, áreas, asignaturas/dimensiones, plan de estudios base (IHS y ponderaciones) y estructura base de grupos/cupos, permitiendo ajustar únicamente las nuevas fechas de calendario, periodos y novedades curriculares aprobadas. | Nuevo año lectivo estructurado en estado `EN_PLANEACION` sin redigitar desde cero ni alterar el año lectivo N en curso (`BRC-NUEVA-26`). |
| **A2** | Cambio estructural afecta registros existentes en el año lectivo (paso 4) [antes A2] | El sistema detecta que ya existen matrículas, asignaciones, horarios o calificaciones asociadas; exige generar una nueva versión de la configuración definiendo el periodo o fecha de entrada en vigencia y bloquea cualquier recálculo silencioso o destructivo sobre resultados de periodos ya cerrados o años históricos. | Nueva versión con vigencia acotada; histórico académico estable e inalterado (`RN-EST-ACA-08`). |
| **A3** | Guardado en borrador por falta de aprobación colegiada o rectoral requerida (paso 5) [antes A3] | Cuando el Coordinador Académico prepara la configuración pero aún no cuenta con la aprobación del Rector o con el acta del Consejo Académico / Consejo Directivo, guarda la estructura en estado `BORRADOR` / `PENDIENTE_APROBACION`; el sistema conserva el trabajo sin aplicarlo a operaciones productivas hasta que el Rector registre la aprobación formal. | Propuesta persistida en borrador; no se publica como vigente hasta registrar la aprobación. |
| **A4** | Registro o actualización de días no lectivos, recesos estudiantiles o suspensión extraordinaria de clases (paso 3) | El Coordinador Académico o Rector registra o actualiza en el calendario escolar los días festivos, semanas de receso estudiantil, jornadas pedagógicas sin asistencia de estudiantes o suspensiones extraordinarias motivadas (indicando sede/jornada afectada y soporte); el sistema actualiza el calendario de días hábiles lectivos para que el módulo de Asistencia (`UC-19`) excluya dichas fechas del denominador de sesiones programadas válidas (`RN-04`). | Calendario de sesiones válidas actualizado con trazabilidad sin afectar notas ni matrículas (`BRC-NUEVA-11`). |
| **A5** | Modificación puntual excepcional sobre un año lectivo o periodo cerrado (pasos 2 y 5) [parte de A4 original] | Cuando se requiere un ajuste excepcional debidamente justificado sobre un periodo o año cerrado, el sistema exige rol Rector, registro obligatorio de motivo, referencia de acta o resolución rectoral y ejecuta una auditoría reforzada conservando la versión previa. | Modificación excepcional trazada y controlada sin perder la evidencia del cierre previo. |
| **E1** | Fechas inválidas, periodos académicos solapados o fechas fuera del rango del año lectivo (paso 4) [antes A1] | El sistema señala específicamente los periodos o campos con fechas superpuestas o incongruentes con el inicio/fin del año lectivo y rechaza la publicación de la configuración. | Se conserva intacta la versión previa; no se guardan periodos solapados. |
| **E2** | Suma de ponderaciones de los periodos distinta de 100.00% o incumplimiento de semanas/horas lectivas (paso 4) | El sistema comprueba con precisión decimal que los pesos de los periodos ordinarios no suman 100.00% conforme a la regla del SIEE o que el calendario no cubre las semanas/horas mínimas configuradas; informa el descuadre exacto e impide publicar como vigente. | Publicación bloqueada hasta corregir la distribución porcentual o el calendario. |
| **E3** | Intento de eliminar físicamente o desactivar elementos estructurales con registros asociados (paso 2 o 4) | El actor intenta eliminar o inactivar un año lectivo, periodo, grado, grupo, área o asignatura que ya cuenta con matrículas, asignaciones docentes, horarios, asistencias o calificaciones; el sistema rechaza la operación con `HTTP 409` e indica las dependencias existentes. | Integridad referencial e histórica protegida; no se permiten eliminaciones destructivas. |
| **E4** | Intento de modificación ordinaria sobre año lectivo o periodo en estado cerrado (paso 2) [parte de A4 original] | El sistema detecta que el año lectivo o el periodo objetivo se encuentra en estado `CERRADO` y que no se ha invocado ni autorizado el flujo excepcional rectoral (`A5`); bloquea la edición ordinaria con `HTTP 409/403`. | Se preserva la inmutabilidad del cierre académico. |
| **E5** | Conflicto concurrente de edición sobre la estructura o calendario (paso 6) [antes A5] | El sistema detecta mediante control de versión optimista que otro usuario autorizado modificó la misma estructura simultáneamente; rechaza el guardado con `HTTP 409` y solicita recargar y reconciliar los cambios. | No se sobrescribe ni se pierde una actualización concurrente ajena. |

---

## UC-PROV-06 · Administrar niveles, grados, sedes, jornadas y grupos con capacidad de cupos

| Campo | Detalle |
| :--- | :--- |
| **ID** | `UC-PROV-06` (Identificador provisional — Brecha `BRC-NUEVA-25`) |
| **Nombre** | Administrar niveles, grados, sedes, jornadas y grupos con capacidad de cupos |
| **Actores principales** | Coordinador Académico; Rector; Secretaría (para consulta de disponibilidad y apoyo administrativo autorizado en la proyección de cupos) |
| **Actores secundarios / sistemas** | Servicio de matrícula (`UC-13`); servicio de asignaciones docentes (`UC-15`); servicio de auditoría |
| **Descripción** | Permite configurar y mantener los niveles de educación formal colombiana (Preescolar, Básica Primaria, Básica Secundaria y Educación Media), el catálogo secuencial de grados con su regla de promoción al grado siguiente, las sedes y jornadas de la institución, y la oferta anual de grupos (cursos) por grado con su capacidad máxima de cupos (`cupoMaximo`), aula base y estado operativo. |
| **Propósito / valor** | Proveer la estructura organizativa y de capacidad de cupos indispensable para ejecutar matrículas nuevas y renovaciones (`UC-13`), determinar automáticamente el grado destino en la promoción escolar (`UC-21`) y asignar directores de grupo y cargas académicas docentes (`UC-15`). |
| **Disparador** | El Coordinador Académico o Rector configura la oferta de grados y grupos para un año lectivo (en planeación o activo) o solicita abrir un nuevo grupo, ajustar el cupo máximo o fusionar/cerrar un grupo sin matrículas. |
| **Precondiciones** | Actor autenticado con rol Coordinador Académico o Rector (`UC-01`); año lectivo existente en estado `EN_PLANEACION` o `ACTIVO` (`UC-14`). |
| **Invariantes y reglas de negocio** | Los niveles y grados respetan la estructura de la Ley 115 de 1994 (Arts. 11, 15, 19 y 27) y el Decreto 1075 de 2015: Preescolar (Prejardín, Jardín, Transición/0°), Básica Primaria (1° a 5°), Básica Secundaria (6° a 9°) y Educación Media (10° y 11°, o ciclo complementario cuando aplique). Cada grado tiene un orden secuencial único y define explícitamente su grado siguiente de promoción (o su condición de último grado de titulación) y su régimen de evaluación (`CUALITATIVO_DIMENSIONES` en Preescolar vs. `CUANTITATIVO_AREAS` en Básica y Media) (`RN-EST-ACA-01`, `RN-EST-ACA-04`). Todo grupo ofertado pertenece a una combinación única de `(AnioLectivo, Sede, Jornada, Grado, CodigoGrupo)` y debe definir un `cupoMaximo` entero mayor que cero (`RN-EST-ACA-06`). El cupo disponible del grupo se calcula dinámicamente en función de las matrículas activas/reservadas en `UC-13`. Nunca se permite reducir el `cupoMaximo` de un grupo por debajo del número de estudiantes ya matriculados en él, ni cerrar o eliminar un grupo que tenga estudiantes matriculados o asignaciones con calificaciones registradas. |
| **Postcondiciones** | Niveles, grados con secuencia de promoción, sedes, jornadas y grupos con capacidad de cupos persistidos y habilitados para el proceso de matrícula (`UC-13`) y asignación docente (`UC-15`); cambios registrados en auditoría. |
| **Tipo** | Primario / Estructura Organizativa y Cobertura |
| **Prioridad** | Alta |

### Flujo principal

| Paso | Actor / responsable | Acción e interacción del sistema |
| :--- | :--- | :--- |
| 1 | Coordinador Académico / Rector | Selecciona el año lectivo, la sede y la jornada, y accede a la configuración de niveles, grados y grupos (cursos). |
| 2 | Sistema | Muestra el catálogo de niveles y grados configurados con su secuencia de promoción, así como los grupos existentes para el año lectivo con su cupo máximo, matrículas actuales, cupos disponibles y estado. |
| 3 | Coordinador Académico / Rector | Configura o verifica los grados activos y su orden de promoción, y crea o actualiza los grupos de cada grado (por ejemplo, 6°A, 6°B, Transición A) definiendo su nomenclatura, sede, jornada, cupo máximo de estudiantes y aula física base opcional. |
| 4 | Sistema | Valida la unicidad del código del grupo dentro de la misma sede, jornada, grado y año lectivo, comprueba que el cupo máximo sea positivo y no sea inferior a las matrículas ya registradas en el grupo, y verifica la coherencia de la secuencia de grados. |
| 5 | Coordinador Académico / Rector | Confirma la configuración de la oferta de grupos y cupos. |
| 6 | Sistema | Guarda los cambios en una transacción atómica, actualiza la disponibilidad de cupos para el módulo de Matrícula (`UC-13`) y registra la operación en auditoría (actor, valores anteriores y nuevos, fecha/hora e ID de correlación). |

### Flujos alternos y de excepción

Cada alternativa indica la condición de entrada, el tratamiento esperado y el resultado. Los flujos `A#` pueden retomar el principal cuando así se indique; los casos `E#` deben terminar de forma controlada, sin persistir cambios parciales.

| ID | Condición / paso de origen | Tratamiento / secuencia alterna | Resultado / estado final |
| :--- | :--- | :--- | :--- |
| **A1** | Apertura de un nuevo grupo adicional o ampliación de cupo máximo por alta demanda de matrícula (paso 3) | Durante el periodo de matrículas o el año lectivo, el Rector o Coordinador Académico autoriza incrementar el `cupoMaximo` de un grupo existente o crear un nuevo grupo paralelo en el grado (por ejemplo, abrir 7°C); el sistema hereda automáticamente el Plan de Estudios vigente del grado (`UC-PROV-07`), habilita los nuevos cupos para `UC-13` y notifica la necesidad de asignar docentes al nuevo grupo en `UC-15`. | Oferta de cupos ampliada de forma controlada y auditada. |
| **A2** | Cierre o inactivación de un grupo proyectado que no alcanzó matrículas (paso 3) | Si un grupo en estado `EN_PROYECCION` o `ACTIVO` tiene cero (0) estudiantes matriculados y ninguna calificación o asistencia registrada, el Rector o Coordinador Académico solicita su cierre/inactivación por baja demanda; el sistema cambia el estado del grupo a `CERRADO_SIN_CUPOS` e impide nuevas matrículas en él. | Grupo cerrado lógicamente sin eliminar el registro ni afectar a otros grupos del grado. |
| **E1** | Intento de reducir el cupo máximo por debajo de la cantidad de estudiantes ya matriculados en el grupo (paso 4) | El sistema detecta que el nuevo valor de `cupoMaximo` es menor que el número de matrículas activas o en proceso en dicho grupo; rechaza el cambio con `HTTP 409` e informa la cantidad actual de estudiantes vinculados. | Se impide generar inconsistencia de sobrecupo retroactivo. |
| **E2** | Intento de cerrar o eliminar un grupo que posee estudiantes matriculados o registros académicos (paso 4) | El sistema comprueba que el grupo tiene estudiantes matriculados activos, horarios, asistencias o calificaciones; bloquea el cierre o eliminación con `HTTP 409` e indica que primero deben reubicarse formalmente los estudiantes en otro grupo del mismo grado (Módulo 04). | Se protege la continuidad de las matrículas y registros académicos del grupo. |
| **E3** | Duplicidad de código de grupo en el mismo año lectivo, sede, jornada y grado, o ciclo inválido en la secuencia de promoción de grados (paso 4) | El sistema detecta que ya existe un grupo con la misma identificación o que la referencia de grado siguiente crea un bucle circular; rechaza la operación e identifica el campo en conflicto. | Estructura de grados y grupos libre de duplicados y ciclos inválidos. |

---

## UC-PROV-07 · Administrar áreas, asignaturas, dimensiones de preescolar y plan de estudios por grado

| Campo | Detalle |
| :--- | :--- |
| **ID** | `UC-PROV-07` (Identificador provisional — Brechas `BRC-NUEVA-03` y `BRC-NUEVA-24`) |
| **Nombre** | Administrar áreas, asignaturas, dimensiones de preescolar y plan de estudios por grado |
| **Actores principales** | Coordinador Académico; Rector |
| **Actores secundarios / sistemas** | Consejo Académico / Consejo Directivo (aprobación curricular documentada en acta); servicio de configuración institucional SIEE (`UC-26`); servicio de auditoría |
| **Descripción** | Permite administrar el catálogo curricular de áreas (obligatorias/fundamentales de Ley 115 de 1994 y optativas del PEI), asignaturas y dimensiones del desarrollo para el nivel Preescolar (Decreto 2247 de 1997 / Decreto 1075 de 2015), y estructurar el Plan de Estudios (malla curricular) por cada grado y año lectivo, definiendo la jerarquía Área -> Asignatura, la Intensidad Horaria Semanal (IHS) y el peso porcentual de cada asignatura dentro de su área conforme al SIEE. |
| **Propósito / valor** | Garantizar que las asignaciones docentes (`UC-15`), los horarios (Módulo 09), el cálculo de inasistencia (`UC-19`), la consolidación ponderada de calificaciones de asignaturas hacia las áreas en boletines y certificados (`UC-17`, `UC-20`, Módulo 12) y las reglas de promoción (`UC-21`) cuenten con una malla curricular completa, exacta y diferenciada entre Preescolar y Básica/Media. |
| **Disparador** | El Coordinador Académico o Rector crea o actualiza el catálogo de áreas y asignaturas/dimensiones o configura el Plan de Estudios de los grados para un año lectivo. |
| **Precondiciones** | Actor autenticado con rol Coordinador Académico o Rector (`UC-01`); año lectivo y grados configurados (`UC-14`, `UC-PROV-06`); versión del SIEE disponible para validar reglas de ponderación y evaluación (`UC-26`). |
| **Invariantes y reglas de negocio** | En Básica Primaria, Básica Secundaria y Educación Media, el Plan de Estudios organiza las asignaturas dentro de Áreas curriculares (incluyendo las áreas obligatorias y fundamentales de los Arts. 23 y 31 de la Ley 115 de 1994 y las optativas del PEI) (`RN-EST-ACA-03`). Toda asignatura o dimensión asignada a un grado en el Plan de Estudios debe tener definida una Intensidad Horaria Semanal (`IHS > 0`) expresada en horas/bloques semanales. Cuando un área en un grado esté compuesta por dos o más asignaturas y el SIEE defina consolidación ponderada, la suma de los pesos porcentuales (`pesoPorcentualEnArea` con `BigDecimal`) de las asignaturas activas de esa área en ese grado debe ser exactamente 100.00% (si el área tiene una única asignatura, su peso es 100.00%). En los grados del nivel Preescolar (Prejardín, Jardín y Transición), el Plan de Estudios se estructura por Dimensiones del Desarrollo (corporal, cognitiva, comunicativa, estética, espiritual, ética y socio-afectiva) o proyectos pedagógicos bajo régimen de valoración cualitativa/descriptiva y sin reprobación de grado conforme al Decreto 2247 de 1997 compilado en el Decreto 1075 de 2015 (`RN-EST-ACA-04`). La publicación o modificación del Plan de Estudios requiere registrar el acta del Consejo Académico / Consejo Directivo (`RN-EST-ACA-07`) y genera una versión inmutable cuando ya existen calificaciones registradas (`RN-EST-ACA-08`). |
| **Postcondiciones** | Catálogo de áreas, asignaturas y dimensiones actualizado; Plan de Estudios por grado y año lectivo persistido y versionado con intensidades horarias y ponderaciones validadas al 100.00%; acta de aprobación colegiada registrada y cambios trazados en auditoría. |
| **Tipo** | Primario / Diseño Curricular y Plan de Estudios |
| **Prioridad** | Alta |

### Flujo principal

| Paso | Actor / responsable | Acción e interacción del sistema |
| :--- | :--- | :--- |
| 1 | Coordinador Académico / Rector | Accede a la gestión curricular del año lectivo y selecciona el catálogo de Áreas / Asignaturas / Dimensiones o el Plan de Estudios de un grado específico. |
| 2 | Sistema | Muestra las áreas curriculares, asignaturas y dimensiones registradas, así como la malla curricular actual del grado seleccionado con sus intensidades horarias semanales (IHS), ponderaciones por área y versión vigente. |
| 3 | Coordinador Académico / Rector | Registra o actualiza las áreas y asignaturas (para Básica y Media) o las dimensiones del desarrollo (para Preescolar), y las asocia al Plan de Estudios del grado indicando para cada componente su Intensidad Horaria Semanal (IHS), su peso porcentual dentro del área (o ponderación automática por IHS según el SIEE) y si computa para promoción en Básica/Media. |
| 4 | Sistema | Valida que cada asignatura pertenezca a un área válida en el grado, que toda IHS sea mayor que cero, que la suma de los pesos porcentuales de las asignaturas dentro de cada área sea exactamente 100.00% (`BigDecimal`), que los grados de Preescolar operen bajo régimen cualitativo sin reprobación y que la suma de horas semanales sea consistente con la jornada escolar del nivel. |
| 5 | Coordinador Académico / Rector | Revisa la validación del Plan de Estudios del grado, ingresa la referencia del acta de aprobación del Consejo Académico / Consejo Directivo cuando corresponda publicar la versión oficial y confirma la operación. |
| 6 | Sistema | Persiste de forma transaccional la versión aprobada del Plan de Estudios del grado, habilita sus asignaturas/dimensiones para la asignación de carga docente (`UC-15`), horarios y evaluación (`UC-16/17`), y registra el detalle completo en auditoría. |

### Flujos alternos y de excepción

Cada alternativa indica la condición de entrada, el tratamiento esperado y el resultado. Los flujos `A#` pueden retomar el principal cuando así se indique; los casos `E#` deben terminar de forma controlada, sin persistir cambios parciales.

| ID | Condición / paso de origen | Tratamiento / secuencia alterna | Resultado / estado final |
| :--- | :--- | :--- | :--- |
| **A1** | Configuración del Plan de Estudios para grados de Educación Preescolar por Dimensiones del Desarrollo (paso 3 y 4) | Al seleccionar un grado del nivel Preescolar (Prejardín, Jardín o Transición), el sistema habilita el esquema curricular por Dimensiones del Desarrollo (Decreto 2247 de 1997 / Decreto 1075 de 2015), asociando el régimen de valoración descriptiva/cualitativa configurado en el SIEE y marcando por invariante legal que el grado no está sujeto a reprobación numérica en `UC-21`. | Malla curricular de Preescolar configurada conforme a la normativa nacional (`BRC-NUEVA-03`, `RN-EST-ACA-04`). |
| **A2** | Área integrada por múltiples asignaturas con consolidación ponderada hacia la definitiva del área (paso 3 y 4) | El Coordinador Académico asocia varias asignaturas a una misma área en un grado (por ejemplo, Física, Química y Biología dentro del Área de Ciencias Naturales, o Lengua Castellana y Lectura Crítica en Humanidades) y define el porcentaje de cada una (o selecciona ponderación proporcional a su IHS según el SIEE); el sistema verifica que sumen exactamente 100.00% y deja configurada la regla para calcular la definitiva del área en `UC-17`, `UC-20` y `UC-21`. | Relación jerárquica Área -> Asignatura configurada con precisión exacta para boletines y promoción (`BRC-NUEVA-24`). |
| **A3** | Copia del Plan de Estudios entre grados del mismo ciclo o desde el año lectivo anterior (paso 1) | Para agilizar la configuración de grados de un mismo ciclo (por ejemplo, de 6° a 7° o de 10° a 11°), el actor selecciona copiar la malla base de otro grado o año lectivo y realiza únicamente los ajustes específicos antes de validar y publicar. | Plan de estudios del grado preparado rápidamente sin errores de omisión. |
| **E1** | La suma de ponderaciones de las asignaturas de un área en el grado es diferente de 100.00% (paso 4) | El sistema detecta que los pesos porcentuales de las asignaturas que integran un área en el grado suman menos o más de 100.00%; señala el área con el descuadre e impide publicar el Plan de Estudios hasta corregirlo. | No se permiten áreas con ponderación incompleta o superior al 100%. |
| **E2** | Intento de retirar una asignatura o cambiar ponderaciones en un periodo que ya tiene calificaciones registradas sin versionamiento (paso 4) | El sistema detecta que la asignatura o área ya posee actividades evaluativas, calificaciones o asistencias registradas en el periodo; bloquea la eliminación destructiva con `HTTP 409` y exige crear una nueva versión aplicable únicamente a periodos futuros no cerrados previa autorización colegiada. | Se protege la integridad e inmutabilidad de las calificaciones ya causadas. |
| **E3** | Omisión de áreas obligatorias y fundamentales de la Ley 115 de 1994 al publicar el Plan de Estudios de Básica o Media (paso 4) | Al solicitar la publicación definitiva del Plan de Estudios de un grado de Básica o Media, el sistema advierte o bloquea la publicación si faltan áreas marcadas como obligatorias según la parametrización legal e institucional, exigiendo completarla o registrar la justificación curricular aprobada en el PEI. | Cumplimiento curricular de la Ley 115 de 1994 verificado. |

---

## UC-PROV-08 · Consultar estructura académica, calendario escolar, oferta de grupos y plan de estudios

| Campo | Detalle |
| :--- | :--- |
| **ID** | `UC-PROV-08` (Identificador provisional — Brecha `BRC-NUEVA-27` e `INC-08`) |
| **Nombre** | Consultar estructura académica, calendario escolar, oferta de grupos y plan de estudios |
| **Actores principales** | Secretaría; Coordinador Académico; Coordinador de Convivencia; Rector; Docente de área; Docente titular/director de grupo; Estudiante; Acudiente / adulto responsable validado |
| **Actores secundarios / sistemas** | Servicio de control de acceso contextual; auditoría (cuando aplique) |
| **Descripción** | Permite consultar, de acuerdo con el rol y contexto del usuario autenticado, la información vigente del año lectivo, el calendario escolar (fechas de periodos académicos, ventanas de evaluación y días no lectivos/recesos), el Plan de Estudios por grado (áreas, asignaturas/dimensiones, intensidades horarias semanales y ponderaciones) y, para el personal administrativo y directivo, la oferta de grupos y disponibilidad de cupos. |
| **Propósito / valor** | Brindar transparencia sobre el calendario escolar y el Plan de Estudios a toda la comunidad educativa (Ley 115 de 1994 y Decreto 1290 de 2009) y suministrar a Secretaría, Coordinación y Docentes las consultas operativas necesarias para matrícula (`UC-13`), asignaciones (`UC-15`), evaluación (`UC-16/17`) y expedición de certificados (Módulo 12). |
| **Disparador** | El actor autenticado solicita consultar el calendario académico, las fechas de los periodos, el Plan de Estudios de un grado o la disponibilidad de grupos y cupos. |
| **Precondiciones** | Sesión válida (`UC-01`); existencia de al menos un año lectivo con estructura publicada como `VIGENTE` (o en estado `EN_PLANEACION` para roles administrativos y directivos). |
| **Invariantes y reglas de negocio** | Se aplica autorización contextual por rol (`RN-09`): Rector, Coordinadores y Secretaría pueden consultar tanto el año lectivo `ACTIVO` como el año `EN_PLANEACION` y los años `CERRADOS` históricos, incluyendo el detalle de capacidad máxima, matrículas ocupadas y cupos disponibles por grupo para operar `UC-13`. Los Docentes consultan el calendario escolar, los periodos, las ventanas de digitación de notas y el Plan de Estudios de los grados de la institución. Los Estudiantes y Acudientes con vínculo verificado consultan exclusivamente las versiones publicadas y vigentes del calendario escolar (periodos y días no lectivos) y el Plan de Estudios correspondiente al grado en que se encuentra matriculado el estudiante, sin exponer borradores no aprobados ni datos internos de cupos administrativos. |
| **Postcondiciones** | Se presenta la información estructural, de calendario o curricular filtrada según el rol y alcance contextual del solicitante, sin alterar ningún dato del sistema. |
| **Tipo** | Básico / Consulta Transversal |
| **Prioridad** | Media-Alta |

### Flujo principal

| Paso | Actor / responsable | Acción e interacción del sistema |
| :--- | :--- | :--- |
| 1 | Actor autenticado | Selecciona el año lectivo permitido según su rol y solicita consultar el calendario escolar/periodos, la oferta de grados/grupos o el Plan de Estudios. |
| 2 | Sistema | Verifica en backend el rol activo de la sesión y el alcance contextual del usuario (para Estudiante o Acudiente, comprueba la matrícula vigente o histórica del estudiante vinculado). |
| 3 | Sistema | Recupera la versión publicada de la estructura académica solicitada: fechas de inicio y fin de periodos, días no lectivos, áreas, asignaturas/dimensiones con su Intensidad Horaria Semanal (IHS) y ponderación, y estado de grupos/cupos si el rol es administrativo o directivo. |
| 4 | Sistema | Oculta cualquier propuesta en estado `BORRADOR` / `PENDIENTE_APROBACION` para usuarios no directivos y presenta la vista estructurada al actor. |

### Flujos alternos y de excepción

Cada alternativa indica la condición de entrada, el tratamiento esperado y el resultado. Los flujos `A#` pueden retomar el principal cuando así se indique; los casos `E#` deben terminar de forma controlada, sin persistir cambios parciales.

| ID | Condición / paso de origen | Tratamiento / secuencia alterna | Resultado / estado final |
| :--- | :--- | :--- | :--- |
| **A1** | Consulta operativa de disponibilidad de cupos por grupo realizada por Secretaría o Dirección para matrícula (pasos 1 a 4) | Secretaría, Coordinación o Rectoría filtra por año lectivo (`ACTIVO` o `EN_PLANEACION`), sede, jornada y grado; el sistema muestra cada grupo con su `cupoMaximo`, número de estudiantes matriculados, reservas en trámite y cupos disponibles en tiempo real para ejecutar `UC-13`. | Información exacta de cobertura disponible para el proceso de matrícula (resuelve la participación de Secretaría en el Módulo 03). |
| **A2** | Consulta de versión histórica del Plan de Estudios de un año lectivo cerrado para expedición de certificados (pasos 1 a 4) | Secretaría o Dirección selecciona un año lectivo anterior en estado `CERRADO_HISTORICO`; el sistema recupera la versión exacta e inmutable del Plan de Estudios (áreas, asignaturas e intensidades horarias) que estuvo vigente en ese año lectivo para garantizar la fidelidad de los certificados de estudio (Módulo 12). | Trazabilidad curricular histórica garantizada por versión. |
| **E1** | Estudiante o Acudiente intenta consultar estructuras en borrador o de grados/controles administrativos no autorizados (paso 2) | El backend detecta que el usuario solicita una configuración en estado `BORRADOR` o datos internos de administración de cupos fuera de su permiso; deniega el acceso con `HTTP 403` o filtra la respuesta exclusivamente a la información pública vigente de su grado. | Se respeta el principio de mínimo privilegio y se evita exponer borradores no aprobados. |
| **E2** | No existe estructura publicada para el año lectivo consultado (paso 3) | El sistema informa que el calendario o Plan de Estudios del año seleccionado aún se encuentra en proceso de aprobación institucional y no muestra datos provisionales como definitivos. | Consulta controlada sin exposición de datos no oficiales. |