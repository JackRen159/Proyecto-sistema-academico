# Frente 7: Control de Asistencia Escolar (Backend)
Plan y seguimiento de implementación del módulo de asistencia escolar (sesiones de clase, asistencia diaria, ausencias justificadas/injustificadas, retardos, justificaciones auditadas y alertas tempranas de ausentismo) en Spring Boot y PostgreSQL.
Dominio: Registro por bloque de asignatura o por jornada general, control de concurrencia y duplicados, doble verificación contextual (docente asignado + estudiante matriculado) y cálculo en tiempo real de porcentajes de inasistencia.

## Specs
- S1: "Backend — Módulo de Control de Asistencia Escolar: Registro diario y por bloques/sesiones de clase, ausencias justificadas/injustificadas, retardos y alertas tempranas por inasistencia."
- S2: "El estudiante debe tener matrícula válida para los registros de asistencia que correspondan. El profesor debe estar asignado al curso/grupo y asignatura antes de registrar o modificar asistencia cuando la operación dependa de esa asignación. La autorización se verifica en backend y sobre el recurso/contexto."
- S3: "No fijes umbrales de inasistencia, porcentajes, justificaciones aceptadas ni reglas de alertas sin una política institucional aprobada; hazlos configurables. Las modificaciones sensibles y justificaciones deben auditarse inmutablemente."

## Tasks
- [x] T1: Diseñar y crear migración Flyway V9 con tablas de sesiones, registros de asistencia, justificaciones y configuración de alertas (S1, S3) | route: inline | commit: d0c1068
- [x] T2: Implementar entidades JPA, enums de asistencia y repositorios Spring Data con consultas de agregación (S1, S2, S3) | route: inline | commit: d0c1068
- [x] T3: Implementar DTOs con validaciones Jakarta y AsistenciaMapper desacoplado (S1, S2) | route: inline | commit: d0c1068
- [x] T4: Implementar servicio de dominio AsistenciaService con reglas de negocio (doble validación contextual, registro en lote, justificación auditada y cálculo de porcentajes) (S1, S2, S3) | route: inline | commit: d0c1068
- [x] T5: Exponer controlador REST AsistenciaController protegido con RBAC contextual y actualizar openapi/openapi.yaml (S1, S2) | route: inline | commit: d0c1068
- [x] T6: Regenerar cliente Angular tipado, verificar compilación ng build y suite completa de pruebas unitarias/integración (S1, S2, S3) | route: inline | commit: d0c1068

## Log
- L1: 2026-11-15: Selección autorizada por el usuario: "estoy de acuerdo, procede" para el Frente 7: Control de Asistencia Escolar. Trazabilidad ODD inicializada con 6 tareas.
- L2: 2026-11-15: Finalizada T1. Creada migración Flyway `V9__modulo_asistencia.sql` con tablas `configuraciones_alerta_asistencia`, `sesiones_asistencia`, `registros_asistencia` y `justificaciones_asistencia`, con índices únicos condicionales para sesiones de clase y jornada diaria, constraints y claves foráneas. Compilación en verde.
- L3: 2026-11-15: Finalizada T2. Creadas entidades JPA (`ConfiguracionAlertaAsistencia` con evaluación de umbrales, `SesionAsistencia` con modalidad dual, `RegistroAsistencia` con minutos de retardo, `JustificacionAsistencia`), enums (`EstadoAsistencia`, `TipoRegistroAsistencia`) y repositorios Spring Data con consultas especializadas y conteos agregados. Batería `AsistenciaEntityTest` (4 tests) en verde.
- L4: 2026-11-15: Finalizada T3. Implementados DTOs con validaciones Jakarta (`ConfiguracionAlertaAsistenciaRequest/Response`, `SesionAsistenciaRequest/Response`, `RegistroAsistenciaItemRequest`, `PlanillaAsistenciaLoteRequest`, `RegistroAsistenciaResponse`, `JustificacionAsistenciaRequest/Response`, `ResumenAsistenciaEstudianteResponse`) y componente `AsistenciaMapper` desacoplado. Compilación limpia (174 archivos compilados).
- L5: 2026-11-15: Finalizada T4. Implementado servicio `AsistenciaService` con reglas de dominio: configuración de alertas por año lectivo, apertura de sesiones con control de no duplicación, registro idempotente de planillas en lote con verificación de matrícula activa, radicación de justificaciones auditadas inmutables y cálculo de resúmenes porcentuales con niveles de alerta (Amarilla y Roja). Suite `AsistenciaServiceTest` (7 tests) en verde.
- L6: 2026-11-15: Finalizada T5. Creado controlador REST `AsistenciaController` (`/api/asistencia`) con endpoints para configuración de alertas, apertura de sesiones duales, registro de planillas en lote, radicación de justificaciones y consultas de ausentismo por estudiante y grupo. Contrato `openapi/openapi.yaml` actualizado con tag `Control de Asistencia`, 10 endpoints y 12 esquemas DTO. Suite `AsistenciaSecurityTest` (10 tests) en verde.
- L7: 2026-11-15: Finalizada T6. Regenerado cliente Angular tipado con nuevo servicio `ControlDeAsistenciaService` y modelos completos. Compilación de producción Angular `ng build` completada exitosamente en verde (0 errores, 306.96 kB). Suite completa de pruebas de backend ejecutada exitosamente: 157 pruebas pasando, 0 fallos, 0 errores. Ausencia total de regresiones confirmada.
- L8: 2026-11-15: Commit atómico generado en rama main: `d0c1068` feat(asistencia): implementar modulo de control de asistencia escolar (backend, openapi y cliente angular). 45 archivos empaquetados.
