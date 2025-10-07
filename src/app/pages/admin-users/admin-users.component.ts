import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HeaderPurpleComponent } from '../../componets/header-purple/header-purple.component';
import { PessoaService, PessoaUpdateRequest } from '../../services/pessoa/pessoa.service';
import { AuthService } from '../../services/auth/auth.service';
import { AlertService } from '../../services/alert/alert.service';
import { firstValueFrom } from 'rxjs';
import { PessoaResponse } from '../../models/responses/pessoa-responses';
import { Router } from '@angular/router';

@Component({
  selector: 'app-list-users',
  standalone: true,
  imports: [CommonModule, FormsModule, HeaderPurpleComponent],
  templateUrl: './admin-users.component.html',
  styleUrls: ['./admin-users.component.scss']
})
export class AdminUsersComponent implements OnInit {
  usuarios: PessoaResponse[] = [];
  filteredUsuarios: PessoaResponse[] = [];
  paginatedUsuarios: PessoaResponse[] = [];

  loading = true;
  searchText = '';

  // paginação
  currentPage = 1;
  itemsPerPage = 5;
  totalPages = 1;

  constructor(
    private pessoaService: PessoaService,
    private authService: AuthService,
    private alertService: AlertService,
    private router:  Router
  ) {}

  ngOnInit(): void {
    this.carregarUsuarios();
  }

  async carregarUsuarios(): Promise<void> {
    const token = this.authService.getToken();
    if (!token) {
      this.alertService.error('Erro', 'Usuário não logado.');
      this.loading = false;
      return;
    }

    this.loading = true;
    try {
      const usuarios = await firstValueFrom(this.pessoaService.listAll(token));
      this.usuarios = usuarios || [];
      this.filteredUsuarios = [...this.usuarios];
      this.setupPagination();
    } catch (err) {
      console.error('Erro ao carregar usuários', err);
      this.alertService.error('Erro', 'Não foi possível carregar os usuários.');
    } finally {
      this.loading = false;
    }
  }

  buscarUsuario(): void {
    const term = this.searchText.toLowerCase().trim();
    this.filteredUsuarios = this.usuarios.filter(u =>
      u.nome.toLowerCase().includes(term) || u.email.toLowerCase().includes(term)
    );
    this.setupPagination(); // reinicia a paginação após busca
  }

  

  setupPagination(): void {
    this.totalPages = Math.ceil(this.filteredUsuarios.length / this.itemsPerPage) || 1;
    this.updatePage(1);
  }

  updatePage(page: number): void {
    if (page < 1 || page > this.totalPages) return;
    this.currentPage = page;
    const start = (page - 1) * this.itemsPerPage;
    const end = start + this.itemsPerPage;
    this.paginatedUsuarios = this.filteredUsuarios.slice(start, end);
  }

  async editarUsuario(usuario: PessoaResponse, updateData: Partial<PessoaUpdateRequest>): Promise<void> {
    const token = this.authService.getToken();
    if (!token) return;

    try {
      await firstValueFrom(this.pessoaService.updateUserAsAdmin(usuario.id, updateData, token));
      Object.assign(usuario, updateData);
      this.alertService.success('Sucesso', `Dados do usuário ${usuario.nome} atualizados!`);
    } catch (err) {
      console.error('Erro ao editar usuário', err);
      this.alertService.error('Erro', 'Não foi possível atualizar o usuário.');
    }
  }

  abrirTelaEdicao(id: string): void {
  this.router.navigate(['/manage/users/edit', id]);
  }

  async alternarStatus(usuario: PessoaResponse): Promise<void> {
  const token = this.authService.getToken();
  if (!token) {
    this.alertService.error('Erro', 'Usuário não autenticado.');
    return;
  }

  try {
    const novoStatus = !usuario.status;

    if (novoStatus) {
      // Reativar usuário
      await firstValueFrom(this.pessoaService.reactivateMe(usuario.id, token));
    } else {
      // Inativar usuário
      await firstValueFrom(this.pessoaService.inactivateMe(usuario.id, token));
    }

    // Atualiza status localmente
    usuario.status = novoStatus;

    this.alertService.success(
      'Sucesso',
      `Usuário ${usuario.nome} foi ${novoStatus ? 'reativado' : 'inativado'} com sucesso!`
    );
  } catch (err) {
    console.error('Erro ao alterar status', err);
    this.alertService.error('Erro', 'Não foi possível alterar o status do usuário.');
  }
}

}
