---
name: sistema-escolar-finanzas
description: Analiza o implementa el módulo financiero escolar: pensiones, pagos, facturas, cartera, becas y descuentos.
---

# Módulo Finanzas

## Alcance documentado

El módulo de evolución puede abarcar pensiones, pagos, facturas, cartera, becas y descuentos.

## Prioridad

El Documento Maestro indica expresamente que Finanzas puede posponerse si no forma parte del alcance mínimo de V1. Su inclusión depende de prioridad, necesidad validada y capacidad de prueba.

## Antes de diseñar

- Confirma alcance V1 o fase posterior.
- Obtén casos de uso, actores, políticas institucionales y requisitos contables/tributarios aplicables.
- Identifica datos, estados, trazabilidad, permisos, conciliación e integraciones necesarias.
- Verifica las obligaciones normativas vigentes antes de convertirlas en reglas del sistema.

## No asumir

No inventes tarifas, fechas de vencimiento, intereses, recargos, descuentos, becas, medios de pago, facturación electrónica, reglas de cartera, reversos o conciliación. No implementes transacciones financieras reales sin requerimientos y controles aprobados.

## Principios

Integridad transaccional, control de permisos, auditoría, protección de datos, reportes consistentes y pruebas de operaciones críticas.

## Dependencias

Usuarios/seguridad, estudiantes/matrículas según el modelo aprobado, configuración, auditoría, documentos/reportes, base de datos e infraestructura.

## Referencia

Documento Maestro V2.0, secciones 4, 7, 12–14 y 18.
