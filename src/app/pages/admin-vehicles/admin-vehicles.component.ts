import { Component, OnInit } from '@angular/core';
import { HeaderPurpleComponent } from '../../componets/header-purple/header-purple.component';
import { VeiculoService } from '../../services/veiculo/veiculo.service';
import { AuthService } from '../../services/auth/auth.service';
import { AlertService } from '../../services/alert/alert.service';
import { VeiculoResponse } from '../../models/responses/veiculo-responses';
import { firstValueFrom } from 'rxjs';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PessoaService } from '../../services/pessoa/pessoa.service';

@Component({
  selector: 'app-registered-car',
  standalone: true,
  imports: [HeaderPurpleComponent, CommonModule, FormsModule],
  templateUrl: './admin-vehicles.component.html',
  styleUrls: ['./admin-vehicles.component.scss']
})
export class AdminVehiclesComponent implements OnInit {
  veiculos: VeiculoResponse[] = [];
  filteredVeiculos: VeiculoResponse[] = [];
  paginatedVeiculos: VeiculoResponse[] = [];

  loading = true;
  searchText = '';

  // Paginação
  currentPage = 1;
  itemsPerPage = 5;
  totalPages = 1;

  constructor(
    private veiculoService: VeiculoService,
    private authService: AuthService,
    private pessoaService: PessoaService,
    private alertService: AlertService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.carregarVeiculos();
  }

  async carregarVeiculos(): Promise<void> {
  const token = this.authService.getToken();
  if (!token) {
    this.alertService.error('Erro', 'Usuário não logado.');
    this.loading = false;
    return;
  }

  this.loading = true;
  try {
    const veiculos = await firstValueFrom(this.veiculoService.listarTodos(token));
    this.veiculos = veiculos || [];

    // Buscar nomes dos portadores
    await Promise.all(
      this.veiculos.map(async v => {
        try {
          const pessoa = await firstValueFrom(this.pessoaService.getById(v.idPessoa, token));
          v.portadorNome = pessoa.nome;
        } catch (err) {
          console.error(`Erro ao buscar portador do veículo ${v.idVeiculo}`, err);
          v.portadorNome = '-';
        }
      })
    );

    this.filteredVeiculos = [...this.veiculos];
    this.setupPagination();
  } catch (err) {
    console.error('Erro ao carregar veículos', err);
    this.alertService.error('Erro', 'Não foi possível carregar os veículos.');
  } finally {
    this.loading = false;
  }
}


  buscarVeiculos(): void {
    const term = this.searchText.toLowerCase().trim();
    this.filteredVeiculos = this.veiculos.filter(v =>
      v.placa.toLowerCase().includes(term) ||
      (v.idRfid ? v.idRfid.toLowerCase().includes(term) : false)
    );
    this.setupPagination();
  }

  setupPagination(): void {
    this.totalPages = Math.ceil(this.filteredVeiculos.length / this.itemsPerPage) || 1;
    this.updatePage(1);
  }

  updatePage(page: number): void {
    if (page < 1 || page > this.totalPages) return;
    this.currentPage = page;
    const start = (page - 1) * this.itemsPerPage;
    const end = start + this.itemsPerPage;
    this.paginatedVeiculos = this.filteredVeiculos.slice(start, end);
  }

  abrirTelaEdicao(id: string): void {
  this.router.navigate(['/manage/vehicles/edit', id]);
}


  async alternarStatus(veiculo: VeiculoResponse): Promise<void> {
  const token = this.authService.getToken();
  if (!token) return;

  try {
    const novoStatus = !veiculo.ativo;

    if (novoStatus) {
      await firstValueFrom(this.veiculoService.reativarVeiculo(veiculo.idVeiculo, token));
    } else {
      await firstValueFrom(this.veiculoService.inativarVeiculo(veiculo.idVeiculo, token));
    }

    veiculo.ativo = novoStatus;
    this.alertService.success(
      'Sucesso',
      `Veículo ${veiculo.placa} ${novoStatus ? 'reativado' : 'inativado'} com sucesso!`
    );
  } catch (err: any) {
    console.error('Erro ao alterar status do veículo', err);

    // Captura a mensagem da API
    const msg = err?.error?.message || 'Não foi possível alterar o status do veículo.';
    this.alertService.error('Erro', msg);
  }
}


}
