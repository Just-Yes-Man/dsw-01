import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  DepartamentoCreateRequest,
  DepartamentoPageResponse,
  DepartamentoUpdateRequest,
  DepartamentoView
} from '../models/departamento.model';

@Injectable({
  providedIn: 'root'
})
export class DepartamentosApiService {
  private readonly baseUrl = `${environment.apiBaseUrl}${environment.departamentosPath}`;

  constructor(private readonly http: HttpClient) {}

  findAll(page: number): Observable<DepartamentoPageResponse> {
    return this.http.get<DepartamentoPageResponse>(`${this.baseUrl}?page=${page}`, { withCredentials: true });
  }

  findById(id: number): Observable<DepartamentoView> {
    return this.http.get<DepartamentoView>(`${this.baseUrl}/${id}`, { withCredentials: true });
  }

  create(payload: DepartamentoCreateRequest): Observable<DepartamentoView> {
    return this.http.post<DepartamentoView>(this.baseUrl, payload, { withCredentials: true });
  }

  update(id: number, payload: DepartamentoUpdateRequest): Observable<DepartamentoView> {
    return this.http.patch<DepartamentoView>(`${this.baseUrl}/${id}`, payload, { withCredentials: true });
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`, { withCredentials: true });
  }
}
