import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { VeiculoService } from '../../services/veiculo/veiculo.service';
import { AlertService } from '../../services/alert/alert.service';
import { AuthService } from '../../services/auth/auth.service';
import { HeaderComponent } from '../../componets/header/header.component';
import { BtnPurpleComponent } from '../../componets/btn-purple/btn-purple.component';
import { BtnWhiteComponent } from '../../componets/btn-white/btn-white.component';

@Component({
  selector: 'app-active-car',
  standalone: true,
  imports: [HeaderComponent, BtnPurpleComponent, BtnWhiteComponent],
  templateUrl: './active-car.component.html',
  styleUrls: ['./active-car.component.scss']
})
export class ActiveCarComponent implements OnInit {
  veiculo: any;

  constructor(
    private router: Router,
    private veiculoService: VeiculoService,
    private alertService: AlertService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.veiculo = history.state.veiculo || JSON.parse(localStorage.getItem('veiculo') || 'null');

    if (!this.veiculo || !this.veiculo.idVeiculo) {
      this.alertService.error('Veículo inválido', 'Não foi possível identificar o veículo.');
      this.router.navigate(['/']);
      return;
    }

    // Armazena temporariamente para não perder ao dar refresh
    localStorage.setItem('veiculo', JSON.stringify(this.veiculo));
  }

  onReativar() {
    const token = this.authService.getToken();
    const veiculoId = this.veiculo?.idVeiculo;

    if (!token || !veiculoId) {
      this.alertService.error('Erro', 'Não foi possível identificar o veículo ou token.');
      return;
    }

    this.veiculoService.reativarVeiculo(veiculoId, token).subscribe({
      next: () => {
        this.alertService.success('Veículo reativado com sucesso!');
        localStorage.removeItem('veiculo');
        this.router.navigate(['/']);
      },
      error: (err) => this.alertService.httpError(err.status, err)
    });
  }

  onCancel() {
    this.router.navigate(['/']);
  }
}
