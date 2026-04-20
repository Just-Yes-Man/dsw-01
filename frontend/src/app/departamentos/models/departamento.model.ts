export type EstadoAcceso = 'ACTIVO' | 'INACTIVO';

export interface DepartamentoView {
  id: number;
  nombre: string;
  estado: EstadoAcceso;
  creadoEn: string;
  actualizadoEn: string;
}

export interface DepartamentoPageResponse {
  page: number;
  size: number;
  totalElements: number;
  items: DepartamentoView[];
}

export interface DepartamentoCreateRequest {
  nombre: string;
}

export interface DepartamentoUpdateRequest {
  nombre: string;
}

export interface ErrorResponse {
  code?: string;
  message?: string;
}
