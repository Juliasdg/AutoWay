import { Component } from '@angular/core';
import { HeaderComponent } from '../../componets/header/header.component';
import { BtnPurpleComponent } from '../../componets/btn-purple/btn-purple.component';
import { BtnWhiteComponent } from '../../componets/btn-white/btn-white.component';

@Component({
  selector: 'app-active-car',
  imports: [HeaderComponent, BtnPurpleComponent, BtnWhiteComponent],
  templateUrl: './active-car.component.html',
  styleUrl: './active-car.component.scss'
})
export class ActiveCarComponent {

}
