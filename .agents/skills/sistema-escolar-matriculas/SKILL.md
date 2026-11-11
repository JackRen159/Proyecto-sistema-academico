---
name: sistema-escolar-matriculas
description: Analiza, diseña, implementa o prueba el módulo Matrículas: proceso, estado, historial y relación del estudiante con curso/grupo y año lectivo.
---

# Módulo Matrículas

## Alcance documentado

- Gestionar el proceso de matrícula.
- Mantener estado e historial de matrícula.
- Vincular estudiante con curso/grupo y año lectivo.
- Distinguir estados del proceso según casos de uso formalizados.
- Conectar la matrícula con calificaciones y asistencia.

## Reglas documentadas

- Un estudiante debe tener una matrícula válida para recibir determinadas calificaciones y registros de asistencia.
- La matrícula debe conservar historial.
- Las operaciones sobre varias entidades relacionadas deben ser atómicas cuando corresponda.
- Deben controlarse conflictos y duplicados según reglas aprobadas.
- La autorización de matrícula debe depender del caso de uso y permisos del actor.

## Decisiones no definidas

No inventes etapas, documentos exigidos, fechas límite, cupos, criterios de admisión, renovación automática, traslados, cancelaciones, estados exactos ni restricciones de una matrícula por año/grupo. Deben salir de casos de uso y reglas institucionales aprobadas.

## Pruebas

Deriva pruebas de estados, historial, duplicados, relaciones válidas, operaciones transaccionales, permisos y concurrencia cuando aplique.

## Dependencias

Estudiantes, acudientes cuando el flujo los incluya, estructura académica, seguridad, auditoría, base de datos, API y calidad. Usa frontend Angular para completar el flujo de extremo a extremo.

## Referencia

Documento Maestro V2.0, secciones 4, 7–8, 18, 21.2 y 22.
