---
name: sistema-escolar-asistencia
description: Diseña, implementa o prueba el registro y consulta de asistencia: eventos diarios o por sesión, ausencias, justificaciones, retardos, porcentajes y alertas institucionales.
---

# Módulo Asistencia

## Fuente funcional vinculante (V2)

Antes de diseñar, implementar o probar este módulo, lee:
- Especificación del módulo: `docs/especificacion/modulos/08_Asistencia_v2.md`
- Reglas transversales: `docs/especificacion/00_Guia_General_y_Reglas_Transversales_v2.md`
- Casos de uso cubiertos: `UC-19`, `UC-03`, `UC-09`, `UC-PROV-18`, `UC-PROV-19`
- Reglas de negocio aplicables: `RN-04`, `RN-05`, `RN-ASI-01` a `RN-ASI-08`
- Convierte cada fila `A#` y `E#` (y su código HTTP esperado) en criterios de aceptación y pruebas automatizadas.

## Alcance documentado

- Registro de asistencia diaria o por sesión.
- Ausencias justificadas/injustificadas y retardos.
- Porcentajes y alertas según reglas institucionales.
- Consultas y reportes autorizados.

## Reglas documentadas

- El estudiante debe tener matrícula válida para los registros de asistencia que correspondan.
- El profesor debe estar asignado al curso/grupo y asignatura antes de registrar o modificar asistencia cuando la operación dependa de esa asignación.
- La autorización se verifica en backend y sobre el recurso/contexto.
- Las modificaciones sensibles deben auditarse.

## Pendientes: no inventar

No fijes umbrales de inasistencia, porcentajes, justificaciones aceptadas, plazos, responsables, efectos sobre promoción, granularidad diaria/sesión definitiva ni reglas de alertas sin una política institucional aprobada.

## Dependencias

Matrículas, estructura académica, profesores/asignaciones, horarios si la asistencia se registra por sesión, configuración, seguridad, auditoría, reportes, API y pruebas.

## Pruebas

Matrícula válida/inválida, docente asignado/no asignado, duplicados, estados de asistencia, justificación, cambios auditados, periodos y cálculo de porcentajes solo con parámetros aprobados.

## Referencia

Documento Maestro V2.0, secciones 4, 7–8, 12–13, 16 y 18.
