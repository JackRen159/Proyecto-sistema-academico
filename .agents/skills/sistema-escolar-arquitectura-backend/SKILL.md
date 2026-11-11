---
name: sistema-escolar-arquitectura-backend
description: Diseña, revisa o modifica la arquitectura interna del backend Java/Spring Boot del Sistema de Gestión Escolar: monolito modular, límites de dominio, capas, servicios, DTO, mappers y transacciones.
---

# Arquitectura backend

## Objetivo

Mantener un backend Spring Boot organizado como **monolito modular**, con responsabilidades claras por dominio y sin acoplar la lógica de negocio a Angular.

## Organización por dominio

- Organiza el código por módulos funcionales, por ejemplo `estudiante`, `acudiente`, `profesor`, `matricula`, `calificacion`, `asistencia`, `horario`, `seguridad`, `auditoria` y `configuracion`.
- Paquete raíz de referencia: `jackren.sistemaacademico.main`. Respeta el paquete real del repositorio salvo migración explícita y planificada.
- Evita dependencias circulares entre módulos. Expón operaciones/casos de uso mediante contratos claros.
- Antes de mover paquetes, inspecciona referencias, pruebas, configuración y migraciones. Prefiere cambios incrementales.

## Responsabilidades por capa

| Capa | Responsabilidad |
|---|---|
| `controller` | Recibir HTTP, validar el contrato de entrada y delegar; no concentrar reglas complejas. |
| `service` / `application` | Coordinar casos de uso, aplicar reglas de negocio y controlar transacciones. |
| `repository` | Acceso a persistencia mediante Spring Data JPA. |
| `entity` | Modelo persistente y relaciones; no usarlo como contrato público. |
| `dto` | Modelos explícitos de entrada/salida; exponer únicamente los datos autorizados. |
| `mapper` | Conversión explícita entre DTO y entidades/modelos internos. |
| `exception` | Excepciones de dominio y traducción global a errores HTTP coherentes. |
| `security` | Autenticación, autorización y protección de endpoints. |
| `config` | CORS, OpenAPI, Jackson, persistencia y configuración transversal. |
| `audit` | Registro controlado de acciones sensibles. |

## Reglas de diseño

- Aplica POO, SOLID, DRY, KISS y Clean Code de forma proporcionada; no crees interfaces, fábricas, capas o patrones sin una necesidad concreta.
- Utiliza inyección por constructor.
- No expongas entidades JPA directamente en controladores ni serialices relaciones persistentes como API pública.
- Mantén las validaciones HTTP/DTO en la frontera y las reglas de negocio en servicios/dominio; la base de datos también debe proteger invariantes.
- Las operaciones sobre varias entidades que deban ser atómicas deben ejecutarse en una transacción.
- No dupliques cálculos o reglas críticas en múltiples controladores o componentes Angular.
- Los errores de negocio deben ser explícitos y traducirse a respuestas HTTP consistentes (400, 401, 403, 404, 409, 500 según corresponda).
- No uses las Collections manuales de ejercicios académicos dentro del producto; el Documento Maestro permite Java Collections.

## Flujo de implementación

1. Identifica el caso de uso, actor, permiso, datos de entrada/salida y reglas.
2. Revisa módulos relacionados y dependencias existentes.
3. Define DTO y comportamiento del caso de uso antes de codificar.
4. Implementa servicio y persistencia necesarios; conserva límites entre dominios.
5. Añade manejo de errores, transacción, autorización y auditoría si corresponde.
6. Añade pruebas unitarias y de integración proporcionales al riesgo.
7. Actualiza OpenAPI, migraciones y documentación si el cambio modifica el contrato o esquema.

## No asumir

No infieras nombres definitivos de paquetes, campos obligatorios, agregados, patrones DDD avanzados, microservicios ni reglas de negocio no documentadas. Confirma primero el repositorio y la especificación del módulo.

## Referencia

Documento Maestro V2.0, secciones 3, 6, 8, 16, 20 y 21.2–21.3.
