# Frente 8: Documentos y Reportes Académicos (Backend y Contrato OpenAPI)
Plan y seguimiento de implementación del módulo de emisión y consulta de documentos académicos oficiales (boletines periódicos y finales, certificados de estudio, constancias de matrícula y actas de comisión) en Spring Boot y PostgreSQL.
Dominio: Consolidación de información académica oficial (matrículas activas, asignaturas, calificaciones SIEE Decreto 1290 y asistencias), foliado/consecutivos atómicos por año lectivo, inmutabilidad y auditoría de emisiones, y control de acceso RBAC.

## Specs
- S1: "dame informacion mas detallada con respecto al frente 1"
- S2: "procede con la primera fase"
- S3: "Los datos deben obtenerse desde servicios autorizados del backend. Aplicar control de acceso y contexto académico; no todos los usuarios pueden consultar todos los documentos. Mantener consistencia con los datos académicos y cálculos validados en servidor. Angular no debe construir documentos oficiales usando cálculos no validados por backend."

## Tasks
- [x] T1: Diseñar y crear migración Flyway V10 con tablas de consecutivos de documentos y registro de emisiones/auditoría (S1, S2, S3) | route: inline | commit: 10b57f5
- [x] T2: Implementar entidades JPA, enums de documentos y repositorios Spring Data con control concurrente (S1, S2, S3) | route: inline | commit: 10b57f5
- [x] T3: Implementar DTOs con validaciones Jakarta y DocumentoMapper desacoplado para boletines, certificados y constancias (S1, S2, S3) | route: inline | commit: 10b57f5
- [x] T4: Implementar servicio de dominio DocumentoAcademicoService con reglas de negocio (consolidación SIEE y asistencia, consecutivos atómicos y trazabilidad de emisión) (S1, S2, S3) | route: inline | commit: 10b57f5
- [x] T5: Exponer controlador REST DocumentoAcademicoController protegido con RBAC contextual y actualizar openapi/openapi.yaml (S1, S2) | route: inline | commit: 10b57f5
- [x] T6: Regenerar cliente Angular tipado, verificar compilación ng build y suite completa de pruebas unitarias/integración (S1, S2, S3) | route: inline | commit: 10b57f5

## Log
- L1: 2026-11-17: Autorización del usuario: "procede con la primera fase" para el Frente 8: Documentos y Reportes Académicos. Inicializada trazabilidad ODD con 6 tareas.
- L2: 2026-11-17: Finalizada T1. Creada migración Flyway `V10__modulo_documentos_reportes.sql` con tablas `consecutivos_documentos` y `registro_emisiones_documentos`, restricciones de clave foránea, unicidad condicional, checks y 4 índices especializados.
- L3: 2026-11-17: Finalizada T2. Implementadas entidades JPA (`TipoDocumentoAcademico`, `ConsecutivoDocumento` con formateo atómico y prefijos, `RegistroEmisionDocumento` con trazabilidad inmutable y anulación auditada) y repositorios Spring Data con bloqueo pesimista `PESSIMISTIC_WRITE` para foliados concurrentes. Suite `DocumentoEntityTest` (3 tests) en verde.
- L4: 2026-11-17: Finalizada T3. Implementados DTOs con validaciones Jakarta (`ConfiguracionConsecutivoRequest/Response`, `EmisionDocumentoRequest`, `AnulacionEmisionRequest`, `RegistroEmisionResponse`, `BoletinPeriodoResponse`, `AreaBoletinDTO`, `AsignaturaBoletinDTO`, `ResumenBoletinDTO`, `ConstanciaMatriculaResponse`, `CertificadoEstudioResponse`, `AsignaturaCertificadoDTO`) y componente `DocumentoMapper` desacoplado.
- L5: 2026-11-17: Finalizada T4. Implementado servicio `DocumentoAcademicoService` con consolidación académica oficial: cálculo ponderado de áreas/asignaturas, homologación a escala nacional Decreto 1290 (Superior, Alto, Básico, Bajo), cruce de inasistencias por materia, emisión de constancias de matrícula y certificados con dictamen de promoción escolar, consulta pública por código de verificación y anulación auditada. Suite `DocumentoAcademicoServiceTest` (5 tests) en verde.
- L6: 2026-11-17: Finalizada T5. Creado controlador REST `DocumentoAcademicoController` (`/api/documentos`) con 9 endpoints protegidos por RBAC institucional y endpoint público de verificación QR/hash (`/api/documentos/verificar/{codigoVerificacion}`). Actualizado `SecurityConfig` y contrato centralizado `openapi/openapi.yaml` con tag `Documentos y Reportes Académicos`. Suite `DocumentoSecurityTest` (9 tests) en verde.
- L7: 2026-11-17: Finalizada T6. Regenerado cliente Angular tipado con nuevo servicio `DocumentosYReportesAcadmicosService` y modelos TypeScript. Compilación de producción Angular `ng build` completada con éxito (306.96 kB). Suite completa de pruebas automatizadas de backend ejecutada exitosamente: **174 pruebas pasando, 0 fallos, 0 errores**. Ausencia total de regresiones confirmada.
- L8: 2026-11-17: Commit atómico generado en rama main: `10b57f5` feat(reportes): implementar modulo de documentos y reportes academicos (backend, openapi y cliente angular). 44 archivos empaquetados.
