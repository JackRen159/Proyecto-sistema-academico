# Frente 5: Matrículas y Vinculación Estudiantil (Backend)
Plan y seguimiento de implementación del módulo de matrículas e historial académico en Spring Boot y PostgreSQL.
Dominio: Inscripción formal de estudiantes a cursos/grupos por año lectivo, control de cupos, estados (INSCRITA, MATRICULADA, RETIRADA, FINALIZADA) e historial de traslados de grupo.

## Specs
- S1: "continuemos con Backend — Módulo de Matrículas: Diseñar la inscripción formal de estudiantes a grupos en el año lectivo activo (control de cupos, historial y estados)."
- S2: "Un estudiante debe tener una matrícula válida para recibir determinadas calificaciones y registros de asistencia. La matrícula debe conservar su historial y diferenciar estados del proceso según los casos de uso que se definan."
- S3: "Las operaciones sobre varias entidades relacionadas deben ser atómicas cuando corresponda. Deben controlarse conflictos y duplicados según reglas aprobadas."

## Tasks
- [x] T1: Diseñar y crear migración Flyway V7 con tablas matriculas e historial_traslados_matricula, constraints e índices (S1, S3) | route: inline | commit: b2c7b50
- [x] T2: Implementar entidades JPA (Matricula, HistorialTrasladoMatricula, enums) y repositorios Spring Data con consultas de cupo y verificación contextual (S1, S2) | route: inline | commit: b2c7b50
- [x] T3: Implementar DTOs con validaciones Jakarta y MatriculaMapper desacoplado (S1, S2) | route: inline | commit: b2c7b50
- [x] T4: Implementar servicio de dominio MatriculaService con reglas de negocio (cupo máximo, año lectivo activo, unicidad por vigencia, traslados) (S1, S2, S3) | route: inline | commit: b2c7b50
- [x] T5: Exponer controlador REST MatriculaController con seguridad RBAC y actualizar contrato openapi/openapi.yaml (S1, S2) | route: inline | commit: b2c7b50
- [x] T6: Regenerar cliente Angular tipado, verificar compilación ng build y suite completa de pruebas unitarias/integración (S1, S2, S3) | route: inline | commit: b2c7b50

## Log
- L1: 2026-11-11: "continuemos con Backend — Módulo de Matrículas: Diseñar la inscripción formal de estudiantes a grupos en el año lectivo activo (control de cupos, historial y estados). pero dame un analisis mas detallado de lo que se debe de programar". Análisis presentado y trazabilidad ODD inicializada.
- L2: 2026-11-11: Finalizada T1. Creada migración Flyway `V7__modulo_matriculas.sql` con tabla `matriculas` (índice único condicional para evitar doble matrícula activa en el mismo año, tipos de matrícula, estados) y tabla `historial_traslados_matricula` (auditoría de origen, destino, motivo y usuario autorizador). Compilación en verde.
- L3: 2026-11-11: Finalizada T2. Implementadas entidades `Matricula`, `HistorialTrasladoMatricula`, enums `EstadoMatricula` y `TipoMatricula`, y repositorios `MatriculaRepository` (métodos `countByGrupoIdAndEstado`, comprobación contextual `existsByEstudianteIdAndGrupoIdAndEstado`) e `HistorialTrasladoMatriculaRepository`. Pruebas unitarias de entidad en verde.
- L4: 2026-11-11: Finalizada T3. Implementados DTOs (`MatriculaRequest`, `MatriculaResponse`, `TrasladoGrupoRequest`, `CambioEstadoMatriculaRequest`, `HistorialTrasladoResponse`) con validaciones Jakarta y `MatriculaMapper` desacoplado. Compilación limpia.
- L5: 2026-11-11: Finalizada T4. Implementado servicio `MatriculaService` con todas las invariantes de negocio (cupo máximo, año lectivo no cerrado, unicidad por vigencia, traslados con auditoría, método contextual `tieneMatriculaActivaEnGrupo` para SIEE y asistencia). Suite unitaria `MatriculaServiceTest` (7 tests) en verde. Pendientes T5 y T6 para la siguiente sesión.
- L6: 2026-11-15: Finalizada T5. Creado controlador REST `MatriculaController` (`/api/matriculas`) con endpoints para formalización, búsqueda por ID/código, consulta filtrada, paginación por año lectivo, traslados con auditoría, cambio de estado y verificación contextual activa. Contrato `openapi/openapi.yaml` actualizado con tag `Matrículas`, 8 endpoints y 9 esquemas DTO. Suite MockMvc `MatriculaSecurityTest` (8 tests) y suite conjunta de seguridad de controladores (23 tests) en verde.
- L7: 2026-11-15: Finalizada T6. Cliente Angular tipado generado exitosamente con modelos y MatrículasService. Compilación de Angular `ng build` completada en verde (0 errores de compilación, 306.96 kB). Suite completa de pruebas de backend ejecutada exitosamente: 113 pruebas pasando, 0 fallos, 0 errores. Ausencia de regresiones verificada.
- L8: 2026-11-15: Commit atómico generado en rama main: `b2c7b50` feat(matriculas): implementar modulo de matriculas y vinculacion estudiantil (backend, openapi y cliente angular). 33 archivos empaquetados.
