package com.jackren.sistemaacademico.service;

import com.jackren.sistemaacademico.dto.AutorizacionSalidaAnticipadaRequest;
import com.jackren.sistemaacademico.dto.AutorizacionSalidaAnticipadaResponse;
import com.jackren.sistemaacademico.entity.*;
import com.jackren.sistemaacademico.exception.RecursoNoEncontradoException;
import com.jackren.sistemaacademico.mapper.AsistenciaMapper;
import com.jackren.sistemaacademico.repository.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class SalidaAnticipadaService {

    private final AutorizacionSalidaAnticipadaRepository salidaRepository;
    private final RegistroAsistenciaRepository registroRepository;
    private final MatriculaRepository matriculaRepository;
    private final EstudianteAcudienteRepository estudianteAcudienteRepository;
    private final AcudienteRepository acudienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final AsistenciaMapper asistenciaMapper;

    public SalidaAnticipadaService(AutorizacionSalidaAnticipadaRepository salidaRepository,
                                  RegistroAsistenciaRepository registroRepository,
                                  MatriculaRepository matriculaRepository,
                                  EstudianteAcudienteRepository estudianteAcudienteRepository,
                                  AcudienteRepository acudienteRepository,
                                  UsuarioRepository usuarioRepository,
                                  AsistenciaMapper asistenciaMapper) {
        this.salidaRepository = salidaRepository;
        this.registroRepository = registroRepository;
        this.matriculaRepository = matriculaRepository;
        this.estudianteAcudienteRepository = estudianteAcudienteRepository;
        this.acudienteRepository = acudienteRepository;
        this.usuarioRepository = usuarioRepository;
        this.asistenciaMapper = asistenciaMapper;
    }

    public AutorizacionSalidaAnticipadaResponse registrarSalidaAnticipada(AutorizacionSalidaAnticipadaRequest request, Long usuarioPorteriaId) {
        Matricula matricula = matriculaRepository.findById(request.getMatriculaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Matrícula no encontrada con ID " + request.getMatriculaId()));

        LocalDate fecha = request.getFecha() != null ? request.getFecha() : LocalDate.now();

        // UC-PROV-18 A1 / E1: Comprobar que el adulto que retira esté autorizado y NO tenga restricción legal
        List<EstudianteAcudiente> vinculos = estudianteAcudienteRepository.findByEstudiante(matricula.getEstudiante());

        boolean tieneRestriccion = vinculos.stream().anyMatch(v ->
                v.getAcudiente().getDocumento().equals(request.getAdultoRetiraDocumento()) &&
                        v.getEstadoVinculo() == EstadoVinculo.BLOQUEADO_RESTRICCION_LEGAL
        );

        long count = salidaRepository.count() + 1;
        String numeroAutorizacion = String.format("SAL-%d-%04d", fecha.getYear(), count);
        Usuario portero = usuarioPorteriaId != null ? usuarioRepository.findById(usuarioPorteriaId).orElse(null) : null;

        if (tieneRestriccion) {
            AutorizacionSalidaAnticipada salidaDenegada = new AutorizacionSalidaAnticipada();
            salidaDenegada.setNumeroAutorizacion(numeroAutorizacion);
            salidaDenegada.setMatricula(matricula);
            salidaDenegada.setFecha(fecha);
            salidaDenegada.setHoraSalida(request.getHoraSalida());
            salidaDenegada.setMotivo(request.getMotivo());
            salidaDenegada.setAdultoRetiraNombre(request.getAdultoRetiraNombre());
            salidaDenegada.setAdultoRetiraDocumento(request.getAdultoRetiraDocumento());
            salidaDenegada.setParentescoAutorizado(request.getParentescoAutorizado());
            salidaDenegada.setUsuarioPorteriaValidador(portero);
            salidaDenegada.setEstado(EstadoAutorizacionSalida.DENEGADA_RESTRICCION_LEGAL);
            salidaDenegada.setObservaciones("Intento de retiro físico bloqueado por medida de protección legal (UC-PROV-18 E1)");
            salidaRepository.save(salidaDenegada);

            throw new AccessDeniedException("Acceso denegado en portería: el adulto registra medida de protección BLOQUEADO_RESTRICCION_LEGAL (Ley 1098, UC-PROV-18 E1)");
        }

        AutorizacionSalidaAnticipada salida = new AutorizacionSalidaAnticipada();
        salida.setNumeroAutorizacion(numeroAutorizacion);
        salida.setMatricula(matricula);
        salida.setFecha(fecha);
        salida.setHoraSalida(request.getHoraSalida());
        salida.setMotivo(request.getMotivo());
        salida.setAdultoRetiraNombre(request.getAdultoRetiraNombre());
        salida.setAdultoRetiraDocumento(request.getAdultoRetiraDocumento());
        salida.setParentescoAutorizado(request.getParentescoAutorizado());
        salida.setUsuarioPorteriaValidador(portero);
        salida.setEstado(EstadoAutorizacionSalida.AUTORIZADA_VERIFICADA);
        salida.setObservaciones(request.getObservaciones());

        AutorizacionSalidaAnticipada guardada = salidaRepository.save(salida);

        // Actualizar sesiones restantes del día a SALIDA_ANTICIPADA_AUTORIZADA
        List<RegistroAsistencia> registrosDelDia = registroRepository
                .findByMatriculaIdAndSesionAsistenciaFecha(matricula.getId(), fecha);

        for (RegistroAsistencia r : registrosDelDia) {
            if (r.getEstado() == EstadoAsistencia.AUSENTE_INJUSTIFICADA ||
                    r.getEstado() == EstadoAsistencia.EVASION_INTERNA_CLASE) {
                r.setEstado(EstadoAsistencia.SALIDA_ANTICIPADA_AUTORIZADA);
                registroRepository.save(r);
            }
        }

        return asistenciaMapper.toResponse(guardada);
    }

    @Transactional(readOnly = true)
    public List<AutorizacionSalidaAnticipadaResponse> listarSalidasMatricula(Long matriculaId) {
        return salidaRepository.findByMatriculaIdOrderByFechaDesc(matriculaId).stream()
                .map(asistenciaMapper::toResponse)
                .toList();
    }
}
