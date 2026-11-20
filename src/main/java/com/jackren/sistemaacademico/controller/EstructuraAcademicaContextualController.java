package com.jackren.sistemaacademico.controller;

import com.jackren.sistemaacademico.dto.DiaNoLectivoResponse;
import com.jackren.sistemaacademico.dto.GradoAsignaturaResponse;
import com.jackren.sistemaacademico.dto.PeriodoAcademicoResponse;
import com.jackren.sistemaacademico.service.EstructuraAcademicaContextualService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/estructura")
public class EstructuraAcademicaContextualController {

    private final EstructuraAcademicaContextualService contextualService;

    public EstructuraAcademicaContextualController(EstructuraAcademicaContextualService contextualService) {
        this.contextualService = contextualService;
    }

    @GetMapping("/periodos-vigentes")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'SECRETARIA', 'PROFESOR', 'ESTUDIANTE', 'ACUDIENTE')")
    public ResponseEntity<List<PeriodoAcademicoResponse>> consultarPeriodosVigentes() {
        return ResponseEntity.ok(contextualService.consultarPeriodosVigentes());
    }

    @GetMapping("/calendario-vigente")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECTOR', 'COORDINADOR', 'SECRETARIA', 'PROFESOR', 'ESTUDIANTE', 'ACUDIENTE')")
    public ResponseEntity<List<DiaNoLectivoResponse>> consultarCalendarioVigente() {
        return ResponseEntity.ok(contextualService.consultarCalendarioVigente());
    }

    @GetMapping("/mi-plan-estudios")
    @PreAuthorize("hasAnyRole('ESTUDIANTE', 'ACUDIENTE', 'PROFESOR', 'COORDINADOR', 'RECTOR', 'ADMINISTRADOR')")
    public ResponseEntity<List<GradoAsignaturaResponse>> consultarMiPlanEstudios(Authentication authentication) {
        String username = authentication != null ? authentication.getName() : "";
        return ResponseEntity.ok(contextualService.consultarMiPlanEstudios(username));
    }
}
