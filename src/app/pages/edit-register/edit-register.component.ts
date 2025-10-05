import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { BtnPurpleComponent } from '../../componets/btn-purple/btn-purple.component';
import { PessoaService } from '../../services/pessoa/pessoa.service';
import { AuthService } from '../../services/auth/auth.service';
import { AlertService } from '../../services/alert/alert.service';
import { PessoaResponse } from '../../models/responses/pessoa-responses';
import { PessoaUpdateRequest } from '../../models/requests/pessoa-requests';
import { HeaderComponent } from '../../componets/header/header.component';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-edit-register',
  standalone: true,
  imports: [CommonModule, HeaderComponent, BtnPurpleComponent, ReactiveFormsModule],
  templateUrl: './edit-register.component.html',
  styleUrls: ['./edit-register.component.scss']
})

export class EditRegisterComponent implements OnInit {
  formRegister!: FormGroup;
  pessoa?: PessoaResponse;

  constructor(
    private fb: FormBuilder,
    private alertService: AlertService,
    private pessoaService: PessoaService,
    private authService: AuthService,
    private router: Router,
    private http: HttpClient
  ) {
      this.formRegister = this.fb.group({
      nome: new FormControl(null, Validators.required),
      dataNascimento: new FormControl(null, Validators.required),
      telefone: new FormControl(null, Validators.required),
      cep: new FormControl(null, Validators.required),
      endereco: new FormControl(null), // preenchido pelo ViaCEP
      bairro: new FormControl(null),   // preenchido pelo ViaCEP
      numero: new FormControl(null, Validators.required),
      complemento: new FormControl(null),
      vencimento: new FormControl(5, Validators.required)
    });
  }

  ngOnInit(): void {
    const token = this.authService.getToken();
    if (token) {
      this.pessoaService.getMe(token).subscribe({
        next: (res) => {
          this.pessoa = res;
          this.initForm(res);
        },
        error: (err) => console.error(err)
      });
    }
  }

  private initForm(pessoa: PessoaResponse) {
    this.formRegister = this.fb.group({
      nome: [pessoa.nome, Validators.required],
      telefone: [pessoa.telefone, Validators.required],
      cep: [pessoa.cep, Validators.required],
      endereco: [pessoa.endereco, Validators.required],
      complemento: [pessoa.complemento],
      dataNascimento: [pessoa.dataNascimento, Validators.required],
      vencimento: [pessoa.vencimento, Validators.required],
      senha: [{ value: '', disabled: true }] // campo de senha desabilitado
    });
  }

  onUpdate() {
  if (this.formRegister.invalid) {
    this.alertService.warning('Preencha todos os campos obrigatórios!', 'Formulário inválido');
    return;
  }

  const token = this.authService.getToken();
  if (!token) {
    this.alertService.error('Usuário não autenticado!');
    return;
  }

  const payload: PessoaUpdateRequest = this.formRegister.value;

  this.pessoaService.updateMe(payload, token).subscribe({
    next: () => {
      this.alertService.success('Perfil atualizado com sucesso!', 'Seus dados foram atualizados corretamente.');
      this.router.navigate(['/profile']);
    },
    error: (err) => {
      // Tratativa para Bad Request (400) e mensagens personalizadas
      let msg = 'Erro ao atualizar perfil!';
      if (err.status === 400 && err.error?.message) {
        msg = err.error.message; // mensagem retornada pelo backend
      } else if (err.message) {
        msg = err.message; // mensagem genérica de erro
      }
      this.alertService.error(msg, new Error(msg));
    }
  });
}


  onEditPassword() {
    this.router.navigate(['/profile/edit/password']);
  }
    buscarEnderecoPorCep() {
    const cep = this.formRegister.get('cep')?.value?.replace(/\D/g, '');

    if (cep && /^[0-9]{8}$/.test(cep)) {
      this.http.get<any>(`https://viacep.com.br/ws/${cep}/json/`).subscribe({
        next: (dados) => {
          if (dados.erro) {
            this.alertService.error('CEP não encontrado!', new Error('CEP inválido'));
            return;
          }

          this.formRegister.patchValue({
            endereco: dados.logradouro,
            bairro: dados.bairro
          });
        },
        error: () => {
          this.alertService.error('Erro ao consultar CEP!', new Error('Falha no ViaCEP'));
        }
      });
    }
  }
}
