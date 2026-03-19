import { EstadoAcceso } from './empleado.model';

export type EmpleadoFormMode = 'create' | 'edit';

export interface EmpleadoFormValue {
  nombre: string;
  direccion: string;
  telefono: string;
  email: string;
  password: string;
  estadoAcceso: EstadoAcceso;
  departamentoId: number;
}

export interface EmpleadoFormState {
  mode: EmpleadoFormMode;
  values: EmpleadoFormValue;
  fieldErrors: Record<string, string>;
  backendError: string | null;
  submitting: boolean;
}

export interface DeleteConfirmationState {
  targetClave: string | null;
  open: boolean;
  processing: boolean;
}

export interface EmpleadoListState {
  page: number;
  size: number;
  totalElements: number;
  loading: boolean;
  showInactive: boolean;
  error: string | null;
}

export const DEFAULT_FORM_VALUE: EmpleadoFormValue = {
  nombre: '',
  direccion: '',
  telefono: '',
  email: '',
  password: '',
  estadoAcceso: 'ACTIVO',
  departamentoId: 1
};
