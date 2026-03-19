import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { EmpleadoFormComponent } from '../components/empleado-form.component';
import { EmpleadoFormValue } from '../models/empleado-form-state.model';
import { EmpleadosStoreService } from '../services/empleados-store.service';
import { EmpleadosErrorMapperService } from '../services/empleados-error-mapper.service';
import { finalize } from 'rxjs';

@Component({
  selector: 'app-empleado-create-page',
  standalone: true,
  imports: [EmpleadoFormComponent],
  template: `
    <app-empleado-form
      mode="create"
      [submitting]="submitting"
      [backendError]="backendError"
      (cancel)="goBack()"
      (save)="save($event)"
    />
  `
})
export class EmpleadoCreatePageComponent {
  submitting = false;
  backendError: string | null = null;

  constructor(
    private readonly store: EmpleadosStoreService,
    private readonly router: Router,
    private readonly errorMapper: EmpleadosErrorMapperService
  ) {}

  save(value: EmpleadoFormValue): void {
    this.submitting = true;
    this.backendError = null;
    this.store.create(value)
      .pipe(finalize(() => {
        this.submitting = false;
      }))
      .subscribe({
        next: () => this.router.navigateByUrl('/empleados'),
        error: (error) => {
          this.backendError = this.errorMapper.toUserMessage(error);
        }
      });
  }

  goBack(): void {
    this.router.navigateByUrl('/empleados');
  }
}
