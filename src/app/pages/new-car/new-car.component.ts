import { Component, OnInit } from '@angular/core';
import { HeaderComponent } from '../../componets/header/header.component';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { VeiculoService } from '../../services/veiculo/veiculo.service';
import { AuthService } from '../../services/auth/auth.service';
import { AlertService } from '../../services/alert/alert.service';
import { Router } from '@angular/router';
import { BtnPurpleComponent } from '../../componets/btn-purple/btn-purple.component';


@Component({
  selector: 'app-new-car',
  imports: [CommonModule, ReactiveFormsModule, HeaderComponent, BtnPurpleComponent],
  templateUrl: './new-car.component.html',
  styleUrls: ['./new-car.component.scss']
})
export class NewCarComponent implements OnInit {
  veiculoForm!: FormGroup;

  constructor(
    private fb: FormBuilder,
    private veiculoService: VeiculoService,
    private authService: AuthService,
    private alertService: AlertService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.veiculoForm = this.fb.group({
      placa: ['', [Validators.required, Validators.minLength(7), Validators.maxLength(7)]]
    });
  }

  cadastrarVeiculo() {
  if (this.veiculoForm.invalid) {
    this.alertService.warning('Preencha o campo Placa corretamente!');
    return;
  }

  const token = this.authService.getToken();
  if (!token) {
    this.alertService.error('Usuário não autenticado!');
    this.router.navigate(['/login']);
    return;
  }

  this.veiculoService.criarVeiculo(this.veiculoForm.value, token).subscribe({
    next: (res) => {
      this.alertService.success('Veículo cadastrado com sucesso! Aguarde a validação do Administrador');
      this.veiculoForm.reset(); // limpa o formulário
      this.router.navigate(['/']); // volta para a home
    },
    error: (err) => {
      // Se houver mensagem do backend, exibe ela
      if (err.status === 400 && err.error && err.error.message) {
        this.alertService.error('Erro de Validação!', err.error.message);
      } else {
        // Caso contrário, utiliza a função httpError do serviço
        this.alertService.httpError(err.status, err);
      }
    }
  });
}


}
