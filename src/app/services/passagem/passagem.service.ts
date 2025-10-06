// passagem.service.ts
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
  import { environment } from '../../environments/environments';
import { Passagem } from '../../models/responses/passagem-responses';


@Injectable({ providedIn: 'root' })
export class PassagemService {
    private apiUrl = `${environment.apiUrl}/passagens`;

  constructor(private http: HttpClient) {}

  getMinhasPassagens(token: string): Observable<Passagem[]> {
  const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
  return this.http.get<any>(`${this.apiUrl}/me`, { headers }).pipe(
    map(res => Array.isArray(res) ? res : res.passagens || [])
  );
}

getMinhasPassagensPorPeriodo(token: string, inicio: string, fim: string): Observable<Passagem[]> {
  const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
  return this.http.get<any>(`${this.apiUrl}/me/filter`, {
    headers,
    params: { dataInicio: inicio, dataFim: fim }
  }).pipe(
    map(res => Array.isArray(res) ? res : res.passagens || [])
  );
}


  contarTodas(): Observable<any> {
    return this.http.get(`${this.apiUrl}/count`);
  }
}
