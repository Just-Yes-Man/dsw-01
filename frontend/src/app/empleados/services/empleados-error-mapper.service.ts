import { HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class EmpleadosErrorMapperService {
  toUserMessage(error: unknown): string {
    if (!(error instanceof HttpErrorResponse)) {
      return 'No fue posible procesar la solicitud.';
    }

    if (error.status === 401) {
      return 'Tu sesión expiró. Inicia sesión nuevamente.';
    }

    if (error.status === 400) {
      const message = this.extractMessage(error);
      if (message.toLowerCase().includes('email') && message.toLowerCase().includes('registrado')) {
        return 'El correo ya está registrado. Usa uno diferente.';
      }
      return message || 'Hay campos inválidos. Revisa la información.';
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
