import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
  import { environment } from '../../environments/environments';


@Injectable({ providedIn: 'root' })
export class BoletoService {
    private apiUrl = `${environment.apiUrl}/boletos`;

  constructor(private http: HttpClient) {}

  listarBoletos(idPessoa: string): Observable<any> {
    return this.http.get(`${this.apiUrl}/${idPessoa}`);
  }

  consultarDebito(idPessoa: string): Observable<any> {
    return this.http.get(`${this.apiUrl}/debito/${idPessoa}`);
  }

  baixarPdf(idBoleto: string): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/pdf/${idBoleto}`, { responseType: 'blob' });
  }
}
