---
name: sistema-escolar-maestro
description: Contexto global del Sistema de Gestión Escolar. Usar al analizar, diseñar, implementar, revisar o documentar tareas del proyecto y para seleccionar las skills específicas.
---

# Skill maestra — Sistema de Gestión Escolar

## 1. Propósito y autoridad

Esta skill establece las reglas globales del proyecto **Sistema de Gestión Escolar**. Se basa en el **Documento Maestro V2.0**, cuya copia íntegra está en `references/Documento_Maestro_Sistema_Escolar_V2.md`.

- Usa el Documento Maestro V2.0 como línea base de alcance, arquitectura, tecnologías, principios y roadmap.
- Una decisión explícita posterior del usuario puede actualizar esa línea base. Señala la decisión modificada y su impacto; no mantengas instrucciones contradictorias.
- El repositorio actual es la fuente de verdad sobre el estado real del código. No presentes como vigente un diagnóstico histórico sin comprobarlo.
- Distingue siempre: **definido por el documento**, **confirmado en el repositorio**, **recomendación técnica** y **decisión pendiente**.
- No inventes reglas institucionales, legales, académicas, campos obligatorios, permisos ni flujos que no estén documentados.

## 2. Identidad y objetivo

Construir progresivamente un sistema escolar colombiano modular, seguro y mantenible para procesos académicos, administrativos y, posteriormente, de comunicación y finanzas.

Arquitectura base:

- Cliente inicial V1: SPA Angular + TypeScript.
- Backend: Java + Spring Boot, API REST/JSON.
- Persistencia: PostgreSQL mediante Spring Data JPA/Hibernate.
- Contrato HTTP: OpenAPI 3.x versionado; cliente TypeScript generado con OpenAPI Generator (`typescript-angular`).
- Arquitectura backend: **monolito modular**, organizado por dominios; no iniciar con microservicios.
- Cliente futuro posible: Kotlin consumiendo la misma API.

Node.js/npm/Angular CLI son herramientas del entorno frontend; Angular se ejecuta en el navegador. Angular y backend son aplicaciones separadas y se integran mediante un contrato HTTP explícito.

## 3. Invariantes globales

1. Flujo de desarrollo: **analizar → diseñar → implementar → probar → revisar → documentar**.
2. Antes de modificar código, inspecciona el repositorio y confirma versiones, estructura, archivos, dependencias, pruebas, migraciones y cambios pendientes.
3. Mantén la lógica crítica y las reglas de negocio en Spring Boot. Angular puede validar para mejorar UX, pero no es una barrera de seguridad ni la autoridad de negocio.
4. No conectes Angular directamente a PostgreSQL.
5. No expongas entidades JPA directamente como contratos HTTP; utiliza DTO y mapeo explícito.
6. Aplica POO, SOLID, DRY, KISS y Clean Code de forma práctica. Evita abstracciones innecesarias y sobreingeniería.
7. Usa Java Collections en el sistema real; las estructuras manuales corresponden a ejercicios académicos, no a este producto.
8. Protege integridad con constraints, transacciones e índices justificados, además de validaciones en servidor.
9. No almacenes tokens ni credenciales en `localStorage` o `sessionStorage`.
10. La autorización efectiva ocurre en backend. Guards y elementos de interfaz Angular solo mejoran la navegación/UX.
11. OpenAPI es la fuente de verdad del contrato HTTP. No edites manualmente el código generado.
12. Configura CORS con orígenes explícitos; no uses `*` junto con credenciales.
13. No guardes secretos en Git, configuración versionada, frontend ni logs.
14. No codifiques como universales notas mínimas, ponderaciones, redondeos, número de periodos, umbrales de asistencia o promoción: deben provenir del SIEE/PEI y de decisiones institucionales aprobadas.
15. No afirmes que una prueba se ejecutó si no se ejecutó. No elimines pruebas para forzar una compilación exitosa.
16. No hagas reestructuraciones masivas ni elimines funcionalidad sin explicar beneficios, riesgos y migración incremental.

## 4. Enrutamiento: qué skills consultar

Carga solo las skills pertinentes a la tarea; combina varias cuando el cambio atraviese capas.

| Necesidad | Skill |
|---|---|
| Diseño del backend, límites de dominio, capas y responsabilidades | `sistema-escolar-arquitectura-backend` |
| Endpoints, DTO HTTP, versionado, OpenAPI y cliente generado | `sistema-escolar-api-openapi` |
| Modelo relacional, JPA, constraints, transacciones y migraciones | `sistema-escolar-base-datos` |
| Usuarios, autenticación, roles, permisos, CSRF, CORS y XSS | `sistema-escolar-seguridad` |
| SPA Angular, formularios, navegación, interceptores y cliente API | `sistema-escolar-frontend-angular` |
| Pruebas, calidad y criterios de aceptación | `sistema-escolar-calidad-testing` |
| Docker, configuración de entornos, secretos, backups y despliegue | `sistema-escolar-infraestructura` |
| Normativa escolar colombiana y decisiones PEI/SIEE | `sistema-escolar-contexto-colombiano` |
| Estudiantes | `sistema-escolar-estudiantes` |
| Acudientes y vínculos estudiante–acudiente | `sistema-escolar-acudientes` |
| Profesores y asignaciones docentes | `sistema-escolar-profesores` |
| Año lectivo, grados, grupos, asignaturas y periodos | `sistema-escolar-estructura-academica` |
| Matrícula e historial | `sistema-escolar-matriculas` |
| Notas, resultados y promoción | `sistema-escolar-calificaciones` |
| Registro y consulta de asistencia | `sistema-escolar-asistencia` |
| Horarios y detección de cruces | `sistema-escolar-horarios` |
| Boletines, certificados, constancias y reportes | `sistema-escolar-documentos-reportes` |
| Trazabilidad de operaciones sensibles | `sistema-escolar-auditoria` |
| Parámetros institucionales y reglas configurables | `sistema-escolar-configuracion` |
| Avisos, notificaciones y mensajería | `sistema-escolar-comunicacion` |
| Pensiones, pagos, cartera, becas y descuentos | `sistema-escolar-finanzas` |

## 5. Dependencias entre skills

- Una funcionalidad de extremo a extremo normalmente requiere: skill del módulo funcional + arquitectura backend + API/OpenAPI + base de datos + seguridad + pruebas.
- Si incluye interfaz, añade frontend Angular.
- Si registra o consulta información sensible, añade auditoría y seguridad.
- Si afecta evaluación, periodos o promoción, añade contexto colombiano y configuración institucional.
- Si emite documentos, añade documentos-reportes, seguridad y auditoría.
- No cargues todas las skills por defecto; consulta las necesarias para el alcance real.

## 6. Método obligatorio para trabajar en el repositorio

### Antes de cambiar

1. Identifica la petición, módulo, flujo de usuario y resultado esperado.
2. Inspecciona el repositorio real: rama, `git status`, estructura, código implicado, configuración, contratos, migraciones y pruebas.
3. Compara lo encontrado con el Documento Maestro. Separa divergencias confirmadas de advertencias históricas.
4. Revisa reglas, actores, permisos, estados, precondiciones, errores y dependencias entre módulos.
5. Expón incertidumbres que afecten el diseño. Si una regla depende del colegio, déjala como decisión pendiente y no la inventes.
6. Propón un cambio incremental y criterios de aceptación proporcionales al riesgo.

### Durante el cambio

- Mantén el límite del módulo y evita dependencias circulares.
- Actualiza en conjunto código, DTO, OpenAPI, migraciones, pruebas y documentación cuando corresponda.
- Conserva compatibilidad del contrato o identifica explícitamente el cambio incompatible.
- Aplica transacciones a operaciones que deban ser atómicas.
- Verifica autorización por rol y por contexto/recurso cuando aplique.
- Protege datos personales, especialmente información de menores.

### Antes de cerrar

- Ejecuta las pruebas relevantes y reporta exactamente cuáles se ejecutaron y sus resultados.
- Verifica compilación, migraciones, contrato OpenAPI y build Angular si el cambio los afecta.
- Revisa `git diff`, secretos, archivos generados y cambios no relacionados.
- Resume archivos/capas modificadas, reglas implementadas, pruebas, riesgos y pendientes.

## 7. Git y control de cambios

- `main` representa la versión estable. `develop` puede utilizarse como rama de integración si el flujo del proyecto lo requiere.
- Usa ramas `feature/*` para funcionalidades y `fix/*` para correcciones cuando encaje con el flujo acordado. No trabajes todo directamente sobre `main`.
- Mantén commits pequeños y descriptivos. Incluye código, migraciones, pruebas, contrato OpenAPI y documentación relacionados dentro del mismo conjunto lógico de cambios.
- Revisa `git diff` antes de cada commit. No incorpores credenciales, logs, temporales, artefactos de build ni dependencias instaladas que deban excluirse.
- Mantén `.gitignore` para Java/Maven, Angular/Node, archivos locales y secretos.
- No reescribas historial para borrar credenciales expuestas sin un plan explícito: rota primero la credencial y luego trata el historial con el procedimiento adecuado.
- Documenta en README cómo compilar y probar backend/frontend, ejecutar migraciones y generar el cliente API.

## 7. Arquitectura y tecnologías: restricciones

- Backend: Java + Spring Boot + Maven, manteniendo las versiones existentes si son compatibles y salvo decisión justificada.
- Persistencia: Spring Data JPA/Hibernate + PostgreSQL.
- Migraciones: elegir **Flyway o Liquibase**, nunca ambos en paralelo.
- API: REST/JSON y OpenAPI 3.x versionado. Si `springdoc-openapi` genera documentación de ejecución, comprobarla contra el contrato versionado.
- Frontend: Angular + TypeScript, Node.js/npm compatibles, Router, HttpClient, RxJS y formularios reactivos.
- Seguridad: Spring Security, RBAC y comprobación contextual. No confiar en Angular para proteger endpoints.
- Pruebas: JUnit/Mockito, pruebas de integración y pruebas Angular; Testcontainers cuando aporte valor.
- Docker, Nginx, Redis, CI/CD y observabilidad avanzada solo cuando exista necesidad concreta.

## 8. Contexto histórico del código

El Documento Maestro incluye observaciones de una auditoría anterior en su sección 21.2. Son **pistas que deben verificarse**, no hechos actuales garantizados. En particular, comprobar antes de actuar:

- existencia y estado de los módulos Estudiante, Acudiente y EstudianteAcudiente;
- configuración de seguridad y posible acceso anónimo amplio;
- credenciales de base de datos expuestas en configuración o historial;
- uso de `ddl-auto=update` frente a migraciones;
- pruebas existentes y su ejecución real;
- concurrencia y políticas de acudiente principal;
- estado del frontend Angular, OpenAPI y demás módulos.

Una auditoría histórica nunca autoriza una reescritura automática. Rotar cualquier secreto expuesto antes de tratar su historial.

## 9. Roadmap resumido

0. Análisis y alcance; actores, casos de uso, reglas, SIEE/PEI y modelo inicial.
1. Fundación backend; estructura, PostgreSQL, migraciones, validación, errores y perfiles.
2. OpenAPI y seguridad base; autenticación, RBAC, CORS, CSRF, auditoría inicial y pruebas.
3. Fundación Angular SPA; `core`, `shared`, `features`, Router, HttpClient, formularios y cliente generado.
4. Núcleo administrativo; estudiantes, acudientes, profesores, estructura académica y matrículas.
5. Académico; periodos, asignaciones, calificaciones, asistencia y horarios.
6. Documentos y reportes.
7. Comunicación.
8. Finanzas, si entra en el alcance priorizado.
9. Calidad e infraestructura, trabajadas transversalmente desde las fases anteriores.
10. Evolución, incluido cliente móvil Kotlin si existe necesidad.

No es obligatorio terminar toda la lógica académica antes de iniciar la base Angular; sí deben existir decisiones de arquitectura, contrato y seguridad suficientes para integrar correctamente.

## 10. Orden de aprendizaje cuando el usuario solicite explicación o tutoría

Acompasa el aprendizaje con la fase del proyecto. Orden recomendado por el Documento Maestro:

1. Java: POO, interfaces, excepciones, genéricos, Collections, Streams, lambdas, Optional, fechas y Maven.
2. SQL/PostgreSQL: CRUD, JOIN, agrupaciones, constraints, índices, transacciones, normalización y diseño relacional.
3. Spring Boot: controllers, services, repositories, DTO, JPA/Hibernate, validación y configuración.
4. Seguridad: Spring Security, hashing, autenticación, autorización, RBAC, cookies/tokens, CSRF y CORS.
5. OpenAPI y generación de cliente TypeScript.
6. Angular + TypeScript: componentes, templates, DI, Router, formularios reactivos y organización por funcionalidades.
7. Node.js/npm y herramientas frontend: compatibilidad, scripts, dependencias y builds reproducibles.
8. RxJS y HTTP: Observables, operadores, HttpClient, interceptores y estados asíncronos.
9. Seguridad SPA: guards, sesión, cookies, CSRF/XSS y errores HTTP.
10. Ingeniería de software: SOLID, DRY, KISS, Clean Code, patrones, UML y arquitectura modular.
11. Testing: JUnit, Mockito, integración, Testcontainers cuando aporte valor y pruebas Angular.
12. Git, colaboración, revisión y documentación.
13. Docker y despliegue: redes, variables de entorno, HTTPS, backups y prácticas seguras.
14. Tecnologías avanzadas (Redis, Nginx avanzado, CI/CD, Kotlin, mensajería y optimización) solo cuando el proyecto lo necesite.

No exijas dominar toda la pila antes de empezar: enseña lo necesario para implementar, probar y documentar cada fase.

## 11. Formato de respuesta esperado del agente

Para tareas de análisis o planificación, entrega: objetivo, estado comprobado, reglas documentadas, decisiones pendientes, dependencias, propuesta incremental, riesgos y criterios de aceptación.

Para tareas de implementación, entrega: resumen del cambio, archivos afectados, decisiones aplicadas, pruebas realmente ejecutadas, resultado y pendientes. No ocultes limitaciones ni supongas que el cambio está completo si faltan capas.
