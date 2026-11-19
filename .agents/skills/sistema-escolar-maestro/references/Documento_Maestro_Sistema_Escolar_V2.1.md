# DOCUMENTO MAESTRO

# Sistema de Gestión Escolar

**Definición del proyecto, arquitectura, módulos, tecnologías y hoja de ruta**

**Versión:** 2.1 — Documento de referencia consolidado tras auditoría funcional V2 de los 12 módulos  
**Fecha de actualización:** 18 de noviembre de 2026  
**Propósito adicional:** servir como contexto base unificado para futuras conversaciones, diseño técnico (DER, Flyway, OpenAPI 3.x) y desarrollo progresivo.

> **Decisión estratégica de la versión 2.0:** el cliente inicial de la V1 será una Single Page Application (SPA) desarrollada con Angular y TypeScript, administrada con Node.js/npm. JavaFX deja de formar parte del alcance del cliente inicial. El backend Spring Boot y sus reglas de negocio se mantienen independientes del frontend.

> **Consolidación funcional de la versión 2.1:** se completó la auditoría normativa y operacional de los **12 módulos funcionales (`v2`)**, consolidando un catálogo de **56 casos de uso** (los 27 originales `UC-01` a `UC-27` corregidos + 29 casos provisionales aprobados `UC-PROV-01` a `UC-PROV-29`). Esta versión integra formalmente el dominio de **Convivencia escolar** (Módulo 11) en la arquitectura y el roadmap, armoniza el modelo de roles, asignaciones contextuales y órganos colegiados (`RN-SEG-01`), y actualiza la secuencia técnica de dependencias con base en `00_Guia_General_y_Reglas_Transversales_v2.docx`, `00_Indice_y_Secuencia_de_Desarrollo_v2.docx` y `MAPA_UC_Y_DEPENDENCIAS_v2.md`.

---

# 1. Objetivo general del proyecto

Construir un sistema de gestión escolar completo, modular, seguro y escalable que permita digitalizar progresivamente los procesos académicos, administrativos, de convivencia escolar, documentales y, en fases posteriores de evolución, financieros y de comunicación de una institución educativa colombiana de educación preescolar, básica primaria, básica secundaria y media.

El proyecto no debe tratarse como un simple CRUD de estudiantes. La intención es construir un software mantenible, con reglas de negocio explícitas, seguridad, permisos contextuales, auditoría inmutable, reportes, manejo de errores, pruebas, documentación y una arquitectura que pueda crecer sin tener que reescribir sus componentes centrales.

La primera versión usable tendrá como cliente una **SPA en Angular + TypeScript**. Angular consumirá una API REST desarrollada con Java y Spring Boot. La persistencia central estará en PostgreSQL. El backend permanecerá desacoplado del cliente web para permitir que, posteriormente, una aplicación móvil desarrollada con Kotlin u otros clientes autorizados consuman la misma API.

El sistema debe adaptarse a los procesos reales de una institución educativa colombiana. Las reglas académicas, criterios de evaluación, escalas de valoración, condiciones de promoción y procedimientos de convivencia deberán responder al marco normativo nacional vigente y a las decisiones institucionales formalizadas y versionadas en el Proyecto Educativo Institucional (PEI), el Sistema Institucional de Evaluación de los Estudiantes (SIEE), el Manual de Convivencia y la Política de Tratamiento de Datos Personales, según corresponda.

Objetivos principales:

- Administrar cuentas nominativas, roles vigentes, membresías colegiadas, estudiantes, acudientes, profesores y estructura académica (sedes, jornadas, niveles, grados, grupos con cupos y plan de estudios).
- Gestionar matrículas, novedades de permanencia (cambios de grupo, promoción anticipada, retiros y traslados) e historial académico.
- Registrar y consultar calificaciones (con cálculo jerárquico hasta áreas curriculares y régimen cualitativo en Preescolar), nivelaciones, reclamaciones, convalidaciones, asistencia con excusas/salidas anticipadas y horarios sin colisiones.
- Aplicar reglas académicas y de convivencia configurables y versionadas en el Módulo 06, evitando codificar como universales decisiones que dependen del colegio.
- Gestionar la convivencia escolar mediante el Observador del Estudiante, la Ruta de Atención Integral (Situaciones Tipo I, II y III), el debido proceso disciplinario con garantías constitucionales, la continuidad académica y el Comité Escolar de Convivencia.
- Generar y versionar boletines, certificados de estudio (anuales y parciales por retiro), constancias, actas oficiales y reportes institucionales/sectoriales autorizados.
- Proteger los datos personales, especialmente los de niños, niñas y adolescentes, incluyendo datos sensibles de salud, inclusión (PIAR) y convivencia reservada.
- Mantener trazabilidad forense mediante auditoría `append-only` con verificación criptográfica de integridad.
- Exponer un contrato API estable y documentado para facilitar la integración de clientes presentes y futuros.
- Desarrollar por fases, validando reglas y pruebas antes de ampliar el alcance.

# 2. Visión actual del sistema

La arquitectura general propuesta es la siguiente:

| Capa | Tecnología / componente | Responsabilidad |
| --- | --- | --- |
| Cliente web inicial | Angular + TypeScript | SPA utilizada por los usuarios para acceder a las funcionalidades autorizadas. |
| Runtime y herramientas frontend | Node.js + npm + Angular CLI | Instalación de dependencias, ejecución local, compilación, pruebas y herramientas de desarrollo. Node.js es una herramienta del entorno de desarrollo/compilación; la aplicación Angular se ejecuta en el navegador. |
| Comunicación | HTTP/HTTPS + REST + JSON | Transporte de solicitudes y respuestas entre Angular y el backend. HTTPS será obligatorio en despliegues que atraviesen redes no confiables. |
| Contrato de API | OpenAPI 3.x + Swagger UI | Definición versionada de endpoints, parámetros, DTO, esquemas, respuestas y errores. Se utilizará OpenAPI Generator para producir un cliente Angular tipado. |
| Backend | Java + Spring Boot | Casos de uso, reglas de negocio, API REST, autenticación, autorización contextual, validación y acceso a datos. |
| Seguridad backend | Spring Security + RBAC + autorización contextual + estrategia de sesión/token definida | Autenticación y autorización reales; protección de endpoints y operaciones sensibles. La SPA no será la autoridad de seguridad. |
| Persistencia | Spring Data JPA / Hibernate | Mapeo objeto-relacional y acceso a los datos. |
| Base de datos | PostgreSQL | Persistencia central y protección de integridad mediante claves, restricciones `UNIQUE`, índices y transacciones ACID. |
| Migraciones | Flyway (o Liquibase; se evitará mantener dos herramientas en paralelo) | Evolución versionada, revisable y reproducible del esquema desde un estado vacío. |
| Pruebas | JUnit + Mockito; pruebas de integración; pruebas Angular | Verificación de reglas de negocio, controladores, persistencia, seguridad, concurrencia e interfaz. Testcontainers podrá utilizarse cuando aporte valor. |
| Infraestructura | Docker / Docker Compose | Ejecución reproducible de backend, base de datos y servicios auxiliares cuando se incorpore esta fase. |
| Exposición | Nginx + HTTPS, cuando sea necesario | Reverse proxy, terminación TLS y configuración de despliegue. |
| Cache / servicios auxiliares | Redis, opcional | Cache, colas o control de frecuencia solo cuando exista una necesidad medible. |
| Futuro cliente móvil | Kotlin | Aplicación móvil que consumirá el mismo contrato REST/OpenAPI sin duplicar las reglas de negocio. |

Decisión importante: el sistema se desarrollará como un **monolito modular** en el backend. No se comenzará con microservicios. Los límites entre módulos deben ser claros, pero desplegar inicialmente un backend cohesivo garantiza transacciones ACID en operaciones multi-módulo críticas (matrícula y cupos, reemplazo de acudiente principal, cierres de periodo y promoción anual) y reduce complejidad operativa.

La SPA y el backend se desarrollan como aplicaciones separadas, con contratos explícitos entre ambas. Compartirán el contrato OpenAPI, no las entidades JPA ni las reglas de negocio internas.

# 3. Principios que guían el proyecto

- Construir por fases: analizar → diseñar → implementar → probar → revisar → documentar.
- No intentar implementar todo el sistema escolar de una sola vez.
- Priorizar reglas de negocio, seguridad e integridad de los datos antes que cantidad de pantallas.
- Mantener separación entre presentación Angular, contrato REST, lógica de negocio Spring Boot y persistencia PostgreSQL.
- Aplicar POO, SOLID, DRY, KISS y Clean Code de forma práctica, sin crear abstracciones innecesarias.
- La lógica de negocio crítica pertenece al backend. Angular puede validar para mejorar la experiencia de usuario, pero sus validaciones no sustituyen las validaciones del servidor.
- No conectar Angular directamente con PostgreSQL: el acceso debe pasar exclusivamente por el backend.
- Usar las Collections de Java en el sistema real; las estructuras de datos implementadas manualmente quedan como aprendizaje académico.
- Utilizar `BigDecimal` con escala y `RoundingMode` explícitos (provenientes del SIEE vigente) para todos los cálculos de ponderaciones, promedios académicos y porcentajes de inasistencia.
- Registrar mediante auditoría inmutable (`append-only`) las operaciones relevantes sobre información sensible o procesos académicos y de convivencia desde la Fase 1.
- Evitar eliminaciones físicas (`DELETE`) sobre cuentas, expedientes, estructuras académicas, matrículas, calificaciones, documentos probatorios o versiones normativas con historial; favorecer estados lógicos y versionamiento inmutable.
- Los errores visibles para el usuario deben ser claros y comprensibles; los detalles técnicos deben quedar en logs protegidos.
- Utilizar OpenAPI como contrato oficial de la API. Los tipos y el cliente Angular generados deben provenir de ese contrato y no escribirse de nuevo a mano cuando puedan generarse de forma fiable.
- Mantener el código generado separado del código manual y no editar manualmente los archivos generados.
- Aplicar CORS con orígenes explícitos; no abrir todos los orígenes por comodidad.
- No guardar JWT, tokens de acceso, refresh tokens ni credenciales en `localStorage` o `sessionStorage`.
- No confiar en Angular Router Guards como barrera de autorización. Spring Security debe validar las autorizaciones en cada solicitud protegida.
- Evitar agregar tecnologías solo por agregarlas: cada componente debe resolver una necesidad concreta.
- Mantener versiones y dependencias reproducibles, con archivos de bloqueo de npm y Maven cuando correspondan.
- No cambiar reglas académicas ni disciplinarias institucionales por suposiciones. Si una regla depende del PEI, el SIEE, el Manual de Convivencia u otra política institucional, debe parametrizarse y versionarse en el Módulo 06.

# 4. Módulos funcionales previstos

Los siguientes módulos constituyen el alcance funcional global del proyecto, alineados con la especificación V2 de los 12 módulos (`56 casos de uso`) y los módulos de evolución posterior. No todos deben implementarse en la primera iteración; se desarrollarán siguiendo las fases y dependencias descritas más adelante.

| Módulo | Casos de uso V2 | Alcance |
| --- | --- | --- |
| 01 · Seguridad y acceso | `UC-01`, `UC-PROV-01` a `UC-PROV-03` | Autenticación, cuentas nominativas sin cuentas compartidas (`RN-SEG-01`), conmutación de rol activo, acceso histórico restringido para egresados/retirados, roles con vigencia y membresías colegiadas, cambio y recuperación segura de acceso. |
| 02 · Estudiantes y acudientes | `UC-12`, `UC-06`, `UC-07`, `UC-PROV-04`, `UC-PROV-05` | Expediente personal (incluyendo documentos migrantes `PPT`/`PEP`/`NES`, EPS/RH y marcador confidencial PIAR), evolución documental (`RC -> TI -> CC`), reclamos Habeas Data (Ley 1581), único acudiente principal atómico, parámetro configurable de segundo acudiente/contacto, autorizaciones de retiro físico y bloqueos por restricción legal (`BLOQUEADO_RESTRICCION_LEGAL`). |
| 03 · Estructura académica | `UC-14`, `UC-PROV-06` a `UC-PROV-08` | Año lectivo (`EN_PLANEACION` a `CERRADO_HISTORICO`), calendario escolar de 40 semanas y días no lectivos, periodos ponderados al $100.00\%$, sedes, jornadas, niveles, secuencia de grados, grupos con `cupoMaximo`, Plan de Estudios (Áreas $\rightarrow$ Asignaturas con `IHS` y ponderación al $100.00\%$) y régimen cualitativo por Dimensiones en Preescolar. |
| 04 · Matrícula | `UC-13`, `UC-PROV-09`, `UC-PROV-10` | Formalización de matrícula nueva, renovación (coherente con promoción previa) y traslado entrante, descuento atómico de cupos, aceptación del PEI/SIEE/Manual, novedades en curso (cambio de grupo, promoción anticipada, retiro/traslado saliente, deserción, cancelación en firme) e historial/listados por grupo. |
| 05 · Profesores y asignaciones docentes | `UC-15`, `UC-PROV-11`, `UC-PROV-12` | Expediente maestro docente, verificación obligatoria de inhabilidades (Ley 1918 de 2018), estados administrativos (licencias/incapacidades), asignación de carga por `IHS` y horas extras, bloques unidocentes en Preescolar/Primaria, Dirección de Grupo titular/suplente y relevos sin perder autoría histórica. |
| 06 · Configuración institucional | `UC-26`, `UC-PROV-13`, `UC-PROV-14` | Versionamiento inmutable y aprobación con actas colegiadas del SIEE (escalas, equivalencia nacional, `BigDecimal`, categorías, fórmulas de recuperación, promoción y plazos) y del Manual de Convivencia (situaciones Tipo I-III, debido proceso, medidas y excusas); datos legales del colegio (DANE, NIT, resoluciones y firmas), Política de Datos Personales y parámetros operativos transversales. |
| 07 · Calificaciones y evaluación | `UC-16` a `UC-18`, `UC-02`, `UC-08`, `UC-PROV-15` a `UC-PROV-17` | Planeación progresiva de actividades (Básica/Media, Preescolar cualitativo, autoevaluación y PIAR), registro por lote atómico, cálculo jerárquico `BigDecimal` hasta Área Curricular y Escala Nacional, evaluaciones diferidas por excusa, nivelaciones con principio de no desmejoramiento, reclamaciones en 2 instancias, convalidación por traslado/promoción anticipada y sábanas grupales. |
| 08 · Asistencia | `UC-19`, `UC-03`, `UC-09`, `UC-PROV-18`, `UC-PROV-19` | Registro por sesión/asignatura o jornada diaria sobre sesiones válidas sin días no lectivos, detección inmediata de evasión interna, radicación y resolución de excusas con reserva de soportes médicos (Ley 1581) y propagación a `UC-17`/`UC-18`, control de salidas anticipadas verificando autorizados de retiro físico, consolidados grupales y protocolo contra deserción. |
| 09 · Horarios | `UC-04`, `UC-10`, `UC-PROV-20` a `UC-PROV-22` | Catálogo de aulas/espacios físicos y plantillas de franjas y descansos por jornada, estructuración y publicación versionada de horarios garantizando Triple No-Colisión (grupo, docente y aula) y cuadre exacto de `IHS`, cambios de aula/permutas/suplencias y consulta para estudiantes, acudientes, docentes y ubicación en tiempo real. |
| 10 · Cierre académico y promoción | `UC-20`, `UC-21`, `UC-PROV-23`, `UC-PROV-24` | Comisiones de evaluación periódicas y observaciones de dirección de grupo, validador de pre-cierre y cierre/reapertura de periodos con boletines versionados (incluyendo boletín aplazado por incapacidad), actas de promoción anual, habilitaciones finales, no reprobación en Preescolar, Promoción Anticipada (Art. 7 Decreto 1290), requisitos de graduación de Media (Servicio Social e ICFES Saber 11) y cierre del año lectivo. |
| 11 · Convivencia escolar | `UC-22` a `UC-25`, `UC-PROV-25`, `UC-PROV-26` | Observador del Estudiante bilateral con adendas inmutables, recepción y clasificación de situaciones Tipo I, II y III (Ley 1620 / Decreto 1965), activación de la Ruta de Atención Integral, reporte SIUCE y reserva frente a agresor intrafamiliar, instrucción del debido proceso (cargos, pruebas, descargos y recursos de reposición/apelación), medidas y sanciones con Plan de Continuidad Académica (`RN-08`), actas del Comité Escolar de Convivencia, seguimiento externo y consulta anonimizada. |
| 12 · Documentos, reportes y auditoría | `UC-05`, `UC-11`, `UC-27`, `UC-PROV-27` a `UC-PROV-29` | Consulta y descarga segura con doble validación (incluyendo modo histórico de egresados), solicitud y expedición en servidor de certificados de estudio (anuales y parciales por retiro), constancias y actas con código de verificación y versionamiento, carga y clasificación documental de 5 niveles con TRD (Ley 594), reportes institucionales/SIMAT/DANE/SIUCE y gobierno de auditoría `append-only` con verificación criptográfica SHA-256. |
| Comunicación *(Evolución — Fase 7)* | *Por especificar en Fase 7* | Noticias institucionales, comunicados masivos, mensajería bidireccional y gestión de preferencias (en V1 las notificaciones operan como servicio transversal de soporte de los módulos 01 a 12). |
| Finanzas *(Evolución — Fase 8)* | *Por especificar en Fase 8* | Pensiones, pagos, facturas, cartera, becas y descuentos como módulo de evolución posterior a la V1 académica-administrativa (respetando gratuidad en colegios oficiales). |

Los módulos deben exponer casos de uso bien definidos. No se deben construir dependencias circulares entre módulos ni permitir que la interfaz web se convierta en el lugar donde se deciden las reglas institucionales.

# 5. Roles previstos, asignaciones contextuales y órganos colegiados

| Rol de sistema (RBAC) | Funciones principales previstas |
| --- | --- |
| `ADMINISTRADOR` | Administración de cuentas, roles, contraseñas, parámetros operativos técnicos, soporte de aulas/repositorio documental y gobierno/integridad de auditoría técnica y de seguridad (sin alterar notas ni acceder a expedientes clínicos o disciplinarios reservados). |
| `RECTOR` | Dirección y aprobación institucional: roles/membresías colegiadas, excepciones, estructura y cargas/horas extras, publicación oficial de SIEE/Manual/Política de Datos/firmas, horarios, cierres/reaperturas, promoción, graduación, presidencia de convivencia y sanciones/recursos, firma de certificados y exportación regulada de auditoría. |
| `COORDINADOR_ACADEMICO` | Gestión de estructura académica, cupos, plan de estudios, asignaciones y suplencias docentes, borrador del SIEE, convalidaciones, 2.ª instancia de reclamos de notas, sábanas, excusas, horarios/aulas, comisiones periódicas y anuales, cierre de periodos, promoción, verificación de graduación y reportes académicos. |
| `COORDINADOR_CONVIVENCIA` | Borrador del Manual de Convivencia, validación de restricciones legales, atención de evasiones/salidas anticipadas y ausentismo, Observador, Ruta de Atención Integral (Tipo I-III), instrucción del debido proceso disciplinario, medidas de su competencia, Comité Escolar de Convivencia, SIUCE y reportes de convivencia. |
| `PROFESOR` | Gestión de actividades evaluativas, calificaciones, nivelaciones, 1.ª instancia de reclamaciones, asistencia, observador y consulta de horario propio en sus cursos asignados (`UC-15`). Incluye la asignación contextual anual de **`Director de grupo`** (`UC-15 A1`) que habilita seguimiento integral (ficha escolar, sábanas de notas, consolidado de asistencia, observaciones de boletín y comisiones) exclusivamente sobre su grupo asignado. |
| `ORIENTADOR_ESCOLAR` | Valoración psicosocial, mediación escolar, acompañamiento en salud mental (Leyes 1620/2013, 2383/2024 y 2503/2025) y participación en el Comité Escolar de Convivencia bajo reserva profesional, sin imponer sanciones disciplinarias ni alterar notas. |
| `SECRETARIA` | Aprovisionamiento limitado de cuentas de estudiantes/acudientes, expedientes personales, rectificaciones Habeas Data, vínculos/retiros físicos, matrícula y novedades, apoyo docente/graduación, expedición de constancias y certificados, gestión documental y reportes administrativos/SIMAT. |
| `ESTUDIANTE` | Autenticación según nivel habilitado (`RN-SEG-09`) o en modo histórico (`INACTIVA_HISTORICA`), consultas propias (notas, asistencia, horario, convivencia, documentos), autoevaluación, reclamaciones de notas, versión/descargos en convivencia y solicitud de certificados. |
| `ACUDIENTE` | Consulta de estudiantes con vínculo activo, verificado y libre de restricción legal (`UC-PROV-05`), peticiones de rectificación Habeas Data, reclamaciones académicas, excusas de inasistencia, acompañamiento en descargos/recursos y solicitud/descarga de documentos. |

Consideraciones clave de diseño de autorización:

- **Multi-rol con rol activo único (`RN-SEG-04`):** En instituciones pequeñas, una misma persona puede desempeñar varios roles compatibles (por ejemplo, `COORDINADOR_ACADEMICO` y `COORDINADOR_CONVIVENCIA`, o `PROFESOR` y `ACUDIENTE` de su hijo). La persona conserva una única cuenta y conmuta su rol activo en sesión (`UC-01 A2`) sin acumular privilegios incompatibles por petición.
- **Órganos colegiados sin cuentas compartidas (`RN-SEG-01`):** La **Comisión de Evaluación y Promoción**, el **Comité Escolar de Convivencia**, el **Consejo Académico** y el **Consejo Directivo** nunca se modelan como usuarios genéricos con contraseñas compartidas. Sus integrantes operan con sus cuentas personales nominativas mediante membresías registradas (`UC-PROV-02 A2`), y sus decisiones se formalizan registrando el número de acta, fecha, quórum, asistentes nominativos y soporte firmado.
- **RBAC + Autorización Contextual:** Los permisos reales se definen por caso de uso y se validan en Spring Security y en los servicios de dominio comprobando el contexto real del recurso (vínculo acudiente-estudiante sin restricción legal, vigencia de matrícula, asignación docente vigente o dirección de grupo, estado del periodo y clasificación documental).
- Angular puede ocultar navegación, botones y opciones según el perfil para mejorar la experiencia de usuario. Eso es una medida de presentación, no de seguridad. La autorización definitiva debe ejecutarse en Spring Security y en la capa de aplicación del backend.

# 6. Arquitectura del backend

## 6.1. Flujo conceptual

**Angular SPA** → HTTP/HTTPS + JSON, conforme al **contrato OpenAPI** → **CORS / Spring Security / autenticación y autorización contextual** → **Controller REST** → **Service / caso de uso** → **Domain Services / Entidades** → **Repository / JPA** → **PostgreSQL**.

Angular consume los endpoints mediante un cliente TypeScript generado a partir del contrato OpenAPI. Los componentes de interfaz no deben construir SQL ni acceder directamente a repositorios; tampoco deben reproducir cálculos académicos críticos ni construir PDFs oficiales con cálculos locales.

## 6.2. Organización interna recomendada

El backend debe organizarse por dominio funcional dentro del monolito modular. La siguiente separación de responsabilidades se aplica dentro de los módulos en los que resulte útil:

- `controller`: endpoints HTTP y traducción entre protocolo y caso de uso; no concentra reglas complejas.
- `service` / `application`: casos de uso, coordinación del flujo transaccional y reglas de negocio.
- `domain` *(cuando la complejidad lo amerite)*: motores de reglas puros (`MotorCalculoSieeDomainService`, `ValidadorConflictosHorarioDomainService`, `EvaluadorPromocionSieeDomainService`, `ProyectorConfidencialConvivenciaService`).
- `repository`: acceso a datos mediante JPA.
- `entity`: entidades persistentes y sus relaciones.
- `dto`: objetos de entrada/salida; no exponer las entidades JPA directamente como contrato público.
- `mapper`: conversión explícita entre DTO y entidades o modelos internos.
- `exception`: excepciones de dominio y manejo global de errores.
- `security`: autenticación, autorización, RBAC, autorización contextual, tratamiento de tokens/cookies y protección de endpoints.
- `config`: configuración del sistema, CORS, OpenAPI, Jackson, persistencia y componentes transversales.
- `audit`: registro inmutable `append-only` de acciones sensibles y trazabilidad con hash encadenado.

## 6.3. Organización modular conceptual

Paquete raíz de ejemplo: `jackren.sistemaacademico.main` (respetar el paquete real existente salvo que se apruebe una migración explícita).

Módulos de dominio previstos (alineados con los 12 módulos `v2`):

- `seguridad` (Módulo 01: cuentas, roles, membresías colegiadas, sesiones y contraseñas)
- `estudiante` y `acudiente` (Módulo 02: expediente, evolución documental, reclamos ARCO, vínculos, retiros físicos y restricciones legales)
- `estructura` / `curso` / `asignatura` (Módulo 03: año lectivo, calendario/días no lectivos, periodos, sedes, jornadas, grados, grupos con cupos y plan de estudios)
- `matricula` (Módulo 04: matrícula, novedades de permanencia e historial)
- `profesor` (Módulo 05: expediente docente, inhabilidades Ley 1918, asignaciones, dirección de grupo y suplencias)
- `configuracion` (Módulo 06: versiones inmutables de SIEE, Manual de Convivencia, Política de Datos y datos legales/firmas del colegio)
- `calificacion` (Módulo 07: actividades, notas, cálculo jerárquico hasta áreas, nivelaciones, reclamaciones, convalidaciones y sábanas)
- `asistencia` (Módulo 08: sesiones de asistencia, excusas con reserva médica, salidas anticipadas y seguimiento contra deserción)
- `horario` (Módulo 09: aulas, plantillas de franjas, horarios versionados sin colisión y novedades)
- `cierre` / `promocion` (Módulo 10: comisiones periódicas, cierres/boletines, promoción anual/anticipada, habilitaciones finales y graduación de Media)
- `convivencia` (Módulo 11: observador, Ruta de Atención Tipo I-III, debido proceso/descargos/recursos, medidas con continuidad académica y Comité Escolar de Convivencia)
- `documento`, `reporte` y `auditoria` (Módulo 12: expedición de certificados/constancias, repositorio documental clasificado, reportes institucionales/SIMAT/SIUCE y gobierno de auditoría `append-only`)

Los paquetes exactos pueden evolucionar, pero los límites de responsabilidad deben mantenerse. Antes de reorganizar el código ya existente, revisar referencias, pruebas y migraciones y proponer un cambio incremental.

## 6.4. Contrato OpenAPI y cliente Angular

- El contrato versionado OpenAPI 3.x será la fuente de verdad del contrato HTTP. Se mantendrá, por ejemplo, en `openapi/openapi.yaml`.
- El contrato debe definir rutas, métodos, parámetros, esquemas, DTO, campos obligatorios/opcionales, formatos, códigos HTTP (derivados directamente de las tablas `A#` y `E#` de los 12 módulos `v2`), respuestas de error, seguridad requerida y ejemplos relevantes.
- El backend deberá mantenerse conforme al contrato. Si se utiliza `springdoc-openapi` para exponer documentación en ejecución, esa especificación deberá verificarse frente al contrato versionado para evitar dos fuentes contradictorias.
- OpenAPI Generator, con el generador `typescript-angular`, producirá los tipos y el cliente HTTP consumido por la SPA.
- El código generado se ubicará en una carpeta aislada, por ejemplo `src/app/core/api/generated/`, y no debe editarse manualmente. Se debe poder regenerar de forma reproducible.
- Los DTO Java seguirán existiendo en el backend y deben respetar el contrato; generar el cliente TypeScript no significa que las clases Java desaparezcan ni que la base de datos dicte directamente el contrato público.
- Los cambios incompatibles deben identificarse explícitamente, revisarse y documentarse. No se debe cambiar un endpoint o una propiedad pública solo para resolver una necesidad local de la UI sin analizar su impacto en otros clientes.

# 7. Base de datos

PostgreSQL será la base de datos principal. El modelo se diseñará antes de implementar gran parte de la lógica, pero evolucionará mediante migraciones versionadas y revisadas conforme avance el proyecto.

Requerimientos técnicos:

- Primary Keys y Foreign Keys adecuadas.
- Constraints para proteger la integridad, incluidas restricciones de unicidad parciales (`UNIQUE`) y de obligatoriedad cuando correspondan.
- Índices cuando exista una necesidad real de consulta o rendimiento.
- Normalización y relaciones bien definidas; las relaciones muchos-a-muchos con atributos o reglas propias deben modelarse mediante entidades intermedias explícitas.
- Transacciones ACID para operaciones que deban ser atómicas, acompañadas de control de concurrencia optimista (`@Version`) o pesimista donde existan condiciones de carrera.
- Migraciones versionadas mediante Flyway (o Liquibase; elegir una sola herramienta y utilizarla consistentemente).
- No depender exclusivamente de validaciones Java para proteger la integridad: la base de datos debe defender sus invariantes.
- No exponer PostgreSQL directamente al navegador ni al cliente futuro.
- Separar la configuración de desarrollo, pruebas y despliegue.
- No mantener credenciales reales dentro del repositorio; utilizar variables de entorno o un gestor de secretos.
- No utilizar `ddl-auto=update` como sustituto permanente de migraciones controladas. En entornos de despliegue, el esquema debe cambiar de forma explícita y reproducible.
- Definir política de copias de seguridad, retención y pruebas de restauración antes de manejar datos institucionales reales.

Relaciones conceptuales consolidadas:

- `Persona` (identidad única) $\rightarrow$ `CuentaUsuario` $\rightarrow$ `AsignacionRolVigente` / `MembresiaOrganoColegiado`.
- `Estudiante` $\rightarrow$ `EstudianteAcudiente` $\rightarrow$ `Acudiente` (con parentesco, representación legal, custodia, indicador único de acudiente principal, autorización de retiro físico, prioridad de emergencia y estado de restricción legal).
- `Institucion` $\rightarrow$ `VersionSiee` / `VersionManualConvivencia` / `VersionPoliticaDatos` (versiones inmutables).
- `AnioLectivo` $\rightarrow$ `PeriodoAcademico` / `DiaNoLectivo` y `Sede` / `Jornada` $\rightarrow$ `Grado` $\rightarrow$ `Grupo` (con `cupoMaximo`) y `PlanEstudiosGrado` (`Area` $\rightarrow$ `Asignatura` con `IHS` y `pesoPorcentualEnArea`, o `DimensionPreescolar`).
- `Estudiante` $\rightarrow$ `Matricula` (`HistorialNovedadMatricula`) $\rightarrow$ `Grupo` / `AnioLectivo`.
- `Profesor` $\rightarrow$ `AsignacionDocente` (titular, suplente o Director de Grupo).
- `Grupo` + `AsignacionDocente` + `AulaEspacioFisico` + `FranjaHoraria` $\rightarrow$ `VersionHorarioGrupo` / `BloqueHorarioClase` / `NovedadHorarioSesion`.
- `Matricula` $\rightarrow$ `CalificacionActividad` / `ConsolidadoPeriodoAsignatura` / `ConsolidadoPeriodoArea` / `ActaNivelacion` / `ReclamacionCalificacion` / `ActaConvalidacion`.
- `Matricula` $\rightarrow$ `RegistroAsistenciaEstudiante` / `SolicitudExcusaInasistencia` / `AutorizacionSalidaAnticipada` / `ExpedienteSeguimientoAusentismo`.
- `Matricula` $\rightarrow$ `BoletinPeriodoEmitido` / `DecisionPromocionMatricula` / `ActaGraduacionEstudiante`.
- `Matricula` $\rightarrow$ `AnotacionObservador` / `CasoConvivencia` $\rightarrow$ `ExpedienteDebidoProceso` $\rightarrow$ `ResolucionMedidaConvivencia` / `ActaComiteConvivencia`.
- `DocumentoInstitucional` (clasificado en 5 niveles de confidencialidad con SHA-256) y `RegistroEventoAuditoria` (`append-only` con hash encadenado SHA-256).

Invariantes de integridad formalizadas en la especificación V2 (que deben reforzarse en servicio y en PostgreSQL):

- Todo estudiante menor de edad activo debe contar en todo momento con **exactamente un (1) acudiente principal activo y verificado** (protegido con índice único parcial en PostgreSQL y reemplazo atómico en `UC-PROV-05`); la exigencia de un segundo acudiente o contacto adicional de emergencia se parametriza institucionalmente (`RN-EST-05`) con excepción documentada (`UC-12 A5`).
- No puede existir más de una matrícula activa o en proceso incompatible por estudiante y año lectivo (`RN-MAT-01`), ni sobrecupo por encima del `cupoMaximo` del grupo (`RN-MAT-05`).
- En Horarios rige la invariante de **Triple No-Colisión** (cero solapamiento de Grupo, Docente o Aula en el mismo día/franja) y cuadre exacto con la `IHS` del Plan de Estudios (`RN-HOR-01`, `RN-HOR-02`).
- En Asistencia existe un único registro por `(sesionAsistencia, matricula)` (`UC-19`), y el denominador excluye días no lectivos (`UC-14 A4`) y fechas fuera de vigencia de la matrícula.
- Prohibición de eliminaciones físicas que rompan trazabilidad académica, convivencial, documental o de auditoría (`DEC-DOM-03`, Ley 594 de 2000).
- Normalización consistente y unicidad controlada de `(tipoDocumento, numeroDocumentoNormalizado)` conservando el historial cuando el documento evoluciona por edad o regularización migratoria (`UC-PROV-04 A1`).

# 8. Reglas de negocio importantes

El sistema debe modelar reglas explícitas, no limitarse a guardar datos. Las reglas se validarán en el backend y, cuando corresponda, se reforzarán mediante restricciones y transacciones de base de datos. El catálogo completo de las 12 reglas transversales (`RN-01` a `RN-12`) y las 12 familias de reglas modulares (`RN-SEG` a `RN-DOC`) reside en `00_Guia_General_y_Reglas_Transversales_v2.docx`.

Reglas generales clave:

- Una calificación debe encontrarse dentro de la escala permitida por la versión del SIEE vigente del periodo y calcularse con `BigDecimal` y `RoundingMode` explícitos.
- Un estudiante debe tener una matrícula válida (`MATRICULADO_ACTIVO` en la fecha del evento) para recibir calificaciones y registros de asistencia; traslados entrantes, cambios de grupo y retiros se respetan por fecha efectiva (`RN-CAL-06`, `RN-ASI-02`).
- Un profesor (titular o suplente activo) debe tener asignación vigente en `UC-15` sobre el curso/grupo y asignatura antes de registrar o modificar actividades, calificaciones o asistencias.
- Un periodo académico puede estar abierto o cerrado. Las modificaciones posteriores al cierre requieren reapertura excepcional autorizada, motivo obligatorio, auditoría reforzada y regeneración versionada de boletines (`UC-20 A3`).
- El sistema debe detectar y bloquear conflictos de horario entre profesor, grupo y aula (`RN-HOR-01`).
- Las modificaciones de información sensible deben quedar auditadas en una bitácora `append-only` verificable criptográficamente (`RN-10`, `UC-PROV-29`).
- Los permisos y el alcance contextual (incluyendo el bloqueo inmediato por `BLOQUEADO_RESTRICCION_LEGAL` en `UC-PROV-05 A3` y `RESERVA_PROTECCION_CONTRA_ACUDIENTE` en `UC-23 A4`) deben validarse siempre en backend.
- Las operaciones que actualizan varias entidades relacionadas deben ser transaccionales y definir el comportamiento ante errores parciales (ej. rechazo atómico por lote en `UC-17 E1`).
- Los errores por duplicados, estados inválidos o conflictos deben convertirse en respuestas HTTP controladas (`400`, `401`, `403`, `404`, `409`, `429`, `500`), nunca filtrarse como excepciones técnicas.

Reglas académicas y de convivencia institucionales:

- No fijar una nota mínima, ponderación, regla de redondeo, número de periodos, fórmula de recuperación, umbral de asistencia, criterio de promoción ni duración de sanción como una constante universal en código. Todo parámetro institucional proviene de una versión aprobada e inmutable del Módulo 06 (`UC-26`, `UC-PROV-13`).
- **Nivel Preescolar (Decreto 2247 de 1997):** Evaluación cualitativa/descriptiva por Dimensiones del Desarrollo, ingreso sin examen de admisión y prohibición absoluta de reprobación de grados o actividades (`RN-EST-ACA-04`, `RN-CAL-02`, `RN-CIE-05`).
- **Educación Inclusiva (Decreto 1421 de 2017):** Soporte para el marcador confidencial PIAR y evaluación con ajustes razonables sin divulgar diagnósticos clínicos (`RN-EST-08`, `RN-CAL-08`).
- **Promoción y Asistencia (`RN-05`):** Ninguna alerta de inasistencia o bajo desempeño se convierte en reprobación automática sin la decisión colegiada registrada en Acta de la Comisión de Evaluación y Promoción (`UC-21`).
- **Convivencia y Debido Proceso (`RN-06`, `RN-07`, `RN-08`):** Situación Tipo I, II o III activa la Ruta de Atención Integral (Ley 1620 / Decreto 1965), nunca una sanción automática; toda sanción exige cargos, traslado de pruebas, descargos, proporcionalidad, recursos (`UC-PROV-25`) y Plan de Continuidad Académica (Sentencia T-004 de 2026) que prohíbe notas punitivas o privación del derecho a ser evaluado.

Marco colombiano: las reglas funcionales se sustentan en la Ley 115 de 1994, el Decreto Único Reglamentario 1075 de 2015 (compilatorio de los Decretos 1290 de 2009, 2247 de 1997, 1850 de 2002, 1965 de 2013 y 1421 de 2017), la Ley 1620 de 2013 (y Leyes 2383 de 2024 y 2503 de 2025), la Ley 1098 de 2006, la Ley 1581 de 2012 y Decreto 1377 de 2013, la Ley 1918 de 2018, la Ley 594 de 2000 y la Sentencia T-004 de 2026. Este documento orienta el diseño de software y no sustituye la validación jurídica del PEI, SIEE y Manual de Convivencia de cada establecimiento.

# 9. Calificaciones y proceso académico

Flujo conceptual consolidado:

**Estudiante → Matrícula (y novedades) → Curso/Grupo → Plan de Estudios (Área → Asignatura con IHS / o Dimensión Preescolar) → Periodo → Categorías SIEE y Actividades → Calificaciones (`BigDecimal`) / Excusas diferenciadas / Nivelaciones (No Desmejoramiento) / Convalidaciones → Definitiva de Asignatura → Definitiva Ponderada de Área → Escala Nacional → Cierre de Periodo y Boletín Versionado → Comisión de Evaluación y Promoción (Acta) → Promoción / Habilitación Final / Repitencia / Promoción Anticipada / Graduación (11.°).**

El sistema contempla de forma explícita en los Módulos 07 y 10:

- Planeación progresiva de actividades evaluativas (tope $\le 100.00\%$ durante el periodo y cierre exacto al $100.00\%$ para consolidar, `RN-CAL-01`), incluyendo heteroevaluación, autoevaluación, coevaluación, dimensiones en Preescolar y ajustes PIAR.
- Cálculo determinista con `BigDecimal` de promedios por categoría, definitiva de asignatura y definitiva ponderada del Área Curricular (`UC-PROV-07 A2`, `RN-CAL-03`).
- Escala institucional de valoración mapeada exhaustivamente y sin traslapes a la Escala Nacional (`Superior`, `Alto`, `Básico`, `Bajo`) conforme a la versión vigente del SIEE (`UC-26`).
- Estado `PENDIENTE_POR_EXCUSA_JUSTIFICADA` articulado con `UC-PROV-18` para pruebas supletorias y boletín individual `APLAZADO_POR_EXCUSA_JUSTIFICADA` al cierre de periodo (`UC-20 A1`).
- Estrategias de apoyo, nivelaciones de periodo y habilitaciones finales (`UC-18`) aplicando la fórmula del SIEE (`REEMPLAZO_TOTAL`, `TOPE_BASICO` o `PROMEDIO_PONDERADO`) bajo la invariante de **no desmejoramiento** ($\text{NotaVigente} \ge \text{NotaOriginal}$) y conservando siempre la nota original y el Acta de Nivelación.
- Reclamaciones académicas en 1.ª instancia (Docente) y 2.ª instancia (Coordinador Académico) dentro de los plazos del SIEE (`UC-PROV-15`).
- Convalidación/homologación de periodos previos por traslado entrante (`UC-13 A5`) o Promoción Anticipada (`UC-PROV-09 A2`) en `UC-PROV-16`.
- Sábanas grupales de calificaciones y tablero de completitud docente (`UC-PROV-17`), comisiones periódicas (`UC-PROV-23`), cierre/reapertura con boletines versionados (`UC-20`), actas de promoción y habilitación final (`UC-21`), y verificación de Servicio Social e ICFES Saber 11 para graduación de Grado 11.° (`UC-PROV-24`).

Evitar expresiones dispersas como `nota >= 3.0` en distintos servicios. Toda lógica de cálculo y mapeo a escala nacional se encapsula en el motor de reglas versionado (`MotorCalculoSieeDomainService`) con pruebas automatizadas por estrategia del SIEE.

# 10. Seguridad

La seguridad se implementará principalmente en Spring Boot mediante Spring Security. Angular tendrá controles complementarios de experiencia de usuario, pero nunca será la autoridad final de autenticación o autorización.

## 10.1. Autenticación, autorización y contraseñas

- Implementar autenticación con una estrategia basada en tokens/sesión adecuada a la SPA y al despliegue. Documentar la decisión de arquitectura y sus amenazas.
- Aplicar RBAC, rol activo único por petición (`RN-SEG-04`) y permisos específicos por caso de uso (`UC-01`, `UC-PROV-01` a `UC-PROV-03`).
- Verificar en backend tanto el rol como el acceso al recurso concreto (autorización contextual); no basta con comprobar que un usuario tenga un rol general.
- Utilizar hashing de contraseñas diseñado para contraseñas, como BCrypt o Argon2, mediante bibliotecas mantenidas.
- No almacenar contraseñas en texto plano ni utilizar SHA-256 simple como función de almacenamiento de contraseñas.
- Validar las entradas en servidor y no confiar en los datos enviados por la SPA.
- Implementar cierre de sesión, expiración, revocación forzosa inmediata en servidor (ante bloqueo de cuenta, cambio de contraseña, revocación de rol o medida de restricción legal `RN-SEG-06`) y activación obligatoria con cambio de contraseña temporal y consentimiento de Ley 1581 en primer ingreso (`UC-01 A1`).
- Proteger procesos de recuperación de acceso (`UC-PROV-03`) en tiempo constante y evitar revelar si una cuenta existe.
- No registrar contraseñas, hashes, JWT, refresh tokens, cookies de sesión, secretos ni datos personales innecesarios en logs o auditoría.

## 10.2. Almacenamiento de JWT y protección contra XSS

**Prohibido:** guardar JWT, tokens de acceso, refresh tokens, identificadores de sesión u otras credenciales en `localStorage` o `sessionStorage`. Todo JavaScript que se ejecute en el mismo origen puede acceder a esos almacenamientos; una vulnerabilidad XSS podría robar los tokens.

La estrategia preferida para el cliente web será autenticación mediante una cookie de autenticación con atributos adecuados:

- `HttpOnly`, para que el JavaScript del navegador no pueda leer directamente la cookie de autenticación.
- `Secure` en entornos HTTPS y producción.
- `SameSite=Lax` o `Strict` según los flujos de navegación que deban admitirse; usar `None` únicamente cuando exista una necesidad cross-site real y siempre con `Secure`.
- Alcance de dominio y ruta lo más limitado posible; evaluar el prefijo `__Host-` cuando las condiciones de despliegue lo permitan.
- Expiración, renovación y revocación definidas explícitamente.

Una alternativa aceptable cuando se decida utilizar el encabezado `Authorization: Bearer` es mantener el token de acceso únicamente **en memoria** durante la sesión activa de la SPA, sin persistirlo en almacenamiento web. Se deben documentar las implicaciones de renovación, recarga de página, cierre de sesión y XSS. Los refresh tokens no deben trasladarse a `localStorage` o `sessionStorage`.

La cookie `HttpOnly` reduce el riesgo de extracción del token por JavaScript, pero **no elimina el riesgo de XSS** ni evita por sí sola que un script malicioso realice operaciones desde la sesión de la víctima. Se deben aplicar defensas XSS en Angular:

- Preferir interpolación y bindings seguros del framework.
- No insertar HTML no confiable directamente en el DOM.
- Evitar `innerHTML` y APIs que omiten la sanitización, salvo justificación y revisión de seguridad.
- No utilizar `bypassSecurityTrust...` como solución rutinaria.
- Sanitizar contenido HTML enriquecido si el producto necesita aceptarlo.
- Definir Content Security Policy (CSP) apropiada para el despliegue y evitar scripts inline innecesarios.
- Mantener dependencias actualizadas y revisar bibliotecas externas.
- No insertar información sensible en URLs, mensajes de error del navegador o logs.

## 10.3. CSRF y solicitudes autenticadas mediante cookies

Cuando el navegador envía automáticamente una cookie de autenticación, el backend debe aplicar una defensa CSRF adecuada. No se debe deshabilitar CSRF sin analizar el mecanismo de autenticación, el origen de las solicitudes y las consecuencias.

- Configurar Spring Security con la estrategia CSRF apropiada para una SPA.
- Utilizar el patrón de token CSRF compatible con Spring Security (por ejemplo, token sincronizado o patrón double-submit correctamente implementado, según la arquitectura elegida).
- Si Angular necesita leer un token CSRF desde una cookie para enviarlo en un encabezado, esa cookie CSRF puede requerir ser legible por JavaScript; esto no significa que la cookie de autenticación deba dejar de ser `HttpOnly`.
- Implementar un interceptor Angular o una integración explícita para adjuntar el token CSRF a métodos que modifican estado, con exclusión de destinos externos no confiables.
- Validar `Origin`/`Referer` cuando sea apropiado como defensa adicional.
- No confiar únicamente en `SameSite`; utilizarlo como defensa en profundidad según la configuración del despliegue.
- Ningún endpoint de lectura (`GET`, `HEAD`) debe modificar el estado del servidor.
- Probar expresamente que las solicitudes de modificación sin token CSRF válido sean rechazadas cuando la arquitectura requiera CSRF.

Si se elige un modelo exclusivamente bearer-token en encabezado y no se envían credenciales automáticamente, el análisis CSRF será diferente. Esa elección debe quedar documentada y revisada; no se debe mantener una configuración híbrida accidental.

Referencias: [Spring Security — CSRF](https://docs.spring.io/spring-security/reference/servlet/exploits/csrf.html) y [OWASP — CSRF Prevention Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Cross-Site_Request_Forgery_Prevention_Cheat_Sheet.html).

## 10.4. CORS en Spring Security

La SPA de desarrollo normalmente se servirá desde `http://localhost:4200` y el backend en otro origen, por ejemplo `http://localhost:8080`. Se debe configurar explícitamente CORS en el backend.

- Añadir la configuración de CORS mediante Spring Security y una `CorsConfigurationSource` o el mecanismo equivalente recomendado para la versión elegida.
- Procesar CORS antes de las comprobaciones de Spring Security que podrían rechazar solicitudes preflight `OPTIONS`.
- Permitir explícitamente `http://localhost:4200` en desarrollo y los dominios exactos de frontend autorizados en cada entorno.
- Definir métodos HTTP y encabezados permitidos de forma explícita, incluyendo los que use el contrato/API, el encabezado de autorización si aplica y el encabezado CSRF si aplica.
- Exponer únicamente los encabezados de respuesta que necesite Angular.
- Si se utilizan cookies/credenciales, habilitar credenciales CORS solo para orígenes explícitos. No combinar `allowCredentials=true` con un origen comodín `*`.
- No confiar en CORS como sustituto de autenticación o autorización: CORS es una política de navegador, no una barrera de acceso al backend para todos los tipos de clientes.
- Separar las listas de orígenes por entorno. No permitir cualquier subdominio ni reflejar automáticamente el encabezado `Origin` de la solicitud.
- Probar solicitudes permitidas, orígenes rechazados y preflight.

Referencia: [Spring Security — CORS](https://docs.spring.io/spring-security/reference/servlet/integrations/cors.html).

## 10.5. Interceptores y guards de Angular

- Utilizar interceptores HTTP funcionales de Angular cuando sean compatibles con la versión seleccionada para centralizar aspectos transversales: autenticación cuando corresponda, CSRF, tratamiento de errores y estados globales de petición.
- El interceptor nunca debe adjuntar tokens o credenciales a un destino arbitrario; las credenciales se adjuntarán únicamente a solicitudes de la API de confianza.
- Gestionar respuestas `401` sin entrar en bucles de redirección; limpiar el estado de autenticación en memoria y dirigir al inicio de sesión cuando corresponda.
- Gestionar `403` como falta de permisos, sin tratarlo automáticamente como sesión expirada.
- Implementar Angular Router Guards para proteger rutas, mejorar la navegación y evitar mostrar vistas que el usuario no necesita ver.
- Los guards consultarán el estado de sesión/roles proporcionado por un servicio de autenticación y redirigirán de forma controlada.
- Los guards y los elementos ocultos de la interfaz nunca reemplazan la autorización de Spring Security.
- Evitar guardar una segunda copia independiente de roles/permisos en múltiples componentes; centralizar la información de sesión en un servicio o estado coherente.

Referencias: [Angular — HTTP interceptors](https://angular.dev/guide/http/interceptors) y [Angular — Route guards](https://angular.dev/guide/routing/route-guards).

## 10.6. Controles adicionales

- HTTPS en producción y entornos que atraviesen redes no confiables.
- Auditoría `append-only` de operaciones sensibles sin incluir credenciales ni secretos.
- Control de acceso reforzado y URLs firmadas efímeras para los archivos generados, documentos y reportes (`UC-05`, `UC-11`, `UC-PROV-28`).
- Límites razonables de tamaño (cargas de archivo) y frecuencia (`rate limiting` en login `UC-01` y exportaciones `UC-27`), según necesidades reales.
- Encabezados de seguridad HTTP y CSP apropiados al despliegue.
- Secretos fuera de Git; usar variables de entorno o un gestor de secretos.
- Revisión de dependencias y vulnerabilidades conocidas.
- Pruebas negativas de acceso anónimo, rol insuficiente, acceso horizontal entre estudiantes/grupos, vínculo revocado o con restricción legal, token vencido, token inválido, CSRF inválido y origen CORS no autorizado.

# 11. Manejo de errores y experiencia del usuario

El usuario final no debería recibir excepciones técnicas incomprensibles. El backend debe clasificar los errores y devolver respuestas consistentes (alineadas con los flujos de excepción `E#` de los 12 módulos `v2`); Angular debe traducirlos a mensajes claros sin exponer trazas ni detalles internos.

| HTTP | Situación | Mensaje orientado al usuario |
| --- | --- | --- |
| 400 | Datos inválidos, petición mal formada, formato de archivo no permitido o falta de motivación obligatoria | Revisa los datos ingresados. |
| 401 | No autenticado, credencial inválida o sesión/token expirado o revocado | Debes iniciar sesión o renovar la sesión. |
| 403 | Usuario autenticado sin permisos suficientes, fuera de alcance contextual, vínculo restringido o fallo CSRF | No tienes permiso para realizar esta acción. |
| 404 | Recurso inexistente o no disponible | No se encontró el registro solicitado. |
| 409 | Conflicto con el estado actual, periodo cerrado, colisión de horario, falta de cupo, incumplimiento de debido proceso o concurrencia (`@Version`) | La operación entra en conflicto con el estado o información existente. Revisa los datos y el estado del registro. |
| 429 | Límite de intentos o frecuencia excedido (`rate limiting` en acceso o exportaciones masivas) | Has realizado demasiadas solicitudes. Intenta nuevamente más tarde. |
| 500 | Error inesperado del servidor | Ocurrió un error inesperado. Intenta nuevamente. Si persiste, contacta al administrador. |

El contrato OpenAPI debe describir las respuestas de error y las estructuras que se puedan exponer de forma segura. Conviene utilizar un formato de error consistente, por ejemplo con código interno, mensaje legible, detalles de validación por fila/campo permitidos (como en el rechazo atómico de planillas `UC-17 E1`) y un identificador de correlación; no incluir stack traces, secretos ni datos personales innecesarios.

En Angular se requiere:

- Manejo centralizado de errores HTTP y traducción de errores conocidos a mensajes útiles.
- Estados globales o por componente para carga, éxito, error y ausencia de datos.
- Evitar que una petición fallida deje indefinidamente una pantalla en estado de carga.
- Evitar reintentos automáticos indiscriminados en operaciones que puedan duplicar efectos; aplicar idempotencia o reglas específicas si un reintento fuese necesario.
- No hacer bucles ante múltiples respuestas `401`.
- Diferenciar una sesión no autenticada (`401`) de un usuario sin permisos (`403`).
- Mostrar mensajes de validación cerca de los campos y mantener una vista accesible del resumen de errores.
- Mantener los detalles técnicos en los logs del backend con controles de acceso adecuados.

La implementación puede utilizar una estrategia central mediante interceptores HTTP y servicios de notificación, sin acoplar los mensajes de interfaz con cada implementación generada de OpenAPI.

# 12. Auditoría

Las operaciones sensibles de los 12 módulos deben dejar evidencia inmutable suficiente para diagnóstico, responsabilidad demostrada (Decreto 1377 de 2013) y trazabilidad forense desde la Fase 1 (`RN-10`, `UC-27`, `UC-PROV-29`). Estructura canónica del evento:

- Usuario: identificador interno del usuario que realizó la acción.
- Rol activo: rol bajo el cual operaba la sesión en el instante de la operación (`RN-SEG-04`).
- Módulo y Acción: por ejemplo, `MODIFICAR_NOTA`, `BLOQUEAR_VINCULO_RESTRICCION_LEGAL`, `APROBAR_EXCUSA`, `REABRIR_PERIODO`, `RESOLVER_RECURSO_CONVIVENCIA`.
- Entidad: entidad de dominio afectada.
- Registro afectado: identificador interno y referencia académica/institucional necesaria.
- Valor anterior: estado previo serializado de forma sanitizada, cuando resulte pertinente.
- Valor nuevo: estado resultante serializado de forma sanitizada.
- Motivo: justificación obligatoria en correcciones, reaperturas, excepciones y exportaciones.
- Fecha y hora: registrada en el servidor con política temporal consistente.
- Resultado de la operación: éxito, fallo de validación o denegación de seguridad (`401`/`403`).
- Correlation/request ID: identificador técnico para relacionar la acción con los logs sin exponer secretos.
- Hash de integridad encadenado (`SHA-256`): huella calculada sobre el evento y el hash del evento inmediatamente anterior para detectar alteraciones o supresiones directas en base de datos (`UC-PROV-29`).

La auditoría contempla cuentas y roles, datos personales y rectificaciones Habeas Data, vínculos de acudientes, autorizaciones de retiro físico y restricciones legales, estructura y plan de estudios, asignaciones y suplencias docentes, matrícula y novedades, configuración versionada de SIEE/Manual/Política de Datos, calificaciones, nivelaciones, reclamaciones, convalidaciones, asistencia y excusas, horarios, cierres/boletines, promoción y graduación, convivencia y debido proceso, expedición documental y consultas/exportaciones de reportes. No registrar jamás contraseñas, hashes, JWT, refresh tokens, cookies de sesión, secretos ni el contenido clínico de historias médicas.

Los registros de auditoría son estrictamente **`append-only`** (ningún usuario, incluido `ADMINISTRADOR` o `RECTOR`, puede modificarlos ni eliminarlos desde la aplicación). Su retención en línea, archivado histórico firmado (`cold storage`), verificación periódica de la cadena SHA-256 y exportación regulada para entes de control se rigen por `UC-PROV-29`.

# 13. Reportes y documentos

El sistema contempla en los Módulos 10 y 12, conforme al alcance priorizado:

- Boletín académico periódico e informe final versionado (`UC-20`), incluyendo modalidad cualitativa por Dimensiones en Preescolar y boletín aplazado por excusa médica vigente.
- Certificado oficial de estudio anual/plurianual y certificado oficial de notas parciales por retiro o traslado saliente (`UC-PROV-27`, Decreto 1290 Art. 17).
- Constancia de matrícula o de estudio activo (`UC-PROV-27`).
- Actas oficiales: Acta de Matrícula, Actas de Nivelación/Habilitación, Actas de Convalidación, Actas de Comisión de Evaluación Periódica y Anual, Actas de Graduación (Grado 11.°), Actas del Consejo Académico/Directivo y Actas reservadas del Comité Escolar de Convivencia.
- Listados oficiales de estudiantes por grupo (`UC-PROV-10`), sábanas de calificaciones (`UC-PROV-17`) y reportes de asistencia y ausentismo (`UC-PROV-19`).
- Reportes administrativos y exportaciones sectoriales (**SIMAT**, **DANE**, **SIUCE**) con agregación/anonimización por defecto (`UC-27`).

JasperReports u otra solución compatible con el ecosistema Java podrá evaluarse cuando el módulo de reportes esté listo. La selección no debe hacerse antes de conocer los formatos, requisitos de impresión, exportación y mantenimiento.

Políticas documentales formalizadas en V2 (`UC-PROV-27` y `UC-PROV-28`):

- Los documentos oficiales se generan exclusivamente en el backend utilizando los encabezados legales y firmas vigentes de `UC-PROV-13` (`RN-CFG-08`) y las versiones históricas inmutables del Plan de Estudios (`UC-PROV-08 A2`) y del SIEE (`UC-PROV-14 A1`); Angular nunca construye documentos oficiales a partir de cálculos locales.
- Cada documento almacenado en el repositorio se clasifica obligatoriamente en uno de los 5 niveles de confidencialidad (`PUBLICO_INSTITUCIONAL`, `ACADEMICO_ESTANDAR`, `ADMINISTRATIVO_EXPEDIENTE`, `CONFIDENCIAL_SALUD_E_INCLUSION`, `RESERVADO_CONVIVENCIA_Y_PROTECCION`), guarda huella `SHA-256` y se entrega únicamente mediante enlace efímero previa doble validación en servidor (`UC-05`, `UC-11`).
- Cuando se corrige de forma autorizada una nota o dato de identificación que afecta un documento previamente emitido, el sistema regenera una **nueva versión ($V+1$)** con código único de verificación y marca la emisión anterior como `HISTORICA_REEMPLAZADA_POR_RECTIFICACION` sin borrado físico (Ley 594 de 2000).

# 14. Tecnologías y herramientas

| Área | Tecnología recomendada | Criterio de uso |
| --- | --- | --- |
| Backend | Java + Spring Boot | API, servicios, reglas de negocio y seguridad. Mantener la versión compatible con el proyecto existente salvo cambio justificado. |
| IDE backend | Visual Studio Code | Entorno de trabajo habitual para el backend. |
| Build backend | Maven | Compilación, dependencias y pruebas del backend. |
| API | REST + JSON | Interfaz estable entre frontend y backend. |
| Contrato API | OpenAPI 3.x + Swagger UI | Especificación versionada y documentación navegable. |
| Generación cliente | OpenAPI Generator (`typescript-angular`) | Generar modelos/interfaces y servicios cliente tipados para Angular. |
| Persistencia | Spring Data JPA / Hibernate | Acceso a datos relacional. |
| Base de datos | PostgreSQL | Persistencia y restricciones de integridad. |
| Migraciones | Flyway (o Liquibase) | Esquema versionado y repetible. Elegir una de las dos herramientas. |
| Cliente inicial | Angular + TypeScript | SPA para escritorio/navegador responsive según necesidades del producto. |
| Herramientas frontend | Node.js + npm + Angular CLI | Instalación y gestión de paquetes, scripts de desarrollo, compilación y pruebas. Seleccionar versiones compatibles con Angular. |
| HTTP reactivo | Angular HttpClient + RxJS | Comunicación asíncrona con la API y composición de flujos. |
| Seguridad | Spring Security + RBAC + autorización contextual + cookies/tokens conforme a la estrategia adoptada | Autenticación, autorización contextual, CORS y mitigación CSRF/XSS. |
| Testing backend | JUnit + Mockito | Pruebas unitarias de reglas de negocio y componentes. |
| Testing de integración | Spring tests; Testcontainers cuando aporte valor | Validar integración y persistencia contra PostgreSQL real. |
| Testing frontend | Herramientas de pruebas compatibles con la versión seleccionada de Angular; pruebas de componentes, servicios, guards e interceptores | Verificar interfaz y comportamiento. Usar el runner soportado por el proyecto generado y fijar la configuración. |
| Versionamiento | Git + GitHub | Historial, revisión y colaboración. |
| Infraestructura | Docker + Docker Compose | Entorno reproducible cuando corresponda. |
| Reverse proxy | Nginx, cuando sea necesario | HTTPS, enrutamiento y despliegue. |
| Cache/colas | Redis, opcional | Solo si existe necesidad demostrada. |
| Reportes | JasperReports (u otra librería PDF Java), cuando se implemente | Generación de documentos del lado del servidor. |
| Cliente futuro | Kotlin para móvil | Consumir la misma API/contrato OpenAPI. |

Las versiones concretas de Angular, TypeScript, Node.js/npm, Spring Boot, Java, OpenAPI Generator y sus plugins deben seleccionarse como conjunto compatible y fijarse en el proyecto. Consultar las matrices oficiales de compatibilidad al inicializar o actualizar el stack; no asumir que la última versión de una herramienta funciona con todas las demás.

# 15. Dependencias iniciales del backend y frontend

## 15.1. Backend Spring Boot

Base recomendada, incorporada en el momento correcto:

- Spring Web.
- Spring Data JPA.
- PostgreSQL Driver.
- Validation.
- Spring Security.
- Lombok, si reduce código repetitivo sin ocultar el comportamiento necesario para comprender el proyecto.
- Spring Boot DevTools solo en desarrollo.
- Springdoc OpenAPI para exponer y revisar la documentación API en ejecución, si encaja con la versión seleccionada.
- Flyway **o** Liquibase para migraciones versionadas.
- Soporte para JWT/cookies/CSRF conforme al diseño acordado; preferir los componentes y extensiones mantenidos del ecosistema Spring Security antes que implementar criptografía propia.
- JUnit y Mockito para pruebas, según las dependencias gestionadas por Spring Boot.
- Testcontainers cuando se necesite probar con PostgreSQL real.

No incorporar indiscriminadamente todas las tecnologías avanzadas desde el primer commit. Actuator, Redis, correo, reportes, contenedores, observabilidad avanzada y otros componentes se agregan cuando exista una necesidad concreta o llegue su fase correspondiente.

## 15.2. Cliente Angular

La base del cliente web debe incluir:

- Angular CLI y las dependencias principales del framework seleccionadas por una versión compatible.
- TypeScript.
- Node.js y npm compatibles con la versión de Angular seleccionada.
- Angular Router para navegación.
- Angular HttpClient para solicitudes HTTP.
- RxJS para flujos asíncronos.
- Formularios reactivos para formularios de datos y validaciones de interfaz.
- OpenAPI Generator con el generador `typescript-angular` para producir el cliente API desde `openapi/openapi.yaml`.
- Herramientas de pruebas compatibles con el proyecto Angular.

Requisitos para la configuración frontend:

- Mantener `package.json` y el archivo de bloqueo (`package-lock.json` si se utiliza npm) versionados.
- Evitar actualizar dependencias directamente a versiones mayores sin verificar compatibilidad y revisar los cambios.
- Definir scripts reproducibles para desarrollo, build, pruebas, linting y generación del cliente API.
- Utilizar archivos/configuración de entorno para la URL base de API. No incluir secretos en `environment` de Angular: el bundle del navegador puede ser inspeccionado.
- Separar el código generado por OpenAPI del código manual.
- Automatizar o documentar la regeneración del cliente cuando cambie el contrato.
- No copiar manualmente DTO Java a interfaces TypeScript si estos ya se pueden generar desde el contrato oficial.

# 16. Testing y calidad

La calidad se validará en las capas de dominio, API, base de datos, seguridad y frontend. Tener una compilación correcta no demuestra por sí solo que las reglas de negocio sean correctas. Las tablas de Flujos Alternos (`A#`) y Flujos de Excepción (`E#`) de los 12 módulos `v2` constituyen el conjunto base de criterios de aceptación y pruebas.

Backend:

- Pruebas unitarias de servicios, motores de cálculo `BigDecimal` (SIEE, ponderaciones, no desmejoramiento), validador de colisiones horarias y reglas de negocio.
- Pruebas de controllers, DTO, validaciones y mapeo de errores HTTP (`400`, `401`, `403`, `404`, `409`, `429`, `500`).
- Pruebas de integración de repositorios, transacciones y restricciones PostgreSQL.
- Testcontainers para escenarios que requieran una base PostgreSQL real, cuando aporte valor.
- Pruebas de seguridad y autorización vertical/horizontal: usuario anónimo, rol insuficiente, acceso a estudiante/grupo ajeno, vínculo revocado o en estado `BLOQUEADO_RESTRICCION_LEGAL`, tokens inválidos o expirados y anonimización de terceros menores.
- Pruebas CORS para origen permitido, origen no permitido y preflight `OPTIONS`.
- Pruebas CSRF cuando la autenticación se base en cookies.
- Pruebas de concurrencia para invariantes sensibles: unicidad del acudiente principal, último cupo disponible en matrícula, matrícula duplicada, colisión simultánea de horario y edición concurrente de calificaciones o cierres.
- Validación de casos límite, periodos cerrados, no reprobación en Preescolar, integridad `append-only` de auditoría y operaciones transaccionales.
- Pruebas de migraciones desde un esquema vacío y desde la versión anterior cuando sea pertinente.

Frontend Angular:

- Pruebas de componentes, formularios, validaciones visuales y estados de carga/vacío/error.
- Pruebas de servicios e integración con `HttpClientTesting` o el mecanismo de prueba compatible con la versión seleccionada.
- Pruebas de interceptores para verificar que las credenciales y el CSRF se adjunten solo a destinos autorizados.
- Pruebas de guards para navegación según estado de sesión y rol activo.
- Pruebas de presentación de `400`, `401`, `403`, `404`, `409`, `429` y `500`.
- Pruebas de servicios cliente generados o, al menos, una comprobación de compilación que garantice que el código generado y el cliente manual se mantienen compatibles con el contrato.
- Pruebas de integración de extremo a extremo para flujos prioritarios una vez que estén disponibles frontend y backend.

Calidad general:

- Mantener formato y convenciones consistentes.
- No aceptar una tarea sin pruebas proporcionales a su riesgo.
- No eliminar pruebas para lograr que la compilación pase.
- Investigar las pruebas fallidas antes de modificar expectativas.
- No reportar como ejecutada una prueba que no se ejecutó.
- Código mantenible antes que código simplemente funcional.

# 17. Git y control del proyecto

Git debe utilizarse desde el inicio para backend, frontend, contrato OpenAPI y documentación (`docs/`).

- `main`: versión estable.
- `develop`: integración de cambios, si el flujo del proyecto lo requiere.
- `feature/*`: funcionalidades individuales.
- `fix/*`: correcciones específicas, cuando encaje con la política de ramas.
- Commits pequeños y descriptivos.
- No trabajar todo directamente sobre `main`.
- Incluir migraciones, tests, contrato OpenAPI y documentación asociados a una funcionalidad en el mismo conjunto lógico de cambios.
- Revisar el diff antes de cada commit; no incorporar logs, credenciales, archivos temporales, carpetas de build o dependencias instaladas que deban excluirse.
- Mantener reglas de `.gitignore` adecuadas para Java/Maven, Angular/Node, entornos locales y archivos de secretos.
- No eliminar ni reescribir historial Git para borrar credenciales expuestas sin un plan explícito; si una credencial fue comprometida, primero rotarla y luego tratar el historial con el procedimiento adecuado.
- Registrar instrucciones de compilación y pruebas para backend y frontend en el README.

# 18. Fases de desarrollo

El desarrollo se organizará por fases, pero no todas las tareas deben ejecutarse estrictamente en serie. La base Angular puede iniciarse cuando la arquitectura, el contrato y la seguridad estén definidos; luego se integrará progresivamente con los módulos de backend. No es necesario esperar a que toda la lógica académica esté terminada para preparar la estructura del cliente.

**Secuencia técnica de dependencias (ver `MAPA_UC_Y_DEPENDENCIAS_v2.md`):** al implementar las Fases 4 y 5, la base del **Módulo 06 (Configuración institucional: `UC-PROV-13` y `UC-26`)** debe inicializarse temprano porque provee la Política de Datos, los parámetros transversales y las versiones del SIEE/Manual consumidos desde los Módulos 01 a 05; asimismo, el núcleo de sesiones del **Módulo 09 (Horarios: `UC-PROV-20`)** debe coordinarse antes o en paralelo con el **Módulo 08 (Asistencia: `UC-19`)**.

| Fase | Objetivo, módulos V2 y tareas principales |
| --- | --- |
| Fase 0 — Análisis y alcance | **Completada en V2:** 12 módulos auditados y actualizados (`56 UCs`), `00_Guia_General_y_Reglas_Transversales_v2.docx`, `00_Indice_y_Secuencia_de_Desarrollo_v2.docx` y `MAPA_UC_Y_DEPENDENCIAS_v2.md`. Cierre de validación global, alcance exacto del MVP V1, matriz RBAC/contextual y DER inicial. |
| Fase 1 — Fundación del backend | Revisar el proyecto existente; configurar Spring Boot, PostgreSQL, JPA, Flyway, estructura modular, validaciones, manejo global de errores, captura base de auditoría `append-only` (`RN-10`) y documentación base. Retirar secretos del código y establecer perfiles de configuración. |
| Fase 2 — Contrato API y seguridad base | Versionar OpenAPI 3.x; establecer endpoints y DTO; implementar **Módulo 01 — Seguridad y acceso (`UC-01`, `UC-PROV-01` a `UC-PROV-03`)** con Spring Security, RBAC, rol activo único, autorización contextual, CORS explícito, cookies `HttpOnly`/CSRF y pruebas negativas. |
| Fase 3 — Fundación de Angular SPA | Crear el proyecto Angular con TypeScript, Node/npm y Angular CLI; organizar `core`, `shared` y `features`; configurar Router, HttpClient, RxJS, formularios reactivos, entorno de API y generación OpenAPI. Preparar autenticación, conmutación de rol activo, guards, interceptores y manejo centralizado de errores/carga. |
| Fase 4 — Núcleo administrativo y configuración base | Inicializar base de **Módulo 06 — Configuración institucional (`UC-PROV-13`, `UC-26`, `UC-PROV-14`)** e implementar **Módulo 02 — Estudiantes y acudientes (`UC-12`, `UC-06`, `UC-07`, `UC-PROV-04`, `UC-PROV-05`)**, **Módulo 03 — Estructura académica (`UC-14`, `UC-PROV-06` a `UC-PROV-08`)**, **Módulo 04 — Matrícula (`UC-13`, `UC-PROV-09`, `UC-PROV-10`)** y **Módulo 05 — Profesores y asignaciones (`UC-15`, `UC-PROV-11`, `UC-PROV-12`)**. Integrar por etapas con Angular. |
| Fase 5 — Académico y cierre | Implementar **Módulo 09 — Horarios (`UC-04`, `UC-10`, `UC-PROV-20` a `UC-PROV-22`)**, **Módulo 08 — Asistencia (`UC-19`, `UC-03`, `UC-09`, `UC-PROV-18`, `UC-PROV-19`)**, **Módulo 07 — Calificaciones y evaluación (`UC-16` a `UC-18`, `UC-02`, `UC-08`, `UC-PROV-15` a `UC-PROV-17`)** y **Módulo 10 — Cierre académico y promoción (`UC-20`, `UC-21`, `UC-PROV-23`, `UC-PROV-24`)** usando el motor `BigDecimal` del SIEE. |
| Fase 5B — Convivencia escolar | Implementar **Módulo 11 — Convivencia escolar (`UC-22` a `UC-25`, `UC-PROV-25`, `UC-PROV-26`)**: Observador bilateral con adendas, Ruta de Atención Integral Tipo I-III, SIUCE, debido proceso con descargos y recursos, medidas con Plan de Continuidad Académica (integrado con Módulos 07 y 08), Comité Escolar de Convivencia y consulta anonimizada. |
| Fase 6 — Documentos, reportes y auditoría | Implementar **Módulo 12 — Documentos, reportes y auditoría (`UC-05`, `UC-11`, `UC-27`, `UC-PROV-27` a `UC-PROV-29`)**: expedición de certificados anuales/parciales y constancias oficiales, repositorio documental clasificado en 5 niveles con TRD, reportes institucionales/SIMAT/DANE/SIUCE y verificación criptográfica SHA-256 + exportación forense de la auditoría. |
| Fase 7 — Comunicación *(Evolución)* | Avisos institucionales, comunicados, mensajería y preferencias de notificación según necesidad concreta y política institucional (posponible fuera del MVP base). |
| Fase 8 — Finanzas *(Evolución)* | Pensiones, pagos, facturación, cartera, becas y descuentos. Posponible fuera del alcance mínimo de la V1 académica-administrativa. |
| Fase 9 — Calidad e infraestructura | Completar pruebas de integración y extremo a extremo, hardening de seguridad, Docker/Compose si corresponde, backups y restauración, logs, monitorización, CI/CD y despliegue HTTPS. Esta calidad debe trabajarse desde las fases anteriores, no dejarse exclusivamente para el final. |
| Fase 10 — Evolución | Cliente móvil Kotlin consumiendo la misma API/OpenAPI; ampliación de capacidades, sincronización u otros clientes solo cuando exista una necesidad validada. |

Los módulos de Comunicación avanzada (Fase 7), Finanzas (Fase 8), Redis y evolución móvil Kotlin (Fase 10) pueden quedar fuera del alcance mínimo de la V1. Su inclusión se decidirá por prioridad, necesidad y capacidad de prueba, no por la mera existencia de una fila en el roadmap.

# 19. Orden de aprendizaje recomendado

1. **Java sólido:** POO, interfaces, excepciones, genéricos, Collections, Streams, lambdas, Optional, `BigDecimal`, fechas (`java.time`) y Maven.
2. **SQL y PostgreSQL:** CRUD, JOIN, agrupaciones, constraints (`UNIQUE` parciales), índices, transacciones, normalización y diseño relacional.
3. **Spring Boot:** controllers, services, repositories, DTO, JPA/Hibernate, `@Version`, validación y configuración.
4. **Seguridad:** Spring Security, hashing de contraseñas, autenticación, autorización RBAC y contextual, JWT/cookies, CSRF, CORS y prácticas de seguridad web.
5. **OpenAPI:** diseño de contratos, schemas, respuestas de error, versionamiento y generación de cliente TypeScript mediante OpenAPI Generator.
6. **Angular + TypeScript:** componentes, templates, standalone components y/o módulos cuando corresponda, servicios, inyección de dependencias, Router, formularios reactivos y organización por funcionalidades.
7. **Node.js/npm y herramientas frontend:** compatibilidad de versiones, scripts, gestión de dependencias, builds reproducibles y archivos de bloqueo.
8. **RxJS y HTTP:** Observables, operadores, cancelación de solicitudes cuando corresponda, HttpClient, interceptores y estados asíncronos.
9. **Seguridad de SPA:** guards de navegación, manejo de sesión, almacenamiento de tokens, cookies seguras, CSRF/XSS y manejo de errores HTTP. Los guards son UX; la autorización real sigue en backend.
10. **Ingeniería de software:** SOLID, DRY, KISS, Clean Code, patrones (`Strategy` para el SIEE), UML, arquitectura modular y diseño de APIs.
11. **Testing:** JUnit, Mockito, pruebas de integración, Testcontainers cuando aporte valor y pruebas Angular de componentes, servicios, guards e interceptores.
12. **Git y colaboración:** ramas, commits claros, revisión de cambios, documentación y regeneración controlada de código OpenAPI.
13. **Docker y despliegue:** redes, variables de entorno, HTTPS, backups y prácticas de despliegue seguro.
14. **Tecnologías avanzadas únicamente cuando el proyecto las necesite:** Redis, Nginx avanzado, CI/CD, cliente Kotlin, mensajería y optimización de rendimiento.

El aprendizaje debe acompasarse con el desarrollo: comprender lo suficiente para implementar cada fase, probarla y documentarla, en lugar de intentar dominar toda la pila antes de comenzar.

# 20. Criterios de diseño y decisiones que NO deben olvidarse

- La SPA no contiene las reglas críticas de negocio como única barrera.
- La API es la frontera de seguridad y negocio; cada operación protegida debe validar autenticación, rol activo, autorización contextual y estado de dominio.
- El contrato OpenAPI es la fuente de verdad de la superficie REST pública; el backend debe ajustarse a él y el cliente Angular se genera desde él.
- Los DTO se utilizan para controlar qué información entra y sale; las entidades JPA nunca se exponen como contrato público.
- Las relaciones muchos-a-muchos se modelarán explícitamente cuando tengan datos o reglas propias.
- Las operaciones críticas deben ser transaccionales, idempotentes y protegidas con control de concurrencia (`@Version` y constraints en PostgreSQL).
- La auditoría `append-only` (`RN-10`) debe diseñarse e implementarse desde la Fase 1 para operaciones sensibles, sin registrar secretos ni contraseñas.
- Los órganos colegiados (Comisión de Evaluación, Comité Escolar de Convivencia, Consejo Académico y Consejo Directivo) no usan cuentas compartidas (`RN-SEG-01`): sus decisiones se formalizan mediante actas numeradas y membresías nominativas.
- El cliente Angular no se conecta directamente con PostgreSQL ni contiene secretos de backend ni construye boletines/certificados con cálculos locales.
- No guardar JWT, refresh tokens ni credenciales en `localStorage` o `sessionStorage`.
- Si la autenticación usa cookies, definir controles CSRF apropiados; `HttpOnly`, `Secure` y `SameSite` son controles complementarios, no reemplazan todas las defensas.
- Configurar CORS con orígenes explícitos. Cuando se permitan credenciales, no usar un origen comodín.
- Los guards e interceptores Angular mejoran navegación y consistencia, pero no sustituyen Spring Security.
- Los errores deben tener códigos HTTP y respuestas consistentes (`400`, `401`, `403`, `404`, `409`, `429`, `500`); `401` y `403` no son intercambiables.
- Los periodos, escalas, ponderaciones, fórmulas de recuperación, umbrales de asistencia, reglas de promoción y plazos de debido proceso provienen de versiones inmutables del Módulo 06 (`UC-26`, `UC-PROV-13`).
- Respetar el régimen legal de Preescolar (evaluación cualitativa por dimensiones y prohibición de reprobación), el derecho de continuidad educativa ante sanciones (`RN-08`), los bloqueos por restricción legal parental (`UC-PROV-05 A3`) y la anonimización de terceros menores en convivencia (`UC-25`).
- Redis, Nginx, Docker, CI/CD y otros componentes no se agregan por moda: se incorporan cuando el sistema los justifique.
- La arquitectura debe permitir añadir un cliente Kotlin sin reescribir la lógica de negocio del backend.
- Las versiones de Angular/TypeScript/Node/npm deben ser compatibles entre sí y deben mantenerse con dependencias bloqueadas.
- El código OpenAPI generado no se modifica manualmente; si se necesita cambiar una propiedad o servicio generado, primero se modifica el contrato o la configuración del generador.
- El tratamiento de datos personales debe respetar la normativa colombiana aplicable (Ley 1581 de 2012, Decreto 1377 de 2013 y Ley 1098 de 2006) y los principios de finalidad, necesidad, acceso restringido, seguridad y conservación permanente del historial escolar (Ley 594 de 2000).

# 21. Contexto para futuros chats

Este apartado está escrito específicamente para que, al iniciar una nueva conversación, pueda utilizarse como contexto del proyecto. Si el proyecto continúa evolucionando, este documento debe actualizarse en lugar de crear descripciones contradictorias.

## 21.1. Resumen operativo de la versión 2.1

- **Proyecto:** Sistema de Gestión Escolar Modular para instituciones educativas colombianas (Preescolar, Básica Primaria, Básica Secundaria y Media).
- **Versión del documento maestro:** 2.1 (sincronizado con los 12 módulos `v2` y sus 56 casos de uso).
- **Documentación funcional consolidada:** `00_Guia_General_y_Reglas_Transversales_v2.docx`, `00_Indice_y_Secuencia_de_Desarrollo_v2.docx`, `MAPA_UC_Y_DEPENDENCIAS_v2.md`, `BRECHAS_DE_COBERTURA_v2.md` y los 12 archivos `01_Seguridad_y_Acceso_v2.docx` a `12_Documentos_Reportes_y_Auditoria_v2.docx`.
- **Alcance funcional V2:** 12 módulos completos (Seguridad, Estudiantes y Acudientes, Estructura Académica, Matrícula, Profesores y Asignaciones, Configuración Institucional, Calificaciones y Evaluación, Asistencia, Horarios, Cierre Académico y Promoción, Convivencia Escolar, y Documentos, Reportes y Auditoría). Comunicación avanzada (Fase 7) y Finanzas (Fase 8) quedan como evolución posterior.
- **Cliente inicial V1:** SPA desarrollada en Angular + TypeScript (Node.js + npm + Angular CLI en desarrollo).
- **Backend:** Java + Spring Boot (monolito modular), desarrollado habitualmente en Visual Studio Code con Maven.
- **API y contrato:** REST/JSON con OpenAPI 3.x versionado como fuente de verdad; OpenAPI Generator (`typescript-angular`) genera el cliente/tipos consumidos por Angular.
- **Base de datos y persistencia:** PostgreSQL + Spring Data JPA/Hibernate + migraciones versionadas (Flyway).
- **Seguridad:** Spring Security, RBAC con rol activo único, autorización contextual por recurso, cuentas nominativas (sin cuentas compartidas para órganos colegiados), cookies `HttpOnly`/CSRF o memoria volátil (prohibido `localStorage`/`sessionStorage`), CORS explícito (`http://localhost:4200` en desarrollo) y auditoría `append-only` con hash encadenado SHA-256.
- **Cliente futuro:** aplicación móvil Kotlin consumiendo la misma API/contrato OpenAPI.
- **Calidad:** JUnit, Mockito, pruebas de integración/Testcontainers, pruebas de seguridad/concurrencia basadas en `A#`/`E#` y pruebas Angular.
- **Marco normativo colombiano:** Ley 115 de 1994, Decreto 1075 de 2015 (Decretos 1290/2009, 2247/1997, 1850/2002, 1965/2013 y 1421/2017), Ley 1620 de 2013 (y Leyes 2383/2024 y 2503/2025), Ley 1098 de 2006, Ley 1581 de 2012 y Decreto 1377 de 2013, Ley 1918 de 2018, Ley 594 de 2000 y Sentencia T-004 de 2026.

## 21.2. Estado técnico conocido del código existente — verificar antes de modificar

La siguiente información procede de una revisión previa del proyecto entregado en una conversación anterior. Debe confirmarse en el repositorio actual antes de tratarla como estado definitivo, porque el código puede haber cambiado:

- El backend existente utiliza el paquete raíz `jackren.sistemaacademico.main`, Java y Spring Boot con Maven, PostgreSQL y Spring Data JPA/Hibernate.
- El módulo inicial incluye entidades/tipos relacionados con `Estudiante`, `Acudiente`, `EstudianteAcudiente` y `Parentesco`, además de DTO, mappers, repositorios, servicios y controladores.
- Se han iniciado operaciones REST para estudiantes y acudientes y reglas para asociar estudiantes con acudientes, evitar parejas duplicadas, elegir un acudiente principal y administrar estados.
- Una auditoría anterior observó una regla amplia equivalente a `requestMatchers("/api/**").permitAll()` en la configuración de seguridad. Si sigue presente, debe considerarse un riesgo crítico: revisar todos los endpoints y sustituir el acceso anónimo por autenticación/autorización correctas antes de utilizar información real.
- Una auditoría anterior observó credenciales de base de datos escritas directamente en la configuración. Si sigue siendo así, deben rotarse, trasladarse a variables de entorno o un gestor de secretos y revisarse los commits anteriores para conocer el alcance de la exposición.
- También se recomendó migrar de una actualización automática del esquema (`ddl-auto=update`) a migraciones versionadas con Flyway.
- Se encontraron pruebas unitarias de servicios, pero deben volver a ejecutarse y ampliarse con pruebas de integración, persistencia, CORS, CSRF y autorización. No asumir que los reportes de una ejecución anterior prueban el estado actual.
- La relación estudiante–acudiente debe alinearse con `02_Estudiantes_y_Acudientes_v2.docx` (`UC-12` y `UC-PROV-05`): protección concurrente de único acudiente principal, diferenciación de representación legal vs. autorización de retiro físico y soporte para `BLOQUEADO_RESTRICCION_LEGAL`.

Estos hallazgos no autorizan una reescritura automática del proyecto. Primero inspeccionar el repositorio actual, verificar cada punto y planificar correcciones incrementales con pruebas.

## 21.3. Instrucción para nuevos chats o agentes de código

Antes de escribir o modificar código:

1. Inspeccionar el repositorio real y confirmar el estado actual de backend, frontend, configuración, dependencias, pruebas y migraciones.
2. Comparar el estado real con este documento y con los módulos `v2` correspondientes dentro de `docs/casos-de-uso/`, señalando divergencias.
3. Separar los hallazgos confirmados, las recomendaciones y las decisiones pendientes de negocio.
4. Proponer un plan incremental con criterios de aceptación derivados de las tablas `A#` y `E#` y riesgos antes de hacer cambios amplios.
5. No inventar reglas institucionales ni cambiar el contrato API sin actualizar `openapi/openapi.yaml`, sus pruebas y el cliente Angular generado.
6. No eliminar funcionalidades existentes ni reestructurar paquetes de forma masiva sin explicar el beneficio, impacto y plan de migración.
7. Mantener la lógica de negocio en Spring Boot; Angular será un cliente independiente.

# 22. Punto de partida recomendado

Con los 12 módulos funcionales auditados y reescritos en V2 (`56 casos de uso`), el siguiente paso práctico no es codificar todas las pantallas a la vez, sino cerrar la validación global de diseño técnico y construir cortes verticales completos desde PostgreSQL hasta Angular:

1. **Cerrar la Validación Global de documentación V2.** Finalizar `BRECHAS_DE_COBERTURA_v2.md` y ratificar si los 29 identificadores `UC-PROV-01` a `UC-PROV-29` pasan a numeración definitiva o conservan su nomenclatura en el repositorio `docs/`.
2. **Confirmar el alcance exacto del MVP V1.** Establecer qué subconjunto de los 12 módulos entra en el primer despliegue usable (por ejemplo, Módulos 01 a 08 + núcleo de 09 y 10, dejando Convivencia avanzada o reportes regulatorios para V1.1).
3. **Derivar la Matriz RBAC + Autorización Contextual y el Modelo Entidad-Relación (DER) V1.** Mapear directamente los 56 UCs y sus invariantes (`UNIQUE`, `@Version`, `append-only`) al esquema relacional de PostgreSQL.
4. **Auditar el backend real existente.** Revisar entidades, DTO, mappers, servicios, controladores, seguridad, dependencias, `application.properties`, logs y pruebas. Confirmar y resolver `permitAll()` y credenciales expuestas si siguen presentes.
5. **Configurar PostgreSQL, Flyway y auditoría base (`RN-10`).** Migrar el esquema de forma versionada desde cero, añadir restricciones de integridad y crear el componente transversal `append-only` de auditoría.
6. **Definir el contrato OpenAPI incremental.** Versionar rutas, DTO, seguridad y respuestas de error (`400`, `401`, `403`, `404`, `409`, `429`, `500`) módulo por módulo en `openapi/openapi.yaml`.
7. **Implementar Módulo 01 (Seguridad base) y base del Módulo 06 (Configuración).** Establecer Spring Security, autenticación (`UC-01`, `UC-PROV-01` a `UC-PROV-03`), RBAC con rol activo único, CORS explícito, cookies `HttpOnly`/CSRF, Política de Datos (`UC-PROV-13`) y pruebas negativas.
8. **Crear la fundación Angular SPA y generar el cliente API.** Inicializar Angular con versiones compatibles de Node/npm, organizar `core`, `shared` y `features`, configurar Router, HttpClient, RxJS, formularios reactivos, interceptores, guards y OpenAPI Generator (`typescript-angular`).
9. **Completar un módulo de extremo a extremo.** Alinear e integrar el Módulo 02 (Estudiantes y acudientes: `UC-12`, `UC-06`, `UC-07`, `UC-PROV-04`, `UC-PROV-05`) desde PostgreSQL/Flyway hasta la interfaz Angular.
10. **Ampliar siguiendo el grafo técnico de `MAPA_UC_Y_DEPENDENCIAS_v2.md`.** Continuar con Estructura Académica (Mód. 03), Matrícula (Mód. 04), Profesores y Asignaciones (Mód. 05), Horarios (Mód. 09), Asistencia (Mód. 08), Calificaciones (Mód. 07), Cierre y Promoción (Mód. 10), Convivencia (Mód. 11) y Documentos/Reportes/Auditoría (Mód. 12).
11. **Validar el ciclo académico completo.** Probar el motor `BigDecimal` del SIEE, promedios de asignatura y área, Preescolar cualitativo, nivelaciones sin desmejoramiento, cierres/boletines versionados, promoción y certificados con datos ficticios realistas.
12. **Revisar antes de cerrar cada fase.** Verificar compilación backend, pruebas automatizadas (basadas en `A#`/`E#`), generación del cliente OpenAPI, build/tests Angular, seguridad, migraciones y documentación.

La meta inicial es obtener un flujo pequeño pero completo, seguro y probado, desde Angular hasta PostgreSQL a través del contrato OpenAPI y la API Spring Boot. Luego se repetirá el patrón para cada módulo, evitando duplicar reglas, exponer datos indebidamente o introducir complejidad antes de que exista una necesidad.

---

## Referencias técnicas y normativas

Estas referencias sirven como punto de partida y deben volver a consultarse cuando se actualicen versiones o se cambien decisiones arquitectónicas. Fecha de revisión: 18 de noviembre de 2026.

### Referencias técnicas
- Angular — HTTP interceptors: https://angular.dev/guide/http/interceptors
- Angular — Route guards: https://angular.dev/guide/routing/route-guards
- Angular — Security best practices: https://angular.dev/best-practices/security
- OpenAPI Generator — TypeScript Angular generator: https://openapi-generator.tech/docs/generators/typescript-angular/
- Spring Security — CORS: https://docs.spring.io/spring-security/reference/servlet/integrations/cors.html
- Spring Security — CSRF: https://docs.spring.io/spring-security/reference/servlet/exploits/csrf.html
- OWASP — Session Management Cheat Sheet (almacenamiento de tokens): https://cheatsheetseries.owasp.org/cheatsheets/Session_Management_Cheat_Sheet.html
- OWASP — CSRF Prevention Cheat Sheet: https://cheatsheetseries.owasp.org/cheatsheets/Cross-Site_Request_Forgery_Prevention_Cheat_Sheet.html

### Referencias normativas colombianas
- Ley 115 de 1994 — Ley General de Educación (Función Pública): https://www.funcionpublica.gov.co/eva/gestornormativo/norma.php?i=292
- Decreto 1075 de 2015 — Decreto Único Reglamentario del Sector Educación (compila Decretos 1290 de 2009, 2247 de 1997, 1850 de 2002, 1965 de 2013 y 1421 de 2017): https://contenidos.mineducacion.gov.co/ntg/men/pdf/decreto_1075_de_2015.pdf y https://www.funcionpublica.gov.co/eva/gestornormativo/norma.php?i=77913
- Ley 1620 de 2013 (y modificaciones Leyes 2383 de 2024 y 2503 de 2025) — Sistema Nacional de Convivencia Escolar: https://www.funcionpublica.gov.co/eva/gestornormativo/norma.php?i=52287
- Ley 1098 de 2006 — Código de la Infancia y la Adolescencia: https://www.funcionpublica.gov.co/eva/gestornormativo/norma.php?i=22106
- Ley 1581 de 2012 y Decreto 1377 de 2013 — Protección de Datos Personales y Responsabilidad Demostrada: https://www.funcionpublica.gov.co/eva/gestornormativo/norma.php?i=49981 y https://www.funcionpublica.gov.co/eva/gestornormativo/norma.php?i=53646
- Ley 1918 de 2018 (inhabilidades por delitos contra menores), Ley 594 de 2000 (Ley General de Archivos), Resolución MEN 4210 de 1996 (Servicio Social Estudiantil), Decreto 869 de 2010 (ICFES Saber 11), Resolución MEN 07797 de 2015 (SIMAT) y Corte Constitucional — Sentencia T-004 de 2026 (debido proceso escolar y continuidad educativa).

**Documento maestro — Sistema de Gestión Escolar — Versión 2.1**