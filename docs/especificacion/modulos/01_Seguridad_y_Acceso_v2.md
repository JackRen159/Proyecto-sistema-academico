# CASOS DE USO — SISTEMA DE GESTIÓN ESCOLAR

## Módulo 01 — Seguridad y acceso

**Autenticación, cierre de sesión, administración de cuentas, roles, recuperación de acceso y control de acceso contextual**

- **Orden recomendado:** 01 de 12
- **Casos de uso incluidos:** `UC-01`, `UC-PROV-01`, `UC-PROV-02`, `UC-PROV-03`
- **Referencia de desarrollo:** Fases 1 y 2 — fundación del backend y seguridad base
- **Dependencias:** Fundación técnica del backend; usuarios, personas registradas y roles habilitados (`UC-PROV-01`, `UC-PROV-02`).

> `UC-01` es transversal: la autenticación y autorización contextual protegen las operaciones de los demás módulos. Los casos `UC-PROV-01`, `UC-PROV-02` y `UC-PROV-03` poseen identificador provisional hasta su incorporación definitiva al catálogo maestro y cubren el ciclo de vida de cuentas, asignación/revocación de roles y gestión de contraseñas.

*Fragmentación del documento fuente · versión actualizada tras auditoría de Fase 1*

---

## UC-01 · Iniciar y cerrar sesión

| Campo | Detalle |
| :--- | :--- |
| **ID** | UC-01 |
| **Nombre** | Iniciar y cerrar sesión |
| **Actores principales** | Administrador, Estudiante (según política institucional configurable por nivel educativo), Acudiente / adulto responsable validado, Docente de área, Docente titular/director de grupo, Coordinador Académico, Coordinador de Convivencia, Rector, Secretaría |
| **Actores secundarios / sistemas** | Proveedor de identidad/autenticación (Spring Security); servicio de control de sesiones/tokens; servicio de auditoría; servicio de notificaciones (si aplica) |
| **Descripción** | Permite autenticar a una persona y abrir una sesión con los permisos correspondientes a su rol activo y contexto institucional. Incluye validación de primer ingreso (cambio obligatorio de contraseña temporal y aceptación auditable de la Política de Tratamiento de Datos Personales), selección y conmutación de rol/contexto activo en usuarios con múltiples roles vigentes, modo de acceso histórico restringido para egresados o retirados, cierre explícito y expiración o revocación forzosa de sesión. |
| **Propósito / valor** | Proteger la información escolar, garantizar la trazabilidad y responsabilidad demostrada sobre el acceso a datos de niños, niñas y adolescentes (Ley 1581 de 2012 y Ley 1098 de 2006) y limitar cada operación a las funciones y datos autorizados. |
| **Disparador** | El actor selecciona Iniciar sesión, Cambiar rol/contexto activo o Cerrar sesión (o el sistema detecta expiración o revocación de la sesión en curso). |
| **Precondiciones** | El usuario tiene una cuenta creada en estado `ACTIVA`, `PENDIENTE_ACTIVACION` o `INACTIVA_HISTORICA` y un medio de autenticación habilitado (`UC-PROV-01`). Las cuentas deben estar asociadas a una persona registrada y a los roles autorizados y vigentes (`UC-PROV-02`). Para acudientes, debe existir al menos un vínculo familiar o de representación legal validado. |
| **Invariantes y reglas de negocio** | La autenticación no implica acceso irrestricto; cada petición valida en backend identidad, rol activo, institución, vínculo con estudiante, matrícula, asignación docente, vigencia del periodo y clasificación del recurso (`RN-01`, `RN-09`, `RN-10`). Nunca se almacenan contraseñas en texto plano (hashing BCrypt/Argon2) ni se guardan JWT, tokens de acceso, refresh tokens o credenciales en `localStorage` o `sessionStorage` (cookie `HttpOnly` + `Secure` + `SameSite` con protección CSRF, o memoria volátil). Los órganos colegiados (Comisión de Evaluación y Promoción, Comité Escolar de Convivencia, Consejo Académico, Consejo Directivo) no utilizan cuentas compartidas: sus integrantes actúan con su cuenta personal nominativa (`RN-SEG-01`). En cuentas con múltiples roles vigentes se opera bajo un único rol activo por petición aplicando mínimo privilegio (`RN-SEG-04`). La revocación de rol, pérdida de vínculo o bloqueo de cuenta invalida de inmediato las sesiones activas en el servidor (`RN-SEG-06`). |
| **Postcondiciones** | Sesión autenticada con permisos mínimos del rol activo seleccionado y aceptación de política de tratamiento de datos trazada (cuando aplique), o sesión/token invalidado en servidor y cliente; el evento de seguridad queda trazado en auditoría (actor, fecha/hora, origen, rol activo, resultado e ID de correlación) sin registrar secretos ni contraseñas. |
| **Tipo** | Básico / Transversal |
| **Prioridad** | Alta |

### Flujo principal

| Paso | Actor / responsable | Acción e interacción del sistema |
| :--- | :--- | :--- |
| 1 | Usuario (candidato a autenticarse) | Ingresa su identificador y credenciales en el cliente de acceso, y solicita autenticarse. |
| 2 | Sistema | Valida formato de entrada, límite de intentos (*rate limiting*), estado de la cuenta y verifica criptográficamente las credenciales conforme a la política de autenticación; no revela si falló el usuario o la contraseña. |
| 3 | Sistema | Comprueba si la cuenta requiere activación de primer ingreso (cambio de contraseña temporal o aceptación de Política de Tratamiento de Datos Personales) y verifica que posea al menos un rol autorizado con vigencia activa (y vínculo validado cuando el rol sea Acudiente). |
| 4 | Sistema | Determina el rol activo de la sesión (automáticamente si posee un único rol vigente o mediante selección explícita si posee múltiples roles conforme a A2), crea una sesión/token con vencimiento, rol activo, alcance contextual mínimo y protección CSRF, y registra fecha, resultado y metadatos de seguridad en auditoría. |
| 5 | Usuario autenticado (según rol activo) | Accede al menú correspondiente a su rol activo y selecciona la función autorizada. |
| 6 | Sistema | En cada petición vuelve a comprobar en backend la validez de la sesión/token, la protección CSRF cuando aplique, los permisos del rol activo y el alcance contextual (vínculo acudiente-estudiante, matrícula, asignación docente y estado del periodo) sin confiar en parámetros enviados por el cliente. |
| 7 | Usuario autenticado / Sistema | Al seleccionar Cerrar sesión (o al cumplirse la expiración), el sistema invalida la sesión o refresh token en el servidor conforme a la arquitectura, limpia el estado en el cliente y registra el evento en auditoría. |

### Flujos alternos y de excepción

*Cada alternativa indica la condición de entrada, el tratamiento esperado y el resultado. Los flujos A# pueden retomar el principal cuando así se indique; los casos E# deben terminar de forma controlada, sin persistir cambios parciales.*

| ID | Condición / paso de origen | Tratamiento / secuencia alterna | Resultado / estado final |
| :--- | :--- | :--- | :--- |
| **A1** | Primer inicio de sesión, contraseña temporal o aceptación pendiente de Política de Tratamiento de Datos (paso 3) | El sistema restringe la sesión al flujo de habilitación inicial, exige el cambio obligatorio de la contraseña temporal (aplicando las reglas de `UC-PROV-03`) y presenta el aviso de privacidad y Política de Tratamiento de Datos Personales (Ley 1581 de 2012) para aceptación explícita. Una vez completados, persiste la evidencia auditable (versión de la política, fecha/hora, actor), cambia el estado de la cuenta a `ACTIVA` y retoma el paso 4. | Cuenta activada con contraseña propia, consentimiento registrado y sesión iniciada. |
| **A2** | Usuario con múltiples roles vigentes al autenticarse o cambio de rol activo durante la sesión (pasos 4 y 5) | El sistema presenta la lista de roles vigentes asociados a la persona (por ejemplo, Docente de área, Director de grupo y Acudiente). El actor selecciona el rol con el que desea operar; el sistema actualiza el token/sesión al rol activo elegido sin acumular privilegios de otros roles y registra la conmutación de contexto en auditoría. | Sesión operando exclusivamente con los permisos y alcance contextual del rol activo seleccionado. |
| **A3** | Ingreso de estudiante egresado o retirado, o su acudiente, con cuenta en estado histórico restringido (paso 3) | El sistema identifica que la cuenta está en estado `INACTIVA_HISTORICA` sin matrícula vigente en el año lectivo actual; concede una sesión limitada únicamente a la consulta y descarga de certificados, constancias e historial autorizado, bloqueando operaciones académicas del año lectivo en curso. | Sesión autenticada en modo de consulta histórica restringida. |
| **A4** | Cierre explícito de sesión solicitado por el usuario (pasos 5 y 7) | El usuario selecciona Cerrar sesión; el sistema revoca la sesión o refresh token en el servidor, ordena eliminar la cookie `HttpOnly` y el estado en memoria de la SPA, y registra el evento de cierre exitoso. | Sesión invalidada en servidor y cliente de forma segura. |
| **E1** | Credenciales incorrectas o identificador inexistente (paso 2) [antes A1] | El actor vuelve a ingresar las credenciales; el sistema ejecuta la comprobación en tiempo constante, responde con mensaje genérico sin revelar si falló el usuario o la contraseña, incrementa el contador antiabuso y no crea sesión. | Permanece sin autenticar; se registra intento fallido en auditoría. |
| **E2** | Cuenta deshabilitada, bloqueada o inactiva sin acceso histórico (paso 2) [antes A2] | El sistema rechaza la autenticación, informa el canal de recuperación o atención autorizado en Secretaría Académica y no expone datos internos de la cuenta. | Sin sesión; se registra evento de seguridad. |
| **E3** | Rol sin vigencia, sin asociación institucional o acudiente sin vínculos activos (paso 3) [antes A3] | El sistema detecta que la cuenta no tiene ningún rol vigente para el año lectivo o que sus vínculos como acudiente fueron revocados; no concede permisos, muestra instrucciones para solicitar habilitación y remite al flujo administrativo correspondiente (`UC-PROV-02`). | Sin acceso a módulos protegidos; evento trazado en auditoría. |
| **E4** | Sesión expirada, token inválido o sesión revocada forzosamente durante una petición (paso 6) [antes A4] | El sistema detecta expiración o revocación forzosa en servidor (por bloqueo de cuenta, cambio de contraseña o revocación de rol/vínculo) y devuelve respuesta HTTP 401 de autenticación requerida; el cliente elimina el estado local sin bucles de redirección y solicita nuevo inicio de sesión. | Operación no autorizada no se ejecuta; sesión local limpiada. |
| **E5** | Demasiados intentos fallidos o proveedor de identidad no disponible (paso 2) [antes A5] | Se aplica limitación temporal configurada (bloqueo antiabuso por umbral de intentos) o se informa indisponibilidad controlada; en ningún caso se degrada a acceso anónimo. | Acceso denegado de forma segura; alerta registrada en auditoría. |
| **E6** | Petición hacia un recurso fuera del alcance contextual del rol activo o fallo de validación CSRF (paso 6) | El usuario autenticado intenta acceder o modificar datos de un estudiante no vinculado, un grupo/asignatura no asignado, una función ajena a su rol activo, o envía una solicitud de modificación sin token CSRF válido; el sistema devuelve HTTP 403 Forbidden sin destruir la sesión válida y registra el intento en auditoría. | Operación rechazada por autorización contextual o CSRF; la sesión continúa activa para operaciones válidas. |

---

## UC-PROV-01 · Administrar cuentas de usuario y aprovisionamiento de credenciales

| Campo | Detalle |
| :--- | :--- |
| **ID** | UC-PROV-01 (Identificador provisional — Brecha BRC-ORIG-01A) |
| **Nombre** | Administrar cuentas de usuario y aprovisionamiento de credenciales |
| **Actores principales** | Administrador, Secretaría (para cuentas de Estudiantes y Acudientes dentro de su competencia administrativa), Rector (supervisión y alta de personal directivo/administrativo) |
| **Actores secundarios / sistemas** | Servicio de seguridad y hashing de contraseñas; servicio de control de sesiones; servicio de auditoría; servicio de notificaciones / generador de comprobante presencial |
| **Descripción** | Permite crear, aprovisionar credenciales iniciales temporales, consultar, bloquear temporalmente, reactivar, pasar a modo histórico restringido o inactivar cuentas de usuario del sistema, vinculándolas de forma unívoca a una persona previamente registrada en la institución. |
| **Propósito / valor** | Asegurar que el acceso al sistema escolar se otorgue exclusivamente a personas identificadas y legitimadas por la institución, protegiendo los datos de menores de edad y manteniendo trazabilidad completa sobre el ciclo de vida de cada cuenta. |
| **Disparador** | El actor autorizado solicita crear o modificar el estado de la cuenta de una persona registrada, o el proceso de matrícula / alta de personal invoca el aprovisionamiento. |
| **Precondiciones** | El actor tiene sesión activa con rol Administrador, Secretaría o Rector según el alcance de la operación. La persona titular existe en el registro institucional; si se trata de un Acudiente, su identidad y su vínculo familiar o de representación legal con al menos un estudiante deben estar previamente registrados y validados. |
| **Invariantes y reglas de negocio** | Toda cuenta debe estar asociada a una persona física única e identificable; se prohíbe crear cuentas genéricas o compartidas para cargos u órganos colegiados (`RN-SEG-01`). No se permite aprovisionar cuentas para adultos sin vínculo validado con un estudiante ni para estudiantes de niveles educativos cuyo acceso directo no esté habilitado por la institución (`RN-SEG-02`, `RN-SEG-08`). La contraseña inicial es siempre temporal, se almacena como hash BCrypt/Argon2 y deja la cuenta en estado `PENDIENTE_ACTIVACION` exigiendo cambio obligatorio en el primer inicio de sesión (`RN-SEG-03`). Ninguna cuenta con registros académicos o eventos en auditoría puede eliminarse físicamente; solo procede su inactivación o paso a estado `INACTIVA_HISTORICA`. El bloqueo o inactivación invalida de inmediato todas las sesiones activas del usuario (`RN-SEG-06`). |
| **Postcondiciones** | Cuenta creada en estado `PENDIENTE_ACTIVACION` (o actualizada en su estado operativo), hash de credencial temporal persistido sin exposición en texto plano, medio de entrega generado y evento registrado en auditoría. |
| **Tipo** | Primario / Administración de Seguridad |
| **Prioridad** | Alta |

### Flujo principal

| Paso | Actor / responsable | Acción e interacción del sistema |
| :--- | :--- | :--- |
| 1 | Actor autorizado (Administrador / Secretaría / Rector) | Selecciona a la persona registrada en el sistema (personal institucional, acudiente validado o estudiante) y solicita crear o gestionar su cuenta de usuario. |
| 2 | Sistema | Comprueba la competencia del actor según el tipo de titular, verifica que la persona no posea ya una cuenta duplicada y valida el cumplimiento de prerrequisitos (vínculo vigente para acudiente; nivel educativo habilitado para estudiante; estado activo para personal institucional). |
| 3 | Actor autorizado | Confirma el identificador único de acceso (documento normalizado o correo verificado según política institucional), verifica el canal de contacto para notificaciones de seguridad y confirma el aprovisionamiento. |
| 4 | Sistema | Genera una contraseña inicial temporal aleatoria de un solo uso, calcula su hash criptográfico (BCrypt/Argon2) y crea la cuenta en estado `PENDIENTE_ACTIVACION` con marca de cambio obligatorio de contraseña y aceptación pendiente de política de datos personales. |
| 5 | Sistema | Asocia el rol inicial correspondiente conforme a las reglas de `UC-PROV-02` cuando el aprovisionamiento provenga del flujo de matrícula o vinculación laboral. |
| 6 | Sistema | Envía las instrucciones de activación y credencial temporal al canal verificado del titular, o genera el comprobante impreso/acta de entrega presencial cuando el trámite se realice en ventanilla de Secretaría. |
| 7 | Sistema | Registra la operación en auditoría (actor autorizador, persona titular, identificador de cuenta, estado resultante, fecha/hora e ID de correlación) sin incluir contraseñas ni tokens en los logs. |

### Flujos alternos y de excepción

*Cada alternativa indica la condición de entrada, el tratamiento esperado y el resultado. Los flujos A# pueden retomar el principal cuando así se indique; los casos E# deben terminar de forma controlada, sin persistir cambios parciales.*

| ID | Condición / paso de origen | Tratamiento / secuencia alterna | Resultado / estado final |
| :--- | :--- | :--- | :--- |
| **A1** | Aprovisionamiento originado desde la formalización de matrícula o alta de personal (paso 1) | Al finalizar el registro de matrícula del estudiante y la validación de su acudiente (Módulos 02 y 04) o el alta de un docente (Módulo 05), el sistema verifica si la persona ya cuenta con usuario; si no lo tiene, inicia el paso 2 para aprovisionar su cuenta. | Cuenta aprovisionada en estado `PENDIENTE_ACTIVACION` sin duplicar cuentas de acudientes con varios hijos matriculados. |
| **A2** | Suspensión, bloqueo administrativo, inactivación o reactivación de cuenta existente (paso 1) | El actor selecciona una cuenta existente, solicita cambiar su estado (`BLOQUEADA`, `INACTIVA` o `ACTIVA`) y registra la justificación obligatoria. Si el nuevo estado es `BLOQUEADA` o `INACTIVA`, el sistema revoca de inmediato todas las sesiones y refresh tokens activos de la cuenta. | Estado de cuenta actualizado; sesiones activas invalidadas inmediatamente en servidor. |
| **A3** | Paso de cuenta a estado histórico restringido por graduación o retiro definitivo (paso 1) | Cuando un estudiante se gradúa o se retira definitivamente de la institución, el sistema actualiza la cuenta del estudiante y la de su acudiente (si no tiene otros estudiantes activos vinculados) al estado `INACTIVA_HISTORICA`, preservando el acceso exclusivo para consulta de certificados e historial (`RN-SEG-07`). | Cuenta en estado `INACTIVA_HISTORICA` con permisos acotados a consulta histórica. |
| **E1** | La persona ya posee una cuenta registrada o el identificador de acceso ya existe (paso 2) | El sistema detecta conflicto de unicidad, rechaza la creación de una segunda cuenta (HTTP 409) y dirige al actor a consultar o reutilizar la cuenta existente de la persona. | No se crean cuentas duplicadas; se preserva la unicidad de identidad. |
| **E2** | Intento de crear cuenta para acudiente sin vínculo validado o estudiante de nivel no habilitado (paso 2) | El sistema comprueba que el adulto no tiene vínculo familiar/legal activo o que el grado del estudiante tiene deshabilitado el acceso directo según configuración institucional; rechaza la creación e informa la regla de negocio incumplida. | Operación rechazada en cumplimiento de `RN-SEG-02` y `RN-SEG-08`. |
| **E3** | Secretaría intenta crear o modificar cuentas de roles directivos, docentes o de administración (paso 2) | El sistema valida el alcance del rol Secretaría, rechaza la operación con HTTP 403 por exceder su competencia funcional y registra el intento en auditoría. | Operación bloqueada por control de autorización vertical. |
| **E4** | Intento de eliminación física de una cuenta con historial (paso 1) | El sistema rechaza el borrado físico de cualquier cuenta que posea trazabilidad académica, administrativa o de seguridad, indicando que solo procede la inactivación o el paso a estado histórico. | Cuenta conservada; integridad referencial y de auditoría protegida. |

---

## UC-PROV-02 · Administrar asignación, vigencia y revocación de roles y contextos

| Campo | Detalle |
| :--- | :--- |
| **ID** | UC-PROV-02 (Identificador provisional — Brecha BRC-ORIG-01B) |
| **Nombre** | Administrar asignación, vigencia y revocación de roles y contextos |
| **Actores principales** | Administrador, Rector, Secretaría (únicamente para asignación/actualización del rol de Acudiente o Estudiante derivada de matrícula y validación documental) |
| **Actores secundarios / sistemas** | Servicio de autorización contextual y control de sesiones; servicio de auditoría |
| **Descripción** | Permite asignar roles institucionales a una cuenta de usuario, definir su vigencia temporal (por año lectivo o periodo de encargo), registrar la pertenencia nominativa a órganos colegiados escolares y revocar roles cuando finaliza la función o representación. |
| **Propósito / valor** | Garantizar la correcta correspondencia entre las funciones reales del personal, estudiantes y acudientes en el colegio y sus permisos en el sistema, evitando privilegios perpetuos o cuentas compartidas. |
| **Disparador** | El actor autorizado solicita asignar un rol, modificar su vigencia, registrar una membresía en órgano colegiado o revocar un rol de una cuenta existente. |
| **Precondiciones** | Existe una cuenta de usuario válida asociada a la persona (`UC-PROV-01`). El actor autenticado cuenta con competencia jerárquica y funcional para otorgar o revocar el rol específico. |
| **Invariantes y reglas de negocio** | Una misma persona puede poseer múltiples roles vigentes simultáneamente (por ejemplo, Docente de área, Director de grupo y Acudiente), los cuales se ejercen seleccionando un rol activo en sesión sin mezclar privilegios incompatibles (`RN-SEG-04`). Todo rol sujeto a periodo lectivo o encargo temporal debe registrar año lectivo y/o fechas de inicio y fin de vigencia. Los órganos colegiados (Comisión de Evaluación y Promoción, Comité Escolar de Convivencia, Consejo Académico, Consejo Directivo) no se representan como cuentas de usuario compartidas, sino como membresías contextuales asociadas a las cuentas personales nominativas de sus integrantes respaldadas por acta institucional (`RN-SEG-01`). La revocación de un rol invalida inmediatamente las sesiones activas que operen bajo dicho rol (`RN-SEG-06`). |
| **Postcondiciones** | Asignación, modificación de vigencia o revocación de rol/membresía persistida transaccionalmente; sesiones activas afectadas invalidadas en caso de revocación; operación registrada en auditoría con valor anterior, valor nuevo y motivo. |
| **Tipo** | Primario / Gobierno de Seguridad |
| **Prioridad** | Alta |

### Flujo principal

| Paso | Actor / responsable | Acción e interacción del sistema |
| :--- | :--- | :--- |
| 1 | Actor autorizado (Administrador / Rector / Secretaría según competencia) | Selecciona la cuenta de usuario objetivo, revisa sus roles vigentes e históricos y solicita asignar un rol, modificar su vigencia o revocarlo. |
| 2 | Sistema | Verifica en backend que el actor tenga competencia jerárquica sobre el rol solicitado y valida que no existan conflictos de segregación de funciones con los roles ya asignados a la cuenta. |
| 3 | Actor autorizado | Ingresa los datos de vigencia del rol (año lectivo, fechas de inicio y fin cuando se trate de un encargo temporal, referencia al acto o soporte documental y motivo) y confirma la operación. |
| 4 | Sistema | Comprueba que se cumplan las precondiciones de dominio del rol (registro laboral activo para personal escolar o vínculo familiar validado para Acudiente) y guarda los cambios de forma transaccional. |
| 5 | Sistema | Si se ejecutó una revocación o vencimiento de rol, invalida de inmediato las sesiones o tokens activos del usuario que estuvieran operando bajo el rol revocado. |
| 6 | Sistema | Registra el evento en auditoría detallando actor autorizador, cuenta afectada, rol, vigencia anterior y nueva, justificación, fecha/hora e ID de correlación. |

### Flujos alternos y de excepción

*Cada alternativa indica la condición de entrada, el tratamiento esperado y el resultado. Los flujos A# pueden retomar el principal cuando así se indique; los casos E# deben terminar de forma controlada, sin persistir cambios parciales.*

| ID | Condición / paso de origen | Tratamiento / secuencia alterna | Resultado / estado final |
| :--- | :--- | :--- | :--- |
| **A1** | Asignación de múltiples roles compatibles a una misma persona (paso 2) | El usuario ya posee un rol vigente (por ejemplo, Docente de área o Secretario) y se le asigna un rol adicional compatible (por ejemplo, Acudiente de su hijo matriculado o Director de grupo); el sistema mantiene ambos roles con vigencias independientes bajo la misma cuenta única. | Usuario habilitado con múltiples roles vigentes seleccionables en `UC-01` (A2). |
| **A2** | Registro de integrante en órgano colegiado institucional (paso 1) | El Rector o Administrador registra la designación de la persona como miembro de una Comisión de Evaluación y Promoción, Comité Escolar de Convivencia, Consejo Académico o Consejo Directivo para el año lectivo, indicando el acta o resolución de conformación; el sistema habilita los permisos contextuales del órgano sobre la cuenta personal del integrante. | Participación en órgano colegiado habilitada nominativamente sin crear cuentas genéricas. |
| **A3** | Vencimiento automático de rol al finalizar encargo temporal o año lectivo (paso 4) | Al cumplirse la fecha de finalización del encargo (por ejemplo, reemplazo por licencia o dirección de grupo anual), el sistema cambia el estado de la asignación del rol a `VENCIDO` e impide nuevas operaciones bajo ese contexto. | Permisos contextuales cesados automáticamente al vencer la vigencia. |
| **E1** | Conflicto de segregación de funciones o incompatibilidad de roles (paso 2) | El sistema detecta que el rol solicitado es mutuamente excluyente con un rol activo de la misma persona (por ejemplo, Estudiante activo con Docente, Coordinador, Secretaría o Rector), rechaza la solicitud con HTTP 409 e informa la incompatibilidad. | Asignación rechazada; se protege la integridad y segregación de funciones. |
| **E2** | Intento de asignar o revocar un rol fuera de la competencia del actor (paso 2) | Un usuario con rol Secretaría intenta otorgar o revocar roles de Administrador, Rector, Coordinador o Docente; el sistema deniega la operación con HTTP 403 y registra alerta en auditoría. | Operación bloqueada por control de autorización jerárquica. |
| **E3** | Intento de revocar el último rol activo de administración general del sistema (paso 2) | El sistema verifica que la cuenta objetivo es la única con rol Administrador/Rector activo en la institución; impide la revocación hasta que exista otro usuario habilitado con dicho rol. | Revocación rechazada para prevenir bloqueo operativo de la institución. |

---

## UC-PROV-03 · Cambiar contraseña y recuperar acceso a la cuenta

| Campo | Detalle |
| :--- | :--- |
| **ID** | UC-PROV-03 (Identificador provisional — Brecha BRC-ORIG-01C) |
| **Nombre** | Cambiar contraseña y recuperar acceso a la cuenta |
| **Actores principales** | Usuario autenticado (cambio voluntario o obligatorio en primer ingreso), Usuario sin sesión activa (recuperación por autoservicio), Secretaría / Administrador (restablecimiento presencial asistido) |
| **Actores secundarios / sistemas** | Servicio de seguridad y hashing de contraseñas; servicio de control de sesiones; servicio de notificaciones; servicio de auditoría |
| **Descripción** | Permite a cualquier usuario cambiar su contraseña (voluntariamente o durante la activación obligatoria de primer acceso) y recuperar el acceso a su cuenta ante olvido de credenciales o bloqueo temporal, mediante enlace/código temporal de un solo uso enviado a su canal verificado o mediante restablecimiento presencial asistido en Secretaría Académica. |
| **Propósito / valor** | Brindar un mecanismo seguro, auditable y adaptado a la realidad operativa de los colegios colombianos para restablecer el acceso legítimo sin exponer contraseñas ni permitir suplantación de identidad. |
| **Disparador** | El actor selecciona Cambiar contraseña, Olvidé mi contraseña, ingresa por primera vez con credencial temporal (`UC-01` A1) o solicita restablecimiento presencial en Secretaría. |
| **Precondiciones** | La cuenta existe en el sistema. Para cambio voluntario, el usuario posee sesión activa; para recuperación por autoservicio, la cuenta tiene un canal de contacto verificado; para restablecimiento presencial, el solicitante acredita su identidad física y vínculo vigente ante el funcionario autorizado. |
| **Invariantes y reglas de negocio** | Las contraseñas nunca se leen ni se envían en texto plano; toda recuperación genera un token criptográfico de un solo uso con vencimiento corto o una credencial temporal que obliga al cambio inmediato en el próximo inicio de sesión (`RN-SEG-03`). El flujo de recuperación pública nunca revela si un identificador o correo existe en el sistema (prevención de enumeración de cuentas). La nueva contraseña debe cumplir la política de seguridad y no reutilizar la contraseña actual o temporal. Todo cambio o restablecimiento de contraseña revoca de inmediato las sesiones y refresh tokens activos en otros dispositivos (`RN-SEG-06`). |
| **Postcondiciones** | Nuevo hash criptográfico almacenado, token de recuperación invalidado, sesiones previas cerradas, cuenta actualizada a estado `ACTIVA` (si estaba en `PENDIENTE_ACTIVACION`), aviso de seguridad enviado al titular y evento registrado en auditoría sin registrar contraseñas. |
| **Tipo** | Primario / Seguridad Operativa |
| **Prioridad** | Alta |

### Flujo principal

| Paso | Actor / responsable | Acción e interacción del sistema |
| :--- | :--- | :--- |
| 1 | Usuario | Selecciona la opción Olvidé mi contraseña e ingresa su identificador de acceso o correo electrónico registrado. |
| 2 | Sistema | Procesa la petición en tiempo constante y responde siempre con un mensaje uniforme indicando que, si los datos corresponden a una cuenta habilitada con canal verificado, se enviarán las instrucciones de recuperación. |
| 3 | Sistema | Si la cuenta existe, está activa y posee canal verificado, invalida tokens de recuperación previos, genera un token criptográfico de un solo uso con expiración acotada y lo envía al canal registrado del titular. |
| 4 | Usuario | Abre el enlace o ingresa el código de verificación dentro del tiempo de vigencia y suministra la nueva contraseña junto con su confirmación. |
| 5 | Sistema | Verifica la autenticidad y vigencia del token de un solo uso, y comprueba que la nueva contraseña cumpla la política de complejidad y sea distinta de la anterior. |
| 6 | Sistema | Calcula el hash seguro (BCrypt/Argon2), actualiza la contraseña, marca el token como utilizado, restablece el contador de intentos fallidos e invalida todas las sesiones y refresh tokens activos de la cuenta. |
| 7 | Sistema | Envía notificación de seguridad al titular informando el cambio de contraseña y registra el evento en auditoría sin incluir secretos. |

### Flujos alternos y de excepción

*Cada alternativa indica la condición de entrada, el tratamiento esperado y el resultado. Los flujos A# pueden retomar el principal cuando así se indique; los casos E# deben terminar de forma controlada, sin persistir cambios parciales.*

| ID | Condición / paso de origen | Tratamiento / secuencia alterna | Resultado / estado final |
| :--- | :--- | :--- | :--- |
| **A1** | Cambio voluntario de contraseña en sesión activa o cambio obligatorio en primer ingreso (paso 1) | El usuario autenticado ingresa su contraseña actual (o temporal en primer acceso) y la nueva contraseña; el sistema valida la credencial actual, ejecuta las comprobaciones de los pasos 5 a 7, cambia el estado de `PENDIENTE_ACTIVACION` a `ACTIVA` cuando corresponda y revoca las demás sesiones activas. | Contraseña actualizada, cuenta activada y sesiones en otros dispositivos revocadas. |
| **A2** | Restablecimiento presencial asistido en Secretaría Académica o Administración (paso 1) | Cuando un acudiente o estudiante no dispone de correo accesible o requiere atención presencial, acude a Secretaría; el funcionario verifica físicamente su documento de identidad y la vigencia de su vínculo en el sistema, genera una credencial temporal de restablecimiento, entrega el comprobante presencial y el sistema marca la cuenta en estado `PENDIENTE_ACTIVACION` revocando sesiones previas. | Credencial temporal entregada presencialmente con obligación de cambio en el próximo inicio de sesión (`UC-01` A1); funcionario y motivo quedan auditados. |
| **E1** | Identificador inexistente o cuenta sin canal de contacto verificado en autoservicio (paso 2) | El sistema no envía comunicación externa pero presenta exactamente la misma respuesta neutra del paso 2 al cliente y registra internamente el intento fallido. | No se revela si la cuenta existe; sin cambios sobre cuentas del sistema. |
| **E2** | Token de recuperación expirado, inválido o ya utilizado (paso 5) | El sistema rechaza la actualización de contraseña, informa que el código o enlace de recuperación venció o es inválido y solicita iniciar nuevamente el proceso. | Contraseña sin cambios; token inválido rechazado. |
| **E3** | La nueva contraseña incumple la política de seguridad o repite la contraseña anterior/temporal (paso 5) | El sistema rechaza la contraseña ingresada detallando las reglas de formato o longitud requeridas y permite reintentar mientras el token o sesión siga vigente. | Se solicita una nueva contraseña conforme a la política. |
| **E4** | Cuenta bloqueada por medida administrativa o pérdida de representación legal (paso 3 o A2) | El sistema detecta que la cuenta posee un bloqueo administrativo o que el adulto perdió la autorización/vínculo legal sobre el estudiante; impide el restablecimiento tanto por autoservicio como por ventanilla de Secretaría y genera alerta de seguridad. | Recuperación denegada; se mantiene el bloqueo de seguridad. |