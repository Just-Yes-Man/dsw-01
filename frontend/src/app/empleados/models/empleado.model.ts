export type EstadoAcceso = 'ACTIVO' | 'INACTIVO';

export interface DepartamentoEmbedded {
  id: number;
  nombre: string;
  estado: EstadoAcceso;
}

export interface EmpleadoView {
  clave: string;
  nombre: string;
  direccion: string;
  telefono: string;
  email: string;
  password?: string;
  estadoAcceso: EstadoAcceso;
  departamento: DepartamentoEmbedded;
}

export interface EmpleadoPageResponse {
  page: number;
  size: number;
  totalElements: number;
  items: EmpleadoView[];
}

export interface EmpleadoUpsertRequest {
  nombre: string;
  direccion: string;
  telefono: string;
  email: string;
  password: string;
  estadoAcceso: EstadoAcceso;
  departamentoId: number;
}
