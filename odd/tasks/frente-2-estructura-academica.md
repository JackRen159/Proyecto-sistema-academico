# Frente 2: Estructura Académica (Backend)
Plan y seguimiento de implementación del núcleo escolar en Spring Boot y PostgreSQL.
Dominio: Años Lectivos, Periodos Académicos, Grados, Grupos, Áreas y Asignaturas flexibles.

## Specs
- S1: "con respecto a los catalogos maestros en la parte de los grados estoy de acuerdo"
- S2: "con las areas fundamentales especificamente la de humanidades en colombia no se maneja asi, se maneja generalmente como materias de etica y valores, religion, filosofia, etc en las asignaturas especificas vinculadas esos ejemplos si aplican, sin embargo los colegio tienen diferentes materias dependiendo que colegio sea, unos manejan contabilidad y otros no por ejemplo, entonces es importante dejar abierto a modificacion de eliminar o agregar una nueva asignatura para que sea adaptable a diferentes colegios"
- S3: "con todo lo demas que me has dicho estoy de acuerdo para comenzar con la fase 1"

## Tasks
- [x] T1: Diseñar y crear migración Flyway V5 con tablas de estructura académica, constraints, índices condicionales y grados colombianos estándar (S1, S2, S3) | route: inline | commit: 5b2d494
- [x] T2: Verificar sintaxis e integridad de la migración V5 y compatibilidad con suite de pruebas (S3) | route: inline | commit: 5b2d494
- [ ] T3: Implementar entidades JPA y repositorios Spring Data para estructura académica (S1, S2) | route: inline | commit: pending
- [ ] T4: Implementar servicios de dominio con reglas de negocio (invariante año activo, periodos, catálogo dinámico) (S2, S3) | route: inline | commit: pending
- [ ] T5: Exponer controladores REST con seguridad RBAC y actualizar openapi.yaml (S2, S3) | route: inline | commit: pending
- [ ] T6: Ejecutar suite de pruebas unitarias y de integración del backend y verificar no regresión (S3) | route: inline | commit: pending

## Log
- L1: 2026-11-11: "con respecto a los catalogos maestros en la parte de los grados estoy de acuerdo, con las areas fundamentales especificamente la de humanidades en colombia no se maneja asi, se maneja generalmente como materias de etica y valores, religion, filosofia, etc en las asignaturas especificas vinculadas esos ejemplos si aplican, sin embargo los colegio tienen diferentes materias dependiendo que colegio sea, unos manejan contabilidad y otros no por ejemplo, entonces es importante dejar abierto a modificacion de eliminar o agregar una nueva asignatura para que sea adaptable a diferentes colegios, con todo lo demas que me has dicho estoy de acuerdo para comenzar con la fase 1"
