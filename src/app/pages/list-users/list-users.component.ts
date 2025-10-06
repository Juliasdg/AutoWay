import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms'; // <--- import necessário
import { HeaderPurpleComponent } from '../../componets/header-purple/header-purple.component';
import { PessoaService, PessoaUpdateRequest } from '../../services/pessoa/pessoa.service';
import { AuthService } from '../../services/auth/auth.service';
import { AlertService } from '../../services/alert/alert.service';
import { firstValueFrom } from 'rxjs';

export interface Usuario {
  id: string;
  nome: string;
  email: string;
  telefone?: string;
  status: boolean;
}

@Component({
  selector: 'app-list-users',
  standalone: true,
  imports: [CommonModule, FormsModule, HeaderPurpleComponent], // <-- FormsModule adicionado aqui
  templateUrl: './list-users.component.html',
  styleUrls: ['./list-users.component.scss']
})
export class ListUsersComponent implements OnInit {
  usuarios: Usuario[] = [];
  filteredUsuarios: Usuario[] = [];
  loading = true;
  searchText = '';

  constructor(
    private pessoaService: PessoaService,
    private authService: AuthService,
    private alertService: AlertService
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
      this.usuarios = usuarios.map(u => ({
        id: u.id,
        nome: u.nome,
        email: u.email,
        telefone: u.telefone,
        status: u.status
      }));
      this.filteredUsuarios = [...this.usuarios];
    } catch (err) {
      console.error('Erro ao carregar usuários', err);
      this.alertService.error('Erro', 'Não foi possível carregar os usuários.');
    } finally {
      this.loading = false;
    }
  }

  buscarUsuario(): void {
    const term = this.searchText.toLowerCase();
    this.filteredUsuarios = this.usuarios.filter(u =>
      u.nome.toLowerCase().includes(term) || u.email.toLowerCase().includes(term)
    );
  }

  async toggleStatus(usuario: Usuario): Promise<void> {
    const token = this.authService.getToken();
    if (!token) return;

    const request: PessoaUpdateRequest = { status: !usuario.status };
    try {
      await firstValueFrom(this.pessoaService.updateMe(request, token));
      usuario.status = !usuario.status;
      this.alertService.success('Sucesso', `Status do usuário ${usuario.nome} atualizado!`);
    } catch (err) {
      console.error('Erro ao atualizar status', err);
      this.alertService.error('Erro', 'Não foi possível atualizar o status.');
    }
  }

  async editarUsuario(usuario: Usuario, updateData: Partial<Usuario>): Promise<void> {
    const token = this.authService.getToken();
    if (!token) return;

    try {
      const request: PessoaUpdateRequest = { ...updateData };
      await firstValueFrom(this.pessoaService.updateMe(request, token));
      Object.assign(usuario, updateData);
      this.alertService.success('Sucesso', `Dados do usuário ${usuario.nome} atualizados!`);
    } catch (err) {
      console.error('Erro ao editar usuário', err);
      this.alertService.error('Erro', 'Não foi possível atualizar os dados do usuário.');
    }
  }

  async buscarPorId(id: string): Promise<Usuario | undefined> {
  const token = this.authService.getToken();
  if (!token) return undefined; // <-- explicitamente retorna undefined

  try {
    const usuario = await firstValueFrom(this.pessoaService.getById(id, token));
    return {
      id: usuario.id,
      nome: usuario.nome,
      email: usuario.email,
      telefone: usuario.telefone,
      status: usuario.status
    };
  } catch (err) {
    console.error('Erro ao buscar usuário', err);
    this.alertService.error('Erro', 'Não foi possível buscar o usuário.');
    return undefined; // <-- garante que algo seja retornado
  }
}

}
