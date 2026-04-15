import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { finalize } from 'rxjs';
import { DepartamentoFormComponent } from '../components/departamento-form.component';
import {
  DEFAULT_DEPARTAMENTO_FORM_VALUE,
  DepartamentoFormValue
} from '../models/departamento-form-state.model';
import { DepartamentosApiService } from '../services/departamentos-api.service';
import { DepartamentosErrorMapperService } from '../services/departamentos-error-mapper.service';
import { DepartamentosStoreService } from '../services/departamentos-store.service';

@Component({
  selector: 'app-departamento-edit-page',
  standalone: true,
  imports: [DepartamentoFormComponent],
  template: `
    <app-departamento-form
      mode="edit"
      [initialValue]="initialValue"
      [submitting]="submitting"
      [backendError]="backendError"
      (cancel)="goBack()"
      (save)="save($event)"
    />
  `
})
export class DepartamentoEditPageComponent implements OnInit {
  submitting = false;
  backendError: string | null = null;
  id = 0;
  initialValue: DepartamentoFormValue = DEFAULT_DEPARTAMENTO_FORM_VALUE;

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly api: DepartamentosApiService,
    private readonly store: DepartamentosStoreService,
    private readonly errorMapper: DepartamentosErrorMapperService
  ) {}

  ngOnInit(): void {
    this.id = Number(this.route.snapshot.paramMap.get('id') ?? 0);
    if (!this.id) {
      this.goBack();
      return;
    }

    this.api.findById(this.id).subscribe({
      next: (departamento) => {
        this.initialValue = {
          nombre: departamento.nombre
        };
      },
      error: () => this.goBack()
    });
  }

  save(value: DepartamentoFormValue): void {
    if (this.submitting) {
      return;
    }

    this.submitting = true;
    this.backendError = null;

    this.store.update(this.id, value)
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
