import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environments';
import { VeiculoResponse } from '../../models/responses/veiculo-responses';
import { VeiculoRequest, VeiculoAdminUpdateRequest } from '../../models/requests/veiculo-requests';

@Injectable({
  providedIn: 'root'
})
export class VeiculoService {
  private apiUrl = `${environment.apiUrl}/veiculos`;

  constructor(private http: HttpClient) {}

  private authHeaders(token: string) {
    return new HttpHeaders({ Authorization: `Bearer ${token}` });
  }

  criarVeiculo(payload: VeiculoRequest, token: string): Observable<VeiculoResponse> {
    return this.http.post<VeiculoResponse>(this.apiUrl, payload, { headers: this.authHeaders(token) });
  }

  listarMeusVeiculos(token: string): Observable<VeiculoResponse[]> {
    return this.http.get<VeiculoResponse[]>(`${this.apiUrl}/me`, { headers: this.authHeaders(token) });
  }

  listarTodos(token: string): Observable<VeiculoResponse[]> {
    return this.http.get<VeiculoResponse[]>(this.apiUrl, { headers: this.authHeaders(token) });
  }

  ativarVeiculo(idVeiculo: string, payload: VeiculoAdminUpdateRequest): Observable<VeiculoResponse> {
    return this.http.put<VeiculoResponse>(`${this.apiUrl}/${idVeiculo}/ativar`, payload);
  }

  inativarVeiculo(idVeiculo: string, token: string): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${idVeiculo}/inativar`, {}, { headers: this.authHeaders(token) });
  }

  reativarVeiculo(idVeiculo: string, token: string): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${idVeiculo}/reativar`, {}, { headers: this.authHeaders(token) });
  }

  buscarPorId(idVeiculo: string, token: string): Observable<VeiculoResponse> {
    return this.http.get<VeiculoResponse>(`${this.apiUrl}/${idVeiculo}`, {headers: this.authHeaders(token)});
  }

  searchVeiculos(placa?: string, rfid?: string): Observable<VeiculoResponse[]> {
    const params: any = {};
    if (placa) params.placa = placa;
    if (rfid) params.rfid = rfid;
    return this.http.get<VeiculoResponse[]>(`${this.apiUrl}/search`, { params });
  }

  countAtivos(token: string): Observable<any> {
    return this.http.get(`${this.apiUrl}/ativos/count`, {headers: this.authHeaders(token)});
  }

  
}
