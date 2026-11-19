package com.jackren.sistemaacademico.service;

import com.jackren.sistemaacademico.dto.*;
import com.jackren.sistemaacademico.entity.*;
import com.jackren.sistemaacademico.exception.ConflictoException;
import com.jackren.sistemaacademico.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Servicio de registro atómico del expediente personal del estudiante, acudiente y consentimiento (UC-12, RN-EST-01, RN-EST-05).
 */
@Service
@Transactional
public class ExpedienteEstudianteService {

    private final EstudianteRepository estudianteRepository;
    private final AcudienteRepository acudienteRepository;
    private final EstudianteAcudienteRepository estudianteAcudienteRepository;
    private final PoliticaTratamientoDatosRepository politicaRepository;
    private final AutorizacionTratamientoDatosMenorRepository autorizacionMenorRepository;
    private final AuditoriaGlobalService auditoriaService;

    public ExpedienteEstudianteService(EstudianteRepository estudianteRepository,
                                       AcudienteRepository acudienteRepository,
                                       EstudianteAcudienteRepository estudianteAcudienteRepository,
                                       PoliticaTratamientoDatosRepository politicaRepository,
                                       AutorizacionTratamientoDatosMenorRepository autorizacionMenorRepository,
                                       AuditoriaGlobalService auditoriaService) {
        this.estudianteRepository = estudianteRepository;
        this.acudienteRepository = acudienteRepository;
        this.estudianteAcudienteRepository = estudianteAcudienteRepository;
        this.politicaRepository = politicaRepository;
        this.autorizacionMenorRepository = autorizacionMenorRepository;
        this.auditoriaService = auditoriaService;
    }

    public ExpedienteCompletoResponse registrarExpedienteCompleto(ExpedienteCompletoRequest request, String autorizador, String ip, String userAgent) {
        // 1. Verificar o crear Estudiante
        Optional<Estudiante> estudianteExistente = estudianteRepository.findByDocumento(request.getDocumento());
        Estudiante estudiante = estudianteExistente.orElseGet(Estudiante::new);
        estudiante.setNombres(request.getNombres());
        estudiante.setApellidos(request.getApellidos());
        estudiante.setTipoDocumento(request.getTipoDocumento());
        estudiante.setDocumento(request.getDocumento());
        estudiante.setFechaNacimiento(request.getFechaNacimiento());
        estudiante.setCorreo(request.getCorreo());
        estudiante.setTelefono(request.getTelefono());
        estudiante.setDireccion(request.getDireccion());
        estudiante.setSexo(request.getSexo());
        estudiante.setEps(request.getEps());
        estudiante.setGrupoSanguineoRh(request.getGrupoSanguineoRh());
        estudiante.setTieneDiscapacidadPiar(request.getTieneDiscapacidadPiar());
        estudiante.setDetallesSaludEmergencia(request.getDetallesSaludEmergencia());
        estudiante.setActivo(true);
        estudiante.setEstadoExpediente(EstadoExpediente.LISTO_PARA_MATRICULA);

        Estudiante estudianteGuardado = estudianteRepository.save(estudiante);

        // 2. Verificar o crear Acudiente Principal
        Optional<Acudiente> acudienteExistente = acudienteRepository.findByDocumento(request.getAcudienteDocumento());
        Acudiente acudiente = acudienteExistente.orElseGet(Acudiente::new);
        acudiente.setNombres(request.getAcudienteNombres());
        acudiente.setApellidos(request.getAcudienteApellidos());
        acudiente.setTipoDocumento(request.getAcudienteTipoDocumento());
        acudiente.setDocumento(request.getAcudienteDocumento());
        acudiente.setTelefono(request.getAcudienteTelefono());
        acudiente.setCorreo(request.getAcudienteCorreo());
        acudiente.setDireccion(request.getAcudienteDireccion() != null ? request.getAcudienteDireccion() : request.getDireccion());
        acudiente.setOcupacion(request.getAcudienteOcupacion() != null ? request.getAcudienteOcupacion() : "No especificada");
        acudiente.setActivo(true);

        Acudiente acudienteGuardado = acudienteRepository.save(acudiente);

        // 3. Crear o actualizar vinculación (exactamente un principal RN-EST-05, E3)
        Optional<EstudianteAcudiente> vinculoExistente = estudianteAcudienteRepository.findByEstudianteAndAcudiente(estudianteGuardado, acudienteGuardado);
        EstudianteAcudiente vinculo = vinculoExistente.orElseGet(EstudianteAcudiente::new);
        vinculo.setEstudiante(estudianteGuardado);
        vinculo.setAcudiente(acudienteGuardado);
        vinculo.setParentesco(request.getParentesco());
        vinculo.setEsPrincipal(true); // Obligatoriamente principal
        vinculo.setConviveConEstudiante(Boolean.TRUE.equals(request.getConviveConEstudiante()));
        vinculo.setAutorizadoRetiro(Boolean.TRUE.equals(request.getAutorizadoRetiro()));
        vinculo.setTieneCustodia(Boolean.TRUE.equals(request.getTieneCustodia()));
        vinculo.setRepresentacionLegal(Boolean.TRUE.equals(request.getRepresentacionLegal()));
        vinculo.setPrioridadEmergencia(1);
        vinculo.setEstadoVinculo(EstadoVinculo.VERIFICADO);

        // Desmarcar principal previo si existiera
        estudianteAcudienteRepository.findByEstudianteAndEsPrincipalTrue(estudianteGuardado).ifPresent(p -> {
            if (!p.getAcudiente().getId().equals(acudienteGuardado.getId())) {
                p.setEsPrincipal(false);
                estudianteAcudienteRepository.save(p);
            }
        });

        estudianteAcudienteRepository.save(vinculo);

        // 4. Registrar Autorización de Tratamiento de Datos del Menor (Ley 1581 / Dec 1377, RN-EST-01)
        boolean autorizacionRegistrada = false;
        Optional<PoliticaTratamientoDatos> politicaVigente = politicaRepository.findFirstByEsVigenteTrueOrderByNumeroVersionDesc();
        if (politicaVigente.isPresent()) {
            PoliticaTratamientoDatos pol = politicaVigente.get();
            Optional<AutorizacionTratamientoDatosMenor> autExistente = autorizacionMenorRepository.findByEstudianteIdAndPoliticaId(estudianteGuardado.getId(), pol.getId());
            AutorizacionTratamientoDatosMenor aut = autExistente.orElseGet(AutorizacionTratamientoDatosMenor::new);
            aut.setEstudiante(estudianteGuardado);
            aut.setAcudiente(acudienteGuardado);
            aut.setPolitica(pol);
            aut.setAutorizaFinalidadesEducativasObligatorias(true);
            aut.setAutorizaUsoImagenFotografiaOpcional(Boolean.TRUE.equals(request.getAutorizaUsoImagenFotografiaOpcional()));
            aut.setDireccionIp(ip != null ? ip : "127.0.0.1");
            aut.setUserAgent(userAgent != null ? userAgent : "Cliente Web");
            autorizacionMenorRepository.save(aut);
            autorizacionRegistrada = true;
        }

        // 5. Trazabilidad en Auditoría Global (RN-10)
        auditoriaService.registrarEvento(new RegistroAuditoriaEventoRequest(
                autorizador != null ? autorizador : "SECRETARIA",
                "SECRETARIA",
                "ESTUDIANTES",
                "REGISTRO_EXPEDIENTE_COMPLETO",
                "Estudiante",
                String.valueOf(estudianteGuardado.getId()),
                "Registro atómico del expediente del estudiante " + estudianteGuardado.getDocumento() + " y su acudiente principal " + acudienteGuardado.getDocumento(),
                ResultadoAuditoria.EXITO,
                "EXP-COMP-" + estudianteGuardado.getId()
        ));

        ExpedienteCompletoResponse resp = new ExpedienteCompletoResponse();
        resp.setEstudianteId(estudianteGuardado.getId());
        resp.setEstudianteDocumento(estudianteGuardado.getDocumento());
        resp.setEstudianteTipoDocumento(estudianteGuardado.getTipoDocumento());
        resp.setEstudianteNombreCompleto(estudianteGuardado.getNombres() + " " + estudianteGuardado.getApellidos());
        resp.setEstadoExpediente(estudianteGuardado.getEstadoExpediente());
        resp.setAcudientePrincipalId(acudienteGuardado.getId());
        resp.setAcudientePrincipalDocumento(acudienteGuardado.getDocumento());
        resp.setAcudientePrincipalNombreCompleto(acudienteGuardado.getNombres() + " " + acudienteGuardado.getApellidos());
        resp.setAutorizacionDatosMenorRegistrada(autorizacionRegistrada);
        resp.setMensaje("Expediente escolar registrado y validado atómicamente en estado LISTO_PARA_MATRICULA (UC-12).");

        return resp;
    }
}
