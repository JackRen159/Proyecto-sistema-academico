---
name: sistema-escolar-auditoria
description: Diseña o implementa trazabilidad de operaciones sensibles del Sistema de Gestión Escolar: actor, acción, entidad, valores, motivo, fecha, resultado y correlation ID.
---

# Módulo Auditoría

## Objetivo

Registrar evidencia suficiente para diagnóstico y trazabilidad de operaciones sensibles, sin convertir el log de auditoría en un almacén de secretos o datos excesivos.

## Datos conceptuales de un evento

- usuario/identificador interno;
- acción (por ejemplo `MODIFICAR_NOTA`);
- entidad afectada y referencia del registro;
- valor anterior y nuevo cuando sea pertinente;
- motivo cuando la política o la operación lo exija;
- fecha/hora del servidor con política temporal consistente;
- resultado cuando sea útil;
- correlation/request ID para enlazar logs sin exponer secretos.

## Áreas que deben contemplar auditoría

Cambios de datos personales, relaciones de acudientes, autorizaciones de retiro, asignaciones docentes, matrícula, calificaciones, asistencia, reglas de configuración, roles y permisos.

## Protección

- Nunca registrar contraseñas, JWT, refresh tokens, cookies, secretos ni datos sensibles innecesarios.
- Restringe lectura de registros; usuarios ordinarios no deben modificarlos.
- Define retención y consulta de acuerdo con políticas institucionales y obligaciones aplicables.
- Las correcciones administrativas deben conservar quién, cuándo, por qué, qué cambió y resultado.

## Diseño

Define eventos de dominio/operación relevantes sin acoplar todos los módulos a una implementación concreta. Evita duplicación y permite relacionar auditoría con request/correlation ID. Si se requiere auditoría de fallos, delimita qué datos se pueden almacenar de forma segura.

## Pruebas

Verifica emisión de eventos, valores pertinentes, usuario correcto, no exposición de secretos, acceso restringido y comportamiento ante fallo de la operación auditada.

## Referencia

Documento Maestro V2.0, secciones 3, 4, 8, 10–13, 16 y 20.
