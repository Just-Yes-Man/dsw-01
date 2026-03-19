import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { NgIf } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { EmpleadoFormMode, EmpleadoFormValue, DEFAULT_FORM_VALUE } from '../models/empleado-form-state.model';

@Component({
  selector: 'app-empleado-form',
  standalone: true,
  imports: [ReactiveFormsModule, NgIf],
  templateUrl: './empleado-form.component.html'
})
export class EmpleadoFormComponent implements OnInit, OnChanges {
  @Input() mode: EmpleadoFormMode = 'create';
  @Input() initialValue: EmpleadoFormValue = DEFAULT_FORM_VALUE;
  @Input() submitting = false;
  @Input() backendError: string | null = null;

  @Output() save = new EventEmitter<EmpleadoFormValue>();
  @Output() cancel = new EventEmitter<void>();

  form!: FormGroup;

  constructor(private readonly fb: FormBuilder) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      nombre: [this.initialValue.nombre, [Validators.required, Validators.maxLength(100)]],
      direccion: [this.initialValue.direccion, [Validators.required, Validators.maxLength(100)]],
      telefono: [this.initialValue.telefono, [Validators.required, Validators.maxLength(100)]],
      email: [this.initialValue.email, [Validators.required, Validators.email, Validators.maxLength(255)]],
      password: [this.initialValue.password, [Validators.required, Validators.maxLength(255)]],
      estadoAcceso: [this.initialValue.estadoAcceso, [Validators.required]],
      departamentoId: [this.initialValue.departamentoId, [Validators.required, Validators.min(1)]]
    });
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (!this.form || !changes['initialValue']) {
      return;
    }

    this.form.patchValue({
      nombre: this.initialValue.nombre,
      direccion: this.initialValue.direccion,
      telefono: this.initialValue.telefono,
      email: this.initialValue.email,
      password: this.initialValue.password,
      estadoAcceso: this.initialValue.estadoAcceso,
      departamentoId: this.initialValue.departamentoId
    });
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.save.emit(this.form.getRawValue() as EmpleadoFormValue);
  }
}
