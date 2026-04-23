import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';

export const sessionExpiredInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  return next(req).pipe(
    catchError((error: unknown) => {
      const isLoginRequest = req.url.includes('/auth/login');

      if (error instanceof HttpErrorResponse && error.status === 401 && !isLoginRequest) {
        authService.markUnauthenticated();
        router.navigateByUrl('/login');
      }
      return throwError(() => error);
    })
  );
};
