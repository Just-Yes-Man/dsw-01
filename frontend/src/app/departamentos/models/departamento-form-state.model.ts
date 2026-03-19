export type DepartamentoFormMode = 'create' | 'edit';

export interface DepartamentoFormValue {
  nombre: string;
}

export interface DepartamentoFormState {
  mode: DepartamentoFormMode;
  values: DepartamentoFormValue;
  fieldErrors: Record<string, string>;
  backendError: string | null;
  submitting: boolean;
}

export interface DeleteDepartamentoState {
  targetId: number | null;
  open: boolean;
  processing: boolean;
  backendError: string | null;
}

export interface DepartamentoListState {
  page: number;
  size: number;
  totalElements: number;
  loading: boolean;
  error: string | null;
}

export const DEFAULT_DEPARTAMENTO_FORM_VALUE: DepartamentoFormValue = {
  nombre: ''
};
