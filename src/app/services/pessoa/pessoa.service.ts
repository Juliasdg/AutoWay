import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { PessoaRequest } from '../../models/requests/pessoa-requests';
import { PessoaResponse } from '../../models/responses/pessoa-responses';
import { environment } from '../../environments/environments';

export type PessoaUpdateRequest = Partial<PessoaRequest> & { status?: boolean };

@Injectable({
  providedIn: 'root'
})
export class PessoaService {
  private apiUrl = `${environment.apiUrl}/pessoas`;

  
  constructor(private http: HttpClient) {}

  // cria usuário
  createPessoa(req: PessoaRequest): Observable<PessoaResponse> {
    return this.http.post<PessoaResponse>(this.apiUrl, req);
  }

  getMe(token: string): Observable<PessoaResponse> {
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}` // <--- aqui é o importante
    });
  return this.http.get<PessoaResponse>(`${this.apiUrl}/me`, { headers });
  }

  // altera dados do usuário logado
  updateMe(req: PessoaUpdateRequest, token: string): Observable<PessoaResponse> {
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.put<PessoaResponse>(`${this.apiUrl}/me`, req, { headers });
  }


  // troca senha do usuário logado
  changePassword(payload: { currentPassword: string; newPassword: string; confirmPassword: string }, token: string) {
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.put(`${this.apiUrl}/me/password`, payload, { headers });
  }

  // lista todos os usuários (ADMIN)
  listAll(token: string): Observable<PessoaResponse[]> {
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.get<PessoaResponse[]>(this.apiUrl, { headers });
  }

  // busca usuário por id
  getById(id: string, token: string): Observable<PessoaResponse> {
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.get<PessoaResponse>(`${this.apiUrl}/${id}`, { headers });
  }
}
