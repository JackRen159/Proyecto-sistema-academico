---
name: sistema-escolar-calificaciones
description: Diseña, implementa o prueba notas, evaluaciones, promedios, ponderaciones, definitivas, recuperaciones, cierre de periodos y promoción dentro del SIEE del Sistema de Gestión Escolar.
---

# Módulo Calificaciones

## Flujo conceptual

Estudiante → Matrícula → Curso/Grupo → Asignatura/Área → Periodo → Actividades/Notas → Promedios → Resultado definitivo → Decisión académica según reglas institucionales.

## Alcance posible documentado

Según defina la institución: calificaciones por asignatura/área y periodo, promedios, ponderaciones, escala institucional y equivalencia nacional cuando aplique, precisión/redondeo, estrategias de apoyo o recuperaciones, resultados definitivos, áreas/asignaturas pendientes o de bajo desempeño, promoción, boletines, cierre/reapertura de periodos y reclamaciones.

## Validaciones obligatorias del dominio

- Matrícula válida para el registro que corresponda.
- Profesor asignado al grupo/curso y asignatura.
- Periodo correcto y abierto, salvo procedimiento autorizado de corrección/reapertura.
- Valor dentro de la escala permitida por la institución y periodo.
- Permiso del actor y acceso al recurso concreto.
- Auditoría de correcciones: valor anterior/nuevo, actor, motivo cuando se exija y fecha/hora.

## Regla crítica: no inventar política académica

No codifiques como universales:

- nota mínima para aprobar;
- ponderación, fórmula, cantidad de actividades o promedios;
- redondeo y precisión;
- número de periodos;
- equivalencias numéricas con Superior/Alto/Básico/Bajo;
- umbrales o criterios de promoción/reprobación;
- recuperación, apoyo, reclamación y reapertura.

Estos valores deben configurarse según SIEE/PEI y decisiones institucionales aprobadas. Evita reglas dispersas como `nota >= 3.0` si el umbral debe ser configurable.

## Calidad

Prueba límites de escala, matrícula inválida, asignación docente inexistente, periodo cerrado, cambio autorizado/no autorizado, cálculo reproducible, corrección auditada y concurrencia si afecta resultados. No consideres completo el módulo por guardar números.

## Dependencias

Matrículas, estructura académica, profesores/asignaciones, configuración, documentos-reportes, seguridad, auditoría, base de datos y pruebas. Consulta contexto colombiano para normas/SIEE.

## Referencia

Documento Maestro V2.0, secciones 8–9, 10, 12, 13, 16 y 18.
