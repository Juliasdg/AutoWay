import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { ChangePasswordResponse, LoginResponse, RegisterResponse } from '../../models/responses/auth-responses';
import { ChangePasswordRequest, ForgotPasswordRequest, LoginRequest, RegisterRequest, ResetPasswordRequest, VerifyResetCodeRequest } from '../../models/requests/auth-requests';
import { environment } from '../../environments/environments';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = `${environment.apiUrl}/auth`;
  private tokenKey = 'auth_token';
  private userIdKey = 'user_id';

  constructor(private http: HttpClient) { }

  private userRoleKey = 'user_role';

  login(payload: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/login`, payload, { withCredentials: true }).pipe(
      map(res => {
        if (res && res.token && res.userId) {
          this.setToken(res.token);
          this.setUserId(res.userId);
          const tipo = res.tipoUsuario ? res.tipoUsuario.toLowerCase() : 'cliente';
          this.setUserRole(tipo);
        }
        return res;
      })
    );
  }

  getUserRole(): string | null {
    return localStorage.getItem(this.userRoleKey);
  }

  setUserRole(role: string): void {
    localStorage.setItem(this.userRoleKey, role);
  }

  clearUserRole(): void {
    localStorage.removeItem(this.userRoleKey);
  }


  logout(): Observable<void> {
    this.clearToken();
    this.clearUserId();
    this.clearUserRole();
    return this.http.post<void>(`${this.apiUrl}/logout`, {}, { withCredentials: true });
  }

  register(payload: RegisterRequest): Observable<RegisterResponse> {
    return this.http.post<RegisterResponse>(`${this.apiUrl}/register`, payload);
  }

  resetPassword(payload: ResetPasswordRequest): Observable<string> {
    return this.http.post(`${this.apiUrl}/reset-password`, payload, { responseType: 'text' });
  }

  forgotPassword(payload: ForgotPasswordRequest): Observable<string> {
    return this.http.post(`${this.apiUrl}/forgot-password`, payload, { responseType: 'text' });
  }

  verifyResetCode(payload: VerifyResetCodeRequest): Observable<boolean> {
    return this.http.post(`${this.apiUrl}/verify-reset-code`, payload, { responseType: 'text' }).pipe(
      map(response => response === 'true')
    );
  }

  changePassword(payload: ChangePasswordRequest) {
    return this.http.post<ChangePasswordResponse>(`${this.apiUrl}/change-password`, payload, { withCredentials: true });
  }


  isAuthenticated(): boolean {
    const token = this.getToken();
    if (!token) return false;

    const payload = JSON.parse(atob(token.split('.')[1]));
    const now = Math.floor(Date.now() / 1000);
    if (payload.exp && payload.exp < now) {
      this.logout(); 
      return false;
    }
    return true;
  }

  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  setToken(token: string): void {
    localStorage.setItem(this.tokenKey, token);
  }

  clearToken(): void {
    localStorage.removeItem(this.tokenKey);
  }

  getUserId(): string | null {
    return localStorage.getItem(this.userIdKey);
  }

  setUserId(userId: string): void {
    localStorage.setItem(this.userIdKey, userId);
  }

  clearUserId(): void {
    localStorage.removeItem(this.userIdKey);
  }
}
