import { Component } from '@angular/core';
import { EmpleadosListPageComponent } from './empleados-list-page.component';

@Component({
  selector: 'app-empleados-home',
  standalone: true,
  imports: [EmpleadosListPageComponent],
  template: '<app-empleados-list-page />'
})
export class EmpleadosHomeComponent {}
