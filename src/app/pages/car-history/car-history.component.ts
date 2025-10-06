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
import { BoletoService } from '../../services/boleto/boleto.service';

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

  debitoMes: number = 0; // <--- novo campo

  // paginação
  currentPage = 1;
  itemsPerPage = 5;
  totalPages = 1;

  boletoDisponivel: boolean = false;
  boletoIdPeriodo: string | null = null;


  // filtro
  dataInicio: string = '';
  dataFim: string = '';
  hoje: string = new Date().toISOString().split('T')[0]; // yyyy-MM-dd

  constructor(
    private passagemService: PassagemService,
    private authService: AuthService,
    private alertService: AlertService,
    private veiculoService: VeiculoService,
    private boletoService: BoletoService // <--- injetado
  ) {}

  ngOnInit(): void {
    this.carregarTodasPassagens();
    this.consultarDebitoAtual();
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

  if (!this.passagens.length) {
    this.paginatedPassagens = [];
    this.loading = false;
    return;
  }

  const userId = this.authService.getUserId();
  if (!userId) {
    this.setupPagination();
    this.loading = false;
    return;
  }

  // Busca os veículos do usuário
  this.veiculoService.listarMeusVeiculos(token).subscribe({
    next: (veiculos) => {
      this.passagens.forEach(p => {
        const veiculo = veiculos.find(v => v.idVeiculo === p.idVeiculo);
        if (veiculo) p.placa = veiculo.placa;
      });

      // Busca os boletos do usuário
      this.boletoService.listarBoletos(userId, token).subscribe({
        next: (boletos: any[]) => {
          this.passagens.forEach(p => {
            const dataPassagem = new Date(p.data);
            const mes = dataPassagem.getMonth() + 1;
            const ano = dataPassagem.getFullYear();

            const boleto = boletos.find(b => {
              const mesmoPeriodo =
                Number(b.mes) === mes &&
                Number(b.ano) === ano;

              if (!mesmoPeriodo) return false;

              // Se o boleto já está fechado, exige que a passagem conste no array
              if (b.mesFechado) {
                return Array.isArray(b.passagens) &&
                       b.passagens.some((bp: any) => bp.idPassagem === p.idPassagem);
              }

              // Se o boleto está em aberto, basta casar pelo período
              return true;
            });

            p.boletoId = boleto?.idBoleto;
          });

          this.setupPagination();
          this.loading = false;
        },
        error: (err) => {
          console.error('Erro ao buscar boletos', err);
          this.setupPagination();
          this.loading = false;
        }
      });

    },
    error: (err) => {
      console.error('Erro ao buscar veículos', err);
      this.setupPagination();
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

visualizarBoleto(boletoId: string) {
  const token = this.authService.getToken();
  if (!token) return;

  this.boletoService.baixarPdf(boletoId, token).subscribe({
    next: (blob) => {
      const url = window.URL.createObjectURL(blob);
      window.open(url, '_blank'); // abre em nova aba
    },
    error: (err) => {
      console.error('Erro ao abrir boleto', err);
      this.alertService.error('Erro', 'Não foi possível visualizar o boleto.');
    }
  });
}


}
