import { Component } from '@angular/core';
import { HeaderComponent } from '../../componets/header/header.component';
import { BtnPurpleComponent } from '../../componets/btn-purple/btn-purple.component';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AlertService } from '../../services/alert/alert.service';
import { AuthService } from '../../services/auth/auth.service';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
// Angular Material
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-register',
  imports: [HeaderComponent, BtnPurpleComponent, ReactiveFormsModule,  MatFormFieldModule,
    MatInputModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatSelectModule,
    MatButtonModule],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss'
})
export class RegisterComponent {
  formRegister: FormGroup;

  constructor(
    private fb: FormBuilder,
    private alertService: AlertService,
    private authService: AuthService,
    private router: Router,
    private http: HttpClient
  ) {
    this.formRegister = this.fb.group({
      nome: new FormControl(null, Validators.required),
      sobrenome: new FormControl(null, Validators.required),
      dataNascimento: new FormControl(null, Validators.required),
      cpf: new FormControl(null, Validators.required),
      telefone: new FormControl(null, Validators.required),
      cep: new FormControl(null, Validators.required),
      endereco: new FormControl(null), // preenchido pelo ViaCEP
      bairro: new FormControl(null),   // preenchido pelo ViaCEP
      numero: new FormControl(null, Validators.required),
      complemento: new FormControl(null),
      email: new FormControl(null, [Validators.required, Validators.email]),
      senha: new FormControl(null, [Validators.required, Validators.minLength(6)]),
      vencimento: new FormControl(5, Validators.required)
    });
  }

  buscarEnderecoPorCep() {
    const cep = this.formRegister.get('cep')?.value;

    if (cep && /^[0-9]{8}$/.test(cep)) { // valida formato de CEP
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

      onSubmit() {
      if (this.formRegister.invalid) {
        this.alertService.error(
          'Campos obrigatórios não preenchidos!',
          new Error('Por favor, preencha todos os campos obrigatórios!')
        );
        return;
      }

      // monta os campos finais antes de enviar
      const enderecoFinal = `${this.formRegister.value.endereco}, ${this.formRegister.value.numero}`;
      const nomeCompleto = `${this.formRegister.value.nome} ${this.formRegister.value.sobrenome}`;

      const payload = {
        ...this.formRegister.value,
        endereco: enderecoFinal,
        nome: nomeCompleto // substitui nome+sobrenome
      };
      delete payload.sobrenome; // não envia mais separado

      this.authService.register(payload).subscribe({
        next: () => {
          this.alertService.success('Conta criada!', 'Seu cadastro foi realizado com sucesso.');
          this.router.navigate(['/login']);
        },
        error: (error) => {
          this.alertService.httpError(error.status, error, 'Erro ao realizar cadastro!');
        }
      });
    }

    // Máscara de CEP
    formatarCep(event: any) {
      let valor = event.target.value.replace(/\D/g, '');
      if (valor.length > 5) {
        valor = valor.replace(/(\d{5})(\d{1,3})/, '$1-$2');
      }
      event.target.value = valor;
      this.formRegister.get('cep')?.setValue(valor.replace('-', '')); // mantém só números para API
    }

    // Máscara de CPF
    formatarCpf(event: any) {
      let valor = event.target.value.replace(/\D/g, '');
      valor = valor.replace(/(\d{3})(\d)/, '$1.$2');
      valor = valor.replace(/(\d{3})(\d)/, '$1.$2');
      valor = valor.replace(/(\d{3})(\d{1,2})$/, '$1-$2');
      event.target.value = valor;
      this.formRegister.get('cpf')?.setValue(valor.replace(/\D/g, '')); // mantém só números para API
    }

}
