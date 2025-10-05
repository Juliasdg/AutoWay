import { Component } from '@angular/core';
import { HeaderComponent } from '../../componets/header/header.component';
import { BtnPurpleComponent } from '../../componets/btn-purple/btn-purple.component';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AlertService } from '../../services/alert/alert.service';
import { AuthService } from '../../services/auth/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  imports: [HeaderComponent, BtnPurpleComponent, ReactiveFormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  formLogin: FormGroup;

  constructor(
    private fb: FormBuilder,
    private alertService: AlertService,
    private authService: AuthService,
    private router: Router
  ) {
    this.formLogin = this.fb.group({
      email: new FormControl(null, Validators.required),
      senha: new FormControl(null, Validators.required)
    });
  }

  onSubmit() {
    if (this.formLogin.invalid) {
      this.alertService.error(
        'Campos obrigatórios não preenchidos!',
        new Error('Por favor, preencha todos os campos obrigatórios!')
      );
      return;
    }

    this.authService.login(this.formLogin.value).subscribe({
      next: (response) => {
        this.alertService.success('Bem-vindo!', 'Login realizado com sucesso!');
        this.authService.setUserId(response.userId);

        if (response.tipoUsuario === 'admin') {
          this.router.navigate(['/home-admin']);
        } else {
          this.router.navigate(['/']);
        }
      },
      error: (error) => {
        let title = 'Erro ao realizar login!';
        let msg = 'Erro ao realizar login!';
        // Aqui pegamos a mensagem específica do backend
        if (error.error?.message) msg = error.error.message;
        else if (error.message) msg = error.message;

        // Exemplo de bad request específico: usuário inativo ou credenciais inválidas
        if (error.status === 401 || error.status == 400) {
          msg = 'Usuário inativo ou Credenciais Inválidas. Em caso de necessidade, entre em contato com o suporte!';
        } else if (error.status === 404) {
          msg = 'Usuário não encontrado.';
        }

        this.alertService.error(title, msg);
      }
    });
  }

}
