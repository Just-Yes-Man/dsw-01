import { Injectable, computed, signal } from '@angular/core';
import { Observable, tap } from 'rxjs';
import { EmpleadoListState } from '../models/empleado-form-state.model';
import { EmpleadoPageResponse, EmpleadoUpsertRequest, EmpleadoView } from '../models/empleado.model';
import { EmpleadosApiService } from './empleados-api.service';

@Injectable({ providedIn: 'root' })
export class EmpleadosStoreService {
  private readonly rawItems = signal<EmpleadoView[]>([]);
  private readonly state = signal<EmpleadoListState>({
    page: 0,
    size: 10,
    totalElements: 0,
    loading: false,
    showInactive: false,
    error: null
  });

  readonly listState = computed(() => this.state());
  readonly items = computed(() => {
    const showInactive = this.state().showInactive;
    return showInactive ? this.rawItems() : this.rawItems().filter((item) => item.estadoAcceso === 'ACTIVO');
  });

  constructor(private readonly api: EmpleadosApiService) {}

  setShowInactive(showInactive: boolean): void {
    this.state.update((current) => ({ ...current, showInactive }));
  }

  setError(message: string | null): void {
    this.state.update((current) => ({ ...current, error: message }));
  }

  refreshCurrentPage(): void {
    this.loadPage(this.state().page).subscribe({
      error: () => undefined
    });
  }

  loadPage(page = 0): Observable<EmpleadoPageResponse> {
    this.state.update((current) => ({ ...current, loading: true, error: null }));
    return this.api.findAll(page).pipe(
      tap({
        next: (response) => {
          this.rawItems.set(response.items);
          this.state.set({
            page: response.page,
            size: response.size,
            totalElements: response.totalElements,
            loading: false,
            showInactive: this.state().showInactive,
            error: null
          });
        },
        error: () => {
          this.state.update((current) => ({ ...current, loading: false, error: 'No fue posible cargar empleados.' }));
        }
      })
    );
  }

  create(payload: EmpleadoUpsertRequest): Observable<EmpleadoView> {
    return this.api.create(payload).pipe(
      tap(() => {
        this.refreshCurrentPage();
      })
    );
  }

  update(clave: string, payload: EmpleadoUpsertRequest): Observable<EmpleadoView> {
    return this.api.update(clave, payload).pipe(
      tap(() => {
        this.refreshCurrentPage();
      })
    );
  }

  deactivate(clave: string): Observable<EmpleadoView> {
    const target = this.rawItems().find((item) => item.clave === clave);
    if (!target) {
      throw new Error('Empleado no encontrado');
    }

    const payload: EmpleadoUpsertRequest = {
      nombre: target.nombre,
      direccion: target.direccion,
      telefono: target.telefono,
      email: target.email,
      password: target.password ?? 'change_me',
      estadoAcceso: 'INACTIVO',
      departamentoId: target.departamento.id
    };

    return this.update(clave, payload);
  }
}
