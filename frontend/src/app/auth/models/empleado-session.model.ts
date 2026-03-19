export interface EmpleadoSession {
  authenticated: boolean;
  empleadoClave: string;
  email: string;
  expiresAt: string;
  idleTimeoutMinutes?: number;
}
