import { Routes } from '@angular/router';
import { authGuard } from './auth/guards/auth.guard';
import { LoginComponent } from './auth/pages/login/login.component';
import { DepartamentoCreatePageComponent } from './departamentos/pages/departamento-create-page.component';
import { DepartamentoEditPageComponent } from './departamentos/pages/departamento-edit-page.component';
import { DepartamentosListPageComponent } from './departamentos/pages/departamentos-list-page.component';
import { EmpleadoCreatePageComponent } from './empleados/pages/empleado-create-page.component';
import { EmpleadoEditPageComponent } from './empleados/pages/empleado-edit-page.component';
import { EmpleadosListPageComponent } from './empleados/pages/empleados-list-page.component';

export const appRoutes: Routes = [
	{ path: 'login', component: LoginComponent },
	{ path: 'empleados', component: EmpleadosListPageComponent, canActivate: [authGuard] },
	{ path: 'empleados/nuevo', component: EmpleadoCreatePageComponent, canActivate: [authGuard] },
	{ path: 'empleados/:clave/editar', component: EmpleadoEditPageComponent, canActivate: [authGuard] },
	{ path: 'departamentos', component: DepartamentosListPageComponent, canActivate: [authGuard] },
	{ path: 'departamentos/nuevo', component: DepartamentoCreatePageComponent, canActivate: [authGuard] },
	{ path: 'departamentos/:id/editar', component: DepartamentoEditPageComponent, canActivate: [authGuard] },
	{ path: '', pathMatch: 'full', redirectTo: 'login' },
	{ path: '**', redirectTo: 'login' }
];
