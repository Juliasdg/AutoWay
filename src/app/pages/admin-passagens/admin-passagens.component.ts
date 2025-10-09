import { Component, OnInit } from '@angular/core';
import { HeaderPurpleComponent } from '../../componets/header-purple/header-purple.component';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PassagemService } from '../../services/passagem/passagem.service';
import { AuthService } from '../../services/auth/auth.service';
import { AlertService } from '../../services/alert/alert.service';
import { Passagem } from '../../models/responses/passagem-responses';
import { BoletoService } from '../../services/boleto/boleto.service';
import { VeiculoService } from '../../services/veiculo/veiculo.service';

@Component({
  selector: 'app-admin-passagens',
  standalone: true,
  imports: [HeaderPurpleComponent, CommonModule, FormsModule],
  templateUrl: './admin-passagens.component.html',
  styleUrls: ['./admin-passagens.component.scss']
})
export class AdminPassagensComponent implements OnInit {
  passagens: Passagem[] = [];
  passagensFiltradas: Passagem[] = [];
  paginatedPassagens: Passagem[] = [];
  loading = true;

  filtroPlaca: string = '';
  filtroDataInicio: string = '';
  filtroDataFim: string = '';

  currentPage = 1;
  itemsPerPage = 5;
  totalPages = 1;

  boletoDisponivel: boolean = false;
  boletoIdPeriodo: string | null = null;

  constructor(
    private passagemService: PassagemService,
    private authService: AuthService,
    private alertService: AlertService,
    private boletoService: BoletoService,
    private veiculoService: VeiculoService
  ) { }

  ngOnInit(): void {
    this.carregarTodasPassagens();
  }

  carregarTodasPassagens() {
    const token = this.authService.getToken();
    if (!token) {
      this.alertService.error('Usuário não logado', 'Faça login para acessar o painel.');
      this.loading = false;
      return;
    }

    this.loading = true;
    this.passagemService.getTodasPassagens(token).subscribe({
      next: (res: Passagem[]) => {
        this.passagens = res || [];

        this.completarPlacas(token).then(() => {
          this.passagensFiltradas = [...this.passagens];
          this.setupPagination();
          this.loading = false;
        });
      },
      error: (err) => {
        console.error('Erro ao buscar passagens', err);
        this.alertService.httpError(err.status, err, 'Erro ao carregar passagens!');
        this.loading = false;
      }
    });
  }

  private async completarPlacas(token: string): Promise<void> {
    const promises = this.passagens.map(async (p) => {
      try {
        const veiculo = await this.veiculoService.buscarPorId(p.idVeiculo, token).toPromise();
        p.placa = veiculo?.placa || '—';
      } catch {
        p.placa = '—';
      }
    });
    await Promise.all(promises);
  }

  aplicarFiltros() {
    let filtradas = [...this.passagens];

    if (this.filtroPlaca.trim()) {
      const termo = this.filtroPlaca.trim().toLowerCase();
      filtradas = filtradas.filter(p => p.placa?.toLowerCase().includes(termo));
    }

    if (this.filtroDataInicio) {
      const inicio = new Date(this.filtroDataInicio);
      filtradas = filtradas.filter(p => new Date(p.data) >= inicio);
    }

    if (this.filtroDataFim) {
      const fim = new Date(this.filtroDataFim);
      filtradas = filtradas.filter(p => new Date(p.data) <= fim);
    }

    this.passagensFiltradas = filtradas;
    this.setupPagination();
  }

  limparFiltros() {
    this.filtroPlaca = '';
    this.filtroDataInicio = '';
    this.filtroDataFim = '';
    this.passagensFiltradas = [...this.passagens];
    this.setupPagination();
  }

  setupPagination() {
    this.totalPages = Math.ceil(this.passagensFiltradas.length / this.itemsPerPage) || 1;
    this.updatePage(1);
  }

  updatePage(page: number) {
    if (page < 1 || page > this.totalPages) return;
    this.currentPage = page;
    const start = (page - 1) * this.itemsPerPage;
    const end = start + this.itemsPerPage;
    this.paginatedPassagens = this.passagensFiltradas.slice(start, end);
  }

  visualizarBoleto(boletoId: string) {
    const token = this.authService.getToken();
    if (!token) return;

    this.boletoService.baixarPdf(boletoId, token).subscribe({
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
}
