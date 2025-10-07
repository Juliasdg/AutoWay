import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { VeiculoService } from '../../services/veiculo/veiculo.service';
import { AuthService } from '../../services/auth/auth.service';
import { AlertService } from '../../services/alert/alert.service';
import { firstValueFrom } from 'rxjs';
import { HeaderComponent } from '../../componets/header/header.component';
import { BtnPurpleComponent } from '../../componets/btn-purple/btn-purple.component';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-edit-car',
  standalone: true,
  imports: [HeaderComponent, BtnPurpleComponent, FormsModule],
  templateUrl: './edit-car.component.html',
  styleUrls: ['./edit-car.component.scss']
})
export class EditCarComponent implements OnInit {

  idVeiculo!: string;
  rfid = '';
  placa = '';
  loading = true;

  constructor(
    private route: ActivatedRoute,
    private veiculoService: VeiculoService,
    private authService: AuthService,
    private alertService: AlertService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.idVeiculo = this.route.snapshot.paramMap.get('id')!;
    this.carregarVeiculo();
  }

  async carregarVeiculo(): Promise<void> {
    const token = this.authService.getToken();
    if (!token) return;

    this.loading = true;
    try {
      const veiculo = await firstValueFrom(this.veiculoService.buscarPorId(this.idVeiculo, token));
      this.rfid = veiculo.idRfid || '';
      this.placa = veiculo.placa;
    } catch (err) {
      console.error(err);
      this.alertService.error('Erro', 'Não foi possível carregar o veículo.');
    } finally {
      this.loading = false;
    }
  }

  async atualizarRfid(): Promise<void> {
    const token = this.authService.getToken();
    if (!token) return;

    if (!this.rfid.trim()) {
      this.alertService.error('Erro', 'O RFID não pode ser vazio.');
      return;
    }

    try {
      await firstValueFrom(this.veiculoService.ativarVeiculo(this.idVeiculo, { idRfid: this.rfid }, token));
      this.alertService.success('Sucesso', 'Veículo ativado com RFID!');
      this.router.navigate(['/manage/vehicles']);
    } catch (err: any) {
      console.error(err);
      const msg = err?.error?.message || 'Não foi possível ativar o veículo.';
      this.alertService.error('Erro', msg);
    }
  }
}
