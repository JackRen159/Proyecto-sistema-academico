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



