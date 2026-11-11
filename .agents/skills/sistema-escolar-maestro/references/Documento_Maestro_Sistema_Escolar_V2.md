# DOCUMENTO MAESTRO

# Sistema de Gestión Escolar

**Definición del proyecto, arquitectura, módulos, tecnologías y hoja de ruta**

**Versión:** 2.0 — Documento de referencia del proyecto  
**Fecha de actualización:** 19 de octubre de 2026  
**Propósito adicional:** servir como contexto base para futuras conversaciones y nuevos chats.

> **Decisión estratégica de la versión 2.0:** el cliente inicial de la V1 será una Single Page Application (SPA) desarrollada con Angular y TypeScript, administrada con Node.js/npm. JavaFX deja de formar parte del alcance del cliente inicial. El backend Spring Boot y sus reglas de negocio se mantienen independientes del frontend.

---

# 1. Objetivo general del proyecto

Construir un sistema de gestión escolar completo, modular, seguro y escalable que permita digitalizar progresivamente los procesos académicos, administrativos, financieros y de comunicación de una institución educativa.

El proyecto no debe tratarse como un simple CRUD de estudiantes. La intención es construir un software mantenible, con reglas de negocio explícitas, seguridad, permisos, auditoría, reportes, manejo de errores, pruebas, documentación y una arquitectura que pueda crecer sin tener que reescribir sus componentes centrales.

La primera versión usable tendrá como cliente una **SPA en Angular + TypeScript**. Angular consumirá una API REST desarrollada con Java y Spring Boot. La persistencia central estará en PostgreSQL. El backend permanecerá desacoplado del cliente web para permitir que, posteriormente, una aplicación móvil desarrollada con Kotlin u otros clientes autorizados consuman la misma API.

El sistema debe adaptarse a los procesos de una institución educativa colombiana. Las reglas académicas, criterios de evaluación, escalas de valoración y condiciones de promoción deberán responder al marco normativo aplicable y a las decisiones institucionales formalizadas en el Proyecto Educativo Institucional (PEI) y el Sistema Institucional de Evaluación de los Estudiantes (SIEE), según corresponda.

Objetivos principales:

- Administrar estudiantes, acudientes, profesores y estructura académica.
- Gestionar matrículas e historial académico.
- Registrar y consultar calificaciones, asistencia y horarios.
- Aplicar reglas académicas configurables, evitando codificar como universales decisiones que dependen del colegio.
- Generar boletines, certificados, constancias y reportes autorizados.
- Proteger los datos personales, especialmente los de niños, niñas y adolescentes.
- Mantener trazabilidad de las operaciones relevantes.
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
| Backend | Java + Spring Boot | Casos de uso, reglas de negocio, API REST, autenticación, autorización, validación y acceso a datos. |
| Seguridad backend | Spring Security + RBAC + estrategia de sesión/token definida | Autenticación y autorización reales; protección de endpoints y operaciones sensibles. La SPA no será la autoridad de seguridad. |
| Persistencia | Spring Data JPA / Hibernate | Mapeo objeto-relacional y acceso a los datos. |
| Base de datos | PostgreSQL | Persistencia central y protección de integridad mediante claves, restricciones, índices y transacciones. |
| Migraciones | Flyway o Liquibase | Evolución versionada, revisable y reproducible del esquema. Se elegirá una herramienta y se evitará mantener dos sistemas de migración en paralelo. |
| Pruebas | JUnit + Mockito; pruebas de integración; pruebas Angular | Verificación de reglas de negocio, controladores, persistencia, seguridad e interfaz. Testcontainers podrá utilizarse cuando aporte valor. |
| Infraestructura | Docker / Docker Compose | Ejecución reproducible de backend, base de datos y servicios auxiliares cuando se incorpore esta fase. |
| Exposición | Nginx + HTTPS, cuando sea necesario | Reverse proxy, terminación TLS y configuración de despliegue. |
| Cache / servicios auxiliares | Redis, opcional | Cache, colas o control de frecuencia solo cuando exista una necesidad medible. |
| Futuro cliente móvil | Kotlin | Aplicación móvil que consumirá el mismo contrato REST/OpenAPI sin duplicar las reglas de negocio. |

Decisión importante: el sistema se desarrollará como un **monolito modular** en el backend. No se comenzará con microservicios. Los límites entre módulos deben ser claros, pero desplegar inicialmente un backend cohesivo reduce complejidad operativa.

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
- Registrar mediante auditoría las operaciones relevantes sobre información sensible o procesos académicos.
- Los errores visibles para el usuario deben ser claros y comprensibles; los detalles técnicos deben quedar en logs protegidos.
- Utilizar OpenAPI como contrato oficial de la API. Los tipos y el cliente Angular generados deben provenir de ese contrato y no escribirse de nuevo a mano cuando puedan generarse de forma fiable.
- Mantener el código generado separado del código manual y no editar manualmente los archivos generados.
- Aplicar CORS con orígenes explícitos; no abrir todos los orígenes por comodidad.
- No guardar JWT, tokens de acceso, refresh tokens ni credenciales en `localStorage` o `sessionStorage`.
- No confiar en Angular Router Guards como barrera de autorización. Spring Security debe validar las autorizaciones en cada solicitud protegida.
- Evitar agregar tecnologías solo por agregarlas: cada componente debe resolver una necesidad concreta.
- Mantener versiones y dependencias reproducibles, con archivos de bloqueo de npm y Maven cuando correspondan.
- No cambiar reglas académicas institucionales por suposiciones. Si una regla depende del PEI, el SIEE u otra política institucional, debe quedar como decisión pendiente hasta contar con esa definición.

# 4. Módulos funcionales previstos

Los siguientes módulos constituyen el alcance funcional global del proyecto. No todos deben implementarse en la primera iteración; se desarrollarán siguiendo las fases y dependencias descritas más adelante.

| Módulo | Alcance |
| --- | --- |
| Usuarios y seguridad | Usuarios, autenticación, roles, permisos, sesiones/tokens, recuperación de acceso y controles de seguridad. |
| Estudiantes | Datos personales, estado, documentos, historial académico y relación con acudientes. |
| Acudientes | Registro de padres/acudientes, estudiantes asociados y acceso a información autorizada. |
| Profesores | Datos del docente, asignaturas, cursos asignados, horarios, asistencia, calificaciones y observaciones. |
| Estructura académica | Año lectivo, grados, grupos, asignaturas, periodos académicos y planes de estudio. |
| Matrículas | Proceso de matrícula, estado, historial y relación estudiante–curso/grupo por año lectivo. |
| Calificaciones | Notas por asignatura/área y periodo, promedios, recuperaciones o estrategias de apoyo cuando correspondan, definitivas, materias pendientes/perdidas y promoción. |
| Asistencia | Asistencia diaria o por sesión, ausencias justificadas/injustificadas, retardos, porcentajes y alertas según reglas institucionales. |
| Horarios | Horarios de cursos y profesores, aulas y prevención de cruces. |
| Documentos y reportes | Boletines, certificados, constancias, actas, listados y reportes administrativos. |
| Finanzas | Pensiones, pagos, facturas, cartera, becas y descuentos como módulo de evolución. |
| Comunicación | Noticias, comunicados, notificaciones, avisos y mensajería. |
| Auditoría | Registro de acciones sensibles: usuario, acción, fecha/hora, entidad afectada y valores relevantes, evitando registrar secretos. |
| Configuración | Parámetros institucionales, años y periodos lectivos, escalas, reglas académicas y configuraciones generales autorizadas. |

Los módulos deben exponer casos de uso bien definidos. No se deben construir dependencias circulares entre módulos ni permitir que la interfaz web se convierta en el lugar donde se deciden las reglas institucionales.

# 5. Roles previstos

| Rol | Funciones principales previstas |
| --- | --- |
| ADMINISTRADOR | Administración general, usuarios, configuración y operaciones autorizadas. |
| RECTOR | Consulta y gestión institucional según permisos. |
| COORDINADOR | Gestión académica y seguimiento. |
| PROFESOR | Cursos asignados, asistencia, calificaciones y funciones docentes autorizadas. |
| ESTUDIANTE | Consulta de notas, asistencia, horario y documentos autorizados. |
| ACUDIENTE | Consulta de estudiantes asociados y su información autorizada. |
| SECRETARIA | Procesos administrativos, estudiantes, matrículas y documentos según permisos. |

Los roles son una base inicial. Los permisos reales deberán definirse por caso de uso y no únicamente por nombre del rol. El modelo de autorización será **RBAC (Role-Based Access Control)**, complementado por comprobaciones de contexto cuando una acción dependa del recurso específico: por ejemplo, un profesor solo podrá modificar las calificaciones de grupos/asignaturas que tenga asignados y un acudiente solo podrá consultar estudiantes vinculados y autorizados.

Angular puede ocultar navegación, botones y opciones según el perfil para mejorar la experiencia de usuario. Eso es una medida de presentación, no de seguridad. La autorización definitiva debe ejecutarse en Spring Security y, cuando corresponda, en los servicios de negocio.

# 6. Arquitectura del backend

## 6.1. Flujo conceptual

**Angular SPA** → HTTP/HTTPS + JSON, conforme al **contrato OpenAPI** → **CORS / Spring Security / autenticación y autorización** → **Controller REST** → **Service / caso de uso** → **Repository / JPA** → **PostgreSQL**.

Angular consume los endpoints mediante un cliente TypeScript generado a partir del contrato OpenAPI. Los componentes de interfaz no deben construir SQL ni acceder directamente a repositorios; tampoco deben reproducir cálculos académicos críticos.

## 6.2. Organización interna recomendada

El backend debe organizarse por dominio funcional dentro del monolito modular. La siguiente separación de responsabilidades se aplica dentro de los módulos en los que resulte útil:

- `controller`: endpoints HTTP y traducción entre protocolo y caso de uso; no concentra reglas complejas.
- `service` / `application`: casos de uso, coordinación del flujo y reglas de negocio.
- `repository`: acceso a datos mediante JPA.
- `entity`: entidades persistentes y sus relaciones.
- `dto`: objetos de entrada/salida; no exponer las entidades JPA directamente como contrato público.
- `mapper`: conversión explícita entre DTO y entidades o modelos internos.
- `exception`: excepciones de dominio y manejo global de errores.
- `security`: autenticación, autorización, RBAC, tratamiento de tokens/cookies y protección de endpoints.
- `config`: configuración del sistema, CORS, OpenAPI, Jackson, persistencia y componentes transversales.
- `audit`: registro de acciones sensibles y trazabilidad.

## 6.3. Organización modular conceptual

Paquete raíz de ejemplo: `jackren.sistemaacademico.main` (respetar el paquete real existente salvo que se apruebe una migración explícita).

Módulos de dominio previstos:

- `estudiante`
- `acudiente`
- `profesor`
- `curso` / `grupo`
- `asignatura`
- `matricula`
- `calificacion`
- `asistencia`
- `horario`
- `documento`
- `reporte`
- `seguridad`
- `auditoria`
- `configuracion`

Los paquetes exactos pueden evolucionar, pero los límites de responsabilidad deben mantenerse. Antes de reorganizar el código ya existente, revisar referencias, pruebas y migraciones y proponer un cambio incremental.

## 6.4. Contrato OpenAPI y cliente Angular

- El contrato versionado OpenAPI 3.x será la fuente de verdad del contrato HTTP. Se mantendrá, por ejemplo, en `openapi/openapi.yaml`.
- El contrato debe definir rutas, métodos, parámetros, esquemas, DTO, campos obligatorios/opcionales, formatos, códigos HTTP, respuestas de error, seguridad requerida y ejemplos relevantes.
- El backend deberá mantenerse conforme al contrato. Si se utiliza `springdoc-openapi` para exponer documentación en ejecución, esa especificación deberá verificarse frente al contrato versionado para evitar dos fuentes contradictorias.
- OpenAPI Generator, con el generador `typescript-angular`, producirá los tipos y el cliente HTTP consumido por la SPA.
- El código generado se ubicará en una carpeta aislada, por ejemplo `src/app/core/api/generated/`, y no debe editarse manualmente. Se debe poder regenerar de forma reproducible.
- Los DTO Java seguirán existiendo en el backend y deben respetar el contrato; generar el cliente TypeScript no significa que las clases Java desaparezcan ni que la base de datos dicte directamente el contrato público.
- Los cambios incompatibles deben identificarse explícitamente, revisarse y documentarse. No se debe cambiar un endpoint o una propiedad pública solo para resolver una necesidad local de la UI sin analizar su impacto en otros clientes.

# 7. Base de datos

PostgreSQL será la base de datos principal. El modelo se diseñará antes de implementar gran parte de la lógica, pero evolucionará mediante migraciones versionadas y revisadas conforme avance el proyecto.

Requerimientos técnicos:

- Primary Keys y Foreign Keys adecuadas.
- Constraints para proteger la integridad, incluidas restricciones de unicidad y de obligatoriedad cuando correspondan.
- Índices cuando exista una necesidad real de consulta o rendimiento.
- Normalización y relaciones bien definidas; las relaciones muchos-a-muchos con atributos o reglas propias deben modelarse mediante entidades intermedias explícitas.
- Transacciones para operaciones que deban ser atómicas.
- Migraciones versionadas mediante Flyway o Liquibase. Elegir una opción y utilizarla consistentemente.
- No depender exclusivamente de validaciones Java para proteger la integridad: la base de datos debe defender sus invariantes.
- No exponer PostgreSQL directamente al navegador ni al cliente futuro.
- Separar la configuración de desarrollo, pruebas y despliegue.
- No mantener credenciales reales dentro del repositorio; utilizar variables de entorno o un gestor de secretos.
- No utilizar `ddl-auto=update` como sustituto permanente de migraciones controladas. En entornos de despliegue, el esquema debe cambiar de forma explícita y reproducible.
- Definir política de copias de seguridad, retención y pruebas de restauración antes de manejar datos institucionales reales.

Relaciones conceptuales iniciales:

- Estudiante → Acudiente, mediante una relación que pueda guardar parentesco, responsabilidad, autorización de retiro y demás atributos pertinentes.
- Estudiante → Matrícula → Curso/Grupo/Año lectivo.
- Curso/Grupo → Asignaturas o plan de estudios.
- Profesor → Asignaciones académicas.
- Matrícula → Calificaciones.
- Matrícula → Asistencia.
- Curso/Grupo + Profesor + Asignatura + espacio/periodo temporal → Horario.

Consideraciones de integridad pendientes de formalizar:

- Determinar si todo estudiante debe contar con al menos un acudiente o responsable activo y qué excepciones existen.
- Si un estudiante solo puede tener un acudiente principal, la regla debe protegerse a nivel de servicio y base de datos, considerando transacciones y concurrencia.
- Definir qué sucede al intentar desactivar o desvincular un acudiente principal.
- Evitar eliminaciones físicas que rompan relaciones históricas o académicas. Favorecer estados/inactivación cuando la política y las obligaciones de conservación lo requieran.
- Definir reglas de unicidad para documentos y correos con normalización consistente, teniendo en cuenta que ciertos campos pueden ser opcionales.

# 8. Reglas de negocio importantes

El sistema debe modelar reglas, no limitarse a guardar datos. Las reglas se validarán en el backend y, cuando corresponda, se reforzarán mediante restricciones y transacciones de base de datos.

Reglas generales:

- Una calificación debe encontrarse dentro de la escala permitida por la institución y el periodo correspondiente.
- Un estudiante debe tener una matrícula válida para recibir determinadas calificaciones y registros de asistencia.
- Un profesor debe estar asignado al curso/grupo y asignatura antes de registrar o modificar una calificación o asistencia.
- Un periodo académico puede estar abierto o cerrado. Las modificaciones posteriores al cierre requieren una política explícita de autorización, corrección y auditoría.
- No debe permitirse registrar información incompatible con un periodo cerrado.
- El sistema debe detectar conflictos de horario entre profesor, grupo y aula, de acuerdo con la granularidad temporal definida.
- Las modificaciones de información sensible deben quedar auditadas.
- Los permisos deben validarse en backend; ocultar un botón en Angular no constituye seguridad.
- Los documentos y reportes solo deben ser accesibles a usuarios autorizados y dentro del contexto académico apropiado.
- La matrícula debe conservar su historial y diferenciar estados del proceso según los casos de uso que se definan.
- Las operaciones que actualizan varias entidades relacionadas deben ser transaccionales y definir el comportamiento ante errores parciales.
- Los errores por duplicados, estados inválidos o conflictos deben convertirse en respuestas controladas, no filtrarse como excepciones técnicas.

Reglas académicas institucionales:

- No fijar una nota mínima, ponderación, regla de redondeo, número de periodos, umbral de asistencia ni criterio de promoción como una regla universal sin respaldo institucional.
- La evaluación debe poder configurarse según el SIEE y las decisiones aprobadas por la institución.
- Las decisiones deben quedar documentadas como reglas identificables, con condiciones, excepciones y pruebas.
- La aplicación debe contemplar los procedimientos de apoyo, seguimiento, recuperación o reclamación que correspondan a la política institucional y al marco normativo aplicable.

Marco colombiano: para educación preescolar, básica y media, las reglas de evaluación y promoción deben revisarse frente al Decreto 1290 de 2009, incorporado en el Decreto 1075 de 2015, sus modificaciones aplicables y el SIEE/PEI de la institución. Este documento orienta el diseño de software, pero no sustituye la validación jurídica o institucional de las reglas concretas.

Referencia normativa: [Decreto 1075 de 2015 — Ministerio de Educación Nacional, PDF oficial](https://contenidos.mineducacion.gov.co/ntg/men/pdf/decreto_1075_de_2015.pdf).

# 9. Calificaciones y proceso académico

Flujo conceptual:

**Estudiante → Matrícula → Curso/Grupo → Asignatura/Área → Periodo → Actividades/Notas → Promedios → Resultado definitivo → Decisión académica según reglas institucionales.**

El sistema deberá considerar, según el alcance que defina cada institución:

- Cálculo de promedios y resultados parciales.
- Ponderaciones o fórmulas de evaluación configurables.
- Escala institucional de valoración y su equivalencia con la escala nacional cuando aplique.
- Reglas de precisión y redondeo.
- Estrategias de apoyo o recuperaciones cuando correspondan.
- Nota o resultado definitivo.
- Identificación de áreas/asignaturas con desempeño pendiente o bajo según las categorías de la institución.
- Resultado de promoción/reprobación según reglas institucionales aprobadas.
- Generación de boletines e informes.
- Registro del periodo al que pertenece cada evaluación.
- Cierre y reapertura controlada de periodos, con permisos y auditoría.
- Correcciones de calificaciones con valor anterior, nuevo valor, actor, motivo y fecha/hora, según la política de auditoría.
- Reclamaciones y trazabilidad de su resolución cuando el flujo institucional se incluya en el alcance.

La escala nacional contempla desempeños Superior, Alto, Básico y Bajo, pero no debe asumirse que una cifra institucional específica equivale de manera universal a aprobar o reprobar. La equivalencia y los criterios de promoción deben configurarse con base en el SIEE. Evitar expresiones dispersas como `nota >= 3.0` en distintos servicios cuando el umbral deba ser configurable.

No se debe concluir que el módulo de calificaciones está completo solo porque permita guardar valores numéricos. Debe validar matrícula, asignación docente, periodo, escala, permisos y estado de cierre; calcular según reglas documentadas; conservar trazabilidad; y producir resultados reproducibles.

# 10. Seguridad

La seguridad se implementará principalmente en Spring Boot mediante Spring Security. Angular tendrá controles complementarios de experiencia de usuario, pero nunca será la autoridad final de autenticación o autorización.

## 10.1. Autenticación, autorización y contraseñas

- Implementar autenticación con una estrategia basada en tokens/sesión adecuada a la SPA y al despliegue. Documentar la decisión de arquitectura y sus amenazas.
- Aplicar RBAC y permisos específicos por caso de uso.
- Verificar en backend tanto el rol como el acceso al recurso concreto cuando corresponda; no basta con comprobar que un usuario tenga un rol general.
- Utilizar hashing de contraseñas diseñado para contraseñas, como BCrypt o Argon2, mediante bibliotecas mantenidas.
- No almacenar contraseñas en texto plano ni utilizar SHA-256 simple como función de almacenamiento de contraseñas.
- Validar las entradas en servidor y no confiar en los datos enviados por la SPA.
- Implementar cierre de sesión, expiración, revocación o rotación de credenciales/tokens según el diseño elegido.
- Proteger procesos de recuperación de acceso y evitar revelar si una cuenta existe cuando eso represente un riesgo.
- No registrar contraseñas, JWT, refresh tokens, cookies de sesión, secretos ni datos personales innecesarios en logs.

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
- Auditoría de operaciones sensibles sin incluir credenciales ni secretos.
- Control de acceso a los archivos generados, documentos y reportes.
- Límites razonables de tamaño y frecuencia para endpoints sensibles, según necesidades reales.
- Encabezados de seguridad HTTP y CSP apropiados al despliegue.
- Secretos fuera de Git; usar variables de entorno o un gestor de secretos.
- Revisión de dependencias y vulnerabilidades conocidas.
- Pruebas negativas de acceso anónimo, rol insuficiente, token vencido, token inválido, CSRF inválido y origen CORS no autorizado.

# 11. Manejo de errores y experiencia del usuario

El usuario final no debería recibir excepciones técnicas incomprensibles. El backend debe clasificar los errores y devolver respuestas consistentes; Angular debe traducirlos a mensajes claros sin exponer trazas ni detalles internos.

| HTTP | Situación | Mensaje orientado al usuario |
| --- | --- | --- |
| 400 | Datos inválidos o petición mal formada | Revisa los datos ingresados. |
| 401 | No autenticado, credencial inválida o sesión/token expirado | Debes iniciar sesión o renovar la sesión. |
| 403 | Usuario autenticado sin permisos suficientes | No tienes permiso para realizar esta acción. |
| 404 | Recurso inexistente | No se encontró el registro solicitado. |
| 409 | Conflicto con el estado actual o registro duplicado | La operación entra en conflicto con información existente. Revisa los datos y el estado del registro. |
| 500 | Error inesperado del servidor | Ocurrió un error inesperado. Intenta nuevamente. Si persiste, contacta al administrador. |

El contrato OpenAPI debe describir las respuestas de error y las estructuras que se puedan exponer de forma segura. Conviene utilizar un formato de error consistente, por ejemplo con código interno, mensaje legible, detalles de validación permitidos y un identificador de correlación; no incluir stack traces, secretos ni datos personales innecesarios.

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

Las operaciones sensibles deben dejar evidencia suficiente para diagnóstico y trazabilidad. Ejemplo conceptual:

- Usuario: identificador interno del usuario que realizó la acción.
- Acción: `MODIFICAR_NOTA`.
- Entidad: calificación.
- Registro afectado: identificador interno de la calificación y referencia académica necesaria.
- Valor anterior: valor que existía antes de la operación, cuando resulte pertinente.
- Valor nuevo: valor resultante.
- Motivo: cuando la política institucional o el tipo de operación lo exija.
- Fecha y hora: registrada en el servidor con política temporal consistente.
- Resultado de la operación: éxito o fallo cuando sea útil para auditoría.
- Correlation/request ID: identificador técnico para relacionar la acción con los logs sin exponer secretos.

La auditoría debe contemplar cambios de datos personales, relaciones de acudientes, autorizaciones de retiro, asignaciones docentes, matrícula, calificaciones, asistencia, configuración de reglas, roles y permisos. No registrar contraseñas, JWT, refresh tokens, cookies de sesión ni contenido sensible que no sea necesario para demostrar la operación.

Los registros de auditoría deben tener acceso restringido y no ser modificables por usuarios ordinarios. Definir retención y consulta según las políticas institucionales y obligaciones aplicables. Las acciones administrativas de corrección deben conservar contexto suficiente para explicar quién cambió un dato, cuándo, por qué y cuál fue el resultado.

# 13. Reportes y documentos

El sistema deberá contemplar, según el alcance priorizado:

- Boletín académico.
- Certificado de estudio.
- Constancia.
- Actas.
- Listados de estudiantes.
- Reporte de asistencia.
- Reporte de calificaciones.
- Reportes administrativos.

JasperReports u otra solución compatible con el ecosistema Java podrá evaluarse cuando el módulo de reportes esté listo. La selección no debe hacerse antes de conocer los formatos, requisitos de impresión, exportación y mantenimiento.

Los documentos generados deben obtener sus datos desde los servicios autorizados del backend. Deben respetar permisos y alcance de cada usuario, conservar consistencia con los datos académicos y evitar que Angular construya documentos oficiales a partir de cálculos no validados por el servidor. Cuando se corrija un dato que afecta un documento previamente emitido, se deberá definir si se regenera, versiona o conserva la emisión anterior según la política institucional.

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
| Migraciones | Flyway o Liquibase | Esquema versionado y repetible. Elegir una de las dos herramientas. |
| Cliente inicial | Angular + TypeScript | SPA para escritorio/navegador responsive según necesidades del producto. |
| Herramientas frontend | Node.js + npm + Angular CLI | Instalación y gestión de paquetes, scripts de desarrollo, compilación y pruebas. Seleccionar versiones compatibles con Angular. |
| HTTP reactivo | Angular HttpClient + RxJS | Comunicación asíncrona con la API y composición de flujos. |
| Seguridad | Spring Security + RBAC + cookies/tokens conforme a la estrategia adoptada | Autenticación, autorización, CORS y mitigación CSRF/XSS. |
| Testing backend | JUnit + Mockito | Pruebas unitarias de reglas de negocio y componentes. |
| Testing de integración | Spring tests; Testcontainers cuando aporte valor | Validar integración y persistencia contra PostgreSQL real. |
| Testing frontend | Herramientas de pruebas compatibles con la versión seleccionada de Angular; pruebas de componentes, servicios, guards e interceptores | Verificar interfaz y comportamiento. Usar el runner soportado por el proyecto generado y fijar la configuración. |
| Versionamiento | Git + GitHub | Historial, revisión y colaboración. |
| Infraestructura | Docker + Docker Compose | Entorno reproducible cuando corresponda. |
| Reverse proxy | Nginx, cuando sea necesario | HTTPS, enrutamiento y despliegue. |
| Cache/colas | Redis, opcional | Solo si existe necesidad demostrada. |
| Reportes | JasperReports, cuando se implemente | Generación de documentos del lado del servidor. |
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

La calidad se validará en las capas de dominio, API, base de datos, seguridad y frontend. Tener una compilación correcta no demuestra por sí solo que las reglas de negocio sean correctas.

Backend:

- Pruebas unitarias de servicios y reglas de negocio.
- Pruebas de controllers, DTO, validaciones y mapeo de errores HTTP.
- Pruebas de integración de repositorios, transacciones y restricciones.
- Testcontainers para escenarios que requieran una base PostgreSQL real, cuando aporte valor.
- Pruebas de seguridad y autorización: usuario anónimo, rol insuficiente, acceso fuera del alcance, tokens inválidos o expirados.
- Pruebas CORS para origen permitido, origen no permitido y preflight `OPTIONS`.
- Pruebas CSRF cuando la autenticación se base en cookies.
- Pruebas de concurrencia para invariantes sensibles, como unicidad del acudiente principal o matrícula duplicada, si aplica a las reglas acordadas.
- Validación de casos límite, conflictos de negocio, periodos cerrados y operaciones transaccionales.
- Pruebas de migraciones desde un esquema vacío y desde la versión anterior cuando sea pertinente.

Frontend Angular:

- Pruebas de componentes, formularios, validaciones visuales y estados de carga/vacío/error.
- Pruebas de servicios e integración con `HttpClientTesting` o el mecanismo de prueba compatible con la versión seleccionada.
- Pruebas de interceptores para verificar que las credenciales y el CSRF se adjunten solo a destinos autorizados.
- Pruebas de guards para navegación según estado de sesión y rol.
- Pruebas de presentación de `400`, `401`, `403`, `404`, `409` y `500`.
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

Git debe utilizarse desde el inicio para backend, frontend, contrato OpenAPI y documentación.

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

| Fase | Objetivo y tareas principales |
| --- | --- |
| Fase 0 — Análisis y alcance | Definir actores, casos de uso, requisitos, reglas de negocio, alcance exacto de V1, SIEE/PEI aplicable, arquitectura inicial y modelo de datos. Identificar decisiones pendientes sin inventar reglas institucionales. |
| Fase 1 — Fundación del backend | Revisar el proyecto existente; configurar Spring Boot, PostgreSQL, JPA, migraciones, estructura modular, validaciones, manejo global de errores y documentación base. Retirar secretos del código y establecer perfiles de configuración. |
| Fase 2 — Contrato API y seguridad base | Versionar OpenAPI; establecer endpoints y DTO; configurar Spring Security, autenticación, RBAC, CORS explícito, estrategia JWT/cookies, CSRF, encabezados de seguridad y auditoría inicial. Probar acceso anónimo/no autorizado. |
| Fase 3 — Fundación de Angular SPA | Crear el proyecto Angular con TypeScript, Node/npm y Angular CLI; organizar `core`, `shared` y `features`; configurar Router, HttpClient, RxJS, formularios reactivos, entorno de API y generación OpenAPI. Preparar autenticación de interfaz, guards, interceptores y manejo centralizado de errores/carga. |
| Fase 4 — Núcleo administrativo | Estudiantes, acudientes, profesores, cursos/grupos, asignaturas y matrículas. Resolver invariantes, relaciones, estados, desactivación, duplicados, permisos y auditoría. Integrar por etapas las pantallas y servicios Angular con la API. |
| Fase 5 — Académico | Periodos, asignaciones académicas, calificaciones, asistencia y horarios. Incorporar reglas configurables conforme al SIEE, cálculos, cierres, recuperación/apoyo y promoción según el alcance aprobado. |
| Fase 6 — Documentos y reportes | Boletines, certificados, constancias, actas y reportes de asistencia/calificaciones/administrativos; controlar permisos, trazabilidad y consistencia de la información emitida. |
| Fase 7 — Comunicación | Avisos, notificaciones, correo y mensajería según una necesidad concreta y la política institucional. |
| Fase 8 — Finanzas | Pensiones, pagos, facturación, cartera, becas y descuentos. Este módulo puede posponerse si no forma parte del alcance mínimo de la V1. |
| Fase 9 — Calidad e infraestructura | Completar pruebas de integración y extremo a extremo, hardening de seguridad, Docker/Compose si corresponde, backups y restauración, logs, monitorización, CI/CD y despliegue HTTPS. Esta calidad debe trabajarse desde las fases anteriores, no dejarse exclusivamente para el final. |
| Fase 10 — Evolución | Cliente móvil Kotlin consumiendo la misma API; ampliación de capacidades, sincronización u otros clientes solo cuando exista una necesidad validada. |

Los módulos financieros, comunicación avanzada, Redis y evolución móvil pueden no pertenecer al alcance funcional mínimo de la V1. Su inclusión se decidirá por prioridad, necesidad y capacidad de prueba, no por la mera existencia de una fila en el roadmap.

# 19. Orden de aprendizaje recomendado

1. **Java sólido:** POO, interfaces, excepciones, genéricos, Collections, Streams, lambdas, Optional, fechas y Maven.
2. **SQL y PostgreSQL:** CRUD, JOIN, agrupaciones, constraints, índices, transacciones, normalización y diseño relacional.
3. **Spring Boot:** controllers, services, repositories, DTO, JPA/Hibernate, validación y configuración.
4. **Seguridad:** Spring Security, hashing de contraseñas, autenticación, autorización, RBAC, JWT/cookies, CSRF, CORS y prácticas de seguridad web.
5. **OpenAPI:** diseño de contratos, schemas, respuestas, versionamiento y generación de cliente TypeScript mediante OpenAPI Generator.
6. **Angular + TypeScript:** componentes, templates, standalone components y/o módulos cuando corresponda, servicios, inyección de dependencias, Router, formularios reactivos y organización por funcionalidades.
7. **Node.js/npm y herramientas frontend:** compatibilidad de versiones, scripts, gestión de dependencias, builds reproducibles y archivos de bloqueo.
8. **RxJS y HTTP:** Observables, operadores, cancelación de solicitudes cuando corresponda, HttpClient, interceptores y estados asíncronos.
9. **Seguridad de SPA:** guards de navegación, manejo de sesión, almacenamiento de tokens, cookies seguras, CSRF/XSS y manejo de errores HTTP. Los guards son UX; la autorización real sigue en backend.
10. **Ingeniería de software:** SOLID, DRY, KISS, Clean Code, patrones, UML, arquitectura modular y diseño de APIs.
11. **Testing:** JUnit, Mockito, pruebas de integración, Testcontainers cuando aporte valor y pruebas Angular de componentes, servicios, guards e interceptores.
12. **Git y colaboración:** ramas, commits claros, revisión de cambios, documentación y regeneración controlada de código OpenAPI.
13. **Docker y despliegue:** redes, variables de entorno, HTTPS, backups y prácticas de despliegue seguro.
14. **Tecnologías avanzadas únicamente cuando el proyecto las necesite:** Redis, Nginx avanzado, CI/CD, cliente Kotlin, mensajería y optimización de rendimiento.

El aprendizaje debe acompasarse con el desarrollo: comprender lo suficiente para implementar cada fase, probarla y documentarla, en lugar de intentar dominar toda la pila antes de comenzar.

# 20. Criterios de diseño y decisiones que NO deben olvidarse

- La SPA no contiene las reglas críticas de negocio como única barrera.
- La API es la frontera de seguridad y negocio; cada operación protegida debe validar autenticación, autorización y estado de dominio.
- El contrato OpenAPI es la fuente de verdad de la superficie REST pública; el backend debe ajustarse a él y el cliente Angular se genera desde él.
- Los DTO se utilizan para controlar qué información entra y sale.
- Las entidades JPA no deben convertirse automáticamente en contratos públicos de la API.
- Las relaciones muchos-a-muchos se modelarán explícitamente cuando tengan datos o reglas propias.
- Las operaciones críticas deben ser transaccionales.
- Las restricciones de PostgreSQL deben reforzar las invariantes que puedan expresarse en la base de datos.
- La auditoría debe diseñarse desde temprano para operaciones sensibles.
- El cliente Angular no se conecta directamente con PostgreSQL ni contiene secretos de backend.
- No guardar JWT, refresh tokens ni credenciales en `localStorage` o `sessionStorage`.
- Si la autenticación usa cookies, definir controles CSRF apropiados; `HttpOnly`, `Secure` y `SameSite` son controles complementarios, no reemplazan todas las defensas.
- Configurar CORS con orígenes explícitos. Cuando se permitan credenciales, no usar un origen comodín.
- Los guards e interceptores Angular mejoran navegación y consistencia, pero no sustituyen Spring Security.
- Los errores deben tener códigos HTTP y respuestas consistentes; `401` y `403` no son intercambiables.
- Los periodos, escalas, ponderaciones y reglas de promoción deben poder configurarse cuando dependan del SIEE o las políticas institucionales.
- Redis, Nginx, Docker, CI/CD y otros componentes no se agregan por moda: se incorporan cuando el sistema los justifique.
- La arquitectura debe permitir añadir un cliente Kotlin sin reescribir la lógica de negocio del backend.
- Las versiones de Angular/TypeScript/Node/npm deben ser compatibles entre sí y deben mantenerse con dependencias bloqueadas.
- El código OpenAPI generado no se modifica manualmente; si se necesita cambiar una propiedad o servicio generado, primero se modifica el contrato o la configuración del generador.
- No implementar reglas que no estén documentadas. Para cada incertidumbre relevante, registrar la decisión pendiente, el responsable de confirmarla y el impacto sobre el diseño.
- El tratamiento de datos personales debe respetar la normativa colombiana aplicable y los principios de finalidad, necesidad, acceso restringido, seguridad y conservación correspondientes.

# 21. Contexto para futuros chats

Este apartado está escrito específicamente para que, al iniciar una nueva conversación, pueda utilizarse como contexto del proyecto. Si el proyecto continúa evolucionando, este documento debe actualizarse en lugar de crear descripciones contradictorias.

## 21.1. Resumen operativo de la versión 2.0

- **Proyecto:** Sistema de Gestión Escolar completo.
- **Versión del documento maestro:** 2.0.
- **Objetivo:** digitalizar procesos académicos, administrativos, comunicación y posteriormente finanzas/documentos.
- **Cliente inicial V1:** SPA desarrollada en Angular + TypeScript.
- **Herramientas frontend:** Node.js + npm + Angular CLI; usar versiones compatibles con la versión de Angular seleccionada.
- **Backend:** Java + Spring Boot, desarrollado habitualmente en Visual Studio Code.
- **API:** REST/JSON.
- **Contrato API:** OpenAPI 3.x versionado y tratado como fuente de verdad; OpenAPI Generator con `typescript-angular` genera el cliente/tipos consumidos por Angular.
- **Base de datos:** PostgreSQL.
- **Persistencia:** Spring Data JPA + Hibernate.
- **Migraciones:** Flyway o Liquibase, elegir una herramienta.
- **Seguridad:** Spring Security, RBAC y una estrategia documentada de autenticación por tokens/cookies; no guardar tokens en `localStorage`/`sessionStorage`; mitigar CSRF y XSS.
- **CORS:** orígenes explícitos por entorno; `http://localhost:4200` para desarrollo cuando esa sea la URL de Angular; no utilizar wildcard si se permiten credenciales.
- **Autorización:** controles definitivos en backend; guards Angular solo para navegación y experiencia de usuario.
- **Arquitectura:** monolito modular desacoplado del cliente.
- **Cliente futuro:** aplicación móvil Kotlin consumiendo la misma API.
- **Infraestructura futura:** Docker Compose, Nginx/HTTPS, CI/CD, backups y monitorización según el alcance.
- **Calidad:** JUnit, Mockito, pruebas de integración, pruebas de seguridad y pruebas Angular.
- **Principios:** SOLID/DRY/KISS/Clean Code, contratos explícitos y desarrollo por fases.
- **Marco académico:** normativa colombiana de educación escolar y reglas institucionales PEI/SIEE; no inventar valores de evaluación o promoción.

## 21.2. Estado técnico conocido del código existente — verificar antes de modificar

La siguiente información procede de una revisión previa del proyecto entregado en una conversación anterior. Debe confirmarse en el repositorio actual antes de tratarla como estado definitivo, porque el código puede haber cambiado:

- El backend existente utiliza el paquete raíz `jackren.sistemaacademico.main`, Java y Spring Boot con Maven, PostgreSQL y Spring Data JPA/Hibernate.
- El módulo inicial incluye entidades/tipos relacionados con `Estudiante`, `Acudiente`, `EstudianteAcudiente` y `Parentesco`, además de DTO, mappers, repositorios, servicios y controladores.
- Se han iniciado operaciones REST para estudiantes y acudientes y reglas para asociar estudiantes con acudientes, evitar parejas duplicadas, elegir un acudiente principal y administrar estados.
- Una auditoría anterior observó una regla amplia equivalente a `requestMatchers("/api/**").permitAll()` en la configuración de seguridad. Si sigue presente, debe considerarse un riesgo crítico: revisar todos los endpoints y sustituir el acceso anónimo por autenticación/autorización correctas antes de utilizar información real.
- Una auditoría anterior observó credenciales de base de datos escritas directamente en la configuración. Si sigue siendo así, deben rotarse, trasladarse a variables de entorno o un gestor de secretos y revisarse los commits anteriores para conocer el alcance de la exposición.
- También se recomendó migrar de una actualización automática del esquema como estrategia permanente a migraciones versionadas con Flyway o Liquibase.
- Se encontraron pruebas unitarias de servicios, pero deben volver a ejecutarse y ampliarse con pruebas de integración, persistencia, CORS, CSRF y autorización. No asumir que los reportes de una ejecución anterior prueban el estado actual.
- La relación estudiante–acudiente necesita protección consistente de la regla de acudiente principal ante concurrencia, y deben definirse las políticas para desactivar, borrar o desvincular responsables.
- La política exacta sobre unicidad de correos/documentos, campos de contacto obligatorios y retención histórica debe definirse y aplicarse de forma coherente entre servicio y base de datos.

Estos hallazgos no autorizan una reescritura automática del proyecto. Primero inspeccionar el repositorio actual, verificar cada punto y planificar correcciones incrementales con pruebas.

## 21.3. Instrucción para nuevos chats o agentes de código

Antes de escribir o modificar código:

1. Inspeccionar el repositorio real y confirmar el estado actual de backend, frontend, configuración, dependencias, pruebas y migraciones.
2. Comparar el estado real con este documento y señalar divergencias.
3. Separar los hallazgos confirmados, las recomendaciones y las decisiones pendientes de negocio.
4. Proponer un plan incremental con criterios de aceptación y riesgos antes de hacer cambios amplios.
5. No inventar reglas institucionales ni cambiar el contrato API sin actualizar OpenAPI, sus pruebas y el cliente generado.
6. No eliminar funcionalidades existentes ni reestructurar paquetes de forma masiva sin explicar el beneficio, impacto y plan de migración.
7. Mantener la lógica de negocio en Spring Boot; Angular será un cliente independiente.

# 22. Punto de partida recomendado

El siguiente paso práctico no es crear cientos de clases ni empezar a construir todas las pantallas. Es cerrar la especificación de la primera versión, auditar el estado actual y asegurar una base de desarrollo reproducible.

1. **Confirmar el alcance exacto de la V1.** Establecer qué módulos deben ser funcionales para el primer despliegue y cuáles se dejan como evolución.
2. **Definir actores y permisos iniciales.** Crear una matriz de permisos por caso de uso para ADMINISTRADOR, RECTOR, COORDINADOR, PROFESOR, ESTUDIANTE, ACUDIENTE y SECRETARIA.
3. **Documentar casos de uso y reglas de negocio.** Identificar precondiciones, resultados, excepciones, reglas de autorización y pruebas. Registrar las reglas académicas que dependan del PEI/SIEE como decisiones que deben confirmarse.
4. **Diseñar el modelo entidad-relación inicial.** Revisar especialmente estudiante–acudiente, matrícula, año lectivo, curso/grupo, asignaturas y asignaciones docentes.
5. **Auditar el backend real.** Revisar entidades, DTO, mappers, servicios, controladores, seguridad, dependencias, `application.properties`, logs y pruebas. Confirmar y resolver el acceso anónimo amplio y las credenciales en configuración si siguen presentes.
6. **Configurar PostgreSQL y migraciones.** Elegir Flyway o Liquibase, migrar el esquema de forma versionada, añadir restricciones necesarias y comprobar la restauración desde un esquema vacío.
7. **Definir el contrato OpenAPI.** Versionar rutas, DTO, seguridad, errores y ejemplos. Establecer una única fuente oficial y un proceso reproducible para generar el cliente Angular.
8. **Implementar seguridad base.** Establecer Spring Security, autenticación, RBAC, CORS explícito, estrategia de tokens/cookies, CSRF, controles XSS complementarios, auditoría y pruebas negativas.
9. **Crear la fundación Angular SPA.** Inicializar Angular con versiones compatibles de Node/npm, organizar `core`, `shared` y `features`, configurar Router, HttpClient, RxJS, formularios reactivos, interceptores, guards y manejo central de errores/carga.
10. **Generar el cliente API TypeScript.** Incorporar OpenAPI Generator, separar el resultado generado del código manual y comprobar que la aplicación compila contra el contrato.
11. **Completar un módulo de extremo a extremo.** Por ejemplo, el flujo de consulta/registro de estudiantes, incorporando backend, contrato, persistencia, seguridad, pruebas y pantalla Angular.
12. **Ampliar por módulos.** Continuar con acudientes, estructura académica, matrículas, asignaciones docentes, calificaciones, asistencia y horarios, según dependencias y alcance.
13. **Validar el ciclo académico.** Configurar SIEE, promedios, periodos, cierre, recuperación/apoyo, promoción y boletines con pruebas basadas en reglas institucionales aprobadas.
14. **Preparar documentación y despliegue.** Mantener README, ejemplos de configuración sin secretos, ejecución de pruebas, generación OpenAPI, instalación del frontend y procedimiento de migraciones/backups.
15. **Revisar antes de cerrar cada fase.** Verificar compilación backend, pruebas automatizadas, generación del cliente, build/tests Angular, seguridad, migraciones y documentación; registrar cualquier limitación pendiente.

La meta inicial es obtener un flujo pequeño pero completo, seguro y probado, desde Angular hasta PostgreSQL a través del contrato OpenAPI y la API Spring Boot. Luego se repetirá el patrón para cada módulo, evitando duplicar reglas, exponer datos indebidamente o introducir complejidad antes de que exista una necesidad.

---

## Referencias técnicas y normativas

Estas referencias sirven como punto de partida y deben volver a consultarse cuando se actualicen versiones o se cambien decisiones arquitectónicas. Fecha de revisión de enlaces: 19 de octubre de 2026.

- Angular — HTTP interceptors: https://angular.dev/guide/http/interceptors
- Angular — Route guards: https://angular.dev/guide/routing/route-guards
- Angular — Security best practices: https://angular.dev/best-practices/security
- OpenAPI Generator — TypeScript Angular generator: https://openapi-generator.tech/docs/generators/typescript-angular/
- Spring Security — CORS: https://docs.spring.io/spring-security/reference/servlet/integrations/cors.html
- Spring Security — CSRF: https://docs.spring.io/spring-security/reference/servlet/exploits/csrf.html
- OWASP — Session Management Cheat Sheet (almacenamiento de tokens): https://cheatsheetseries.owasp.org/cheatsheets/Session_Management_Cheat_Sheet.html
- OWASP — CSRF Prevention Cheat Sheet: https://cheatsheetseries.owasp.org/cheatsheets/Cross-Site_Request_Forgery_Prevention_Cheat_Sheet.html
- Ministerio de Educación Nacional — Decreto 1075 de 2015: https://contenidos.mineducacion.gov.co/ntg/men/pdf/decreto_1075_de_2015.pdf

**Documento maestro — Sistema de Gestión Escolar — Versión 2.0**
