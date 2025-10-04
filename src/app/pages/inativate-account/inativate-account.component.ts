import { Component } from '@angular/core';
import { HeaderComponent } from '../../componets/header/header.component';
import { BtnPurpleComponent } from '../../componets/btn-purple/btn-purple.component';
import { BtnWhiteComponent } from '../../componets/btn-white/btn-white.component';

@Component({
  selector: 'app-inativate-account',
  imports: [HeaderComponent, BtnPurpleComponent, BtnWhiteComponent],
  templateUrl: './inativate-account.component.html',
  styleUrl: './inativate-account.component.scss'
})
export class InativateAccountComponent {

}
