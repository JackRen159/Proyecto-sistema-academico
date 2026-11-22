import { Routes } from '@angular/router';
import { LoginComponent } from './features/auth/login.component';
import { RecuperarPasswordComponent } from './features/auth/recuperar-password.component';
import { PrimerIngresoComponent } from './features/auth/primer-ingreso.component';
import { EstudiantesListComponent } from './features/estudiantes/estudiantes-list.component';
import { EstudianteFormComponent } from './features/estudiantes/estudiante-form.component';
import { EstudianteDetalleComponent } from './features/estudiantes/estudiante-detalle.component';
import { AppLayoutComponent } from './core/layout/app-layout.component';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  {
    path: 'login',
    component: LoginComponent
  },
  {
    path: 'recuperar-password',
    component: RecuperarPasswordComponent
  },
  {
    path: 'primer-ingreso',
    component: PrimerIngresoComponent,
    canActivate: [authGuard]
  },
  {
    path: '',
    component: AppLayoutComponent,
    canActivate: [authGuard],
    children: [
      {
        path: 'estudiantes',
        component: EstudiantesListComponent
      },
      {
        path: 'estudiantes/nuevo',
        component: EstudianteFormComponent
      },
      {
        path: 'estudiantes/:id',
        component: EstudianteDetalleComponent
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


