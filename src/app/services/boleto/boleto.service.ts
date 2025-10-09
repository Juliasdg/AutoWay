import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { map, Observable } from "rxjs";
import { environment } from '../../environments/environments';
import { Boleto } from "../../pages/boleto-history/boleto-history.component";

@Injectable({ providedIn: 'root' })
export class BoletoService {
  private apiUrl = `${environment.apiUrl}/boletos`;

  constructor(private http: HttpClient) { }

  private authHeaders(token: string) {
    return new HttpHeaders({ Authorization: `Bearer ${token}` });
  }

  listarBoletos(idPessoa: string, token: string): Observable<any> {
    return this.http.get(`${this.apiUrl}/${idPessoa}`, { headers: this.authHeaders(token) });
  }

  consultarDebito(idPessoa: string, token: string): Observable<any> {
    return this.http.get(`${this.apiUrl}/debito/${idPessoa}`, { headers: this.authHeaders(token) });
  }

  baixarPdf(idBoleto: string, token: string): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/pdf/${idBoleto}`, { headers: this.authHeaders(token), responseType: 'blob' });
  }

  contarTodas(token: string): Observable<any> {
    return this.http.get(`${this.apiUrl}/count`, { headers: this.authHeaders(token) });
  }

  getTodosBoletos(token: string): Observable<Boleto[]> {
    return this.http
      .get<any>(`${this.apiUrl}`, { headers: this.authHeaders(token) })
      .pipe(map(res => Array.isArray(res) ? res : res.boletos || []));
  }
}
