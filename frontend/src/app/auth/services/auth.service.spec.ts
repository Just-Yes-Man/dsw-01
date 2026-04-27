/// <reference types="jasmine" />

import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { AuthService } from './auth.service';
import { EmpleadoSession } from '../models/empleado-session.model';

declare const expect: any;

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        AuthService
      ]
    });
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should login and mark user as authenticated', () => {
    const expected: EmpleadoSession = {
      authenticated: true,
      empleadoClave: 'EMP-1',
      email: 'ana@example.com',
      expiresAt: '2026-03-18T10:00:00Z'
    };

    service.login({ email: 'ana@example.com', password: 'ana123' }).subscribe((response) => {
      expect(response.authenticated).toBeTrue();
      expect(service.isAuthenticated()).toBeTrue();
    });

    const req = httpMock.expectOne('/api/v1/auth/login');
    expect(req.request.method).toBe('POST');
    expect(req.request.withCredentials).toBeTrue();
    req.flush(expected);
  });

  it('should mark unauthenticated on logout', () => {
    service.logout().subscribe();

    const req = httpMock.expectOne('/api/v1/auth/logout');
    expect(req.request.method).toBe('POST');
    req.flush({});

    expect(service.isAuthenticated()).toBeFalse();
  });

  it('should mark authenticated when session is valid', () => {
    service.checkSession().subscribe((response) => {
      expect(response.authenticated).toBeTrue();
      expect(service.isAuthenticated()).toBeTrue();
    });

    const req = httpMock.expectOne('/api/v1/auth/session');
    expect(req.request.method).toBe('GET');
    expect(req.request.withCredentials).toBeTrue();
    req.flush({
      authenticated: true,
      empleadoClave: 'EMP-1',
      email: 'ana@example.com',
      expiresAt: '2026-03-18T10:00:00Z'
    });
  });
});
