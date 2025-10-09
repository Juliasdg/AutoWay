import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { PessoaService } from '../../services/pessoa/pessoa.service';
import { AuthService } from '../../services/auth/auth.service';
import { AlertService } from '../../services/alert/alert.service';
import { HeaderComponent } from '../../componets/header/header.component';
import { BtnPurpleComponent } from '../../componets/btn-purple/btn-purple.component';
import { BtnWhiteComponent } from '../../componets/btn-white/btn-white.component';

@Component({
  selector: 'app-inativate-account',
  standalone: true,
  imports: [HeaderComponent, BtnPurpleComponent, BtnWhiteComponent],
  templateUrl: './inativate-account.component.html',
  styleUrls: ['./inativate-account.component.scss']
})
export class InativateAccountComponent implements OnInit {

  constructor(
    private pessoaService: PessoaService,
    private authService: AuthService,
    private alertService: AlertService,
    private router: Router
  ) { }

  ngOnInit(): void {
    const token = this.authService.getToken();
    const userId = this.authService.getUserId();
    if (!token || !userId) {
      this.alertService.error('Usuário não logado', 'Faça login para acessar esta funcionalidade.');
      this.router.navigate(['/login']);
    }
  }

  onInactivate() {
    const token = this.authService.getToken();
    const userId = this.authService.getUserId();

    if (!token || !userId) return;

    this.pessoaService.inactivateMe(userId, token).subscribe({
      next: () => {
        this.alertService.success(
          'Conta inativada',
          'Sua conta foi inativada com sucesso.'
        );
        this.authService.clearToken();
        this.authService.clearUserId();
        this.authService.clearUserRole();
        this.router.navigate(['/login']);
      },
      error: (err) => {
        this.alertService.httpError(err.status, err, 'Erro ao inativar conta');
      }
    });
  }

  onCancel() {
    this.router.navigate(['/profile']);
  }
}
