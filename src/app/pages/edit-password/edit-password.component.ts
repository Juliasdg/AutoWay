import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth/auth.service';
import { PessoaService } from '../../services/pessoa/pessoa.service';
import { AlertService } from '../../services/alert/alert.service';
import { BtnPurpleComponent } from '../../componets/btn-purple/btn-purple.component';
import { HeaderComponent } from '../../componets/header/header.component';
import { PessoaResponse } from '../../models/responses/pessoa-responses';

interface ChangePasswordRequest {
  currentPassword: string;
  newPassword: string;
  confirmPassword: string;
}


@Component({
  selector: 'app-edit-password',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, BtnPurpleComponent, HeaderComponent],
  templateUrl: './edit-password.component.html',
  styleUrls: ['./edit-password.component.scss']
})
export class EditPasswordComponent implements OnInit {
  formPassword!: FormGroup;
  email!: string;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private pessoaService: PessoaService, // injetando PessoaService
    private alertService: AlertService,
    private router: Router
  ) {
    this.formPassword = this.fb.group({
      currentPassword: ['', Validators.required],
      newPassword: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    const token = this.authService.getToken();
    if (token) {
      this.pessoaService.getMe(token).subscribe({
        next: (res: PessoaResponse) => {
          this.email = res.email; // guarda para montar o payload
        },
        error: () => {
          this.alertService.warning('Não foi possível carregar os dados do usuário.');
        }
      });
    } else {
      this.alertService.error('Usuário não autenticado!');
      this.router.navigate(['/login']);
    }
  }

 onSubmit() {
  if (this.formPassword.invalid) {
    this.alertService.warning('Preencha os campos corretamente!');
    return;
  }

  const token = this.authService.getToken();
  if (!token) {
    this.alertService.error('Usuário não autenticado!');
    this.router.navigate(['/login']);
    return;
  }

  const payload: ChangePasswordRequest = {
    currentPassword: this.formPassword.value.currentPassword,
    newPassword: this.formPassword.value.newPassword,
    confirmPassword: this.formPassword.value.confirmPassword
  };

  this.pessoaService.changePassword(payload, token).subscribe({
    next: () => {
      // resposta de sucesso é texto ou vazia
      this.alertService.success('Senha alterada com sucesso!', 'Sua senha foi atualizada corretamente.');
      this.router.navigate(['/profile']);
    },
    error: (err) => {
      // resposta de erro continua JSON
      let msg = 'Erro ao alterar senha!';
      try {
        const body = typeof err.error === 'string' ? JSON.parse(err.error) : err.error;
        if (body?.message) {
          msg = body.message;
        }
      } catch {
        if (err.message) {
          msg = err.message;
        }
      }
      this.alertService.error(msg, new Error(msg));
    }
  });
}



}
