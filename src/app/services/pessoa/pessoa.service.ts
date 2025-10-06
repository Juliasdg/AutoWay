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
  return this.http.put(`${this.apiUrl}/me/password`, payload, { headers, responseType: 'text' }); 
  // responseType: 'text' evita JSON.parse em sucesso
}


  // lista todos os usuários (ADMIN)
  listAll(token: string): Observable<PessoaResponse[]> {
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.get<PessoaResponse[]>(this.apiUrl, { headers });
  }

  // busca usuário por id
  getById(id: string, token: string): Observable<PessoaResponse> {
    return this.http.get<PessoaResponse>(`${this.apiUrl}/${id}`, { headers: this.authHeaders(token) });
  }

  inactivateMe(userId: string, token: string): Observable<void> {
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.patch<void>(`${this.apiUrl}/${userId}/inactivate`, {}, { headers });
  }

  private authHeaders(token: string) {
      return new HttpHeaders({ Authorization: `Bearer ${token}` });
    }

  countAtivos(token: string): Observable<any> {
    return this.http.get(`${this.apiUrl}/ativos/count`, {headers: this.authHeaders(token)});
  }

  updateUserAsAdmin(id: string, req: PessoaUpdateRequest, token: string): Observable<PessoaResponse> {
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.put<PessoaResponse>(`${this.apiUrl}/${id}`, req, { headers });
  }

}
