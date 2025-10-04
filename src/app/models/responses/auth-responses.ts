export interface LoginResponse {
  token: string;
  userId: string;
  tipoUsuario: string;
}

export interface LogoutResponse {
  message: string;
}

export interface RegisterResponse {
  userId: string;
  message: string;
}

export interface ChangePasswordResponse {
  message: string;
}
