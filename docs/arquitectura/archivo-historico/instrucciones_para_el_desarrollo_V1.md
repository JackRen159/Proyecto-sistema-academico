# Instrucciones para el Desarrollo V1

> **Documento de entrada para Claude Code (Claude Sonnet 4.6 en Antigravity).** Claude Code debe actuar como arquitecto técnico, coordinador del trabajo, planificador de tareas y revisor de calidad. La implementación posterior se realizará colaborativamente con **Gemini Flash 3.8**, mediante tareas concretas, acotadas y verificables.
>
> **Objetivo de estas instrucciones:** hacer evolucionar el proyecto existente de Sistema de Gestión Escolar sin perder el trabajo ya realizado, corregir primero los riesgos de seguridad e integridad y construir los módulos académicos por fases, de acuerdo con reglas de negocio explícitas.

---

## 1. Visión General del Proyecto

### 1.1. Resumen ejecutivo

Desarrollar un **Sistema de Gestión Escolar completo, modular, seguro y mantenible** que permita digitalizar progresivamente los procesos académicos, administrativos, financieros y de comunicación de una institución educativa.

El producto no debe convertirse en un CRUD de estudiantes. Debe incorporar reglas de negocio reales, validación, autenticación y autorización, integridad de datos, auditoría, manejo coherente de errores, reportes, pruebas, documentación y procedimientos de despliegue y recuperación.

El cliente principal de la V1 será una **Single Page Application (SPA) desarrollada con Angular + TypeScript**, gestionada con Node.js/npm. El backend Spring Boot se mantendrá independiente del cliente: la SPA consumirá la API REST y, en una evolución posterior, una aplicación móvil desarrollada con Kotlin u otros clientes podrán consumir el mismo contrato sin reescribir las reglas de negocio.

El contexto normativo de partida es el de una institución escolar colombiana de educación preescolar, básica o media. Las reglas exactas de evaluación y promoción deben parametrizarse de acuerdo con el PEI y el Sistema Institucional de Evaluación de los Estudiantes (SIEE) de la institución. No asumir que todos los colegios utilizan la misma escala de notas, ponderaciones, número de periodos, umbral de aprobación, porcentaje de asistencia o criterios de promoción.

### 1.2. Arquitectura y pila tecnológica

La arquitectura acordada es un **monolito modular con cliente web desacoplado**. No iniciar con microservicios. La SPA Angular se desplegará como cliente independiente del backend; la lógica de negocio y la autorización real permanecerán en Spring Boot.

| Capa o responsabilidad | Tecnología prevista | Decisión e intención |
|---|---|---|
| Cliente principal V1 | **Angular + TypeScript** | SPA para usuarios administrativos y académicos. Consumirá exclusivamente la API REST; nunca se conectará directamente a PostgreSQL. |
| Toolchain frontend | **Node.js + npm + Angular CLI** | Instalar dependencias, ejecutar pruebas, generar el cliente API tipado y compilar el frontend. Confirmar versiones compatibles con la versión Angular elegida. |
| Backend | Java + Spring Boot | API REST, reglas de negocio, autenticación, autorización y orquestación de casos de uso. |
| Comunicación | HTTP/HTTPS + JSON | Contrato estable entre Angular y el backend; HTTPS obligatorio en entornos desplegados sobre redes no confiables. |
| Contrato API | **OpenAPI / Swagger con prioridad alta** | Contrato documentado y exportable. A partir de la especificación se generarán modelos/interfaces TypeScript y, cuando sea conveniente, servicios cliente Angular mediante OpenAPI Generator. |
| Persistencia | Spring Data JPA + Hibernate | Acceso a PostgreSQL. Las entidades JPA no se convertirán automáticamente en contratos públicos de la API. |
| Base de datos | PostgreSQL | Fuente central de persistencia e integridad. Base de desarrollo conocida: `sistema_academico`, local en el puerto `5432`. Nunca incrustar credenciales en el repositorio. |
| Validación backend | Jakarta Bean Validation | Validación de forma en DTO, complementada con reglas de dominio y restricciones de base de datos. |
| Seguridad backend | Spring Security, JWT/tokens y RBAC | Autenticación y permisos aplicados en el backend. CORS explícito para Angular; el diseño de tokens debe considerar XSS, CSRF, expiración, renovación y revocación. |
| Seguridad frontend | Angular Router Guards, interceptores HTTP y controles XSS/CSRF | Control de navegación y manejo de peticiones para experiencia de usuario. No son sustituto de la autorización backend. No persistir JWT en `localStorage` ni `sessionStorage`. |
| Pruebas backend | JUnit + Mockito | Pruebas unitarias de reglas de negocio y servicios. |
| Pruebas de integración backend | Spring Boot Test y Testcontainers cuando aporten valor | Comprobar controladores, seguridad y persistencia real en PostgreSQL. |
| Pruebas frontend | Herramientas de pruebas configuradas por la versión de Angular CLI seleccionada | Probar componentes, servicios HTTP, interceptores, guards y flujos de interfaz; añadir E2E para recorridos críticos cuando aporte valor. |
| Control de versiones | Git + GitHub | Cambios pequeños, trazables y revisables; frontend y backend pueden residir en el mismo repositorio si esa es la organización actual. |
| Infraestructura futura | Docker + Docker Compose | Entorno reproducible para base de datos, backend y, si conviene, frontend. |
| Exposición futura | Nginx + HTTPS | Servir la SPA, configurar reverse proxy y transporte seguro en despliegues donde aplique. |
| Caché y servicios auxiliares | Redis opcional | No incorporarlo hasta que exista un caso de uso medible para caché, colas o control de frecuencia. |
| Reportes | JasperReports u otra solución Java compatible | Evaluar en la fase de reportes, no en la primera iteración técnica. |
| Cliente futuro | Kotlin para móvil | Debe consumir la misma API OpenAPI/REST. |

**Versiones observadas en el backend existente:** Java 21 (en registros previos, Eclipse Adoptium 21.0.8.9) y Spring Boot 4.1.1. Confirma las versiones reales en `pom.xml`, Maven Wrapper y el entorno antes de hacer cambios. Para el frontend, confirma versiones compatibles vigentes de Angular, Node.js y npm mediante la documentación oficial. No actualices versiones principales ni sustituyas dependencias por preferencia personal; cualquier actualización debe estar justificada, documentada y probada.

**Contrato API como fuente de verdad:** los DTO públicos y endpoints del backend deben describirse en OpenAPI. Utiliza una herramienta compatible con la versión real de Spring Boot (por ejemplo, springdoc-openapi si su compatibilidad está confirmada) y evalúa OpenAPI Generator con el generador `typescript-angular`. Los archivos generados no deben editarse manualmente; define un comando reproducible de regeneración y revisa los cambios generados en cada cambio incompatible del contrato.

### 1.3. Principios rectores

1. Desarrollar por fases: **analizar → planificar → implementar → probar → revisar → documentar**.
2. No intentar construir todo el sistema en una sola entrega ni crear cientos de clases antes de cerrar el modelo funcional.
3. Priorizar reglas de negocio, seguridad, integridad y arquitectura antes que el número de pantallas o endpoints.
4. Separar cliente, API, servicios de dominio y persistencia.
5. Aplicar POO, SOLID, DRY, KISS y Clean Code de forma práctica, no como excusa para sobrearquitecturar.
6. Utilizar las Collections estándar de Java en el sistema real. Las estructuras de datos implementadas manualmente pertenecen a los ejercicios académicos, no a este sistema de producción.
7. No conectar Angular directamente a PostgreSQL; toda operación debe pasar por la API REST.
8. Auditar las operaciones sensibles y proteger datos personales.
9. Proteger la integridad tanto en el servicio como en PostgreSQL: las validaciones de Java/TypeScript no sustituyen las reglas del backend ni las restricciones de base de datos.
10. Mantener mensajes de error comprensibles para el usuario y detalles técnicos controlados en logs.
11. No agregar tecnologías por moda. Cada dependencia nueva debe resolver una necesidad concreta.
12. No modificar o eliminar grandes partes del código existente sin explicar el motivo, establecer una línea base y obtener una decisión explícita cuando el cambio sea destructivo o de alcance amplio.
13. El cambio estratégico de la capa de presentación a Angular no es permiso para reescribir sin necesidad los servicios, entidades, reglas académicas, esquema PostgreSQL, migraciones ni transacciones del backend. Cualquier cambio en esas áreas debe responder a un requisito independiente, preservar compatibilidad y contar con pruebas.

### 1.4. Estado inicial conocido y alcance del análisis

El paquete principal observado es `jackren.sistemaacademico.main`, con una organización general por `config`, `controller`, `dto`, `entity`, `exception`, `mapper`, `repository` y `service`. La clase principal observada es `jackren.sistemaacademico.main.MainApplication`.

La funcionalidad implementada se concentra en:

- Estudiantes: controladores y operaciones de consulta, registro y gestión de estado/eliminación.
- Acudientes: operaciones de registro, consulta y gestión.
- Relación estudiante–acudiente: entidad intermedia, parentesco y atributos de la relación.
- Validaciones mediante DTO.
- Servicios, repositorios, mappers y un manejador global de errores.
- Configuración inicial de Spring Security.
- Pruebas unitarias de servicios.

Entre los recursos conocidos están `GET /api/estudiantes`, `GET /api/estudiantes/{id}` y `POST /api/estudiantes`. No cambies nombres ni rutas públicas sin inventariar todos los controladores y clientes que las consumen. La asignación académica, matrícula, calificaciones y otros módulos descritos en conversaciones o diagramas pueden estar diseñados conceptualmente, pero no se deben considerar implementados hasta encontrarlos en el repositorio. El archivo comprimido auditado anteriormente estaba centrado en el backend; Claude debe verificar si ya existe una carpeta Angular, `package.json`, `angular.json`, pruebas frontend y configuración de Node/npm. No asumir que el cliente Angular ya está creado.

**Advertencia sobre la verificación previa:** en la auditoría se identificó un reporte anterior de 24 pruebas unitarias aprobadas, pero no fue posible volver a ejecutar la suite en ese entorno porque Maven Wrapper no pudo descargar la distribución requerida. Claude Code debe verificar el estado actual con los archivos reales y los comandos disponibles, no asumir que todas las pruebas pasan hoy.

---

## 2. Requerimientos Técnicos y Arquitectura

### 2.1. Arquitectura por capas y flujo de datos

Flujo esperado:

```text
Navegador
  └── Angular SPA (TypeScript, RxJS, Router)
        │ HTTPS/HTTP + JSON; contrato OpenAPI
        ▼
      Spring Boot REST Controller
        │ valida la solicitud y delega
        ▼
      Service / caso de uso
        │ aplica autorización de dominio y reglas de negocio
        ▼
      Repository (Spring Data JPA)
        │ ORM / consultas / transacciones
        ▼
      PostgreSQL
```

Angular obtiene tipos/contratos desde la especificación OpenAPI y consume la API mediante `HttpClient` y los servicios generados o servicios de aplicación que encapsulen ese cliente. La respuesta vuelve como DTO, con un código HTTP apropiado. El cliente no comparte entidades JPA, no implementa la lógica académica como autoridad y no lee/escribe directamente la base de datos.

Responsabilidad de las capas:

- `controller`: endpoints HTTP, validación de entrada, códigos HTTP y delegación. No colocar cálculos académicos ni reglas complejas aquí.
- `service`: casos de uso, validaciones de negocio, transacciones y coordinación de repositorios y mappers.
- `repository`: persistencia y consultas. No convertirlo en una capa donde se decidan políticas institucionales.
- `entity`: estado persistente, relaciones e invariantes que correspondan al modelo.
- `dto`: modelos de solicitud/respuesta distintos de las entidades; deben publicarse correctamente en OpenAPI.
- `mapper`: conversiones explícitas entre DTO y entidad; impedir la exposición accidental de campos internos.
- `exception`: excepciones de negocio y representación coherente del error.
- `config`: configuración técnica, propiedades y beans.
- `security`: autenticación, autorización, CORS y configuración de filtros; puede ser un paquete dedicado o un módulo claramente delimitado.
- `audit`: diseño y registro de operaciones relevantes, integrado sin duplicar la lógica de auditoría en todos los controladores.
- `Angular core`: autenticación/sesión, configuración API, cliente generado, interceptores HTTP, tratamiento global de errores, guards y layout principal.
- `Angular features`: páginas/componentes, formularios y servicios de aplicación específicos de cada dominio funcional.
- `Angular shared`: componentes visuales reutilizables, directivas, pipes y validadores genéricos; no colocar aquí políticas académicas del servidor.

Usar inyección por constructor en Java. Usar `@Transactional` para operaciones que requieren atomicidad; usar `readOnly = true` en consultas transaccionales cuando sea apropiado. Las restricciones y cambios múltiples en una operación de negocio deben persistirse de forma atómica.

En Angular, preferir los patrones modernos y soportados por la versión elegida, mantener TypeScript en modo estricto, utilizar formularios reactivos para formularios de negocio y RxJS para composición asíncrona. Preferir interceptores funcionales si la versión elegida los admite de forma estable, siguiendo la recomendación de la documentación oficial de Angular. No introducir una biblioteca global de estado como NgRx hasta demostrar que la complejidad del estado justifica esa dependencia.

### 2.2. Organización modular

La estructura actual está separada por capas globales. No realizar un refactor total de forma automática. Primero inventariar dependencias y completar prioridades urgentes. A medida que crezcan los módulos, orientar la organización hacia paquetes por dominio, manteniendo límites claros entre API, servicios y persistencia. Ejemplo de destino conceptual:

```text
jackren.sistemaacademico.main
├── config
├── security
├── shared
│   ├── exception
│   ├── api
│   └── validation
├── estudiante
│   ├── controller
│   ├── service
│   ├── repository
│   ├── entity
│   ├── dto
│   └── mapper
├── acudiente
├── profesor
├── estructuraacademica
├── matricula
├── calificacion
├── asistencia
├── horario
├── documento
├── reporte
├── usuario
├── auditoria
├── comunicacion
├── finanzas
└── configuracion
```

Este árbol es una **dirección objetivo**, no una orden de mover todos los archivos inmediatamente. Claude debe evaluar el coste y riesgo del refactor y proponer migraciones incrementales, sin duplicar clases ni romper los endpoints actuales.

Estructura conceptual del frontend Angular, separada del paquete Java:

```text
frontend/ (o la carpeta que confirme el repositorio)
├── package.json
├── angular.json
├── tsconfig.json
├── src/
│   ├── environments/                 # Solo configuración pública, nunca secretos
│   └── app/
│       ├── core/
│       │   ├── api/generated/         # Código OpenAPI autogenerado; no editar a mano
│       │   ├── auth/                  # Estado de autenticación y usuario actual
│       │   ├── guards/                # Guards de autenticación/RBAC para navegación
│       │   ├── interceptors/           # Auth/CSRF/error/correlación cuando aplique
│       │   ├── services/               # Servicios de aplicación transversales
│       │   └── layout/                 # Shell, menú y navegación general
│       ├── shared/                    # Componentes, directivas, pipes y validadores reutilizables
│       ├── features/
│       │   ├── dashboard/
│       │   ├── estudiantes/
│       │   ├── acudientes/
│       │   ├── profesores/
│       │   ├── estructura-academica/
│       │   ├── matriculas/
│       │   ├── calificaciones/
│       │   ├── asistencia/
│       │   ├── horarios/
│       │   ├── documentos-reportes/
│       │   ├── usuarios-seguridad/
│       │   ├── comunicacion/
│       │   ├── finanzas/
│       │   └── configuracion/
│       ├── app.routes.ts
│       └── app.config.ts
└── tests/configuración E2E cuando corresponda
```

Este árbol es orientativo, debe adaptarse a la organización real y a la versión de Angular. No es obligatorio crear todos los `features` al inicio ni crear NgModules si la versión/proyecto usa componentes standalone. Organizar por dominio y cargar de forma diferida las áreas grandes cuando aporte valor. Mantener los artefactos generados separados del código escrito a mano.

### 2.3. Módulos funcionales previstos

El mapa funcional completo del sistema comprende:

1. **Usuarios y seguridad:** cuentas, autenticación, roles, permisos, sesiones/tokens, recuperación de acceso y controles de seguridad.
2. **Estudiantes:** datos personales, documentos, estado, historial académico y relaciones con acudientes.
3. **Acudientes:** datos de padres/responsables, estudiantes asociados y acceso exclusivamente a información autorizada.
4. **Profesores:** datos, asignaturas, cursos asignados, horarios, asistencia, calificaciones y observaciones.
5. **Estructura académica:** año lectivo, grados, cursos/grupos, asignaturas, periodos académicos y planes de estudio.
6. **Matrículas:** proceso de matrícula, estados, historial y relación estudiante–curso/grupo/año lectivo.
7. **Calificaciones:** notas por asignatura y periodo, promedios, recuperaciones, definitivas, asignaturas no aprobadas y promoción según criterios institucionales.
8. **Asistencia:** registros diarios o por sesión, faltas justificadas/injustificadas, retardos, porcentajes y alertas.
9. **Horarios:** horarios de cursos y profesores, aulas y prevención de cruces.
10. **Documentos y reportes:** boletines, certificados, constancias, actas, listados y reportes académicos/administrativos.
11. **Finanzas, fase posterior:** pensiones, pagos, facturas, cartera, becas y descuentos.
12. **Comunicación:** noticias, comunicados, avisos, notificaciones y mensajería.
13. **Auditoría:** usuario, acción, fecha/hora, entidad afectada y valores relevantes en cambios sensibles.
14. **Configuración:** parámetros institucionales, periodos, reglas académicas y configuraciones generales.

El sistema debe avanzar por fases. La lista completa define la visión, no el alcance obligatorio de una sola versión. Claude debe proponer y validar con el propietario del proyecto el alcance concreto de V1 y marcar explícitamente qué queda fuera.

### 2.4. Roles previstos y autorización

Roles iniciales de referencia:

- `ADMINISTRADOR`: administración general, usuarios, configuración y operaciones autorizadas.
- `RECTOR`: consulta y gestión institucional según permisos.
- `COORDINADOR`: gestión académica y seguimiento.
- `PROFESOR`: acciones docentes y acceso a cursos/asignaturas asignados.
- `ESTUDIANTE`: consulta de notas, asistencia, horarios y documentos autorizados propios.
- `ACUDIENTE`: consulta de estudiantes asociados y de la información autorizada.
- `SECRETARIA`: procesos administrativos, estudiantes, matrículas y documentos según permisos.

Los nombres son una base inicial; los permisos deben definirse por caso de uso y no solo por rol. Utilizar **RBAC (Role-Based Access Control)** con comprobaciones específicas de recurso. Por ejemplo, un docente no puede consultar ni modificar libremente cualquier matrícula solo porque tiene el rol `PROFESOR`: debe verificarse la asignación y el contexto correspondiente.

La matrícula es un proceso administrativo y **SECRETARIA** es el actor recomendado para registrar/formalizar la matrícula, sujeto a la autorización institucional. No asignar automáticamente toda operación al administrador.

La autorización debe ejecutarse en backend. Ocultar botones en Angular no es una medida de seguridad. Estudiantes y acudientes necesitan además reglas de acceso por propietario/relación para que solo consulten información asociada con su identidad y relaciones vigentes.

### 2.5. Reglas de negocio de estudiantes, acudientes y su relación

#### Entidades actuales

El modelo actual incluye `Estudiante`, `Acudiente`, `EstudianteAcudiente` como entidad intermedia y `Parentesco` como enumeración. La relación intermedia es correcta en concepto porque permite datos propios del vínculo, como parentesco, indicador de acudiente principal, convivencia o autorización de retiro (confirmar los nombres exactos en el código fuente).

Mantener una relación muchos-a-muchos mediante entidad intermedia explícita. No reemplazarla por una lista de acudientes serializada en un campo de texto.

#### Reglas que ya existen o se intentan aplicar en el servicio

Verificar el código actual antes de asumir que siguen presentes:

- No asociar un estudiante inexistente.
- No asociar un acudiente inexistente.
- No crear un vínculo con estudiante inactivo.
- No crear un vínculo con acudiente inactivo.
- No duplicar el par estudiante–acudiente.
- Al crear el primer vínculo, asignar al acudiente el carácter de principal según la regla existente.
- Al cambiar el principal, desmarcar el principal anterior.
- No permitir ciertas desvinculaciones del principal cuando existan otros acudientes si no se ha sustituido primero.

#### Reglas y vacíos que deben resolverse

- La unicidad del acudiente principal no está garantizada solo por lógica Java. Resolver concurrencia y agregar una defensa de integridad en PostgreSQL, por ejemplo un índice único parcial sobre `estudiante_id` para relaciones donde `es_principal = true`, si el esquema y las capacidades de la versión usada lo permiten.
- Definir si cada estudiante debe tener al menos un acudiente/responsable y en qué circunstancias se permiten excepciones. No codificar una regla universal sin validarla con el propietario y los requisitos institucionales.
- El servicio actual permite retirar el único vínculo en ciertas condiciones; decidir si debe bloquearse para estudiantes menores o si se requiere un flujo excepcional documentado.
- Antes de desactivar o eliminar un acudiente, revisar si es principal, si tiene estudiantes asociados y si deben reasignarse responsabilidades o autorizaciones.
- Diferenciar el contacto principal, el representante/responsable legal y la autorización para retirar al estudiante. No son conceptos necesariamente equivalentes.
- Revisar el enum `Parentesco`: admite tipos comunes y una opción residual, pero validar las categorías que requiera la institución, incluidos tutor/representante legal y personas autorizadas. No agregar opciones de forma indiscriminada.
- Registrar quién modificó la relación y cuándo, sobre todo si cambia el acudiente principal, el parentesco o la autorización de retiro.
- Mantener claves foráneas, unicidad del vínculo y transacciones consistentes. Las excepciones de la base de datos deben transformarse en errores controlados.

#### Eliminación, desactivación e historial

Las operaciones de borrado físico pueden fallar debido a claves foráneas o destruir contexto necesario cuando existan matrículas, notas, asistencia o auditoría. Para las operaciones administrativas habituales, preferir desactivación o cambio de estado; reservar el borrado físico para casos excepcionales, autorizados y compatibles con la política de conservación y las obligaciones aplicables. Una confirmación enviada por el cliente no sustituye el permiso de autorización.

Los registros desactivados deben tratarse consistentemente en búsquedas, asociaciones y reportes. Determinar qué datos se conservan y por cuánto tiempo mediante decisiones documentadas de protección de datos; no inventar periodos legales de conservación.

### 2.6. Validación de datos y restricciones de integridad

Actualmente se utilizan anotaciones como `@NotBlank`, `@Size`, `@NotNull`, `@Past` y `@Email` en DTO de estudiantes/acudientes. La validación debe contemplar tres niveles:

1. **Validación de entrada:** formato, obligatoriedad y longitudes en DTO con Jakarta Bean Validation.
2. **Validación de negocio:** existencia, estado, permisos, relaciones válidas, ventanas de fechas, duplicidades y condiciones de transición en servicios.
3. **Integridad de persistencia:** `NOT NULL`, `UNIQUE`, `FOREIGN KEY`, `CHECK` e índices cuando correspondan.

Puntos por revisar:

- Normalizar espacios en nombres, documentos y campos donde sea apropiado antes de comparar o almacenar.
- Definir la normalización de documentos y correos. Evitar duplicados por diferencias irrelevantes de mayúsculas/minúsculas o espacios cuando la política así lo indique.
- Mantener restricciones de unicidad en PostgreSQL; una consulta previa en Java no evita una carrera entre dos solicitudes simultáneas.
- No imponer obligatoriedad del correo electrónico si la institución no lo requiere.
- No inventar edad mínima/máxima universal de estudiante. La admisión debe obedecer al nivel, la política y los requisitos aplicables.
- `@Past` garantiza una fecha pasada, pero por sí sola no garantiza que la edad sea válida para cualquier proceso. Añadir restricciones de negocio solo si están fundamentadas.
- Revisar la obligatoriedad y finalidad de cada campo personal para cumplir el principio de minimización.

### 2.7. Modelo conceptual de datos académico

Relaciones iniciales que deben definirse y evolucionar por migraciones:

- Estudiante ↔ Acudiente mediante `EstudianteAcudiente`.
- Estudiante → Matrícula → Año lectivo / curso / grupo.
- Estructura académica: año lectivo → grados/grupos, asignaturas y periodos.
- Profesor → asignaciones académicas.
- Asignación académica ↔ curso/grupo + asignatura + profesor, con reglas para evitar duplicados y conflictos según política.
- Matrícula → calificaciones y asistencia.
- Curso/grupo + profesor + asignatura → horario, con validación de cruces de profesor, grupo y aula.

No poner simplemente un `curso` permanente en `Estudiante`: el estudiante progresa entre años, puede cambiar de grupo, retirarse o repetir. La entidad `Matricula` debe conservar la participación académica en un año lectivo y sus transiciones de estado.

#### Matrícula

Definir antes de implementar:

- Diferencia entre solicitud/admisión y matrícula formalizada, si aplica al alcance.
- Año lectivo, grado, grupo, fechas y estado.
- Restricciones contra duplicidad de matrícula según la política institucional.
- Cambios de grupo, traslado, retiro y conservación histórica.
- Consecuencias de cambiar o retirar la matrícula para notas, asistencia, informes y acceso.
- Condiciones para matricular al estudiante en el año lectivo siguiente.
- Roles autorizados en cada transición, con `SECRETARIA` como actor administrativo recomendado.

#### Asignación académica

El modelo debe vincular profesor, grupo/curso y asignatura. Antes de registrar una nota, comprobar que el estudiante tenga matrícula pertinente y que el profesor/usuario esté autorizado en la asignación correspondiente. Verificar duplicados y conflictos para el mismo curso y asignatura; las reglas exactas de múltiples profesores, co-docencia y periodos deben documentarse antes de restringirlas.

#### Calificaciones, evaluación y promoción

No tratar las calificaciones solo como números sin contexto. El diseño debe contemplar:

- Escala institucional y sus límites.
- Áreas/asignaturas, periodos y matrícula del estudiante.
- Actividades evaluativas y ponderaciones cuando el SIEE las utilice.
- Cálculo de resultados parciales, promedio y definitiva.
- Reglas de redondeo.
- Autoevaluación y estrategias de valoración integral cuando formen parte del sistema institucional.
- Recuperaciones/planes de apoyo según el SIEE.
- Cierre y reapertura controlada de periodos.
- Autorización para registrar, modificar o aprobar notas.
- Historial de correcciones, usuario responsable, fecha/hora, valor anterior, nuevo valor y motivo cuando corresponda.
- Recalcular o versionar informes si una nota cambia después de generar un boletín.
- Resultados de promoción/reprobación calculados según reglas institucionales configuradas, sin codificar umbrales arbitrarios como una constante dispersa por los servicios.

La escala nacional incluye los desempeños **Superior, Alto, Básico y Bajo**, pero cada institución define su escala y equivalencia según su SIEE. No asumir universalmente que una nota igual o superior a `3.0` aprueba, que todos usan el mismo número de periodos o las mismas ponderaciones.

#### Asistencia

Definir:

- Registro diario o por sesión/curso, según el modelo institucional.
- Estados de asistencia: presente, ausencia, retardo y otros necesarios.
- Flujo de justificación, adjuntos si aplican y quién puede aprobarla.
- Corrección de una asistencia ya cerrada, con auditoría.
- Reportes por estudiante, curso, asignatura y periodo.
- Cálculo de porcentajes conforme a los parámetros institucionales.
- Interacción de asistencia con la promoción solo cuando el criterio institucional aplicable lo establezca.
- Consideración de ajustes pedagógicos y flexibilización curricular para estudiantes con discapacidad conforme a la normativa y políticas aplicables.

No codificar un único umbral de asistencia para todos los colegios.

#### Horarios

Comprobar conflictos de horario entre profesor, grupo y aula; definir zona horaria y precisión temporal; decidir si existen excepciones de co-docencia o uso compartido de espacios. Las validaciones deben considerar concurrencia y persistencia, no limitarse a alertas visuales del cliente.

### 2.8. Seguridad web, autenticación JWT, CORS y autorización

**Hallazgo crítico del análisis previo:** la configuración contenía una regla equivalente a `.requestMatchers("/api/**").permitAll()`, que dejaba accesible sin autenticación la API bajo `/api/**`. Verifica el código real y elimínala como regla global. Esto puede exponer datos personales y permitir altas, modificaciones, desactivaciones o eliminaciones sin autorización.

#### 2.8.1. Autenticación y autorización del backend

- Implementar autenticación antes de exponer el sistema a datos reales. Definir el ciclo de vida completo del JWT/token: emisión, firma y algoritmo aprobado, validación, `issuer`, `audience` cuando corresponda, expiración (`exp`), uso/validación de `nbf` cuando se incluya, renovación, rotación, cierre de sesión y revocación/invalidación.
- Confirmar si la propia aplicación emitirá JWT o se integrará con un proveedor de identidad. No inventar infraestructura de autenticación sin comparar complejidad, mantenimiento y requisitos de V1.
- Si Spring Security valida JWT como Bearer token, utilizar soporte oficial compatible con la versión real (por ejemplo, Resource Server JWT) en lugar de construir validación criptográfica manual.
- Crear modelo de usuarios, roles y permisos; no confundir las personas (estudiantes, acudientes, docentes) con las credenciales de acceso.
- Definir qué rutas son realmente públicas. Permitir solo excepciones puntuales, explícitas y justificadas. Cualquier endpoint no incluido en una excepción debe exigir autenticación y autorización según corresponda.
- Añadir autorización por endpoint y operación sensible, con controles de propiedad/relación sobre registros. Validar permisos de lectura, actualización, desactivación y eliminación por separado.
- Aplicar RBAC y, cuando corresponda, control de acceso al recurso: un docente solo opera en asignaciones autorizadas; un estudiante/acudiente solo consulta datos asociados y permitidos.
- Usar hashing adaptativo de contraseña, como Argon2id o BCrypt con parámetros adecuados. Nunca texto plano ni SHA-256 simple para almacenar contraseñas.
- No almacenar ni registrar contraseñas, tokens ni secretos en logs. No incluir credenciales, secretos de firma ni claves privadas en el cliente Angular ni en archivos versionados.
- Añadir pruebas con usuarios autenticados y no autenticados; comprobar respuestas `401` y `403`, además de acceso cruzado entre estudiantes/acudientes.

#### 2.8.2. Decisión de almacenamiento de token y mitigación de XSS/CSRF

Claude debe producir una decisión de arquitectura documentada antes de escribir el flujo definitivo de autenticación. Debe comparar al menos las alternativas siguientes y elegir una en función de complejidad y seguridad; no mezclar ambas sin definir responsabilidades:

1. **Token Bearer en memoria + cookie HttpOnly para renovación:** mantener el access token de corta duración solo en memoria de la SPA (no persistirlo en `localStorage`/`sessionStorage`) y almacenar el refresh token o identificador de sesión en cookie `HttpOnly`, `Secure` en HTTPS y `SameSite` adecuado. Implementar protección CSRF en los endpoints que dependan de cookies, especialmente renovación y cierre de sesión. Definir rotación y revocación del refresh token.
2. **JWT/sesión transportados en cookie HttpOnly:** el navegador adjunta la cookie automáticamente y Angular no puede leer el token ni agregarlo manualmente como `Authorization: Bearer`. Configurar `HttpOnly`, `Secure`, `SameSite` y alcance de cookie apropiados, y habilitar protección CSRF del lado servidor mediante un token anti-CSRF y un encabezado validado. Esta alternativa debe probarse con CORS y `withCredentials` cuando frontend y API tengan distintos orígenes.
3. **Backend-for-Frontend (BFF):** evaluar si conviene un backend de sesión que gestione credenciales/tokens y exponga a Angular cookies seguras, sobre todo si el despliegue futuro aumenta los riesgos o se integra con un proveedor de identidad. No incorporar un BFF solo por moda; documentar coste y beneficio.

**Regla predeterminada:** no guardar access JWT ni refresh JWT en `localStorage` ni `sessionStorage`. Son accesibles a cualquier JavaScript ejecutado en el mismo origen; una vulnerabilidad XSS puede robarlos. Si se considera cualquier otro almacenamiento, justificar el riesgo explícitamente y obtener aprobación del propietario.

Medidas contra XSS:

- Aprovechar el escape/encapsulado de plantillas Angular y no desactivarlo sin justificación revisada.
- Evitar `innerHTML`, `bypassSecurityTrustHtml` y métodos `bypassSecurityTrust*` salvo que exista un caso validado, sanitización adecuada y revisión explícita.
- Tratar los datos de API, datos de usuario y almacenamiento del navegador como no confiables.
- No construir HTML concatenando datos ingresados; no ejecutar código recibido del backend.
- Mantener dependencias actualizadas y revisar vulnerabilidades; considerar una Content Security Policy (CSP) adecuada al despliegue, sin introducir políticas que rompan Angular sin probar.
- No considerar guards, ocultamiento de botones ni validación de formularios en el cliente como barreras de seguridad.

Medidas contra CSRF:

- Determinar la configuración CSRF a partir del mecanismo real de autenticación, no desactivarla de forma automática.
- Si la autenticación usa cookies que el navegador adjunta automáticamente, habilitar protección CSRF adecuada en Spring Security. Usar un token CSRF y verificar su transmisión/validación en solicitudes que cambien estado.
- Angular incluye mecanismos XSRF para ciertos escenarios same-origin; no asumir que el comportamiento predeterminado cubre automáticamente llamadas cross-origin a otra URL/puerto. Configurar y probar explícitamente la integración Angular–Spring Security para el origen y mecanismo escogidos.
- Si el backend utiliza exclusivamente Bearer tokens que solo se adjuntan explícitamente en el encabezado `Authorization` y no dependen de cookies automáticas, documentar el análisis de riesgo CSRF. Aun así, proteger los endpoints de cookies de renovación/logout si existen.
- Ninguna solicitud `GET` debe modificar estado.

#### 2.8.3. CORS explícito para Angular

El cliente de desarrollo utilizará normalmente `http://localhost:4200` y el backend Spring Boot el puerto de su ejecución local (confirmar configuración real, por ejemplo `8080`). Debido a que son orígenes distintos, configurar CORS explícitamente en Spring Security/Spring MVC y comprobar el flujo de preflight `OPTIONS`.

- Permitir el origen exacto de desarrollo `http://localhost:4200`; parametrizar orígenes por entorno y añadir únicamente los dominios reales de despliegue que se aprueben.
- Declarar solo los métodos HTTP necesarios (`GET`, `POST`, `PUT`, `PATCH`, `DELETE`, `OPTIONS` según endpoints reales) y encabezados necesarios, incluidos `Content-Type`, `Authorization` si el esquema Bearer lo usa, y encabezados CSRF si se usan.
- Si se usan cookies/`withCredentials`, habilitar credenciales de forma explícita (`allowCredentials`) y nunca combinar credenciales con `Access-Control-Allow-Origin: *`. Utilizar lista exacta de orígenes.
- Procesar CORS antes de la autorización de Spring Security para que los preflight no sean rechazados por falta de sesión/token.
- No usar CORS como sustituto de autenticación o autorización: CORS restringe el comportamiento de navegadores, no protege la API frente a clientes no navegador.
- Añadir pruebas de configuración para origen permitido, origen no permitido, métodos, encabezados, preflight y cookies si aplica.

#### 2.8.4. Integración de autenticación Angular

- Crear un `AuthService`/servicio equivalente para login, logout, renovación si se usa, carga de identidad actual y permisos/roles.
- Usar un interceptor HTTP para añadir `Authorization: Bearer ...` **solo si** la estrategia elegida transmite el access token en ese encabezado. No intentar leer un JWT guardado en cookie HttpOnly. No enviar credenciales/tokens a dominios distintos al backend configurado.
- Si la autenticación es mediante cookie, configurar las solicitudes con credenciales cuando sean necesarias y el servidor lo permita; crear el interceptor/flujo CSRF apropiado, sin extraer el token de autenticación de la cookie HttpOnly.
- Evitar ciclos infinitos de renovación ante `401`; excluir login/refresh de reintentos automáticos cuando corresponda y limpiar el estado de sesión de forma controlada.
- Implementar `CanActivate`/`CanActivateChild` u otros guards Angular para mejorar la navegación por autenticación y roles. **Los guards no son seguridad real**: el backend debe verificar todos los permisos.
- Diferenciar en la interfaz `401` (sesión ausente/expirada), `403` (sin permiso), `404` (recurso no encontrado), `409` (conflicto de negocio) y `500` (error inesperado) sin filtrar internals.

#### 2.8.5. Reglas adicionales

- Usar HTTPS en despliegues sobre redes no confiables.
- Controlar errores de autenticación para no filtrar detalles sensibles.
- Evaluar límites/rate limiting cuando exista exposición externa, sin agregar Redis antes de justificarlo.
- Revisar dependencias, cabeceras de seguridad y configuración de cookies por entorno; no asumir que un control sustituye al resto.

### 2.9. Secretos y protección de datos personales

**Hallazgo crítico del análisis previo:** el archivo de configuración incluido con el proyecto contenía una credencial de base de datos. No copiarla a documentación, logs, salidas de herramientas ni prompts. El propietario debe rotarla cuanto antes.

- Mover credenciales a variables de entorno o a un almacén de secretos apropiado.
- Revisar `.gitignore`, archivos de configuración por perfil y la historia de Git; cambiar el archivo actual no elimina un secreto de commits anteriores.
- No reescribir el historial de Git de forma destructiva sin coordinarlo con el propietario. Si el secreto estuvo expuesto, rotarlo primero; planificar la purga histórica con cuidado.
- Usar permisos mínimos para el usuario de base de datos; no utilizar automáticamente una cuenta superusuario en despliegues.
- Minimizar datos personales en DTO, respuestas, logs y copias de seguridad.
- Definir finalidad, acceso, actualización/rectificación, conservación, eliminación y acceso de titulares de datos conforme a las obligaciones aplicables.
- Aplicar controles reforzados a información de niños, niñas y adolescentes.
- Registrar acciones sensibles, pero nunca registrar contraseñas, tokens ni datos innecesarios.
- No declarar cumplimiento jurídico solo por incorporar controles técnicos. Identificar requisitos pendientes de validación por la institución o asesoría competente.
- Recordar que todo archivo/configuración incluido en el bundle Angular será visible para el navegador. Las variables de entorno de build del frontend no son un almacén de secretos; solo pueden contener configuración pública como la URL base de la API.

### 2.10. Configuración de PostgreSQL, transacciones y migraciones

Se identificaron configuraciones como `spring.jpa.hibernate.ddl-auto=update`, `spring.jpa.show-sql=true` y credenciales directas en configuración. Claude debe comprobar su estado actual.

- `ddl-auto=update` puede servir durante prototipado, pero no debe ser la estrategia permanente para cambios de esquema controlados.
- Introducir **Flyway o Liquibase** como herramienta de migraciones versionadas; elegir una, no combinar varias tecnologías de inicialización sin una razón documentada.
- Definir la transición con cuidado: revisar el esquema y los datos actuales antes de crear la primera migración. No generar una migración inicial destructiva ni borrar datos existentes.
- En entornos controlados, usar `ddl-auto=validate` o una política explícita compatible con las migraciones elegidas; definirlo por perfil.
- Desactivar `show-sql` en entornos no destinados a diagnóstico y configurar logging SQL con cuidado.
- Añadir claves primarias, foráneas, `UNIQUE`, `CHECK` e índices basados en reglas y consultas reales.
- Diseñar transacciones para operaciones atómicas, por ejemplo el cambio de acudiente principal o la matrícula.
- Definir backups y comprobar periódicamente la restauración; un backup no verificado no es una estrategia suficiente.
- Separar entornos de desarrollo, pruebas y producción.

### 2.11. Manejo uniforme de errores HTTP

El manejador global existente es una base, pero se observó que ciertos errores de negocio se representaban con `IllegalArgumentException` y podían terminar como `400` aunque el recurso no existiera o la operación estuviera en conflicto.

Utilizar excepciones de dominio específicas, por ejemplo `RecursoNoEncontradoException`, `ConflictoNegocioException` y excepciones de acceso cuando corresponda, sin capturar toda excepción bajo una respuesta ambigua.

| Caso | Código esperado |
|---|---:|
| DTO mal formado o validación de entrada fallida | `400 Bad Request` |
| Falta de autenticación | `401 Unauthorized` |
| Usuario autenticado sin permiso | `403 Forbidden` |
| Estudiante, acudiente o recurso no encontrado | `404 Not Found` |
| Documento duplicado, vínculo duplicado o conflicto de estado | `409 Conflict` |
| Error inesperado del servidor | `500 Internal Server Error` |

La API debe devolver un esquema de error consistente, con código/mensaje seguro y opcionalmente un identificador de trazabilidad. Los detalles técnicos se deben registrar en logs controlados. Evitar enviar stack traces o información sensible al usuario.

Añadir paginación, orden y filtros a los listados que puedan crecer. No devolver todos los estudiantes o acudientes como una única respuesta sin límite.

### 2.12. Auditoría

Auditar al menos las operaciones sensibles: cambio de datos personales relevantes, cambio de acudiente principal, autorización de retiro, creación/modificación/cierre de notas, cambio de estado de matrícula, registros de asistencia corregidos, cambios en permisos y generación/corrección de documentos académicos.

Cada evento debe capturar, cuando sea pertinente:

- usuario que realizó la operación;
- tipo de acción;
- fecha y hora del servidor;
- entidad/tipo e identificador afectado;
- valores anteriores y nuevos de los campos modificados;
- motivo, si la operación requiere explicación;
- identificador de petición o correlación, cuando esté disponible.

Diseñar la auditoría desde temprano, de manera consistente y protegida contra modificaciones no autorizadas. No guardar secretos ni duplicar todos los datos personales indiscriminadamente.

### 2.13. Pruebas, calidad y observabilidad

Mantener la pirámide de pruebas:

- Unitarias de servicios y reglas de negocio con JUnit/Mockito.
- De DTO/validación y controladores donde corresponda.
- De integración para repositorios, restricciones, transacciones y comportamiento HTTP.
- De seguridad: falta de autenticación, roles incorrectos, acceso a recursos de otros estudiantes y acudientes.
- De casos límite: registros inactivos, duplicados, operaciones simultáneas, matrículas de estado incompatible, periodos cerrados, modificación retroactiva de notas.
- Pruebas de componentes/servicios Angular y, cuando aporte valor, pruebas end-to-end de los flujos críticos al integrar la SPA con la API.

Testcontainers puede utilizarse para pruebas que requieran PostgreSQL real. Evitar afirmar que una funcionalidad está completa si no se ejecutaron sus pruebas relevantes. Informar claramente qué comandos se ejecutaron, cuáles fallaron y por qué.

Añadir logs útiles para diagnóstico sin incluir contraseñas, tokens o datos personales innecesarios. Actuator, métricas, tracing y monitoreo deben incorporarse cuando se defina la fase de infraestructura y exista necesidad.

### 2.14. Reglas académicas colombianas que deben orientar el diseño

Este proyecto toma como marco inicial la educación escolar colombiana de preescolar, básica y media. Las referencias de partida son el Decreto 1075 de 2015 (incluida la sección compilada sobre evaluación y promoción), el Decreto 1290 de 2009 y las modificaciones aplicables, entre ellas el Decreto 1421 de 2017 para disposiciones relacionadas con educación inclusiva y promoción de estudiantes con discapacidad. La Ley 1581 de 2012 es referencia relevante para protección de datos personales y derechos prevalentes de niños, niñas y adolescentes.

**Principio no negociable:** distinguir normativa nacional, decisión institucional documentada y regla técnica. No presentar una decisión propia del código como obligación legal universal.

El SIEE institucional debe conducir, como mínimo, el diseño de:

- criterios de evaluación y promoción;
- escala institucional y su equivalencia nacional;
- estrategias de valoración integral;
- acciones de seguimiento y mejoramiento;
- procesos de autoevaluación;
- estrategias de apoyo para situaciones pedagógicas pendientes;
- periodicidad y estructura de informes;
- procedimientos de reclamación de estudiantes/familias sobre evaluación y promoción;
- procedimientos institucionales de divulgación, aprobación y actualización del SIEE.

Cada institución determina sus criterios de promoción conforme a su sistema y define el porcentaje de asistencia que incida en la promoción. También debe considerarse la normativa aplicable sobre flexibilización curricular y evaluación de estudiantes con discapacidad. Antes de convertir estos principios en reglas ejecutables, obtener el SIEE real o marcar los parámetros como pendientes de configuración.

El sistema debe poder conservar y comunicar resultados, historiales y reclamaciones de forma autorizada y trazable. No prometer cumplimiento regulatorio completo solo porque se hayan implementado notas, boletines o permisos.

### 2.15. Errores de diseño que se deben evitar

- Convertir controladores en servicios de negocio.
- Exponer entidades JPA directamente como respuesta de API.
- Depender solo de validaciones Java para unicidad e integridad.
- Usar borrado físico de forma habitual para datos relacionados con historia académica.
- Guardar datos académicos actuales directamente en la entidad `Estudiante` en lugar de registrar periodos y matrículas.
- Hardcodear nota mínima, escala, cantidad de periodos, ponderaciones o porcentaje de asistencia sin parámetro institucional.
- Permitir que un profesor califique estudiantes sin comprobar su asignación académica y matrícula.
- Permitir cambios ilimitados después del cierre de un periodo.
- Suponer que el acudiente principal siempre es representante legal o está autorizado para retirar al estudiante.
- Creer que ocultar funciones o usar guards en Angular protege la API.
- Exponer PostgreSQL directamente al cliente.
- Agregar Redis, microservicios, Nginx, Docker, CI/CD o herramientas avanzadas antes de definir un caso de uso concreto.
- Hacer refactors masivos que mezclen la consolidación de reglas con cambios de formato o arquitectura no necesarios.

---

## 3. Plan de Acción y Desglose de Tareas (Roadmap para Claude Code)

### 3.1. Reglas de planificación del roadmap

- Ordenar tareas por riesgo, dependencia y valor funcional.
- No crear código para un módulo hasta definir las entidades, reglas, actores, permisos, casos de uso y criterios de aceptación mínimos.
- Completar primero una funcionalidad de extremo a extremo y después repetir el patrón para el siguiente módulo.
- Dividir las tareas de Gemini en cambios pequeños y revisables.
- Antes de cambiar el esquema, generar un plan de migración y evaluar datos existentes.
- No afirmar que el proyecto está “completo” a partir de una compilación correcta: validar reglas, autorización, persistencia y respuestas HTTP.

### Fase 0 — Inventario y línea base del repositorio

**Objetivo:** conocer el estado real del código, preservar lo existente y distinguir hechos de suposiciones.

- [ ] Revisar `pom.xml`, Maven Wrapper, versión de Java y Spring Boot, dependencias y perfiles.
- [ ] Buscar el frontend Angular existente o confirmar que aún no existe; revisar `package.json`, `package-lock.json`, `angular.json`, `tsconfig*.json`, configuración de entornos, rutas, componentes, servicios, guards, interceptores y pruebas.
- [ ] Verificar versiones compatibles de Node.js/npm/Angular mediante fuentes oficiales sin actualizar automáticamente el stack.
- [ ] Ejecutar `git status`, comprobar rama actual y cambios sin confirmar; no sobrescribir el trabajo local.
- [ ] Inventariar controladores, endpoints, servicios, DTO, entidades, repositorios, mappers, excepciones y pruebas.
- [ ] Identificar todas las configuraciones de seguridad y comprobar qué rutas están públicas.
- [ ] Revisar `application.properties`/YAML y otros archivos de configuración sin imprimir secretos.
- [ ] Buscar credenciales en configuración y revisar `.gitignore`; no copiar el valor encontrado en reportes.
- [ ] Confirmar qué clases de estudiantes, acudientes y relación están implementadas y las reglas concretas del servicio.
- [ ] Ejecutar la compilación y pruebas disponibles; registrar comandos, resultados y bloqueos por separado.
- [ ] Crear o actualizar una matriz de inventario: `Implementado`, `Parcial`, `Pendiente`, `No verificado`.
- [ ] Crear una lista priorizada de riesgos, sin reescribir el proyecto durante la inspección.

**Criterio de aceptación:** inventario reproducible del repositorio, línea base de compilación/pruebas y lista de riesgos basada en archivos reales.

### Fase 1 — Mitigar riesgos críticos de seguridad

**Prioridad inmediata antes de datos reales.**

- [ ] Rotar la credencial de PostgreSQL que apareció en la configuración del archivo entregado; no incluirla en ningún commit ni respuesta.
- [ ] Mover credenciales a variables de entorno/configuración local fuera del control de versiones.
- [ ] Revisar la historia Git para determinar exposición histórica; coordinar cualquier reescritura de historial con el propietario.
- [ ] Eliminar el permiso global equivalente a `permitAll()` para `/api/**`.
- [ ] Definir explícitamente las rutas realmente públicas; las restantes deben requerir autenticación.
- [ ] Diseñar autenticación y modelo de usuario/rol/permisos compatible con clientes separados y tokens.
- [ ] Implementar autorización por ruta/operación y comprobar acceso por propietario/relación.
- [ ] Aplicar hashing adaptativo de contraseñas (Argon2id o BCrypt correctamente configurado), nunca texto plano o SHA-256 simple.
- [ ] Definir y documentar estrategia de autenticación JWT/cookie y amenazas XSS/CSRF antes de implementar el flujo Angular; no guardar tokens en `localStorage` ni `sessionStorage`.
- [ ] Configurar CORS explícito en Spring Security para el origen de desarrollo `http://localhost:4200`, con métodos/headers concretos y preflight `OPTIONS`; añadir orígenes de despliegue solo por configuración.
- [ ] Si se usan cookies/`withCredentials`, configurar credenciales CORS con origen exacto (nunca `*`), atributos de cookie seguros y protección CSRF compatible con Angular.
- [ ] Probar el ciclo completo de CORS, autenticación, renovación/logout y CSRF de acuerdo con el esquema elegido.
- [ ] Añadir pruebas para acceso anónimo, rol insuficiente y acceso a datos ajenos.
- [ ] Verificar que logs y respuestas no expongan secretos, stack traces o información personal innecesaria.

**Criterio de aceptación:** las operaciones sensibles no pueden ejecutarse sin autenticación/permiso; los casos de acceso no autorizado tienen pruebas; los secretos ya no están en configuración versionada y la credencial expuesta se ha rotado por el responsable.

### Fase 2 — Consolidar estudiantes, acudientes y vínculos

- [ ] Documentar casos de uso y reglas para crear, consultar, actualizar, desactivar, reactivar, vincular y desvincular.
- [ ] Revisar los DTO `EstudianteRequest`, `EstudianteResponse` y equivalentes de acudiente/vínculo.
- [ ] Definir políticas de normalización de documentos, correos y espacios.
- [ ] Mantener restricciones únicas respaldadas por PostgreSQL para identificadores y vínculo estudiante–acudiente.
- [ ] Definir la política de acudiente principal: máximo uno; establecer si se exige mínimo uno y las excepciones autorizadas.
- [ ] Hacer transaccional el cambio de principal y diseñar una restricción de unicidad que proteja frente a concurrencia.
- [ ] Revisar la desactivación de acudientes que mantienen relaciones o son principales.
- [ ] Revisar borrado físico frente a relaciones e historial; preferir estados inactivos donde corresponda.
- [ ] Diferenciar contacto principal, representante/responsable legal y autorización de retiro.
- [ ] Definir las categorías de parentesco que necesita la institución.
- [ ] Añadir auditoría a cambios sensibles del vínculo.
- [ ] Corregir la clasificación de excepciones HTTP para `404` y `409`.
- [ ] Añadir paginación a listados que puedan crecer.
- [ ] Completar pruebas unitarias, integración y concurrencia de los casos críticos.

**Criterio de aceptación:** el módulo conserva sus contratos existentes o documenta formalmente cualquier cambio, mantiene relaciones consistentes y pasa pruebas de casos normales, errores, estados y conflictos de persistencia.

### Fase 3 — Estandarizar persistencia, errores y contratos API

**Prioridad alta:** OpenAPI no es documentación decorativa; es el contrato central entre Spring Boot y Angular y debe quedar establecido antes de ampliar mucho el frontend.

- [ ] Seleccionar Flyway o Liquibase y justificar la selección.
- [ ] Inspeccionar el esquema actual antes de elaborar migraciones; respaldar datos de desarrollo que deban conservarse.
- [ ] Crear una estrategia de migración segura desde el esquema existente sin borrar información accidentalmente.
- [ ] Configurar perfiles para desarrollo/pruebas/producción.
- [ ] Usar una política explícita para `ddl-auto`; no depender de `update` en despliegues controlados.
- [ ] Desactivar `show-sql` donde no sea necesario.
- [ ] Estandarizar el formato de errores y las excepciones de dominio.
- [ ] Asegurar que las restricciones únicas de base de datos se traduzcan en conflictos controlados.
- [ ] Definir paginación, orden y filtros para endpoints de colección.
- [ ] Revisar contratos DTO para no exponer campos internos ni entidades JPA.
- [ ] Incorporar OpenAPI en el backend con una biblioteca compatible con la versión real de Spring Boot; confirmar compatibilidad antes de añadir dependencia.
- [ ] Documentar operaciones, parámetros, DTO de request/response, validaciones, códigos HTTP, respuestas de error, paginación y esquema de autenticación.
- [ ] Publicar/exportar el documento OpenAPI en JSON/YAML (por ejemplo, `/v3/api-docs` si la configuración elegida lo permite) y validar que represente el contrato real.
- [ ] Configurar Swagger UI para desarrollo; decidir y documentar si se habilita, restringe o deshabilita en producción.
- [ ] Evaluar OpenAPI Generator con `typescript-angular` estable y compatible para generar modelos/interfaces y un cliente TypeScript consumible desde Angular.
- [ ] Definir un comando reproducible de generación (por ejemplo, script npm o script del repositorio), fijar versión/configuración de la herramienta y separar archivos generados del código manual.
- [ ] No editar manualmente código generado ni duplicar en Angular los DTO que puede derivar del contrato OpenAPI.
- [ ] Incorporar validación/diff del contrato para detectar cambios incompatibles antes de integrar cambios de backend.

**Criterio de aceptación:** migraciones seguras; errores conocidos con códigos coherentes; contrato OpenAPI exportable y revisado; cliente Angular tipado generable de manera repetible; cambios del backend reflejados en el cliente sin mantener manualmente dos fuentes de verdad.

### Fase 4 — Auditoría y marco de pruebas

- [ ] Definir esquema/evento de auditoría y entidades/acciones que deben registrarse.
- [ ] Añadir identidad del actor, fecha/hora, acción, entidad afectada y valores anteriores/nuevos cuando corresponda.
- [ ] Proteger los registros y excluir secretos.
- [ ] Añadir pruebas de auditoría para cambios de acudiente principal y operaciones administrativas iniciales.
- [ ] Incorporar pruebas de controladores y seguridad.
- [ ] Evaluar Testcontainers para las pruebas que dependan de PostgreSQL real.
- [ ] Documentar comandos y requisitos para ejecutar las pruebas localmente.
- [ ] Confirmar resultados reales tras cada merge de Gemini; no reutilizar resultados antiguos como prueba del estado actual.

**Criterio de aceptación:** las acciones sensibles priorizadas dejan evidencia verificable y las pruebas se ejecutan de forma repetible.

### Fase 5 — Especificación funcional de V1

- [ ] Definir con el propietario el alcance exacto de la primera versión utilizable.
- [ ] Crear una matriz de actores, casos de uso y permisos.
- [ ] Crear catálogo de reglas con identificadores, condición, resultado, excepciones, fuente y pruebas esperadas.
- [ ] Obtener o documentar como pendiente el SIEE/PEI institucional aplicable.
- [ ] Separar reglas legales/nacionales, decisiones institucionales y decisiones técnicas.
- [ ] Especificar estados y transiciones de Estudiante, Acudiente, AñoLectivo, Grupo, Matrícula, Periodo, Calificación y Asistencia.
- [ ] Diseñar modelo entidad-relación inicial y revisar claves, relaciones, restricciones y operaciones transaccionales.
- [ ] Establecer qué módulos se incluyen en V1 y qué módulos quedan pospuestos. Propuesta: seguridad, estudiantes/acudientes, estructura académica, docentes/asignaciones, matrícula, calificaciones, asistencia y reportes básicos; finanzas, comunicación avanzada y cliente móvil pueden quedar para evolución, salvo decisión del propietario.
- [ ] No implementar los módulos académicos hasta que sus reglas y criterios mínimos estén aprobados/documentados.

**Criterio de aceptación:** especificación funcional con actores, reglas, límites de alcance, modelo de datos y criterios de aceptación; todos los parámetros institucionales desconocidos se registran como pendientes, no como constantes inventadas.

### Fase 6 — Estructura académica y año lectivo

- [ ] Diseñar entidades para año lectivo, grado, curso/grupo, asignatura, periodo académico y configuración relevante.
- [ ] Definir claves únicas y estados para cada entidad.
- [ ] Definir qué entidades son globales y cuáles dependen de un año lectivo.
- [ ] Implementar DTO, validaciones, mappers, servicios, repositorios y controladores siguiendo los patrones aprobados.
- [ ] Restringir modificaciones de estructura que afecten a matrículas o resultados existentes.
- [ ] Añadir pruebas unitarias y de integración para integridad, duplicados, estados y autorizaciones.

**Criterio de aceptación:** estructura académica consistente, sin duplicados prohibidos, con trazabilidad/versionado suficiente para que las matrículas históricas no cambien silenciosamente cuando se modifica la estructura actual.

### Fase 7 — Matrículas

- [ ] Diseñar entidad `Matricula` vinculada a estudiante, año lectivo y curso/grupo, con estado y fechas pertinentes.
- [ ] Definir reglas de solicitud/admisión y formalización si ambas fases existen.
- [ ] Definir duplicidad permitida/prohibida según año lectivo y estado.
- [ ] Implementar flujo de alta, formalización, traslado/cambio de grupo, retiro y consulta de historial según alcance.
- [ ] Definir quién puede realizar cada transición; usar `SECRETARIA` como actor administrativo de referencia.
- [ ] Aplicar transacciones e integridad de persistencia.
- [ ] Comprobar cómo afectan cambios de estado a notas, asistencia e informes.
- [ ] Añadir autorización, auditoría y pruebas de todos los cambios de estado.

**Criterio de aceptación:** el historial académico no se pierde al cambiar el año/grupo o retirar al estudiante; se impiden transiciones inválidas y duplicados conforme a las reglas documentadas.

### Fase 8 — Profesores y asignaciones académicas

- [ ] Definir entidad/perfil de profesor y su relación con cuentas de usuario.
- [ ] Diseñar asignación docente en función de grupo/curso, asignatura, año lectivo y parámetros necesarios.
- [ ] Implementar consulta de asignaciones y permisos del profesor.
- [ ] Rechazar duplicados y conflictos según reglas aprobadas.
- [ ] Hacer que las operaciones futuras de notas/asistencia validen matrícula activa pertinente y asignación autorizada.
- [ ] Añadir pruebas de profesor no asignado, curso/asignatura inexistente, duplicados y conflictos.

**Criterio de aceptación:** solo un docente autorizado puede ejecutar operaciones académicas en el contexto asignado; los conflictos definidos se detectan en backend y se respaldan con integridad adecuada.

### Fase 9 — Calificaciones y evaluación institucional

- [ ] Configurar escala institucional, equivalencia nacional, periodos, reglas de evaluación, ponderaciones y redondeo según SIEE aprobado.
- [ ] Implementar registro y consulta de calificaciones vinculadas a matrícula/asignación/periodo.
- [ ] Validar rango de nota y existencia/estado de todos los elementos relacionados.
- [ ] Bloquear cambios cuando un periodo esté cerrado, salvo procedimiento autorizado de reapertura/corrección.
- [ ] Implementar historial de cambios y motivo cuando corresponda.
- [ ] Implementar promedios, definitivas y recuperación únicamente según reglas aprobadas.
- [ ] Diseñar seguimiento, autoevaluación y planes de apoyo si son parte del sistema institucional.
- [ ] Evitar constantes universales como nota mínima fija o número fijo de periodos.
- [ ] Añadir pruebas para límites, redondeo, ponderaciones, estados, periodos cerrados, usuarios sin permiso y correcciones.

**Criterio de aceptación:** todos los resultados son reproducibles desde configuración/versiones de reglas; solo usuarios autorizados pueden registrar/corregir; el cierre de periodo se respeta y queda auditado.

### Fase 10 — Asistencia, promoción, reclamaciones e informes

- [ ] Implementar registros de asistencia por el nivel temporal definido, justificaciones y correcciones.
- [ ] Configurar el porcentaje de asistencia que pueda incidir en promoción solo de acuerdo con la política institucional aplicable.
- [ ] Implementar criterios de promoción y promoción anticipada si están dentro del alcance y según las decisiones documentadas.
- [ ] Considerar la flexibilización curricular y el contexto de estudiantes con discapacidad conforme a la normativa aplicable y los procesos institucionales.
- [ ] Implementar boletines/reportes que reflejen los resultados, la escala/equivalencia institucional y la periodicidad configurada.
- [ ] Conservar evidencia/versiones de informes emitidos y mantener trazabilidad de correcciones posteriores.
- [ ] Definir flujo y permisos para reclamaciones sobre evaluación/promoción.
- [ ] Añadir certificados, constancias, actas y listados según alcance V1.
- [ ] Evaluar JasperReports cuando el módulo de documentos esté listo.
- [ ] Añadir pruebas de cálculos, autorizaciones, generación de informes, corrección de notas e historial.

**Criterio de aceptación:** asistencia y promoción respetan reglas configuradas; reportes son comprensibles, coherentes con el periodo y accesibles exclusivamente a usuarios autorizados; reclamaciones/correcciones son trazables.

### Fase 11 — Cliente web Angular SPA

**Objetivo:** construir el cliente principal de V1 como una SPA accesible, mantenible, tipada y segura, sin trasladar reglas críticas fuera del backend.

#### A. Base del proyecto y configuración

- [ ] Confirmar ubicación del proyecto frontend; preferir una carpeta independiente (por ejemplo, `frontend/`) salvo que el repositorio existente recomiende otra estructura.
- [ ] Confirmar versiones soportadas de Angular, Node.js y npm; crear el proyecto con Angular CLI y fijar versiones reproducibles.
- [ ] Configurar TypeScript estricto, scripts de desarrollo, pruebas, lint si existe y build de producción.
- [ ] Definir entornos/configuración pública para la URL base de la API; no guardar secretos en variables Angular.
- [ ] Definir rutas principales, layout, navegación, accesibilidad básica y carga diferida por funcionalidad cuando sea útil.
- [ ] Documentar los comandos reproducibles (`npm ci`, scripts de tests/lint/build, generación de API) en un README del frontend.

#### B. Estructura, componentes y módulos funcionales

- [ ] Crear una estructura clara con `core`, `shared`, `layout` y `features` organizados por dominio. Adaptar el árbol de la sección 2.2 a las convenciones de la versión Angular usada.
- [ ] Crear layout base, menú/navegación, login, pantalla de acceso denegado, no encontrado, error general y estados de carga.
- [ ] Crear componentes/páginas para los módulos que forman parte del alcance V1 aprobado, priorizando estudiantes y acudientes antes de matrícula y módulos académicos.
- [ ] Utilizar formularios reactivos, validación de entrada y mensajes accesibles. Las validaciones Angular son ayuda de experiencia de usuario, no reemplazo de las validaciones del backend.
- [ ] Mantener componentes enfocados en presentación y composición; trasladar llamadas HTTP y coordinación a servicios.
- [ ] No crear pantallas para módulos que aún no tengan un alcance/reglas aprobados; se pueden dejar rutas futuras explícitamente no implementadas.

#### C. Contrato OpenAPI y servicios HTTP

- [ ] Conectar la generación TypeScript con la salida OpenAPI del backend.
- [ ] Ubicar modelos/servicios generados en una carpeta dedicada, no editarlos manualmente y explicar cómo regenerarlos.
- [ ] Usar `HttpClient` y RxJS; implementar servicios de aplicación por dominio que encapsulen el cliente generado cuando se necesite lógica específica de UI.
- [ ] No crear interfaces TypeScript duplicadas de DTO ya descritos por OpenAPI, excepto tipos puramente locales de UI.
- [ ] Usar tipos explícitos; evitar `any` salvo excepción justificada y documentada.
- [ ] Configurar la URL base por entorno y evitar llamadas a hosts externos no autorizados.
- [ ] Asegurar consistencia entre el contrato generado y los códigos HTTP/errores reales del backend.

#### D. Autenticación, interceptores y seguridad web

- [ ] Implementar el flujo de login/logout y carga del usuario actual acorde con la estrategia JWT/cookie aprobada en la sección 2.8.
- [ ] Crear interceptores HTTP para autenticación, manejo común de errores y, si aplica, CSRF/correlación; preferir interceptores funcionales si están soportados por la versión elegida.
- [ ] Añadir encabezado Bearer solo si la arquitectura transmite access JWT en ese encabezado; no intentar leer cookies `HttpOnly` desde TypeScript.
- [ ] Si se usan cookies, configurar `withCredentials` donde proceda y probar CORS/CSRF de extremo a extremo. No usar `withCredentials` indiscriminadamente para dominios externos.
- [ ] No guardar JWT ni refresh tokens en `localStorage` ni `sessionStorage`.
- [ ] Implementar guards de autenticación/RBAC para mejorar la navegación y ocultar acciones irrelevantes al rol actual, recordando que la seguridad real está en Spring Security.
- [ ] Evitar `innerHTML`, funciones `bypassSecurityTrust*` y otras desactivaciones de seguridad Angular sin revisión explícita.
- [ ] Asegurar que no existan tokens, secretos de firma, credenciales ni datos reales dentro del bundle frontend.

#### E. RBAC y navegación

- [ ] Definir las rutas con datos de permisos/roles o políticas explícitas y aplicar `CanActivate`/`CanActivateChild` (o API equivalente vigente) según corresponda.
- [ ] Consultar identidad y permisos desde una fuente validada del backend; no confiar en un rol editable por el usuario ni en datos provenientes solo del navegador.
- [ ] Diferenciar ausencia de autenticación (`401`) de usuario autenticado sin permiso (`403`).
- [ ] Devolver a login en sesión expirada y preservar/recuperar la ruta de retorno de manera segura.
- [ ] Los permisos sobre un recurso concreto deben seguir siendo validados por Spring Security/servicios backend, incluso si la ruta está protegida por guard.

#### F. Estado de carga, errores HTTP y RxJS

- [ ] Crear un patrón reutilizable para estados de carga por pantalla/operación y evitar indicadores globales que se bloqueen por solicitudes en segundo plano.
- [ ] Centralizar la interpretación/normalización de errores HTTP en un servicio/interceptor sin ocultar errores inesperados.
- [ ] `400`: mostrar errores de validación vinculados al formulario cuando el contrato incluya detalles de campos.
- [ ] `401`: tratar sesión ausente/expirada, gestionar renovación solo si existe el flujo seguro aprobado y evitar bucles de reintento.
- [ ] `403`: mostrar acceso denegado sin filtrar datos.
- [ ] `404`: informar que el recurso no existe o ya no está disponible.
- [ ] `409`: comunicar conflicto de negocio (por ejemplo, documento duplicado, vínculo duplicado o conflicto de estado) y permitir corregir/recargar cuando proceda.
- [ ] `500`: mostrar mensaje genérico y un identificador de seguimiento si el backend lo proporciona; no mostrar stack trace, SQL ni detalles técnicos.
- [ ] Manejar fallos de red, timeout y respuesta no válida de manera diferenciada.
- [ ] Usar operadores RxJS y cancelación/limpieza de suscripciones conforme al patrón Angular vigente; evitar suscripciones anidadas y suscripciones que sobrevivan a la vista sin necesidad.

#### G. Pruebas y entrega

- [ ] Añadir pruebas de componentes, formularios, servicios HTTP, interceptores, guards y manejo de errores.
- [ ] Añadir pruebas de integración UI/API y E2E de flujos críticos cuando el coste esté justificado.
- [ ] Validar login/logout, permisos por rol, sesión expirada, solicitudes fallidas, paginación y formularios con datos inválidos.
- [ ] Ejecutar instalación reproducible con `npm ci` y los scripts declarados para pruebas, lint y build. No asumir nombres de scripts sin revisar `package.json`.
- [ ] Probar la comunicación con backend: CORS, preflight, autenticación y CSRF/cookies conforme a la estrategia elegida.
- [ ] Probar que no hay llamadas directas a PostgreSQL ni lógica crítica exclusiva del cliente.
- [ ] Documentar configuración local, comandos, variables públicas, estrategia de autenticación y procedimiento de regeneración OpenAPI.

**Criterio de aceptación:** la SPA Angular compila y pasa pruebas relevantes; consume exclusivamente la API tipada/documentada; los flujos de auth/CORS/CSRF funcionan según la estrategia aprobada; los guards ofrecen navegación contextual sin sustituir la seguridad backend; los errores `400/401/403/404/409/500` se manejan con mensajes apropiados; no hay JWT persistidos en almacenamiento web ni secretos en el bundle.

### Fase 12 — Documentos, comunicación, finanzas e infraestructura de evolución

- [ ] Añadir comunicación (avisos, notificaciones, correo o mensajería) solo cuando esté definida la necesidad y política de privacidad.
- [ ] Diseñar finanzas (pensiones, pagos, facturas, cartera, becas y descuentos) como módulo de evolución con sus propias reglas y permisos.
- [ ] Configurar Docker/Docker Compose cuando ayude a reproducir el entorno; contemplar PostgreSQL, backend y Angular según el modo de despliegue.
- [ ] Planificar backups y realizar pruebas de restauración.
- [ ] Incorporar Nginx/HTTPS para despliegues donde aplique; definir cómo servir la SPA y enrutar `/api` o el dominio del backend.
- [ ] Añadir CI/CD, monitoreo y logs estructurados cuando la aplicación esté preparada.
- [ ] Evaluar Redis solo para casos de caché/colas/rate limiting demostrables.
- [ ] Evaluar el cliente móvil Kotlin consumiendo la misma API OpenAPI/REST.
- [ ] No iniciar un cliente alternativo con otro framework frontend: Angular es el cliente principal V1 definido en este documento. Cambiar de framework requiere decisión explícita del propietario y análisis del coste.

**Criterio de aceptación:** cada tecnología añadida tiene una necesidad documentada, configuración segura, pruebas y una forma de operarla/diagnosticarla; Angular y cualquier futuro cliente móvil se mantienen desacoplados de la persistencia y dependen del mismo contrato de API.

### 3.2. Códigos de reglas de negocio iniciales

Crear y mantener un catálogo central. Como punto de partida:

| Código | Regla a definir/validar |
|---|---|
| `RB-EST-001` | Condiciones de alta, modificación, desactivación y reactivación de estudiante. |
| `RB-ACU-001` | Condiciones para asignar/cambiar/desvincular acudiente principal. |
| `RB-ACU-002` | Política de mínimo/máximo de responsables y excepciones. |
| `RB-ACU-003` | Diferenciar contacto principal, representante legal y autorizado de retiro. |
| `RB-MAT-001` | Condiciones de formalización de matrícula. |
| `RB-MAT-002` | Restricciones de duplicidad por año lectivo, estudiante, grupo y estado. |
| `RB-MAT-003` | Reglas de traslado, retiro y conservación histórica. |
| `RB-ESTR-001` | Integridad de año lectivo, grados, grupos, asignaturas y periodos. |
| `RB-ASIG-001` | Asignación válida de profesor, grupo y asignatura. |
| `RB-CAL-001` | Escala de calificación y límites permitidos. |
| `RB-CAL-002` | Cálculo de resultados, ponderaciones y redondeo. |
| `RB-CAL-003` | Operaciones autorizadas después del cierre de periodo. |
| `RB-CAL-004` | Recuperaciones, apoyos y definitivas según SIEE. |
| `RB-ASI-001` | Registro, justificación y corrección de asistencia. |
| `RB-PRO-001` | Criterios institucionales de promoción/reprobación. |
| `RB-SEG-001` | Permisos de cada endpoint y operación. |
| `RB-SEG-002` | Restricciones de acceso por pertenencia/relación con estudiante. |
| `RB-AUD-001` | Operaciones sensibles que deben auditarse. |
| `RB-DAT-001` | Finalidad, acceso, minimización y tratamiento de datos personales. |
| `RB-INF-001` | Periodicidad, estructura y acceso a boletines/informes. |
| `RB-REC-001` | Atención y resolución de reclamaciones de evaluación/promoción. |

Cada ficha de regla debe incluir: identificador, descripción, motivo/fuente, precondiciones, flujo exitoso, errores esperados, excepciones, rol autorizado, impacto sobre datos, transacción requerida y pruebas de aceptación.

---

## 4. Guía de Implementación para IAs (Claude Code + Gemini Flash)

### 4.1. Responsabilidades del agente

#### Claude Code — arquitecto, coordinador y revisor

Claude Code debe:

1. Inspeccionar el repositorio real antes de opinar sobre su estado.
2. Mantener la arquitectura, el modelo de dominio, las reglas de negocio, el contrato OpenAPI, la integración Angular/backend y el roadmap coherentes.
3. Detectar dependencias, riesgos de seguridad, inconsistencias y decisiones pendientes.
4. Dividir el trabajo en tareas pequeñas con límites precisos y criterios de aceptación.
5. Preparar una especificación escrita para Gemini antes de encargar cambios de código, incluyendo cuando corresponda la separación frontend/backend y si hay generación automática OpenAPI.
6. Revisar el resultado de Gemini línea por línea o por diff; no aceptar automáticamente su salida.
7. Ejecutar o coordinar compilación, pruebas, revisión de API, seguridad y persistencia.
8. Informar qué se modificó, qué se verificó, qué falló y qué queda pendiente.
9. Mantener documentos de arquitectura, decisiones (ADR), reglas y tareas actualizados.
10. Detener una implementación si una regla de negocio esencial es ambigua. En ese caso, formular una decisión/pregunta concreta al propietario o dejarla explícitamente como pendiente; no inventar políticas institucionales.

Claude debe actuar como líder técnico: su prioridad es la corrección global y la coordinación. No debe dejar que Gemini defina implícitamente la arquitectura a través de cambios ad hoc.

#### Gemini Flash 3.8 — implementador colaborativo de tareas acotadas

Gemini debe recibir tareas específicas, no instrucciones vagas como “termina el sistema académico”. Debe:

1. Leer los archivos y convenciones relevantes antes de editar.
2. Implementar únicamente el alcance asignado.
3. Respetar contratos, nombres, arquitectura, DTO y reglas aprobadas.
4. Explicar brevemente el plan de cambios antes de tocar varias capas.
5. Añadir o actualizar pruebas asociadas con cada regla modificada.
6. No introducir dependencias, patrones o refactors no solicitados sin justificar y pedir aprobación.
7. Reportar todos los archivos modificados, decisiones asumidas, comandos ejecutados y pruebas no ejecutadas.
8. No alterar credenciales ni datos sensibles ni volcar secretos en salida.

Claude es responsable de integrar la tarea, revisar la coherencia del sistema y rechazar cambios incompletos, inseguros o fuera de alcance.

### 4.2. Plantilla obligatoria para asignar tareas a Gemini

Cada encargo debe contener estas secciones:

```text
TAREA:
Identificador y nombre corto.

CONTEXTO:
Qué existe, qué problema se resuelve y por qué ahora.

RESULTADO ESPERADO:
Comportamiento observable al terminar.

ARCHIVOS DE REFERENCIA:
Lista de clases y archivos que debe leer primero.

ALCANCE PERMITIDO:
Archivos/capas que puede cambiar y operaciones que debe implementar.

FUERA DE ALCANCE:
Qué no debe cambiar; dependencias, endpoints o módulos que no debe tocar.

REGLAS DE NEGOCIO:
Identificadores RB-*, precondiciones, restricciones y excepciones.

CONTRATOS:
DTO, métodos, endpoints, códigos HTTP y transacciones esperadas.

PRUEBAS:
Pruebas que debe crear/actualizar y comandos que debe ejecutar.

CRITERIOS DE ACEPTACIÓN:
Checklist verificable, no frases subjetivas.

ENTREGA:
Resumen, archivos tocados, decisiones, riesgos, pruebas ejecutadas y pendientes.
```

### 4.3. Flujo de colaboración por tarea

1. **Claude inspecciona.** Confirmar el estado de la rama y los archivos. No asumir que el repositorio sigue igual que en la última conversación.
2. **Claude diseña.** Definir regla de negocio, dependencias, API, cambios de esquema y pruebas antes de implementar.
3. **Claude divide.** Separar tareas por responsabilidad, evitando que varias IAs editen al mismo tiempo los mismos archivos críticos.
4. **Gemini propone.** Para cambios que involucren múltiples clases o el esquema, solicitar plan breve antes del diff.
5. **Gemini implementa.** Trabajar en el ámbito asignado, sin incorporar cambios laterales.
6. **Claude revisa el diff.** Buscar errores de lógica, autorización, transacciones, integridad, compatibilidad, convenciones y cobertura de pruebas.
7. **Ejecutar validaciones.** Ejecutar validaciones relevantes de backend (`mvnw`/Maven) y frontend (`npm ci`, pruebas, lint y build según scripts reales). Si algo no puede ejecutarse, marcarlo como no verificado y explicar la causa.
8. **Corregir.** Devolver a Gemini una lista concreta de defectos detectados; evitar peticiones genéricas de “mejorar todo”.
9. **Integrar.** Aceptar solo al cumplir los criterios de aceptación. Realizar commits pequeños y descriptivos según el flujo Git acordado.
10. **Documentar.** Actualizar reglas, arquitectura, endpoints y roadmap si la tarea cambió el comportamiento.

No ejecutar dos agentes sobre el mismo archivo en paralelo. Evitar que un agente cambie entidades mientras otro cambia DTO/repositorios relacionados sin un contrato compartido.

### 4.4. Estilo y convenciones estrictas de código

#### Java

- Clases e interfaces: `UpperCamelCase`, nombres descriptivos en singular para entidades y conceptos.
- Métodos y variables: `lowerCamelCase`.
- Constantes: `UPPER_SNAKE_CASE`; solo extraer constantes cuando representen una política estable, no esconder reglas institucionales pendientes dentro de constantes.
- Paquetes: minúsculas y coherentes con la estructura actual; no cambiar el paquete raíz sin razón justificada.
- Un concepto principal por clase. Evitar clases “Dios” que acumulen responsabilidades.
- Preferir inyección por constructor.
- Usar tipos concretos adecuados, `Optional` cuando la semántica de ausencia lo justifique y Collections estándar de Java.
- No utilizar excepciones genéricas como mecanismo normal de flujo.
- No capturar `Exception` indiscriminadamente ni ocultar fallos con respuestas exitosas.
- No dejar `TODO`, `FIXME`, `System.out.println` o datos de prueba en código de producción sin justificar/documentar.
- No introducir lógica académica crítica en controladores, mappers, repositorios ni cliente Angular. El cliente valida para UX; el backend aplica la regla de autoridad.
- Comentarios para explicar decisiones o invariantes no obvios; no comentar cada línea de forma redundante.
- Código simple y explícito; aplicar SOLID/DRY/KISS con criterio. No crear capas, interfaces o fábricas sin una necesidad verificable.

#### Angular + TypeScript

- Usar TypeScript en modo estricto y tipos explícitos. Evitar `any`; justificar cada excepción.
- Convención habitual Angular: clases/componentes/servicios en `UpperCamelCase`; métodos, variables, funciones y propiedades en `lowerCamelCase`; constantes compartidas en `UPPER_SNAKE_CASE` únicamente si el patrón del repositorio lo utiliza de forma consistente.
- Respetar las convenciones oficiales de nombres de archivos Angular (por ejemplo, `estudiante-list.component.ts`, `estudiantes-api.service.ts`, `auth.interceptor.ts`, `auth.guard.ts`) y la convención real del CLI/version.
- Organizar por feature/dominio y mantener `core` para servicios singleton y preocupaciones globales; `shared` debe contener elementos reutilizables sin reglas institucionales concretas.
- Utilizar componentes presentacionales y servicios de aplicación con responsabilidades claras. Evitar componentes enormes que mezclen formularios, autenticación, llamadas HTTP y reglas académicas.
- Preferir formularios reactivos para formularios de negocio; reutilizar validadores sin duplicar reglas complejas que pertenecen al backend.
- Utilizar RxJS para componer flujos asíncronos; evitar suscripciones anidadas. Elegir señales/estado local/RxJS según la versión de Angular y la complejidad real. No introducir NgRx u otro gestor global sin justificar necesidad.
- Mantener interceptores, guards y servicios HTTP pequeños y probables. No duplicar en cada componente el manejo de `401`, `403`, `404`, `409` o `500`.
- El código generado por OpenAPI vive en una carpeta claramente separada. Nunca editarlo a mano; ajustar la especificación, la configuración del generador o escribir una capa adaptadora propia.
- Mantener URL base y configuración no secreta en configuración pública de entorno. Nunca incluir contraseñas, claves JWT, refresh tokens ni secretos en el frontend.
- No guardar credenciales/tokens en `localStorage`/`sessionStorage`; no usar `innerHTML` o `bypassSecurityTrust*` sin caso aprobado y revisión de seguridad.
- Los guards no otorgan permisos reales: toda operación y acceso a datos debe verificarse en Spring Security/backend.
- No usar estilos visuales, bibliotecas de UI, formularios o un sistema global de estado por preferencia personal sin decidirlo según las necesidades de V1. Revisar accesibilidad y mensajes de error en formularios.
- No modificar manualmente modelos/clientes generados; generar de nuevo y revisar el diff con la versión/configuración fijadas.

#### DTO, entidades y mappers

- Solicitudes: sufijo `Request` (por ejemplo, `EstudianteRequest`).
- Respuestas: sufijo `Response` (por ejemplo, `EstudianteResponse`).
- Entidades JPA: representan persistencia y relaciones; no serializarlas directamente como respuesta pública.
- Mappers: conversión explícita entre DTO y entidades. Evitar copiar campos sensibles o controlados por servidor desde el cliente.
- Los campos como `id`, `activo`, marcas de auditoría o identificadores de relaciones solo se deben aceptar desde la entrada si el caso de uso lo requiere expresamente.
- Usar `@Valid` en entrada HTTP y restricciones de Bean Validation apropiadas; complementar con reglas de servicio.
- No asignar valores predeterminados de negocio ocultos en DTO o mapper si deben ser decisiones del servicio o configuración institucional.

#### Servicios y repositorios

- Los servicios deben expresar casos de uso y reglas de negocio.
- Mantener operaciones críticas transaccionales y con manejo de conflictos.
- Los repositorios se enfocan en persistencia/consultas; no deciden roles, promoción ni reglas de SIEE.
- Evitar consultas repetidas que produzcan problemas N+1; usar consultas específicas cuando el caso lo justifique.
- No devolver una entidad directamente desde el servicio al controlador si la frontera API requiere DTO.
- No confiar únicamente en “consultar si existe” antes de insertar: la base de datos debe proteger las restricciones contra concurrencia.

#### API HTTP

- Mantener convenciones consistentes para nombres de rutas, métodos, respuestas y errores.
- Respetar los endpoints existentes, salvo que se apruebe una migración de contrato.
- No imponer `/api/v1` de forma retroactiva sin evaluar clientes existentes; definir una política de versionado explícita.
- `GET` no debe realizar cambios de negocio.
- Responder con códigos apropiados: `200`, `201`, `204`, `400`, `401`, `403`, `404`, `409` y `500` según el caso.
- Listados extensos deben soportar paginación/orden/filtros con límites razonables.
- No devolver internals, stack traces, SQL, secretos o información de otros usuarios.
- Mantener OpenAPI como contrato fuente revisado; incluir schemas, ejemplos no sensibles, códigos de error y esquema de seguridad.
- Al cambiar endpoints o DTO, actualizar la especificación y regenerar el cliente TypeScript Angular; revisar breaking changes antes de integrar.
- Los artefactos generados no deben editarse manualmente ni ser una segunda fuente de verdad.

#### Base de datos

- Restricciones e índices deben tener nombres descriptivos cuando la herramienta lo permita.
- Evitar cascadas de borrado sin analizar el historial y las relaciones.
- Versionar los cambios mediante migraciones.
- No editar manualmente una migración que ya se haya aplicado en entornos compartidos; crear una migración nueva.
- Probar migraciones sobre base vacía y sobre una copia representativa del esquema/datos anterior cuando aplique.
- No ejecutar comandos destructivos sobre datos del usuario sin autorización explícita.

#### Pruebas

- Nombre de prueba que exprese condición y resultado esperado.
- Probar éxito, validación, recursos inexistentes, conflictos, permisos, estados y límites relevantes.
- No usar pruebas que solo comprueben que el método no lanza excepción cuando la regla real exige verificar estado, repositorios o respuesta.
- Limpiar y aislar datos de prueba; nunca utilizar datos personales reales.
- Cada bug corregido debe tener una prueba de regresión si es viable.

### 4.5. Gestión de decisiones pendientes

Cuando una regla dependa de la institución, Claude debe crear un apartado de “Decisiones pendientes” con:

- pregunta precisa;
- alternativas razonables;
- consecuencias técnicas de cada alternativa;
- opción recomendada y por qué, si puede justificarse;
- responsable de aprobación;
- módulos afectados;
- efecto sobre datos y migraciones.

Ejemplos de decisiones que no se pueden inventar:

- escala y nota mínima de aprobación;
- número y duración de los periodos;
- ponderaciones de notas y redondeo;
- criterios de recuperación y promoción;
- porcentaje de asistencia que incide en promoción;
- requisitos de edad/admisión;
- obligación de mantener un acudiente mínimo y tratamiento de excepciones;
- estados válidos de matrícula y transiciones permitidas;
- categorías de parentesco y autorizaciones;
- plazos de conservación de datos y documentos;
- reglas específicas de facturación, pensiones, becas o cartera.

Claude puede proponer valores de ejemplo para pruebas locales, pero debe marcarlos inequívocamente como **datos ficticios de desarrollo**, no como política institucional.

### 4.6. Criterios de aceptación generales para cerrar cualquier tarea

Una tarea no está finalizada solo porque Gemini diga que terminó o el código compile. Claude debe comprobar:

- [ ] La implementación responde al requisito descrito.
- [ ] La regla de negocio está documentada y se aplica en backend.
- [ ] Las verificaciones de autenticación/autorización están presentes cuando correspondan.
- [ ] DTO, entidad, mapper, servicio, repositorio y controlador mantienen responsabilidades claras.
- [ ] La integridad se preserva frente a datos inválidos, relaciones inexistentes y duplicidad concurrente cuando sea relevante.
- [ ] Las transacciones cubren todas las operaciones que deben ser atómicas.
- [ ] Los códigos HTTP y mensajes de error son coherentes y no filtran datos sensibles.
- [ ] Hay pruebas de éxito, errores y bordes relevantes.
- [ ] La compilación y las pruebas relevantes fueron ejecutadas o el bloqueo quedó documentado expresamente.
- [ ] No se agregaron dependencias ni refactors fuera de alcance sin aprobación.
- [ ] No se incorporaron secretos ni datos personales reales.
- [ ] La documentación/API/reglas y el roadmap se actualizaron si hubo cambios contractuales.
- [ ] Si afecta la API, OpenAPI está actualizado y el cliente Angular generado se ha regenerado/verificado.
- [ ] Si afecta frontend, los tests/build Angular se ejecutaron y se verificaron guards/interceptores/CORS/CSRF según el caso.
- [ ] El diff fue revisado y se puede explicar por qué cada archivo modificado es necesario.

### 4.7. Git y control de cambios

- `main`: versión estable.
- `develop`: rama de integración si el flujo del proyecto la necesita.
- `feature/*`: funcionalidades o cambios acotados.
- Usar commits pequeños y descriptivos; no acumular una funcionalidad completa y varios refactors inconexos en un único commit.
- No trabajar todo directamente en `main`.
- Antes de editar, comprobar cambios no guardados. No eliminar, restaurar ni sobrescribir cambios del propietario.
- No hacer `push --force`, borrar ramas o reescribir historia sin aprobación explícita.
- No incluir `.env`, claves, credenciales, archivos temporales, `target/` ni archivos locales que no pertenezcan al proyecto.

### 4.8. Orden de aprendizaje y crecimiento técnico

El plan debe mantener una progresión que favorezca comprender lo que se implementa:

1. Java sólido: POO, interfaces, excepciones, genéricos, Collections, Streams, lambdas, `Optional`, fechas y Maven.
2. SQL/PostgreSQL: CRUD, `JOIN`, agrupaciones, constraints, índices, transacciones y normalización.
3. Spring Boot: controllers, services, repositories, DTO, JPA/Hibernate, validación y configuración.
4. Seguridad: Spring Security, hashing, autenticación, roles, permisos y tokens.
5. Angular + TypeScript: componentes, routing, formularios reactivos, RxJS, servicios HTTP, interceptores, guards y consumo del contrato OpenAPI.
6. Ingeniería de software: SOLID, DRY, KISS, Clean Code, patrones, UML y arquitectura.
7. Pruebas: JUnit, Mockito e integración.
8. Docker y despliegue.
9. Tecnologías avanzadas solo cuando el proyecto las necesite.

La explicación de cambios debe ser suficientemente clara para que el propietario pueda aprender y revisar el diseño, pero sin sacrificar precisión técnica. Al solicitar implementación a Gemini, Claude debe preferir tareas comprensibles y verificables en lugar de grandes bloques de código sin explicación.

### 4.9. Referencias normativas y técnicas de consulta

Claude debe revisar la versión vigente de la documentación oficial antes de tomar decisiones dependientes de versiones o de regulación. Las fuentes iniciales son:

- **Decreto 1075 de 2015 — Gestor Normativo de Función Pública:** https://www.funcionpublica.gov.co/eva/gestornormativo/norma.php?i=77913
- **Decreto 1290 de 2009 — Ministerio de Educación Nacional:** https://www.mineducacion.gov.co/portal/ejes-tematicos/Normas-sobre-Educacion-Preescolar-Basica-y-Media/187765:Decreto-1290-de-Abril-16-de-2009
- **Decreto 1421 de 2017 — Gestor Normativo de Función Pública:** https://www.funcionpublica.gov.co/eva/gestornormativo/norma.php?i=87040
- **Ley 1581 de 2012 — Gestor Normativo de Función Pública:** https://www.funcionpublica.gov.co/eva/gestornormativo/norma.php?i=49981
- **Spring Security — autorización de solicitudes HTTP:** https://docs.spring.io/spring-security/reference/servlet/authorization/
- **Spring Boot — inicialización y migraciones de base de datos:** https://docs.spring.io/spring-boot/how-to/data-initialization.html
- **OWASP — Password Storage Cheat Sheet:** https://cheatsheetseries.owasp.org/cheatsheets/Password_Storage_Cheat_Sheet.html

- **Angular — HTTP interceptors:** https://angular.dev/guide/http/interceptors
- **Angular — Route guards:** https://angular.dev/guide/routing/route-guards
- **Angular — Security best practices (XSS y XSRF):** https://angular.dev/best-practices/security
- **Spring Security — CORS:** https://docs.spring.io/spring-security/reference/servlet/integrations/cors.html
- **Spring Security — OAuth 2.0 Resource Server JWT:** https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/jwt.html
- **OWASP — Session Management Cheat Sheet (tokens y almacenamiento web):** https://cheatsheetseries.owasp.org/cheatsheets/Session_Management_Cheat_Sheet.html
- **OWASP — HTML5 Security Cheat Sheet:** https://cheatsheetseries.owasp.org/cheatsheets/HTML5_Security_Cheat_Sheet.html
- **OpenAPI Specification:** https://spec.openapis.org/oas/latest.html
- **OpenAPI Generator — TypeScript Angular:** https://openapi-generator.tech/docs/generators/typescript-angular/

Las referencias son un punto de partida; no reemplazan la verificación de cambios normativos ni la revisión del SIEE, el PEI y las políticas de protección de datos de la institución.

---

## Instrucción inicial para Claude Code

Comienza por la **Fase 0 — Inventario y línea base del repositorio**. No implementes inmediatamente todos los módulos ni hagas un refactor masivo. Devuelve primero un diagnóstico breve con:

1. estructura real del proyecto y versiones confirmadas de Java, Spring Boot, Angular, Node.js y npm;
2. controladores/endpoints, DTO y módulos realmente implementados;
3. resultados reales de compilación, pruebas backend y, si existe frontend, pruebas/build Angular;
4. riesgos de seguridad e integridad encontrados, en especial secretos expuestos, acceso anónimo, CORS y almacenamiento/autorización de tokens;
5. estado del contrato OpenAPI y viabilidad de generar modelos/cliente Angular sin incompatibilidades;
6. discrepancias entre este documento y el código actual;
7. plan de tareas ordenadas, dependencias y criterios de aceptación;
8. decisiones que requieren aprobación del propietario, especialmente autenticación JWT/cookie, CSRF, orígenes CORS y parámetros del SIEE.

Después, prioriza la exposición de credenciales y la configuración que permite acceso anónimo a `/api/**`. A continuación, define y prueba el diseño de autenticación web, CORS y CSRF; establece OpenAPI como contrato antes de ampliar la SPA. No uses datos reales ni expongas secretos al inspeccionar el repositorio. Mantén la lógica de negocio, el esquema PostgreSQL, las migraciones y los flujos transaccionales del backend bajo control; delega a Gemini Flash 3.8 mediante encargos acotados y revisa todos los cambios antes de integrarlos.
