import { Component } from '@angular/core';
import { HeaderComponent } from '../../componets/header/header.component';
import { BtnPurpleComponent } from '../../componets/btn-purple/btn-purple.component';
import { BtnWhiteComponent } from '../../componets/btn-white/btn-white.component';

@Component({
  selector: 'app-inativate-car',
  imports: [HeaderComponent, BtnPurpleComponent, BtnWhiteComponent],
  templateUrl: './inativate-car.component.html',
  styleUrl: './inativate-car.component.scss'
})
export class InativateCarComponent {

}
