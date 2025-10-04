import { Component } from '@angular/core';
import { HeaderComponent } from '../../componets/header/header.component';
import { BtnPurpleComponent } from '../../componets/btn-purple/btn-purple.component';
import { Router } from '@angular/router';
import { AlertService } from '../../services/alert/alert.service';
import { AuthService } from '../../services/auth/auth.service';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators, ɵInternalFormsSharedModule } from "@angular/forms";
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-forgot-password',
  imports: [HeaderComponent, BtnPurpleComponent, ɵInternalFormsSharedModule, ReactiveFormsModule, CommonModule],
  templateUrl: './forgot-password.component.html',
  styleUrl: './forgot-password.component.scss'
})
export class ForgotPasswordComponent {
  formForgotPassword: FormGroup;
  codeSent = false;
  codeVerified = false;
  code: string = '';
  email: string = '';

  constructor(
    private authService: AuthService,
    private alertService: AlertService,
    private router: Router,
    private fb: FormBuilder
  ) {
    this.formForgotPassword = this.fb.group({
      email: new FormControl('', [Validators.required, Validators.email]),
      code: new FormControl('', [Validators.required]),
      newPassword: new FormControl('', [Validators.required, Validators.minLength(6)]),
      confirmNewPassword: new FormControl('', [Validators.required])
    });
  }

  sendForgotPasswordEmail(email: string) {
    this.authService.forgotPassword({ email }).subscribe({
      next: (response) => {
        this.alertService.success('Sucesso', response);
        this.codeSent = true;
        this.email = email;
      },
      error: (error) => {
        this.alertService.httpError(error.status, error, 'Erro ao enviar e-mail de redefinição de senha!');
        this.codeSent = false;
      }
    });
  }

  verifyResetCode(code: string) {
    this.authService.verifyResetCode({ email: this.formForgotPassword.value.email, code }).subscribe({
      next: (valid) => {
        this.codeVerified = valid;

        if (valid) {
          this.alertService.success('Sucesso!', 'Código de redefinição válido!');
          this.code = code;
        } else {
          this.alertService.error('Código inválido!', new Error('Código de redefinição inválido!'));
        }
      },
      error: (error) => {
        this.alertService.httpError(error.status, error, 'Erro ao validar código de redefinição!');
        this.codeVerified = false;
      }
    });
  }

  resetPassword(email: string, code: string, newPassword: string) {
    this.authService.resetPassword({ email, code, newPassword }).subscribe({
      next: (response) => {
        this.alertService.success('Sucesso', response);
        this.router.navigate(['/login']);
      },
      error: (error) => {
        this.alertService.httpError(error.status, error, 'Erro ao redefinir senha!');
      }
    });
  }

  onSubmitForgotPassword() {
    const { email } = this.formForgotPassword.value;

    if (!email || this.codeSent || this.codeVerified) {
      this.alertService.error('Por favor, preencha todos os campos obrigatórios!', new Error('Campos obrigatórios não preenchidos!'));
      return;
    }

    this.sendForgotPasswordEmail(email);
  }

  onSubmitVerifyCode() {
    const { code } = this.formForgotPassword.value;

    if (!code || !this.codeSent || this.codeVerified) {
      this.alertService.error('Por favor, preencha todos os campos obrigatórios!', new Error('Campos obrigatórios não preenchidos!'));
      return;
    }

    this.verifyResetCode(code);
  }

  onSubmitResetPassword() {
    const { newPassword, confirmNewPassword } = this.formForgotPassword.value;

    if (!newPassword || !confirmNewPassword || !this.codeSent || !this.codeVerified) {
      this.alertService.error('Por favor, preencha todos os campos obrigatórios!', new Error('Campos obrigatórios não preenchidos!'));
      return;
    }

    if (newPassword === confirmNewPassword) {
      this.resetPassword(this.email, this.code, newPassword);
    }
    else {
      this.alertService.error('As senhas não coincidem!', new Error('Erro ao alterar senha!'));
    }
  }
}
