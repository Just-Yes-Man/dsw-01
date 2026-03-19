import { Component } from '@angular/core';
import { finalize } from 'rxjs';
import { Router } from '@angular/router';
import { DepartamentoFormComponent } from '../components/departamento-form.component';
import { DepartamentoFormValue } from '../models/departamento-form-state.model';
import { DepartamentosErrorMapperService } from '../services/departamentos-error-mapper.service';
import { DepartamentosStoreService } from '../services/departamentos-store.service';

@Component({
  selector: 'app-departamento-create-page',
  standalone: true,
  imports: [DepartamentoFormComponent],
  template: `
    <app-departamento-form
      mode="create"
      [submitting]="submitting"
      [backendError]="backendError"
      (cancel)="goBack()"
      (save)="save($event)"
    />
  `
})
export class DepartamentoCreatePageComponent {
  submitting = false;
  backendError: string | null = null;

  constructor(
    private readonly store: DepartamentosStoreService,
    private readonly router: Router,
    private readonly errorMapper: DepartamentosErrorMapperService
  ) {}

  save(value: DepartamentoFormValue): void {
    if (this.submitting) {
      return;
    }

    this.submitting = true;
    this.backendError = null;
    this.store.create(value)
      .pipe(finalize(() => {
        this.submitting = false;
      }))
      .subscribe({
        next: () => this.router.navigateByUrl('/departamentos'),
        error: (error) => {
          this.backendError = this.errorMapper.toUserMessage(error);
        }
      });
  }

  goBack(): void {
    this.router.navigateByUrl('/departamentos');
  }
}
