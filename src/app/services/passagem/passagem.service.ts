// passagem.service.ts
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
  import { environment } from '../../environments/environments';


@Injectable({ providedIn: 'root' })
export class PassagemService {
    private apiUrl = `${environment.apiUrl}/passagens`;

  constructor(private http: HttpClient) {}

  getMinhasPassagens(token: string): Observable<any> {
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.get(`${this.apiUrl}/me`, { headers });
  }

  getMinhasPassagensPorPeriodo(token: string, inicio: string, fim: string): Observable<any> {
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.get(`${this.apiUrl}/me/filter`, {
      headers,
      params: { dataInicio: inicio, dataFim: fim }
    });
  }

  contarTodas(): Observable<any> {
    return this.http.get(`${this.apiUrl}/count`);
  }
}
