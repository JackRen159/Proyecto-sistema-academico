---
name: sistema-escolar-acudientes
description: Analiza, diseña, implementa o prueba el módulo Acudientes y el vínculo Estudiante–Acudiente: responsables, parentesco, autorizaciones, acudiente principal y acceso contextual.
---

# Módulo Acudientes

## Fuente funcional vinculante (V2)

Antes de diseñar, implementar o probar este módulo, lee:
- Especificación del módulo: `docs/especificacion/modulos/02_Estudiantes_y_Acudientes_v2.md`
- Reglas transversales: `docs/especificacion/00_Guia_General_y_Reglas_Transversales_v2.md`
- Casos de uso cubiertos: `UC-12`, `UC-06`, `UC-07`, `UC-PROV-05`
- Reglas de negocio aplicables: `RN-09`, `RN-EST-02`, `RN-EST-05`, `RN-EST-06`, `RN-EST-07`
- Convierte cada fila `A#` y `E#` (y su código HTTP esperado) en criterios de aceptación y pruebas automatizadas.

## Alcance documentado

- Administrar padres/acudientes y estudiantes asociados.
- Permitir acceso del acudiente únicamente a la información de estudiantes vinculados y autorizados.
- Modelar el vínculo estudiante–acudiente como relación explícita cuando contiene atributos o reglas propias.
- La relación puede contemplar parentesco, responsabilidad y autorización de retiro.

## Invariantes y cautelas

- El documento identifica como pendiente confirmar si todo estudiante debe tener al menos un acudiente/responsable activo y las excepciones.
- Si se establece un único acudiente principal, la regla debe protegerse en servicio y base de datos, considerando transacciones y concurrencia.
- Debe definirse qué ocurre al desactivar o desvincular al acudiente principal.
- Debe definirse cuándo se permite eliminación física y cuándo se conserva historial mediante estado/inactivación.
- Protege la integridad relacional y las reglas de unicidad que hayan sido aprobadas y documentadas.
- Los permisos del acudiente son contextuales: vínculo activo/autorizado y alcance de la información consultable.
- Audita cambios sensibles en relaciones y autorizaciones.

## No asumir

No inventes parentescos permitidos, número máximo de acudientes, obligatoriedad de dos acudientes, reglas de custodia, autorización legal, prioridad automática, proceso de reemplazo ni campos obligatorios. La regla de acudiente principal y sus excepciones deben confirmarse formalmente.

## Dependencias

Estudiantes, seguridad, auditoría, persistencia/migraciones, API/OpenAPI y pruebas de concurrencia. Añade frontend si se implementan pantallas.

## Referencia

Documento Maestro V2.0, secciones 4–5, 7, 12, 18, 21.2 y 22.
