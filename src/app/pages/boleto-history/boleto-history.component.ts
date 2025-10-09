import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BtnPurpleComponent } from '../../componets/btn-purple/btn-purple.component';
import { HeaderPurpleComponent } from '../../componets/header-purple/header-purple.component';
import { BoletoService } from '../../services/boleto/boleto.service';
import { AuthService } from '../../services/auth/auth.service';
import { AlertService } from '../../services/alert/alert.service';

export interface Boleto {
  idBoleto: string;
  idPessoa: string;
  valorTotal: number;
  dataInicio: string;
  dataFim: string;
  dataEmissao: string;
  dataVencimento: string;
  statusPagamento: string;
}

@Component({
  selector: 'app-boleto-list',
  standalone: true,
  imports: [CommonModule, HeaderPurpleComponent],
  templateUrl: './boleto-history.component.html',
  styleUrls: ['./boleto-history.component.scss']
})
export class BoletoHistoryComponent implements OnInit {
  boletos: Boleto[] = [];
  paginatedBoletos: Boleto[] = [];
  loading = true;
  debitoMes: number = 0; 


  currentPage = 1;
  itemsPerPage = 5;
  totalPages = 1;

  constructor(
    private boletoService: BoletoService,
    private authService: AuthService,
    private alertService: AlertService
  ) { }

  ngOnInit(): void {
    this.carregarBoletos();
    this.consultarDebitoAtual();
  }

  carregarBoletos() {
    const token = this.authService.getToken();
    const userId = this.authService.getUserId();
    if (!token || !userId) {
      this.loading = false;
      return;
    }

    this.loading = true;
    this.boletoService.listarBoletos(userId, token).subscribe({
      next: (res: Boleto[]) => {
        this.boletos = res || [];
        this.setupPagination();
        this.loading = false;
      },
      error: (err) => {
        console.error('Erro ao carregar boletos', err);
        this.alertService.error('Erro', 'Não foi possível carregar os boletos.');
        this.loading = false;
      }
    });
  }

  visualizarBoleto(idBoleto: string) {
    const token = this.authService.getToken();
    if (!token) return;

    this.boletoService.baixarPdf(idBoleto, token).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        window.open(url, '_blank');
      },
      error: (err) => {
        console.error('Erro ao abrir boleto', err);
        this.alertService.error('Erro', 'Não foi possível visualizar o boleto.');
      }
    });
  }

  consultarDebitoAtual() {
    const userId = this.authService.getUserId();
    const token = this.authService.getToken();
    if (!userId || !token) return;

    this.boletoService.consultarDebito(userId, token).subscribe({
      next: (res: any) => {
        this.debitoMes = res || 0;
      },
      error: (err) => {
        console.error('Erro ao consultar débito', err);
      }
    });
  }

  setupPagination() {
    this.totalPages = Math.ceil(this.boletos.length / this.itemsPerPage) || 1;
    this.updatePage(1);
  }

  updatePage(page: number) {
    if (page < 1 || page > this.totalPages) return;
    this.currentPage = page;
    const start = (page - 1) * this.itemsPerPage;
    const end = start + this.itemsPerPage;
    this.paginatedBoletos = this.boletos.slice(start, end);
  }
}
