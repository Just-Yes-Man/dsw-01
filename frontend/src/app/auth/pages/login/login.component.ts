import { Component } from '@angular/core';
import { NgIf } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule, NgIf],
  templateUrl: './login.component.html'
})
export class LoginComponent {
  errorMessage = '';
  readonly form: FormGroup;

  constructor(
    private readonly formBuilder: FormBuilder,
    private readonly authService: AuthService,
    private readonly router: Router
  ) {
    this.form = this.formBuilder.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required]]
    });
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.errorMessage = 'Credenciales inválidas';
    const credentials = this.form.getRawValue() as { email: string; password: string };
    this.authService.login(credentials).subscribe({
      next: () => {
        this.errorMessage = '';
        this.router.navigateByUrl('/empleados');
      },
      error: (error: HttpErrorResponse) => {
        this.errorMessage = error.status === 401
          ? 'Credenciales inválidas'
          : 'No fue posible iniciar sesión. Intenta nuevamente.';
      }
    });
  }
}
