package com.jackren.sistemaacademico.service;

import com.jackren.sistemaacademico.dto.*;
import com.jackren.sistemaacademico.entity.*;
import com.jackren.sistemaacademico.exception.RecursoNoEncontradoException;
import com.jackren.sistemaacademico.exception.ValidacionException;
import com.jackren.sistemaacademico.repository.InstitucionDatosLegalesRepository;
import com.jackren.sistemaacademico.repository.ParametroInstitucionalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Servicio de administración de identificación legal institucional y parámetros generales (UC-PROV-13, UC-PROV-14).
 */
@Service
@Transactional
public class ConfiguracionInstitucionalService {

    private final InstitucionDatosLegalesRepository institucionRepository;
    private final ParametroInstitucionalRepository parametroRepository;
    private final AuditoriaGlobalService auditoriaService;

    public ConfiguracionInstitucionalService(InstitucionDatosLegalesRepository institucionRepository,
                                            ParametroInstitucionalRepository parametroRepository,
                                            AuditoriaGlobalService auditoriaService) {
        this.institucionRepository = institucionRepository;
        this.parametroRepository = parametroRepository;
        this.auditoriaService = auditoriaService;
    }

    @Transactional(readOnly = true)
    public InstitucionDatosLegalesResponse obtenerDatosLegales() {
        InstitucionDatosLegales inst = institucionRepository.findFirstByActivoTrue()
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontraron datos legales institucionales configurados"));
        return toInstitucionResponse(inst);
    }

    public InstitucionDatosLegalesResponse actualizarDatosLegales(ActualizarInstitucionRequest request, String autorizador) {
        if (request.getCodigoDane() == null || request.getCodigoDane().length() != 12) {
            throw new ValidacionException("El código DANE institucional debe tener exactamente 12 dígitos (RN-CFG-08)");
        }

        InstitucionDatosLegales inst = institucionRepository.findFirstByActivoTrue()
                .orElseGet(InstitucionDatosLegales::new);

        String valorAnterior = inst.getId() != null ? inst.getNombreOficial() + " | DANE: " + inst.getCodigoDane() : "NUEVA_CONFIGURACION";

        inst.setNombreOficial(request.getNombreOficial());
        inst.setCodigoDane(request.getCodigoDane());
        inst.setNit(request.getNit());
        inst.setResolucionAprobacion(request.getResolucionAprobacion());
        inst.setCaracter(request.getCaracter());
        inst.setCorreoInstitucional(request.getCorreoInstitucional());
        inst.setTelefono(request.getTelefono());
        inst.setDireccion(request.getDireccion());
        inst.setMunicipio(request.getMunicipio());
        inst.setDepartamento(request.getDepartamento());
        inst.setNombreRectorFirmante(request.getNombreRectorFirmante());
        inst.setCargoRectorFirmante(request.getCargoRectorFirmante() != null ? request.getCargoRectorFirmante() : "Rector(a)");
        inst.setDocumentoRectorFirmante(request.getDocumentoRectorFirmante());
        inst.setNombreSecretarioFirmante(request.getNombreSecretarioFirmante());
        inst.setCargoSecretarioFirmante(request.getCargoSecretarioFirmante() != null ? request.getCargoSecretarioFirmante() : "Secretario(a) Académico(a)");
        inst.setDocumentoSecretarioFirmante(request.getDocumentoSecretarioFirmante());
        inst.setLema(request.getLema());
        inst.setActivo(true);

        InstitucionDatosLegales guardado = institucionRepository.save(inst);

        auditoriaService.registrarEvento(new RegistroAuditoriaEventoRequest(
                autorizador != null ? autorizador : "SISTEMA",
                "RECTOR",
                "CONFIGURACION",
                "ACTUALIZAR_DATOS_INSTITUCION",
                "InstitucionDatosLegales",
                String.valueOf(guardado.getId()),
                "Actualización de encabezados y firmas autorizadas de Rectoría y Secretaría Académica",
                ResultadoAuditoria.EXITO,
                "CFG-INST-" + guardado.getId()
        ));

        return toInstitucionResponse(guardado);
    }

    @Transactional(readOnly = true)
    public List<ParametroInstitucionalResponse> listarParametros() {
        return parametroRepository.findAll().stream().map(this::toParametroResponse).toList();
    }

    @Transactional(readOnly = true)
    public ParametroInstitucionalResponse obtenerParametroPorClave(String clave) {
        ParametroInstitucional param = parametroRepository.findByClave(clave)
                .orElseThrow(() -> new RecursoNoEncontradoException("Parámetro no encontrado con clave: " + clave));
        return toParametroResponse(param);
    }

    public ParametroInstitucionalResponse actualizarParametro(String clave, ActualizarParametroRequest request, String autorizador) {
        ParametroInstitucional param = parametroRepository.findByClave(clave)
                .orElseThrow(() -> new RecursoNoEncontradoException("Parámetro no encontrado con clave: " + clave));

        if (!Boolean.TRUE.equals(param.getEditablePorUsuario())) {
            throw new ValidacionException("El parámetro " + clave + " es de solo lectura y no admite edición manual.");
        }

        // Validación de límites legales (E1)
        if ("PLAZO_ARCO_DIAS_HABILES".equalsIgnoreCase(clave)) {
            try {
                int dias = Integer.parseInt(request.getValor());
                if (dias <= 0 || dias > 15) {
                    throw new ValidacionException("El plazo para responder reclamos ARCO no puede superar los 15 días hábiles conforme al Art. 15 de la Ley 1581 de 2012 (E1)");
                }
            } catch (NumberFormatException e) {
                throw new ValidacionException("El valor para PLAZO_ARCO_DIAS_HABILES debe ser un número entero.");
            }
        }

        if (param.getLimiteMaximoNumerico() != null) {
            try {
                BigDecimal valorNum = new BigDecimal(request.getValor());
                if (valorNum.compareTo(param.getLimiteMaximoNumerico()) > 0) {
                    throw new ValidacionException("El valor ingresado supera el límite máximo permitido de " + param.getLimiteMaximoNumerico() + " (E1)");
                }
                if (param.getLimiteMinimoNumerico() != null && valorNum.compareTo(param.getLimiteMinimoNumerico()) < 0) {
                    throw new ValidacionException("El valor ingresado es inferior al límite mínimo permitido de " + param.getLimiteMinimoNumerico() + " (E1)");
                }
            } catch (NumberFormatException ignored) {
                // si no es numérico, no se valida contra límites numéricos
            }
        }

        String valorAnterior = param.getValor();
        param.setValor(request.getValor());
        ParametroInstitucional actualizado = parametroRepository.save(param);

        auditoriaService.registrarEvento(new RegistroAuditoriaEventoRequest(
                autorizador != null ? autorizador : "SISTEMA",
                "ADMINISTRADOR",
                "CONFIGURACION",
                "ACTUALIZAR_PARAMETRO",
                "ParametroInstitucional",
                clave,
                "Actualización de " + clave + " de [" + valorAnterior + "] a [" + request.getValor() + "]. Motivo: " + request.getMotivo(),
                ResultadoAuditoria.EXITO,
                "CFG-PARAM-" + clave
        ));

        return toParametroResponse(actualizado);
    }

    private InstitucionDatosLegalesResponse toInstitucionResponse(InstitucionDatosLegales i) {
        InstitucionDatosLegalesResponse r = new InstitucionDatosLegalesResponse();
        r.setId(i.getId());
        r.setNombreOficial(i.getNombreOficial());
        r.setCodigoDane(i.getCodigoDane());
        r.setNit(i.getNit());
        r.setResolucionAprobacion(i.getResolucionAprobacion());
        r.setCaracter(i.getCaracter());
        r.setCorreoInstitucional(i.getCorreoInstitucional());
        r.setTelefono(i.getTelefono());
        r.setDireccion(i.getDireccion());
        r.setMunicipio(i.getMunicipio());
        r.setDepartamento(i.getDepartamento());
        r.setNombreRectorFirmante(i.getNombreRectorFirmante());
        r.setCargoRectorFirmante(i.getCargoRectorFirmante());
        r.setDocumentoRectorFirmante(i.getDocumentoRectorFirmante());
        r.setNombreSecretarioFirmante(i.getNombreSecretarioFirmante());
        r.setCargoSecretarioFirmante(i.getCargoSecretarioFirmante());
        r.setDocumentoSecretarioFirmante(i.getDocumentoSecretarioFirmante());
        r.setLema(i.getLema());
        r.setActivo(i.getActivo());
        r.setFechaActualizacion(i.getFechaActualizacion());
        return r;
    }

    private ParametroInstitucionalResponse toParametroResponse(ParametroInstitucional p) {
        ParametroInstitucionalResponse r = new ParametroInstitucionalResponse();
        r.setId(p.getId());
        r.setClave(p.getClave());
        r.setValor(p.getValor());
        r.setTipoDato(p.getTipoDato());
        r.setDescripcion(p.getDescripcion());
        r.setCategoria(p.getCategoria());
        r.setLimiteMinimoNumerico(p.getLimiteMinimoNumerico());
        r.setLimiteMaximoNumerico(p.getLimiteMaximoNumerico());
        r.setEditablePorUsuario(p.getEditablePorUsuario());
        r.setFechaActualizacion(p.getFechaActualizacion());
        return r;
    }
}
