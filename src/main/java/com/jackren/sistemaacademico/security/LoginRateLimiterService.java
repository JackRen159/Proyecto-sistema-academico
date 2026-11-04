package com.jackren.sistemaacademico.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Servicio de limitacion de tasa (Rate Limiting) en memoria para el endpoint de login.
 * Protege contra ataques de fuerza bruta restringiendo a un maximo de 5 intentos por minuto por direccion IP.
 */
@Service
public class LoginRateLimiterService {

    private static final int MAX_INTENTOS_POR_MINUTO = 5;
    private static final long VENTANA_SEGUNDOS = 60;

    private static class IntentoIp {
        private final AtomicInteger contador = new AtomicInteger(0);
        private volatile Instant inicioVentana = Instant.now();

        boolean intentar(Instant ahora) {
            synchronized (this) {
                if (ahora.isAfter(inicioVentana.plusSeconds(VENTANA_SEGUNDOS))) {
                    inicioVentana = ahora;
                    contador.set(1);
                    return true;
                }
                if (contador.get() < MAX_INTENTOS_POR_MINUTO) {
                    contador.incrementAndGet();
                    return true;
                }
                return false;
            }
        }

        boolean esObsoleto(Instant ahora) {
            return ahora.isAfter(inicioVentana.plusSeconds(VENTANA_SEGUNDOS * 2));
        }
    }

    private final ConcurrentHashMap<String, IntentoIp> intentosPorIp = new ConcurrentHashMap<>();

    /**
     * Intenta registrar una solicitud de login para la direccion IP dada.
     *
     * @param ip Direccion IP del cliente.
     * @return true si la solicitud esta dentro del limite permitido; false si ha superado el maximo de 5 intentos.
     */
    public boolean intentar(String ip) {
        if (ip == null || ip.isBlank()) {
            return true;
        }
        limpiarObsoletosSiEsNecesario();
        Instant ahora = Instant.now();
        IntentoIp registro = intentosPorIp.computeIfAbsent(ip, k -> new IntentoIp());
        return registro.intentar(ahora);
    }

    /**
     * Extrae la direccion IP del cliente priorizando la cabecera X-Forwarded-For.
     *
     * @param request Peticion HTTP.
     * @return Primera direccion IP presente en X-Forwarded-For o getRemoteAddr().
     */
    public String extraerIpCliente(HttpServletRequest request) {
        if (request == null) {
            return "unknown";
        }
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }
        String remoteAddr = request.getRemoteAddr();
        return (remoteAddr != null && !remoteAddr.isBlank()) ? remoteAddr : "unknown";
    }

    /**
     * Reinicia el conteo para una IP (util para pruebas unitarias).
     *
     * @param ip Direccion IP a reiniciar.
     */
    public void reiniciar(String ip) {
        if (ip != null) {
            intentosPorIp.remove(ip);
        }
    }

    /**
     * Limpia todos los registros (util para pruebas unitarias).
     */
    public void limpiarTodo() {
        intentosPorIp.clear();
    }

    private void limpiarObsoletosSiEsNecesario() {
        if (intentosPorIp.size() > 500) {
            Instant ahora = Instant.now();
            intentosPorIp.entrySet().removeIf(entry -> entry.getValue().esObsoleto(ahora));
        }
    }
}

