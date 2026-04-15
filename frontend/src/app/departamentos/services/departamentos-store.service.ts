import { Injectable, computed, signal } from '@angular/core';
import { Observable, of } from 'rxjs';
import { map, switchMap, tap } from 'rxjs/operators';
import {
  DepartamentoCreateRequest,
  DepartamentoPageResponse,
  DepartamentoUpdateRequest,
  DepartamentoView
} from '../models/departamento.model';
import { DepartamentoListState } from '../models/departamento-form-state.model';
import { DepartamentosApiService } from './departamentos-api.service';

@Injectable({ providedIn: 'root' })
export class DepartamentosStoreService {
  private readonly rawItems = signal<DepartamentoView[]>([]);
  private readonly state = signal<DepartamentoListState>({
    page: 0,
    size: 10,
    totalElements: 0,
    loading: false,
    error: null
  });

  readonly listState = computed(() => this.state());
  readonly items = computed(() => this.rawItems());

  constructor(private readonly api: DepartamentosApiService) {}

  setError(message: string | null): void {
    this.state.update((current) => ({ ...current, error: message }));
  }

  refreshCurrentPage(): void {
    this.loadPage(this.state().page).subscribe({ error: () => undefined });
  }

  loadPage(page = 0): Observable<DepartamentoPageResponse> {
    const safePage = Math.max(0, page);
    this.state.update((current) => ({ ...current, loading: true, error: null }));

    return this.api.findAll(safePage).pipe(
      switchMap((response) => {
        const maxPage = Math.max(0, Math.ceil(response.totalElements / response.size) - 1);
        const normalizedPage = Math.min(safePage, maxPage);

        if (normalizedPage !== safePage) {
          return this.api.findAll(normalizedPage);
        }

        return of(response);
      }),
      tap({
        next: (response) => {
          this.rawItems.set(response.items);
          this.state.set({
            page: response.page,
            size: response.size,
            totalElements: response.totalElements,
            loading: false,
            error: null
          });
        },
        error: () => {
          this.state.update((current) => ({
            ...current,
            loading: false,
            error: 'No fue posible cargar departamentos.'
          }));
        }
      })
    );
  }

  create(payload: DepartamentoCreateRequest): Observable<DepartamentoView> {
    return this.api.create(payload).pipe(
      tap(() => this.refreshCurrentPage())
    );
  }

  update(id: number, payload: DepartamentoUpdateRequest): Observable<DepartamentoView> {
    return this.api.update(id, payload).pipe(
      tap(() => this.refreshCurrentPage())
    );
  }

  delete(id: number): Observable<void> {
    return this.api.delete(id).pipe(
      tap(() => this.refreshCurrentPage())
    );
  }

  findCurrentById(id: number): DepartamentoView | undefined {
    return this.rawItems().find((item) => item.id === id);
  }

  updateLocalItem(updated: DepartamentoView): void {
    this.rawItems.update((items) => items.map((item) => (item.id === updated.id ? updated : item)));
  }

  ensureExists(id: number): Observable<DepartamentoView> {
    const local = this.findCurrentById(id);
    if (local) {
      return of(local);
    }
    return this.api.findById(id).pipe(
      map((item) => item),
      tap((item) => this.updateLocalItem(item))
    );
  }
}
