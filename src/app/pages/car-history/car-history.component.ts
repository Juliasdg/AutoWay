import { Component, OnInit } from '@angular/core';
import { BtnPurpleComponent } from '../../componets/btn-purple/btn-purple.component';
import { HeaderPurpleComponent } from '../../componets/header-purple/header-purple.component';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PassagemService } from '../../services/passagem/passagem.service';
import { AuthService } from '../../services/auth/auth.service';
import { AlertService } from '../../services/alert/alert.service';
import { VeiculoService } from '../../services/veiculo/veiculo.service';
import { Passagem } from '../../models/responses/passagem-responses';

@Component({
  selector: 'app-car-history',
  standalone: true,
  imports: [HeaderPurpleComponent, BtnPurpleComponent, CommonModule, FormsModule],
  templateUrl: './car-history.component.html',
  styleUrls: ['./car-history.component.scss']
})
export class CarHistoryComponent implements OnInit {
  passagens: Passagem[] = [];
  paginatedPassagens: Passagem[] = [];
  loading = true;

  // paginação
  currentPage = 1;
  itemsPerPage = 5;
  totalPages = 1;

  // filtro
  dataInicio: string = '';
  dataFim: string = '';
  hoje: string = new Date().toISOString().split('T')[0]; // yyyy-MM-dd

  constructor(
    private passagemService: PassagemService,
    private authService: AuthService,
    private alertService: AlertService,
    private veiculoService: VeiculoService
  ) {}

  ngOnInit(): void {
    this.carregarTodasPassagens();
  }

  carregarTodasPassagens() {
    const token = this.authService.getToken();
    if (!token) {
      this.alertService.error('Usuário não logado', 'Faça login para acessar seu histórico.');
      this.loading = false;
      return;
    }

    this.loading = true;
    this.passagemService.getMinhasPassagens(token).subscribe({
      next: (res: Passagem[]) => this.processarPassagens(res, token),
      error: (err) => {
        console.error('Erro ao buscar passagens', err);
        this.alertService.httpError(err.status, err, 'Erro ao carregar histórico de passagens!');
        this.loading = false;
      }
    });
  }

  filtrarPorPeriodo() {
    if (!this.dataInicio || !this.dataFim) {
      this.alertService.error('Selecione as duas datas', 'É necessário escolher data início e fim.');
      return;
    }

    const inicio = new Date(this.dataInicio);
    const fim = new Date(this.dataFim);
    const hojeDate = new Date(this.hoje);

    if (inicio > fim) {
      this.alertService.error('Datas inválidas', 'A data de início deve ser antes da data de fim.');
      return;
    }

    if (inicio > hojeDate || fim > hojeDate) {
      this.alertService.error('Datas inválidas', 'Não é permitido selecionar datas futuras.');
      return;
    }

    const token = this.authService.getToken();
    if (!token) {
      this.alertService.error('Usuário não logado', 'Faça login para acessar seu histórico.');
      return;
    }

    this.loading = true;
    this.passagemService.getMinhasPassagensPorPeriodo(token, this.dataInicio, this.dataFim).subscribe({
      next: (res: Passagem[]) => {
        this.processarPassagens(res, token);
        if (!res || res.length === 0) {
          this.alertService.info('Nenhuma passagem encontrada', 'Não foram encontradas passagens neste período.');
        }
      },
      error: (err) => {
        console.error('Erro ao filtrar passagens', err);
        this.alertService.httpError(err.status, err, 'Erro ao aplicar filtro de período!');
        this.loading = false;
      }
    });
  }

  private processarPassagens(res: Passagem[], token: string) {
    this.passagens = res || [];

    // atualiza a paginação imediatamente
    this.setupPagination();

    if (this.passagens.length === 0) {
      this.loading = false;
      return;
    }

    // enriquecimento assíncrono das placas
    this.veiculoService.listarMeusVeiculos(token).subscribe({
      next: (veiculos) => {
        this.passagens.forEach((p) => {
          const veiculo = veiculos.find(v => v.idVeiculo === p.idVeiculo);
          if (veiculo) p.placa = veiculo.placa;
        });
        this.loading = false;
      },
      error: (err) => {
        console.error('Erro ao buscar veículos', err);
        this.loading = false;
      }
    });
  }

  setupPagination() {
    this.totalPages = Math.ceil(this.passagens.length / this.itemsPerPage) || 1;
    this.updatePage(1);
  }

  updatePage(page: number) {
    if (page < 1 || page > this.totalPages) return;
    this.currentPage = page;
    const start = (page - 1) * this.itemsPerPage;
    const end = start + this.itemsPerPage;
    this.paginatedPassagens = this.passagens.slice(start, end);
  }
}
