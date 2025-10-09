import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HeaderPurpleComponent } from '../../componets/header-purple/header-purple.component';
import { BoletoService } from '../../services/boleto/boleto.service';
import { AuthService } from '../../services/auth/auth.service';
import { AlertService } from '../../services/alert/alert.service';
import { PessoaService } from '../../services/pessoa/pessoa.service';
import { firstValueFrom } from 'rxjs';
import { FormsModule } from '@angular/forms';

export interface Boleto {
  idBoleto: string;
  idPessoa: string;
  valorTotal: number;
  dataInicio: string;
  dataFim: string;
  dataEmissao: string;
  dataVencimento: string;
  statusPagamento: string;
  nomePessoa?: string;
}

@Component({
  selector: 'app-admin-boleto',
  standalone: true,
  imports: [CommonModule, HeaderPurpleComponent, FormsModule],
  templateUrl: './admin-boleto.component.html',
  styleUrls: ['./admin-boleto.component.scss']
})
export class AdminBoletoComponent implements OnInit {
  boletos: Boleto[] = [];
  boletosFiltrados: Boleto[] = [];
  paginatedBoletos: Boleto[] = [];
  loading = true;

  filtroNome: string = '';
  filtroDataInicio: string = '';
  filtroDataFim: string = '';

  currentPage = 1;
  itemsPerPage = 5;
  totalPages = 1;

  constructor(
    private boletoService: BoletoService,
    private authService: AuthService,
    private alertService: AlertService,
    private pessoaService: PessoaService
  ) { }

  ngOnInit(): void {
    this.carregarTodosBoletos();
  }

  async carregarTodosBoletos() {
    const token = this.authService.getToken();
    if (!token) {
      this.alertService.error('Erro', 'Usuário não logado.');
      this.loading = false;
      return;
    }

    this.loading = true;

    try {
      const boletos = await firstValueFrom(this.boletoService.getTodosBoletos(token));
      this.boletos = boletos || [];
      await this.completarUsuarios(token);

      this.boletosFiltrados = [...this.boletos];
      this.setupPagination();
    } catch (err) {
      console.error('Erro ao buscar boletos', err);
      this.alertService.error('Erro', 'Não foi possível carregar os boletos.');
    } finally {
      this.loading = false;
    }
  }

  private async completarUsuarios(token: string): Promise<void> {
    try {
      const usuarios = await firstValueFrom(this.pessoaService.listAll(token));
      if (!usuarios) return;

      this.boletos.forEach(b => {
        const user = usuarios.find(u => u.id === b.idPessoa);
        b.nomePessoa = user?.nome || user?.email || 'Usuário desconhecido';
      });
    } catch (err) {
      console.error('Erro ao buscar usuários', err);
    }
  }

  aplicarFiltros() {
    let filtrados = [...this.boletos];

    if (this.filtroNome.trim()) {
      const termo = this.filtroNome.toLowerCase();
      filtrados = filtrados.filter(b =>
        b.nomePessoa?.toLowerCase().includes(termo)
      );
    }

    if (this.filtroDataInicio) {
      const inicio = new Date(this.filtroDataInicio);
      filtrados = filtrados.filter(b =>
        new Date(b.dataInicio) >= inicio
      );
    }

    if (this.filtroDataFim) {
      const fim = new Date(this.filtroDataFim);
      filtrados = filtrados.filter(b =>
        new Date(b.dataFim) <= fim
      );
    }

    this.boletosFiltrados = filtrados;
    this.setupPagination();
  }

  limparFiltros() {
    this.filtroNome = '';
    this.filtroDataInicio = '';
    this.filtroDataFim = '';
    this.boletosFiltrados = [...this.boletos];
    this.setupPagination();
  }

  visualizarBoleto(idBoleto: string) {
    const token = this.authService.getToken();
    if (!token) return;

    this.boletoService.baixarPdf(idBoleto, token).subscribe({
      next: blob => {
        const url = window.URL.createObjectURL(blob);
        window.open(url, '_blank');
      },
      error: err => {
        console.error('Erro ao abrir boleto', err);
        this.alertService.error('Erro', 'Não foi possível visualizar o boleto.');
      }
    });
  }

  setupPagination() {
    this.totalPages = Math.ceil(this.boletosFiltrados.length / this.itemsPerPage) || 1;
    this.updatePage(1);
  }

  updatePage(page: number) {
    if (page < 1 || page > this.totalPages) return;
    this.currentPage = page;
    const start = (page - 1) * this.itemsPerPage;
    const end = start + this.itemsPerPage;
    this.paginatedBoletos = this.boletosFiltrados.slice(start, end);
  }
}
