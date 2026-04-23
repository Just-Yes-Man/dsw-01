import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { NgIf } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import {
  DEFAULT_DEPARTAMENTO_FORM_VALUE,
  DepartamentoFormMode,
  DepartamentoFormValue
} from '../models/departamento-form-state.model';

@Component({
  selector: 'app-departamento-form',
  standalone: true,
  imports: [ReactiveFormsModule, NgIf],
  templateUrl: './departamento-form.component.html'
})
export class DepartamentoFormComponent implements OnInit, OnChanges {
  @Input() mode: DepartamentoFormMode = 'create';
  @Input() initialValue: DepartamentoFormValue = DEFAULT_DEPARTAMENTO_FORM_VALUE;
  @Input() submitting = false;
  @Input() backendError: string | null = null;

  @Output() save = new EventEmitter<DepartamentoFormValue>();
  @Output() cancel = new EventEmitter<void>();

  form!: FormGroup;

  constructor(private readonly fb: FormBuilder) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      nombre: [this.initialValue.nombre, [Validators.required, Validators.maxLength(255)]]
    });
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (!this.form || !changes['initialValue']) {
      return;
    }

    this.form.patchValue({
      nombre: this.initialValue.nombre
    });
  }

  submit(): void {
    if (this.submitting) {
      return;
    }

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.save.emit(this.form.getRawValue() as DepartamentoFormValue);
  }
}
