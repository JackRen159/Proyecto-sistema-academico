# Frente 4: Profesores y Asignación Académica (Backend)
Plan y seguimiento de implementación del módulo de docentes y asignación curricular en Spring Boot y PostgreSQL.
Dominio: Profesores, Especialidad, Carga Horaria y Asignación Curricular (Docente - Grupo - Asignatura).

## Specs
- S1: "2. Backend: Profesores y Carga Academica - Gestion de profesores y asignacion curricular: docente - asignatura - grupo."
- S2: "Un profesor solo puede registrar o modificar calificaciones o asistencia cuando está asignado al curso/grupo y asignatura que corresponden a la operación. La autorización debe verificarse en backend y sobre el recurso concreto; no basta con que la cuenta tenga el rol PROFESOR."
- S3: "Mantén la asignación docente como relación verificable entre profesor, curso/grupo, asignatura y periodo/vigencia que se defina. Audita operaciones sensibles asociadas con asignaciones y cambios académicos."

## Tasks
- [x] T1: Diseñar y crear migración Flyway V6 con tablas profesores y asignaciones_academicas, constraints e índices (S1, S2, S3) | route: inline | commit: 45ab391
- [x] T2: Implementar entidades JPA (Profesor, AsignacionAcademica) y repositorios Spring Data JPA con consultas de asignación contextual (S1, S2) | route: inline | commit: 45ab391
- [x] T3: Implementar DTOs con validaciones Jakarta y Mappers desacoplados (S1, S3) | route: inline | commit: 45ab391
- [x] T4: Implementar servicios de dominio con reglas de negocio e invariantes (profesor activo, no asignación en año cerrado, unicidad de docente por grupo-asignatura, verificación contextual) (S1, S2, S3) | route: inline | commit: 45ab391
- [x] T5: Exponer controladores REST con seguridad RBAC @PreAuthorize y actualizar contrato openapi/openapi.yaml (S1, S2) | route: inline | commit: 45ab391
- [x] T6: Regenerar cliente Angular tipado, verificar compilación ng build y suite de pruebas unitarias/integración (S1, S2, S3) | route: inline | commit: 45ab391

## Log
- L1: 2026-11-11: Selección autorizada por el usuario: "2. Backend: Profesores y Carga Academica". Trazabilidad ODD inicializada con 6 tareas.
- L2: 2026-11-11: Finalizada T1. Creada migración Flyway `V6__profesores_asignacion_academica.sql` con tablas `profesores` (vínculo opcional 1-a-1 a `usuarios`, tipo de documento, datos profesionales) y `asignaciones_academicas` (relación docente-grupo-asignatura con restricción de unicidad, intensidad horaria e índices). Compilación en verde.
- L3: 2026-11-11: Finalizada T2. Creadas entidades JPA `Profesor`, `AsignacionAcademica`, enum `TipoDocumento` y repositorios Spring Data `ProfesorRepository` y `AsignacionAcademicaRepository` con métodos para validación de autorización contextual (`existsByProfesorUsuarioIdAndGrupoIdAndAsignaturaIdAndActivoTrue`). Pruebas unitarias de entidad en verde.
- L4: 2026-11-11: Finalizada T3. Implementados DTOs (`ProfesorRequest`, `ProfesorResponse`, `AsignacionAcademicaRequest`, `AsignacionAcademicaResponse`) con validaciones Jakarta (@NotBlank, @Email, @Min, @Size) y componentes mapper (`ProfesorMapper`, `AsignacionAcademicaMapper`). Compilación limpia.
- L5: 2026-11-11: Finalizada T4. Implementados servicios de dominio `ProfesorService` y `AsignacionAcademicaService` con validaciones de negocio: unicidad de documento/correo/usuario, rechazo de asignación en año cerrado, rechazo de profesor/asignatura/grupo inactivos, unicidad de asignación por grupo-asignatura, director de grupo único y comprobación contextual docente para SIEE/asistencia (`tieneAutorizacionDocente`). 9 pruebas unitarias pasando.
- L6: 2026-11-11: Finalizada T5. Creados controladores REST `ProfesorController` (`/api/profesores`) y `AsignacionAcademicaController` (`/api/asignaciones-academicas`) protegidos con RBAC (@PreAuthorize). Contrato `openapi/openapi.yaml` actualizado con 7 endpoints y 5 schemas. 5 pruebas MockMvc de seguridad RBAC pasando.
- L7: 2026-11-11: Finalizada T6. Regenerado cliente Angular TypeScript (`npm run generate:api`) con modelos y servicios para Profesores y Asignaciones Académicas. Compilación de Angular frontend en verde (`ng build` limpia en 9.6s). Suite completa de pruebas de backend ejecutada exitosamente: 96 pruebas pasando, 0 fallos, 0 errores. No regresión confirmada en módulos previos.
- L8: 2026-11-11: Commit atómico generado en rama main: `45ab391` feat(profesores): implementar modulo de profesores y asignacion academica (backend, openapi y cliente angular). 32 archivos empaquetados.
