---
name: sistema-escolar-convivencia
description: Diseña, implementa o prueba el módulo Convivencia Escolar: observador del estudiante, Ruta de Atención Integral (Situaciones Tipo I, II y III), instrucción del debido proceso (cargos, pruebas, descargos, recursos), medidas/sanciones con Plan de Continuidad Académica y actas del Comité Escolar de Convivencia.
---

# Módulo Convivencia Escolar

## Fuente funcional vinculante (lectura obligatoria)
- **Especificación principal:** `docs/especificacion/modulos/11_Convivencia_v2.md`
- **Casos de uso:** `UC-22`, `UC-23`, `UC-24`, `UC-25`, `UC-PROV-25`, `UC-PROV-26`
- **Reglas obligatorias:** `RN-06` a `RN-10` y `RN-CON-01` a `RN-CON-10`
- **Guía transversal:** `docs/especificacion/00_Guia_General_y_Reglas_Transversales_v2.md`

## Invariantes críticos del dominio
- Observador inmutable: separar hechos objetivos de juicios; correcciones solo mediante adenda vinculada; garantizar versión libre del estudiante.
- Clasificación Tipo I, II o III (Ley 1620 / Decreto 1965) activa protocolos de protección, nunca culpabilidad ni sanción automática (`RN-06`).
- En Tipo III: prohibida la conciliación o careo con la víctima; obligatoria remisión inmediata a Salud y autoridades externas (ICBF, Policía de Infancia, Comisaría).
- Reserva de protección: si el presunto agresor es un acudiente/familiar (`RESERVA_PROTECCION_CONTRA_ACUDIENTE`), bloquear notificaciones y acceso en `UC-25` y enlazar con `UC-PROV-05`.
- Debido proceso bloqueante en backend (`UC-PROV-25` -> `UC-24`): impedir sanción sin apertura notificada, cargos/normas del Manual vigente, traslado de pruebas y término de descargos en días hábiles; respetar doble instancia (reposición/apelación con efecto suspensivo en sanciones graves).
- Continuidad educativa (`RN-08`, Sentencia T-004/2026): prohibido rebajar notas por disciplina; toda suspensión presencial exige Plan de Continuidad Académica sincronizado con Calificaciones y Asistencia.
- Anonimización estricta de terceros menores (víctimas y testigos) en las vistas de `UC-25`.

## Dependencias
`sistema-escolar-configuracion`, `sistema-escolar-estudiantes`, `sistema-escolar-acudientes`, `sistema-escolar-matriculas`, `sistema-escolar-calificaciones`, `sistema-escolar-asistencia`, `sistema-escolar-seguridad`, `sistema-escolar-auditoria`, `sistema-escolar-contexto-colombiano` y `sistema-escolar-calidad-testing`.