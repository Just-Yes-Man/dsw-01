import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { EmpleadoFormComponent } from '../components/empleado-form.component';
import { DEFAULT_FORM_VALUE, EmpleadoFormValue } from '../models/empleado-form-state.model';
import { EmpleadosApiService } from '../services/empleados-api.service';
import { EmpleadosErrorMapperService } from '../services/empleados-error-mapper.service';
import { EmpleadosStoreService } from '../services/empleados-store.service';

@Component({
  selector: 'app-empleado-edit-page',
  standalone: true,
  imports: [EmpleadoFormComponent],
  template: `
    <app-empleado-form
      mode="edit"
      [initialValue]="initialValue"
      [submitting]="submitting"
      [backendError]="backendError"
      (cancel)="goBack()"
      (save)="save($event)"
    />
  `
})
export class EmpleadoEditPageComponent implements OnInit {
  submitting = false;
  backendError: string | null = null;
  clave = '';
  initialValue: EmpleadoFormValue = DEFAULT_FORM_VALUE;

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly api: EmpleadosApiService,
    private readonly store: EmpleadosStoreService,
    private readonly errorMapper: EmpleadosErrorMapperService
  ) {}

  ngOnInit(): void {
    this.clave = this.route.snapshot.paramMap.get('clave') ?? '';
    if (!this.clave) {
      this.goBack();
      return;
    }

    this.api.findByClave(this.clave).subscribe({
      next: (empleado) => {
        this.initialValue = {
          nombre: empleado.nombre,
          direccion: empleado.direccion,
          telefono: empleado.telefono,
          email: empleado.email,
          password: empleado.password ?? '',
          estadoAcceso: empleado.estadoAcceso,
          departamentoId: empleado.departamento.id
        };
      },
      error: () => this.goBack()
    });
  }

  save(value: EmpleadoFormValue): void {
    this.submitting = true;
    this.backendError = null;
    this.store.update(this.clave, value).subscribe({
      next: () => this.router.navigateByUrl('/empleados'),
      error: (error) => {
        this.backendError = this.errorMapper.toUserMessage(error);
        this.submitting = false;
      }
    });
  }

  goBack(): void {
    this.router.navigateByUrl('/empleados');
  }
}
