# Frente 9: Hardening Seguridad V2, Configuración Institucional V2 y Auditoría Global append-only
Plan y seguimiento de implementación del Módulo 01 (Seguridad y Acceso V2), Módulo 06 (Configuración Institucional V2) y Módulo 12 Base (Auditoría Transversal Inmutable con Integridad Criptográfica SHA-256) en Spring Boot, PostgreSQL y OpenAPI.
Dominio: Ciclo de vida de usuarios, credenciales temporales, múltiples roles con vigencia, órganos colegiados nominativos, identificación DANE/firmas, Habeas Data Ley 1581, SIEE inmutable con validación matemática, y bitácora transversal append-only con encadenamiento criptográfico.

## Specs
- S1: "Vamos a planificar el Frente 9 (Hardening Seguridad V2, Configuración Institucional V2 y Auditoría Global append-only)."
- S2: "Módulo 01 Seguridad y Acceso V2: UC-01, UC-PROV-01, UC-PROV-02, UC-PROV-03. Separar COORDINADOR_ACADEMICO y COORDINADOR_CONVIVENCIA, agregar ORIENTADOR_ESCOLAR preservando compatibilidad; múltiples roles con vigencia temporal por persona (RN-SEG-04); miembros nominativos de órganos colegiados sin cuentas compartidas (RN-SEG-01); ciclo de vida de cuentas con estados ACTIVA, PENDIENTE_ACTIVACION, BLOQUEADA, INACTIVA, INACTIVA_HISTORICA; primer ingreso con cambio obligatorio de contraseña temporal y consentimiento de Política de Datos; recuperación de contraseña por autoservicio en tiempo constante sin enumeración de cuentas."
- S3: "Módulo 06 Configuración Institucional V2: UC-26, UC-PROV-13, UC-PROV-14. Identificación oficial del colegio (código DANE 12 dígitos, NIT, resoluciones, carácter OFICIAL/PRIVADO, firmas autorizadas de Rectoría y Secretaría Académica); Política de Tratamiento de Datos Personales inmutable versionada con registro auditable de consentimientos (Ley 1581 de 2012 / Decreto 1377 de 2013); versionamiento inmutable del SIEE y Manual de Convivencia con validación matemática (continuidad de escala sin solapamientos, sumatoria exacta de ponderaciones al 100.00%, no reprobación en Preescolar, actas de Consejo Académico y Consejo Directivo); y catálogo centralizado de parámetros institucionales (plazos ARCO <= 15 días hábiles)."
- S4: "Módulo 12 Base Auditoría Global: UC-27, UC-PROV-29. Bitácora transversal inmutable append-only con trigger/restricción en PostgreSQL contra UPDATE y DELETE; registro canónico de eventos con usuario, rol activo, módulo, acción, entidad, valorAnterior, valorNuevo, motivo, resultado, correlationId, IP y timestamp; exclusión absoluta de contraseñas, hashes, JWT y secretos (RN-DOC-06); cadena criptográfica encadenada SHA-256 (hashEvento = SHA-256(canónico + hashAnterior)); servicio de verificación criptográfica de integridad de la cadena y exportación regulada con manifiesto firmado."

## Tasks
- [x] T1: Diseñar y crear migraciones Flyway V11 y V12 con esquemas de Seguridad V2, Configuración V2 y Auditoría Global con trigger append-only y cadena de hashes (S1, S2, S3, S4) | route: inline | commit: ab6721d
- [x] T2: Implementar entidades JPA, enums ampliados (Rol con COORDINADOR_ACADEMICO/COORDINADOR_CONVIVENCIA/ORIENTADOR_ESCOLAR, EstadoUsuario, OrganoColegiado, EstadoVersionConfiguracion, etc.) y repositorios Spring Data (S1, S2, S3, S4) | route: inline | commit: 4e17ee3
- [x] T3: Implementar componente de Auditoría Global (AuditoriaGlobalService y repositorio) con encadenamiento SHA-256 atómico, verificación criptográfica de integridad y exportación regulada (S1, S4) | route: inline | commit: 005d444
- [x] T4: Implementar servicios de Seguridad V2 (UsuarioService, AuthService y RecuperacionPasswordService) con ciclo de vida de cuentas, login con primer ingreso, conmutación de rol activo, membresía en órganos colegiados y recuperación de credenciales (S1, S2) | route: inline | commit: 0f84caf
- [x] T5: Implementar servicios de Configuración Institucional V2 (ConfiguracionInstitucionalService, PoliticaDatosService y SieeConfiguracionService) con identificación legal DANE/firmas, Habeas Data inmutable y validación matemática de SIEE/Manual (S1, S3) | route: inline | commit: 7b290c2
- [x] T6: Implementar DTOs con validaciones Jakarta, mappers desacoplados, controladores REST protegidos con RBAC contextual y actualizar contrato openapi/openapi.yaml (S1, S2, S3, S4) | route: inline | commit: pending
- [x] T7: Implementar suite exhaustiva de pruebas unitarias y de integración para A# y E#, regenerar cliente Angular tipado y verificar build limpio (S1, S2, S3, S4) | route: inline | commit: pending

## Criterios de Aceptación Derivados de Flujos Alternos (A#) y Excepciones (E#)

### Módulo 01: Seguridad y Acceso V2
- **UC-01 (Iniciar y cerrar sesión):**
  - `A1`: Primer ingreso con credencial temporal (`PENDIENTE_ACTIVACION` o `debeCambiarPassword=true`) restringe el acceso al flujo de cambio obligatorio de contraseña y aceptación auditable de Política de Tratamiento de Datos Personales, pasando la cuenta a `ACTIVA` tras completarlo.
  - `A2`: Usuario con múltiples roles vigentes selecciona el rol activo para la sesión; el token emitido opera únicamente con los privilegios del rol seleccionado sin acumular permisos de otros roles.
  - `A3`: Cuentas en estado `INACTIVA_HISTORICA` reciben acceso acotado a consulta histórica de certificados y constancias, bloqueando operaciones académicas del año lectivo en curso.
  - `A4`: Cierre de sesión (`/api/auth/logout`) revoca inmediatamente el `jti` en servidor y el cliente descarta credenciales en memoria.
  - `E1`: Credenciales inválidas o usuario inexistente responden HTTP 401 con mensaje genérico en tiempo constante sin revelar si falló el usuario o la contraseña.
  - `E2`: Cuenta en estado `BLOQUEADA` o `INACTIVA` sin acceso histórico es rechazada con HTTP 403/401 y genera evento de seguridad en auditoría.
  - `E3`: Cuenta sin ningún rol vigente o acudiente sin vínculos activos recibe HTTP 403 informando la falta de habilitación institucional.
  - `E4`: Token expirado o revocado forzosamente en servidor responde HTTP 401 sin bucles de redirección.
  - `E5`: Más de 5 intentos fallidos en 60 segundos por IP activa rate limiting HTTP 429 con cabecera `Retry-After: 60`.
  - `E6`: Petición fuera del alcance contextual del rol activo responde HTTP 403 sin destruir la sesión válida.

- **UC-PROV-01 (Administrar cuentas y credenciales):**
  - `A1`: Aprovisionamiento vinculado unívocamente a persona física (Estudiante, Acudiente, Profesor, Administrativo) evitando cuentas duplicadas.
  - `A2`: Suspensión, bloqueo administrativo o inactivación exige justificación obligatoria e invalida de inmediato todas las sesiones activas en el servidor.
  - `A3`: Transición a `INACTIVA_HISTORICA` tras graduación o retiro definitivo acotando los permisos a consulta documental.
  - `E1`: Conflicto de unicidad en identificador o username responde HTTP 409 Conflict.
  - `E2`: Intento de crear cuenta para acudiente sin vínculo validado o estudiante de nivel no habilitado responde HTTP 400/409.
  - `E3`: Rol Secretaría intentando crear o modificar cuentas de roles directivos, docentes o administradores responde HTTP 403 Forbidden.
  - `E4`: Intento de eliminación física de una cuenta con historial académico o de auditoría es denegado de forma absoluta con HTTP 409 (solo procede inactivación).

- **UC-PROV-02 (Asignación, vigencia y revocación de roles y órganos):**
  - `A1`: Asignación de múltiples roles compatibles a la misma persona con vigencias independientes por año lectivo o rango temporal.
  - `A2`: Registro nominativo de miembros en órganos colegiados (Comisión de Evaluación y Promoción, Comité de Convivencia, Consejo Académico, Consejo Directivo) con número de acta, año lectivo y vigencia sin cuentas compartidas.
  - `A3`: Vencimiento automático de roles temporales al expirar la fecha de encargo.
  - `E1`: Incompatibilidad de roles / conflicto de segregación de funciones (ej. Estudiante activo con Docente/Coordinador/Rector) rechazado con HTTP 409.
  - `E2`: Asignación o revocación de roles fuera de competencia jerárquica del actor denegada con HTTP 403.
  - `E3`: Intento de revocar el último rol Administrador o Rector activo del sistema rechazado con HTTP 409 para prevenir bloqueo institucional.

- **UC-PROV-03 (Cambiar contraseña y recuperación de acceso):**
  - `A1`: Cambio voluntario u obligatorio de contraseña en sesión activa actualiza el hash seguro (BCrypt), marca la cuenta activa e invalida sesiones en otros dispositivos.
  - `A2`: Restablecimiento presencial asistido en Secretaría genera credencial temporal auditada y deja la cuenta en `PENDIENTE_ACTIVACION` con cambio obligatorio.
  - `E1`: Solicitud pública de recuperación para identificador inexistente o sin canal verificado responde con mensaje neutro uniforme en tiempo constante (anti-enumeración).
  - `E2`: Token de recuperación expirado, inválido o reutilizado es rechazado con HTTP 400 Bad Request.
  - `E3`: Nueva contraseña que incumpla la política de complejidad o repita la contraseña actual/temporal es rechazada con HTTP 400.
  - `E4`: Cuenta con bloqueo administrativo o pérdida de patria potestad impide el restablecimiento y genera alerta de seguridad.

### Módulo 06: Configuración Institucional V2
- **UC-26 (Configurar, aprobar y versionar SIEE y Manual de Convivencia):**
  - `A1`: Preparación técnica de borrador por Coordinación (`LISTA_PARA_APROBACION`) y posterior formalización/publicación por Rectoría con actas colegiadas.
  - `A2`: Aprobación anticipada para el año lectivo siguiente en planeación en estado `APROBADA_VIGENCIA_FUTURA` sin alterar el año en curso.
  - `A3`: Ajuste operativo con vigencia a partir de periodo futuro no iniciado, garantizando irretroactividad sobre periodos cerrados.
  - `A4`: Simulación y validación previa de reglas de cálculo, ponderaciones y redondeo `BigDecimal` en borrador.
  - `E1`: Escala institucional con vacíos, solapamientos entre rangos (Superior, Alto, Básico, Bajo) o ponderaciones que no sumen exactamente 100.00% rechazada con HTTP 400/409.
  - `E2`: Intento de publicar versión sin registrar actas obligatorias de órganos colegiados (Consejo Académico/Directivo) o fecha de vigencia rechazado con HTTP 409.
  - `E3`: Intento de aplicar retroactivamente una nueva versión sobre año o periodo académico cerrado rechazado con HTTP 409.
  - `E4`: Reglas contrarias a normativa nacional (como configurar reprobación en Preescolar o eliminar descargos/recursos) rechazadas con HTTP 400.
  - `E5`: Intento de editar in-situ o eliminar físicamente una versión en estado `VIGENTE`, `APROBADA_VIGENCIA_FUTURA` o `HISTORICA` rechazado con HTTP 409 (solo procede clonar a nuevo borrador).

- **UC-PROV-13 (Identificación legal, Política de Datos y parámetros transversales):**
  - `A1`: Publicación inmutable de nueva versión de la Política de Tratamiento de Datos Personales con aviso de privacidad, conservando consentimientos previos vinculados a su versión histórica original.
  - `A2`: Actualización de resolución de estudios, NIT, DANE o cambio de Rector/Secretario firmante aplica a nuevos documentos sin alterar históricos.
  - `E1`: Parámetro operativo que exceda límites legales (ej. plazo de respuesta ARCO > 15 días hábiles conforme al Art. 15 Ley 1581) rechazado con HTTP 400.
  - `E2`: Intento de sobrescribir o eliminar una versión histórica de Política de Datos aceptada por usuarios rechazado con HTTP 409.
  - `E3`: Usuario sin rol Rector o Administrador intentando alterar parámetros institucionales reservados denegado con HTTP 403 Forbidden.

- **UC-PROV-14 (Consultar versiones de configuración y parámetros):**
  - `A1`: Consulta y comparación (`diff`) de versiones históricas del SIEE y Manual aplicadas a años lectivos anteriores.
  - `A2`: Despliegue completo de la Política de Datos vigente para lectura y consentimiento informado en primer ingreso y matrícula.
  - `E1`: Usuario no directivo (Estudiante, Acudiente, Docente) intentando consultar borradores no aprobados o parámetros internos de seguridad denegado con HTTP 403 (solo acceso a versiones `VIGENTE`).

### Módulo 12 Base: Auditoría Global y Verificación Criptográfica
- **UC-27 (Consultar reportes institucionales y auditoría transversal):**
  - `A1`: Anonimización y agregación estadística por defecto en consultas de datos sensibles para roles sin autorización expresa.
  - `A2`: Paginación estricta y límites de volumen en consultas masivas de auditoría.
  - `A3`: Respuesta clara con estructura vacía y metadatos cuando no existen coincidencias para los filtros.
  - `A4`: Reconstrucción cronológica completa de eventos de una entidad o seguimiento unificado por `correlationId`.
  - `E1`: Consulta o filtro de auditoría fuera de la competencia funcional del rol activo denegado con HTTP 403 Forbidden.
  - `E2`: Denegación absoluta en backend y trigger PostgreSQL contra cualquier `UPDATE` o `DELETE` sobre registros de auditoría (estrictamente `append-only`, HTTP 403/409).
  - `E3`: Solicitud de exportación con campos reservados sin motivo obligatorio o autorización reforzada rechazada con HTTP 400/403.
  - `E4`: Consulta abusiva que supere los límites de frecuencia/volumen rechazada con HTTP 429/400.

- **UC-PROV-29 (Integridad criptográfica, alertas y exportación regulada):**
  - `A1`: Archivado histórico firmado (`cold storage`) para eventos de años lectivos antiguos cerrados garantizando cadena de custodia.
  - `A2`: Detección en tiempo real de anomalías (múltiples HTTP 403 o violaciones de restricción legal) generando alerta de seguridad.
  - `E1`: Detección de discrepancia o manipulación directa en la cadena de hashes SHA-256 (`hashEvento != SHA-256(canónico + hashAnterior)`) genera estado `ALERTA_CRITICA_INTEGRIDAD_AUDITORIA` con identificación exacta del rango comprometido.
  - `E2`: Intento de purgar registros de auditoría o fijar retención menor a la legal denegado con HTTP 403/409.
  - `E3`: Exportación forense completa sin autorización expresa de Rectoría o sin número de requerimiento oficial bloqueada con HTTP 403/400.

## Log
- L1: 2026-11-18: Planificación y diseño integral del Frente 9 (Hardening Seguridad V2, Configuración Institucional V2 y Auditoría Global append-only). Especificación estructurada con 7 tareas incrementales, esquema de migraciones V11 y V12, y matriz de criterios de aceptación para todos los flujos alternos (A#) y excepciones (E#).
- L2: 2026-11-18: Finalizada T1. Creadas migraciones Flyway `V11__modulo_seguridad_v2.sql` (usuarios V2, usuario_roles, organos_colegiados_miembros, tokens_recuperacion_password) y `V12__modulo_configuracion_v2_auditoria_global.sql` (institución DANE 12 dígitos, política de datos y consentimientos, SIEE inmutable, manual de convivencia, parámetros institucionales y auditoria_eventos con trigger PostgreSQL append-only y cadena de hashes SHA-256). Commit: `ab6721d`.
- L3: 2026-11-18: Finalizada T2. Implementadas 10 entidades JPA, 8 enumeraciones ampliadas (roles especializados V2, estados de cuenta, órganos y vigencias) y 10 repositorios Spring Data JPA. Suite `Frente9EntityTest` (7 tests) en verde. Commit: `4e17ee3`.
- L4: 2026-11-18: Finalizada T3. Implementado componente de dominio `AuditoriaGlobalService` con encadenamiento SHA-256 atómico, verificación de integridad de la cadena y exportación regulada de paquetes firmados (UC-27, UC-PROV-29). Suite `AuditoriaGlobalServiceTest` (5 tests) en verde. Commit: `005d444`.
- L5: 2026-11-18: Finalizada T4. Implementados servicios de Seguridad V2 (`UsuarioService`, `RecuperacionPasswordService` con anti-enumeración en tiempo constante E1, y `AuthService` ampliado con primer ingreso, conmutación de rol activo A2 y cambio de contraseña). Suites `AuthServiceTest`, `UsuarioServiceTest` y `RecuperacionPasswordServiceTest` (15 tests) en verde. Commit: `0f84caf`.
- L6: 2026-11-18: Finalizada T5. Implementados servicios de Configuración Institucional V2 (`ConfiguracionInstitucionalService` con validación DANE y tope ARCO <= 15 días, `PoliticaDatosService` inmutable y `SieeConfiguracionService` con validación matemática de escalas y ponderaciones al 100.00% y actas obligatorias). Suite `Frente9ConfiguracionServiceTest` (6 tests) en verde. Commit: `7b290c2`.
- L7: 2026-11-18: Finalizada T6. Implementados controladores REST RBAC (`AuthController` ampliado, `UsuarioController`, `ConfiguracionInstitucionalController`, `AuditoriaGlobalController`), actualización de `SecurityConfig` con endpoints públicos/protegidos y contrato centralizado `openapi/openapi.yaml` con 3 tags nuevos y 23 rutas REST. Suites `UsuarioSecurityTest`, `ConfiguracionSecurityTest`, `AuditoriaSecurityTest`, `OpenApiSecurityTest` y `SecurityEndpointsTest` en verde.
- L8: 2026-11-18: Finalizada T7. Regenerado cliente Angular tipado (`npm run generate:api`) con nuevos servicios TypeScript y modelos. Verificado build limpio de Angular SPA (`ng build`, 309.71 kB) y tests unitarios de frontend (4 test files, 9 tests en verde). Suite completa de backend ejecutada exitosamente: **215 pruebas pasando, 0 fallos, 0 errores**. Ausencia total de regresiones confirmada.
