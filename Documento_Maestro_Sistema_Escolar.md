**DOCUMENTO MAESTRO**

**Sistema de Gestión Escolar**

*Definición del proyecto, arquitectura, módulos, tecnologías y hoja de ruta*

Versión: 1.0 — Documento de referencia del proyecto

Propósito adicional: servir como contexto base para futuras conversaciones y nuevos chats.

# 1. Objetivo general del proyecto

Construir un sistema de gestión escolar completo, modular y escalable que permita digitalizar progresivamente los procesos académicos, administrativos, financieros y de comunicación de una institución educativa.

El proyecto no debe tratarse como un simple CRUD de estudiantes. La intención es construir un software real, con reglas de negocio, seguridad, permisos, auditoría, reportes, manejo de errores, pruebas, documentación y una arquitectura que pueda crecer.

La primera versión tendrá como objetivo principal una aplicación de escritorio. El backend será independiente para que, posteriormente, pueda ser consumido por una aplicación móvil desarrollada con Kotlin y por otros clientes si el proyecto evoluciona.

# 2. Visión actual del sistema

La solución se plantea inicialmente con esta arquitectura:

| Capa | Tecnología / componente | Responsabilidad |
| --- | --- | --- |
| Cliente de escritorio | JavaFX + FXML + Maven | Interfaz utilizada por los usuarios del sistema. |
| Backend | Java + Spring Boot | Lógica de negocio, API REST, seguridad y acceso a datos. |
| Base de datos | PostgreSQL | Persistencia central de la información. |
| Comunicación | HTTP/HTTPS + JSON | Intercambio entre cliente y backend. |
| Cache / servicios auxiliares | Redis (opcional) | Cache, colas o control de frecuencia cuando realmente sea necesario. |
| Infraestructura | Docker / Docker Compose | Ejecución reproducible del backend y servicios. |
| Exposición | Nginx + HTTPS | Reverse proxy y acceso seguro en despliegues donde aplique. |
| Futuro cliente móvil | Kotlin | Aplicación móvil consumiendo la misma API. |

Decisión importante: el sistema se desarrollará inicialmente como un monolito modular. No se comenzará con microservicios. La separación interna por módulos permitirá crecer sin introducir complejidad innecesaria.

# 3. Principios que guían el proyecto

- Construir por fases: aprender → aplicar → probar → mejorar.

- No intentar implementar todo el sistema escolar de una sola vez.

- Priorizar reglas de negocio y arquitectura antes que cantidad de pantallas.

- Mantener una separación clara entre interfaz, API, lógica de negocio y persistencia.

- Aplicar POO, SOLID, DRY, KISS y Clean Code de forma práctica.

- Usar las Collections de Java en el sistema real; las estructuras de datos implementadas manualmente quedan como aprendizaje académico.

- No conectar el cliente directamente con PostgreSQL: el acceso debe pasar por el backend.

- Registrar las operaciones importantes mediante auditoría.

- Los errores visibles para el usuario deben ser claros y comprensibles; los detalles técnicos deben quedar en logs.

- Evitar agregar tecnologías solo por agregarlas: cada componente debe resolver una necesidad real.

# 4. Módulos funcionales previstos

| Módulo | Alcance |
| --- | --- |
| Usuarios y seguridad | Usuarios, autenticación, roles, permisos, sesiones/tokens, recuperación de acceso y controles de seguridad. |
| Estudiantes | Datos personales, estado, documentos, historial académico y relación con acudientes. |
| Acudientes | Registro de padres/acudientes, estudiantes asociados y acceso a información autorizada. |
| Profesores | Datos del docente, asignaturas, cursos asignados, horarios, asistencia, calificaciones y observaciones. |
| Estructura académica | Año lectivo, grados, grupos, asignaturas, periodos académicos y planes de estudio. |
| Matrículas | Proceso de matrícula, estado, historial y relación estudiante–curso. |
| Calificaciones | Notas por asignatura y periodo, promedios, recuperaciones, definitivas, materias perdidas y promoción. |
| Asistencia | Asistencia diaria, faltas justificadas/injustificadas, retardos, porcentajes y alertas. |
| Horarios | Horarios de cursos y profesores, aulas y prevención de cruces. |
| Documentos y reportes | Boletines, certificados, constancias, actas, listados y reportes administrativos. |
| Finanzas | Pensiones, pagos, facturas, cartera, becas y descuentos como módulo de evolución. |
| Comunicación | Noticias, comunicados, notificaciones, avisos y mensajería. |
| Auditoría | Registro de acciones sensibles: usuario, acción, fecha, entidad afectada y valores relevantes. |
| Configuración | Parámetros institucionales, periodos, reglas y configuraciones generales. |

# 5. Roles previstos

| Rol | Funciones principales |
| --- | --- |
| ADMINISTRADOR | Administración general, usuarios, configuración y operaciones autorizadas. |
| RECTOR | Consulta y gestión institucional según permisos. |
| COORDINADOR | Gestión académica y seguimiento. |
| PROFESOR | Cursos asignados, asistencia, calificaciones y funciones docentes. |
| ESTUDIANTE | Consulta de notas, asistencia, horario y documentos autorizados. |
| ACUDIENTE | Consulta de estudiantes asociados y su información autorizada. |
| SECRETARIA | Procesos administrativos, estudiantes, matrículas y documentos según permisos. |

Los roles son una base inicial. Los permisos reales deberán definirse por caso de uso y no únicamente por nombre del rol. El modelo de autorización será RBAC (Role-Based Access Control).

# 6. Arquitectura del backend

Estructura conceptual recomendada:

**Cliente JavaFX** → HTTP/HTTPS → **Spring Boot REST API** → **Service** → **Repository** → **PostgreSQL**

Organización interna recomendada por módulo:

- controller — endpoints HTTP.

- service — reglas de negocio y casos de uso.

- repository — acceso a datos mediante JPA.

- entity — entidades persistentes.

- dto — objetos de entrada/salida de la API.

- mapper — conversión entre entidades y DTO.

- exception — excepciones y manejo global de errores.

- security — autenticación y autorización.

- config — configuración del sistema.

Ejemplo de organización modular:

**com.colegio**

- estudiante

- acudiente

- profesor

- curso

- asignatura

- matricula

- calificacion

- asistencia

- horario

- documento

- reporte

- seguridad

- auditoria

- configuracion

# 7. Base de datos

PostgreSQL será la base de datos principal. El modelo final se diseñará antes de implementar gran parte de la lógica, pero evolucionará por migraciones conforme avance el proyecto.

- Primary Keys y Foreign Keys.

- Constraints para proteger la integridad.

- Índices donde exista una necesidad real de rendimiento.

- Normalización y relaciones bien definidas.

- Transacciones para operaciones que deban ser atómicas.

- Migraciones versionadas de la base de datos.

- No exponer PostgreSQL directamente al cliente.

Relaciones conceptuales iniciales:

- Estudiante → Acudiente.

- Estudiante → Matrícula → Curso/Grupo.

- Curso/Grupo → Asignaturas.

- Profesor → Asignaciones académicas.

- Matrícula → Calificaciones.

- Matrícula → Asistencia.

- Curso + Profesor + Asignatura → Horario.

# 8. Reglas de negocio importantes

El sistema debe modelar reglas, no limitarse a guardar datos. Ejemplos:

- Una calificación debe encontrarse dentro del rango permitido por la institución.

- Un estudiante debe estar matriculado para recibir determinadas calificaciones.

- Un profesor debe estar asignado al curso/asignatura antes de registrar una calificación.

- Un periodo académico puede estar abierto o cerrado.

- No debe permitirse registrar información incompatible con un periodo cerrado.

- El sistema debe detectar conflictos de horario.

- Las modificaciones de información sensible deben quedar auditadas.

- Los permisos deben validarse en backend; ocultar un botón en el cliente no constituye seguridad.

- Los documentos y reportes solo deben ser accesibles a usuarios autorizados.

# 9. Calificaciones y proceso académico

Flujo conceptual:

**Estudiante → Matrícula → Curso → Asignatura → Periodo → Notas → Promedio → Definitiva → Resultado**

- Cálculo de promedios.

- Recuperaciones cuando corresponda.

- Nota definitiva.

- Materias perdidas.

- Resultado de promoción/reprobación según reglas institucionales.

- Generación de boletines.

# 10. Seguridad

- Spring Security.

- Autenticación mediante una estrategia basada en tokens para clientes separados.

- RBAC y permisos.

- Hashing de contraseñas con un algoritmo diseñado para contraseñas, como BCrypt o Argon2.

- No almacenar contraseñas en texto plano.

- No utilizar SHA-256 simple como almacenamiento de contraseñas.

- Validación de entrada.

- CORS cuando sea necesario para clientes web.

- HTTPS en despliegues donde la comunicación atraviese redes no confiables.

- Auditoría de acciones sensibles.

- No registrar contraseñas, tokens u otros secretos en logs.

# 11. Manejo de errores y experiencia del usuario

El usuario final no debería recibir errores técnicos incomprensibles. El backend debe clasificar los errores y devolver respuestas consistentes.

| HTTP | Situación | Mensaje orientado al usuario |
| --- | --- | --- |
| 400 | Datos inválidos | Revisa los datos ingresados. |
| 401 | No autenticado | Debes iniciar sesión. |
| 403 | Sin permisos | No tienes permiso para realizar esta acción. |
| 404 | Recurso inexistente | No se encontró el registro solicitado. |
| 409 | Conflicto | La operación entra en conflicto con información existente. |
| 500 | Error inesperado | Ocurrió un error inesperado. Intenta nuevamente. |

El sistema puede evolucionar hacia mensajes acompañados de ilustraciones en el cliente. Los detalles técnicos deben mantenerse en logs para diagnóstico y soporte.

# 12. Auditoría

Las operaciones sensibles deben dejar evidencia. Ejemplo conceptual:

- Usuario: admin01

- Acción: MODIFICAR_NOTA

- Entidad: calificación

- Estudiante afectado: identificador interno

- Valor anterior: 2.5

- Valor nuevo: 4.0

- Fecha y hora: registrada por el sistema

# 13. Reportes y documentos

- Boletín académico.

- Certificado de estudio.

- Constancia.

- Actas.

- Listados de estudiantes.

- Reporte de asistencia.

- Reporte de calificaciones.

- Reportes administrativos.

JasperReports u otra solución compatible con el ecosistema Java podrá evaluarse cuando el módulo de reportes esté listo.

# 14. Tecnologías y herramientas

| Área | Tecnología |
| --- | --- |
| Backend | Java + Spring Boot |
| IDE backend | Visual Studio Code |
| Build | Maven |
| API | REST + JSON |
| Persistencia | Spring Data JPA / Hibernate |
| Base de datos | PostgreSQL |
| Cliente inicial | JavaFX + FXML + Maven |
| Seguridad | Spring Security + tokens + RBAC |
| Testing | JUnit + Mockito |
| Integración | Testcontainers, cuando aporte valor |
| Documentación API | OpenAPI / Swagger |
| Versionamiento | Git + GitHub |
| Infraestructura | Docker + Docker Compose |
| Reverse proxy | Nginx, cuando sea necesario |
| Cache/colas | Redis, opcional |
| Reportes | JasperReports, cuando se implemente |
| Cliente futuro | Kotlin para móvil |

React + TypeScript puede ser una opción futura si se decide crear un cliente web. No forma parte del cliente inicial de este documento, cuyo objetivo actual es la aplicación de escritorio JavaFX.

# 15. Dependencias iniciales del backend

Para comenzar el backend Spring Boot, la base recomendada es:

- Spring Web.

- Spring Data JPA.

- PostgreSQL Driver.

- Validation.

- Spring Security.

- Lombok.

- Spring Boot DevTools (solo desarrollo).

No se deben incorporar todas las tecnologías avanzadas desde el primer commit. OpenAPI, Actuator, Testcontainers, Redis, correo, reportes y demás componentes se agregan cuando exista una necesidad concreta o llegue su fase correspondiente.

# 16. Testing y calidad

- Pruebas unitarias de servicios y reglas de negocio.

- Pruebas de controllers y validaciones cuando corresponda.

- Pruebas de integración.

- Testcontainers para escenarios que requieran una base PostgreSQL real.

- Pruebas de seguridad y autorización.

- Validación de casos límite y conflictos de negocio.

- Código mantenible antes que código simplemente funcional.

# 17. Git y control del proyecto

Git debe utilizarse desde el inicio.

- main — versión estable.

- develop — integración de cambios, si el flujo del proyecto lo requiere.

- feature/* — funcionalidades individuales.

- Commits pequeños y descriptivos.

- No trabajar todo directamente sobre main.

# 18. Fases de desarrollo

| Fase | Objetivo |
| --- | --- |
| Fase 0 — Análisis | Actores, casos de uso, requisitos, reglas de negocio, arquitectura inicial y modelo de datos. |
| Fase 1 — Fundación | Proyecto Spring Boot, configuración, PostgreSQL, JPA, estructura modular, manejo de errores y documentación base. |
| Fase 2 — Seguridad | Usuarios, autenticación, roles, permisos y auditoría inicial. |
| Fase 3 — Núcleo administrativo | Estudiantes, acudientes, profesores, cursos, asignaturas y matrículas. |
| Fase 4 — Académico | Periodos, calificaciones, asistencia y horarios. |
| Fase 5 — Documentos | Boletines, certificados, constancias y reportes. |
| Fase 6 — Comunicación | Avisos, notificaciones, correo y mensajería según necesidad. |
| Fase 7 — Finanzas | Pensiones, pagos, facturación, cartera, becas y descuentos. |
| Fase 8 — Calidad e infraestructura | Testing, Docker, backups, logs, monitoring y CI/CD. |
| Fase 9 — Evolución | Cliente móvil Kotlin y/o cliente web, sincronización y nuevas capacidades. |

# 19. Orden de aprendizaje recomendado

1. Java sólido: POO, interfaces, excepciones, generics, Collections, Streams, lambdas, Optional, fechas y Maven.

1. SQL y PostgreSQL: CRUD, JOIN, agrupaciones, constraints, índices, transacciones y normalización.

1. Spring Boot: controllers, services, repositories, DTO, JPA/Hibernate, validación y configuración.

1. Seguridad: Spring Security, hashing de contraseñas, autenticación, roles, permisos y tokens.

1. JavaFX: vistas, FXML, navegación, formularios y consumo de la API.

1. Ingeniería de software: SOLID, DRY, KISS, Clean Code, patrones, UML y arquitectura.

1. Testing: JUnit, Mockito e integración.

1. Docker y despliegue.

1. Tecnologías avanzadas únicamente cuando el proyecto las necesite.

# 20. Criterios de diseño y decisiones que NO deben olvidarse

- El frontend/cliente no contiene las reglas críticas de negocio como única barrera.

- La API es la frontera de seguridad y negocio.

- Los DTO se utilizan para controlar qué información entra y sale.

- Las entidades JPA no deben convertirse automáticamente en contratos públicos de la API.

- Las relaciones muchos-a-muchos se modelarán explícitamente cuando tengan datos o reglas propias.

- Las operaciones críticas deben ser transaccionales.

- La auditoría debe diseñarse desde temprano para operaciones sensibles.

- La base de datos debe proteger su integridad mediante constraints, no depender únicamente de Java.

- Redis, Nginx, Docker, CI/CD y otros componentes no se agregan por moda: se incorporan cuando el sistema los justifique.

- La arquitectura debe permitir añadir un cliente Kotlin sin reescribir la lógica de negocio del backend.

# 21. Contexto para futuros chats

Este apartado está escrito específicamente para que, al iniciar una nueva conversación, pueda utilizarse como contexto del proyecto. Si el proyecto continúa evolucionando, este documento debe actualizarse en lugar de crear descripciones contradictorias.

Resumen operativo:

- Proyecto: Sistema de Gestión Escolar completo.

- Objetivo: digitalizar procesos académicos, administrativos, comunicación y posteriormente finanzas/documentos.

- Cliente inicial: aplicación de escritorio JavaFX + FXML + Maven.

- Backend: Java + Spring Boot, desarrollado en Visual Studio Code.

- API: REST/JSON.

- Base de datos: PostgreSQL.

- Persistencia: Spring Data JPA + Hibernate.

- Seguridad: Spring Security, autenticación basada en tokens, RBAC y hashing seguro de contraseñas.

- Arquitectura: monolito modular.

- Cliente futuro: aplicación móvil Kotlin consumiendo la misma API.

- Infraestructura futura: Docker Compose, Nginx/HTTPS, CI/CD y backups.

- Calidad: JUnit, Mockito, pruebas de integración y principios SOLID/DRY/KISS.

- Metodología: desarrollar por fases, no intentar construir todo de una vez.

- Estado conceptual: antes de implementar módulos grandes, definir actores, casos de uso, requisitos, reglas de negocio y modelo de datos.

# 22. Punto de partida recomendado

El siguiente paso práctico no es crear cientos de clases. Es cerrar la especificación de la primera versión.

1. Definir alcance exacto de la V1.

1. Definir actores y permisos iniciales.

1. Crear casos de uso principales.

1. Diseñar el modelo entidad-relación inicial.

1. Crear el proyecto Spring Boot con las dependencias mínimas.

1. Configurar PostgreSQL y migraciones.

1. Crear estructura modular del backend.

1. Implementar seguridad base.

1. Implementar el primer módulo completo de extremo a extremo.

1. Conectar la aplicación JavaFX con la API y repetir el patrón para los siguientes módulos.

**Documento maestro — Sistema de Gestión Escolar**
