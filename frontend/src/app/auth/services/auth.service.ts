import { HttpClient } from '@angular/common/http';
import { Injectable, signal } from '@angular/core';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { EmpleadoAuthCredential } from '../models/empleado-auth-credential.model';
import { EmpleadoSession } from '../models/empleado-session.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly baseUrl = `${environment.apiBaseUrl}/auth`;
  private readonly authenticated = signal<boolean>(false);

  constructor(private readonly http: HttpClient) {}

  isAuthenticated(): boolean {
    return this.authenticated();
  }

  login(credentials: EmpleadoAuthCredential): Observable<EmpleadoSession> {
    return this.http.post<EmpleadoSession>(`${this.baseUrl}/login`, credentials, { withCredentials: true }).pipe(
      tap(() => this.authenticated.set(true))
    );
  }

  checkSession(): Observable<EmpleadoSession> {
    return this.http.get<EmpleadoSession>(`${this.baseUrl}/session`, { withCredentials: true }).pipe(
      tap(() => this.authenticated.set(true))
    );
  }

  logout(): Observable<void> {
    return this.http.post<void>(`${this.baseUrl}/logout`, {}, { withCredentials: true }).pipe(
      tap(() => this.authenticated.set(false))
    );
  }

  markUnauthenticated(): void {
    this.authenticated.set(false);
  }
}
