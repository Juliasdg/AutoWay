import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { HeaderPurpleComponent } from '../../componets/header-purple/header-purple.component';
import { BtnPurpleComponent } from '../../componets/btn-purple/btn-purple.component';
import { PessoaService } from '../../services/pessoa/pessoa.service';
import { AuthService } from '../../services/auth/auth.service';
import { PessoaResponse } from '../../models/responses/pessoa-responses';
import { PessoaRequest } from '../../models/requests/pessoa-requests';

@Component({
  selector: 'app-edit-register',
  standalone: true,
  imports: [CommonModule, HeaderPurpleComponent, BtnPurpleComponent],
  templateUrl: './edit-register.component.html',
  styleUrls: ['./edit-register.component.scss']
})
export class EditRegisterComponent implements OnInit {
  pessoa?: PessoaResponse;

  constructor(
    private route: ActivatedRoute,
    private pessoaService: PessoaService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    const token = this.authService.getToken();
    if (token) {
    this.pessoaService.getMe(token).subscribe({
      next: res => this.pessoa = res,
      error: err => console.error(err)
    });
  }
  }

  onUpdate(formValue: Partial<PessoaRequest>) {
    const token = this.authService.getToken();
    if (!token) return;

    this.pessoaService.updateMe(formValue, token).subscribe({
      next: res => {
        alert('Perfil atualizado com sucesso!');
      },
      error: err => {
        alert('Erro ao atualizar perfil');
      }
    });
  }
}
