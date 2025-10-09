import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { HeaderPurpleComponent } from '../../componets/header-purple/header-purple.component';
import { BtnPurpleComponent } from '../../componets/btn-purple/btn-purple.component';
import { BtnWhiteComponent } from '../../componets/btn-white/btn-white.component';
import { PessoaService } from '../../services/pessoa/pessoa.service';
import { PessoaResponse } from '../../models/responses/pessoa-responses';
import { AuthService } from '../../services/auth/auth.service';
import { AlertService } from '../../services/alert/alert.service';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, HeaderPurpleComponent, BtnPurpleComponent, BtnWhiteComponent],
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss']
})
export class ProfileComponent implements OnInit {
  pessoa?: PessoaResponse;
  loading = true;

  constructor(
    private pessoaService: PessoaService,
    private authService: AuthService,
    private router: Router,
    private alertService: AlertService
  ) { }

  ngOnInit(): void {
    const token = this.authService.getToken();
    if (token) {
      this.pessoaService.getMe(token).subscribe({
        next: (res) => {
          this.pessoa = res;
          this.loading = false;
        },
        error: (err) => {
          console.error('Erro ao buscar perfil', err);
          this.alertService.httpError(err.status, err, 'Erro ao carregar perfil!');
          this.loading = false;
        }
      });
    } else {
      this.alertService.error('Usuário não logado', 'Faça login para acessar seu perfil.');
      this.loading = false;
    }
  }

  onEdit() {
    if (this.pessoa?.id) {
      this.router.navigate(['/profile/edit']);
    }
  }

  onInactivate() {
    this.router.navigate(['/profile/inactivate-account']);
  }

}
