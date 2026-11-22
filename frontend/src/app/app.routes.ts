import { Routes } from '@angular/router';
import { AppLayoutComponent } from './core/layout/app-layout.component';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  {
    path: 'login',
    loadComponent: () => import('./features/auth/login.component').then(m => m.LoginComponent)
  },
  {
    path: 'recuperar-password',
    loadComponent: () => import('./features/auth/recuperar-password.component').then(m => m.RecuperarPasswordComponent)
  },
  {
    path: 'primer-ingreso',
    loadComponent: () => import('./features/auth/primer-ingreso.component').then(m => m.PrimerIngresoComponent),
    canActivate: [authGuard]
  },
  {
    path: '',
    component: AppLayoutComponent,
    canActivate: [authGuard],
    children: [
      {
        path: 'estudiantes',
        loadComponent: () => import('./features/estudiantes/estudiantes-list.component').then(m => m.EstudiantesListComponent)
      },
      {
        path: 'estudiantes/nuevo',
        loadComponent: () => import('./features/estudiantes/estudiante-form.component').then(m => m.EstudianteFormComponent)
      },
      {
        path: 'estudiantes/:id',
        loadComponent: () => import('./features/estudiantes/estudiante-detalle.component').then(m => m.EstudianteDetalleComponent)
      },
      {
        path: 'estructura',
        loadComponent: () => import('./features/estructura/estructura-academica.component').then(m => m.EstructuraAcademicaComponent)
      },
      {
        path: 'configuracion',
        loadComponent: () => import('./features/configuracion/configuracion-institucional.component').then(m => m.ConfiguracionInstitucionalComponent)
      },
      {
        path: 'matriculas',
        loadComponent: () => import('./features/matriculas/matriculas.component').then(m => m.MatriculasComponent)
      },
      {
        path: 'profesores',
        loadComponent: () => import('./features/profesores/profesores.component').then(m => m.ProfesoresComponent)
      },
      {
        path: 'horarios',
        loadComponent: () => import('./features/horarios/horarios.component').then(m => m.HorariosComponent)
      },
      {
        path: 'asistencia',
        loadComponent: () => import('./features/asistencia/asistencia.component').then(m => m.AsistenciaComponent)
      },
      {
        path: 'calificaciones',
        loadComponent: () => import('./features/calificaciones/calificaciones.component').then(m => m.CalificacionesComponent)
      },
      {
        path: 'cierre-promocion',
        loadComponent: () => import('./features/cierre-promocion/cierre-promocion.component').then(m => m.CierrePromocionComponent)
      },
      {
        path: 'convivencia',
        loadComponent: () => import('./features/convivencia/convivencia.component').then(m => m.ConvivenciaComponent)
      },
      {
        path: 'documentos',
        loadComponent: () => import('./features/documentos/documentos.component').then(m => m.DocumentosComponent)
      },
      {
        path: 'auditoria',
        loadComponent: () => import('./features/auditoria/auditoria.component').then(m => m.AuditoriaComponent)
      },
      {
        path: '',
        redirectTo: 'estudiantes',
        pathMatch: 'full'
      }
    ]
  },
  {
    path: '**',
    redirectTo: '/estudiantes'
  }
];



