# Plan de Acción Integral: Perfeccionamiento y Robustecimiento de los 7 Casos de Uso del Ciclo Escolar

Plan de acción derivado del diagnóstico técnico de los casos de uso UC-01, UC-12, UC-14, UC-13, UC-15, UC-19 y UC-17.

## Specs
- S1: En UC-13 (Matrícula), implementar bloqueo pesimista de escritura (`@Lock(LockModeType.PESSIMISTIC_WRITE)`) en `GrupoRepository.findByIdWithLock` y utilizarlo en `MatriculaService.matricular` para prevenir condiciones de carrera en la reserva de cupos (`cupoMaximo`, UC-13 E8, RN-MAT-05).
- S2: En UC-17 (Calificaciones), ejecutar el recálculo automático de definitivas de asignatura y área curricular inmediatamente tras persistir las notas de actividad en `CalificacionService.registrarCalificacionesLote`, garantizando sincronización en tiempo real sin requerir cálculo manual.
- S3: En UC-01 (Seguridad), implementar el filtro `VerificacionPasswordTemporalFilter` en la cadena de seguridad (`SecurityConfig`) para rechazar con HTTP 403 Forbidden cualquier petición hacia endpoints protegidos de negocio si el JWT contiene el claim `debeCambiarPassword: true`, permitiendo únicamente operaciones de autenticación y cambio de contraseña (`UC-01 A1`).
- S4: En UC-14 (Estructura), validar en cascada en `AnioLectivoService.actualizar` que la modificación de fechas de inicio o fin del año lectivo no deje periodos académicos existentes fuera de los nuevos límites temporales (`UC-14 E1`).
- S5: En UC-19 (Asistencia), optimizar la toma de asistencia en lote en `AsistenciaService.registrarPlanillaLote` eliminando consultas N+1 sobre asistencias del día y salidas autorizadas mediante consultas `IN` previas.

## Tasks
- [x] T1 (S1): Implementar `findByIdWithLock` en `GrupoRepository`, integrarlo en `MatriculaService.matricular` y verificar con pruebas unitarias y de concurrencia. (Completado y verificado en `MatriculaServiceTest`).
- [x] T2 (S2): Integrar el recálculo interno de definitivas de periodo en `CalificacionService.registrarCalificacionesLote` y certificar con pruebas. (Completado y verificado en `CalificacionServiceTest`).
- [x] T3 (S3): Crear `VerificacionPasswordTemporalFilter`, configurarlo en `SecurityConfig` y verificar rechazo 403 con pruebas de seguridad. (Completado y verificado en `SecurityEndpointsTest`).
- [x] T4 (S4): Agregar validación en cascada de rango de fechas de periodos en `AnioLectivoService.actualizar` y verificar con pruebas. (Completado y verificado en `AnioLectivoServiceTest`).
- [x] T5 (S5): Optimizar `AsistenciaService.registrarPlanillaLote` eliminando consultas N+1 y verificar que todas las pruebas pasen. (Completado y verificado en `AsistenciaServiceTest`).
- [x] T6: Ejecutar suite de pruebas completa (467 tests OK), verificar compilación limpia de frontend y backend, y realizar commit y push a GitHub.

## Log
- L1: El usuario solicitó proceder con el plan de acción para dejar los casos de uso al 100% funcionales, y al finalizar realizar el commit y subir los cambios a GitHub.
- L2: Implementado bloqueo pesimista en `GrupoRepository`, recálculo automático de notas en `CalificacionService`, filtro perimetral de seguridad `VerificacionPasswordTemporalFilter`, validación en cascada de fechas de periodos en `AnioLectivoService` y optimización en lote de asistencia. Suite completa verificada: 467 tests unitarios y de integración exitosos, frontend compilado sin errores.
