---
name: sistema-escolar-base-datos
description: Diseña o revisa el modelo PostgreSQL, entidades JPA, relaciones, constraints, índices, transacciones y migraciones Flyway/Liquibase del Sistema de Gestión Escolar.
---

# Base de datos y persistencia

## Principios

- PostgreSQL es la persistencia central; Spring Data JPA/Hibernate es la capa de acceso.
- Diseña relaciones normalizadas y explícitas. Una relación muchos-a-muchos con atributos o reglas propias debe representarse mediante entidad intermedia.
- No dependas solo de validaciones Java: protege invariantes también con PK, FK, `UNIQUE`, `NOT NULL`, checks e índices cuando corresponda.
- Crea índices por necesidad de consulta/rendimiento, no por rutina.
- Las operaciones que actualicen varias entidades relacionadas y deban ser atómicas requieren transacción.
- No conectes Angular directamente a PostgreSQL.

## Migraciones

- Elige **Flyway o Liquibase** y utiliza una sola herramienta.
- Versiona cada cambio de esquema, revisa las migraciones y verifica su ejecución desde una base vacía y desde la versión previa cuando corresponda.
- No utilices `ddl-auto=update` como sustituto permanente de migraciones controladas.
- En despliegue, valida el esquema mediante la estrategia acordada (`validate` o equivalente), no lo cambies implícitamente.

## Relaciones conceptuales iniciales

- Estudiante ↔ Acudiente mediante vínculo explícito con atributos que puedan incluir parentesco, responsabilidad y autorización de retiro.
- Estudiante → Matrícula → Curso/Grupo/Año lectivo.
- Curso/Grupo → Asignaturas o plan de estudios.
- Profesor → Asignaciones académicas.
- Matrícula → Calificaciones y asistencia.
- Curso/Grupo + Profesor + Asignatura + espacio/periodo temporal → Horario.

Estas son relaciones conceptuales; confirma el modelo físico y sus cardinalidades en el diseño del módulo.

## Invariantes pendientes expresamente

No des por resueltas estas decisiones sin evidencia:

- Si todo estudiante necesita al menos un acudiente/responsable activo y qué excepciones aplican.
- Si existe un único acudiente principal y cómo proteger esa regla frente a concurrencia.
- Qué ocurre al desactivar o desvincular al acudiente principal.
- Cuándo se permite eliminación física frente a inactivación y conservación histórica.
- Qué documentos/correos son únicos, cuáles son opcionales y cómo se normalizan.

Si se acuerda un único acudiente principal, la protección debe contemplar servicio, transacción, restricción/índice apropiado y concurrencia, según la base elegida.

## Seguridad y configuración

- Separa perfiles de desarrollo, pruebas y despliegue.
- Mantén credenciales fuera del repositorio; usa variables de entorno o gestor de secretos.
- Define copias de seguridad, retención y pruebas de restauración antes de usar datos institucionales reales.
- No registres secretos ni datos personales innecesarios.

## Validación del cambio

Revisa DDL/migraciones, constraints, índices, cardinalidades, transacciones, compatibilidad con entidades/repositorios y pruebas de persistencia. No afirmes que una migración funciona sin ejecutarla.

## Referencia

Documento Maestro V2.0, secciones 7, 8, 16, 17, 18 y 21.2.
