/// <reference types="jasmine" />

import { HttpErrorResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { AuthService } from '../../services/auth.service';
import { LoginComponent } from './login.component';

declare const expect: any;

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let routerSpy: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    authServiceSpy = jasmine.createSpyObj<AuthService>('AuthService', ['login']);
    routerSpy = jasmine.createSpyObj<Router>('Router', ['navigateByUrl']);

    await TestBed.configureTestingModule({
      imports: [LoginComponent],
      providers: [
        { provide: AuthService, useValue: authServiceSpy },
        { provide: Router, useValue: routerSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('shows generic credentials message on 401', () => {
    component.form.setValue({ email: 'ana@example.com', password: 'wrong' });
    authServiceSpy.login.and.returnValue(
      throwError(() => new HttpErrorResponse({ status: 401 }))
    );

    component.submit();

    expect(component.errorMessage).toBe('Credenciales inválidas');
  });

  it('shows recoverable message on technical error', () => {
    component.form.setValue({ email: 'ana@example.com', password: 'ana123' });
    authServiceSpy.login.and.returnValue(
      throwError(() => new HttpErrorResponse({ status: 500 }))
    );

    component.submit();

    expect(component.errorMessage).toBe('No fue posible iniciar sesión. Intenta nuevamente.');
  });

  it('redirects to /index on successful login', () => {
    component.form.setValue({ email: 'ana@example.com', password: 'ana123' });
    authServiceSpy.login.and.returnValue(
      of({
        authenticated: true,
        empleadoClave: 'EMP-1',
        email: 'ana@example.com',
        expiresAt: '2026-03-18T10:00:00Z'
      })
    );

    component.submit();

    expect(routerSpy.navigateByUrl).toHaveBeenCalledWith('/index');
  });
});
