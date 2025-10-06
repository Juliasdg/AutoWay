import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HeaderPurpleComponent } from '../../componets/header-purple/header-purple.component';
import { BoletoService } from '../../services/boleto/boleto.service';
import { AuthService } from '../../services/auth/auth.service';
import { AlertService } from '../../services/alert/alert.service';
import { PessoaService } from '../../services/pessoa/pessoa.service';
import { firstValueFrom } from 'rxjs';

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
  imports: [CommonModule, HeaderPurpleComponent],
  templateUrl: './admin-boleto.component.html',
  styleUrls: ['./admin-boleto.component.scss']
})
export class AdminBoletoComponent implements OnInit {
  boletos: Boleto[] = [];
  paginatedBoletos: Boleto[] = [];
  loading = true;

  currentPage = 1;
  itemsPerPage = 5;
  totalPages = 1;

  constructor(
    private boletoService: BoletoService,
    private authService: AuthService,
    private alertService: AlertService,
    private pessoaService: PessoaService
  ) {}

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
      this.setupPagination();
    } catch (err) {
      console.error('Erro ao buscar boletos', err);
      this.alertService.error('Erro', 'Não foi possível carregar os boletos.');
    } finally {
      this.loading = false;
    }
  }

  /** 🔹 Carrega todos os usuários de uma vez e mapeia no boleto */
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
