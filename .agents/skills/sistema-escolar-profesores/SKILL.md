---
name: sistema-escolar-profesores
description: Diseña, implementa o revisa el módulo Profesores: datos docentes, asignaturas, cursos/grupos asignados, horarios, asistencia, calificaciones y observaciones autorizadas.
---

# Módulo Profesores

## Alcance documentado

- Administrar datos del docente.
- Relacionar profesores con asignaturas y cursos/grupos asignados.
- Habilitar, conforme a permisos, funciones docentes relacionadas con horarios, asistencia, calificaciones y observaciones.
- Conectar con estructura académica y asignaciones académicas.

## Regla de autorización contextual

Un profesor solo puede registrar o modificar calificaciones o asistencia cuando está asignado al curso/grupo y asignatura que corresponden a la operación. La autorización debe verificarse en backend y sobre el recurso concreto; no basta con que la cuenta tenga el rol PROFESOR.

## Reglas de diseño

- Mantén la asignación docente como relación verificable entre profesor, curso/grupo, asignatura y periodo/vigencia que se defina.
- No supongas cardinalidades, carga académica máxima, requisitos laborales, estados contractuales ni reglas de reemplazo que no estén documentados.
- Audita operaciones sensibles asociadas con asignaciones y cambios académicos.
- Aplica DTO, transacciones, constraints y pruebas correspondientes.

## Dependencias

Estructura académica, asignaciones académicas, calificaciones, asistencia, horarios, seguridad, auditoría, API, base de datos y pruebas.

## Referencia

Documento Maestro V2.0, secciones 4–5, 7–8, 9, 12 y 18.
