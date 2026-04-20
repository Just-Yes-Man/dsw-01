export interface AuthErrorState {
  code: 'UNAUTHORIZED' | 'LOCKED' | 'SERVER_ERROR' | 'VALIDATION_ERROR';
  message: string;
}
