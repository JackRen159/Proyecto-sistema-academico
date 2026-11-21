package com.jackren.sistemaacademico.service;

import com.jackren.sistemaacademico.dto.*;
import com.jackren.sistemaacademico.entity.*;
import com.jackren.sistemaacademico.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;

/**
 * Servicio centralizado para la bitácora transversal inmutable de auditoría (Módulo 12 Base).
 * Implementa encadenamiento criptográfico SHA-256 de eventos (append-only), verificación
 * de integridad y exportación regulada conforme a UC-27, UC-PROV-29, RN-10, RN-DOC-06 y RN-DOC-07.
 */
@Service
@Transactional
public class AuditoriaGlobalService {

    public static final String HASH_GENESIS = "0000000000000000000000000000000000000000000000000000000000000000";
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final AuditoriaEventoRepository auditoriaRepository;
    private final UsuarioRepository usuarioRepository;

    @org.springframework.beans.factory.annotation.Autowired(required = false)
    private AlertaSeguridadAuditoriaRepository alertaRepository;

    public AuditoriaGlobalService(AuditoriaEventoRepository auditoriaRepository,
                                  UsuarioRepository usuarioRepository) {
        this.auditoriaRepository = auditoriaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Sobrecarga conveniente para registrar eventos desde servicios del backend
     * con generación automática de correlationId y defaults del módulo.
     */
    public synchronized AuditoriaEventoResponse registrarEvento(String accion, String entidad, String entidadId,
                                                                 String valorAnterior, String valorNuevo,
                                                                 String motivo, String resultadoStr) {
        RegistroAuditoriaEventoRequest req = new RegistroAuditoriaEventoRequest();
        req.setUsername("SISTEMA");
        req.setRolActivo("SISTEMA");
        req.setModulo("ESTRUCTURA_ACADEMICA");
        req.setAccion(accion);
        req.setEntidad(entidad);
        req.setEntidadId(entidadId);
        req.setValorAnterior(valorAnterior);
        req.setValorNuevo(valorNuevo);
        req.setMotivo(motivo != null ? motivo : accion);
        req.setResultado("EXITOSO".equalsIgnoreCase(resultadoStr) || "EXITO".equalsIgnoreCase(resultadoStr)
                ? ResultadoAuditoria.EXITO : ResultadoAuditoria.FALLO);
        req.setCorrelationId(java.util.UUID.randomUUID().toString());
        return registrarEvento(req);
    }

    /**
     * Registra un nuevo evento de auditoría en la bitácora transversal calculando
     * de forma atómica su hash encadenado SHA-256 respecto al evento inmediatamente anterior.
     */
    public synchronized AuditoriaEventoResponse registrarEvento(RegistroAuditoriaEventoRequest request) {
        Usuario usuario = null;
        if (request.getUsuarioId() != null) {
            usuario = usuarioRepository.findById(request.getUsuarioId()).orElse(null);
        }

        // Obtener el hash del evento anterior en la cadena ininterrumpida
        Optional<AuditoriaEvento> ultimoEventoOpt = auditoriaRepository.findTopByOrderByIdDesc();
        String hashAnterior = ultimoEventoOpt.map(AuditoriaEvento::getHashEvento).orElse(HASH_GENESIS);

        LocalDateTime ahora = LocalDateTime.now();
        String fechaIso = ahora.format(ISO_FORMATTER);

        // Sanitización preventiva: nunca almacenar secretos ni tokens en texto plano (RN-DOC-06)
        String valorAnteriorSanitizado = sanitizarValoresSensibles(request.getValorAnterior());
        String valorNuevoSanitizado = sanitizarValoresSensibles(request.getValorNuevo());

        String hashEvento = calcularHashCanónico(
                request.getUsername(),
                request.getRolActivo(),
                request.getModulo(),
                request.getAccion(),
                request.getEntidad(),
                request.getEntidadId(),
                request.getMotivo(),
                request.getResultado().name(),
                request.getCorrelationId(),
                fechaIso,
                hashAnterior
        );

        AuditoriaEvento evento = new AuditoriaEvento(
                usuario,
                request.getUsername(),
                request.getRolActivo(),
                request.getModulo(),
                request.getAccion(),
                request.getEntidad(),
                request.getEntidadId(),
                valorAnteriorSanitizado,
                valorNuevoSanitizado,
                request.getMotivo(),
                request.getResultado(),
                request.getCorrelationId(),
                request.getDireccionIp(),
                request.getUserAgent(),
                hashAnterior,
                hashEvento
        );
        evento.setFechaEvento(ahora);

        AuditoriaEvento guardado = auditoriaRepository.save(evento);
        return toResponse(guardado);
    }

    /**
     * Ejecuta la verificación criptográfica de integridad sobre la cadena de auditoría (UC-PROV-29).
     * Recalcula el SHA-256 de cada evento y comprueba la continuidad de hashes anteriores.
     */
    @Transactional(readOnly = true)
    public VerificacionIntegridadResponse verificarIntegridadCadena() {
        List<AuditoriaEvento> eventos = auditoriaRepository.findAllByOrderByIdAsc();
        if (eventos.isEmpty()) {
            return VerificacionIntegridadResponse.integra(0);
        }

        String hashEsperadoAnterior = HASH_GENESIS;
        for (int i = 0; i < eventos.size(); i++) {
            AuditoriaEvento ev = eventos.get(i);

            // 1. Verificar enlace con el evento anterior
            if (!hashEsperadoAnterior.equalsIgnoreCase(ev.getHashAnterior())) {
                return VerificacionIntegridadResponse.discrepancia(
                        i,
                        ev.getId(),
                        String.format("Discrepancia en enlace de hashes en evento ID %d: hashAnterior grabado [%s] no coincide con el hash previo esperado [%s]. Posible supresion o insercion fisica.",
                                ev.getId(), ev.getHashAnterior(), hashEsperadoAnterior)
                );
            }

            // 2. Recalcular el hash del evento canónico
            String fechaIso = ev.getFechaEvento().format(ISO_FORMATTER);
            String hashCalculado = calcularHashCanónico(
                    ev.getUsername(),
                    ev.getRolActivo(),
                    ev.getModulo(),
                    ev.getAccion(),
                    ev.getEntidad(),
                    ev.getEntidadId(),
                    ev.getMotivo(),
                    ev.getResultado().name(),
                    ev.getCorrelationId(),
                    fechaIso,
                    ev.getHashAnterior()
            );

            if (!hashCalculado.equalsIgnoreCase(ev.getHashEvento())) {
                registrarAlertaSeguridad(
                        TipoAlertaSeguridad.DISCREPANCIA_CADENA_HASH,
                        SeveridadAlertaSeguridad.CRITICA,
                        "Discrepancia criptográfica en evento ID " + ev.getId(),
                        ev.getUsuario() != null ? ev.getUsuario().getId() : null,
                        ev.getDireccionIp(),
                        ev.getCorrelationId()
                );
                return VerificacionIntegridadResponse.discrepancia(
                        i,
                        ev.getId(),
                        String.format("Discrepancia en contenido del evento ID %d: hashEvento grabado [%s] difiere del hash recalculado [%s]. Evidencia de modificacion fisica directa en la fila.",
                                ev.getId(), ev.getHashEvento(), hashCalculado)
                );
            }

            hashEsperadoAnterior = ev.getHashEvento();
        }

        return VerificacionIntegridadResponse.integra(eventos.size());
    }

    /**
     * Genera un paquete de exportación forense/regulatoria firmado con manifiesto SHA-256 (UC-PROV-29 paso 5-6).
     * Registra la propia exportación en la bitácora de auditoría.
     */
    public PaqueteAuditoriaExportadoResponse exportarPaqueteAuditoria(ExportacionAuditoriaRequest request, String autorizadorUsername) {
        List<AuditoriaEvento> eventos = auditoriaRepository.findAllByOrderByIdAsc();
        List<AuditoriaEventoResponse> respuestas = eventos.stream().map(this::toResponse).toList();

        // Calcular manifiesto SHA-256 del paquete completo
        StringBuilder contenidoManifiesto = new StringBuilder();
        for (AuditoriaEvento ev : eventos) {
            contenidoManifiesto.append(ev.getId())
                    .append(":")
                    .append(ev.getHashEvento())
                    .append(";");
        }
        String manifiestoSha256 = calcularSha256(contenidoManifiesto.toString());

        // Auditar el evento de exportación en la misma bitácora
        registrarEvento(new RegistroAuditoriaEventoRequest(
                autorizadorUsername != null ? autorizadorUsername : "SISTEMA",
                "RECTOR",
                "AUDITORIA",
                "EXPORTAR_PAQUETE_AUDITORIA",
                "AuditoriaEvento",
                String.valueOf(eventos.size()),
                "Exportación autorizada según oficio: " + request.getNumeroOficioRequerimiento() + " para " + request.getAutoridadSolicitante(),
                ResultadoAuditoria.EXITO,
                "EXP-" + System.currentTimeMillis()
        ));

        return new PaqueteAuditoriaExportadoResponse(
                request.getNumeroOficioRequerimiento(),
                request.getAutoridadSolicitante(),
                request.getMotivo(),
                eventos.size(),
                manifiestoSha256,
                respuestas
        );
    }

    @Transactional(readOnly = true)
    public List<AuditoriaEventoResponse> listarPorCorrelationId(String correlationId) {
        return auditoriaRepository.findByCorrelationIdOrderByFechaEventoAsc(correlationId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AuditoriaEventoResponse> listarPorEntidad(String entidad, String entidadId) {
        return auditoriaRepository.findByEntidadAndEntidadIdOrderByFechaEventoAsc(entidad, entidadId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<AuditoriaEventoResponse> listarEventosPaginados(String modulo, LocalDateTime desde, LocalDateTime hasta, Pageable pageable) {
        LocalDateTime fInicio = desde != null ? desde : LocalDateTime.now().minusYears(1);
        LocalDateTime fFin = hasta != null ? hasta : LocalDateTime.now();

        if (modulo != null && !modulo.isBlank()) {
            return auditoriaRepository.findByModuloAndFechaEventoBetween(modulo, fInicio, fFin, pageable)
                    .map(this::toResponse);
        }
        return auditoriaRepository.findAll(pageable).map(this::toResponse);
    }

    public static String calcularHashCanónico(String username, String rolActivo, String modulo, String accion,
                                              String entidad, String entidadId, String motivo, String resultado,
                                              String correlationId, String fechaIso, String hashAnterior) {
        String canonico = String.format("%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s",
                val(username),
                val(rolActivo),
                val(modulo),
                val(accion),
                val(entidad),
                val(entidadId),
                val(motivo),
                val(resultado),
                val(correlationId),
                val(fechaIso),
                val(hashAnterior)
        );
        return calcularSha256(canonico);
    }

    public static String calcularSha256(String texto) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(texto.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Algoritmo SHA-256 no disponible en la JVM", e);
        }
    }

    public synchronized void registrarAlertaSeguridad(TipoAlertaSeguridad tipo, SeveridadAlertaSeguridad severidad,
                                                      String descripcion, Long usuarioId, String ip, String correlationId) {
        if (alertaRepository != null) {
            AlertaSeguridadAuditoria alerta = new AlertaSeguridadAuditoria();
            alerta.setTipoAlerta(tipo);
            alerta.setSeveridad(severidad != null ? severidad : SeveridadAlertaSeguridad.ALTA);
            alerta.setDescripcionEvento(descripcion);
            if (usuarioId != null && usuarioRepository != null) {
                alerta.setUsuarioInvolucrado(usuarioRepository.findById(usuarioId).orElse(null));
            }
            alerta.setIpOrigen(ip);
            alerta.setCorrelationId(correlationId);
            alerta.setLeidaPorRector(false);
            alertaRepository.save(alerta);
        }
    }

    public List<AlertaSeguridadResponse> listarAlertasPendientesRector() {
        if (alertaRepository == null) return Collections.emptyList();
        return alertaRepository.findByLeidaPorRectorFalseOrderByFechaAlertaDesc().stream()
                .map(a -> new AlertaSeguridadResponse(
                        a.getId(),
                        a.getTipoAlerta(),
                        a.getSeveridad(),
                        a.getDescripcionEvento(),
                        a.getUsuarioInvolucrado() != null ? a.getUsuarioInvolucrado().getId() : null,
                        a.getUsuarioInvolucrado() != null ? a.getUsuarioInvolucrado().getUsername() : null,
                        a.getIpOrigen(),
                        a.getCorrelationId(),
                        a.getLeidaPorRector(),
                        a.getFechaAlerta()
                ))
                .toList();
    }

    public void marcarAlertaLeida(Long alertaId, Long rectorUsuarioId) {
        if (alertaRepository != null) {
            alertaRepository.findById(alertaId).ifPresent(a -> {
                a.setLeidaPorRector(true);
                alertaRepository.save(a);
            });
        }
    }

    private static String val(String s) {
        return s != null ? s : "";
    }

    private static String sanitizarValoresSensibles(String valor) {
        if (valor == null) {
            return null;
        }
        // Enmascarar contraseñas o tokens si aparecieran en valores
        return valor.replaceAll("(?i)(\"password\"\\s*:\\s*\")[^\"]+(\")", "$1[REDACTED]$2")
                    .replaceAll("(?i)(\"token\"\\s*:\\s*\")[^\"]+(\")", "$1[REDACTED]$2")
                    .replaceAll("(?i)(\"secret\"\\s*:\\s*\")[^\"]+(\")", "$1[REDACTED]$2");
    }

    public AuditoriaEventoResponse toResponse(AuditoriaEvento e) {
        AuditoriaEventoResponse r = new AuditoriaEventoResponse();
        r.setId(e.getId());
        r.setUsuarioId(e.getUsuario() != null ? e.getUsuario().getId() : null);
        r.setUsername(e.getUsername());
        r.setRolActivo(e.getRolActivo());
        r.setModulo(e.getModulo());
        r.setAccion(e.getAccion());
        r.setEntidad(e.getEntidad());
        r.setEntidadId(e.getEntidadId());
        r.setValorAnterior(e.getValorAnterior());
        r.setValorNuevo(e.getValorNuevo());
        r.setMotivo(e.getMotivo());
        r.setResultado(e.getResultado());
        r.setCorrelationId(e.getCorrelationId());
        r.setDireccionIp(e.getDireccionIp());
        r.setUserAgent(e.getUserAgent());
        r.setFechaEvento(e.getFechaEvento());
        r.setHashAnterior(e.getHashAnterior());
        r.setHashEvento(e.getHashEvento());
        return r;
    }
}
