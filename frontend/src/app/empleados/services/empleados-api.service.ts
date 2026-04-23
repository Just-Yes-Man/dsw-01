import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { EmpleadoPageResponse, EmpleadoUpsertRequest, EmpleadoView } from '../models/empleado.model';

@Injectable({
  providedIn: 'root'
})
export class EmpleadosApiService {
  private readonly baseUrl = `${environment.apiBaseUrl}${environment.empleadosPath}`;

  constructor(private readonly http: HttpClient) {}

  findAll(page: number): Observable<EmpleadoPageResponse> {
    return this.http.get<EmpleadoPageResponse>(`${this.baseUrl}?page=${page}`, { withCredentials: true });
  }

  findByClave(clave: string): Observable<EmpleadoView> {
    return this.http.get<EmpleadoView>(`${this.baseUrl}/${clave}`, { withCredentials: true });
  }

  create(payload: EmpleadoUpsertRequest): Observable<EmpleadoView> {
    return this.http.post<EmpleadoView>(this.baseUrl, payload, { withCredentials: true });
  }

  update(clave: string, payload: EmpleadoUpsertRequest): Observable<EmpleadoView> {
    return this.http.put<EmpleadoView>(`${this.baseUrl}/${clave}`, payload, { withCredentials: true });
  }
}
