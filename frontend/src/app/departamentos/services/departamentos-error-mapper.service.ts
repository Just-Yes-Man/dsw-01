import { HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class DepartamentosErrorMapperService {
  toUserMessage(error: unknown): string {
    if (!(error instanceof HttpErrorResponse)) {
      return 'No fue posible procesar la solicitud.';
    }

    if (error.status === 401) {
      return 'Tu sesión expiró. Inicia sesión nuevamente.';
    }

    if (error.status === 409) {
      const message = this.extractMessage(error).toLowerCase();
      if (message.includes('empleados')) {
        return 'No se puede eliminar: el departamento tiene empleados asociados.';
      }
      if (message.includes('nombre') || message.includes('existe')) {
        return 'El nombre del departamento ya existe. Usa uno diferente.';
      }
      return 'Conflicto de negocio. Revisa la información e intenta nuevamente.';
    }

    if (error.status === 400) {
      return this.extractMessage(error) || 'Hay campos inválidos. Revisa la información.';
    }

    if (error.status >= 500) {
      return 'Error técnico temporal. Intenta nuevamente.';
    }

    return this.extractMessage(error) || 'No fue posible procesar la solicitud.';
  }

  private extractMessage(error: HttpErrorResponse): string {
    const payload = error.error as { message?: string } | null;
    return payload?.message ?? '';
  }
}
