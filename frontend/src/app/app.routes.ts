import { Routes } from '@angular/router';
import { LoginComponent } from './features/auth/login.component';
import { EstudiantesListComponent } from './features/estudiantes/estudiantes-list.component';
import { AppLayoutComponent } from './core/layout/app-layout.component';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  {
    path: 'login',
    component: LoginComponent
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

