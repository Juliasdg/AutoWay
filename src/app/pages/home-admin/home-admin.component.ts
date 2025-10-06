import { Component, OnInit } from '@angular/core';
import { HeaderPurpleComponent } from '../../componets/header-purple/header-purple.component';
import { PessoaService } from '../../services/pessoa/pessoa.service';
import { VeiculoService } from '../../services/veiculo/veiculo.service';
import { BoletoService } from '../../services/boleto/boleto.service';
import { PassagemService } from '../../services/passagem/passagem.service';

@Component({
  selector: 'app-home-admin',
  standalone: true,
  imports: [HeaderPurpleComponent],
  templateUrl: './home-admin.component.html',
  styleUrl: './home-admin.component.scss'
})
export class HomeAdminComponent implements OnInit {

  pessoasCount = 0;
  veiculosCount = 0;
  boletosCount = 0;
  passagensCount = 0;

  constructor(
    private pessoaService: PessoaService,
    private veiculoService: VeiculoService,
    private boletoService: BoletoService,
    private passagemService: PassagemService
  ) {}

  ngOnInit(): void {
  const token = localStorage.getItem('auth_token') || ''; // ✅ chave correta

    this.pessoaService.countAtivos(token).subscribe({
      next: res => this.pessoasCount = res.quantidade,
      error: () => this.pessoasCount = 0
    });

    this.veiculoService.countAtivos(token).subscribe({
      next: res => this.veiculosCount = res.quantidade,
      error: () => this.veiculosCount = 0
    });

    this.boletoService.contarTodas(token).subscribe({
      next: (res: any) => this.boletosCount = res.quantidade,
      error: () => this.boletosCount = 0
    });

    this.passagemService.contarTodas(token).subscribe({
      next: (res: any) => this.passagensCount = res.quantidade,
      error: () => this.passagensCount = 0
    });
  }
}
