import { Component, OnInit } from '@angular/core';
import { HeaderPurpleComponent } from '../../componets/header-purple/header-purple.component';
import { BtnPurpleComponent } from '../../componets/btn-purple/btn-purple.component';
import { VeiculoService } from '../../services/veiculo/veiculo.service';
import { AuthService } from '../../services/auth/auth.service';
import { AlertService } from '../../services/alert/alert.service';
import { VeiculoResponse } from '../../models/responses/veiculo-responses';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-home',
  imports: [CommonModule, HeaderPurpleComponent, BtnPurpleComponent],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {
  veiculos: VeiculoResponse[] = [];

  constructor(
    private veiculoService: VeiculoService,
    private authService: AuthService,
    private alertService: AlertService,
    public router: Router
  ) {}

  ngOnInit(): void {
    const token = this.authService.getToken();
    if (!token) {
      this.alertService.error('Usuário não autenticado!');
      this.router.navigate(['/login']);
      return;
    }

    this.veiculoService.listarMeusVeiculos(token).subscribe({
      next: (res) => this.veiculos = res,
      error: () => this.alertService.error('Erro ao carregar veículos!')
    });
  }

  handleAtivarInativar(veiculo: VeiculoResponse) {
    if (!veiculo.idRfid) {
      this.alertService.warning('Este veículo ainda não possui RFID cadastrado!');
      return;
    }

    if (veiculo.ativo) {
      this.router.navigate(['/inactivate-car'], { state: { veiculo } });
    } else {
      this.router.navigate(['/activate-car'], { state: { veiculo } });
    }
  }
}
